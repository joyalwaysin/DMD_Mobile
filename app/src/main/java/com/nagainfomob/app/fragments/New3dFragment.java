package com.nagainfomob.app.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidadvance.topsnackbar.TSnackbar;
import com.nagainfomob.app.DisplayMyDesign.GlobalVariables;
import com.nagainfomob.app.DisplayMyDesign.Room3DActivity;
import com.nagainfomob.app.R;
import com.nagainfomob.app.adapter.BottomSheetAdapter;
import com.nagainfomob.app.api.ApiClient;
import com.nagainfomob.app.api.ApiInterface;
import com.nagainfomob.app.helpers.InputValidation;
import com.nagainfomob.app.helpers.SessionManager;
import com.nagainfomob.app.main.DashboardActivity;
import com.nagainfomob.app.model.CreateProject.CreateProjectData;
import com.nagainfomob.app.model.CreateProject.CreateProjectResult;
import com.nagainfomob.app.model.ProjectsModel;
import com.nagainfomob.app.model.SettingsModel;
import com.nagainfomob.app.model.UnitList;
import com.nagainfomob.app.sql.DatabaseManager;

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


/**
 * Created by root on 2/2/17.
 */

public class New3dFragment extends Fragment implements View.OnClickListener {

    public static int flag = 0;
    public static int unit_flag = 0;
    private View view;
    private EditText input_unit;
    private EditText hidden_unit;
    private EditText hidden_unit_scale;
    private EditText project_name;
    private EditText customer_name;
    private EditText customer_email;
    private EditText customer_no;
    private EditText model_height;
    private EditText model_width;
    private EditText model_depth;

    private Button btn_submit;

    private SessionManager session;
    private String access_token;
    private String user_id;

    private ImageView ImgDownCountry;

    private BottomSheetAdapter chooseCountryAdapter;
    private BottomSheetBehavior bs_choose_contry;
    private RecyclerView recyclerViewCountry;
    private ProgressBar choose_country_progress;
    private InputValidation inputValidation;
    private TextView defaultViewCountry;

