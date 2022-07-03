import com.google.gson.annotations.SerializedName

data class MembershipTypePeriod(
        @SerializedName("id") val id: Int,
        @SerializedName("membership_lookup_period_type") val membershipLookupPeriodType: String,
        @SerializedName("membership_lookup_period") val membershipLookupPeriod: Int
)