package com.nagainfomob.app.main;

/**
 * Created by joy on 13/09/17.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.view.Window;


import com.google.firebase.iid.FirebaseInstanceId;
import com.nagainfomob.app.R;


/**
 * Created by joy on 31/05/17.
 */

public class SplashActivity extends Activity {

    private String fcm_token;

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = getWindow();
        window.setFormat(PixelFormat.RGBA_8888);
    }

    Thread splashTread;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        fcm_token = FirebaseInstanceId.getInstance().getToken();
        StartAnimations();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    private void StartAnimations() {

        splashTread = new Thread() {
            @Override
            public void run() {
                try {
                    int waited = 0;
                    // Splash screen pause time
                    while (waited < 1500) {
                        sleep(200);
                        waited += 150;
                    }
                    Intent intent = new Intent(SplashActivity.this, LandingActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);

                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };
        splashTread.start();
    }

}