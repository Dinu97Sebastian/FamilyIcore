package com.familheey.app.Fragments.Posts;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.familheey.app.CustomViews.TextViews.SemiBoldTextView;
import com.familheey.app.FamilheeyApplication;
import com.familheey.app.Networking.Retrofit.ApiServices;
import com.familheey.app.Networking.Retrofit.RetrofitBase;
import com.familheey.app.R;
import com.familheey.app.Utilities.SharedPref;
import com.familheey.app.pagination.PaginationAdapterCallback;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import static com.familheey.app.Utilities.Constants.Bundle.POSITION;

public class PublicFeedPostFragment extends Fragment implements PaginationAdapterCallback, PostAdapter.postItemClick {
    public static final int EDIT_REQUEST_CODE = 1002;
    public static final int CHAT_REQUEST_CODE = 1003;
    private final List<PostData> data = new ArrayList<>();
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private final int TOTAL_PAGES = 5;
    private int currentPage = 0;
    private Integer prev_id = 0;
    @BindView(R.id.emptyResultText)
    SemiBoldTextView emptyResultText;
    private PostAdapter adapter;

    private HashTagSearch mListener;
    @BindView(R.id.refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.shimmer_view_container)
    com.facebook.shimmer.ShimmerFrameLayout shimmerFrameLayout;

    @BindView(R.id.myfamilypostlist)
    RecyclerView myFamilyList;

    @BindView(R.id.layoutEmpty)
    LinearLayout layoutEmpty;
    private String query = "";

    public CompositeDisposable subscriptions;

    public PublicFeedPostFragment() {
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        adapter = new PostAdapter(this, getActivity(), data, this, "PUBLIC", false);
        setupRecyclerView();

        shimmerFrameLayout.setVisibility(View.VISIBLE);
        shimmerFrameLayout.startShimmer();
        getPost();
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            data.clear();
            currentPage = 0;
            isLoading = false;
            isLastPage = false;
            setupRecyclerView();
            mSwipeRefreshLayout.setRefreshing(false);
            shimmerFrameLayout.setVisibility(View.VISIBLE);
            shimmerFrameLayout.startShimmer();
            getPost();
        });
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        subscriptions = new CompositeDisposable();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_public_feed_post, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void makeAsSticky(int poston) {

    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);

    }

    private void getPost() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        jsonObject.addProperty("type", "post");
        jsonObject.addProperty("query", query);
        jsonObject.addProperty("offset", currentPage + "");
        jsonObject.addProperty("limit", "30");
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        FamilheeyApplication application = FamilheeyApplication.get(getActivity());
        ApiServices apiServices = RetrofitBase.createRxResource(getActivity(), ApiServices.class);
        assert application != null;
        subscriptions.add(apiServices.getpublicfeed(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(response -> {
                    if (currentPage > 0) {
                        adapter.removeLoadingFooter();
                        isLoading = false;
                    }
                    assert response.body() != null;
                    if (response.body().getData() != null) {
                        data.addAll(response.body().getData());
                    }

                    currentPage = data.size();
                    if (data.size() == 0) {
                        if (query.trim().length() > 0)
                            emptyResultText.setVisibility(View.VISIBLE);
                        else
                            layoutEmpty.setVisibility(View.VISIBLE);
                    } else {
                        layoutEmpty.setVisibility(View.GONE);
                        emptyResultText.setVisibility(View.GONE);
                    }

                    adapter.notifyDataSetChanged();

                    if (response.body().getData().size() > 0) adapter.addLoadingFooter();
                    else isLastPage = true;

                    shimmerFrameLayout.setVisibility(View.GONE);
                }, throwable -> {
                    adapter.showRetry(true, null);
                    shimmerFrameLayout.setVisibility(View.GONE);
                }));
    }

    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        myFamilyList.setLayoutManager(layoutManager);
        myFamilyList.setItemAnimator(new DefaultItemAnimator());
        myFamilyList.setAdapter(adapter);
        myFamilyList.addOnScrollListener(new com.familheey.app.pagination.PaginationScrollListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                //currentPage = data.size();
                getPost();
            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    int c = layoutManager.findFirstVisibleItemPosition();
                    if (c > 0) {
                        if (data.size() >= c)
                            if (data.get(c) != null) {
                                if (data.get(c).getPost_id() != null) {
                                    if (!(prev_id.equals(data.get(c).getPost_id()))) {
                                        addViewCount(data.get(c).getPost_id() + "");
                                        prev_id = data.get(c).getPost_id();
                                    }
                                }
                            }
                    }
                }
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

    public void setmListener(HashTagSearch mListener) {
        this.mListener = mListener;
    }

    public void searchPost(String query) {
        this.query = query;
        data.clear();
        currentPage = 0;
        isLoading = false;
        isLastPage = false;
        setupRecyclerView();
        mSwipeRefreshLayout.setRefreshing(false);
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        shimmerFrameLayout.startShimmer();
        layoutEmpty.setVisibility(View.GONE);
        emptyResultText.setVisibility(View.GONE);
        getPost();
    }

    @Override
    public void retryPageLoad() {
        getPost();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            assert data != null;
            int pos = Objects.requireNonNull(data.getExtras()).getInt("pos");
            PostData postData = new Gson().fromJson(data.getExtras().getString("data"), PostData.class);
            this.data.get(pos).setPost_attachment(postData.getPost_attachment());
            this.data.get(pos).setIs_shareable(postData.getIs_shareable());
            this.data.get(pos).setSnap_description(postData.getSnap_description());
            this.data.get(pos).setConversation_enabled(postData.getConversation_enabled());
            adapter.notifyDataSetChanged();
        } else if (requestCode == CHAT_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            assert data != null;
            if (Objects.requireNonNull(data.getExtras()).getBoolean("isChat")) {
                this.data.clear();
                currentPage = 0;
                isLoading = false;
                isLastPage = false;
                setupRecyclerView();
                mSwipeRefreshLayout.setRefreshing(false);
                shimmerFrameLayout.setVisibility(View.VISIBLE);
                shimmerFrameLayout.startShimmer();
                layoutEmpty.setVisibility(View.GONE);
                emptyResultText.setVisibility(View.GONE);
                getPost();
            } else {
                this.data.get(data.getExtras().getInt(POSITION)).setConversation_count_new("0");
                adapter.notifyDataSetChanged();
            }
        }


    }

    @Override
    public void onEditActivity(Intent intent) {
        startActivityForResult(intent, EDIT_REQUEST_CODE);
    }

    @Override
    public void onChatActivity(Intent intent) {
        startActivityForResult(intent, CHAT_REQUEST_CODE);
        requireActivity().overridePendingTransition(R.anim.enter,
                R.anim.exit);
    }

    @Override
    public void onSearchTag(String hashtag) {
        searchPost(hashtag);
        mListener.onSearchTag(hashtag);
        //Utilities.showCircularReveal(constraintSearch);
        //search.setText(hashtag);
    }


    private void addViewCount(String post_id) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        jsonObject.addProperty("post_id", post_id);
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        FamilheeyApplication application = FamilheeyApplication.get(getActivity());
        ApiServices apiServices = RetrofitBase.createRxResource(getActivity(), ApiServices.class);
        assert application != null;
        subscriptions.add(apiServices.addViewCount(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(response -> {
                }, throwable -> {
                }));
    }

    void pauseVideo() {
        if (adapter != null)
            adapter.notifyDataSetChanged();
    }
    //Dinu(18-03-2021)
    @Override
    public void onPause() {
        super.onPause();
        if (adapter != null)
            adapter.notifyDataSetChanged();
    }
}
