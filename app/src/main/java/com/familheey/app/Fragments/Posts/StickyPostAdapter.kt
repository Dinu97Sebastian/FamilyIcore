package com.familheey.app.Fragments.Posts

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.familheey.app.BuildConfig
import com.familheey.app.Post.PostCommentActivity
import com.familheey.app.Post.PostDetailForPushActivity
import com.familheey.app.R
import com.familheey.app.Utilities.Constants
import com.familheey.app.Utilities.SharedPref
import kotlinx.android.synthetic.main.item_sticky_post.view.*


class StickyPostAdapter(internal val items: MutableList<StickyPost>, internal val context: Context, internal val isAdmin: Boolean, internal val groupId: String, internal val mListener: PostAdapterInFamilyFeed.postItemClick, internal val stickyAutoScroll: Boolean) : androidx.recyclerview.widget.RecyclerView.Adapter<StickyPostAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_sticky_post, parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
        holder.itemView.setOnClickListener {
            SharedPref.write(SharedPref.STICKY_POST_POSITION, position.toString())
          //  val intent = Intent(context, PostDetailForPushActivity::class.java).putExtra("ids", items[position].postId.toString()).putExtra(Constants.Bundle.TYPE, "NOTIFICATION")

            val intent = Intent(context, PostCommentActivity::class.java)
                    .putExtra(Constants.Bundle.DATA, items[position].postId.toString())
                    .putExtra(Constants.Bundle.TYPE, "STICKY")
                    .putExtra(Constants.Bundle.SUB_TYPE, "POST")
                    .putExtra("POS", 0)
            context.startActivity(intent)
        }
    }

    inner class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        fun bind(position: Int) {
            val item = items[position]

            itemView.btn_more.setOnClickListener {
               // showMenusNormalUser(it, position)
                confirmationForStiky(position)
            }
            /*
           * Dinu(26-05-2022): hide unsctickysticky when sticky_auto_scroll is false */
            if(!stickyAutoScroll) {
                itemView.owner_family.visibility=View.GONE
                if (isAdmin) {
                    itemView.btn_more.visibility = View.VISIBLE
                } else {
                    itemView.btn_more.visibility = View.GONE
                }
            }else{
                if(item.adminOf!=null) {
                    itemView.owner_family.visibility = View.VISIBLE
                    itemView.owner_family.text = "Of ${item.adminOf}"
                }
                if(item.createdUserId== SharedPref.getUserRegistration().id.toInt() && isAdmin){
                    itemView.btn_more.visibility = View.VISIBLE
                } else {
                    itemView.btn_more.visibility = View.GONE
                }
            }
            itemView.txt_by.text = "By ${item.createdUserName}"
            if (item.snapDescription.isEmpty()) {
                itemView.txt_description.visibility = View.GONE
            } else {
                itemView.txt_description.visibility = View.VISIBLE
                itemView.txt_description.text = item.snapDescription
            }
            if (item.postAttachment.isNotEmpty()) {
                itemView.img_image.visibility = View.VISIBLE
                if (item.postAttachment[0].type.contains("image")) {
                    Glide.with(itemView.context).load(BuildConfig.IMAGE_BASE_URL + "post/" + item.postAttachment[0].filename).into(itemView.img_image)
                } else if (item.postAttachment[0].type.contains("video")) {
                    Glide.with(itemView.context).load(BuildConfig.IMAGE_BASE_URL + item.postAttachment[0].videoThumb).into(itemView.img_image)
                }
                /*megha(05/11/21), for audio preview in audio sticky post.*/
                else if (item.postAttachment[0].type.contains("audio")){
                    Glide.with(itemView.context).load(R.drawable.audio).into(itemView.img_image)

                }else {
                    if (item.postAttachment[0].type.contains("pdf")){
                        Glide.with(itemView.context).load(R.drawable.pdf).into(itemView.img_image)
                    }else if (item.postAttachment[0].type.contains("xls")||item.postAttachment[0].type.contains("excel")
                            ||item.postAttachment[0].type.contains("sheet")){
                        Glide.with(itemView.context).load(R.drawable.ms_excel).into(itemView.img_image)
                    }else if (item.postAttachment[0].type.contains("ppt")
                            ||item.postAttachment[0].type.contains("presentation")
                            ||item.postAttachment[0].type.contains("powerpoint")){
                        Glide.with(itemView.context).load(R.drawable.ms_powerpoint).into(itemView.img_image)

                    }else if (item.postAttachment[0].type.contains("doc")
                            ||item.postAttachment[0].type.contains("word")
                            && !item.postAttachment[0].type.contains("presentation")
                            ||!item.postAttachment[0].type.contains("sheet")
                            ||!item.postAttachment[0].type.contains("xls")){ Glide.with(itemView.context).load(R.drawable.ms_word).into(itemView.img_image)
                    }else if (item.postAttachment[0].type.contains("zip")
                            ||item.postAttachment[0].type.contains("rar")
                            ||item.postAttachment[0].type.contains("octet-stream")){
                        Glide.with(itemView.context).load(R.drawable.zip).into(itemView.img_image)
                    }else{
                        Glide.with(itemView.context).load(R.drawable.doc).into(itemView.img_image)
                    }
                }
            } else {
                itemView.img_image.visibility = View.GONE
            }


        }

        fun showMenusNormalUser(v: View, position: Int) {
            val popup = PopupMenu(v.context, v)
            popup.menuInflater.inflate(R.menu.unsticky_menu, popup.menu)

            popup.setOnMenuItemClickListener { item: MenuItem ->
                when (item.itemId) {
                    R.id.unsticky -> {

                        confirmationForStiky(position)
                    }
                }
                true
            }
            popup.show()
        }
    }


    fun confirmationForStiky(position: Int) {
        val pDialog = SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                .setContentText("Do you want to unstick this post?")
                .setConfirmText("Yes")
                .setCancelText("No")
        pDialog.setConfirmClickListener {

            mListener.makeAsUnSticky(items[position].postId.toString())
            pDialog.cancel()
        }
        pDialog.setCancelClickListener { obj: SweetAlertDialog -> obj.cancel() }
        pDialog.show()
    }

}