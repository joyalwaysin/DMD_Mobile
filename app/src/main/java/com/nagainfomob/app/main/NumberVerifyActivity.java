package com.nagainfomob.app.main;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.nagainfomob.app.R;
import com.nagainfomob.app.api.ApiClient;
import com.nagainfomob.app.api.ApiInterface;
import com.nagainfomob.app.helpers.InputValidation;
import com.nagainfomob.app.helpers.SessionManager;
import com.nagainfomob.app.model.OTP.OtpData;
import com.nagainfomob.app.model.OTP.OtpResult;
import com.nagainfomob.app.model.RegisterDevice.RegisterDeviceData;
import com.nagainfomob.app.model.RegisterDevice.RegisterDeviceResult;
import com.nagainfomob.app.model.ResendOTP.ResendOTPData;
import com.nagainfomob.app.model.ResendOTP.ResendOTPResult;
import com.nagainfomob.app.model.SettingsModel;
import com.nagainfomob.app.model.UserModel;
import com.nagainfomob.app.sql.DatabaseManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.nagainfomob.app.api.ErrorUtils.ERROR_MESSAGE_NO_CONNECTION;
import static com.nagainfomob.app.api.ErrorUtils.ERROR_MESSAGE_SLOW_CONNECTION;
import static com.nagainfomob.app.api.ErrorUtils.ERROR_MESSAGE_UNKNOWN;
import static com.nagainfomob.app.api.ErrorUtils.ERROR_MESSAGE_WRONG_JSON_FORMAT;

public class NumberVerifyActivity extends AppCompatActivity implements View.OnClickListener {

    private final AppCompatActivity activity = NumberVerifyActivity.this;
    private String storageURL = "https://storage-vro.s3.amazonaws.com/";
    private static int downloadingFlag = 0;
    private SweetAlertDialog dDialog;

    private ImageView img_close;

    private TextView txt_mobile;
    private TextView txt_resend_otp;

    private EditText otp1;
    private EditText otp2;
    private EditText otp3;
    private EditText otp4;

    private String userMobileCode;
    private String userMobileNumber;
    private String android_id;
    private String fcm_token;

    private Button btn_Verify;

    private InputValidation inputValidation;
    private SweetAlertDialog pDialog;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_otp);

        initViews();
        initListeners();

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        userMobileCode = extras.getString("UserMobileCode");
        userMobileNumber = extras.getString("UserMobileNumber");

        txt_mobile.setText(userMobileCode+" "+userMobileNumber);

        session = new SessionManager(activity);
        android_id = Settings.Secure.getString(activity.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        fcm_token = FirebaseInstanceId.getInstance().getToken();

    }

    private void initViews() {

        img_close = findViewById(R.id. ImgClose);
        txt_mobile = findViewById(R.id. txt_mobile);
        txt_resend_otp = findViewById(R.id. txt_resend_otp);
        otp1 = findViewById(R.id. otp1);
        otp2 = findViewById(R.id. otp2);
        otp3 = findViewById(R.id. otp3);
        otp4 = findViewById(R.id. otp4);
        btn_Verify = findViewById(R.id. btn_Verify);
    }

    private void initListeners() {
        img_close.setOnClickListener(this);
        btn_Verify.setOnClickListener(this);
        txt_resend_otp.setOnClickListener(this);

        //OTP

        otp1.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(otp1.getText().toString().length()==1)
                {
                    otp2.requestFocus();
                }

            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void afterTextChanged(Editable s) {}
        });
        otp2.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(otp2.getText().toString().length()==1)
                {
                    otp3.requestFocus();
                }
                else{
                    otp1.requestFocus();
                }
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void afterTextChanged(Editable s) {}
        });
        otp3.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(otp3.getText().toString().length()==1)
                {
                    otp4.requestFocus();
                }
                else{
                    otp2.requestFocus();
                }
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void afterTextChanged(Editable s) {}
        });

        otp4.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(otp4.getText().toString().length()==0)
                {
                    otp3.requestFocus();
                }
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void afterTextChanged(Editable s) {}
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ImgClose:
                finish();
                break;
            case R.id.btn_Verify:
                doVerify();
