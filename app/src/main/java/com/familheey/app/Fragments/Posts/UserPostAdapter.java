package com.familheey.app.Fragments.Posts;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.familheey.app.Activities.CreateAlbumDetailedActivity;
import com.familheey.app.Activities.FamilyDashboardActivity;
import com.familheey.app.Activities.ProfileActivity;
import com.familheey.app.Activities.ShareEventActivity;
import com.familheey.app.Activities.SharelistActivity;
import com.familheey.app.Activities.SubFolderActivity;
import com.familheey.app.BuildConfig;
import com.familheey.app.FamilheeyApplication;
import com.familheey.app.Models.Response.Family;
import com.familheey.app.Models.Response.FamilyMember;
import com.familheey.app.Networking.Retrofit.ApiServices;
import com.familheey.app.Networking.Retrofit.RetrofitBase;
import com.familheey.app.Post.EditPostActivity;
import com.familheey.app.Post.PostAggregateListingActivity;
import com.familheey.app.Post.PostCommentActivity;
import com.familheey.app.R;
import com.familheey.app.Utilities.Constants;
import com.familheey.app.Utilities.SharedPref;
import com.familheey.app.Utilities.Utilities;
import com.familheey.app.pagination.PaginationAdapterCallback;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.luseen.autolinklibrary.AutoLinkMode;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.volokh.danylo.hashtaghelper.HashTagHelper;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import static android.content.Context.WINDOW_SERVICE;
import static com.familheey.app.Utilities.Constants.ApiPaths.FIREBASE_DATABASE_URL;
import static com.familheey.app.Utilities.Constants.ApiPaths.IMAGE_BASE_URL;
import static com.familheey.app.Utilities.Constants.Bundle.CAN_CREATE;
import static com.familheey.app.Utilities.Constants.Bundle.CAN_UPDATE;
import static com.familheey.app.Utilities.Constants.Bundle.DATA;
import static com.familheey.app.Utilities.Constants.Bundle.ID;
import static com.familheey.app.Utilities.Constants.Bundle.IDENTIFIER;
import static com.familheey.app.Utilities.Constants.Bundle.IS_ADMIN;
import static com.familheey.app.Utilities.Constants.Bundle.IS_UPDATE_MODE;
import static com.familheey.app.Utilities.Constants.Bundle.SUB_TYPE;
import static com.familheey.app.Utilities.Constants.Bundle.TYPE;

