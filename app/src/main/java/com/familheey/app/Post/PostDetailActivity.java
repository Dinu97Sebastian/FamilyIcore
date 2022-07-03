package com.familheey.app.Post;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.viewpager.widget.ViewPager;

import com.familheey.app.Models.Request.HistoryImages;
import com.familheey.app.R;
import com.familheey.app.Utilities.HackyViewPager;
import com.familheey.app.Utilities.Utilities;
import com.familheey.app.Utilities.VideoEnabledWebView;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.mediacodec.MediaCodecRenderer;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.familheey.app.Utilities.Constants.ApiPaths.IMAGE_BASE_URL;
import static com.familheey.app.Utilities.Constants.Bundle.DATA;
import static com.familheey.app.Utilities.Constants.Bundle.DETAIL;

public class PostDetailActivity extends AppCompatActivity  {

    public static final String VIEW_NAME_HEADER_IMAGE = "detail:header:image";
    @BindView(R.id.rl_player_activity)
    RelativeLayout rl_player_activity;
    @BindView(R.id.imagedownload)
    ImageView imagedownload;
    @BindView(R.id.pager)
    HackyViewPager pager;
    private int pos = 0;
    @BindView(R.id.onback)
    ImageView onback;
    @BindView(R.id.worm_dots_indicator)
    WormDotsIndicator worm_dots_indicator;
    private String type = "";
    private com.google.android.exoplayer2.ui.PlayerView audio_view;


