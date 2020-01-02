package com.nagainfomob.app.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidadvance.topsnackbar.TSnackbar;
import com.nagainfomob.app.DisplayMyDesign.GlobalVariables;
import com.nagainfomob.app.R;
import com.nagainfomob.app.adapter.BottomSheetAdapter;
import com.nagainfomob.app.api.ApiClient;
import com.nagainfomob.app.api.ApiInterface;
import com.nagainfomob.app.database.DatabaseHandler;
import com.nagainfomob.app.helpers.InputValidation;
import com.nagainfomob.app.helpers.SessionManager;
import com.nagainfomob.app.main.DashboardActivity;
import com.nagainfomob.app.model.TileCategoryModel;
import com.nagainfomob.app.model.CreateProject.CreateProjectResult;
import com.nagainfomob.app.model.ProjectsModel;
import com.nagainfomob.app.model.SettingsModel;
import com.nagainfomob.app.model.TileModel;
import com.nagainfomob.app.model.TileTypeModel;
import com.nagainfomob.app.model.UnitList;
import com.nagainfomob.app.sql.DatabaseManager;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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

public class NewTileFragment extends Fragment implements View.OnClickListener {

    private View view;
    private Uri mCropImageUri;
    private Bitmap tilemap = null;
    private static Uri imageUri;

    private static int unit_flag = 0;
    private static int category_flag = 0;
    private static int type_flag = 0;

    private EditText input_unit;
    private EditText hidden_unit;
    private EditText hidden_unit_scale;
    private EditText tile_name;
    private EditText tile_brand;
    private EditText tile_height;
    private EditText tile_width;
    private EditText tile_price;
    private EditText input_category;
    private EditText hidden_category;
    private EditText input_type;
    private EditText hidden_type;
    private EditText input_tile_image;

    private Button btn_submit;

    private ImageView ImgDownCountry;
    private ImageView ImgDownCategory;
    private ImageView ImgDownType;

    private SessionManager session;
    private String access_token;

    private static LinearLayout layout_tile_image;
    private static ImageView tile_image;
    private static EditText input_image_hidden;

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

        view = inflater.inflate(R.layout.fragment_new_tile, container, false);
        session = new SessionManager(getActivity());
        session.setFragmentId("tab3.3");

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

        layout_tile_image = (LinearLayout) view.findViewById(R.id.TileImageView);
        tile_image = (ImageView) view.findViewById(R.id.TileImg);
        input_tile_image = (EditText) view.findViewById(R.id.input_tile_image);
        input_image_hidden = (EditText) view.findViewById(R.id.input_image_hidden);

        btn_submit = view.findViewById(R.id.btn_submit);
        input_unit = view.findViewById(R.id.input_company_unit);
        hidden_unit = view.findViewById(R.id.input_unit_hidden);
        hidden_unit_scale = view.findViewById(R.id.input_unit_scale_hidden);
        tile_name = view.findViewById(R.id.tile_name);
        tile_brand = view.findViewById(R.id.tile_brand);
        tile_height = view.findViewById(R.id.tile_height);
        tile_width = view.findViewById(R.id.tile_width);
        tile_price = view.findViewById(R.id.tile_price);
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

        tile_name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    collapseBottomSheet();
                }
            }
        });

        tile_height.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    collapseBottomSheet();
                }
            }
        });

        tile_width.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    collapseBottomSheet();
                }
            }
        });

        tile_price.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    collapseBottomSheet();
                }
            }
        });

        tile_brand.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    hideSoftKeyboard();
                    input_unit.requestFocus();
                }
                return false;
            }
        });

        //Choose Tile Image

        LinearLayout lay =(LinearLayout) view.findViewById(R.id.tileImgLay);
        lay.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                hideSoftKeyboard();
                collapseBottomSheet();
//                ((DashboardActivity) getActivity()).captureImage(view);
                checkRatio();
            }
        });

        input_tile_image.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(MotionEvent.ACTION_UP == event.getAction()) {
                    hideSoftKeyboard();
                    collapseBottomSheet();
//                    ((DashboardActivity) getActivity()).captureImage(view);
                    checkRatio();
                }
                return true;
            }
        });
        input_tile_image.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    hideSoftKeyboard();
                    collapseBottomSheet();
