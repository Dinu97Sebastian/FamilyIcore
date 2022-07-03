package com.familheey.app.Topic

import com.familheey.app.Models.Request.HistoryImages
import com.google.gson.annotations.SerializedName

data class TopicCreateRequest(
        @SerializedName("created_by") val created_by: String,
        @SerializedName("title") val title: String,
        @SerializedName("description") val description: String,
        @SerializedName("topic_attachment") val topicAttachment: ArrayList<HistoryImages>,
        @SerializedName("to_user_id") val toUserId: ArrayList<String>)

