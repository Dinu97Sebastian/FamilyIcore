package com.familheey.app.Models.Response;

import android.os.Parcel;
import android.os.Parcelable;

import com.familheey.app.Utilities.Constants;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class GetCommentReplyResponse implements Parcelable {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("replied_user")
    @Expose
    private String repliedUser;
    @SerializedName("comment")
    @Expose
    private String comment;
    @SerializedName("propic")
    @Expose
    private String profilePic;
    @SerializedName("commented_by")
    @Expose
    private String userId;
    @SerializedName("createdAt")
    @Expose
    private String createdAt;
    @SerializedName("attachment")
    @Expose
    private List<SocketCommentResponse.Attachment> attachmentList;

    public List<SocketCommentResponse.Attachment> getAttachmentList() {
        return attachmentList;
    }

    public void setAttachmentList(List<SocketCommentResponse.Attachment> attachmentList) {
        this.attachmentList = attachmentList;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setRepliedUser(String repliedUser) {
        this.repliedUser = repliedUser;
    }

    public String getRepliedUser() {
        return repliedUser;
    }

    protected GetCommentReplyResponse(Parcel in) {

        id = in.readInt();
        repliedUser = in.readString();
        comment=in.readString();
        profilePic=in.readString();
        userId=in.readString();

    }
    public GetCommentReplyResponse() {
    }
    public static final Creator<GetCommentReplyResponse> CREATOR = new Creator<GetCommentReplyResponse>() {
        @Override
        public GetCommentReplyResponse createFromParcel(Parcel in) {
            return new GetCommentReplyResponse(in);
        }

        @Override
        public GetCommentReplyResponse[] newArray(int size) {
            return new GetCommentReplyResponse[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(id);
        }
        dest.writeString(repliedUser);

    }
    public ArrayList<String> getImagesUrls() {
        ArrayList<String> imageUrls = new ArrayList<>();
        for (SocketCommentResponse.Attachment historyImage : getAttachmentList()) {
            imageUrls.add(Constants.ApiPaths.IMAGE_BASE_URL + "file_name/" + historyImage.getFilename());
        }
        return imageUrls;
    }
}
