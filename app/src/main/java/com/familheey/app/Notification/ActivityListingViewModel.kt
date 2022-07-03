package com.familheey.app.Notification

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.familheey.app.FamilheeyApplication
import com.familheey.app.Interfaces.RetrofitListener
import com.familheey.app.Models.ApiCallbackParams
import com.familheey.app.Models.ErrorData
import com.familheey.app.Models.Request.CreateEventRequest
import com.familheey.app.Models.Response.GetEventByIdResponse
import com.familheey.app.Models.Response.Reminder
import com.familheey.app.Models.Response.UserSettings
import com.familheey.app.Networking.Retrofit.ApiServiceProvider
import com.familheey.app.Networking.Retrofit.ApiServices
import com.familheey.app.Networking.Retrofit.RetrofitBase
import com.familheey.app.Networking.utils.GsonUtils
import com.familheey.app.Topic.RestApiService
import com.familheey.app.Topic.RetrofitUtil
import com.familheey.app.Utilities.Constants
import com.familheey.app.Utilities.Constants.ApiPaths.FIREBASE_DATABASE_URL
import com.familheey.app.Utilities.SharedPref
import com.google.firebase.database.*
import com.google.gson.Gson
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.*

class ActivityListingViewModel(application: Application) : AndroidViewModel(application) {
    private val context = getApplication<Application>().applicationContext
    private val compositeDisposable = CompositeDisposable()
    private val _authors = MutableLiveData<List<Activity>>()
    private var databaseReference: DatabaseReference? = null
    private val application = "application/json; charset=utf-8"
    private lateinit var getEventByIdResponse: GetEventByIdResponse

    private var otpReceived: String? = null
    private var mobileNumber: String? = null
    private var currentTimezoneOffset: String? = null
    private var timezoneName: String? = null
    private var isVerified = false
    //var subscriptions: CompositeDisposable? = null
    var db = FirebaseDatabase.getInstance(FIREBASE_DATABASE_URL).getReference(Constants.ApiPaths.FIREBASE_USER_TYPE + SharedPref.getUserRegistration().id + "_notification")
    var count = MutableLiveData<Long>()

    val authors: LiveData<List<Activity>>
        get() = _authors

    private val _result = MutableLiveData<String?>()
    val result: LiveData<String?>
        get() = _result


    private val _clear = MutableLiveData<String?>()
    val clear: LiveData<String?>
        get() = _clear


    private val _read = MutableLiveData<String?>()
    val read: LiveData<String?>
        get() = _read

    /**variable "verify"
     *  ticket 693 reverification of blocked users **/
    private val _verify = MutableLiveData<String?>()
    val verify: LiveData<String?>
        get() = _verify


    private val _response = MutableLiveData<AdminAcceptResponse?>()
    val response: LiveData<AdminAcceptResponse?>
        get() = _response

    private val _getEventDataFromEventId = MutableLiveData<GetEventByIdResponse?>()
    val getEventDataFromEventId: LiveData<GetEventByIdResponse?>
        get() = _getEventDataFromEventId



    fun deleteAuthor(author: Activity?) {
        db.child(author?.key!!).removeValue()
                .addOnCompleteListener {

                }
    }


    fun clearNotification() {
        updateSettings()
        val jsonObject = JsonObject()
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().id)
        val requestBody: RequestBody = jsonObject.toString().toRequestBody(application.toMediaTypeOrNull())
        val application = FamilheeyApplication.get(context)
        val apiServices = RetrofitBase.createRxResource(context, ApiServices::class.java)
        compositeDisposable.add(apiServices.clearNotification(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe({
                    _clear.value = "200"
                }) {
                    _clear.value = "200"
                })
    }

    fun markAsReadAllNotification() {
        updateSettings()
        val jsonObject = JsonObject()
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().id)
        val requestBody: RequestBody = jsonObject.toString().toRequestBody(application.toMediaTypeOrNull())
        val application = FamilheeyApplication.get(context)
        val apiServices = RetrofitBase.createRxResource(context, ApiServices::class.java)
        compositeDisposable.add(apiServices.readNotifications(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe({
                    _read.value = "200"
                }) {
                    _read.value = "200"
                })
    }

