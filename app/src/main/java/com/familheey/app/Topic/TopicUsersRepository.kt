package com.familheey.app.Topic

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.familheey.app.Utilities.SharedPref
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class TopicUsersRepository(val context: Context) {
    private var users = mutableListOf<User>()
    private var mutableLiveData = MutableLiveData<List<User>>()
    val compositeDisposable = CompositeDisposable()


    fun getMutableLiveData(topicId: String, searchQuery: String): MutableLiveData<List<User>> {
        val jsonObject = JsonObject()
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().id)
        jsonObject.addProperty("topic_id", topicId)
        jsonObject.addProperty("txt", searchQuery)
        val requestBody: RequestBody = jsonObject.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        compositeDisposable.add(
                RetrofitUtil.createRxResource(context, RestApiService::class.java).getUsersForTopics(requestBody).observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ response ->
                            val userWrapper = response.body()
                            if (userWrapper != null) {
                                users = userWrapper.getMergedUsers() as MutableList<User>
                                mutableLiveData.value = users
                            }
                        }, {
                        })
        )
        return mutableLiveData
    }
}