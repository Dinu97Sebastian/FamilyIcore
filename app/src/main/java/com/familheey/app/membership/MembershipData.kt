import com.google.gson.annotations.SerializedName

data class MembershipData(

        @SerializedName("membershipid") val membershipid: Int,
        @SerializedName("membership_name") val membershipName: String,
        @SerializedName("member_count") val memberCount: Int
)