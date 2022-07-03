import com.google.gson.annotations.SerializedName

data class WraperMembershipType (

		@SerializedName("message") val message : String,
		@SerializedName("doc") val doc : Doc
)