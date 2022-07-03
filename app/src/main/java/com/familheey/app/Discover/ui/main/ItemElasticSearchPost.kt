package com.familheey.app.Discover.ui.main

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.familheey.app.BuildConfig
import com.familheey.app.Discover.model.ElasticPost
import com.familheey.app.Post.PostDetailForPushActivity
import com.familheey.app.R
import com.familheey.app.Utilities.Constants
import com.familheey.app.Utilities.Utilities
import kotlinx.android.synthetic.main.item_discover_elastic_search_post.view.*


class ItemElasticSearchPost(internal val items: MutableList<ElasticPost>?) :
        androidx.recyclerview.widget.RecyclerView.Adapter<ItemElasticSearchPost.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_discover_elastic_search_post, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return items?.size ?: 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
        holder.itemView.setOnClickListener {
            holder.itemView.context.startActivity(Intent(holder.itemView.context, PostDetailForPushActivity::class.java).putExtra(Constants.Bundle.TYPE, "NOTIFICATION").putExtra("ids", items?.get(position)?._source?.id.toString()))
        }
    }

    inner class ViewHolder(itemView: View) :
            androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        fun bind(position: Int) {
            val item = items?.get(position)

            if (item?._source?.post_attachment != null && item._source.post_attachment.size > 0) {

                if (item._source.post_attachment[0].type.contains("video")) {
                    if(item._source.post_attachment[0].video_thumb==null){
                        Glide.with(itemView.post_attachment.context)
                                .load(R.drawable.video_icon)
                                .transition(DrawableTransitionOptions.withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                                .into(itemView.post_attachment)
                    }
                    else{
                        Glide.with(itemView.context).load(Constants.ApiPaths.S3_DEV_IMAGE_URL_ELASTIC + BuildConfig.IMAGE_BASE_URL + item._source.post_attachment[0].video_thumb)
                                .diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.color.black).into(itemView.post_attachment)
                    }
                }
                // For handle preview audio->Dinu
                else if (item._source.post_attachment[0].type.contains("audio")||item._source.post_attachment[0].type.contains("mp3")) {
                var audioArray=item._source.post_attachment;
                    for(item in audioArray){
                        if(item.type.contains("video")){

                            if(item.video_thumb==null){
                                Glide.with(itemView.post_attachment.context)
                                        .load(R.drawable.video_icon)
                                        .transition(DrawableTransitionOptions.withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                                        .into(itemView.post_attachment)
                                return
                            }
                            else{
                                Glide.with(itemView.context).load(Constants.ApiPaths.S3_DEV_IMAGE_URL_ELASTIC + BuildConfig.IMAGE_BASE_URL + item.video_thumb)
                                        .diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.color.black).into(itemView.post_attachment)
                                return
                            }

                        }
                        else if(item.type.contains("image")){
                            Glide.with(itemView.context).load(Constants.ApiPaths.S3_DEV_IMAGE_URL_ELASTIC + BuildConfig.IMAGE_BASE_URL + "post/" + item.filename)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.color.black).into(itemView.post_attachment)
                            return
                        }
                    }
                    return
                }
                else if(item._source.post_attachment[0].type.contains("image")){
                    Glide.with(itemView.context).load(Constants.ApiPaths.S3_DEV_IMAGE_URL_ELASTIC + BuildConfig.IMAGE_BASE_URL + "post/" + item._source.post_attachment[0].filename)
                            .diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.color.black).into(itemView.post_attachment)
                }
                else{
                    var audioArray=item._source.post_attachment;
                    for(item in audioArray){
                          if(item.type.contains("video")){

                            if(item.video_thumb==null){
                                Glide.with(itemView.post_attachment.context)
                                        .load(R.drawable.video_icon)
                                        .transition(DrawableTransitionOptions.withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                                        .into(itemView.post_attachment)
                                return
                            }
                            else{
                                Glide.with(itemView.context).load(Constants.ApiPaths.S3_DEV_IMAGE_URL_ELASTIC + BuildConfig.IMAGE_BASE_URL + item.video_thumb)
                                        .diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.color.black).into(itemView.post_attachment)
                                return
                            }

                        }
                        else if(item.type.contains("image")){
                            Glide.with(itemView.context).load(Constants.ApiPaths.S3_DEV_IMAGE_URL_ELASTIC + BuildConfig.IMAGE_BASE_URL + "post/" + item.filename)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.color.black).into(itemView.post_attachment)
                            return
                        }
                    }
                    return;
                }
            } else {
                Glide.with(itemView.post_attachment.context)
                        .load(R.drawable.family_logo)
                        .transition(DrawableTransitionOptions.withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                        .into(itemView.post_attachment)
            }

        }
    }

}