    fun acceptOrReject(jsonObject: JsonObject) {
        val requestBody: RequestBody = jsonObject.toString().toRequestBody(application.toMediaTypeOrNull())
        val application = FamilheeyApplication.get(context)
        val apiServices = RetrofitBase.createRxResource(context, ApiServices::class.java)
        compositeDisposable.add(apiServices.respondToFamilyInvitationRx(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe({
                    _result.value = "200"
                }) {
                    _result.value = it?.message!!
                })
    }


    fun acceptOrRejectAdmin(jsonObject: JsonObject) {
        val requestBody: RequestBody = jsonObject.toString().toRequestBody(application.toMediaTypeOrNull())
        val application = FamilheeyApplication.get(context)
        val apiServices = RetrofitBase.createRxResource(context, ApiServices::class.java)
        compositeDisposable.add(apiServices.memberRequestActionNew(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe({
                    _response.value = it.body()
                }) {
                    _response.value = null

                })
    }

    fun fetchEventDetailApi(eventId: String?) {
        val jsonObject = JsonObject()
        jsonObject.addProperty("event_id", eventId)
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId())

        val requestBody: RequestBody = jsonObject.toString().toRequestBody(application.toMediaTypeOrNull())
        val application = FamilheeyApplication.get(context)
        val apiServices = RetrofitBase.createRxResource(context, ApiServices::class.java)

        compositeDisposable.add(apiServices.getEventsByEventId(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe({
                    getEventByIdResponse = it.body()!!
                    _result.value = "200"

                }) {
                    _result.value = it?.message!!

                })

    }
    /**Ticket 693**/
    fun getMobileDetailsFromUserId() {
        val jsonObject = JsonObject()
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().id)
        val apiServiceProvider: ApiServiceProvider = ApiServiceProvider.getInstance(context)
        apiServiceProvider?.getMobileDetailsFromUserId(jsonObject, null, object : RetrofitListener {
            override fun onResponseSuccess(responseBodyString: String?, apiCallbackParams: ApiCallbackParams?, apiFlag: Int) {
                _verify.value = responseBodyString
            }
            override fun onResponseError(errorData: ErrorData, apiCallbackParams: ApiCallbackParams, throwable: Throwable, apiFlag: Int) {
            }
        })
    }
/**@author Devika
 * for getting event reminder when clicking on Going,Interested,Not Interested buttons inside bell notification
 * invoked fetchEventDetailApiOne(eventId: String?) method for getting the eventId to pass it to createEventReminder for
 * setting reminder
 * **/
    fun fetchEventDetailApiOne(eventId: String?) {
        val jsonObject = JsonObject()
        jsonObject.addProperty("event_id", eventId)
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().id)
        val apiServiceProvider: ApiServiceProvider = ApiServiceProvider.getInstance(context)
        apiServiceProvider?.getEventById(jsonObject, null, object : RetrofitListener {
            override fun onResponseSuccess(responseBodyString: String?, apiCallbackParams: ApiCallbackParams?, apiFlag: Int) {
                getEventByIdResponse = Gson().fromJson(responseBodyString, GetEventByIdResponse::class.java)
                createEventReminder(eventId, 10)
            }
            override fun onResponseError(errorData: ErrorData, apiCallbackParams: ApiCallbackParams, throwable: Throwable, apiFlag: Int) {

            }
        })
    }

