package com.nagainfomob.app.model.OTP;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by joy on 09/11/17.
 */

public class OtpResult {
    @SerializedName("access_token")
    @Expose
    private String accessToken;
    @SerializedName("resource_server")
    @Expose
    private String resourceServer;
    @SerializedName("error")
    @Expose
    private String error;
    @SerializedName("error_message")
    @Expose
    private String errorMessage;
    @SerializedName("user")
    @Expose
    private OtpUserResult user;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getResourceServer() {
        return resourceServer;
    }

    public void setResourceServer(String resourceServer) {
        this.resourceServer = resourceServer;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public OtpUserResult getUser() {
        return user;
    }

    public void setUser(OtpUserResult user) {
        this.user = user;
    }
}
