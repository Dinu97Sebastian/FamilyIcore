package com.familheey.app.Topic

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.viewpager.widget.ViewPager
import cn.pedant.SweetAlert.SweetAlertDialog
import com.familheey.app.Activities.CalendarActivity
import com.familheey.app.Activities.FamilyDashboardActivity
import com.familheey.app.Activities.FeedbackActivity
import com.familheey.app.Adapters.EventTabAdapter
import com.familheey.app.Announcement.AnnouncementListing
import com.familheey.app.FamilheeyApplication
import com.familheey.app.Firebase.MessagePushDelegate
import com.familheey.app.Interfaces.HomeInteractor
import com.familheey.app.Interfaces.ProgressListener
import com.familheey.app.Interfaces.RetrofitListener
import com.familheey.app.Models.ApiCallbackParams
import com.familheey.app.Models.ErrorData
import com.familheey.app.Models.Request.Device
import com.familheey.app.Models.Response.Family
import com.familheey.app.Networking.Retrofit.ApiServiceProvider
import com.familheey.app.Networking.Retrofit.ApiServices
import com.familheey.app.Networking.Retrofit.RetrofitBase
import com.familheey.app.Notification.ActivityActivity
import com.familheey.app.R
import com.familheey.app.Utilities.Constants
import com.familheey.app.Utilities.Constants.Bundle.IS_LOGGED_IN_NOW
import com.familheey.app.Utilities.SharedPref
import com.familheey.app.Utilities.Utilities
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.InstanceIdResult
import com.google.gson.JsonObject
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialogue_permission.*
import java.util.*

open class MainActivity : AppCompatActivity(), HomeInteractor, ProgressListener, MessagePushDelegate {

