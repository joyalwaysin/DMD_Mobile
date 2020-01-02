package com.nagainfomob.app.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.nagainfomob.app.DisplayMyDesign.GlobalVariables;
import com.nagainfomob.app.R;
import com.nagainfomob.app.adapter.MyExpandableListAdapter;
import com.nagainfomob.app.adapter.MyRecyclerViewAdapter;
import com.nagainfomob.app.api.ApiClient;
import com.nagainfomob.app.api.ApiInterface;
import com.nagainfomob.app.database.DatabaseHandler;
import com.nagainfomob.app.helpers.BadgedTabLayout;
import com.nagainfomob.app.helpers.RowItem1;
import com.nagainfomob.app.helpers.SessionManager;
import com.nagainfomob.app.main.DashboardActivity;
import com.nagainfomob.app.model.LoadTile.LoadTileResult;
import com.nagainfomob.app.model.SettingsModel;
import com.nagainfomob.app.model.TileModel;
import com.nagainfomob.app.sql.DatabaseManager;
import com.nagainfomob.app.utils.FileUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Response;

import static com.nagainfomob.app.api.ErrorUtils.ERROR_MESSAGE_NO_CONNECTION;
import static com.nagainfomob.app.api.ErrorUtils.ERROR_MESSAGE_SLOW_CONNECTION;
import static com.nagainfomob.app.api.ErrorUtils.ERROR_MESSAGE_UNKNOWN;
import static com.nagainfomob.app.api.ErrorUtils.ERROR_MESSAGE_WRONG_JSON_FORMAT;


/**
 * Created by root on 2/2/17.
 */

public class LibraryFragment extends Fragment implements View.OnClickListener {

    private static final String KEY_BRAND_NAME = "pro_brand";
    private static final String KEY_DIMEN = "pro_dimen";
    private static final String KEY_COLOR = "pro_color";
    private static final String KEY_TYPE = "pro_type";
    private static final String KEY_COMPANY = "pro_company";
    private static final String KEY_BRAND = "pro_brand";
    private static final String KEY_NAME = "pro_name";
    private static final String KEY_CATEGORY = "tile_category";

    List<Integer> selectedBrands = new ArrayList<Integer>();
    List<Integer> selectedSize = new ArrayList<Integer>();
    List<Integer> selectedColor = new ArrayList<Integer>();
    List<Integer> selectedType = new ArrayList<Integer>();
    List<Integer> selectedCategory = new ArrayList<Integer>();

    List<Integer> selectedBrands_c = new ArrayList<Integer>();
    List<Integer> selectedSize_c = new ArrayList<Integer>();
    List<Integer> selectedColor_c = new ArrayList<Integer>();
    List<Integer> selectedType_c = new ArrayList<Integer>();
    List<Integer> selectedCategory_c = new ArrayList<Integer>();

    ArrayList<HashMap<String, String>> dbResult = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> dbResult1 = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> dbResult2 = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> dbResult3 = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> dbResult4 = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> filterResult = new ArrayList<HashMap<String, String>>();

    ArrayList<HashMap<String, String>> dbResult_c = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> dbResult1_c = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> dbResult2_c = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> dbResult3_c = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> dbResult4_c = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> filterResult_c = new ArrayList<HashMap<String, String>>();

    ArrayList<HashMap<String, String>> dbPaginatedList = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> dbPaginatedList_c = new ArrayList<HashMap<String, String>>();
    private static int dbOffset = 0;
    private static int dbLimit = 50;
    private static int searchFlag = 0;
    private static String tile2Delete = "";
    private MyRecyclerViewAdapter adapter;
    private MyRecyclerViewAdapter adapter_c;

    //    private String storageURL = "https://storage-vro.s3.amazonaws.com/";
    private String storageURL = "https://dmd-file.s3.us-east-1.amazonaws.com/";
    private static int downloadingFlag = 0;
    private SweetAlertDialog dDialog;
    DownloadTask downloadTask;
    private SessionManager session;
    private View view;
    private boolean isButtonClicked = false;
    private boolean mIsRestoredFromBackstack;

    private static int tabFlag = 0;
    private static int filterCount = 0;
    private static int filterCount_c = 0;
    private Dialog dialog;
    private Dialog dialog_c;
    private Animation animInleft;
    private Animation animInright;
    private ViewFlipper viewFlipper;
    private RecyclerView patternView;
    private RecyclerView patternView_c;
    private LinearLayout pattern_detailed_view;
    private LinearLayout pattern_detailed_view_c;
    private TextView backToView;
    private TextView backToView_c;
    private TextView title_manu;
    private TextView title_custom;
    private TextView tile_name;
    private TextView tile_name_c;
    private TextView brand_name;
    private TextView brand_name_c;
    private TextView tile_category;
    private TextView tile_category_c;
    private TextView tile_type;
    private TextView tile_type_c;
    private TextView tile_dim;
    private TextView tile_dim_c;
    private TextView tile_color;
    private TextView tile_color_c;
    private TextView tile_price;
    private TextView tile_price_c;
    private TextView syncLibrary;
    private ImageView img_filter;
    private ImageView img_filter_c;
    private ImageView pattern_img;
    private ImageView pattern_img_c;
    private ImageView img_add_tile;
    private EditText txtSearch;
    private EditText txtSearch_c;
    private LinearLayout view_placeholder;
    private LinearLayout view_placeholder_c;
    private LinearLayout view_errorText;
    private LinearLayout view_errorText_c;
    private LinearLayout viewProgress;
    private LinearLayout viewProgress_c;
    private LinearLayout delete_tile;
    private LinearLayout delete_tile_c;
    private BadgedTabLayout tabLayout;
    private BadgedTabLayout tabLayout_c;

    private Context context;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        context = getContext();
        View view = inflater.inflate(R.layout.fragment_library, container, false);
        session = new SessionManager(getActivity());
//        session.setBackStackId("0");
        session.setFragmentId("tab3");

        initViews(view);
        initListeners();
        initObjects();

        final TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Manufacturer Library"));
        tabLayout.addTab(tabLayout.newTab().setText("Custom Tiles"));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition() == 0){
                    viewFlipper.setInAnimation(animInleft);
                    viewFlipper.setDisplayedChild(0);
                    tabFlag = 0;
                }
                else{
                    viewFlipper.setInAnimation(animInright);
                    viewFlipper.setDisplayedChild(1);
                    tabFlag = 1;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        getActivity().getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener()
        {
            public void onBackStackChanged()
            {
                String backstackFlag = "";
                if(session.getBackStackId() != null) {
                    backstackFlag = session.getBackStackId().toString();
                    if (backstackFlag.equals("99")) {
                        getAllpatternsCustom(context);
                        prepareFilterDialogCustom(context);
                        session.setBackStackId("0");
                    }
                }
            }
        });

        patternView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastCompletelyVisibleItemPosition = 0;

                lastCompletelyVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
                if (lastCompletelyVisibleItemPosition == dbPaginatedList.size() - 1 &&
                        lastCompletelyVisibleItemPosition + 1 >= dbLimit) {
                    if(searchFlag == 1) return;
                    dbOffset = dbPaginatedList.size();
                    paginatedbList(dbOffset);
                }
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mIsRestoredFromBackstack = false;
    }

    private void initViews(View view) {
        animInleft = AnimationUtils.loadAnimation(getContext(), R.anim.in_from_left);
        animInright = AnimationUtils.loadAnimation(getContext(), R.anim.in_from_right);
        viewFlipper = (ViewFlipper) view.findViewById(R.id.view_flipper);
        patternView = (RecyclerView) view.findViewById(R.id.view_pattern);
        patternView_c = (RecyclerView) view.findViewById(R.id.view_pattern_c);
        pattern_detailed_view = (LinearLayout) view.findViewById(R.id.pattern_detailed_view);
        pattern_detailed_view_c = (LinearLayout) view.findViewById(R.id.pattern_detailed_view_c);
        view_placeholder = (LinearLayout) view.findViewById(R.id.view_placeholder);
        viewProgress = (LinearLayout) view.findViewById(R.id.viewProgress);
        viewProgress_c = (LinearLayout) view.findViewById(R.id.viewProgress_c);
        delete_tile = (LinearLayout) view.findViewById(R.id.delete_tile);
        delete_tile_c = (LinearLayout) view.findViewById(R.id.delete_tile_c);
        view_placeholder_c = (LinearLayout) view.findViewById(R.id.view_placeholder_c);
        view_errorText = (LinearLayout) view.findViewById(R.id.view_errorText);
        view_errorText_c = (LinearLayout) view.findViewById(R.id.view_errorText_c);
        backToView = (TextView) view.findViewById(R.id.backToView);
        backToView_c = (TextView) view.findViewById(R.id.backToViewCustom);
        title_manu = (TextView) view.findViewById(R.id.title_manu);
        title_custom = (TextView) view.findViewById(R.id.title_custom);
        tile_name = (TextView) view.findViewById(R.id.tile_name);
        tile_name_c = (TextView) view.findViewById(R.id.tile_name_c);
        brand_name = (TextView) view.findViewById(R.id.brand_name);
        brand_name_c = (TextView) view.findViewById(R.id.brand_name_c);
        tile_category = (TextView) view.findViewById(R.id.tile_category);
        tile_category_c = (TextView) view.findViewById(R.id.tile_category_c);
        tile_type = (TextView) view.findViewById(R.id.tile_type);
        tile_type_c = (TextView) view.findViewById(R.id.tile_type_c);
        tile_dim = (TextView) view.findViewById(R.id.tile_dim);
        tile_dim_c = (TextView) view.findViewById(R.id.tile_dim_c);
        tile_color = (TextView) view.findViewById(R.id.tile_color);
        tile_color_c = (TextView) view.findViewById(R.id.tile_color_c);
        tile_price = (TextView) view.findViewById(R.id.tile_price);
        tile_price_c = (TextView) view.findViewById(R.id.tile_price_c);
        syncLibrary = (TextView) view.findViewById(R.id.syncLibrary);
        img_filter = (ImageView) view.findViewById(R.id.img_filter);
        img_filter_c = (ImageView) view.findViewById(R.id.img_filter_c);
        pattern_img = (ImageView) view.findViewById(R.id.pattern_img);
        pattern_img_c = (ImageView) view.findViewById(R.id.pattern_img_c);
        txtSearch = (EditText) view.findViewById(R.id.txtSearch);
        txtSearch_c = (EditText) view.findViewById(R.id.txtSearch_c);
        img_add_tile = (ImageView) view.findViewById(R.id.img_add_tile);

        txtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    ((DashboardActivity)getActivity()).hideSoftKeyboard();
                    searchTiles(txtSearch.getText().toString(), "M");
                    return true;
                }
                return false;
            }
        });

        txtSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.length() != 0) {
                    searchTiles(s.toString(), "M");
                }
                else {
                    searchFlag = 0;
                    getAllpatterns();
                }
            }
        });

        txtSearch_c.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    ((DashboardActivity)getActivity()).hideSoftKeyboard();
                    searchTiles(txtSearch_c.getText().toString(), "C");
