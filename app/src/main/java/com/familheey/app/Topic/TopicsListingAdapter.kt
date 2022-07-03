package com.familheey.app.Topic

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.familheey.app.R
import kotlinx.android.synthetic.main.item_topic.view.*
import org.ocpsoft.prettytime.PrettyTime
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class TopicsListingAdapter(val topics: List<Topic>, val context: Context, val onTopicClickListener: OnTopicClickListener) : RecyclerView.Adapter<ViewHolder>() {

    override fun getItemCount(): Int {
        return topics.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_topic, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val topic = topics[position]
        holder.userName.text = topic.getFormattedUsers()

        holder.description.text = topic.title
        holder.m_count.text = topic.conversationCount
        holder.conversationCount.text = topic.conversationCountNew ?: "0"
        try {
            if ((topic.conversationCountNew ?: "0").toInt() > 0)
                holder.conversationCount.visibility = VISIBLE
            else holder.conversationCount.visibility = GONE
        } catch (e: NumberFormatException) {
            e.printStackTrace()
            holder.conversationCount.visibility = GONE
        }
        holder.postedTimeAgo.text = getTimeAgo(topic.createdAt!!)

        holder.itemView.setOnClickListener {
            onTopicClickListener.onClicked(topic)
        }

        holder.itemView.setOnLongClickListener {
            if (topic.isAccept!!)
                onTopicClickListener.onLongClicked(topic)
            return@setOnLongClickListener true
        }

    }
    private fun getTimeAgo(createdAt: String): String? {
        val dateFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val tzInAmerica = TimeZone.getTimeZone("IST")
        dateFormatter.timeZone = tzInAmerica
        try {
            val date = dateFormatter.parse(createdAt)
            val p = PrettyTime()
            return p.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return "Just now"
    }

    interface OnTopicClickListener {
        fun onLongClicked(topic: Topic)
        fun onClicked(topic: Topic)
    }
}

class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val userName: TextView = view.userName
    val postedTimeAgo: TextView = view.txt_chat_date
    val description: TextView = view.description
    val m_count: TextView = view.m_count
    val conversationCount: TextView = view.conversationCount
}

