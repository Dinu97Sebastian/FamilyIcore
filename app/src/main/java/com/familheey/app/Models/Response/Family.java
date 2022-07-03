package com.familheey.app.Models.Response;

import android.os.Parcel;
import android.os.Parcelable;

import com.familheey.app.Activities.OtherUsersFamilyActivity;
import com.familheey.app.Models.Request.HistoryImages;
import com.familheey.app.Utilities.Constants;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import static com.familheey.app.Utilities.Constants.ApiPaths.IMAGE_BASE_URL;

public class Family implements Parcelable {

    public static final int JOINED = 0;
    public static final int JOIN = 1;
    public static final int PENDING = 2;
    public static final int REJECTED = 3;
    public static final int PRIVATE = 4;
    public static final int ACCEPT_INVITATION = 5;

    @SerializedName("membership_total_payed_amount")
    @Expose
    private String membership_total_payed_amount;


    @SerializedName("membership_payment_notes")
    @Expose
    private String membership_payment_notes;
    @SerializedName("membership_customer_notes")
    @Expose
    private String membership_customer_notes;

    @SerializedName("membership_payment_status")
    @Expose
    private String membership_payment_status;

    @SerializedName("membership_from")
    @Expose
    private String membership_from;

    @SerializedName("group_map_id")
    @Expose
    private String group_map_id;
    @SerializedName("membership_fees")
    @Expose
    private String membership_fees;

    @SerializedName("stripe_account_id")
    @Expose
    private String stripe_account_id;
    @SerializedName("full_name")
    @Expose
    private String full_name;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("group_name")
    @Expose
    private String groupName;
    @SerializedName("group_type")
    @Expose
    private String groupType;
    @SerializedName("logo")
    @Expose
    private String logo;

    @SerializedName("cover_pic")
    @Expose
    private String cover_pic;

    @SerializedName("base_region")
    @Expose
    private String baseRegion;

    @SerializedName("created_by")
    @Expose
    private String createdBy;


    @SerializedName("membership_to")
    @Expose
    private String membership_to;


    @SerializedName("membership_period_type_id")
    @Expose
    private int membership_period_type_id;

    @SerializedName("membership_type")
    @Expose
    private String membership_type;

    @SerializedName("membership_period_type")
    @Expose
    private String membership_period_type;

    @SerializedName("group_level")
    @Expose
    private Object groupLevel;
    @SerializedName("intro")
    @Expose
    private String intro;
    @SerializedName("is_active")
    @Expose
    private Boolean isActive;

    @SerializedName("is_membership")
    @Expose
    private Boolean is_membership;
    @SerializedName("searchable")
    @Expose
    private Boolean searchable;
    @SerializedName("visibility")
    @Expose
    private Boolean visibility;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("member_joining")
    @Expose
    private String memberJoining;
    @SerializedName("pending_request_count")
    @Expose
    private String requestCount;
    @SerializedName("member_approval")
    @Expose
    private Integer memberApproval;
    @SerializedName("post_create")
    @Expose
    private String postCreate;
    @SerializedName("post_approval")
    @Expose
    private String postApproval;
    @SerializedName("post_visibilty")
    @Expose
    private String postVisibilty;


    @SerializedName("request_visibility")
    @Expose
    private String request_visibility;

