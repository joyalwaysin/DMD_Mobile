package com.nagainfomob.app.model.ForgotPassword;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by joy on 21/04/18.
 */

public class ForgotPassData {

    @SerializedName("mobile")
    @Expose
    private String mobile;
    @SerializedName("mobile_otp")
    @Expose
    private String mobileOtp;
    @SerializedName("new_password")
    @Expose
    private String newPassword;
    @SerializedName("confirm_password")
    @Expose
    private String confirmPassword;

    /**
     * No args constructor for use in serialization
     *
     */
    public ForgotPassData() {
    }

    /**
     *
     * @param confirmPassword
     * @param newPassword
     * @param mobileOtp
     * @param mobile
     */
    public ForgotPassData(String mobile, String mobileOtp, String newPassword, String confirmPassword) {
        super();
        this.mobile = mobile;
        this.mobileOtp = mobileOtp;
        this.newPassword = newPassword;
        this.confirmPassword = confirmPassword;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getMobileOtp() {
        return mobileOtp;
    }

    public void setMobileOtp(String mobileOtp) {
        this.mobileOtp = mobileOtp;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