//                    ((DashboardActivity) getActivity()).captureImage(view);
                    checkRatio();
                }
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

    private void checkRatio(){

        if(!input_unit.getText().toString().trim().isEmpty() &&
                !tile_height.getText().toString().trim().isEmpty() &&
                !tile_width.getText().toString().trim().isEmpty() ) {

            float w = 0 , h = 0, wr = 0, hr = 0;
            float w_in_inches = 0, h_in_inshes = 0;
            String ws, hs, unit;
            String err_mesg = "Tile should be min 5 and max 120 inches in size";

            unit = input_unit.getText().toString().trim();
            w = Float.parseFloat(tile_width.getText().toString().trim());
            h = Float.parseFloat(tile_height.getText().toString().trim());

            if(unit.equalsIgnoreCase("millimetre")){
                w_in_inches = GlobalVariables.mmToInches(w);
                h_in_inshes = GlobalVariables.mmToInches(h);
            }
            else if (unit.equalsIgnoreCase("feet")){
                w_in_inches = GlobalVariables.feetToInches(w);
                h_in_inshes = GlobalVariables.feetToInches(h);
            }
            else{
                w_in_inches = w;
                h_in_inshes = h;
            }

            if(w_in_inches < 5 ||  w_in_inches > 120){
                tile_width.setError(err_mesg);
                tile_width.setFocusableInTouchMode(true);
                tile_width.requestFocus();
                hideSoftKeyboard();

                return;
            }

            if(h_in_inshes < 5 ||  h_in_inshes > 120){
                tile_height.setError(err_mesg);
                tile_height.setFocusableInTouchMode(true);
                tile_height.requestFocus();
                hideSoftKeyboard();

                return;
            }

            ws = String.format("%.2f", (w/h));
            hs = String.format("%.2f", (h/h));

            wr = Float.parseFloat(ws) * 100;
            hr = Float.parseFloat(hs) * 100;

            Log.d("Ratio", " W : H => " + Math.round(wr) +" : " + Math.round(hr));


            proceedCaptureImage(Math.round(wr), Math.round(hr));

        }
        else{
            new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Confirm Action!")
                    .setContentText("Proceed to select image without entering dimensions?\n" +
                            "Please Enter values for Unit, Width and Height to maintain aspect ratio of the Image.")
                    .setCancelText("Proceed")
                    .setConfirmText("Exit & Enter")
                    .showCancelButton(true)
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();

                        }
                    })
                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();
                            proceedCaptureImage(0, 0);
                        }
                    })

                    .show();
        }

    }

    private void proceedCaptureImage(int width, int height){
        ((DashboardActivity) getActivity()).captureImage(view, width, height);
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
        if(session.getProcessingFlag().toString().equals("1")) return;
        if(formValidation()){
            session.setProcessingFlag("1");
            final SweetAlertDialog pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE)
                    .setTitleText("Processing");
            pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.colorAccent));
            pDialog.show();
            pDialog.setCancelable(false);

            try{

                String tWidth = "";
                String tHeight = "";
                tilemap = null;
                String unit = hidden_unit.getText().toString().trim();
                String fileURL = input_image_hidden.getText().toString().trim();
                tWidth = tile_width.getText().toString();
                tHeight = tile_height.getText().toString();

                if (unit.equals("3")) {
                    tWidth = String.format("%1.2f", (Double.parseDouble(tWidth) * 304.8f));
                    tHeight = String.format("%1.2f", (Double.parseDouble(tHeight) * 304.8f));

                } else if (unit.equals("2")) {
                    tWidth = String.format("%1.2f", (Double.parseDouble(tWidth) * 25.4f));
                    tHeight = String.format("%1.2f", (Double.parseDouble(tHeight) * 25.4f));
                }

                String dim = Math.round(Float.parseFloat(tWidth)) + "x" +
                        Math.round(Float.parseFloat(tHeight));

                DatabaseHandler db = new DatabaseHandler(
                        getContext());
                TileModel tile = new TileModel();

                tile.setTile_name(tile_name.getText().toString());
                tile.setTile_dimen(dim);
                tile.setTile_type(input_type.getText().toString());
                tile.setBarnd_id("0");
                tile.setTile_brand(tile_brand.getText().toString());
                tile.setType_id(hidden_type.getText().toString());
                tile.setCategory_id(hidden_category.getText().toString());
                tile.setTile_id("0");
                tile.setTile_color("");
                tile.setTile_category(input_category.getText().toString());
                tile.setModel_no("Custom");
                tile.setTile_p_type("C");
                tile.setTile_price(tile_price.getText().toString());
                tile.setIs_active("1");

                db.insertRecordCustomNew(tile);

                tilemap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);

                if(tilemap != null) {
                    writeStorate(getContext(), tilemap, tile_name.getText().toString(), tile_brand.getText().toString());
                }

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        session.setBackStackId("99");
                        pDialog.setTitleText(getString(R.string.success))
                                .setContentText(getString(R.string.c_tile_add_success))
                                .setConfirmText("OK")
                                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                        pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                session.setProcessingFlag("0");
                                pDialog.dismissWithAnimation();
                                collapseBottomSheet();
                                triggerBackStack();
                            }
                        })
                                .show();
                    }
                }, 1500);



            } catch (Exception e) {
                pDialog.dismissWithAnimation();
                Log.e("Error", e.getMessage());
            }
        }
    }

    private void writeStorate(Context context, Bitmap photo, String tile_name, String brand) {

        DatabaseHandler dh = new DatabaseHandler(context);
        String file_path = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/DMD/" + brand + "/";

//        Log.d("DMD_Log", "Storage Link - " + file_path+tile_name + ".jpg");

        File dir = new File(file_path);
        if (!dir.exists())
            dir.mkdirs();
        File file = new File(dir, tile_name + ".jpg");

//        if (!file.exists()) {

        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        photo.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        try {
            fOut.flush();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
//        }
    }

    private void saveDB(Response<CreateProjectResult> response){

        ProjectsModel project = new ProjectsModel();

        project.setProject_id(response.body().getData().getId());
        project.setProject_type("2");
        project.setRoom_type("1");
        project.setUnit_id(hidden_unit.getText().toString());
        project.setUnit_name(input_unit.getText().toString());
        project.setUnit_scale(hidden_unit_scale.getText().toString());

        DatabaseManager.addProjectsInfo(getActivity(), project);

    }

    private boolean formValidation(){

        inputValidation = new InputValidation(getActivity());

        float w = 0 , h = 0;
        float w_in_inches = 0, h_in_inshes = 0;
        String unit;
        String err_mesg = "Tile should be min 5 and max 120 inches in size";

        if (!inputValidation.isEmptyText(tile_name, getString(R.string.enter) +
                " "+getString(R.string.tile_name))) {
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
        if (!inputValidation.isEmptyText(input_image_hidden, getString(R.string.choose) +
                " "+getString(R.string.tile_image))) {
            return false;
        }
        if (!inputValidation.isEmptyText(tile_brand, getString(R.string.enter) +
                " "+getString(R.string.tile_brand))) {
            return false;
        }
        if (!inputValidation.isEmptyText(input_unit, getString(R.string.error_unit))) {
            return false;
        }
        if (!inputValidation.isEmptyText(tile_width, getString(R.string.enter) +
                " "+getString(R.string.tile_width))) {
            return false;
        }
        if (!inputValidation.isEmptyText(tile_height, getString(R.string.enter) +
                " "+getString(R.string.tile_height))) {
            return false;
        }
        if (!inputValidation.isEmptyText(tile_price, getString(R.string.enter) +
                " "+getString(R.string.tile_price))) {
            return false;
        }

        unit = input_unit.getText().toString().trim();
        w = Float.parseFloat(tile_width.getText().toString().trim());
        h = Float.parseFloat(tile_height.getText().toString().trim());

        if(unit.equalsIgnoreCase("millimetre")){
            w_in_inches = GlobalVariables.mmToInches(w);
            h_in_inshes = GlobalVariables.mmToInches(h);
        }
        else if (unit.equalsIgnoreCase("feet")){
            w_in_inches = GlobalVariables.feetToInches(w);
            h_in_inshes = GlobalVariables.feetToInches(h);
        }
        else{
            w_in_inches = w;
            h_in_inshes = h;
        }

        if(w_in_inches < 5 ||  w_in_inches > 120){
            tile_width.setError(err_mesg);
            tile_width.setFocusableInTouchMode(true);
            tile_width.requestFocus();
            hideSoftKeyboard();

            return false;
        }

        if(h_in_inshes < 5 ||  h_in_inshes > 120){
            tile_height.setError(err_mesg);
            tile_height.setFocusableInTouchMode(true);
            tile_height.requestFocus();
            hideSoftKeyboard();

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
                        defaultViewCatrgory.setVisibility(View.VISIBLE);
                        defaultViewCatrgory.setText("Couldn't find Category details.");
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

        /*getView().setFocusableInTouchMode(true);
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
                    else {
                        FragmentManager fm = getFragmentManager();
                        if (fm.getBackStackEntryCount() > 0) {
                            fm.popBackStack();
                        }
                    }
                    return true;
                }
                return false;
            }
        });*/

    }

    public static void setImage(CropImage.ActivityResult result){
        layout_tile_image.setPadding(3,3,3,3);
        tile_image.setImageURI(result.getUri());
        imageUri = result.getUri();
        input_image_hidden.setText(result.getUri()+"");
    }

    public void triggerBackStack(){
        session.setFragmentId("tab3");
        session.setBackStackId("99");
        FragmentManager fm = getFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
        }
    }


}