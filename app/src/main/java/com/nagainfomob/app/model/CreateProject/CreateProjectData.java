package com.nagainfomob.app.model.CreateProject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by joy on 15/11/17.
 */

public class CreateProjectData {
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("customer_name")
    @Expose
    private String customerName;
    @SerializedName("mobile")
    @Expose
    private String mobile;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("room_type")
    @Expose
    private String roomType;
    @SerializedName("width")
    @Expose
    private String width;
    @SerializedName("height")
    @Expose
    private String height;
    @SerializedName("depth")
    @Expose
    private String depth;
    @SerializedName("unit")
    @Expose
    private String unit;
    @SerializedName("ambience_id")
    @Expose
    private String ambienceId;
    @SerializedName("remarks ")
    @Expose
    private String remarks;
    @SerializedName("image_url")
    @Expose
    private String imageUrl;

    /**
     * No args constructor for use in serialization
     *
     */
    public CreateProjectData() {
    }

    /**
     *
     * @param customerName
     * @param imageUrl
     * @param width
     * @param remarks
     * @param type
     * @param ambienceId
     * @param depth
     * @param unit
     * @param height
     * @param email
     * @param roomType
     * @param name
     * @param mobile
     */
    public CreateProjectData(String name, String type, String customerName, String mobile, String email, String roomType,
                             String width, String height, String depth, String unit, String ambienceId,
                             String remarks, String imageUrl) {
        super();
        this.name = name;
        this.type = type;
        this.customerName = customerName;
        this.mobile = mobile;
        this.email = email;
        this.roomType = roomType;
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.unit = unit;
        this.ambienceId = ambienceId;
        this.remarks = remarks;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getDepth() {
        return depth;
    }

    public void setDepth(String depth) {
        this.depth = depth;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getAmbienceImageId() {
        return ambienceId;
    }

    public void setAmbienceImageId(String ambienceId) {
        this.ambienceId = ambienceId;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
