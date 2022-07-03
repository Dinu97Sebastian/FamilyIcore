package com.familheey.app.membership


import com.google.gson.annotations.SerializedName

data class Membership(
        @SerializedName("id")
        val id: String? = null,
        @SerializedName("membership_name")
        val membershipName: String?,
        @SerializedName("group_id")
        val groupId: String?,
        @SerializedName("created_by")
        val createdBy: String?,
        @SerializedName("membership_period")
        val membershipPeriod: String?,
        @SerializedName("membership_period_type_id")
        val membershipPeriodTypeId: String?,
        @SerializedName("membership_currency")
        val membershipCurrency: String?,
        @SerializedName("membership_fees")
        val membershipFees: Int?,
        @SerializedName("is_active")
        val isActive: Boolean?


)