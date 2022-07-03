package com.familheey.app.Fragments.Posts;

import android.os.Parcel;
import android.os.Parcelable;

import com.familheey.app.Models.Request.HistoryImages;
import com.familheey.app.Models.Response.SelectFamilys;
import com.familheey.app.Need.Item_need;
import com.familheey.app.Need.User;

import java.util.ArrayList;

public class PostData implements Parcelable {
/*megha()2/09/2021) for rating*/
    public String rating_count;
    public  String rating_by_user;
    public String rating;
    public Boolean rating_enabled;
    public Boolean is_shareable;
    private boolean read_status;
    private int post_id;
    private int id;
    private String selected_family_count;
    private String selected_user_count;
    private String is_viewed;
    private String to_user_name;
    private String group_name;
    private String created_user_name;
    private String propic;
    private String parent_post_created_user_name;
    private String parent_post_grp_name;
    private String views_count;
    private String conversation_count;
    private String conversation_count_new;
    private int created_by;
    private String shared_user_count;
    private String shared_user_names;
    private String shared_user_name;
    private String common_share_count;
    private String post_ref_id;
    private String parent_post_created_user_propic;
    private String parent_post_created_propic;
    private Boolean muted;
    private int category_id;
    private String orgin_id;
    private String from_id;
    private String family_logo;
    private String to_group_id;
    private String to_user_id;
    private String shared_user_id;
    private String title;
    private String snap_description;
    private Boolean is_active;
    private String createdAt;
    private String updatedAt;
    private Boolean conversation_enabled;
    private String privacy_type;
    private String type;
    private int aggregated_count;
    private ArrayList<String> inactive_active_array;
    private String parent_post_created_user_id;
    private int is_approved;
    private ArrayList<HistoryImages> post_attachment;
    private String parent_family_logo;
    private ArrayList<String> valid_urls;
    private UrlMetadata url_metadata;
    private String publish_type;
    private String publish_id;



    private String notification_key;
    private ArrayList<User> publish_mention_users;
    private ArrayList<Item_need> publish_mention_items;
    public static final Creator<PostData> CREATOR = new Creator<PostData>() {
        @Override
        public PostData createFromParcel(Parcel in) {
            return new PostData(in);
        }

        @Override
        public PostData[] newArray(int size) {
            return new PostData[size];
        }
    };

    public PostData() {
    }

