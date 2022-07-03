package com.familheey.app.Announcement;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.familheey.app.FamilheeyApplication;
import com.familheey.app.Fragments.Posts.PostData;
import com.familheey.app.Networking.Retrofit.ApiServices;
import com.familheey.app.Networking.Retrofit.RetrofitBase;
import com.familheey.app.R;
import com.familheey.app.Utilities.SharedPref;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnEditorAction;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import static com.familheey.app.Utilities.Constants.Bundle.POSITION;
import static com.familheey.app.Utilities.Utilities.hideCircularReveal;
import static com.familheey.app.Utilities.Utilities.showCircularReveal;

public class AnnouncementListingActivity extends AppCompatActivity {


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

    @BindView(R.id.constraintSearch)
    ConstraintLayout constraintSearch;
    @BindView(R.id.imageBack)
    ImageView imageBack;
    @BindView(R.id.searchQuery)
    EditText searchQuery;
    @BindView(R.id.clearSearch)
    ImageView clearSearch;
    @BindView(R.id.imgSearch)
    ImageView imgSearch;

    public CompositeDisposable subscriptions;
    String query = "";
    List<PostData> data = new ArrayList<>();
    String groupId = "";
    AnnouncementAdapter announcementAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcement_listing);
        ButterKnife.bind(this);
        initListener();
        if (getIntent().getExtras() != null && getIntent().hasExtra("id")) {
            groupId = getIntent().getExtras().getString("id");
        }
        subscriptions = new CompositeDisposable();
        getAnnouncement();
        toolBarTitle.setText("Announcements");
        goBack.setOnClickListener(v -> onBackPressed());
        initializeSearchClearCallback();
        LinearLayoutManager manager = new LinearLayoutManager(this);
        listRecyclerView.setLayoutManager(manager);
        listRecyclerView.setAdapter(announcementAdapter);
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            data.clear();
            announcementAdapter.notifyDataSetChanged();
            mSwipeRefreshLayout.setRefreshing(false);
            getAnnouncement();
        });

        addUpdate_Announcement_Seen();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left,
                R.anim.right);
    }

    private void initializeSearchClearCallback() {
        searchQuery.addTextChangedListener(new TextWatcher() {

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
            searchQuery.setText("");
            onSearchQueryListener(EditorInfo.IME_ACTION_SEARCH);
        });
    }

    private void getAnnouncement() {
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        shimmerFrameLayout.startShimmer();
        JsonObject jsonObject = new JsonObject();
        if (groupId.length() > 0) {
            jsonObject.addProperty("group_id", groupId);
        }
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        jsonObject.addProperty("type", "announcement");
        jsonObject.addProperty("query", query);
        jsonObject.addProperty("offset", "0");
        jsonObject.addProperty("limit", "1000");
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        FamilheeyApplication application = FamilheeyApplication.get(this);
        ApiServices apiServices = RetrofitBase.createRxResource(this, ApiServices.class);
        if (groupId != null && groupId.length() > 0) {
            subscriptions.add(apiServices.getPost(requestBody)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(application.defaultSubscribeScheduler())
                    .subscribe(response -> {
                        shimmerFrameLayout.setVisibility(View.GONE);
                        if (response.body() == null || response.body().getData().size() == 0) {
                            linearLayout3.setVisibility(View.GONE);
                            layoutEmpty.setVisibility(View.VISIBLE);
                        } else {
                            linearLayout3.setVisibility(View.VISIBLE);
                            layoutEmpty.setVisibility(View.GONE);
                            data.addAll(response.body().getData());
                            announcementAdapter.notifyDataSetChanged();
                        }


                    }, throwable -> {
                        shimmerFrameLayout.setVisibility(View.GONE);
                        if (throwable instanceof IOException) {
                            networkError();
                        }
                    }));
        } else {
            subscriptions.add(apiServices.getAnnouncement(requestBody)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(application.defaultSubscribeScheduler())
                    .subscribe(response -> {
                        shimmerFrameLayout.setVisibility(View.GONE);
                        assert response.body() != null;
                        if (response.body().getRead_announcement().size() == 0 && response.body().getUnread_announcement().size() == 0) {
                            linearLayout3.setVisibility(View.GONE);
                            layoutEmpty.setVisibility(View.VISIBLE);
                        } else {
                            linearLayout3.setVisibility(View.VISIBLE);
                            layoutEmpty.setVisibility(View.GONE);
                            announcementAdapter.unreadCount = response.body().getUnread_announcement().size();
                            announcementAdapter.readCount = response.body().getRead_announcement().size();
                            if (response.body().getUnread_announcement().size() > 0) {

                                PostData d = new PostData();
                                d.setPost_ref_id("UNREAD");
                                data.add(d);
                                data.addAll(response.body().getUnread_announcement());
                            }

                            if (response.body().getRead_announcement().size() > 0) {

                                PostData d = new PostData();
                                d.setPost_ref_id("READ");
                                data.add(d);
                                data.addAll(response.body().getRead_announcement());
                            }

                            announcementAdapter.notifyDataSetChanged();
                        }
                    }, throwable -> {

                        shimmerFrameLayout.setVisibility(View.GONE);
                        // progressListMember.setVisibility(View.GONE);
                        if (throwable instanceof IOException) {
                            networkError();
                        }
                    }));
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK) {
            assert data != null;
            int pos = Objects.requireNonNull(data.getExtras()).getInt("pos");
            PostData postData = new Gson().fromJson(data.getExtras().getString("data"), PostData.class);
            this.data.get(pos).setPost_attachment(postData.getPost_attachment());
            this.data.get(pos).setIs_shareable(postData.getIs_shareable());
            this.data.get(pos).setSnap_description(postData.getSnap_description());
            this.data.get(pos).setConversation_enabled(postData.getConversation_enabled());
            announcementAdapter.notifyDataSetChanged();
        } else if (requestCode == 1003 && resultCode == RESULT_OK) {
            assert data != null;
            if (Objects.requireNonNull(data.getExtras()).getBoolean("isChat")) {
                this.data.clear();
                getAnnouncement();
            } else {
                this.data.get(data.getExtras().getInt(POSITION)).setConversation_count_new("0");
                announcementAdapter.notifyDataSetChanged();
            }
        } else {
            this.data.clear();
            announcementAdapter.notifyDataSetChanged();
            getAnnouncement();
        }
    }


    private void addUpdate_Announcement_Seen() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        FamilheeyApplication application = FamilheeyApplication.get(this);
        ApiServices apiServices = RetrofitBase.createRxResource(this, ApiServices.class);
        subscriptions.add(apiServices.addUpdate_Announcement_Seen(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(response -> {
                }, throwable -> {
                }));
    }


    private void networkError() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Uh oh! Check your internet connection and retry.")
                .setCancelable(false)
                .setPositiveButton("Retry", (dialog, which) -> getAnnouncement()).setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
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

    private void initListener() {
        imgSearch.setOnClickListener(view -> {
            showCircularReveal(constraintSearch);
            showKeyboard();

        });

        imageBack.setOnClickListener(view -> {
            hideCircularReveal(constraintSearch);
            searchQuery.setText("");
            onSearchQueryListener(EditorInfo.IME_ACTION_SEARCH);


        });
    }

    private void showKeyboard() {
        if (searchQuery.requestFocus()) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(searchQuery, InputMethodManager.SHOW_IMPLICIT);
            }
        }
    }

    @OnEditorAction(R.id.searchQuery)
    protected boolean onSearchQueryListener(int actionId) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            data.clear();
            query = searchQuery.getText().toString();
            getAnnouncement();
            try {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                assert imm != null;
                imm.hideSoftInputFromWindow(searchQuery.getWindowToken(), 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }


}


