package com.familheey.app.Topic

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class TopicUsersViewModelFactory(val application: Application, val topicId: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return TopicUsersViewModel(application, topicId) as T
    }
}