//                    searchTilesCustom(txtSearch_c.getText().toString(), "C");
                    return true;
                }
                return false;
            }
        });

        txtSearch_c.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.length() != 0) {
                    searchTiles(txtSearch_c.getText().toString(), "C");
//                    searchTilesCustom(s.toString(), "C");
                }
                else {
                    getAllpatternsCustom(getContext());
                }
            }
        });

//        prepareFilterDialog();
//        prepareFilterDialogCustom(getContext());

    }

    private void initObjects() {
        /*inputValidation = new InputValidation(getActivity());
        session = new SessionManager(getActivity());*/
    }

    private void initListeners() {
        backToView.setOnClickListener(this);
        backToView_c.setOnClickListener(this);
        img_filter.setOnClickListener(this);
        img_filter_c.setOnClickListener(this);
        syncLibrary.setOnClickListener(this);
        img_add_tile.setOnClickListener(this);
        delete_tile.setOnClickListener(this);
        delete_tile_c.setOnClickListener(this);
//        img_PhotoVis.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backToView:
            case R.id.backToViewCustom:
                session.setFragmentId("tab3");
                backtoLibrary();
                break;
            case R.id.img_filter:
                showFilterDialog();
                break;
            case R.id.img_filter_c:
                showFilterDialogCustom();
                break;
            case R.id.syncLibrary:
                syncLibrary();
                break;
            case R.id.img_add_tile:
                addCustomTiles();
                break;
            case R.id.delete_tile:
                deleteTile("T");
                break;
            case R.id.delete_tile_c:
                deleteTile("C");
                break;



        }
    }

    public void deleteTile(final String flag){
        new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Are you sure?")
                .setContentText("Do you wish to delete this Tile?")
                .setCancelText("Cancel")
                .setConfirmText("Delete")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        if(flag.equals("T")){
                            proceedDelete(flag);
                            sDialog
                                    .setTitleText("Deleted!")
                                    .setContentText("The selected Tile has been deleted!")
                                    .setConfirmText("OK")
                                    .showCancelButton(false)
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            getAllpatterns();
                                            backtoLibrary();
                                            sDialog.cancel();
                                        }
                                    })
                                    .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);

                        }

                        else if(flag.equals(flag)){
                            proceedDelete(flag);
                            sDialog
                                    .setTitleText("Deleted!")
                                    .setContentText("The selected Tile has been deleted!")
                                    .setConfirmText("OK")
                                    .showCancelButton(false)
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            getAllpatternsCustom(context);
                                            backtoLibrary();
                                            sDialog.cancel();
                                        }
                                    })
                                    .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                        }
                        else{
                            sDialog
                                    .setTitleText("Error!")
                                    .setContentText("Process could not be completed")
                                    .setConfirmText("OK")
                                    .showCancelButton(false)
                                    .setConfirmClickListener(null)
                                    .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                        }
                    }
                })
                .show();
    }

    private void proceedDelete(String flag){
        DatabaseHandler dh = new DatabaseHandler(context);
        if(flag.equals("T")){
            dh.deleteTile(tile2Delete);
        }
        if(flag.equals("C")){
            dh.deleteTileCustom(tile2Delete);
        }
    }

    public void getAllpatterns() {

        DatabaseHandler db = new DatabaseHandler(getContext());
        ArrayList<HashMap<String, String>> dbResult = new ArrayList<HashMap<String, String>>();

        dbPaginatedList.clear();
        dbOffset = 0;
        dbResult = db.getAllPatternCustomPaginated(false, dbLimit, dbOffset);

        if(dbResult.size() > 0){
            dbPaginatedList.addAll(dbResult);
            view_errorText.setVisibility(View.GONE);
            view_placeholder.setVisibility(View.GONE);
            patternView.setVisibility(View.VISIBLE);
        }
        else{
            view_errorText.setVisibility(View.GONE);
            view_placeholder.setVisibility(View.VISIBLE);
            patternView.setVisibility(View.GONE);
        }

        // set up the RecyclerView
        patternView.setLayoutManager(new GridLayoutManager(getContext(), 5));
        adapter = new MyRecyclerViewAdapter(getContext(), dbPaginatedList, LibraryFragment.this, "M");
        patternView.setAdapter(adapter);

    }


    private void paginatedbList(int dbOffset){
        DatabaseHandler db = new DatabaseHandler(getContext());
        ArrayList<HashMap<String, String>> dbResult = new ArrayList<HashMap<String, String>>();

        dbResult = db.getAllPatternPaginated(dbLimit, dbOffset);

        if(dbResult.size() > 0){
            dbPaginatedList.addAll(dbResult);
        }

        adapter.notifyDataSetChanged();

    }

    private class getPatterns extends AsyncTask<Object, Object, ArrayList<HashMap<String, String>>> {
        private Context context;

        public getPatterns(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute (){
            viewProgress.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<HashMap<String, String>> doInBackground(Object... params) {

            DatabaseHandler db = new DatabaseHandler(context);
            ArrayList<HashMap<String, String>> dbResult = new ArrayList<HashMap<String, String>>();
            dbPaginatedList.clear();
            dbOffset = 0;
            dbResult = db.getAllPatternCustomPaginated(false, dbLimit, dbOffset);

            return dbResult;
        }

        @Override
        protected void onPostExecute(ArrayList<HashMap<String, String>> result) {

            if(result.size() > 0){
                dbPaginatedList.addAll(result);
                view_errorText.setVisibility(View.GONE);
                view_placeholder.setVisibility(View.GONE);
                viewProgress.setVisibility(View.GONE);
                patternView.setVisibility(View.VISIBLE);
            }
            else{
                viewProgress.setVisibility(View.GONE);
                view_errorText.setVisibility(View.GONE);
                view_placeholder.setVisibility(View.VISIBLE);
                patternView.setVisibility(View.GONE);
            }

            /*LibraryAdapter pgAdapter = new LibraryAdapter(context, result,
                    LibraryFragment.this, "M");
            patternView.refreshDrawableState();
            pgAdapter.notifyDataSetChanged();
            patternView.setAdapter(null);
            patternView.setAdapter(pgAdapter);*/

            patternView.setLayoutManager(new GridLayoutManager(getContext(), 5));
            adapter = new MyRecyclerViewAdapter(getContext(), dbPaginatedList, LibraryFragment.this, "M");
            patternView.setAdapter(adapter);
        }
    }

    public void getAllpatternsCustom(Context context) {
        DatabaseHandler db = new DatabaseHandler(context);
        ArrayList<HashMap<String, String>> dbResult = new ArrayList<HashMap<String, String>>();
        viewProgress_c.setVisibility(View.VISIBLE);
        dbPaginatedList_c.clear();
        dbResult = db.getAllPatternCustom(true);

        if(dbResult.size() > 0){
            dbPaginatedList_c.addAll(dbResult);
            view_errorText_c.setVisibility(View.GONE);
            viewProgress_c.setVisibility(View.GONE);
            view_placeholder_c.setVisibility(View.GONE);
            patternView_c.setVisibility(View.VISIBLE);
        }
        else{
            view_errorText_c.setVisibility(View.GONE);
            viewProgress_c.setVisibility(View.GONE);
            view_placeholder_c.setVisibility(View.VISIBLE);
            patternView_c.setVisibility(View.GONE);
        }

        /*LibraryAdapter pgAdapter = new LibraryAdapter(context, dbResult,
                LibraryFragment.this, "C");
        patternView_c.refreshDrawableState();
        pgAdapter.notifyDataSetChanged();
        patternView_c.setAdapter(null);
        patternView_c.setAdapter(pgAdapter);*/

        patternView_c.setLayoutManager(new GridLayoutManager(context, 5));
        adapter_c = new MyRecyclerViewAdapter(context, dbPaginatedList_c, LibraryFragment.this, "C");
        patternView_c.setAdapter(adapter_c);

    }

    private class getPatternsCustom extends AsyncTask<Object, Object, ArrayList<HashMap<String, String>>> {
        private Context context;

        public getPatternsCustom(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute (){
            viewProgress_c.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<HashMap<String, String>> doInBackground(Object... params) {

            DatabaseHandler db = new DatabaseHandler(context);
            ArrayList<HashMap<String, String>> dbResult = new ArrayList<HashMap<String, String>>();
            dbPaginatedList_c.clear();
            dbResult = db.getAllPatternCustom(true);

            return dbResult;
        }

        @Override
        protected void onPostExecute(ArrayList<HashMap<String, String>> result) {

            if(result.size() > 0){
                dbPaginatedList_c.addAll(result);
                view_errorText_c.setVisibility(View.GONE);
                view_placeholder_c.setVisibility(View.GONE);
                viewProgress_c.setVisibility(View.GONE);
                patternView_c.setVisibility(View.VISIBLE);
            }
            else{
                view_errorText_c.setVisibility(View.GONE);
                viewProgress_c.setVisibility(View.GONE);
                view_placeholder_c.setVisibility(View.VISIBLE);
                patternView_c.setVisibility(View.GONE);
            }

            /*LibraryAdapter pgAdapter = new LibraryAdapter(context, result,
                    LibraryFragment.this, "C");
            patternView_c.refreshDrawableState();
            pgAdapter.notifyDataSetChanged();
            patternView_c.setAdapter(null);
            patternView_c.setAdapter(pgAdapter);*/

            patternView_c.setLayoutManager(new GridLayoutManager(context, 5));
            adapter_c = new MyRecyclerViewAdapter(context, dbPaginatedList_c, LibraryFragment.this, "C");
            patternView_c.setAdapter(adapter_c);

        }
    }

    public void searchTiles(String str, String flag){
        DatabaseHandler db = new DatabaseHandler(getContext());
        ArrayList<HashMap<String, String>> dbResult = new ArrayList<HashMap<String, String>>();

        searchFlag = 1;
        if(flag.equals("C")){
            clearFilterCustom();
        }
        else{
            clearFilter();
        }

        if(flag.equals("C")){
            dbResult = db.searchkeyword(str, flag);
            if(dbResult.size() > 0){
                view_errorText_c.setVisibility(View.GONE);
                patternView_c.setVisibility(View.VISIBLE);
            }
            else{
                view_errorText_c.setVisibility(View.VISIBLE);
                patternView_c.setVisibility(View.GONE);
            }
        }
        else{
            dbResult = db.searchkeyword(str, flag);
            if(dbResult.size() > 0){
                view_errorText.setVisibility(View.GONE);
                patternView.setVisibility(View.VISIBLE);
            }
            else{
                view_errorText.setVisibility(View.VISIBLE);
                patternView.setVisibility(View.GONE);
            }
        }

        if(flag.equals("C")){
            /*LibraryAdapter pgAdapter = new LibraryAdapter(getContext(), dbResult,
                    LibraryFragment.this, "C");

            patternView_c.refreshDrawableState();
            pgAdapter.notifyDataSetChanged();
            patternView_c.setAdapter(null);
            patternView_c.setAdapter(pgAdapter);*/

            dbPaginatedList_c.clear();
            dbPaginatedList_c.addAll(dbResult);
            adapter_c.notifyDataSetChanged();
        }
        else{
            /*LibraryAdapter pgAdapter = new LibraryAdapter(getContext(), dbResult,
                    LibraryFragment.this, "M");

            patternView.refreshDrawableState();
            pgAdapter.notifyDataSetChanged();
            patternView.setAdapter(null);
            patternView.setAdapter(pgAdapter);*/

            dbPaginatedList.clear();
            dbPaginatedList.addAll(dbResult);
            adapter.notifyDataSetChanged();
        }
    }

    /*public void searchTilesCustom(String str, String flag){
        DatabaseHandler db = new DatabaseHandler(getContext());
        ArrayList<HashMap<String, String>> dbResult = new ArrayList<HashMap<String, String>>();

        clearFilterCustom();

        dbResult = db.searchkeyword(str, flag);

        if(dbResult.size() > 0){
            view_errorText_c.setVisibility(View.GONE);
            patternView_c.setVisibility(View.VISIBLE);
        }
        else{
            view_errorText_c.setVisibility(View.VISIBLE);
            patternView_c.setVisibility(View.GONE);
        }

        LibraryAdapter pgAdapter = new LibraryAdapter(getContext(), dbResult,
                LibraryFragment.this, "C");
        patternView_c.refreshDrawableState();
        pgAdapter.notifyDataSetChanged();
        patternView_c.setAdapter(null);
        patternView_c.setAdapter(pgAdapter);


    }*/

    public void viewTile(String path, String tname, final String tileSize, String brand,
                         String type, String category, String color, String tile_id, String tile_p_type,
                         String price) {

        Bitmap bmp;
        final String filePath = path;
        bmp = BitmapFactory.decodeFile(filePath);
        String[] tName = tname.split("/");
        tname = tName[tName.length - 1].replace(".jpg", "");
        tile2Delete = tile_id;

        if(category.equals("")) category = "Not Available";
        if(type.equals("")) type = "Not Available";
        if(color.equals("")) color = "Not Available";
        if(price.equals("")) {
            price = "Not Available";
        }
        else{
            price = "Rs."+ price;
        }

        tile_name.setText(tname);
        brand_name.setText(brand);
        tile_category.setText(category);
        tile_type.setText(type);
        tile_color.setText(color);
        tile_dim.setText(tileSize + " mm");
        pattern_img.setImageBitmap(bmp);
        tile_price.setText(price);

        patternView.setVisibility(View.GONE);
        pattern_detailed_view.setVisibility(View.VISIBLE);
        title_manu.setVisibility(View.VISIBLE);
        txtSearch.setVisibility(View.GONE);
        img_filter.setVisibility(View.GONE);
        delete_tile.setVisibility(View.VISIBLE);
        delete_tile_c.setVisibility(View.GONE);

        session.setFragmentId("tab3.1");
    }

    public void viewTileCustom(String path, String tname, final String tileSize, String brand,
                               String type, String category, String color, String tile_id, String tile_type,
                               String price) {

        Bitmap bmp;
        final String filePath = path;
        bmp = BitmapFactory.decodeFile(filePath);
        String[] tName = tname.split("/");
        tname = tName[tName.length - 1].replace(".jpg", "");
        tile2Delete = tile_id;

        if(category.equals("")) category = "Not Available";
        if(type.equals("")) type = "Not Available";
        if(color.equals("")) color = "Not Available";
        if(price.equals("")) {
            price = "Not Available";
        }
        else{
            price = "Rs. "+ price;
        }

        tile_name_c.setText(tname);
        brand_name_c.setText(brand);
        tile_category_c.setText(category);
        tile_type_c.setText(type);
        tile_color_c.setText(color);
        tile_dim_c.setText(tileSize + " mm");
        pattern_img_c.setImageBitmap(bmp);
        tile_price_c.setText(price);

        patternView_c.setVisibility(View.GONE);
        pattern_detailed_view_c.setVisibility(View.VISIBLE);
        title_custom.setVisibility(View.VISIBLE);
        txtSearch_c.setVisibility(View.GONE);
        img_filter_c.setVisibility(View.GONE);
        img_add_tile.setVisibility(View.GONE);
        delete_tile.setVisibility(View.GONE);
        delete_tile_c.setVisibility(View.VISIBLE);

        session.setFragmentId("tab3.2");
    }

    private void backtoLibrary(){
        if(tabFlag == 0) {
            pattern_detailed_view.setVisibility(View.GONE);
            patternView.setVisibility(View.VISIBLE);
            title_manu.setVisibility(View.GONE);
            txtSearch.setVisibility(View.VISIBLE);
            img_filter.setVisibility(View.VISIBLE);
            delete_tile.setVisibility(View.GONE);
        }else{
            pattern_detailed_view_c.setVisibility(View.GONE);
            patternView_c.setVisibility(View.VISIBLE);
            title_custom.setVisibility(View.GONE);
            txtSearch_c.setVisibility(View.VISIBLE);
            img_filter_c.setVisibility(View.VISIBLE);
            img_add_tile.setVisibility(View.VISIBLE);
            delete_tile_c.setVisibility(View.GONE);
        }
    }

    public void prepareFilterDialog(){

        TextView dialog_title;
        ImageView dialog_close;
        final ViewFlipper viewFlipperDialog;
        final Button filterButton;
        final TextView clear_filter;
        final ImageView img_clear_filter;

        dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dmd_dialog);
        dialog.getWindow()
                .getAttributes().windowAnimations = R.style.DialogAnimation;

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog_title = (TextView) dialog.findViewById(R.id.dialog_title);
        dialog_close = (ImageView) dialog.findViewById(R.id.dialog_close);
        viewFlipperDialog = (ViewFlipper) dialog.findViewById(R.id.view_flipper);

        dialog_title.setText("Tile Filter");

        dialog_close.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                applyDialog();
//                dialog.cancel();
            }
        });

        tabLayout = (BadgedTabLayout) dialog.findViewById(R.id.tab_layout);

        tabLayout.addTab(tabLayout.newTab().setText("Brand"));
        tabLayout.addTab(tabLayout.newTab().setText("Category"));
        tabLayout.addTab(tabLayout.newTab().setText("Type"));
        tabLayout.addTab(tabLayout.newTab().setText("Size"));
        tabLayout.addTab(tabLayout.newTab().setText("Color"));

        tabLayout.setBadgeText(0,null);
        tabLayout.setBadgeText(1,null);
        tabLayout.setBadgeText(2,null);
        tabLayout.setBadgeText(3,null);
        tabLayout.setBadgeText(4,null);

        addbrandCheckBox();
        addcategoryCheckBox(false);
        addtileTypeCheckBox(false);
        addtileSizeCheckBox(false);
        addtileColorCheckBox();

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition() == 0){
                    viewFlipperDialog.setDisplayedChild(0);
                }
                if(tab.getPosition() == 1){
                    viewFlipperDialog.setDisplayedChild(1);
                }
                if(tab.getPosition() == 2){
                    viewFlipperDialog.setDisplayedChild(2);
                }
                if(tab.getPosition() == 3){
                    viewFlipperDialog.setDisplayedChild(3);
                }
                if(tab.getPosition() == 4){
                    viewFlipperDialog.setDisplayedChild(4);
                }
