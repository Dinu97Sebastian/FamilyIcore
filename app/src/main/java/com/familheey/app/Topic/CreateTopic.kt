package com.familheey.app.Topic

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import cn.pedant.SweetAlert.SweetAlertDialog
import com.familheey.app.Interfaces.ProgressListener
import com.familheey.app.Models.Request.HistoryImages
import com.familheey.app.R
import com.familheey.app.Utilities.Constants
import com.familheey.app.Utilities.SharedPref
import com.familheey.app.Utilities.Utilities
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_create_topic.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody


class CreateTopic : AppCompatActivity(), ProgressListener, TopicsListingAdapter.OnTopicClickListener {
    private var compositeDisposable = CompositeDisposable()
    private var toUsers = ArrayList<String>()
    var historyImages = java.util.ArrayList<HistoryImages>()
    private val TOPIC_EDIT_REQUEST_CODE  = 401
    private val TOPIC_CHAT_REQUEST_CODE  = 400
    private val TOPIC_USER_REQUEST_CODE  = 102
    private var progressDialog: SweetAlertDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_topic)
        init()
        if (intent.getStringExtra(Constants.Bundle.DATA) != null) {
            toUsers.add(intent.getStringExtra(Constants.Bundle.DATA)!!)
            view_people.visibility = View.GONE
            getCommonTopic()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == TOPIC_USER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            toUsers = Gson().fromJson(data!!.extras!!.getString("DATA"), object : TypeToken<java.util.ArrayList<String?>?>() {}.type)
            if (toUsers.size == 1) txt_count.text = "1 People selected" else if (toUsers.size > 1) txt_count.text = toUsers.size.toString() + " People selected"
        } else if (requestCode == TOPIC_CHAT_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            setResult(Activity.RESULT_OK)
            finish()
        } else if (requestCode == TOPIC_EDIT_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            getCommonTopic()
        }
    }

    override fun hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog?.dismiss()
        }
    }

    override fun showProgressDialog() {
        progressDialog = Utilities.getProgressDialog(this)
        progressDialog?.show()
    }

    override fun onLongClicked(topic: Topic) {

        val intent = Intent(this, EditTopic::class.java)
        intent.putExtra(Constants.Bundle.DATA, topic.topicId.toString())
        startActivityForResult(intent, TOPIC_EDIT_REQUEST_CODE)
    }

    override fun onClicked(topic: Topic) {

        val intent = Intent(this, TopicChatActivity::class.java)
        intent.putExtra(Constants.Bundle.DATA, topic.topicId.toString())
        startActivityForResult(intent, TOPIC_CHAT_REQUEST_CODE)
    }
    //CHECKSTYLE:OFF
    override fun showErrorDialog(errorMessage: String?) {
/*
                Don't need this
               */
    }

    //CHECKSTYLE:ON
    private fun init() {
        user_selection.setOnClickListener {
            txt_count.text = ""
            startActivityForResult(Intent(this@CreateTopic, TopicUsersActivity::class.java).putExtra(Constants.Bundle.DATA, "").putExtra(Constants.Bundle.IDENTIFIER, true), TOPIC_USER_REQUEST_CODE)
        }

        btn_submit.setOnClickListener {
            if (validate()) createTopic()
        }
        toolBarTitle.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        finish()
        overridePendingTransition(R.anim.left,
                R.anim.right)
    }

    private fun createTopic() {
        showProgressDialog()
        val requsetbody = TopicCreateRequest(SharedPref.getUserRegistration().id, txt_topic.text.toString(), what_to_post_descrption.text.toString(), historyImages, toUsers)
        val requestInterface = RetrofitUtil.createRxResource(this, RestApiService::class.java)
        compositeDisposable.add(
                requestInterface.createTopic(requsetbody).observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ response ->
                            hideProgressDialog()
                            startActivityForResult(Intent(this, TopicChatActivity::class.java).putExtra(Constants.Bundle.DATA, response.body()?.data?.id.toString()), TOPIC_CHAT_REQUEST_CODE)
                        }, {
                            hideProgressDialog()
                        })
        )
    }

    private fun getCommonTopic() {
        val jsonObject = JsonObject()
        jsonObject.addProperty("login_user", SharedPref.getUserRegistration().id)
        jsonObject.addProperty("second_user", toUsers[0])
        val requestBody: RequestBody = jsonObject.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        compositeDisposable.add(
                RetrofitUtil.createRxResource(this, RestApiService::class.java).getCommonTopicListByUsers(requestBody).observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ response ->
                            if (response.body()?.data?.size!! > 0) {
                                txt_tittle_previous.text = "Previous Conversation"
                            }
                            topic_list.layoutManager = LinearLayoutManager(this)
                            topic_list.itemAnimator = DefaultItemAnimator()
                            topic_list.adapter = TopicsListingAdapter(response.body()?.data!!, this, this)

                        }, { error ->
                            error.printStackTrace()
                        })
        )
    }

    private fun validate(): Boolean {
        //Dinu -> for remove topic validation
//        if (txt_topic.text.isEmpty()) {
//            txt_topic.error = "Topic is required"
//            return false
//        }
        if (toUsers.size == 0) {
            Toast.makeText(this, "Please select the people", Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }


}

