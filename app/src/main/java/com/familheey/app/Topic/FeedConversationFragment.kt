package com.familheey.app.Topic

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.familheey.app.R
import com.familheey.app.Utilities.SharedPref
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_discover_event.data_list
import kotlinx.android.synthetic.main.fragment_discover_event.discover_refresh
import kotlinx.android.synthetic.main.fragment_discover_event.layoutEmpty
import kotlinx.android.synthetic.main.fragment_discover_event.shimmer_view_container
import kotlinx.android.synthetic.main.fragment_feed_conversation.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class FeedConversationFragment : Fragment() {

    private var compositeDisposable = CompositeDisposable()
    var query: String? = ""
    private var data: ArrayList<PostMessage> = ArrayList()
    private var discoverEventAdapter: FeedChatAdapter? = null


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_feed_conversation, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        discover_refresh.setOnRefreshListener {
            data.clear()
            discoverEventAdapter?.notifyDataSetChanged()
            showShimmer()
            discover_refresh.isRefreshing = false
            getUserCommentedPost()
        }
        initRecyclerView()
        getUserCommentedPost()
    }

    private fun initRecyclerView() {
        discoverEventAdapter = FeedChatAdapter(data)
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

    private fun getUserCommentedPost() {
        val jsonObject = JsonObject()
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().id)
        jsonObject.addProperty("query", query)

        val requestBody: RequestBody = jsonObject.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        val requestInterface = RetrofitUtil.createRxResource(activity?.applicationContext!!, RestApiService::class.java)
        compositeDisposable.add(
                requestInterface.getUserCommentedPost(requestBody).observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({
                            hideShimmer()
                            discover_refresh.visibility = View.VISIBLE
                            layoutEmpty.visibility = View.GONE
                            data.addAll(it.body()!!)
                            discoverEventAdapter?.notifyDataSetChanged()

                            if (data.size == 0) {
                                discover_refresh.visibility = View.GONE
                                layoutEmpty.visibility = View.VISIBLE
                                if (!query.equals("")) {
                                    txt_msge.text = "Sorry, no results found!"
                                }
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
        getUserCommentedPost()

    }

    companion object {

        @JvmStatic
        fun newInstance(): FeedConversationFragment {
            return FeedConversationFragment().apply {

            }
        }
    }
}