//                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        filterButton = ((Button) dialog.findViewById( R.id.filter_button ));
        filterButton.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                applyDialog();
            }

        });

        clear_filter = ((TextView) dialog.findViewById( R.id.clear_filter ));
        clear_filter.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                searchFlag = 0;
                clearFilter();
            }

        });

        img_clear_filter = ((ImageView) dialog.findViewById( R.id.img_clear_filter ));
        img_clear_filter.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                searchFlag = 0;
                clearFilter();
            }

        });
    }

    public void prepareFilterDialogCustom(Context context){

        TextView dialog_title;
        ImageView dialog_close;
        final ViewFlipper viewFlipperDialog;
        final Button filterButton;
        final TextView clear_filter;
        final ImageView img_clear_filter;

        dialog_c = new Dialog(context);
        dialog_c.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_c.setCancelable(false);
        dialog_c.setContentView(R.layout.dmd_dialog_custom);
        dialog_c.getWindow()
                .getAttributes().windowAnimations = R.style.DialogAnimation;

        dialog_c.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog_title = (TextView) dialog_c.findViewById(R.id.dialog_title);
        dialog_close = (ImageView) dialog_c.findViewById(R.id.dialog_close);
        viewFlipperDialog = (ViewFlipper) dialog_c.findViewById(R.id.view_flipper);

        dialog_title.setText("Tile Filter");

        dialog_close.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                applyDialogCustom();
//                dialog.cancel();
            }
        });

        tabLayout_c = (BadgedTabLayout) dialog_c.findViewById(R.id.tab_layout);

        tabLayout_c.addTab(tabLayout_c.newTab().setText("Category"));
        tabLayout_c.addTab(tabLayout_c.newTab().setText("Type"));
        tabLayout_c.addTab(tabLayout_c.newTab().setText("Size"));

        tabLayout_c.setBadgeText(0,null);
        tabLayout_c.setBadgeText(1,null);
        tabLayout_c.setBadgeText(2,null);

        addcategoryCheckBox(true);
        addtileTypeCheckBox(true);
        addtileSizeCheckBox(true);

        tabLayout_c.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition() == 0){
                    viewFlipperDialog.setDisplayedChild(0);
                }
                if(tab.getPosition() == 1){
                    viewFlipperDialog.setDisplayedChild(1);
                }
                if(tab.getPosition() == 2){
                    viewFlipperDialog.setDisplayedChild(2);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        filterButton = ((Button) dialog_c.findViewById( R.id.filter_button ));
        filterButton.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                applyDialogCustom();
            }

        });

        clear_filter = ((TextView) dialog_c.findViewById( R.id.clear_filter ));
        clear_filter.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                clearFilterCustom();
            }

        });

        img_clear_filter = ((ImageView) dialog_c.findViewById( R.id.img_clear_filter ));
        img_clear_filter.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                clearFilterCustom();
            }

        });
    }

