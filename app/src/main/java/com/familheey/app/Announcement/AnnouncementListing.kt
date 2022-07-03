package com.familheey.app.Announcement

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.familheey.app.Adapters.EventTabAdapter
import com.familheey.app.R
import com.familheey.app.Utilities.Utilities
import kotlinx.android.synthetic.main.activityannouncement.*

class AnnouncementListing : AppCompatActivity() {

    private var eventTabAdapter: EventTabAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activityannouncement)
        btn_back.setOnClickListener { onBackPressed() }

        eventTabAdapter = EventTabAdapter(supportFragmentManager)
        eventTabAdapter?.addFragment(AnnouncementUnReadFragment.newInstance(), "UNREAD")
        eventTabAdapter?.addFragment(AnnouncementReadFragment.newInstance(), "READ")

        view_pager.adapter = eventTabAdapter
        tabs.setupWithViewPager(view_pager)

        initializeSearchClearCallback()
        imgSearch.setOnClickListener {
            Utilities.showCircularReveal(constraintSearch)
            showKeyboard()
        }
        imageBack.setOnClickListener {
            Utilities.hideCircularReveal(constraintSearch)
            search_post.setText("")
            search_post.onEditorAction(EditorInfo.IME_ACTION_SEARCH)
        }
    }

    override fun onBackPressed() {
        finish()

        overridePendingTransition(R.anim.left,
                R.anim.right)
    }


    private fun initializeSearchClearCallback() {
        search_post.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                /*
                Not needed
                 */
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                /*
                Not needed
                 */
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isEmpty()) clearSearch.visibility = View.INVISIBLE else clearSearch.visibility = View.VISIBLE
            }
        })
        clearSearch.setOnClickListener {
            search_post.setText("")
            onSearchQueryListener(EditorInfo.IME_ACTION_SEARCH)

        }

        search_post.setOnEditorActionListener { _, actionId, _ ->
            onSearchQueryListener(actionId)
        }
    }


    private fun onSearchQueryListener(actionId: Int): Boolean {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            val fragment1 = eventTabAdapter!!.getRegisteredFragment(0) as AnnouncementUnReadFragment
            fragment1.onSearch(search_post.text.toString())


            val fragment2 = eventTabAdapter!!.getRegisteredFragment(1) as AnnouncementReadFragment
            fragment2.onSearch(search_post.text.toString())

            try {
                val imm = (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                imm.hideSoftInputFromWindow(search_post.windowToken, 0)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return true
        }
        return false
    }

    private fun showKeyboard() {
        if (search_post.requestFocus()) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(search_post, InputMethodManager.SHOW_IMPLICIT)
        }

    }
}