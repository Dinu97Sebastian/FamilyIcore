package com.familheey.app.Models.Response

import com.familheey.app.Fragments.Posts.PostData
import com.google.gson.annotations.SerializedName
import java.util.ArrayList

data class PostDataResponse(
        @SerializedName("data") val data: ArrayList<PostData>
)
