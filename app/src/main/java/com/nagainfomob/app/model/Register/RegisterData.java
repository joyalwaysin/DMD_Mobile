package com.nagainfomob.app.model.Register;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by joy on 08/11/17.
 */

public class RegisterData {
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("password")
    @Expose
    private String password;
    @SerializedName("country_code")
    @Expose
    private String countryCode;
    @SerializedName("mobile")
    @Expose
    private String mobile;
    @SerializedName("user_type")
    @Expose
    private String userType;

    /**
     * No args constructor for use in serialization
     *
     */
    public RegisterData() {
    }

    /**
     *
     * @param name
     * @param countryCode
     * @param password
     * @param userType
     * @param mobile
     */
    public RegisterData(String name, String password, String countryCode, String mobile, String userType) {
        super();
        this.name = name;
        this.password = password;
        this.countryCode = countryCode;
        this.mobile = mobile;
        this.userType = userType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

}
