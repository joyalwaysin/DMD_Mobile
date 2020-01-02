package com.nagainfomob.app.model.OTP;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by joy on 09/11/17.
 */

public class OtpData {
    @SerializedName("country_code")
    @Expose
    private String countryCode;
    @SerializedName("mobile")
    @Expose
    private String mobile;
    @SerializedName("mobile_otp")
    @Expose
    private String mobileOtp;

    /**
     * No args constructor for use in serialization
     *
     */
    public OtpData() {
    }

    /**
     *
     * @param countryCode
     * @param mobileOtp
     * @param mobile
     */
    public OtpData(String countryCode, String mobile, String mobileOtp) {
        super();
        this.countryCode = countryCode;
        this.mobile = mobile;
        this.mobileOtp = mobileOtp;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
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
}
