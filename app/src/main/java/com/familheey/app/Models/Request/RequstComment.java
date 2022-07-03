package com.familheey.app.Models.Request;

import com.familheey.app.Models.Response.SocketCommentResponse;

import java.util.List;

public class RequstComment {
    private String topic_id;
    private String post_id;
    private String comment;
    private String commented_by;
    private String group_id;


    private String file_name;
    private String file_type;


    private String name;
    private String quoted_item;
    private String quoted_id;
    private String quoted_user;
    private String quoted_date;
    private List<SocketCommentResponse.Attachment> attachment;

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setCommented_by(String commented_by) {
        this.commented_by = commented_by;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public void setQuoted_item(String quoted_item) {
        this.quoted_item = quoted_item;
    }

    public void setQuoted_id(String quoted_id) {
        this.quoted_id = quoted_id;
    }

    public void setQuoted_user(String quoted_user) {
        this.quoted_user = quoted_user;
    }

    public void setQuoted_date(String quoted_date) {
        this.quoted_date = quoted_date;
    }

    public void setAttachment(List<SocketCommentResponse.Attachment> attachment) {
        this.attachment = attachment;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTopic_id(String topic_id) {
        this.topic_id = topic_id;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public void setFile_type(String file_type) {
        this.file_type = file_type;
    }
}
