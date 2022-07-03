package com.familheey.app.Post;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.familheey.app.Models.Request.HistoryImages;
import com.familheey.app.R;
import com.familheey.app.Utilities.Constants;
import com.familheey.app.Utilities.VideoEnabledWebChromeClient;
import com.familheey.app.Utilities.VideoEnabledWebView;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.gson.Gson;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;

import static com.familheey.app.Utilities.Constants.ApiPaths.IMAGE_BASE_URL;
import static com.familheey.app.Utilities.Constants.ApiPaths.S3_DEV_IMAGE_URL_SQUARE_DETAILED;
import static com.familheey.app.Utilities.Constants.Bundle.DATA;

public class PostPagerAdapter extends PagerAdapter {

    private float cancelX,cancelY,downX, downY;
    private Context context;
    private ArrayList<HistoryImages> historyImages;

    private SimpleExoPlayer player;
    private String type;
    RelativeLayout videoview;
    RelativeLayout webView;
    PlayerView playerView=null;
    ImageView fullscreenButton;
    boolean fullscreen = false;
    private VideoEnabledWebChromeClient webChromeClient;

    PostPagerAdapter(Context context, ArrayList<HistoryImages> historyImages, SimpleExoPlayer player, String type) {

        this.historyImages = historyImages;
        this.context = context;
        this.player = player;
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.transforms(new RoundedCorners(16));
        this.type = type;
        // player = ExoPlayerFactory.newSimpleInstance(context);
    }


    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        HistoryImages images = historyImages.get(position);
        LayoutInflater inflater = LayoutInflater.from(context);
        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.item_post_pager, container, false);

        PhotoView photoView = layout.findViewById(R.id.image);
        ProgressBar progressBar = layout.findViewById(R.id.progressBar);
        ImageView volume_on = layout.findViewById(R.id.volume_on);
        ImageView volume_off = layout.findViewById(R.id.volume_off);

        ImageView doc = layout.findViewById(R.id.doc);

         videoview = layout.findViewById(R.id.videoview);
        webView=layout.findViewById(R.id.webView);
        VideoEnabledWebView  web=layout.findViewById(R.id.web);
        RelativeLayout photoContainer = layout.findViewById(R.id.photoContainer);
        com.google.android.exoplayer2.ui.PlayerView video_view = layout.findViewById(R.id.video_view);

        RelativeLayout audioview = layout.findViewById(R.id.audioview);
        com.google.android.exoplayer2.ui.PlayerView audio_view = layout.findViewById(R.id.audio_view);
        ImageView audio_volume_on = layout.findViewById(R.id.audio_volume_on);
        ImageView audio_volume_off = layout.findViewById(R.id.audio_volume_off);

        if (images.getType().contains("image")) {

            photoContainer.setVisibility(View.VISIBLE);
            doc.setVisibility(View.GONE);
            videoview.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            String url = S3_DEV_IMAGE_URL_SQUARE_DETAILED + IMAGE_BASE_URL + "post/" + images.getFilename();
            if ("albums".equals(type)) {
                url = S3_DEV_IMAGE_URL_SQUARE_DETAILED + IMAGE_BASE_URL + "Documents/" + images.getFilename();
            }
            Glide.with(context)
                    .load(url)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            progressBar.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                            progressBar.setVisibility(View.GONE);
                            return false;
                        }
                    }).into(photoView);

        }
        else if (images.getType().contains("video")) {
            photoContainer.setVisibility(View.GONE);
            doc.setVisibility(View.GONE);
            videoview.setVisibility(View.GONE);
            webView.setVisibility(View.VISIBLE);
            String url = IMAGE_BASE_URL + "post/" + images.getFilename();
            if ("albums".equals(type)) {
                url = IMAGE_BASE_URL + "Documents/" + images.getFilename();
            }
            String previwurl = IMAGE_BASE_URL + "default_video.jpg";
            if (images.getVideo_thumb() != null && !images.getVideo_thumb().equals("")) {
                previwurl = IMAGE_BASE_URL + images.getVideo_thumb();
            }
            String sourc = "<source src=\"" + url + "\"  type=\"video/mp4\">";
            String html = "<html><body><video width=\"100%\" height=\"100%\" poster=\"" + previwurl + "\"controls=\"controls\" controlsList=\"nodownload\" >" + sourc + "</video></body></html>";
            web.setBackgroundColor(Color.TRANSPARENT);
            webChromeClient = new VideoEnabledWebChromeClient(webView, webView) // See all available constructors...
            {
                // Subscribe to standard events, such as onProgressChanged()...
                @Override
                public void onProgressChanged(WebView view, int progress) {
                    // Your code...
                }
            };
            webChromeClient.setOnToggledFullscreen(new VideoEnabledWebChromeClient.ToggledFullscreenCallback() {
                @Override
                public void toggledFullscreen(boolean fullscreen) {
                    if(!fullscreen){

                        ((Activity)context).getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                        ((Activity)context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) web.getLayoutParams();
                        params.width = params.MATCH_PARENT;
                        params.height = params.MATCH_PARENT;
                        web.setLayoutParams(params);
                    }else{

                        ((Activity)context).getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
                                |View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                                |View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

                        ((Activity)context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

                        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) web.getLayoutParams();
                        params.width = params.MATCH_PARENT;
                        params.height = params.MATCH_PARENT;
                        web.setLayoutParams(params);
                    }
                }
            });
            //  Log.e("HTML", html);
            web.setWebChromeClient(webChromeClient);
            web.loadData(html, "text/html", "UTF-8");




        }

       /* else if (images.getType().contains("video")) {
            photoContainer.setVisibility(View.GONE);
            doc.setVisibility(View.GONE);
            videoview.setVisibility(View.VISIBLE);
            DataSource.Factory mediaDataSourceFactory = new DefaultDataSourceFactory(context, Util.getUserAgent(context, "Familheey"));

            String url = IMAGE_BASE_URL + "post/" + images.getFilename();
            if ("albums".equals(type)) {
                url = IMAGE_BASE_URL + "Documents/" + images.getFilename();
            }

            ProgressiveMediaSource mediaSource = new ProgressiveMediaSource.Factory(mediaDataSourceFactory)
                    .createMediaSource(Uri.parse(url));


            *//**@author Devika on 18-04-2022
             * for implementing fullscreen option for videos in view pager.
             * included a new layout exo_playback_control_view with a fullscreen icon (ic_fullscreen_open)
             * when the video is in portrait mode and implemented its click functionality.In landscape mode
             * button ic_fullscreen_open replaces by ic_fullscreen_close and implemented its click functionality also**//*

            if(player!=null){
                player= ExoPlayerFactory.newSimpleInstance(context);
            }

            playerView = video_view;
            fullscreenButton = playerView.findViewById(R.id.exo_fullscreen_icon);
            fullscreenButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(fullscreen) {
                        fullscreenButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_fullscreen_open));
                        ((Activity)context).getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                        ((Activity)context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) playerView.getLayoutParams();
                        params.width = params.MATCH_PARENT;
                        params.height = (int) ( 300 * context.getResources().getDisplayMetrics().density);
                        playerView.setLayoutParams(params);

                        fullscreen = false;
                    }else{
                        fullscreenButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_fullscreen_close));

                        ((Activity)context).getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
                                |View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                                |View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

                        ((Activity)context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

                        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) playerView.getLayoutParams();
                        params.width = params.MATCH_PARENT;
                        params.height = params.MATCH_PARENT;
                        playerView.setLayoutParams(params);

                        fullscreen = true;
                    }
                }
            });
            *//**end fullscreen feature**//*
            player.prepare(mediaSource, false, false);
            player.setPlayWhenReady(false);
            video_view.setShutterBackgroundColor(Color.TRANSPARENT);
            video_view.setPlayer(player);
            video_view.requestFocus();
            float currentvolume = player.getVolume();
            volume_on.setOnClickListener(v -> {
                volume_on.setVisibility(View.GONE);
                volume_off.setVisibility(View.VISIBLE);
                player.setVolume(0f);
            });

            volume_off.setOnClickListener(v -> {
                volume_on.setVisibility(View.VISIBLE);
                volume_off.setVisibility(View.GONE);
                player.setVolume(currentvolume);
            });

        }*/
