package com.familheey.app.Models.Response;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.File;

public class UserRegistrationResponse implements Parcelable {

    @SerializedName("code")
    @Expose
    private String statusCode;

    @SerializedName("notification")
    private boolean notification;

    @SerializedName("countryCode")
    @Expose
    private String capturedCountryCode;

    @SerializedName("mobileNumber")
    @Expose
    private String capturedMobileNumber;

    @SerializedName("is_active")
    private boolean isActive;

    @SerializedName("propic")
    private String propic;

    @SerializedName("gender")
    private String gender;

    @SerializedName("otp_time")
    private String otpTime;

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
    private String id;

    @SerializedName("email")
    private String email;

    @SerializedName("accessToken")
    private String accessToken;

    @SerializedName("refreshToken")
    private String refreshToken;

    @SerializedName("reLogin")
    private Boolean reLogin;

    @SerializedName("token")
    private String token;

    @SerializedName("lat")
    private Double lat;

    @SerializedName("long")
    private Double lng;

    private String capturedImage = null;

    public UserRegistrationResponse() {
    }


    protected UserRegistrationResponse(Parcel in) {
        notification = in.readByte() != 0;
        capturedCountryCode = in.readString();
        statusCode=in.readString();
        capturedMobileNumber = in.readString();
        isActive = in.readByte() != 0;
        propic = in.readString();
        gender = in.readString();
        otpTime = in.readString();
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
        id = in.readString();
        email = in.readString();
        accessToken = in.readString();
        refreshToken = in.readString();
        byte tmpReLogin = in.readByte();
        reLogin = tmpReLogin == 0 ? null : tmpReLogin == 1;
        token = in.readString();
        if (in.readByte() == 0) {
            lat = null;
        } else {
            lat = in.readDouble();
        }
        if (in.readByte() == 0) {
            lng = null;
        } else {
            lng = in.readDouble();
        }
        capturedImage = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeByte((byte) (notification ? 1 : 0));
        dest.writeString(capturedCountryCode);
        dest.writeString( statusCode );
        dest.writeString(capturedMobileNumber);
        dest.writeByte((byte) (isActive ? 1 : 0));
        dest.writeString(propic);
        dest.writeString(gender);
        dest.writeString(otpTime);
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
        dest.writeString(id);
        dest.writeString(email);
        dest.writeString(accessToken);
        dest.writeString(refreshToken);
        dest.writeByte((byte) (reLogin == null ? 0 : reLogin ? 1 : 2));
        dest.writeString(token);
        if (lat == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(lat);
        }
        if (lng == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(lng);
        }
        dest.writeString(capturedImage);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UserRegistrationResponse> CREATOR = new Creator<UserRegistrationResponse>() {
        @Override
        public UserRegistrationResponse createFromParcel(Parcel in) {
            return new UserRegistrationResponse(in);
        }

        @Override
        public UserRegistrationResponse[] newArray(int size) {
            return new UserRegistrationResponse[size];
        }
    };

    public void setCapturedCountryCode(String capturedCountryCode) {
        this.capturedCountryCode = capturedCountryCode;
    }
    public void setstatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public void setCapturedMobileNumber(String capturedMobileNumber) {
        this.capturedMobileNumber = capturedMobileNumber;
    }

    public String getCapturedCountryCode() {
        return capturedCountryCode;
    }
    public String getStatusCode() {
        return statusCode;
    }

    public String getCapturedMobileNumber() {
        return capturedMobileNumber;
    }

    public boolean isActive() {
        return isActive;
    }

    public String getPropic() {
        return propic;
    }

    public String getGender() {
        return gender;
    }

    public String getOtpTime() {
        return otpTime;
    }

    public String getOrigin() {
        return origin;
    }

    public String getAbout() {
        return about;
    }

    public String getOtp() {
        return otp;
    }

    public String getType() {
        return type;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public String getPassword() {
        return password;
    }

    public String getFullName() {
        if (fullName == null)
            return "";
        return fullName;
    }

    public String getPhone() {
        if (phone == null)
            return "";
        return phone;
    }

    public String getDob() {
        return dob;
    }

    public String getLocation() {
        return location;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public boolean isNotification() {
        return notification;
    }

    public void setNotification(boolean notification) {
        this.notification = notification;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public boolean isAnExistingUser() {
        try {
            return getFullName() != null && getEmail() != null && getFullName().length() > 0 && getEmail().length() > 0 && getReLogin() != null && getReLogin();
        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Boolean getReLogin() {
        return reLogin;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public void setPropic(String propic) {
        this.propic = propic;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setOtpTime(String otpTime) {
        this.otpTime = otpTime;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    @Override
    public String toString() {
        return
                "UserRegistrationResponse{" +
                        "is_active = '" + isActive + '\'' +
                        ",propic = '" + propic + '\'' +
                        ",gender = '" + gender + '\'' +
                        ",otp_time = '" + otpTime + '\'' +
                        ",origin = '" + origin + '\'' +
                        ",about = '" + about + '\'' +
                        ",otp = '" + otp + '\'' +
                        ",type = '" + type + '\'' +
                        ",is_verified = '" + isVerified + '\'' +
                        ",password = '" + password + '\'' +
                        ",full_name = '" + fullName + '\'' +
                        ",phone = '" + phone + '\'' +
                        ",dob = '" + dob + '\'' +
                        ",location = '" + location + '\'' +
                        ",id = '" + id + '\'' +
                        ",email = '" + email + '\'' +
                        ",lat = '" + lat + '\'' +
                        ",lng = '" + lng + '\'' +
                        "}";
    }

    public String getMobileNumber() {
        if (!TextUtils.isEmpty(getPhone()))
            return getPhone();
        else return getCapturedCountryCode() + getCapturedMobileNumber();
    }

    public Uri getProfilePictureUri() {
        return Uri.parse(getCapturedImage());
    }

    public File getProfilePictureFile() {
        return new File(getProfilePictureUri().getPath());
    }

    public String getToken() {
        return token;
    }

    public String getCapturedImage() {
        return capturedImage;
    }

    public void setCapturedImage(String capturedImage) {
        this.capturedImage = capturedImage;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }
}