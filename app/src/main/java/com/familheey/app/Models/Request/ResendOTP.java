package com.familheey.app.Models.Request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResendOTP {
    @SerializedName("phone")
    @Expose
    private String phone;

    @SerializedName("reverify")
    @Expose
    private String reverify;
    @SerializedName("device_unique_id")
    @Expose
    private String device_unique_id ;

    public String getDevice_unique_id() {
        return device_unique_id;
    }

    public void setDevice_unique_id(String device_unique_id) {
        this.device_unique_id = device_unique_id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getReverify() {
        return reverify;
    }
    public void setReverify(String reverify) {
        this.reverify = reverify;
    }
}
