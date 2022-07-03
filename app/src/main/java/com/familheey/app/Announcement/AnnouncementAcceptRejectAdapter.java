package com.familheey.app.Announcement;

import android.content.Context;
import android.content.Intent;
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
import com.familheey.app.FamilheeyApplication;
import com.familheey.app.Fragments.Posts.PostData;
import com.familheey.app.Models.Response.FamilyMember;
import com.familheey.app.Networking.Retrofit.ApiServices;
import com.familheey.app.Networking.Retrofit.RetrofitBase;
import com.familheey.app.R;
import com.familheey.app.Utilities.Constants;
import com.familheey.app.Utilities.SharedPref;
import com.google.gson.JsonObject;

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

public class AnnouncementAcceptRejectAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<PostData> repositories;
    private static final int ITEM = 0;
    private static final int SHARE = 1;
    private RequestOptions requestOptions;
    private CompositeDisposable subscriptions;

    public AnnouncementAcceptRejectAdapter(Context context, List<PostData> repositories) {
        this.repositories = repositories;
        this.context = context;
        subscriptions = new CompositeDisposable();
        requestOptions = new RequestOptions();
        requestOptions.transforms(new RoundedCorners(16));
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.item_announcement_accept_reject, parent, false);
        return new ViewHolderOne(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        PostData postData = repositories.get(position);
        ViewHolderOne holderOne = (ViewHolderOne) holder;
        Glide.with(holder.itemView.getContext())
                .load(IMAGE_BASE_URL + Constants.Paths.LOGO + postData.getFamily_logo())
                .placeholder(R.drawable.family_dashboard_background)
                .apply(requestOptions).into(holderOne.userProfileImage);

        holderOne.userName.setText(postData.getGroup_name());
        holderOne.postedin.setText("Posted by " + postData.getCreated_user_name());
        holderOne.txt_date.setText(dateFormat(postData.getCreatedAt()));

        if (postData.getSnap_description().length() > 100) {
            holderOne.txt_less_or_more.setVisibility(View.VISIBLE);
            holderOne.txt_des.setMaxLines(2);
            holderOne.txt_des.setEllipsize(TextUtils.TruncateAt.END);
            holderOne.txt_des.setText(postData.getSnap_description());
        } else {
            holderOne.txt_less_or_more.setVisibility(View.GONE);
            holderOne.txt_des.setText(postData.getSnap_description());
        }


        ((ViewHolderOne) holder).txt_less_or_more.setOnClickListener(v -> {
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
        holderOne.userProfileImage.setOnClickListener(v -> {
            Intent userIntent = new Intent(context, ProfileActivity.class);
            FamilyMember familyMember = new FamilyMember();
            familyMember.setUserId(postData.getCreated_by());
            userIntent.putExtra(DATA, familyMember);
            context.startActivity(userIntent);
        });


        holderOne.btn_reject.setOnClickListener(v -> approveorreject("1",postData.getPost_id()+""));
        holderOne.btnaccept.setOnClickListener(v -> approveorreject("2",postData.getPost_id()+""));
    }

    @Override
    public int getItemCount() {
        return repositories.size();
    }


    @Override
    public int getItemViewType(int position) {

        PostData postData = repositories.get(position);
        if (postData.getShared_user_names() != null)
            return SHARE;
        else return ITEM;
    }

    private String dateFormat(String sdate) {
        DateTime dateTime = ISODateTimeFormat.dateTimeParser().parseDateTime(sdate);
        DateTimeFormatter dtfOut = DateTimeFormat.forPattern("MMM dd yyyy hh:mm aa");
        return dtfOut.print(dateTime);
    }

    static class ViewHolderOne extends RecyclerView.ViewHolder {
        @BindView(R.id.userProfileImage)
        ImageView userProfileImage;
        @BindView(R.id.userName)
        TextView userName;
        @BindView(R.id.postedin)
        TextView postedin;
        @BindView(R.id.txt_date)
        TextView txt_date;
        @BindView(R.id.txt_des)
        TextView txt_des;
        @BindView(R.id.txt_less_or_more)
        TextView txt_less_or_more;

        @BindView(R.id.btnaccept)
        com.google.android.material.button.MaterialButton btnaccept;

        @BindView(R.id.btn_reject)
        com.google.android.material.button.MaterialButton btn_reject;
        ViewHolderOne(View view) {

            super(view);
            ButterKnife.bind(this, view);
        }
    }


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
                    nee to handle
                     */
                }));
    }

}