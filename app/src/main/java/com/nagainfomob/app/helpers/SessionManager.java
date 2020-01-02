package com.nagainfomob.app.helpers;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by root on 2/2/17.
 */

public class SessionManager {
    // Shared preferences file name
    private static final String PREF_NAME = "DMDPref";
    private static final String KEY_IS_LOGGEDIN = "isLoggedIn";
    private static final String KEY_USER_ID = "usrID";
    private static final String KEY_USER_MOB = "usrMobile";
    private static final String KEY_ACCESS_TOKEN = "sessionFlag";
    private static final String KEY_FRAGMENT = "fragmentId";
    private static final String KEY_BACKSTACK = "backstackId";
    private static final String KEY_PROCESSING = "backstackId";
    // Shared Preferences
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    // Shared pref mode
    int PRIVATE_MODE = 0;

    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setLogin(boolean isLoggedIn) {

        editor.putBoolean(KEY_IS_LOGGEDIN, isLoggedIn);
        editor.commit();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGEDIN, false);
    }

    public void setUserID(String user_id) {

        editor.putString(KEY_USER_ID, user_id);
        editor.commit();
    }

    public String getUserID(){
        String code = pref.getString(KEY_USER_ID, null);
        return code;
    }

    public void setKeyUserMob(String userMob) {

        editor.putString(KEY_USER_MOB, userMob);
        editor.commit();
    }

    public String getKeyUserMob(){
        String code = pref.getString(KEY_USER_MOB, null);
        return code;
    }

    public void setAccessToken(String access_token) {

        editor.putString(KEY_ACCESS_TOKEN, access_token);
        editor.commit();
    }

    public String getAccessToken() {
        String token = pref.getString(KEY_ACCESS_TOKEN, null);
        return token;
    }

    public void setFragmentId(String fragmentId) {

        editor.putString(KEY_FRAGMENT, fragmentId);
        editor.commit();
    }

    public String getFragmentId() {
        String fragId = pref.getString(KEY_FRAGMENT, null);
        return fragId;
    }

    public void setBackStackId(String backStackId) {

        editor.putString(KEY_BACKSTACK, backStackId);
        editor.commit();
    }

    public String getBackStackId() {
        String backstackId = pref.getString(KEY_BACKSTACK, null);
        return backstackId;
    }

    public void setProcessingFlag(String process) {

        editor.putString(KEY_PROCESSING, process);
        editor.commit();
    }

    public String getProcessingFlag() {
        String process = pref.getString(KEY_PROCESSING, "0");
        return process;
    }

}