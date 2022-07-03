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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.familheey.app.CustomViews.TextViews.SemiBoldTextView;
import com.familheey.app.FamilheeyApplication;
import com.familheey.app.Models.Response.PostDataResponse;
import com.familheey.app.Networking.Retrofit.ApiServices;
import com.familheey.app.Networking.Retrofit.RetrofitBase;
import com.familheey.app.R;
import com.familheey.app.Utilities.SharedPref;
import com.familheey.app.Utilities.Utilities;
import com.familheey.app.pagination.PaginationAdapterCallback;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import static com.familheey.app.Utilities.Constants.Bundle.POSITION;

public class MyFamilyPostFragment extends Fragment implements PaginationAdapterCallback, PostAdapter.postItemClick,PostAdapter.postRating {
    public static final int EDIT_REQUEST_CODE = 1002;
    public static final int CHAT_REQUEST_CODE = 1003;
    List<PostData> data = new ArrayList<>();
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private final int TOTAL_PAGES = 5;
    private int currentPage = 0;
    private Integer prev_id = 0;
    private String starCount;
    private String rateCount;

    private HashTagSearch mListener;
    @BindView(R.id.myfamilypostlist)
    RecyclerView myFamilyList;
    @BindView(R.id.emptyResultText)
    SemiBoldTextView emptyResultText;
    private PostAdapter adapter;

    @BindView(R.id.layoutEmpty)
    LinearLayout layoutEmpty;

    private String query = "";
    @BindView(R.id.refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.shimmer_view_container)
    com.facebook.shimmer.ShimmerFrameLayout shimmerFrameLayout;
    //final EditText search;
    public CompositeDisposable subscriptions;
    private ConstraintLayout constraintSearch;

    public MyFamilyPostFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        subscriptions = new CompositeDisposable();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_family_post, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        adapter = new PostAdapter(this, getActivity(), data, this, "PRIVATE", false,this);
        setupRecyclerView();
        initListener();

        shimmerFrameLayout.setVisibility(View.VISIBLE);
        shimmerFrameLayout.startShimmer();
        //getPost();
        String postData = SharedPref.read(SharedPref.POST_DATA, "");
        SharedPref.write(SharedPref.POST_DATA, "");
        if(!postData.equals("")) {
            PostDataResponse datas = new Gson().fromJson(postData, PostDataResponse.class);
            datas.getData().removeAll(Collections.singleton(null));
            if (datas != null && datas.getData() != null && datas.getData().size() > 0) {
                data.addAll(datas.getData());
                if (currentPage > 0) {
                    adapter.removeLoadingFooter();
                    isLoading = false;
                }
                /*(28/08/2021)megha-> post rating is enabled or not */
                for (int i=0;i<data.size();i++){
                    Boolean rate = data.get(i).rating_enabled;
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
                currentPage = data.size();
                mListener.newAnnouncement(true);
                adapter.addLoadingFooter();

            } else {
                isLastPage = true;
            }
            if (data.size() > 0) {
                layoutEmpty.setVisibility(View.GONE);
                emptyResultText.setVisibility(View.GONE);

            } else {
                if (query.trim().length() > 0) {
                    emptyResultText.setVisibility(View.VISIBLE);
                    layoutEmpty.setVisibility(View.GONE);
                } else {
                    emptyResultText.setVisibility(View.GONE);
                    layoutEmpty.setVisibility(View.VISIBLE);
                }
            }
            adapter.notifyDataSetChanged();
            shimmerFrameLayout.setVisibility(View.GONE);
            shimmerFrameLayout.stopShimmer();
        }
        else{
            getPost();
        }

    }

