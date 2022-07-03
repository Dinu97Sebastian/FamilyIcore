package com.familheey.app.Models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

public class FamilyCreation implements Parcelable {
    private String id = "";
    private String name = "";
    private String type = "";
    private String location = "";
    private String introduction = "";
    private Uri coverPicUri = null;
    private Uri unCroppedCoverPicUri = null;
    private Uri logoUri = null;
    private boolean isPrivate = true;
    private boolean isSearchable = true;
    private boolean isLinkable = false;
    public static final Creator<FamilyCreation> CREATOR = new Creator<FamilyCreation>() {
        @Override
        public FamilyCreation createFromParcel(Parcel in) {
            return new FamilyCreation(in);
        }

        @Override
        public FamilyCreation[] newArray(int size) {
            return new FamilyCreation[size];
        }
    };

    public FamilyCreation() {
    }

    private LatLng latLng = null;

    protected FamilyCreation(Parcel in) {
        id = in.readString();
        name = in.readString();
        type = in.readString();
        location = in.readString();
        introduction = in.readString();
        coverPicUri = in.readParcelable(Uri.class.getClassLoader());
        unCroppedCoverPicUri = in.readParcelable(Uri.class.getClassLoader());
        logoUri = in.readParcelable(Uri.class.getClassLoader());
        isPrivate = in.readByte() != 0;
        isSearchable = in.readByte() != 0;
        isLinkable = in.readByte() != 0;
        latLng = in.readParcelable(LatLng.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(type);
        dest.writeString(location);
        dest.writeString(introduction);
        dest.writeParcelable(coverPicUri, flags);
        dest.writeParcelable(unCroppedCoverPicUri, flags);
        dest.writeParcelable(logoUri, flags);
        dest.writeByte((byte) (isPrivate ? 1 : 0));
        dest.writeByte((byte) (isSearchable ? 1 : 0));
        dest.writeByte((byte) (isLinkable ? 1 : 0));
        dest.writeParcelable(latLng, flags);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public Uri getCoverPicUri() {
        return coverPicUri;
    }

    public void setCoverPicUri(Uri coverPicUri) {
        this.coverPicUri = coverPicUri;
    }

    public Uri getLogoUri() {
        return logoUri;
    }

    public void setLogoUri(Uri logoUri) {
        this.logoUri = logoUri;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public boolean isSearchable() {
        return isSearchable;
    }

    public void setSearchable(boolean searchable) {
        isSearchable = searchable;
    }

    public boolean isLinkable() {
        return isLinkable;
    }

    public void setLinkable(boolean linkable) {
        isLinkable = linkable;
    }

    public Uri getUnCroppedCoverPicUri() {
        return unCroppedCoverPicUri;
    }

    public void setUnCroppedCoverPicUri(Uri unCroppedCoverPicUri) {
        this.unCroppedCoverPicUri = unCroppedCoverPicUri;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }
}
