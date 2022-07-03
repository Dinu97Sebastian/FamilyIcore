package com.familheey.app.membership

import MembershipTypePeriod
import android.app.Activity
import android.os.Bundle
import android.text.InputFilter
import android.widget.ArrayAdapter
import android.widget.Toast
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
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_add_membership.*


class AddMembershipTypeActivity : AppCompatActivity(), ProgressListener {

    private var progressDialog: SweetAlertDialog? = null
    private var compositeDisposable = CompositeDisposable()

    private var membershipTypePeriods: ArrayList<MembershipTypePeriod>? = null
    private var groupId: String? = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_membership)
        groupId = intent.extras?.getString(Constants.Bundle.ID)
        etxt_fees.filters = arrayOf<InputFilter>(InputFilterMinMax("0", "999999"))
        getMemberShipType()
        goBack.setOnClickListener { onBackPressed() }
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
              *  Don't need this
               */
    }

    private fun validate(): Boolean {
        if (ext_mtype.text.isBlank())
            Toast.makeText(this, "Please add membership type", Toast.LENGTH_SHORT).show()
//            return false
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
        val requestBody = Membership(null,ext_mtype.text.toString(), groupId, SharedPref.getUserRegistration().id, duration.toString(),id.toString(), "USD", etxt_fees.text.toString().toInt(), active.isChecked)
        val requestInterface = RetrofitUtil.createRxResource(this, RestApiService::class.java)
        compositeDisposable.add(
                requestInterface.addMembershipLookup(requestBody).observeOn(AndroidSchedulers.mainThread())
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
                        }, {
                            hideProgressDialog()
                        })
        )
    }
}
