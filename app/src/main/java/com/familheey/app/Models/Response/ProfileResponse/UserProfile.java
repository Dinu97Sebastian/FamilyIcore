package com.familheey.app.Models.Response.ProfileResponse;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UserProfile implements Parcelable {

    @SerializedName("profile")
    private Profile profile;
    @SerializedName("count")
    private Count count;
    @SerializedName("groups")
    private List<GroupsItem> groups;

    public UserProfile() {
    }

    public static final Creator<UserProfile> CREATOR = new Creator<UserProfile>() {
        @Override
        public UserProfile createFromParcel(Parcel in) {
            return new UserProfile(in);
        }

        @Override
        public UserProfile[] newArray(int size) {
            return new UserProfile[size];
        }
    };

    protected UserProfile(Parcel in) {
        profile = in.readParcelable(Profile.class.getClassLoader());
        count = in.readParcelable(Count.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(profile, flags);
        dest.writeParcelable(count, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public Count getCount() {
        return count;
    }

    public void setCount(Count count) {
        this.count = count;
    }

    public List<GroupsItem> getGroups() {
        return groups;
    }

    public void setGroups(List<GroupsItem> groups) {
        this.groups = groups;
    }
}