public class UserPostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private static final int ITEM = 0;
    private static final int SHARE = 1;
    private static final int LOADING = 2;
    public CompositeDisposable subscriptions;
    private boolean isLoadingAdded = false;
    private final PaginationAdapterCallback mCallback;
    private final Context context;
    private final List<PostData> repositories;
    private final RequestOptions requestOptions;
    private final OnSearchQueryChangedListener mListener;
    private PostSliderAdapter postSliderAdapter;
    private  RatingBar ratingBar;
    private LayerDrawable stars;
    private String user_id;
    private String starCount;
    private String rateCount;
    private postRating mListener1;
    private int newRating_by_user;
    private boolean isChecked;
    private DatabaseReference database;
    //private HashTagHelper mTextHashTagHelper;
    private postItemClick mListener2;

    public UserPostAdapter(Context context, List<PostData> data, PaginationAdapterCallback mCallback, OnSearchQueryChangedListener mListener,postRating mListener1,postItemClick mListener2) {
        this.repositories = data;
        this.context = context;
        this.mCallback = mCallback;
        requestOptions = new RequestOptions();
        requestOptions.transforms(new RoundedCorners(16));
        subscriptions = new CompositeDisposable();
        this.mListener = mListener;
        this.mListener2=mListener2;
        this.mListener1=mListener1;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == ITEM) {

            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View listItem = layoutInflater.inflate(R.layout.item_myfamily_post, parent, false);
            return new ViewHolderOne(listItem);
        } else if (viewType == LOADING) {

            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View listItem = layoutInflater.inflate(R.layout.item_progress, parent, false);
            return new LoadingVH(listItem);
        } else {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View listItem = layoutInflater.inflate(R.layout.item_myfamily_post_share, parent, false);
            return new ViewHolderTwo(listItem);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        PostData postData = repositories.get(position);

        switch (getItemViewType(position)) {
            case ITEM:
                UserPostAdapter.ViewHolderOne holderOne = (UserPostAdapter.ViewHolderOne) holder;
                holderOne.itemView.setOnClickListener(v -> {
                    if (postData.getAggregated_count() > 1/* && postData.getTo_user_name() == null*/) {
                        context.startActivity(new Intent(context, PostAggregateListingActivity.class).putExtra("id", postData.getPost_ref_id()));
                    }
                });

                /*holderOne.txt_des.addAutoLinkMode(
                        AutoLinkMode.MODE_HASHTAG,
                        AutoLinkMode.MODE_URL);
                holderOne.txt_des.setHashtagModeColor(ContextCompat.getColor(context, R.color.buttoncolor));
                holderOne.txt_des.setUrlModeColor(ContextCompat.getColor(context, R.color.buttoncolor));*/

                String id = postData.getCreated_by() + "";
                Glide.with(holder.itemView.getContext())
                        .load(IMAGE_BASE_URL + Constants.Paths.PROFILE_PIC + repositories.get(position).getPropic())
                        .placeholder(R.drawable.avatar_male)
                        .apply(requestOptions).into(holderOne.profileImage);

                holderOne.postusername.setText(postData.getCreated_user_name());


                if (postData.getValid_urls() != null && postData.getValid_urls().size() == 1 && postData.getPost_attachment() == null || postData.getPost_attachment().size() == 0) {

                    if (postData.getUrl_metadata() != null) {
                        holderOne.album_view.setVisibility(View.GONE);
                        holderOne.sliderView.setVisibility(View.GONE);
                        holderOne.thanks_post_view.setVisibility(View.GONE);
                        holderOne.link_preview.setVisibility(View.GONE);
                        holderOne.txt_less_or_more.setVisibility(View.VISIBLE);
                        holderOne.middle_view.setVisibility(View.VISIBLE);
                        holderOne.middle_first_view.setVisibility(View.VISIBLE);
                        holderOne.txt_temp.setText(postData.getSnap_description().trim());
                        if (postData.getUrl_metadata().getUrlMetadataResult() != null) {
                            holderOne.link_preview.setVisibility(View.VISIBLE);
                            String t = postData.getUrl_metadata().getUrlMetadataResult().getTitle();
                            String d = postData.getUrl_metadata().getUrlMetadataResult().getDescription();
                            if (t != null && !t.isEmpty()) {
                                holderOne.txt_url_des.setText(t);
                            } else if (d != null && !d.isEmpty()) {
                                holderOne.txt_url_des.setText(t);
                            }
                            holderOne.txt_url.setText(postData.getUrl_metadata().getUrlMetadataResult().getUrl());

                            String url = postData.getUrl_metadata().getUrlMetadataResult().getImage() + "";
                            if (!url.contains("http")) {
                                url = postData.getUrl_metadata().getUrlMetadataResult().getUrl() + url;
                            }
                            if (!url.isEmpty() && url.length() > 5) {
                                holderOne.url_img.setScaleType(ImageView.ScaleType.CENTER);
                                Glide.with(holder.itemView.getContext())
                                        .load(url)
                                        .placeholder(R.drawable.d_image)
                                        .apply(Utilities.getCurvedRequestOptions()).into(holderOne.url_img);
                            } else {
                                holderOne.url_img.setScaleType(ImageView.ScaleType.FIT_XY);
                            }
                            holderOne.link_preview.setOnClickListener(view -> {
                                try {
                                    String urls = postData.getValid_urls().get(0).trim();
                                    if (!urls.contains("http")) {
                                        urls = urls.replaceAll("www.", "http://www.");
                                    }
                                    if (urls.contains("blog.familheey.com")) {
                                        // Dinu(09/11/2021) for open another app
                                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urls));
                                        context.startActivity(intent);
                                    }
                                    else if (urls.contains("familheey")) {
                                        openAppGetParams(urls);
                                    } else {
                                        // Dinu(15/07/2021) for open another app
                                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urls));
                                        context.startActivity(intent);
                                      //  context.startActivity(new Intent(context, BrowserActivity.class).putExtra("URL", urls));
                                    }
                                } catch (Exception e) {
                                }
                            });
                        }
                    }

                    /*if (postData.getUrl_metadata() != null) {

                        holderOne.sliderView.setVisibility(View.GONE);
                        holderOne.thanks_post_view.setVisibility(View.GONE);
                        if (postData.getUrl_metadata().getUrlMetadataResult() != null) {
                            holderOne.link_preview.setVisibility(View.VISIBLE);
                            String t = postData.getUrl_metadata().getUrlMetadataResult().getTitle();
                            String d = postData.getUrl_metadata().getUrlMetadataResult().getDescription();
                            if (t != null && !t.isEmpty()) {
                                holderOne.txt_url_des.setText(t);
                            } else if (d != null && !d.isEmpty()) {
                                holderOne.txt_url_des.setText(t);
                            }
                            holderOne.txt_url.setText(postData.getUrl_metadata().getUrlMetadataResult().getUrl());

                            String url = postData.getUrl_metadata().getUrlMetadataResult().getImage() + "";
                            if (!url.contains("http")) {
                                url = postData.getUrl_metadata().getUrlMetadataResult().getUrl() + url;
                            }
                            if (!url.isEmpty() && url.length() > 5) {
                                holderOne.url_img.setScaleType(ImageView.ScaleType.CENTER);
                                Glide.with(holder.itemView.getContext())
                                        .load(url)
                                        .placeholder(R.drawable.d_image)
                                        .apply(Utilities.getCurvedRequestOptions()).into(holderOne.url_img);
                            } else {
                                holderOne.url_img.setScaleType(ImageView.ScaleType.FIT_XY);
                            }
                            holderOne.link_preview.setOnClickListener(view -> {
                                try {
                                    String urls = postData.getValid_urls().get(0).trim();
                                    if (!urls.contains("http")) {
                                        urls = urls.replaceAll("www.", "http://www.");
                                    }
                                    Uri uri = Uri.parse(urls);
                                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                    context.startActivity(intent);
                                } catch (Exception e) {
                                }
                            });
                        }
                    }*/
                } else if (postData.getPublish_type() != null && "request".equals(postData.getPublish_type())) {

                    holderOne.link_preview.setVisibility(View.GONE);
                    holderOne.sliderView.setVisibility(View.GONE);
                    holderOne.txt_less_or_more.setVisibility(View.GONE);
                    holderOne.txt_des.setVisibility(View.GONE);
                    holderOne.middle_first_view.setVisibility(View.VISIBLE);
                    holderOne.thanks_post_view.setVisibility(View.VISIBLE);
                    holderOne.text_description.setText(postData.getSnap_description());
                    holderOne.middle_view.setVisibility(View.VISIBLE);
                    holderOne.album_view.setVisibility(View.GONE);
                    holderOne.txt_less_or_more.setVisibility(View.GONE);
                    if (postData.getPublish_mention_items() != null && postData.getPublish_mention_items().size() > 0)
                        holderOne.txt_request_item.setText(postData.getPublish_mention_items().get(0).getItem_name());
                    Glide.with(holder.itemView.getContext())
                            .load(BuildConfig.IMAGE_BASE_URL + "post/" + postData.getPost_attachment().get(0).getFilename())
                            .into(holderOne.thanks_post_img);
                    if (postData.getPublish_mention_users() != null) {
                        holderOne.rv_name_list.setLayoutManager(new GridLayoutManager(context, 5));
                        holderOne.rv_name_list.setAdapter(new MensionNameAdapter(postData.getPublish_mention_users(), context));
                    }
                    /*
                    holderOne.link_preview.setVisibility(View.GONE);
                    holderOne.sliderView.setVisibility(View.GONE);
                    holderOne.txt_less_or_more.setVisibility(View.GONE);
                    holderOne.txt_des.setVisibility(View.GONE);
                    holderOne.thanks_post_view.setVisibility(View.VISIBLE);
                    holderOne.text_description.setText(postData.getSnap_description());

                    if (postData.getPublish_mention_items() != null && postData.getPublish_mention_items().size() > 0)
                        holderOne.txt_request_item.setText(postData.getPublish_mention_items().get(0).getItem_name());
                    Glide.with(holder.itemView.getContext())
                            .load(BuildConfig.IMAGE_BASE_URL + "post/" + postData.getPost_attachment().get(0).getFilename())
                            .into(holderOne.thanks_post_img);

                    holderOne.rv_name_list.setLayoutManager(new GridLayoutManager(context, 6));
                    holderOne.rv_name_list.setAdapter(new MensionNameAdapter(postData.getPublish_mention_users(), context));
*/

                } else {
/*
                    holderOne.thanks_post_view.setVisibility(View.GONE);
                    holderOne.link_preview.setVisibility(View.GONE);
                    if (postData.getPost_attachment() != null && postData.getPost_attachment().size() > 0) {
                        holderOne.sliderView.setVisibility(View.VISIBLE);
                        if (postData.getPost_attachment().get(0).getType().contains("image")) {
                            if (postData.getPost_attachment().get(0).getWidth() != null && postData.getPost_attachment().get(0).getHeight() != null) {
                                ViewGroup.LayoutParams params = holderOne.sliderView.getLayoutParams();
                                params.height = getwidgetsize(postData.getPost_attachment().get(0).getWidth(), postData.getPost_attachment().get(0).getHeight());
                                postData.getPost_attachment().get(0).setHeight1(params.height + "");
                                holderOne.sliderView.setLayoutParams(params);
                            } else {
                                ViewGroup.LayoutParams params = holderOne.sliderView.getLayoutParams();
                                params.height = getwidgetsize();
                                holderOne.sliderView.setLayoutParams(params);
                            }
                        } else if (postData.getPost_attachment().get(0).getType().contains("video")) {
                            ViewGroup.LayoutParams params = holderOne.sliderView.getLayoutParams();
                            params.height = 850;
                            holderOne.sliderView.setLayoutParams(params);
                        } else {
                            ViewGroup.LayoutParams params = holderOne.sliderView.getLayoutParams();
                            params.height = 600;
                            holderOne.sliderView.setLayoutParams(params);
                        }
                        PostSliderAdapter adapter = new PostSliderAdapter(context, postData);
                        holderOne.sliderView.setSliderAdapter(adapter);

                        holderOne.sliderView.setIndicatorAnimation(IndicatorAnimations.THIN_WORM);
                        holderOne.sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
                    } else {
                        holderOne.sliderView.setVisibility(View.GONE);
                    }*/

                    holderOne.middle_first_view.setVisibility(View.GONE);
                    holderOne.thanks_post_view.setVisibility(View.GONE);
                    holderOne.link_preview.setVisibility(View.GONE);
                    holderOne.sliderView.setVisibility(View.GONE);
                    holderOne.txt_less_or_more.setVisibility(View.VISIBLE);
                    if (postData.getPublish_type() != null && "albums".equals(postData.getPublish_type()) || "documents".equals(postData.getPublish_type())) {
                        holderOne.middle_view.setVisibility(View.GONE);
                        holderOne.album_view.setVisibility(View.VISIBLE);
                        holderOne.txt_album_des.setText(postData.getSnap_description().trim());

                        if ("documents".equals(postData.getPublish_type())) {
                            holderOne.btn_open_album.setText("Open Folder");
                        } else {
                            holderOne.btn_open_album.setText("Open Album");
                        }

                        holderOne.btn_open_album.setOnClickListener(view -> {
                            Boolean s = false;
                            if (id.equals(SharedPref.getUserRegistration().getId())) {
                                s = true;
                            }
                            if ("albums".equals(postData.getPublish_type())) {
                                Bundle args = new Bundle();
                                //args.putParcelable(Constants.Bundle.DATA, folder);
                                args.putBoolean(IS_UPDATE_MODE, true);

                                args.putBoolean(IS_ADMIN, s);
                                args.putBoolean(CAN_CREATE, s);
                                args.putBoolean(CAN_UPDATE, s);
                                args.putInt(TYPE, 1);
                                args.putString(ID, postData.getPublish_id());
                                args.putString(IDENTIFIER, postData.getTo_group_id());
                                args.putString("FROM", "POST");
                                Intent intent = new Intent(context, CreateAlbumDetailedActivity.class);
                                intent.putExtras(args);
                                context.startActivity(intent);
                            } else {
                                Intent intent = new Intent(context, SubFolderActivity.class);
                                intent.putExtra(Constants.Bundle.ID, postData.getTo_group_id());
                                intent.putExtra(Constants.Bundle.FOLDER_ID, postData.getPublish_id());
                                intent.putExtra(Constants.Bundle.TYPE, 1);
                                intent.putExtra(Constants.Bundle.TITLE, "");
                                intent.putExtra(Constants.Bundle.DESCRIPTION, "");
                                intent.putExtra(IS_ADMIN, s);
                                intent.putExtra(CAN_CREATE, s);
                                intent.putExtra(CAN_UPDATE, s);
                                context.startActivity(intent);
                            }
                        });
                    } else {
                        holderOne.middle_view.setVisibility(View.VISIBLE);
                        holderOne.album_view.setVisibility(View.GONE);
                        holderOne.txt_temp.setText(postData.getSnap_description().trim());
                    }

                    if (postData.getPost_attachment() != null && postData.getPost_attachment().size() > 0) {
                        holderOne.sliderView.setVisibility(View.VISIBLE);
                        if (postData.getPost_attachment().get(0).getType().contains("image")) {
                            if (postData.getPost_attachment().get(0).getWidth() != null && postData.getPost_attachment().get(0).getHeight() != null) {
                                ViewGroup.LayoutParams params = holderOne.sliderView.getLayoutParams();
                                params.height = getwidgetsize(postData.getPost_attachment().get(0).getWidth(), postData.getPost_attachment().get(0).getHeight());
                                postData.getPost_attachment().get(0).setHeight1(params.height + "");
                                holderOne.sliderView.setLayoutParams(params);
                            } else {
                                ViewGroup.LayoutParams params = holderOne.sliderView.getLayoutParams();
                                params.height = getwidgetsize();
                                holderOne.sliderView.setLayoutParams(params);
                            }
                        } else if (postData.getPost_attachment().get(0).getType().contains("video")) {
                            ViewGroup.LayoutParams params = holderOne.sliderView.getLayoutParams();
                            params.height = 850;
                            holderOne.sliderView.setLayoutParams(params);
                        } else {
                            ViewGroup.LayoutParams params = holderOne.sliderView.getLayoutParams();
                            params.height = 600;
                            holderOne.sliderView.setLayoutParams(params);
                        }

                        PostSliderAdapter adapter = new PostSliderAdapter(context, postData);
                        holderOne.sliderView.setSliderAdapter(adapter);

                        holderOne.sliderView.setIndicatorAnimation(IndicatorAnimations.THIN_WORM);
                        holderOne.sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
                    }
                }


          /*      holderOne.txt_des.setAutoLinkOnClickListener((autoLinkMode, matchedText) -> {

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
                             //   context.startActivity(new Intent(context, BrowserActivity.class).putExtra("URL", url));
                            }
                        } catch (Exception e) {
                        }
                    } else {
                        mListener.onSearchQueryChanged(matchedText.trim());
                    }

                });*/

                if (postData.getGroup_name() != null) {
                    String s = "Posted in " + postData.getGroup_name();
                    if (postData.getSelected_family_count() != null && !postData.getSelected_family_count().isEmpty()) {
                        int fcount = Integer.parseInt(postData.getSelected_family_count());
                        if (fcount > 1) {
                            s += " and " + (fcount - 1) + " other groups";
                        }
                    }
                    holderOne.postedgroup.setText(s);
                    holderOne.postedgroup.setVisibility(View.VISIBLE);
                }
                else if (postData.getTo_user_name() != null) {
                    holderOne.postedgroup.setText("Posted to " + postData.getTo_user_name());
                    holderOne.postedgroup.setVisibility(View.VISIBLE);
                } else {
                    holderOne.postedgroup.setText("Posted in " + postData.getPrivacy_type());
                    holderOne.postedgroup.setVisibility(View.VISIBLE);
                }

                holderOne.txt_temp.setText(postData.getSnap_description().trim());

                holderOne.txt_temp.post(() -> {
                    String description = postData.getSnap_description().trim();
                    description = description.replaceAll("HTTP:", "http:").replaceAll("Http:", "http:")
                            .replaceAll("Https:", "https:")
                            .replaceAll("HTTPS:", "https:");
                    description = description.replaceAll("WWW.", "www.").replaceAll("Www.", "www.");
                    if (holderOne.txt_temp.getLineCount() > 2) {
                        holderOne.txt_less_or_more.setVisibility(View.VISIBLE);
                        holderOne.txt_des.setMaxLines(3);
                        holderOne.txt_des.setEllipsize(TextUtils.TruncateAt.END);
                        holderOne.txt_des.setText(description);
                        /**
                         * @author Devika on 06-12-2021
                         * for managing hashtag click**/
                        if(holderOne.txt_des!=null){
                            holderOne.mTextHashTagHelper1 = HashTagHelper.Creator.create(ContextCompat.getColor(context,R.color.buttoncolor), new HashTagHelper.OnHashTagClickListener() {
                                @Override
                                public void onHashTagClicked(String hashTag) {
                                    String s = "#";
                                    s=s.concat(hashTag);
                                    mListener2.onSearchTag(s);
                                }
                            });
                            holderOne.mTextHashTagHelper1.handle(holderOne.txt_des);
                            Linkify.addLinks(holderOne.txt_des, Linkify.ALL);
                            holderOne.txt_des.setLinkTextColor(ContextCompat.getColor(context,R.color.buttoncolor));
                        }
                    } else {
                        holderOne.txt_less_or_more.setVisibility(View.GONE);
                        holderOne.txt_des.setText(description);
                        /**
                         * @author Devika on 06-12-2021
                         * for managing hashtag click**/
                        if(holderOne.txt_des!=null){
                            holderOne.mTextHashTagHelper1 = HashTagHelper.Creator.create(ContextCompat.getColor(context,R.color.buttoncolor), new HashTagHelper.OnHashTagClickListener() {
                                @Override
                                public void onHashTagClicked(String hashTag) {
                                    String s = "#";
                                    s=s.concat(hashTag);
                                    mListener2.onSearchTag(s);
                                }
                            });



                            holderOne.mTextHashTagHelper1.handle(holderOne.txt_des);
                            Linkify.addLinks(holderOne.txt_des, Linkify.ALL);
                            holderOne.txt_des.setLinkTextColor(ContextCompat.getColor(context,R.color.buttoncolor));
                        }
                    }
                });


                holderOne.postdate.setText(dateFormat(postData.getCreatedAt()));
                holderOne.share_count.setText(postData.getShared_user_count());

