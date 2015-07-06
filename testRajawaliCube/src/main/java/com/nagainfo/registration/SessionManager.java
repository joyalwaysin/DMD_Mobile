package com.nagainfo.registration;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.nagainfo.smartShowroom.GlobalVariables;

public class SessionManager {
    SharedPreferences pref;
    Editor editor;
    Context context;
    private static final String IS_ACTIVATED = "App_Activated";
    private static final String PREF_NAME = "AndroidHivePref";
    private static final int PRIVATE_MODE = 0;
    private static final String AMBIENCE_IS_ACTIVATED = "Ambience_Activated";
    private static final String THREE_D_IS_ACTIVATED = "3D_Activated";
    private static final String CAM_CLICK_IS_ACTIVATED = "CamClick_Activated";
    private static final String HELP_IS_ACTIVATED = "Help_Activated";
    private static final String DAY = "DAY";

    // String string;
    // int a;

    public SessionManager(Context context) {
        // this.string=string;
        // this.a=a;
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public Boolean isActivated(String date) {
        String tchk = pref.getString(DAY, "00-0000");
        System.out.println(tchk + " : " + date);
        if (tchk.equals("00-0000")) {
            editor.putBoolean(IS_ACTIVATED, false);

            editor.commit();
        }
        String[] prevDate = tchk.split("-");
        String[] currDate = date.split("-");
        int regDay, currDay, regYear, currYear;
        regDay = Integer.parseInt(prevDate[0]);
        currDay = Integer.parseInt(currDate[0]);
        regYear = Integer.parseInt(prevDate[1]);
        currYear = Integer.parseInt(currDate[1]);
        if (currYear > regYear) {
            editor.putBoolean(IS_ACTIVATED, false);
            editor.commit();
        } else if (currYear == regYear) {
            if (currDay > regDay + 6) {
                editor.putBoolean(IS_ACTIVATED, false);
                editor.commit();
            }
        }
        Boolean status = this.checkStatus();
        return status;
    }

    public Boolean isAmbienceActivated() {

        return pref.getBoolean(AMBIENCE_IS_ACTIVATED, true);

    }

    public Boolean is3DActivated() {

        return pref.getBoolean(THREE_D_IS_ACTIVATED, true);
    }

    public Boolean isCamClickActivated() {

        return pref.getBoolean(CAM_CLICK_IS_ACTIVATED, true);
    }

    public Boolean isHelpActivated() {

        return pref.getBoolean(HELP_IS_ACTIVATED, true);
    }

    private Boolean checkStatus() {
        // TODO Auto-generated method stub

        return pref.getBoolean(IS_ACTIVATED, false);

    }

    public void createActivateSession(Boolean status, String date) {
        editor.putBoolean(IS_ACTIVATED, status);
        if (status == true) {
            editor.putString(DAY, date);
            System.out.println(date);
        }
        editor.putBoolean(AMBIENCE_IS_ACTIVATED, GlobalVariables.getAmbienceActivationStatus());
        editor.putBoolean(THREE_D_IS_ACTIVATED, GlobalVariables.get3DActivationStatus());
        editor.putBoolean(CAM_CLICK_IS_ACTIVATED, GlobalVariables.getCamClickActivationStatus());
        editor.putBoolean(HELP_IS_ACTIVATED, GlobalVariables.getHelpActivationStatus());
        editor.commit();
    }

}
