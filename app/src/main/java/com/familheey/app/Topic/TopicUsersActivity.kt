package com.familheey.app.Topic

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.familheey.app.R
import com.familheey.app.Utilities.Constants
import com.familheey.app.Utilities.Constants.Bundle.*
import com.familheey.app.Utilities.SharedPref
import com.familheey.app.Utilities.Utilities
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_topic_users.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class TopicUsersActivity : AppCompatActivity(), OnUserSelectedStatusListener {

    private var topicUsersViewModel: TopicUsersViewModel? = null
    private var topicUsersAdapter: TopicUsersAdapter? = null
    private var users: MutableList<User> = mutableListOf()
    private var searchQueryApi: String = ""
    private lateinit var topicId: String
    private var selectedUserIds = mutableListOf<Int>()
    private val compositeDisposable = CompositeDisposable()
    private var isCreationMode = false
    private var disableSelectionMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_topic_users)
        topicId = intent.getStringExtra(DATA)!!
        isCreationMode = intent.getBooleanExtra(IDENTIFIER, false)
        disableSelectionMode = intent.getBooleanExtra(VIEWING_ONLY, false)
        topicUsersViewModel = ViewModelProvider(this, TopicUsersViewModelFactory(application, topicId)).get(TopicUsersViewModel::class.java)
        topicUsersAdapter = TopicUsersAdapter(users, this)
        usersList.layoutManager = LinearLayoutManager(this)
        usersList.itemAnimator = DefaultItemAnimator()
        usersList.adapter = topicUsersAdapter
        showShimmer()
        getUsers()
        initListener()
        initializeToolbar()
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    private fun initializeToolbar() {
        toolBarTitle.text = "My Connections"
        goBack.setOnClickListener { onBackPressed() }
    }

    private fun getUsers() {
        searchQueryApi = searchQuery.text.toString().trim()
        topicUsersViewModel!!.users.observe(this, Observer { fetchedUsers ->
            users.clear()
            if (fetchedUsers == null) {
                networkError()
            } else {
                for (user in fetchedUsers) {
                    if (selectedUserIds.contains(user.userId))
                        user.currentSelection = true
                    if (disableSelectionMode) {
                        if (user.currentSelection && user.alreadySelected)
                            users.add(user)
                    } else
                        users.add(user)
                }
            }
            if (users.size > 0)
                emptyResultText.visibility = GONE
            else emptyResultText.visibility = VISIBLE
            topicUsersAdapter?.notifyDataSetChanged()
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
                    topicUsersViewModel?.getTopicUsers(topicId, searchQuery.text.toString().trim())
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

        done.setOnClickListener {
            when {
                disableSelectionMode -> finish()
                isCreationMode -> {
                    returnResult()
                }
                else -> updateUsers()
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
        done.visibility = INVISIBLE
        emptyResultText.visibility = GONE
        usersList.visibility = GONE
        if (shimmerLoader != null) {
            shimmerLoader.visibility = VISIBLE
            shimmerLoader.startShimmer()
        }
    }

    fun hideShimmer() {
        done.visibility = VISIBLE
        usersList.visibility = VISIBLE
        if (shimmerLoader != null) {
            shimmerLoader.stopShimmer()
            shimmerLoader.visibility = GONE
        }
    }

    private fun returnResult() {
        val returnIntent = Intent()
        returnIntent.putExtra("DATA", Gson().toJson(selectedUserIds))
        if (selectedUserIds.size > 0)
            setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }

    private fun updateUsers() {
        val jsonArray = JsonArray()
        for (id in selectedUserIds) {
            jsonArray.add(id)
        }
        if (jsonArray.size() > 0)
            addUsersToTopic(jsonArray)
        else Toast.makeText(applicationContext, "Please select users", Toast.LENGTH_SHORT).show()
    }

    private fun addUsersToTopic(jsonArray: JsonArray) {
        val progressDialog = Utilities.getProgressDialog(this)
        progressDialog.show()
        val jsonObject = JsonObject()
        jsonObject.addProperty("created_by", SharedPref.getUserRegistration().id)
        jsonObject.addProperty("topic_id", topicId)
        jsonObject.add("to_user_id", jsonArray)
        val requestBody: RequestBody = jsonObject.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        compositeDisposable.add(
                RetrofitUtil.createRxResource(applicationContext, RestApiService::class.java).addUsersToTopic(requestBody).observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ response ->
                            if (response.code() == 200) {
                                progressDialog?.dismissWithAnimation()
                                val intent = Intent()
                                setResult(Activity.RESULT_OK, intent)
                                finish()
                            } else Utilities.getErrorDialog(progressDialog, Constants.SOMETHING_WRONG).show()
                        }, {
                            Utilities.getErrorDialog(progressDialog, Constants.SOMETHING_WRONG).show()
                        })
        )
    }

    private fun networkError() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Uh oh! Check your internet connection and retry.")
                .setCancelable(false)
                .setPositiveButton("Retry") { _: DialogInterface?, _: Int ->
                    showShimmer()
                    topicUsersViewModel?.getTopicUsers(topicId, searchQuery.text.toString().trim())
                }.setNegativeButton("Cancel") { _: DialogInterface, _: Int ->
                    finish()
                }
        val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        )
        val alert = builder.create()
        alert.setTitle("Connection Unavailable")
        alert.show()
        params.setMargins(0, 0, 20, 0)
        alert.getButton(DialogInterface.BUTTON_NEGATIVE).layoutParams = params
    }

    override fun onUserIdSelected(id: Int) {
        selectedUserIds.add(id)
    }

    override fun onUserIdRemoved(id: Int) {
        selectedUserIds.remove(id)
    }
}
