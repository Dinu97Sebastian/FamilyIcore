package com.familheey.app.Models.Response;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class RelationShip implements Parcelable {

    public static final Creator<RelationShip> CREATOR = new Creator<RelationShip>() {
        @Override
        public RelationShip createFromParcel(Parcel in) {
            return new RelationShip(in);
        }

        @Override
        public RelationShip[] newArray(int size) {
            return new RelationShip[size];
        }
    };
    @SerializedName("createdAt")
    private String createdAt;
    @SerializedName("opposite_relation_female")
    private String oppositeRelationFemale;
    @SerializedName("id")
    private Integer id;
    @SerializedName("relationship")
    private String relationship;
    @SerializedName("opposite_relation_male")
    private String oppositeRelationMale;
    @SerializedName("updatedAt")
    private String updatedAt;

    public RelationShip() {
    }

    protected RelationShip(Parcel in) {
        createdAt = in.readString();
        oppositeRelationFemale = in.readString();
        id = in.readInt();
        relationship = in.readString();
        oppositeRelationMale = in.readString();
        updatedAt = in.readString();
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getOppositeRelationFemale() {
        return oppositeRelationFemale;
    }

    public void setOppositeRelationFemale(String oppositeRelationFemale) {
        this.oppositeRelationFemale = oppositeRelationFemale;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public String getOppositeRelationMale() {
        return oppositeRelationMale;
    }

    public void setOppositeRelationMale(String oppositeRelationMale) {
        this.oppositeRelationMale = oppositeRelationMale;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return
                "RelationShip{" +
                        "createdAt = '" + createdAt + '\'' +
                        ",opposite_relation_female = '" + oppositeRelationFemale + '\'' +
                        ",id = '" + id + '\'' +
                        ",relationship = '" + relationship + '\'' +
                        ",opposite_relation_male = '" + oppositeRelationMale + '\'' +
                        ",updatedAt = '" + updatedAt + '\'' +
                        "}";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(createdAt);
        dest.writeString(oppositeRelationFemale);
        dest.writeInt(id);
        dest.writeString(relationship);
        dest.writeString(oppositeRelationMale);
        dest.writeString(updatedAt);
    }

}