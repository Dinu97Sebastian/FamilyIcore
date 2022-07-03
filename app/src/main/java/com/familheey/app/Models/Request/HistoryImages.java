package com.familheey.app.Models.Request;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class HistoryImages implements Parcelable {

    @SerializedName(value = "filename")
    @Expose
    private String filename;

    @SerializedName("type")
    @Expose
    private String type;

    @SerializedName("width")
    @Expose
    private String width;

    @SerializedName("height")
    @Expose
    private String height;


    @SerializedName("height1")
    @Expose
    private String height1;


    @SerializedName("video_thumb")
    @Expose
    private String video_thumb;

    @SerializedName("original_name")
    @Expose
    private String original_name;

    @SerializedName("is_play")
    @Expose
    private Boolean is_play;

    @SerializedName("is_ready")
    @Expose
    private int is_ready = 0;


    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }


    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getVideo_thumb() {
        return video_thumb;
    }

    public void setVideo_thumb(String video_thumb) {
        this.video_thumb = video_thumb;
    }

    protected HistoryImages(Parcel in) {
        filename = in.readString();
        type = in.readString();
        width = in.readString();
        height = in.readString();
        height1 = in.readString();
        video_thumb = in.readString();
        original_name = in.readString();
        byte tmpis_play = in.readByte();
        is_play = tmpis_play == 0 ? null : tmpis_play == 1;
        is_ready = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(filename);
        dest.writeString(type);
        dest.writeString(width);
        dest.writeString(height);
        dest.writeString(height1);
        dest.writeString(video_thumb);
        dest.writeString(original_name);
        dest.writeByte((byte) (is_play == null ? 0 : is_play ? 1 : 2));
        dest.writeInt(is_ready);
    }

    public HistoryImages() {
    }

    public static final Creator<HistoryImages> CREATOR = new Creator<HistoryImages>() {
        @Override
        public HistoryImages createFromParcel(Parcel in) {
            return new HistoryImages(in);
        }

        @Override
        public HistoryImages[] newArray(int size) {
            return new HistoryImages[size];
        }
    };

    public boolean isIs_play() {
        return is_play;
    }

    public void setIs_play(boolean is_play) {
        this.is_play = is_play;
    }

    public String getHeight1() {
        return height1;
    }

    public void setHeight1(String height1) {
        this.height1 = height1;
    }

    public String getOriginal_name() {
        return original_name;
    }

    public void setOriginal_name(String original_name) {
        this.original_name = original_name;
    }


    public int getIs_ready() {
        return is_ready;
    }

    public void setIs_ready(int is_ready) {
        this.is_ready = is_ready;
    }
}
