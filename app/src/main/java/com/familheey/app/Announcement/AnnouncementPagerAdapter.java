package com.familheey.app.Announcement;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.familheey.app.Activities.ChatActivity;
import com.familheey.app.Activities.SharelistActivity;
import com.familheey.app.FamilheeyApplication;
import com.familheey.app.Fragments.Posts.PostData;
import com.familheey.app.Networking.Retrofit.ApiServices;
import com.familheey.app.Networking.Retrofit.RetrofitBase;
import com.familheey.app.Post.PostCommentActivity;
import com.familheey.app.Post.PostDetailActivity;
import com.familheey.app.R;
import com.familheey.app.Utilities.Constants;
import com.familheey.app.Utilities.SharedPref;
import com.familheey.app.Utilities.Utilities;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.luseen.autolinklibrary.AutoLinkMode;
import com.volokh.danylo.hashtaghelper.HashTagHelper;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import static com.familheey.app.Utilities.Constants.ApiPaths.IMAGE_BASE_URL;
import static com.familheey.app.Utilities.Constants.Bundle.DATA;
import static com.familheey.app.Utilities.Constants.Bundle.SUB_TYPE;
import static com.familheey.app.Utilities.Constants.Bundle.TYPE;

public class AnnouncementPagerAdapter extends PagerAdapter {
    Context context;
    ArrayList<PostData> postDatas;
    public CompositeDisposable subscriptions;

    private HashTagHelper mTextHashTagHelper;
    private RequestOptions requestOptions;

    public AnnouncementPagerAdapter(Context context, ArrayList<PostData> postDatas) {
        this.postDatas = postDatas;
        this.context = context;
        requestOptions = new RequestOptions();
        requestOptions.transforms(new RoundedCorners(16));
        subscriptions = new CompositeDisposable();
    }


    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        PostData postData = postDatas.get(position);
        LayoutInflater inflater = LayoutInflater.from(context);
        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.item_announcement, container, false);
        layout.findViewById(R.id.btn_close).setOnClickListener(v -> {
            if (context instanceof AnnouncementDetailActivity) {
                ((AnnouncementDetailActivity) context).onBackPressed();
            } else {
                ((AnnouncementFragmentDetailActivity) context).onBackPressed();
            }
        });

       // com.luseen.autolinklibrary.AutoLinkTextView txt_des_with_image1 = layout.findViewById(R.id.txt_des_with_image);
        TextView txt_des_with_image = layout.findViewById(R.id.txt_des_with_image1);
        TextView txt_less_or_more = layout.findViewById(R.id.txt_less_or_more);
        LinearLayout bottomView_for_des = layout.findViewById(R.id.bottomview_for_des);
        ImageView pro = layout.findViewById(R.id.profileImage);
        TextView postusername = layout.findViewById(R.id.postusername);
        TextView postedgroup = layout.findViewById(R.id.postedgroup);
        TextView postdate = layout.findViewById(R.id.postdate);
        com.luseen.autolinklibrary.AutoLinkTextView autoOne = layout.findViewById(R.id.txt_des);
        TextView txt_des = layout.findViewById(R.id.txt_des1);
        TextView view_count = layout.findViewById(R.id.view_count);
        TextView txt_count = layout.findViewById(R.id.txt_count);
        ImageView img_con = layout.findViewById(R.id.img_con);
        ImageView doc_icon = layout.findViewById(R.id.doc);

        ImageView imgviw = layout.findViewById(R.id.imgview);
        ImageView doc = layout.findViewById(R.id.doc);
        ImageView img_preview = layout.findViewById(R.id.img_preview);
        ImageView video_view = layout.findViewById(R.id.video_view);
        ProgressBar progressBar = layout.findViewById(R.id.progressBar);
        RelativeLayout vide_container=layout.findViewById(R.id.vide_container);
        Glide.with(context)
                .load(Constants.ApiPaths.IMAGE_BASE_URL + Constants.Paths.LOGO + postData.getFamily_logo())
                .placeholder(R.drawable.avatar_male)
                .apply(requestOptions).into(pro);
        postusername.setText(postData.getGroup_name());
        postedgroup.setText("Posted by " + postData.getCreated_user_name());
        postdate.setText(dateFormat(postData.getCreatedAt()));
