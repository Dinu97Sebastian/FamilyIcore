package com.familheey.app.Models.Response;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Document implements Parcelable {

    @SerializedName("createdAt")
    private String mCreatedAt;
    @SerializedName("event_id")
    private String mEventId;
    @SerializedName("file_type")
    private String mFileType;
    @SerializedName("folder_id")
    private Long mFolderId;
    @SerializedName("group_id")
    private Object mGroupId;
    @SerializedName("id")
    private Long mId;
    @SerializedName("is_active")
    private Object mIsActive;
    @SerializedName("is_removed")
    private Object mIsRemoved;
    @SerializedName("is_sharable")
    private Boolean mIsSharable;
    @SerializedName("updatedAt")
    private String mUpdatedAt;
    @SerializedName("original_name")
    private String mUrl;
    @SerializedName("user_id")
    private Long mUserId;
    @SerializedName("file_name")
    private String file_name;
    @SerializedName("url")
    private String url;
    public static final Creator<Document> CREATOR = new Creator<Document>() {
        @Override
        public Document createFromParcel(Parcel in) {
            return new Document(in);
        }

        @Override
        public Document[] newArray(int size) {
            return new Document[size];
        }
    };

    public Document() {
    }

    private boolean isLongPressed = false;

    protected Document(Parcel in) {
        mCreatedAt = in.readString();
        mFileType = in.readString();
        if (in.readByte() == 0) {
            mFolderId = null;
        } else {
            mFolderId = in.readLong();
        }
        if (in.readByte() == 0) {
            mId = null;
        } else {
            mId = in.readLong();
        }
        byte tmpMIsSharable = in.readByte();
        mIsSharable = tmpMIsSharable == 0 ? null : tmpMIsSharable == 1;
        mUpdatedAt = in.readString();
        mUrl = in.readString();
        if (in.readByte() == 0) {
            mUserId = null;
        } else {
            mUserId = in.readLong();
        }
        isLongPressed = in.readByte() != 0;
        file_name = in.readString();
        url = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mCreatedAt);
        dest.writeString(mFileType);
        if (mFolderId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(mFolderId);
        }
        if (mId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(mId);
        }
        dest.writeByte((byte) (mIsSharable == null ? 0 : mIsSharable ? 1 : 2));
        dest.writeString(mUpdatedAt);
        dest.writeString(mUrl);
        if (mUserId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(mUserId);
        }
        dest.writeByte((byte) (isLongPressed ? 1 : 0));
        dest.writeString(file_name);
        dest.writeString(url);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getCreatedAt() {
        return mCreatedAt;
    }

    public void setCreatedAt(String createdAt) {
        mCreatedAt = createdAt;
    }

    public String getEventId() {
        return mEventId;
    }

    public void setEventId(String eventId) {
        mEventId = eventId;
    }

    public String getFileType() {
        return mFileType;
    }

    public void setFileType(String fileType) {
        mFileType = fileType;
    }

    public Long getFolderId() {
        return mFolderId;
    }

    public void setFolderId(Long folderId) {
        mFolderId = folderId;
    }

    public Object getGroupId() {
        return mGroupId;
    }

    public void setGroupId(Object groupId) {
        mGroupId = groupId;
    }

    public Long getId() {
        return mId;
    }

    public void setId(Long id) {
        mId = id;
    }

    public Object getIsActive() {
        return mIsActive;
    }

    public void setIsActive(Object isActive) {
        mIsActive = isActive;
    }

    public Object getIsRemoved() {
        return mIsRemoved;
    }

    public void setIsRemoved(Object isRemoved) {
        mIsRemoved = isRemoved;
    }

    public Boolean getIsSharable() {
        return mIsSharable;
    }

    public void setIsSharable(Boolean isSharable) {
        mIsSharable = isSharable;
    }

    public String getUpdatedAt() {
        return mUpdatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        mUpdatedAt = updatedAt;
    }

    public String getUrl() {
        //change in Glide in AlbumEventAdapter if this is changed
        return url;
    }

    public String getoriginalName() {
        //change in Glide in AlbumEventAdapter if this is changed
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public Long getUserId() {
        return mUserId;
    }

    public void setUserId(Long userId) {
        mUserId = userId;
    }

    public boolean isVideo() {
        return getFileType() != null && getFileType().contains("video");
    }

    public boolean isLongPressed() {
        return isLongPressed;
    }

    public void setLongPressed(boolean longPressed) {
        isLongPressed = longPressed;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }
}