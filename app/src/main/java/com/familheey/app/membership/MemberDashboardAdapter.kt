package com.familheey.app.membership

import MembershipData
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.familheey.app.R
import kotlinx.android.synthetic.main.item_dashboard_member.view.*

class MemberDashboardAdapter(internal val items: List<MembershipData>, internal val listener: MemberDashboardItemClick) : androidx.recyclerview.widget.RecyclerView.Adapter<MemberDashboardAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_dashboard_member, parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
        holder.itemView.attending.setOnClickListener { listener.onItemClick(position) }
    }

    inner class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        fun bind(position: Int) {
            val item = items[position]
            itemView.attending_count.text = item.memberCount.toString()
            itemView.labelAttending.text = item.membershipName
        }

    }
    interface MemberDashboardItemClick {
        fun onItemClick(position:Int)
    }
}