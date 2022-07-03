package com.familheey.app.Discover.model

import com.google.gson.annotations.SerializedName

data class DiscoverWrapper(
        @SerializedName("users") val users: List<DiscoverUsers>,
        @SerializedName("events") val events: List<DiscoverEvent>,
        @SerializedName("groups") val groups: List<DiscoverGroups>,
        @SerializedName("posts") val posts: DiscoverPostWrapper

)