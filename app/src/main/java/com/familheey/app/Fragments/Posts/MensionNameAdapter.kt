package com.familheey.app.Fragments.Posts

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.familheey.app.Activities.ProfileActivity
import com.familheey.app.Models.Response.FamilyMember
import com.familheey.app.Need.User
import com.familheey.app.R
import com.familheey.app.Utilities.Constants
import com.familheey.app.Utilities.SharedPref
import kotlinx.android.synthetic.main.item_mension_name.view.*

class MensionNameAdapter(internal val items: ArrayList<User>, internal val context: Context) : androidx.recyclerview.widget.RecyclerView.Adapter<MensionNameAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_mension_name, parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
        holder.itemView.txt_name.setOnClickListener {
            val intent = Intent(context, ProfileActivity::class.java)
            val familyMember = FamilyMember()
            familyMember.id = SharedPref.getUserRegistration().id.toInt()
            familyMember.userId = items.get(position).user_id
            intent.putExtra(Constants.Bundle.DATA, familyMember)
            intent.putExtra(Constants.Bundle.FOR_EDITING, true)
            context.startActivity(intent)
        }
    }

    inner class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        fun bind(position: Int) {
            val item = items[position]
            itemView.txt_name.text = "@" + item.user_name.split(" ")[0]
        }

    }
}