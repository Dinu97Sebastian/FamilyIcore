package com.familheey.app.Topic


import com.google.gson.annotations.SerializedName

data class UserWrapper(
        @SerializedName("connectionUsers")
        val connectionUsers: List<User>?,
        @SerializedName("nonConnectionUsers")
        val nonConnectionUsers: List<User>?,
        @SerializedName("topicUsers")
        val topicUsers: List<User>?
) {
    fun getMergedUsers(): List<User> {
        val mergedUsers = mutableListOf<User>()
        if (topicUsers != null)
            for (topicUser in topicUsers) {
                topicUser.alreadySelected = true
                topicUser.currentSelection = true
                mergedUsers.add(topicUser)
            }
        if (connectionUsers != null) {
            for (connectionUser in connectionUsers) {
                connectionUser.alreadySelected = false
                connectionUser.currentSelection = false
                mergedUsers.add(connectionUser)
            }
        }
        if (nonConnectionUsers != null) {
            for (nonConnectionUser in nonConnectionUsers) {
                nonConnectionUser.alreadySelected = false
                nonConnectionUser.currentSelection = false
                mergedUsers.add(nonConnectionUser)
            }
        }
        return mergedUsers
    }
}