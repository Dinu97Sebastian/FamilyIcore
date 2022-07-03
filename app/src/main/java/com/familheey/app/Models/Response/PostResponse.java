package com.familheey.app.Models.Response;

import com.familheey.app.Fragments.Posts.PostData;

import java.util.ArrayList;

public class PostResponse {
    private ArrayList<PostData> data;
    private ArrayList<PostData> read_announcement;
    private ArrayList<PostData> unread_announcement;

    public ArrayList<PostData> getData() {
        return data;
    }

    public ArrayList<PostData> getRead_announcement() {
        return read_announcement;
    }

    public ArrayList<PostData> getUnread_announcement() {
        return unread_announcement;
    }
}
