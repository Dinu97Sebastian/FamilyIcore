package com.familheey.app.Notification

import com.google.gson.annotations.SerializedName

data class AdminAcceptResponse(

		@SerializedName("data") val data: List<Datas>
)


data class Datas(

        @SerializedName("action") val action: String,
        @SerializedName("message") val message: String
)