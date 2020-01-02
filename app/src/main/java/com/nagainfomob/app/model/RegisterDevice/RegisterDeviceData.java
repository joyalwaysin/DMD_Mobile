package com.nagainfomob.app.model.RegisterDevice;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by joy on 13/04/18.
 */

public class RegisterDeviceData {
    @SerializedName("device_id")
    @Expose
    public String deviceId;
    @SerializedName("device_token")
    @Expose
    public String deviceToken;

    /**
     * No args constructor for use in serialization
     *
     */
    public RegisterDeviceData() {
    }

    /**
     *
     * @param deviceToken
     * @param deviceId
     */
    public RegisterDeviceData(String deviceId, String deviceToken) {
        super();
        this.deviceId = deviceId;
        this.deviceToken = deviceToken;
    }
}
