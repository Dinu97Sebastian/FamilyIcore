package com.familheey.app.Need;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ItemDetailWrapper {
    @SerializedName("code")
    @Expose
    private Integer code;
    @SerializedName("data")
    @Expose
    private Item need;

    public Integer getCode() {
        return code;
    }

    public Item getNeed() {
        return need;
    }
}
