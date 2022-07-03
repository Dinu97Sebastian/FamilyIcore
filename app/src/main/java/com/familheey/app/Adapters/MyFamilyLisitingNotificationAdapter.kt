package com.familheey.app.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.familheey.app.Discover.model.DiscoverGroups
import com.familheey.app.R
import com.familheey.app.Utilities.Constants
import com.familheey.app.Utilities.Utilities
import kotlinx.android.synthetic.main.item_family.view.*


class MyFamilyLisitingNotificationAdapter(internal val items: MutableList<DiscoverGroups>?, internal val listener: MyfamilyItemClick) : androidx.recyclerview.widget.RecyclerView.Adapter<MyFamilyLisitingNotificationAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_family, parent, false))
    }

    override fun getItemCount(): Int {
        return items?.size ?: 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
        holder.itemView.setOnClickListener {
            //  listener.onItemClick(position)
        }
    }

    inner class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        fun bind(position: Int) {
            val item = items?.get(position)
            if (item?.logo != null) {
                Glide.with(itemView.familyLogo.context)
                        .load(Constants.ApiPaths.S3_DEV_IMAGE_URL_SQUARE + Constants.ApiPaths.IMAGE_BASE_URL + Constants.Paths.LOGO + item.logo)
                        .apply(Utilities.getCurvedRequestOptionsSmall())
                        .placeholder(R.drawable.family_logo)
                        .transition(DrawableTransitionOptions.withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                        .into(itemView.familyLogo)
            } else {
                Glide.with(itemView.familyLogo.context)
                        .load(R.drawable.family_logo)
                        .into(itemView.familyLogo)
            }

            itemView.name.text = item?.groupName?.split(' ')?.joinToString(" ") { it.capitalize() }
            itemView.txt_by.text = "By ${item?.createdBy}"
            itemView.postedIn.text = item?.getFormattedLocation()
            itemView.itemselected.setOnClickListener {
                listener.onItemCheckBoxClick(position)
            }
            itemView.itemselected.isChecked = item?.selected!!
        }
    }


    interface MyfamilyItemClick {
        fun onItemCheckBoxClick(position: Int)
    }

}