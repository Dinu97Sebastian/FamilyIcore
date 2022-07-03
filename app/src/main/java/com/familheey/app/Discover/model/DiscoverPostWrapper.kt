package com.familheey.app.Discover.model

import com.familheey.app.Fragments.Posts.PostData
import com.google.gson.annotations.SerializedName
import java.util.*

data class DiscoverPostWrapper(
        @SerializedName("data") val data: ArrayList<PostData>
)