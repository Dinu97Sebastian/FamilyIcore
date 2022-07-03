package com.familheey.app.Discover.ui.main

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.familheey.app.Activities.CreatedEventDetailActivity
import com.familheey.app.Activities.ProfileActivity
import com.familheey.app.Discover.model.DiscoverEvent
import com.familheey.app.Models.Response.FamilyMember
import com.familheey.app.R
import com.familheey.app.Utilities.Constants
import com.familheey.app.Utilities.Constants.ApiPaths
import com.familheey.app.Utilities.SharedPref
import com.familheey.app.Utilities.Utilities
import kotlinx.android.synthetic.main.item_discover_event.view.*
import kotlinx.android.synthetic.main.item_discover_event.view.txt_location
import kotlinx.android.synthetic.main.item_discover_post.view.*


class DiscoverEventAdapter(internal val items: MutableList<DiscoverEvent>?) : androidx.recyclerview.widget.RecyclerView.Adapter<DiscoverEventAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_discover_event, parent, false))
    }

    override fun getItemCount(): Int {
        return items?.size ?: 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
        holder.itemView.setOnClickListener {
            holder.itemView.context.startActivity(Intent(holder.itemView.context, CreatedEventDetailActivity::class.java).putExtra(Constants.Bundle.ID, items?.get(position)?.id.toString()))
        }
    }


    inner class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        fun bind(position: Int) {
            val item = items?.get(position)

            itemView.eventName.text = item?.eventName
            // Dinu(19-04-2021)
            if(item?.location==""){
//                itemView.txt_location.text =""
                itemView.txt_location.visibility = View.GONE
                itemView.location_icon.visibility = View.GONE
            }else{
                itemView.txt_location.text = item?.getFormattedLocation()
            }

            itemView.txt_going_count.text = item?.goingCount.toString()
            itemView.txt_interst_count.text = item?.interestedCount.toString()
            itemView.txt_create_user_name.text = item?.createdUser
            itemView.txt_event_date.text = item?.eventStartToEndDate()
            itemView.txt_event_type.text = item?.eventType
            itemView.txt_create_date.text = item?.getFormattedDate(item.createdAt, "MMM dd yyyy hh:mm aa")
            if (item?.propic != null) {
                Glide.with(itemView.user_pro.context)
                        .load(ApiPaths.S3_DEV_IMAGE_URL_SQUARE + ApiPaths.IMAGE_BASE_URL + Constants.Paths.PROFILE_PIC + item.propic)
                        .apply(Utilities.getCurvedRequestOptions())
                        .transition(DrawableTransitionOptions.withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                        .placeholder(R.drawable.avatar_male)
                        .into(itemView.user_pro)
            } else {
                Glide.with(itemView.user_pro.context)
                        .load(R.drawable.avatar_male)
                        .transition(DrawableTransitionOptions.withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                        .into(itemView.user_pro)
            }

            if (item?.eventImage != null) {
                Glide.with(itemView.event_image.context)
                        .load(ApiPaths.S3_DEV_IMAGE_URL_COVER + ApiPaths.IMAGE_BASE_URL + Constants.Paths.EVENT_IMAGE + item.eventImage)
                        .apply(Utilities.getCurvedRequestOptions())
                        .placeholder(R.drawable.default_event_image)
                        .transition(DrawableTransitionOptions.withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                        .into(itemView.event_image)
            } else {
                Glide.with(itemView.event_image.context)
                        .load(R.drawable.default_event_image)
                        .apply(Utilities.getCurvedRequestOptions())
                        .transition(DrawableTransitionOptions.withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                        .into(itemView.event_image)
            }
            itemView.create_data_view.setOnClickListener {

                val intent = Intent(itemView.context, ProfileActivity::class.java)
                val familyMember = FamilyMember()
                familyMember.id = SharedPref.getUserRegistration().id.toInt()
                familyMember.userId = items?.get(position)?.userId
                familyMember.proPic = items?.get(position)?.propic
                intent.putExtra(Constants.Bundle.DATA, familyMember)
                intent.putExtra(Constants.Bundle.FOR_EDITING, true)
                val options = ActivityOptionsCompat.makeSceneTransitionAnimation((itemView.context as Activity?)!!, itemView.user_pro, "profile")
                itemView.context.startActivity(intent, options.toBundle())


            }
        }
    }
}