package com.nagainfomob.app.main;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidadvance.topsnackbar.TSnackbar;
import com.google.firebase.iid.FirebaseInstanceId;
import com.nagainfomob.app.R;
import com.nagainfomob.app.adapter.BottomSheetLAAdapter;
import com.nagainfomob.app.api.ApiClient;
import com.nagainfomob.app.api.ApiInterface;
import com.nagainfomob.app.helpers.InputValidation;
import com.nagainfomob.app.helpers.SessionManager;
import com.nagainfomob.app.model.CountryList;
import com.nagainfomob.app.model.Register.RegisterData;
import com.nagainfomob.app.model.Register.RegisterResult;

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

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener, BottomSheetLAAdapter.ItemListener {

    private final AppCompatActivity activity = RegisterActivity.this;
    private Uri mCropImageUri;
    private String fcm_token="";
    public static int unit_flag = 0;

    private LinearLayout layout_company_logo;

    private CheckBox terms_check;

    private ImageView img_close;
    private ImageView ImgDown;

    private TextView welcomeView;
    private TextView defaultViewCountry;
    private TextView txt_terms;
    private TextView txt_privacy;
    private TextView txt_terms_1;

    private EditText input_mobile;
    private EditText input_ccd;
    private EditText hidden_ccd;
    private EditText hidden_ccdiso;
    private EditText input_name;
    private EditText input_password;

    private String userType;
    private Button btn_Register;
    private SessionManager session;

    private BottomSheetLAAdapter chooseCountryAdapter;
    private BottomSheetBehavior bs_choose_contry;
    private RecyclerView recyclerViewCountry;
    private ProgressBar choose_country_progress;
    SearchView searchViewCountry;
    private InputValidation inputValidation;

    ArrayList<String> ChooseCountryList_original = new ArrayList<>();
    ArrayList<String> ChooseCountryCode_original = new ArrayList<>();
    ArrayList<String> ChooseCountryCodeISO_original = new ArrayList<>();
    ArrayList<String> ChooseCountryList = new ArrayList<>();
    ArrayList<String> ChooseCountryCode = new ArrayList<>();
    ArrayList<String> ChooseCountryCodeISO = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_register);
        fcm_token = FirebaseInstanceId.getInstance().getToken();

        initViews();
        initListeners();
        initBottomSheet();

        userType = getIntent().getExtras().getString("UserType");
        if(userType.toString().equals("2")) {
            welcomeView.setText("Welcome Manufacturer");
        }
        else if(userType.toString().equals("3")) {
            welcomeView.setText("Welcome Dealer");
        }

        session = new SessionManager(activity);
        getCountryList();
    }


    private void initViews() {

        choose_country_progress = findViewById(R.id.ChooseCountryProgress);
        img_close = findViewById(R.id. ImgClose);
        ImgDown = findViewById(R.id. ImgDown);
        welcomeView = findViewById(R.id. welcomeView);
        input_mobile = findViewById(R.id. input_mobile);
        btn_Register = findViewById(R.id. btn_Register);
        input_ccd = findViewById(R.id.input_ccd);
        hidden_ccd = findViewById(R.id.input_ccd_hidden);
        hidden_ccdiso = findViewById(R.id.input_ccdiso_hidden);
        defaultViewCountry = findViewById(R.id.defaultViewCountry);
        searchViewCountry = findViewById(R.id.countrySearchView);
        input_name = findViewById(R.id.input_name);
        input_password = findViewById(R.id.input_password);
        txt_terms = findViewById(R.id.txt_terms);
        txt_privacy = findViewById(R.id.txt_privacy);
        txt_terms_1 = findViewById(R.id.txt_terms_1);
        terms_check = findViewById(R.id.terms_check);

        searchViewCountry.setActivated(true);
        searchViewCountry.setQueryHint("Search Country");
        searchViewCountry.onActionViewExpanded();
        searchViewCountry.setIconified(false);
        searchViewCountry.clearFocus();
    }

    private void initObjects() {
        inputValidation = new InputValidation(this);
//        session = new SessionManager(getActivity());

    }

    private void initListeners() {

        img_close.setOnClickListener(this);
        ImgDown.setOnClickListener(this);
        btn_Register.setOnClickListener(this);
        txt_terms.setOnClickListener(this);
        txt_privacy.setOnClickListener(this);
        txt_terms_1.setOnClickListener(this);

        //Search Country

        searchViewCountry.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                filter_branch(newText);
                return false;
            }
        });

        //Choose Country

        RelativeLayout relative_benef_branch =(RelativeLayout) findViewById(R.id.CountryView);
        relative_benef_branch.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                hideSoftKeyboard();
                collapseBottomSheet();
                bs_choose_contry.setState(BottomSheetBehavior.STATE_EXPANDED);
                if(unit_flag == 0) {
                    getCountryList();
                }
            }
        });
        input_ccd.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(MotionEvent.ACTION_UP == event.getAction()) {
                    hideSoftKeyboard();
                    collapseBottomSheet();
                    bs_choose_contry.setState(BottomSheetBehavior.STATE_EXPANDED);
                    if(unit_flag == 0) {
                        getCountryList();
                    }
                }
                return true;
            }
        });
        input_ccd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    hideSoftKeyboard();
                    collapseBottomSheet();
                    bs_choose_contry.setState(BottomSheetBehavior.STATE_EXPANDED);
                    if(unit_flag == 0) {
                        getCountryList();
                    }
                }
            }
        });


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.ImgClose:
                finish();
                break;
            case R.id.ImgDown:
                collapseBottomSheet();
                break;
            case R.id.btn_Register:
                doRegister();
                break;
            case R.id.txt_terms:
                gotoWebView("T");
                break;
            case R.id.txt_terms_1:
                gotoWebView("T");
                break;
            case R.id.txt_privacy:
                gotoWebView("P");
                break;
        }
    }

    public void gotoWebView(String type){
        Intent intent = new Intent(RegisterActivity.this, ShowWebView.class);
        intent.putExtra("pageType",type);
        startActivity(intent);
    }

    public void doRegister(){
        if(formValidation()){
            try{
                final SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
                        .setTitleText("Processing");
                pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.colorAccent));
                pDialog.show();
                pDialog.setCancelable(false);

                ApiInterface apiService =
                        ApiClient.getClient().create(ApiInterface.class);
                Call<RegisterResult> call = apiService.registerUser(new RegisterData(input_name.getText().toString(),
                        input_password.getText().toString(), hidden_ccd.getText().toString(),
                        input_mobile.getText().toString(), userType));

                call.enqueue(new Callback<RegisterResult>() {
                    @Override
                    public void onResponse(Call<RegisterResult> call, Response<RegisterResult> response) {
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
                                        viewVerifyAvtivity();
                                    }
                                })
                                        .show();
                            }

                        } else {
                            try {
                                JSONObject jObjError = new JSONObject(response.errorBody().string());
                                if(statusCode == 403){ //Error
                                    pDialog.setTitleText(getString(R.string.oops))
                                            .setContentText(jObjError.getString("error_message"))
                                            .setConfirmText("OK")
                                            .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                                }
                                else if(statusCode == 405){ //Already
                                    pDialog.setTitleText(getString(R.string.exists))
                                            .setConfirmText("OK")
                                            .changeAlertType(SweetAlertDialog.WARNING_TYPE);
                                    pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            pDialog.dismissWithAnimation();
                                            Intent LoginIntent = new Intent(activity, LandingActivity.class);
                                            LoginIntent.setFlags(LoginIntent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                                            startActivity(LoginIntent);
                                        }
                                    })
                                            .show();
                                }
                                else{
                                    pDialog.setTitleText(getString(R.string.oops))
                                            .setContentText(jObjError.getString("error_message"))
                                            .setConfirmText("OK")
                                            .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<RegisterResult> call, Throwable throwable) {

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

    public void viewVerifyAvtivity(){
        Intent intent = new Intent(activity, NumberVerifyActivity.class);
        Bundle extras = new Bundle();
        extras.putString("UserMobileCode", hidden_ccd.getText().toString());
        extras.putString("UserMobileNumber", input_mobile.getText().toString());
        intent.putExtras(extras);
        startActivity(intent);
    }

    public void getCountryList() {
        try {
            ChooseCountryList_original.clear();
            ChooseCountryCode_original.clear();
            ChooseCountryList.clear();
            ChooseCountryCode.clear();
            chooseCountryAdapter = new BottomSheetLAAdapter(ChooseCountryList, RegisterActivity.this);
            recyclerViewCountry.setAdapter(chooseCountryAdapter);
            choose_country_progress.setVisibility(View.VISIBLE);
            defaultViewCountry.setVisibility(View.GONE);

            ApiInterface apiService =
                    ApiClient.getClient().create(ApiInterface.class);
            Call<List<CountryList>> call = apiService.getCountryList();

            call.enqueue(new Callback<List<CountryList>>() {
                @Override
                public void onResponse(Call<List<CountryList>> call, Response<List<CountryList>> response) {
                    int statusCode = response.code();
                    choose_country_progress.setVisibility(View.GONE);
                    if (response.isSuccessful()) {
                        input_ccd.setText(null);
                        if(statusCode == 200) {
                            unit_flag = 1;
                            for (int i = 0; i < response.body().size(); i++) {
                                ChooseCountryList_original.add(response.body().get(i).getCountryName()
                                        +" (" +response.body().get(i).getCountryCode()+")");
                                ChooseCountryCode_original.add(response.body().get(i).getCountryCode());
                                ChooseCountryCodeISO_original.add(response.body().get(i).getIso());
                                ChooseCountryList.add(response.body().get(i).getCountryName()
                                        +" (" +response.body().get(i).getCountryCode()+")");
                                ChooseCountryCode.add(response.body().get(i).getIso());
                                ChooseCountryCodeISO.add(response.body().get(i).getIso());
                                ChooseCountryCode.add(response.body().get(i).getCountryCode());
                            }

                            chooseCountryAdapter.notifyDataSetChanged();
                        } else {
                            /*if (checkFlagView == 1) {
                                return;
                            }*/
                            defaultViewCountry.setVisibility(View.VISIBLE);
                            defaultViewCountry.setText("Couldn't find details for the selected Bank.");
                        }
                    } else {
                        TSnackbar.make(findViewById(android.R.id.content), response.message(),
                                TSnackbar.LENGTH_LONG);
                    }
                }

                @Override
                public void onFailure(Call<List<CountryList>> call, Throwable throwable) {

                    Log.e("Error", throwable.toString());

                    choose_country_progress.setVisibility(View.GONE);
                    String error_message = ERROR_MESSAGE_NO_CONNECTION;

                    choose_country_progress.setVisibility(View.GONE);
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

                    defaultViewCountry.setVisibility(View.VISIBLE);
                    defaultViewCountry.setText(error_message);
                }
            });

        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }
    }

    private void initBottomSheet() {
        View bottomSheet_choose_branch = findViewById(R.id.bottom_sheet_choose_country);

        //Beneficiary Branch

        bs_choose_contry = BottomSheetBehavior.from(bottomSheet_choose_branch);
        bs_choose_contry.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    onStateExpanded();
                }
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    onStateCollapsed();
                    hideSoftKeyboard();
                }
            }
            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // React to dragging events
            }
        });

        recyclerViewCountry = findViewById(R.id.recyclerViewChooseCountry);
        recyclerViewCountry.setHasFixedSize(true);
        recyclerViewCountry.setLayoutManager(new LinearLayoutManager(activity));

        chooseCountryAdapter = new BottomSheetLAAdapter(ChooseCountryList, this);
        recyclerViewCountry.setAdapter(chooseCountryAdapter);
    }

    private void onStateExpanded(){
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setTitle("Please make a choice");
        input_ccd.setError(null);
    }

    private void onStateCollapsed(){
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add Beneficiary");
    }

    public void hideSoftKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onItemClick(String item) {
        if(bs_choose_contry.getState() == 3) {
            input_ccd.setError(null);
            input_ccd.setText(item);
            hidden_ccd.setText(ChooseCountryCode.get(ChooseCountryList.indexOf(item)));
            hidden_ccdiso.setText(ChooseCountryCodeISO.get(ChooseCountryList.indexOf(item)));
            hideSoftKeyboard();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    bs_choose_contry.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }, 200);
        }
    }

    private void filter_branch(String query) {
        final String lowerCaseQuery = query.toLowerCase();
        ChooseCountryList.clear();
        ChooseCountryCode.clear();
        ChooseCountryCodeISO.clear();

        for (String object: ChooseCountryList_original) {
            final String text = object.toLowerCase();
            if(text.contains(lowerCaseQuery)) {
                ChooseCountryList.add(object);
                ChooseCountryCode.add(ChooseCountryCode_original.get(ChooseCountryList_original.indexOf(object)));
                ChooseCountryCodeISO.add(ChooseCountryCodeISO_original.get(ChooseCountryList_original.indexOf(object)));
            }
        }
        chooseCountryAdapter.notifyDataSetChanged();
    }

    private void collapseBottomSheet(){
        if(bs_choose_contry.getState() == 3) {
            bs_choose_contry.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    private boolean formValidation(){

        inputValidation = new InputValidation(this);

        if (!inputValidation.isEmptyText(input_name, getString(R.string.error_message_name))) {
            return false;
        }
        if (!inputValidation.isEmptyText(input_ccd, getString(R.string.error_country_code))) {
            return false;
        }
        if (!inputValidation.isEmptyText(input_mobile, getString(R.string.error_mobile_no))) {
            return false;
        }
        if (!inputValidation.lengthValidation(input_mobile, getString(R.string.error_mobile_no), 10)) {
            return false;
        }
        if (!inputValidation.isEmptyText(input_password, getString(R.string.error_password))) {
            return false;
        }

        if(!terms_check.isChecked()){
            terms_check.setError(getString(R.string.error_terms));
            terms_check.setFocusableInTouchMode(true);
            terms_check.requestFocus();
            return false;
        }

        return true;
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

    @Override
    public void onBackPressed()
    {
        if(bs_choose_contry.getState() == 3) {
            bs_choose_contry.setState(BottomSheetBehavior.STATE_COLLAPSED);
            return;
        }
        else{
            super.onBackPressed();
        }
    }


}
