package com.familheey.app.Discover.ui.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.familheey.app.Activities.FamilyDashboardActivity
import com.familheey.app.Discover.model.DiscoverGroups
import com.familheey.app.R.layout
import com.familheey.app.Topic.RestApiService
import com.familheey.app.Topic.RetrofitUtil
import com.familheey.app.Utilities.Constants
import com.familheey.app.Utilities.SharedPref
import com.familheey.app.Utilities.Utilities
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_discover.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody


class DiscoverFamilyFragment : Fragment(), DiscoverFamilyAdapter.OnFamilyJoiningStatusChanged {

    private var compositeDisposable = CompositeDisposable()
    var query: String = ""
    private var progressDialog: SweetAlertDialog? = null
    private var discoverFamilyAdapter: DiscoverFamilyAdapter? = null
    private var data: ArrayList<DiscoverGroups> = ArrayList()
    private val DASHBOARD_REQUEST_CODE  = 1004
    var isLoading = false
    var isLastPage = false
    var currentPage = 0

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(layout.fragment_discover, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        discover_refresh.setOnRefreshListener {
            data.clear()
            discoverFamilyAdapter?.notifyDataSetChanged()
            showShimmer()
            discover_refresh.isRefreshing = false
            getFamily()
        }
        initRecyclerView()

        getFamily()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == DASHBOARD_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            this.data.clear()
            discoverFamilyAdapter?.notifyDataSetChanged()
            showShimmer()
            getFamily()
        }
    }

    private fun initRecyclerView() {
        discoverFamilyAdapter = DiscoverFamilyAdapter(data, this, requireContext())
        data_list.adapter = discoverFamilyAdapter
        val linearLayoutManager = LinearLayoutManager(activity)
        data_list.layoutManager = linearLayoutManager
        data_list.itemAnimator = DefaultItemAnimator()

        data_list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView,
                                    dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (linearLayoutManager.findLastCompletelyVisibleItemPosition() == data.size - 1)
                {
                    getFamily()
                    discover_refresh.updatePadding(bottom = 142)
                       progresbar.visibility=View.VISIBLE
                    isLoading = true
                }
            }
        })


//        data_list.addOnScrollListener(object : PaginationScrollListener(linearLayoutManager) {
//
//
//            override fun isLastPage(): Boolean {
//                return isLastPage
//            }
//
//            override fun isLoading(): Boolean {
//                return isLoading
//            }
//
//            override fun loadMoreItems() {
//                progresbar.visibility=View.VISIBLE
//                discoverFamilyAdapter!!.addLoadingFooter()
//                isLoading = true
//                getFamily()
//            }
//
//        })
    }
    public fun showProgresbar(){

    }
    fun showShimmer() {
        discover_refresh.visibility = View.GONE

        shimmer_view_container.visibility = View.VISIBLE
        shimmer_view_container.startShimmer()
    }

    fun hideShimmer() {
        if (shimmer_view_container != null) {
            shimmer_view_container.stopShimmer()
            shimmer_view_container.visibility = View.GONE
        }
    }

    private fun getFamily() {
        val jsonObject = JsonObject()

        if (SharedPref.read(SharedPref.IS_REGISTERED, false)) {
            jsonObject.addProperty("userid", SharedPref.getUserRegistration().id)
        }
        jsonObject.addProperty("searchtxt", this.query)
        jsonObject.addProperty("type", "family")
        jsonObject.addProperty("offset", data.size.toString())
        jsonObject.addProperty("limit", "30")

        val requestBody: RequestBody = jsonObject.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        val requestInterface = RetrofitUtil.createRxResource(activity?.applicationContext!!, RestApiService::class.java)
        compositeDisposable.add(
                requestInterface.searchData(requestBody).observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({
                            hideShimmer()
                            if (it.body()?.groups!!.size == 30) {
                                  progresbar.visibility = View.GONE
                                discover_refresh.updatePadding(bottom = 0)
                                isLoading = false
                            } else {
                                progresbar.visibility = View.GONE
                                discover_refresh.updatePadding(bottom = 0)
                                isLastPage = true
                            }

                            data.addAll(it.body()?.groups!!)

                            if (data != null && data.size > 0) {
                                layoutEmpty.visibility = View.GONE
                                discover_refresh.visibility = View.VISIBLE
                                discoverFamilyAdapter?.notifyDataSetChanged()
                            } else {

                                layoutEmpty.visibility = View.VISIBLE
                                discover_refresh.visibility = View.GONE
                            }

                        }, {
                        })
        )
    }

    fun onSearch(query: String) {
        data.clear()
        this.query = query
        discoverFamilyAdapter?.notifyDataSetChanged()
        showShimmer()
        getFamily()

    }

    fun hideProgressDialog() {
        if (progressDialog != null) progressDialog?.dismiss()
    }

    fun showProgressDialog() {
        progressDialog = Utilities.getProgressDialog(activity)
        progressDialog?.show()
    }

    companion object {
        private const val ARG_SECTION_NUMBER = "section_number"

        @JvmStatic
        fun newInstance(sectionNumber: Int): DiscoverFamilyFragment {
            return DiscoverFamilyFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                }
            }
        }

    }

    override fun onFamilyJoinRequested(family: DiscoverGroups?, position: Int) {

        showProgressDialog()
        val jsonObject = JsonObject()
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().id)
        jsonObject.addProperty("group_id", family?.groupId.toString())
        val requestBody: RequestBody = jsonObject.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        val requestInterface = RetrofitUtil.createRxResource(activity?.applicationContext!!, RestApiService::class.java)

        compositeDisposable.add(requestInterface.joinFamily(requestBody).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ response ->
                    if (response.isSuccessful) {
                        hideProgressDialog()
                        val responseJson = response.body()!!
                        val status = responseJson.get("data").asJsonObject.get("status").asString
                        val type = responseJson.get("data").asJsonObject.get("type").asString
                        when {
                            "Pending".equals(status, true) -> {
                                family.let {
                                    it?.isJoined = null
                                    it?.memberJoining = 0
                                    it?.status = "Pending"
                                    it?.type = type
                                }
                            }
                            else -> {
                                family.let {
                                    it?.isJoined = "1"
                                    it?.memberJoining = 0
                                    it?.status = "Member"
                                    it?.type = type
                                }
                            }
                        }
                    }
                    discoverFamilyAdapter?.notifyDataSetChanged()
                }, { error ->
                    hideProgressDialog()
                    error.printStackTrace()
                })
        )
    }

    override fun onStartActivity(family: DiscoverGroups?, position: Int) {

        val intent = Intent(activity, FamilyDashboardActivity::class.java)
        intent.putExtra(Constants.Bundle.DATA, family?.groupId.toString())
        intent.putExtra(Constants.Bundle.TYPE, Constants.Bundle.GLOBAL_SEARCH)
        startActivityForResult(intent, DASHBOARD_REQUEST_CODE)
    }
}