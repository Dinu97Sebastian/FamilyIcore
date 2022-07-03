package com.familheey.app.Topic

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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


class EditTopic : AppCompatActivity(), ProgressListener {
    lateinit var topicId: String
    private lateinit var createId: String
    private var compositeDisposable = CompositeDisposable()
    private var toUsers = ArrayList<Int>()
    var historyImages = java.util.ArrayList<HistoryImages>()
    private val EDIT_TOPIC_REQUEST_CODE  = 102
    private var progressDialog: SweetAlertDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_topic)
        init()
        if (intent.getStringExtra(Constants.Bundle.DATA) != null) {
            topicId = (intent.getStringExtra(Constants.Bundle.DATA)!!)
            toolBarTitle.text = "Edit Topic"
            getSingleTopic()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EDIT_TOPIC_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            toUsers = Gson().fromJson(data!!.extras!!.getString("DATA"), object : TypeToken<java.util.ArrayList<Int?>?>() {}.type)
            if (toUsers.size == 1) txt_count.text = "1 People selected" else if (toUsers.size > 1) txt_count.text = toUsers.size.toString() + " People selected"
        }

    }


    private fun init() {
        user_selection.setOnClickListener {
            startActivityForResult(Intent(this@EditTopic, TopicUsersActivity::class.java).putExtra(Constants.Bundle.DATA, topicId).putExtra(Constants.Bundle.IDENTIFIER, true), EDIT_TOPIC_REQUEST_CODE)
        }

        btn_submit.setOnClickListener {
            if (validate()) updateTopic()
        }
        toolBarTitle.setOnClickListener {
            onBackPressed()
        }
    }


    private fun validate(): Boolean {
        if (txt_topic.text.isEmpty()) {
            txt_topic.error = "Topic is required"
            return false
        }
        if (toUsers.size == 0) {
            Toast.makeText(this, "Please select the people", Toast.LENGTH_LONG).show()
            return false
        }
        return true
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

    override fun showErrorDialog(errorMessage: String?) {
        if (progressDialog != null) {
            progressDialog?.dismiss()
        }
    }

    private fun getSingleTopic() {
        showProgressDialog()
        val jsonObject = JsonObject()
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().id)
        jsonObject.addProperty("topic_id", topicId)
        val requestBody: RequestBody = jsonObject.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        compositeDisposable.add(
                RetrofitUtil.createRxResource(this, RestApiService::class.java).getSingleTopic(requestBody).observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ response ->
                            val topicDetail = response.body()?.data?.get(0)
                            createId = topicDetail?.createdBy.toString()
                            txt_topic.setText(topicDetail?.title)
                            what_to_post_descrption.setText(topicDetail?.description)
                            for (user in topicDetail?.toUsers!!) {
                                toUsers.add(user.userId!!)
                            }
                            if (toUsers.size == 1) txt_count.text = "1 People selected" else if (toUsers.size > 1) txt_count.text = toUsers.size.toString() + " People selected"
                            hideProgressDialog()
                        }, {
                            hideProgressDialog()
                        })
        )
    }
    private fun updateTopic() {
        showProgressDialog()
        val requsetbody = TopicCreateUpdateRequest(topicId, SharedPref.getUserRegistration().id, createId, txt_topic.text.toString(), what_to_post_descrption.text.toString(), historyImages, toUsers)
        val requestInterface = RetrofitUtil.createRxResource(this, RestApiService::class.java)
        compositeDisposable.add(
                requestInterface.updateTopic(requsetbody).observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({
                            hideProgressDialog()
                            setResult(Activity.RESULT_OK)
                            finish()
                        }, {
                            hideProgressDialog()
                        })
        )
    }
}

