package com.nagainfomob.app.main;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidadvance.topsnackbar.TSnackbar;
import com.google.firebase.iid.FirebaseInstanceId;
import com.nagainfomob.app.R;
import com.nagainfomob.app.api.ApiClient;
import com.nagainfomob.app.api.ApiInterface;
import com.nagainfomob.app.helpers.InputValidation;
import com.nagainfomob.app.helpers.SessionManager;
import com.nagainfomob.app.model.ForgotPassword.ForgotPassData;
import com.nagainfomob.app.model.ForgotPassword.ForgotPassResult;

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

public class ForgotPassActivity extends AppCompatActivity implements View.OnClickListener {

    private final AppCompatActivity activity = ForgotPassActivity.this;
    private String storageURL = "https://storage-vro.s3.amazonaws.com/";
    private static int downloadingFlag = 0;
    private SweetAlertDialog dDialog;

    private ImageView img_close;

    private TextView txt_mobile;

    private EditText input_otp;
    private EditText input_password;
    private EditText input_password_c;

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
        setContentView(R.layout.activity_forgot_pass);

        initViews();
        initListeners();

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        userMobileCode = extras.getString("UserMobileCode");
        userMobileNumber = extras.getString("UserMobileNumber");

//        txt_mobile.setText(userMobileCode+" "+userMobileNumber);
        txt_mobile.setText(userMobileNumber);

        session = new SessionManager(activity);
        android_id = Settings.Secure.getString(activity.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        fcm_token = FirebaseInstanceId.getInstance().getToken();

    }

    private void initViews() {

        img_close = findViewById(R.id. ImgClose);
        txt_mobile = findViewById(R.id. txt_mobile);
        input_otp = findViewById(R.id. input_otp);
        input_password = findViewById(R.id. input_password);
        input_password_c = findViewById(R.id. input_password_c);

        btn_Verify = findViewById(R.id. btn_Verify);
    }

    private void initListeners() {
        img_close.setOnClickListener(this);
        btn_Verify.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ImgClose:
                finish();
                break;
            case R.id.btn_Verify:
                doReset();
                break;
        }
    }

    public void doReset(){

        if(formValidation() && checkMobileData()){
            try{
                String otp = input_otp.getText().toString().trim();
                String pass = input_password.getText().toString().trim();
                String pass_c = input_password_c.getText().toString().trim();

                final SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
                        .setTitleText("Processing");
                pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.colorAccent));
                pDialog.show();
                pDialog.setCancelable(false);

                ApiInterface apiService =
                        ApiClient.getClient().create(ApiInterface.class);
                Call<ForgotPassResult> call = apiService.resetPass(new ForgotPassData(
                        userMobileNumber, otp, pass, pass_c));

                call.enqueue(new Callback<ForgotPassResult>() {
                    @Override
                    public void onResponse(Call<ForgotPassResult> call, Response<ForgotPassResult> response) {
                        int statusCode = response.code();
                        if (response.isSuccessful()) {
                            if(statusCode == 200) {
                                pDialog.setTitleText(getString(R.string.success))
                                        .setContentText(response.body().getSuccessMessage())
                                        .setConfirmText("OK")
                                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        pDialog.dismissWithAnimation();
                                        finish();
                                    }
                                })
                                        .show();
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
                    public void onFailure(Call<ForgotPassResult> call, Throwable throwable) {

                        Log.e("Error", throwable.toString());
                        pDialog.dismissWithAnimation();

                        if (throwable instanceof SocketTimeoutException) {
                            TSnackbar.make(findViewById(android.R.id.content), ERROR_MESSAGE_SLOW_CONNECTION,
                                    TSnackbar.LENGTH_LONG).show();
                        } else if (throwable instanceof IllegalStateException) {
                            TSnackbar.make(findViewById(android.R.id.content), ERROR_MESSAGE_WRONG_JSON_FORMAT,
                                    TSnackbar.LENGTH_LONG).show();
                        } else if (throwable instanceof ConnectException) {
                            TSnackbar.make(findViewById(android.R.id.content), ERROR_MESSAGE_NO_CONNECTION,
                                    TSnackbar.LENGTH_LONG).show();
                        } else if (throwable instanceof UnknownHostException) {
                            TSnackbar.make(findViewById(android.R.id.content), ERROR_MESSAGE_NO_CONNECTION,
                                    TSnackbar.LENGTH_LONG).show();
                        } else {
                            TSnackbar.make(findViewById(android.R.id.content), ERROR_MESSAGE_UNKNOWN,
                                    TSnackbar.LENGTH_LONG).show();
                        }
                    }
                });


            } catch (Exception e) {
                Log.e("Error", e.getMessage());
            }
        }
    }

    private boolean formValidation(){

        inputValidation = new InputValidation(this);

        if (!inputValidation.isEmptyText(input_otp, getString(R.string.error_mesg))) {
            return false;
        }
        if (!inputValidation.isEmptyText(input_password, getString(R.string.error_mesg))) {
            return false;
        }
        if (!inputValidation.isEmptyText(input_password_c, getString(R.string.error_mesg))) {
            return false;
        }
        if (!inputValidation.isInputEditTextMatches(input_password, input_password_c, getString(R.string.error_match))) {
            return false;
        }

        return true;
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