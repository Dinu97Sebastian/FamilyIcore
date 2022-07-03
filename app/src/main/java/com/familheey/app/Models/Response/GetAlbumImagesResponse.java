
package com.familheey.app.Models.Response;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class GetAlbumImagesResponse {

    @SerializedName("documents")
    private List<Document> mDocuments = new ArrayList<>();
    String cover_pic;
    @SerializedName("folder_details")
    ViewContents.FolderDetails folderDetails;

    public String getCover_pic() {
        return cover_pic;
    }

    public List<Document> getDocuments() {
        return mDocuments;
    }

    public void setDocuments(List<Document> documents) {
        mDocuments = documents;
    }

    public ViewContents.FolderDetails getFolderDetails() {
        return folderDetails;
    }
}
