package com.familheey.app.Adapters

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.familheey.app.Activities.ImageChangerActivity
import com.familheey.app.Activities.ProfileActivity
import com.familheey.app.BrowserActivity
import com.familheey.app.Models.Response.FamilyMember
import com.familheey.app.Models.Response.GetCommentsResponse
import com.familheey.app.Post.VideoActivity
import com.familheey.app.R
import com.familheey.app.Utilities.Constants
import com.familheey.app.Utilities.SharedPref
import com.luseen.autolinklibrary.AutoLinkMode
import kotlinx.android.synthetic.main.message_audio_list.view.*
import kotlinx.android.synthetic.main.message_list.view.*
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.ISODateTimeFormat
import org.ocpsoft.prettytime.PrettyTime
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class ChatAdapter1(internal val context: Context, internal val items: List<GetCommentsResponse.Data>, internal val listener: OnChatLongClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val NORMAL = 1
    private val AUDIO = 2
    private val bucketName = "file_name/"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == NORMAL) {
            return NormalViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.message_list, parent, false))
        } else {
            return AudioViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.message_audio_list, parent, false))
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == NORMAL) {
            val myViewHolder = holder as NormalViewHolder
            myViewHolder.bind(position)
        } else {
            val myViewHolder = holder as AudioViewHolder
            myViewHolder.bind(position)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val model: GetCommentsResponse.Data = items.get(position)
        return if (model.attachment != null && model.attachment.size > 0 && model.attachment.get(0).type.equals("audio"))
            AUDIO
        else NORMAL
    }

    inner class NormalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(position: Int) {
            val item = items[position]
            if (item.commentedBy.equals(SharedPref.getUserRegistration().id)) {
                itemView.constraintLayoutLeft.visibility = View.GONE
                itemView.constraintLayoutRight.visibility = View.VISIBLE
                showSenderViews(itemView, position)
            } else {
                itemView.constraintLayoutLeft.visibility = View.VISIBLE
                itemView.constraintLayoutRight.visibility = View.GONE
                showReceiveView(itemView, position)
            }
        }
    }

    //CHECKSTYLE:OFF
    inner class AudioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(position: Int) {
            val item = items[position]
            if (item.commentedBy.equals(SharedPref.getUserRegistration().id)) {
                itemView.constraintLayoutRightAudio.visibility = View.VISIBLE
                itemView.constraintLayoutLeftAudio.visibility = View.GONE
                val url = Constants.ApiPaths.IMAGE_BASE_URL + bucketName + item.attachment[0].filename
                val sourc = "<source src=\"$url\"  type=\"audio/mpeg\">"
                val html = "<html><body><audio  controls controlsList=\"nodownload\">$sourc</audio></body></html>"
                itemView.txtTimeRightaudio.text = getFormattedDate(item.createdAt)
                itemView.text_message_name_received_audio.text = ""
                itemView.webviewright.setBackgroundColor(Color.TRANSPARENT)
                itemView.webviewright.loadData(html, "text/html", "UTF-8")
                itemView.webviewright.setOnLongClickListener {
                    onLongClicked(position)
                    true
                }
            } else {
                itemView.constraintLayoutRightAudio.visibility = View.GONE
                itemView.constraintLayoutLeftAudio.visibility = View.VISIBLE
                itemView.txtTimeLeft_audio.text = getFormattedDate(item.createdAt)
                itemView.text_message_name_received_audio.text = item.fullName

                if (item.propic != "") {
                    Glide.with(context)
                            .load(item.propic)
                            .into(itemView.image_received_audio)
                    itemView.image_received_audio.setOnClickListener {
                        val intent = Intent(context, ProfileActivity::class.java)
                        val familyMember = FamilyMember()
                        familyMember.id = SharedPref.getUserRegistration().id.toInt()
                        familyMember.userId = item.userId
                        intent.putExtra(Constants.Bundle.DATA, familyMember)
                        intent.putExtra(Constants.Bundle.FOR_EDITING, true)
                        context.startActivity(intent)
                    }
                } else {
                    itemView.image_received_audio.setImageResource(R.drawable.avatar_male)
                }
                val url = Constants.ApiPaths.IMAGE_BASE_URL + bucketName + item.attachment.get(0).filename
                val sourc = "<source src=\"$url\"  type=\"audio/mpeg\">"

                val html = "<html><body><audio controls controlsList=\"nodownload\">$sourc</audio></body></html>"

                itemView.webviewleft.setOnLongClickListener {
                    onLongClicked(position)
                    true
                }
                itemView.webviewleft.setBackgroundColor(Color.TRANSPARENT)
                itemView.webviewleft.loadData(html, "text/html", "UTF-8")
            }
        }
    }
    //CHECKSTYLE:ON

    //CHECKSTYLE:OFF
    private fun showSenderViews(holder: View, position: Int) {
        val chatModel = items[position]

        if (chatModel.quoted_item != null && !chatModel.quoted_item.isEmpty()) {
            holder.qoute_msg_view.visibility = View.VISIBLE
            holder.view_separation.visibility = View.VISIBLE
            holder.text_qoute_right.text = chatModel.quoted_item
            if (chatModel.quoted_user != null) {
                holder.text_qoute_name_date.text = chatModel.quoted_user + ", " + dateFormat(chatModel.quoted_date)
            }
        } else {
            holder.qoute_msg_view.visibility = View.GONE
            holder.view_separation.visibility = View.GONE
        }
        holder.txtTimeRight.text = getFormattedDate(chatModel.createdAt)
        holder.text_message_body_send.setOnLongClickListener { v: View? ->
            onLongClicked(position)
            true
        }
        var description = chatModel.comment.trim()
        description = description.replace("HTTP:".toRegex(), "http:").replace("Http:".toRegex(), "http:")
                .replace("Https:".toRegex(), "https:")
                .replace("HTTPS:".toRegex(), "https:")
        description = description.replace("WWW.".toRegex(), "www.").replace("Www.".toRegex(), "www.")
        holder.text_message_body_send.setAutoLinkText(description)
        holder.text_message_body_send.setAutoLinkOnClickListener { autoLinkMode: AutoLinkMode, matchedText: String ->
            if (autoLinkMode == AutoLinkMode.MODE_URL) {
                try {
                    var url = matchedText.trim { it <= ' ' }
                    if (!url.contains("http")) {
                        url = url.replace("www.".toRegex(), "http://www.")
                    } else {
                        context.startActivity(Intent(context, BrowserActivity::class.java).putExtra("URL", url))
                    }
                } catch (e: Exception) {
                    /*
                    Need to handle
                     */
                }
            }
        }
        if (chatModel.attachment != null && chatModel.attachment.size == 1) {
            val fname = chatModel.attachment.get(0).filename
            holder.text_message_body_send.visibility = View.GONE
            holder.cardAttachRight.visibility = View.VISIBLE
            if (fname.toLowerCase().contains("mp4")) {
                holder.imgAttachmentSend.setImageResource(0)
                holder.imgAttachmentSend.setImageDrawable(null)
                holder.imgPlayIcon.setImageResource(R.drawable.exo_icon_play)
                holder.imgPlayIcon.visibility = View.VISIBLE
                holder.setOnClickListener {
                    val intent = Intent(context, VideoActivity::class.java)
                    intent.putExtra("URL", Constants.ApiPaths.IMAGE_BASE_URL + bucketName + fname)
                    intent.putExtra("NAME", fname)
                    context.startActivity(intent)
                }
                holder.imgAttachmentSend.setOnClickListener {
                    val intent = Intent(context, VideoActivity::class.java)
                    intent.putExtra("URL", Constants.ApiPaths.IMAGE_BASE_URL + bucketName + fname)
                    intent.putExtra("NAME", fname)
                    context.startActivity(intent)
                }
                holder.setOnLongClickListener {
                    onLongClicked(position)
                    true
                }
                Glide.with(context)
                        .load(Constants.ApiPaths.IMAGE_BASE_URL + bucketName + fname)
                        .into(holder.imgAttachmentSend)
            } else if (fname.toLowerCase().contains("pdf") || fname.toLowerCase().contains("doc") || fname.toLowerCase().contains("Xls") || fname.toLowerCase().contains(".xlsx") || fname.toLowerCase().contains(".docx")) {
                holder.imgAttachmentSend.setImageResource(0)
                holder.imgAttachmentSend.setImageDrawable(null)
                holder.imgPlayIcon.visibility = View.VISIBLE
                holder.imgPlayIcon.setImageResource(R.drawable.document_default_item)
                holder.imgPlayIcon.setOnClickListener {
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(Constants.ApiPaths.IMAGE_BASE_URL + bucketName + fname))
                    context.startActivity(browserIntent)
                }
                holder.imgPlayIcon.setOnLongClickListener {
                    onLongClicked(position)
                    true
                }
            } else {
                holder.imgPlayIcon.visibility = View.GONE
                holder.imgAttachmentSend.setOnClickListener {
                    val userProfileCoverIntent = Intent(context, ImageChangerActivity::class.java)
                    userProfileCoverIntent.putExtra(Constants.Bundle.DATA, Constants.ApiPaths.IMAGE_BASE_URL + bucketName + fname)
                    userProfileCoverIntent.putExtra(Constants.Bundle.TYPE, Constants.ImageUpdate.GENERAL)
                    userProfileCoverIntent.putExtra(Constants.Bundle.IDENTIFIER, false)
                    userProfileCoverIntent.putExtra("DOWNLOAD", true)
                    userProfileCoverIntent.putExtra("FNAME", fname)
                    context.startActivity(userProfileCoverIntent)
                }
                holder.imgAttachmentSend.setOnLongClickListener {
                    onLongClicked(position)
                    true
                }
                Glide.with(context)
                        .load(Constants.ApiPaths.S3_DEV_IMAGE_URL_ALBUM + Constants.ApiPaths.IMAGE_BASE_URL + bucketName + fname)
                        .into(holder.imgAttachmentSend)
            }
        } else {
            holder.cardAttachRight.visibility = View.GONE
            holder.text_message_body_send.visibility = View.VISIBLE
        }
    }

    //CHECKSTYLE:ON
    private fun showReceiveView(holder: View, position: Int) {

        val chatModel = items[position]
        if (chatModel.quoted_item != null && !chatModel.quoted_item.isEmpty()) {
            holder.qoute_msg_received_view.visibility = View.VISIBLE
            holder.text_qoute_received.text = chatModel.quoted_item
            if (chatModel.quoted_user != null) {
                holder.text_qoute_name_date_received.text = chatModel.quoted_user + ", " + dateFormat(chatModel.quoted_date)
            }
        } else {
            holder.qoute_msg_received_view.visibility = View.GONE
        }
        holder.txtTimeLeft.text = getFormattedDate(chatModel.createdAt)
        var description = chatModel.comment.trim()
        description = description.replace("HTTP:".toRegex(), "http:").replace("Http:".toRegex(), "http:")
                .replace("Https:".toRegex(), "https:")
                .replace("HTTPS:".toRegex(), "https:")
        description = description.replace("WWW.".toRegex(), "www.").replace("Www.".toRegex(), "www.")

        val fname = chatModel.attachment.get(0).filename

        holder.constraintLayoutLeft.setOnLongClickListener {
            if (fname.toLowerCase().contains("mp4") || fname.toLowerCase().contains("mov") || fname.toLowerCase().contains("wmv") || fname.toLowerCase().contains("webm") || fname.toLowerCase().contains("mkv") || fname.toLowerCase().contains("flv") || fname.toLowerCase().contains("avi")) {
                /*
                    Don't need this
                   */
            } else {
                onLongClicked(position)
            }
            true
        }
        holder.text_message_body_received.setOnLongClickListener {
            if (fname.toLowerCase().contains("mp4") || fname.toLowerCase().contains("mov") || fname.toLowerCase().contains("wmv") || fname.toLowerCase().contains("webm") || fname.toLowerCase().contains("mkv") || fname.toLowerCase().contains("flv") || fname.toLowerCase().contains("avi")) {
                /*
                     Don't need this
                    */
            } else {
                onLongClicked(position)
            }
            true
        }
        holder.text_message_body_received.setAutoLinkText(description)
        holder.text_message_name_received.text = chatModel.fullName
        holder.text_message_body_received.setAutoLinkOnClickListener { autoLinkMode: AutoLinkMode, matchedText: String ->
            if (autoLinkMode == AutoLinkMode.MODE_URL) {
                try {
                    var url = matchedText.trim { it <= ' ' }
                    if (!url.contains("http")) {
                        url = url.replace("www.".toRegex(), "http://www.")
                    }
                    if (url.contains("familheey")) {
                        val uri = Uri.parse(url)
                        val intent = Intent(Intent.ACTION_VIEW, uri)
                        context.startActivity(intent)
                    } else {
                        context.startActivity(Intent(context, BrowserActivity::class.java).putExtra("URL", url))
                    }
                } catch (e: java.lang.Exception) {
                    /*
                Don't need this
               */
                }
            }
        }
        holder.imgAttachmentReceived.setOnLongClickListener {
            if (fname.toLowerCase().contains("mp4") || fname.toLowerCase().contains("mov") || fname.toLowerCase().contains("wmv") || fname.toLowerCase().contains("webm") || fname.toLowerCase().contains("mkv") || fname.toLowerCase().contains("flv") || fname.toLowerCase().contains("avi")) {
                /*
                      Don't need this
                     */
            } else {
                onLongClicked(position)
            }
            true
        }
        if (chatModel.propic != "") {
            Glide.with(context)
                    .load(chatModel.propic)
                    .into(holder.image_received)
        } else {
            holder.image_received.setImageResource(R.drawable.avatar_male)
        }
        holder.image_received.setOnClickListener {
            val intent = Intent(context, ProfileActivity::class.java)
            val familyMember = FamilyMember()
            familyMember.id = SharedPref.getUserRegistration().id.toInt()
            familyMember.userId = chatModel.userId
            intent.putExtra(Constants.Bundle.DATA, familyMember)
            intent.putExtra(Constants.Bundle.FOR_EDITING, true)
            context.startActivity(intent)
        }
        if (fname != "") {
            holder.cardAttachLeft.visibility = View.VISIBLE
            holder.text_message_body_received.visibility = View.GONE
            if (fname != "") {
                if (fname.toLowerCase().contains("mp4") || fname.toLowerCase().contains("mov") || fname.toLowerCase().contains("wmv") || fname.toLowerCase().contains("webm") || fname.toLowerCase().contains("mkv") || fname.toLowerCase().contains("flv") || fname.toLowerCase().contains("avi")) {
                    holder.imgAttachmentReceived.setImageResource(0)
                    holder.imgAttachmentReceived.setImageDrawable(null)
                    holder.imgPlayIconLeft.setImageResource(R.drawable.exo_icon_play)
                    holder.imgPlayIconLeft.visibility = View.VISIBLE
                    holder.imgAttachmentReceived.setOnClickListener {
                        val intent = Intent(context, VideoActivity::class.java)
                        intent.putExtra("URL", Constants.ApiPaths.IMAGE_BASE_URL + bucketName + fname)
                        intent.putExtra("NAME", fname)
                        // Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.ApiPaths.IMAGE_BASE_URL + "file_name/" + chatModel.getFilename()));
                        context.startActivity(intent)
                    }
                    Glide.with(context)
                            .load(Constants.ApiPaths.IMAGE_BASE_URL + bucketName + fname)
                            .into(holder.imgAttachmentReceived)
                } else if (fname.toLowerCase().contains("pdf")) {
                    holder.imgAttachmentReceived.setImageResource(0)
                    holder.imgAttachmentReceived.setImageDrawable(null)
                    holder.imgPlayIconLeft.setImageResource(R.drawable.document_default_item)
                    holder.imgPlayIconLeft.visibility = View.VISIBLE
                    holder.imgPlayIconLeft.setOnClickListener {
                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(Constants.ApiPaths.IMAGE_BASE_URL + bucketName + fname))
                        context.startActivity(browserIntent)
                    }
                } else {
                    holder.imgPlayIconLeft.visibility = View.GONE
                    Glide.with(context)
                            .load(Constants.ApiPaths.IMAGE_BASE_URL + bucketName + fname)
                            .into(holder.imgAttachmentReceived)
                    holder.imgAttachmentReceived.setOnClickListener {
                        val userProfileCoverIntent = Intent(context, ImageChangerActivity::class.java)
                        userProfileCoverIntent.putExtra(Constants.Bundle.DATA, Constants.ApiPaths.IMAGE_BASE_URL + bucketName + fname)
                        userProfileCoverIntent.putExtra(Constants.Bundle.TYPE, Constants.ImageUpdate.GENERAL)
                        userProfileCoverIntent.putExtra(Constants.Bundle.IDENTIFIER, false)
                        userProfileCoverIntent.putExtra("DOWNLOAD", true)
                        userProfileCoverIntent.putExtra("FNAME", fname)
                        context.startActivity(userProfileCoverIntent)
                    }
                }
            }
        } else {
            holder.cardAttachLeft.visibility = View.GONE
            holder.text_message_body_received.visibility = View.VISIBLE
        }
    }


    interface OnChatLongClickListener {
        fun onChatLongClicked(position: Int)
    }

    private fun onLongClicked(position: Int) {
        listener.onChatLongClicked(position)
    }

    //CHECKSTYLE:OFF
    private fun dateFormat(time: String): String? {
        val dateTime = ISODateTimeFormat.dateTimeParser().parseDateTime(time)
        val dtfOut = DateTimeFormat.forPattern("MMM dd yyyy")
        return dtfOut.print(dateTime)
    }
    //CHECKSTYLE:ON

    //CHECKSTYLE:OFF
    private fun getFormattedDate(createdAt: String): String? {
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
    //CHECKSTYLE:ON
}