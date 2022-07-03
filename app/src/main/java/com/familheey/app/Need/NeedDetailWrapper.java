package com.familheey.app.Need;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NeedDetailWrapper {


    @SerializedName("is_admin")
    @Expose
    private Boolean is_admin;

    @SerializedName("code")
    @Expose
    private Integer code;
    @SerializedName("data")
    @Expose
    private Need needRequests;

    public Integer getCode() {
        return code;
    }

    public Need getNeedRequest() {
        return needRequests;
    }

    public Boolean getIs_admin() {
        return is_admin;
    }
}
