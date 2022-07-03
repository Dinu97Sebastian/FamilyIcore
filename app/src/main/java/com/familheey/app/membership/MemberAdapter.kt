package com.familheey.app.membership

import Data
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.familheey.app.R
import com.familheey.app.Topic.CreateTopic
import com.familheey.app.Utilities.Constants
import com.familheey.app.Utilities.Constants.ApiPaths
import com.familheey.app.Utilities.Utilities
import com.skydoves.androidribbon.RibbonView
import kotlinx.android.synthetic.main.item_membership_member.view.*
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import java.util.concurrent.TimeUnit


class MemberAdapter(internal val items: List<Data>, internal val listener: MemberItemClick) : androidx.recyclerview.widget.RecyclerView.Adapter<MemberAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_membership_member, parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
        holder.itemView.setOnClickListener { listener.onItemClick(position) }
    }

    private fun getFormattedCreatedAt(values: Long): String? {
        var value: Long? = values
        if (value != null && value > 100) {
            value = TimeUnit.SECONDS.toMillis(value)
            val dateTime = DateTime(value)
            val dtfOut = DateTimeFormat.forPattern("MMM dd yyyy")
            return dtfOut.print(dateTime)
        }
        return ""
    }

    //CHECKSTYLE:OFF
    inner class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        fun bind(position: Int) {
            val item = items[position]
            itemView.memberName.text = item.fullName
            itemView.addRole.text = item.membershipType


            itemView.btn_edit.setOnClickListener {
                listener.onEditClick(position)
            }
            if (item.isExpaired) {
                itemView.membervaliidity.text = "Expired"
                itemView.btn_reminder.visibility = View.VISIBLE
                itemView.membervaliidity.setTextColor(Color.parseColor("#E70909"))
            } else if (item.membershipTo != null && !item.membershipTo.equals("")) {
                itemView.btn_reminder.visibility = View.GONE

                if (item.membershipPeriodTypeId == 4) {
                    itemView.membervaliidity.text = "Valid till : " + item.membershipPeriodType
                } else
                    itemView.membervaliidity.text = "Valid till : " + getFormattedCreatedAt(item.membershipTo.toLong())
            }
            if ("Partial" == item.membershipPaymentStatus || "Pending" == item.membershipPaymentStatus || item.isExpaired) {
                itemView.btn_reminder.visibility = View.VISIBLE
                if (item.membershipFees != null && item.membershipTotalPayedAmount != null) {
                    var amt = item.membershipFees.toInt() - item.membershipTotalPayedAmount.toInt()
                    if (amt > 0)
                        itemView.memberdues.text = "Due Amount : $amt"
                    else itemView.memberdues.text = itemView.context.resources.getString(R.string.due_amount_zero)
                } else if (item.membershipFees != null && item.membershipTotalPayedAmount == null) {
                    itemView.memberdues.text = "Due Amount : ${item.membershipFees.toInt()}"
                } else {
                    itemView.memberdues.text = " "
                }
                itemView.ribbonLayout01.ribbonHeader =
                        RibbonView.Builder(itemView.context)
                                .setText("Android-Ribbon1")
                                .setRibbonBackgroundColor(Color.parseColor("#F8ED11"))
                                .setTextColor(Color.parseColor("#F8ED11"))
                                .setTextSize(10f)
                                .setRibbonRotation(45)
                                .build()
            } else if (item.membershipPaymentStatus == null) {

                itemView.memberdues.text = itemView.context.resources.getString(R.string.due_amount_zero)
                itemView.btn_reminder.visibility = View.GONE
                itemView.ribbonLayout01.ribbonHeader = RibbonView.Builder(itemView.context)
                        .setText("Android-Ribbon2")
                        .setRibbonBackgroundColor(Color.parseColor("#F8ED11"))
                        .setTextColor(Color.parseColor("#F8ED11"))
                        .setTextSize(10f)
                        .setRibbonRotation(45)
                        .build()
            } else {
                itemView.memberdues.text = itemView.context.resources.getString(R.string.due_amount_zero)
                itemView.btn_reminder.visibility = View.GONE
                itemView.ribbonLayout01.ribbonHeader =
                        RibbonView.Builder(itemView.context)
                                .setText("Android-Ribbon2")
                                .setRibbonBackgroundColor(Color.parseColor("#62FA2E"))
                                .setTextColor(Color.parseColor("#62FA2E"))
                                .setTextSize(10f)
                                .setRibbonRotation(45)
                                .build()
            }
            itemView.btn_reminder.setOnClickListener {
                listener.onSendReminderClick(position)
            }
            Glide.with(itemView.context)
                    .load(ApiPaths.S3_DEV_IMAGE_URL_SQUARE + ApiPaths.IMAGE_BASE_URL + Constants.Paths.PROFILE_PIC + item.propic)
                    .apply(Utilities.getCurvedRequestOptions())
                    .transition(DrawableTransitionOptions.withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                    .placeholder(R.drawable.avatar_male)
                    .into(itemView.memberImage)
            itemView.memberOptions.setOnClickListener {
                val intent = Intent(itemView.context, CreateTopic::class.java)
                intent.putExtra(Constants.Bundle.DATA, item.userId.toString())
                itemView.context.startActivity(intent)
            }
        }

    }

    //CHECKSTYLE:ON
    interface MemberItemClick {
        fun onItemClick(position: Int)
        fun onSendReminderClick(position: Int)
        fun onEditClick(position: Int)

    }
}