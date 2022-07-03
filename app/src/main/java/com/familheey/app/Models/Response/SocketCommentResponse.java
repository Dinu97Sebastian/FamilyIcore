
package com.familheey.app.Models.Response;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class SocketCommentResponse {
    String type;
    String comment_id;
    ArrayList<String> delete_id;
    String comment;
    String commented_by;
    String group_id;
    String full_name;
    String propic;
    String file_name;
    String file_type;
    String createdAt;
    String quoted_item, quoted_id, quoted_date, quoted_user,reply_count;

    public List<DeletedComments> getDeleted_comments() {
        return deleted_comments;
    }

    public void setDeleted_comments(List<DeletedComments> deleted_comments) {
        this.deleted_comments = deleted_comments;
    }

    private List<DeletedComments> deleted_comments;

    @SerializedName("attachment")
   public List<SocketCommentResponse.Attachment> attachmentList;

    public List<Attachment> getAttachmentList() {
        return attachmentList;
    }

    public String getFile_type() {
        return file_type;
    }

    public String getFile_name() {
        return file_name;
    }

    public String getComment() {
        return comment;
    }

    public String getCommented_by() {
        return commented_by;
    }

    public String getGroup_id() {
        return group_id;
    }

    public String getFull_name() {
        return full_name;
    }

    public String getPropic() {
        return propic;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getReply_count() {
        return reply_count;
    }

    public void setReply_count(String reply_count) {
        this.reply_count = reply_count;
    }

    public String getComment_id() {
        return comment_id;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }

    public ArrayList<String> getDelete_id() {
        return delete_id;
    }

    public void setDelete_id(ArrayList<String> delete_id) {
        this.delete_id = delete_id;
    }

    public String getQuoted_item() {
        return quoted_item;
    }

    public void setQuoted_item(String quoted_item) {
        this.quoted_item = quoted_item;
    }

    public String getQuoted_id() {
        return quoted_id;
    }

    public void setQuoted_id(String quoted_id) {
        this.quoted_id = quoted_id;
    }

    public String getQuoted_date() {
        return quoted_date;
    }

    public void setQuoted_date(String quoted_date) {
        this.quoted_date = quoted_date;
    }

    public String getQuoted_user() {
        return quoted_user;
    }

    public void setQuoted_user(String quoted_user) {
        this.quoted_user = quoted_user;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public static class Attachment {
        String type;
        String filename;
        String video_thumb;

        public String getType() {
            return type;
        }

        public String getFilename() {
            return filename;
        }

        public void setType(String type) {
            this.type = type;
        }

        public void setFilename(String filename) {
            this.filename = filename;
        }

        public String getVideo_thumb() {
            return video_thumb;
        }

        public void setVideo_thumb(String video_thumb) {
            this.video_thumb = video_thumb;
        }
    }
    public class DeletedComments {
        @SerializedName("id")
        @Expose
        private Integer id;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Integer getQuoted_id() {
            return quoted_id;
        }

        public void setQuoted_id(Integer quoted_id) {
            this.quoted_id = quoted_id;
        }

        @SerializedName("quoted_id")
        @Expose
        private Integer quoted_id;
    }
}
