package com.familheey.app.Post;

import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.familheey.app.Activities.FamilyDashboardActivity;
import com.familheey.app.Activities.ProfileActivity;
import com.familheey.app.Activities.ShareEventActivity;
import com.familheey.app.Activities.SharelistActivity;
import com.familheey.app.BuildConfig;
import com.familheey.app.FamilheeyApplication;
import com.familheey.app.Fragments.Posts.MensionNameAdapter;
import com.familheey.app.Fragments.Posts.PostData;
import com.familheey.app.Fragments.Posts.PostSliderAdapter;
import com.familheey.app.Models.Response.Family;
import com.familheey.app.Models.Response.FamilyMember;
import com.familheey.app.Need.NeedRequestDetailedActivity;
import com.familheey.app.Networking.Retrofit.ApiServices;
import com.familheey.app.Networking.Retrofit.RetrofitBase;
import com.familheey.app.R;
import com.familheey.app.Topic.MainActivity;
import com.familheey.app.Utilities.Constants;
import com.familheey.app.Utilities.SharedPref;
import com.familheey.app.Utilities.Utilities;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
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
import java.util.Objects;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import static com.familheey.app.Utilities.Constants.ApiPaths.FIREBASE_DATABASE_URL;
import static com.familheey.app.Utilities.Constants.ApiPaths.IMAGE_BASE_URL;
import static com.familheey.app.Utilities.Constants.Bundle.DATA;
import static com.familheey.app.Utilities.Constants.Bundle.NOTIFICATION_ID;
import static com.familheey.app.Utilities.Constants.Bundle.SUB_TYPE;
import static com.familheey.app.Utilities.Constants.Bundle.TYPE;
public class PostDetailForPushActivity extends AppCompatActivity implements View.OnClickListener {

    public CompositeDisposable subscriptions;
    private PostSliderAdapter adapter;
    private RequestOptions requestOptions;
    private PostData postData;
    String description1 = "";
    private boolean isChecked;
    private List<PostData> repositories;
    private RatingBar ratingBar;
    private LayerDrawable stars;
    private int newRating_by_user;
    private String user_id;
    private String starCount;
    private String rateCount;


    @BindView(R.id.edtxMessage)
    TextView edtxmessage;

    @BindView(R.id.rating_icon)
    ImageView rating_icon;

    @BindView(R.id.rating_count)
    TextView rating_count;

//    @BindView(R.id.settings)
//    ImageView settings;

    @BindView(R.id.link_preview)
    LinearLayout link_preview;

    @BindView(R.id.url_img)
    ImageView url_img;

    @BindView(R.id.txt_url_des)
    TextView txt_url_des;

    @BindView(R.id.txt_url)
    TextView txt_url;

    @BindView(R.id.share_view)
    com.google.android.material.card.MaterialCardView share_view;
    @BindView(R.id.normal_view)
    com.google.android.material.card.MaterialCardView normal_view;


    @BindView(R.id.unread_status)
    View unread_status;
    @BindView(R.id.imgsha)
    ImageView imgsha;
    @BindView(R.id.profileImage)
    ImageView profileImage;
    @BindView(R.id.txt_temp)
    TextView txt_temp;

    @BindView(R.id.share_txt_temp)
    TextView share_txt_temp;

    @BindView(R.id.txt_less_or_more)
    TextView txt_less_or_more;

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
    /*@BindView(R.id.txt_des1)
    AutoLinkTextView txt_des1;*/
    @BindView(R.id.txt_des)
    TextView txt_des;
    @BindView(R.id.btn_more)
    ImageView btn_more;
    @BindView(R.id.imageSlider)
    com.smarteist.autoimageslider.SliderView sliderView;
    @BindView(R.id.view_count)
    TextView view_count;
    @BindView(R.id.imgviw)
    ImageView imgviw;

    @BindView(R.id.btn_share)
    ImageView btn_share;


    @BindView(R.id.share_count)
    TextView share_count;


    @BindView(R.id.share_postusername)
    TextView share_postusername;

    @BindView(R.id.share_postedgroup)
    TextView share_postedgroup;

    @BindView(R.id.share_postdate)
    TextView share_postdate;

    @BindView(R.id.innerprofileImage)
    ImageView innerprofileImage;

    @BindView(R.id.innerpostedgroup)
    TextView innerpostedgroup;

    @BindView(R.id.innerpostusername)
    TextView innerpostusername;

    @BindView(R.id.share_imageSlider)
    com.smarteist.autoimageslider.SliderView share_imageSlider;

   /* @BindView(R.id.share_txt_des1)
    AutoLinkTextView share_txt_des1;*/
    @BindView(R.id.share_txt_des)
    TextView share_txt_des;
    @BindView(R.id.share_txt_less_or_more)
    TextView share_txt_less_or_more;


    @BindView(R.id.share_txt_count)
    TextView share_txt_count;

    @BindView(R.id.share_share_count)
    TextView share_share_count;

    @BindView(R.id.share_view_count)
    TextView share_view_count;

    @BindView(R.id.share_unread_status)
    View share_unread_status;

    @BindView(R.id.share_imgsha)
    ImageView share_imgsha;

    @BindView(R.id.share_imgviw)
    ImageView share_imgviw;

    @BindView(R.id.share_img_con)
    ImageView share_img_con;

    @BindView(R.id.share_btn_share)
    ImageView share_btn_share;
    @BindView(R.id.shimmer_view_container)
    com.facebook.shimmer.ShimmerFrameLayout shimmer_view_container;

    @BindView(R.id.link_preview1)
    LinearLayout link_preview1;

    @BindView(R.id.url_img1)
    ImageView url_img1;

    @BindView(R.id.txt_url_des1)
    TextView txt_url_des1;

    @BindView(R.id.txt_url1)
    TextView txt_url1;
    @BindView(R.id.reviewers_count)
    TextView reviewers_count;


