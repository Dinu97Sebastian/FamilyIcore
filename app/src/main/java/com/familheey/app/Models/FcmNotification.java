package com.familheey.app.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class FcmNotification implements Parcelable {
    String type;
    String type_id;
    String sub_type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType_id() {
        return type_id;
    }

    public void setType_id(String type_id) {
        this.type_id = type_id;
    }

    public String getSub_type() {
        return sub_type;
    }

    public void setSub_type(String sub_type) {
        this.sub_type = sub_type;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.type);
        dest.writeString(this.type_id);
        dest.writeString(this.sub_type);
    }

    public FcmNotification() {
    }

    protected FcmNotification(Parcel in) {
        this.type = in.readString();
        this.type_id = in.readString();
        this.sub_type = in.readString();
    }

    public static final Parcelable.Creator<FcmNotification> CREATOR = new Parcelable.Creator<FcmNotification>() {
        @Override
        public FcmNotification createFromParcel(Parcel source) {
            return new FcmNotification(source);
        }

        @Override
        public FcmNotification[] newArray(int size) {
            return new FcmNotification[size];
        }
    };
}
