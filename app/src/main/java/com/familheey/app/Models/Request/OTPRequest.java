package com.familheey.app.Models.Request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OTPRequest {
    @SerializedName("otp")
    @Expose
    private String otp;
    @SerializedName("phone")
    @Expose
    private String phone;
    /**for ticket 693**/
    @SerializedName("reverify")
    @Expose
    private String reverify;

    public String getReverify() {
        return reverify;
    }

    public void setReverify(String reverify) {
        this.reverify = reverify;
    }
    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
