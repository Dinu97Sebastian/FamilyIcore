
package com.familheey.app.Models.Response;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class ListEventAlbumsResponse {

    @SerializedName("data")
    private List<Datum> mData = new ArrayList<>();

    public List<Datum> getData() {
        return mData;
    }

    public void setData(List<Datum> data) {
        mData = data;
    }

    public static class Datum implements Parcelable {
        @SerializedName("cover_pic")
        private String mCoverPic;
        @SerializedName("created_at")
        private String mCreatedAt;
        @SerializedName("created_by")
        private Long mCreatedBy;
        @SerializedName("description")
        private String mDescription;
        @SerializedName("event_id")
        private String mEventId;
        @SerializedName("folder_for")
        private String mFolderFor;
        @SerializedName("folder_name")
        private String mFolderName;
        @SerializedName("folder_type")
        private String mFolderType;
        @SerializedName("group_id")
        private String mGroupId;
        @SerializedName("id")
        private Long mId;
        @SerializedName("is_active")
        private Boolean mIsActive;
        @SerializedName("is_sharable")
        private Boolean mIsSharable;
        @SerializedName("subfolder_of")
        private Object mSubfolderOf;
        @SerializedName("type")
        private String mType;
        @SerializedName("updated_at")
        private String mUpdatedAt;
        @SerializedName("permissions")
        private String permission;

        @SerializedName("doc_count")
        private String docCount;

        public String getDocCount() {
            return docCount;
        }

        @SerializedName("created_by_name")
        private String createdByName;
        @SerializedName("permission_users")
        private List<Integer> permissionGrantedUsers = new ArrayList<>();
        private boolean isLongPressed = false;

        public Datum() {
        }

        protected Datum(Parcel in) {
            mCoverPic = in.readString();
            mCreatedAt = in.readString();
            if (in.readByte() == 0) {
                mCreatedBy = null;
            } else {
                mCreatedBy = in.readLong();
            }
            mDescription = in.readString();
            mEventId = in.readString();
            mFolderFor = in.readString();
            mFolderName = in.readString();
            mFolderType = in.readString();
            mGroupId = in.readString();
            if (in.readByte() == 0) {
                mId = null;
            } else {
                mId = in.readLong();
            }
            byte tmpMIsActive = in.readByte();
            mIsActive = tmpMIsActive == 0 ? null : tmpMIsActive == 1;
            byte tmpMIsSharable = in.readByte();
            mIsSharable = tmpMIsSharable == 0 ? null : tmpMIsSharable == 1;
            mType = in.readString();
            mUpdatedAt = in.readString();
            permission = in.readString();
            docCount = in.readString();
            createdByName = in.readString();
            isLongPressed = in.readByte() != 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(mCoverPic);
            dest.writeString(mCreatedAt);
            if (mCreatedBy == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeLong(mCreatedBy);
            }
            dest.writeString(mDescription);
            dest.writeString(mEventId);
            dest.writeString(mFolderFor);
            dest.writeString(mFolderName);
            dest.writeString(mFolderType);
            dest.writeString(mGroupId);
            if (mId == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeLong(mId);
            }
            dest.writeByte((byte) (mIsActive == null ? 0 : mIsActive ? 1 : 2));
            dest.writeByte((byte) (mIsSharable == null ? 0 : mIsSharable ? 1 : 2));
            dest.writeString(mType);
            dest.writeString(mUpdatedAt);
            dest.writeString(permission);
            dest.writeString(docCount);
            dest.writeString(createdByName);
            dest.writeByte((byte) (isLongPressed ? 1 : 0));
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<Datum> CREATOR = new Creator<Datum>() {
            @Override
            public Datum createFromParcel(Parcel in) {
                return new Datum(in);
            }

            @Override
            public Datum[] newArray(int size) {
                return new Datum[size];
            }
        };

        public String getCoverPic() {
            return mCoverPic;
        }

        public void setCoverPic(String coverPic) {
            mCoverPic = coverPic;
        }

        public String getCreatedAt() {
            return mCreatedAt;
        }

        public void setCreatedAt(String createdAt) {
            mCreatedAt = createdAt;
        }

        public Long getCreatedBy() {
            return mCreatedBy;
        }

        public void setCreatedBy(Long createdBy) {
            mCreatedBy = createdBy;
        }

        public String getDescription() {
            return mDescription;
        }

        public void setDescription(String description) {
            mDescription = description;
        }

        public String getEventId() {
            return mEventId;
        }

        public void setEventId(String eventId) {
            mEventId = eventId;
        }

        public String getFolderFor() {
            return mFolderFor;
        }

        public void setFolderFor(String folderFor) {
            mFolderFor = folderFor;
        }

        public String getFolderName() {
            return mFolderName;
        }

        public void setFolderName(String folderName) {
            mFolderName = folderName;
        }

        public String getFolderType() {
            return mFolderType;
        }

        public void setFolderType(String folderType) {
            mFolderType = folderType;
        }

        public String getGroupId() {
            return mGroupId;
        }

        public void setGroupId(String groupId) {
            mGroupId = groupId;
        }

        public Long getId() {
            return mId;
        }

        public void setId(Long id) {
            mId = id;
        }

        public Boolean getIsActive() {
            return mIsActive;
        }

        public void setIsActive(Boolean isActive) {
            mIsActive = isActive;
        }

        public Boolean getIsSharable() {
            return mIsSharable;
        }

        public void setIsSharable(Boolean isSharable) {
            mIsSharable = isSharable;
        }

        public Object getSubfolderOf() {
            return mSubfolderOf;
        }

        public void setSubfolderOf(Object subfolderOf) {
            mSubfolderOf = subfolderOf;
        }

        public String getType() {
            return mType;
        }

        public void setType(String type) {
            mType = type;
        }

        public String getUpdatedAt() {
            return mUpdatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            mUpdatedAt = updatedAt;
        }

        public String getCreatedByName() {
            return createdByName;
        }

        public boolean isLongPressed() {
            return isLongPressed;
        }

        public void setLongPressed(boolean longPressed) {
            isLongPressed = longPressed;
        }

        public String getPermission() {
            return permission;
        }

        public List<Integer> getPermissionGrantedUsers() {
            return permissionGrantedUsers;
        }

        public void setPermission(String permission) {
            this.permission = permission;
        }

        public void setPermissionGrantedUsers(List<Integer> permissionGrantedUsers) {
            this.permissionGrantedUsers = permissionGrantedUsers;
        }

        public boolean canUpdate(String parentId) {
            if (parentId == null)
                return false;
            if (String.valueOf(getCreatedBy()).equalsIgnoreCase(parentId))
                return true;
            return false;
        }
    }
}
