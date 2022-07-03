package com.familheey.app.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class InvitedOther implements Parcelable {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("full_name")
    @Expose
    private String fullName;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("event_id")
    @Expose
    private Integer eventId;
    @SerializedName("group_id")
    @Expose
    private String groupId;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("invited_by")
    @Expose
    private Integer invitedBy;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("rsvp")
    @Expose
    private Boolean rsvp;
    @SerializedName("is_active")
    @Expose
    private Boolean isActive;
    @SerializedName("createdAt")
    @Expose
    private String createdAt;
    @SerializedName("updatedAt")
    @Expose
    private String updatedAt;

    public static final Creator<InvitedOther> CREATOR = new Creator<InvitedOther>() {
        @Override
        public InvitedOther createFromParcel(Parcel in) {
            return new InvitedOther(in);
        }

        @Override
        public InvitedOther[] newArray(int size) {
            return new InvitedOther[size];
        }
    };

    protected InvitedOther(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        fullName = in.readString();
        email = in.readString();
        phone = in.readString();
        if (in.readByte() == 0) {
            eventId = null;
        } else {
            eventId = in.readInt();
        }
        groupId = in.readString();
        userId = in.readString();
        if (in.readByte() == 0) {
            invitedBy = null;
        } else {
            invitedBy = in.readInt();
        }
        type = in.readString();
        byte tmpRsvp = in.readByte();
        rsvp = tmpRsvp == 0 ? null : tmpRsvp == 1;
        byte tmpIsActive = in.readByte();
        isActive = tmpIsActive == 0 ? null : tmpIsActive == 1;
        createdAt = in.readString();
        updatedAt = in.readString();
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
        dest.writeString(email);
        dest.writeString(phone);
        if (eventId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(eventId);
        }
        dest.writeString(groupId);
        dest.writeString(userId);
        if (invitedBy == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(invitedBy);
        }
        dest.writeString(type);
        dest.writeByte((byte) (rsvp == null ? 0 : rsvp ? 1 : 2));
        dest.writeByte((byte) (isActive == null ? 0 : isActive ? 1 : 2));
        dest.writeString(createdAt);
        dest.writeString(updatedAt);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public Integer getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public Integer getEventId() {
        return eventId;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getUserId() {
        return userId;
    }

    public Integer getInvitedBy() {
        return invitedBy;
    }

    public String getType() {
        return type;
    }

    public Boolean getRsvp() {
        return rsvp;
    }

    public Boolean getActive() {
        return isActive;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }
}
