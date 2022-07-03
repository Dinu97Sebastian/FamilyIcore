package com.familheey.app.membership

import Data
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.familheey.app.R
import com.familheey.app.Utilities.Constants
import com.familheey.app.Utilities.Utilities
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_membership_user_list.*
import kotlinx.android.synthetic.main.activity_topics_listing.shimmerLoader
import kotlinx.android.synthetic.main.activity_topics_listing.topicsList
import kotlinx.android.synthetic.main.item_toolbar_animated_search.constraintSearch
import kotlinx.android.synthetic.main.item_toolbar_animated_search.imageBack
import kotlinx.android.synthetic.main.item_toolbar_animated_search.imgSearch
import kotlinx.android.synthetic.main.item_toolbar_animated_search.searchQuery
import kotlinx.android.synthetic.main.item_toolbar_animated_search.toolBarTitle

class MembershipUserListActivity : AppCompatActivity(), MemberAdapter.MemberItemClick {
    private val EDIT_MEMBERSHIP_REQUEST_CODE  = 4001
    private val EDIT_USER_MEMBERSHIP_REQUEST_CODE  = 6001
    private var memberListingViewModel: MemberListingViewModel? = null
    private var memberAdapter: MemberAdapter? = null
    private var topics: MutableList<Data> = mutableListOf()
    private var familyId: String = ""
    private var mId: String = ""
    private var mName: String = ""
    private var type: String = ""
    private var default_mid: String? = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_membership_user_list)

        mId = intent.extras?.getString(Constants.Bundle.ID)!!
        familyId = intent.extras?.getString(Constants.Bundle.FAMILY_ID)!!
        mName = intent.extras?.getString(Constants.Bundle.DATA)!!
        type = intent.extras?.getString(Constants.Bundle.TYPE)!!
        default_mid = intent.extras?.getString(Constants.Bundle.IDENTIFIER)
        if (type == "LIST") {
            toolBarTitle.text = "Members"
            addUser.visibility = View.GONE
            feedback.visibility = View.GONE
            imgSearch.visibility = View.GONE
        } else {
            imgSearch.visibility = View.GONE
            toolBarTitle.text = mName
        }
        memberAdapter = MemberAdapter(topics, this)
        topicsList.layoutManager = LinearLayoutManager(this)
        topicsList.itemAnimator = DefaultItemAnimator()
        topicsList.adapter = memberAdapter
        memberListingViewModel = ViewModelProvider(this, MembershipUsersViewModelFactory(application, familyId, type, mId)).get(MemberListingViewModel::class.java)
        showShimmer()
        getTopics()
        initListener()

    }

    override fun onItemClick(position: Int) {

        startActivityForResult(Intent(this, EditUserMembershipActivity::class.java).putExtra(Constants.Bundle.FAMILY_ID, familyId).putExtra(Constants.Bundle.DATA, Gson().toJson(topics[position])).putExtra(Constants.Bundle.TYPE, mName), EDIT_USER_MEMBERSHIP_REQUEST_CODE)
    }

    override fun onSendReminderClick(position: Int) {
        memberListingViewModel?.getReminder(familyId, topics[position].userId.toString())
        Toast.makeText(this, "Sent Reminder", Toast.LENGTH_LONG).show()
    }

    override fun onEditClick(position: Int) {
        startActivityForResult(Intent(this, EditUserMembershipActivity::class.java).putExtra(Constants.Bundle.FAMILY_ID, familyId).putExtra(Constants.Bundle.DATA, Gson().toJson(topics[position])).putExtra(Constants.Bundle.TYPE, mName), EDIT_USER_MEMBERSHIP_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EDIT_USER_MEMBERSHIP_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            topics.clear()
            memberAdapter?.notifyDataSetChanged()
            showShimmer()
            memberListingViewModel?.getTopics(familyId, mId)
        } else if (requestCode == EDIT_MEMBERSHIP_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            toolBarTitle.text = data?.extras?.getString("type")
            topics.clear()
            memberAdapter?.notifyDataSetChanged()
            showShimmer()
            memberListingViewModel?.getTopics(familyId, mId)
        }
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun getTopics() {
        memberListingViewModel!!.topics.observe(this, Observer { fetchedTopics ->
            if (fetchedTopics != null) {

                empty_view.visibility = View.GONE
                topicsList.visibility = View.VISIBLE

                if (type != "LIST") {
                    addUser.visibility = View.VISIBLE
                }
                topics.clear()
                topics.addAll(fetchedTopics)
                memberAdapter?.notifyDataSetChanged()
            }
            hideShimmer()
            if (topics.isEmpty()) {
                empty_view.visibility = View.VISIBLE
                topicsList.visibility = View.GONE
                addUser.visibility = View.GONE
            }
        })


    }

    private fun initListener() {
        if (mId.equals(default_mid)) {
            feedback.visibility = View.INVISIBLE
        }

        btn_add.setOnClickListener {
            startActivityForResult(Intent(this, MembershipUserListActivity::class.java).putExtra(Constants.Bundle.FAMILY_ID, familyId).putExtra(Constants.Bundle.ID, mId).putExtra(Constants.Bundle.DATA, mName).putExtra(Constants.Bundle.TYPE, "LIST"), EDIT_USER_MEMBERSHIP_REQUEST_CODE)
        }
        addUser.setOnClickListener {
            startActivityForResult(Intent(this, MembershipUserListActivity::class.java).putExtra(Constants.Bundle.FAMILY_ID, familyId).putExtra(Constants.Bundle.ID, mId).putExtra(Constants.Bundle.DATA, mName).putExtra(Constants.Bundle.TYPE, "LIST"), EDIT_USER_MEMBERSHIP_REQUEST_CODE)
        }
        feedback.setOnClickListener {
            showMenus(it)
        }
        goBack.setOnClickListener {
            setResult(Activity.RESULT_OK)
            onBackPressed()
        }
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


        refresh.setOnRefreshListener {

            refresh.isRefreshing = false
            topics.clear()
            showShimmer()
            memberAdapter?.notifyDataSetChanged()
            memberListingViewModel?.getTopics(familyId, mId)
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


    private fun showMenus(v: View) {
        val popup = PopupMenu(v.context, v)
        popup.menuInflater.inflate(R.menu.membership_navigation_more, popup.menu)
        popup.setOnMenuItemClickListener { item: MenuItem ->
            if (item.itemId == R.id.edit) {
                startActivityForResult(Intent(this, EditMembershipActivity::class.java).putExtra(Constants.Bundle.FAMILY_ID, familyId).putExtra(Constants.Bundle.ID, mId).putExtra(Constants.Bundle.DATA, mName), EDIT_MEMBERSHIP_REQUEST_CODE)
            }
            true
        }
        popup.show()
    }
}
