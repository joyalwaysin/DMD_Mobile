package com.nagainfomob.app.main;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.nagainfomob.app.R;
import com.nagainfomob.app.api.ApiClient;
import com.nagainfomob.app.api.ApiInterface;
import com.nagainfomob.app.helpers.InputValidation;
import com.nagainfomob.app.helpers.SessionManager;
import com.nagainfomob.app.model.ChangePassData;
import com.nagainfomob.app.model.ResponseModel;
import com.nagainfomob.app.model.SettingsModel;
import com.nagainfomob.app.sql.DatabaseManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.nagainfomob.app.api.ErrorUtils.ERROR_MESSAGE_NO_CONNECTION;
import static com.nagainfomob.app.api.ErrorUtils.ERROR_MESSAGE_SLOW_CONNECTION;
import static com.nagainfomob.app.api.ErrorUtils.ERROR_MESSAGE_UNKNOWN;
import static com.nagainfomob.app.api.ErrorUtils.ERROR_MESSAGE_WRONG_JSON_FORMAT;

public class ChangePassActivity extends AppCompatActivity implements View.OnClickListener {

    private final AppCompatActivity activity = ChangePassActivity.this;
    private String storageURL = "https://storage-vro.s3.amazonaws.com/";
    private static int downloadingFlag = 0;
    private SweetAlertDialog dDialog;

    private ImageView img_close;

    private EditText input_old_password;
    private EditText input_password;
    private EditText input_password_c;

    private Button btn_Change_Pass;

    private InputValidation inputValidation;
    private SweetAlertDialog pDialog;
    private SessionManager session;
    private String access_token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_change_pass);

        initViews();
        initListeners();
        getDatafromdb();

        session = new SessionManager(activity);

    }

    private void initViews() {

        img_close = findViewById(R.id. ImgClose);
        input_old_password = findViewById(R.id. input_old_password);
        input_password = findViewById(R.id. input_password);
        input_password_c = findViewById(R.id. input_password_c);

        btn_Change_Pass = findViewById(R.id. btn_Change_Pass);
    }

    private void initListeners() {
        img_close.setOnClickListener(this);
        btn_Change_Pass.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ImgClose:
                finish();
                break;
            case R.id.btn_Change_Pass:
                doChangePass();
                break;
        }
    }

    private void getDatafromdb(){
        List<SettingsModel> settngs;
        settngs = DatabaseManager.getSettings(this);
        access_token = "Bearer "+settngs.get(0).getAccessToken();
    }

    public void doChangePass(){

        if(formValidation()){
            try{
                String old_pass = input_old_password.getText().toString().trim();
                String pass = input_password.getText().toString().trim();
                String pass_c = input_password_c.getText().toString().trim();

                final SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
                        .setTitleText("Processing");
                pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.colorAccent));
                pDialog.show();
                pDialog.setCancelable(false);

                ApiInterface apiService =
                        ApiClient.getClient().create(ApiInterface.class);
                Call<ResponseModel> call = apiService.changePass(new ChangePassData(
                        session.getKeyUserMob(), old_pass, pass, pass_c), access_token);

                call.enqueue(new Callback<ResponseModel>() {
                    @Override
                    public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
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
                    public void onFailure(Call<ResponseModel> call, Throwable throwable) {

                        Log.e("Error", throwable.toString());
                        String error_message = ERROR_MESSAGE_NO_CONNECTION;

                        if (throwable instanceof SocketTimeoutException) {
                            error_message = ERROR_MESSAGE_SLOW_CONNECTION;
                        } else if (throwable instanceof IllegalStateException) {
                            error_message = ERROR_MESSAGE_WRONG_JSON_FORMAT;
                        } else if (throwable instanceof ConnectException) {
                            error_message = ERROR_MESSAGE_NO_CONNECTION;
                        } else if (throwable instanceof UnknownHostException) {
                            error_message = ERROR_MESSAGE_NO_CONNECTION;
                        } else {
                            error_message = ERROR_MESSAGE_UNKNOWN;
                        }

                        pDialog.setTitleText(getString(R.string.oops))
                                .setContentText(error_message)
                                .setConfirmText("OK")
                                .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                    }
                });


            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                pDialog.dismissWithAnimation();
            }
        }
    }

    private boolean formValidation(){

        inputValidation = new InputValidation(this);

        if (!inputValidation.isEmptyText(input_old_password, getString(R.string.error_mesg))) {
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

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
    }

}