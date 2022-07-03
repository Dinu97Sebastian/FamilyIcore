package com.familheey.app.Topic

import com.familheey.app.Fragments.Posts.PostAttachment
import com.google.gson.annotations.SerializedName
import org.ocpsoft.prettytime.PrettyTime
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

data class PostMessage(


        @SerializedName("total_comments")
        val totalComments: String?,

        @SerializedName("commented_user_count")
        val commentedUserCount: String?,

        @SerializedName("last_commented_user")
        val lastCommentedUser: String?,
        @SerializedName("group_name")
        val groupName: String?,
        @SerializedName("post_id")
        val postId: String?,
        @SerializedName("last_comment")
        val lastComment: String?,
        @SerializedName("last_commented_date")
        val lastCommentedDate: String?,
        @SerializedName("snap_description")
        val snapDescription: String?,
        @SerializedName("unread_conversation_count")
        var unreadConversationCount: String?,
        @SerializedName("post_attachment")
        val postAttachment: ArrayList<PostAttachment>?,
        @SerializedName("publish_type")
        val publishType: String?,
        @SerializedName("last_comment_attachment")
        val lastCommentAttachment: ArrayList<PostAttachment>?

) {

    fun getFormattedDate(): String? {
        val dateFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val tzInAmerica = TimeZone.getTimeZone("IST")
        dateFormatter.timeZone = tzInAmerica
        try {
            val date = dateFormatter.parse(lastCommentedDate!!)
            val p = PrettyTime()
            return p.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return "Just now"
    }

}