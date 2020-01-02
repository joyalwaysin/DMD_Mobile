package com.nagainfomob.app.Services;

import android.os.Bundle;


/**
 * Created by joy on 13/04/18.
 */

public class GcmListenerService extends com.google.android.gms.gcm.GcmListenerService {

    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");
    }
}
