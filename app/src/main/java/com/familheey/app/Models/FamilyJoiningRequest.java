package com.familheey.app.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FamilyJoiningRequest {
    @SerializedName("req_id")
    @Expose
    private Integer reqId;
    @SerializedName("group_id")
    @Expose
    private Integer groupId;
    @SerializedName("base_region")
    @Expose
    private String baseRegion;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("group_name")
    @Expose
    private String groupName;
    @SerializedName("logo")
    @Expose
    private String logo;
    @SerializedName("full_name")
    @Expose
    private String fullName;
    @SerializedName("propic")
    @Expose
    private String propic;
    @SerializedName("from_id")
    @Expose
    private String fromId;

    public Integer getReqId() {
        return reqId;
    }

    public void setReqId(Integer reqId) {
        this.reqId = reqId;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public String getBaseRegion() {
        return baseRegion;
    }

    public void setBaseRegion(String baseRegion) {
        this.baseRegion = baseRegion;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPropic() {
        return propic;
    }

    public void setPropic(String propic) {
        this.propic = propic;
    }

    public String getFromId() {
        return fromId;
    }
}
