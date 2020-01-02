package com.nagainfomob.app.model.LoadTile;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by joy on 16/01/18.
 */

public class Datum {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("image_url")
    @Expose
    private String imageUrl;
    @SerializedName("brand_id")
    @Expose
    private String brandId;
    @SerializedName("brand_name")
    @Expose
    private String brandName;
    @SerializedName("category_name")
    @Expose
    private String categoryName;
    @SerializedName("category")
    @Expose
    private String category;
    @SerializedName("tile_type")
    @Expose
    private String tileType;
    @SerializedName("tile_type_name")
    @Expose
    private String tileTypeName;
    @SerializedName("model_no")
    @Expose
    private String modelNo;
    @SerializedName("unit")
    @Expose
    private String unit;
    @SerializedName("unit_name")
    @Expose
    private String unitName;
    @SerializedName("width")
    @Expose
    private String width;
    @SerializedName("height")
    @Expose
    private String height;
    @SerializedName("color")
    @Expose
    private String color;
    @SerializedName("color_code")
    @Expose
    private String colorCode;
    @SerializedName("color_name")
    @Expose
    private String colorName;
    @SerializedName("price")
    @Expose
    private String price;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("is_active")
    @Expose
    private Boolean isActive;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;

    /**
     * No args constructor for use in serialization
     *
     */
    public Datum() {
    }

    /**
     *
     * @param colorName
     * @param status
     * @param imageUrl
     * @param width
     * @param brandId
     * @param brandName
     * @param tileTypeName
     * @param updatedAt
     * @param isActive
     * @param id
     * @param categoryName
     * @param tileType
     * @param modelNo
     * @param unit
     * @param category
     * @param price
     * @param height
     * @param color
     * @param createdAt
     * @param unitName
     * @param description
     * @param name
     * @param colorCode
     */
    public Datum(String id, String name, String description, String imageUrl, String brandId, String brandName, String categoryName, String category, String tileType, String tileTypeName, String modelNo, String unit, String unitName, String width, String height, String color, String colorCode, String colorName, String price, String status, Boolean isActive, String createdAt, String updatedAt) {
        super();
        this.id = id;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.brandId = brandId;
        this.brandName = brandName;
        this.categoryName = categoryName;
        this.category = category;
        this.tileType = tileType;
        this.tileTypeName = tileTypeName;
        this.modelNo = modelNo;
        this.unit = unit;
        this.unitName = unitName;
        this.width = width;
        this.height = height;
        this.color = color;
        this.colorCode = colorCode;
        this.colorName = colorName;
        this.price = price;
        this.status = status;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getBrandId() {
        return brandId;
    }

    public void setBrandId(String brandId) {
        this.brandId = brandId;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTileType() {
        return tileType;
    }

    public void setTileType(String tileType) {
        this.tileType = tileType;
    }

    public String getTileTypeName() {
        return tileTypeName;
    }

    public void setTileTypeName(String tileTypeName) {
        this.tileTypeName = tileTypeName;
    }

    public String getModelNo() {
        return modelNo;
    }

    public void setModelNo(String modelNo) {
        this.modelNo = modelNo;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
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

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    public String getColorName() {
        return colorName;
    }

    public void setColorName(String colorName) {
        this.colorName = colorName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
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

}