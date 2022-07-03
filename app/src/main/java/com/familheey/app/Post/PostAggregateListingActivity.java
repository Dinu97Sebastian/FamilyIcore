package com.familheey.app.Post;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.familheey.app.FamilheeyApplication;
import com.familheey.app.Fragments.Posts.PostData;
import com.familheey.app.Fragments.Posts.UserPostAggregateAdapter;
import com.familheey.app.Networking.Retrofit.ApiServices;
import com.familheey.app.Networking.Retrofit.RetrofitBase;
import com.familheey.app.R;
import com.familheey.app.Utilities.SharedPref;
import com.familheey.app.Utilities.Utilities;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import static com.familheey.app.Utilities.Constants.Bundle.POSITION;

public class PostAggregateListingActivity extends AppCompatActivity implements UserPostAggregateAdapter.postRating {

    public static final int CHAT_REQUEST_CODE = 1003;
    @BindView(R.id.shimmer_view_container)
    com.facebook.shimmer.ShimmerFrameLayout shimmerFrameLayout;
    @BindView(R.id.refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.list_recyclerView)
    RecyclerView listRecyclerView;
    @BindView(R.id.toolBarTitle)
    TextView toolBarTitle;
    @BindView(R.id.goBack)
    ImageView goBack;
    @BindView(R.id.layoutEmpty)
    LinearLayout layoutEmpty;
    @BindView(R.id.linearLayout3)
    RelativeLayout linearLayout3;


