package com.familheey.app.Post;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.familheey.app.Activities.ProfileActivity;
import com.familheey.app.Activities.ShareEventActivity;
import com.familheey.app.FamilheeyApplication;
import com.familheey.app.Fragments.Posts.PostData;
import com.familheey.app.Fragments.Posts.PostSliderAdapter;
import com.familheey.app.Models.Response.FamilyMember;
import com.familheey.app.Networking.Retrofit.ApiServices;
import com.familheey.app.Networking.Retrofit.RetrofitBase;
import com.familheey.app.R;
import com.familheey.app.Utilities.Constants;
import com.familheey.app.Utilities.SharedPref;
import com.google.gson.JsonObject;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import static com.familheey.app.Utilities.Constants.ApiPaths.IMAGE_BASE_URL;
import static com.familheey.app.Utilities.Constants.Bundle.DATA;

public class PostAcceptRejectAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private Context context;
    private List<PostData> repositories;

    private RequestOptions requestOptions;

    public CompositeDisposable subscriptions;

    public PostAcceptRejectAdapter(Context context, List<PostData> data) {
        this.repositories = data;
        this.context = context;
        requestOptions = new RequestOptions();
        requestOptions.transforms(new RoundedCorners(16));
        subscriptions = new CompositeDisposable();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.item_post, parent, false);
        return new ViewHolderOne(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        PostData postData = repositories.get(position);
        ViewHolderOne holderOne = (ViewHolderOne) holder;
        if(repositories.get(position).getPropic()!=null){
            Glide.with(holder.itemView.getContext())
                    .load(IMAGE_BASE_URL + Constants.Paths.PROFILE_PIC + repositories.get(position).getPropic())
                    .placeholder(R.drawable.family_dashboard_background)
                    .apply(requestOptions).into(holderOne.profileImage);
        }else {
            Glide.with(holder.itemView.getContext())
                    .load(R.drawable.family_dashboard_background)
                    .apply(requestOptions).into(holderOne.profileImage);
        }
        holderOne.postusername.setText(postData.getCreated_user_name());

        holderOne.postedgroup.setText("Posted in " + postData.getGroup_name());

        if (postData.getSnap_description().trim().length() > 120) {
            holderOne.txt_less_or_more.setVisibility(View.VISIBLE);
            holderOne.txt_des.setMaxLines(2);
            holderOne.txt_des.setEllipsize(TextUtils.TruncateAt.END);
            holderOne.txt_des.setText(postData.getSnap_description());
        } else {
            holderOne.txt_less_or_more.setVisibility(View.GONE);
            holderOne.txt_des.setText(postData.getSnap_description());
        }
        holderOne.postdate.setText(dateFormat(postData.getCreatedAt()));
        if (postData.getPost_attachment() != null && postData.getPost_attachment().size() > 0) {
            holderOne.sliderView.setVisibility(View.VISIBLE);
            if (postData.getPost_attachment().get(0).getType().contains("image")) {
                if (postData.getPost_attachment().get(0).getWidth() != null && postData.getPost_attachment().get(0).getHeight() != null) {
                    ViewGroup.LayoutParams params = holderOne.sliderView.getLayoutParams();
                    params.height = getwidgetsize(postData.getPost_attachment().get(0).getWidth(), postData.getPost_attachment().get(0).getHeight());
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
        }

        holderOne.txt_less_or_more.setOnClickListener(v -> {
            if (holderOne.txt_less_or_more.getText().equals("Read More")) {
                holderOne.txt_less_or_more.setText("Read Less");
                holderOne.txt_des.setText(postData.getSnap_description());
                holderOne.txt_des.setMaxLines(Integer.MAX_VALUE);
                holderOne.txt_des.setEllipsize(null);
            } else {
                holderOne.txt_less_or_more.setText("Read More");
                holderOne.txt_des.setMaxLines(2);
                holderOne.txt_des.setEllipsize(TextUtils.TruncateAt.END);
            }
        });


        if (postData.getIs_shareable()) {
            holderOne.btn_share.setVisibility(View.VISIBLE);
        } else {
            holderOne.btn_share.setVisibility(View.GONE);
        }

        holderOne.btn_share.setOnClickListener(v -> {
            context.startActivity(new Intent(context, ShareEventActivity.class).putExtra(Constants.Bundle.TYPE, "POST").putExtra("Post_id", postData.getPost_id() + ""));
        });

        holderOne.profileImage.setOnClickListener(v -> {
            Intent userIntent = new Intent(context, ProfileActivity.class);
            FamilyMember familyMember = new FamilyMember();
            familyMember.setUserId(postData.getCreated_by());
            userIntent.putExtra(DATA, familyMember);
            context.startActivity(userIntent);
        });

        if (postData.getSnap_description().equals("")) {
            holderOne.txt_less_or_more.setVisibility(View.GONE);
            holderOne.txt_des.setVisibility(View.GONE);
        } else {
            holderOne.txt_des.setVisibility(View.VISIBLE);
        }

        holderOne.btn_reject.setOnClickListener(v -> approveorreject("1",postData.getPost_id()+""));
        holderOne.btnaccept.setOnClickListener(v -> approveorreject("2",postData.getPost_id()+""));

    }

    @Override
    public int getItemCount() {
        return repositories.size();
    }


    public PostData getItem(int position) {
        return repositories.get(position);
    }


    private String dateFormat(String time) {
        DateTime dateTime = ISODateTimeFormat.dateTimeParser().parseDateTime(time);
        DateTimeFormatter dtfOut = DateTimeFormat.forPattern("MMM dd yyyy hh:mm aa");
        return dtfOut.print(dateTime);
    }

    static class ViewHolderOne extends RecyclerView.ViewHolder {
        @BindView(R.id.txt_less_or_more)
        TextView txt_less_or_more;
        @BindView(R.id.profileImage)
        ImageView profileImage;
        @BindView(R.id.postusername)
        TextView postusername;
        @BindView(R.id.postedgroup)
        TextView postedgroup;
        @BindView(R.id.postdate)
        TextView postdate;
        @BindView(R.id.img_con)
        ImageView img_con;
        @BindView(R.id.txt_des)
        TextView txt_des;
        @BindView(R.id.imageSlider)
        com.smarteist.autoimageslider.SliderView sliderView;

        @BindView(R.id.btn_share)
        ImageView btn_share;

        @BindView(R.id.btnaccept)
        com.google.android.material.button.MaterialButton btnaccept;

        @BindView(R.id.btn_reject)
        com.google.android.material.button.MaterialButton btn_reject;

        ViewHolderOne(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }


    private int getwidgetsize(String width, String hight) {
        float screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        float wf = Float.parseFloat(width);
        float hf = Float.parseFloat(hight);
        return Math.round((screenWidth / wf) * hf);
    }


    private int getwidgetsize() {
        float screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        float s = screenWidth / 4;
        return Math.round(s * 3);
    }
    //
    private void approveorreject(String status,String postid) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        jsonObject.addProperty("post_id", postid);
        jsonObject.addProperty("is_approved", status);
        jsonObject.addProperty("type", "post");
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        FamilheeyApplication application = FamilheeyApplication.get(context);
        ApiServices apiServices = RetrofitBase.createRxResource(context, ApiServices.class);
        subscriptions.add(apiServices.approveRejectPost(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(response -> notifyDataSetChanged(), throwable -> {
                    /*
                    Not needed
                     */
                }));
    }
}