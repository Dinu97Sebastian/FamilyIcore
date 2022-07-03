package com.familheey.app.Fragments.Posts;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.familheey.app.Models.Request.HistoryImages;
import com.familheey.app.R;
import com.familheey.app.Utilities.TouchImageView;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.familheey.app.Utilities.Constants.ApiPaths.IMAGE_BASE_URL;

public class PostAttachmentSliderAdapter extends SliderViewAdapter<PostAttachmentSliderAdapter.SliderAdapterVH> {
    private Context context;
    private ArrayList<HistoryImages> documents;

    public PostAttachmentSliderAdapter(Context context, ArrayList<HistoryImages> documents) {
        this.context = context;
        this.documents = documents;
    }

    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post_attachment_slider, null);
        return new SliderAdapterVH(inflate);
    }

    @Override
    public void onBindViewHolder(SliderAdapterVH viewHolder, int position) {
        HistoryImages document = documents.get(position);

        if (document.getType().contains("image")) {
            viewHolder.doc.setVisibility(View.GONE);
            viewHolder.autoSliderImage.setVisibility(View.VISIBLE);
            viewHolder.videoView.setVisibility(View.GONE);
            Glide.with(context)
                    .load(IMAGE_BASE_URL + "post/" + document.getFilename())
                    .into(viewHolder.autoSliderImage);
           // viewHolder.playVideo.setVisibility(View.GONE);
        } else if (document.getType().contains("video")) {

            viewHolder.doc.setVisibility(View.GONE);
           // viewHolder.playVideo.setVisibility(View.VISIBLE);
            viewHolder.autoSliderImage.setVisibility(View.GONE);
            viewHolder.videoView.setVisibility(View.GONE);
           // viewHolder.videoView.setVideoURI(Uri.parse(IMAGE_BASE_URL + "post/" + document.getFilename()));
          /*  viewHolder.playVideo.setOnClickListener(v -> {*//*
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(IMAGE_BASE_URL + "post/" + document.getFilename()));
                intent.setDataAndType(Uri.parse(IMAGE_BASE_URL + "post/" + document.getFilename()), "video/mp4");
                viewHolder.playVideo.getContext().startActivity(intent);*//*

                viewHolder.playVideo.getContext().startActivity(new Intent(viewHolder.playVideo.getContext(), VideoActivity.class).putExtra("url",IMAGE_BASE_URL + "post/" + document.getFilename()));
            });*/
           /* viewHolder.videoView.setOnCompletionListener(mp -> viewHolder.playVideo.setVisibility(View.VISIBLE));
            if (viewHolder.videoView.isPlaying()) {
                viewHolder.playVideo.setVisibility(View.GONE);
            } else {
                viewHolder.playVideo.setVisibility(View.VISIBLE);
            }*/

            SimpleExoPlayer player = ExoPlayerFactory.newSimpleInstance(context);

            DataSource.Factory mediaDataSourceFactory = new DefaultDataSourceFactory(context, Util.getUserAgent(context, "Familheey"));
            ProgressiveMediaSource mediaSource =new ProgressiveMediaSource.Factory(mediaDataSourceFactory)
                    .createMediaSource(Uri.parse(IMAGE_BASE_URL + "post/" + document.getFilename()));
            player.prepare(mediaSource, false, false);
            player.setPlayWhenReady(true);
            viewHolder.videoView.setShutterBackgroundColor(Color.TRANSPARENT);
            viewHolder.videoView.setPlayer(player);
            viewHolder.videoView.requestFocus();

        } else {
            //viewHolder.playVideo.setVisibility(View.GONE);
            viewHolder.autoSliderImage.setVisibility(View.GONE);
            viewHolder.videoView.setVisibility(View.GONE);
            viewHolder.doc.setVisibility(View.VISIBLE);
            viewHolder.doc.setImageResource(R.drawable.doc);
            viewHolder.doc.setOnClickListener(v -> {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(IMAGE_BASE_URL + "post/" + document.getFilename()));
                viewHolder.itemView.getContext().startActivity(browserIntent);
            });

        }

    }

    @Override
    public int getCount() {
        return documents.size();
    }

    class SliderAdapterVH extends SliderViewAdapter.ViewHolder {
        View itemView;
        @BindView(R.id.iv_auto_image_slider)
        TouchImageView autoSliderImage;
        //@BindView(R.id.playVideo)
        //ImageView playVideo;
        @BindView(R.id.doc)
        ImageView doc;

        @BindView(R.id.video_view)
        com.google.android.exoplayer2.ui.PlayerView videoView;

        public SliderAdapterVH(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.itemView = itemView;
        }
    }
}