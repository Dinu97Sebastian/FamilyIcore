package com.familheey.app.Fragments.Posts;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.familheey.app.FamilheeyApplication;
import com.familheey.app.Models.Response.FamilyMember;
import com.familheey.app.Networking.Retrofit.ApiServices;
import com.familheey.app.Networking.Retrofit.RetrofitBase;
import com.familheey.app.R;
import com.familheey.app.Utilities.SharedPref;
import com.familheey.app.Utilities.Utilities;
import com.familheey.app.pagination.PaginationAdapterCallback;
import com.familheey.app.pagination.PaginationScrollListener;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnEditorAction;
import cn.pedant.SweetAlert.SweetAlertDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import static com.familheey.app.Utilities.Constants.Bundle.POSITION;

public class OnlyMePostFragment extends Fragment implements PaginationAdapterCallback, UserPostAdapter.OnSearchQueryChangedListener,UserPostAdapter.postRating,UserPostAdapter.postItemClick {

    public CompositeDisposable subscriptions;
    @BindView(R.id.searchPost)
    EditText searchPost;
    @BindView(R.id.clearSearch)
    ImageView clearSearch;

    @BindView(R.id.layoutNoPost)
    LinearLayout layoutNoPost;

    @BindView(R.id.postList)
    RecyclerView postList;
    private final List<PostData> data = new ArrayList<>();
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    public static final int EDIT_REQUEST_CODE = 1002;
    public static final int CHAT_REQUEST_CODE = 1003;

    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES = 5;
    private int currentPage = 0;
    private String query = "";
    private UserPostAdapter adapter;
    private Integer prev_id=0;
    private String starCount;
    private String user_id;
    private String rateCount;
    private RatingBar ratingBar;
    private HashTagSearch mListener;
    public OnlyMePostFragment() {
        // Required empty public constructor
    }

    public static OnlyMePostFragment newInstance(FamilyMember familyMember) {
        OnlyMePostFragment fragment = new OnlyMePostFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        subscriptions = new CompositeDisposable();
        assert getArguments() != null;
        getArguments().clear();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_only_me_post, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new UserPostAdapter(getContext(), data, this, this,this,this);
        setupRecyclerView();
        initializeSearchClearCallback();
        getPost();
    }
    public void setmListener(HashTagSearch mListener) {
        this.mListener = mListener;
    }
    private void initializeSearchClearCallback() {
        searchPost.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0)
                    clearSearch.setVisibility(View.INVISIBLE);
                else clearSearch.setVisibility(View.VISIBLE);
            }
        });
        clearSearch.setOnClickListener(v -> {
            searchPost.setText("");
            onSearchQueryListener(EditorInfo.IME_ACTION_SEARCH);
        });
    }

    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        postList.setLayoutManager(layoutManager);
        postList.setItemAnimator(new DefaultItemAnimator());
        postList.setAdapter(adapter);
        postList.addOnScrollListener(new PaginationScrollListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                //currentPage = data.size();
                getPost();
            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if(newState==RecyclerView.SCROLL_STATE_IDLE){
                    int c = layoutManager.findFirstVisibleItemPosition();
                    if (c > 0) {
                        if (data.get(c) != null) {
                            if (data.get(c).getPost_id() != null) {
                                if(!(prev_id.equals(data.get(c).getPost_id()))) {
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

    private void getPost() {
        if (currentPage == 0) {
            progressBar.setVisibility(View.VISIBLE);
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        jsonObject.addProperty("type", "post");
        jsonObject.addProperty("query", query);
        jsonObject.addProperty("offset", currentPage + "");
        jsonObject.addProperty("limit", "30");
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        FamilheeyApplication application = FamilheeyApplication.get(getActivity());
        ApiServices apiServices = RetrofitBase.createRxResource(getActivity(), ApiServices.class);
        subscriptions.add(apiServices.getMyPost(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(response -> {
                    if (currentPage > 0) {
                        adapter.removeLoadingFooter();
                        isLoading = false;
                    }


                    progressBar.setVisibility(View.GONE);
                    if (response.body() != null) {
                        data.addAll(response.body().getData());
                        for (int i=0;i<data.size();i++) {
                            Boolean rate = data.get(i).rating_enabled;
                            if (rate == null) {
                                data.get(i).setRating_enabled(false);
                            } else if (rate) {
                                data.get(i).setRating_enabled(true);
                                data.get(i).setRating(data.get(i).getRating());
                                data.get(i).setRating_by_user(data.get(i).getRating_by_user());
                                data.get(i).setRating_count(data.get(i).getRating_count());
                            } else if (!rate) {
                                data.get(i).setRating_enabled(false);
                            }
                        }
                        currentPage = data.size();
                        if (data.size() == 0) {
                            layoutNoPost.setVisibility(View.VISIBLE);
                        } else {
                            layoutNoPost.setVisibility(View.GONE);

                        }
                    }

                    adapter.notifyDataSetChanged();

                    if (response.body().getData().size() == 150) adapter.addLoadingFooter();
                    else isLastPage = true;

                }, throwable -> {
                    progressBar.setVisibility(View.GONE);
                    adapter.showRetry(true, null);
                }));
    }

    public void searchPost(String query) {
        this.query = query;
        isLastPage = false;
        data.clear();
        currentPage = 0;
        getPost();
    }

    @OnEditorAction(R.id.searchPost)
    protected boolean onSearchQueryListener(int actionId) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            searchPost(searchPost.getText().toString());
            try {
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchPost.getWindowToken(), 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }

    @Override
    public void retryPageLoad() {
        getPost();
    }

    @Override
    public void onSearchQueryChanged(String searchQuery) {
        searchPost.setText(searchQuery);
        onSearchQueryListener(EditorInfo.IME_ACTION_SEARCH);
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

    @Override
    public void onResume() {
        super.onResume();
       /* isLoading = false;
        isLastPage = false;
        TOTAL_PAGES = 5;
        currentPage = 0;
        query = "";
        if (data != null)
            data.clear();
        if (adapter != null)
            adapter.notifyDataSetChanged();
        getPost();*/
    }

    public void onPause() {
        super.onPause();
        if (adapter != null)
            adapter.notifyDataSetChanged();
    }

    @Override
    public void onRating(int position, JsonObject jsonObject,String rating) {
        onItemRating(jsonObject,position,rating);
    }

    @Override
    public void onEditActivity(Intent intent) {
        startActivityForResult(intent, EDIT_REQUEST_CODE);
        requireActivity().overridePendingTransition(R.anim.enter,
                R.anim.exit);
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
        searchPost.setText(hashtag);
        clearSearch.setVisibility(View.VISIBLE);
        searchPost(hashtag);
        //mListener.onSearchTag(hashtag);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener=null;
    }
}