    ArrayList<String> ChooseUnitList_original = new ArrayList<>();
    ArrayList<String> ChooseUnitCode_original = new ArrayList<>();
    ArrayList<String> ChooseUnitScale_original = new ArrayList<>();
    ArrayList<String> ChooseUnitList = new ArrayList<>();
    ArrayList<String> ChooseUnitCode = new ArrayList<>();
    ArrayList<String> ChooseUnitScale = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_new_3d_project, container, false);

        initViews(view);
        initListeners();
        initObjects();

        initBottomSheet();
        session = new SessionManager(getActivity());
        session.setFragmentId("tab2");
        getCountryList();
        getDatafromdb();

        flag = 0;

        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    private void initViews(View view) {

        btn_submit = view.findViewById(R.id.btn_submit);
        input_unit = view.findViewById(R.id.input_company_unit);
        hidden_unit = view.findViewById(R.id.input_unit_hidden);
        hidden_unit_scale = view.findViewById(R.id.input_unit_scale_hidden);
        project_name = view.findViewById(R.id.project_name);
        customer_name = view.findViewById(R.id.customer_name);
        customer_no = view.findViewById(R.id.customer_no);
        customer_email = view.findViewById(R.id.customer_email);
        model_height = view.findViewById(R.id.model_height);
        model_width = view.findViewById(R.id.model_width);
        model_depth = view.findViewById(R.id.model_depth);

        defaultViewCountry = ((DashboardActivity) getActivity()).defaultViewCountry;
        choose_country_progress = ((DashboardActivity) getActivity()).choose_country_progress;
        ImgDownCountry = ((DashboardActivity) getActivity()).ImgDownCountry;
    }

    private void initObjects() {
        /*inputValidation = new InputValidation(getActivity());
        session = new SessionManager(getActivity());*/
    }

    private void initListeners() {

        btn_submit.setOnClickListener(this);
        ImgDownCountry.setOnClickListener(this);

        project_name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    collapseBottomSheet();
                }
            }
        });

        customer_name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    collapseBottomSheet();
                }
            }
        });

        model_height.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    collapseBottomSheet();
                }
            }
        });

        model_width.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    collapseBottomSheet();
                }
            }
        });

        model_depth.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    collapseBottomSheet();
                }
            }
        });

        customer_email.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    hideSoftKeyboard();
                    input_unit.requestFocus();
                }
                return false;
            }
        });
        //Choose Country

        RelativeLayout relative_benef_branch =(RelativeLayout) view.findViewById(R.id.CountryView);
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
        input_unit.setOnTouchListener(new View.OnTouchListener() {
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
        input_unit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit:
                doSubmit();
                break;
            case R.id.ImgDownCountry:
                collapseBottomSheet();
                break;
        }
    }

    private void getDatafromdb(){
//        List<UserModel> user;
        List<SettingsModel> settngs;

//        user = DatabaseManager.getUser(activity, session.getUserID());
        settngs = DatabaseManager.getSettings(getActivity());

//        if(user.get(0).getName() != null && user.get(0).getMobile_no() != null) {
//            input_name.setText(user.get(0).getName());
//            input_mobile.setText(user.get(0).getMobile_no());
//        }

        access_token = "Bearer "+settngs.get(0).getAccessToken();
    }

    public void doSubmit(){
        if(formValidation()){
            try{
                CreateProjectData projectData = new CreateProjectData();
                projectData.setName(project_name.getText().toString());
                projectData.setType("2");
                projectData.setCustomerName(customer_name.getText().toString());
                projectData.setMobile(customer_no.getText().toString());
                projectData.setEmail(customer_email.getText().toString());
                projectData.setRoomType("1");
                projectData.setWidth(model_width.getText().toString());
                projectData.setHeight(model_height.getText().toString());
                projectData.setDepth(model_depth.getText().toString());
                projectData.setUnit(hidden_unit.getText().toString());

                final SweetAlertDialog pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE)
                        .setTitleText("Processing");
                pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.colorAccent));
                pDialog.show();
                pDialog.setCancelable(false);

                ApiInterface apiService =
                        ApiClient.getClient().create(ApiInterface.class);
                Call<CreateProjectResult> call = apiService.createProject(projectData, access_token);

                call.enqueue(new Callback<CreateProjectResult>() {
                    @Override
                    public void onResponse(Call<CreateProjectResult> call, final Response<CreateProjectResult> response) {
                        int statusCode = response.code();
                        if (response.isSuccessful()) {
                            if(statusCode == 200) {
                                final String date = (DateFormat.format("dd-MM-yyyy hh:mm a", new java.util.Date()).toString());
                                saveDB(response);
                                pDialog.setTitleText(getString(R.string.success))
                                        .setContentText(response.body().getSuccessMessage())
                                        .setConfirmText("OK")
                                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        pDialog.dismissWithAnimation();
                                        view3D(project_name.getText().toString(), hidden_unit.getText().toString(),
                                                model_width.getText().toString(), model_height.getText().toString(),
                                                model_depth.getText().toString(), response.body().getData().getId().toString(),
                                                date, customer_name.getText().toString(), customer_no.getText().toString());
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
                    public void onFailure(Call<CreateProjectResult> call, Throwable throwable) {

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

    private void saveDB(Response<CreateProjectResult> response){

        String date = (DateFormat.format("dd-MM-yyyy hh:mm a", new java.util.Date()).toString());
        ProjectsModel project = new ProjectsModel();

        project.setProject_name(project_name.getText().toString());
        project.setProject_id(response.body().getData().getId());
        project.setShare_id(response.body().getData().getShareId());
        project.setUser_id(session.getUserID());
        project.setProject_type("2");
        project.setRoom_type("1");
        project.setUnit_id(hidden_unit.getText().toString());
        project.setUnit_name(input_unit.getText().toString());
        project.setUnit_scale(hidden_unit_scale.getText().toString());
        project.setWidth(model_width.getText().toString());
        project.setHeight(model_height.getText().toString());
        project.setDepth(model_depth.getText().toString());
        project.setCust_name(customer_name.getText().toString());
        project.setCust_mob(customer_no.getText().toString());
        project.setTime_stamp(date);
        project.setStatus("1");

        DatabaseManager.addProjectsInfo(getActivity(), project);

    }

    private boolean formValidation(){

        inputValidation = new InputValidation(getActivity());
        float w = 0 , h = 0, d = 0;
        float w_in_feet = 0, h_in_feet = 0, d_in_feet = 0;
        String unit;
        String err_mesg = "Room dimensions should be minimum 5 and maximum 25 feets";

        if(!inputValidation.isEmptyText(project_name, getString(R.string.enter) +
                " "+getString(R.string.project_name))) {
            return false;
        }
        /*if (!inputValidation.isEmptyText(customer_name, getString(R.string.enter) +
                " "+getString(R.string.customer_name))) {
            return false;
        }*/
        if(customer_no.getText().toString() != null && !customer_no.getText().toString().isEmpty()) {
            if (!inputValidation.lengthValidation(customer_no, getString(R.string.error_mobile_no), 10)) {
                return false;
            }
        }
        if(customer_email.getText().toString() != null && !customer_email.getText().toString().isEmpty()) {
            if (!inputValidation.isValidEmail(customer_email, getString(R.string.error_email))) {
                return false;
            }
        }
        if (!inputValidation.isEmptyText(input_unit, getString(R.string.error_unit))) {
            return false;
        }
        if (!inputValidation.isEmptyText(model_height, getString(R.string.enter) +
                " "+getString(R.string.height))) {
            return false;
        }
        if (!inputValidation.isEmptyText(model_width, getString(R.string.enter) +
                " "+getString(R.string.width))) {
            return false;
        }
        if (!inputValidation.isEmptyText(model_depth, getString(R.string.enter) +
                " "+getString(R.string.depth))) {
            return false;
        }

        unit = input_unit.getText().toString().trim();
        w = Float.parseFloat(model_width.getText().toString().trim());
        h = Float.parseFloat(model_height.getText().toString().trim());
        d = Float.parseFloat(model_depth.getText().toString().trim());

        if(unit.equalsIgnoreCase("millimetre")){
            w_in_feet = GlobalVariables.mmToFeet(w);
            h_in_feet = GlobalVariables.mmToFeet(h);
            d_in_feet = GlobalVariables.mmToFeet(d);
        }
        else if (unit.equalsIgnoreCase("inch")){
            w_in_feet = GlobalVariables.inchToFeet(w);
            h_in_feet = GlobalVariables.inchToFeet(h);
            d_in_feet = GlobalVariables.inchToFeet(d);
        }
        else{
            w_in_feet = w;
            h_in_feet = h;
            d_in_feet = d;
        }

        if(w_in_feet < 5 ||  w_in_feet > 25){
            model_width.setError(err_mesg);
            model_width.setFocusableInTouchMode(true);
            model_width.requestFocus();
            hideSoftKeyboard();

            return false;
        }

        if(h_in_feet < 5 ||  h_in_feet > 120){
            model_height.setError(err_mesg);
            model_height.setFocusableInTouchMode(true);
            model_height.requestFocus();
            hideSoftKeyboard();

            return false;
        }

        if(d_in_feet < 5 ||  d_in_feet > 120){
            model_depth.setError(err_mesg);
            model_depth.setFocusableInTouchMode(true);
            model_depth.requestFocus();
            hideSoftKeyboard();

            return false;
        }


        return true;
    }

    public void getCountryList() {
        try {
            ChooseUnitList_original.clear();
            ChooseUnitCode_original.clear();
            ChooseUnitList.clear();
            ChooseUnitCode.clear();

            chooseCountryAdapter = new BottomSheetAdapter(ChooseUnitList, (DashboardActivity) getActivity());
            recyclerViewCountry.setAdapter(chooseCountryAdapter);
            choose_country_progress.setVisibility(View.VISIBLE);
            defaultViewCountry.setVisibility(View.GONE);

            ApiInterface apiService =
                    ApiClient.getClient().create(ApiInterface.class);
            Call<List<UnitList>> call = apiService.getUnits();

            call.enqueue(new Callback<List<UnitList>>() {
                @Override
                public void onResponse(Call<List<UnitList>> call, Response<List<UnitList>> response) {
                    int statusCode = response.code();
                    choose_country_progress.setVisibility(View.GONE);
                    if (response.isSuccessful()) {
                        input_unit.setText(null);
                        if(statusCode == 200) {
                            unit_flag = 1;
                            for (int i = 0; i < response.body().size(); i++) {
                                ChooseUnitList_original.add(response.body().get(i).getName());
                                ChooseUnitCode_original.add(response.body().get(i).getId());
                                ChooseUnitScale_original.add(response.body().get(i).getScale());
                                ChooseUnitList.add(response.body().get(i).getName());
                                ChooseUnitCode.add(response.body().get(i).getId());
                                ChooseUnitScale.add(response.body().get(i).getScale());
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
                        TSnackbar.make(view.findViewById(android.R.id.content), response.message(),
                                TSnackbar.LENGTH_LONG);
                    }
                }

                @Override
                public void onFailure(Call<List<UnitList>> call, Throwable throwable) {

                    Log.e("Error", throwable.toString());

                    choose_country_progress.setVisibility(View.GONE);
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

                    defaultViewCountry.setVisibility(View.VISIBLE);
                    defaultViewCountry.setText(error_message);
                }
            });

        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }
    }

    private void initBottomSheet() {
//        View bottomSheet_choose_branch = view.findViewById(R.id.bottom_sheet_choose_country);
        View bottomSheet_choose_branch = ((DashboardActivity) getActivity()).bottomSheet_choose_branch;

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

        recyclerViewCountry = ((DashboardActivity) getActivity()).recyclerViewCountry;
        recyclerViewCountry.setHasFixedSize(true);
        recyclerViewCountry.setLayoutManager(new LinearLayoutManager(getActivity()));

        ViewGroup.LayoutParams params=recyclerViewCountry.getLayoutParams();
        params.height= 300;
        recyclerViewCountry.setLayoutParams(params);

        chooseCountryAdapter = new BottomSheetAdapter(ChooseUnitList, (DashboardActivity) getActivity());
        recyclerViewCountry.setAdapter(chooseCountryAdapter);
    }

    private void onStateExpanded(){
        ((DashboardActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((DashboardActivity) getActivity()).getSupportActionBar().setTitle("Please make a choice");
    }

    private void onStateCollapsed(){
        ((DashboardActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((DashboardActivity) getActivity()).getSupportActionBar().setTitle("Add Beneficiary");
    }

    public void hideSoftKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void collapseBottomSheet(){
        if(bs_choose_contry.getState() == 3) {
            bs_choose_contry.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    public void selectItem(String item){
        if(bs_choose_contry.getState() == 3) {
            input_unit.setError(null);
            input_unit.setText(item);
            hidden_unit.setText(ChooseUnitCode.get(ChooseUnitList.indexOf(item)));
            hidden_unit_scale.setText(ChooseUnitScale.get(ChooseUnitList.indexOf(item)));
            hideSoftKeyboard();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    bs_choose_contry.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    model_height.requestFocus();
                }
            }, 200);
        }
    }

    public void view3D(String projectName, String unit, String width, String height, String depth,
                       String projectId, String projectCreated, String custName, String custMob) {

        String unit_string = "Millimeter";
        float w = Float.valueOf(width);
        float h = Float.valueOf(height);
        float d = Float.valueOf(depth);

        switch (Integer.parseInt(unit)){
            case 2:
                w = GlobalVariables.inchesToMm(Float.valueOf(width));
                h = GlobalVariables.inchesToMm(Float.valueOf(height));
                d = GlobalVariables.inchesToMm(Float.valueOf(depth));
                unit_string = "Inches";
                break;
            case 3:
                w = GlobalVariables.feetToMm(Float.valueOf(width));
                h = GlobalVariables.feetToMm(Float.valueOf(height));
                d = GlobalVariables.feetToMm(Float.valueOf(depth));
                unit_string = "Feet";
                break;
        }

        if(w<=0.0 || h<=0.0 || d<=0.0 )
        {
            new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Oops...")
                    .setContentText("Something went wrong;  Invalid room dimesions!")
                    .show();
            return;
        }

        GlobalVariables.setProjectName(projectName);
        GlobalVariables.setUnit(unit_string);

        Intent intent = new Intent(getActivity(), Room3DActivity.class);
        intent.putExtra("projectName", projectName+"");
        intent.putExtra("projectCreated", projectCreated+"");
        intent.putExtra("width", w+"");
        intent.putExtra("height", h+"");
        intent.putExtra("depth", d+"");
        intent.putExtra("projectId", projectId+"");
        intent.putExtra("custName", custName+"");
        intent.putExtra("custMob", custMob+"");
        intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

        ((DashboardActivity) getActivity()).displaySelectedScreen(R.id.ib1);
    }


    @Override
    public void onResume() {
        super.onResume();

        /*getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){

                    if(bs_choose_contry.getState() == 3) {
                        collapseBottomSheet();
                    }
                    else{
                        if(flag++ > 0) {
                            FragmentManager fm = getFragmentManager();
                            if (fm.getBackStackEntryCount() > 0) {
                                fm.popBackStack();
                            }
                        }
                    }

                    return true;
                }
                return false;
            }
        });*/

    }

}