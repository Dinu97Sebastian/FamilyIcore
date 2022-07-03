package com.familheey.app.PaymentHistory

import com.google.gson.annotations.SerializedName

data class PaymentWrapper(
        @SerializedName("code") val code: Int,
        @SerializedName("message") val message: String,
        @SerializedName("data") val data: List<Data>
)