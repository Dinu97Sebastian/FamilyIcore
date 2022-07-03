package com.familheey.app.Topic

import com.familheey.app.Models.Request.HistoryImages
import com.google.gson.annotations.SerializedName

data class TopicCreateUpdateRequest(
        @SerializedName("topic_id") val topicId: String,
        @SerializedName("updated_by") val updatedBy: String,
        @SerializedName("created_by") val createdBy: String,
        @SerializedName("title") val title: String,
        @SerializedName("description") val description: String,
        @SerializedName("topic_attachment") val topicAttachment: ArrayList<HistoryImages>,
        @SerializedName("to_user_id") val toUserId: ArrayList<Int>)

