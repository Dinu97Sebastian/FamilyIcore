package com.familheey.app.Models.Response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

@SuppressWarnings("unused")
public class ListAllMembersResponse {

    @SerializedName("page")
    private Long mPage;
    @SerializedName("pageSize")
    private Long mPageSize;
    @SerializedName("rows")
    private List<User> mRows;
    @SerializedName("total")
    private Long mTotal;
    @SerializedName("totalPages")
    private Long mTotalPages;

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

    public List<User> getRows() {
        return mRows;
    }

    public void setRows(List<User> rows) {
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

}


