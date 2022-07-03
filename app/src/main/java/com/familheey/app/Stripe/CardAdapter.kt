package com.familheey.app.Stripe

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.familheey.app.R
import kotlinx.android.synthetic.main.item_strip_card.view.*

class CardAdapter(internal val items: List<Card>?, internal val listener: CardItemClick) : androidx.recyclerview.widget.RecyclerView.Adapter<CardAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_strip_card, parent, false))
    }

    override fun getItemCount(): Int {
        if (items != null) {
            return items.size
        }
        return 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
        holder.itemView.rb_seletion.setOnClickListener { listener.onItemClick(position) }
    }

    inner class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        fun bind(position: Int) {
            val item = items?.get(position)
            itemView.cardlastdigit.text = item?.last4.toString()
            itemView.txtexp.text = item?.expMonth.toString() + "/" + item?.expYear

            if (item?.isSelected!!) {
                itemView.rb_seletion.isChecked = true
                itemView.cardlastdigit.setTextColor(Color.parseColor("#000000"))
                itemView.txtexp.setTextColor(Color.parseColor("#000000"))
            } else {
                itemView.rb_seletion.isChecked = false
                itemView.cardlastdigit.setTextColor(Color.parseColor("#999999"))
                itemView.txtexp.setTextColor(Color.parseColor("#999999"))
            }
        }

    }

    interface CardItemClick {
        fun onItemClick(position: Int)
        fun onItemDelete(position: Int)
    }
}