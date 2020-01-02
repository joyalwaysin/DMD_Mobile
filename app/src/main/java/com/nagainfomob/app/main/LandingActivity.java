package com.nagainfomob.app.main;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.iid.FirebaseInstanceId;
import com.nagainfomob.app.R;
import com.nagainfomob.app.api.ApiClient;
import com.nagainfomob.app.api.ApiInterface;
import com.nagainfomob.app.helpers.InputValidation;
import com.nagainfomob.app.helpers.SessionManager;
import com.nagainfomob.app.model.CompanyModel;
import com.nagainfomob.app.model.ForgotPasswordOTP.ForgotPassOTPData;
import com.nagainfomob.app.model.ForgotPasswordOTP.ForgotPassOTPResult;
import com.nagainfomob.app.model.Login.LoginData;
import com.nagainfomob.app.model.Login.LoginResult;
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
import java.net.InetAddress;
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

public class LandingActivity extends AppCompatActivity implements View.OnClickListener {

    private final AppCompatActivity activity = LandingActivity.this;
    private String storageURL = "https://storage-vro.s3.amazonaws.com/";
    private static int downloadingFlag = 0;
    private String ccd = "0091";
    private Dialog dialog;

    private EditText username;
    private EditText password;

    private TextView txt_forgot_pswd;

    private TextInputLayout usernameLayout;

    private SessionManager session;
    private InputValidation inputValidation;

    private String android_id;
    private String fcm_token="";

    private SweetAlertDialog dDialog;
    private SweetAlertDialog pDialog;

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_landing);
        fcm_token = FirebaseInstanceId.getInstance().getToken();

        /*dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog);
        dialog.setCancelable(false);
        dialog.getWindow().setDimAmount(0.5f);*/

        verifyStoragePermissions(this);
        initViews();
        initListeners();

        session = new SessionManager(activity);
        android_id = Settings.Secure.getString(activity.getContentResolver(),
                Settings.Secure.ANDROID_ID);


//        saveImagetoStorage();

        DisplayMetrics metrics = new DisplayMetrics();
        LandingActivity.this.getWindowManager().getDefaultDisplay().getMetrics(metrics);

        float yInches = metrics.heightPixels / metrics.ydpi;
        float xInches = metrics.widthPixels / metrics.xdpi;
        double diagonalInches = Math.sqrt(xInches * xInches + yInches * yInches);

        if (diagonalInches < 5.2) {
            new SweetAlertDialog(LandingActivity.this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Screen Resolution Error")
                    .setContentText("Mobiles with screen size minimum of 5.2 inches required to run this application!")
                    .setConfirmText("OK")
                    .showCancelButton(true)
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();
                            finish();
                        }
                    })
                    .show();
        }

        if (isOnline()) {
            Log.d("Internet", "Available");
        } else {
            Log.d("Internet", "Not Available");
        }


            /*new SweetAlertDialog(LandingActivity.this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Internet Not Available")
                    .setContentText("Please check your Internet settings and try again")
                    .setCancelText("Exit")
                    .setConfirmText("Retry")
                    .showCancelButton(true)
                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();
                            finish();
                        }

                    })
                    .show();*/

