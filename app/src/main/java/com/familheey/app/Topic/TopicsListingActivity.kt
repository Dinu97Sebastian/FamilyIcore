package com.familheey.app.Topic

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.familheey.app.R
import com.familheey.app.Utilities.Utilities
import kotlinx.android.synthetic.main.activity_topics_listing.*
import kotlinx.android.synthetic.main.item_toolbar_animated_search.*

class TopicsListingActivity : AppCompatActivity() {

    private var topicsListingViewModel: TopicsListingViewModel? = null
    private var topicsListingAdapter: TopicsListingAdapter? = null
    private var topics: MutableList<Topic> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_topics_listing)
        topicsListingViewModel = ViewModelProvider(this).get(TopicsListingViewModel::class.java)
        // topicsListingAdapter = TopicsListingAdapter(topics, this)
        topicsList.layoutManager = LinearLayoutManager(this)
        topicsList.itemAnimator = DefaultItemAnimator()
        topicsList.adapter = topicsListingAdapter
        showShimmer()
        getTopics()
        initListener()
        initializeToolbar()
    }

    private fun initializeToolbar() {
        toolBarTitle.text = "Topics"
        goBack.setOnClickListener { onBackPressed() }
    }

    private fun getTopics() {
        topicsListingViewModel!!.topics.observe(this, Observer { fetchedTopics ->
            topics.clear()
            topics.addAll(fetchedTopics)
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
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                try {
                    val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(searchQuery.windowToken, 0)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                true
            } else {
                false
            }
        }
    }

    private fun showKeyboard() {
        if (searchQuery.requestFocus()) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(searchQuery, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    fun showShimmer() {
        if (shimmerLoader != null) {
            shimmerLoader.visibility = View.VISIBLE
            shimmerLoader.startShimmer()
        }
    }

    fun hideShimmer() {
        if (shimmerLoader != null) {
            shimmerLoader.stopShimmer()
            shimmerLoader.visibility = View.GONE
        }
    }

}