//Brand

    public void addbrandCheckBox() {
        Dialog v = dialog;
        ArrayList<HashMap<String, String>> dbResult1 = new ArrayList<HashMap<String, String>>();

        LinearLayout ll = (LinearLayout) v.findViewById(R.id.brandCheckBox);
        ll.removeAllViews();

        DatabaseHandler db = new DatabaseHandler(getContext());
        dbResult1 = db.getAllBrand();
        List<String> brandNameList = new ArrayList<String>();

        for (int i = 0; i < dbResult1.size(); i++) {
            Map<String, String> temp1 = dbResult1.get(i);
            if (!temp1.get(KEY_BRAND_NAME).equalsIgnoreCase(""))
                brandNameList.add(temp1.get(KEY_BRAND_NAME));
        }

        for (int i = 0; i < brandNameList.size(); i++) {

            CheckBox cb = new CheckBox(getContext());
            cb.setText(brandNameList.get(i));
            cb.setTextColor(getResources().getColor(R.color.itemTitleColorLight));
            cb.setTag(i);
            cb.setOnCheckedChangeListener(brandCheckListener);
            ll.addView(cb);
        }

    }

    CompoundButton.OnCheckedChangeListener brandCheckListener = new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            // TODO Auto-generated method stub
            if (isChecked) {
                try {
                    int t = (Integer) buttonView.getTag();
                    selectedBrands.add(new Integer(t));
                    tabLayout.setBadgeText(0, selectedBrands.size() + "");
                    filterCount++;
                } catch (Exception e) {
                    Log.e("Filter", e.getMessage());
                }

            }
            if (!isChecked) {
                try {
                    int t = (Integer) buttonView.getTag();
                    selectedBrands.remove(new Integer(t));
                    tabLayout.setBadgeText(0, selectedBrands.size() + "");
                    filterCount--;
                } catch (Exception e) {
                    Log.e("Filter", e.getMessage());
                }
            }

            if(selectedBrands.size() == 0) {
                tabLayout.setBadgeText(0, null);
            }

        }

    };

//Brand

//Category

    public void addcategoryCheckBox(boolean isCustom) {

        Dialog v;

        if(isCustom)
            v = dialog_c;
        else
            v = dialog;

        ArrayList<HashMap<String, String>> dbResult = new ArrayList<HashMap<String, String>>();

        LinearLayout ll = (LinearLayout) v.findViewById(R.id.categoryCheckBox);
        ll.removeAllViews();

        DatabaseHandler db = new DatabaseHandler(getContext());
        if(isCustom)
            dbResult = db.getDistinctCategoryCustom(true);
        else
            dbResult = db.getDistinctCategoryCustom(false);
        List<String> categoryNameList = new ArrayList<String>();

        for (int i = 0; i < dbResult.size(); i++) {
            Map<String, String> temp1 = dbResult.get(i);
            if (!temp1.get(KEY_CATEGORY).equalsIgnoreCase(""))
                categoryNameList.add(temp1.get(KEY_CATEGORY));
        }

        for (int i = 0; i < categoryNameList.size(); i++) {

            CheckBox cb = new CheckBox(getContext());
            cb.setText(categoryNameList.get(i));
            cb.setTextColor(getResources().getColor(R.color.itemTitleColorLight));
            cb.setTag(i);
            if(isCustom){
                cb.setOnCheckedChangeListener(categoryCheckListener_c);
            }
            else{
                cb.setOnCheckedChangeListener(categoryCheckListener);
            }
            ll.addView(cb);
        }

    }

    CompoundButton.OnCheckedChangeListener categoryCheckListener = new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            // TODO Auto-generated method stub
            if (isChecked) {
                try {
                    int t = (Integer) buttonView.getTag();
                    selectedCategory.add(new Integer(t));
                    tabLayout.setBadgeText(1, selectedCategory.size() + "");
                    filterCount++;
                } catch (Exception e) {
                    Log.e("Filter", e.getMessage());
                }

            }
            if (!isChecked) {
                try {
                    int t = (Integer) buttonView.getTag();
                    selectedCategory.remove(new Integer(t));
                    tabLayout.setBadgeText(1, selectedCategory.size() + "");
                    filterCount--;
                } catch (Exception e) {
                    Log.e("Filter", e.getMessage());
                }
            }

            if(selectedCategory.size() == 0) {
                tabLayout.setBadgeText(1, null);
            }
        }

    };

    CompoundButton.OnCheckedChangeListener categoryCheckListener_c = new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            // TODO Auto-generated method stub
            if (isChecked) {
                try {
                    int t = (Integer) buttonView.getTag();
                    selectedCategory_c.add(new Integer(t));
                    tabLayout_c.setBadgeText(0, selectedCategory_c.size() + "");
                    filterCount_c++;
                } catch (Exception e) {
                    Log.e("Filter", e.getMessage());
                }

            }
            if (!isChecked) {
                try {
                    int t = (Integer) buttonView.getTag();
                    selectedCategory_c.remove(new Integer(t));
                    tabLayout_c.setBadgeText(0, selectedCategory_c.size() + "");
                    filterCount_c--;
                } catch (Exception e) {
                    Log.e("Filter", e.getMessage());
                }
            }

            if(selectedCategory_c.size() == 0) {
                tabLayout_c.setBadgeText(0, null);
            }
        }

    };

//Category

