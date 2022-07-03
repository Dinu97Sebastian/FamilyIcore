
package com.familheey.app.Models.Response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

@SuppressWarnings("unused")
public class EventItemsResponse {

    @SerializedName("data")
    private List<EventSignUp> mData;

    public List<EventSignUp> getData() {
        return mData;
    }

    public void setData(List<EventSignUp> data) {
        mData = data;
    }

}