// for play audio->Dinu
        else if (images.getType().contains("audio")||images.getType().contains("mp3")) {

            photoContainer.setVisibility(View.GONE);
            doc.setVisibility(View.GONE);
            videoview.setVisibility(View.GONE);
            audioview.setVisibility(View.VISIBLE);
            DataSource.Factory mediaDataSourceFactory = new DefaultDataSourceFactory(context, Util.getUserAgent(context, "Familheey"));

            String url = IMAGE_BASE_URL + "post/" + images.getFilename();
            if ("albums".equals(type)) {
                url = IMAGE_BASE_URL + "Documents/" + images.getFilename();
            }

            ProgressiveMediaSource mediaSource = new ProgressiveMediaSource.Factory(mediaDataSourceFactory)
                    .createMediaSource(Uri.parse(url));
            player.prepare(mediaSource, false, false);
            player.setPlayWhenReady(false);
            audio_view.setShutterBackgroundColor(Color.TRANSPARENT);
            audio_view.setPlayer(player);
            audio_view.requestFocus();
            float currentvolume = player.getVolume();
            audio_volume_on.setOnClickListener(v -> {
                audio_volume_on.setVisibility(View.GONE);
                audio_volume_off.setVisibility(View.VISIBLE);
                player.setVolume(0f);
            });

            audio_volume_off.setOnClickListener(v -> {
                audio_volume_on.setVisibility(View.VISIBLE);
                audio_volume_off.setVisibility(View.GONE);
                player.setVolume(currentvolume);
            });

            audio_view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            downX = event.getX();
                            downY = event.getY();

                            break;

                        case MotionEvent.ACTION_CANCEL:
                            cancelX=event.getX();
                            cancelY=event.getY();

                            float deltaX = downX - cancelX;
                            float deltaY = downY - cancelY;
                            if (Math.abs(deltaX) > Math.abs(deltaY)) {
                                if (deltaX < 0) {
                                    player.prepare(mediaSource, false, false);
                                    player.setPlayWhenReady(false);
                                    audio_view.setShutterBackgroundColor(Color.TRANSPARENT);
                                    audio_view.setPlayer(player);
                                    audio_view.requestFocus();
                                }
                                if (deltaX > 0) {

                                    player.prepare(mediaSource, false, false);
                                    player.setPlayWhenReady(false);
                                    audio_view.setShutterBackgroundColor(Color.TRANSPARENT);
                                    audio_view.setPlayer(player);
                                    audio_view.requestFocus();
                                }
                            }
                            break;
                    }
                    return true;
                }
            });

        }

        else {
            //player.release();

            photoContainer.setVisibility(View.GONE);
            doc.setVisibility(View.VISIBLE);
            videoview.setVisibility(View.GONE);
            if(images.getType().contains("pdf")){
                doc.setImageResource(R.drawable.pdf);
            }
            else if(images.getType().contains("xls")|| images.getType().contains("excel")||images.getType().contains("sheet"))
            {
                doc.setImageResource(R.drawable.ms_excel);
            }
             else if(images.getType().contains("ppt")||images.getType().contains("presentation")||images.getType().contains("powerpoint"))
            {
                doc.setImageResource(R.drawable.ms_powerpoint);
            }
            else if(images.getType().contains("doc")||images.getType().contains("word") &&(!images.getType().contains("presentation")||!images.getType().contains("sheet")))
            {
                doc.setImageResource(R.drawable.ms_word);
            }
            else if(images.getType().contains("zip")||images.getType().contains("rar")||images.getType().contains("octet-stream"))
            {
                doc.setImageResource(R.drawable.zip);
            }
            else
            {
                doc.setImageResource(R.drawable.doc);
                doc.setColorFilter( context.getResources().getColor(R.color.white));
            }

            doc.setOnClickListener(v -> {

                String url = IMAGE_BASE_URL + "post/" + images.getFilename();
                if ("albums".equals(type)) {
                    url = IMAGE_BASE_URL + "Documents/" + images.getFilename();
                }

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                context.startActivity(browserIntent);
            });
        }


        container.addView(layout);
        return layout;
    }
    //Dinu(19-03-2021)
    private boolean isReadStoragePermissionGranted() {
        return TedPermission.isGranted(context.getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }


    @Override
    public int getCount() {
        return historyImages.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
    /**@author Devika
     * returns the item position in viewpager
     * added to fix the video conflict issue inside the viewpager**/
    @Override
    public int getItemPosition(@NonNull Object object) {
        View o = (View) object;
        int index = historyImages.indexOf(object);
        if (index == -1)
            return POSITION_NONE;
        else
            return index;
    }
}