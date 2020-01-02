package com.nagainfomob.app.model.ForgotPasswordOTP;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by joy on 21/04/18.
 */

public class ForgotPassOTPData {
    @SerializedName("mobile")
    @Expose
    private String mobile;

    /**
     * No args constructor for use in serialization
     *
     */
    public ForgotPassOTPData() {
    }

    /**
     *
     * @param mobile
     */
    public ForgotPassOTPData(String mobile) {
        super();
        this.mobile = mobile;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
