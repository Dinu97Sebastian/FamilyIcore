package com.familheey.app.membership

import MembershipTypePeriod
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.familheey.app.Interfaces.ProgressListener
import com.familheey.app.Need.InputFilterMinMax
import com.familheey.app.R
import com.familheey.app.Topic.RestApiService
import com.familheey.app.Topic.RetrofitUtil
import com.familheey.app.Utilities.Constants
import com.familheey.app.Utilities.SharedPref
import com.familheey.app.Utilities.Utilities
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_add_membership.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody


class EditMembershipActivity : AppCompatActivity(), ProgressListener {

    private var progressDialog: SweetAlertDialog? = null
    private var compositeDisposable = CompositeDisposable()
    private var membershipTypePeriods: ArrayList<MembershipTypePeriod>? = null
    private var familyId: String = ""
    private var mId: String = ""
    private var mName: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_membership)

        mId = intent.extras?.getString(Constants.Bundle.ID)!!
        familyId = intent.extras?.getString(Constants.Bundle.FAMILY_ID)!!
        mName= intent.extras?.getString(Constants.Bundle.DATA)!!
        etxt_fees.filters = arrayOf<InputFilter>(InputFilterMinMax("0", "999999"))
        toolBarTitle.text = "Edit Membership Type"
        goBack.setOnClickListener { onBackPressed() }
        getMemberShipType()
        btn_submit.setOnClickListener {
            if (validate()) createMemberShip()
        }
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
                Don't need this
               *
               */
    }

    private fun validate(): Boolean {
        if (ext_mtype.text.isBlank())
            return false
        if (etxt_fees.text.isBlank())
            return false
        if (spinner_period.selectedItemPosition == 0)
            return false
        return true
    }

    private fun createMemberShip() {
        showProgressDialog()

        val duration = membershipTypePeriods?.get(spinner_period.selectedItemPosition - 1)?.membershipLookupPeriod
        val id = membershipTypePeriods?.get(spinner_period.selectedItemPosition - 1)?.id
        val requestBody = Membership(mId,ext_mtype.text.toString(), familyId, SharedPref.getUserRegistration().id, duration.toString(), id.toString(),"USD", etxt_fees.text.toString().toInt(), active.isChecked)
        val requestInterface = RetrofitUtil.createRxResource(this, RestApiService::class.java)
        compositeDisposable.add(
                requestInterface.updateMembershipLookup(requestBody).observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({
                            hideProgressDialog()
                            val returnIntent = Intent()
                            returnIntent.putExtra("type", ext_mtype.text.toString())
                            setResult(Activity.RESULT_OK, returnIntent)
                            finish()
                        }, {
                            hideProgressDialog()
                        })
        )
    }

    private fun getMemberShip() {
        showProgressDialog()
        val jsonObject = JsonObject()
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().id)
        jsonObject.addProperty("membership_id", mId)
        val requestBody: RequestBody = jsonObject.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        val requestInterface = RetrofitUtil.createRxResource(this, RestApiService::class.java)
        compositeDisposable.add(
                requestInterface.getMembershiptypeById(requestBody).observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({
                            etxt_fees.setText(it.body()?.doc?.membershipFees.toString())
                            ext_mtype.setText(it.body()?.doc?.membershipName)
                            active.isChecked = it.body()?.doc?.isActive!!

                            for (i in 0 until membershipTypePeriods!!.size) {
                                if (membershipTypePeriods!![i].id == it.body()?.doc?.membershipPeriodTypeId) {
                                    spinner_period.setSelection(i + 1)
                                    break
                                }
                            }
                            hideProgressDialog()
                        }, {
                            hideProgressDialog()
                        })
        )
    }
    private fun getMemberShipType() {
        showProgressDialog()
        val requestInterface = RetrofitUtil.createRxResource(this, RestApiService::class.java)
        compositeDisposable.add(
                requestInterface.getMembershipLookupPeriods().observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({
                            hideProgressDialog()
                            val options = ArrayList<String>()
                            options.add("Select")
                            membershipTypePeriods = it.body()
                            for (mem in membershipTypePeriods!!) {
                                options.add(mem.membershipLookupPeriodType)
                            }
                            val spinnerArrayAdapter: ArrayAdapter<String>? = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, options)
                            spinnerArrayAdapter!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            spinner_period.adapter = spinnerArrayAdapter
                            getMemberShip()
                        }, {
                            hideProgressDialog()
                        })
        )
    }
}