    private CompositeDisposable subscriptions;
    private List<PostData> data = new ArrayList<>();
    private UserPostAggregateAdapter adapter;
    private String PostRefId;
    private String rateCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_aggregate_listing);
        init();
    }


    private void init() {
        ButterKnife.bind(this);
        subscriptions = new CompositeDisposable();
        if (getIntent().getExtras() != null && getIntent().hasExtra("id")) {
            PostRefId = getIntent().getExtras().getString("id");
        }
        getPost();
        toolBarTitle.setText("");
        goBack.setOnClickListener(v -> onBackPressed());
        listRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UserPostAggregateAdapter(this, data,this);
        listRecyclerView.setAdapter(adapter);

        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            data.clear();
            adapter.notifyDataSetChanged();
            mSwipeRefreshLayout.setRefreshing(false);
            getPost();
        });
    }

    private void getPost() {
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        shimmerFrameLayout.startShimmer();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        jsonObject.addProperty("type", "post");
        jsonObject.addProperty("offset", "0");
        jsonObject.addProperty("limit", "100");
        jsonObject.addProperty("post_ref_id", PostRefId);
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        FamilheeyApplication application = FamilheeyApplication.get(this);
        ApiServices apiServices = RetrofitBase.createRxResource(this, ApiServices.class);

        subscriptions.add(apiServices.getPostAggregate(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(response -> {
                    shimmerFrameLayout.setVisibility(View.GONE);

                    linearLayout3.setVisibility(View.VISIBLE);
                    layoutEmpty.setVisibility(View.GONE);
                    assert response.body() != null;
                    data.addAll(response.body().getData());

                    for (int i=0;i<data.size();i++){

                        Boolean rate = data.get(i).rating_enabled;
                        if(data.get(i).getRating_by_user()==null) {
                            data.get(i).setRating_by_user("0");
                        }
                        if(data.get(i).getRating()==null){
                            data.get(i).setRating("0.0");
                        }
//                            double ratingValue=Double.parseDouble( data.get(i).getRating());
//                            data.get(i).setRating(String.valueOf(ratingValue));

                        if (rate==null){
                            data.get(i).setRating_enabled(false);
                        }

                        else if (rate){
                            data.get(i).setRating_enabled(true);
                            data.get(i).setRating(data.get(i).getRating());
                            data.get(i).setRating_by_user(data.get(i).getRating_by_user());
                            data.get(i).setRating_count(data.get(i).getRating_count());
                        }else if(!rate){
                            data.get(i).setRating_enabled(false);
                        }
                    }
                    adapter.notifyDataSetChanged();

                }, throwable -> {
                    shimmerFrameLayout.setVisibility(View.GONE);
                    if (throwable instanceof IOException) {
                        networkError();
                    }
                }));


    }


    private void networkError() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Uh oh! Check your internet connection and retry.")
                .setCancelable(false)
                .setPositiveButton("Retry", (dialog, which) -> getPost()).setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        AlertDialog alert = builder.create();
        alert.setTitle("Connection Unavailable");
        alert.show();
        params.setMargins(0, 0, 20, 0);
        alert.getButton(DialogInterface.BUTTON_NEGATIVE).setLayoutParams(params);


    }

    @Override
    public void onRating(int position, JsonObject jsonObject, String rating) {
        onItemRating(jsonObject,position,rating);
    }

    @Override
    public void onChatActivity(Intent intent) {
        startActivityForResult(intent, CHAT_REQUEST_CODE);
    }

    /* megha(03/09/21) for rating api*/
    private void onItemRating(JsonObject jsonObject, int position,String rating) {
        SweetAlertDialog progressDialog = Utilities.getProgressDialog(this);
        progressDialog.show();
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        FamilheeyApplication application = FamilheeyApplication.get(this);
        ApiServices apiServices = RetrofitBase.createRxResource(this, ApiServices.class);
        subscriptions.add(apiServices.rate(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(response -> {
                    JSONObject json= (JSONObject) new JSONTokener(response.body().string()).nextValue();
                    JSONObject json2 = json.getJSONObject("data");
                    rateCount = (String) json2.get("total_rating");
if(rateCount.equals("0"))
    rateCount="0.0";
                    String reviewers=(String)json2.get("rating_count");

                        data.get(position).setRating_by_user(rating);
                        data.get(position).setRating(rateCount);
                        data.get(position).setRating_count(reviewers);

                    adapter.notifyDataSetChanged();
                    progressDialog.hide();
                },throwable -> {
                    progressDialog.hide();
                }));
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHAT_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            if (data.getExtras().getBoolean("isChat")) {
                /*this.data.clear();
                getPost();*/
                this.data.get(data.getExtras().getInt(POSITION)).setConversation_count_new("0");
                int pos = data.getExtras().getInt(POSITION);
                PostData postData = new Gson().fromJson(data.getExtras().getString("data"), PostData.class);
                this.data.get(pos).setPost_attachment(postData.getPost_attachment());
                this.data.get(pos).setIs_shareable(postData.getIs_shareable());
                this.data.get(pos).setShared_user_count(postData.getShared_user_count());
                this.data.get(pos).setSnap_description(postData.getSnap_description());
                this.data.get(pos).setConversation_enabled(postData.getConversation_enabled());
                this.data.get(pos).setRating_enabled(postData.getRating_enabled());
                this.data.get(pos).setConversation_count(postData.getConversation_count());
                this.data.get(pos).setRating(postData.getRating());
                this.data.get(pos).setRating_count(postData.getRating_count());
                this.data.get(pos).setRating_by_user(postData.getRating_by_user());
                this.data.get(pos).setIs_viewed(postData.getIs_viewed());
                this.data.get(pos).setViews_count(postData.getViews_count());
                adapter.notifyDataSetChanged();
            } else {
                this.data.get(data.getExtras().getInt(POSITION)).setConversation_count_new("0");
                int pos = data.getExtras().getInt(POSITION);
                PostData postData = new Gson().fromJson(data.getExtras().getString("data"), PostData.class);
                this.data.get(pos).setPost_attachment(postData.getPost_attachment());
                this.data.get(pos).setIs_shareable(postData.getIs_shareable());
                this.data.get(pos).setShared_user_count(postData.getShared_user_count());
                this.data.get(pos).setSnap_description(postData.getSnap_description());
                this.data.get(pos).setConversation_enabled(postData.getConversation_enabled());
                this.data.get(pos).setRating_enabled(postData.getRating_enabled());
                this.data.get(pos).setConversation_count(postData.getConversation_count());
                this.data.get(pos).setRating(postData.getRating());
                this.data.get(pos).setRating_count(postData.getRating_count());
                this.data.get(pos).setRating_by_user(postData.getRating_by_user());
                this.data.get(pos).setIs_viewed(postData.getIs_viewed());
                this.data.get(pos).setViews_count(postData.getViews_count());
                adapter.notifyDataSetChanged();
            }
        }
    }
}


