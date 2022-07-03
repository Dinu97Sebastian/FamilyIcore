package com.familheey.app.Discover.ui.main

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.familheey.app.Activities.MyFamiliesActivity
import com.familheey.app.Activities.ProfileActivity
import com.familheey.app.Discover.model.DiscoverUsers
import com.familheey.app.Models.Response.FamilyMember
import com.familheey.app.Models.Response.PeopleSearchModal
import com.familheey.app.R
import com.familheey.app.Topic.CreateTopic
import com.familheey.app.Utilities.Constants
import com.familheey.app.Utilities.Constants.ApiPaths
import com.familheey.app.Utilities.SharedPref
import com.familheey.app.Utilities.Utilities
import kotlinx.android.synthetic.main.item_discover_event.view.*
import kotlinx.android.synthetic.main.item_discover_users.view.*
import kotlinx.android.synthetic.main.item_discover_users.view.txt_location


class DiscoverMemberAdapter(internal val items: MutableList<DiscoverUsers>?/*, internal val listener: MemberItemClick*/) : androidx.recyclerview.widget.RecyclerView.Adapter<DiscoverMemberAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_discover_users, parent, false))
    }

    override fun getItemCount(): Int {
        return items?.size ?: 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
        holder.itemView.setOnClickListener {

            val intent = Intent(holder.itemView.context, ProfileActivity::class.java)
            val familyMember = FamilyMember()
            familyMember.id = SharedPref.getUserRegistration().id.toInt()
            familyMember.userId = items?.get(position)?.id
            familyMember.proPic = items?.get(position)?.propic
            intent.putExtra(Constants.Bundle.DATA, familyMember)
            intent.putExtra(Constants.Bundle.FOR_EDITING, true)
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation((holder.itemView.context as Activity?)!!, holder.itemView.userImage, "profile")
            holder.itemView.context.startActivity(intent, options.toBundle())
        }
    }


    inner class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        fun bind(position: Int) {
            val item = items?.get(position)
            itemView.userName.text = item?.fullName
            if(item?.location=="" || item?.location==null){
                itemView.icon_location.visibility=View.GONE
                itemView.txt_location.text =""
            }else{
                itemView.txt_location.text = item?.getFormattedLocation()
            }
            itemView.txt_family_count.text = item?.familycount.toString()
            if (item?.propic != null) {
                Glide.with(itemView.userImage.context)
                        .load(ApiPaths.S3_DEV_IMAGE_URL_SQUARE + ApiPaths.IMAGE_BASE_URL + Constants.Paths.PROFILE_PIC + item.propic)
                        .apply(Utilities.getCurvedRequestOptionsSmall())
                        .placeholder(R.drawable.avatar_male)
                        .transition(DrawableTransitionOptions.withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                        .into(itemView.userImage)
            } else {
                Glide.with(itemView.userImage.context)
                        .load(R.drawable.avatar_male)
                        .into(itemView.userImage)
            }

            if (item?.loginedUserFamilyCount != null && item.loginedUserFamilyCount > 0) {
                itemView.addToFamily.visibility = View.VISIBLE
            } else {
                itemView.addToFamily.visibility = View.INVISIBLE
            }

            itemView.addToTopic.setOnClickListener {
                val intent = Intent(itemView.context, CreateTopic::class.java)
                intent.putExtra(Constants.Bundle.DATA, item?.id.toString() + "")
                itemView.context.startActivity(intent)
            }

            itemView.addToFamily.setOnClickListener {

                val searchModal = PeopleSearchModal()
                searchModal.id = items?.get(position)?.id
                searchModal.familycount = items?.get(position)?.familycount.toString()
                searchModal.fullName = items?.get(position)?.fullName
                searchModal.gender = items?.get(position)?.gender
                searchModal.location = items?.get(position)?.location
                searchModal.propic = items?.get(position)?.propic
                itemView.context.startActivity(Intent(itemView.context, MyFamiliesActivity::class.java).putExtra(Constants.Bundle.DATA, searchModal))
            }
        }

    }

}