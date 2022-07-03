package com.familheey.app.membership

import Data
import Members
import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.familheey.app.Topic.RestApiService
import com.familheey.app.Topic.RetrofitUtil
import com.familheey.app.Utilities.SharedPref
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class MemberListingRepository(val context: Context) {
    private var topics = mutableListOf<Data>()
    private var mutableLiveData = MutableLiveData<List<Data>>()
    val compositeDisposable = CompositeDisposable()


    fun getMutableLiveData(groupId: String, type: String, membershipId: String): MutableLiveData<List<Data>> {
        val jsonObject = JsonObject()
        jsonObject.addProperty("crnt_user_id", SharedPref.getUserRegistration().id)
        jsonObject.addProperty("group_id", groupId)
        if (type == "DASH") {
            jsonObject.addProperty("membership_id", membershipId)
        }
        val requestBody: RequestBody = jsonObject.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        compositeDisposable.add(
                RetrofitUtil.createRxResource(context, RestApiService::class.java).getMembers(requestBody).observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ response ->
                            val topicsBaseModel: Members? = response.body()
                            if (topicsBaseModel?.data != null) {
                                topics = topicsBaseModel.data as MutableList<Data>
                                mutableLiveData.value = topics
                            } else mutableLiveData.value = null
                        }, { error ->
                            mutableLiveData.value = null
                            error.printStackTrace()
                        })
        )
        return mutableLiveData
    }

    fun sendReminder(groupId: String, userId: String) {
        val jsonObject = JsonObject()
        jsonObject.addProperty("loggedin_user_id", SharedPref.getUserRegistration().id)
        jsonObject.addProperty("group_id", groupId)
        jsonObject.addProperty("to_userid", userId)
        val requestBody: RequestBody = jsonObject.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        compositeDisposable.add(
                RetrofitUtil.createRxResource(context, RestApiService::class.java).getMembershipReminder(requestBody).observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({
                        }, { error ->
                            error.printStackTrace()
                        })
        )
    }
}