package com.familheey.app.Models.Response.ProfileResponse;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Profile implements Parcelable {

    @SerializedName("is_active")
    private boolean isActive;

    @SerializedName("propic")
    private String propic;

    @SerializedName("gender")
    private String gender;

    @SerializedName("otp_time")
    private String otpTime;

    @SerializedName("cover_pic")
    private String coverPic;

    @SerializedName("origin")
    private String origin;

    @SerializedName("about")
    private String about;

    @SerializedName("otp")
    private String otp;

    @SerializedName("type")
    private String type;

    @SerializedName("is_verified")
    private boolean isVerified;

    @SerializedName("password")
    private String password;

    @SerializedName("full_name")
    private String fullName;

    @SerializedName("phone")
    private String phone;

    @SerializedName("dob")
    private String dob;

    @SerializedName("location")
    private String location;

    @SerializedName("id")
    private int id;

    @SerializedName("email")
    private String email;

    @SerializedName("work")
    private String work;

    public static final Creator<Profile> CREATOR = new Creator<Profile>() {
        @Override
        public Profile createFromParcel(Parcel in) {
            return new Profile(in);
        }

        @Override
        public Profile[] newArray(int size) {
            return new Profile[size];
        }
    };
    @SerializedName("searchable")
    private Boolean searchable;

    @SerializedName("user_original_image")
    private String userOriginalImage;
    @SerializedName("notification")
    private Boolean notification;

    public Profile() {
    }

    protected Profile(Parcel in) {
        isActive = in.readByte() != 0;
        propic = in.readString();
        gender = in.readString();
        otpTime = in.readString();
        coverPic = in.readString();
        origin = in.readString();
        about = in.readString();
        otp = in.readString();
        type = in.readString();
        isVerified = in.readByte() != 0;
        password = in.readString();
        fullName = in.readString();
        phone = in.readString();
        dob = in.readString();
        location = in.readString();
        id = in.readInt();
        email = in.readString();
        work = in.readString();
        byte tmpSearchable = in.readByte();
        searchable = tmpSearchable == 0 ? null : tmpSearchable == 1;
        byte tmpNotification = in.readByte();
        notification = tmpNotification == 0 ? null : tmpNotification == 1;
        userOriginalImage = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (isActive ? 1 : 0));
        dest.writeString(propic);
        dest.writeString(gender);
        dest.writeString(otpTime);
        dest.writeString(coverPic);
        dest.writeString(origin);
        dest.writeString(about);
        dest.writeString(otp);
        dest.writeString(type);
        dest.writeByte((byte) (isVerified ? 1 : 0));
        dest.writeString(password);
        dest.writeString(fullName);
        dest.writeString(phone);
        dest.writeString(dob);
        dest.writeString(location);
        dest.writeInt(id);
        dest.writeString(email);
        dest.writeString(work);
        dest.writeByte((byte) (searchable == null ? 0 : searchable ? 1 : 2));
        dest.writeByte((byte) (notification == null ? 0 : notification ? 1 : 2));
        dest.writeString(userOriginalImage);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public boolean isIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public String getPropic() {
        return propic;
    }

    public void setPropic(String propic) {
        this.propic = propic;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getOtpTime() {
        return otpTime;
    }

    public void setOtpTime(String otpTime) {
        this.otpTime = otpTime;
    }

    public String getCoverPic() {
        return coverPic;
    }

    public void setCoverPic(String coverPic) {
        this.coverPic = coverPic;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isIsVerified() {
        return isVerified;
    }

    public void setIsVerified(boolean isVerified) {
        this.isVerified = isVerified;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWork() {
        return work;
    }

    public void setWork(String work) {
        this.work = work;
    }

    public String getUserOriginalImage() {
        return userOriginalImage;
    }

    public String getFormattedDate() {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            Date date = simpleDateFormat.parse(getDob());
            SimpleDateFormat ourFormat = new SimpleDateFormat("MMM dd yyyy", Locale.getDefault());
            return ourFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return getDob();
        }
    }

    public Boolean getSearchable() {
        return searchable;
    }

    public Boolean getNotification() {
        return notification;
    }
}