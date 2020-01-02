package com.nagainfomob.app.model.ResendOTP;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by joy on 10/11/17.
 */

public class ResendOTPData {
    @SerializedName("country_code")
    @Expose
    private String countryCode;
    @SerializedName("mobile")
    @Expose
    private String mobile;

    /**
     * No args constructor for use in serialization
     *
     */
    public ResendOTPData() {
    }

    /**
     *
     * @param countryCode
     * @param mobile
     */
    public ResendOTPData(String countryCode, String mobile) {
        super();
        this.countryCode = countryCode;
        this.mobile = mobile;
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
}
