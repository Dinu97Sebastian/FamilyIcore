import com.google.gson.annotations.SerializedName

data class Data(

        @SerializedName("id") val id: Int,
        @SerializedName("is_blocked") val isBlocked: Boolean,
        @SerializedName("is_removed") val isRemoved: Boolean,
        @SerializedName("group_id") val groupId: Int,
        @SerializedName("user_id") val userId: Int,
        @SerializedName("user_type") val userType: String,
        @SerializedName("member_since") val memberSince: String,
        @SerializedName("full_name") val fullName: String,
        @SerializedName("email") val email: String,
        @SerializedName("gender") val gender: String,
        @SerializedName("propic") val propic: String,
        @SerializedName("membership_id") val membershipId: Int,
        @SerializedName("membership_payment_status") val membershipPaymentStatus: String,
        @SerializedName("membership_from") val membershipFrom: String,
        @SerializedName("membership_to") val membershipTo: String?,
        @SerializedName("membership_fees") val membershipFees: String,
        @SerializedName("membership_customer_notes") val membershipCustomerNotes: String,
        @SerializedName("membership_payment_notes") val membershipPaymentNotes: String,
        @SerializedName("membership_paid_on") val membershipPaidOn: String,
        @SerializedName("membership_duration") val membershipDuration: String,
        @SerializedName("membership_period_type_id") val membershipPeriodTypeId: Int,
        @SerializedName("membership_type") val membershipType: String,
        @SerializedName("membership_period_type") val membershipPeriodType: String,
        @SerializedName("is_expaired") val isExpaired: Boolean,
        @SerializedName("membership_since") val membershipSince: MembershipSince,
        @SerializedName("is_primar_admin") val isPrimarAdmin: String,
        @SerializedName("crnt_user_id") val crntUserId: Int,
        @SerializedName("group_category") val groupCategory: String,
        @SerializedName("relation_ship") val relationShip: String,
        @SerializedName("relation_id") val relationId: String,
        @SerializedName("membership_total_payed_amount") val membershipTotalPayedAmount: String

)