//Tile Type

    public void addtileTypeCheckBox(boolean isCustom) {
        Dialog v;

        if(isCustom)
            v = dialog_c;
        else
            v = dialog;

        ArrayList<HashMap<String, String>> dbResult = new ArrayList<HashMap<String, String>>();

        LinearLayout ll = (LinearLayout) v.findViewById(R.id.tileTypeCheckBox);
        ll.removeAllViews();

        DatabaseHandler db = new DatabaseHandler(getContext());
        if(isCustom)
            dbResult = db.getDistinctTypeCustom(true);
        else
            dbResult = db.getDistinctTypeCustom(false);
        List<String> tileTypeList = new ArrayList<String>();

        for (int i = 0; i < dbResult.size(); i++) {
            Map<String, String> temp1 = dbResult.get(i);
            if (!temp1.get(KEY_TYPE).equalsIgnoreCase(""))
                tileTypeList.add(temp1.get(KEY_TYPE));
        }

        for (int i = 0; i < tileTypeList.size(); i++) {

            CheckBox cb = new CheckBox(getContext());
            cb.setText(tileTypeList.get(i));
            cb.setTextColor(getResources().getColor(R.color.itemTitleColorLight));
            cb.setTag(i);
            if(isCustom){
                cb.setOnCheckedChangeListener(titeTypeCheckListener_c);
            }
            else{
                cb.setOnCheckedChangeListener(titeTypeCheckListener);
            }
            ll.addView(cb);
        }

    }

    CompoundButton.OnCheckedChangeListener titeTypeCheckListener = new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            // TODO Auto-generated method stub
            if (isChecked) {
                try {
                    int t = (Integer) buttonView.getTag();
                    selectedType.add(new Integer(t));
                    tabLayout.setBadgeText(2, selectedType.size() + "");
                    filterCount++;
                } catch (Exception e) {
                    Log.e("Filter", e.getMessage());
                }

            }
            if (!isChecked) {
                try {
                    int t = (Integer) buttonView.getTag();
                    selectedType.remove(new Integer(t));
                    tabLayout.setBadgeText(2, selectedType.size() + "");
                    filterCount--;
                } catch (Exception e) {
                    Log.e("Filter", e.getMessage());
                }
            }

            if(selectedType.size() == 0) {
                tabLayout.setBadgeText(2, null);
            }
        }

    };

    CompoundButton.OnCheckedChangeListener titeTypeCheckListener_c = new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            // TODO Auto-generated method stub
            if (isChecked) {
                try {
                    int t = (Integer) buttonView.getTag();
                    selectedType_c.add(new Integer(t));
                    tabLayout_c.setBadgeText(1, selectedType_c.size() + "");
                    filterCount_c++;
                } catch (Exception e) {
                    Log.e("Filter", e.getMessage());
                }

            }
            if (!isChecked) {
                try {
                    int t = (Integer) buttonView.getTag();
                    selectedType_c.remove(new Integer(t));
                    tabLayout_c.setBadgeText(1, selectedType_c.size() + "");
                    filterCount_c--;
                } catch (Exception e) {
                    Log.e("Filter", e.getMessage());
                }
            }

            if(selectedType_c.size() == 0) {
                tabLayout_c.setBadgeText(1, null);
            }
        }

    };

//Tile Type

//Tile Size

    public void addtileSizeCheckBox(boolean isCustom) {
        Dialog v;

        if(isCustom)
            v = dialog_c;
        else
            v = dialog;

        ArrayList<HashMap<String, String>> dbResult = new ArrayList<HashMap<String, String>>();

        LinearLayout ll = (LinearLayout) v.findViewById(R.id.sizeCheckBox);
        ll.removeAllViews();

        DatabaseHandler db = new DatabaseHandler(getContext());
        if(isCustom)
            dbResult = db.getDistinctSizeCustom(true);
        else
            dbResult = db.getDistinctSizeCustom(false);
        List<String> tileSizeList = new ArrayList<String>();

        for (int i = 0; i < dbResult.size(); i++) {
            Map<String, String> temp1 = dbResult.get(i);
            if (!temp1.get(KEY_DIMEN).equalsIgnoreCase(""))
                tileSizeList.add(temp1.get(KEY_DIMEN)+"mm");
        }

        for (int i = 0; i < tileSizeList.size(); i++) {

            CheckBox cb = new CheckBox(getContext());
            cb.setText(tileSizeList.get(i));
            cb.setTextColor(getResources().getColor(R.color.itemTitleColorLight));
            cb.setTag(i);
            if(isCustom){
                cb.setOnCheckedChangeListener(titeSizeCheckListener_c);
            }
            else{
                cb.setOnCheckedChangeListener(titeSizeCheckListener);
            }
            ll.addView(cb);
        }

    }

    CompoundButton.OnCheckedChangeListener titeSizeCheckListener = new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            // TODO Auto-generated method stub
            if (isChecked) {
                try {
                    int t = (Integer) buttonView.getTag();
                    selectedSize.add(new Integer(t));
                    tabLayout.setBadgeText(3, selectedSize.size() + "");
                    filterCount++;
                } catch (Exception e) {
                    Log.e("Filter", e.getMessage());
                }
            }
            if (!isChecked) {
                try {
                    int t = (Integer) buttonView.getTag();
                    selectedSize.remove(new Integer(t));
                    tabLayout.setBadgeText(3, selectedSize.size() + "");
                    filterCount--;
                } catch (Exception e) {
                    Log.e("Filter", e.getMessage());
                }
            }

            if(selectedSize.size() == 0) {
                tabLayout.setBadgeText(3, null);
            }
        }

    };

    CompoundButton.OnCheckedChangeListener titeSizeCheckListener_c = new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            // TODO Auto-generated method stub
            if (isChecked) {
                try {
                    int t = (Integer) buttonView.getTag();
                    selectedSize_c.add(new Integer(t));
                    tabLayout_c.setBadgeText(2, selectedSize_c.size() + "");
                    filterCount_c++;
                } catch (Exception e) {
                    Log.e("Filter", e.getMessage());
                }
            }
            if (!isChecked) {
                try {
                    int t = (Integer) buttonView.getTag();
                    selectedSize_c.remove(new Integer(t));
                    tabLayout_c.setBadgeText(2, selectedSize_c.size() + "");
                    filterCount_c--;
                } catch (Exception e) {
                    Log.e("Filter", e.getMessage());
                }
            }

            if(selectedSize_c.size() == 0) {
                tabLayout_c.setBadgeText(2, null);
            }
        }

    };

//Tile Size

//Tile Color

    public void addtileColorCheckBox() {
        Dialog v = dialog;
        ArrayList<HashMap<String, String>> dbResult = new ArrayList<HashMap<String, String>>();

        LinearLayout ll = (LinearLayout) v.findViewById(R.id.colorCheckBox);
        ll.removeAllViews();

        DatabaseHandler db = new DatabaseHandler(getContext());
        dbResult = db.getDistinctColor();
        List<String> tileColorList = new ArrayList<String>();

        for (int i = 0; i < dbResult.size(); i++) {
            Map<String, String> temp1 = dbResult.get(i);
            if (!temp1.get(KEY_COLOR).equalsIgnoreCase(""))
                tileColorList.add(temp1.get(KEY_COLOR));
        }

        for (int i = 0; i < tileColorList.size(); i++) {

            CheckBox cb = new CheckBox(getContext());
            cb.setText(tileColorList.get(i));
            cb.setTextColor(getResources().getColor(R.color.itemTitleColorLight));
            cb.setTag(i);
            cb.setOnCheckedChangeListener(titeColorCheckListener);
            ll.addView(cb);
        }
    }

    CompoundButton.OnCheckedChangeListener titeColorCheckListener = new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            // TODO Auto-generated method stub
            if (isChecked) {
                try {
                    int t = (Integer) buttonView.getTag();
                    selectedColor.add(new Integer(t));
                    tabLayout.setBadgeText(4, selectedColor.size() + "");
                    filterCount++;
                } catch (Exception e) {
                    Log.e("Filter", e.getMessage());
                }
            }
            if (!isChecked) {
                try {
                    int t = (Integer) buttonView.getTag();
                    selectedColor.remove(new Integer(t));
                    tabLayout.setBadgeText(4, selectedColor.size() + "");
                    filterCount--;
                } catch (Exception e) {
                    Log.e("Filter", e.getMessage());
                }
            }

            if(selectedColor.size() == 0) {
                tabLayout.setBadgeText(4, null);
            }
        }

    };

