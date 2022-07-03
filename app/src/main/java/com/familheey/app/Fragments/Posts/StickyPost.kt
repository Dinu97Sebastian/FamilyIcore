package com.familheey.app.Fragments.Posts

import com.google.gson.annotations.SerializedName

data class StickyPost(

        @SerializedName("post_id") val postId: Int,
        @SerializedName("post_attachment") val postAttachment: List<PostAttachment>,
        @SerializedName("snap_description") val snapDescription: String,
        @SerializedName("created_user_name") val createdUserName: String,
        @SerializedName("created_user_id") val createdUserId: Int,
        @SerializedName("adminOf") val adminOf: String
)