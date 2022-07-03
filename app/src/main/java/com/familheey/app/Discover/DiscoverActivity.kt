package com.familheey.app.Discover

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.familheey.app.Adapters.EventTabAdapter
import com.familheey.app.Discover.ui.main.DiscoverEventFragment
import com.familheey.app.Discover.ui.main.DiscoverFamilyFragment
import com.familheey.app.Discover.ui.main.DiscoverMemberFragment
import com.familheey.app.Discover.ui.main.DiscoverPostFragment
import com.familheey.app.R
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_discover.*
import kotlinx.android.synthetic.main.activity_payment_history.view_pager

class DiscoverActivity : AppCompatActivity() {

    private var eventTabAdapter: EventTabAdapter? = null
    var tabPosition = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_discover)
        if (intent.extras != null) {
            tabPosition = intent.extras!!.getInt("POS")
        }
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        eventTabAdapter = EventTabAdapter(supportFragmentManager)
    //    eventTabAdapter?.addFragment(DiscoverPostFragment.newInstance(0), "POST")
        eventTabAdapter?.addFragment(DiscoverFamilyFragment.newInstance(1), "FAMILY")
        eventTabAdapter?.addFragment(DiscoverMemberFragment.newInstance(2), "PEOPLE")
        eventTabAdapter?.addFragment(DiscoverEventFragment.newInstance(3), "EVENTS")

        view_pager.adapter = eventTabAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)
        viewPager.offscreenPageLimit = 4
        viewPager.setCurrentItem(tabPosition, true)
        search_discover.setText("")
        search_discover.onEditorAction(EditorInfo.IME_ACTION_SEARCH)
        initializeSearchClearCallback()

        search_discover.setOnEditorActionListener { _, actionId, _ ->
            onSearchQueryListener(actionId)
        }


        btn_back_home.setOnClickListener {
            onBackPressed()
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.left,
                R.anim.right)
    }
    //CHECKSTYLE:OFF
    private fun initializeSearchClearCallback() {
        search_discover.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                /*
                Not needed
                 **/
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                /*
                Not needed
                 **/
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                /*
                Not needed
                 **/
            }
        })
    }

    //CHECKSTYLE:ON
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }

    protected fun onSearchQueryListener(actionId: Int): Boolean {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            search()
            try {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return true
        }
        return false
    }

    private fun search() {

//        val fragment1 = eventTabAdapter!!.getRegisteredFragment(0) as DiscoverPostFragment
//        fragment1.onSearch(search_discover.text.toString())
        val fragment2 = eventTabAdapter!!.getRegisteredFragment(1) as DiscoverFamilyFragment
        fragment2.onSearch(search_discover.text.toString())
        val fragment3 = eventTabAdapter!!.getRegisteredFragment(2) as DiscoverMemberFragment
        fragment3.onSearch(search_discover.text.toString())
        val fragment4 = eventTabAdapter!!.getRegisteredFragment(3) as DiscoverEventFragment
        fragment4.onSearch(search_discover.text.toString())


    }

}