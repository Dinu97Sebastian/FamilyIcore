package com.familheey.app.Interfaces;

import com.familheey.app.Models.Response.Family;

public interface PostCommentListener {
    void longClickListener(int position);
    void commentReplyClickListener(int position, int currentPosition);
    void onReplyLongClickListener(int childPosition, int parentPosition);
}
