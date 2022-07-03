import com.google.gson.annotations.SerializedName

data class MembershipSince (

		@SerializedName("months") val months : Int,
		@SerializedName("days") val days : Int,
		@SerializedName("hours") val hours : Int,
		@SerializedName("minutes") val minutes : Int,
		@SerializedName("seconds") val seconds : Int
)