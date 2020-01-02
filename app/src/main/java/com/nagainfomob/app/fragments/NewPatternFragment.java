package com.nagainfomob.app.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.nagainfomob.app.DisplayMyDesign.CustomPatternActivity;
import com.nagainfomob.app.DisplayMyDesign.GlobalVariables;
import com.nagainfomob.app.R;
import com.nagainfomob.app.adapter.BottomSheetAdapter;
import com.nagainfomob.app.api.ApiClient;
import com.nagainfomob.app.api.ApiInterface;
import com.nagainfomob.app.helpers.InputValidation;
import com.nagainfomob.app.helpers.SessionManager;
import com.nagainfomob.app.main.DashboardActivity;
import com.nagainfomob.app.model.SettingsModel;
import com.nagainfomob.app.model.TileCategoryModel;
import com.nagainfomob.app.model.TileTypeModel;
import com.nagainfomob.app.model.UnitList;
import com.nagainfomob.app.sql.DatabaseManager;

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

public class NewPatternFragment extends Fragment implements View.OnClickListener {

    private View view;

    private static int unit_flag = 0;
    private static int category_flag = 0;
    private static int type_flag = 0;

    private EditText input_unit;
    private EditText hidden_unit;
    private EditText hidden_unit_scale;
    private EditText pattern_name;
    private EditText pattern_height;
    private EditText pattern_width;
    private EditText pattern_price;
    private EditText pattern_brand;
    private EditText input_category;
    private EditText hidden_category;
    private EditText input_type;
    private EditText hidden_type;

    private Button btn_submit;

    private ImageView ImgDownCountry;
    private ImageView ImgDownCategory;
    private ImageView ImgDownType;

    private SessionManager session;
    private String access_token;

    private BottomSheetAdapter chooseUnitAdapter;
    private BottomSheetAdapter chooseCategoryAdapter;
    private BottomSheetAdapter chooseTypeAdapter;
    private BottomSheetBehavior bs_choose_unit;
    private BottomSheetBehavior bs_choose_category;
    private BottomSheetBehavior bs_choose_type;
    private RecyclerView recyclerViewUnit;
    private RecyclerView recyclerViewCategory;
    private RecyclerView recyclerViewType;
    private ProgressBar choose_unit_progress;
    private ProgressBar choose_category_progress;
    private ProgressBar choose_type_progress;

    private InputValidation inputValidation;
    private TextView defaultViewCountry;
    private TextView defaultViewCatrgory;
    private TextView defaultViewType;

    ArrayList<String> ChooseUnitList_original = new ArrayList<>();
    ArrayList<String> ChooseUnitCode_original = new ArrayList<>();
    ArrayList<String> ChooseUnitScale_original = new ArrayList<>();
    ArrayList<String> ChooseUnitList = new ArrayList<>();
    ArrayList<String> ChooseUnitCode = new ArrayList<>();
    ArrayList<String> ChooseUnitScale = new ArrayList<>();

    ArrayList<String> CategoryList = new ArrayList<>();
    ArrayList<String> CategoryCode = new ArrayList<>();