    @SerializedName("link_approval")
    @Expose
    private String linkApproval;
    @SerializedName("group_category")
    @Expose
    private String groupCategory;
    @SerializedName("link_family")
    @Expose
    private String linkFamily;
    @SerializedName("is_linkable")
    @Expose
    private Boolean isLinkable;
    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("group_id")
    @Expose
    private Integer groupId;
    @SerializedName("following")
    @Expose
    private Boolean following;
    @SerializedName("is_blocked")
    @Expose
    private Boolean isBlocked;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("is_linked")
    @Expose
    private String isLinked;
    @SerializedName("is_joined")
    @Expose
    private Boolean isJoined;
    @SerializedName("user_status")
    @Expose
    private String userStatus;
    @SerializedName(value = "member_count", alternate = {"membercount"})
    @Expose
    private String membersCount;
    @SerializedName(value = "new_events_count", alternate = {"events_count"})
    @Expose
    private String eventsCount;
    @SerializedName("JOINED")
    @Expose
    private Boolean hasUserJoinedAdminsFamily;
    @SerializedName("requested_by_name")
    @Expose
    private String requestedByName;
    @SerializedName("created_by_name")
    @Expose
    private String createdByName;
    @SerializedName("is_primar_admin")
    @Expose
    private String isPrimaryAdmin;
    @SerializedName("knowncount")
    @Expose
    private String knownCount;
    @SerializedName("created_by_id")
    @Expose
    private String createdById;
    @SerializedName("is_removed")
    @Expose
    private String isRemoved;
    @SerializedName("history_pic")
    @Expose
    private String historyPic;
    @SerializedName("group_original_image")
    @Expose
    private String groupOriginalImage;
    @SerializedName("history_text")
    @Expose
    private String historyText;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("user_type")
    @Expose
    private String userType;
    @SerializedName("history_images")
    @Expose
    private ArrayList<HistoryImages> history_images = new ArrayList<>();
    @SerializedName("joined_status")
    @Expose
    private String joinedStatus;
    @SerializedName("post_count")
    @Expose
    private String postCount;
    @SerializedName("f_text")
    @Expose
    private String urlPathOnly;

    @SerializedName("f_link")
    @Expose
    private String urlPath;

    @SerializedName("fweb_link")
    @Expose
    private String webUrlPath;





    @SerializedName("is_shared")
    @Expose
    private Boolean isShared = false;
    @SerializedName("rate_all_post")
    @Expose
    private Boolean isRated = false;
    @SerializedName("from_group")
    @Expose
    private Integer fromGroup;
    @SerializedName("to_group")
    @Expose
    private Integer toGroup;
    @SerializedName("req_status")
    @Expose
    private String requestStatus;
    @SerializedName("announcement_create")
    @Expose
    private String announcementCreate = Constants.FamilySettings.AnnouncementCreate.ADMIN;
    @SerializedName("announcement_visibility")
    @Expose
    private String announcementVisibility = Constants.FamilySettings.AnnouncementVisibility.MEMBERS_ONLY;
    @SerializedName("announcement_approval")
    @Expose
    private String announcementApproval;
    @SerializedName("created_by_")
    @Expose
    private String similar_family_created_by;
    @SerializedName("invitation_status")
    @Expose
    private Boolean invitationStatus;
    // Dinu(03-03-2021)-> added logined_user for check admin or member
    @SerializedName("logined_user")
    @Expose
    private String logined_user;
    // Dinu(24-06-2021)-> added  for accept invitation
    @SerializedName("reqId")
    @Expose
    private String reqId;
    @SerializedName("fromId")
    @Expose
    private String fromId;

    @SerializedName("sticky_scroll_time")
    @Expose
    private int sticky_scroll_time;

    @SerializedName("sticky_auto_scroll")
    @Expose
    private boolean sticky_auto_scroll;



    @SerializedName("sticky_post_limit")
    @Expose
    private int sticky_post_limit;


    private boolean isDevSelected = false;
    private boolean addedTogroup = false;
    private boolean invited = false;

    public Family() {
    }

