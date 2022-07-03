package com.familheey.app.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.familheey.app.Discover.model.DiscoverUsers
import com.familheey.app.R
import com.familheey.app.Utilities.Constants
import com.familheey.app.Utilities.Utilities
import kotlinx.android.synthetic.main.item_member.view.*


class MemberLisitingAdapter(internal val items: MutableList<DiscoverUsers>?, internal val listener: MemberItemClick) : androidx.recyclerview.widget.RecyclerView.Adapter<MemberLisitingAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_member, parent, false))
    }

    override fun getItemCount(): Int {
        return items?.size ?: 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
        holder.itemView.setOnClickListener {
            listener.onItemClick(position)
        }
    }

    inner class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        fun bind(position: Int) {
            val item = items?.get(position)

            if (item?.propic != null) {
                Glide.with(itemView.userProfileImage.context)
                        .load(Constants.ApiPaths.S3_DEV_IMAGE_URL_SQUARE + Constants.ApiPaths.IMAGE_BASE_URL + Constants.Paths.PROFILE_PIC + item.propic)
                        .apply(Utilities.getCurvedRequestOptionsSmall())
                        .placeholder(R.drawable.avatar_male)
                        .transition(DrawableTransitionOptions.withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                        .into(itemView.userProfileImage)
            } else {
                Glide.with(itemView.userProfileImage.context)
                        .load(R.drawable.avatar_male)
                        .into(itemView.userProfileImage)
            }


            if (item?.exists != null && item.exists) {
                itemView.txt_staus.visibility = View.VISIBLE
                itemView.txt_staus.text = "Member"
                itemView.itemselected.visibility = View.GONE
            } else if (item?.invitationStatus != null && item.invitationStatus) {
                itemView.txt_staus.visibility = View.VISIBLE
                itemView.txt_staus.text = "Invitation status pending"
                itemView.itemselected.visibility = View.GONE
            } else {
                itemView.txt_staus.visibility = View.GONE
                itemView.itemselected.visibility = View.VISIBLE
            }

            itemView.name.text = item?.fullName?.split(' ')?.joinToString(" ") { it.capitalize() }

            itemView.postedIn.text = item?.location

            itemView.itemselected.setOnClickListener {

                listener.onItemCheckBoxClick(position)
            }

            itemView.itemselected.isChecked = item?.selected!!

        }
    }


    interface MemberItemClick {
        fun onItemCheckBoxClick(position: Int)
        fun onItemClick(position: Int)
    }

}