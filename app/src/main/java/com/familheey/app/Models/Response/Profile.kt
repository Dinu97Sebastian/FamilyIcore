package com.familheey.app.Models.Response

import com.google.gson.annotations.SerializedName

data class Profile(

        @SerializedName("id") val id: Int,
        @SerializedName("full_name") val fullName: String,
        @SerializedName("email") val email: String,
        @SerializedName("phone") val phone: String,
        @SerializedName("propic") val propic: String,
        @SerializedName("cover_pic") val coverPic: String,
        @SerializedName("location") val location: String,
        @SerializedName("origin") val origin: String,
        @SerializedName("dob") val dob: String,
        @SerializedName("gender") val gender: String,
        @SerializedName("about") val about: String,
        @SerializedName("work") val work: String,
        @SerializedName("created_at") val createdAt: String,
        @SerializedName("is_active") val isActive: Boolean,
        @SerializedName("is_verified") val isVerified: Boolean,
        @SerializedName("user_original_image") val userOriginalImage: String,
        @SerializedName("auto_password") val autoPassword: String,
        @SerializedName("notification") val notification: Boolean,
        @SerializedName("family_notification_off") val familyNotificationOff: ArrayList<Int>? = ArrayList(),
        @SerializedName("searchable") val searchable: Boolean
)