    @BindView(R.id.thanks_post_view)
    LinearLayout thanks_post_view;
    @BindView(R.id.thanks_post_img)
    ImageView thanks_post_img;
    @BindView(R.id.text_description)
    TextView text_description;
    @BindView(R.id.rv_name_list)
    RecyclerView rv_name_list;
    @BindView(R.id.txt_request_item)
    TextView txt_request_item;
    @BindView(R.id.btn_open_request)
    TextView btn_open_request;
    private String notificationId = "";
    private DatabaseReference database;
    HashTagHelper hashTagHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail_for_push);
        ButterKnife.bind(this);
        subscriptions = new CompositeDisposable();
        NotificationManagerCompat.from(this).cancelAll();
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.cancelAll();
        }
        requestOptions = new RequestOptions();
        requestOptions.transforms(new RoundedCorners(16));
        getPost(Objects.requireNonNull(getIntent().getExtras()).getString("ids"));
        findViewById(R.id.onback).setOnClickListener(v -> {
            onBackPressed();
        });
        // Modified By: Dinu(22/02/2021) For update visible_status="read" in firebase
        if (getIntent().hasExtra(NOTIFICATION_ID)) {
            notificationId=getIntent().getStringExtra(NOTIFICATION_ID);
            database= FirebaseDatabase.getInstance(FIREBASE_DATABASE_URL).getReference(Constants.ApiPaths.FIREBASE_USER_TYPE + SharedPref.getUserRegistration().getId() + "_notification");
            database.child(notificationId).child("visible_status").setValue("read");
        }
    }

    private void getPost(String id) {
        shimmer_view_container.setVisibility(View.VISIBLE);
        shimmer_view_container.startShimmer();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("post_id", id);
        jsonObject.addProperty("type", "post");
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        FamilheeyApplication application = FamilheeyApplication.get(this);
        ApiServices apiServices = RetrofitBase.createRxResource(this, ApiServices.class);
        subscriptions.add(apiServices.getMyPost(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(response -> {
                    shimmer_view_container.setVisibility(View.GONE);
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        if (response.body().getData().size() > 0) {
                            postData = response.body().getData().get(0);
                            if (postData.getShared_user_name() == null) {
                                normal_view.setVisibility(View.VISIBLE);
                                if(postData.getRating_enabled()==null){
                                    postData.setRating_enabled(false);
                                }
                                normalViewDataSet(postData);
                            } else {
                                share_view.setVisibility(View.VISIBLE);
                                share_view(postData);
                            }
                            addViewCount(postData.getPost_id() + "");
                            shimmer_view_container.setVisibility(View.GONE);
                        } else {
                            SweetAlertDialog contentNotFoundDialog = Utilities.getContentNotFoundDialog(this);
                            contentNotFoundDialog.setConfirmClickListener(sweetAlertDialog -> {
                                contentNotFoundDialog.cancel();
                                Intent intent = new Intent( this, MainActivity.class );
                                intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                                startActivity( intent );
                            });
                            contentNotFoundDialog.setCanceledOnTouchOutside(false);
                            contentNotFoundDialog.setCancelable(false);
                            contentNotFoundDialog.show();
                            Utilities.addPositiveButtonMargin(contentNotFoundDialog);
                        }
                    } else {
                        SweetAlertDialog contentNotFoundDialog = Utilities.getContentNotFoundDialog(this);
                        contentNotFoundDialog.setConfirmClickListener(sweetAlertDialog -> {
                            contentNotFoundDialog.cancel();
                            Intent intent = new Intent( this, MainActivity.class );
                            intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                            startActivity( intent );
                        });
                        contentNotFoundDialog.setCanceledOnTouchOutside(false);
                        contentNotFoundDialog.setCancelable(false);
                        contentNotFoundDialog.show();
                        Utilities.addPositiveButtonMargin(contentNotFoundDialog);
                    }
                }, throwable -> shimmer_view_container.setVisibility(View.GONE)));
    }

    @Override
    public void onBackPressed() {
        if ("NOTIFICATION".equals(Objects.requireNonNull(getIntent().getExtras()).getString(TYPE))) {
            finish();
        } else {
            startActivity(new Intent(this, MainActivity.class));
        }
        overridePendingTransition(R.anim.left,
                R.anim.right);

    }

    private void share_view(PostData postData) {


        if (postData.getValid_urls() != null && postData.getValid_urls().size() > 0 && postData.getValid_urls().size() == 1 && postData.getPost_attachment() == null || postData.getPost_attachment().size() == 0) {
            if (postData.getUrl_metadata() != null) {
                if (postData.getUrl_metadata().getUrlMetadataResult() != null) {
                    link_preview1.setVisibility(View.VISIBLE);
                    String t = postData.getUrl_metadata().getUrlMetadataResult().getTitle();
                    String d = postData.getUrl_metadata().getUrlMetadataResult().getDescription();
                    if (t != null && !t.isEmpty()) {
                        txt_url_des1.setText(t);
                    } else if (d != null && !d.isEmpty()) {
                        txt_url_des1.setText(t);
                    }
                    txt_url1.setText(postData.getUrl_metadata().getUrlMetadataResult().getUrl());

                    String url = postData.getUrl_metadata().getUrlMetadataResult().getImage() + "";
                    if (!url.contains("http")) {
                        url = postData.getUrl_metadata().getUrlMetadataResult().getUrl() + url;
                    }

                    url_img1.setScaleType(ImageView.ScaleType.CENTER);
                    Glide.with(this)
                            .load(url)
                            .placeholder(R.drawable.d_image)
                            .apply(Utilities.getCurvedRequestOptions())
                            .into(url_img1);

                    link_preview1.setOnClickListener(view -> {
                        try {
                            String urls = postData.getValid_urls().get(0).trim();
                            if (!urls.contains("http")) {
                                urls = urls.replaceAll("www.", "http://www.");
                            }
                            if (urls.contains("blog.familheey.com")) {
                                // Dinu(09/11/2021) for open another app
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urls));
                                startActivity(intent);
                            }
                            else if (urls.contains("familheey")) {
                                openAppGetParams(urls);
                            } else {
                                // Dinu(15/07/2021) for open another app
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urls));
                                startActivity(intent);
                                //   startActivity(new Intent(this, BrowserActivity.class).putExtra("URL", urls));
                            }
                        } catch (Exception e) {
                          /*
                          Not needed
                           */
                        }
                    });

                }
            }
        } else {
            link_preview1.setVisibility(View.GONE);
        }


        description1 = postData.getSnap_description().trim();

        description1 = description1.replaceAll("HTTP:", "http:").replaceAll("Http:", "http:")
                .replaceAll("Https:", "https:")
                .replaceAll("HTTPS:", "https:");
        description1 = description1.replaceAll("WWW.", "www.").replaceAll("Www.", "www.");

       /* share_txt_des.addAutoLinkMode(
                AutoLinkMode.MODE_HASHTAG,
                AutoLinkMode.MODE_URL);
        share_txt_des.setUrlModeColor(ContextCompat.getColor(this, R.color.buttoncolor));
        share_txt_des.setHashtagModeColor(ContextCompat.getColor(this, R.color.buttoncolor));*/
        String id = postData.getCreated_by() + "";

        if (postData.getCommon_share_count() != null && Integer.parseInt(postData.getCommon_share_count()) > 1) {
            int c = Integer.parseInt(postData.getCommon_share_count());
            share_postusername.setText(postData.getShared_user_name() + " and other " + (c - 1) + " shared a post");
        } else {
            share_postusername.setText(postData.getShared_user_name() + " shared a post");
        }


        innerpostusername.setText(postData.getParent_post_created_user_name());

        if (postData.getGroup_name() != null) {
            share_postedgroup.setVisibility(View.VISIBLE);
            share_postedgroup.setText("Shared in " + postData.getGroup_name());
        } else if (postData.getTo_user_name() != null) {
            share_postedgroup.setVisibility(View.VISIBLE);
            share_postedgroup.setText("Shared to you");
        }

        share_postedgroup.setOnClickListener(v -> {
            Family family = new Family();
            family.setId(Integer.parseInt(postData.getTo_group_id()));
            Intent intent = new Intent(this, FamilyDashboardActivity.class);
            intent.putExtra(DATA, family);
            this.startActivity(intent);
        });

        if (postData.getParent_post_grp_name() != null) {
            innerpostedgroup.setText("Posted in " + postData.getParent_post_grp_name());
        } else {
            innerpostedgroup.setText("Posted in " + postData.getPrivacy_type());
        }

        share_postdate.setText(dateFormat(postData.getCreatedAt()));

        share_txt_temp.setText(description1);
        share_txt_temp.post(() -> {
            if (share_txt_temp.getLineCount() > 2) {
                share_txt_less_or_more.setVisibility(View.VISIBLE);
                share_txt_des.setMaxLines(3);
                share_txt_des.setEllipsize(TextUtils.TruncateAt.END);
                /**07-12-2021**/
                share_txt_des.setText(description1);
                if(share_txt_des!=null){
                    hashTagHelper = HashTagHelper.Creator.create(ContextCompat.getColor(this,R.color.buttoncolor), null);
                    hashTagHelper.handle(share_txt_des);
                    Linkify.addLinks(share_txt_des, Linkify.ALL);
                    share_txt_des.setLinkTextColor(ContextCompat.getColor(this,R.color.buttoncolor));
                }
            } else {
                share_txt_less_or_more.setVisibility(View.GONE);
                /**07-12-2021**/
                share_txt_des.setText(description1);
                if(share_txt_des!=null){
                    hashTagHelper = HashTagHelper.Creator.create(ContextCompat.getColor(this,R.color.buttoncolor), null);
                    hashTagHelper.handle(share_txt_des);
                    Linkify.addLinks(share_txt_des, Linkify.ALL);
                    share_txt_des.setLinkTextColor(ContextCompat.getColor(this,R.color.buttoncolor));
                }
            }
        });


        if (postData.getParent_post_created_user_propic() != null && !postData.getParent_post_created_user_propic().isEmpty()) {
            Glide.with(this)
                    .load(IMAGE_BASE_URL + Constants.Paths.PROFILE_PIC + postData.getParent_post_created_user_propic())
                    .placeholder(R.drawable.family_dashboard_background)
                    .into(innerprofileImage);
        } else {
            innerprofileImage.setImageResource(R.drawable.avatar_male);
        }


        if (postData.getPost_attachment() != null && postData.getPost_attachment().size() > 0) {
            share_imageSlider.setVisibility(View.VISIBLE);

            if (postData.getPost_attachment().get(0).getType().contains("image")) {
                if (postData.getPost_attachment().get(0).getWidth() != null && postData.getPost_attachment().get(0).getHeight() != null) {

                    ViewGroup.LayoutParams params = share_imageSlider.getLayoutParams();
                    params.height = getinnerwidgetsize(postData.getPost_attachment().get(0).getWidth(), postData.getPost_attachment().get(0).getHeight());
                    share_imageSlider.setLayoutParams(params);

                } else {
                    ViewGroup.LayoutParams params = share_imageSlider.getLayoutParams();
                    params.height = getinnerwidgetsize();
                    share_imageSlider.setLayoutParams(params);
                }
            } else {

                ViewGroup.LayoutParams params = share_imageSlider.getLayoutParams();
                params.height = 600;
                share_imageSlider.setLayoutParams(params);
            }


            adapter = new PostSliderAdapter(this, postData);
            share_imageSlider.setSliderAdapter(adapter);
            share_imageSlider.setIndicatorAnimation(IndicatorAnimations.THIN_WORM);
            share_imageSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        } else {
            share_imageSlider.setVisibility(View.GONE);
        }


        share_txt_count.setText(postData.getConversation_count());

        share_txt_less_or_more.setOnClickListener(view -> {
            String description = postData.getSnap_description().trim();
            description = description.replaceAll("HTTP:", "http:").replaceAll("Http:", "http:")
                    .replaceAll("Https:", "https:")
                    .replaceAll("HTTPS:", "https:");
            description = description.replaceAll("WWW.", "www.").replaceAll("Www.", "www.");
            if (share_txt_less_or_more.getText().equals("More")) {
                share_txt_less_or_more.setText("Less");
                /**07-12-2021**/
                share_txt_des.setText(description);
                if(share_txt_des!=null){
                    hashTagHelper = HashTagHelper.Creator.create(ContextCompat.getColor(this,R.color.buttoncolor), null);
                    hashTagHelper.handle(share_txt_des);
                    Linkify.addLinks(share_txt_des, Linkify.ALL);
                    share_txt_des.setLinkTextColor(ContextCompat.getColor(this,R.color.buttoncolor));
                }
                share_txt_des.setMaxLines(Integer.MAX_VALUE);
                share_txt_des.setEllipsize(null);
            } else {
                share_txt_less_or_more.setText("More");
                share_txt_des.setMaxLines(3);
                share_txt_des.setEllipsize(TextUtils.TruncateAt.END);
            }
        });
        share_share_count.setText(postData.getShared_user_count());


        if (id.equals(SharedPref.getUserRegistration().getId())) {
            share_view_count.setText(postData.getViews_count());
            share_view_count.setVisibility(View.VISIBLE);
            share_share_count.setVisibility(View.VISIBLE);

            share_imgviw.setVisibility(View.VISIBLE);
            share_imgsha.setVisibility(View.VISIBLE);
        } else {
            share_imgviw.setVisibility(View.GONE);
             share_imgsha.setVisibility(View.GONE);

            share_view_count.setVisibility(View.GONE);
            share_share_count.setVisibility(View.GONE);
        }
        if (postData.getConversation_enabled()) {
            share_txt_count.setText(postData.getConversation_count());
            share_img_con.setVisibility(View.VISIBLE);
            share_txt_count.setVisibility(View.VISIBLE);
        } else {
            share_img_con.setVisibility(View.GONE);
            share_txt_count.setVisibility(View.GONE);
        }

        if (postData.getConversation_enabled() && Integer.parseInt(postData.getConversation_count_new()) > 0) {
            share_unread_status.setVisibility(View.VISIBLE);
        } else {
            share_unread_status.setVisibility(View.GONE);
        }


        share_img_con.setOnClickListener(v -> startActivity(new Intent(this, PostCommentActivity.class)
                .putExtra(DATA, postData)
                .putExtra(TYPE, "")
                .putExtra(SUB_TYPE, "POST")
                .putExtra("POS", 0)));
    }


    private void normalViewDataSet(PostData postData) {
        postdate.setText(dateFormat(postData.getCreatedAt()));
        if (postData.getGroup_name() != null) {
            postedgroup.setVisibility(View.VISIBLE);
            postedgroup.setText("Posted in " + postData.getGroup_name());
        } else {
            postedgroup.setVisibility(View.VISIBLE);
            postedgroup.setText("Posted in " + postData.getPrivacy_type());
        }
        postedgroup.setOnClickListener(v -> {
                Family family = new Family();
                family.setId(Integer.parseInt(postData.getTo_group_id()));
                Intent intent = new Intent(this, FamilyDashboardActivity.class);
                intent.putExtra(DATA, family);
              this.startActivity(intent);
            
        });
        if (postData.getPropic() != null && !postData.getPropic().isEmpty()) {
            Glide.with(this)
                    .load(IMAGE_BASE_URL + Constants.Paths.PROFILE_PIC + postData.getPropic())
                    .placeholder(R.drawable.family_dashboard_background)
                    .apply(requestOptions).into(profileImage);
        } else {
            profileImage.setImageResource(R.drawable.avatar_male);
        }
        postusername.setText(postData.getCreated_user_name());
        if (postData.getIs_shareable()) {
            btn_share.setVisibility(View.VISIBLE);
        } else {
            btn_share.setVisibility(View.GONE);
        }
        if (postData.getConversation_enabled()) {
            txt_count.setText(postData.getConversation_count());
            img_con.setVisibility(View.VISIBLE);

            txt_count.setVisibility(View.VISIBLE);
        } else {
            img_con.setVisibility(View.GONE);
            txt_count.setVisibility(View.GONE);
        }
        if (postData.getValid_urls() != null && postData.getValid_urls().size() == 1 && postData.getPost_attachment() == null || postData.getPost_attachment().size() == 0) {
            if (postData.getUrl_metadata() != null) {
                if (postData.getUrl_metadata().getUrlMetadataResult() != null) {
                    link_preview.setVisibility(View.VISIBLE);
                    thanks_post_view.setVisibility(View.GONE);
                    sliderView.setVisibility(View.GONE);
                    String t = postData.getUrl_metadata().getUrlMetadataResult().getTitle();
                    String d = postData.getUrl_metadata().getUrlMetadataResult().getDescription();
                    if (t != null && !t.isEmpty()) {
                        txt_url_des.setText(t);
                    } else if (d != null && !d.isEmpty()) {
                        txt_url_des.setText(t);
                    }
                    txt_url.setText(postData.getUrl_metadata().getUrlMetadataResult().getUrl());

                    String url = postData.getUrl_metadata().getUrlMetadataResult().getImage() + "";
                    if (!url.contains("http")) {
                        url = postData.getUrl_metadata().getUrlMetadataResult().getUrl() + url;
                    }
                    if (!url.isEmpty() && url.length() > 5) {
                        url_img.setScaleType(ImageView.ScaleType.CENTER);
                        Glide.with(this)
                                .load(url)
                                .placeholder(R.drawable.d_image)
                                .apply(Utilities.getCurvedRequestOptions()).into(url_img);
                    } else {
                        url_img.setScaleType(ImageView.ScaleType.FIT_XY);
                    }
                    link_preview.setOnClickListener(view -> {
                        try {
                            String urls = postData.getValid_urls().get(0).trim();
                            if (!urls.contains("http")) {
                                urls = urls.replaceAll("www.", "http://www.");
                            }

                            if (urls.contains("blog.familheey.com")) {
                                // Dinu(09/11/2021) for open another app
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urls));
                                startActivity(intent);
                            }
                            else if (urls.contains("familheey")) {
                                openAppGetParams(urls);
                            } else {
                                // Dinu(15/07/2021) for open another app
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urls));
                                startActivity(intent);
                                // startActivity(new Intent(this, BrowserActivity.class).putExtra("URL", urls));
                            }
                        } catch (Exception e) {
                            /*
                            Not needed
                             */
                        }
                    });

                }
            }
        } else if (postData.getPublish_type() != null && "request".equals(postData.getPublish_type())) {
            link_preview.setVisibility(View.GONE);
            sliderView.setVisibility(View.GONE);
            txt_less_or_more.setVisibility(View.GONE);
            txt_des.setVisibility(View.GONE);
            thanks_post_view.setVisibility(View.VISIBLE);
            text_description.setText(postData.getSnap_description());

            if (postData.getPublish_mention_items() != null && postData.getPublish_mention_items().size() > 0)
                txt_request_item.setText(postData.getPublish_mention_items().get(0).getItem_name());
            Glide.with(this)
                    .load(BuildConfig.IMAGE_BASE_URL + "post/" + postData.getPost_attachment().get(0).getFilename())
                    .into(thanks_post_img);
            if (postData.getPublish_mention_users() != null) {
                rv_name_list.setLayoutManager(new GridLayoutManager(this, 5));
                rv_name_list.setAdapter(new MensionNameAdapter(postData.getPublish_mention_users(), this));
            }

        } else {

            thanks_post_view.setVisibility(View.GONE);
            link_preview.setVisibility(View.GONE);
            sliderView.setVisibility(View.GONE);
            if (postData.getPost_attachment() != null && postData.getPost_attachment().size() > 0) {
                sliderView.setVisibility(View.VISIBLE);
                if (postData.getPost_attachment().get(0).getType().contains("image")) {
                    if (postData.getPost_attachment().get(0).getWidth() != null && postData.getPost_attachment().get(0).getHeight() != null) {
                        ViewGroup.LayoutParams params = sliderView.getLayoutParams();
                        params.height = getwidgetsize(postData.getPost_attachment().get(0).getWidth(), postData.getPost_attachment().get(0).getHeight());
                        sliderView.setLayoutParams(params);
                    } else {
                        ViewGroup.LayoutParams params = sliderView.getLayoutParams();
                        params.height = getwidgetsize();
                        sliderView.setLayoutParams(params);
                    }
                } else if (postData.getPost_attachment().get(0).getType().contains("video")) {

                    ViewGroup.LayoutParams params = sliderView.getLayoutParams();
                    params.height = 850;
                    sliderView.setLayoutParams(params);
                } else {

                    ViewGroup.LayoutParams params = sliderView.getLayoutParams();
                    params.height = 600;
                    sliderView.setLayoutParams(params);
                }


                adapter = new PostSliderAdapter(this, postData);
                sliderView.setSliderAdapter(adapter);
                sliderView.setIndicatorAnimation(IndicatorAnimations.THIN_WORM);
                sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
            }
        }


        String id = postData.getCreated_by() + "";
        share_count.setText(postData.getShared_user_count());

        /*txt_des.addAutoLinkMode(
                AutoLinkMode.MODE_HASHTAG,
                AutoLinkMode.MODE_URL);
        txt_des.setHashtagModeColor(ContextCompat.getColor(this, R.color.buttoncolor));
        txt_des.setUrlModeColor(ContextCompat.getColor(this, R.color.buttoncolor));*/
        txt_temp.setText(postData.getSnap_description());


        if (postData.getConversation_enabled() && Integer.parseInt(postData.getConversation_count_new()) > 0) {
            unread_status.setVisibility(View.VISIBLE);
        } else {
            unread_status.setVisibility(View.GONE);
        }

        if (id.equals(SharedPref.getUserRegistration().getId()) && !"request".equals(postData.getPublish_type())) {
            view_count.setText(postData.getViews_count());
            imgviw.setVisibility(View.VISIBLE);
            view_count.setVisibility(View.VISIBLE);
//            settings.setVisibility(View.VISIBLE);
//            settings.setOnClickListener(v -> {
//                //  (megha,06/09/2021)for popup window
//                View popupView = LayoutInflater.from(this).inflate(R.layout.popup_post_settings, null);
//                final PopupWindow popupWindow = new PopupWindow(popupView,LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//                popupWindow.setOutsideTouchable(true);
//                popupWindow.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.new_background_rounded));
//                popupWindow.showAtLocation(popupView, Gravity.CENTER_VERTICAL,0, 400);
//
//                //(Megha,05/08/2021)dim background
//                View container = popupWindow.getContentView().getRootView();
//                if (container != null) {
//                    WindowManager wm = (WindowManager) this.getSystemService(WINDOW_SERVICE);
//                    WindowManager.LayoutParams p = (WindowManager.LayoutParams) container.getLayoutParams();
//                    p.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
//                    p.dimAmount = 0.7f;
//                    if (wm != null) {
//                        wm.updateViewLayout(container, p);
//                    }
//                }
//
//                ImageView btnDismiss = (ImageView) popupView.findViewById(R.id.btnDismiss);
//                btnDismiss.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        popupWindow.dismiss();
//                    }
//                });
//
//            });
            imgsha.setVisibility(View.VISIBLE);
            share_count.setVisibility(View.VISIBLE);
        } else {
            imgviw.setVisibility(View.GONE);
            view_count.setVisibility(View.GONE);
            //settings.setVisibility(View.GONE);
              imgsha.setVisibility(View.GONE);
             share_count.setVisibility(View.GONE);
        }

        /*megha(02/09/2021)-> popup for rating bar*/
        if (postData.getRating_enabled()) {
            rating_count.setVisibility(View.VISIBLE);
            rating_count.setText(postData.getRating());
            rating_icon.setVisibility(View.VISIBLE);
            reviewers_count.setVisibility(View.VISIBLE);
            reviewers_count.setText("["+postData.getRating_count()+" Rating"+"]");
            rating_icon.setOnClickListener(v -> {

                View popupView = LayoutInflater.from(this).inflate(R.layout.popup_rating_bar, null);
                final PopupWindow popupWindow = new PopupWindow(popupView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
                popupWindow.showAsDropDown(rating_icon,500,0);
                popupWindow.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.new_background_rounded));

                View container = popupWindow.getContentView().getRootView();
                if (container != null) {
                    WindowManager wm = (WindowManager) this.getSystemService(WINDOW_SERVICE);
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
                String rating_by_user=postData.getRating_by_user();
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

                        onItemRating(jsonObject,String.valueOf((int)ratingBar.getRating()) );
                        /*(04/09/21)->for popup delay*/
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                popupWindow.dismiss();
                            }

                        }, 2000);

                    }
                });
            });

        }else{
            rating_icon.setVisibility(View.GONE);
            rating_count.setVisibility(View.GONE);
            reviewers_count.setVisibility(View.GONE);
        }
        if (postData.getRating_enabled()){
            rating_count.setText(postData.getRating());
            reviewers_count.setText("["+postData.getRating_count()+" Rating"+"]");
            if(postData.getRating_by_user()==null||postData.getRating_by_user().equals("0")) {
                rating_icon.setVisibility(View.VISIBLE);
                rating_icon.setImageResource(R.drawable.images);
            }
            else{
                rating_icon.setVisibility(View.VISIBLE);
                rating_icon.setImageResource(R.drawable.coloured_star);
            }
            rating_icon.setVisibility(View.VISIBLE);
            rating_count.setVisibility(View.VISIBLE);
            reviewers_count.setVisibility(View.VISIBLE);
        }else{
            rating_icon.setVisibility(View.GONE);
            rating_count.setVisibility(View.GONE);
            reviewers_count.setVisibility(View.GONE);
        }

        /*txt_des.setAutoLinkOnClickListener((autoLinkMode, matchedText) -> {

            if (autoLinkMode == AutoLinkMode.MODE_URL) {
                try {
                    String url = matchedText.trim();
                    if (!url.contains("http")) {
                        url = url.replaceAll("www.", "http://www.");
                    }
                    if (url.contains("blog.familheey.com")) {
                        // Dinu(09/11/2021) for open another app
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                    }
                    else if (url.contains("familheey")) {
                        openAppGetParams(url);
                    } else {
                        // Dinu(15/07/2021) for open another app
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                        //  startActivity(new Intent(this, BrowserActivity.class).putExtra("URL", url));
                    }
                } catch (Exception e) {
                  *//*
                  Not needed
                   *//*
                }
            }

        });
*/
        txt_temp.post(() -> {
            String description = postData.getSnap_description().trim();
            description = description.replaceAll("HTTP:", "http:").replaceAll("Http:", "http:")
                    .replaceAll("Https:", "https:")
                    .replaceAll("HTTPS:", "https:");
            description = description.replaceAll("WWW.", "www.").replaceAll("Www.", "www.");
            if (txt_temp.getLineCount() > 2) {
                txt_less_or_more.setVisibility(View.VISIBLE);
                txt_des.setMaxLines(3);
                txt_des.setEllipsize(TextUtils.TruncateAt.END);
            } else {
                txt_less_or_more.setVisibility(View.GONE);
            }
            /**07-12-2021**/
            txt_des.setText(description);
            if(txt_des!=null){
                hashTagHelper = HashTagHelper.Creator.create(ContextCompat.getColor(this,R.color.buttoncolor), null);
                hashTagHelper.handle(txt_des);
                Linkify.addLinks(txt_des, Linkify.ALL);
                txt_des.setLinkTextColor(ContextCompat.getColor(this,R.color.buttoncolor));
            }
        });
        txt_less_or_more.setOnClickListener(v -> {
            String description = postData.getSnap_description().trim();
            description = description.replaceAll("HTTP:", "http:").replaceAll("Http:", "http:")
                    .replaceAll("Https:", "https:")
                    .replaceAll("HTTPS:", "https:");
            description = description.replaceAll("WWW.", "www.").replaceAll("Www.", "www.");
            if (txt_less_or_more.getText().equals("More")) {
                txt_less_or_more.setText("Less");
                /**07-12-2021**/
                txt_des.setText(description);
                if(txt_des!=null){
                    hashTagHelper = HashTagHelper.Creator.create(ContextCompat.getColor(this,R.color.buttoncolor), null);
                    hashTagHelper.handle(txt_des);
                    Linkify.addLinks(txt_des, Linkify.ALL);
                    txt_des.setLinkTextColor(ContextCompat.getColor(this,R.color.buttoncolor));
                }
                txt_des.setMaxLines(Integer.MAX_VALUE);
                txt_des.setEllipsize(null);
            } else {
                txt_less_or_more.setText("More");
                txt_des.setMaxLines(3);
                txt_des.setEllipsize(TextUtils.TruncateAt.END);
            }
        });

        btn_share.setOnClickListener(v -> showMenusShare(v, postData));
        share_btn_share.setOnClickListener(v -> showMenusShare(v, postData));

        profileImage.setOnClickListener(v -> {
            Intent userIntent = new Intent(this, ProfileActivity.class);
            FamilyMember familyMember = new FamilyMember();
            familyMember.setUserId(postData.getCreated_by());
            userIntent.putExtra(DATA, familyMember);
            startActivity(userIntent);
        });

        img_con.setOnClickListener(v -> startActivity(new Intent(this, PostCommentActivity.class)
                .putExtra(DATA, postData)
                .putExtra(TYPE, "")
                .putExtra(SUB_TYPE, "POST")
                .putExtra("POS", 0)));

        edtxmessage.setOnClickListener(v -> startActivity(new Intent(this, PostCommentActivity.class)
                .putExtra(DATA, postData)
                .putExtra(TYPE, "")
                .putExtra(SUB_TYPE, "POST")
                .putExtra("POS", 0)));
        imgsha.setOnClickListener(v -> {
            if (Integer.parseInt(postData.getShared_user_count()) > 0) {
                addViewCount(postData.getPost_id() + "");
                if (postData.getShared_user_count() != null && Integer.parseInt(postData.getShared_user_count()) > 0) {
                    String pid = "";
                    if (postData.getOrgin_id() != null) {
                        pid = postData.getOrgin_id();
                    } else {
                        pid = postData.getPost_id() + "";
                    }

                   startActivity(new Intent(this, SharelistActivity.class)
                            .putExtra(Constants.Bundle.TYPE, "POST")
                            .putExtra("event_id", pid)
                            .putExtra("user_id", postData.getCreated_by() + ""));
                }
//                startActivity(new Intent(this, SharelistActivity.class)
//                        .putExtra(Constants.Bundle.TYPE, "POST")
//                        .putExtra("event_id", postData.getOrgin_id() + "")
//                        .putExtra("user_id", postData.getCreated_by() + ""));
            }
         });


        imgviw.setOnClickListener(v -> {
            if (Integer.parseInt(postData.getViews_count()) > 0) {
                startActivity(new Intent(this, SharelistActivity.class)
                        .putExtra(Constants.Bundle.TYPE, "POSTVIEW")
                        .putExtra("event_id", postData.getPost_id() + "")
                        .putExtra("user_id", postData.getCreated_by() + ""));
            }
        });

        btn_more.setOnClickListener(v ->

                {
                    if (id.equals(SharedPref.getUserRegistration().getId())) {
                        showMenusPostOwner(v, postData);
                    } else {
                        showMenusNormalUser(v, postData);
                    }
                }


        );
        btn_open_request.setOnClickListener(view -> {
            Intent requestDetailedIntent = new Intent(this, NeedRequestDetailedActivity.class);
            requestDetailedIntent.putExtra(DATA, postData.getPublish_id());
            startActivity(requestDetailedIntent);
        });
    }

    private String dateFormat(String time) {
        /* megha(16/08/21) for getting post time */

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
            }else{
                isChecked = false;
            }

        if (isChecked){
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

    private void addViewCount(String post_id) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        jsonObject.addProperty("post_id", post_id);
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        FamilheeyApplication application = FamilheeyApplication.get(this);
        ApiServices apiServices = RetrofitBase.createRxResource(this, ApiServices.class);
        subscriptions.add(apiServices.addViewCount(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(response -> {
                }, throwable -> {
                }));
    }

    private void onItemRating(JsonObject jsonObject,String rating) {
        SweetAlertDialog progressDialog = Utilities.getProgressDialog(this);
        progressDialog.show();
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        FamilheeyApplication application = FamilheeyApplication.get(this);
        ApiServices apiServices = RetrofitBase.createRxResource(this, ApiServices.class);
        subscriptions.add(apiServices.rate(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(response -> {
                    getPost(Objects.requireNonNull(getIntent().getExtras()).getString("ids"));
                    progressDialog.hide();
                },throwable -> {
                    progressDialog.hide();
                }));
    }

    public int getinnerwidgetsize(String width, String hight) {
        float screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels - 100;
        float wf = Float.parseFloat(width);
        float hf = Float.parseFloat(hight);
        return Math.round((screenWidth / wf) * hf);
    }

    public int getinnerwidgetsize() {

        float screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels - 100;
        float s = screenWidth / 4;
        return Math.round(s * 3);
    }

    public int getwidgetsize() {

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


    private void showMenusNormalUser(View v, PostData postData) {
        PopupMenu popup = new PopupMenu(v.getContext(), v);
        popup.getMenuInflater().inflate(R.menu.popup_menu_post_user, popup.getMenu());
        if (postData.getMuted() != null && postData.getMuted()) {
            popup.getMenu().getItem(0).setTitle("UnMute");
        } else {
            popup.getMenu().getItem(0).setTitle("Mute");
        }
        popup.getMenu().getItem(3).setVisible(false);
        popup.getMenu().getItem(4).setVisible(false);

        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.mutePost:
                    muteConversation(postData);
                    break;
                case R.id.removepost:
                    removePost(postData.getPost_id() + "");
                    break;
                case R.id.report:
                    Dialoguereport(postData.getPost_id() + "");
                    break;

                case R.id.delete:
                    // confirmation(postData);
                    break;

                case R.id.share:
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_SEND);

                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, Constants.ApiPaths.BASE_URL);
                    startActivity(Intent.createChooser(intent, "Share"));
                    break;
            }
            return true;
        });

        popup.show();
    }

    private void showMenusPostOwner(View v, PostData postData) {
        PopupMenu popup = new PopupMenu(v.getContext(), v);
        popup.getMenuInflater().inflate(R.menu.popup_menu_post_owner, popup.getMenu());
        if (postData.getMuted() != null && postData.getMuted()) {
            popup.getMenu().getItem(0).setTitle("UnMute");
        } else {
            popup.getMenu().getItem(0).setTitle("Mute");
        }

popup.getMenu().getItem(3).setVisible(false);
        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.mutePost:
                    popup.dismiss();
                    muteConversation(postData);
                    break;
                case R.id.editpost:
                    startActivity(new Intent(this, EditPostActivity.class).putExtra("pos", 0).putExtra("POST", new Gson().toJson(postData)));
                    break;
                case R.id.delete:
                    confirmation(postData);
                    break;

                case R.id.share:
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_SEND);

                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, Constants.ApiPaths.BASE_URL);
                    startActivity(Intent.createChooser(intent, "Share"));
                    break;
            }
            return true;
        });

        popup.show();
    }


    private void showMenusShare(View v, PostData postData) {
        /*popup for share(10/09/21)*/
        View popupView = LayoutInflater.from(this).inflate(R.layout.popup_share, null);
        final PopupWindow popupWindow = new PopupWindow(popupView,LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(ContextCompat.getDrawable(this,R.drawable.new_background_rounded));
        popupWindow.showAsDropDown(v,500,0);
        View container = popupWindow.getContentView().getRootView();
        if (container != null) {
            WindowManager wm = (WindowManager) this.getSystemService(WINDOW_SERVICE);
            WindowManager.LayoutParams p = (WindowManager.LayoutParams) container.getLayoutParams();
            p.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            p.dimAmount = 0.1f;
            if (wm != null) {
                wm.updateViewLayout(container, p);
            }
        }
        TextView shares = (TextView)popupView.findViewById(R.id.shares);
        ImageView shares_icon=(ImageView)popupView.findViewById(R.id.shares_icon);
        TextView socials = (TextView)popupView.findViewById(R.id.socials);

        shares.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),ShareEventActivity.class).putExtra(TYPE,"POST").putExtra("Post_id",postData.getPost_id().toString()));
                popupWindow.dismiss();
            }
        });
        socials.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, Constants.ApiPaths.SHARE_URL + "page/posts/" + postData.getPost_id());
                    startActivity(Intent.createChooser(intent, "Share"));
                    popupWindow.dismiss();
            }
        });
