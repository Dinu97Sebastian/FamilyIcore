import com.google.gson.annotations.SerializedName

data class Membershipcounts(

        @SerializedName("all_count") val allCount: Int,
        @SerializedName("expaired_count") val expairedCount: Int,
        @SerializedName("active_count") val activeCount: Int,
        @SerializedName("total_due_amount") val totalDueAmount: String,
        @SerializedName("default_membership_id") val defaultMembershipId: String
)