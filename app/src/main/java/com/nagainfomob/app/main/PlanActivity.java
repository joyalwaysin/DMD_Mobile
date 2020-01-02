package com.nagainfomob.app.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nagainfomob.app.R;
import com.nagainfomob.app.adapter.PlanAdapter;
import com.nagainfomob.app.api.ApiClient;
import com.nagainfomob.app.api.ApiInterface;
import com.nagainfomob.app.helpers.SessionManager;
import com.nagainfomob.app.model.Payment.PaymentData;
import com.nagainfomob.app.model.Payment.PaymentResult;
import com.nagainfomob.app.model.Plan.PlanModel;
import com.nagainfomob.app.model.Plan.PlanResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.nagainfomob.app.api.ErrorUtils.ERROR_MESSAGE_NO_CONNECTION;
import static com.nagainfomob.app.api.ErrorUtils.ERROR_MESSAGE_SLOW_CONNECTION;
import static com.nagainfomob.app.api.ErrorUtils.ERROR_MESSAGE_UNKNOWN;
import static com.nagainfomob.app.api.ErrorUtils.ERROR_MESSAGE_WRONG_JSON_FORMAT;

public class PlanActivity extends AppCompatActivity implements View.OnClickListener {

    private final AppCompatActivity activity = PlanActivity.this;

    private SessionManager session;
    private ImageView img_close;
    private LinearLayout page_ph;
    private LinearLayout page_loader;
    private TextView ph_text;
    private EditText hidden_ccd;
    private String access_token;
    private String u_mobile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_plan);

        initViews();
        initListeners();
        getPlan();

        if(!getIntent().getExtras().getString("user_mobile").equals("")) {
            u_mobile = getIntent().getExtras().getString("user_mobile");
        }
        else{
            u_mobile = session.getKeyUserMob();
        }

    }

    private void initViews() {

        img_close = findViewById(R.id.ImgClose);
        ph_text = findViewById(R.id.ph_text);
        page_ph = findViewById(R.id.page_ph);
        page_loader = findViewById(R.id.page_loader);
        hidden_ccd = findViewById(R.id.input_ccd_hidden);

        session = new SessionManager(activity);
    }

    private void initListeners() {
        img_close.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.ImgClose:
                String message="";
                Intent intent=new Intent();
                intent.putExtra("MESSAGE",message);
                setResult(3,intent);
                finish();
                break;
        }
    }

    public void getPlan() {
        try {

            page_loader.setVisibility(View.VISIBLE);
            ApiInterface apiService =
                    ApiClient.getClient().create(ApiInterface.class);
            Call<List<PlanResponse>> call = apiService.getPlan();

            call.enqueue(new Callback<List<PlanResponse>>() {
                @Override
                public void onResponse(Call<List<PlanResponse>> call, Response<List<PlanResponse>> response) {
                    int statusCode = response.code();
                    if (response.isSuccessful()) {
//                        input_company_county.setText(null);
                        if(statusCode == 200) {
                            page_loader.setVisibility(View.GONE);
                            page_ph.setVisibility(View.GONE);
                            ArrayList<PlanModel> planModels = new ArrayList<>();
                            for (int i = 0; i < response.body().size(); i++) {
                                PlanModel model        =   new PlanModel();
                                model.id = response.body().get(i).getId();
                                model.name = response.body().get(i).getName();
                                model.description = response.body().get(i).getDescription();
                                model.price = response.body().get(i).getPrice();
                                model.duration = response.body().get(i).getDuration();
                                model.duration_type = response.body().get(i).getDurationType();
                                planModels.add(model);
                            }

                            RecyclerView recyclerView = findViewById(R.id.plan_item);
                            PlanAdapter recyclerAdapter = new PlanAdapter(getApplicationContext(), planModels, PlanActivity.this);
                            recyclerView.setHasFixedSize(false);

                            LinearLayoutManager manager_brand1 = new LinearLayoutManager(
                                    getApplicationContext(),
                                    LinearLayoutManager.HORIZONTAL,
                                    false
                            );
                            recyclerView.setLayoutManager(manager_brand1);
                            recyclerView.setAdapter(recyclerAdapter);


                        } else {
                            page_loader.setVisibility(View.GONE);
                            page_ph.setVisibility(View.VISIBLE);
                            ph_text.setText("Couldn't find details for the selected Bank.");
                        }
                    } else {
                        page_loader.setVisibility(View.GONE);
                        page_ph.setVisibility(View.VISIBLE);
                        ph_text.setText(response.message());
                    }
                }

                @Override
                public void onFailure(Call<List<PlanResponse>> call, Throwable throwable) {

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

                    page_loader.setVisibility(View.GONE);
                    page_ph.setVisibility(View.VISIBLE);
                    ph_text.setText(error_message);
                }
            });

        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        /*if (session.isLoggedIn()) {
            Intent intent = new Intent(activity, DashboardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }*/
    }

    public void requestPay(String plan_id, String price){

        try{

            final SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
                    .setTitleText("Processing");
            pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.colorAccent));
            pDialog.show();
            pDialog.setCancelable(false);

            ApiInterface apiService =
                    ApiClient.getClient().create(ApiInterface.class);
            Call<PaymentResult> call = apiService.requestPay(new PaymentData(
                    u_mobile, price, plan_id, "1"));

            call.enqueue(new Callback<PaymentResult>() {
                @Override
                public void onResponse(Call<PaymentResult> call, Response<PaymentResult> response) {
                    int statusCode = response.code();
                    if (response.isSuccessful()) {
                        if(statusCode == 200) {
                            pDialog.dismissWithAnimation();
                            proceedPay(response.body().getData().getUrl());
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
                public void onFailure(Call<PaymentResult> call, Throwable throwable) {

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
        }

    }

    private void proceedPay(String pay_url){

        finish();
        Intent intent = new Intent(PlanActivity.this, PaymentActivity.class);
        intent.putExtra("pay_url",pay_url);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

}
