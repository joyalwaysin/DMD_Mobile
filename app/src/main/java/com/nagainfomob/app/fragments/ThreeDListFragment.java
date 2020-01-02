package com.nagainfomob.app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nagainfomob.app.DisplayMyDesign.GlobalVariables;
import com.nagainfomob.app.DisplayMyDesign.Room3DActivity;
import com.nagainfomob.app.R;
import com.nagainfomob.app.adapter.TDListAdapter;
import com.nagainfomob.app.api.ApiClient;
import com.nagainfomob.app.api.ApiInterface;
import com.nagainfomob.app.helpers.SessionManager;
import com.nagainfomob.app.model.MyDesignsModel;
import com.nagainfomob.app.model.ProjectsModel;
import com.nagainfomob.app.model.ResponseModel;
import com.nagainfomob.app.model.SettingsModel;
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
 * Created by joy on 01/05/18.
 */

public class ThreeDListFragment extends Fragment implements View.OnClickListener {
    private TextView defaultViewPV;
    private TextView defaultViewTD;
    private TextView title;

    private SessionManager session;
    private String access_token;
    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_3d_list, container, false);

        initViews(view);
        initListeners();
        initObjects();

        session = new SessionManager(getActivity());
        getDatafromdb();

        load3DProjects();

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
        defaultViewTD = view.findViewById(R.id.defaultViewTD);
        title = view.findViewById(R.id.title);
    }

    private void initObjects() {
        /*inputValidation = new InputValidation(getActivity());
        session = new SessionManager(getActivity());*/
    }

    private void initListeners() {
    }

    @Override
    public void onClick(View v) {
        /*switch (v.getId()) {
            case R.id.td_view_all:
                vieAllThreeD();
                break;
        }*/
    }

    private void getDatafromdb(){
        List<SettingsModel> settngs;
        settngs = DatabaseManager.getSettings(getActivity());
        access_token = "Bearer "+settngs.get(0).getAccessToken();
    }

    public void load3DProjects(){

        defaultViewTD.setVisibility(View.GONE);

        List<ProjectsModel> projects;
        projects = DatabaseManager.getAllProjects(getContext(), session.getUserID());

        if(projects.size() > 0){
            ArrayList<MyDesignsModel> TdItem = new ArrayList<>();

            title.setText("3D Visualizer ("+projects.size()+")");

            for (int i = 0; i < projects.size(); i++) {
                MyDesignsModel model        =   new MyDesignsModel();
                model.ItemTitle = projects.get(i).getProject_name();
                model.projectId = projects.get(i).getProject_id();
                model.ItemDateTime = projects.get(i).getTime_stamp();
                model.unit = projects.get(i).getUnit_id();
                model.width = projects.get(i).getWidth();
                model.height = projects.get(i).getHeight();
                model.depth = projects.get(i).getDepth();
                model.cust_name = projects.get(i).getCust_name();
                model.cust_mob = projects.get(i).getCust_mob();
                TdItem.add(model);
            }

            RecyclerView recyclerView = view.findViewById(R.id.threeD_item);
            TDListAdapter recyclerAdapter = new TDListAdapter(getContext(), TdItem, ThreeDListFragment.this);
            recyclerView.setHasFixedSize(false);

            LinearLayoutManager manager_brand = new LinearLayoutManager(
                    getActivity(),
                    LinearLayoutManager.VERTICAL,
                    false
            );
            recyclerView.setLayoutManager(manager_brand);
            recyclerView.setAdapter(recyclerAdapter);
        }
        else{
            title.setText("3D Visualizer");
            defaultViewTD.setVisibility(View.VISIBLE);
            defaultViewTD.setText("No Projects Found!");
        }
    }

    public void view3D(String projectName, String unit, String width, String height,
                       String depth, String projectId, String projectCreated, String custName, String custMob) {

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
    }


    public void deleteProject(final String projectId){

        new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Are you sure?")
                .setContentText("Do you wish to delete this project?")
                .setCancelText("Cancel")
                .setConfirmText("Delete")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                        deactivateProject(projectId);
                    }
                })
                .show();
    }

    public void deactivateProject(final String projectId){

        try{
            final SweetAlertDialog pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE)
                    .setTitleText("Processing");
            pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.colorAccent));
            pDialog.show();
            pDialog.setCancelable(false);

            ApiInterface apiService =
                    ApiClient.getClient().create(ApiInterface.class);
            Call<ResponseModel> call = apiService.deactivateProject(projectId, access_token);

            call.enqueue(new Callback<ResponseModel>() {
                @Override
                public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                    int statusCode = response.code();
                    if (response.isSuccessful()) {
                        if(statusCode == 200) {
                            session.setBackStackId("1");
                            DatabaseManager.deleteProject(getActivity(), projectId);
                            load3DProjects();
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
                public void onFailure(Call<ResponseModel> call, Throwable throwable) {

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

                    if(errot_text == null || errot_text.equals("")){
                        errot_text = "Something went wrong;  Please try after some time.";
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

    @Override
    public void onResume() {
        super.onResume();

        /*String backstackFlag = "";
        backstackFlag = session.getBackStackId();
        if(backstackFlag.equals("2")){

            FragmentManager fm = getFragmentManager();
            if (fm.getBackStackEntryCount() > 0) {
                fm.popBackStack();
            }
        }*/
    }
}
