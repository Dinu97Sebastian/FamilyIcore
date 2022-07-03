package com.familheey.app.Models.Response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LinkfamilyList {

    boolean isChecked;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("group_name")
    @Expose
    private String groupName;
    @SerializedName("logo")
    @Expose
    private String logo;
    @SerializedName("is_active")
    @Expose
    private Boolean isActive;
    @SerializedName("searchable")
    @Expose
    private Boolean searchable;
    @SerializedName("is_linkable")
    @Expose
    private Boolean isLinkable;
    @SerializedName("link_family")
    @Expose
    private Integer linkFamily;
    @SerializedName("link_approval")
    @Expose
    private Integer linkApproval;
    @SerializedName("post_visibilty")
    @Expose
    private Integer postVisibilty;
    @SerializedName("post_approval")
    @Expose
    private Integer postApproval;
    @SerializedName("post_create")
    @Expose
    private Integer postCreate;
    @SerializedName("member_approval")
    @Expose
    private Integer memberApproval;
    @SerializedName("member_joining")
    @Expose
    private Integer memberJoining;
    @SerializedName("is_linked")
    @Expose
    private String isLinked;

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
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

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public Boolean getIsActive() {
        return isActive;
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

    public Boolean getIsLinkable() {
        return isLinkable;
    }

    public void setIsLinkable(Boolean isLinkable) {
        this.isLinkable = isLinkable;
    }

    public Integer getLinkFamily() {
        return linkFamily;
    }

    public void setLinkFamily(Integer linkFamily) {
        this.linkFamily = linkFamily;
    }

    public Integer getLinkApproval() {
        return linkApproval;
    }

    public void setLinkApproval(Integer linkApproval) {
        this.linkApproval = linkApproval;
    }

    public Integer getPostVisibilty() {
        return postVisibilty;
    }

    public void setPostVisibilty(Integer postVisibilty) {
        this.postVisibilty = postVisibilty;
    }

    public Integer getPostApproval() {
        return postApproval;
    }

    public void setPostApproval(Integer postApproval) {
        this.postApproval = postApproval;
    }

    public Integer getPostCreate() {
        return postCreate;
    }

    public void setPostCreate(Integer postCreate) {
        this.postCreate = postCreate;
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

    public String getIsLinked() {
        return isLinked;
    }

    public void setIsLinked(String isLinked) {
        this.isLinked = isLinked;
    }

}