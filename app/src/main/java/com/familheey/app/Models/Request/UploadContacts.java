package com.familheey.app.Models.Request;

import com.familheey.app.Models.ContactModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class UploadContacts {

    @SerializedName("country_code")
    @Expose
    String countyCode;
    @SerializedName("group_id")
    @Expose
    String groupId;
    @SerializedName("from_user")
    @Expose
    String fromUser;
    @SerializedName("contacts")
    @Expose
    List<ContactModel> contacts = new ArrayList<>();

    public String getCountyCode() {
        return countyCode;
    }

    public void setCountyCode(String countyCode) {
        this.countyCode = countyCode;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public List<ContactModel> getContacts() {
        return contacts;
    }

    public void setContacts(List<ContactModel> contacts) {
        this.contacts = contacts;
    }

    public String getFromUser() {
        return fromUser;
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }
}
