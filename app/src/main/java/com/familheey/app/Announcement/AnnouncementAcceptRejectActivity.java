package com.familheey.app.Announcement;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.familheey.app.FamilheeyApplication;
import com.familheey.app.Fragments.Posts.PostData;
import com.familheey.app.Networking.Retrofit.ApiServices;
import com.familheey.app.Networking.Retrofit.RetrofitBase;
import com.familheey.app.Post.PostAcceptRejectAdapter;
import com.familheey.app.R;
import com.familheey.app.Utilities.SharedPref;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class AnnouncementAcceptRejectActivity extends AppCompatActivity {


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

    public CompositeDisposable subscriptions;
    String query = "";
    List<PostData> data = new ArrayList<>();
    String groupId = "";
    AnnouncementAcceptRejectAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_accept_reject);
        ButterKnife.bind(this);
        subscriptions = new CompositeDisposable();
        if (getIntent().getExtras() != null && getIntent().hasExtra("id")) {
            groupId = getIntent().getExtras().getString("id");
        }
        getPost();
        toolBarTitle.setText("Announcements");
        goBack.setOnClickListener(v -> onBackPressed());
        listRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AnnouncementAcceptRejectAdapter(this, data);
        listRecyclerView.setAdapter(adapter);

        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            data.clear();
            mSwipeRefreshLayout.setRefreshing(false);
            getPost();
        });

    }//9895120183
    private void getPost() {
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        shimmerFrameLayout.startShimmer();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("group_id", groupId);
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        jsonObject.addProperty("type", "post");
        jsonObject.addProperty("query", query);
        jsonObject.addProperty("offset", "0");
        jsonObject.addProperty("limit", "1000");
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        FamilheeyApplication application = FamilheeyApplication.get(this);
        ApiServices apiServices = RetrofitBase.createRxResource(this, ApiServices.class);

            subscriptions.add(apiServices.getPendingApprovals(requestBody)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(application.defaultSubscribeScheduler())
                    .subscribe(response -> {
                        shimmerFrameLayout.setVisibility(View.GONE);
                        if (response.body().getData().size() == 0) {
                            linearLayout3.setVisibility(View.GONE);
                            layoutEmpty.setVisibility(View.VISIBLE);
                        } else {
                            linearLayout3.setVisibility(View.VISIBLE);
                            layoutEmpty.setVisibility(View.GONE);
                            data.addAll(response.body().getData());
                            adapter.notifyDataSetChanged();
                        }
                    }, throwable -> {
                        shimmerFrameLayout.setVisibility(View.GONE);
                    }));

    }
}


