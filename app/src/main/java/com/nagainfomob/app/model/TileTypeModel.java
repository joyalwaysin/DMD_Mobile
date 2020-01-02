package com.nagainfomob.app.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by joy on 01/02/18.
 */

public class TileTypeModel {
    @SerializedName("data")
    @Expose
    private List<TypeData> data = null;

    public List<TypeData> getData() {
        return data;
    }

    public void setData(List<TypeData> data) {
        this.data = data;
    }

    public class TypeData {

        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("name")
        @Expose
        private String name;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

    }
}