    private void initListener() {
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
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void getPost() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        jsonObject.addProperty("type", "post");
        jsonObject.addProperty("query", query);
        jsonObject.addProperty("offset", currentPage + "");
        jsonObject.addProperty("limit", "20");
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        FamilheeyApplication application = FamilheeyApplication.get(getActivity());
        ApiServices apiServices = RetrofitBase.createRxResource(getActivity(), ApiServices.class);
        assert application != null;
        subscriptions.add(apiServices.getPost(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(response -> {
                    if (currentPage > 0) {
                        adapter.removeLoadingFooter();
                        isLoading = false;
                    }
                    assert response.body() != null;
                    response.body().getData().removeAll(Collections.singleton(null));
                    if (response.body() != null && response.body().getData() != null && response.body().getData().size() > 0) {
                        data.addAll(response.body().getData());
                        /*(28/08/2021)megha-> post rating is enabled or not */
                        for (int i=0;i<data.size();i++){
                            Boolean rate = data.get(i).rating_enabled;
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
                        currentPage = data.size();
                        mListener.newAnnouncement(true);
                        adapter.addLoadingFooter();

                    } else {
                        isLastPage = true;
                    }


                    if (data.size() > 0) {
                        layoutEmpty.setVisibility(View.GONE);
                        emptyResultText.setVisibility(View.GONE);

                    } else {
                        if (query.trim().length() > 0) {
                            emptyResultText.setVisibility(View.VISIBLE);
                            layoutEmpty.setVisibility(View.GONE);
                        } else {
                            emptyResultText.setVisibility(View.GONE);
                            layoutEmpty.setVisibility(View.VISIBLE);
                        }
                    }

                    adapter.notifyDataSetChanged();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    shimmerFrameLayout.stopShimmer();
                }, throwable -> {
                    adapter.showRetry(true, null);
                    shimmerFrameLayout.setVisibility(View.GONE);
                    shimmerFrameLayout.stopShimmer();
                }));
    }

    /* megha(03/09/21) for rating api*/
    private void onItemRating(JsonObject jsonObject, int position,String rating) {

        SweetAlertDialog progressDialog = Utilities.getProgressDialog(getActivity());
        progressDialog.show();
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        FamilheeyApplication application = FamilheeyApplication.get(getActivity());
        ApiServices apiServices = RetrofitBase.createRxResource(getActivity(), ApiServices.class);
        subscriptions.add(apiServices.rate(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(response -> {
                    JSONObject json= (JSONObject) new JSONTokener(response.body().string()).nextValue();
                    JSONObject json2 = json.getJSONObject("data");
                    rateCount = (String) json2.get("total_rating");

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

    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        myFamilyList.setLayoutManager(layoutManager);
        myFamilyList.setItemAnimator(new DefaultItemAnimator());
        myFamilyList.setAdapter(adapter);
        myFamilyList.addOnScrollListener(new com.familheey.app.pagination.PaginationScrollListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;

                getPost();
            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {

                    int c = layoutManager.findFirstVisibleItemPosition();
                    if (c > 0 && data.size() >= c) {
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

    @Override
    public void retryPageLoad() {
        getPost();
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
        getPost();
    }
    public void setmListener(HashTagSearch mListener) {
        this.mListener = mListener;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            assert data != null;
            int pos = data.getExtras().getInt("pos");
            PostData postData = new Gson().fromJson(data.getExtras().getString("data"), PostData.class);
            this.data.get(pos).setPost_attachment(postData.getPost_attachment());
            this.data.get(pos).setIs_shareable(postData.getIs_shareable());
            this.data.get(pos).setShared_user_count(postData.getShared_user_count());
            this.data.get(pos).setSnap_description(postData.getSnap_description());
            this.data.get(pos).setConversation_enabled(postData.getConversation_enabled());
            this.data.get(pos).setRating_enabled(postData.getRating_enabled());
            this.data.get(pos).setIs_viewed(postData.getIs_viewed());
            this.data.get(pos).setViews_count(postData.getViews_count());
            adapter.notifyDataSetChanged();
        } else if (requestCode == CHAT_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            assert data != null;
            if (data.getExtras().getBoolean("isChat")) {
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

    private void addViewCount(String post_id) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        jsonObject.addProperty("post_id", post_id);
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        FamilheeyApplication application = FamilheeyApplication.get(getActivity());
        ApiServices apiServices = RetrofitBase.createRxResource(getActivity(), ApiServices.class);
        subscriptions.add(apiServices.addViewCount(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(response -> {
                }, throwable -> {
                }));
    }

    @Override
    public void onEditActivity(Intent intent) {
        startActivityForResult(intent, EDIT_REQUEST_CODE);
        requireActivity().overridePendingTransition(R.anim.enter,
                R.anim.exit);
    }

    @Override
    public void makeAsSticky(int poston) {

    }

    @Override
    public void onChatActivity(Intent intent) {
        startActivityForResult(intent, CHAT_REQUEST_CODE);
        requireActivity().overridePendingTransition(R.anim.enter,
                R.anim.exit);
    }
/**handles search based on hashtag text passed**/
    @Override
    public void onSearchTag(String hashtag) {
        searchPost(hashtag);
        mListener.onSearchTag(hashtag);
    }

    public void pauseVideo() {
        if (adapter != null)
            adapter.notifyDataSetChanged();
    }
    //Dinu(18-03-2021)
    @Override
    public void onPause() {

        adapter.notifyDataSetChanged();
        super.onPause();
    }

    @Override
    public void onRating(int position, JsonObject jsonObject,String rating) {
        onItemRating(jsonObject,position,rating);
    }

}


