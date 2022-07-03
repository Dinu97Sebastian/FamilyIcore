package com.familheey.app.Models.Response.ProfileResponse;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Count implements Parcelable {

    @SerializedName("familyCount")
    private int familyCount;

    @SerializedName("connections")
    private int connections;

    @SerializedName("mutual_connections")
    private int mutualConnections;

    @SerializedName("mutual_families")
    private int mutualFamiliesCount;

    public static final Creator<Count> CREATOR = new Creator<Count>() {
        @Override
        public Count createFromParcel(Parcel in) {
            return new Count(in);
        }

        @Override
        public Count[] newArray(int size) {
            return new Count[size];
        }
    };

    protected Count(Parcel in) {
        familyCount = in.readInt();
        connections = in.readInt();
        mutualConnections = in.readInt();
        mutualFamiliesCount = in.readInt();
    }

    public int getFamilyCount() {
        return familyCount;
    }

    public void setFamilyCount(int familyCount) {
        this.familyCount = familyCount;
    }

    public int getConnections() {
        return connections;
    }

    public void setConnections(int connections) {
        this.connections = connections;
    }

    public int getMutualConnections() {
        return mutualConnections;
    }

    public int getMutualFamiliesCount() {
        return mutualFamiliesCount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(familyCount);
        dest.writeInt(connections);
        dest.writeInt(mutualConnections);
        dest.writeInt(mutualFamiliesCount);
    }
}