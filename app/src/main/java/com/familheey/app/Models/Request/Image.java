package com.familheey.app.Models.Request;

import java.io.File;

public class Image {
    private File file;
    private String mUrl;
    private boolean isuploading;
    private boolean isUrl;
    private boolean isVideo;
    private boolean isDoc;
    private boolean isAudio;
    private String fileType;
    private int Prograss=0;

    public int getPrograss() {
        return Prograss;
    }

    public void setPrograss(int prograss) {
        Prograss = prograss;
    }

    public String getmUrl() {
        return mUrl;
    }

    public void setmUrl(String mUrl) {
        this.mUrl = mUrl;
    }

    public boolean isIsuploading() {
        return isuploading;
    }

    public void setIsuploading(boolean isuploading) {
        this.isuploading = isuploading;
    }

    public boolean isUrl() {
        return isUrl;
    }

    public void setUrl(boolean url) {
        isUrl = url;
    }

    public boolean isVideo() {
        return isVideo;
    }

    public void setVideo(boolean video) {
        isVideo = video;
    }

    public boolean isDoc() {
        return isDoc;
    }
    public void setDoc(boolean doc) {
        isDoc = doc;
    }
    public void setAudio(boolean audio) {
        isAudio = audio;
    }
    public boolean isAudio() {
        return isAudio;
    }
    public File getFile() {
        return file;
    }
    public void setFile(File file) {
        this.file = file;
    }

    public String getfileType() {
        return fileType;
    }
    public void setfileType(String fileType) {
        this.fileType = fileType;
    }
}