//        PopupMenu popup = new PopupMenu(v.getContext(), v);
//        popup.getMenuInflater().inflate(R.menu.popup_menu_post_share, popup.getMenu());
//        popup.setOnMenuItemClickListener(item -> {
//            switch (item.getItemId()) {
//
//                case R.id.sharefamily:
//
//
//                    startActivity(new Intent(this, ShareEventActivity.class).putExtra(Constants.Bundle.TYPE, "POST").putExtra("Post_id", postData.getPost_id() + ""));
//                    break;
//
//
//                case R.id.sharesocial:
//                    Intent intent = new Intent();
//                    intent.setAction(Intent.ACTION_SEND);
//                    intent.setType("text/plain");
//                    intent.putExtra(Intent.EXTRA_TEXT, Constants.ApiPaths.SHARE_URL + "page/posts/" + postData.getPost_id());
//                    startActivity(Intent.createChooser(intent, "Share"));
//                    break;
//            }
//            return true;
//        });
//
//        popup.show();
    }

    private void muteConversation(PostData postData) {
        SweetAlertDialog progressDialog = Utilities.getProgressDialog(this);
        progressDialog.show();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        jsonObject.addProperty("post_id", postData.getPost_id() + "");
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        FamilheeyApplication application = FamilheeyApplication.get(this);
        ApiServices apiServices = RetrofitBase.createRxResource(this, ApiServices.class);
        subscriptions.add(apiServices.muteConversation(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(response -> {
                    progressDialog.dismiss();
                    assert response.body() != null;
                    postData.setMuted(response.body().getData().getIs_active());
                }, throwable -> progressDialog.dismiss()));
    }

    private void removePost(String post_id) {
        SweetAlertDialog progressDialog = Utilities.getProgressDialog(this);
        progressDialog.show();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        jsonObject.addProperty("post_id", post_id);
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        FamilheeyApplication application = FamilheeyApplication.get(this);
        ApiServices apiServices = RetrofitBase.createRxResource(this, ApiServices.class);
        subscriptions.add(apiServices.removePost(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(response -> progressDialog.dismiss(), throwable -> progressDialog.dismiss()));
    }


    private void confirmation(PostData postData) {

        SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setContentText("Are you sure you want to delete this post?")
                .setConfirmText("Yes")
                .setCancelText("No");

        pDialog.show();
        pDialog.setConfirmClickListener(sDialog -> {
            deletePost(postData.getPost_id() + "");
            pDialog.cancel();
        });
        pDialog.setCancelClickListener(SweetAlertDialog::cancel);
    }

    private void deletePost(String id) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", id);
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        FamilheeyApplication application = FamilheeyApplication.get(this);
        ApiServices apiServices = RetrofitBase.createRxResource(this, ApiServices.class);
        subscriptions.add(apiServices.deletePost(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(response -> onBackPressed(), throwable -> {
                }));
    }

    private void Dialoguereport(String id) {

        final Dialog dialog = new Dialog(this);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialogue_report);
        dialog.setCanceledOnTouchOutside(false);
        TextInputEditText editText = dialog.findViewById(R.id.description);
        dialog.findViewById(R.id.btn_close).setOnClickListener(view -> dialog.dismiss());
        dialog.findViewById(R.id.btndone).setOnClickListener(v -> {
            if (Objects.requireNonNull(editText.getText()).toString().trim().equals("")) {
                Toast.makeText(this, "Reason is required", Toast.LENGTH_LONG).show();
            } else {
                reportPost(id, editText.getText().toString().trim(), dialog);
            }
        });

        dialog.show();
    }

    private void reportPost(String post_id, String des, Dialog dialog) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        jsonObject.addProperty("type_id", post_id);
        jsonObject.addProperty("description", des);
        jsonObject.addProperty("spam_page_type", "post");
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        FamilheeyApplication application = FamilheeyApplication.get(this);
        ApiServices apiServices = RetrofitBase.createRxResource(this, ApiServices.class);
        subscriptions.add(apiServices.reportPost(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(response -> {
                    dialog.dismiss();
                    Toast.makeText(this, "Reported successfully.We will review the post and do the needful,thank you", Toast.LENGTH_LONG).show();
                }, throwable -> {
                }));
    }


    private void openAppGetParams(String url) {
        // UserNotification
        SweetAlertDialog progressDialog = Utilities.getProgressDialog(this);
        progressDialog.show();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("url", url);
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        FamilheeyApplication application = FamilheeyApplication.get(this);
        ApiServices apiServices = RetrofitBase.createRxResource(this, ApiServices.class);
        subscriptions.add(apiServices.openAppGetParams(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(response -> {
                    progressDialog.dismiss();
                    if(response.body() != null) {
                        if (response.body().getData().getSub_type().equals("family_link")) {
                            response.body().getData().setSub_type("");
                        }
                        response.body().getData().goToCorrespondingDashboard(this);
                    }else{
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                    }
                }, throwable -> progressDialog.dismiss()));
    }

    //Dinu(18-03-2021)
    @Override
    public void onPause() {

        if (postData != null) {
            if (postData.getShared_user_name() == null) {
                normalViewDataSet( postData );
            } else {
                share_view( postData );
            }
        } else {
            Intent intent = new Intent( this, MainActivity.class );
            intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
            startActivity( intent );

        }
        super.onPause();
    }

    @Override
    public void onClick(View v) {
    }
}
