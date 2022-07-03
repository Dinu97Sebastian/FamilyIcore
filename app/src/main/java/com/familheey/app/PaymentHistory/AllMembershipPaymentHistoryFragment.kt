package com.familheey.app.PaymentHistory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import cn.pedant.SweetAlert.SweetAlertDialog
import com.familheey.app.Interfaces.ProgressListener
import com.familheey.app.R
import com.familheey.app.Topic.RestApiService
import com.familheey.app.Topic.RetrofitUtil
import com.familheey.app.Utilities.Constants
import com.familheey.app.Utilities.Utilities
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_payment_history.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

class AllMembershipPaymentHistoryFragment : Fragment(), ProgressListener, AllPaymentHistoryAdapter.ItemClick {


    private var progressDialog: SweetAlertDialog? = null
    private var compositeDisposable = CompositeDisposable()
    private var groupId: String? = ""
    private var filterStartdate: String? = ""
    private var filterEnddate: String? = ""

    private var query: String? = ""


    var callback: OnHeadlineSelectedListener? = null

    fun setOnHeadlineSelectedListener(callback: OnHeadlineSelectedListener?) {
        this.callback = callback
    }

    interface OnHeadlineSelectedListener {
        fun onArticleSelected(data: Data?)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            groupId = requireArguments().getString(Constants.Bundle.FAMILY_ID)
            filterStartdate = requireArguments().getString("SDATE") + " 1:00"
            filterEnddate = requireArguments().getString("EDATE") + " 23:59"
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_payment_history, container, false)
        getMemberShipPaymentHistory()
        return root
    }


    override fun hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog?.dismiss()
        }
    }

    override fun showProgressDialog() {
        progressDialog = Utilities.getProgressDialog(activity)
        progressDialog?.show()
    }

    private fun getMemberShipPaymentHistory() {
        showProgressDialog()
        val jsonObject = JsonObject()
        jsonObject.addProperty("type", "membership")
        jsonObject.addProperty("group_id", groupId)
        jsonObject.addProperty("query", query)
        if (!filterEnddate.isNullOrEmpty() && !filterStartdate.isNullOrEmpty()) {
            val eDate = DateTime.parse(filterEnddate, DateTimeFormat.forPattern("MM/dd/yyyy HH:mm"))
            val sDate = DateTime.parse(filterStartdate, DateTimeFormat.forPattern("MM/dd/yyyy HH:mm"))
            jsonObject.addProperty("filter_endDate", (eDate.millis / 1000).toString())
            jsonObject.addProperty("filter_startDate", (sDate.millis / 1000).toString())
        }
        val requestBody: RequestBody = jsonObject.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        val requestInterface = RetrofitUtil.createRxResource(activity?.applicationContext!!, RestApiService::class.java)
        compositeDisposable.add(
                requestInterface.paymentHistoryByGroup(requestBody).observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({
                            hideProgressDialog()
                            payment_history_list.layoutManager = LinearLayoutManager(activity)
                            payment_history_list.itemAnimator = DefaultItemAnimator()
                            payment_history_list.adapter = AllPaymentHistoryAdapter(it?.body()?.data, this)
                            empty_screen.visibility = View.GONE
                            payment_history_list.visibility = View.VISIBLE
                            if (it?.body()?.data?.size == 0) {
                                empty_screen.visibility = View.VISIBLE
                                payment_history_list.visibility = View.GONE
                            }
                        }, {
                            hideProgressDialog()
                        })
        )
    }

    //CHECKSTYLE:OFF
    override fun showErrorDialog(errorMessage: String?) {
        /*
        Not Needed
         */
    }

    //CHECKSTYLE:ON
    companion object {

        @JvmStatic
        fun newInstance(sdate: String, edate: String, familyId: String?): AllMembershipPaymentHistoryFragment {
            return AllMembershipPaymentHistoryFragment().apply {
                arguments = Bundle().apply {
                    putString("SDATE", sdate)
                    putString("EDATE", edate)
                    putString(Constants.Bundle.FAMILY_ID, familyId)
                }
            }
        }
    }

    fun onSearch(query: String, filterStartDate: String, filterEndDate: String) {
        this.query = query
        this.filterEnddate = filterEndDate + " 23:59"
        this.filterStartdate = filterStartDate + " 1:00"
        getMemberShipPaymentHistory()
    }

    override fun onNoteClick(position: Int, data: Data?) {
        callback?.onArticleSelected(data)
    }
}