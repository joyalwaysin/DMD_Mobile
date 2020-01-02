package com.nagainfomob.app.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nagainfomob.app.DisplayMyDesign.CustomPatternActivity;
import com.nagainfomob.app.DisplayMyDesign.GlobalVariables;
import com.nagainfomob.app.R;
import com.nagainfomob.app.adapter.RecyclerPaternAdapter;
import com.nagainfomob.app.database.DatabaseHandler;
import com.nagainfomob.app.helpers.SessionManager;
import com.nagainfomob.app.main.DashboardActivity;
import com.nagainfomob.app.model.SettingsModel;
import com.nagainfomob.app.sql.DatabaseManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;


/**
 * Created by root on 2/2/17.
 */

public class PatternFragment extends Fragment implements View.OnClickListener {

    private EditText txtSearch;

    ArrayList<HashMap<String, String>> dbPaginatedList = new ArrayList<HashMap<String, String>>();

    private RecyclerView patternView;
    private LinearLayout view_placeholder;
    private LinearLayout view_errorText;
    private LinearLayout pattern_detailed_view;
    private LinearLayout edit_pattern;
    private LinearLayout viewProgress;

    private Button settingsButton;
    private ImageView img_add;
    private ImageView pattern_img;

    private TextView tile_name;
    private TextView tile_category;
    private TextView tile_type;
    private TextView tile_dim;
    private TextView tile_price;
    private TextView brand_name;
    private TextView backToView;
    private TextView title;

    private String pat_name = "";
    private String pat_dimen = "";
    private String pat_brand = "";

    private SessionManager session;
    private String access_token;

