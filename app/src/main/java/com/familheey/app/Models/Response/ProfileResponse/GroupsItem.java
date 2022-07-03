package com.familheey.app.Models.Response.ProfileResponse;

import com.google.gson.annotations.SerializedName;

public class GroupsItem {

    @SerializedName("link_family")
    private int linkFamily;

    @SerializedName("is_active")
    private boolean isActive;

    @SerializedName("group_name")
    private String groupName;

    @SerializedName("post_approval")
    private int postApproval;

    @SerializedName("is_linkable")
    private boolean isLinkable;

    @SerializedName("link_approval")
    private int linkApproval;

    @SerializedName("searchable")
    private boolean searchable;

    @SerializedName("logo")
    private String logo;

    @SerializedName("post_create")
    private int postCreate;

    @SerializedName("member_approval")
    private int memberApproval;

    @SerializedName("id")
    private int id;

    @SerializedName("post_visibilty")
    private int postVisibilty;

    @SerializedName("member_joining")
    private int memberJoining;

    public int getLinkFamily() {
        return linkFamily;
    }

    public void setLinkFamily(int linkFamily) {
        this.linkFamily = linkFamily;
    }

    public boolean isIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getPostApproval() {
        return postApproval;
    }

    public void setPostApproval(int postApproval) {
        this.postApproval = postApproval;
    }

    public boolean isIsLinkable() {
        return isLinkable;
    }

    public void setIsLinkable(boolean isLinkable) {
        this.isLinkable = isLinkable;
    }

    public int getLinkApproval() {
        return linkApproval;
    }

    public void setLinkApproval(int linkApproval) {
        this.linkApproval = linkApproval;
    }

    public boolean isSearchable() {
        return searchable;
    }

    public void setSearchable(boolean searchable) {
        this.searchable = searchable;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public int getPostCreate() {
        return postCreate;
    }

    public void setPostCreate(int postCreate) {
        this.postCreate = postCreate;
    }

    public int getMemberApproval() {
        return memberApproval;
    }

    public void setMemberApproval(int memberApproval) {
        this.memberApproval = memberApproval;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPostVisibilty() {
        return postVisibilty;
    }

    public void setPostVisibilty(int postVisibilty) {
        this.postVisibilty = postVisibilty;
    }

    public int getMemberJoining() {
        return memberJoining;
    }

    public void setMemberJoining(int memberJoining) {
        this.memberJoining = memberJoining;
    }

    @Override
    public String toString() {
        return
                "GroupsItem{" +
                        "link_family = '" + linkFamily + '\'' +
                        ",is_active = '" + isActive + '\'' +
                        ",group_name = '" + groupName + '\'' +
                        ",post_approval = '" + postApproval + '\'' +
                        ",is_linkable = '" + isLinkable + '\'' +
                        ",link_approval = '" + linkApproval + '\'' +
                        ",searchable = '" + searchable + '\'' +
                        ",logo = '" + logo + '\'' +
                        ",post_create = '" + postCreate + '\'' +
                        ",member_approval = '" + memberApproval + '\'' +
                        ",id = '" + id + '\'' +
                        ",post_visibilty = '" + postVisibilty + '\'' +
                        ",member_joining = '" + memberJoining + '\'' +
                        "}";
    }
}