//                if (postData.getPrivacy_type().equalsIgnoreCase("private")){
//
//                    holderOne.imgviw.setVisibility(View.GONE);
//                }else{
//
//                    holderOne.imgviw.setVisibility(View.VISIBLE);
//
//                }
                if (id.equals(SharedPref.getUserRegistration().getId()) && !"request".equals(postData.getPublish_type()) && !"albums".equals(postData.getPublish_type()) && !"documents".equals(postData.getPublish_type())) {
                    holderOne.view_count.setText(postData.getViews_count());
                    holderOne.imgviw.setVisibility(View.VISIBLE);
                    holderOne.view_count.setVisibility(View.VISIBLE);
//                    holderOne.settings.setVisibility(View.VISIBLE);
//                    holderOne.settings.setOnClickListener(v -> {
//                        //  (megha,05/08/2021)for popup window
//                        View popupView = LayoutInflater.from(context).inflate(R.layout.popup_post_settings, null);
//                        final PopupWindow popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//                        popupWindow.setOutsideTouchable(true);
//                        popupWindow.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.new_background_rounded));
//
//                        popupWindow.showAtLocation(popupView, Gravity.CENTER_VERTICAL, 0, 400);
//
//                        //(Megha,05/08/2021)dim background
//                        View container = popupWindow.getContentView().getRootView();
//                        if (container != null) {
//                            WindowManager wm = (WindowManager) context.getSystemService(WINDOW_SERVICE);
//                            WindowManager.LayoutParams p = (WindowManager.LayoutParams) container.getLayoutParams();
//                            p.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
//                            p.dimAmount = 0.7f;
//                            if (wm != null) {
//                                wm.updateViewLayout(container, p);
//                            }
//                        }
//
//                        ImageView btnDismiss = (ImageView) popupView.findViewById(R.id.btnDismiss);
//                        btnDismiss.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                popupWindow.dismiss();
//                            }
//                        });
//
//                    });
                    holderOne.imgsha.setVisibility(View.VISIBLE);
                    holderOne.share_count.setVisibility(View.VISIBLE);
                } else {
                    holderOne.imgviw.setVisibility(View.GONE);
                    holderOne.view_count.setVisibility(View.GONE);
                   // holderOne.settings.setVisibility(View.GONE);
                    holderOne.imgsha.setVisibility(View.GONE);
                    holderOne.share_count.setVisibility(View.GONE);
                }

                /*megha(02/09/2021)-> popup for rating bar*/
                if (postData.getRating_enabled()) {
                    holderOne.rating_count.setVisibility(View.VISIBLE);
                    holderOne.rating_count.setText(postData.getRating());
                    holderOne.reviewers_count.setVisibility(View.VISIBLE);
                    holderOne.reviewers_count.setText("["+postData.getRating_count()+" Rating"+"]");
                    holderOne.rating_icon.setOnClickListener(v -> {

                        View popupView = LayoutInflater.from(context).inflate(R.layout.popup_rating_bar, null);
                        final PopupWindow popupWindow = new PopupWindow(popupView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
                        popupWindow.showAsDropDown(holderOne.rating_icon,500,0);
                        popupWindow.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.new_background_rounded));

                        View container = popupWindow.getContentView().getRootView();
                        if (container != null) {
                            WindowManager wm = (WindowManager) context.getSystemService(WINDOW_SERVICE);
                            WindowManager.LayoutParams p = (WindowManager.LayoutParams) container.getLayoutParams();
                            p.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                            p.dimAmount = 0.1f;
                            if (wm != null) {
                                wm.updateViewLayout(container, p);
                            }
                        }
                        ratingBar = (RatingBar) popupView.findViewById(R.id.ratingBar);
                        stars = (LayerDrawable) ratingBar.getProgressDrawable();
                        ratingBar.setIsIndicator(false);
                        String rating_by_user=repositories.get(position).getRating_by_user();
                        if (rating_by_user==null) {
                            rateCount = String.valueOf(ratingBar.getRating());
                            ratingBar.setRating(Float.parseFloat(rateCount));

                        }else{
                            switch (postData.getRating_by_user()) {
                                case "0":
                                    ratingBar.setRating(0);
                                    break;
                                case "1":
                                    ratingBar.setRating(1);
                                    break;
                                case "2":
                                    ratingBar.setRating(2);
                                    break;
                                case "3":
                                    ratingBar.setRating(3);
                                    break;
                                case "4":
                                    ratingBar.setRating(4);
                                    break;
                                case "5":
                                    ratingBar.setRating(5);
                                    break;
                                default:
                                    ratingBar.setRating(Float.parseFloat(starCount));
                                    break;
                            }
                        }

                        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                            @Override
                            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser){
                                JsonObject jsonObject=new JsonObject();
                                user_id=SharedPref.getUserRegistration().getId();
                                starCount = String.valueOf(ratingBar.getRating());
                                jsonObject.addProperty("user_id",user_id);
                                jsonObject.addProperty("post_id",postData.getPost_id().toString());
                                jsonObject.addProperty("type","rating");
                                jsonObject.addProperty("rating",starCount);

                                if (ratingBar.getRating()==0){
                                    jsonObject.addProperty("rating","0");
                                }else if (ratingBar.getRating() == 1) {
                                    jsonObject.addProperty("rating", "1");
                                } else if (ratingBar.getRating() == 2) {
                                    jsonObject.addProperty("rating", "2");
                                } else if (ratingBar.getRating() == 3) {
                                    jsonObject.addProperty("rating", "3");
                                } else if (ratingBar.getRating() == 4) {
                                    jsonObject.addProperty("rating", "4");
                                } else if (ratingBar.getRating() == 5) {
                                    jsonObject.addProperty("rating", "5");
                                }

                                mListener1.onRating(position,jsonObject,String.valueOf((int)ratingBar.getRating()));
                                /*(04/09/21)->for popup delay*/
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {

                                    @Override
                                    public void run() {
                                        popupWindow.dismiss();
                                    }

                                }, 1000);

                            }
                        });
                    });

                }else{
                    holderOne.rating_icon.setVisibility(View.GONE);
                    holderOne.rating_count.setVisibility(View.GONE);
                }

                if (postData.getRating_enabled()){
                    holderOne.rating_count.setText(postData.getRating());
                    holderOne.reviewers_count.setText("["+postData.getRating_count()+" Rating"+"]");
                    holderOne.rating_icon.setVisibility(View.VISIBLE);
                    holderOne.rating_count.setVisibility(View.VISIBLE);
                    holderOne.reviewers_count.setVisibility(View.VISIBLE);
                    try {
                         if(postData.getRating_by_user()==null||postData.getRating_by_user().equals("0")) {
                             holderOne.rating_icon.setVisibility(View.VISIBLE);
                             holderOne.rating_icon.setImageResource(R.drawable.images);
                    }else{
                             holderOne.rating_icon.setVisibility(View.VISIBLE);
                             holderOne.rating_icon.setImageResource(R.drawable.coloured_star);
                    }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else{
                    holderOne.rating_icon.setVisibility(View.GONE);
                    holderOne.rating_count.setVisibility(View.GONE);
                    holderOne.reviewers_count.setVisibility(View.GONE);
                }


                if (postData.getConversation_enabled()) {
                    holderOne.txt_count.setText(postData.getConversation_count());
                    holderOne.img_con.setVisibility(View.VISIBLE);
                    holderOne.txt_count.setVisibility(View.VISIBLE);
                    holderOne.chatview.setVisibility(View.VISIBLE);
                } else {
                    holderOne.img_con.setVisibility(View.GONE);
                    holderOne.txt_count.setVisibility(View.GONE);
                    holderOne.chatview.setVisibility(View.GONE);
                }


                if (postData.getConversation_enabled() && Integer.parseInt(postData.getConversation_count_new()) > 0) {
                    holderOne.unread_status.setVisibility(View.VISIBLE);
                } else {
                    holderOne.unread_status.setVisibility(View.GONE);
                }
                holderOne.txt_less_or_more.setOnClickListener(v -> {

                    String description = postData.getSnap_description().trim();
                    description = description.replaceAll("HTTP:", "http:").replaceAll("Http:", "http:")
                            .replaceAll("Https:", "https:")
                            .replaceAll("HTTPS:", "https:");
                    description = description.replaceAll("WWW.", "www.").replaceAll("Www.", "www.");
                    if (holderOne.txt_less_or_more.getText().equals("More")) {
                        holderOne.txt_less_or_more.setText("Less");
                        holderOne.txt_des.setText(description);
                        /**
                         * @author Devika on 06-12-2021
                         * for managing hashtag click**/
                        if(holderOne.txt_des!=null){
                            holderOne.mTextHashTagHelper1 = HashTagHelper.Creator.create(ContextCompat.getColor(context,R.color.buttoncolor), new HashTagHelper.OnHashTagClickListener() {
                                @Override
                                public void onHashTagClicked(String hashTag) {
                                    String s = "#";
                                    s=s.concat(hashTag);
                                    mListener2.onSearchTag(s);
                                }
                            });

                            holderOne.mTextHashTagHelper1.handle(holderOne.txt_des);
                            Linkify.addLinks(holderOne.txt_des, Linkify.ALL);
                            holderOne.txt_des.setLinkTextColor(ContextCompat.getColor(context,R.color.buttoncolor));
                        }
                        holderOne.txt_des.setMaxLines(Integer.MAX_VALUE);
                        holderOne.txt_des.setEllipsize(null);
                    } else {
                        holderOne.txt_less_or_more.setText("More");
                        holderOne.txt_des.setMaxLines(3);
                        holderOne.txt_des.setEllipsize(TextUtils.TruncateAt.END);
                    }
                });

                holderOne.btn_more.setOnClickListener(v ->
                        {
                            if (postData.getAggregated_count() > 1 && postData.getTo_user_name() == null) {
                                context.startActivity(new Intent(context, PostAggregateListingActivity.class).putExtra("id", postData.getPost_ref_id()));
                            } else {
                                if (id.equals(SharedPref.getUserRegistration().getId())) {
                                    showMenusPostOwner(v, position);
                                } else {
                                    showMenusNormalUser(v, position);
                                }
                            }
                        }
                );

                if (postData.getIs_shareable()) {
                    holderOne.btn_share.setVisibility(View.VISIBLE);
                } else {
                    holderOne.btn_share.setVisibility(View.GONE);
                }

                holderOne.btn_share.setOnClickListener(v -> showMenusShare(v, position));

                holderOne.profileImage.setOnClickListener(v -> {
                    Intent userIntent = new Intent(context, ProfileActivity.class);
                    FamilyMember familyMember = new FamilyMember();
                    familyMember.setUserId(postData.getCreated_by());
                    userIntent.putExtra(DATA, familyMember);
                    context.startActivity(userIntent);
                });

                holderOne.img_con.setOnClickListener(v -> {
                    if (postData.getAggregated_count() > 1 && postData.getTo_user_name() == null) {
                        context.startActivity(new Intent(context, PostAggregateListingActivity.class).putExtra("id", postData.getPost_ref_id()));
                    } else {
                        context.startActivity(new Intent(context, PostCommentActivity.class)
                                .putExtra("POS", position)
                                .putExtra(SUB_TYPE, "POST")
                                .putExtra(TYPE, "0")
                                .putExtra(DATA, postData));
                    }
                });

                holderOne.edtxMessage.setOnClickListener(v -> {
                   /* context.startActivity(new Intent(context, PostCommentActivity.class)
                            .putExtra("POS", position)
                            .putExtra(SUB_TYPE, "POST")
                            .putExtra(DATA, postData)
                            .putExtra(TYPE, ""));*/
                    mListener2.onChatActivity(new Intent(context, PostCommentActivity.class)
                            .putExtra(SUB_TYPE, "POST")
                            .putExtra(TYPE, "1")
                            .putExtra("POS", position)
                            .putExtra(DATA, postData));

                });
                holderOne.imgsha.setOnClickListener(v -> {
                    if (postData.getAggregated_count() > 1 && postData.getTo_user_name() == null) {
                        context.startActivity(new Intent(context, PostAggregateListingActivity.class).putExtra("id", postData.getPost_ref_id()));
                    } else {
                        if (Integer.parseInt(postData.getShared_user_count()) > 0) {
                            String pid;
                            if (postData.getOrgin_id() != null) {
                                pid = postData.getOrgin_id();
                            } else {
                                pid = postData.getPost_id() + "";
                            }
                            context.startActivity(new Intent(context, SharelistActivity.class)
                                    .putExtra(Constants.Bundle.TYPE, "POST")
                                    .putExtra("event_id", pid)
                                    .putExtra("user_id", postData.getCreated_by() + ""));
                        }
                    }
                });


                holderOne.imgviw.setOnClickListener(v -> {
                    if (postData.getAggregated_count() > 1 && postData.getTo_user_name() == null) {
                        context.startActivity(new Intent(context, PostAggregateListingActivity.class).putExtra("id", postData.getPost_ref_id()));
                    } else {
                        if (Integer.parseInt(postData.getViews_count()) > 0) {
                            context.startActivity(new Intent(context, SharelistActivity.class)
                                    .putExtra(Constants.Bundle.TYPE, "POSTVIEW")
                                    .putExtra("event_id", postData.getPost_id() + "")
                                    .putExtra("user_id", postData.getCreated_by() + ""));
                        }
                    }
                });

                if (postData.getSnap_description().equals("")) {
                    holderOne.txt_less_or_more.setVisibility(View.GONE);
                    holderOne.txt_des.setVisibility(View.GONE);
                } else {
                    holderOne.txt_des.setVisibility(View.VISIBLE);

                }
                break;
            case SHARE:

                String ids = postData.getCreated_by() + "";
                ViewHolderTwo holderTwo = (ViewHolderTwo) holder;
                /*holderTwo.txt_des.addAutoLinkMode(
                        AutoLinkMode.MODE_HASHTAG,
                        AutoLinkMode.MODE_URL);
                holderTwo.txt_des.setHashtagModeColor(ContextCompat.getColor(context, R.color.buttoncolor));
                holderTwo.txt_des.setUrlModeColor(ContextCompat.getColor(context, R.color.buttoncolor));
                holderTwo.txt_des.setPhoneModeColor(ContextCompat.getColor(context, R.color.buttoncolor));*/

                holderTwo.txt_less_or_more.setText("More");
          /*      holderTwo.txt_des.setAutoLinkOnClickListener((autoLinkMode, matchedText) -> {

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
                        } catch (Exception e) {
                        }
                    } else {
                        mListener.onSearchQueryChanged(matchedText.trim());
                    }

                });*/
                holderTwo.itemView.setOnClickListener(v -> {
                    if (postData.getAggregated_count() > 1 && postData.getTo_user_name() == null) {
                        context.startActivity(new Intent(context, PostAggregateListingActivity.class).putExtra("id", postData.getPost_ref_id()));
                    }
                });
                if (postData.getCommon_share_count() != null && Integer.parseInt(postData.getCommon_share_count()) > 1) {
                    int c = Integer.parseInt(postData.getCommon_share_count());
                    holderTwo.postusername.setText(postData.getShared_user_name() + " and other " + (c - 1) + " shared a post");
                } else {
                    holderTwo.postusername.setText(postData.getShared_user_name() + " shared a post");
                }
                holderTwo.innerpostusername.setText(postData.getParent_post_created_user_name());
                holderTwo.postedgroup.setVisibility(View.VISIBLE);
                holderTwo.postedgroup.setText("Shared in " + postData.getGroup_name() + "");
                holderTwo.postdate.setText(dateFormat(postData.getCreatedAt()) + "");

                holderTwo.txt_count.setText(postData.getConversation_count());
                holderTwo.share_count.setText(postData.getShared_user_count());
                holderTwo.view_count.setText(postData.getViews_count());



                if (postData.getValid_urls() != null && postData.getValid_urls().size() > 0 && postData.getValid_urls().size() == 1 && postData.getPost_attachment() == null || postData.getPost_attachment().size() == 0) {
                    if (postData.getUrl_metadata() != null) {
                        if (postData.getUrl_metadata().getUrlMetadataResult() != null) {
                            holderTwo.link_preview.setVisibility(View.VISIBLE);
                            String t = postData.getUrl_metadata().getUrlMetadataResult().getTitle();
                            String d = postData.getUrl_metadata().getUrlMetadataResult().getDescription();
                            if (t != null && !t.isEmpty()) {
                                holderTwo.txt_url_des.setText(t);
                            } else if (d != null && !d.isEmpty()) {
                                holderTwo.txt_url_des.setText(t);
                            }
                            holderTwo.txt_url.setText(postData.getUrl_metadata().getUrlMetadataResult().getUrl());

                            String url = postData.getUrl_metadata().getUrlMetadataResult().getImage() + "";
                            if (!url.contains("http")) {
                                url = postData.getUrl_metadata().getUrlMetadataResult().getUrl() + url;
                            }
                            Glide.with(holder.itemView.getContext())
                                    .load(url)
                                    .placeholder(R.drawable.avatar_male)
                                    .into(holderTwo.url_img);

                            holderTwo.link_preview.setOnClickListener(view -> {
                                try {
                                    String urls = postData.getValid_urls().get(0).trim();
                                    if (!urls.contains("http")) {
                                        urls = urls.replaceAll("www.", "http://www.");
                                    }
                                    if (urls.contains("blog.familheey.com")) {
                                        // Dinu(09/11/2021) for open another app
                                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urls));
                                        context.startActivity(intent);
                                    }
                                    else if (urls.contains("familheey")) {
                                        openAppGetParams(urls);
                                    } else {
                                        // Dinu(15/07/2021) for open another app
                                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urls));
                                        context.startActivity(intent);
                                       // context.startActivity(new Intent(context, BrowserActivity.class).putExtra("URL", urls));
                                    }
                                } catch (Exception e) {
                                }
                            });

                        }
                    }
                } else {
                    holderTwo.link_preview.setVisibility(View.GONE);
                }




                        if (postData.getTo_user_name() != null) {
                            holderTwo.postedgroup.setText("Shared to you");
                          }
                if (postData.getParent_post_grp_name() != null) {
                    holderTwo.innerpostedgroup.setText("Posted in " + postData.getParent_post_grp_name());
                } else {
                    holderTwo.innerpostedgroup.setText("Posted in " + postData.getPrivacy_type());
                }
                if (postData.getConversation_enabled() && Integer.parseInt(postData.getConversation_count_new()) > 0) {
                    holderTwo.unread_status.setVisibility(View.VISIBLE);
                } else {
                    holderTwo.unread_status.setVisibility(View.GONE);
                }

                holderTwo.txt_temp.setText(postData.getSnap_description());
                holderTwo.txt_temp.post(() -> {

                    String description = postData.getSnap_description().trim();
                    description = description.replaceAll("HTTP:", "http:").replaceAll("Http:", "http:")
                            .replaceAll("Https:", "https:")
                            .replaceAll("HTTPS:", "https:");
                    description = description.replaceAll("WWW.", "www.").replaceAll("Www.", "www.");
                    if (holderTwo.txt_temp.getLineCount() > 2) {
                        holderTwo.txt_less_or_more.setVisibility(View.VISIBLE);
                        holderTwo.txt_des.setMaxLines(3);
                        holderTwo.txt_des.setEllipsize(TextUtils.TruncateAt.END);
                        holderTwo.txt_des.setText(description);
                        /**
                         * @author Devika on 06-12-2021
                         * for managing hashtag click**/
                        if(holderTwo.txt_des!=null){
                            holderTwo.mTextHashTagHelper2 = HashTagHelper.Creator.create(ContextCompat.getColor(context,R.color.buttoncolor), new HashTagHelper.OnHashTagClickListener() {
                                @Override
                                public void onHashTagClicked(String hashTag) {
                                    String s = "#";
                                    s=s.concat(hashTag);
                                    mListener2.onSearchTag(s);
                                }
                            });


                            holderTwo.mTextHashTagHelper2.handle(holderTwo.txt_des);
                            Linkify.addLinks(holderTwo.txt_des, Linkify.ALL); // linkify all links in text.
                            holderTwo.txt_des.setLinkTextColor(ContextCompat.getColor(context,R.color.buttoncolor));
                        }
                    } else {
                        holderTwo.txt_less_or_more.setVisibility(View.GONE);
                        holderTwo.txt_des.setText(description);
                        /**
                         * @author Devika on 06-12-2021
                         * for managing hashtag click**/
                        if(holderTwo.txt_des!=null){
                            holderTwo.mTextHashTagHelper2 = HashTagHelper.Creator.create(ContextCompat.getColor(context,R.color.buttoncolor), new HashTagHelper.OnHashTagClickListener() {
                                @Override
                                public void onHashTagClicked(String hashTag) {
                                    String s = "#";
                                    s=s.concat(hashTag);
                                    mListener2.onSearchTag(s);
                                }
                            });


                            holderTwo.mTextHashTagHelper2.handle(holderTwo.txt_des);
                            Linkify.addLinks(holderTwo.txt_des, Linkify.ALL); // linkify all links in text.
                            holderTwo.txt_des.setLinkTextColor(ContextCompat.getColor(context,R.color.buttoncolor));
                        }
                    }
                });
                String url = IMAGE_BASE_URL + Constants.Paths.PROFILE_PIC + repositories.get(position).getParent_post_created_propic();
                Log.e("LIJUPS", url);
                Glide.with(holder.itemView.getContext())
                        .load(url)
                        .placeholder(R.drawable.avatar_male)
                        .into(holderTwo.innerprofileImage);

                if (ids.equals(SharedPref.getUserRegistration().getId())) {
                    holderTwo.view_count.setText(postData.getViews_count());
                    holderTwo.imgviw.setVisibility(View.VISIBLE);
                    holderTwo.view_count.setVisibility(View.VISIBLE);
//                    holderTwo.settings.setVisibility(View.VISIBLE);
//                    holderTwo.settings.setOnClickListener(v -> {
//                        //  (megha,05/08/2021)for popup window
//                        View popupView = LayoutInflater.from(context).inflate(R.layout.popup_post_settings, null);
//                        final PopupWindow popupWindow = new PopupWindow(popupView, WindowManager.LayoutParams.TYPE_APPLICATION_PANEL, WindowManager.LayoutParams.WRAP_CONTENT);
//                        popupWindow.setOutsideTouchable(true);
//                        popupWindow.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.new_background_rounded));
//
//                        popupWindow.showAtLocation(popupView, Gravity.CENTER_VERTICAL, 0, 400);
//
//                        //(Megha,05/08/2021)dim background
//                        View container = popupWindow.getContentView().getRootView();
//                        if (container != null) {
//                            WindowManager wm = (WindowManager) context.getSystemService(WINDOW_SERVICE);
//                            WindowManager.LayoutParams p = (WindowManager.LayoutParams) container.getLayoutParams();
//                            p.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
//                            p.dimAmount = 0.7f;
//                            if (wm != null) {
//                                wm.updateViewLayout(container, p);
//                            }
//                        }
//
//                        ImageView btnDismiss = (ImageView) popupView.findViewById(R.id.btnDismiss);
//                        btnDismiss.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                popupWindow.dismiss();
//                            }
//                        });
//
//                    });


                    holderTwo.imgsha.setVisibility(View.VISIBLE);
                    holderTwo.share_count.setVisibility(View.VISIBLE);
                } else {
                    holderTwo.imgviw.setVisibility(View.GONE);
                    holderTwo.view_count.setVisibility(View.GONE);
                   // holderTwo.settings.setVisibility(View.GONE);
                    holderTwo.imgsha.setVisibility(View.GONE);
                    holderTwo.share_count.setVisibility(View.GONE);
                }

                if (postData.getPost_attachment() != null && postData.getPost_attachment().size() > 0) {
                    holderTwo.sliderView.setVisibility(View.VISIBLE);

                    if (postData.getPost_attachment().get(0).getType().contains("image")) {
                        if (postData.getPost_attachment().get(0).getWidth() != null && postData.getPost_attachment().get(0).getHeight() != null) {

                            ViewGroup.LayoutParams params = holderTwo.sliderView.getLayoutParams();
                            params.height = getinnerwidgetsize(postData.getPost_attachment().get(0).getWidth(), postData.getPost_attachment().get(0).getHeight());
                            holderTwo.sliderView.setLayoutParams(params);

                        } else {
                            ViewGroup.LayoutParams params = holderTwo.sliderView.getLayoutParams();
                            params.height = getinnerwidgetsize();
                            holderTwo.sliderView.setLayoutParams(params);
                        }
                    } else if (postData.getPost_attachment().get(0).getType().contains("video")) {
                        ViewGroup.LayoutParams params = holderTwo.sliderView.getLayoutParams();
                        params.height = 850;
                        holderTwo.sliderView.setLayoutParams(params);
                    } else {
                        ViewGroup.LayoutParams params = holderTwo.sliderView.getLayoutParams();
                        params.height = 550;
                        holderTwo.sliderView.setLayoutParams(params);
                    }


                    PostSliderAdapter adapter = new PostSliderAdapter(context, postData);
                    holderTwo.sliderView.setSliderAdapter(adapter);
                    holderTwo.sliderView.setIndicatorAnimation(IndicatorAnimations.THIN_WORM);
                    holderTwo.sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
                } else {
                    holderTwo.sliderView.setVisibility(View.GONE);
                }

                holderTwo.btn_share.setOnClickListener(v -> showMenusShare(v, position));


                holderTwo.img_con.setOnClickListener(v -> {
                    if (postData.getAggregated_count() > 1 && postData.getTo_user_name() == null) {
                        context.startActivity(new Intent(context, PostAggregateListingActivity.class).putExtra("id", postData.getPost_ref_id()));
                    } else
                        context.startActivity(new Intent(context, PostCommentActivity.class)
                                .putExtra("POS", position)
                                .putExtra(SUB_TYPE, "POST")
                                .putExtra(DATA, postData)
                                .putExtra(TYPE, "0"));
                });

                //(megha)chat icon
                holderTwo.edtxMessage.setOnClickListener(v -> {
                    context.startActivity(new Intent(context, PostCommentActivity.class)
                            .putExtra("POS", position)
                            .putExtra(SUB_TYPE, "POST")
                            .putExtra(DATA, postData)
                            .putExtra(TYPE, "1"));

                });
                holderTwo.imgsha.setOnClickListener(v -> {
                    if (postData.getAggregated_count() > 1 && postData.getTo_user_name() == null) {
                        context.startActivity(new Intent(context, PostAggregateListingActivity.class).putExtra("id", postData.getPost_ref_id()));
                    } else if (Integer.parseInt(postData.getShared_user_count()) > 0) {

                        context.startActivity(new Intent(context, SharelistActivity.class)
                                .putExtra(Constants.Bundle.TYPE, "POST")
                                .putExtra("event_id", postData.getOrgin_id() + "")
                                .putExtra("user_id", postData.getCreated_by() + ""));
                    }
                });


                holderTwo.imgviw.setOnClickListener(v -> {
                    if (postData.getAggregated_count() > 1 && postData.getTo_user_name() == null) {
                        context.startActivity(new Intent(context, PostAggregateListingActivity.class).putExtra("id", postData.getPost_ref_id()));
                    } else if (Integer.parseInt(postData.getViews_count()) > 0) {
                        context.startActivity(new Intent(context, SharelistActivity.class)
                                .putExtra(Constants.Bundle.TYPE, "POSTVIEW")
                                .putExtra("event_id", postData.getPost_id() + "")
                                .putExtra("user_id", postData.getCreated_by() + ""));
                    }
                });
                holderTwo.txt_less_or_more.setOnClickListener(v -> {
                    if (holderTwo.txt_less_or_more.getText().equals("More")) {
                        holderTwo.txt_less_or_more.setText("Less");
                        holderTwo.txt_des.setText(postData.getSnap_description());
                        /**
                         * @author Devika on 06-12-2021
                         * for managing hashtag click**/
                        if(holderTwo.txt_des!=null){
                            holderTwo.mTextHashTagHelper2 = HashTagHelper.Creator.create(ContextCompat.getColor(context,R.color.buttoncolor), new HashTagHelper.OnHashTagClickListener() {
                                @Override
                                public void onHashTagClicked(String hashTag) {
                                    String s = "#";
                                    s=s.concat(hashTag);
                                    mListener2.onSearchTag(s);
                                }
                            });


                            holderTwo.mTextHashTagHelper2.handle(holderTwo.txt_des);
                            Linkify.addLinks(holderTwo.txt_des, Linkify.ALL); // linkify all links in text.
                            holderTwo.txt_des.setLinkTextColor(ContextCompat.getColor(context,R.color.buttoncolor));
                        }
                        holderTwo.txt_des.setMaxLines(Integer.MAX_VALUE);
                        holderTwo.txt_des.setEllipsize(null);
                    } else {
                        holderTwo.txt_less_or_more.setText("More");
                        holderTwo.txt_des.setMaxLines(3);
                        holderTwo.txt_des.setEllipsize(TextUtils.TruncateAt.END);
                    }
                });
                holderTwo.btn_more.setOnClickListener(v -> {

                    if (postData.getAggregated_count() > 1 && postData.getTo_user_name() == null) {
                        context.startActivity(new Intent(context, PostAggregateListingActivity.class).putExtra("id", postData.getPost_ref_id()));
                    } else
                        showMenusPostOwnerShare(v, position, postData);
                });

                holderTwo.postedgroup.setOnClickListener(v -> {
                    if(!holderTwo.postedgroup.getText().equals("Shared to you")) {
                        Family family = new Family();
                        family.setId(Integer.parseInt(postData.getTo_group_id()));
                        Intent intent = new Intent(context, FamilyDashboardActivity.class);
                        intent.putExtra(DATA, family);
                        holder.itemView.getContext().startActivity(intent);
                    }
                });
                break;
        }
        /**04/04/2022**/
        // for update read status
        if(postData.getNotification_key()!=null) {
            database = FirebaseDatabase.getInstance(FIREBASE_DATABASE_URL).getReference(Constants.ApiPaths.FIREBASE_USER_TYPE + SharedPref.getUserRegistration().getId() + "_notification");
            database.child(postData.getNotification_key()).child("visible_status").setValue("read");
        }
    }

    @Override
    public int getItemCount() {
        return repositories.size();
    }


    @Override
    public int getItemViewType(int position) {
        PostData postData = repositories.get(position);
        if (postData == null && isLoadingAdded)
            return LOADING;
        else if (postData != null && postData.getShared_user_name() == null)
            return ITEM;
        else return SHARE;
    }


    void addLoadingFooter() {
        isLoadingAdded = true;
        repositories.add(null);
        notifyItemInserted(repositories.size() - 1);
    }

    void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = repositories.size() - 1;
        PostData result = getItem(position);

        if (result == null) {
            repositories.remove(position);
            notifyItemRemoved(position);
        }
    }

    public PostData getItem(int position) {
        return repositories.get(position);
    }

    private String dateFormat(String time) {
        /* megha(06/09/21) for getting post time */
        for (int i=0;i<repositories.size();i++){
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date day = null;
            try {
                day = outputFormat.parse(time);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String formattedDate = outputFormat.format(day);
            String currentDate = new LocalDate().toString();
            if (currentDate.equals(formattedDate)){
                isChecked = true;
                break;
            }else{
                isChecked = false;
            }
        }if (isChecked){
            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            TimeZone tz = TimeZone.getTimeZone("TimeZone.getDefault");
            dateFormatter.setTimeZone(tz);

            try {
                Date date = dateFormatter.parse(time);
                PrettyTime p = new PrettyTime();
                return p.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

        DateTime dateTime = ISODateTimeFormat.dateTimeParser().parseDateTime(time);
        DateTimeFormatter dtfOut = DateTimeFormat.forPattern("MMM dd yyyy hh:mm aa");
        return dtfOut.print(dateTime);

//        DateTime dateTime = ISODateTimeFormat.dateTimeParser().parseDateTime(time);
//        DateTimeFormatter dtfOut = DateTimeFormat.forPattern("MMM dd yyyy hh:mm aa");
//        return dtfOut.print(dateTime);
    }

    private void showMenusPostOwnerShare(View v, int position, PostData postData) {
        PopupMenu popup = new PopupMenu(v.getContext(), v);
        popup.getMenuInflater().inflate(R.menu.popup_menu_post_owner, popup.getMenu());
        if (postData.getMuted() != null && postData.getMuted()) {
            popup.getMenu().getItem(0).setTitle("UnMute");
        } else {
            popup.getMenu().getItem(0).setTitle("Mute");
        }
        popup.getMenu().getItem(1).setVisible(false);
        popup.getMenu().getItem(3).setVisible(false);

        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.mutePost:
                    muteConversation(position);
                    break;
                case R.id.delete:
                    confirmation(position);
                    break;


            }
            return true;
        });

        popup.show();
    }

    private void showMenusNormalUser(View v, int position) {
        PopupMenu popup = new PopupMenu(v.getContext(), v);
        popup.getMenuInflater().inflate(R.menu.popup_menu_post_user, popup.getMenu());
        if (repositories.get(position).getMuted() != null && repositories.get(position).getMuted()) {
            popup.getMenu().getItem(0).setTitle("UnMute");
        } else {
            popup.getMenu().getItem(0).setTitle("Mute");
        }
        popup.getMenu().getItem(3).setVisible(false);
        popup.getMenu().getItem(4).setVisible(false);

        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.mutePost:
                    muteConversation(position);
                    break;
                case R.id.removepost:
                    removePost(repositories.get(position).getPost_id() + "");
                    repositories.remove(position);
                    notifyDataSetChanged();
                    break;
                case R.id.report:

                    break;

                case R.id.delete:
                    confirmation(position);
                    break;

                case R.id.share:
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_SEND);

                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, Constants.ApiPaths.BASE_URL);
                    context.startActivity(Intent.createChooser(intent, "Share"));
                    break;
            }
            return true;
        });

        popup.show();
    }

    private int getinnerwidgetsize() {

        float screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels - 100;
        float s = screenWidth / 4;
        return Math.round(s * 3);
    }

    private int getinnerwidgetsize(String width, String hight) {
        float screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels - 100;
        float wf = Float.parseFloat(width);
        float hf = Float.parseFloat(hight);
        return Math.round((screenWidth / wf) * hf);

    }

    private void showMenusPostOwner(View v, int position) {
        PopupMenu popup = new PopupMenu(v.getContext(), v);
        popup.getMenuInflater().inflate(R.menu.popup_menu_post_owner, popup.getMenu());
        if (repositories.get(position).getMuted() != null && repositories.get(position).getMuted()) {
            popup.getMenu().getItem(0).setTitle("UnMute");
        } else {
            popup.getMenu().getItem(0).setTitle("Mute");
        }


        if (repositories.get(position).getPublish_type() != null && "request".equals(repositories.get(position).getPublish_type()) || "albums".equals(repositories.get(position).getPublish_type()) || "documents".equals(repositories.get(position).getPublish_type())) {
            popup.getMenu().getItem(1).setVisible(false);
            popup.getMenu().getItem(2).setVisible(false);
        }
        popup.getMenu().getItem(3).setVisible(false);

        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.mutePost:
                    muteConversation(position);
                    break;
                case R.id.editpost:

                    /**25-10-21**/
                    mListener2.onEditActivity(new Intent(context, EditPostActivity.class).putExtra("pos", position).putExtra("POST", new Gson().toJson(repositories.get(position))));
                    //context.startActivity(new Intent(context, EditPostActivity.class).putExtra("POST", new Gson().toJson(repositories.get(position))));
                    break;
                case R.id.delete:
                    confirmation(position);
                    break;

                case R.id.share:
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_SEND);

                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, Constants.ApiPaths.BASE_URL);
                    context.startActivity(Intent.createChooser(intent, "Share"));
                    break;
            }
            return true;
        });

        popup.show();
    }


    private void showMenusShare(View v, int position) {
        /*popup for share(10/09/21)*/
        View popupView = LayoutInflater.from(context).inflate(R.layout.popup_share, null);
        final PopupWindow popupWindow = new PopupWindow(popupView,LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(ContextCompat.getDrawable(context,R.drawable.new_background_rounded));
        popupWindow.showAsDropDown(v,500,0);
        View container = popupWindow.getContentView().getRootView();
        if (container != null) {
            WindowManager wm = (WindowManager) context.getSystemService(WINDOW_SERVICE);
            WindowManager.LayoutParams p = (WindowManager.LayoutParams) container.getLayoutParams();
            p.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            p.dimAmount = 0.1f;
            if (wm != null) {
                wm.updateViewLayout(container, p);
            }
        }
        TextView shares = (TextView)popupView.findViewById(R.id.shares);
        TextView socials = (TextView)popupView.findViewById(R.id.socials);
        shares.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, ShareEventActivity.class).putExtra(Constants.Bundle.TYPE, "POST").putExtra("Post_id", repositories.get(position).getPost_id() + ""));
                popupWindow.dismiss();
            }
        });
        socials.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, Constants.ApiPaths.SHARE_URL + "page/posts/" + repositories.get(position).getPost_id());
                context.startActivity(Intent.createChooser(intent, "Share"));
                popupWindow.dismiss();
            }
        });
