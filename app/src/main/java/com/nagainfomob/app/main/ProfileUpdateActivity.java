package com.nagainfomob.app.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidadvance.topsnackbar.TSnackbar;
import com.nagainfomob.app.R;
import com.nagainfomob.app.adapter.BottomSheetLAAdapter;
import com.nagainfomob.app.api.ApiClient;
import com.nagainfomob.app.api.ApiInterface;
import com.nagainfomob.app.helpers.InputValidation;
import com.nagainfomob.app.helpers.SessionManager;
import com.nagainfomob.app.model.CompanyModel;
import com.nagainfomob.app.model.CountryList;
import com.nagainfomob.app.model.ProfileUpdate.UserData;
import com.nagainfomob.app.model.ProfileUpdate.UserResult;
import com.nagainfomob.app.model.SettingsModel;
import com.nagainfomob.app.model.UserModel;
import com.nagainfomob.app.sql.DatabaseManager;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

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

public class ProfileUpdateActivity extends AppCompatActivity implements View.OnClickListener, BottomSheetLAAdapter.ItemListener {

    private final AppCompatActivity activity = ProfileUpdateActivity.this;
    private Uri mCropImageUri;
    public static int unit_flag = 0;

    private LinearLayout layout_company_logo;
    private LinearLayout layout_company_lay;
    private SessionManager session;
    private ImageView img_company_logo;
    private ImageView img_close;
    private ImageView ImgDown;

    private TextView txt_skip;
    private TextView defaultViewCountry;

    private EditText input_company_name;
    private EditText input_company_desc;
    private EditText input_company_phone;
    private EditText input_company_address;
    private EditText input_company_county;
    private EditText input_name;
    private EditText input_mobile;
    private EditText input_email;
    private EditText hidden_ccd;
    private EditText input_company_logo;

    private Button btn_Upadte;

    private BottomSheetLAAdapter chooseCountryAdapter;
    private BottomSheetBehavior bs_choose_contry;
    private RecyclerView recyclerViewCountry;
    private ProgressBar choose_country_progress;
    SearchView searchViewCountry;
    private InputValidation inputValidation;

    ArrayList<String> ChooseCountryList_original = new ArrayList<>();
    ArrayList<String> ChooseCountryCode_original = new ArrayList<>();
    ArrayList<String> ChooseCountryList = new ArrayList<>();
    ArrayList<String> ChooseCountryCode = new ArrayList<>();

