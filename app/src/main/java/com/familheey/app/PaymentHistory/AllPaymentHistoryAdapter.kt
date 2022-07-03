package com.familheey.app.PaymentHistory

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.familheey.app.R
import com.familheey.app.Utilities.Constants
import com.familheey.app.Utilities.Constants.ApiPaths
import com.familheey.app.Utilities.Utilities
import com.skydoves.androidribbon.RibbonView
import kotlinx.android.synthetic.main.item_payment_history.view.*
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import java.util.concurrent.TimeUnit


class AllPaymentHistoryAdapter(internal val items: List<Data>?, internal val listener: ItemClick) : androidx.recyclerview.widget.RecyclerView.Adapter<AllPaymentHistoryAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_payment_history, parent, false))
    }

    override fun getItemCount(): Int {
        return items?.size ?: 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    private fun getFormattedCreatedAt(values: Long?): String? {
        var value: Long? = values
        if (value != null && value > 100) {
            value = TimeUnit.SECONDS.toMillis(value)
            val dateTime = DateTime(value)
            val dtfOut = DateTimeFormat.forPattern("MMM dd yyyy")
            return dtfOut.print(dateTime)
        }
        return ""
    }


    private fun getFormattedCreatedAtDDMMM(values: Long?): String? {
        var value: Long? = values
        if (value != null && value > 100) {
            value = TimeUnit.SECONDS.toMillis(value)
            val dateTime = DateTime(value)
            val dtfOut = DateTimeFormat.forPattern("dd MMM")
            return dtfOut.print(dateTime)
        }
        return ""
    }

    inner class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        fun bind(position: Int) {
            val item = items?.get(position)
            itemView.groupName.text = item?.fullName
            itemView.groupType.text = item?.membershipName
            itemView.txt_till.text = "Till :"
            itemView.txt_validity.text = getFormattedCreatedAt(item?.membershipTo)
            if (item?.membershipTotalPayedAmount != null) {
                val due = item.membershipFees - item.membershipTotalPayedAmount
                if (due > 0) {
                    itemView.txt_due_title.text = "Payment Due: "
                    itemView.memberdues.setTextColor(Color.parseColor("#E70909"))
                    itemView.memberdues.text = "$" + due.toString()
                } else {
                    itemView.memberdues.setTextColor(Color.parseColor("#E6E5E5"))
                    itemView.txt_due_title.text = "Payment Due "

                    itemView.memberdues.setTextColor(Color.parseColor("#979494"))
                    itemView.memberdues.text = ": Nill"
                }
            }
            if (item?.membershipCustomerNotes.isNullOrEmpty() && item?.membershipPaymentNotes.isNullOrEmpty()) {
                itemView.payment_note_view.visibility = View.GONE
            } else {
                itemView.payment_note_view.visibility = View.VISIBLE
            }

            itemView.txt_paid_amount.text = "$" + item?.membershipPayedAmount.toString()
            itemView.txt_paid_date.text = getFormattedCreatedAtDDMMM(item?.membershipPaidOn)
            Glide.with(itemView.context)
                    .load(ApiPaths.S3_DEV_IMAGE_URL_SQUARE + ApiPaths.IMAGE_BASE_URL + Constants.Paths.PROFILE_PIC + item?.propic)
                    .apply(Utilities.getCurvedRequestOptions())
                    .transition(DrawableTransitionOptions.withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                    .placeholder(R.drawable.avatar_male)
                    .into(itemView.familyImage)
            if (item?.paymentStatus.equals("Completed") || item?.paymentStatus.equals("success")) {
                itemView.ribbonLayout01.ribbonBottom = RibbonView.Builder(itemView.context)
                        .setRibbonDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.ic_ribbon_green))
                        .build()
            } else {
                itemView.ribbonLayout01.ribbonBottom = RibbonView.Builder(itemView.context)
                        .setRibbonDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.ic_ribbon_yellow))
                        .build()
            }

            itemView.payment_note_view.setOnClickListener {
                listener.onNoteClick(position, item)
            }

        }

    }

    interface ItemClick {
        fun onNoteClick(position: Int, data: Data?)

    }
}