package com.familheey.app.Models.Request;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SimilarFamilyRequest implements Parcelable {

    public static final Creator<SimilarFamilyRequest> CREATOR = new Creator<SimilarFamilyRequest>() {
        @Override
        public SimilarFamilyRequest createFromParcel(Parcel in) {
            return new SimilarFamilyRequest(in);
        }

        @Override
        public SimilarFamilyRequest[] newArray(int size) {
            return new SimilarFamilyRequest[size];
        }
    };
    @SerializedName("group_name")
    @Expose
    private final String groupName;
    @SerializedName("group_category")
    @Expose
    private final String groupCategory;
    @SerializedName("base_region")
    @Expose
    private final String baseRegion;
    @SerializedName("user_id")
    @Expose
    private final String userId;

    public SimilarFamilyRequest(String groupName, String groupCategory, String baseRegion, String userId) {
        this.groupName = groupName;
        this.groupCategory = groupCategory;
        this.baseRegion = baseRegion;
        this.userId = userId;
    }

    protected SimilarFamilyRequest(Parcel in) {
        groupName = in.readString();
        groupCategory = in.readString();
        baseRegion = in.readString();
        userId = in.readString();
    }

    public String getGroupName() {
        return groupName;
    }

    public String getGroupCategory() {
        return groupCategory;
    }

    public String getBaseRegion() {
        return baseRegion;
    }

    public String getUserId() {
        return userId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(groupName);
        dest.writeString(groupCategory);
        dest.writeString(baseRegion);
        dest.writeString(userId);
    }
}
