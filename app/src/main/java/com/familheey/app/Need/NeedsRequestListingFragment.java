package com.familheey.app.Need;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.familheey.app.CustomViews.TextViews.SemiBoldTextView;
import com.familheey.app.Decorators.BottomAdditionalMarginDecorator;
import com.familheey.app.Networking.Retrofit.ApiServices;
import com.familheey.app.Networking.Retrofit.RetrofitBase;
import com.familheey.app.R;
import com.familheey.app.Utilities.SharedPref;
import com.familheey.app.pagination.PaginationAdapterCallback;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class NeedsRequestListingFragment extends Fragment implements PaginationAdapterCallback {

    @BindView(R.id.layoutEmpty)
    LinearLayout layoutEmpty;
    @BindView(R.id.needRequestList)
    RecyclerView needRequestList;
    @BindView(R.id.shimmerLoader)
    ShimmerFrameLayout shimmerLoader;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.emptyResultText)
    SemiBoldTextView emptyResultText;

    private String query = "";
    private CompositeDisposable subscriptions;
    private NeedsRequestAdapter needsRequestAdapter;
    private List<Need> needRequests = new ArrayList<>();
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES = 5;
    private int currentPage = 0;

    public NeedsRequestListingFragment() {
        // Required empty public constructor
    }

    public static NeedsRequestListingFragment newInstance() {
        NeedsRequestListingFragment fragment = new NeedsRequestListingFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_needs_request_listing, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeOthers();
        initializeAdapter();
        getNeedList();
    }

    private void initializeOthers() {
        subscriptions = new CompositeDisposable();
        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(false);
            needRequests.clear();
            currentPage = 0;
            isLastPage = false;
            needsRequestAdapter.notifyDataSetChanged();
            getNeedList();
        });
    }

    private void initializeAdapter() {
        needsRequestAdapter = new NeedsRequestAdapter(this, needRequests, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        needRequestList.setLayoutManager(layoutManager);
        needRequestList.setItemAnimator(new DefaultItemAnimator());
        needRequestList.setAdapter(needsRequestAdapter);

        needRequestList.addItemDecoration(new BottomAdditionalMarginDecorator());

        needRequestList.addOnScrollListener(new com.familheey.app.pagination.PaginationScrollListener(layoutManager) {

            @Override
            protected void loadMoreItems() {
                isLoading = true;
                getNeedList();
            }


            @Override
            public int getTotalPageCount() {
                return TOTAL_PAGES;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });
    }

    private void getNeedList() {
        if (currentPage == 0) {
            showShimmer();
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        jsonObject.addProperty("txt", query);
        jsonObject.addProperty("offset", currentPage + "");
        jsonObject.addProperty("limit", "10");
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        final ApiServices authService = RetrofitBase.createRxResource(getContext(), ApiServices.class);
        subscriptions.add(authService.getNeedsRequestList(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(response -> {
                    assert response.body() != null;
                    needRequests.addAll(response.body().getNeedRequests());
                    currentPage = needRequests.size();
                    if (response.body().getNeedRequests().size() < 10) {
                        isLastPage = true;
                    }
                    if (needRequests.size() > 0) {
                        isLoading = false;
                        layoutEmpty.setVisibility(View.INVISIBLE);
                        emptyResultText.setVisibility(View.INVISIBLE);
                    } else {
                        if (query.length() > 0) {
                            layoutEmpty.setVisibility(View.INVISIBLE);
                            emptyResultText.setVisibility(View.VISIBLE);
                        } else {
                            layoutEmpty.setVisibility(View.VISIBLE);
                            emptyResultText.setVisibility(View.INVISIBLE);
                        }
                    }
                    needsRequestAdapter.notifyDataSetChanged();
                    hideShimmer();
                }, throwable -> {
                    try {
                        hideShimmer();
                        networkError();
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Please try again", Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    private void showShimmer() {
        layoutEmpty.setVisibility(View.GONE);
        emptyResultText.setVisibility(View.GONE);
        if (shimmerLoader != null) {
            shimmerLoader.setVisibility(View.VISIBLE);
            shimmerLoader.startShimmer();
        }
    }

    private void hideShimmer() {
        if (shimmerLoader != null) {
            shimmerLoader.stopShimmer();
            shimmerLoader.setVisibility(View.GONE);
        }
    }

    private void networkError() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Uh oh! Check your internet connection and retry.")
                .setCancelable(false)
                .setPositiveButton("Retry", (dialog, which) -> getNeedList()).setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {//Just Detail
            if (resultCode == Activity.RESULT_OK) {
                clearAndSearch();
            }
        } else if (requestCode == 1001) {//Edit
            clearAndSearch();
        }
    }

    public void searchNeeds(String query) {
        this.query = query;
        clearAndSearch();
    }

    private void clearAndSearch() {
        needRequests.clear();
        currentPage = 0;
        isLastPage = false;
        needsRequestAdapter.notifyDataSetChanged();
        getNeedList();
    }

    @Override
    public void retryPageLoad() {

    }
}