//Tile Color

    /*public void applyDialog() {

        String cr_category = null, br_name = null, pro_size = null, pro_color = null, pro_type = null;

        DatabaseHandler db = new DatabaseHandler(getContext());
        DatabaseHandler db1 = new DatabaseHandler(getContext());
        DatabaseHandler db2 = new DatabaseHandler(getContext());
        DatabaseHandler db3 = new DatabaseHandler(getContext());
        DatabaseHandler db4 = new DatabaseHandler(getContext());

        dbResult = db.getDistinctCategoryCustom(false);
        dbResult1 = db1.getAllBrand();
        dbResult2 = db2.getDistinctSizeCustom(false);
        dbResult3 = db3.getDistinctColor();
        dbResult4 = db4.getDistinctTypeCustom(false);

        txtSearch.setText("");

        if (selectedCategory.size() > 0)
            cr_category = getStringFromCheckedList(dbResult, selectedCategory,
                    KEY_CATEGORY);
        if (selectedBrands.size() > 0)
            br_name = getStringFromCheckedList(dbResult1, selectedBrands,
                    KEY_BRAND_NAME);
        if (selectedSize.size() > 0)
            pro_size = getStringFromCheckedList(dbResult2, selectedSize,
                    KEY_DIMEN);
        if (selectedColor.size() > 0)
            pro_color = getStringFromCheckedList(dbResult3, selectedColor,
                    KEY_COLOR);
        if (selectedType.size() > 0)
            pro_type = getStringFromCheckedList(dbResult4, selectedType,
                    KEY_TYPE);

        if (filterCount >  0){
            img_filter.setImageResource(R.drawable.dmd_filter_active);
        }
        else{
            img_filter.setImageResource(R.drawable.dmd_filter);
        }

        DatabaseHandler db5 = new DatabaseHandler(getContext());
        filterResult = db5.getResultByFilterNew(cr_category, br_name, pro_size,
                pro_color, pro_type, "M");

        if(filterResult.size() > 0){
            view_errorText.setVisibility(View.GONE);
            patternView.setVisibility(View.VISIBLE);
        }
        else{
            view_errorText.setVisibility(View.VISIBLE);
            patternView.setVisibility(View.GONE);
        }

        LibraryAdapter pgAdapter = new LibraryAdapter(getContext(), filterResult,
                LibraryFragment.this, "M");
        patternView.refreshDrawableState();
        pgAdapter.notifyDataSetChanged();
        patternView.setAdapter(null);
        patternView.setAdapter(pgAdapter);

        patternView.setLayoutManager(new GridLayoutManager(getContext(), 5));
        adapter = new RecyclerPaternAdapter(getContext(), dbPaginatedList, LibraryFragment.this);
        patternView.setAdapter(adapter);

        dialog.dismiss();

    }*/

    public void applyDialog() {

        String cr_category = null, br_name = null, pro_size = null, pro_color = null, pro_type = null;

        DatabaseHandler db = new DatabaseHandler(getContext());
        DatabaseHandler db1 = new DatabaseHandler(getContext());
        DatabaseHandler db2 = new DatabaseHandler(getContext());
        DatabaseHandler db3 = new DatabaseHandler(getContext());
        DatabaseHandler db4 = new DatabaseHandler(getContext());

        dbResult = db.getDistinctCategory();
        dbResult1 = db1.getAllBrand();
        dbResult2 = db2.getDistinctSize();
        dbResult3 = db3.getDistinctColor();
        dbResult4 = db4.getDistinctType();

        txtSearch.setText("");

        if (selectedCategory.size() > 0)
            cr_category = getStringFromCheckedList(dbResult, selectedCategory,
                    KEY_CATEGORY);
        if (selectedBrands.size() > 0)
            br_name = getStringFromCheckedList(dbResult1, selectedBrands,
                    KEY_BRAND_NAME);
        if (selectedSize.size() > 0)
            pro_size = getStringFromCheckedList(dbResult2, selectedSize,
                    KEY_DIMEN);
        if (selectedColor.size() > 0)
            pro_color = getStringFromCheckedList(dbResult3, selectedColor,
                    KEY_COLOR);
        if (selectedType.size() > 0)
            pro_type = getStringFromCheckedList(dbResult4, selectedType,
                    KEY_TYPE);

        if (filterCount >  0){
            img_filter.setImageResource(R.drawable.dmd_filter_active);
        }
        else{
            img_filter.setImageResource(R.drawable.dmd_filter);
        }

        DatabaseHandler db5 = new DatabaseHandler(getContext());
        filterResult = db5.getResultByFilterNew(cr_category, br_name, pro_size,
                pro_color, pro_type, "M");

        if(filterResult.size() > 0){
            view_errorText.setVisibility(View.GONE);
            patternView.setVisibility(View.VISIBLE);

            searchFlag = 1;
            dbPaginatedList.clear();
            dbPaginatedList.addAll(filterResult);
            adapter.notifyDataSetChanged();
        }
        else{
            view_errorText.setVisibility(View.VISIBLE);
            patternView.setVisibility(View.GONE);
        }

        dialog.dismiss();

    }

    public void applyDialogCustom() {

        String cr_category = null, br_name = null, pro_size = null, pro_color = null, pro_type = null;

        DatabaseHandler db = new DatabaseHandler(getContext());
        DatabaseHandler db1 = new DatabaseHandler(getContext());
        DatabaseHandler db2 = new DatabaseHandler(getContext());
        DatabaseHandler db3 = new DatabaseHandler(getContext());
        DatabaseHandler db4 = new DatabaseHandler(getContext());

        dbResult_c = db.getDistinctCategoryCustom(true);
//        dbResult1_c = db1.getAllBrand();
        dbResult2_c = db2.getDistinctSizeCustom(true);
//        dbResult3_c = db3.getDistinctColor();
        dbResult4_c = db4.getDistinctTypeCustom(true);

        txtSearch_c.setText("");

        if (selectedCategory_c.size() > 0)
            cr_category = getStringFromCheckedList(dbResult_c, selectedCategory_c,
                    KEY_CATEGORY);
        if (selectedBrands_c.size() > 0)
            br_name = getStringFromCheckedList(dbResult1_c, selectedBrands_c,
                    KEY_BRAND_NAME);
        if (selectedSize_c.size() > 0)
            pro_size = getStringFromCheckedList(dbResult2_c, selectedSize_c,
                    KEY_DIMEN);
        if (selectedColor_c.size() > 0)
            pro_color = getStringFromCheckedList(dbResult3_c, selectedColor_c,
                    KEY_COLOR);
        if (selectedType_c.size() > 0)
            pro_type = getStringFromCheckedList(dbResult4_c, selectedType_c,
                    KEY_TYPE);

        if (filterCount_c >  0){
            img_filter_c.setImageResource(R.drawable.dmd_filter_active);
        }
        else{
            img_filter_c.setImageResource(R.drawable.dmd_filter);
        }

        DatabaseHandler db5 = new DatabaseHandler(getContext());
        filterResult_c = db5.getResultByFilterNew(cr_category, br_name, pro_size,
                pro_color, pro_type, "C");

        if(filterResult_c.size() > 0){
            view_errorText_c.setVisibility(View.GONE);
            patternView_c.setVisibility(View.VISIBLE);

            dbPaginatedList_c.clear();
            dbPaginatedList_c.addAll(filterResult_c);
            adapter_c.notifyDataSetChanged();
        }
        else{
            view_errorText_c.setVisibility(View.VISIBLE);
            patternView_c.setVisibility(View.GONE);
        }

        /*LibraryAdapter pgAdapter = new LibraryAdapter(getContext(), filterResult_c,
                LibraryFragment.this, "C");
        patternView_c.refreshDrawableState();
        pgAdapter.notifyDataSetChanged();
        patternView_c.setAdapter(null);
        patternView_c.setAdapter(pgAdapter);*/

        dialog_c.dismiss();

    }


    public String getStringFromCheckedList(
            ArrayList<HashMap<String, String>> dbResult,
            List<Integer> selectedItems, String ketItem) {
        List<String> itemString = new ArrayList<String>();
        String resString = "";
        if (selectedItems.size() == 0) {
            return null;
        }
        for (int i = 0; i < dbResult.size(); i++) {
            String temp = dbResult.get(i).get(ketItem);
            if (temp.contains("mm")) {
                // itemString.add(temp.replace("mm", ""));
                itemString.add(temp);
            } else {
                itemString.add(temp);
            }
        }
        if (selectedItems.size() == 1) {
            try {
                int index = selectedItems.get(0);
                resString = "'" + itemString.get(index) + "'";
            } catch (Exception ex) {
                Log.e("error", ex.getMessage());

            }
        } else {
            resString = "'" + itemString.get(selectedItems.get(0)) + "'";
            System.out.println(selectedItems.size());
            for (int i = 1; i < selectedItems.size(); i++) {
                try {
                    int itemId = selectedItems.get(i);
                    resString += ",'" + itemString.get(selectedItems.get(i))
                            + "'";
                } catch (Exception e) {
                    // TODO: handle exception
                    Log.e("Filter", "getStringFromCheckedList error!_"+e.getMessage());
                    return null;

                }
            }
        }
        return resString;

    }

    private void clearFilter(){

        filterCount = 0;
        img_filter.setImageResource(R.drawable.dmd_filter);

        selectedCategory.clear();
        selectedBrands.clear();
        selectedSize.clear();
        selectedColor.clear();
        selectedType.clear();

        tabLayout.setBadgeText(0,null);
        tabLayout.setBadgeText(1,null);
        tabLayout.setBadgeText(2,null);
        tabLayout.setBadgeText(3,null);
        tabLayout.setBadgeText(4,null);

        getAllpatterns();

        /*LinearLayout ll = (LinearLayout) dialog.findViewById(R.id.brandCheckBox);
        ll.removeAllViews();*/

        addbrandCheckBox();
        addcategoryCheckBox(false);
        addtileTypeCheckBox(false);
        addtileSizeCheckBox(false);
        addtileColorCheckBox();

        dialog.dismiss();
    }

    private void clearFilterCustom(){

        filterCount_c = 0;
        img_filter_c.setImageResource(R.drawable.dmd_filter);

        selectedCategory_c.clear();
        selectedBrands_c.clear();
        selectedSize_c.clear();
        selectedColor_c.clear();
        selectedType_c.clear();

        tabLayout_c.setBadgeText(0,null);
        tabLayout_c.setBadgeText(1,null);
        tabLayout_c.setBadgeText(2,null);

        getAllpatternsCustom(getContext());

        /*LinearLayout ll = (LinearLayout) dialog_c.findViewById(R.id.categoryCheckBox);
        ll.removeAllViews();*/

        addcategoryCheckBox(true);
        addtileTypeCheckBox(true);
        addtileSizeCheckBox(true);

        dialog_c.dismiss();
    }

    private void showFilterDialog(){
        TabLayout.Tab tab = tabLayout.getTabAt(0);
        tab.select();
        dialog.show();
    }

    private void showFilterDialogCustom(){
        TabLayout.Tab tab = tabLayout_c.getTabAt(0);
        tab.select();
        dialog_c.show();
    }


    public void createProject(int type){

        if(type == 1) {
            Fragment fragment = new New3dFragment();
            if (fragment != null) {
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right,
                        R.anim.slide_out_left, R.anim.slide_in_left);
                ft.replace(R.id.content_frame_id, fragment);
                ft.addToBackStack(null);
                ft.commit();
            }
        }
    }

    List<Integer> selectedTiles = new ArrayList<Integer>();
    private Dialog dialog_download;
