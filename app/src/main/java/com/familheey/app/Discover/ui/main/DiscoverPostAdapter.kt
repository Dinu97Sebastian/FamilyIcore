package com.familheey.app.Discover.ui.main

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.text.util.Linkify
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.familheey.app.Activities.ProfileActivity
import com.familheey.app.Activities.ShareEventActivity
import com.familheey.app.BrowserActivity
import com.familheey.app.BuildConfig
import com.familheey.app.FamilheeyApplication
import com.familheey.app.Fragments.Posts.PostData
import com.familheey.app.Models.Response.FamilyMember
import com.familheey.app.Models.Response.UrlParse
import com.familheey.app.Networking.Retrofit.ApiServices
import com.familheey.app.Networking.Retrofit.RetrofitBase
import com.familheey.app.Post.PostCommentActivity
import com.familheey.app.Post.PostDetailForPushActivity
import com.familheey.app.R
import com.familheey.app.Utilities.Constants
import com.familheey.app.Utilities.SharedPref
import com.familheey.app.Utilities.Utilities
import com.google.gson.JsonObject
import com.luseen.autolinklibrary.AutoLinkMode
import com.volokh.danylo.hashtaghelper.HashTagHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.item_discover_post.view.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.ISODateTimeFormat
import retrofit2.Response


class DiscoverPostAdapter(internal val items: MutableList<PostData>?/*, internal val listener: MemberItemClick*/) : androidx.recyclerview.widget.RecyclerView.Adapter<DiscoverPostAdapter.ViewHolder>() {

