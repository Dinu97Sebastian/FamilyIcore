
package com.familheey.app.Models.Response;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class ViewContents {

    @SerializedName("documents")
    private List<Document> mDocuments;
    @SerializedName("folders")
    private List<Object> mFolders;
    @SerializedName("folder_details")
    FolderDetails folderDetails;

    public List<Document> getDocuments() {
        return mDocuments;
    }

    public void setDocuments(List<Document> documents) {
        mDocuments = documents;
    }

    public List<Object> getFolders() {
        return mFolders;
    }

    public void setFolders(List<Object> folders) {
        mFolders = folders;
    }

    public FolderDetails getFolderDetails() {
        return folderDetails;
    }

    public class Document implements Parcelable {

        @SerializedName("createdAt")
        private String mCreatedAt;
        @SerializedName("file_type")
        private String mFileType;
        @SerializedName("folder_id")
        private Long mFolderId;
        @SerializedName("group_id")
        private Long mGroupId;
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
        @SerializedName("url")
        private String mUrl;
        @SerializedName("user_id")
        private Long mUserId;
        @SerializedName("file_name")
        private String fileName;
        @SerializedName("original_name")
        private String originalname;
        private boolean isLongPressed = false;

        public final Creator<Document> CREATOR = new Creator<Document>() {
            @Override
            public Document createFromParcel(Parcel in) {
                return new Document(in);
            }

            @Override
            public Document[] newArray(int size) {
                return new Document[size];
            }
        };

        protected Document(Parcel in) {
            mCreatedAt = in.readString();
            mFileType = in.readString();
            if (in.readByte() == 0) {
                mFolderId = null;
            } else {
                mFolderId = in.readLong();
            }
            if (in.readByte() == 0) {
                mGroupId = null;
            } else {
                mGroupId = in.readLong();
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
            fileName = in.readString();
            isLongPressed = in.readByte() != 0;
        }

        public String getCreatedAt() {
            return mCreatedAt;
        }

        public void setCreatedAt(String createdAt) {
            mCreatedAt = createdAt;
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

        public Long getGroupId() {
            return mGroupId;
        }

        public void setGroupId(Long groupId) {
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

        public boolean isLongPressed() {
            return isLongPressed;
        }

        public void setLongPressed(boolean longPressed) {
            this.isLongPressed = longPressed;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getOriginalname() {
            return originalname;
        }

        public void setOriginalname(String originalname) {
            this.originalname = originalname;
        }

        @Override
        public int describeContents() {
            return 0;
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
            if (mGroupId == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeLong(mGroupId);
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
            dest.writeString(fileName);
            dest.writeByte((byte) (isLongPressed ? 1 : 0));
        }
    }

    public class FolderDetails {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("folder_name")
        @Expose
        private String folderName;
        @SerializedName("created_by")
        @Expose
        private Integer createdBy;
        @SerializedName("group_id")
        @Expose
        private Integer groupId;
        @SerializedName("type")
        @Expose
        private String type;
        @SerializedName("is_sharable")
        @Expose
        private Boolean isSharable;
        @SerializedName("is_active")
        @Expose
        private Boolean isActive;
        @SerializedName("subfolder_of")
        @Expose
        private String subfolderOf;
        @SerializedName("event_id")
        @Expose
        private String eventId;
        @SerializedName("folder_type")
        @Expose
        private String folderType;
        @SerializedName("permissions")
        @Expose
        private String permissions;
        @SerializedName("folder_for")
        @Expose
        private String folderFor;
        @SerializedName("description")
        @Expose
        private String description;
        @SerializedName("cover_pic")
        @Expose
        private String coverPic;
        @SerializedName("createdAt")
        @Expose
        private String createdAt;
        @SerializedName("updatedAt")
        @Expose
        private String updatedAt;
        @SerializedName("permission_users")
        @Expose
        private List<Integer> permissionUsers = new ArrayList<>();

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getFolderName() {
            return folderName;
        }

        public void setFolderName(String folderName) {
            this.folderName = folderName;
        }

        public Integer getCreatedBy() {
            return createdBy;
        }

        public void setCreatedBy(Integer createdBy) {
            this.createdBy = createdBy;
        }

        public Integer getGroupId() {
            return groupId;
        }

        public void setGroupId(Integer groupId) {
            this.groupId = groupId;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Boolean getIsSharable() {
            return isSharable;
        }

        public void setIsSharable(Boolean isSharable) {
            this.isSharable = isSharable;
        }

        public Boolean getIsActive() {
            return isActive;
        }

        public void setIsActive(Boolean isActive) {
            this.isActive = isActive;
        }

        public String getSubfolderOf() {
            return subfolderOf;
        }

        public void setSubfolderOf(String subfolderOf) {
            this.subfolderOf = subfolderOf;
        }

        public String getEventId() {
            return eventId;
        }

        public void setEventId(String eventId) {
            this.eventId = eventId;
        }

        public String getFolderType() {
            return folderType;
        }

        public void setFolderType(String folderType) {
            this.folderType = folderType;
        }

        public String getPermissions() {
            return permissions;
        }

        public void setPermissions(String permissions) {
            this.permissions = permissions;
        }

        public String getFolderFor() {
            return folderFor;
        }

        public void setFolderFor(String folderFor) {
            this.folderFor = folderFor;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getCoverPic() {
            return coverPic;
        }

        public void setCoverPic(String coverPic) {
            this.coverPic = coverPic;
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

        public List<Integer> getPermissionUsers() {
            return permissionUsers;
        }

        public void setPermissionUsers(List<Integer> permissionUsers) {
            this.permissionUsers = permissionUsers;
        }

    }
}
