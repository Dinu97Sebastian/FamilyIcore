package com.familheey.app.Models.Response;

import android.os.Parcel;
import android.os.Parcelable;

import com.familheey.app.Utilities.SharedPref;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FamilyMember implements Parcelable {

    @SerializedName("is_primar_admin")
    @Expose
    private String isPrimarAdmin;

    @SerializedName("propic")
    @Expose
    private String proPic;

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("is_blocked")
    @Expose
    private Boolean isBlocked;
    @SerializedName("is_removed")
    @Expose
    private Boolean isRemoved;
    @SerializedName("group_id")
    @Expose
    private Integer groupId;
    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("user_type")
    @Expose
    private String userType;
    @SerializedName("full_name")
    @Expose
    private String fullName;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("crnt_user_id")
    @Expose
    private String crntUserId;
    @SerializedName("relation_ship")
    @Expose
    private String relationShip;
    @SerializedName("relation_id")
    @Expose
    private String relationId;
    public static final Creator<FamilyMember> CREATOR = new Creator<FamilyMember>() {
        @Override
        public FamilyMember createFromParcel(Parcel in) {
            return new FamilyMember(in);
        }

        @Override
        public FamilyMember[] newArray(int size) {
            return new FamilyMember[size];
        }
    };
    @SerializedName("member_since")
    @Expose
    private String memberSince;

    public void setProPic(String proPic) {
        this.proPic = proPic;
    }
    private Integer developerStatus = 0;
    private String developerMessage = "";
    private Boolean isDevEnabled = false;

    public FamilyMember() {
    }

    @SerializedName("groupmap_id")
    @Expose
    private Integer groupMapId;

    protected FamilyMember(Parcel in) {
        isPrimarAdmin = in.readString();
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        byte tmpIsBlocked = in.readByte();
        isBlocked = tmpIsBlocked == 0 ? null : tmpIsBlocked == 1;
        byte tmpIsRemoved = in.readByte();
        isRemoved = tmpIsRemoved == 0 ? null : tmpIsRemoved == 1;
        if (in.readByte() == 0) {
            groupId = null;
        } else {
            groupId = in.readInt();
        }
        if (in.readByte() == 0) {
            userId = null;
        } else {
            userId = in.readInt();
        }
        userType = in.readString();
        fullName = in.readString();
        email = in.readString();
        gender = in.readString();
        crntUserId = in.readString();
        relationShip = in.readString();
        relationId = in.readString();
        proPic = in.readString();
        memberSince = in.readString();
        if (in.readByte() == 0) {
            groupMapId = null;
        } else {
            groupMapId = in.readInt();
        }
        if (in.readByte() == 0) {
            developerStatus = null;
        } else {
            developerStatus = in.readInt();
        }
        developerMessage = in.readString();
        byte tmpIsDevEnabled = in.readByte();
        isDevEnabled = tmpIsDevEnabled == 0 ? null : tmpIsDevEnabled == 1;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(isPrimarAdmin);
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(id);
        }
        dest.writeByte((byte) (isBlocked == null ? 0 : isBlocked ? 1 : 2));
        dest.writeByte((byte) (isRemoved == null ? 0 : isRemoved ? 1 : 2));
        if (groupId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(groupId);
        }
        if (userId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(userId);
        }
        dest.writeString(userType);
        dest.writeString(fullName);
        dest.writeString(email);
        dest.writeString(gender);
        dest.writeString(crntUserId);
        dest.writeString(relationShip);
        dest.writeString(relationId);
        dest.writeString(proPic);
        dest.writeString(memberSince);
        if (groupMapId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(groupMapId);
        }
        if (developerStatus == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(developerStatus);
        }
        dest.writeString(developerMessage);
        dest.writeByte((byte) (isDevEnabled == null ? 0 : isDevEnabled ? 1 : 2));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getIsPrimarAdmin() {
        return isPrimarAdmin;
    }

    public void setIsPrimarAdmin(String isPrimarAdmin) {
        this.isPrimarAdmin = isPrimarAdmin;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getIsBlocked() {
        return isBlocked;
    }

    public void setIsBlocked(Boolean isBlocked) {
        this.isBlocked = isBlocked;
    }

    public Boolean getIsRemoved() {
        return isRemoved;
    }

    public void setIsRemoved(Boolean isRemoved) {
        this.isRemoved = isRemoved;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCrntUserId() {
        return crntUserId;
    }

    public void setCrntUserId(String crntUserId) {
        this.crntUserId = crntUserId;
    }

    public String getRelationShip() {
        return relationShip;
    }

    public void setRelationShip(String relationShip) {
        this.relationShip = relationShip;
    }

    public String getRelationId() {
        return relationId;
    }

    public void setRelationId(String relationId) {
        this.relationId = relationId;
    }

    public Integer getDeveloperStatus() {
        return developerStatus;
    }

    public void setDeveloperStatus(Integer developerStatus) {
        this.developerStatus = developerStatus;
    }

    public String getDeveloperMessage() {
        return developerMessage;
    }

    public void setDeveloperMessage(String developerMessage) {
        this.developerMessage = developerMessage;
    }

    public Boolean getDevEnabled() {
        return isDevEnabled;
    }

    public void setDevEnabled(Boolean devEnabled) {
        isDevEnabled = devEnabled;
    }

    public Integer getGroupMapId() {
        return groupMapId;
    }

    public String getMemberSince() {
        return memberSince;
    }

    public boolean isPrimaryAdmin() {
        return (getIsPrimarAdmin() != null && getIsPrimarAdmin().equalsIgnoreCase("admin"));
    }

    public boolean isAdmin() {
        return (getUserType() != null && getUserType().equalsIgnoreCase("admin"));
    }

    public String getProPic() {
        return proPic;
    }

    public boolean getIsAdminByValidatingID() {
        try {
            return String.valueOf(getId()).equalsIgnoreCase(SharedPref.getUserRegistration().getId());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
