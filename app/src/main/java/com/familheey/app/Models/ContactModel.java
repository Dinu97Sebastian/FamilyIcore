package com.familheey.app.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ContactModel implements Comparable<ContactModel> {
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("phone")
    @Expose
    private String mobileNumber;
    private transient Boolean isChecked;
    @SerializedName("isReqExist")
    private String status;
    @SerializedName("isExistInGroup")
    @Expose
    private Boolean isExistInGroup = false;
    @SerializedName("isExistinApp")
    @Expose
    private Boolean isExistinApp = false;

    public void setName(String name) {
        this.name = name;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getName() {
        if (name != null)
            return name;
        else
            return "";
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public Boolean getChecked() {
        return isChecked;
    }

    public void setChecked(Boolean checked) {
        isChecked = checked;
    }

    public Boolean getExistInGroup() {
        return isExistInGroup;
    }

    public Boolean getExistinApp() {
        return isExistinApp;
    }

    public void setExistinApp(Boolean existinApp) {
        isExistinApp = existinApp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public int compareTo(ContactModel comparingContact) {
        return this.getName().compareTo(comparingContact.getName());
    }
}
