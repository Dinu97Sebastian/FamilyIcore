package com.familheey.app.Topic


import com.google.gson.annotations.SerializedName

data class TopicsBaseModel(
        @SerializedName("code")
        val code: Int,
        @SerializedName("data")
        val data: List<Topic>
)