    private var unReadNotificationCount = 0
    private var eventTabAdapter: EventTabAdapter? = null
    private var compositeDisposable = CompositeDisposable()
    private var progressDialog: SweetAlertDialog? = null
    private var mLastLocation: Location? = null
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()

    }

    fun init() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        eventTabAdapter = EventTabAdapter(supportFragmentManager)
        eventTabAdapter!!.addFragment(MesssageHomeFragment(), "")
        eventTabAdapter!!.addFragment(HomeFragment.newInstance((intent != null && intent.hasExtra(Constants.Bundle.IS_GLOBAL_SEARCH_ENABLED) && intent.getBooleanExtra(Constants.Bundle.IS_GLOBAL_SEARCH_ENABLED, false))), "")
        view_pager.adapter = eventTabAdapter
        view_pager.currentItem = 1
        view_pager?.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {

            }
        })
        if (isLocationPermissionGranted()) {
            addHistory()
        } else {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)) {

                showPermission()
            }
        }
        getFCMToken()
    }

    override fun loadFamilyGroupDashboard(family: Family?) {
        if (family!!.isBlocked != null && family.isBlocked) {
            Toast.makeText(this, "You have been blocked from this family!!", Toast.LENGTH_SHORT).show()
            return
        }
        val intent = Intent(this, FamilyDashboardActivity::class.java)
        intent.putExtra(Constants.Bundle.DATA, family)

        startActivity(intent)
        overridePendingTransition(R.anim.enter,
                R.anim.exit)
    }

    override fun showProgressDialog() {
        progressDialog = Utilities.getProgressDialog(this)
        progressDialog?.show()
    }

    override fun hideProgressDialog() {
        if (progressDialog != null) progressDialog!!.dismiss()
    }

    override fun showErrorDialog(errorMessage: String?) {
        if (progressDialog == null) {
            progressDialog = Utilities.getErrorDialog(this, errorMessage)
            progressDialog!!.show()
            return
        }
        Utilities.getErrorDialog(progressDialog, errorMessage)
    }


    override fun loadGlobalSearch() {
        try {
            val fragment = eventTabAdapter?.getRegisteredFragment(view_pager.currentItem)
            if (fragment != null && fragment is HomeFragment)
                fragment.bottomNavigation.selectedItemId = R.id.navigationSearch
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun loadNotifications() {

        startActivity(Intent(this, ActivityActivity::class.java))
        overridePendingTransition(R.anim.enter,
                R.anim.exit)
    }

    override fun loadNewUserHelper() {
        try {
            val fragment = eventTabAdapter?.getRegisteredFragment(view_pager.currentItem)
            if (fragment != null && fragment is HomeFragment)
                fragment.getNewUserFamilySuggestions()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getNotificationsCount(): Int {
        return unReadNotificationCount
    }


    override fun loadFeedback() {
        startActivity(Intent(this, FeedbackActivity::class.java))
        overridePendingTransition(R.anim.enter,
                R.anim.exit)
    }

    override fun loadAnnouncement() {

        startActivity(Intent(this, AnnouncementListing::class.java))
        overridePendingTransition(R.anim.enter,
                R.anim.exit)
    }

    override fun loadCalender() {

        startActivity(Intent(this, CalendarActivity::class.java))
        overridePendingTransition(R.anim.enter,
                R.anim.exit)
    }
    //Dinu(05/07/2021) for remove quickTour
//    override fun loadSpotlight() {
//        try {
//            SharedPref.setWalkThroughStatus(true)
//            val fragment = eventTabAdapter?.getRegisteredFragment(view_pager.currentItem)
//            if (fragment != null && fragment is HomeFragment)
//                fragment.initializeSpotLight()
//        } catch (e: Exception) {
//            e.printStackTrace()
//            SharedPref.setWalkThroughStatus(false)
//        }
//    }

    override fun onResume() {
        super.onResume()
        FamilheeyApplication.messagePushDelegate = this
    }

    override fun onPause() {
        super.onPause()
        FamilheeyApplication.messagePushDelegate = null
    }

    override fun getPush(type: String?) {
        if ("home" == type) {
            runOnUiThread { Toast.makeText(this@MainActivity, "New Post", Toast.LENGTH_SHORT).show() }
        }
    }



    override fun navigateMessageScreen() {
        view_pager.setCurrentItem(0, true)
    }

    override fun setNotificationCount(count:Int) {
        unReadNotificationCount=count
    }

    private fun generateFcmToken() {
        FirebaseInstanceId.getInstance().instanceId
                .addOnCompleteListener { task: Task<InstanceIdResult> ->
                    if (!task.isSuccessful) {
                        return@addOnCompleteListener
                    }
                    val token = task.result.token
                    saveDevice(token)
                    saveFcmToken(token)
                }
    }

    private fun saveDevice(fcmToken: String) {
        val androidId = Settings.Secure.getString(contentResolver,
                Settings.Secure.ANDROID_ID)
        val device = Device()
        device.user_id = SharedPref.getUserRegistration().id
        device.device_id = androidId
        device.device_token = fcmToken
        device.device_type = "ANDROID"
        val application = FamilheeyApplication.get(this)
        val apiServices = RetrofitBase.createRxResource(this, ApiServices::class.java)
        compositeDisposable.add(apiServices.saveDevice(device)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe({ }) { })
    }


    private fun saveFcmToken(idToken: String) {
        val pref = getSharedPreferences("Token_pref", Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putString("USER_FCM_TOKEN", idToken)
        editor.apply()
    }

    private fun getFCMToken() {
        val pref = getSharedPreferences("Token_pref", Context.MODE_PRIVATE)
        val token = pref.getString("USER_FCM_TOKEN", "")
        generateFcmToken()

        //Dinu(17-03-2021) for generate token
//        if (token!!.isEmpty()) {
//            generateFcmToken()
//        }
    }

    private fun checkPermissions() {
        if (intent != null && !intent.hasExtra(IS_LOGGED_IN_NOW))
            return
        if (TedPermission.isGranted(applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) && TedPermission.isGranted(applicationContext, Manifest.permission.READ_CONTACTS)) {
            addHistory()
        } else {
            val permissionBuilder = TedPermission.with(this)
                    .setPermissionListener(object : PermissionListener {
                        override fun onPermissionGranted() {
                            addHistory()
                        }

                        override fun onPermissionDenied(deniedPermissions: List<String>) {
                            addHistory()
                        }
                    })
                    .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
            permissionBuilder.check()
        }
    }

    private fun isLocationPermissionGranted(): Boolean {
        return TedPermission.isGranted(applicationContext, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    }

    private fun addHistory() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return
        }
        mFusedLocationClient!!.lastLocation
                .addOnCompleteListener(this) { task: Task<Location> ->
                    if (task.isSuccessful && task.result != null) {
                        mLastLocation = task.result
                        if (mLastLocation != null) {
                            val latLng = LatLng(mLastLocation!!.latitude, mLastLocation!!.longitude)
                            try {
                                getAddressFromLatLng(latLng)
                            } catch (e: Exception) {
                                postHistory(null)
                                e.printStackTrace()
                            }
                        } else postHistory(null)
                    } else {
                        postHistory(null)
                    }
                }
    }

    @Throws(Exception::class)
    private fun getAddressFromLatLng(latLng: LatLng) {
        val geocoder: Geocoder
        val addresses: List<Address>
        geocoder = Geocoder(this, Locale.getDefault())
        addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
        val address = addresses[0].getAddressLine(0)
        val city = addresses[0].locality
        val country = addresses[0].countryName
        val postalCode = addresses[0].postalCode
        if (address != null && address.isNotEmpty()) postHistory(address) else postHistory("$city, $country, $postalCode")
    }

    private fun postHistory(locationAddress: String?) {
        val apiServiceProvider = ApiServiceProvider.getInstance(FamilheeyApplication.getInstance())
        val jsonObject = JsonObject()
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().id)
        jsonObject.addProperty("login_type", "Android")
        jsonObject.addProperty("login_device", Utilities.getDeviceName())
        jsonObject.addProperty("login_ip", Utilities.getIPAddress(true))
        jsonObject.addProperty("login_location", locationAddress ?: "")
        apiServiceProvider.addHistory(jsonObject, ApiCallbackParams(), object : RetrofitListener {
            override fun onResponseSuccess(responseBodyString: String, apiCallbackParams: ApiCallbackParams, apiFlag: Int) {
                /* Don't need the response */
            }

            override fun onResponseError(errorData: ErrorData, apiCallbackParams: ApiCallbackParams, throwable: Throwable, apiFlag: Int) {
                /* Don't need the error response */
            }
        })
    }

    override fun onBackPressed() {
        if (view_pager != null && view_pager.currentItem == 0) {
            view_pager.currentItem = 1
        } else if (view_pager != null && view_pager.currentItem == 1) {
            val fragment = eventTabAdapter?.getRegisteredFragment(view_pager.currentItem)
            if (fragment != null && fragment is HomeFragment)
                fragment.onBackPressed()
        } else super.onBackPressed()
    }

    private fun showPermission() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialogue_permission)
        val window = dialog.window!!
        window.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.icon_type.setImageResource(R.drawable.ic_location_on_24)
        dialog.btn_cancel.setOnClickListener { dialog.dismiss() }
        dialog.txt_decs.text = "For a better experience, turn on device location, which uses Google's location services."
        dialog.btn_continue.setOnClickListener {
            dialog.dismiss()
            checkPermissions()
        }
        dialog.show()
    }


}
