package com.familheey.app.Fragments.Posts;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.DownloadListener;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.familheey.app.Activities.FamilyDashboardActivity;
import com.familheey.app.BuildConfig;
import com.familheey.app.Models.Request.HistoryImages;
import com.familheey.app.Post.PostDetailActivity;
import com.familheey.app.R;
import com.familheey.app.Topic.MainActivity;
import com.familheey.app.Topic.TopicsListingActivity;
import com.familheey.app.Utilities.Constants;
import com.familheey.app.Utilities.Utilities;
import com.familheey.app.Utilities.VideoEnabledWebChromeClient;
import com.familheey.app.Utilities.VideoEnabledWebView;
import com.google.gson.Gson;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.familheey.app.Utilities.Constants.ApiPaths.S3_DEV_IMAGE_URL_SQUARE_DETAILED;
import static com.familheey.app.Utilities.Constants.ApiPaths.S3_DEV_IMAGE_URL_THUMB;
import static com.familheey.app.Utilities.Constants.Bundle.DATA;

public class PostSliderAdapter extends SliderViewAdapter<PostSliderAdapter.SliderAdapterVH> {
    private float cancelX,cancelY,downX, downY;
    private Context context;
    private PostData postData;
    private String IMAGE_BASE_URL = BuildConfig.IMAGE_BASE_URL;
    public PostSliderAdapter(Context context, PostData postData) {
        this.context = context;
        this.postData = postData;
    }


    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post_slider, null);
        return new SliderAdapterVH(inflate);
    }

    @Override
    public void onBindViewHolder(SliderAdapterVH viewHolder, int position) {
        HistoryImages document = postData.getPost_attachment().get(position);

        if (document.getType() == null || document.getType().contains("image")) {

            viewHolder.doc.setVisibility(View.GONE);
            viewHolder.autoSliderImage.setVisibility(View.VISIBLE);
            viewHolder.web.setVisibility(View.GONE);
            viewHolder.video_back_ground.setVisibility(View.GONE);
            viewHolder.audio_back_ground.setVisibility(View.GONE);
            if (position == 0 && document.getHeight() != null && document.getWidth() != null) {
                int w = ((int) Double.parseDouble(document.getWidth()));
                int h = ((int) Double.parseDouble(document.getHeight()));

                if (document.getHeight1() != null && document.getHeight1().length() > 0) {
                    h = ((int) Double.parseDouble(document.getHeight1()));
                }

                if (w < h || w > 1400 && h > 850) {
                    viewHolder.autoSliderImage.setScaleType(ImageView.ScaleType.FIT_XY);
                } else {
                    viewHolder.autoSliderImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
                }
            } else {
                viewHolder.autoSliderImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            }

            String url = "";
            String turl = "";

            if (document.getFilename() != null) {
                if (postData.getPublish_type() != null && "albums".equals(postData.getPublish_type())) {
                    if (document.getFilename().contains(".gif")) {
                        url = BuildConfig.IMAGE_BASE_URL + "Documents/" + document.getFilename();
                        turl = url;
                    } else {
                        url = S3_DEV_IMAGE_URL_SQUARE_DETAILED + BuildConfig.IMAGE_BASE_URL + "Documents/" + document.getFilename();
                        turl = S3_DEV_IMAGE_URL_THUMB + BuildConfig.IMAGE_BASE_URL + "Documents/" + document.getFilename();
                    }
                } else {
                    if (document.getFilename().contains(".gif")) {
                        url = BuildConfig.IMAGE_BASE_URL + "post/" + document.getFilename();
                        turl = url;
                    } else {
                        url = S3_DEV_IMAGE_URL_SQUARE_DETAILED + BuildConfig.IMAGE_BASE_URL + "post/" + document.getFilename();
                        turl = S3_DEV_IMAGE_URL_THUMB + BuildConfig.IMAGE_BASE_URL + "post/" + document.getFilename();
                    }
                }
            }
            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .placeholder(R.drawable.progress_animation)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .priority(Priority.HIGH)
                    .dontAnimate()
                    .dontTransform();
            RequestBuilder thumbnailRequest = Glide
                    .with( context )
                    .load(turl);
            Glide.with(context)
                    .load(url)
                    .thumbnail(thumbnailRequest)
                    .transition(DrawableTransitionOptions.withCrossFade()).apply(options)
                    .into(viewHolder.autoSliderImage);
            viewHolder.autoSliderImage.setOnClickListener(v -> {
                String type = "";
                if ("albums".equals(postData.getPublish_type())) {
                    type = "albums";
                }
                Intent intent = new Intent(context, PostDetailActivity.class).putExtra(DATA, new Gson().toJson(postData.getPost_attachment())).putExtra("pos", position).putExtra(Constants.Bundle.DETAIL, type);
                ActivityOptionsCompat activityOptions;
                if (context instanceof FamilyDashboardActivity) {
                    activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(((FamilyDashboardActivity) context),
                            new Pair<>(viewHolder.autoSliderImage, PostDetailActivity.VIEW_NAME_HEADER_IMAGE));
                    ActivityCompat.startActivity(context, intent, activityOptions.toBundle());
                } else if (context instanceof MainActivity) {
                    activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(((MainActivity) context),
                            new Pair<>(viewHolder.autoSliderImage, PostDetailActivity.VIEW_NAME_HEADER_IMAGE));
                    ActivityCompat.startActivity(context, intent, activityOptions.toBundle());
                } else if (context instanceof TopicsListingActivity) {
                    activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(((TopicsListingActivity) context),
                            new Pair<>(viewHolder.autoSliderImage, PostDetailActivity.VIEW_NAME_HEADER_IMAGE));
                    ActivityCompat.startActivity(context, intent, activityOptions.toBundle());
                } else {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }

            });
        } else if (document.getType().contains("video")) {
            viewHolder.doc.setVisibility(View.GONE);
            viewHolder.video_back_ground.setVisibility(View.VISIBLE);
            viewHolder.autoSliderImage.setVisibility(View.GONE);
            viewHolder.web.setVisibility(View.VISIBLE);
            viewHolder.audio_back_ground.setVisibility(View.GONE);
            String url = IMAGE_BASE_URL + "post/" + document.getFilename();

            if (postData.getPublish_type() != null && "albums".equals(postData.getPublish_type())) {
                url = IMAGE_BASE_URL + "Documents/" + document.getFilename();
            }

            String previwurl = IMAGE_BASE_URL + "default_video.jpg";
            if (document.getVideo_thumb() != null && !document.getVideo_thumb().equals("")) {
                previwurl = IMAGE_BASE_URL + document.getVideo_thumb();
            }
            String sourc = "<source src=\"" + url + "\"  type=\"video/mp4\">";
            String html = "<html><body><video width=\"100%\" height=\"100%\" poster=\"" + previwurl + "\"controls=\"controls\" controlsList=\"nodownload\" >" + sourc + "</video></body></html>";
            viewHolder.web.setBackgroundColor(Color.TRANSPARENT);
            viewHolder.webChromeClient = new VideoEnabledWebChromeClient(viewHolder.video_back_ground, viewHolder.video_back_ground) // See all available constructors...
            {
                // Subscribe to standard events, such as onProgressChanged()...
                @Override
                public void onProgressChanged(WebView view, int progress) {
                    // Your code...
                }
            };
            String type = "";
            if ("albums".equals(postData.getPublish_type())) {
                type = "albums";
            }
            String finalType = type;
            viewHolder.webChromeClient.setOnToggledFullscreen(fullscreen -> context.startActivity(new Intent(context, PostDetailActivity.class).putExtra(DATA, new Gson().toJson(postData.getPost_attachment())).putExtra("pos", position).putExtra(Constants.Bundle.DETAIL, finalType)));
            //  Log.e("HTML", html);
            viewHolder.web.setWebChromeClient(viewHolder.webChromeClient);
            viewHolder.web.loadData(html, "text/html", "UTF-8");

        }
        // For play audio(Dinu)
        else if (document.getType().contains("audio")||document.getType().contains("mp3")) {
            viewHolder.doc.setVisibility(View.GONE);
            viewHolder.audio_back_ground.setVisibility(View.VISIBLE);
            viewHolder.video_back_ground.setVisibility(View.GONE);
            viewHolder.autoSliderImage.setVisibility(View.GONE);
            viewHolder.web.setVisibility(View.GONE);
            String url = IMAGE_BASE_URL + "post/" + document.getFilename();
            String fileName=document.getFilename();

            String source = "<source src=\"" + url + "\"  type=\"audio/mpeg\">";
            String html = "<html><body><audio  controls>" + source + "</audio></body></html>";
            viewHolder.webviewaudio.setBackgroundColor(Color.TRANSPARENT);
            viewHolder.webviewaudio.loadData(html, "text/html", "UTF-8");
            viewHolder.webviewaudio.setDownloadListener(new DownloadListener() {
                @Override
                public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {

                    if (isReadStoragePermissionGranted()) {

                        Toast.makeText(context, "Downloading media...", Toast.LENGTH_SHORT).show();
                        Utilities.downloadDocuments(context, url,fileName);
                    } else
                        showPermission();
                }
            });


            viewHolder.webviewaudio.setOnFocusChangeListener(new View.OnFocusChangeListener(){
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(!hasFocus){
                        viewHolder.webviewaudio.setBackgroundColor(Color.TRANSPARENT);
                        viewHolder.webviewaudio.loadData(html, "text/html", "UTF-8");
                    }
                }
            });


            viewHolder.audio_back_ground.setOnTouchListener(new View.OnTouchListener() {
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
                                    viewHolder.webviewaudio.setBackgroundColor(Color.TRANSPARENT);
                                    viewHolder.webviewaudio.loadData(html, "text/html", "UTF-8");
                                }
                                if (deltaX > 0) {

                                    viewHolder.webviewaudio.setBackgroundColor(Color.TRANSPARENT);
                                    viewHolder.webviewaudio.loadData(html, "text/html", "UTF-8");
                                }
                            }
                            break;
                    }
                    return true;
                }
              });
        }
        else {
            viewHolder.doc.setVisibility(View.VISIBLE);
            if(document.getOriginal_name()!=null&&!document.getOriginal_name().isEmpty()){
                viewHolder.txt_doc_name.setText(document.getOriginal_name());
            }else{
                viewHolder.txt_doc_name.setText(document.getFilename());
            }
            if(document.getType().contains("pdf")){
                viewHolder.img_doc.setImageResource(R.drawable.pdf);
            }
            else if(document.getType().contains("xls")|| document.getType().contains("excel")||document.getType().contains("sheet"))
            {
                viewHolder.img_doc.setImageResource(R.drawable.ms_excel);
            }
            else if(document.getType().contains("ppt")||document.getType().contains("presentation")||document.getType().contains("powerpoint"))
            {
                viewHolder.img_doc.setImageResource(R.drawable.ms_powerpoint);
            }
            else if(document.getType().contains("doc")||document.getType().contains("word")  && (!document.getType().contains("presentation") || !document.getType().contains("sheet") ||!document.getType().contains("xls")))

            {
                viewHolder.img_doc.setImageResource(R.drawable.ms_word);
            }
            else if(document.getType().contains("zip")||document.getType().contains("rar")||document.getType().contains("octet-stream"))
            {
                viewHolder.img_doc.setImageResource(R.drawable.zip);
            }
            else
            {
                viewHolder.img_doc.setImageResource(R.drawable.document_default_item);
            }

            viewHolder.audio_back_ground.setVisibility(View.GONE);
            viewHolder.autoSliderImage.setVisibility(View.GONE);
            //viewHolder.videoView.setVisibility(View.GONE);
            viewHolder.video_back_ground.setVisibility(View.GONE);
            viewHolder.doc.setOnClickListener(v -> {
                String url = IMAGE_BASE_URL + "post/" + document.getFilename();

                if (postData.getPublish_type() != null && "documents".equals(postData.getPublish_type())) {
                    url = IMAGE_BASE_URL + "Documents/" + document.getFilename();
                }

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                viewHolder.itemView.getContext().startActivity(browserIntent);
            });
        }

    }




    @Override
    public int getCount() {
        return postData.getPost_attachment().size();
    }
    private boolean isReadStoragePermissionGranted() {
        return TedPermission.isGranted(context.getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }
    private void showPermission() {

        Dialog dialog = new Dialog(context);
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
    private void requestPermission() {
        TedPermission.with(context)
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                       // audiodownload.performClick();
                    }

                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {

                    }
                })
                .setDeniedMessage("If you reject permission,you can not download images\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();
    }

    static class SliderAdapterVH extends SliderViewAdapter.ViewHolder {

        private VideoEnabledWebChromeClient webChromeClient;

        View itemView;

        @BindView(R.id.web)
        VideoEnabledWebView web;

        @BindView(R.id.iv_auto_image_slider)
        ImageView autoSliderImage;

        @BindView(R.id.doc)
        RelativeLayout doc;

        @BindView(R.id.txt_doc_name)
        TextView txt_doc_name;

        @BindView(R.id.doc_view)
        TextView doc_view;

       /* @BindView(R.id.video_view)
        VideoView videoView;*/

        @BindView(R.id.video_back_ground)
        RelativeLayout video_back_ground;

        @BindView(R.id.audio_back_ground)
        LinearLayout audio_back_ground;

        @BindView(R.id.webview_audio)
        WebView webviewaudio;
        @BindView(R.id.img_doc)
        ImageView img_doc;

        private MediaPlayer mediaPlayer;

        public SliderAdapterVH(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.itemView = itemView;
        }

    }



}