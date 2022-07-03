import com.google.gson.annotations.SerializedName

data class MembershipDashboard(
        @SerializedName("membership_data") val membershipData: List<MembershipData>,
        @SerializedName("membership_counts") val membershipCounts: List<Membershipcounts>
)