    private lateinit var mTextHashTagHelper: HashTagHelper
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_discover_post, parent, false))
    }

    override fun getItemCount(): Int {
        return items?.size ?: 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
        holder.itemView.setOnClickListener {
            holder.itemView.context.startActivity(Intent(holder.itemView.context, PostDetailForPushActivity::class.java).putExtra(Constants.Bundle.TYPE, "NOTIFICATION").putExtra("ids", items?.get(position)?.post_id.toString()))
        }
    }

    //CHECKSTYLE:OFF
    inner class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        fun bind(position: Int) {
            val item = items?.get(position)
            if (!item?.snap_description.isNullOrEmpty()) {
                itemView.txt_description.visibility = View.VISIBLE

                /*itemView.txt_description.addAutoLinkMode(
                        AutoLinkMode.MODE_HASHTAG,
                        AutoLinkMode.MODE_URL)
                itemView.txt_description.setHashtagModeColor(ContextCompat.getColor(itemView.txt_description.context, R.color.buttoncolor))
                itemView.txt_description.setUrlModeColor(ContextCompat.getColor(itemView.txt_description.context, R.color.buttoncolor))*/

                itemView.txt_description.setText(item?.snap_description)
                /**25-11-21**/
                if (itemView.txt_description != null) {
                    mTextHashTagHelper = HashTagHelper.Creator.create(Color.parseColor("#7E57C2"), null)
                    mTextHashTagHelper.handle(itemView.txt_description)
                    Linkify.addLinks(itemView.txt_description, Linkify.ALL) // linkify all links in text.
                    itemView.txt_description.setLinkTextColor(Color.parseColor("#7E57C2"))
                }
            } else {
                itemView.txt_description.visibility = View.GONE
            }
            itemView.txt_name.text = item?.created_user_name
            itemView.txt_chat_count.text = item?.conversation_count
            itemView.txt_date.text = dateFormat(item?.createdAt!!)

            if (item.is_shareable) {
                itemView.btn_share.visibility = View.VISIBLE
            } else {
                itemView.btn_share.visibility = View.GONE
            }
            if (item.conversation_enabled) {
                itemView.chat_view.visibility = View.VISIBLE
            } else {
                itemView.chat_view.visibility = View.GONE
            }

            itemView.btn_share.setOnClickListener {
                showMenusShare(it, position)
            }

            if (item.post_attachment != null && item.post_attachment.size > 0) {
                itemView.attach_pic.visibility = View.VISIBLE
                if (item.post_attachment[0].type.contains("image")) {

                    Glide.with(itemView.attach_pic.context)
                            .load(Constants.ApiPaths.S3_DEV_IMAGE_URL_SQUARE_DETAILED + BuildConfig.IMAGE_BASE_URL + "post/" + item.post_attachment[0].filename)
                            .apply(Utilities.getCurvedRequestOptionsSmall())
                            .transition(DrawableTransitionOptions.withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                            .into(itemView.attach_pic)

                } else if (item.post_attachment[0].type.contains("video")) {
                    if(item.post_attachment[0].video_thumb==null){
                        val resourceid: Int?
                        resourceid = R.drawable.video_icon
                        Glide.with(itemView.attach_pic.context)
                                .load(resourceid)
                                .into(itemView.attach_pic)
                    }
                    else{
                        Glide.with(itemView.attach_pic.context)
                                .load(BuildConfig.IMAGE_BASE_URL + item.post_attachment[0].video_thumb)
                                .apply(Utilities.getCurvedRequestOptions())
                                .transition(DrawableTransitionOptions.withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                                .into(itemView.attach_pic)
                    }

                }
                else if (item.post_attachment[0].type.contains("audio")||item.post_attachment[0].type.contains("mp3")) {
                    val resourceid: Int?
                    resourceid = R.drawable.audio
                    Glide.with(itemView.attach_pic.context)
                            .load(resourceid)
                            .into(itemView.attach_pic)
                }

                else {
                    var resourceid: Int?

                    resourceid = if (item.post_attachment[0].type.contains("pdf")) {
                        R.drawable.pdf
                    } else if (item.post_attachment[0].type.contains("xls")||item.post_attachment[0].type.contains("excel")||item.post_attachment[0].type.contains("sheet"))
                    {
                        R.drawable.ms_excel
                    } else if (item.post_attachment[0].type.contains("ppt")||item.post_attachment[0].type.contains("presentation")||item.post_attachment[0].type.contains("powerpoint"))
                    {
                        R.drawable.ms_powerpoint
                    } else if (item.post_attachment[0].type.contains("doc")||item.post_attachment[0].type.contains("word")&&(!item.post_attachment[0].type.contains("presentation")||!item.post_attachment[0].type.contains("sheet")))
                    {
                        R.drawable.ms_word
                    } else if (item.post_attachment[0].type.contains("zip") || item.post_attachment[0].type.contains("rar")|| item.post_attachment[0].type.contains("octet-stream")) {
                        R.drawable.zip
                    } else {
                        R.drawable.doc
                    }

                    Glide.with(itemView.attach_pic.context)
                            .load(resourceid)
                            .into(itemView.attach_pic)
                }
            } else {
                itemView.attach_pic.visibility = View.GONE
            }

            if (item.propic != null) {
                Glide.with(itemView.img_pro.context)
                        .load(Constants.ApiPaths.S3_DEV_IMAGE_URL_SQUARE + Constants.ApiPaths.IMAGE_BASE_URL + Constants.Paths.PROFILE_PIC + item.propic)
                        .circleCrop()
                        .transition(DrawableTransitionOptions.withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                        .placeholder(R.drawable.avatar_male)
                        .into(itemView.img_pro)
            } else {
                Glide.with(itemView.img_pro.context)
                        .load(R.drawable.avatar_male)
                        .circleCrop()
                        .transition(DrawableTransitionOptions.withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                        .into(itemView.img_pro)
            }

            itemView.chat_view.setOnClickListener {
                itemView.context.startActivity(Intent(itemView.context, PostCommentActivity::class.java)
                        .putExtra(Constants.Bundle.SUB_TYPE, "")
                        .putExtra(Constants.Bundle.TYPE, "HOME")
                        .putExtra("POS", position)
                        .putExtra(Constants.Bundle.DATA, item.post_id.toString()))
            }

            itemView.memberview.setOnClickListener {

                val intent = Intent(itemView.context, ProfileActivity::class.java)
                val familyMember = FamilyMember()
                familyMember.id = SharedPref.getUserRegistration().id.toInt()
                familyMember.userId = items?.get(position)?.created_by
                familyMember.proPic = items?.get(position)?.propic
                intent.putExtra(Constants.Bundle.DATA, familyMember)
                intent.putExtra(Constants.Bundle.FOR_EDITING, true)
                val options = ActivityOptionsCompat.makeSceneTransitionAnimation((itemView.context as Activity?)!!, itemView.img_pro, "profile")
                itemView.context.startActivity(intent, options.toBundle())

            }

            /*itemView.txt_description.setAutoLinkOnClickListener { autoLinkMode, matchedText ->
                if (autoLinkMode === AutoLinkMode.MODE_URL) {
                    try {
                        var url = matchedText.trim()
                        if (!url.contains("http")) {
                            url = url.replace("www.".toRegex(), "http://www.")
                        }
                        if (url.contains("familheey")) {
                            openAppGetParams(url, itemView.txt_description.context)
                        } else {
                            itemView.txt_description.context.startActivity(Intent(itemView.txt_description.context, BrowserActivity::class.java).putExtra("URL", url))
                        }
                    } catch (e: Exception) {
                        *//*
                Don't need this
               *//*
                    }
                }
            }*/

        }

    }

    //CHECKSTYLE:ON
    private fun dateFormat(time: String): String? {
        val dateTime = ISODateTimeFormat.dateTimeParser().parseDateTime(time)
        val dtfOut = DateTimeFormat.forPattern("MMM dd yyyy hh:mm aa")
        return dtfOut.print(dateTime)
    }

    private fun openAppGetParams(url: String, context: Context) {
        // UserNotification

        val compositeDisposable = CompositeDisposable()
        val progressDialog = Utilities.getProgressDialog(context)
        progressDialog.show()
        val jsonObject = JsonObject()
        jsonObject.addProperty("url", url)

        val requestBody: RequestBody = jsonObject.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        val application = FamilheeyApplication.get(context)
        val apiServices = RetrofitBase.createRxResource(context, ApiServices::class.java)
        compositeDisposable.add(apiServices.openAppGetParams(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe({ response: Response<UrlParse> ->
                    progressDialog.dismiss()
                    response.body()!!.data.goToCorrespondingDashboard(context)
                }) { throwable: Throwable? -> progressDialog.dismiss() })
    }

    private fun showMenusShare(v: View, position: Int) {
        val popup = PopupMenu(v.context, v)
        popup.menuInflater.inflate(R.menu.popup_menu_post_share, popup.menu)
        if (!SharedPref.userHasFamily()) {
            popup.menu.getItem(0).isVisible = false
        }
        popup.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.sharefamily -> {
                    v.context.startActivity(Intent(v.context, ShareEventActivity::class.java).putExtra(Constants.Bundle.TYPE, "POST").putExtra("Post_id", items?.get(position)?.post_id.toString() + ""))
                }
                R.id.sharesocial -> {
                    val url = Constants.ApiPaths.SHARE_URL + "page/posts/" + items?.get(position)?.post_id
                    val intent = Intent()
                    intent.action = Intent.ACTION_SEND
                    intent.putExtra(Intent.EXTRA_SUBJECT, "post")
                    intent.type = "text/plain"
                    intent.putExtra(Intent.EXTRA_TEXT, url)
                    v.context.startActivity(Intent.createChooser(intent, "Share"))
                }
            }
            true
        }
        popup.show()
    }
}