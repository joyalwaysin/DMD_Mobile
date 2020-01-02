package com.nagainfomob.app.model.LoadProject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by joy on 17/11/17.
 */

public class LoadProjectResult {


    @SerializedName("data")
    @Expose
    private List<Datum> data = null;

    /**
     * No args constructor for use in serialization
     *
     */
    public LoadProjectResult() {
    }

    /**
     *
     * @param data
     */
    public LoadProjectResult(List<Datum> data) {
        super();
        this.data = data;
    }

    public List<Datum> getData() {
        return data;
    }

    public void setData(List<Datum> data) {
        this.data = data;
    }


    public class Datum {

        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("user_id")
        @Expose
        private String userId;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("type")
        @Expose
        private String type;
        @SerializedName("share_id")
        @Expose
        private String shareId;
        @SerializedName("customer_name")
        @Expose
        private String customerName;
        @SerializedName("mobile")
        @Expose
        private String mobile;
        @SerializedName("remarks")
        @Expose
        private String remarks;
        @SerializedName("is_favourite")
        @Expose
        private String isFavourite;
        @SerializedName("email")
        @Expose
        private String email;
        @SerializedName("image_url")
        @Expose
        private String imageUrl;
        @SerializedName("created_at")
        @Expose
        private String createdAt;
        @SerializedName("updated_at")
        @Expose
        private String updatedAt;
        @SerializedName("ambience")
        @Expose
        private Ambience ambience;
        @SerializedName("rooms")
        @Expose
        private Rooms rooms;

        /**
         * No args constructor for use in serialization
         *
         */
        public Datum() {
        }

        /**
         *
         * @param customerName
         * @param ambience
         * @param imageUrl
         * @param remarks
         * @param type
         * @param updatedAt
         * @param id
         * @param shareId
         * @param email
         * @param createdAt
         * @param userId
         * @param name
         * @param isFavourite
         * @param rooms
         * @param mobile
         */
        public Datum(String id, String userId, String name, String type, String shareId, String customerName, String mobile, String remarks, String isFavourite, String email, String imageUrl, String createdAt, String updatedAt, Ambience ambience, Rooms rooms) {
            super();
            this.id = id;
            this.userId = userId;
            this.name = name;
            this.type = type;
            this.shareId = shareId;
            this.customerName = customerName;
            this.mobile = mobile;
            this.remarks = remarks;
            this.isFavourite = isFavourite;
            this.email = email;
            this.imageUrl = imageUrl;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
            this.ambience = ambience;
            this.rooms = rooms;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
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

        public String getShareId() {
            return shareId;
        }

        public void setShareId(String shareId) {
            this.shareId = shareId;
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

        public String getRemarks() {
            return remarks;
        }

        public void setRemarks(String remarks) {
            this.remarks = remarks;
        }

        public String getIsFavourite() {
            return isFavourite;
        }

        public void setIsFavourite(String isFavourite) {
            this.isFavourite = isFavourite;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }

        public Ambience getAmbience() {
            return ambience;
        }

        public void setAmbience(Ambience ambience) {
            this.ambience = ambience;
        }

        public Rooms getRooms() {
            return rooms;
        }

        public void setRooms(Rooms rooms) {
            this.rooms = rooms;
        }

    }


    public class Rooms {

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
        @SerializedName("room_type")
        @Expose
        private String roomType;

        /**
         * No args constructor for use in serialization
         *
         */
        public Rooms() {
        }

        /**
         *
         * @param unit
         * @param height
         * @param roomType
         * @param width
         * @param depth
         */
        public Rooms(String width, String height, String depth, String unit, String roomType) {
            super();
            this.width = width;
            this.height = height;
            this.depth = depth;
            this.unit = unit;
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

        public String getRoomType() {
            return roomType;
        }

        public void setRoomType(String roomType) {
            this.roomType = roomType;
        }

    }



    public class Ambience {

        @SerializedName("ambience_id")
        @Expose
        private String ambienceId;

        /**
         * No args constructor for use in serialization
         *
         */
        public Ambience() {
        }

        /**
         *
         * @param ambienceId
         */
        public Ambience(String ambienceId) {
            super();
            this.ambienceId = ambienceId;
        }

        public String getAmbienceId() {
            return ambienceId;
        }

        public void setAmbienceId(String ambienceId) {
            this.ambienceId = ambienceId;
        }

    }

}
