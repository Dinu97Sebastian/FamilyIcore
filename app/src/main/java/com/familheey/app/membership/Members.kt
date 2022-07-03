import com.google.gson.annotations.SerializedName

data class Members (
        @SerializedName("data") val data: List<Data>,
        @SerializedName("adminsCount") val adminsCount: Int
)

