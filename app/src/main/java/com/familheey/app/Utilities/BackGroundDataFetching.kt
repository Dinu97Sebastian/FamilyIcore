package com.familheey.app.Utilities

import android.content.Context
import com.familheey.app.Discover.model.ElasticSearch
import com.familheey.app.Networking.Retrofit.ApiServices
import com.familheey.app.Networking.Retrofit.RetrofitBase
import com.google.gson.Gson
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response


class BackGroundDataFetching(internal val context: Context) {


    fun loadDataFromApi() {

        val compositeDisposable = CompositeDisposable()
        val jsonObject = JsonObject()
        jsonObject.addProperty("index", "post")
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().id)
        jsonObject.addProperty("offset", "0")
        jsonObject.addProperty("limit", "400")
        val requestBody: RequestBody = jsonObject.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        val authService = RetrofitBase.createRxResource(context, ApiServices::class.java)
        compositeDisposable.add(authService.getRecords(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ response: Response<ElasticSearch?> ->
                    SharedPref.write(SharedPref.EVENT_SUGGESTION, Gson().toJson(response.body()))
                }) {
                })

    }

}