    private String access_token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_profile_update);

        initViews();
        initListeners();
        initBottomSheet();
        getCountryList();
        getDatafromdb();


    }

    private void initViews() {
        layout_company_lay = (LinearLayout) findViewById(R.id.CompLogoLay);
        layout_company_logo = (LinearLayout) findViewById(R.id.CompLogoView);
        img_company_logo = findViewById(R.id.CompLogoImg);
        img_close = findViewById(R.id.ImgClose);
        ImgDown = findViewById(R.id.ImgDown);
        txt_skip = findViewById(R.id.txt_skip);
        input_company_name = findViewById(R.id.input_company_name);
        input_company_desc = findViewById(R.id.input_company_desc);
        input_company_phone = findViewById(R.id.input_company_phone);
        input_company_address = findViewById(R.id.input_company_address);
        input_company_county = findViewById(R.id.input_company_county);
        input_company_logo = findViewById(R.id.input_company_logo);
        input_name = findViewById(R.id.input_name);
        input_mobile = findViewById(R.id.input_mobile);
        input_email = findViewById(R.id.input_email);
        defaultViewCountry = findViewById(R.id.defaultViewCountry);
        searchViewCountry = findViewById(R.id.countrySearchView);
        choose_country_progress = findViewById(R.id.ChooseCountryProgress);
        hidden_ccd = findViewById(R.id.input_ccd_hidden);
        btn_Upadte = findViewById(R.id.btn_Upadte );

        input_mobile.setEnabled(false);

        searchViewCountry.setActivated(true);
        searchViewCountry.setQueryHint("Search Country");
        searchViewCountry.onActionViewExpanded();
        searchViewCountry.setIconified(false);
        searchViewCountry.clearFocus();

        session = new SessionManager(activity);
    }

    private void initListeners() {
        layout_company_lay.setOnClickListener(this);
        img_close.setOnClickListener(this);
        ImgDown.setOnClickListener(this);
        txt_skip.setOnClickListener(this);
        btn_Upadte.setOnClickListener(this);

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
        input_company_county.setOnTouchListener(new View.OnTouchListener() {
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
        input_company_county.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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
            case R.id.CompLogoLay:
                captureImage(view);
                break;
            case R.id.ImgClose:
                finish();
                break;
            case R.id.ImgDown:
                collapseBottomSheet();
                break;
            case R.id.txt_skip:
                viewDash();
                break;
            case R.id.btn_Upadte:
                UpdateUser();
                break;
        }
    }

    public void captureImage(View view){
        onSelectImageClick(view);
    }

    /**
     * Start pick image activity with chooser.
     */
    public void onSelectImageClick(View view) {
        CropImage.startPickImageActivity(this);
    }

    @Override
    @SuppressLint("NewApi")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // handle result of pick image chooser
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri imageUri = CropImage.getPickImageResultUri(this, data);

            // For API >= 23 we need to check specifically that we have permissions to read external storage.
            if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)) {
                // request permissions and handle the result in onRequestPermissionsResult()
                mCropImageUri = imageUri;
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            } else {
                // no permissions required or already grunted, can start crop image activity
                startCropImageActivity(imageUri);
            }
        }

        // handle result of CropImageActivity
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                layout_company_logo.setPadding(0,0,0,0);
                img_company_logo.setImageURI(result.getUri());
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
//                Toast.makeText(this, "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // required permissions granted, start crop image activity
            startCropImageActivity(mCropImageUri);
        } else {
            Toast.makeText(this, "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Start crop image activity for the given image.
     */
    private void startCropImageActivity(Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .start(this);
    }

    private void viewDash(){
        Intent mainIntent = new Intent(activity, DashboardActivity.class);
        mainIntent.setFlags(mainIntent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(mainIntent);
    }

    private void initBottomSheet() {
        View bottom_sheet_choose_country = findViewById(R.id.bottom_sheet_choose_country);

        //Beneficiary Branch

        bs_choose_contry = BottomSheetBehavior.from(bottom_sheet_choose_country);
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
        //Beneficiary Bank

        if(bs_choose_contry.getState() == 3) {
            input_company_county.setError(null);
            input_company_county.setText(item);
            hidden_ccd.setText(ChooseCountryCode.get(ChooseCountryList.indexOf(item)));
            hideSoftKeyboard();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    bs_choose_contry.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }, 200);
        }
    }

    public void getCountryList() {
        try {
            ChooseCountryList_original.clear();
            ChooseCountryCode_original.clear();
            ChooseCountryList.clear();
            ChooseCountryCode.clear();
            chooseCountryAdapter = new BottomSheetLAAdapter(ChooseCountryList, ProfileUpdateActivity.this);
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
                        input_company_county.setText(null);
                        if(statusCode == 200) {
                            unit_flag = 1;
                            for (int i = 0; i < response.body().size(); i++) {
                                ChooseCountryList_original.add(response.body().get(i).getCountryName()
                                        +" (" +response.body().get(i).getCountryCode()+")");
                                ChooseCountryCode_original.add(response.body().get(i).getIso());
                                ChooseCountryList.add(response.body().get(i).getCountryName()
                                        +" (" +response.body().get(i).getCountryCode()+")");
                                ChooseCountryCode.add(response.body().get(i).getIso());
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
                    /*if (checkFlagView == 1) {
                        return;
                    }*/

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

    private void collapseBottomSheet(){
        if(bs_choose_contry.getState() == 3) {
            bs_choose_contry.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    private void filter_branch(String query) {
        final String lowerCaseQuery = query.toLowerCase();
        ChooseCountryList.clear();
        ChooseCountryCode.clear();

        for (String object: ChooseCountryList_original) {
            final String text = object.toLowerCase();
            if(text.contains(lowerCaseQuery)) {
                ChooseCountryList.add(object);
                ChooseCountryCode.add(ChooseCountryCode_original.get(ChooseCountryList_original.indexOf(object)));
            }
        }
        chooseCountryAdapter.notifyDataSetChanged();
    }

    private void getDatafromdb(){
        List<UserModel> user;
        List<SettingsModel> settngs;

        user = DatabaseManager.getUser(activity, session.getUserID());
        settngs = DatabaseManager.getSettings(activity);

        if(user.get(0).getName() != null && user.get(0).getMobile_no() != null) {
            input_name.setText(user.get(0).getName());
            input_mobile.setText(user.get(0).getMobile_no());
        }

        access_token = "Bearer "+settngs.get(0).getAccessToken();

        Log.d("Token", "Update Profile - "+ settngs.get(0).getAccessToken());
    }

    public void UpdateUser(){
        if(formValidation()){
            try{
                final SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
                        .setTitleText("Processing");
                pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.colorAccent));
                pDialog.show();
                pDialog.setCancelable(false);

                ApiInterface apiService =
                        ApiClient.getClient().create(ApiInterface.class);
                Call<UserResult> call = apiService.updateUser(new UserData(input_name.getText().toString(),
                                input_email.getText().toString(), input_company_name.getText().toString(),
                                input_company_desc.getText().toString(), input_company_phone.getText().toString(),
                                input_company_address.getText().toString(), "", hidden_ccd.getText().toString(), ""),
                        access_token);

                call.enqueue(new Callback<UserResult>() {
                    @Override
                    public void onResponse(Call<UserResult> call, Response<UserResult> response) {
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
//                                        viewDash();
                                        saveDB();
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
                    public void onFailure(Call<UserResult> call, Throwable throwable) {

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

    public void saveDB() {
        UserModel userdata = new UserModel();
        CompanyModel compdata = new CompanyModel();

        userdata.setUserId(session.getUserID().toString());
        userdata.setName(input_name.getText().toString());
        userdata.setEmail(input_email.getText().toString());
        userdata.setIs_active("1");

        compdata.setUserId(session.getUserID().toString());
        if(!input_company_name.getText().toString().trim().isEmpty())
            compdata.setComp_name(input_company_name.getText().toString());
        if(!input_company_desc.getText().toString().trim().isEmpty())
            compdata.setComp_desc(input_company_desc.getText().toString());
        if(!input_company_address.getText().toString().trim().isEmpty())
            compdata.setComp_addr1(input_company_address.getText().toString());
        if(!input_company_phone.getText().toString().trim().equals("0"))
            compdata.setComp_phone_number(input_company_phone.getText().toString());
        if(!hidden_ccd.getText().toString().trim().isEmpty())
            compdata.setComp_country_code(hidden_ccd.getText().toString());
        if(!input_company_county.getText().toString().trim().isEmpty())
            compdata.setComp_country_name(input_company_county.getText().toString());

        DatabaseManager.saveUser(activity, userdata);
        DatabaseManager.addCompanyInfo(activity, compdata);
        viewDash();

    }

    private boolean formValidation(){

        inputValidation = new InputValidation(this);

        if (!inputValidation.isEmptyText(input_name, getString(R.string.error_message_name))) {
            return false;
        }
        if (!inputValidation.isEmptyText(input_mobile, getString(R.string.error_mobile_no))) {
            return false;
        }
        if (!inputValidation.lengthValidation(input_mobile, getString(R.string.error_mobile_no), 10)) {
            return false;
        }

        return true;
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
