package com.familheey.app.Models.Response;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PeopleSearchModal implements Parcelable {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("full_name")
    @Expose
    private String fullName;
    @SerializedName("origin")
    @Expose
    private String origin;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("propic")
    @Expose
    private String propic;
    @SerializedName("familycount")
    @Expose
    private String familycount;
    @SerializedName("mutualfamilycount")
    @Expose
    private String mutualfamilycount;
    @SerializedName("location")
    @Expose
    private String location;

    public PeopleSearchModal() {
    }

    public static final Creator<PeopleSearchModal> CREATOR = new Creator<PeopleSearchModal>() {
        @Override
        public PeopleSearchModal createFromParcel(Parcel in) {
            return new PeopleSearchModal(in);
        }

        @Override
        public PeopleSearchModal[] newArray(int size) {
            return new PeopleSearchModal[size];
        }
    };
    @SerializedName("logined_user_family_count")
    @Expose
    private String loggedUserFamilyCount;

    protected PeopleSearchModal(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        fullName = in.readString();
        origin = in.readString();
        gender = in.readString();
        propic = in.readString();
        familycount = in.readString();
        mutualfamilycount = in.readString();
        location = in.readString();
        loggedUserFamilyCount = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(id);
        }
        dest.writeString(fullName);
        dest.writeString(origin);
        dest.writeString(gender);
        dest.writeString(propic);
        dest.writeString(familycount);
        dest.writeString(mutualfamilycount);
        dest.writeString(location);
        dest.writeString(loggedUserFamilyCount);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPropic() {
        return propic;
    }

    public void setPropic(String propic) {
        this.propic = propic;
    }

    public String getFamilycount() {
        return familycount;
    }

    public void setFamilycount(String familycount) {
        this.familycount = familycount;
    }

    public String getMutualfamilycount() {
        return mutualfamilycount;
    }

    public void setMutualfamilycount(String mutualfamilycount) {
        this.mutualfamilycount = mutualfamilycount;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLoggedUserFamilyCount() {
        return loggedUserFamilyCount;
    }
}