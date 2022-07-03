package com.familheey.app.Topic

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData

class TopicsListingViewModel(application: Application) : AndroidViewModel(application) {
    private val topicsListingRepository = TopicsListingRepository(application.applicationContext)
    val topics: LiveData<List<Topic>> get() = topicsListingRepository.getMutableLiveData("")

    fun getTopics(searchQuery: String) {
        topicsListingRepository.getMutableLiveData(searchQuery)
    }

    override fun onCleared() {
        super.onCleared()
        topicsListingRepository.compositeDisposable.dispose()
    }
}