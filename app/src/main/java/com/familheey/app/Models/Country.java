package com.familheey.app.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Country implements Parcelable {
    @SerializedName("code")
    @Expose
    private String countryCode;
    @SerializedName("name")
    @Expose
    private String countryName;
    @SerializedName("dial_code")
    @Expose
    public String dialCode;
    @SerializedName("flag")
    @Expose
    public int flag;
    @SerializedName("currency")
    @Expose
    public String currency;
    public Country() {
        //empty Constructor
    }

    protected Country(Parcel in) {
        countryCode = in.readString();
        countryName = in.readString();
        dialCode = in.readString();
        flag=in.readInt();
        currency=in.readString();
    }

    public static final Creator<Country> CREATOR = new Creator<Country>() {
        @Override
        public Country createFromParcel(Parcel in) {
            return new Country(in);
        }

        @Override
        public Country[] newArray(int size) {
            return new Country[size];
        }
    };


    public String getCountryCode() {
        return countryCode;
    }
   public Country(String code, String name, String dialCode, int flag,String currency) {
        this.countryCode = code;
        this.countryName = name;
        this.dialCode = dialCode;
        this.flag = flag;
        this.currency=currency;
    }
    public String getCountryName() {
        return countryName;
    }

    public String getDialCode() {
        return dialCode;
    }
    public int getFlag() {
        return flag;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(countryCode);
        dest.writeString(countryName);
        dest.writeString(dialCode);
        dest.writeInt(flag);
    }
}