    fun goingOrInterstedOrNotgoing(eventId: String?, resp: String?) {
        val jsonObject = JsonObject()
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().id)
        jsonObject.addProperty("event_id", eventId)
        jsonObject.addProperty("resp", resp)
        val requestBody: RequestBody = jsonObject.toString().toRequestBody(application.toMediaTypeOrNull())
        val application = FamilheeyApplication.get(context)
        val apiServices = RetrofitBase.createRxResource(context, ApiServices::class.java)
        compositeDisposable.add(apiServices.respondToRSVP1(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe({
                    _result.value = "200"

                }) {
                    _result.value = it?.message!!

                })

    }
    /**@author Devika
     * for getting event reminder when clicking on Going,Interested,Not Interested buttons inside bell notification
     * createEventReminder sets reminder for the event using it's id get from fetchEventDetailApiOne(eventId: String?)
     * **/
     fun createEventReminder(eventId: String?, reminder: Int) {
      //  currentTimezoneOffset = getCurrentTimezoneOffset()
        timezoneName = getTimeZoneName()
        val obj = CreateEventRequest()
        //subscriptions = CompositeDisposable()
        obj.user_id = SharedPref.getUserRegistration().id
        obj.event_id = eventId.toString()
        obj.remind_on = reminder
        obj.timezone_offset = timezoneName
        obj.event_date = getEventByIdResponse!!.data.event[0].fromDate.toString() + ""
        if (getEventByIdResponse?.data?.event?.get(0)?.reminder_id != null && getEventByIdResponse!!.getData().getEvent().get(0).getIsRecurrence() !== 0) {
            obj.remind_id = getEventByIdResponse?.data?.event?.get(0)?.reminder_id
            obj.remind_date = getEventByIdResponse?.data?.event?.get(0)?.remindOn
           // obj.timezone_offset= timezoneName
            obj.is_recurrence = "0"
        }
        if (getEventByIdResponse?.getData()?.getEvent()?.get(0)?.getIsRecurrence() === 1) {
          //  obj.timezone_offset = timezoneName
            obj.is_recurrence = "1"
        }
        val application: FamilheeyApplication = FamilheeyApplication.get(context)
        val apiServices: ApiServices = RetrofitBase.createRxResource(context, ApiServices::class.java)
        compositeDisposable.add(apiServices.createReminder(obj)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe({ response ->
                    assert(response.body() != null)
                    val eventsJson: JsonObject = response.body()!!.getAsJsonObject("data")
                    val datum: Reminder = Gson().fromJson(eventsJson.toString(), Reminder::class.java)
                    getEventByIdResponse!!.data.event[0].reminder_id = datum.id
                    getEventByIdResponse!!.data.event[0].remindOn = datum.remind_on
                }) { throwable -> })
    }


    fun getCurrentTimezoneOffset(): String? {
        val tz = TimeZone.getDefault()
        val cal = GregorianCalendar.getInstance(tz)
        val offsetInMillis = tz.getOffset(cal.timeInMillis)
        var offset = String.format("%02d.%02d", Math.abs(offsetInMillis / 3600000), Math.abs(offsetInMillis / 60000 % 60))
        offset = (if (offsetInMillis >= 0) "+" else "-") + offset
        return offset
    }
    private fun getTimeZoneName(): String? {
        val tz = TimeZone.getDefault()
        return tz.id.toString()
        //System.out.println("TimeZone   "+tz.getDisplayName(false, TimeZone.SHORT)+" Timezone id :: " +tz.getID());
    }
    fun fetchDataFromPref(): Boolean {
        val response = SharedPref.read(SharedPref.ON_BOARD, "")

        if (response != "" && GsonUtils.getInstance().gson.fromJson(response, UserSettings::class.java).notification_auto_delete != null)
            return GsonUtils.getInstance().gson.fromJson(response, UserSettings::class.java).notification_auto_delete

        return false
    }

    private fun updateSettings() {
        val response = SharedPref.read(SharedPref.ON_BOARD, "")
        if (response != "") {
            val setting = GsonUtils.getInstance().gson.fromJson(response, UserSettings::class.java)
            setting.notification_auto_delete = false
            SharedPref.write(SharedPref.ON_BOARD, Gson().toJson(setting))
        }

    }


    fun notificationAutoDeleteStatusChange() {

        val jsonObject = JsonObject()
        jsonObject.addProperty("id", SharedPref.getUserRegistration().id)
        jsonObject.addProperty("notification_auto_delete", false)
        val requestBody: RequestBody = jsonObject.toString().toRequestBody(application.toMediaTypeOrNull())
        val requestInterface = RetrofitUtil.createRxResource(context, RestApiService::class.java)
        compositeDisposable.add(
                requestInterface.updateSettings(requestBody).observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({
                        }, {
                        })
        )
    }


