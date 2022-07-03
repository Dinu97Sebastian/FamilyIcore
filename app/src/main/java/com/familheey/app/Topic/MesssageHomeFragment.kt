package com.familheey.app.Topic

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import com.familheey.app.Adapters.EventTabAdapter
import com.familheey.app.R
import com.familheey.app.Utilities.Utilities
import kotlinx.android.synthetic.main.fragment_message_home.*

class MesssageHomeFragment : Fragment() {

    private var eventTabAdapter: EventTabAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_message_home, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initListener()
        initializeToolbar()
    }

    private fun initializeToolbar() {
        home.visibility = VISIBLE

        eventTabAdapter = EventTabAdapter(activity?.supportFragmentManager!!)
        eventTabAdapter?.addFragment(TopicsListingFragment(), "DIRECT CONVERSATIONS")
        //Dinu(07-09-2021)-- for hide feed conversation
        eventTabAdapter?.addFragment(FeedConversationFragment.newInstance(), "FEED CONVERSATIONS")
        messagePager.adapter = eventTabAdapter
        tabLayout.setupWithViewPager(messagePager)
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
                    val imm = context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(searchQuery.windowToken, 0)


                    val fragment1 = eventTabAdapter!!.getRegisteredFragment(0) as TopicsListingFragment
                    fragment1.getPostWithShimmer(searchQuery.text.toString())


                    val fragment2 = eventTabAdapter!!.getRegisteredFragment(1) as FeedConversationFragment
                    fragment2.onSearch(searchQuery.text.toString())
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                true
            } else {
                false
            }
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


}