    ArrayList<String> TypeList = new ArrayList<>();
    ArrayList<String> TypeCode = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_new_pattern, container, false);
        session = new SessionManager(getActivity());
        session.setFragmentId("tab4.1");

        initViews(view);
        initListeners();
        initObjects();

        initBottomSheetUnit();
        initBottomSheetCategory();
        initBottomSheetType();
        getUnitList();
        getCategoryList();
        getTypeList();
        getDatafromdb();

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
        pattern_name = view.findViewById(R.id.pattern_name);
        pattern_height = view.findViewById(R.id.pattern_height);
        pattern_width = view.findViewById(R.id.pattern_width);
        pattern_price = view.findViewById(R.id.pattern_price);
        pattern_brand = view.findViewById(R.id.pattern_brand);
        input_category = view.findViewById(R.id.input_category);
        hidden_category = view.findViewById(R.id.input_category_hidden);
        input_type = view.findViewById(R.id.input_type);
        hidden_type = view.findViewById(R.id.input_type_hidden);

        defaultViewCountry = ((DashboardActivity) getActivity()).defaultViewCountry;
        choose_unit_progress = ((DashboardActivity) getActivity()).choose_country_progress;

        defaultViewCatrgory = ((DashboardActivity) getActivity()).defaultViewCategory;
        choose_category_progress = ((DashboardActivity) getActivity()).choose_category_progress;

        defaultViewType = ((DashboardActivity) getActivity()).defaultViewType;
        choose_type_progress = ((DashboardActivity) getActivity()).choose_type_progress;

        ImgDownCountry = ((DashboardActivity) getActivity()).ImgDownCountry;
        ImgDownCategory = ((DashboardActivity) getActivity()).ImgDownCategory;
        ImgDownType = ((DashboardActivity) getActivity()).ImgDownType;

    }

    private void initObjects() {
        /*inputValidation = new InputValidation(getActivity());
        session = new SessionManager(getActivity());*/
    }

    private void initListeners() {

        btn_submit.setOnClickListener(this);
        ImgDownCountry.setOnClickListener(this);
        ImgDownCategory.setOnClickListener(this);
        ImgDownType.setOnClickListener(this);
        ImgDownCountry.setOnClickListener(this);
        ImgDownCountry.setOnClickListener(this);
        ImgDownCountry.setOnClickListener(this);

        pattern_name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    collapseBottomSheet();
                }
            }
        });

        pattern_brand.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    collapseBottomSheet();
                }
            }
        });

        pattern_width.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    collapseBottomSheet();
                }
            }
        });

        pattern_height.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    collapseBottomSheet();
                }
            }
        });

        pattern_price.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    collapseBottomSheet();
                }
            }
        });

        pattern_brand.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    hideSoftKeyboard();
                    input_unit.requestFocus();
                }
                return false;
            }
        });

        //Choose Unit

        RelativeLayout relative_unit =(RelativeLayout) view.findViewById(R.id.UnitView);
        relative_unit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                hideSoftKeyboard();
                collapseBottomSheet();
                bs_choose_unit.setState(BottomSheetBehavior.STATE_EXPANDED);
                if(unit_flag == 0) {
                    getUnitList();
                }

            }
        });
        input_unit.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(MotionEvent.ACTION_UP == event.getAction()) {
                    hideSoftKeyboard();
                    collapseBottomSheet();
                    bs_choose_unit.setState(BottomSheetBehavior.STATE_EXPANDED);
                    if(unit_flag == 0) {
                        getUnitList();
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
                    bs_choose_unit.setState(BottomSheetBehavior.STATE_EXPANDED);
                    if(unit_flag == 0) {
                        getUnitList();
                    }
                }
            }
        });

        //Choose Tile Category

        RelativeLayout relative_category =(RelativeLayout) view.findViewById(R.id.CategoryView);
        relative_category.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                hideSoftKeyboard();
                collapseBottomSheet();
                bs_choose_category.setState(BottomSheetBehavior.STATE_EXPANDED);
                if(category_flag == 0) {
                    getCategoryList();
                }
            }
        });
        input_category.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(MotionEvent.ACTION_UP == event.getAction()) {
                    hideSoftKeyboard();
                    collapseBottomSheet();
                    bs_choose_category.setState(BottomSheetBehavior.STATE_EXPANDED);
                    if(category_flag == 0) {
                        getCategoryList();
                    }
                }
                return true;
            }
        });
        input_category.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    hideSoftKeyboard();
                    collapseBottomSheet();
                    bs_choose_category.setState(BottomSheetBehavior.STATE_EXPANDED);
                    if(category_flag == 0) {
                        getCategoryList();
                    }
                }
            }
        });

        //Choose Tile Type

        RelativeLayout relative_type =(RelativeLayout) view.findViewById(R.id.TypeView);
        relative_type.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                hideSoftKeyboard();
                collapseBottomSheet();
                bs_choose_type.setState(BottomSheetBehavior.STATE_EXPANDED);
                if(type_flag == 0) {
                    getTypeList();
                }
            }
        });
        input_type.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(MotionEvent.ACTION_UP == event.getAction()) {
                    hideSoftKeyboard();
                    collapseBottomSheet();
                    bs_choose_type.setState(BottomSheetBehavior.STATE_EXPANDED);
                    if(type_flag == 0) {
                        getTypeList();
                    }
                }
                return true;
            }
        });
        input_type.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    hideSoftKeyboard();
                    collapseBottomSheet();
                    bs_choose_type.setState(BottomSheetBehavior.STATE_EXPANDED);
                    if(type_flag == 0) {
                        getTypeList();
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
            case R.id.ImgDownCategory:
                collapseBottomSheet();
                break;
            case R.id.ImgDownType:
                collapseBottomSheet();
                break;
        }
    }

    private void getDatafromdb(){
        List<SettingsModel> settngs;

        settngs = DatabaseManager.getSettings(getActivity());
        access_token = "Bearer "+settngs.get(0).getAccessToken();
    }

    public void doSubmit(){
        if(formValidation()) {

            session.setBackStackId("2");

            float w = Float.valueOf(pattern_width.getText().toString());
            float h = Float.valueOf(pattern_height.getText().toString());

            switch (Integer.parseInt(hidden_unit.getText().toString())){
                case 2:
                    w = GlobalVariables.inchesToMm(w);
                    h = GlobalVariables.inchesToMm(h);
                    break;
                case 3:
                    w = GlobalVariables.feetToMm(w);
                    h = GlobalVariables.feetToMm(h);
                    break;
            }

            if(w<=0.0 || h<=0.0  )
            {
                new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Oops...")
                        .setContentText("Something went wrong;  Invalid room dimesions!")
                        .show();
                return;
            }

            GlobalVariables.setProjectName(pattern_name.getText().toString());
            GlobalVariables.setUnit("Millimeter");
            GlobalVariables.setDesignerUnit(GlobalVariables.getUnit());
            GlobalVariables.setWallDim(h, w, 0, 0 ,0);

            Intent intent = new Intent(getActivity(), CustomPatternActivity.class);
            intent.putExtra("name", pattern_name.getText().toString());
            intent.putExtra("width", w+"");
            intent.putExtra("height", h+"");
            intent.putExtra("category", input_category.getText().toString());
            intent.putExtra("category_id", hidden_category.getText().toString());
            intent.putExtra("price", pattern_price.getText().toString());
            intent.putExtra("brand", pattern_brand.getText().toString());
            intent.putExtra("type", input_type.getText().toString());
            intent.putExtra("type_id", hidden_type.getText().toString());
            intent.putExtra("is_edit", "0");
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        }
    }

    private boolean formValidation(){

        inputValidation = new InputValidation(getActivity());

        if (!inputValidation.isEmptyText(pattern_name, getString(R.string.enter) +
                " "+getString(R.string.pattern_name))) {
            return false;
        }
        if (!inputValidation.isEmptyText(input_category, getString(R.string.choose) +
                " "+getString(R.string.tile_category))) {
            return false;
        }
        if (!inputValidation.isEmptyText(input_type, getString(R.string.choose) +
                " "+getString(R.string.tile_type))) {
            return false;
        }
        if (!inputValidation.isEmptyText(pattern_brand, getString(R.string.enter) +
                " "+getString(R.string.pattern_brand))) {
            return false;
        }
        if (!inputValidation.isEmptyText(input_unit, getString(R.string.error_unit))) {
            return false;
        }
        if (!inputValidation.isEmptyText(pattern_width, getString(R.string.enter) +
                " "+getString(R.string.pattern_width))) {
            return false;
        }
        if (!inputValidation.isEmptyText(pattern_height, getString(R.string.enter) +
                " "+getString(R.string.pattern_height))) {
            return false;
        }
        if (!inputValidation.isEmptyText(pattern_price, getString(R.string.enter) +
                " "+getString(R.string.pattern_price))) {
            return false;
        }

        return true;
    }

    public void getUnitList() {
        try {
            ChooseUnitList_original.clear();
            ChooseUnitCode_original.clear();
            ChooseUnitList.clear();
            ChooseUnitCode.clear();
            chooseUnitAdapter = new BottomSheetAdapter(ChooseUnitList, (DashboardActivity) getActivity());
            recyclerViewUnit.setAdapter(chooseUnitAdapter);
            choose_unit_progress.setVisibility(View.VISIBLE);
            defaultViewCountry.setVisibility(View.GONE);

            ApiInterface apiService =
                    ApiClient.getClient().create(ApiInterface.class);
            Call<List<UnitList>> call = apiService.getUnits();

            call.enqueue(new Callback<List<UnitList>>() {
                @Override
                public void onResponse(Call<List<UnitList>> call, Response<List<UnitList>> response) {
                    int statusCode = response.code();
                    choose_unit_progress.setVisibility(View.GONE);
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

                            chooseUnitAdapter.notifyDataSetChanged();
                        } else {
                            /*if (checkFlagView == 1) {
                                return;
                            }*/
                            defaultViewCountry.setVisibility(View.VISIBLE);
                            defaultViewCountry.setText("Couldn't find details.");
                        }
                    } else {
                        TSnackbar.make(view.findViewById(android.R.id.content), response.message(),
                                TSnackbar.LENGTH_LONG);
                    }
                }

                @Override
                public void onFailure(Call<List<UnitList>> call, Throwable throwable) {

                    Log.e("Error", throwable.toString());
                    /*if (checkFlagView == 1) {
                        return;
                    }*/

                    choose_unit_progress.setVisibility(View.GONE);
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

    public void getCategoryList() {
        try {
            String token = "Bearer " + session.getAccessToken();
            CategoryList.clear();
            CategoryCode.clear();
            chooseCategoryAdapter = new BottomSheetAdapter(CategoryList, (DashboardActivity) getActivity());
            recyclerViewCategory.setAdapter(chooseCategoryAdapter);
            choose_category_progress.setVisibility(View.VISIBLE);
            defaultViewCatrgory.setVisibility(View.GONE);

            ApiInterface apiService =
                    ApiClient.getClient().create(ApiInterface.class);
            Call<TileCategoryModel> call = apiService.getTileCategory(token);

            call.enqueue(new Callback<TileCategoryModel>() {
                @Override
                public void onResponse(Call<TileCategoryModel> call, Response<TileCategoryModel> response) {
                    int statusCode = response.code();
                    choose_category_progress.setVisibility(View.GONE);
                    if (response.isSuccessful()) {
                        input_category.setText(null);
                        if(statusCode == 200) {
                            category_flag = 1;
                            for (int i = 0; i < response.body().getData().size(); i++) {
                                CategoryList.add(response.body().getData().get(i).getName());
                                CategoryCode.add(response.body().getData().get(i).getId());
                            }

                            chooseCategoryAdapter.notifyDataSetChanged();
                        } else {
                            /*if (checkFlagView == 1) {
                                return;
                            }*/
                            defaultViewCatrgory.setVisibility(View.VISIBLE);
                            defaultViewCatrgory.setText("Couldn't find Category details.");
                        }
                    } else {
                        TSnackbar.make(view.findViewById(android.R.id.content), response.message(),
                                TSnackbar.LENGTH_LONG);
                    }
                }

                @Override
                public void onFailure(Call<TileCategoryModel> call, Throwable throwable) {

                    Log.e("Error", throwable.toString());

                    choose_category_progress.setVisibility(View.GONE);
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

                    defaultViewCatrgory.setVisibility(View.VISIBLE);
                    defaultViewCatrgory.setText(error_message);
                }
            });

        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }
    }

    public void getTypeList() {
        try {
            String token = "Bearer " + session.getAccessToken();
            TypeList.clear();
            TypeCode.clear();
            chooseTypeAdapter = new BottomSheetAdapter(TypeList, (DashboardActivity) getActivity());
            recyclerViewType.setAdapter(chooseTypeAdapter);
            choose_type_progress.setVisibility(View.VISIBLE);
            defaultViewType.setVisibility(View.GONE);

            ApiInterface apiService =
                    ApiClient.getClient().create(ApiInterface.class);
            Call<TileTypeModel> call = apiService.getTileType(token);

            call.enqueue(new Callback<TileTypeModel>() {
                @Override
                public void onResponse(Call<TileTypeModel> call, Response<TileTypeModel> response) {
                    int statusCode = response.code();
                    choose_type_progress.setVisibility(View.GONE);
                    if (response.isSuccessful()) {
                        input_type.setText(null);
                        if(statusCode == 200) {
                            type_flag = 1;
                            for (int i = 0; i < response.body().getData().size(); i++) {
                                TypeList.add(response.body().getData().get(i).getName());
                                TypeCode.add(response.body().getData().get(i).getId());
                            }

                            chooseTypeAdapter.notifyDataSetChanged();
                        } else {
                            /*if (checkFlagView == 1) {
                                return;
                            }*/
                            defaultViewType.setVisibility(View.VISIBLE);
                            defaultViewType.setText("Couldn't find Category details.");
                        }
                    } else {
                        TSnackbar.make(view.findViewById(android.R.id.content), response.message(),
                                TSnackbar.LENGTH_LONG);
                    }
                }

                @Override
                public void onFailure(Call<TileTypeModel> call, Throwable throwable) {

                    Log.e("Error", throwable.toString());

                    choose_type_progress.setVisibility(View.GONE);
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

                    defaultViewType.setVisibility(View.VISIBLE);
                    defaultViewType.setText(error_message);
                }
            });

        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }
    }

    private void initBottomSheetUnit() {
//        View bottomSheet_choose_branch = view.findViewById(R.id.bottom_sheet_choose_country);
        View bottomSheet_choose_branch = ((DashboardActivity) getActivity()).bottomSheet_choose_branch;

        bs_choose_unit = BottomSheetBehavior.from(bottomSheet_choose_branch);
        bs_choose_unit.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                }
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    hideSoftKeyboard();
                }
            }
            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // React to dragging events
            }
        });

        recyclerViewUnit = ((DashboardActivity) getActivity()).recyclerViewCountry;
        recyclerViewUnit.setHasFixedSize(true);
        recyclerViewUnit.setLayoutManager(new LinearLayoutManager(getActivity()));

        ViewGroup.LayoutParams params=recyclerViewUnit.getLayoutParams();
        params.height= 300;
        recyclerViewUnit.setLayoutParams(params);

        chooseUnitAdapter = new BottomSheetAdapter(ChooseUnitList, (DashboardActivity) getActivity());
        recyclerViewUnit.setAdapter(chooseUnitAdapter);
    }

    private void initBottomSheetCategory() {
        View bottomSheet_choose_category = ((DashboardActivity) getActivity()).bottomSheet_choose_category;

        bs_choose_category = BottomSheetBehavior.from(bottomSheet_choose_category);
        bs_choose_category.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                }
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    hideSoftKeyboard();
                }
            }
            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // React to dragging events
            }
        });

        recyclerViewCategory = ((DashboardActivity) getActivity()).recyclerViewCategory;
        recyclerViewCategory.setHasFixedSize(true);
        recyclerViewCategory.setLayoutManager(new LinearLayoutManager(getActivity()));

        ViewGroup.LayoutParams params=recyclerViewCategory.getLayoutParams();
        params.height= 300;
        recyclerViewCategory.setLayoutParams(params);

        chooseCategoryAdapter = new BottomSheetAdapter(CategoryList, (DashboardActivity) getActivity());
        recyclerViewCategory.setAdapter(chooseCategoryAdapter);
    }

    private void initBottomSheetType() {
        View bottomSheet_choose_type = ((DashboardActivity) getActivity()).bottomSheet_choose_type;

        bs_choose_type = BottomSheetBehavior.from(bottomSheet_choose_type);
        bs_choose_type.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                }
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    hideSoftKeyboard();
                }
            }
            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // React to dragging events
            }
        });

        recyclerViewType = ((DashboardActivity) getActivity()).recyclerViewType;
        recyclerViewType.setHasFixedSize(true);
        recyclerViewType.setLayoutManager(new LinearLayoutManager(getActivity()));

        ViewGroup.LayoutParams params=recyclerViewType.getLayoutParams();
        params.height= 300;
        recyclerViewType.setLayoutParams(params);

        chooseTypeAdapter = new BottomSheetAdapter(TypeList, (DashboardActivity) getActivity());
        recyclerViewType.setAdapter(chooseTypeAdapter);
    }

    public void hideSoftKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        }
    }

    private void collapseBottomSheet(){
        if(bs_choose_unit.getState() == 3) {
            bs_choose_unit.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
        if(bs_choose_category.getState() == 3) {
            bs_choose_category.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
        if(bs_choose_type.getState() == 3) {
            bs_choose_type.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    public void selectItem(String item){
        if(bs_choose_unit.getState() == 3) {
            input_unit.setError(null);
            input_unit.setText(item);
            hidden_unit.setText(ChooseUnitCode.get(ChooseUnitList.indexOf(item)));
            hidden_unit_scale.setText(ChooseUnitScale.get(ChooseUnitList.indexOf(item)));
            hideSoftKeyboard();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    bs_choose_unit.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    pattern_width.requestFocus();
                }
            }, 200);
        }

        if(bs_choose_category.getState() == 3) {
            input_category.setError(null);
            input_category.setText(item);
            hidden_category.setText(CategoryCode.get(CategoryList.indexOf(item)));
            hideSoftKeyboard();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    bs_choose_category.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }, 200);
        }

        if(bs_choose_type.getState() == 3) {
            input_type.setError(null);
            input_type.setText(item);
            hidden_type.setText(TypeCode.get(TypeList.indexOf(item)));
            hideSoftKeyboard();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    bs_choose_type.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }, 200);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        String backstackFlag = "";
        backstackFlag = session.getBackStackId();
        if(backstackFlag.equals("2")){
            collapseBottomSheet();
            session.setBackStackId("0");
            FragmentManager fm = getFragmentManager();
            if (fm.getBackStackEntryCount() > 0) {
                fm.popBackStack();
            }

        }

        /*String backstackFlag = "";
        backstackFlag = session.getBackStackId();
        if(backstackFlag.equals("2")){
            collapseBottomSheet();

            FragmentManager fm = getFragmentManager();
            if (fm.getBackStackEntryCount() > 0) {
                fm.popBackStack();
            }

        }

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){

                    if(bs_choose_unit.getState() == 3) {
                        collapseBottomSheet();
                    }
                    else if(bs_choose_category.getState() == 3) {
                        collapseBottomSheet();
                    }
                    else if(bs_choose_type.getState() == 3) {
                        collapseBottomSheet();
                    }
                    else{
                        FragmentManager fm = getFragmentManager();
                        if (fm.getBackStackEntryCount() > 0) {
                            session.setBackStackId("0");
                            fm.popBackStack();
                        }
                    }

                    return true;
                }
                return false;
            }
        });*/

    }

}