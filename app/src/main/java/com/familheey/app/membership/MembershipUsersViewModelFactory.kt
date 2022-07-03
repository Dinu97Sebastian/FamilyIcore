package com.familheey.app.membership

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MembershipUsersViewModelFactory(val application: Application, val groupId: String, val type: String, val membershipId: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MemberListingViewModel(application, groupId, type, membershipId) as T
    }
}