    fun initializeFireBase() {
        databaseReference = FirebaseDatabase.getInstance(FIREBASE_DATABASE_URL).getReference(Constants.ApiPaths.FIREBASE_USER_TYPE + SharedPref.getUserRegistration().id + "_notification")
        databaseReference?.removeEventListener(notificationListener)
        databaseReference?.addValueEventListener(notificationListener)
    }



    //CHECKSTYLE:OFF

    private var notificationListener: ValueEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            val authors = mutableListOf<Activity>()
            for (authorSnapshot in dataSnapshot.children) {

                val obj = Activity()
                if (authorSnapshot.child("from_id").value != null) {
                    obj.fromId = authorSnapshot.child("from_id").value.toString()
                }
                if (authorSnapshot.child("group_id").value != null) {
                    obj.groupId = authorSnapshot.child("group_id").value.toString()
                }
                if (authorSnapshot.child("category").value != null) {
                    obj.category = authorSnapshot.child("category").value.toString()
                }
                if (authorSnapshot.child("comment").value != null) {
                    obj.comment = authorSnapshot.child("comment").value.toString()
                }
                if (authorSnapshot.child("create_time").value != null) {
                    obj.createTime = authorSnapshot.child("create_time").value.toString()
                }
                if (authorSnapshot.child("rsvp").value != null) {
                    obj.rsvp = authorSnapshot.child("rsvp").getValue(Boolean::class.java)
                }
                if (authorSnapshot.child("from_date").value != null) {
                    obj.fromDate = authorSnapshot.child("from_date").getValue(Long::class.java)
                }
                if (authorSnapshot.child("type_id").value != null) {
                    obj.typeId = authorSnapshot.child("type_id").value.toString()
                }
                if (authorSnapshot.child("event_id").value != null) {
                    obj.eventId = authorSnapshot.child("event_id").value.toString()
                }

                if (authorSnapshot.child("visible_status").value != null) {
                    obj.visibleStatus = authorSnapshot.child("visible_status").value.toString()
                }
                if (authorSnapshot.child("type").value != null) {
                    obj.type = authorSnapshot.child("type").value.toString()
                }
                if (authorSnapshot.child("message").value != null) {
                    obj.message = authorSnapshot.child("message").value.toString()
                }
                if (authorSnapshot.child("privacy_type").value != null) {
                    obj.privacyType = authorSnapshot.child("privacy_type").value.toString()
                }
                if (authorSnapshot.child("message_title").value != null) {
                    obj.messageTitle = authorSnapshot.child("message_title").value.toString()
                }
                if (authorSnapshot.child("propic").value != null) {
                    obj.propic = authorSnapshot.child("propic").value.toString()
                }
                if (authorSnapshot.child("sub_type").value != null) {
                    obj.subType = authorSnapshot.child("sub_type").value.toString()
                }
                if (authorSnapshot.child("link_to").value != null) {
                    obj.linkTo = authorSnapshot.child("link_to").value.toString()
                }
                if (authorSnapshot.child("cover_image").value != null) {
                    obj.coverImage = authorSnapshot.child("cover_image").value.toString()
                }
                if (authorSnapshot.child("to_group_name").value != null) {
                    obj.toGroupName = authorSnapshot.child("to_group_name").value.toString()
                }
                if (authorSnapshot.child("description").value != null) {
                    obj.description = authorSnapshot.child("description").value.toString()
                }
                if (authorSnapshot.child("created_by_user").value != null) {
                    obj.createdByUser = authorSnapshot.child("created_by_user").value.toString()
                }
                if (authorSnapshot.child("created_by_propic").value != null) {
                    obj.createdByPropic = authorSnapshot.child("created_by_propic").value.toString()
                }
                if (authorSnapshot.child("location").value != null) {
                    obj.location = authorSnapshot.child("location").value.toString()
                }
                if (authorSnapshot.child("membercount").value != null) {
                    obj.membercount = authorSnapshot.child("membercount").value.toString()
                }
                obj.key = authorSnapshot.key
                obj.let {
                    if (!obj.type.equals(""))
                        authors.add(obj)
                }
            }

            _authors.value = authors
        }

        override fun onCancelled(databaseError: DatabaseError) {
            _authors.value = mutableListOf<Activity>()
        }
    }

    //CHECKSTYLE:ON

}