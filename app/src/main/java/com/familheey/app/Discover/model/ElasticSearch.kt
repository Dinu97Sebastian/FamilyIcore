package com.familheey.app.Discover.model

import com.google.gson.annotations.SerializedName

data class ElasticSearch(
        @SerializedName("hits") val hits: ArrayList<ElasticPost>
)