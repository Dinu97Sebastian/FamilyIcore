
package com.familheey.app.Models.Response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@SuppressWarnings("unused")
public class ListGroupFoldersResponse {

    public static SimpleDateFormat fetchDisplay = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());

    @SerializedName("member")
    private Boolean mMember;
    @SerializedName("page")
    private Long mPage;
    @SerializedName("pageSize")
    private Long mPageSize;
    @SerializedName("data")
    private List<Row> mRows = new ArrayList<>();
    @SerializedName("total")
    private Long mTotal;
    @SerializedName("totalPages")
    private Long mTotalPages;

    public Boolean getMember() {
        return mMember;
    }

    public void setMember(Boolean member) {
        mMember = member;
    }

    public Long getPage() {
        return mPage;
    }

    public void setPage(Long page) {
        mPage = page;
    }

    public Long getPageSize() {
        return mPageSize;
    }

    public void setPageSize(Long pageSize) {
        mPageSize = pageSize;
    }

    public List<Row> getRows() {
        return mRows;
    }

    public void setRows(List<Row> rows) {
        mRows = rows;
    }

    public Long getTotal() {
        return mTotal;
    }

    public void setTotal(Long total) {
        mTotal = total;
    }

    public Long getTotalPages() {
        return mTotalPages;
    }

    public void setTotalPages(Long totalPages) {
        mTotalPages = totalPages;
    }

    public class Row {

        @SerializedName("id")
        @Expose
        public Integer id;
        @SerializedName("folder_name")
        @Expose
        public String folderName;
        @SerializedName("created_by")
        @Expose
        public Integer createdBy;
        @SerializedName("group_id")
        @Expose
        public Integer groupId;
        @SerializedName("type")
        @Expose
        public String type;
        @SerializedName("is_sharable")
        @Expose
        public Boolean isSharable;
        @SerializedName("is_active")
        @Expose
        public Boolean isActive;
        @SerializedName("subfolder_of")
        @Expose
        public Object subfolderOf;
        @SerializedName("event_id")
        @Expose
        public Object eventId;
        @SerializedName("folder_type")
        @Expose
        public String folderType;
        @SerializedName("folder_for")
        @Expose
        public String folderFor;
        @SerializedName("description")
        @Expose
        public String description;
        @SerializedName("created_at")
        @Expose
        public String createdAt;
        @SerializedName("updated_at")
        @Expose
        public String updatedAt;
        @SerializedName("cover_pic")
        @Expose
        public Object coverPic;
        @SerializedName("permissions")
        @Expose
        public String permissions;
        @SerializedName("created_by_name")
        @Expose
        public String createdByName;

        public String getDocCount() {
            return docCount;
        }

        @SerializedName("doc_count")
        public String docCount;

        private boolean isLongPressed = false;

        public Row() {

        }

        public Integer getId() {
            return id;
        }

        public String getFolderName() {
            return folderName;
        }

        public Integer getCreatedBy() {
            return createdBy;
        }

        public Integer getGroupId() {
            return groupId;
        }

        public String getType() {
            return type;
        }

        public Boolean getSharable() {
            return isSharable;
        }

        public Boolean getActive() {
            return isActive;
        }

        public Object getSubfolderOf() {
            return subfolderOf;
        }

        public Object getEventId() {
            return eventId;
        }

        public String getFolderType() {
            return folderType;
        }

        public String getFolderFor() {
            return folderFor;
        }

        public String getDescription() {
            return description;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public Object getCoverPic() {
            return coverPic;
        }

        public String getPermissions() {
            return permissions;
        }

        public String getCreatedByName() {
            return createdByName;
        }

        public String getFormattedDate() {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd yyyy", Locale.getDefault());
                Date parsedDate = fetchDisplay.parse(getCreatedAt());
                String formattedDate = dateFormat.format(parsedDate);
                return formattedDate;
            } catch (ParseException e) {
                e.printStackTrace();
                return getCreatedAt();
            }
        }

        public boolean isLongPressed() {
            return isLongPressed;
        }

        public void setLongPressed(boolean longPressed) {
            isLongPressed = longPressed;
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
