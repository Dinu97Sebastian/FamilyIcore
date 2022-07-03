package com.familheey.app.Discover.model

import com.google.gson.annotations.SerializedName
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import java.util.*
import java.util.concurrent.TimeUnit

data class DiscoverEvent(
/*
* megha,(03/12/2021) for recurring events.*/
        @SerializedName("is_recurrence") val is_recurrence: Int,
        @SerializedName("recurrence_from_date") val recurrence_from_date: Long,
        @SerializedName("recurrence_to_date") val recurrence_to_date: Long,

        @SerializedName("id") val id: Int,
        @SerializedName("event_type") val eventType: String,
        @SerializedName("event_name") val eventName: String,
        @SerializedName("event_host") val eventHost: String,
        @SerializedName("location") val location: String?,
        @SerializedName("meeting_link") val meetingLink: String,
        @SerializedName("meeting_dial_number") val meetingDialNumber: String,
        @SerializedName("meeting_pin") val meetingPin: String,
        @SerializedName("meeting_logo") val meetingLogo: String,
        @SerializedName("is_sharable") val isSharable: Boolean,
        @SerializedName("created_user") val createdUser: String,
        @SerializedName("user_id") val userId: Int,
        @SerializedName("from_date") val fromDate: Long,
        @SerializedName("to_date") val toDate: Long,
        @SerializedName("event_image") val eventImage: String,
        @SerializedName("full_name") val fullName: String,
        @SerializedName("created_at_") val createdAt: Long,
        @SerializedName("first_person_going") val firstPersonGoing: String,
        @SerializedName("going_count") val goingCount: Int,
        @SerializedName("interested_count") val interestedCount: Int,
        @SerializedName("propic") val propic: String,
        @SerializedName("sortlocation") val sortlocation: String

) {
    fun getFormattedLocation(): String {
        if (this.location != null)
            return StringTokenizer(this.location, ",").nextToken() ?: ""
        else return ""
    }


    fun getFormattedDate(valu: Long, format: String): String? {
        var value: Long? = valu
        if (value != null) {
            value = TimeUnit.SECONDS.toMillis(value)
            val dateTime = DateTime(value)
            val dtfOut = DateTimeFormat.forPattern(format)
            return dtfOut.print(dateTime)
        }
        return ""
    }

    fun eventStartToEndDate(): String {
/*
* megha(03/12/2021) for showing correct event date on discover event list*/
        if (is_recurrence==1 && isSameDate(this.recurrence_from_date, this.recurrence_to_date)) {
            val fromTime1 = dateFormat(this.recurrence_from_date, "EEE MMM dd, yyyy hh:mm aa")
            val toTime1 = dateFormat(this.recurrence_to_date, "hh:mm aa")
            return (String.format(" %s - %s", fromTime1, toTime1))

        } else if (is_recurrence==1 && !isSameDate(this.recurrence_from_date,this.recurrence_to_date)){
            val fromTime = dateFormat(this.recurrence_from_date, "EEE MMM dd")
            val toTime = dateFormat(this.recurrence_to_date, "EEE MMM dd, yyyy")
            return (String.format("%s - %s", fromTime, toTime))

        }else if (is_recurrence==0 && isSameDate(this.fromDate, this.toDate)) {
            val fromTime1 = dateFormat(this.fromDate, "EEE MMM dd")
            val toTime1 = dateFormat(this.toDate, "EEE MMM dd, yyyy")
            return (String.format(" %s - %s", fromTime1, toTime1))

        } else {
            val fromTime = dateFormat(this.fromDate, "EEE MMM dd")
            val toTime = dateFormat(this.toDate, "EEE MMM dd, yyyy")
            return (String.format("%s - %s", fromTime, toTime))
        }
        }

//        if (isSameDate(this.fromDate, this.toDate)) {
//            val fromTime1 = dateFormat(this.fromDate, "EEE MMM dd, yyyy hh:mm aa")
//            val toTime1 = dateFormat(this.toDate, "hh:mm aa")
//            return (String.format(" %s - %s", fromTime1, toTime1))
//        } else {
//            val fromTime = dateFormat(this.fromDate, "EEE MMM dd")
//            val toTime = dateFormat(this.toDate, "EEE MMM dd, yyyy")
//            return (String.format("%s - %s", fromTime, toTime))
//        }


    fun isSameDate(from1: Long, to1: Long): Boolean {
        var from: Long? = from1
        var to: Long? = to1
        return if (from != null && to != null) {
            val dtfOut = DateTimeFormat.forPattern("yyyyMMdd")
            from = TimeUnit.SECONDS.toMillis(from)
            to = TimeUnit.SECONDS.toMillis(to)
            val date1 = DateTime(from)
            val date2 = DateTime(to)
            dtfOut.print(date1) == dtfOut.print(date2)
        } else false
    }

    fun dateFormat(value1: Long, format: String): String? {
        var value: Long? = value1
        if (value != null) {
            value = TimeUnit.SECONDS.toMillis(value)
            val dateTime = DateTime(value)
            val dtfOut = DateTimeFormat.forPattern(format)
            return dtfOut.print(dateTime)
        }
        return ""
    }
}

