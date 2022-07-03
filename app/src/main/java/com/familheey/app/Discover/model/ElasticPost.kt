package com.familheey.app.Discover.model

import com.familheey.app.Fragments.Posts.PostData
import com.google.gson.annotations.SerializedName

data class ElasticPost(
        @SerializedName("_source") val _source: PostData
)