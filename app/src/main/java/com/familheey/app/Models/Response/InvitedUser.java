package com.familheey.app.Models.Response;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class InvitedUser implements Parcelable {
    @SerializedName("event_name")
    @Expose
    private String eventName;
    @SerializedName("event_image")
    @Expose
    private String eventImage;
    @SerializedName("event_original_image")
    @Expose
    private Object eventOriginalImage;
    @SerializedName("location")
    @Expose
    private String location;
    @SerializedName("from_date")
    @Expose
    private Integer fromDate;
    @SerializedName("to_date")
    @Expose
    private Integer toDate;
    @SerializedName("full_name")
    @Expose
    private String fullName;
    @SerializedName("propic")
    @Expose
    private String propic;

    public static final Creator<InvitedUser> CREATOR = new Creator<InvitedUser>() {
        @Override
        public InvitedUser createFromParcel(Parcel in) {
            return new InvitedUser(in);
        }

        @Override
        public InvitedUser[] newArray(int size) {
            return new InvitedUser[size];
        }
    };

    protected InvitedUser(Parcel in) {
        eventName = in.readString();
        eventImage = in.readString();
        location = in.readString();
        if (in.readByte() == 0) {
            fromDate = null;
        } else {
            fromDate = in.readInt();
        }
        if (in.readByte() == 0) {
            toDate = null;
        } else {
            toDate = in.readInt();
        }
        fullName = in.readString();
        propic = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(eventName);
        dest.writeString(eventImage);
        dest.writeString(location);
        if (fromDate == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(fromDate);
        }
        if (toDate == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(toDate);
        }
        dest.writeString(fullName);
        dest.writeString(propic);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getEventName() {
        return eventName;
    }

    public String getEventImage() {
        return eventImage;
    }

    public Object getEventOriginalImage() {
        return eventOriginalImage;
    }

    public String getLocation() {
        return location;
    }

    public Integer getFromDate() {
        return fromDate;
    }

    public Integer getToDate() {
        return toDate;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPropic() {
        return propic;
    }
}
