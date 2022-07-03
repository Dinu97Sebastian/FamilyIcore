package com.familheey.app.Models


import com.familheey.app.Models.Response.FamilySearchModal
import com.google.gson.annotations.SerializedName

data class FamilySuggestionWrapper(
        @SerializedName("code")
        val code: Int,
        @SerializedName("data")
        val suggestedFamily: List<FamilySearchModal>
)