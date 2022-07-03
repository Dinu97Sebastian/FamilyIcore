package com.familheey.app.Discover.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.familheey.app.Discover.model.PaginationScrollListener
import com.familheey.app.Fragments.Posts.PostData
import com.familheey.app.R
import com.familheey.app.Topic.RestApiService
import com.familheey.app.Topic.RetrofitUtil
import com.familheey.app.Utilities.SharedPref
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_discover_feed.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class DiscoverPostFragment : Fragment() {

    private var discoverPostAdapter: DiscoverPostAdapter? = null
    private var data: ArrayList<PostData> = ArrayList()
    private var compositeDisposable = CompositeDisposable()
    var query: String? = ""

    var isLoading = false
    var isLastPage = false
    var currentPage = 0
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_discover_feed, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        discover_refresh.setOnRefreshListener {
            currentPage = 0
            data.clear()
            discoverPostAdapter?.notifyDataSetChanged()
            showShimmer()
            discover_refresh.isRefreshing = false
            getPost()
        }
        initRecyclerView()
        getPost()
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

    private fun getPost() {
        val jsonObject = JsonObject()
        jsonObject.addProperty("searchtxt", this.query)
        jsonObject.addProperty("userid", SharedPref.getUserRegistration().id)
        jsonObject.addProperty("type", "posts")
        jsonObject.addProperty("offset", currentPage.toString())
        jsonObject.addProperty("limit", "30")
        val requestBody: RequestBody = jsonObject.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        val requestInterface = RetrofitUtil.createRxResource(activity?.applicationContext!!, RestApiService::class.java)
        compositeDisposable.add(
                requestInterface.searchData(requestBody).observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({
                            hideShimmer()
                            if (it.body()?.posts?.data!!.size > 0) {
                                discover_refresh.visibility = View.VISIBLE
                                layoutEmpty.visibility = View.GONE
                                isLoading = false
                                discoverPostAdapter?.notifyDataSetChanged()
                                data.addAll(it?.body()?.posts?.data!!)
                                currentPage = data.size

                            } else {
                                isLastPage = true
                            }

                            if (data.size == 0) {
                                layoutEmpty.visibility = View.VISIBLE
                                discover_refresh.visibility = View.GONE
                            }


                        }, {
                            hideShimmer()
                        })
        )
    }


    private fun initRecyclerView() {
        discoverPostAdapter = DiscoverPostAdapter(data)
        data_list.adapter = discoverPostAdapter
        val linearLayoutManager = LinearLayoutManager(activity)
        data_list.layoutManager = linearLayoutManager
        data_list.itemAnimator = DefaultItemAnimator()

        data_list.addOnScrollListener(object : PaginationScrollListener(linearLayoutManager) {
            override fun isLastPage(): Boolean {
                return isLastPage
            }

            override fun isLoading(): Boolean {
                return isLoading
            }

            override fun loadMoreItems() {
                isLoading = true
                getPost()
            }

        })

    }

    companion object {
        private const val ARG_SECTION_NUMBER = "section_number"

        @JvmStatic
        fun newInstance(sectionNumber: Int): DiscoverPostFragment {
            return DiscoverPostFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                }
            }
        }
    }

    fun onSearch(query: String) {
        data.clear()
        currentPage = 0
        this.query = query
        discoverPostAdapter?.notifyDataSetChanged()
        showShimmer()
        getPost()

    }
}