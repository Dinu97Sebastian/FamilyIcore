package com.familheey.app.Models;

import android.net.Uri;

import com.familheey.app.Fragments.Posts.PostData;
import com.familheey.app.Models.Request.PostRequest;

import java.io.File;
import java.util.ArrayList;

public class PostDraftModel {
    private PostRequest postRequest;
    private ArrayList<FeedAttachmentModel2> feedAttachmentModel;


    public PostDraftModel(PostRequest postRequest, ArrayList<FeedAttachmentModel2> feedAttachmentModel) {
        this.postRequest = postRequest;
        this.feedAttachmentModel = feedAttachmentModel;

    }

    public PostRequest getPostRequest() {
        return postRequest;
    }
    public ArrayList<FeedAttachmentModel2> getFeedAttachmentModel() {
        return feedAttachmentModel;
    }

    public void setFeedAttachmentModel(ArrayList<FeedAttachmentModel2> feedAttachmentModel) {
        this.feedAttachmentModel = feedAttachmentModel;
    }

    public void setPostRequest(PostRequest postRequest) {
        this.postRequest = postRequest;
    }
}
