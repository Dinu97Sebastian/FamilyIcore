package com.familheey.app.Models.Response;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FamilyCreationResponse implements Parcelable {

    @SerializedName("is_active")
    @Expose
    private Boolean isActive = null;
    @SerializedName("searchable")
    @Expose
    private Boolean searchable = null;
    @SerializedName("id")
    @Expose
    private Integer id = null;
    @SerializedName("base_region")
    @Expose
    private String baseRegion = null;
    @SerializedName("created_by")
    @Expose
    private Integer createdBy = null;
    @SerializedName("group_category")
    @Expose
    private String groupCategory = null;
    @SerializedName("group_name")
    @Expose
    private String groupName = null;
    @SerializedName("group_type")
    @Expose
    private String groupType = null;
    @SerializedName("logo")
    @Expose
    private String logo = null;
    @SerializedName("cover_pic")
    @Expose
    private String coverPic = null;
    @SerializedName("group_level")
    @Expose
    private String groupLevel = null;
    @SerializedName("intro")
    @Expose
    private String intro = null;
    @SerializedName("visibility")
    @Expose
    private Boolean visibility = null;
    @SerializedName("member_joining")
    @Expose
    private Integer memberJoining = null;
    @SerializedName("member_approval")
    @Expose
    private Integer memberApproval = null;
    @SerializedName("post_create")
    @Expose
    private Integer postCreate = null;
    @SerializedName("post_approval")
    @Expose
    private Integer postApproval = null;
    @SerializedName("post_visibilty")
    @Expose
    private Integer postVisibilty = null;
    @SerializedName("link_approval")
    @Expose
    private Integer linkApproval = null;
    @SerializedName("link_family")
    @Expose
    private Integer linkFamily = null;
    @SerializedName("is_linkable")
    @Expose
    private Boolean isLinkable = null;

    public FamilyCreationResponse() {

    }

    protected FamilyCreationResponse(Parcel in) {
        byte tmpIsActive = in.readByte();
        isActive = tmpIsActive == 0 ? null : tmpIsActive == 1;
        byte tmpSearchable = in.readByte();
        searchable = tmpSearchable == 0 ? null : tmpSearchable == 1;
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        baseRegion = in.readString();
        if (in.readByte() == 0) {
            createdBy = null;
        } else {
            createdBy = in.readInt();
        }
        groupCategory = in.readString();
        groupName = in.readString();
        groupType = in.readString();
        logo = in.readString();
        coverPic = in.readString();
        groupLevel = in.readString();
        intro = in.readString();
        byte tmpVisibility = in.readByte();
        visibility = tmpVisibility == 0 ? null : tmpVisibility == 1;
        if (in.readByte() == 0) {
            memberJoining = null;
        } else {
            memberJoining = in.readInt();
        }
        if (in.readByte() == 0) {
            memberApproval = null;
        } else {
            memberApproval = in.readInt();
        }
        if (in.readByte() == 0) {
            postCreate = null;
        } else {
            postCreate = in.readInt();
        }
        if (in.readByte() == 0) {
            postApproval = null;
        } else {
            postApproval = in.readInt();
        }
        if (in.readByte() == 0) {
            postVisibilty = null;
        } else {
            postVisibilty = in.readInt();
        }
        if (in.readByte() == 0) {
            linkApproval = null;
        } else {
            linkApproval = in.readInt();
        }
        if (in.readByte() == 0) {
            linkFamily = null;
        } else {
            linkFamily = in.readInt();
        }
        byte tmpIsLinkable = in.readByte();
        isLinkable = tmpIsLinkable == 0 ? null : tmpIsLinkable == 1;
    }

    public static final Creator<FamilyCreationResponse> CREATOR = new Creator<FamilyCreationResponse>() {
        @Override
        public FamilyCreationResponse createFromParcel(Parcel in) {
            return new FamilyCreationResponse(in);
        }

        @Override
        public FamilyCreationResponse[] newArray(int size) {
            return new FamilyCreationResponse[size];
        }
    };

    public Boolean getActive() {
        return isActive;
    }

    public Boolean getSearchable() {
        return searchable;
    }

    public Integer getId() {
        return id;
    }

    public String getBaseRegion() {
        return baseRegion;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public String getGroupCategory() {
        return groupCategory;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getGroupType() {
        return groupType;
    }

    public String getLogo() {
        return logo;
    }

    public String getCoverPic() {
        return coverPic;
    }

    public String getGroupLevel() {
        return groupLevel;
    }

    public String getIntro() {
        return intro;
    }

    public Boolean getVisibility() {
        return visibility;
    }

    public Integer getMemberJoining() {
        return memberJoining;
    }

    public Integer getMemberApproval() {
        return memberApproval;
    }

    public Integer getPostCreate() {
        return postCreate;
    }

    public Integer getPostApproval() {
        return postApproval;
    }

    public Integer getPostVisibilty() {
        return postVisibilty;
    }

    public Integer getLinkApproval() {
        return linkApproval;
    }

    public Integer getLinkFamily() {
        return linkFamily;
    }

    public Boolean getLinkable() {
        return isLinkable;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public void setSearchable(Boolean searchable) {
        this.searchable = searchable;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setBaseRegion(String baseRegion) {
        this.baseRegion = baseRegion;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public void setGroupCategory(String groupCategory) {
        this.groupCategory = groupCategory;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public void setCoverPic(String coverPic) {
        this.coverPic = coverPic;
    }

    public void setGroupLevel(String groupLevel) {
        this.groupLevel = groupLevel;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public void setVisibility(Boolean visibility) {
        this.visibility = visibility;
    }

    public void setMemberJoining(Integer memberJoining) {
        this.memberJoining = memberJoining;
    }

    public void setMemberApproval(Integer memberApproval) {
        this.memberApproval = memberApproval;
    }

    public void setPostCreate(Integer postCreate) {
        this.postCreate = postCreate;
    }

    public void setPostApproval(Integer postApproval) {
        this.postApproval = postApproval;
    }

    public void setPostVisibilty(Integer postVisibilty) {
        this.postVisibilty = postVisibilty;
    }

    public void setLinkApproval(Integer linkApproval) {
        this.linkApproval = linkApproval;
    }

    public void setLinkFamily(Integer linkFamily) {
        this.linkFamily = linkFamily;
    }

    public void setLinkable(Boolean linkable) {
        isLinkable = linkable;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (isActive == null ? 0 : isActive ? 1 : 2));
        dest.writeByte((byte) (searchable == null ? 0 : searchable ? 1 : 2));
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(id);
        }
        dest.writeString(baseRegion);
        if (createdBy == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(createdBy);
        }
        dest.writeString(groupCategory);
        dest.writeString(groupName);
        dest.writeString(groupType);
        dest.writeString(logo);
        dest.writeString(coverPic);
        dest.writeString(groupLevel);
        dest.writeString(intro);
        dest.writeByte((byte) (visibility == null ? 0 : visibility ? 1 : 2));
        if (memberJoining == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(memberJoining);
        }
        if (memberApproval == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(memberApproval);
        }
        if (postCreate == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(postCreate);
        }
        if (postApproval == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(postApproval);
        }
        if (postVisibilty == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(postVisibilty);
        }
        if (linkApproval == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(linkApproval);
        }
        if (linkFamily == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(linkFamily);
        }
        dest.writeByte((byte) (isLinkable == null ? 0 : isLinkable ? 1 : 2));
    }
}