    protected PostData(Parcel in) {
        byte tmpIs_shareable = in.readByte();
        is_shareable = tmpIs_shareable == 0 ? null : tmpIs_shareable == 1;
        byte tmpRating_enabled = in.readByte();
        rating_enabled = tmpRating_enabled == 0 ? null : tmpRating_enabled == 1;
        read_status = in.readByte() != 0;
        post_id = in.readInt();
        to_user_name = in.readString();
        id = in.readInt();
        aggregated_count = in.readInt();
        selected_family_count = in.readString();
        selected_user_count = in.readString();
        is_viewed = in.readString();
        group_name = in.readString();
        created_user_name = in.readString();
        propic = in.readString();
        parent_post_created_user_name = in.readString();
        parent_post_grp_name = in.readString();
        views_count = in.readString();
        rating = in.readString();
        rating_by_user=in.readString();
        rating_count=in.readString();
        conversation_count = in.readString();
        conversation_count_new = in.readString();
        created_by = in.readInt();
        shared_user_count = in.readString();
        shared_user_names = in.readString();
        shared_user_name = in.readString();
        common_share_count = in.readString();
        post_ref_id = in.readString();
        byte tmpMuted = in.readByte();
        muted = tmpMuted == 0 ? null : tmpMuted == 1;
        category_id = in.readInt();
        orgin_id = in.readString();
        from_id = in.readString();
        family_logo = in.readString();
        to_group_id = in.readString();
        to_user_id = in.readString();
        shared_user_id = in.readString();
        title = in.readString();
        snap_description = in.readString();
        byte tmpIs_active = in.readByte();
        is_active = tmpIs_active == 0 ? null : tmpIs_active == 1;
        createdAt = in.readString();
        updatedAt = in.readString();
        byte tmpConversation_enabled = in.readByte();
        conversation_enabled = tmpConversation_enabled == 0 ? null : tmpConversation_enabled == 1;
        privacy_type = in.readString();
        notification_key = in.readString();
        type = in.readString();
        parent_post_created_user_id = in.readString();
        parent_post_created_user_propic = in.readString();
        is_approved = in.readInt();


        post_attachment = in.createTypedArrayList(HistoryImages.CREATOR);
        valid_urls = in.createStringArrayList();
        inactive_active_array = in.createStringArrayList();
        parent_family_logo = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (is_shareable == null ? 0 : is_shareable ? 1 : 2));
        dest.writeByte((byte)(rating_enabled == null ? 0 : rating_enabled ? 1: 2));
        dest.writeByte((byte) (read_status ? 1 : 0));
        dest.writeInt(post_id);
        dest.writeString(to_user_name);
        dest.writeInt(id);
        dest.writeInt(aggregated_count);
        dest.writeString(selected_family_count);
        dest.writeString(selected_user_count);
        dest.writeString(is_viewed);
        dest.writeString(group_name);
        dest.writeString(created_user_name);
        dest.writeString(propic);
        dest.writeString(parent_post_created_user_name);
        dest.writeString(parent_post_grp_name);
        dest.writeString(views_count);
        dest.writeString(rating);
        dest.writeString(rating_by_user);
        dest.writeString(rating_count);
        dest.writeString(conversation_count);
        dest.writeString(conversation_count_new);
        dest.writeInt(created_by);
        dest.writeString(shared_user_count);
        dest.writeString(shared_user_names);
        dest.writeString(shared_user_name);
        dest.writeString(common_share_count);
        dest.writeString(post_ref_id);
        dest.writeByte((byte) (muted == null ? 0 : muted ? 1 : 2));
        dest.writeInt(category_id);
        dest.writeString(orgin_id);
        dest.writeString(from_id);
        dest.writeString(family_logo);
        dest.writeString(to_group_id);
        dest.writeString(to_user_id);
        dest.writeString(shared_user_id);
        dest.writeString(title);
        dest.writeString(snap_description);
        dest.writeByte((byte) (is_active == null ? 0 : is_active ? 1 : 2));
        dest.writeString(createdAt);
        dest.writeString(updatedAt);
        dest.writeByte((byte) (conversation_enabled == null ? 0 : conversation_enabled ? 1 : 2));
        dest.writeString(privacy_type);
        dest.writeString(notification_key);
        dest.writeString(type);
        dest.writeString(parent_post_created_user_id);
        dest.writeString(parent_post_created_user_propic);
        dest.writeInt(is_approved);
        dest.writeTypedList(post_attachment);
        dest.writeStringList(valid_urls);
        dest.writeStringList(inactive_active_array);
        dest.writeString(parent_family_logo);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    private ArrayList<SelectFamilys> familyList = new ArrayList<>();

    public Integer getPost_id() {
        return post_id;
    }

