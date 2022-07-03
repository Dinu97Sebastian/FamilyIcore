package com.familheey.app.Models.Response

import com.google.gson.annotations.SerializedName

data class NotificationSeetings(
        @SerializedName("announcement_notification") var announcementNotification: Boolean,
        @SerializedName("notification") var notification: Boolean,
        @SerializedName("public_notification") var publicNotification: Boolean,
        @SerializedName("conversation_notification") var conversationNotification: Boolean,
        @SerializedName("event_notification") var eventNotification: Boolean,
        @SerializedName("family_notification_off") var familyNotificationOff: ArrayList<Int>? = ArrayList(),
        @SerializedName("families") var familyIds:ArrayList<Int>? = ArrayList(),
        @SerializedName("family_count") var familyCount: Int? = 0)

