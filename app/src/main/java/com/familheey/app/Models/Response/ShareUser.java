package com.familheey.app.Models.Response;

import com.google.gson.annotations.SerializedName;

public class ShareUser {
   private String propic;
    private String location;
    private String id;
    private String to_group_name;
    private String to_user_name;
    private String created_date;


    @SerializedName(value = "shared_user_name", alternate = {"full_name"})
    private String shared_user_name;
    @SerializedName("user_id")
    private String userId;


    public String getPropic() {
        return propic;
    }

    public void setPropic(String propic) {
        this.propic = propic;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFull_name() {
        return shared_user_name;
    }

    public void setFull_name(String full_name) {
        this.shared_user_name = full_name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTo_group_name() {
        return to_group_name;
    }

    public void setTo_group_name(String to_group_name) {
        this.to_group_name = to_group_name;
    }

    public String getTo_user_name() {
        return to_user_name;
    }

    public void setTo_user_name(String to_user_name) {
        this.to_user_name = to_user_name;
    }


    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }
}
