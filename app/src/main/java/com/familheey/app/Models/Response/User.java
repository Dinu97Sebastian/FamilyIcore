package com.familheey.app.Models.Response;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("about")
    private Object mAbout;
    @SerializedName("createdAt")
    private String mCreatedAt;
    @SerializedName("dob")
    private String mDob;
    @SerializedName("exists")
    private Boolean mEXISTS;
    @SerializedName("email")
    private String mEmail;
    @SerializedName("FLAG")
    private Boolean mFLAG;
    @SerializedName("full_name")
    private String mFullName;
    @SerializedName("gender")
    private String mGender;
    @SerializedName("invitation_status")
    private Boolean mINVITATIONSTATUS;
    @SerializedName("id")
    private int mId;
    @SerializedName("is_active")
    private Boolean mIsActive;
    @SerializedName("is_verified")
    private Boolean mIsVerified;
    @SerializedName("location")
    private String mLocation;
    @SerializedName("origin")
    private String mOrigin;
    @SerializedName("otp")
    private String mOtp;
    @SerializedName("otp_time")
    private Object mOtpTime;
    @SerializedName("password")
    private String mPassword;
    @SerializedName("phone")
    private String mPhone;
    @SerializedName("propic")
    private String mPropic;
    @SerializedName("type")
    private Object mType;
    @SerializedName("updatedAt")
    private String mUpdatedAt;

    private boolean isUpdating = false;

    public Object getAbout() {
        return mAbout;
    }

    public void setAbout(Object about) {
        mAbout = about;
    }

    public String getCreatedAt() {
        return mCreatedAt;
    }

    public void setCreatedAt(String createdAt) {
        mCreatedAt = createdAt;
    }

    public String getDob() {
        return mDob;
    }

    public void setDob(String dob) {
        mDob = dob;
    }

    public Boolean getEXISTS() {
        return mEXISTS;
    }

    public void setEXISTS(Boolean eXISTS) {
        mEXISTS = eXISTS;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public Boolean getFLAG() {
        return mFLAG;
    }

    public void setFLAG(Boolean fLAG) {
        mFLAG = fLAG;
    }

    public String getFullName() {
        return mFullName;
    }

    public void setFullName(String fullName) {
        mFullName = fullName;
    }

    public String getGender() {
        return mGender;
    }

    public void setGender(String gender) {
        mGender = gender;
    }

    public Boolean getINVITATIONSTATUS() {
        return mINVITATIONSTATUS;
    }

    public void setINVITATIONSTATUS(Boolean iNVITATIONSTATUS) {
        mINVITATIONSTATUS = iNVITATIONSTATUS;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public Boolean getIsActive() {
        return mIsActive;
    }

    public void setIsActive(Boolean isActive) {
        mIsActive = isActive;
    }

    public Boolean getIsVerified() {
        return mIsVerified;
    }

    public void setIsVerified(Boolean isVerified) {
        mIsVerified = isVerified;
    }

    public String getLocation() {
        return mLocation;
    }

    public void setLocation(String location) {
        mLocation = location;
    }

    public String getOrigin() {
        return mOrigin;
    }

    public void setOrigin(String origin) {
        mOrigin = origin;
    }

    public String getOtp() {
        return mOtp;
    }

    public void setOtp(String otp) {
        mOtp = otp;
    }

    public Object getOtpTime() {
        return mOtpTime;
    }

    public void setOtpTime(Object otpTime) {
        mOtpTime = otpTime;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        mPassword = password;
    }

    public String getPhone() {
        return mPhone;
    }

    public void setPhone(String phone) {
        mPhone = phone;
    }

    public String getPropic() {
        return mPropic;
    }

    public void setPropic(String propic) {
        mPropic = propic;
    }

    public Object getType() {
        return mType;
    }

    public void setType(Object type) {
        mType = type;
    }

    public String getUpdatedAt() {
        return mUpdatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        mUpdatedAt = updatedAt;
    }

    public boolean isUpdating() {
        return isUpdating;
    }

    public void setUpdating(boolean updating) {
        isUpdating = updating;
    }
}
