package com.familheey.app.Models.Response;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FamilySearchModal implements Parcelable {

    public static final int JOINED = 0;
    public static final int JOIN = 1;
    public static final int PENDING = 2;
    public static final int REJECTED = 3;
    public static final int PRIVATE = 4;
    public static final int ACCEPT_INVITATION = 5;

    @SerializedName("is_joined")
    @Expose
    private String isJoined;
    @SerializedName("group_name")
    @Expose
    private String groupName;
    @SerializedName("group_id")
    @Expose
    private Integer groupId;
    @SerializedName("group_type")
    @Expose
    private String groupType;
    @SerializedName("group_category")
    @Expose
    private String groupCategory;
    @SerializedName("visibility")
    @Expose
    private String visibility;
    @SerializedName("member_approval")
    @Expose
    private Integer memberApproval;
    @SerializedName("member_joining")
    @Expose
    private Integer memberJoining;
    @SerializedName("is_linkable")
    @Expose
    private Boolean isLinkable;
    @SerializedName("base_region")
    @Expose
    private String baseRegion;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("membercount")
    @Expose
    private String membercount;
    @SerializedName("knowncount")
    @Expose
    private String knowncount;
    @SerializedName("created_by_name")
    @Expose
    private String createdByName;
    @SerializedName("propic")
    @Expose
    private String propic;
    @SerializedName("is_removed")
    @Expose
    private Boolean isRemoved;
    @SerializedName("logo")
    @Expose
    private String logo;
    public Boolean isDevSelected = false;
    @SerializedName("id")
    @Expose
    private String id;
    public static final Creator<FamilySearchModal> CREATOR = new Creator<FamilySearchModal>() {
        @Override
        public FamilySearchModal createFromParcel(Parcel in) {
            return new FamilySearchModal(in);
        }

        @Override
        public FamilySearchModal[] newArray(int size) {
            return new FamilySearchModal[size];
        }
    };
    @SerializedName("req_id")
    @Expose
    private String requestId;

    public FamilySearchModal() {
    }

    @SerializedName("from_id")
    @Expose
    private String fromId;

    protected FamilySearchModal(Parcel in) {
        isJoined = in.readString();
        groupName = in.readString();
        if (in.readByte() == 0) {
            groupId = null;
        } else {
            groupId = in.readInt();
        }
        groupType = in.readString();
        groupCategory = in.readString();
        visibility = in.readString();
        if (in.readByte() == 0) {
            memberApproval = null;
        } else {
            memberApproval = in.readInt();
        }
        if (in.readByte() == 0) {
            memberJoining = null;
        } else {
            memberJoining = in.readInt();
        }
        byte tmpIsLinkable = in.readByte();
        isLinkable = tmpIsLinkable == 0 ? null : tmpIsLinkable == 1;
        baseRegion = in.readString();
        type = in.readString();
        status = in.readString();
        membercount = in.readString();
        knowncount = in.readString();
        createdByName = in.readString();
        propic = in.readString();
        byte tmpIsRemoved = in.readByte();
        isRemoved = tmpIsRemoved == 0 ? null : tmpIsRemoved == 1;
        logo = in.readString();
        byte tmpIsDevSelected = in.readByte();
        isDevSelected = tmpIsDevSelected == 0 ? null : tmpIsDevSelected == 1;
        id = in.readString();
        requestId = in.readString();
        fromId = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(isJoined);
        dest.writeString(groupName);
        if (groupId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(groupId);
        }
        dest.writeString(groupType);
        dest.writeString(groupCategory);
        dest.writeString(visibility);
        if (memberApproval == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(memberApproval);
        }
        if (memberJoining == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(memberJoining);
        }
        dest.writeByte((byte) (isLinkable == null ? 0 : isLinkable ? 1 : 2));
        dest.writeString(baseRegion);
        dest.writeString(type);
        dest.writeString(status);
        dest.writeString(membercount);
        dest.writeString(knowncount);
        dest.writeString(createdByName);
        dest.writeString(propic);
        dest.writeByte((byte) (isRemoved == null ? 0 : isRemoved ? 1 : 2));
        dest.writeString(logo);
        dest.writeByte((byte) (isDevSelected == null ? 0 : isDevSelected ? 1 : 2));
        dest.writeString(id);
        dest.writeString(requestId);
        dest.writeString(fromId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getIsJoined() {
        return isJoined;
    }

    public void setIsJoined(String isJoined) {
        this.isJoined = isJoined;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public String getGroupType() {
        return groupType;
    }

    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }

    public String getGroupCategory() {
        return groupCategory;
    }

    public void setGroupCategory(String groupCategory) {
        this.groupCategory = groupCategory;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public Integer getMemberApproval() {
        return memberApproval;
    }

    public void setMemberApproval(Integer memberApproval) {
        this.memberApproval = memberApproval;
    }

    public Integer getMemberJoining() {
        return memberJoining;
    }

    public void setMemberJoining(Integer memberJoining) {
        this.memberJoining = memberJoining;
    }

    public Boolean getIsLinkable() {
        return isLinkable;
    }

    public void setIsLinkable(Boolean isLinkable) {
        this.isLinkable = isLinkable;
    }

    public String getBaseRegion() {
        return baseRegion;
    }

    public void setBaseRegion(String baseRegion) {
        this.baseRegion = baseRegion;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMembercount() {
        return membercount;
    }

    public void setMembercount(String membercount) {
        this.membercount = membercount;
    }

    public String getKnowncount() {
        return knowncount;
    }

    public void setKnowncount(String knowncount) {
        this.knowncount = knowncount;
    }

    //This is the function which calculates the User Status of Joining
    public int getUserJoinedCalculated() {
        if (getIsJoined() != null && getIsJoined().equalsIgnoreCase("1")) {
            if (getIsRemoved() != null && getIsRemoved()) {
                return JOIN;
            } else {
                return JOINED;
            }
        } else if (getMemberJoining() != null && getMemberJoining().equals(1)) {
            return PRIVATE;
        } else if (getStatus() != null && getStatus().equalsIgnoreCase("Pending")) {
            if ("invite".equalsIgnoreCase(getType()))
                return ACCEPT_INVITATION;
            else return PENDING;
        } else if (getStatus() != null && getStatus().equalsIgnoreCase("Rejected")) {
            return REJECTED;
        } else
            return JOIN;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public String getPropic() {
        return propic;
    }

    public Boolean getIsRemoved() {
        return isRemoved;
    }

    public void setIsRemoved(Boolean isRemoved) {
        this.isRemoved = isRemoved;
    }

    public String getLogo() {
        return logo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getDevSelected() {
        return isDevSelected;
    }

    public void setDevSelected(Boolean devSelected) {
        isDevSelected = devSelected;
    }

    public int getNewUserFamilyJoiningStatus() {
        if (getIsJoined() != null && getIsJoined().equalsIgnoreCase("1")) {
            return JOINED;
        } else if (getStatus() != null && getStatus().equalsIgnoreCase("Pending")) {
            return PENDING;
        } else if (getStatus() != null && getStatus().equalsIgnoreCase("Rejected")) {
            return JOIN;
        } else
            return JOIN;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getFromId() {
        return fromId;
    }
}