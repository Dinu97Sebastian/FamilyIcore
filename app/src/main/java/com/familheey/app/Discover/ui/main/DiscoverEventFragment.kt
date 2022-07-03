package com.familheey.app.Discover.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.familheey.app.Discover.model.DiscoverEvent
import com.familheey.app.R
import com.familheey.app.Topic.RestApiService
import com.familheey.app.Topic.RetrofitUtil
import com.familheey.app.Utilities.SharedPref
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_discover_event.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class DiscoverEventFragment : Fragment() {

    private var compositeDisposable = CompositeDisposable()
    var query: String? = ""
    private var data: ArrayList<DiscoverEvent> = ArrayList()
    private var discoverEventAdapter: DiscoverEventAdapter? = null

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_discover_event, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        discover_refresh.setOnRefreshListener {
            data.clear()
            discoverEventAdapter?.notifyDataSetChanged()
            showShimmer()
            discover_refresh.isRefreshing = false
            getEvents()
        }
        initRecyclerView()
        getEvents()
        data.clear()
    }

    private fun initRecyclerView() {
        discoverEventAdapter = DiscoverEventAdapter(data)
        data_list.adapter = discoverEventAdapter
        data_list.layoutManager = LinearLayoutManager(activity)
        data_list.itemAnimator = DefaultItemAnimator()
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

    private fun getEvents() {
        val jsonObject = JsonObject()

        jsonObject.addProperty("searchtxt", this.query)
        jsonObject.addProperty("userid", SharedPref.getUserRegistration().id)
        jsonObject.addProperty("type", "events")
        jsonObject.addProperty("offset", "0")
        jsonObject.addProperty("limit", "1000")

        val requestBody: RequestBody = jsonObject.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        val requestInterface = RetrofitUtil.createRxResource(activity?.applicationContext!!, RestApiService::class.java)
        compositeDisposable.add(
                requestInterface.searchData(requestBody).observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({
                            hideShimmer()
                            data.addAll(it.body()?.events!!)
                            if (data != null && data.size > 0) {
                                layoutEmpty.visibility = View.GONE
                                discover_refresh.visibility = View.VISIBLE
                                discoverEventAdapter?.notifyDataSetChanged()

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
        discoverEventAdapter?.notifyDataSetChanged()
        showShimmer()
        getEvents()

    }

    companion object {
        private const val ARG_SECTION_NUMBER = "section_number"

        @JvmStatic
        fun newInstance(sectionNumber: Int): DiscoverEventFragment {
            return DiscoverEventFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                }
            }
        }
    }
}