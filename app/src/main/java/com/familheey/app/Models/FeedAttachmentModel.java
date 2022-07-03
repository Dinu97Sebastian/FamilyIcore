package com.familheey.app.Models;

import android.net.Uri;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.File;

public class FeedAttachmentModel {

    @SerializedName("attachment_uri")
    @Expose
    private Uri attachment_uri;

    @SerializedName("attachment_type")
    @Expose
    private String attachment_type;
    @SerializedName("file")
    @Expose
    private File file;
    @SerializedName("thumb_file")
    @Expose
    private File thumb_file;

    public FeedAttachmentModel(Uri attachment_uri, String attachment_type,File file,File thumb_file) {
        this.attachment_uri = attachment_uri;
        this.attachment_type = attachment_type;
        this.file=file;
        this.thumb_file=thumb_file;
    }

    public String getAttachment_type() {
        return attachment_type;
    }
    public Uri getAttachment_uri() {
        return attachment_uri;
    }
    public File getFile(){return file;}
    public File getThumb_file(){return thumb_file;}
}

