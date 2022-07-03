package com.familheey.app.Models.Request;

import com.familheey.app.Models.Response.SelectFamilys;
import com.familheey.app.Need.Item_need;
import com.familheey.app.Need.User;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class PostRequest {

    @SerializedName("publish_mention_items")
    @Expose
    private ArrayList<Item_need> publish_mention_items;

    @SerializedName("publish_type")
    @Expose
    private String publish_type;


    @SerializedName("publish_id")
    @Expose
    private String publish_id;

    @SerializedName("publish_mention_users")
    @Expose
    private ArrayList<com.familheey.app.Need.User> publish_mention_users;


    @SerializedName("type_id")
    @Expose
    private String type_id;

    @SerializedName("post_ref_id")
    @Expose
    private String post_ref_id;

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("category_id")
    @Expose
    private String category_id;

    @SerializedName("created_by")
    @Expose
    private String created_by;

    @SerializedName("snap_description")
    @Expose
    private String snap_description;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("type")
    @Expose
    private String type;

    @SerializedName("selected_groups")
    @Expose
    private ArrayList<SelectFamilys> selected_groups;

    @SerializedName("selected_users")
    @Expose
    private ArrayList<String> selected_users;

    @SerializedName("post_attachment")
    @Expose
    private ArrayList<HistoryImages> post_attachment;

    @SerializedName("post_type")
    @Expose
    private String post_type;

    @SerializedName("privacy_type")
    @Expose
    private String privacy_type;

    @SerializedName("post_info")
    @Expose
    private PostInfo post_info;

    @SerializedName("is_shareable")
    @Expose
    private Boolean is_shareable;

    @SerializedName("conversation_enabled")
    @Expose
    private Boolean conversation_enabled;
    //
    @SerializedName("rating_enabled")
    @Expose
    private Boolean rating_enabled;
    //

    @SerializedName("delete_post")
    @Expose
    private List<Integer> deletedFamily = null;

    @SerializedName("to_group_id_array")
    @Expose
    private List<SelectFamilys> to_group_id_array = null;


    @SerializedName("inactive_active_array")
    @Expose
    private List<String> inactive_active_array ;

    @SerializedName("update_type")
    @Expose
    private String updateType;

    @SerializedName("is_active")
    @Expose
    private Boolean is_active;

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public String getSnap_description() {
        return snap_description;
    }

    public void setSnap_description(String snap_description) {
        this.snap_description = snap_description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<SelectFamilys> getSelected_groups() {
        return selected_groups;
    }

    public void setSelected_groups(ArrayList<SelectFamilys> selected_groups) {
        this.selected_groups = selected_groups;
    }

    public ArrayList<String> getSelected_users() {
        return selected_users;
    }

    public void setSelected_users(ArrayList<String> selected_users) {
        this.selected_users = selected_users;
    }

    public ArrayList<HistoryImages> getPost_attachment() {
        return post_attachment;
    }

    public void setPost_attachment(ArrayList<HistoryImages> post_attachment) {
        this.post_attachment = post_attachment;
    }

    public String getPost_type() {
        return post_type;
    }

    public void setPost_type(String post_type) {
        this.post_type = post_type;
    }

    public String getPrivacy_type() {
        return privacy_type;
    }

    public void setPrivacy_type(String privacy_type) {
        this.privacy_type = privacy_type;
    }

    public PostInfo getPost_info() {
        return post_info;
    }

    public void setPost_info(PostInfo post_info) {
        this.post_info = post_info;
    }

    public Boolean getIs_shareable() {
        return is_shareable;
    }

    public void setIs_shareable(Boolean is_shareable) {
        this.is_shareable = is_shareable;
    }

    public Boolean getConversation_enabled() {
        return conversation_enabled;
    }

    public void setConversation_enabled(Boolean conversation_enabled) {
        this.conversation_enabled = conversation_enabled;
    }

    //
    public Boolean getRating_enabled(){return rating_enabled;}
    public void setRating_enabled(Boolean  rating_enabled){
        this.rating_enabled = rating_enabled;
    }
    //

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPost_ref_id() {
        return post_ref_id;
    }

    public void setPost_ref_id(String post_ref_id) {
        this.post_ref_id = post_ref_id;
    }

    public List<Integer> getDeletedFamily() {
        return deletedFamily;
    }

    public void setDeletedFamily(List<Integer> deletedFamily) {
        this.deletedFamily = deletedFamily;
    }

    public String getUpdateType() {
        return updateType;
    }

    public void setUpdateType(String updateType) {
        this.updateType = updateType;
    }

    public List<SelectFamilys> getTo_group_id_array() {
        return to_group_id_array;
    }

    public void setTo_group_id_array(List<SelectFamilys> to_group_id_array) {
        this.to_group_id_array = to_group_id_array;
    }

    public List<String> getInactive_active_array() {
        return inactive_active_array;
    }

    public void setInactive_active_array(List<String> inactive_active_array) {
        this.inactive_active_array = inactive_active_array;
    }

    public void setPublish_mention_items(ArrayList<Item_need> publish_mention_items) {
        this.publish_mention_items = publish_mention_items;
    }

    public String getType_id() {
        return type_id;
    }

    public void setType_id(String type_id) {
        this.type_id = type_id;
    }

    public String getPublish_type() {
        return publish_type;
    }

    public void setPublish_type(String publish_type) {
        this.publish_type = publish_type;
    }

    public String getPublish_id() {
        return publish_id;
    }

    public void setPublish_id(String publish_id) {
        this.publish_id = publish_id;
    }

    public ArrayList<User> getPublish_mention_users() {
        return publish_mention_users;
    }

    public void setPublish_mention_users(ArrayList<com.familheey.app.Need.User> publish_mention_users) {
        this.publish_mention_users = publish_mention_users;
    }

    public Boolean getIs_active() {
        return is_active;
    }

    public void setIs_active(Boolean is_active) {
        this.is_active = is_active;
    }
}
