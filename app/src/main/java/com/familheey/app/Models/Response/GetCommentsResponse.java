
package com.familheey.app.Models.Response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

@SuppressWarnings("unused")
public class GetCommentsResponse {

    @SerializedName("data")
    private List<Data> mData;

    public List<Data> getData() {
        return mData;
    }

    @SerializedName("attachment")
    public List<SocketCommentResponse.Attachment> attachmentList;

    public void setData(List<Data> data) {
        mData = data;
    }

    /*
        full_name":"Shijon",
        "phone":"+919487310163",
        "user_id":53,


     */
    public class Data {

        @SerializedName("quoted_item")
        private String quoted_item;

        @SerializedName("quoted_id")
        private String quoted_id;

        @SerializedName("quoted_date")
        private String quoted_date;

        @SerializedName("quoted_user")
        private String quoted_user;

        @SerializedName("attachment")
        private List<SocketCommentResponse.Attachment> mAttachment;
        @SerializedName("comment_id")
        private Long mCommentId;

        @SerializedName("commented_by")
        private String mCommentedBy;
        @SerializedName("comment")
        private String mComment;
        @SerializedName("createdAt")
        private String mCreatedAt;
        @SerializedName("unread")
        private String mUnread;
        @SerializedName("propic")
        private String mPropic;
        @SerializedName("user_id")
        private int mUserId;
        @SerializedName("phone")
        private String mPhone;
        @SerializedName("full_name")
        private String mFullName;
        @SerializedName("reply_count")
        private String reply_count;


        public List<SocketCommentResponse.Attachment> getAttachment() {
            return mAttachment;
        }

        public void setAttachment(List<SocketCommentResponse.Attachment> attachment) {
            mAttachment = attachment;
        }

        public String getComment() {
            return mComment;
        }

        public void setComment(String comment) {
            mComment = comment;
        }

        public Long getCommentId() {
            return mCommentId;
        }

        public void setCommentId(Long commentId) {
            mCommentId = commentId;
        }

        public String getCommentedBy() {
            return mCommentedBy;
        }

        public void setCommentedBy(String commentedBy) {
            mCommentedBy = commentedBy;
        }

        public String getCreatedAt() {
            return mCreatedAt;
        }

        public void setCreatedAt(String createdAt) {
            mCreatedAt = createdAt;
        }


        public String getFullName() {
            return mFullName;
        }

        public void setFullName(String fullName) {
            mFullName = fullName;
        }

        public String getPhone() {
            return mPhone;
        }

        public void setPhone(String phone) {
            mPhone = phone;
        }

        public String getPropic() {
            return mPropic;
        }

        public void setPropic(String propic) {
            mPropic = propic;
        }

        public String getUnread() {
            return mUnread;
        }

        public void setUnread(String unread) {
            mUnread = unread;
        }

        public int getUserId() {
            return mUserId;
        }

        public void setUserId(int userId) {
            mUserId = userId;
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

        public String getReply_count() {
            return reply_count;
        }

        public void setReply_count(String reply_count) {
            this.reply_count = reply_count;
        }
    }


}
