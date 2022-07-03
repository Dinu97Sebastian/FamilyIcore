package com.familheey.app.Models.Response;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class CreateAlbumEventResponse implements Parcelable {

    @SerializedName("data")
    private ArrayList<Row> mRows = new ArrayList<>();

    protected CreateAlbumEventResponse(Parcel in) {
        mRows = in.createTypedArrayList(Row.CREATOR);
    }

    public static final Creator<CreateAlbumEventResponse> CREATOR = new Creator<CreateAlbumEventResponse>() {
        @Override
        public CreateAlbumEventResponse createFromParcel(Parcel in) {
            return new CreateAlbumEventResponse(in);
        }

        @Override
        public CreateAlbumEventResponse[] newArray(int size) {
            return new CreateAlbumEventResponse[size];
        }
    };

    public ArrayList<Row> getRows() {
        return mRows;
    }

    public void setRows(ArrayList<Row> rows) {
        mRows = rows;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeTypedList(mRows);
    }


    public static class Row implements Parcelable {
        @SerializedName("cover_pic")
        private String mCoverPic;
        @SerializedName("createdAt")
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
        public static final Creator<Row> CREATOR = new Creator<Row>() {
            @Override
            public Row createFromParcel(Parcel in) {
                return new Row(in);
            }

            @Override
            public Row[] newArray(int size) {
                return new Row[size];
            }
        };
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
        @SerializedName("updatedAt")
        private String mUpdatedAt;
        @SerializedName("group_id")
        private String mGroupId;

        protected Row(Parcel in) {
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
        }

        @Override
        public int describeContents() {
            return 0;
        }

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
    }
}
