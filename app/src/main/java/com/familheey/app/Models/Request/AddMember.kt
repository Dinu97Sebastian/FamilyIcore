package com.familheey.app.Models.Request

import com.google.gson.annotations.SerializedName


data class AddMember(

        @SerializedName("userId") var userId: List<Int>? = ArrayList(),
        @SerializedName("from_id") var fromId: String = "",
        @SerializedName("groupId") var groupId: String = ""
)