//        PopupMenu popup = new PopupMenu(v.getContext(), v);
//        popup.getMenuInflater().inflate(R.menu.popup_menu_post_share, popup.getMenu());
//        popup.setOnMenuItemClickListener(item -> {
//            switch (item.getItemId()) {
//                case R.id.sharefamily:
//                    context.startActivity(new Intent(context, ShareEventActivity.class).putExtra(Constants.Bundle.TYPE, "POST").putExtra("Post_id", repositories.get(position).getPost_id() + ""));
//                    break;
//
//
//                case R.id.sharesocial:
//                    Intent intent = new Intent();
//                    intent.setAction(Intent.ACTION_SEND);
//                    intent.setType("text/plain");
//                    intent.putExtra(Intent.EXTRA_TEXT, Constants.ApiPaths.SHARE_URL + "page/posts/" + repositories.get(position).getPost_id());
//                    context.startActivity(Intent.createChooser(intent, "Share"));
//                    break;
//            }
//            return true;
//        });
//
//        popup.show();
    }


    void showRetry(boolean show, @Nullable String errorMsg) {
        notifyItemChanged(repositories.size() - 1);

    }


    private int getwidgetsize() {

        float screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        float s = screenWidth / 4;
        return Math.round(s * 3);
    }

    public int getwidgetsize(String width, String hight) {
        float screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        float wf = Float.parseFloat(width);
        float hf = Float.parseFloat(hight);
        return Math.round((screenWidth / wf) * hf);
    }

    private void deletePost(String id) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", id);
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        FamilheeyApplication application = FamilheeyApplication.get(context);
        ApiServices apiServices = RetrofitBase.createRxResource(context, ApiServices.class);
        subscriptions.add(apiServices.deletePost(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(response -> {
                    notifyDataSetChanged();
                }, throwable -> {
                    /*
                    Not needed
                     */
                }));
    }

    private void muteConversation(int position) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        jsonObject.addProperty("post_id", repositories.get(position).getPost_id() + "");
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        FamilheeyApplication application = FamilheeyApplication.get(context);
        ApiServices apiServices = RetrofitBase.createRxResource(context, ApiServices.class);
        subscriptions.add(apiServices.muteConversation(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(response -> {
                    repositories.get(position).setMuted(response.body().getData().getIs_active());
                    notifyDataSetChanged();
                }, throwable -> {

                }));
    }

    private void removePost(String post_id) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        jsonObject.addProperty("post_id", post_id);
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        FamilheeyApplication application = FamilheeyApplication.get(context);
        ApiServices apiServices = RetrofitBase.createRxResource(context, ApiServices.class);
        subscriptions.add(apiServices.removePost(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(response -> {
                }, throwable -> {
                    /*
                    not needed
                     */
                }));
    }


    private void confirmation(int position) {

        SweetAlertDialog pDialog = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                .setContentText("Are you sure you want to delete this post?")
                .setConfirmText("Yes")
                .setCancelText("No");

        pDialog.show();
        pDialog.setConfirmClickListener(sDialog -> {
            deletePost(repositories.get(position).getPost_id() + "");
            repositories.remove(position);
            notifyDataSetChanged();
            pDialog.cancel();
        });
        pDialog.setCancelClickListener(SweetAlertDialog::cancel);
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


    static class ViewHolderOne extends RecyclerView.ViewHolder {

        @BindView(R.id.chatview)
        RelativeLayout chatview;
        @BindView(R.id.edtxMessage)
        TextView edtxMessage;
        @BindView(R.id.unread_status)
        View unread_status;
        @BindView(R.id.txt_less_or_more)
        TextView txt_less_or_more;
        @BindView(R.id.imgsha)
        ImageView imgsha;
        @BindView(R.id.profileImage)
        ImageView profileImage;
        @BindView(R.id.txt_temp)
        TextView txt_temp;
        @BindView(R.id.postusername)
        TextView postusername;
        @BindView(R.id.postedgroup)
        TextView postedgroup;
        @BindView(R.id.postdate)
        TextView postdate;
        @BindView(R.id.txt_count)
        TextView txt_count;
        @BindView(R.id.img_con)
        ImageView img_con;
        @BindView(R.id.txt_des)
        com.luseen.autolinklibrary.AutoLinkTextView autoTwo;

        @BindView(R.id.txt_des1)
        TextView txt_des;

        @BindView(R.id.imageSlider)
        com.smarteist.autoimageslider.SliderView sliderView;
        @BindView(R.id.view_count)
        TextView view_count;

        @BindView(R.id.middle_first_view)
        LinearLayout middle_first_view;
        @BindView(R.id.imgviw)
        ImageView imgviw;
        @BindView(R.id.btn_more)
        ImageView btn_more;
        @BindView(R.id.btn_share)
        ImageView btn_share;
        @BindView(R.id.share_count)
        TextView share_count;
        @BindView(R.id.bottom_view)
        RelativeLayout bottom_view;
        @BindView(R.id.link_preview)
        LinearLayout link_preview;
        @BindView(R.id.url_img)
        ImageView url_img;
        @BindView(R.id.txt_url_des)
        TextView txt_url_des;
        @BindView(R.id.txt_url)
        TextView txt_url;


        @BindView(R.id.album_view)
        RelativeLayout album_view;

        @BindView(R.id.middle_view)
        LinearLayout middle_view;
        @BindView(R.id.thanks_post_view)
        LinearLayout thanks_post_view;
        @BindView(R.id.thanks_post_img)
        ImageView thanks_post_img;
        @BindView(R.id.text_description)
        TextView text_description;
        @BindView(R.id.txt_request_item)
        TextView txt_request_item;
        @BindView(R.id.btn_open_request)
        TextView btn_open_request;
        @BindView(R.id.rv_name_list)
        RecyclerView rv_name_list;

        @BindView(R.id.txt_album_des)
        TextView txt_album_des;

        @BindView(R.id.btn_open_album)
        TextView btn_open_album;
//        @BindView(R.id.settings)
//        ImageView settings;
        @BindView(R.id.rating_icon)
        ImageView rating_icon;
        @BindView(R.id.rating_count)
        TextView rating_count;
        @BindView(R.id.reviewers_count)
        TextView reviewers_count;
HashTagHelper mTextHashTagHelper1;
        ViewHolderOne(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    static class ViewHolderTwo extends RecyclerView.ViewHolder {
        @BindView(R.id.edtxMessage)
        TextView edtxMessage;
//        @BindView(R.id.settings)
//        ImageView settings;
        @BindView(R.id.unread_status)
        View unread_status;
        @BindView(R.id.txt_less_or_more)
        TextView txt_less_or_more;
        @BindView(R.id.innerprofileImage)
        ImageView innerprofileImage;
        @BindView(R.id.postusername)
        TextView postusername;

        @BindView(R.id.innerpostedgroup)
        TextView innerpostedgroup;
        @BindView(R.id.postedgroup)
        TextView postedgroup;
        @BindView(R.id.innerpostusername)
        TextView innerpostusername;
        @BindView(R.id.postdate)
        TextView postdate;
        @BindView(R.id.txt_count)
        TextView txt_count;

        @BindView(R.id.txt_des)
        com.luseen.autolinklibrary.AutoLinkTextView autoOne;

        @BindView(R.id.txt_temp)
        TextView txt_temp;

        @BindView(R.id.share_count)
        TextView share_count;

        @BindView(R.id.view_count)
        TextView view_count;


        @BindView(R.id.imageSlider)
        com.smarteist.autoimageslider.SliderView sliderView;


        @BindView(R.id.btn_share)
        ImageView btn_share;

        @BindView(R.id.img_con)
        ImageView img_con;

        @BindView(R.id.imgsha)
        ImageView imgsha;


        @BindView(R.id.imgviw)
        ImageView imgviw;
        @BindView(R.id.btn_more)
        ImageView btn_more;

        @BindView(R.id.link_preview)
        LinearLayout link_preview;

        @BindView(R.id.url_img)
        ImageView url_img;

        @BindView(R.id.txt_url_des)
        TextView txt_url_des;

        @BindView(R.id.txt_des1)
        TextView txt_des;

        @BindView(R.id.txt_url)
        TextView txt_url;
        HashTagHelper mTextHashTagHelper2;
        ViewHolderTwo(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    protected class LoadingVH extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ImageButton mRetryBtn;
        private final LinearLayout mErrorLayout;

        LoadingVH(View itemView) {
            super(itemView);

            //    mProgressBar = itemView.findViewById(R.id.loadmore_progress);
            mRetryBtn = itemView.findViewById(R.id.loadmore_retry);
            //  mErrorTxt = itemView.findViewById(R.id.loadmore_errortxt);
            mErrorLayout = itemView.findViewById(R.id.loadmore_errorlayout);

            mRetryBtn.setOnClickListener(this);
            mErrorLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.loadmore_retry:
                case R.id.loadmore_errorlayout:

                    showRetry(false, null);
                    mCallback.retryPageLoad();

                    break;
                default:
                    break;
            }
        }
    }

    public interface OnSearchQueryChangedListener {
        void onSearchQueryChanged(String searchQuery);
    }

    public interface postRating{
        void onRating(int position, JsonObject jsonObject,String rating);
    }
    public interface postItemClick {
        void onEditActivity(Intent intent);

        void onChatActivity(Intent intent);

        void onSearchTag(String hashtag);

       // void makeAsSticky(int poston);

    }
}