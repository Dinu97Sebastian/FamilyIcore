package com.familheey.app.Notification

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import cn.pedant.SweetAlert.SweetAlertDialog
import com.familheey.app.Activities.OTPVerificationActivity
import com.familheey.app.Activities.ProfileActivity
import com.familheey.app.R
import com.familheey.app.Utilities.Constants
import com.familheey.app.Utilities.SharedPref
import com.familheey.app.Utilities.Utilities
import com.familheey.app.Utilities.Utilities.getContentNotFoundDialog
import com.familheey.app.Utilities.Utilities.getProgressDialog
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_activity.*
import kotlinx.android.synthetic.main.dialogue_user_block.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList


class ActivityActivity : AppCompatActivity(), ActivityAdapter.ActivityItemClick {

    private lateinit var viewModel: ActivityListingViewModel

    private var authors: ArrayList<Activity> = ArrayList()
    private var adapter: ActivityAdapter? = null
    var progressDialog: SweetAlertDialog? = null
    var action = ""
    var jsonObject = JsonObject()

    private var otpReceived: String? = null
    private var mobileNumber: String? = null
    private var isVerified = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_activity)
        viewModel = ViewModelProvider(this).get(ActivityListingViewModel::class.java)
        initRecyclerview()
        progressDialog = getProgressDialog(this)
        viewModel.initializeFireBase()


        viewModel.clear.observe(this, Observer {

            authors.clear()
            progressDialog?.hide()
            adapter?.notifyDataSetChanged()
        })

        viewModel.read.observe(this, Observer {

            adapter?.notifyDataSetChanged()
            progressDialog?.hide()
        })

        viewModel.result.observe(this, Observer {
            if (it.equals("200")) {
                progressDialog?.hide()
                adapter?.notifyDataSetChanged()
                Toast.makeText(this, "Successfully Updated", Toast.LENGTH_SHORT).show()
            } else {
                progressDialog?.hide()
                getContentNotFoundDialog(this)
            }
        })

        /** ticket 693 **/
        viewModel.verify.observe(this, Observer {
            var jObject: JSONObject? = null
            if (it != null) {
                try {
                    jObject = JSONObject(it)
                    val message = jObject.getString("message")
                    if (message.equals("otp limit exceeded", ignoreCase = true)) {
                        Toast.makeText(this, "Sorry.. you cannot request for OTP more than 2 times in 12 hours", Toast.LENGTH_LONG).show()
                        //this.finish();
                        //thread.start();
                        Handler().postDelayed({ this.finish() }, 3500)
                    }else if(message.equals("user is not blocked",ignoreCase = true)){
                        Toast.makeText(this, "Already verified", Toast.LENGTH_LONG).show()
                        Handler().postDelayed({ this.finish() }, 2000)
                    }
                    else {
                        val mobileData = jObject.getJSONObject("data")
                        otpReceived = mobileData.getString("otp")
                        mobileNumber = mobileData.getString("phone")
                        //isVerified = mobileData.getBoolean("is_verified")
                        val intent = Intent(this, OTPVerificationActivity::class.java)
                        intent.putExtra("otpReceived", otpReceived)
                        intent.putExtra("mobileNumber", mobileNumber)
                        //intent.putExtra("isVerified", isVerified)
                        startActivity(intent)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        })
        /**end of 693**/
        viewModel.response.observe(this, Observer {
            /**
             * solved notifications actions(#353),12/10/2021 & solved #282 (28/20/21)
             */
            if (it != null && it.data[0].message != null) {
                progressDialog?.hide()
                adapter?.notifyDataSetChanged()
                Toast.makeText(this, "This request has already accepted", Toast.LENGTH_SHORT).show()
            } else {
                if (it != null) {
                    progressDialog?.hide()
                    adapter?.notifyDataSetChanged()
                    Toast.makeText(this, "Successfully Updated", Toast.LENGTH_SHORT).show()
                } else {
                    progressDialog?.hide()
                    getContentNotFoundDialog(this)
                }
            }

//            if (it != null) {
//                if (it.data[0].message.isEmpty()) {
//                    Toast.makeText(this, "Successfully Updated", Toast.LENGTH_SHORT).show()
//                } else {
//                    if (it.data[0].action == action) {
//                        showMesg(it.data[0].message)
//                    } else {
//                        showContinue(it.data[0].message)
//                    }
//
//                }
//
//            } else {
//                progressDialog?.hide()
//
//                getContentNotFoundDialog(this)
//            }
        })
        viewModel.authors.observe(this, androidx.lifecycle.Observer {
            if (it?.size!! > 0) {
                authors.clear()
                adapter?.notifyDataSetChanged()
                authors.addAll(it.reversed())
                rv_activity_list.visibility = View.VISIBLE
                layoutEmpty.visibility = View.GONE
            } else {
                if (viewModel.fetchDataFromPref()) {
                    empty_text.text = "All your notifications are deleted by the system! You will receive upcoming notifications!"
                }
                rv_activity_list.visibility = View.GONE
                layoutEmpty.visibility = View.VISIBLE
                authors.clear()
                adapter?.notifyDataSetChanged()
            }
        })
        btn_back.setOnClickListener { onBackPressed() }
        btn_clear.setOnClickListener { v: View? -> showMenus(v!!) }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.left,
                R.anim.right)
    }

    override fun onItemClick(position: Int) {
        if (Utilities.isNullOrEmpty(authors.get(position).typeId)) {
            return
        }

        val intent = authors.get(position).goToCorrespondingDashboard(this)
        startActivity(intent)
        overridePendingTransition(R.anim.enter, R.anim.exit)
        viewModel.db.child(authors.get(position).key!!).child("visible_status").setValue("read")
        authors.get(position).visibleStatus = "read"
        adapter?.notifyDataSetChanged()
    }

    override fun onItemDelete(position: Int) {
        viewModel.deleteAuthor(authors.get(position))
        authors.removeAt(position)
        adapter?.notifyDataSetChanged()
        if (authors.size == 0) {
            viewModel.notificationAutoDeleteStatusChange()
            rv_activity_list.visibility = View.GONE
            layoutEmpty.visibility = View.VISIBLE
        }
    }

    override fun onItemAcceptOrReject(position: Int, staus: String) {
        viewModel.db.child(authors[position].key!!).child("visible_status").setValue("read")
        authors[position].visibleStatus = "read"
        progressDialog?.show()


        val jsonObject = JsonObject()

        if (authors.get(position).category.equals("invitation")) {
            jsonObject.addProperty("user_id", SharedPref.getUserRegistration().id)
            jsonObject.addProperty("group_id", authors[position].groupId)
            jsonObject.addProperty("status", staus)
            jsonObject.addProperty("from_id", authors[position].fromId)
            viewModel.acceptOrReject(jsonObject)


        } else {
            jsonObject.addProperty("user_id", authors.get(position).fromId.toString())
            jsonObject.addProperty("group_id", authors.get(position).typeId.toString())
            jsonObject.addProperty("status", staus)
            jsonObject.addProperty("type", "request")
            jsonObject.addProperty("responded_by", SharedPref.getUserRegistration().id)
            action = staus
            this.jsonObject = jsonObject
            viewModel.acceptOrRejectAdmin(jsonObject)
        }

        authors.get(position).visibleStatus = "read"
        adapter?.notifyDataSetChanged()
    }

    override fun linkingAcceptOrReject(position: Int, staus: String) {
        viewModel.db.child(authors[position].key!!).child("visible_status").setValue("read")
        authors[position].visibleStatus = "read"
        progressDialog?.show()
        val jsonObject = JsonObject()
        jsonObject.addProperty("from_group", authors.get(position).fromId.toString())
        jsonObject.addProperty("to_group", authors.get(position).typeId.toString())
        jsonObject.addProperty("status", staus)
        jsonObject.addProperty("type", "fetch_link")
        jsonObject.addProperty("responded_by", SharedPref.getUserRegistration().id)
        action = staus
        this.jsonObject = jsonObject
        viewModel.acceptOrRejectAdmin(jsonObject)
        authors.get(position).visibleStatus = "read"
        adapter?.notifyDataSetChanged()
    }

    override fun onItemGoingOrInterstedOrNotIntersted(position: Int, staus: String) {
        progressDialog?.show()
        if(staus.contains("not-going")){
            viewModel.goingOrInterstedOrNotgoing(authors.get(position).typeId, staus)
            //viewModel.fetchEventDetailApiOne(authors.get(position).typeId)
            viewModel.db.child(authors.get(position).key!!).child("visible_status").setValue("read")
            authors.get(position).visibleStatus = "read"
        }else{
            viewModel.goingOrInterstedOrNotgoing(authors.get(position).typeId, staus)
            viewModel.fetchEventDetailApiOne(authors.get(position).typeId)
            viewModel.db.child(authors.get(position).key!!).child("visible_status").setValue("read")
            authors.get(position).visibleStatus = "read"
        }
//        viewModel.goingOrInterstedOrNotgoing(authors.get(position).typeId, staus)
//        viewModel.fetchEventDetailApiOne(authors.get(position).typeId)
//        viewModel.db.child(authors.get(position).key!!).child("visible_status").setValue("read")
//        authors.get(position).visibleStatus = "read"
    }



    private fun initRecyclerview() {
        adapter = ActivityAdapter(authors, this)
        rv_activity_list.adapter = adapter
        val linearLayoutManager = LinearLayoutManager(this)
        rv_activity_list.layoutManager = linearLayoutManager
        rv_activity_list.itemAnimator = DefaultItemAnimator()
    }

    private fun clearNotificatoionAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Do you want to clear all notifications?")
                .setCancelable(false)
                .setPositiveButton(" Yes ") { _: DialogInterface?, _: Int ->
                    run {
                        progressDialog?.show()
                        viewModel.clearNotification()
                    }
                }.setNegativeButton(" No ") { dialog: DialogInterface, _: Int -> dialog.dismiss() }
        val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        )
        val alert = builder.create()
        alert.setTitle("Clear Notifications")
        alert.show()
        params.setMargins(0, 0, 20, 0)
        alert.getButton(DialogInterface.BUTTON_NEGATIVE).layoutParams = params
    }


    private fun showMenus(v: View) {
        val popup = PopupMenu(v.context, v)
        popup.menuInflater.inflate(R.menu.notification_menu, popup.menu)

        var s = SpannableString("Notification Settings")
        s.setSpan(ForegroundColorSpan(Color.BLACK), 0, s.length, 0)
        popup.menu.getItem(0).title = s


        s = SpannableString("Mark all read")
        s.setSpan(ForegroundColorSpan(Color.BLACK), 0, s.length, 0)
        popup.menu.getItem(1).title = s


        s = SpannableString("Clear Notifications")
        s.setSpan(ForegroundColorSpan(Color.BLACK), 0, s.length, 0)
        popup.menu.getItem(2).title = s


        if (authors.size == 0) {
            popup.menu.getItem(2).isVisible = false
            popup.menu.getItem(1).isVisible = false
        }

        popup.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.notification_read -> readAll()
                R.id.clear_notification -> clearNotificatoionAlert()
                R.id.notification_settings -> {
                    startActivity(Intent(this, NotificationSettingsActivity::class.java))
                    overridePendingTransition(R.anim.enter, R.anim.exit)
                }
            }
            true
        }
        popup.show()
    }

    private fun readAll() {
        progressDialog?.show()
        viewModel.markAsReadAllNotification()
        for (obj in authors) {
            obj.visibleStatus = "read"
        }
    }

    private fun showContinue(msg: String) {
        val dialog = Dialog(this)
        Objects.requireNonNull(dialog.window)?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialogue_user_block)
        dialog.setCanceledOnTouchOutside(false)
        dialog.txt_msg.text = msg + ", do you want to continue?"
        dialog.btn_close.text = "No"
        dialog.btn_login.text = "Yes"
        dialog.btn_close.setOnClickListener { dialog.dismiss() }
        dialog.btn_login.setOnClickListener {
            jsonObject.addProperty("is_force_update", "true")
            viewModel.acceptOrRejectAdmin(jsonObject)
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun showMesg(msg: String) {
        val dialog = Dialog(this)
        Objects.requireNonNull(dialog.window)?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialogue_user_block)
        dialog.setCanceledOnTouchOutside(false)
        dialog.txt_msg.text = msg
        dialog.btn_login.text = "OK"
        dialog.btn_close.visibility = View.GONE
        dialog.btn_login.setOnClickListener { dialog.dismiss() }

        dialog.show()
    }
    override fun onItemUpdateNowOrUpdateLater(position: Int, status: String) {
        viewModel.db.child(authors[position].key!!).child("visible_status").setValue("read")
        authors[position].visibleStatus = "read"
       if(status=="updateNow") {
           val intent = Intent(this, ProfileActivity::class.java).putExtra(Constants.Bundle.TYPE, Constants.Bundle.PUSH).putExtra(Constants.Bundle.DATA, authors.get(position).typeId.toString())
           startActivity(intent)
       }
    }
/**ticket 693**/
    override fun onClickVerifyNow(position: Int, status: String) {
        if(status=="verifyNow") {
            viewModel.getMobileDetailsFromUserId()
        }
    viewModel.db.child(authors[position].key!!).child("visible_status").setValue("read")
    authors[position].visibleStatus = "read"
    }

}