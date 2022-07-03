package com.familheey.app.Topic

import com.familheey.app.Models.Request.HistoryImages
import com.google.gson.annotations.SerializedName
import java.util.*

data class Topic(
        @SerializedName("created_at")
        val createdAt: String?,
        @SerializedName("created_by")
        val createdBy: Int?,
        @SerializedName("created_user")
        val createdUser: String?,
        @SerializedName("description")
        val description: String?,
        @SerializedName("is_accept")
        val isAccept: Boolean?,
        @SerializedName("is_active")
        val isActive: Boolean?,
        @SerializedName("title")
        val title: String?,

        @SerializedName("id")
        val id: Int?,
        @SerializedName("topic_id")
        val topicId: Int?,
        @SerializedName("topic_map_id")
        val topicMapId: Int?,
        @SerializedName("topic_attachment")
        var topicAttachment: ArrayList<HistoryImages>? = null,
        @SerializedName("conversation_count")
        val conversationCount: String?,
        @SerializedName("conversation_count_new")
        val conversationCountNew: String?,
        @SerializedName("to_users")
        val toUsers: ArrayList<String>? = null
) {
    fun getFormattedUsers(): String {
        var formattedUser = ""
        if (toUsers != null && toUsers.size > 0) {
            if (toUsers.size == 1) {
                formattedUser = toUsers[0]
            } else if (toUsers.size == 2) {
                formattedUser = toUsers[0] + ", " + toUsers[1]
            } else if (toUsers.size > 2) {
                formattedUser = toUsers[0] + ", " + toUsers[1] + " and " + (toUsers.size - 2) + " others"
            }
        }
        return formattedUser
    }
}