package com.familheey.app.Discover.ui.main

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.familheey.app.Activities.FamilyDashboardActivity
import com.familheey.app.Activities.LoginActivity
import com.familheey.app.Discover.model.DiscoverGroups
import com.familheey.app.FamilheeyApplication
import com.familheey.app.Models.Response.FamilySearchModal
import com.familheey.app.Networking.Retrofit.ApiServices
import com.familheey.app.Networking.Retrofit.RetrofitBase
import com.familheey.app.R
import com.familheey.app.Utilities.Constants
import com.familheey.app.Utilities.Constants.ApiPaths
import com.familheey.app.Utilities.SharedPref
import com.familheey.app.Utilities.Utilities
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.item_discover_family.view.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.IOException


class DiscoverFamilyAdapter(internal val items: MutableList<DiscoverGroups>?, internal val listener: OnFamilyJoiningStatusChanged, val context: Context) : androidx.recyclerview.widget.RecyclerView.Adapter<DiscoverFamilyAdapter.ViewHolder>() {
    var subscriptions: CompositeDisposable? = CompositeDisposable()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_discover_family, parent, false))
    }

    override fun getItemCount(): Int {
        return items?.size ?: 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
        if (SharedPref.getUserRegistration() != null) {
            holder.itemView.setOnClickListener {
                listener.onStartActivity(items?.get(position), position)
            }
        }

    }


    interface OnFamilyJoiningStatusChanged {
        fun onFamilyJoinRequested(family: DiscoverGroups?, position: Int)
        fun onStartActivity(family: DiscoverGroups?, position: Int)
    }

    inner class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        fun bind(position: Int) {
            val item = items?.get(position)
            itemView.txtGroupName.text = item?.groupName
            itemView.txtcreatedby.text = "By " + item?.createdByName
            itemView.txt_location.text = item?.getFormattedLocation()
            itemView.txt_member_count.text = item?.membercount.toString()
            itemView.txt_post_count.text = item?.postCount.toString()
            if (item?.logo != null) {
                Glide.with(itemView.familyLogo.context)
                        .load(ApiPaths.S3_DEV_IMAGE_URL_SQUARE + ApiPaths.IMAGE_BASE_URL + Constants.Paths.LOGO + item.logo)
                        .apply(Utilities.getCurvedRequestOptionsSmall())
                        .placeholder(R.drawable.family_logo)
                        .transition(DrawableTransitionOptions.withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                        .into(itemView.familyLogo)
            } else {
                Glide.with(itemView.familyLogo.context)
                        .load(R.drawable.family_logo)
                        .into(itemView.familyLogo)
            }

            itemView.familyLogo.setOnClickListener {

            }

            when (item?.getUserJoinedCalculated()) {
                FamilySearchModal.REJECTED, FamilySearchModal.JOIN -> {
                    itemView.join.text = "Join"
                    itemView.join.visibility = View.VISIBLE
                    itemView.txt_joined.visibility = View.GONE
                }
                FamilySearchModal.JOINED -> {
                    itemView.txt_joined.text = "Member"
                    itemView.join.visibility = View.GONE
                    itemView.txt_joined.visibility = View.VISIBLE
                }
                FamilySearchModal.PENDING -> {
                    itemView.txt_joined.text = "Pending"
                    itemView.join.visibility = View.GONE
                    itemView.txt_joined.visibility = View.VISIBLE
                }
                FamilySearchModal.PRIVATE -> {
                    itemView.txt_joined.text = "Private"
                    itemView.join.visibility = View.GONE
                    itemView.txt_joined.visibility = View.VISIBLE
                }
                FamilySearchModal.ACCEPT_INVITATION -> {
                    itemView.join.text = "Accept"
                    itemView.join.visibility = View.VISIBLE
                    itemView.txt_joined.visibility = View.GONE
                }
            }

            itemView.join.setOnClickListener {
                if (SharedPref.getUserRegistration() != null) {
                    when (item?.getUserJoinedCalculated()) {
                        FamilySearchModal.REJECTED, FamilySearchModal.JOIN -> {
                            listener.onFamilyJoinRequested(item, position)
                        }
                        FamilySearchModal.JOINED -> {
                            Toast.makeText(itemView.join.context, "Already joined!", Toast.LENGTH_SHORT).show()
                        }
                        FamilySearchModal.PENDING -> {
                            Toast.makeText(itemView.join.context, "Already requested for joining!", Toast.LENGTH_SHORT).show()

                        }
                        FamilySearchModal.PRIVATE -> {
                            Toast.makeText(itemView.join.context, "Only admin of this group can send you a joining request!", Toast.LENGTH_SHORT).show()
                        }
                        FamilySearchModal.ACCEPT_INVITATION -> {
                            val progressDialog = Utilities.getProgressDialog(itemView.context)
                            progressDialog.show()
                            val jsonObject = JsonObject()
                            jsonObject.addProperty("id", item.reqId.toString())
                            jsonObject.addProperty("user_id", SharedPref.getUserRegistration().id)
                            jsonObject.addProperty("group_id", item.groupId.toString())
                            jsonObject.addProperty("from_id", item.fromId.toString())
                            jsonObject.addProperty("status", "accepted")
                            val application = FamilheeyApplication.get(itemView.context)
                            val requestBody: RequestBody = jsonObject.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
                            val apiServices = RetrofitBase.createRxResource(itemView.context, ApiServices::class.java)

                            subscriptions!!.add(apiServices.respondToFamilyInvitationRx(requestBody)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribeOn(application.defaultSubscribeScheduler())
                                    .subscribe({ response: Response<ResponseBody?> ->
                                        if (response.isSuccessful) {
                                            items?.removeAt(position)
                                            notifyDataSetChanged()
                                        } else {
                                            Toast.makeText(application, Constants.SOMETHING_WRONG, Toast.LENGTH_SHORT).show()
                                        }
                                        progressDialog.dismiss()
                                    }) { throwable: Throwable? ->
                                        if (throwable !is IOException) {
                                            Toast.makeText(application, "Please check your internet connectivity", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(application, Constants.SOMETHING_WRONG, Toast.LENGTH_SHORT).show()
                                        }
                                        progressDialog.dismiss()
                                    })

                        }
                    }
                }
                else{
                    val intent = Intent(context, LoginActivity::class.java)
                    if (item != null) {
                        intent.putExtra(Constants.Bundle.JOIN_FAMILY_ID,item.groupId.toString())
                    }
                    context.startActivity(intent)
                }
            }

        }

    }

}