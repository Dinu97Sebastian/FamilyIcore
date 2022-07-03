package com.familheey.app.Models.Response;

import com.familheey.app.Models.ChatModel;
import com.familheey.app.Utilities.Constants;

import java.util.ArrayList;
import java.util.List;

public class PostComment {
    private boolean isOwner;
    private String chatText, senderName, filename, profilePic, createdAt, type, comment_id, quoted_item, quoted_id, quoted_date, quoted_user,reply_count;
    private int userId;
    private List<SocketCommentResponse.Attachment> attachmentList;
    private List<GetCommentReplyResponse> commentReply;
    public String getCreatedAt() {
        return createdAt;
    }

    public PostComment(String chatText) {
        this.chatText = chatText;
    }

    public PostComment(boolean isOwner, String chatText, String senderName, String filename, String profilePic, String createdAt, String type, String id, int userId, String quoted_date, String quoted_id, String quoted_item, String quoted_user, List<SocketCommentResponse.Attachment> attachmentList,List<GetCommentReplyResponse> commentReply,String reply_count) {
        this.chatText = chatText;
        this.isOwner = isOwner;
        this.senderName = senderName;
        this.filename = filename;
        this.profilePic = profilePic;
        this.createdAt = createdAt;
        this.type = type;
        this.comment_id = id;
        this.userId = userId;
        this.quoted_item = quoted_item;
        this.quoted_id = quoted_id;
        this.quoted_date = quoted_date;
        this.quoted_user = quoted_user;
        this.attachmentList = attachmentList;
        this.commentReply=commentReply;
        this.reply_count=reply_count;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public String getFilename() {
        return filename;
    }

    public boolean isOwner() {
        return isOwner;
    }

    public String getChatText() {
        return chatText;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getType() {
        return type;
    }

    public String getComment_id() {
        return comment_id;
    }

    public int getUserId() {
        return userId;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }

    public String getQuoted_item() {
        return quoted_item;
    }

    public String getQuoted_id() {
        return quoted_id;
    }

    public String getQuoted_date() {
        return quoted_date;
    }

    public String getQuoted_user() {
        return quoted_user;
    }
    public String getReply_count() {
        return reply_count;
    }
    public void setReply_count(String reply_count) {
        this.reply_count = reply_count;
    }
    public List<SocketCommentResponse.Attachment> getAttachmentList() {
        return attachmentList;
    }
    public void setAttachmentList(List<SocketCommentResponse.Attachment> attachmentList) {
        this.attachmentList = attachmentList;
    }

    public ArrayList<String> getImagesUrls() {
        ArrayList<String> imageUrls = new ArrayList<>();
        for (SocketCommentResponse.Attachment historyImage : getAttachmentList()) {
            imageUrls.add(Constants.ApiPaths.IMAGE_BASE_URL + "file_name/" + historyImage.getFilename());
        }
        return imageUrls;
    }
    public List<GetCommentReplyResponse> getCommentReply()
    {
        return commentReply;
    }

    public void setCommentReply(List<GetCommentReplyResponse> reply)
    {
        commentReply = reply;
    }
}