//                viewVerifyAvtivity();
                break;
            case R.id.txt_resend_otp:
                resendOTP();
                break;

        }
    }

    public void doVerify(){
        if(formValidation() && checkMobileData()){
            try{
                final String opt = otp1.getText().toString()+otp2.getText().toString()+otp3.getText().toString()+
                        otp4.getText().toString();
                pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
                        .setTitleText("Processing");
                pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.colorAccent));
                pDialog.show();
                pDialog.setCancelable(false);

                ApiInterface apiService =
                        ApiClient.getClient().create(ApiInterface.class);
                Call<OtpResult> call = apiService.verifyOPT(new OtpData(userMobileCode,
                        userMobileNumber, opt));

                call.enqueue(new Callback<OtpResult>() {
                    @Override
                    public void onResponse(Call<OtpResult> call, final Response<OtpResult> response) {
                        int statusCode = response.code();
                        if (response.isSuccessful()) {
                            if(statusCode == 200) {
                                registerDevice(response);
                                /*pDialog.setTitleText(getString(R.string.success))
                                        .setContentText(getString(R.string.opt_success))
                                        .setConfirmText("OK")
                                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        pDialog.dismissWithAnimation();
                                        saveDB(response);
                                    }
                                })
                                        .show();*/
                            }

                        } else {
                            try {
                                JSONObject jObjError = new JSONObject(response.errorBody().string());
                                pDialog.setTitleText(getString(R.string.oops))
                                        .setContentText(jObjError.getString("error_message"))
                                        .setConfirmText("OK")
                                        .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    @Override
                    public void onFailure(Call<OtpResult> call, Throwable throwable) {

                        Log.e("Error", throwable.toString());

                        String errot_text = ERROR_MESSAGE_NO_CONNECTION;
                        if (throwable instanceof SocketTimeoutException) {
                            errot_text = ERROR_MESSAGE_SLOW_CONNECTION;
                        } else if (throwable instanceof IllegalStateException) {
                            errot_text = ERROR_MESSAGE_WRONG_JSON_FORMAT;
                        } else if (throwable instanceof ConnectException) {
                            errot_text = ERROR_MESSAGE_NO_CONNECTION;
                        } else if (throwable instanceof UnknownHostException) {
                            errot_text = ERROR_MESSAGE_NO_CONNECTION;
                        } else {
                            errot_text = ERROR_MESSAGE_UNKNOWN;
                        }

                        pDialog.setTitleText(getString(R.string.oops))
                                .setContentText(errot_text)
                                .setConfirmText("OK")
                                .changeAlertType(SweetAlertDialog.ERROR_TYPE);

                    }
                });


            } catch (Exception e) {
                Log.e("Error", e.getMessage());
            }
        }
    }

    private boolean formValidation(){

        inputValidation = new InputValidation(this);

        if (!inputValidation.isEmptyText(otp1, getString(R.string.error_mesg))) {
            return false;
        }
        if (!inputValidation.isEmptyText(otp2, getString(R.string.error_mesg))) {
            return false;
        }
        if (!inputValidation.isEmptyText(otp3, getString(R.string.error_mesg))) {
            return false;
        }
        if (!inputValidation.isEmptyText(otp4, getString(R.string.error_mesg))) {
            return false;
        }

        return true;
    }

    public void resendOTP(){
        if(checkMobileData()){
            try{
                final SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
                        .setTitleText("Processing");
                pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.colorAccent));
                pDialog.show();
                pDialog.setCancelable(false);

                ApiInterface apiService =
                        ApiClient.getClient().create(ApiInterface.class);
                Call<ResendOTPResult> call = apiService.resendOPT(new ResendOTPData(userMobileCode, userMobileNumber));

                call.enqueue(new Callback<ResendOTPResult>() {
                    @Override
                    public void onResponse(Call<ResendOTPResult> call, Response<ResendOTPResult> response) {
                        int statusCode = response.code();
                        if (response.isSuccessful()) {
                            if(statusCode == 200) {
                                pDialog.setTitleText(getString(R.string.success))
                                        .setContentText(response.body().getSuccessMessage())
                                        .setConfirmText("OK")
                                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                            }
                        } else {
                            try {
                                JSONObject jObjError = new JSONObject(response.errorBody().string());
                                pDialog.setTitleText(getString(R.string.oops))
                                        .setContentText(jObjError.getString("error_message"))
                                        .setConfirmText("OK")
                                        .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                            }
                            catch (JSONException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResendOTPResult> call, Throwable throwable) {

                        Log.e("Error", throwable.toString());

                        String errot_text = ERROR_MESSAGE_NO_CONNECTION;
                        if (throwable instanceof SocketTimeoutException) {
                            errot_text = ERROR_MESSAGE_SLOW_CONNECTION;
                        } else if (throwable instanceof IllegalStateException) {
                            errot_text = ERROR_MESSAGE_WRONG_JSON_FORMAT;
                        } else if (throwable instanceof ConnectException) {
                            errot_text = ERROR_MESSAGE_NO_CONNECTION;
                        } else if (throwable instanceof UnknownHostException) {
                            errot_text = ERROR_MESSAGE_NO_CONNECTION;
                        } else {
                            errot_text = ERROR_MESSAGE_UNKNOWN;
                        }

                        pDialog.setTitleText(getString(R.string.oops))
                                .setContentText(errot_text)
                                .setConfirmText("OK")
                                .changeAlertType(SweetAlertDialog.ERROR_TYPE);

                    }
                });


            } catch (Exception e) {
                Log.e("Error", e.getMessage());
            }
        }
    }

    public boolean checkMobileData(){
        if (userMobileCode != null && !userMobileCode.isEmpty()) {
            if (userMobileNumber != null && !userMobileNumber.isEmpty()) {
                return true;
            }
        }
        new SweetAlertDialog(activity, SweetAlertDialog.ERROR_TYPE)
                .setTitleText(getString(R.string.oops))
                .setContentText(getString(R.string.data_missing))
                .show();
        return false;
    }

    public void registerDevice(final Response<OtpResult> responseData) {
        try {

            String access_token;
            access_token = "Bearer "+responseData.body().getAccessToken();

            ApiInterface apiService =
                    ApiClient.getClient().create(ApiInterface.class);
            Call<RegisterDeviceResult> call = apiService.devicesRegister(new RegisterDeviceData(android_id,
                    fcm_token), access_token);
            call.enqueue(new Callback<RegisterDeviceResult>() {
                @Override
                public void onResponse(Call<RegisterDeviceResult> call, Response<RegisterDeviceResult> response) {
                    int statusCode = response.code();
                    if (response.isSuccessful()) {
                        if(statusCode == 200) {
                            if(response.body().getSuccess().equals("device_register_success")) {
                                pDialog.setTitleText(getString(R.string.success))
                                        .setContentText(getString(R.string.opt_success))
                                        .setConfirmText("OK")
                                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        pDialog.dismissWithAnimation();
                                        saveDB(responseData);
                                    }
                                })
                                        .show();
                            }
                            else{
                                pDialog.setTitleText(getString(R.string.oops))
                                        .setContentText("Device Registration was failure!")
                                        .setConfirmText("OK")
                                        .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                            }

                        } else {
                            JSONObject jObjError = null;
                            try {
                                jObjError = new JSONObject(response.errorBody().string());

                                pDialog.setTitleText(getString(R.string.oops))
                                        .setContentText(jObjError.getString("error_message"))
                                        .setConfirmText("OK")
                                        .changeAlertType(SweetAlertDialog.ERROR_TYPE);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        JSONObject jObjError = null;
                        try {
                            jObjError = new JSONObject(response.errorBody().string());
                            pDialog.setTitleText(getString(R.string.oops))
                                    .setContentText(jObjError.getString("error_message"))
                                    .setConfirmText("OK")
                                    .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<RegisterDeviceResult> call, Throwable throwable) {

                    Log.e("Error", throwable.toString());
                    pDialog.dismissWithAnimation();
                }
            });

        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }
    }

    public void saveDB(Response<OtpResult> response){
        UserModel userdata = new UserModel();
        SettingsModel settingsdata = new SettingsModel();

        userdata.setUserId(response.body().getUser().getUserId());
        userdata.setName(response.body().getUser().getName());
        userdata.setMobile_no(response.body().getUser().getMobile());
        userdata.setCountry_code(response.body().getUser().getCountryCode());
        userdata.setUserType(response.body().getUser().getUserType());
        userdata.setSubType(response.body().getUser().getSubscriptionType());
        userdata.setAccounType(response.body().getUser().getAccountType());
        userdata.setIs_active("1");

        settingsdata.setId(1);
        settingsdata.setDevice_type("A");
        settingsdata.setDevice_token(android_id);
        settingsdata.setFcm_token(fcm_token);
        settingsdata.setAccessToken(response.body().getAccessToken());
        settingsdata.setActive_user(response.body().getUser().getUserId());
        settingsdata.setResourceServer(response.body().getResourceServer());

        DatabaseManager.saveUser(activity, userdata);
        DatabaseManager.addSettingsInfo(activity, settingsdata);

        session.setLogin(true);
        session.setUserID(response.body().getUser().getUserId());
        session.setKeyUserMob(response.body().getUser().getMobile());
        session.setAccessToken(response.body().getAccessToken());
//        syncLibrary();
        viewUpdateProfile();

    }

    public void viewUpdateProfile(){
        Intent intent = new Intent(activity, ProfileUpdateActivity.class);
        Bundle extras = new Bundle();
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (session.isLoggedIn()) {
            Intent intent = new Intent(activity, DashboardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }
}
