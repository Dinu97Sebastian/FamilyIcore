package com.familheey.app.Models.Request;

import com.familheey.app.Models.Response.SelectFamilys;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class PostShareRequest {


    @SerializedName("post_id")
    @Expose
    private String post_id;

    @SerializedName("shared_user_id")
    @Expose
    private String shared_user_id;

    @SerializedName("to_group_id")
    @Expose
    private ArrayList<SelectFamilys> to_group_id;

    @SerializedName("to_user_id")
    @Expose
    private ArrayList<String> to_user_id;


    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public String getShared_user_id() {
        return shared_user_id;
    }

    public void setShared_user_id(String shared_user_id) {
        this.shared_user_id = shared_user_id;
    }

    public ArrayList<SelectFamilys> getTo_group_id() {
        return to_group_id;
    }

    public void setTo_group_id(ArrayList<SelectFamilys> to_group_id) {
        this.to_group_id = to_group_id;
    }

    public ArrayList<String> getTo_user_id() {
        return to_user_id;
    }

    public void setTo_user_id(ArrayList<String> to_user_id) {
        this.to_user_id = to_user_id;
    }
}
