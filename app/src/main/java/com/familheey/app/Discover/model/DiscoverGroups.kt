package com.familheey.app.Discover.model

import com.familheey.app.Models.Response.FamilySearchModal
import com.google.gson.annotations.SerializedName
import java.util.*
//CHECKSTYLE:OFF
data class DiscoverGroups(
        @SerializedName("group_id") var groupId: Int,
        @SerializedName("created_by") var createdBy: String?,
        @SerializedName("is_joined") var isJoined: String?,
        @SerializedName("is_removed") var isRemoved: Boolean,
        @SerializedName("is_blocked") var isBlocked: String,
        @SerializedName("group_name") var groupName: String,
        @SerializedName("logo") val logo: String,
        @SerializedName("base_region") val baseRegion: String,
        @SerializedName("group_type") val groupType: String,
        @SerializedName("group_category") val groupCategory: String,
        @SerializedName("visibility") val visibility: String,
        @SerializedName("member_approval") val memberApproval: Int,
        @SerializedName("created_by_id") val createdById: Int,
        @SerializedName("created_by_name") val createdByName: String,
        @SerializedName("propic") val propic: String,
        @SerializedName("member_joining") var memberJoining: Int,
        @SerializedName("is_linkable") val isLinkable: Boolean,
        @SerializedName("intro") val intro: String,
        @SerializedName("type") var type: String,
        @SerializedName("status") var status: String,
        @SerializedName("req_id") val reqId: Int,
        @SerializedName("from_id") val fromId: Int,
        @SerializedName("id") val id: Int,
        @SerializedName("membercount") val membercount: Int,
        @SerializedName("selected") var selected: Boolean = false,
        @SerializedName("post_count") val postCount: Int
) {
    fun getFormattedLocation(): String {
        return StringTokenizer(this.baseRegion, ",").nextToken() ?: ""
    }

    fun getUserJoinedCalculated(): Int {
        return if (isJoined != null && isJoined.equals("1", ignoreCase = true)) {
            if (isRemoved != null && isRemoved) {
                if (status != null && status.equals("Pending", ignoreCase = true)) {
                    if ("invite".equals(type, ignoreCase = true)) FamilySearchModal.ACCEPT_INVITATION else FamilySearchModal.PENDING
                } else
                    /*megha(12/11/2021)
                    * for chnging the join button respective to the settings of families*/
                    if ((isJoined==null && memberJoining==1)||(isJoined=="1"&& memberJoining==1)){
                        FamilySearchModal.PRIVATE
                    }else{
                        FamilySearchModal.JOIN
                    }
            } else {
                FamilySearchModal.JOINED
            }
        } else if ((memberJoining != null && memberJoining == 1)) {
            FamilySearchModal.PRIVATE
        } else if (status != null && status.equals("Pending", ignoreCase = true)) {
            if ("invite".equals(type, ignoreCase = true)) FamilySearchModal.ACCEPT_INVITATION else FamilySearchModal.PENDING
        } else if (status != null && status.equals("Rejected", ignoreCase = true)) {
            FamilySearchModal.REJECTED
        } else FamilySearchModal.JOIN
    }
}

//CHECKSTYLE:ON