package com.familheey.app.Topic


import com.google.gson.annotations.SerializedName

data class User(
        @SerializedName("full_name")
    val fullName: String?,
        @SerializedName("is_accept")
    val isAccept: Boolean?,
        @SerializedName("location")
    val location: String?,
        @SerializedName("propic")
    val propic: String?,
        @SerializedName("user_id")
        val userId: Int?,
        @Transient
        var alreadySelected: Boolean = false,
        @Transient
        var currentSelection: Boolean = false
)