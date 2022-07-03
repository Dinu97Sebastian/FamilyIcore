package com.familheey.app.membership

import Data
import MembershipType
import android.app.Activity
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.familheey.app.Interfaces.ProgressListener
import com.familheey.app.R
import com.familheey.app.Topic.RestApiService
import com.familheey.app.Topic.RetrofitUtil
import com.familheey.app.Utilities.Constants
import com.familheey.app.Utilities.SharedPref
import com.familheey.app.Utilities.Utilities
import com.google.gson.Gson
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_add_membership.btn_submit
import kotlinx.android.synthetic.main.activity_add_membership.goBack
import kotlinx.android.synthetic.main.activity_add_membership.spinner_period
import kotlinx.android.synthetic.main.activity_add_user_membership.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


class EditUserMembershipActivity : AppCompatActivity(), ProgressListener {

    private var progressDialog: SweetAlertDialog? = null
    private var compositeDisposable = CompositeDisposable()
    private var groupId: String? = ""
    private var type: String? = ""
    private var user: Data? = null
    private var data: ArrayList<MembershipType>? = null
    private var date: String = ""
    private var fdate: String = ""
    private var isFirst: Boolean = true
    private val dateFormate: String = "dd-MM-yyyy"

    private var isFirst1: Boolean = false
    var r = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_user_membership)
        groupId = intent.extras?.getString(Constants.Bundle.FAMILY_ID)
        user = Gson().fromJson(intent.extras?.getString(Constants.Bundle.DATA), Data::class.java)
        type = intent.extras?.getString(Constants.Bundle.TYPE)
        toolBarTitle.text = user?.fullName
        getMemberShipType()
        loadData()
        clickListener()
    }

    override fun hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog?.dismiss()
        }
    }

    override fun showProgressDialog() {

        progressDialog = Utilities.getProgressDialog(this)
        progressDialog?.show()
    }

    override fun showErrorDialog(errorMessage: String?) {
        /*
               * Don't need this
               */
    }

    private fun validate(): Boolean {
        if (spinner_mtype.selectedItemPosition == 0) {
            Toast.makeText(this, "Please select the Membership type", Toast.LENGTH_LONG).show()
            return false
        }
        if (etxt_paid_amount.text.isEmpty()) {
            Toast.makeText(this, "Please enter the Amount", Toast.LENGTH_LONG).show()
            return false
        }

        if (txt_from.text.isEmpty()) {
            Toast.makeText(this, "Please choose Membership start on Date", Toast.LENGTH_LONG).show()
            return false
        }
        if (spinner_period.selectedItemPosition == 0) {
            if (spinner_period.selectedItem == "Select") {
                Toast.makeText(this, "Please select duration", Toast.LENGTH_LONG).show()
                return false
            }
            return true
        }
        return true
    }

    fun init() {
        val options = ArrayList<String>()
        options.add("Select")
        for (mem in data?.get(spinner_mtype.selectedItemPosition - 1)?.types!!) {
            options.add(mem.typeName)
        }

        val spinnerArrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, options)
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_period.adapter = spinnerArrayAdapter

        if (isFirst) {

            if (user?.membershipDuration != null && user?.membershipPeriodType != null) {
                for (i in 0 until options.size) {
                    if (options[i] == user?.membershipPeriodType!!) {
                        spinner_period.setSelection(i)
                        break
                    }
                }
            }
            isFirst = false
        } else {
            payment_status.setSelection(1)
        }
    }

    private fun loadData() {
        val options: Array<String> = arrayOf("Completed", "Partial", "Pending")
        val spinnerArrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, options)
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        payment_status.adapter = spinnerArrayAdapter
        etxt_paid_amount.setText(user?.membershipFees)
        if (!user?.membershipCustomerNotes.isNullOrEmpty())
            txt_note_from_customer1.text = user?.membershipCustomerNotes
        else {
            txt_note_from_customer.text = ""
        }
        if (user?.membershipFees != null && user?.membershipPaidOn != null) {
            val amt = user?.membershipFees!!.toInt() - user?.membershipTotalPayedAmount!!.toInt()
            if (amt > 0)
                txt_due.text = "Due Amount : $amt"
            else
                txt_due.text = "Due Amount : 0"
        }
        etxt_comment.setText(user?.membershipPaymentNotes)

        if (user?.membershipPaymentStatus != null) {
            if (user?.membershipPaymentStatus == "Pending") {
                payment_status.setSelection(2)
            } else if (user?.membershipPaymentStatus == "Partial") {
                payment_status.setSelection(1)
            }
        }

        txt_from.text = getFormattedCreatedAt(user?.membershipFrom?.toLong(), "FROM")
        txt_to.text = getFormattedCreatedAt(user?.membershipTo?.toLong(), "TO")
    }

    private fun getFormattedCreatedAt(values: Long?, type: String): String? {
        var value: Long? = values
        if (value != null && value > 100) {
            value = TimeUnit.SECONDS.toMillis(value)
            val dateTime = DateTime(value)
            val dtfOut = DateTimeFormat.forPattern("MMM dd yyyy")
            val dtfOut1 = DateTimeFormat.forPattern(dateFormate)
            if (type == "PAID")
                date = dtfOut1.print(dateTime)
            else if (type == "FROM")
                fdate = dtfOut1.print(dateTime)
            return dtfOut.print(dateTime)
        }
        return ""
    }

    private fun getFormattedCreatedAt(values: Long?): String? {
        val value: Long? = values
        if (value != null && value > 100) {
            val dateTime = DateTime(value)
            val dtfOut = DateTimeFormat.forPattern("MMM dd yyyy")
            val dtfOut1 = DateTimeFormat.forPattern(dateFormate)
            fdate = dtfOut1.print(dateTime)
            return dtfOut.print(dateTime)
        }
        return ""
    }

    private fun clickListener() {
        goBack.setOnClickListener { onBackPressed() }
        btn_submit.setOnClickListener {
            if (validate()) {
                userMembershipUpdate()
            }
        }

        btn_from_calender.setOnClickListener {
            showCalender(txt_from, true)
        }


        view_from_date.setOnClickListener {
            showCalender(txt_from, true)
        }

        spinner_mtype.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                if (position > 0) {
                    init()

                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                /*
              *  Don't need this
               */
            }
        }

        spinner_period.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {


                if (isFirst1 && position > 0 && data?.get(spinner_mtype.selectedItemPosition - 1)?.membershipFees != null) {
                    etxt_paid_amount.setText((data?.get(spinner_mtype.selectedItemPosition - 1)?.membershipFees!! * position).toString())
                }

                toDateGenerate()


                isFirst1 = true
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                /*
               * Don't need this
               */
            }
        }
    }

    private fun showCalender(tview: TextView, isFrom: Boolean) {

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        val dpd = DatePickerDialog(this, { _, year, monthOfYear, dayOfMonth ->

            val r = monthOfYear + 1
            val s = "$dayOfMonth-$r-$year"
            val dateTime = DateTime.parse(s,
                    DateTimeFormat.forPattern(dateFormate))
            val dtfOut = DateTimeFormat.forPattern("dd MMM yyyy")
            tview.text = (dtfOut.print(dateTime))
            if (isFrom) {
                fdate = s
                payment_status.setSelection(1)
                toDateGenerate()
            } else {
                date = s
            }

        }, year, month, day)

        dpd.show()
    }

    private fun toDateGenerate() {
        if (spinner_period.selectedItem.toString() != "Select") {
            if (data?.get(spinner_mtype.selectedItemPosition - 1)?.membershipPeriodTypeId == 4) {
                txt_to.text = data?.get(spinner_mtype.selectedItemPosition - 1)?.types?.get(spinner_period.selectedItemPosition - 1)?.typeName
            } else {
                if (fdate == "") {
                    txt_from.text = getFormattedCreatedAt(System.currentTimeMillis())
                }
                val startDateTime = DateTime.parse(fdate, DateTimeFormat.forPattern(dateFormate))
                val c = startDateTime.plusDays(data?.get(spinner_mtype.selectedItemPosition - 1)?.types?.get(spinner_period.selectedItemPosition - 1)?.typeValue!!)
                val fmt: DateTimeFormatter = DateTimeFormat.forPattern("dd MMM yyyy")
                txt_to.text = fmt.print(c)
            }
        }

    }

    private fun getMemberShipType() {
        showProgressDialog()
        val jsonObject = JsonObject()
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().id)
        jsonObject.addProperty("group_id", groupId)
        val requestBody: RequestBody = jsonObject.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        val requestInterface = RetrofitUtil.createRxResource(this, RestApiService::class.java)
        compositeDisposable.add(
                requestInterface.listMembershipLookup(requestBody).observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({
                            hideProgressDialog()
                            val options = ArrayList<String>()
                            options.add("Select")
                            data = it.body()
                            for (mem in data!!) {
                                options.add(mem.membershipName)
                            }
                            val spinnerArrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, options)
                            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            spinner_mtype.adapter = spinnerArrayAdapter
                            for (i in 0 until data!!.size) {
                                if (data?.get(i)?.membershipName == user?.membershipType) {
                                    spinner_mtype.setSelection(i + 1)
                                    break
                                }
                            }
                        }, {
                            hideProgressDialog()
                        })
        )
    }

    private fun userMembershipUpdate() {
        showProgressDialog()
        val jsonObject = JsonObject()
        val fDate = DateTime.parse(fdate, DateTimeFormat.forPattern("dd-MM-yyyy"))
        jsonObject.addProperty("user_id", user?.userId.toString())
        jsonObject.addProperty("group_id", groupId)
        jsonObject.addProperty("edited_user_id", SharedPref.getUserRegistration().id)
        jsonObject.addProperty("membership_id", data?.get(spinner_mtype.selectedItemPosition - 1)?.id.toString())
        jsonObject.addProperty("membership_fees", etxt_paid_amount.text.toString())
        jsonObject.addProperty("membership_payment_status", payment_status.selectedItem.toString())
        jsonObject.addProperty("membership_paid_on", (System.currentTimeMillis() / 1000).toString())
        jsonObject.addProperty("membership_payment_notes", etxt_comment.text.toString())
        jsonObject.addProperty("membership_from", (fDate.millis / 1000).toString())
        jsonObject.addProperty("membership_period_type", data?.get(spinner_mtype.selectedItemPosition - 1)?.types?.get(spinner_period.selectedItemPosition - 1)?.typeName)
        jsonObject.addProperty("membership_duration", data?.get(spinner_mtype.selectedItemPosition - 1)?.types?.get(spinner_period.selectedItemPosition - 1)?.typeValue.toString())
        val requestBody: RequestBody = jsonObject.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        val requestInterface = RetrofitUtil.createRxResource(this, RestApiService::class.java)
        compositeDisposable.add(
                requestInterface.userMembershipUpdate(requestBody).observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({
                            hideProgressDialog()
                            setResult(Activity.RESULT_OK)
                            finish()
                        }, {
                            hideProgressDialog()
                        })
        )
    }
}
