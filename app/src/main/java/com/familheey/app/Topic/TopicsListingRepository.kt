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

class TopicsListingRepository(val context: Context) {
    private var topics = mutableListOf<Topic>()
    private var mutableLiveData = MutableLiveData<List<Topic>>()
    val compositeDisposable = CompositeDisposable()


    fun getMutableLiveData(searchQuery: String): MutableLiveData<List<Topic>> {
        val jsonObject = JsonObject()
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().id)
        jsonObject.addProperty("txt", searchQuery)
        val requestBody: RequestBody = jsonObject.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        compositeDisposable.add(
                RetrofitUtil.createRxResource(context, RestApiService::class.java).getTopics(requestBody).observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ response ->
                            val topicsBaseModel: TopicsBaseModel? = response.body()
                            if (topicsBaseModel?.data != null) {
                                topics = topicsBaseModel.data as MutableList<Topic>
                                mutableLiveData.value = topics
                            } else mutableLiveData.value = null
                        }, { error ->
                            mutableLiveData.value = null
                            error.printStackTrace()
                        })
        )
        return mutableLiveData
    }
}