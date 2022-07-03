package com.familheey.app.Models.Response;

import android.text.TextUtils;

import com.familheey.app.BuildConfig;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UserSettings {


    @SerializedName("notification_auto_delete")
    @Expose
    private Boolean notification_auto_delete;

    @SerializedName("notification")
    @Expose
    private Boolean notification;

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("full_name")
    @Expose
    private String fullName;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("dob")
    @Expose
    private String dob;
    @SerializedName("origin")
    @Expose
    private String origin;
    @SerializedName("location")
    @Expose
    private String location;
    @SerializedName("family_count")
    @Expose
    private Integer familyCount;
    @SerializedName("code")
    @Expose
    private Integer code;

    @SerializedName("android_version")
    @Expose
    String versionCode;

    @SerializedName("android_force")
    @Expose
    Boolean forceUpdateRequest;


    @SerializedName("user_is_blocked")
    @Expose
    Boolean user_is_blocked;

    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("is_active")
    @Expose
    private Boolean isActive;

    @SerializedName("subscriptions")
    List<subscriptions> subscriptionsList;



    /** ticket 693 **/
    @SerializedName("allow_reverification")
    @Expose
    Boolean allowReVerification;

    public Boolean getAllowReVerification() {
        return allowReVerification;
    }

    public void setAllowReVerification(Boolean allowReVerification) {
        this.allowReVerification = allowReVerification;
    }

    public Boolean getUser_is_blocked() {
        return user_is_blocked;
    }

    public void setUser_is_blocked(Boolean user_is_blocked) {
        this.user_is_blocked = user_is_blocked;
    }

    public Boolean getNotification_auto_delete() {
        return notification_auto_delete;
    }

    public void setNotification_auto_delete(Boolean notification_auto_delete) {
        this.notification_auto_delete = notification_auto_delete;
    }

    public List<subscriptions> getSubscriptionsList() {
        return subscriptionsList;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public Boolean getNotification() {
        return notification;
    }

    public void setNotification(Boolean notification) {
        this.notification = notification;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getFamilyCount() {
        return familyCount;
    }

    public void setFamilyCount(Integer familyCount) {
        this.familyCount = familyCount;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public boolean isValidUser() {
        return (getEmail() != null && !TextUtils.isEmpty(getEmail()) && getFullName() != null && !TextUtils.isEmpty(getFullName()) && getIsActive() != null && getIsActive());
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public boolean isForceUpdateRequired() {
        try {
            if (getForceUpdateRequest()) {
                if (BuildConfig.VERSION_CODE < Integer.parseInt(getVersionCode())) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public Boolean getForceUpdateRequest() {
        if (forceUpdateRequest == null)
            return false;
        return forceUpdateRequest;
    }

    public String getPhone() {
        return phone;
    }

   public class subscriptions{
        boolean isexpired;
        boolean is_active;
        int difference;
        int group_period;
        String subscribe_expiry;
        String subscribed_date;

       public String getSubscribe_expiry() {
           return subscribe_expiry;
       }

       public String getSubscribed_date() {
           return subscribed_date;
       }

       public boolean isIsexpired() {
            return isexpired;
        }

        public boolean isIs_active() {
            return is_active;
        }

        public int getDifference() {
            return difference;
        }

        public int getGroup_period() {
            return group_period;
        }
    }
}
