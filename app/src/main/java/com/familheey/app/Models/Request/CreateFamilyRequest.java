package com.familheey.app.Models.Request;

import com.familheey.app.Utilities.SharedPref;
import com.google.gson.annotations.SerializedName;

public class CreateFamilyRequest {

    @SerializedName("group_category")
    private String groupCategory;

    @SerializedName("group_name")
    private String groupName;

    @SerializedName("created_by")
    private String createdBy = SharedPref.getUserRegistration().getId();

    @SerializedName("base_region")
    private String baseRegion;

    @SerializedName("is_active")
    private Boolean isActive;

    @SerializedName("lat")
    private Double lat;

    @SerializedName("long")
    private Double lng;

    public String getGroupCategory() {
        return groupCategory;
    }

    public void setGroupCategory(String groupCategory) {
        this.groupCategory = groupCategory;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getBaseRegion() {
        return baseRegion;
    }

    public void setBaseRegion(String baseRegion) {
        this.baseRegion = baseRegion;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    @Override
    public String toString() {
        return
                "CreateFamilyRequest{" +
                        "group_category = '" + groupCategory + '\'' +
                        ",group_name = '" + groupName + '\'' +
                        ",created_by = '" + createdBy + '\'' +
                        ",base_region = '" + baseRegion + '\'' +
                        ",lat = '" + lat + '\'' +
                        ",lng = '" + lng + '\'' +
                        "}";
    }
}