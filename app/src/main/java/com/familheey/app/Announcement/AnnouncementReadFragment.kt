package com.familheey.app.Announcement

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.familheey.app.Activities.ChatActivity
import com.familheey.app.Discover.model.PaginationScrollListener
import com.familheey.app.Fragments.Posts.PostData
import com.familheey.app.Post.PostCommentActivity
import com.familheey.app.R
import com.familheey.app.Topic.RestApiService
import com.familheey.app.Topic.RetrofitUtil
import com.familheey.app.Utilities.Constants
import com.familheey.app.Utilities.SharedPref
import com.google.gson.Gson
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_unread_announcement.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.*

class AnnouncementReadFragment : Fragment(), AnnouncementAdapter.OnAnnouncementSelectedListener {

    private val ANNOUNCEMENT_DETAILS_REQUEST_CODE  = 1001
    private val ANNOUNCEMENT_EDIT_REQUEST_CODE  = 101
    private val POST_COMMENT_REQUEST_CODE  = 1003

    private var compositeDisposable = CompositeDisposable()
    private var query: String? = ""
    private var announcementAdapter: AnnouncementAdapter? = null
    private var data: ArrayList<PostData> = ArrayList()
    var isLoading = false
    var isLastPage = false
    var currentPage = 0
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_unread_announcement, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val linearLayoutManager = LinearLayoutManager(activity)
        announcement_list.layoutManager = linearLayoutManager
        announcementAdapter = AnnouncementAdapter(data, this)
        announcement_list.adapter = announcementAdapter
        showShimmer()
        getReadAnnouncement()
        announcement_list.addOnScrollListener(object : PaginationScrollListener(linearLayoutManager) {
            override fun isLastPage(): Boolean {
                return isLastPage
            }

            override fun isLoading(): Boolean {
                return isLoading
            }

            override fun loadMoreItems() {
                isLoading = true
                getReadAnnouncement()
            }

        })
        announcement_refresh.setOnRefreshListener {
            announcement_refresh.isRefreshing = false
            data.clear()
            currentPage = 0
            announcementAdapter?.notifyDataSetChanged()
            showShimmer()
            getReadAnnouncement()
        }
    }

    private fun getReadAnnouncement() {
        val jsonObject = JsonObject()

        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().id)
        jsonObject.addProperty("type", "announcement")
        jsonObject.addProperty("query", query)
        jsonObject.addProperty("read_status", true)
        jsonObject.addProperty("offset", currentPage.toString())
        jsonObject.addProperty("limit", "30")
        val requestBody: RequestBody = jsonObject.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        val requestInterface = RetrofitUtil.createRxResource(activity?.applicationContext!!, RestApiService::class.java)
        compositeDisposable.add(
                requestInterface.getPost(requestBody).observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({
                            hideShimmer()
                            if (it.body()?.read_announcement!!.size > 0) {

                                layoutEmpty.visibility = View.GONE
                                announcement_refresh.visibility = View.VISIBLE
                                data.addAll(it.body()?.read_announcement!!)
                                currentPage = data.size
                                announcementAdapter!!.notifyDataSetChanged()
                                isLoading = false
                            } else {
                                isLastPage = true
                            }
                            if (data.size == 0) {
                                layoutEmpty.visibility = View.VISIBLE
                                announcement_refresh.visibility = View.GONE
                                if (!query.equals("")) {
                                    txt_msge.text = "Sorry, no results found!"
                                }
                            }


                        }, {
                            hideShimmer()
                        })
        )
    }

    override fun onAnnouncementselected(pos: Int) {
        startActivityForResult(Intent(context, AnnouncementDetailActivity::class.java).putExtra("pos", pos).putExtra(Constants.Bundle.TYPE, "LIST"), ANNOUNCEMENT_DETAILS_REQUEST_CODE)

        activity?.overridePendingTransition(R.anim.enter,
                R.anim.exit)

        data.get(pos).setRead_status(false)
        announcementAdapter?.notifyDataSetChanged()
    }


    override fun onAnnouncementChat(pos: Int) {
        startActivityForResult(Intent(context, PostCommentActivity::class.java)
                .putExtra(Constants.Bundle.DATA, data[pos])
                .putExtra(Constants.Bundle.SUB_TYPE, "ANNOUNCEMENT")
                .putExtra("POS", pos)
                .putExtra(Constants.Bundle.TYPE, ""), POST_COMMENT_REQUEST_CODE)
        activity?.overridePendingTransition(R.anim.enter,
                R.anim.exit)
    }

    override fun onAnnouncementEdit(pos: Int) {

        startActivityForResult(Intent(context, EditAnnouncementActivity::class.java).putExtra("pos", pos).putExtra("POST", Gson().toJson(data[pos])), ANNOUNCEMENT_EDIT_REQUEST_CODE)
        activity?.overridePendingTransition(R.anim.enter,
                R.anim.exit)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ANNOUNCEMENT_EDIT_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            val pos = Objects.requireNonNull(data!!.extras!!).getInt("pos")
            val postData = Gson().fromJson(data.extras!!.getString("data"), PostData::class.java)
            this.data[pos].post_attachment = postData.post_attachment
            this.data[pos].setIs_shareable(postData.getIs_shareable())
            this.data[pos].snap_description = postData.snap_description
            this.data[pos].conversation_enabled = postData.conversation_enabled
            announcementAdapter!!.notifyDataSetChanged()
        } else if (requestCode == POST_COMMENT_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (Objects.requireNonNull(data!!.extras!!).getBoolean("isChat")) {
                this.data.clear()
                currentPage = 0
                getReadAnnouncement()
            } else {
                this.data[data.extras!!.getInt(Constants.Bundle.POSITION)].conversation_count_new = "0"
                announcementAdapter!!.notifyDataSetChanged()
            }
        } else {
            this.data.clear()
            currentPage = 0
            announcementAdapter!!.notifyDataSetChanged()
            showShimmer()
            getReadAnnouncement()
        }
    }


    //CHECKSTYLE:ON
    companion object {

        @JvmStatic
        fun newInstance(): AnnouncementReadFragment {
            return AnnouncementReadFragment().apply {
                arguments = Bundle().apply {
                }
            }
        }
    }


    fun showShimmer() {
        announcement_refresh.visibility = View.GONE
        shimmer_view_container.visibility = View.VISIBLE
        shimmer_view_container.startShimmer()
    }

    fun hideShimmer() {

        if (shimmer_view_container != null) {
            shimmer_view_container.stopShimmer()
            shimmer_view_container.visibility = View.GONE
        }
    }

    fun onSearch(query: String) {
        data.clear()
        currentPage = 0
        announcementAdapter?.notifyDataSetChanged()
        this.query = query
        getReadAnnouncement()
    }

}