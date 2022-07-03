package com.familheey.app.Models.Response

import com.google.gson.annotations.SerializedName


data class Count(

        @SerializedName("connections") val connections: Int,
        @SerializedName("familyCount") val familyCount: Int,
        @SerializedName("mutual_families") val mutualFamilies: Int,
        @SerializedName("mutual_connections") val mutualConnections: Int
)