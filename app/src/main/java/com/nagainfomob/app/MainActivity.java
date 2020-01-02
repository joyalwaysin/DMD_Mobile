package com.nagainfomob.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.nagainfomob.app.DisplayMyDesign.CamCrop;
import com.nagainfomob.app.DisplayMyDesign.Room3DActivity;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void sendMessage(View view)
    {
        Intent intent = new Intent(MainActivity.this, Room3DActivity.class);
        startActivity(intent);
    }

    public void camClick(View view)
    {
        Intent intent = new Intent(MainActivity.this, CamCrop.class);
        startActivity(intent);
    }

}
