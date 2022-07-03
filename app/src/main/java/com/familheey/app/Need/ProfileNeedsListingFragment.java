package com.familheey.app.Need;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
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
import com.google.android.material.button.MaterialButton;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnEditorAction;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import static com.familheey.app.Utilities.Constants.Bundle.DATA;

public class ProfileNeedsListingFragment extends Fragment implements PaginationAdapterCallback {
    public static final int REQUEST_CREATE_REQUEST_CODE = 501;

    @BindView(R.id.layoutEmpty)
    ConstraintLayout layoutEmpty;
    @BindView(R.id.needRequestList)
    RecyclerView needRequestList;
    @BindView(R.id.shimmerLoader)
    ShimmerFrameLayout shimmerLoader;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.emptyResultText)
    SemiBoldTextView emptyResultText;
    @BindView(R.id.createRequest)
    MaterialButton createRequest;
    @BindView(R.id.viewMyContributions)
    TextView viewMyContributions;

    @BindView(R.id.clearSearch)
    ImageView clearSearch;
    @BindView(R.id.search)
    EditText search;

    private CompositeDisposable subscriptions;
    private NeedsRequestAdapter needsRequestAdapter;
    private final List<Need> needRequests = new ArrayList<>();
    private String profileId = "";

    public ProfileNeedsListingFragment() {
        // Required empty public constructor
    }

    public static ProfileNeedsListingFragment newInstance(String profileId) {
        ProfileNeedsListingFragment fragment = new ProfileNeedsListingFragment();
        Bundle args = new Bundle();
        args.putString(DATA, profileId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            profileId = getArguments().getString(DATA, "");
            getArguments().clear();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_needs_listing, container, false);
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
        swipeRefreshLayout.setEnabled(false);
        subscriptions = new CompositeDisposable();
        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(false);
            needRequests.clear();
            needsRequestAdapter.notifyDataSetChanged();
            getNeedList();
        });
        clearSearch.setOnClickListener(v -> {
            search.setText("");
            onSearchQueryListener(EditorInfo.IME_ACTION_SEARCH);
        });
        createRequest.setOnClickListener(v -> createNewNeed());
        viewMyContributions.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ProfileMyContributionsListingActivity.class);
            intent.putExtra(DATA, profileId);
            startActivity(intent);
        });
    }

    private void initializeAdapter() {
        needsRequestAdapter = new NeedsRequestAdapter(this, needRequests, this);
        needRequestList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        needRequestList.setAdapter(needsRequestAdapter);
        needRequestList.addItemDecoration(new BottomAdditionalMarginDecorator());
    }

    private void getNeedList() {
        showShimmer();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", profileId);
        jsonObject.addProperty("txt", search.getText().toString().trim());
        jsonObject.addProperty("by_user", 1);
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        final ApiServices authService = RetrofitBase.createRxResource(getContext(), ApiServices.class);
        subscriptions.add(authService.getNeedsRequestList(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(response -> {
                    needRequests.clear();
                    assert response.body() != null;
                    needRequests.addAll(response.body().getNeedRequests());
                    if (needRequests.size() > 0) {
                        layoutEmpty.setVisibility(View.INVISIBLE);
                        emptyResultText.setVisibility(View.INVISIBLE);
                        search.setVisibility(View.VISIBLE);
                        if (search.getText().toString().trim().length() > 0)
                            clearSearch.setVisibility(View.VISIBLE);
                        else clearSearch.setVisibility(View.INVISIBLE);
                    } else {
                        if (search.getText().toString().trim().length() > 0) {
                            emptyResultText.setVisibility(View.VISIBLE);
                            clearSearch.setVisibility(View.VISIBLE);
                            layoutEmpty.setVisibility(View.INVISIBLE);
                        } else {
                            search.setVisibility(View.INVISIBLE);
                            clearSearch.setVisibility(View.INVISIBLE);
                            emptyResultText.setVisibility(View.INVISIBLE);
                            if(SharedPref.getUserRegistration().getId().equalsIgnoreCase(profileId))
                            layoutEmpty.setVisibility(View.VISIBLE);
                            else emptyResultText.setVisibility(View.VISIBLE);
                        }
                    }
                    needsRequestAdapter.notifyDataSetChanged();
                    hideShimmer();
                    if (SharedPref.getUserRegistration().getId().equalsIgnoreCase(profileId))
                        viewMyContributions.setVisibility(View.VISIBLE);
                    else viewMyContributions.setVisibility(View.GONE);
                }, throwable -> {
                    hideShimmer();
                    networkError();
                }));
    }

    private void showShimmer() {
        layoutEmpty.setVisibility(View.GONE);
        emptyResultText.setVisibility(View.GONE);
        clearSearch.setVisibility(View.INVISIBLE);
        search.setVisibility(View.INVISIBLE);
        viewMyContributions.setVisibility(View.GONE);
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
                .setPositiveButton("Retry", (dialog, which) -> getNeedList()).setNegativeButton("Cancel", (dialog, which) -> {
            dialog.dismiss();
        });
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
        } else if (requestCode == REQUEST_CREATE_REQUEST_CODE) {//Create
            clearAndSearch();
        }
    }

    private void clearAndSearch() {
        needRequests.clear();
        needsRequestAdapter.notifyDataSetChanged();
        getNeedList();
    }

    @OnEditorAction(R.id.search)
    protected boolean onSearchQueryListener(int actionId) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            try {
                InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                assert imm != null;
                imm.hideSoftInputFromWindow(search.getWindowToken(), 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
            clearAndSearch();
            return true;
        }
        return false;
    }

    private void createNewNeed() {
        startActivityForResult(new Intent(getContext(), CreateRequestActivity.class), REQUEST_CREATE_REQUEST_CODE);
    }

    @Override
    public void retryPageLoad() {

    }
}