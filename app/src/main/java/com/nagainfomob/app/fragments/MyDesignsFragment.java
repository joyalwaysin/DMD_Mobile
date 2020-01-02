package com.nagainfomob.app.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nagainfomob.app.DisplayMyDesign.AmbienceBathroom01;
import com.nagainfomob.app.DisplayMyDesign.AmbienceBedroom;
import com.nagainfomob.app.DisplayMyDesign.AmbienceExterior01;
import com.nagainfomob.app.DisplayMyDesign.AmbienceKitchen01;
import com.nagainfomob.app.DisplayMyDesign.AmbienceLivingRoom01;
import com.nagainfomob.app.DisplayMyDesign.AmbienceLivingRoom02;
import com.nagainfomob.app.DisplayMyDesign.GlobalVariables;
import com.nagainfomob.app.DisplayMyDesign.Room3DActivity;
import com.nagainfomob.app.R;
import com.nagainfomob.app.adapter.PhotoVisualizerAdapter;
import com.nagainfomob.app.adapter.TDVisualizerAdapter;
import com.nagainfomob.app.api.ApiClient;
import com.nagainfomob.app.api.ApiInterface;
import com.nagainfomob.app.helpers.SessionManager;
import com.nagainfomob.app.main.DashboardActivity;
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
 * Created by root on 2/2/17.
 */

public class MyDesignsFragment extends Fragment implements View.OnClickListener {

    private TextView defaultViewPV;
    private TextView defaultViewTD;
    private TextView td_view_all;
    private TextView td_title;

    private SessionManager session;
    private String access_token;

    private Context context;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        context = getContext();
        final View view = inflater.inflate(R.layout.fragment_my_designs, container, false);

        initViews(view);
        initListeners();
        initObjects();

        session = new SessionManager(getActivity());
        getDatafromdb();

        load3DProjects(view);
        load2DProjects(view);

        /*getActivity().getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener()
        {
            public void onBackStackChanged()
            {
                String backstackFlag = "";
                backstackFlag = session.getBackStackId();
                if(backstackFlag.equals("1")){
                    session.setBackStackId("0");
                    ((DashboardActivity) context).displaySelectedScreen(R.id.ib1);
                }
            }
        });*/

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
        defaultViewPV = view.findViewById(R.id.defaultViewPV);
        defaultViewTD = view.findViewById(R.id.defaultViewTD);
        td_view_all = view.findViewById(R.id.td_view_all);
        td_title = view.findViewById(R.id.td_title);
    }

    private void initObjects() {
        /*inputValidation = new InputValidation(getActivity());
        session = new SessionManager(getActivity());*/
    }

    private void initListeners() {
        td_view_all.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.td_view_all:
                vieAllThreeD();
                break;
        }
    }

    private void getDatafromdb(){
        List<SettingsModel> settngs;
        settngs = DatabaseManager.getSettings(getActivity());
        access_token = "Bearer "+settngs.get(0).getAccessToken();
    }

    public void load3DProjects(final View view){

        defaultViewPV.setVisibility(View.GONE);
        defaultViewTD.setVisibility(View.GONE);

        List<ProjectsModel> projects;
        List<ProjectsModel> projectsAll;
        projects = DatabaseManager.getProjects(getContext(), session.getUserID());
        projectsAll = DatabaseManager.getAllProjects(getContext(), session.getUserID());


        if(projects.size() > 0){

            td_title.setText("3D Visualizer ("+projects.size()+"/"+projectsAll.size() + ")");

            ArrayList<MyDesignsModel> TdItem = new ArrayList<>();
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
            TDVisualizerAdapter recyclerAdapter = new TDVisualizerAdapter(getContext(), TdItem, MyDesignsFragment.this);
            recyclerView.setHasFixedSize(false);

            LinearLayoutManager manager_brand = new LinearLayoutManager(
                    getActivity(),
                    LinearLayoutManager.HORIZONTAL,
                    false
            );
            recyclerView.setLayoutManager(manager_brand);
            recyclerView.setAdapter(recyclerAdapter);
        }
        else{
            td_title.setText("3D Visualizer");
            defaultViewTD.setVisibility(View.VISIBLE);
            defaultViewTD.setText("No Projects Found!");
        }
    }

    public void load2DProjects(final View view){

        ArrayList<MyDesignsModel> PhotoItem = new ArrayList<>();

        MyDesignsModel model        =   new MyDesignsModel();
        model.ItemTitle = "LivingRoom 1";
        PhotoItem.add(model);

        MyDesignsModel model2        =   new MyDesignsModel();
        model2.ItemTitle = "LivingRoom 2";
        PhotoItem.add(model2);

        MyDesignsModel model3        =   new MyDesignsModel();
        model3.ItemTitle = "BedRoom 1";
        PhotoItem.add(model3);

        MyDesignsModel model4        =   new MyDesignsModel();
        model4.ItemTitle = "BathRoom 1";
        PhotoItem.add(model4);

        MyDesignsModel model5        =   new MyDesignsModel();
        model5.ItemTitle = "Exterior 1";
        PhotoItem.add(model5);

        MyDesignsModel model6        =   new MyDesignsModel();
        model6.ItemTitle = "Kitchen 1";
        PhotoItem.add(model6);

        RecyclerView recyclerView1 = view.findViewById(R.id.photo_item);
        PhotoVisualizerAdapter recyclerAdapter1 = new PhotoVisualizerAdapter(getContext(), PhotoItem, MyDesignsFragment.this);
        recyclerView1.setHasFixedSize(false);

        LinearLayoutManager manager_brand1 = new LinearLayoutManager(
                getActivity(),
                LinearLayoutManager.HORIZONTAL,
                false
        );
        recyclerView1.setLayoutManager(manager_brand1);
        recyclerView1.setAdapter(recyclerAdapter1);
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

    public void view2D(int position) {
        Intent intent = new Intent(getActivity(), AmbienceLivingRoom01.class);
        switch (position){
            case 0:
                intent = new Intent(getActivity(), AmbienceLivingRoom01.class);
                break;
            case 1:
                intent = new Intent(getActivity(), AmbienceLivingRoom02.class);
                break;
            case 2:
                intent = new Intent(getActivity(), AmbienceBedroom.class);
                break;
            case 3:
                intent = new Intent(getActivity(), AmbienceBathroom01.class);
                break;
            case 4:
                intent = new Intent(getActivity(), AmbienceExterior01.class);
                break;
            case 5:
                intent = new Intent(getActivity(), AmbienceKitchen01.class);
                break;

        }
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
                            DatabaseManager.deleteProject(getActivity(), projectId);
                            ((DashboardActivity) getActivity()).displaySelectedScreen(R.id.ib1);
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

    public void vieAllThreeD(){

        Fragment fragment = new ThreeDListFragment();
        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction ft=fragmentManager.beginTransaction();
            ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right,
                    R.anim.slide_out_left, R.anim.slide_in_left);
            ft.add(R.id.content_frame_id, fragment);
            ft.hide(MyDesignsFragment.this);
            ft.addToBackStack(PatternFragment.class.getName());
            ft.commit();
        }

    }

}