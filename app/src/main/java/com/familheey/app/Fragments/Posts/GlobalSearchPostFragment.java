package com.familheey.app.Fragments.Posts;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.familheey.app.CustomViews.TextViews.SemiBoldTextView;
import com.familheey.app.Interfaces.GlobalSearchListener;
import com.familheey.app.Interfaces.RetrofitListener;
import com.familheey.app.Models.ApiCallbackParams;
import com.familheey.app.Models.ErrorData;
import com.familheey.app.Networking.Retrofit.ApiServiceProvider;
import com.familheey.app.Networking.utils.GsonUtils;
import com.familheey.app.R;
import com.familheey.app.Utilities.SharedPref;
import com.familheey.app.Utilities.Utilities;
import com.familheey.app.pagination.PaginationAdapterCallback;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;

import static com.familheey.app.Utilities.Constants.Bundle.POSITION;

public class GlobalSearchPostFragment extends Fragment implements PaginationAdapterCallback, PostAdapter.postItemClick, RetrofitListener {
    public static final int EDIT_REQUEST_CODE = 1002;
    public static final int CHAT_REQUEST_CODE = 1003;
    public CompositeDisposable subscriptions;
    List<PostData> data = new ArrayList<>();
    @BindView(R.id.myfamilypostlist)
    RecyclerView myFamilyList;
    @BindView(R.id.layoutEmpty)
    LinearLayout layoutEmpty;
    @BindView(R.id.refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.searchLabelIndicator)
    TextView searchLabelIndicator;
    @BindView(R.id.emptyResultText)
    SemiBoldTextView emptyResultText;
    private GlobalSearchListener globalSearchListener;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private final int TOTAL_PAGES = 5;
    private int currentPage = 0;
    private PostAdapter adapter;
    /*@BindView(R.id.buttonSearchFamily)
    Button buttonSearchFamily;*/
    private String query = "";


    public GlobalSearchPostFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        subscriptions = new CompositeDisposable();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_global_search_post, container, false);
        ButterKnife.bind(this, view);

        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        adapter = new PostAdapter(this, getActivity(), data, this, "PUBLIC", false);
        setupRecyclerView();
        mSwipeRefreshLayout.setEnabled(false);
        if (progressBar != null)
            progressBar.setVisibility(View.VISIBLE);
    }

    /*private void initListener() {
       // buttonSearchFamily.setOnClickListener(v -> myFamilyPostInterface.onSearchFamilyClicked());
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
    }*/

    public void getPost(String query) {
        this.query = query;
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("userid", SharedPref.getUserRegistration().getId());//
        jsonObject.addProperty("type", "posts");
        jsonObject.addProperty("searchtxt", query);
        /*jsonObject.addProperty("offset", currentPage + "");
        jsonObject.addProperty("limit", "5");*/
        jsonObject.addProperty("offset", "0");
        jsonObject.addProperty("limit", "10000");
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getContext());
        apiServiceProvider.searchData(jsonObject, null, this);
    }

    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        myFamilyList.setLayoutManager(layoutManager);
        myFamilyList.setItemAnimator(new DefaultItemAnimator());
        myFamilyList.setAdapter(adapter);
        /*myFamilyList.addOnScrollListener(new com.familheey.app.pagination.PaginationScrollListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage = data.size();
                int c = layoutManager.findFirstVisibleItemPosition();
                if (c > 0) {
                    if (data.get(c) != null) {
                        if (data.get(c).getPost_id() != null) {
                            addViewCount(data.get(c).getPost_id() + "");
                        }
                    }
                }
                getPost();
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
        });*/

    }

    @Override
    public void retryPageLoad() {
        getPost(query);
    }

    public void searchPost(String query) {
        this.query = query;
        data.clear();
        currentPage = 0;
        isLoading = false;
        isLastPage = false;
        setupRecyclerView();
        mSwipeRefreshLayout.setRefreshing(false);
        if (progressBar != null)
            progressBar.setVisibility(View.VISIBLE);
        getPost(query);
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        globalSearchListener = Utilities.getListener(this, GlobalSearchListener.class);
        if (globalSearchListener == null) {
            throw new ClassCastException("GlobalSearchPostFragment should implement GlobalSearchListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        globalSearchListener = null;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            int pos = data.getExtras().getInt("pos");
            PostData postData = new Gson().fromJson(data.getExtras().getString("data"), PostData.class);
            this.data.get(pos).setPost_attachment(postData.getPost_attachment());
            this.data.get(pos).setIs_shareable(postData.getIs_shareable());
            this.data.get(pos).setSnap_description(postData.getSnap_description());
            this.data.get(pos).setConversation_enabled(postData.getConversation_enabled());
            adapter.notifyDataSetChanged();
        } else if (requestCode == CHAT_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            if (data.getExtras().getBoolean("isChat")) {
                this.data.clear();
                currentPage = 0;
                isLoading = false;
                isLastPage = false;
                setupRecyclerView();
                mSwipeRefreshLayout.setRefreshing(false);
                if (progressBar != null)
                    progressBar.setVisibility(View.VISIBLE);
                getPost(query);
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
    }

    @Override
    public void onSearchTag(String hashtag) {
        globalSearchListener.performSearch(hashtag);
    }

    public void updatePost(List<PostData> posts) {
        data.clear();
        data.addAll(posts);
        adapter.notifyDataSetChanged();
        searchLabelIndicator.setVisibility(View.VISIBLE);
        if (progressBar != null)
            progressBar.setVisibility(View.INVISIBLE);
        if (data.size() == 0)
            emptyResultText.setVisibility(View.VISIBLE);
        else
            emptyResultText.setVisibility(View.INVISIBLE);
    }

    public void updateSearchIndication(String searchText) {
        if (searchLabelIndicator == null) return;
        if (searchText.length() == 0) {
            searchLabelIndicator.setText("Suggested");
        } else {
            searchLabelIndicator.setText("Showing results for \"" + searchText + "\"");
        }
        query = searchText;
        data.clear();
        adapter.notifyDataSetChanged();
        searchLabelIndicator.setVisibility(View.VISIBLE);
        if (progressBar != null)
            progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {
        try {
            JSONObject mainObject = new JSONObject(responseBodyString);
            JSONObject postObject = mainObject.getJSONObject("posts");
            JSONArray peopleArray = postObject.getJSONArray("data");
            data.clear();
            data.addAll(GsonUtils.getInstance().getGson().fromJson(peopleArray.toString(), new TypeToken<ArrayList<PostData>>() {
            }.getType()));
            adapter.notifyDataSetChanged();
            //
            searchLabelIndicator.setVisibility(View.VISIBLE);
            if (progressBar != null)
                progressBar.setVisibility(View.INVISIBLE);
            if (data.size() == 0)
                emptyResultText.setVisibility(View.VISIBLE);
            else
                emptyResultText.setVisibility(View.INVISIBLE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
        adapter.showRetry(true, null);
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void makeAsSticky(int poston) {

    }
}