    protected Family(Parcel in) {
        membership_total_payed_amount = in.readString();
        membership_payment_notes = in.readString();
        membership_customer_notes = in.readString();
        membership_payment_status = in.readString();
        membership_from = in.readString();
        group_map_id = in.readString();
        membership_fees = in.readString();
        stripe_account_id = in.readString();
        full_name = in.readString();
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        membership_to = in.readString();
        groupName = in.readString();
        groupType = in.readString();
        logo = in.readString();
        cover_pic = in.readString();
        logined_user = in.readString();
        fromId = in.readString();
        reqId = in.readString();
        membership_type= in.readString();
        baseRegion = in.readString();
        createdBy = in.readString();
        intro = in.readString();
        request_visibility = in.readString();

        byte tmpIsActive = in.readByte();
        isActive = tmpIsActive == 0 ? null : tmpIsActive == 1;
        byte tmpis_membership = in.readByte();
        is_membership = tmpis_membership == 0 ? null : tmpis_membership == 1;
        byte tmpSearchable = in.readByte();
        searchable = tmpSearchable == 0 ? null : tmpSearchable == 1;
        byte tmpVisibility = in.readByte();
        visibility = tmpVisibility == 0 ? null : tmpVisibility == 1;
        createdAt = in.readString();
        updatedAt = in.readString();
        memberJoining = in.readString();
        requestCount = in.readString();
        if (in.readByte() == 0) {
            memberApproval = null;
        } else {
            memberApproval = in.readInt();
        }
        postCreate = in.readString();
        postApproval = in.readString();
        postVisibilty = in.readString();
        linkApproval = in.readString();
        groupCategory = in.readString();
        linkFamily = in.readString();
        byte tmpIsLinkable = in.readByte();
        isLinkable = tmpIsLinkable == 0 ? null : tmpIsLinkable == 1;
        if (in.readByte() == 0) {
            userId = null;
        } else {
            userId = in.readInt();
        }
        if (in.readByte() == 0) {
            groupId = null;
        } else {
            groupId = in.readInt();
        }
        byte tmpFollowing = in.readByte();
        following = tmpFollowing == 0 ? null : tmpFollowing == 1;
        byte tmpIsBlocked = in.readByte();
        isBlocked = tmpIsBlocked == 0 ? null : tmpIsBlocked == 1;
        type = in.readString();
        isLinked = in.readString();

        byte tmpIsJoined = in.readByte();
        isJoined = tmpIsJoined == 0 ? null : tmpIsJoined == 1;
        userStatus = in.readString();
        membersCount = in.readString();
        eventsCount = in.readString();
        byte tmpHasUserJoinedAdminsFamily = in.readByte();
        hasUserJoinedAdminsFamily = tmpHasUserJoinedAdminsFamily == 0 ? null : tmpHasUserJoinedAdminsFamily == 1;
        requestedByName = in.readString();
        createdByName = in.readString();
        isPrimaryAdmin = in.readString();
        knownCount = in.readString();
        createdById = in.readString();
        isRemoved = in.readString();
        historyPic = in.readString();
        groupOriginalImage = in.readString();
        historyText = in.readString();
        status = in.readString();
        userType = in.readString();
        history_images = in.createTypedArrayList(HistoryImages.CREATOR);
        joinedStatus = in.readString();
        postCount = in.readString();
        urlPathOnly = in.readString();
        urlPath = in.readString();

        //04-03
        webUrlPath = in.readString();
        byte tmpIsShared = in.readByte();
        isShared = tmpIsShared == 0 ? null : tmpIsShared == 1;
        byte tmpIsRated = in.readByte();
        isRated = tmpIsRated == 0 ? null : tmpIsRated == 1;
        if (in.readByte() == 0) {
            fromGroup = null;
        } else {
            fromGroup = in.readInt();
        }
        if (in.readByte() == 0) {
            toGroup = null;
        } else {
            toGroup = in.readInt();
        }
        requestStatus = in.readString();
        announcementCreate = in.readString();
        announcementVisibility = in.readString();
        announcementApproval = in.readString();
        similar_family_created_by = in.readString();

        byte tmpInvitationStatus = in.readByte();
        invitationStatus = tmpInvitationStatus == 0 ? null : tmpInvitationStatus == 1;
        isDevSelected = in.readByte() != 0;
        addedTogroup = in.readByte() != 0;
        invited = in.readByte() != 0;
        sticky_auto_scroll=in.readByte()!=0;
        sticky_scroll_time=in.readInt();
        sticky_post_limit=in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(membership_total_payed_amount);
        dest.writeString(membership_payment_notes);
        dest.writeString(membership_customer_notes);
        dest.writeString(membership_payment_status);
        dest.writeString(membership_from);
        dest.writeString(group_map_id);
        dest.writeString(membership_fees);
        dest.writeString(stripe_account_id);
        dest.writeString(full_name);
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(id);
        }
        dest.writeString(membership_to);
        dest.writeString(groupName);
        dest.writeString(logined_user);
        dest.writeString(fromId);
        dest.writeString(reqId);
        dest.writeString(groupType);
        dest.writeString(logo);
        dest.writeString(cover_pic);
        dest.writeString(membership_type);
        dest.writeString(baseRegion);
        dest.writeString(createdBy);
        dest.writeString(intro);
        dest.writeString(request_visibility);
        dest.writeByte((byte) (isActive == null ? 0 : isActive ? 1 : 2));
        dest.writeByte((byte) (is_membership == null ? 0 : is_membership ? 1 : 2));
        dest.writeByte((byte) (searchable == null ? 0 : searchable ? 1 : 2));
        dest.writeByte((byte) (visibility == null ? 0 : visibility ? 1 : 2));
        dest.writeString(createdAt);
        dest.writeString(updatedAt);
        dest.writeString(memberJoining);
        dest.writeString(requestCount);
        if (memberApproval == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(memberApproval);
        }

