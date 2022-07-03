package com.familheey.app.Models.Response

import com.google.gson.annotations.SerializedName


data class Member(

        @SerializedName("id") val id: Int,
        @SerializedName("full_name") val fullName: String,
        @SerializedName("propic") val propic: String,
        @SerializedName("location") val location: String,
        @SerializedName("type") val type: String,
        @SerializedName("about") val about: String,
        @SerializedName("work") val work: String,
        @SerializedName("exists") val exists: Boolean,
        @SerializedName("selected") var selected: Boolean = false,
        @SerializedName("FLAG") val fLAG: Boolean,
        @SerializedName("invitation_status") val invitationStatus: Boolean
)