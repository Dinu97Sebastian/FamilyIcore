import com.google.gson.annotations.SerializedName

data class MyFamilySelection(

        @SerializedName("id") val id: Int,
        @SerializedName("group_name") val groupName: String,
        @SerializedName("base_region") val baseRegion: String,
        @SerializedName("logo") val logo: String,
        @SerializedName("stripe_account_id") val stripeAccountId: String,
        @SerializedName("full_name") val fullName: String,
        @SerializedName("propic") val propic: String
)