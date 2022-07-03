package com.familheey.app.Topic

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData


class TopicUsersViewModel(application: Application, private val topicId: String) : AndroidViewModel(application) {
    private val topicUsersRepository = TopicUsersRepository(application.applicationContext)
    val users: LiveData<List<User>> get() = topicUsersRepository.getMutableLiveData(topicId, "")

    fun getTopicUsers(topicId: String, searchQuery: String) {
        topicUsersRepository.getMutableLiveData(topicId, searchQuery)
    }

    override fun onCleared() {
        super.onCleared()
        topicUsersRepository.compositeDisposable.dispose()
    }
}