    private SimpleExoPlayer player=null;
    VideoEnabledWebView playerView;
    PostPagerAdapter postPagerAdapter;
    private int currentPosition=0;
    ArrayList<HistoryImages> historyImages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);
        ButterKnife.bind(this);

    }
    /**@author Devika
     * Invoked when configuration changes occur at runtime based on user actions
     * changed visibility of back button and download icon based on the screen
     * orientation**/
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
                    |View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    |View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            if(getSupportActionBar() != null){
                getSupportActionBar().hide();
            }
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) rl_player_activity.getLayoutParams();
            params.width = params.MATCH_PARENT;
            params.height = params.MATCH_PARENT;
            rl_player_activity.setLayoutParams(params);
            onback.setVisibility(View.GONE);
            imagedownload.setVisibility(View.GONE);
            worm_dots_indicator.setVisibility(View.GONE);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            onback.setVisibility(View.VISIBLE);
            imagedownload.setVisibility(View.VISIBLE);
            worm_dots_indicator.setVisibility(View.VISIBLE);
        }
    }
    /**End**/
    private void init() {
        player = ExoPlayerFactory.newSimpleInstance(this);
        WormDotsIndicator dotsIndicator = findViewById(R.id.worm_dots_indicator);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            historyImages = new Gson().fromJson(getIntent().getExtras().getString(DATA), new TypeToken<ArrayList<HistoryImages>>() {
            }.getType());
            if (getIntent().getExtras().getString(DETAIL) != null) {
                type = getIntent().getExtras().getString(DETAIL);
            }

            if (historyImages != null) {
                pos = getIntent().getExtras().getInt("pos");
                ViewCompat.setTransitionName(pager, VIEW_NAME_HEADER_IMAGE);
                postPagerAdapter = new PostPagerAdapter(this, historyImages, player, type);
                pager.setAdapter(postPagerAdapter);
                pager.setOffscreenPageLimit(historyImages.size());
                dotsIndicator.setViewPager(pager);
              //  pager.setCurrentItem(pos);
                //  currentPosition=pos;
                onback.setOnClickListener(v -> onBackPressed());
                if (historyImages.size() == 1) {
                    dotsIndicator.setVisibility(View.GONE);
                } else {
                    dotsIndicator.setVisibility(View.VISIBLE);
                }

                imagedownload.setVisibility(View.VISIBLE);
                /**@author Devika
                 * modified callback methods of viewpager to fix the video player's
                 * stop,pause and play on swipe of viewpager**/
                pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    private int previousPosition;
                    private int nextPosition;
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {



                        if(historyImages.get(position).getType().contains("video")|| historyImages.get(position).getType().contains("audio")||historyImages.get(position).getType().contains("mp3")){
                            View view = pager.getChildAt(position);
                            playerView = view.findViewById(R.id.web);
                            //   playerView.onResume();
                        }
                    }
                    @Override
                    public void onPageSelected(int position) {
                        previousPosition=currentPosition;
                        currentPosition = position;
                        if (historyImages.get(position).getType().contains("image")) {
                            imagedownload.setVisibility(View.VISIBLE);
                        }else if( historyImages.get(position).getType().contains("audio")){
                            DataSource.Factory mediaDataSourceFactory = new DefaultDataSourceFactory(getApplicationContext(), Util.getUserAgent(getApplicationContext(), "Familheey"));

                            String url = IMAGE_BASE_URL + "post/" + historyImages.get(currentPosition).getFilename();
                            if ("albums".equals(type)) {
                                url = IMAGE_BASE_URL + "Documents/" + historyImages.get(currentPosition).getFilename();
                            }

                            ProgressiveMediaSource mediaSource = new ProgressiveMediaSource.Factory(mediaDataSourceFactory)
                                    .createMediaSource(Uri.parse(url));

                            View view = pager.getChildAt(currentPosition);
                            audio_view=view.findViewById(R.id.audio_view);
                            player.prepare(mediaSource, false, false);
                            player.setPlayWhenReady(false);
                            audio_view.setShutterBackgroundColor(Color.TRANSPARENT);
                            audio_view.setPlayer(player);
                            audio_view.requestFocus();
                            imagedownload.setVisibility(View.VISIBLE);
                        }
                        else{
                            imagedownload.setVisibility(View.VISIBLE);
                        }
                        if(historyImages.get(previousPosition).getType().contains("video") && historyImages.get(currentPosition).getType().contains("video")){
                            View view = pager.getChildAt(previousPosition);
                            playerView = view.findViewById(R.id.web);
                            playerView.onPause();
                            View currentView = pager.getChildAt(currentPosition);
                            playerView = currentView.findViewById(R.id.web);
                            playerView.onResume();

                            //  playerView.loadData(html, "text/html", "UTF-8");
                        }
                        else if(historyImages.get(currentPosition).getType().contains("video")) {
                            View currentView = pager.getChildAt(currentPosition);
                            playerView = currentView.findViewById(R.id.web);
                            playerView.onResume();
                        }else{
                            View currentView = pager.getChildAt(previousPosition);
                            playerView = currentView.findViewById(R.id.web);
                            playerView.onPause();
                        }
                    }
                    @Override
                    public void onPageScrollStateChanged(int state) {
                       /* if(historyImages.get(currentPosition).getType().contains("video")){
                            View view = pager.getChildAt(currentPosition);
                            playerView = view.findViewById(R.id.video_view);
                            if(playerView.getPlayer()!=null){
                                if(playerView.getPlayer().getPlayWhenReady()){
                                    playerView.getPlayer().setPlayWhenReady(false);
                                }
                            }

                           // playerView=null;
                        }*/
                    }
                });
            }
            /**End**/
            imagedownload.setOnClickListener(view -> {
                        if (isReadStoragePermissionGranted()) {
                            assert historyImages != null;
                            String url = IMAGE_BASE_URL + "post/" + historyImages.get(pager.getCurrentItem()).getFilename();
                            if ("albums".equals(type)) {
                                url = IMAGE_BASE_URL + "Documents/" + historyImages.get(pager.getCurrentItem()).getFilename();
                            }

                            Toast.makeText(this, "Downloading media...", Toast.LENGTH_SHORT).show();
                            Utilities.downloadDocuments(this, url, historyImages.get(pager.getCurrentItem()).getFilename());
                        } else
                            showPermission();

                    }
            );
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) player.release();
    }
    @Override
    protected void onResume() {
        super.onResume();
        init();
    }
    @Override
    protected void onPause() {
        super.onPause();
    }
    private boolean isReadStoragePermissionGranted() {
        return TedPermission.isGranted(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    private void requestPermission() {
        TedPermission.with(this)
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        imagedownload.performClick();
                    }

                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {

                    }
                })
                .setDeniedMessage("If you reject permission,you can not download images\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();
    }

    private void showPermission() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialogue_permission);
        Window window = dialog.getWindow();
        assert window != null;
        window.setBackgroundDrawableResource(android.R.color.transparent);

        TextView txt_decs = dialog.findViewById(R.id.txt_decs);
        txt_decs.setText("To view images, allow Familheey access to your device's photos,media, and files.");
        dialog.findViewById(R.id.btn_cancel).setOnClickListener(view -> dialog.dismiss());
        dialog.findViewById(R.id.btn_continue).setOnClickListener(view -> {
            dialog.dismiss();
            requestPermission();
        });
        dialog.show();
    }

    /**@author Devika
     * modified onBackpressed inorder to exit from the VideoActivity
     * even if the current video in viewpager is playing **/
    @Override
    public void onBackPressed() {
        if(historyImages.get(currentPosition).getType().contains("video")){
            //  stopPlayerIfPlaying();
        }
        String url = IMAGE_BASE_URL + "post/" + historyImages.get(currentPosition).getFilename();
        if ("albums".equals(type)) {
            url = IMAGE_BASE_URL + "Documents/" + historyImages.get(currentPosition).getFilename();
        }
        String previwurl = IMAGE_BASE_URL + "default_video.jpg";
        if (historyImages.get(currentPosition).getVideo_thumb() != null && !historyImages.get(currentPosition).getVideo_thumb().equals("")) {
            previwurl = IMAGE_BASE_URL + historyImages.get(currentPosition).getVideo_thumb();
        }
        String sourc = "<source src=\"" + url + "\"  type=\"video/mp4\">";
        String html = "<html><body><video width=\"100%\" height=\"100%\" poster=\"" + previwurl + "\"controls=\"controls\" controlsList=\"nodownload\" >" + sourc + "</video></body></html>";


        View view = pager.getChildAt(currentPosition);
        playerView =  view.findViewById(R.id.web);
        playerView.loadData(html, "text/html", "UTF-8");
        super.onBackPressed();
    }
    private void stopPlayerIfPlaying() {
      /*  View view = pager.getChildAt(currentPosition);
        playerView =  view.findViewById(R.id.video_view);
        if(playerView.getPlayer().getPlayWhenReady()){
            playerView.getPlayer().setPlayWhenReady(false);
        }
        playerView=null;*/
    }
    /**End**/
}