        dest.writeString(postCreate);
        dest.writeString(postApproval);
        dest.writeString(postVisibilty);
        dest.writeString(linkApproval);
        dest.writeString(groupCategory);
        dest.writeString(linkFamily);
        dest.writeByte((byte) (isLinkable == null ? 0 : isLinkable ? 1 : 2));
        if (userId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(userId);
        }
        if (groupId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(groupId);
        }

        dest.writeByte((byte) (following == null ? 0 : following ? 1 : 2));
        dest.writeByte((byte) (isBlocked == null ? 0 : isBlocked ? 1 : 2));
        dest.writeString(type);
        dest.writeString(isLinked);
        dest.writeByte((byte) (isJoined == null ? 0 : isJoined ? 1 : 2));
        dest.writeString(userStatus);
        dest.writeString(membersCount);
        dest.writeString(eventsCount);
        dest.writeByte((byte) (hasUserJoinedAdminsFamily == null ? 0 : hasUserJoinedAdminsFamily ? 1 : 2));
        dest.writeString(requestedByName);
        dest.writeString(createdByName);
        dest.writeString(isPrimaryAdmin);
        dest.writeString(knownCount);
        dest.writeString(createdById);
        dest.writeString(isRemoved);
        dest.writeString(historyPic);
        dest.writeString(groupOriginalImage);
        dest.writeString(historyText);
        dest.writeString(status);
        dest.writeString(userType);
        dest.writeTypedList(history_images);
        dest.writeString(joinedStatus);
        dest.writeString(postCount);
        dest.writeString(urlPathOnly);
        dest.writeString(urlPath);

        //04-03
        dest.writeString(webUrlPath);
        dest.writeByte((byte) (isShared == null ? 0 : isShared ? 1 : 2));
        dest.writeByte((byte) (isRated == null ? 0 : isRated ? 1 : 2));
        if (fromGroup == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(fromGroup);
        }
        if (toGroup == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(toGroup);
        }

        dest.writeString(requestStatus);
        dest.writeString(announcementCreate);
        dest.writeString(announcementVisibility);
        dest.writeString(announcementApproval);


        dest.writeString(similar_family_created_by);
        dest.writeByte((byte) (invitationStatus == null ? 0 : invitationStatus ? 1 : 2));
        dest.writeByte((byte) (isDevSelected ? 1 : 0));
        dest.writeByte((byte) (addedTogroup ? 1 : 0));
        dest.writeByte((byte) (invited ? 1 : 0));
        dest.writeByte((byte) (sticky_auto_scroll ? 1 : 0));
        dest.writeInt(sticky_scroll_time);
        dest.writeInt(sticky_post_limit);

    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Family> CREATOR = new Creator<Family>() {
        @Override
        public Family createFromParcel(Parcel in) {
            return new Family(in);
        }

        @Override
        public Family[] newArray(int size) {
            return new Family[size];
        }
    };

    public boolean isInvited() {
        return invited;
    }

