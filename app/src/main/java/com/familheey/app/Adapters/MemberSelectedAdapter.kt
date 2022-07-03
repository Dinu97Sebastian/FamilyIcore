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
import kotlinx.android.synthetic.main.item_seleted_member.view.*


class MemberSelectedAdapter(internal val items: MutableList<DiscoverUsers>?, internal val listener: MemberSelectedItemClick) : androidx.recyclerview.widget.RecyclerView.Adapter<MemberSelectedAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_seleted_member, parent, false))
    }

    override fun getItemCount(): Int {
        return items?.size ?: 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
        holder.itemView.setOnClickListener {}
    }

    inner class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        fun bind(position: Int) {
            val item = items?.get(position)

            if (item?.propic != null) {
                Glide.with(itemView.userProfileImage.context)
                        .load(Constants.ApiPaths.S3_DEV_IMAGE_URL_SQUARE + Constants.ApiPaths.IMAGE_BASE_URL + Constants.Paths.PROFILE_PIC + item.propic)
                        .apply(Utilities.getCurvedRequestOptions())
                        .placeholder(R.drawable.avatar_male)
                        .transition(DrawableTransitionOptions.withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                        .into(itemView.userProfileImage)
            } else {
                Glide.with(itemView.userProfileImage.context)
                        .load(R.drawable.avatar_male)
                        .into(itemView.userProfileImage)
            }
            itemView.txt_name.text = item?.fullName?.split(' ')?.joinToString(" ") { it.capitalize() }
            itemView.btn_close.setOnClickListener {
                listener.onItemCloseClick(position)
            }
        }
    }


    interface MemberSelectedItemClick {
        fun onItemCloseClick(position: Int)
    }

}