package com.familheey.app.Models


import com.google.gson.annotations.SerializedName

data class SuggestedFamily(
        @SerializedName("base_region")
        val baseRegion: String,
        @SerializedName("created_by")
        val createdBy: String,
        @SerializedName("group_category")
        val groupCategory: String,
        @SerializedName("group_name")
        val groupName: String,
        @SerializedName("group_type")
        val groupType: String,
        @SerializedName("id")
        val id: Int,
        @SerializedName("is_active")
        val isActive: Boolean,
        @SerializedName("logo")
        val logo: String,
        @SerializedName("propic")
        val propic: String
)