//    private ExpandableListAdapter listAdapter;
    private MyExpandableListAdapter listAdapter;
    private ExpandableListView expListView;
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;
    private HashMap<String, List<String>> listDataChildIds;
    private ImageView dialog_close;
    private LinearLayout LoaderLay;
    private LinearLayout selectLay;
    private LinearLayout errorLay;
    private LinearLayout resultLay;
    private RelativeLayout right_ph_lay;
    private RelativeLayout right_down_lay;
    private TextView error_text;
    private TextView down_error_text;
    private TextView result_text;
    private TextView down_tile_name;
    private TextView down_brand_name;
    private TextView down_progress;
    private TextView txt_tot_tiles;
    private TextView down_count;
    private Button downloadButton;
    private Button cancel_download;
    private ProgressBar down_progress_bar;

    public void syncLibrary() {

        if(downloadingFlag != 1) {
            showDialog();
        }
    }

    public void showDialog(){

        final String token;
        TextView dialog_title;
        final Button btn_ok;
        final Button btn_sync_again;

        token = "Bearer " + session.getAccessToken();

        dialog_download = new Dialog(getContext());
        dialog_download.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_download.setCancelable(false);
        dialog_download.setContentView(R.layout.dmd_dialog_download);
        dialog_download.getWindow()
                .getAttributes().windowAnimations = R.style.DialogAnimation;

        dialog_download.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog_title = (TextView) dialog_download.findViewById(R.id.dialog_title);
        dialog_close = (ImageView) dialog_download.findViewById(R.id.dialog_close);
        downloadButton = dialog_download.findViewById(R.id.start_download);
        cancel_download = dialog_download.findViewById(R.id.cancel_download);
        btn_ok = dialog_download.findViewById(R.id.btn_ok);
        btn_sync_again = dialog_download.findViewById(R.id.btn_sync_again);
        LoaderLay = dialog_download.findViewById(R.id.LoaderLay);
        selectLay = dialog_download.findViewById(R.id.selectLay);
        errorLay = dialog_download.findViewById(R.id.errorLay);
        resultLay = dialog_download.findViewById(R.id.resultLay);
        error_text = dialog_download.findViewById(R.id.error_text);
        down_error_text = dialog_download.findViewById(R.id.down_error_text);
        down_tile_name = dialog_download.findViewById(R.id.down_tile_name);
        down_brand_name = dialog_download.findViewById(R.id.down_brand_name);
        down_progress = dialog_download.findViewById(R.id.down_progress);
        down_progress_bar = dialog_download.findViewById(R.id.down_progress_bar);
        right_ph_lay = dialog_download.findViewById(R.id.right_ph_lay);
        right_down_lay = dialog_download.findViewById(R.id.right_down_lay);
        result_text = dialog_download.findViewById(R.id.result_text);
        down_count = dialog_download.findViewById(R.id.down_count);
        txt_tot_tiles = dialog_download.findViewById(R.id.txt_tot_tiles);

        dialog_title.setText("Download Tile Library");

        LoaderLay.setVisibility(View.VISIBLE);
        selectLay.setVisibility(View.GONE);
        errorLay.setVisibility(View.GONE);
        resultLay.setVisibility(View.GONE);
        down_error_text.setVisibility(View.GONE);
        downloadButton.setVisibility(View.VISIBLE);
        cancel_download.setVisibility(View.GONE);

        dialog_close.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                downloadingFlag = 0;
                dialog_download.cancel();
                prepareFilterDialog();
                getAllpatterns();
            }
        });

        downloadButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                if(selectedTiles.isEmpty()){
                    down_error_text.setVisibility(View.VISIBLE);
                    return;
                }
                else{
                    downloadTask = new DownloadTask(getActivity());
                    downloadTask.execute("");
                }
            }
        });

        btn_sync_again.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                LoaderLay.setVisibility(View.VISIBLE);
                selectLay.setVisibility(View.GONE);
                errorLay.setVisibility(View.GONE);
                resultLay.setVisibility(View.GONE);
                down_error_text.setVisibility(View.GONE);
                downloadButton.setVisibility(View.VISIBLE);
                dialog_close.setVisibility(View.VISIBLE);
                cancel_download.setVisibility(View.GONE);
                dialog_close.setVisibility(View.VISIBLE);

                getTiles(token);

            }
        });

        btn_ok.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                downloadingFlag = 0;
                dialog_download.cancel();
                prepareFilterDialog();
                getAllpatterns();
            }
        });

        cancel_download.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                downloadTask.cancel(true);
                downloadingFlag = 0;

                result_text.setText("Downloading Tiles Cancelled!");
                LoaderLay.setVisibility(View.GONE);
                selectLay.setVisibility(View.GONE);
                errorLay.setVisibility(View.GONE);
                resultLay.setVisibility(View.VISIBLE);
                down_error_text.setVisibility(View.GONE);
            }
        });

        expListView = (ExpandableListView) dialog_download.findViewById(R.id.lvExp);
        getTiles(token);
        dialog_download.show();

    }

    public void getTiles(String access_token) {
        try {
            selectedTiles.clear();
            String filter = "";
            List<SettingsModel> settngs;
            settngs = DatabaseManager.getSettings(getActivity());
            filter = settngs.get(0).getUpdated_at();

            ApiInterface apiService =
                    ApiClient.getClient().create(ApiInterface.class);
            Call<LoadTileResult> call = apiService.getTiles(filter, access_token);

            call.enqueue(new retrofit2.Callback<LoadTileResult>() {
                @Override
                public void onResponse(Call<LoadTileResult> call, Response<LoadTileResult> response) {
                    int statusCode = response.code();
                    if (response.isSuccessful()) {
                        if (statusCode == 200) {

                            if (!response.body().getData().isEmpty()) {
                                String name;
                                String updated_at = "";
                                String status = "0";
                                ArrayList<String> tile_ids = new ArrayList<String>();
                                downloadingFlag = 1;

                                DatabaseHandler db = new DatabaseHandler(
                                        getContext());
                                ArrayList<HashMap<String, String>> dbResult = new ArrayList<HashMap<String, String>>();

                                int countRec = 0;
                                String unit;
                                float w,h;
                                dbResult = db.getAllPattern();
                                countRec = dbResult.size();
                                txt_tot_tiles.setText("Downloaded Tiles in local Library : "+countRec);

                                for (int i = 0; i < response.body().getData().size(); i++) {

                                    name = response.body().getData().get(i).getName();
//                                    dDialog.setTitleText("Downloading Tile Library...");

                                    TileModel tile = new TileModel();
                                    updated_at = response.body().getData().get(0).getUpdatedAt();
                                    unit= response.body().getData().get(i).getUnit().trim();

                                    w = Float.parseFloat(response.body().getData().get(i).getWidth());
                                    h = Float.parseFloat(response.body().getData().get(i).getHeight());

                                    switch (Integer.parseInt(unit)){
                                        case 2:
                                            w = GlobalVariables.inchesToMm(w);
                                            h = GlobalVariables.inchesToMm(h);
                                            break;
                                        case 3:
                                            w = GlobalVariables.feetToMm(w);
                                            h = GlobalVariables.feetToMm(h);
                                            break;
                                    }

                                    /*String dim = Math.round(Float.parseFloat(response.body().getData().get(i).getWidth())) + "x" +
                                            Math.round(Float.parseFloat(response.body().getData().get(i).getHeight()));*/
                                    String dim = Math.round(w) + "x" +
                                            Math.round(h);
                                    if (response.body().getData().get(i).getIsActive()) {
                                        status = "1";
                                    } else {
                                        status = "0";
                                    }

                                    tile.setTile_name(response.body().getData().get(i).getName());
                                    tile.setTile_color(response.body().getData().get(i).getColorName());
                                    tile.setTile_dimen(dim);
                                    tile.setTile_type(response.body().getData().get(i).getTileTypeName());
                                    tile.setBarnd_id(response.body().getData().get(i).getBrandId());
                                    tile.setTile_brand(response.body().getData().get(i).getBrandName());
                                    tile.setType_id(response.body().getData().get(i).getTileType());
                                    tile.setCategory_id(response.body().getData().get(i).getCategory());
                                    tile.setTile_id(response.body().getData().get(i).getId());
                                    tile.setTile_category(response.body().getData().get(i).getCategoryName());
                                    tile.setTile_type(response.body().getData().get(i).getTileTypeName());
                                    tile.setColor_code(response.body().getData().get(i).getColorCode());
                                    tile.setModel_no(response.body().getData().get(i).getModelNo());
                                    tile.setTile_p_type("M");
                                    tile.setTile_price(response.body().getData().get(i).getPrice());
                                    tile.setImage_url(response.body().getData().get(i).getImageUrl());
                                    tile.setIs_active(status);

//                                    tile_ids[i] = response.body().getData().get(i).getId();
                                    tile_ids.add(response.body().getData().get(i).getId().trim());

                                    db.insertRecordNew(tile);

//                                    Log.d("DMD_Log", "URL from server - " + response.body().getData().get(i).getName());

                                }

                                SettingsModel settingsdata = new SettingsModel();
                                settingsdata.setId(1);
                                settingsdata.setDevice_type("A");
                                settingsdata.setUpdated_at(updated_at);

                                DatabaseManager.addSettingsInfo(getActivity(), settingsdata);

                                /*dDialog.setTitleText("Syncing Tile Library...");

                                downloadTask = new DownloadTask(getActivity());
                                downloadTask.execute("");*/

                                prepareListData();

                                LoaderLay.setVisibility(View.GONE);
                                selectLay.setVisibility(View.VISIBLE);
                                right_ph_lay.setVisibility(View.VISIBLE);
                                right_down_lay.setVisibility(View.GONE);


                            } /*else {
                                dDialog.setTitleText(getString(R.string.oops))
                                        .setContentText("Could not complete Tile Library sync!")
                                        .setConfirmText("OK")
                                        .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                            }*/


                        } else {
                            JSONObject jObjError = null;
                            try {

                                jObjError = new JSONObject(response.errorBody().string());

                                LoaderLay.setVisibility(View.GONE);
                                error_text.setText(jObjError.getString("error_message"));
                                errorLay.setVisibility(View.VISIBLE);

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

                            if (statusCode == 402 || statusCode == 406){
                                ((DashboardActivity) getActivity()).sessionOut();
                            }
                            else{

                                if (jObjError.getString("error").equals("invalid_token")) {
                                    ((DashboardActivity) getActivity()).sessionOut();
                                } else {
                                    LoaderLay.setVisibility(View.GONE);
                                    error_text.setText(jObjError.getString("error_message"));
                                    errorLay.setVisibility(View.VISIBLE);
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<LoadTileResult> call, Throwable throwable) {

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

                    LoaderLay.setVisibility(View.GONE);
                    error_text.setText(errot_text);
                    errorLay.setVisibility(View.VISIBLE);

                }
            });


        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }
    }

    public void setTiesForDownload(String tile, Boolean checked){
        String[] separated = tile.split("X");
        int groupPosition = Integer.parseInt(separated[0]);
        int childPosition = Integer.parseInt(separated[1]);
        int tile_id = 0;

        tile_id = Integer.parseInt(listDataChildIds.get(
                listDataHeader.get(groupPosition)).get(
                childPosition));

        if(checked) {
            selectedTiles.add(new Integer(tile_id));
        }else{
            selectedTiles.remove(new Integer(tile_id));
        }

        if(!selectedTiles.isEmpty()){
            down_error_text.setVisibility(View.GONE);
        }
    }

    private void prepareListData() {
        DatabaseHandler dh = new DatabaseHandler(context);
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();
        listDataChildIds = new HashMap<String, List<String>>();

        List<TileModel> tileList;
        tileList = dh.getTileDetailsforDownload();

        if(tileList.size() > 0){

            String brand_name;

            for(int i=0; i<tileList.size(); i++){
                brand_name = tileList.get(i).getTile_brand().toString().trim();
                listDataHeader.add(brand_name);
            }

            HashSet<String> hashSet = new HashSet<String>();
            hashSet.addAll(listDataHeader);
            listDataHeader.clear();
            listDataHeader.addAll(hashSet);

            for (String curVal : listDataHeader){
                List<String> tempNameList = new ArrayList<String>();
                List<String> tempIdList = new ArrayList<String>();
                for(int i=0; i<tileList.size(); i++){
                    brand_name = tileList.get(i).getTile_brand().toString().trim();
                    if (curVal.contains(brand_name)){
                        tempNameList.add(tileList.get(i).getTile_name().toString().trim());
                        tempIdList.add(tileList.get(i).getTile_id().toString().trim());
                    }
                }
                listDataChild.put(curVal, tempNameList);
                listDataChildIds.put(curVal, tempIdList);
            }

//            listAdapter = new ExpandableListAdapter(LibraryFragment.this, listDataHeader, listDataChild);
            listAdapter = new MyExpandableListAdapter(LibraryFragment.this, listDataHeader, listDataChild);
            expListView.setAdapter(listAdapter);
            listAdapter.notifyDataSetChanged();
        }

        if(listDataHeader.isEmpty()) {
            LoaderLay.setVisibility(View.GONE);
            error_text.setText("Tiles are not available for download!");
            errorLay.setVisibility(View.VISIBLE);
        }
    }

    private class DownloadTask extends AsyncTask<String, Integer, List<RowItem1>> {
        ProgressDialog progressDialog;
        private Activity context;
        List<RowItem1> rowItems;
        int noOfURLs;

        public DownloadTask(Activity context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {

        }


        @Override
        protected List<RowItem1> doInBackground(String... value) {
            DatabaseHandler dh = new DatabaseHandler(context);
            List<TileModel> tileList;
            rowItems = new ArrayList<RowItem1>();
            Bitmap map = null;

            tileList = dh.getTileDetailsforDownload();
//            noOfURLs = tileList.size();
            noOfURLs = selectedTiles.size();

            if(tileList.size() > 0){
                int id;
                String tile_id;
                String url_link;
                String tile_name;
                String brand_name;

                for(int i=0; i<tileList.size(); i++){

                    url_link = storageURL+tileList.get(i).getImage_url();

                    tile_id = tileList.get(i).getTile_id().trim();
                    tile_name = tileList.get(i).getTile_name();
                    brand_name = tileList.get(i).getTile_brand();
                    id = Integer.parseInt(tile_id);

                    if(!selectedTiles.contains(id)){
                        continue;
                    }

                    if(!url_link.isEmpty()) {

                        setDownText(tile_name, brand_name);
                        map = downloadImage(url_link);

                        if(map != null && !tile_name.isEmpty() && !brand_name.isEmpty() && !tile_id.isEmpty()) {

                            writeStorate(context, map, tile_id, tile_name, brand_name);
//                            rowItems.add(new RowItem(map));
                            rowItems.add(new RowItem1(""));
                        }
                    }
                }
            }

            return rowItems;
        }

        protected void onProgressUpdate(Integer... progress) {

            if (rowItems != null) {

                /*down_progress.setText("Loading " + (rowItems.size() + 1) + "/" + noOfURLs
                        + "  (" + progress[0] + "%)");*/

                down_progress_bar.setProgress(progress[0]);
                down_progress.setText(progress[0] + "%");
                down_count.setText("Downloading " + (rowItems.size() + 1) + "/" + noOfURLs);

            }

        }

        @Override
        protected void onPostExecute(List<RowItem1> rowItems) {

            downloadingFlag = 0;

            result_text.setText("Downloading completed Successfully!");
            LoaderLay.setVisibility(View.GONE);
            selectLay.setVisibility(View.GONE);
            errorLay.setVisibility(View.GONE);
            resultLay.setVisibility(View.VISIBLE);
            down_error_text.setVisibility(View.GONE);

        }

        private void setDownText(final String name, final String brand){
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    down_count.setText("Downloading " + (rowItems.size() + 1) + "/" + noOfURLs);

                    down_tile_name.setText(name);
                    down_brand_name.setText(brand);
                    down_progress_bar.setProgress(0);
                    down_progress.setText(0 + "%");

                    right_ph_lay.setVisibility(View.GONE);
                    right_down_lay.setVisibility(View.VISIBLE);

                    downloadButton.setVisibility(View.GONE);
                    dialog_close.setVisibility(View.GONE);
                    dialog_close.setVisibility(View.GONE);
                    cancel_download.setVisibility(View.VISIBLE);
                }
            });
        }

        private Bitmap downloadImage(String urlString) {

            int count = 0;
            Bitmap bitmap = null;

            URL url;
            InputStream inputStream = null;
            BufferedOutputStream outputStream = null;

            try {
                url = new URL(urlString);
                URLConnection connection = url.openConnection();
                long lenghtOfFile = connection.getContentLength();

                inputStream = new BufferedInputStream(url.openStream());
                ByteArrayOutputStream dataStream = new ByteArrayOutputStream();

                outputStream = new BufferedOutputStream(dataStream);

                byte data[] = new byte[4096];
                long total = 0;

                publishProgress(0);

                while ((count = inputStream.read(data)) != -1) {
                    total += count;
//                    Log.d("Downloading", "total " + total + " Count " + count + "___" + lenghtOfFile);
                    publishProgress((int) ((total * 100) / lenghtOfFile));
                    outputStream.write(data, 0, count);
                }
                outputStream.flush();

                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                bmOptions.inSampleSize = 1;

                byte[] bytes = dataStream.toByteArray();
                bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, bmOptions);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                FileUtils.close(inputStream);
                FileUtils.close(outputStream);
            }
            return bitmap;
        }
    }

    private void writeStorate(Context context, Bitmap photo, String tile_id, String tile_name, String brand) {

        DatabaseHandler dh = new DatabaseHandler(context);
        String file_path = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/DMD/" + brand + "/";

        File dir = new File(file_path);
        if (!dir.exists())
            dir.mkdirs();
        File file = new File(dir, tile_name + ".jpg");

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
            dh.updateDownloadStatus(tile_id);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void addCustomTiles(){
        Fragment fragment = new NewTileFragment();
        if (fragment != null && !session.getFragmentId().toString().equals("tab3.3")) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction ft=fragmentManager.beginTransaction();
            ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right,
                    R.anim.slide_out_left, R.anim.slide_in_left);
            ft.add(R.id.content_frame_id, fragment);
            ft.hide(LibraryFragment.this);
            ft.addToBackStack(LibraryFragment.class.getName());
            ft.commit();
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();

        new getPatterns(getContext()).execute();
        new getPatternsCustom(context).execute();
        prepareFilterDialog();
        prepareFilterDialogCustom(getContext());

    }
}