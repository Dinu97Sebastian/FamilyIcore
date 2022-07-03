package com.familheey.app.Topic


import com.google.gson.annotations.SerializedName

data class TopicsCreateResponse(
        @SerializedName("code")
        val code: Int,
        @SerializedName("data")
        val data: Topic
)