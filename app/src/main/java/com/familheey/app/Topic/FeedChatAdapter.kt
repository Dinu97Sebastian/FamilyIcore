package com.familheey.app.Topic

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.familheey.app.Activities.ChatActivity
import com.familheey.app.BuildConfig
import com.familheey.app.R
import com.familheey.app.Utilities.Constants
import com.familheey.app.Utilities.Constants.ApiPaths
import com.familheey.app.Utilities.Utilities
import kotlinx.android.synthetic.main.item_feed_chat.view.*


class FeedChatAdapter(internal val items: MutableList<PostMessage>?/*, internal val listener: MemberItemClick*/) : androidx.recyclerview.widget.RecyclerView.Adapter<FeedChatAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_feed_chat, parent, false))
    }

    override fun getItemCount(): Int {
        return items?.size ?: 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
        holder.itemView.setOnClickListener {
            items?.get(position)?.unreadConversationCount = "0"
            val intent = Intent(holder.itemView.context, ChatActivity::class.java)
                    .putExtra(Constants.Bundle.SUB_TYPE, "")
                    .putExtra(Constants.Bundle.TYPE, "HOME")
                    .putExtra("POS", position)
                    .putExtra(Constants.Bundle.DATA, items?.get(position)?.postId!!)
            holder.itemView.context.startActivity(intent)

            (holder.itemView.context as Activity).overridePendingTransition(R.anim.enter,
                    R.anim.exit)
            notifyDataSetChanged()
        }
    }
    //CHECKSTYLE:OFF
    inner class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        fun bind(position: Int) {
            val item = items?.get(position)
            itemView.txt_last_message.text = item?.lastComment
            if (item?.groupName.isNullOrEmpty()) {
                itemView.txt_family_name.text = "Public"
            } else {
                itemView.txt_family_name.text = item?.groupName
                itemView.txt_family_name.visibility = View.VISIBLE
            }

            if (item?.snapDescription.isNullOrEmpty()) {
                itemView.txt_des.visibility = View.GONE
            } else {
                itemView.txt_des.text = item?.snapDescription
                itemView.txt_des.visibility = View.VISIBLE
            }


            itemView.txt_user_name.text = item?.lastCommentedUser
            itemView.m_count.text = item?.totalComments
            itemView.p_count.text = item?.commentedUserCount
            if (item?.unreadConversationCount != null && item.unreadConversationCount?.toInt()!! > 0) {
                itemView.txt_chat_count.text = item.unreadConversationCount
                itemView.txt_chat_count.visibility = View.VISIBLE
            } else {
                itemView.txt_chat_count.visibility = View.GONE
            }

            if (item?.lastCommentAttachment != null && item.lastCommentAttachment.size > 0) {
                if (item.lastCommentAttachment[0].type.contains("image")) {
                    itemView.txt_last_message.text = "Image"
                } else if (item.lastCommentAttachment[0].type.contains("video")) {
                    itemView.txt_last_message.text = "Video"
                } else {
                    itemView.txt_last_message.text = "Attachments"
                }
            }

            itemView.txt_chat_date.text = item?.getFormattedDate()

            if (item?.postAttachment != null && item.postAttachment.size > 0) {

                if (item.postAttachment[0].type.contains("image")) {

                    var url = ApiPaths.S3_DEV_IMAGE_URL_SQUARE_DETAILED + BuildConfig.IMAGE_BASE_URL + "post/" + item.postAttachment[0].filename

                    if (item.publishType != null && item.publishType.equals("albums")) {
                        url = ApiPaths.S3_DEV_IMAGE_URL_SQUARE_DETAILED + BuildConfig.IMAGE_BASE_URL + "Documents/" + item.postAttachment[0].filename
                    }

                    Glide.with(itemView.post_image.context)
                            .load(url)
                            .apply(Utilities.getCurvedRequestOptions())
                            .transition(DrawableTransitionOptions.withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                            .placeholder(R.drawable.family_logo)
                            .into(itemView.post_image)
                } else if (item.postAttachment[0].type.contains("video")) {

                    Glide.with(itemView.post_image.context)
                            .load(BuildConfig.IMAGE_BASE_URL + item.postAttachment[0].videoThumb)
                            .apply(Utilities.getCurvedRequestOptions())
                            .transition(DrawableTransitionOptions.withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                            .placeholder(R.drawable.family_logo)
                            .into(itemView.post_image)

                } else {
                    Glide.with(itemView.post_image.context)
                            .load(R.drawable.family_logo)
                            .apply(Utilities.getCurvedRequestOptions())
                            .transition(DrawableTransitionOptions.withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                            .into(itemView.post_image)
                }
            } else {
                Glide.with(itemView.post_image.context)
                        .load(R.drawable.family_logo)
                        .apply(Utilities.getCurvedRequestOptions())
                        .transition(DrawableTransitionOptions.withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                        .into(itemView.post_image)
            }


        }
    }
    //CHECKSTYLE:ON
}