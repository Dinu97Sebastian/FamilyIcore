package com.familheey.app.Models.Response;

import android.os.Parcel;
import android.os.Parcelable;

public class EventContacts implements Parcelable {
    String name = "";
    String email = "";
    String id = "";

    protected EventContacts(Parcel in) {
        name = in.readString();
        email = in.readString();
        phone = in.readString();
        id = in.readString();
    }

    public static final Creator<EventContacts> CREATOR = new Creator<EventContacts>() {
        @Override
        public EventContacts createFromParcel(Parcel in) {
            return new EventContacts(in);
        }

        @Override
        public EventContacts[] newArray(int size) {
            return new EventContacts[size];
        }
    };

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    String phone = "";

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(email);
        parcel.writeString(phone);
        parcel.writeString(id);
    }

    public String getId() {
        return id;
    }
}