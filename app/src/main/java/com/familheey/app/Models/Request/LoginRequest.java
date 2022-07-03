package com.familheey.app.Models.Request;

import com.familheey.app.BuildConfig;
import com.familheey.app.Utilities.Utilities;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LoginRequest {

    @SerializedName("country")
    @Expose
    private String countryCode;
    @SerializedName("phone")
    @Expose
    private String mobileNumber;


    @SerializedName("login_ip")
    @Expose
    private String login_ip;
    @SerializedName("login_location")
    @Expose
    private String login_location;
    @SerializedName("login_type")
    @Expose
    private String login_type;
    @SerializedName("login_device")
    @Expose
    private String login_device;
    @SerializedName("app_version")
    @Expose
    private String app_version;
    @SerializedName("isExisting")
    @Expose
    private String isExisting ;
    @SerializedName("device_unique_id")
    @Expose
    private String device_unique_id ;

    @SerializedName("device")
    @Expose
    private String device ;

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
    public void setIsExisting(String isExisting) {
        this.isExisting = isExisting;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public void setLogin_ip(String login_ip) {
        this.login_ip = login_ip;
    }

    public void setLogin_location(String login_location) {
        this.login_location = login_location;
    }

    public void setLogin_type(String login_type) {
        this.login_type = login_type;
    }

    public void setLogin_device(String login_device) {
        this.login_device = login_device;
    }

    public void setApp_version(String app_version) {
        this.app_version = app_version;
    }

    public void setDevice_unique_id(String device_unique_id) {
        this.device_unique_id = device_unique_id;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public void setData() {
        login_type = "Android";
        login_device = Utilities.getDeviceName();
        login_device = Utilities.getIPAddress(true);
        app_version = BuildConfig.VERSION_NAME;

    }

}