    public void setPost_id(int post_id) {
        this.post_id = post_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public String getCreated_user_name() {
        return created_user_name;
    }

    public void setCreated_user_name(String created_user_name) {
        this.created_user_name = created_user_name;
    }

    public String getNotification_key() {
        return notification_key;
    }

    public void setNotification_key(String notification_key) {
        this.notification_key = notification_key;
    }

    public ArrayList<User> getPublish_mention_users() {
        return publish_mention_users;
    }

    public ArrayList<Item_need> getPublish_mention_items() {
        return publish_mention_items;
    }

    public String getPropic() {
        return propic;
    }

    public void setPropic(String propic) {
        this.propic = propic;
    }

    public String getParent_post_created_user_name() {
        return parent_post_created_user_name;
    }

    public void setParent_post_created_user_name(String parent_post_created_user_name) {
        this.parent_post_created_user_name = parent_post_created_user_name;
    }

    public String getParent_post_grp_name() {
        return parent_post_grp_name;
    }

    public void setParent_post_grp_name(String parent_post_grp_name) {
        this.parent_post_grp_name = parent_post_grp_name;
    }

    public String getViews_count() {
        return views_count;
    }

    public void setViews_count(String views_count) {
        this.views_count = views_count;
    }

    public String getRating(){return rating;}
    public void setRating(String rating){this.rating = rating;}

    public String getRating_by_user(){return rating_by_user;}
    public void setRating_by_user(String rating_by_user){this.rating_by_user=rating_by_user;}

    public String getRating_count(){return rating_count;}
    public void setRating_count(String rating_count){this.rating_count=rating_count;}

    public String getConversation_count() {
        return conversation_count;
    }

    public void setConversation_count(String conversation_count) {
        this.conversation_count = conversation_count;
    }

    public String getConversation_count_new() {
        return conversation_count_new;
    }

    public void setConversation_count_new(String conversation_count_new) {
        this.conversation_count_new = conversation_count_new;
    }

    public int getCreated_by() {
        return created_by;
    }

    public void setCreated_by(int created_by) {
        this.created_by = created_by;
    }

    public String getShared_user_count() {
        return shared_user_count;
    }

    public void setShared_user_count(String shared_user_count) {
        this.shared_user_count = shared_user_count;
    }

    public String getShared_user_names() {
        return shared_user_names;
    }

    public void setShared_user_names(String shared_user_names) {
        this.shared_user_names = shared_user_names;
    }

    public String getCommon_share_count() {
        return common_share_count;
    }

    public void setCommon_share_count(String common_share_count) {
        this.common_share_count = common_share_count;
    }

    public String getParent_post_created_propic() {
        return parent_post_created_propic;
    }

    public Boolean getMuted() {
        return muted;
    }

    public void setMuted(Boolean muted) {
        this.muted = muted;
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public String getOrgin_id() {
        return orgin_id;
    }

    public void setOrgin_id(String orgin_id) {
        this.orgin_id = orgin_id;
    }

    /*public String getFrom_id() {
        return from_id;
    }
*/
    public void setFrom_id(String from_id) {
        this.from_id = from_id;
    }

    public String getTo_group_id() {
        return to_group_id;
    }

    public void setTo_group_id(String to_group_id) {
        this.to_group_id = to_group_id;
    }

    public String getTo_user_id() {
        return to_user_id;
    }

    public void setTo_user_id(String to_user_id) {
        this.to_user_id = to_user_id;
    }

    public String getShared_user_id() {
        return shared_user_id;
    }

    public void setShared_user_id(String shared_user_id) {
        this.shared_user_id = shared_user_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSnap_description() {
        return snap_description;
    }

    public void setSnap_description(String snap_description) {
        this.snap_description = snap_description;
    }
/*
    public Boolean getIs_active() {
        return is_active;
    }*/

    public void setIs_active(Boolean is_active) {
        this.is_active = is_active;
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

    public Boolean getIs_shareable() {
        return is_shareable;
    }

    public void setIs_shareable(Boolean is_shareable) {
        this.is_shareable = is_shareable;
    }

    public Boolean getRating_enabled(){return rating_enabled;}
    public void setRating_enabled(Boolean rating_enabled){this.rating_enabled = rating_enabled;}

    public Boolean getConversation_enabled() {
        return conversation_enabled;
    }

    public void setConversation_enabled(Boolean conversation_enabled) {
        this.conversation_enabled = conversation_enabled;
    }

    public Boolean getRead_status() {
        return read_status;
    }

    public void setRead_status(Boolean read_status) {
        this.read_status = read_status;
    }

    public String getPrivacy_type() {
        return privacy_type;
    }

    public void setPrivacy_type(String privacy_type) {
        this.privacy_type = privacy_type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getIs_approved() {
        return is_approved;
    }

    public void setIs_approved(int is_approved) {
        this.is_approved = is_approved;
    }

    public ArrayList<HistoryImages> getPost_attachment() {
        return post_attachment;
    }

    public void setPost_attachment(ArrayList<HistoryImages> post_attachment) {
        this.post_attachment = post_attachment;
    }

    public String getShared_user_name() {
        return shared_user_name;
    }

    public void setShared_user_name(String shared_user_name) {
        this.shared_user_name = shared_user_name;
    }

    public String getParent_post_created_user_id() {
        return parent_post_created_user_id;
    }

    public void setParent_post_created_user_id(String parent_post_created_user_id) {
        this.parent_post_created_user_id = parent_post_created_user_id;
    }

    public String getFamily_logo() {
        return family_logo;
    }

    public void setFamily_logo(String family_logo) {
        this.family_logo = family_logo;
    }

    public String getPost_ref_id() {
        return post_ref_id;
    }

    public void setPost_ref_id(String post_ref_id) {
        this.post_ref_id = post_ref_id;
    }

    public String getIs_viewed() {
        return is_viewed;
    }

    public void setIs_viewed(String is_viewed) {
        this.is_viewed = is_viewed;
    }

    public boolean isRead_status() {
        return read_status;
    }

    public void setRead_status(boolean read_status) {
        this.read_status = read_status;
    }

    public String getSelected_family_count() {
        return selected_family_count;
    }

    public void setSelected_family_count(String selected_family_count) {
        this.selected_family_count = selected_family_count;
    }

    public ArrayList<SelectFamilys> getFamilyList() {
        return familyList;
    }

    public ArrayList<String> getInactive_active_array() {
        return inactive_active_array;
    }

    public void setInactive_active_array(ArrayList<String> inactive_active_array) {
        this.inactive_active_array = inactive_active_array;
    }

    public void setFamilyList(ArrayList<SelectFamilys> familyList) {
        this.familyList = familyList;
    }

    public String getParent_post_created_user_propic() {
        return parent_post_created_user_propic;
    }

    public void setParent_post_created_user_propic(String parent_post_created_user_propic) {
        this.parent_post_created_user_propic = parent_post_created_user_propic;
    }

    public String getTo_user_name() {
        return to_user_name;
    }

    public void setTo_user_name(String to_user_name) {
        this.to_user_name = to_user_name;
    }

    public String getSelected_user_count() {
        return selected_user_count;
    }

    public void setSelected_user_count(String selected_user_count) {
        this.selected_user_count = selected_user_count;
    }

    public String getParent_family_logo() {
        return parent_family_logo;
    }

    public int getAggregated_count() {
        return aggregated_count;
    }

    public ArrayList<String> getValid_urls() {
        return valid_urls;
    }

    public void setValid_urls(ArrayList<String> valid_urls) {
        this.valid_urls = valid_urls;
    }

    public UrlMetadata getUrl_metadata() {
        return url_metadata;
    }

    public void setUrl_metadata(UrlMetadata url_metadata) {
        this.url_metadata = url_metadata;
    }

    public String getPublish_type() {
        return publish_type;
    }

    public String getPublish_id() {
        return publish_id;
    }

    public class UrlMetadata {
        private ArrayList<String> urls;
private UrlMetadataResult urlMetadataResult;

        public UrlMetadataResult getUrlMetadataResult() {
            return urlMetadataResult;
        }

        public void setUrlMetadataResult(UrlMetadataResult urlMetadataResult) {
            this.urlMetadataResult = urlMetadataResult;
        }

        public ArrayList<String> getUrls() {
            return urls;
        }

        public void setUrls(ArrayList<String> urls) {
            this.urls = urls;
        }
    }


    public class UrlMetadataResult {

        private String url;
        private String title;
        private String description;
        private String image;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }
    }

}
