package com.familheey.app.Models.Response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MemberAdded {
    @SerializedName("createdAt")
    @Expose
    public String createdAt;
    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("user_id")
    @Expose
    public Integer userId;
    @SerializedName("from_id")
    @Expose
    public Integer fromId;
    @SerializedName("group_id")
    @Expose
    public Integer groupId;
    @SerializedName("type")
    @Expose
    public String type;
    @SerializedName("status")
    @Expose
    public String status;
    @SerializedName("responded_by")
    @Expose
    public String respondedBy;
    @SerializedName("email")
    @Expose
    public String email;
    @SerializedName("phone")
    @Expose
    public String phone;

    public MemberAdded() {
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public Integer getId() {
        return id;
    }

    public Integer getUserId() {
        return userId;
    }

    public Integer getFromId() {
        return fromId;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public String getType() {
        return type;
    }

    public String getStatus() {
        return status;
    }

    public String getRespondedBy() {
        return respondedBy;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }
}
