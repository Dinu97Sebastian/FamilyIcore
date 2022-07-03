import com.google.gson.annotations.SerializedName

data class MembershipType(

        @SerializedName("id") val id: Int,
        @SerializedName("membership_name") val membershipName: String,
        @SerializedName("created_by") val createdBy: Int,
        @SerializedName("membership_period") val membershipPeriod: Int,
        @SerializedName("membership_period_type") val membershipPeriodType: String,
        @SerializedName("membership_fees") val membershipFees: Int,
        @SerializedName("membership_period_type_id") val membershipPeriodTypeId: Int,
        @SerializedName("membership_currency") val membershipCurrency: String,
        @SerializedName("is_active") val isActive: Boolean,
        @SerializedName("types") val types: ArrayList<Type>
)