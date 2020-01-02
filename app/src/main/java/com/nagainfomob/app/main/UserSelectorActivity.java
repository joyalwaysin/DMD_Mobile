package com.nagainfomob.app.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.google.firebase.iid.FirebaseInstanceId;
import com.nagainfomob.app.R;

public class UserSelectorActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout layout_manufacturer;
    private LinearLayout layout_dealer;
    private String fcm_token="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_user_selector);
        fcm_token = FirebaseInstanceId.getInstance().getToken();

        initViews();
        initListeners();
    }

    private void initViews() {
        layout_manufacturer = (LinearLayout) findViewById(R.id.LayoutManufacturer);
        layout_dealer = (LinearLayout) findViewById(R.id.LayoutDealer);
    }

    private void initListeners() {
        layout_manufacturer.setOnClickListener(this);
        layout_dealer.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.LayoutManufacturer:
                viewRegisterAvtivity("2");
                break;
            case R.id.LayoutDealer:
                viewRegisterAvtivity("3");
                break;
        }
    }

    public void viewRegisterAvtivity(String type){
        Intent intent = new Intent(UserSelectorActivity.this, RegisterActivity.class);
        intent.putExtra("UserType",type);
        startActivity(intent);
    }
}