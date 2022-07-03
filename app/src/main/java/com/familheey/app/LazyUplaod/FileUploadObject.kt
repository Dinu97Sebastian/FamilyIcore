package com.familheey.app.LazyUplaod

import android.app.ActivityManager
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.content.Intent
import android.util.Log
import com.familheey.app.FamilheeyApplication
import com.familheey.app.Networking.Retrofit.ApiServices
import com.familheey.app.Networking.Retrofit.RetrofitBase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import retrofit2.Response
import java.util.*


object FileUploadObject : Observable() {

    lateinit var subscriptions: CompositeDisposable
    fun uploadFiles(context: Context,files : FileUploadModel){
    if(!isMyServiceRunning(UploadService::class.java,context))
    startUpload(context,files, START_UPLOAD)
    else startUpload(context,files, START_UPLOAD)
    }

    private fun isMyServiceRunning(serviceClass: Class<*>,context: Context): Boolean {
        val manager = context.getSystemService(ACTIVITY_SERVICE) as ActivityManager?
        for (service in manager!!.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

    fun startUpload(context: Context, fileList: FileUploadModel,action:String) {Log.e("FileUploadObject"," startUpload called")
        val intent = Intent(context, UploadService::class.java)
        intent.action = action
        intent.putExtra(FILE_LIST, fileList)
        context.startService(intent)
    }

    fun updatePost(context: Context,request: UpdateRequest){
        subscriptions = CompositeDisposable()
        val application = FamilheeyApplication.get(context)
        val apiServices = RetrofitBase.createRxResource(context, ApiServices::class.java)
        subscriptions.add(apiServices.postUpdate(request)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe({ response: Response<Void?> ->
                    if (response.code() == 200) {
                        Log.e("post update","success")

                    }
                }) { throwable: Throwable? -> Log.e("post update","failed") })
    }

}