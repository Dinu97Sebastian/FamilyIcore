package com.familheey.app.Topic

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.familheey.app.R
import com.familheey.app.Utilities.Constants.Bundle.DATA
import com.familheey.app.Utilities.Utilities
import kotlinx.android.synthetic.main.activity_topics_listing.shimmerLoader
import kotlinx.android.synthetic.main.activity_topics_listing.topicsList
import kotlinx.android.synthetic.main.fragment_topics_listing.*

class TopicsListingFragment : Fragment(),TopicsListingAdapter.OnTopicClickListener {
    private var topicsListingViewModel: TopicsListingViewModel? = null
    private var topicsListingAdapter: TopicsListingAdapter? = null
    private var topics: MutableList<Topic> = mutableListOf()
    private val REQUEST_CODE  = 400
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        topicsListingViewModel = ViewModelProvider(this).get(TopicsListingViewModel::class.java)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_topics_listing, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance(topicId: String) =
                TopicsListingFragment().apply {
                    arguments = Bundle().apply {
                        putString(DATA, topicId)
                    }
                }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        topicsListingAdapter = TopicsListingAdapter(topics, requireContext(),this)
        topicsList.layoutManager = LinearLayoutManager(requireContext())
        topicsList.itemAnimator = DefaultItemAnimator()
        topicsList.adapter = topicsListingAdapter
        showShimmer()
        getTopics()
        initListener()
        initializeToolbar()
    }

    private fun initializeToolbar() {
        toolBarTitle.text = "Messages"
        home.visibility = VISIBLE
    }

    fun getTopics() {
        topicsListingViewModel!!.topics.observe(viewLifecycleOwner, Observer { fetchedTopics ->
            topics.clear()
            if (fetchedTopics == null) {
                layoutEmpty.visibility = VISIBLE
            } else {
                topics.addAll(fetchedTopics)
            }
            if (topics.size > 0)
                layoutEmpty.visibility = INVISIBLE
            else layoutEmpty.visibility = VISIBLE
            topicsListingAdapter?.notifyDataSetChanged()
            hideShimmer()
        })
    }

    private fun initListener() {
        imgSearch.setOnClickListener {
            Utilities.showCircularReveal(constraintSearch)
            searchQuery.setText("")
            showKeyboard()
        }
        imageBack.setOnClickListener {
            Utilities.hideCircularReveal(constraintSearch)
            searchQuery.setText("")
            searchQuery.onEditorAction(EditorInfo.IME_ACTION_SEARCH)
        }

        searchQuery.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                try {
                    showShimmer()
                    topics.clear()
                    topicsListingAdapter?.notifyDataSetChanged()
                    topicsListingViewModel?.getTopics(searchQuery.text.toString().trim())
                    val imm = context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(searchQuery.windowToken, 0)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                true
            } else {
                false
            }
        }

        swipeRefreshLayout.setOnRefreshListener {
            swipeRefreshLayout.isRefreshing = false
            showShimmer()
            topicsListingViewModel?.getTopics(searchQuery.text.toString().trim())
        }

        createTopic.setOnClickListener {
            val intent = Intent(context, CreateTopic::class.java)
            startActivityForResult(intent, REQUEST_CODE)
            (context as Activity).overridePendingTransition(R.anim.enter,
                    R.anim.exit)
        }

        home.setOnClickListener {
            activity?.onBackPressed()
        }
    }

    private fun showKeyboard() {
        if (searchQuery.requestFocus()) {
            val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(searchQuery, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    fun showShimmer() {
        if (shimmerLoader != null) {
            shimmerLoader.visibility = VISIBLE
            shimmerLoader.startShimmer()
        }
        topicsList.visibility = INVISIBLE
        createTopic.visibility = INVISIBLE
        layoutEmpty.visibility = INVISIBLE
    }

    fun hideShimmer() {
        if (shimmerLoader != null) {
            shimmerLoader.stopShimmer()
            shimmerLoader.visibility = GONE
        }
        topicsList.visibility = VISIBLE
        createTopic.visibility = VISIBLE
    }

    fun getPostWithShimmer(query: String) {
        showShimmer()
        topics.clear()
        topicsListingAdapter?.notifyDataSetChanged()
        topicsListingViewModel?.getTopics(query.trim())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            showShimmer()
            topics.clear()
            topicsListingAdapter?.notifyDataSetChanged()
            topicsListingViewModel?.getTopics(searchQuery.text.toString().trim())

        }
    }

    override fun onLongClicked(topic: Topic) {
        val intent = Intent(activity, EditTopic::class.java)
        intent.putExtra(DATA, topic.topicId.toString())
        startActivityForResult(intent, REQUEST_CODE)
        (context as Activity).overridePendingTransition(R.anim.enter,
                R.anim.exit)
    }

    override fun onClicked(topic: Topic) {
        val intent = Intent(activity, TopicChatActivity::class.java)
        intent.putExtra(DATA, topic.topicId.toString())
        startActivityForResult(intent, REQUEST_CODE)
        (context as Activity).overridePendingTransition(R.anim.enter,
                R.anim.exit)
    }
}
