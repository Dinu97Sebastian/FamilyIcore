package com.familheey.app.Models

import com.familheey.app.Models.Response.Count
import com.familheey.app.Models.Response.Profile
import com.google.gson.annotations.SerializedName

data class ProfileWraper(
        @SerializedName("profile") val profile: Profile,
        @SerializedName("count") val count: Count
)