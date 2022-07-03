import com.google.gson.annotations.SerializedName

data class Type(
        @SerializedName("id") val id: Int,
        @SerializedName("type_name") val typeName: String,
        @SerializedName("type_value") val typeValue: Int
)