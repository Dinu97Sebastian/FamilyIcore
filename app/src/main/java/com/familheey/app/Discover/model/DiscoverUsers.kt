package com.familheey.app.Discover.model

import com.google.gson.annotations.SerializedName
import java.util.*

data class DiscoverUsers(
        @SerializedName("id") val id: Int,
        @SerializedName("full_name") val fullName: String,
        @SerializedName("location") val location: String,
        @SerializedName("origin") val origin: String,
        @SerializedName("gender") val gender: String,
        @SerializedName("propic") val propic: String,
        @SerializedName("familycount") val familycount: Int,
        @SerializedName("sortlocation") val sortlocation: String,
        @SerializedName("logined_user_family_count") val loginedUserFamilyCount: Int,
        @SerializedName("type") val type: String,
        @SerializedName("exists") val exists: Boolean,
        @SerializedName("selected") var selected: Boolean = false,
        @SerializedName("invitation_status") val invitationStatus: Boolean
) {
    fun getFormattedLocation(): String {
        return StringTokenizer(this.location, ",").nextToken() ?: ""
    }
}