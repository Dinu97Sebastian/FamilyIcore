package com.familheey.app.Models;

import com.familheey.app.Utilities.SharedPref;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class FamilyUpdate {
    private String intro;
    private File coverPic;
    private File logo;
    public String whoCanSeePosts;
    private String isthisPostSearchabel;
    private String linkOtherFamilies;

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public File getCoverPic() {
        return coverPic;
    }

    public void setCoverPic(File coverPic) {
        this.coverPic = coverPic;
    }

    public File getLogo() {
        return logo;
    }

    public void setLogo(File logo) {
        this.logo = logo;
    }

    public String getWhoCanSeePosts() {
        return whoCanSeePosts;
    }

    public void setWhoCanSeePosts(String whoCanSeePosts) {
        this.whoCanSeePosts = whoCanSeePosts;
    }

    public String getIsthisPostSearchabel() {
        return isthisPostSearchabel;
    }

    public void setIsthisPostSearchabel(String isthisPostSearchabel) {
        this.isthisPostSearchabel = isthisPostSearchabel;
    }

    public String getLinkOtherFamilies() {
        return linkOtherFamilies;
    }

    public void setLinkOtherFamilies(String linkOtherFamilies) {
        this.linkOtherFamilies = linkOtherFamilies;
    }

    public MultipartBody.Builder getPostingMultipartBuilderDatas(){
        MultipartBody.Builder multiPartBodyBuilder = new MultipartBody.Builder().setType(MediaType.parse("multipart/form-data"));
        multiPartBodyBuilder.addFormDataPart("id", SharedPref.getUserRegistration().getId());
        multiPartBodyBuilder.addFormDataPart("intro", getIntro());
        multiPartBodyBuilder.addFormDataPart("visibility", getWhoCanSeePosts());
        multiPartBodyBuilder.addFormDataPart("searchable", getIsthisPostSearchabel());
        if (getCoverPic() != null) {
            File coverPicFile = new File(getCoverPic().getPath());
            final MediaType MEDIA_TYPE = getCoverPic().toString().endsWith("png") ? MediaType.parse("image/png") : MediaType.parse("image/jpeg");
            RequestBody profilePictureRequestBody = RequestBody.create(MEDIA_TYPE, coverPicFile);
            multiPartBodyBuilder.addFormDataPart("cover_pic", coverPicFile.getName(), profilePictureRequestBody);
        }
        if (getLogo() != null) {
            File logoFile = new File(getLogo().getPath());
            final MediaType MEDIA_TYPE = getLogo().toString().endsWith("png") ? MediaType.parse("image/png") : MediaType.parse("image/jpeg");
            RequestBody profilePictureRequestBody = RequestBody.create(MEDIA_TYPE, logoFile);
            multiPartBodyBuilder.addFormDataPart("logo", logoFile.getName(), profilePictureRequestBody);
        }
        return multiPartBodyBuilder;
    }
}
