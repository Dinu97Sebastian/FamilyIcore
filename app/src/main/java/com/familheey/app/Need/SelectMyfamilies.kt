package com.familheey.app.Need

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.familheey.app.R
import com.familheey.app.Topic.RestApiService
import com.familheey.app.Topic.RetrofitUtil
import com.familheey.app.Utilities.SharedPref
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_admin_group_listing.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class SelectMyfamilies : AppCompatActivity(), AdminGroupAdapter.ItemClick {

    private var compositeDisposable = CompositeDisposable()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_group_listing)
        toolBarTitle.text = "Select Family"
        goBack.setOnClickListener {
            onBackPressed()
        }
        getUserAdminGroups()
    }

    fun showShimmer() {
        refresh.visibility = View.GONE
        //emptyResultText.setVisibility(View.GONE)
        if (shimmer_view_container != null) {
            shimmer_view_container.visibility = View.VISIBLE
            shimmer_view_container.startShimmer()
        }
    }

    fun hideShimmer() {
        if (shimmer_view_container != null) {
            shimmer_view_container.stopShimmer()
            shimmer_view_container.visibility = View.GONE
        }
        refresh.visibility = View.VISIBLE
    }

    override fun onItemClick(id: String?, name: String?, account: String?) {
        val returnIntent = Intent()
        returnIntent.putExtra("DATA", id)
        returnIntent.putExtra("NAME", name)
        if (account != null) {
            returnIntent.putExtra("ACC", account)
        } else {
            returnIntent.putExtra("ACC", "")
        }
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }

    private fun getUserAdminGroups() {
        val jsonObject = JsonObject()
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().id)
        val requestBody: RequestBody = jsonObject.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        val requestInterface = RetrofitUtil.createRxResource(this, RestApiService::class.java)
        compositeDisposable.add(
                requestInterface.getUserAdminGroups(requestBody).observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({
                            hideShimmer()
                            list_recyclerView.layoutManager = LinearLayoutManager(this)
                            list_recyclerView.adapter = AdminGroupAdapter(it.body()?.data, this)

                            if (it.body()?.data!!.isEmpty()) {
                                layoutEmpty.visibility = View.VISIBLE
                                list_recyclerView.visibility = View.GONE
                            } else {
                                layoutEmpty.visibility = View.GONE
                                list_recyclerView.visibility = View.VISIBLE
                            }
                        }, {
                            hideShimmer()
                        })
        )
    }

}