package com.familheey.app.Notification

import android.content.Context
import android.content.Intent
import com.familheey.app.Activities.*
import com.familheey.app.Announcement.AnnouncementDetailActivity
import com.familheey.app.Models.Response.EventDetail
import com.familheey.app.Need.NeedRequestDetailedActivity
import com.familheey.app.Post.PostCommentActivity
import com.familheey.app.Post.PostDetailForPushActivity
import com.familheey.app.Topic.MainActivity
import com.familheey.app.Topic.TopicChatActivity
import com.familheey.app.Utilities.Constants
import com.google.gson.annotations.SerializedName
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import java.util.concurrent.TimeUnit

//CHECKSTYLE:OFF
data class Activity(var key: String? = "",
                    @SerializedName("event_id") var eventId: String? = "",
                    @SerializedName("from_id") var fromId: String? = "",
                    @SerializedName("group_id") var groupId: String? = "",
                    @SerializedName("category") var category: String? = "",
                    @SerializedName("create_time") var createTime: String? = "",
                    @SerializedName("rsvp") var rsvp: Boolean? = false,
                    @SerializedName("from_date") var fromDate: Long? = 0,
                    @SerializedName("type_id") var typeId: String? = "",
                    @SerializedName("visible_status") var visibleStatus: String? = "",
                    @SerializedName("type") var type: String? = "",
                    @SerializedName("message") var message: String? = "",
                    @SerializedName("privacy_type") var privacyType: String? = "",
                    @SerializedName("message_title") var messageTitle: String? = "",
                    @SerializedName("propic") var propic: String? = "",
                    @SerializedName("sub_type") var subType: String? = "",
                    @SerializedName("ilink_tod") var linkTo: String? = "",
                    @SerializedName("tittle") var tittle: String = "",
                    @SerializedName("to_group_name") var toGroupName: String = "",
                    @SerializedName("description") var description: String = "",
                    @SerializedName("created_by_user") var createdByUser: String = "",
                    @SerializedName("created_by_propic") var createdByPropic: String = "",
                    @SerializedName("location") var location: String = "",
                    @SerializedName("membercount") var membercount: String = "",
                    @SerializedName("cover_image") var coverImage: String = "",
                    @SerializedName("comment") var comment: String? = "")
{


    fun dateFormat(): String? {
        var value: Long? = fromDate
        if (value != null) {
            value = TimeUnit.SECONDS.toMillis(value)
            val dateTime = DateTime(value)
            val dtfOut = DateTimeFormat.forPattern("dd MMM yyyy")
            return dtfOut.print(dateTime)
        }
        return ""
    }

    fun isMessageRead(): Boolean {
        return visibleStatus.equals("read", ignoreCase = true)
    }

    fun goToCorrespondingDashboard(context: Context): Intent {

        val intent: Intent
        when (type) {
            "home" -> {
                intent = if ("familyfeed" == subType || "publicfeed" == subType) {
                    Intent(context, PostDetailForPushActivity::class.java).putExtra("ids", typeId.toString()).putExtra(Constants.Bundle.TYPE, "NOTIFICATION")
                } else if ("requestFeed" == subType) {
                    var s = "NOTIFICATION"
                    if (category.equals("contribution_create")) {
                        s = "INAPP"
                    }

                    Intent(context, NeedRequestDetailedActivity::class.java).putExtra("FROM", s).putExtra(Constants.Bundle.TYPE, Constants.Bundle.MEMBER).putExtra(Constants.Bundle.DATA, typeId.toString())
                } else {
                    Intent(context, MainActivity::class.java)
                }
            }
            "announcement" -> {
                intent = if ("conversation" == subType) {
                    Intent(context, PostCommentActivity::class.java)
                            .putExtra(Constants.Bundle.DATA, typeId.toString())
                            .putExtra("POS", 0)
                            .putExtra(Constants.Bundle.TYPE, "NOTIFICATION")
                            .putExtra(Constants.Bundle.SUB_TYPE, "ANNOUNCEMENT")
                } else {
                    Intent(context, AnnouncementDetailActivity::class.java).putExtra(Constants.Bundle.TYPE, "NOTIFICATION").putExtra("id", typeId.toString())
                }
            }
            "event" -> {
                val eventDetail = EventDetail()
                eventDetail.eventId = typeId.toString()
                when (subType) {
                    Constants.Bundle.GUEST_INTERESTED -> intent = Intent(context, GuestActivity::class.java).putExtra(Constants.Bundle.TYPE, Constants.Bundle.PUSH).putExtra(Constants.Bundle.DATA, typeId.toString()).putExtra(Constants.Bundle.POSITION, Constants.Bundle.GUEST_MAY_ATTEND)
                    Constants.Bundle.GUEST_ATTENDING -> intent = Intent(context, GuestActivity::class.java).putExtra(Constants.Bundle.TYPE, Constants.Bundle.PUSH).putExtra(Constants.Bundle.DATA, typeId.toString()).putExtra(Constants.Bundle.POSITION, Constants.Bundle.GUEST_ATTENDING)
                    "calendar" -> intent = Intent(context, CalendarActivity::class.java).putExtra(Constants.Bundle.TYPE, Constants.Bundle.PUSH).putExtra(Constants.Bundle.ID, typeId.toString()).putExtra(Constants.Bundle.SUB_TYPE, subType)
                    else -> intent = Intent(context, CreatedEventDetailActivity::class.java).putExtra(Constants.Bundle.TYPE, Constants.Bundle.NOTIFICATION).putExtra(Constants.Bundle.ID, typeId.toString()).putExtra(Constants.Bundle.SUB_TYPE, subType)
                }
            }
            "member_count" -> {
                intent = Intent(context, FamilyAddMemberActivity::class.java).putExtra(Constants.Bundle.NOTIFICATION, "NOTIFICATION").putExtra(Constants.Bundle.TYPE, Constants.Bundle.INVITE).putExtra(Constants.Bundle.DATA, typeId.toString())
            }
            "family" -> {
                when (subType) {
                    "member" -> intent = Intent(context, FamilyDashboardActivity::class.java).putExtra(Constants.Bundle.NOTIFICATION, "NOTIFICATION").putExtra(Constants.Bundle.TYPE, Constants.Bundle.MEMBER).putExtra(Constants.Bundle.DATA, typeId.toString())
                    "request" -> intent = Intent(context, FamilyDashboardActivity::class.java).putExtra(Constants.Bundle.NOTIFICATION, "NOTIFICATION").putExtra(Constants.Bundle.TYPE, Constants.Bundle.REQUEST).putExtra(Constants.Bundle.DATA, typeId.toString())
                    "family_link" -> intent = Intent(context, FamilyDashboardActivity::class.java).putExtra(Constants.Bundle.NOTIFICATION, "NOTIFICATION").putExtra(Constants.Bundle.TYPE, Constants.Bundle.LINK_FAMILY_REQUEST).putExtra(Constants.Bundle.DATA, typeId.toString())
                    "fetch_link" -> intent = Intent(context, FamilyDashboardActivity::class.java).putExtra(Constants.Bundle.NOTIFICATION, "NOTIFICATION").putExtra(Constants.Bundle.TYPE, Constants.Bundle.LINKED_FAMILIES).putExtra(Constants.Bundle.DATA, typeId.toString())
                    "about" -> {
                        if (category!!.contains("Membership reminder")) {
                            intent = Intent(context, FamilyDashboardActivity::class.java).putExtra("PAY", "PAY").putExtra(Constants.Bundle.NOTIFICATION, "NOTIFICATION").putExtra(Constants.Bundle.TYPE, Constants.Bundle.PUSH).putExtra(Constants.Bundle.DATA, typeId.toString())
                        } else
                            intent = Intent(context, FamilyDashboardActivity::class.java).putExtra(Constants.Bundle.NOTIFICATION, "NOTIFICATION").putExtra(Constants.Bundle.TYPE, Constants.Bundle.PUSH).putExtra(Constants.Bundle.DATA, typeId.toString())
                    }

                    else -> intent = Intent(context, FamilyDashboardActivity::class.java).putExtra(Constants.Bundle.NOTIFICATION, "NOTIFICATION").putExtra(Constants.Bundle.TYPE, Constants.Bundle.PUSH).putExtra(Constants.Bundle.DATA, typeId.toString())
                }
            }
            "user" -> intent = if ("request" == subType) {
                Intent(context, ProfileActivity::class.java).putExtra(Constants.Bundle.TYPE, Constants.Bundle.REQUEST).putExtra(Constants.Bundle.DATA, typeId.toString())
            }
            else {
                Intent(context, ProfileActivity::class.java).putExtra(Constants.Bundle.TYPE, Constants.Bundle.PUSH).putExtra(Constants.Bundle.DATA, typeId.toString())
            }

            "profile" -> intent =   Intent(context, ProfileActivity::class.java).putExtra(Constants.Bundle.TYPE, Constants.Bundle.PUSH).putExtra(Constants.Bundle.DATA, typeId.toString())


            "request" -> intent = Intent(context, NeedRequestDetailedActivity::class.java).putExtra("FROM", "INAPP").putExtra(Constants.Bundle.TYPE, Constants.Bundle.MEMBER).putExtra(Constants.Bundle.DATA, typeId.toString())
            "post" -> intent = Intent(context, PostCommentActivity::class.java)
                    .putExtra(Constants.Bundle.DATA, typeId.toString())
                    .putExtra("POS", 0)
                    .putExtra(Constants.Bundle.TYPE, "NOTIFICATION")
                    .putExtra(Constants.Bundle.SUB_TYPE, "POST")
            "topic" -> intent = Intent(context, TopicChatActivity::class.java).putExtra(Constants.Bundle.DATA, typeId.toString())
            else -> intent = Intent(context, MainActivity::class.java)
        }
        return intent
    }
//CHECKSTYLE:ON
}