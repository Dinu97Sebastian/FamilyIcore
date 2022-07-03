package com.familheey.app.Need;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ContributorsWrapper {
    @SerializedName("code")
    @Expose
    private Integer code;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private List<Contributor> contributors = new ArrayList<>();

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public List<Contributor> getContributors() {
        if (contributors == null)
            return new ArrayList<>();
        return contributors;
    }
}
