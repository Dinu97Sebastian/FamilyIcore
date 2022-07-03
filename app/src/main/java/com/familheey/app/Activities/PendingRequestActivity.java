package com.familheey.app.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.familheey.app.Adapters.EventGuest.CancelRequestAdapter;
import com.familheey.app.FamilheeyApplication;
import com.familheey.app.Models.Response.Family;
import com.familheey.app.Networking.Retrofit.ApiServices;
import com.familheey.app.Networking.Retrofit.RetrofitBase;
import com.familheey.app.R;
import com.familheey.app.Utilities.SharedPref;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class PendingRequestActivity extends AppCompatActivity {


    public CompositeDisposable subscriptions;
    @BindView(R.id.shimmer_view_container)
    com.facebook.shimmer.ShimmerFrameLayout shimmerFrameLayout;
    @BindView(R.id.refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.list_recyclerView)
    RecyclerView listRecyclerView;
    @BindView(R.id.progressListMember)
    ProgressBar progressListMember;
    @BindView(R.id.toolBarTitle)
    TextView toolBarTitle;
    @BindView(R.id.goBack)
    ImageView goBack;
    @BindView(R.id.layoutEmpty)
    LinearLayout layoutEmpty;
    @BindView(R.id.linearLayout3)
    RelativeLayout linearLayout3;
    ArrayList<Family> data = new ArrayList<>();
    CancelRequestAdapter cancelRequestAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancel_request);
        ButterKnife.bind(this);

        subscriptions = new CompositeDisposable();
        getPendingRequest();
        toolBarTitle.setText("Pending Requests");
        goBack.setOnClickListener(v -> onBackPressed());
        listRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        cancelRequestAdapter = new CancelRequestAdapter(this, data);
        listRecyclerView.setAdapter(cancelRequestAdapter);

        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            data.clear();
            cancelRequestAdapter.notifyDataSetChanged();
            mSwipeRefreshLayout.setRefreshing(false);
            getPendingRequest();
        });


    }


    private void getPendingRequest() {
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        shimmerFrameLayout.startShimmer();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        jsonObject.addProperty("type", "request");
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        FamilheeyApplication application = FamilheeyApplication.get(this);
        ApiServices apiServices = RetrofitBase.createRxResource(this, ApiServices.class);
        subscriptions.add(apiServices.getPendingRequest(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(response -> {
                    shimmerFrameLayout.setVisibility(View.GONE);
                    assert response.body() != null;
                    if (response.body().getData().size() == 0) {
                        linearLayout3.setVisibility(View.GONE);
                        layoutEmpty.setVisibility(View.VISIBLE);
                    } else {
                        linearLayout3.setVisibility(View.VISIBLE);
                        layoutEmpty.setVisibility(View.GONE);
                        data.addAll(response.body().getData());
                        cancelRequestAdapter.notifyDataSetChanged();
                    }
                }, throwable -> {
                    shimmerFrameLayout.setVisibility(View.GONE);
                    if (throwable instanceof IOException) {
                        networkError();
                    }
                }));


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }


    private void networkError() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Uh oh! Check your internet connection and retry.")
                .setCancelable(false)
                .setPositiveButton("Retry", (dialog, which) -> getPendingRequest()).setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
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


}


