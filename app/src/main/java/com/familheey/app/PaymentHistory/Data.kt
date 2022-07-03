package com.familheey.app.PaymentHistory

import com.google.gson.annotations.SerializedName

data class Data(
        @SerializedName("id") val id: Int,
        @SerializedName("item_id") val itemId: Int,
        @SerializedName("request_item_title") val requestItemTitle: String,
        @SerializedName("payment_status") val paymentStatus: String,
        @SerializedName("contribute_item_quantity") val contributeItemQuantity: String,
        @SerializedName("membership_name") val membershipName: String,
        @SerializedName("group_id") val groupId: Int,
        @SerializedName("group_name") val groupName: String,
        @SerializedName("logo") val logo: String,
        @SerializedName("user_id") val userId: String,
        @SerializedName("full_name") val fullName: String,
        @SerializedName("propic") val propic: String,
        @SerializedName("membership_id") val membershipId: Int,
        @SerializedName("paid_on") val paidOn: Long,
        @SerializedName("membership_fees") val membershipFees: Int,
        @SerializedName("membership_from") val membershipFrom: Long,
        @SerializedName("membership_paid_on") val membershipPaidOn: Long,
        @SerializedName("membership_payed_amount") val membershipPayedAmount: Int,
        @SerializedName("membership_to") val membershipTo: Long,
        @SerializedName("membership_total_payed_amount") val membershipTotalPayedAmount: Int,
        @SerializedName("membership_payment_notes") val membershipPaymentNotes: String,
        @SerializedName("membership_customer_notes") val membershipCustomerNotes: String,
        @SerializedName("payment_note") val paymentNote: String


)