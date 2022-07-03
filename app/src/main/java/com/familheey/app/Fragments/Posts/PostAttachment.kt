package com.familheey.app.Fragments.Posts

import com.google.gson.annotations.SerializedName

data class PostAttachment(
        @SerializedName("filename") val filename: String,
        @SerializedName("video_thumb") val videoThumb: String,
        @SerializedName("height") val height: Int,
        @SerializedName("height1") val height1: Int,
        @SerializedName("type") val type: String,
        @SerializedName("width") val width: Int
)