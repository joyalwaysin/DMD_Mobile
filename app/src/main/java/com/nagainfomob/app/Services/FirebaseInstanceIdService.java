package com.nagainfomob.app.Services;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;

/**
 * Created by joy on 12/04/18.
 */

public class FirebaseInstanceIdService extends com.google.firebase.iid.FirebaseInstanceIdService {

    private static final String TAG = "MyAndroidFCMIIDService";

    @Override
    public void onTokenRefresh() {
        //Get hold of the registration token
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        //Log the token
        Log.d("FCM", "Refreshed token: " + refreshedToken);

    }
    private void sendRegistrationToServer(String token) {
        //Implement this method if you want to store the token on your server
    }
}