if(postData.getPost_attachment().size()>0) {
    if (postData.getPost_attachment().get(0).getType().contains("pdf")) {
        doc_icon.setImageResource(R.drawable.pdf);
    } else if (postData.getPost_attachment().get(0).getType().contains("doc")||postData.getPost_attachment().get(0).getType().contains("word")&&(!postData.getPost_attachment().get(0).getType().contains("presentation")||!postData.getPost_attachment().get(0).getType().contains("sheet"))) {
        doc_icon.setImageResource(R.drawable.ms_word);
    } else if (postData.getPost_attachment().get(0).getType().contains("xls") || postData.getPost_attachment().get(0).getType().contains("excel")|| postData.getPost_attachment().get(0).getType().contains("sheet")) {
        doc_icon.setImageResource(R.drawable.ms_excel);
    } else if (postData.getPost_attachment().get(0).getType().contains("ppt")|| postData.getPost_attachment().get(0).getType().contains("presentation")|| postData.getPost_attachment().get(0).getType().contains("powerpoint")) {
        doc_icon.setImageResource(R.drawable.ms_powerpoint);
    } else if (postData.getPost_attachment().get(0).getType().contains("zip") || postData.getPost_attachment().get(0).getType().contains("rar")|| postData.getPost_attachment().get(0).getType().contains("octet-stream")) {
        doc_icon.setImageResource(R.drawable.zip);
    } else if (postData.getPost_attachment().get(0).getType().contains("audio")) {
        doc_icon.setImageResource(R.drawable.audio);
    }
    else {
        doc_icon.setImageResource(R.drawable.doc);
        doc.setColorFilter( context.getResources().getColor(R.color.white));
    }
}
       /* txt_des_with_image.addAutoLinkMode(
                AutoLinkMode.MODE_URL);
        txt_des_with_image.setUrlModeColor(ContextCompat.getColor(context, R.color.buttoncolor));*/
       /* txt_des.addAutoLinkMode(
                AutoLinkMode.MODE_URL);
        txt_des.setUrlModeColor(ContextCompat.getColor(context, R.color.buttoncolor));*/

        String description=postData.getSnap_description().trim();
        description=description.replaceAll("HTTP:","http:").replaceAll("Http:","http:")
                .replaceAll("Https:","https:")
                .replaceAll("HTTPS:","https:");
        description=description.replaceAll("WWW.","www.").replaceAll("Www.","www.");
        /** modified url code**/
        txt_des.setText(description);
        if(txt_des!=null){
            mTextHashTagHelper = HashTagHelper.Creator.create(ContextCompat.getColor(context,R.color.buttoncolor), null);
            mTextHashTagHelper.handle(txt_des);
            Linkify.addLinks(txt_des, Linkify.ALL);
            txt_des.setLinkTextColor(ContextCompat.getColor(context,R.color.buttoncolor));
        }
        txt_des_with_image.setText(description);
        /**modified url**/
        if(txt_des_with_image!=null){
            mTextHashTagHelper = HashTagHelper.Creator.create(ContextCompat.getColor(context,R.color.buttoncolor), null);
            mTextHashTagHelper.handle(txt_des_with_image);
            Linkify.addLinks(txt_des_with_image, Linkify.ALL);
            txt_des_with_image.setLinkTextColor(ContextCompat.getColor(context,R.color.buttoncolor));
        }


        /*txt_des_with_image.setAutoLinkOnClickListener((autoLinkMode, matchedText) -> {
            if (autoLinkMode == AutoLinkMode.MODE_URL) {
                try {
                    String url = matchedText.trim();
                    if (!url.contains("http")) {
                        url = url.replaceAll("www.", "http://www.");
                    }
                    if (url.contains("blog.familheey.com")) {
                        // Dinu(09/11/2021) for open another app
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        context.startActivity(intent);
                    }
                    else if (url.contains("familheey")) {
                        openAppGetParams(url);
                    } else {
                        // Dinu(15/07/2021) for open another app
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        context.startActivity(intent);
                        //context.startActivity(new Intent(context, BrowserActivity.class).putExtra("URL", url));
                    }
                }
                catch (Exception e){}
            }

        });*/
       /* txt_des.setAutoLinkOnClickListener((autoLinkMode, matchedText) -> {
            if (autoLinkMode == AutoLinkMode.MODE_URL) {
                try {
                    String url = matchedText.trim();
                    if (!url.contains("http")) {
                        url = url.replaceAll("www.", "http://www.");
                    }
                    if (url.contains("blog.familheey.com")) {
                        // Dinu(09/11/2021) for open another app
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        context.startActivity(intent);
                    }
                    else if (url.contains("familheey")) {
                        openAppGetParams(url);
                    } else {
                        // Dinu(15/07/2021) for open another app
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        context.startActivity(intent);
                       // context.startActivity(new Intent(context, BrowserActivity.class).putExtra("URL", url));
                    }
                }
                catch (Exception e){}
            }

        });*/

        String id = postData.getCreated_by() + "";
        if (id.equals(SharedPref.getUserRegistration().getId())) {
            view_count.setText(postData.getViews_count());
            imgviw.setVisibility(View.VISIBLE);
            view_count.setVisibility(View.VISIBLE);
        } else {
            imgviw.setVisibility(View.GONE);
            view_count.setVisibility(View.GONE);
        }

        if (postData.getConversation_enabled()) {
            txt_count.setText(postData.getConversation_count());
            img_con.setVisibility(View.VISIBLE);
            txt_count.setVisibility(View.VISIBLE);
        } else {
            img_con.setVisibility(View.GONE);
            txt_count.setVisibility(View.GONE);
        }

        if (postData.getPost_attachment() != null && postData.getPost_attachment().size() > 0) {
            bottomView_for_des.setVisibility(View.VISIBLE);
            txt_des.setVisibility(View.GONE);
            img_preview.setVisibility(View.VISIBLE);
            if (postData.getPost_attachment().get(0).getType().contains("image")) {
                vide_container.setVisibility(View.GONE);
                doc.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(Constants.ApiPaths.IMAGE_BASE_URL + "post/" + postData.getPost_attachment().get(0).getFilename())
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                progressBar.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                progressBar.setVisibility(View.GONE);
                                return false;
                            }
                        }).into(img_preview);
            }
            else if (postData.getPost_attachment().get(0).getType().contains("video")) {
                progressBar.setVisibility(View.GONE);
                doc.setVisibility(View.GONE);
                vide_container.setVisibility(View.VISIBLE);
                img_preview.setVisibility(View.GONE);

                String previwurl = IMAGE_BASE_URL + "default_video.jpg";
                if (postData.getPost_attachment().get(0).getVideo_thumb() != null && !postData.getPost_attachment().get(0).getVideo_thumb().equals("")) {
                    previwurl = IMAGE_BASE_URL + postData.getPost_attachment().get(0).getVideo_thumb();
                }
                Glide.with(context).load(previwurl).into(video_view);
            } else {
                vide_container.setVisibility(View.GONE);
                doc.setVisibility(View.VISIBLE);
                img_preview.setVisibility(View.GONE);
            }


            if (postData.getSnap_description().length() > 100) {
                txt_less_or_more.setVisibility(View.VISIBLE);
                txt_des_with_image.setMaxLines(2);
                txt_des_with_image.setEllipsize(TextUtils.TruncateAt.END);
                /**modified url**/
                txt_des_with_image.setText(description);
                if(txt_des_with_image!=null){
                    mTextHashTagHelper = HashTagHelper.Creator.create(ContextCompat.getColor(context,R.color.buttoncolor), null);
                    mTextHashTagHelper.handle(txt_des_with_image);
                    Linkify.addLinks(txt_des_with_image, Linkify.ALL);
                    txt_des_with_image.setLinkTextColor(ContextCompat.getColor(context,R.color.buttoncolor));
                }
            } else {
                txt_less_or_more.setVisibility(View.GONE);
                /**modified url**/
                txt_des_with_image.setText(description);
                if(txt_des_with_image!=null){
                    mTextHashTagHelper = HashTagHelper.Creator.create(ContextCompat.getColor(context,R.color.buttoncolor), null);
                    mTextHashTagHelper.handle(txt_des_with_image);
                    Linkify.addLinks(txt_des_with_image, Linkify.ALL);
                    txt_des_with_image.setLinkTextColor(ContextCompat.getColor(context,R.color.buttoncolor));
                }
            }
        } else {
            bottomView_for_des.setVisibility(View.GONE);
            txt_des.setVisibility(View.VISIBLE);
            img_preview.setVisibility(View.GONE);
        }
        img_preview.setOnClickListener(v -> {
            context.startActivity(new Intent(context, PostDetailActivity.class).putExtra(DATA, new Gson().toJson(postData.getPost_attachment())).putExtra("pos", position).putExtra(Constants.Bundle.DETAIL, ""));
        });
        vide_container.setOnClickListener(v -> {
            context.startActivity(new Intent(context, PostDetailActivity.class).putExtra(DATA, new Gson().toJson(postData.getPost_attachment())).putExtra("pos", position).putExtra(Constants.Bundle.DETAIL, ""));
        });
        doc.setOnClickListener(v -> {
            if(postData.getPost_attachment().size()==1){
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(IMAGE_BASE_URL + "post/" + postData.getPost_attachment().get(0).getFilename()));
            context.startActivity(browserIntent);}
            else
                context.startActivity(new Intent(context, PostDetailActivity.class).putExtra(DATA, new Gson().toJson(postData.getPost_attachment())).putExtra("pos", 0).putExtra(Constants.Bundle.DETAIL, ""));

        });
        txt_less_or_more.setOnClickListener(v -> {
            if (txt_less_or_more.getText().equals("Read More")) {
                txt_less_or_more.setText("Read Less");
                /**modified url**/
                txt_des_with_image.setText(postData.getSnap_description());
                if(txt_des_with_image!=null){
                    mTextHashTagHelper = HashTagHelper.Creator.create(ContextCompat.getColor(context,R.color.buttoncolor), null);
                    mTextHashTagHelper.handle(txt_des_with_image);
                    Linkify.addLinks(txt_des_with_image, Linkify.ALL);
                    txt_des_with_image.setLinkTextColor(ContextCompat.getColor(context,R.color.buttoncolor));
                }
                txt_des_with_image.setMaxLines(Integer.MAX_VALUE);
                txt_des_with_image.setEllipsize(null);
            } else {
                txt_less_or_more.setText("Read More");
                txt_des_with_image.setMaxLines(2);
                txt_des_with_image.setEllipsize(TextUtils.TruncateAt.END);
            }
        });
        img_con.setOnClickListener(v -> {
            context.startActivity(new Intent(context, PostCommentActivity.class)
                    .putExtra(DATA, postData)
                    .putExtra(SUB_TYPE, "ANNOUNCEMENT")
                    .putExtra(TYPE, "")
                    .putExtra("POS", position));
        });

        imgviw.setOnClickListener(v -> {
            if (Integer.parseInt(postData.getViews_count()) > 0) {
                context.startActivity(new Intent(context, SharelistActivity.class)
                        .putExtra(Constants.Bundle.TYPE, "POSTVIEW")
                        .putExtra("event_id", postData.getPost_id() + "")
                        .putExtra("user_id", postData.getCreated_by() + ""));
            }
        });
        container.addView(layout);
        return layout;
    }

    @Override
    public int getCount() {
        return postDatas.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    private String dateFormat(String time) {
        DateTime dateTime = ISODateTimeFormat.dateTimeParser().parseDateTime(time);
        DateTimeFormatter dtfOut = DateTimeFormat.forPattern("MMM dd yyyy hh:mm aa");
        return dtfOut.print(dateTime);
    }

    private void openAppGetParams(String url) {
        // UserNotification
        SweetAlertDialog progressDialog = Utilities.getProgressDialog(context);
        progressDialog.show();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("url", url);
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        FamilheeyApplication application = FamilheeyApplication.get(context);
        ApiServices apiServices = RetrofitBase.createRxResource(context, ApiServices.class);
        subscriptions.add(apiServices.openAppGetParams(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(response -> {
                    progressDialog.dismiss();
                    if(response.body()!=null) {
                        if (response.body().getData().getSub_type().equals("family_link")) {
                            response.body().getData().setSub_type("");
                        }
                        response.body().getData().goToCorrespondingDashboard(context);
                    }
                    else{
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        context.startActivity(intent);
                    }
                }, throwable -> progressDialog.dismiss()));
    }

}