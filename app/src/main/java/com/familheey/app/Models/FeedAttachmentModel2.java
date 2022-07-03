package com.familheey.app.Models;

import android.net.Uri;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.File;

public class FeedAttachmentModel2 {
    @SerializedName("attachment_uri")
    @Expose
    private String attachment_uri;

    @SerializedName("attachment_type")
    @Expose
    private String attachment_type;


    public FeedAttachmentModel2(String attachment_uri, String attachment_type) {
        this.attachment_uri = attachment_uri;
        this.attachment_type = attachment_type;
    }

    public String getAttachment_type() {
        return attachment_type;
    }
    public String getAttachment_uri() {
        return attachment_uri;
    }
}
