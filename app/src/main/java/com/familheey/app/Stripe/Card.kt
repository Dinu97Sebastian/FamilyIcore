package com.familheey.app.Stripe

import com.google.gson.annotations.SerializedName

data class Card(
        @SerializedName("id") val id: String,
        @SerializedName("address_city") val addressCity: String,
        @SerializedName("address_country") val addressCountry: String,
        @SerializedName("address_line1") val addressLine1: String,
        @SerializedName("address_line1_check") val addressLine1Check: String,
        @SerializedName("address_line2") val addressLine2: String,
        @SerializedName("address_state") val addressState: String,
        @SerializedName("address_zip") val addressZip: String,
        @SerializedName("address_zip_check") val addressZipCheck: String,
        @SerializedName("brand") val brand: String,
        @SerializedName("country") val country: String,
        @SerializedName("customer") val customer: String,
        @SerializedName("cvc_check") val cvcCheck: String,
        @SerializedName("dynamic_last4") val dynamicLast4: String,
        @SerializedName("exp_month") val expMonth: Int,
        @SerializedName("exp_year") val expYear: Int,
        @SerializedName("fingerprint") val fingerprint: String,
        @SerializedName("funding") val funding: String,
        @SerializedName("last4") val last4: Int,
        @SerializedName("name") val name: String,
        @SerializedName("tokenization_method") val tokenizationMethod: String,
        var isSelected: Boolean = false
)