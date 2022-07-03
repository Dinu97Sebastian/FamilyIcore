package com.familheey.app.PaymentHistory

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.familheey.app.R
import com.familheey.app.Utilities.Constants
import com.familheey.app.Utilities.Utilities
import com.skydoves.androidribbon.RibbonView
import kotlinx.android.synthetic.main.item_payment_history.view.*
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import java.util.concurrent.TimeUnit


class RequestPaymentHistoryAdapter(internal val items: List<Data>?, internal val listener: AllPaymentHistoryAdapter.ItemClick) : androidx.recyclerview.widget.RecyclerView.Adapter<RequestPaymentHistoryAdapter.ViewHolder>() {
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


    inner class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        fun bind(position: Int) {
            val item = items?.get(position)
            itemView.groupName.text = item?.groupName
            itemView.groupType.text = item?.requestItemTitle
            itemView.txt_validity.text = getFormattedCreatedAt(item?.paidOn)
            itemView.txt_paid_amount.text = "$" + item?.contributeItemQuantity
            itemView.due_view.visibility = View.GONE
            if (item?.paymentNote.isNullOrEmpty()) {
                itemView.payment_note_view.visibility = View.GONE
            } else {
                itemView.payment_note_view.visibility = View.VISIBLE
            }
            Glide.with(itemView.context)
                    .load(Constants.ApiPaths.S3_DEV_IMAGE_URL_SQUARE + Constants.ApiPaths.IMAGE_BASE_URL + Constants.Paths.LOGO + item?.logo)
                    .apply(Utilities.getCurvedRequestOptions())
                    .transition(DrawableTransitionOptions.withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                    .placeholder(R.drawable.family_logo)
                    .into(itemView.familyImage)
            if (item?.paymentStatus.equals("pending")) {
                itemView.ribbonLayout01.ribbonBottom = RibbonView.Builder(itemView.context)
                        .setRibbonDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.ic_ribbon_yellow))
                        .build()
            } else {

                itemView.ribbonLayout01.ribbonBottom = RibbonView.Builder(itemView.context)
                        .setRibbonDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.ic_ribbon_green))
                        .build()
            }
            itemView.payment_note_view.setOnClickListener {
                listener.onNoteClick(position, item)
            }
        }
    }
}