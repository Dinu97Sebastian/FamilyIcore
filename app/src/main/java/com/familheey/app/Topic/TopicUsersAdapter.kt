package com.familheey.app.Topic

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.familheey.app.Activities.ProfileActivity
import com.familheey.app.Models.Response.FamilyMember
import com.familheey.app.R
import com.familheey.app.Utilities.Constants
import com.familheey.app.Utilities.Constants.ApiPaths.IMAGE_BASE_URL
import com.familheey.app.Utilities.Constants.ApiPaths.S3_DEV_IMAGE_URL_SQUARE
import com.familheey.app.Utilities.Constants.Paths.PROFILE_PIC
import com.familheey.app.Utilities.SharedPref
import kotlinx.android.synthetic.main.item_topic_users.view.*

class TopicUsersAdapter(val users: List<User>, val context: Context) : RecyclerView.Adapter<UserViewHolder>() {
    val listener = context as OnUserSelectedStatusListener

    override fun getItemCount(): Int {
        return users.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        return UserViewHolder(LayoutInflater.from(context).inflate(R.layout.item_topic_users, parent, false))
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        holder.name.text = user.fullName
        if (user.location != null) {
            holder.location.visibility = VISIBLE
            holder.locationIcon.visibility = VISIBLE
            holder.location.text = user.location
        } else {
            holder.location.visibility = INVISIBLE
            holder.locationIcon.visibility = INVISIBLE
        }
        if (user.propic != null) {


            Glide.with(context)
                    .load(S3_DEV_IMAGE_URL_SQUARE + IMAGE_BASE_URL + PROFILE_PIC + user.propic)
                    //.transform(RoundedCorners(90))
                    .transforms(CenterCrop(), RoundedCorners(90))
                    //.placeholder(R.drawable.avatar_male)
                    //.error(R.drawable.avatar_male)
                    .transition(DrawableTransitionOptions.withCrossFade()).into(holder.image)
        } else {

            Glide.with(context)
                    .load(R.drawable.avatar_male)
                    .transforms(CenterCrop(), RoundedCorners(90))
                    .into(holder.image)
        }
/*
            Glide.with(holder.itemView.context)
                    .load(S3_DEV_IMAGE_URL_SQUARE + IMAGE_BASE_URL + PROFILE_PIC + user.propic)
                    .apply(Utilities.getCurvedRequestOptions())
                    .transition(withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                    .placeholder(R.drawable.avatar_male)
                    .into(holder.image)*///}
        /*  else
              holder.image.setImageResource(R.drawable.avatar_male)*/
        holder.itemView.setOnClickListener {
            val intent = Intent(context, ProfileActivity::class.java)
            val familyMember = FamilyMember()
            familyMember.id = SharedPref.getUserRegistration().id.toInt()
            familyMember.userId = user.userId
            intent.putExtra(Constants.Bundle.DATA, familyMember)
            intent.putExtra(Constants.Bundle.FOR_EDITING, true)
            intent.putExtra(Constants.Bundle.IDENTIFIER, true)
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation((context as Activity), holder.image, "profile")
            context.startActivity(intent, options.toBundle())
        }
        when {
            user.alreadySelected -> {
                holder.selectUser.visibility = INVISIBLE
                //holder.selectUser.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_hand, 0, 0, 0);
            }
            user.currentSelection -> {
                // holder.selectUser.text = "Selected"
                holder.selectUser.visibility = VISIBLE
                holder.selectUser.isChecked = true
                //holder.selectUser.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_hand, 0, 0, 0);
            }
            else -> {
                // holder.selectUser.text = "Select"
                holder.selectUser.isChecked = false
                holder.selectUser.visibility = VISIBLE
                //holder.selectUser.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            }
        }

        holder.selectUser.setOnClickListener {
            when {
                user.alreadySelected -> {
                    Toast.makeText(holder.itemView.context, "Cannot remove an existing user from Topic", Toast.LENGTH_SHORT)
                    return@setOnClickListener
                }
                else -> {
                    if (user.currentSelection)
                        listener.onUserIdRemoved(user.userId!!)
                    else listener.onUserIdSelected(user.userId!!)
                    user.currentSelection = !user.currentSelection
                    notifyDataSetChanged()
                    return@setOnClickListener
                }
            }
        }
    }
}

class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val image: ImageView = view.image
    val locationIcon: ImageView = view.locationIcon
    val name: TextView = view.name
    val location: TextView = view.location
    val selectUser: CheckBox = view.selectUser
}

interface OnUserSelectedStatusListener {
    fun onUserIdSelected(id: Int)
    fun onUserIdRemoved(id: Int)
}