package com.familheey.app.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.familheey.app.Activities.FamilyDashboardActivity
import com.familheey.app.Models.Response.Family
import com.familheey.app.Models.Response.FamilySearchModal
import com.familheey.app.R
import com.familheey.app.Topic.RestApiService
import com.familheey.app.Topic.RetrofitUtil
import com.familheey.app.Utilities.Constants
import com.familheey.app.Utilities.Constants.ApiPaths
import com.familheey.app.Utilities.SharedPref
import com.familheey.app.Utilities.Utilities
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.item_family_suggestion_new_user.view.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class FamilySuggestionAdapter(val context: Context, var families: List<FamilySearchModal>) : RecyclerView.Adapter<FamilySuggestionAdapter.FamilyiewHolder>() {

    private var compositeDisposable: CompositeDisposable = CompositeDisposable()

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = FamilyiewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_family_suggestion_new_user, parent, false)
    )

    override fun getItemCount() = families.size

    override fun onBindViewHolder(holder: FamilyiewHolder, position: Int) {
        holder.bind(families[position])
    }
    //CHECKSTYLE:OFF
    inner class FamilyiewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val root = view
        private val familyLogo = view.familyLogo
        private val familyName = view.familyName
        private val membersCount = view.membersCount
        private val familyLocation = view.familyLocation
        private val join = view.join
        private val progressBar = view.progressBar
        fun bind(family: FamilySearchModal) {
            if (family.logo != null) {
                Glide.with(familyLogo.context)
                        .load(ApiPaths.S3_DEV_IMAGE_URL_SQUARE + ApiPaths.IMAGE_BASE_URL + Constants.Paths.LOGO + family.logo)
                        .apply(Utilities.getCurvedRequestOptions())
                        .placeholder(R.drawable.family_logo)
                        .transition(DrawableTransitionOptions.withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                        .into(familyLogo)
            } else familyLogo.setImageResource(R.drawable.family_logo)
            familyName.text = family.groupName
            if (family.baseRegion.isNullOrEmpty()) {
                familyLocation.visibility = View.INVISIBLE
            } else {
                familyLocation.visibility = View.VISIBLE
                familyLocation.text = family.baseRegion
            }
            if (family.membercount.isNullOrEmpty()) {
                membersCount.visibility = View.INVISIBLE
            } else {
                membersCount.visibility = View.VISIBLE
                membersCount.text = family.membercount + " members"
            }
            root.setOnClickListener {
                val goToFamily = Family()
                goToFamily.id = Integer.parseInt(family.id)
                val intent = Intent(context, FamilyDashboardActivity::class.java)
                intent.putExtra(Constants.Bundle.DATA, goToFamily)
                context.startActivity(intent)
            }
            when {
                family.isDevSelected -> {
                    progressBar.visibility = View.VISIBLE
                    join.text = ""
                }
                else -> {
                    progressBar.visibility = View.INVISIBLE
                    when (family.newUserFamilyJoiningStatus) {
                        FamilySearchModal.JOIN -> join.text = "Join"
                        FamilySearchModal.REJECTED -> join.text = "Rejected"
                        FamilySearchModal.JOINED -> join.text = "Member"
                        FamilySearchModal.PENDING -> join.text = "Pending"
                        FamilySearchModal.PRIVATE -> join.text = "Private"
                    }
                }
            }
            join.setOnClickListener {
                when (family.newUserFamilyJoiningStatus) {

                    FamilySearchModal.REJECTED -> {
                        Toast.makeText(join.context, "Admin has rejected your family joining request", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                    FamilySearchModal.JOINED -> {
                        Toast.makeText(join.context, "Already joined!", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                    FamilySearchModal.PENDING -> {
                        Toast.makeText(join.context, "Already requested for joining!", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                    FamilySearchModal.PRIVATE -> {
                        Toast.makeText(join.context, "Only admin of this group can send you a joining request!", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                }
                family.isDevSelected = true
                notifyDataSetChanged()
                val jsonObject = JsonObject()
                jsonObject.addProperty("user_id", SharedPref.getUserRegistration().id)
                jsonObject.addProperty("group_id", family.id.toString())
                val requestBody: RequestBody = jsonObject.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
                compositeDisposable.add(
                        RetrofitUtil.createRxResource(context, RestApiService::class.java).joinFamily(requestBody).observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(Schedulers.io())
                                .subscribe({ response ->
                                    if (response.isSuccessful) {
                                        val responseJson = response.body()!!
                                        val status = responseJson.get("data").asJsonObject.get("status").asString
                                        if ("Pending".equals(status, true)) {
                                            family.let {
                                                it.isJoined = null
                                                it.memberJoining = null
                                                it.status = "Pending"
                                            }
                                        } else if ("rejected".equals(status, true)) {
                                            family.let {
                                                it.isJoined = null
                                                it.memberJoining = null
                                                it.status = "Rejected"
                                            }
                                        } else {
                                            family.let {
                                                it.isJoined = "1"
                                                it.memberJoining = null
                                                it.status = "Member"
                                            }
                                        }
                                        family.isDevSelected = false
                                    } else {
                                        family.isDevSelected = false
                                    }
                                    notifyDataSetChanged()
                                }, { error ->
                                    family.isDevSelected = false
                                    notifyDataSetChanged()
                                    error.printStackTrace()
                                })
                )
            }
        }
    }
    //CHECKSTYLE:ON
}