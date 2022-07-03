package com.familheey.app.Fragments.FamilyDashboard;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
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
import com.familheey.app.Interfaces.FamilyDashboardInteractor;
import com.familheey.app.Need.CreateRequestActivity;
import com.familheey.app.Need.Need;
import com.familheey.app.Need.NeedsRequestAdapter;
import com.familheey.app.Networking.Retrofit.ApiServices;
import com.familheey.app.Networking.Retrofit.RetrofitBase;
import com.familheey.app.R;
import com.familheey.app.Utilities.SharedPref;
import com.familheey.app.Utilities.Utilities;
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
import static com.familheey.app.Utilities.Constants.Bundle.ID;
import static com.familheey.app.Utilities.Constants.FamilyDashboardIdentifiers.TypeFamilyNeedsListingFragment;

public class FamilyNeedsListingFragment extends Fragment implements PaginationAdapterCallback {
    public static final int CREATE_REQUEST_REQUEST_CODE = 501;
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

    @BindView(R.id.clearSearch)
    ImageView clearSearch;
    @BindView(R.id.search)
    EditText search;
    /**06-09-21**/
    private String query = "";
    private CompositeDisposable subscriptions;
    private NeedsRequestAdapter needsRequestAdapter;
    private FamilyDashboardInteractor familyDashboardInteractor;
    private List<Need> needRequests = new ArrayList<>();
    private String familyId = "";
    private String acc = "";
    private String gname = "";
    private String create_post = "";
    private Boolean isAdmin;
    public FamilyNeedsListingFragment() {
        // Required empty public constructor
    }

    public static FamilyNeedsListingFragment newInstance(String familyId, String create_post, String acc, Boolean isAdmin, String name) {
        FamilyNeedsListingFragment fragment = new FamilyNeedsListingFragment();
        Bundle args = new Bundle();
        args.putString(DATA, create_post);
        args.putString("ACC", acc);
        args.putBoolean("isAdmin", isAdmin);
        args.putString("NAME", name);
        args.putString(ID, familyId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            familyId = getArguments().getString(ID, "");
            create_post = getArguments().getString(DATA, "");
            acc = getArguments().getString("ACC", "");
            gname = getArguments().getString("NAME", "");
            isAdmin = getArguments().getBoolean("isAdmin", false);
            getArguments().clear();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_family_needs_listing, container, false);
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
        /*clearSearch.setOnClickListener(v -> {
            search.setText("");
            onSearchQueryListener(EditorInfo.IME_ACTION_SEARCH);
        });*/
        createRequest.setOnClickListener(v -> createNewNeed());
    }

    private void initializeAdapter() {
        needsRequestAdapter = new NeedsRequestAdapter(this, needRequests, this);
        needRequestList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        needRequestList.setAdapter(needsRequestAdapter);
        needRequestList.addItemDecoration(new BottomAdditionalMarginDecorator());
    }

    private void getNeedList() {
        showShimmer();
        if (familyDashboardInteractor != null)
            familyDashboardInteractor.onFamilyAddComponentHidden(TypeFamilyNeedsListingFragment);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        jsonObject.addProperty("txt", query.trim());
        jsonObject.addProperty("group_id", familyId);
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        final ApiServices authService = RetrofitBase.createRxResource(getContext(), ApiServices.class);
        subscriptions.add(authService.getNeedsRequestList(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(response -> {
                    needRequests.clear();
                    needRequests.addAll(response.body().getNeedRequests());
                    if (needRequests.size() > 0) {
                        layoutEmpty.setVisibility(View.INVISIBLE);
                        emptyResultText.setVisibility(View.INVISIBLE);
                       /* if (query.trim().length() > 0)
                            clearSearch.setVisibility(View.VISIBLE);
                        else clearSearch.setVisibility(View.INVISIBLE);*/
                        if (familyDashboardInteractor != null)
                            familyDashboardInteractor.onFamilyAddComponentVisible(TypeFamilyNeedsListingFragment);
                       // search.setVisibility(View.VISIBLE);
                    } else {
                        if (query.trim().length() > 0) {
                            emptyResultText.setVisibility(View.VISIBLE);
                            //clearSearch.setVisibility(View.VISIBLE);
                            layoutEmpty.setVisibility(View.INVISIBLE);
                          //  search.setVisibility(View.VISIBLE);
                        } else {
                         //   clearSearch.setVisibility(View.INVISIBLE);
                           // search.setVisibility(View.INVISIBLE);
                            emptyResultText.setVisibility(View.INVISIBLE);
                           layoutEmpty.setVisibility(View.VISIBLE);
                        }
                        if (familyDashboardInteractor != null)
                            familyDashboardInteractor.onFamilyAddComponentHidden(TypeFamilyNeedsListingFragment);
                    }
                    needsRequestAdapter.notifyDataSetChanged();
                    hideShimmer();
                }, throwable -> {
                    hideShimmer();
                    networkError();
                }));
    }

    private void showShimmer() {
        layoutEmpty.setVisibility(View.GONE);
        emptyResultText.setVisibility(View.GONE);
        //clearSearch.setVisibility(View.INVISIBLE);
        //search.setVisibility(View.INVISIBLE);
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
        } else if (requestCode == CREATE_REQUEST_REQUEST_CODE) {//Create
            clearAndSearch();
        }
    }

    private void clearAndSearch() {
        needRequests.clear();
        needsRequestAdapter.notifyDataSetChanged();
        getNeedList();
    }

   /* @OnEditorAction(R.id.search)
    protected boolean onSearchQueryListener(int actionId) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            try {
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(search.getWindowToken(), 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
            clearAndSearch();
            return true;
        }
        return false;
    }*/

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        familyDashboardInteractor = Utilities.getListener(this, FamilyDashboardInteractor.class);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        familyDashboardInteractor = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (familyDashboardInteractor != null) {
            if (needRequests.size() == 0)
                familyDashboardInteractor.onFamilyAddComponentHidden(TypeFamilyNeedsListingFragment);
            else
                familyDashboardInteractor.onFamilyAddComponentVisible(TypeFamilyNeedsListingFragment);
        }
    }

    public void createNewNeed() {
        startActivityForResult(new Intent(getContext(), CreateRequestActivity.class).putExtra(ID, familyId).putExtra("ACC", acc).putExtra(create_post, DATA).putExtra("isAdmin", isAdmin).putExtra("NAME", gname), CREATE_REQUEST_REQUEST_CODE);
    }

    @Override
    public void retryPageLoad() {

    }

    public void startSearch(String query) {
        this.query=query;
        clearAndSearch();
    }
}