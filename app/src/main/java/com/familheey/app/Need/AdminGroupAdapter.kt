package com.familheey.app.Need

import MyFamilySelection
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.familheey.app.R
import com.familheey.app.Utilities.Constants
import com.familheey.app.Utilities.Constants.ApiPaths
import com.familheey.app.Utilities.Utilities
import kotlinx.android.synthetic.main.item_share_event_to_family.view.*


class AdminGroupAdapter(internal val items: List<MyFamilySelection>?, internal val listener: ItemClick) : androidx.recyclerview.widget.RecyclerView.Adapter<AdminGroupAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_share_event_to_family, parent, false))
    }

    override fun getItemCount(): Int {
        return items?.size ?: 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }


    inner class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        fun bind(position: Int) {
            val item = items?.get(position)
            itemView.familyName.text = item?.groupName
            itemView.familyLocation.text = item?.baseRegion
            itemView.createdBy.text = item?.fullName
            itemView.familyType.text = "Admin"
            if (item?.logo != null) {
                Glide.with(itemView.context)
                        .load(ApiPaths.S3_DEV_IMAGE_URL_SQUARE + ApiPaths.IMAGE_BASE_URL + Constants.Paths.LOGO + item.logo)
                        .apply(Utilities.getCurvedRequestOptions())
                        .transition(DrawableTransitionOptions.withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                        .placeholder(R.drawable.family_logo)
                        .into(itemView.familyLogo)
            } else {

                Glide.with(itemView.context)
                        .load(R.drawable.family_logo)
                        .into(itemView.familyLogo)
            }
            itemView.linkFamily.setOnClickListener {
                listener.onItemClick(item?.id?.toString(), item?.groupName, item?.stripeAccountId)

            }
        }

    }

    interface ItemClick {
        fun onItemClick(id: String?, name: String?, account: String?)

    }
}