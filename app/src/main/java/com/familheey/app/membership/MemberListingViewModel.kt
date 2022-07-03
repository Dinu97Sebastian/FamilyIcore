package com.familheey.app.membership

import Data
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData

class MemberListingViewModel(application: Application, private val groupId: String, private val type: String, private val membershipId: String) : AndroidViewModel(application) {
    private val topicsListingRepository = MemberListingRepository(application.applicationContext)
    val topics: LiveData<List<Data>> get() = topicsListingRepository.getMutableLiveData(groupId, type, membershipId)

    fun getTopics(group_id: String, membership_id: String) {
        topicsListingRepository.getMutableLiveData(group_id, type, membership_id)
    }

    fun getReminder(groupId: String, userId: String) {
        topicsListingRepository.sendReminder(groupId, userId)
    }

    override fun onCleared() {
        super.onCleared()
        topicsListingRepository.compositeDisposable.dispose()
    }
}