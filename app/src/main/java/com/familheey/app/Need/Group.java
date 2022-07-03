package com.familheey.app.Need;

import android.os.Parcel;
import android.os.Parcelable;

public class Group implements Parcelable {

    private Integer id;
    private String group_name;
    private String logo;
    private String base_region;

    public Group() {
    }

    protected Group(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        group_name = in.readString();
        logo = in.readString();
        base_region = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(id);
        }
        dest.writeString(group_name);
        dest.writeString(logo);
        dest.writeString(base_region);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Group> CREATOR = new Creator<Group>() {
        @Override
        public Group createFromParcel(Parcel in) {
            return new Group(in);
        }

        @Override
        public Group[] newArray(int size) {
            return new Group[size];
        }
    };

    public Integer getId() {
        return id;
    }

    public String getGroup_name() {
        return group_name;
    }

    public String getLogo() {
        return logo;
    }

    public String getBase_region() {
        return base_region;
    }
}
