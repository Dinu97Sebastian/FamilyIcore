package com.familheey.app.Need;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class NeedRequestWrapper {
    @SerializedName("code")
    @Expose
    private Integer code;
    @SerializedName("data")
    @Expose
    private List<Need> needRequests = new ArrayList<>();

    public Integer getCode() {
        return code;
    }

    public List<Need> getNeedRequests() {
        return needRequests;
    }
}