    private RecyclerPaternAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_pattern, container, false);

        initViews(view);
        initListeners();
        initObjects();

        session = new SessionManager(getActivity());
        getDatafromdb();
        session.setBackStackId("0");

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
        img_add = view.findViewById(R.id.img_add);
        patternView = (RecyclerView) view.findViewById(R.id.view_pattern);
        view_placeholder = (LinearLayout) view.findViewById(R.id.view_placeholder);
        viewProgress = (LinearLayout) view.findViewById(R.id.viewProgress);
        edit_pattern = (LinearLayout) view.findViewById(R.id.edit_pattern);
        view_errorText = (LinearLayout) view.findViewById(R.id.view_errorText);
        tile_name = (TextView) view.findViewById(R.id.tile_name);
        brand_name = (TextView) view.findViewById(R.id.brand_name);
        tile_category = (TextView) view.findViewById(R.id.tile_category);
        tile_type = (TextView) view.findViewById(R.id.tile_type);
        tile_dim = (TextView) view.findViewById(R.id.tile_dim);
        tile_price = (TextView) view.findViewById(R.id.tile_price);
        pattern_img = (ImageView) view.findViewById(R.id.pattern_img);
        pattern_detailed_view = (LinearLayout) view.findViewById(R.id.pattern_detailed_view);
        backToView = (TextView) view.findViewById(R.id.backToView);
        title = (TextView) view.findViewById(R.id.title);

        txtSearch = (EditText) view.findViewById(R.id.txtSearch);

        txtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    ((DashboardActivity)getActivity()).hideSoftKeyboard();
                    searchPattern(txtSearch.getText().toString());
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
                    searchPattern(s.toString());
                }
                else {
                    getAllpatterns();
                }
            }
        });
    }

    private void initObjects() {
        /*inputValidation = new InputValidation(getActivity());
        session = new SessionManager(getActivity());*/
    }

    private void initListeners() {
        img_add.setOnClickListener(this);
        backToView.setOnClickListener(this);
        edit_pattern.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_add:
                viewNewPattern();
                break;
            case R.id.backToView:
                backtoLibrary();
                break;
            case R.id.edit_pattern:
                editPattern();
                break;
        }
    }

    private void getDatafromdb(){
        List<SettingsModel> settngs;
        settngs = DatabaseManager.getSettings(getActivity());
        access_token = "Bearer "+settngs.get(0).getAccessToken();

    }

    public void getAllpatterns() {
        DatabaseHandler db = new DatabaseHandler(getActivity());
        ArrayList<HashMap<String, String>> dbResult = new ArrayList<HashMap<String, String>>();
        viewProgress.setVisibility(View.VISIBLE);
        dbPaginatedList.clear();
        dbResult = db.getPatterns();

        if(dbResult.size() > 0){
            dbPaginatedList.addAll(dbResult);
            view_errorText.setVisibility(View.GONE);
            viewProgress.setVisibility(View.GONE);
            view_placeholder.setVisibility(View.GONE);
            patternView.setVisibility(View.VISIBLE);
        }
        else{
            view_errorText.setVisibility(View.GONE);
            viewProgress.setVisibility(View.GONE);
            view_placeholder.setVisibility(View.VISIBLE);
            patternView.setVisibility(View.GONE);
        }

        patternView.setLayoutManager(new GridLayoutManager(getContext(), 5));
        adapter = new RecyclerPaternAdapter(getContext(), dbPaginatedList, PatternFragment.this);
        patternView.setAdapter(adapter);

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
            dbResult = db.getPatterns();
            return dbResult;
        }

        @Override
        protected void onPostExecute(ArrayList<HashMap<String, String>> result) {

            if(result.size() > 0){
                dbPaginatedList.addAll(result);
                view_errorText.setVisibility(View.GONE);
                viewProgress.setVisibility(View.GONE);
                view_placeholder.setVisibility(View.GONE);
                patternView.setVisibility(View.VISIBLE);
            }
            else{
                view_errorText.setVisibility(View.GONE);
                viewProgress.setVisibility(View.GONE);
                view_placeholder.setVisibility(View.VISIBLE);
                patternView.setVisibility(View.GONE);
            }

            /*PatternAdapter pgAdapter = new PatternAdapter(getContext(), result,
                    PatternFragment.this);
            patternView.refreshDrawableState();
            pgAdapter.notifyDataSetChanged();
            patternView.setAdapter(null);
            patternView.setAdapter(pgAdapter);*/

            patternView.setLayoutManager(new GridLayoutManager(getContext(), 5));
            adapter = new RecyclerPaternAdapter(getContext(), dbPaginatedList, PatternFragment.this);
            patternView.setAdapter(adapter);
        }
    }

    public void viewPattern(String path, String tname, final String tileSize, String brand,
                            String type, String category, String price) {

        Bitmap bmp;
        final String filePath = path;
        bmp = BitmapFactory.decodeFile(filePath);
        String[] tName = tname.split("/");
        tname = tName[tName.length - 1].replace(".jpg", "");

        if(category.equals("")) category = "Not Available";
        if(type.equals("")) type = "Not Available";
        if(price.equals("")) price = "Not Available";

        tile_name.setText(tname);
        brand_name.setText(brand);
        tile_category.setText(category);
        tile_type.setText(type);
        tile_price.setText("Rs. "+price);
        tile_dim.setText(tileSize + " mm");
        pattern_img.setImageBitmap(bmp);

        patternView.setVisibility(View.GONE);
        pattern_detailed_view.setVisibility(View.VISIBLE);
        title.setVisibility(View.VISIBLE);
        edit_pattern.setVisibility(View.VISIBLE);
        txtSearch.setVisibility(View.GONE);
        img_add.setVisibility(View.GONE);

        pat_name = tname;
        pat_dimen = tileSize;
        pat_brand = brand;

        session.setFragmentId("tab4.2");
    }

    private void editPattern(){

        if(!pat_name.equals("") && !pat_dimen.equals("") && !pat_brand.equals("")){

            String[] sizes = pat_dimen.split("x");
            String h = sizes[0].trim();
            String w = sizes[1].trim();

            GlobalVariables.setProjectName(pat_name);
            GlobalVariables.setUnit("Millimeter");
            GlobalVariables.setDesignerUnit(GlobalVariables.getUnit());
            GlobalVariables.setWallDim(Float.parseFloat(h), Float.parseFloat(w), 0, 0 ,0);

            Intent intent = new Intent(getActivity(), CustomPatternActivity.class);
            intent.putExtra("name", pat_name);
            intent.putExtra("width", w+"");
            intent.putExtra("height", h+"");
            intent.putExtra("category", "");
            intent.putExtra("category_id", "");
            intent.putExtra("price", "");
            intent.putExtra("brand", pat_brand);
            intent.putExtra("type", "");
            intent.putExtra("type_id", "");
            intent.putExtra("is_edit", "1");

            intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
//            startActivity(intent);
            startActivityForResult(intent, 1);
        }
        else{
            new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Oops...")
                    .setContentText("Something went wrong;  Invalid pattern details!")
                    .show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == 0){
                session.setBackStackId("0");
                backtoLibrary();
                getAllpatterns();
            }
        }
    }//onActivityResult

    private void backtoLibrary(){
        pattern_detailed_view.setVisibility(View.GONE);
        patternView.setVisibility(View.VISIBLE);
        title.setVisibility(View.GONE);
        edit_pattern.setVisibility(View.GONE);
        txtSearch.setVisibility(View.VISIBLE);
        img_add.setVisibility(View.VISIBLE);

        pat_name = "";
        pat_dimen = "";
        pat_brand = "";

        session.setFragmentId("tab4");
    }

    private void searchPattern(String str){

        DatabaseHandler db = new DatabaseHandler(getContext());
        ArrayList<HashMap<String, String>> dbResult = new ArrayList<HashMap<String, String>>();

        dbResult = db.searchPatterns(str);

        if(dbResult.size() > 0){
            view_errorText.setVisibility(View.GONE);
            patternView.setVisibility(View.VISIBLE);

            /*PatternAdapter pgAdapter = new PatternAdapter(getContext(), dbResult,
                    PatternFragment.this);
            patternView.refreshDrawableState();
            pgAdapter.notifyDataSetChanged();
            patternView.setAdapter(null);
            patternView.setAdapter(pgAdapter);*/

            dbPaginatedList.clear();
            dbPaginatedList.addAll(dbResult);
            adapter.notifyDataSetChanged();
        }
        else{
            view_errorText.setVisibility(View.VISIBLE);
            patternView.setVisibility(View.GONE);
        }

    }

    private void viewNewPattern(){
        Fragment fragment = new NewPatternFragment();
        if (fragment != null && !session.getFragmentId().toString().equals("tab4.1")) {

            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction ft=fragmentManager.beginTransaction();
            ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right,
                    R.anim.slide_out_left, R.anim.slide_in_left);
            ft.add(R.id.content_frame_id, fragment);
            ft.hide(PatternFragment.this);
            ft.addToBackStack(PatternFragment.class.getName());
            ft.commit();
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();

        new getPatterns(getContext()).execute();

        session = new SessionManager(getActivity());
        session.setFragmentId("tab4");
    }

}