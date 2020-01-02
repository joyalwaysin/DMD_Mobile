package com.nagainfomob.app.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.nagainfomob.app.R;


/**
 * Created by root on 2/2/17.
 */

public class NewDesignFragment extends Fragment implements View.OnClickListener {

    private EditText ip1;
    private EditText ip2;
    private EditText ip3;
    private EditText ip4;

    private ImageView img_3dvis;
    private ImageView img_PhotoVis;

    private Button settingsButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_new_designs, container, false);

        initViews(view);
        initListeners();
        initObjects();

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
        img_3dvis = view.findViewById(R.id.Img_3dvis);
        img_PhotoVis = view.findViewById(R.id.Img_PhotoVis);
    }

    private void initObjects() {
        /*inputValidation = new InputValidation(getActivity());
        session = new SessionManager(getActivity());*/
    }

    private void initListeners() {
        img_3dvis.setOnClickListener(this);
        img_PhotoVis.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Img_3dvis:
                createProject(1);
                break;

            case R.id.Img_PhotoVis:
                createProject(2);
                break;
        }
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

}