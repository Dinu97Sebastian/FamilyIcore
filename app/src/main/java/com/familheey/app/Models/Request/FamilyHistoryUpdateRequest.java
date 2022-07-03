package com.familheey.app.Models.Request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class FamilyHistoryUpdateRequest {

    @SerializedName("user_id")
    @Expose
    private String user_id;

    @SerializedName("history_text")
    @Expose
    private String history_text;

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("history_images")
    @Expose
    private ArrayList<HistoryImages> history_images;

    public String getHistory_text() {
        return history_text;
    }

    public void setHistory_text(String history_text) {
        this.history_text = history_text;
    }

    public ArrayList<HistoryImages> getHistory_images() {
        return history_images;
    }

    public void setHistory_images(ArrayList<HistoryImages> history_images) {
        this.history_images = history_images;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