    public void setInvited(boolean invited) {
        this.invited = invited;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupType() {
        return groupType;
    }

    public boolean isPublic() {
        return (getGroupType() != null && getGroupType().equalsIgnoreCase("public"));
    }

    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getCoverPic() {
        return cover_pic;
    }

    public void setCoverPic(String coverPic) {
        this.cover_pic = coverPic;
    }

    public String getBaseRegion() {
        return baseRegion;
    }

    public String getMembership_customer_notes() {
        return membership_customer_notes;
    }

    public void setBaseRegion(String baseRegion) {
        this.baseRegion = baseRegion;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getMembership_from() {
        return membership_from;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Object getGroupLevel() {
        return groupLevel;
    }

    public void setGroupLevel(Object groupLevel) {
        this.groupLevel = groupLevel;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getMembership_total_payed_amount() {
        return membership_total_payed_amount;
    }


    public Boolean getIsActive() {
        return isActive;
    }

    public String getGroup_map_id() {
        return group_map_id;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Boolean getSearchable() {
        return searchable;
    }

    public void setSearchable(Boolean searchable) {
        this.searchable = searchable;
    }

    public Boolean getVisibility() {
        return visibility;
    }

    public void setVisibility(Boolean visibility) {
        this.visibility = visibility;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getMemberJoining() {
        return memberJoining;
    }
    public String getRequestCount(){
        return requestCount;
    }

    public void setMemberJoining(String memberJoining) {
        this.memberJoining = memberJoining;
    }
    public void setRequestCount(String requestCount) {
        this.requestCount = requestCount;
    }

    public Integer getMemberApproval() {
        return memberApproval;
    }

    public void setMemberApproval(Integer memberApproval) {
        this.memberApproval = memberApproval;
    }

    public String getPostCreate() {
        return postCreate;
    }

    public void setPostCreate(String postCreate) {
        this.postCreate = postCreate;
    }

    public String getMembership_type() {
        return membership_type;
    }

    public String getPostApproval() {
        return postApproval;
    }

    public void setPostApproval(String postApproval) {
        this.postApproval = postApproval;
    }

    public String getPostVisibilty() {
        return postVisibilty;
    }

    public void setPostVisibilty(String postVisibilty) {
        this.postVisibilty = postVisibilty;
    }

    public String getLinkApproval() {
        return linkApproval;
    }

    public void setLinkApproval(String linkApproval) {
        this.linkApproval = linkApproval;
    }

    public String getRequest_visibility() {
        return request_visibility;
    }

    public String getGroupCategory() {
        return groupCategory;
    }

    public void setGroupCategory(String groupCategory) {
        this.groupCategory = groupCategory;
    }

    public String getLinkFamily() {
        return linkFamily;
    }

    public void setLinkFamily(String linkFamily) {
        this.linkFamily = linkFamily;
    }

    public Boolean getIsLinkable() {
        return isLinkable;
    }

    public void setIsLinkable(Boolean isLinkable) {
        this.isLinkable = isLinkable;
    }

    public Integer getUserId() {
        return userId;
    }

    public String getMembership_to() {
        return membership_to;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public String getMembership_payment_notes() {
        return membership_payment_notes;
    }

    public Boolean getFollowing() {
        return following;
    }

    public void setFollowing(Boolean following) {
        this.following = following;
    }

    public Boolean getIsBlocked() {
        return isBlocked;
    }

    public void setIsBlocked(Boolean isBlocked) {
        this.isBlocked = isBlocked;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIsLinked() {
        return isLinked;
    }

    public void setIsLinked(String isLinked) {
        this.isLinked = isLinked;
    }

    public boolean isDevSelected() {
        return isDevSelected;
    }

    public void setDevSelected(boolean devSelected) {
        isDevSelected = devSelected;
    }

    public boolean isAddedTogroup() {
        return addedTogroup;
    }

    public void setAddedTogroup(boolean addedTogroup) {
        this.addedTogroup = addedTogroup;
    }

    public String getUserStatus() {
        return userStatus;
    }
    //geter and setter ->Dinu
    public String getLogined_user() {
        return logined_user;
    }
    public String getFromId() {
        return fromId;
    }
    public String getReqId() {
        return reqId;
    }

    public void setLogined_user(String logined_user) {
        this.logined_user = logined_user;
    }
    public void setReqId(String reqId) { this.reqId = reqId; }
    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

    public String getMembersCount() {
        return membersCount;
    }
    public void setMembersCount(String membersCount) {
        this.membersCount=membersCount;
    }
    public void setKnownCount(String knownCount) {
        this.knownCount=knownCount;
    }

    public String getEventsCount() {
        return eventsCount;
    }

    public Boolean getHasUserJoinedAdminsFamily() {
        return hasUserJoinedAdminsFamily;
    }

    public void setHasUserJoinedAdminsFamily(Boolean hasUserJoinedAdminsFamily) {
        this.hasUserJoinedAdminsFamily = hasUserJoinedAdminsFamily;
    }

    public int getMembership_period_type_id() {
        return membership_period_type_id;
    }

    public String getRequestedByName() {
        return requestedByName;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public boolean isRegularGroup() {
        if (getGroupType() == null)
            return true;
        else
            return getGroupCategory().equalsIgnoreCase("regular") || getGroupCategory().equalsIgnoreCase("regular family") || getGroupCategory().equalsIgnoreCase("regularfamily");
    }

    public boolean isUserInThisFamily() {
        if (getUserStatus() == null)
            return false;
        return !getUserStatus().equalsIgnoreCase("not-member");
    }

    public String getIsPrimaryAdmin() {
        return isPrimaryAdmin;
    }

    public Boolean getIsJoined() {
        return isJoined;
    }

    public String getKnownCount() {
        return knownCount;
    }

    public String getCreatedById() {
        return createdById;
    }

    public String getIsRemoved() {
        return isRemoved;
    }

    public String getHistoryPic() {
        return historyPic;
    }

    public String getGroupOriginalImage() {
        return groupOriginalImage;
    }

    public ArrayList<com.familheey.app.Models.Request.HistoryImages> getHistoryImages() {
        return history_images;
    }

    public void setHistoryImages(ArrayList<HistoryImages> historyImages) {
        this.history_images = historyImages;
    }

    public String getHistoryText() {
        return historyText;
    }

    public void setHistoryText(String historyText) {
        this.historyText = historyText;
    }

    public String getMembership_payment_status() {
        return membership_payment_status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setIsJoined(Boolean isJoined) {
        this.isJoined = isJoined;
    }

    public void setIsRemoved(String isRemoved) {
        this.isRemoved = isRemoved;
    }

    public String getUserType() {
        return userType;
    }

    public String getJoinedStatus() {
        return joinedStatus;
    }
    public void setJoinedStatus(String joinedStatus){this.joinedStatus=joinedStatus;}

    public String getUrlPathOnly() {
        return urlPathOnly;
    }

    public void setUrlPathOnly(String urlPathOnly) {
        this.urlPathOnly = urlPathOnly;
    }

    public String getUrlPath() {
        return urlPath;
    }

    public void setUrlPath(String urlPath) {
        this.urlPath = urlPath;
    }

//04-03
public String getWebUrlPath() {
    return webUrlPath;
}

    public void setWebUrlPath(String webUrlPath) {
        this.webUrlPath = webUrlPath;
    }



    public String getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(String requestStatus) {
        this.requestStatus = requestStatus;
    }

    public int getUserJoinedCalculated() {
        if (getHasUserJoinedAdminsFamily() != null && getHasUserJoinedAdminsFamily()) {
            return JOINED;
        } else if (getMemberJoining() != null && getMemberJoining().equalsIgnoreCase("1")) {
            return PRIVATE;
        } else if (getStatus() != null && getStatus().equalsIgnoreCase("Pending")) {
            if ("invite".equalsIgnoreCase(getType()))
                return ACCEPT_INVITATION;
            else
                return PENDING;
        } else if (getStatus() != null && getStatus().equalsIgnoreCase("Rejected")) {
            return REJECTED;
        } else
            return JOIN;
    }

    public String getMembership_fees() {
        return membership_fees;
    }

    public String getMembership_period_type() {
        return membership_period_type;
    }

    public ArrayList<String> getHistoryImagesUrls() {
        ArrayList<String> historyImageUrls = new ArrayList<>();
        for (HistoryImages historyImage : getHistoryImages()) {
            historyImageUrls.add(IMAGE_BASE_URL + "history_images/" + historyImage.getFilename());
        }
        return historyImageUrls;
    }

    public boolean isAdmin() {
        try {
            if (getUserStatus() == null)
                return false;
            return getUserStatus() != null && getUserStatus().equalsIgnoreCase("admin");
        } catch (Exception e) {
            return false;
        }
    }

    public boolean canLinkFamily() {
        if (isLinkable != null && isLinkable) {
            if (isAdmin())
                return true;
            else
                return getUserStatus() != null && getUserStatus().equalsIgnoreCase("member") && getLinkFamily().equalsIgnoreCase(Constants.FamilySettings.LinkFamily.MEMBER);
        }
        return false;
        /*if (isAdmin())
            return true;
        else
            return getLinkFamily() != null && getUserStatus() != null && getUserStatus().equalsIgnoreCase("member") && getLinkFamily().equalsIgnoreCase(Constants.FamilySettings.LinkFamily.MEMBER);*/
    }

    public String getStripe_account_id() {
        return stripe_account_id;
    }

    public void setStripe_account_id(String stripe_account_id) {
        this.stripe_account_id = stripe_account_id;
    }

    public boolean whoCanApproveMemberRequest() {
        if (isAdmin()) {
            return true;
        } else if (isMember()) {
            return getMemberApproval() != null && memberApproval.equals(5);
        }
        return false;
    }

    public boolean isAnonymous() {
        return !((getUserStatus() != null && getUserStatus().equalsIgnoreCase("admin")) || (getUserStatus() != null && getUserStatus().equalsIgnoreCase("member")));
    }
    /*
     * megha
     * added condition for solving the issue that shows the warning even if the member clicks the family details*/
    public boolean memberNotAnonymous(){
        return (getUserStatus()==null);
    }
    public boolean isMember() {
        return (getUserStatus() != null && getUserStatus().equalsIgnoreCase("member"));
    }

    public Boolean getIs_membership() {
        return is_membership;
    }

    public String getPostCount() {
        return postCount;
    }
public void setPostCount(String postCount){
       this.postCount=postCount;
}
    public Boolean getShared() {
        return isShared;
    }
    public Boolean getIsRated() {
        return isRated;
    }

    public String getMembership_sub_type() {
        return membership_period_type;
    }

    public void setShared(Boolean shared) {
        isShared = shared;
    }
    public void setIsRated(Boolean rated) {
        isRated = rated;
    }

    public Integer getFromGroup() {
        return fromGroup;
    }

    public void setFromGroup(Integer fromGroup) {
        this.fromGroup = fromGroup;
    }

    public Integer getToGroup() {
        return toGroup;
    }

    public void setToGroup(Integer toGroup) {
        this.toGroup = toGroup;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public int getSimilarFamilyJoiningStatus() {
        Boolean joined = getIsJoined();
        String member_joined = getMemberJoining();
        String status = getRequestStatus();

        if (joined != null && joined) {
            return JOINED;
        } else {
            if (member_joined != null && member_joined.equalsIgnoreCase("1")) {
                return PRIVATE;
            } else if (status != null && status.equalsIgnoreCase("pending")) {
                return PENDING;
            } else if (status != null && status.equalsIgnoreCase("rejected")) {
                return REJECTED;
            } else {
                return JOIN;
            }
        }
    }

    public String getAnnouncementCreate() {
        return announcementCreate;
    }

    public String getAnnouncementVisibility() {
        return announcementVisibility;
    }

    public String getAnnouncementApproval() {
        return announcementApproval;
    }

    public String getCreatedByUserUsingType(int type) {
        if (type == OtherUsersFamilyActivity.CONNECTIONS) {
            return getCreatedBy();
        } else {
            return getFull_name();
        }
    }
    public void setCreatedByUserUsingType(String user) {
        this.createdBy=user;
    }
    public String getSharingbasePath() {
        if (getUrlPath() == null || getWebUrlPath() == null)
            return "";
        try {
            return getUrlPath().substring(0, getUrlPath().lastIndexOf("/") + 1);
        } catch (Exception e) {
            e.printStackTrace();
            return getUrlPath();
        }
    }

    public boolean canAcceptMemberInvitations() {
        if (isAdmin())
            return true;
        else if (isMember()) {
            return memberApproval != null && memberApproval.equals(Constants.FamilySettings.AnnouncementApproval.MEMBERS);
        } else return false;
    }

    public boolean isNotMember() {
        return (userType != null && userType.equalsIgnoreCase("not-member"));
    }

    public boolean canCreatePost() {
        if (isNotMember())
            return false;
        if (isAdmin())
            return true;
        else if (isMember()) {
            return getPostCreate() != null && (getPostCreate().equalsIgnoreCase(Constants.FamilySettings.PostCreate.MEMBERS_ONLY));  //getPostCreate().equalsIgnoreCase(Constants.FamilySettings.PostCreate.MEMBERS_ONLY)
        }
        return false;
    }

    public boolean canCreateAnnouncement() {
        if (isNotMember())
            return false;
        if (isAdmin())
            return true;
        else if (isMember()) {
            return getAnnouncementCreate() != null && (getAnnouncementCreate().equalsIgnoreCase(Constants.FamilySettings.AnnouncementCreate.ANY_MEMBER));
        }
        return false;
    }

    public void setPublicDefault() {
        memberJoining = Constants.FamilySettings.MemberJoining.ANYONE_CAN_JOIN;
        memberApproval = Integer.parseInt(Constants.FamilySettings.MemberApproval.ANY_MEMBER);
        postCreate = Constants.FamilySettings.PostCreate.MEMBERS_ONLY;
        postVisibilty = Constants.FamilySettings.PostVisibility.PUBLIC;
        announcementCreate = Constants.FamilySettings.AnnouncementCreate.ADMIN;
        announcementVisibility = Constants.FamilySettings.AnnouncementVisibility.PUBLIC;
        linkFamily = Constants.FamilySettings.LinkFamily.ADMINS;
        linkApproval = Constants.FamilySettings.LinkApproval.ADMIN;

    }


    public void setPrivateDefault() {
        memberJoining = Constants.FamilySettings.MemberJoining.ANYONE_CAN_JOIN_WITH_APPROVAL;
        memberApproval = Integer.parseInt(Constants.FamilySettings.MemberApproval.MEMBER_APPROVAL);
        postCreate = Constants.FamilySettings.PostCreate.MEMBERS_ONLY;
        postVisibilty = Constants.FamilySettings.PostVisibility.MEMBERS_ONLY;
        announcementCreate = Constants.FamilySettings.AnnouncementCreate.ADMIN;
        announcementVisibility = Constants.FamilySettings.AnnouncementVisibility.MEMBERS_ONLY;
        linkFamily = Constants.FamilySettings.LinkFamily.ADMINS;
        linkApproval = Constants.FamilySettings.LinkApproval.ADMIN;

    }

    public String getSimilarFamilyCreatedBy() {
        return similar_family_created_by;
    }

    public Boolean getInvitationStatus() {
        return invitationStatus;
    }

    public int getSticky_scroll_time() {
        return sticky_scroll_time;
    }

    public void setSticky_scroll_time(int sticky_scroll_time) {
        this.sticky_scroll_time = sticky_scroll_time;
    }

    public boolean getSticky_auto_scroll() {
        return sticky_auto_scroll;
    }

    public void setSticky_auto_scroll(boolean sticky_auto_scroll) {
        this.sticky_auto_scroll = sticky_auto_scroll;
    }
    public int getSticky_post_limit() {
        return sticky_post_limit;
    }

    public void setSticky_post_limit(int sticky_post_limit) {
        this.sticky_post_limit = sticky_post_limit;
    }
}