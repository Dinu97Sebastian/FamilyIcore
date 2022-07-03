package com.familheey.app.Models.Response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MemberRequests {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("from_group")
    @Expose
    private Integer fromGroup;
    @SerializedName("to_group")
    @Expose
    private Integer toGroup;
    @SerializedName("requested_by")
    @Expose
    private Integer requestedBy;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("createdAt")
    @Expose
    private String createdAt;
    @SerializedName("updatedAt")
    @Expose
    private String updatedAt;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("full_name")
    @Expose
    private String fullName;
    @SerializedName("propic")
    @Expose
    private String proPic;
    @SerializedName("base_region")
    @Expose
    private String baseRegion;
    @SerializedName("group_name")
    @Expose
    private String groupName;
    @SerializedName("group_id")
    @Expose
    private Integer groupId;

    public String getLocation() {
        return location;
    }

    @SerializedName("location")
    @Expose
    private String location;
    @SerializedName("user_id")
    private String userId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getFromGroup() {
        return fromGroup;
    }

    public void setFromGroup(Integer fromGroup) {
        this.fromGroup = fromGroup;
    }

    public Integer getToGroup() {
        return toGroup;
    }

    public void setToGroup(Integer toGroup) {
        this.toGroup = toGroup;
    }

    public Integer getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(Integer requestedBy) {
        this.requestedBy = requestedBy;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getProPic() {
        return proPic;
    }

    public String getBaseRegion() {
        return baseRegion;
    }

    public String getGroupName() {
        return groupName;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public String getUserId(){
        return userId;
    }
}