//        getAccountType();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    private void initViews() {

        username = findViewById(R.id.input_username);
        password = findViewById(R.id.input_password);
        usernameLayout = (TextInputLayout) findViewById(R.id.usernameLayout);
        txt_forgot_pswd = (TextView) findViewById(R.id.txt_forgot_pswd);
    }

    private void initListeners() {
        txt_forgot_pswd.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txt_forgot_pswd:
                forgotPass();
                break;
        }
    }

    public void UserSelector(View view) {
//        Intent intent = new Intent(LandingActivity.this, ProfileUpdateActivity.class);
        Intent intent = new Intent(LandingActivity.this, UserSelectorActivity.class);
        startActivity(intent);
    }

    public void Login(View view) {
        if (formValidation()) {
            try {
                fcm_token = FirebaseInstanceId.getInstance().getToken();
                pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
                        .setTitleText("Processing");
                pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.colorAccent));
                pDialog.setCancelable(false);
                pDialog.show();

                ApiInterface apiService =
                        ApiClient.getClient().create(ApiInterface.class);
                Call<LoginResult> call = apiService.loginUser(new LoginData(username.getText().toString(),
                        password.getText().toString()));

                call.enqueue(new Callback<LoginResult>() {
                    @Override
                    public void onResponse(Call<LoginResult> call, Response<LoginResult> response) {
                        int statusCode = response.code();
                        if (response.isSuccessful()) {
                            if (statusCode == 200) {
                                if(fcm_token == null){
                                    pDialog.dismissWithAnimation();
                                    saveDB(response);
                                }
                                else{
                                    registerDevice(response);
                                }
                            }
                        } else {
                            try {
                                JSONObject jObjError = new JSONObject(response.errorBody().string());
                                if (statusCode == 403) { //Error
                                    pDialog.setTitleText(getString(R.string.oops))
                                            .setContentText(jObjError.getString("error_message"))
                                            .setConfirmText("OK")
                                            .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                                } else if (statusCode == 405) { //Authorized, continue login
                                    pDialog.setTitleText(getString(R.string.oops))
                                            .setContentText(jObjError.getString("error_message"))
                                            .setConfirmText("Resend OTP")
                                            .changeAlertType(SweetAlertDialog.WARNING_TYPE);
                                    pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            pDialog.dismissWithAnimation();
                                            resendOTP();
                                        }
                                    })
                                            .show();
                                } else if (statusCode == 402) { //paid user expired
                                    pDialog.setTitleText(getString(R.string.oops))
                                            .setContentText(getString(R.string.paid_expired))
                                            .setConfirmText("Subscribe")
                                            .changeAlertType(SweetAlertDialog.WARNING_TYPE);
                                    pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            pDialog.dismissWithAnimation();
                                            viewPayOptions(username.getText().toString());
                                        }
                                    })
                                            .show();
                                }
                                else if (statusCode == 406) { //Trial period expired
                                    pDialog.setTitleText(getString(R.string.pay_alert))
                                            .setContentText(getString(R.string.trail_expired))
                                            .setConfirmText("Subscribe")
                                            .changeAlertType(SweetAlertDialog.WARNING_TYPE);
                                    pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            pDialog.dismissWithAnimation();
                                            viewPayOptions(username.getText().toString());
                                        }
                                    })
                                            .show();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginResult> call, Throwable throwable) {

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

        /*Intent intent = new Intent(LandingActivity.this, DashboardActivity.class);
        startActivity(intent);*/

    }

    public void resendOTP() {
        try {
            final SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
                    .setTitleText("Processing");
            pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.colorAccent));
            pDialog.show();
            pDialog.setCancelable(false);

            ApiInterface apiService =
                    ApiClient.getClient().create(ApiInterface.class);
            Call<ResendOTPResult> call = apiService.resendOPT(new ResendOTPData(ccd, username.getText().toString()));

            call.enqueue(new Callback<ResendOTPResult>() {
                @Override
                public void onResponse(Call<ResendOTPResult> call, Response<ResendOTPResult> response) {
                    int statusCode = response.code();
                    if (response.isSuccessful()) {
                        if (statusCode == 200) {
                            pDialog.setTitleText(getString(R.string.success))
                                    .setContentText(response.body().getSuccessMessage())
                                    .setConfirmText(getString(R.string.verify_number))
                                    .changeAlertType(SweetAlertDialog.WARNING_TYPE);
                            pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    pDialog.dismissWithAnimation();
                                    viewVerifyAvtivity();
                                }
                            })
                                    .show();

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
                        } catch (JSONException e) {
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

    public void viewVerifyAvtivity() {
        Intent intent = new Intent(activity, NumberVerifyActivity.class);
        Bundle extras = new Bundle();
        extras.putString("UserMobileCode", "0091");
        extras.putString("UserMobileNumber", username.getText().toString());
        intent.putExtras(extras);
        startActivity(intent);
    }

    public void viewforgotPassAvtivity() {
        Intent intent = new Intent(activity, ForgotPassActivity.class);
        Bundle extras = new Bundle();
        extras.putString("UserMobileCode", "0091");
        extras.putString("UserMobileNumber", username.getText().toString());
        intent.putExtras(extras);
        startActivity(intent);
    }

    private boolean formValidation() {

        inputValidation = new InputValidation(this);

        if (!inputValidation.isEmptyText(username, getString(R.string.error_mobile_no))) {
            return false;
        }
        if (!inputValidation.lengthValidation(username, getString(R.string.error_mobile_no), 10)) {
            return false;
        }
        if (!inputValidation.isEmptyText(password, getString(R.string.error_password))) {
            return false;
        }

        return true;
    }

    private boolean forgotValidation() {

        inputValidation = new InputValidation(this);

        if (!inputValidation.isEmptyText(username, getString(R.string.error_mobile_no))) {
            return false;
        }
        if (!inputValidation.lengthValidation(username, getString(R.string.error_mobile_no), 10)) {
            return false;
        }

        return true;
    }

    public void registerDevice(final Response<LoginResult> responseData) {
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
                                pDialog.dismissWithAnimation();
                                saveDB(responseData);
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

    public void saveDB(Response<LoginResult> response) {
        UserModel userdata = new UserModel();
        SettingsModel settingsdata = new SettingsModel();
        CompanyModel compdata = new CompanyModel();

        userdata.setUserId(response.body().getUser().getUserId());
        userdata.setName(response.body().getUser().getName());
        userdata.setMobile_no(response.body().getUser().getMobile());
        userdata.setEmail(response.body().getUser().getEmail());
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

        compdata.setUserId(response.body().getUser().getUserId());
        if(!response.body().getUser().getOrganization().toString().trim().isEmpty())
            compdata.setComp_name(response.body().getUser().getOrganization());
        if(!response.body().getUser().getOrganizationDesc().toString().trim().isEmpty())
            compdata.setComp_desc(response.body().getUser().getOrganizationDesc());
        if(!response.body().getUser().getAddress1().toString().trim().isEmpty())
            compdata.setComp_addr1(response.body().getUser().getAddress1());
        if(!response.body().getUser().getCompanyLogo().toString().trim().isEmpty())
            compdata.setComp_logo_url(response.body().getUser().getCompanyLogo());
        if(!response.body().getUser().getPhone().toString().trim().equals("0"))
            compdata.setComp_phone_number(response.body().getUser().getPhone());
        if(!response.body().getUser().getCountryCode().toString().trim().isEmpty())
            compdata.setComp_country_code(response.body().getUser().getCountryCode());
        if(!response.body().getUser().getCountry().toString().trim().isEmpty())
            compdata.setComp_country_name(response.body().getUser().getCountryName());

        DatabaseManager.saveUser(activity, userdata);
        DatabaseManager.addSettingsInfo(activity, settingsdata);
        DatabaseManager.addCompanyInfo(activity, compdata);

        session.setLogin(true);
        session.setUserID(response.body().getUser().getUserId());
        session.setKeyUserMob(response.body().getUser().getMobile());
        session.setAccessToken(response.body().getAccessToken());
//        syncLibrary();
        viewDashboardAvtivity();

    }

    public void viewDashboardAvtivity() {

        Intent intent = new Intent(activity, DashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void forgotPass(){
        if(forgotValidation()){
            try {
                final SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
                        .setTitleText("Processing");
                pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.colorAccent));
                pDialog.show();
                pDialog.setCancelable(false);

                ApiInterface apiService =
                        ApiClient.getClient().create(ApiInterface.class);
                Call<ForgotPassOTPResult> call = apiService.forgotPass(new ForgotPassOTPData(username.getText().toString()));

                call.enqueue(new Callback<ForgotPassOTPResult>() {
                    @Override
                    public void onResponse(Call<ForgotPassOTPResult> call, Response<ForgotPassOTPResult> response) {
                        int statusCode = response.code();
                        if (response.isSuccessful()) {
                            if (statusCode == 200) {
                                pDialog.setTitleText(getString(R.string.success))
                                        .setContentText(response.body().getSuccessMessage())
                                        .setConfirmText(getString(R.string.reset_pass))
                                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        pDialog.dismissWithAnimation();
                                        viewforgotPassAvtivity();
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
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ForgotPassOTPResult> call, Throwable throwable) {

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

    @Override
    public void onResume() {
        super.onResume();

        if (session.isLoggedIn()) {
            Intent intent = new Intent(activity, DashboardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();

            viewDashboardAvtivity();
        }
    }

    public boolean isOnline() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com"); //You can replace it with your name
            return !ipAddr.equals("");

        } catch (Exception e) {
            return false;
        }
    }


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Landing Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    private void viewPayOptions(String user_mobile){

        Intent intent = new Intent(LandingActivity.this, PlanActivity.class);
        intent.putExtra("user_mobile", user_mobile);
        startActivity(intent);
    }
}
