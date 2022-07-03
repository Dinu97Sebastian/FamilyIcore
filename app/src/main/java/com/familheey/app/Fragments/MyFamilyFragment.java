package com.familheey.app.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.familheey.app.Activities.CreateFamilyActivity;
import com.familheey.app.Activities.PendingRequestActivity;
import com.familheey.app.Activities.ProfileActivity;
import com.familheey.app.Adapters.FamilyListingAdapter;
import com.familheey.app.Discover.DiscoverActivity;
import com.familheey.app.Interfaces.HomeInteractor;
import com.familheey.app.Interfaces.RetrofitListener;
import com.familheey.app.Models.ApiCallbackParams;
import com.familheey.app.Models.ErrorData;
import com.familheey.app.Models.Response.Family;
import com.familheey.app.Models.Response.FamilyMember;
import com.familheey.app.Networking.Retrofit.ApiServiceProvider;
import com.familheey.app.PaginationListener;
import com.familheey.app.Parsers.FamilyParser;
import com.familheey.app.R;
import com.familheey.app.Utilities.Constants;
import com.familheey.app.Utilities.SharedPref;
import com.familheey.app.Utilities.Utilities;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.JsonObject;
import com.nex3z.notificationbadge.NotificationBadge;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;

import static com.familheey.app.Utilities.Constants.ApiPaths.FIREBASE_DATABASE_URL;
import static com.familheey.app.Utilities.Utilities.hideCircularReveal;
import static com.familheey.app.Utilities.Utilities.showCircularReveal;

public class MyFamilyFragment extends Fragment implements RetrofitListener {

    @BindView(R.id.familyToolBarTitle)
    TextView toolBarTitle;
    @BindView(R.id.bellNotificationCount)
    NotificationBadge bellNotificationCount;
    @BindView(R.id.bellIcon)
    ImageView bellIcon;
    @BindView(R.id.profileImage)
    ImageView profileImage;
    @BindView(R.id.feedback)
    ImageView feedback;
    @BindView(R.id.createFamily)
    CardView createFamily;
    @BindView(R.id.collapsingToolbar)
    CollapsingToolbarLayout collapsingToolbar;
    @BindView(R.id.searchMyFamilies)
    EditText searchMyFamilies;
    @BindView(R.id.myFamilyList)
    RecyclerView myFamilyList;
    @BindView(R.id.buttonSearchFamily)
    MaterialButton buttonSearchFamily;
    @BindView(R.id.clearSearch)
    ImageView clearSearch;
    @BindView(R.id.familyEmptyContainer)
    View familyEmptyContainer;
    @BindView(R.id.createFamilyNow)
    MaterialButton createFamilyNow;
    @BindView(R.id.txt_pending)
    TextView txt_pending;
    @BindView(R.id.emptyResultText)
    TextView emptyResultText;
    @BindView(R.id.txtSearchResult)
    TextView txtSearchResult;
    @BindView(R.id.constraintSearch)
    ConstraintLayout constraintSearch;

    @BindView(R.id.imgSearch)
    ImageView imgSearch;
    @BindView(R.id.progresbar)
    ProgressBar progresbar;
    @BindView(R.id.imageBack)
    ImageView imageBack;
    @BindView(R.id.shimmer_view_container)
    com.facebook.shimmer.ShimmerFrameLayout shimmer_view_container;
    private String query = "";

    private final int TOTAL_PAGES = 5;
    private final ArrayList<Family> families = new ArrayList<>();
    //private boolean isLoading = false;
    private HomeInteractor mListener;
    private FamilyListingAdapter familyListingAdapter;
   // private boolean isLastPage = false;
    private boolean isAllowReload = false;
    public static final int PAGE_START = 1;

    private int currentPage = PAGE_START;
    private boolean isLastPage = false;
    private int totalPage = 10;
    private boolean isLoading = false;
    int itemCount = 0;
    private DatabaseReference database;
    public MyFamilyFragment() {
        // Required empty public constructor
    }

    public static MyFamilyFragment newInstance() {
        MyFamilyFragment fragment = new MyFamilyFragment();
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
        View view = inflater.inflate(R.layout.fragment_my_family, container, false);
        ButterKnife.bind(this, view);
        initView();
        initializeSearchClearCallback();
        initializeToolbar();
        initListener();
        //loadData();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }
    private void initListener() {
        imgSearch.setOnClickListener(view -> {
            showCircularReveal(constraintSearch);
            profileImage.setEnabled(false);
            showKeyboard();

        });

        imageBack.setOnClickListener(view -> {
            hideCircularReveal(constraintSearch);
            searchMyFamilies.setText("");
            profileImage.setEnabled(true);
            onSearchQueryListener(EditorInfo.IME_ACTION_SEARCH);


        });
    }

    private void showKeyboard() {
        if (searchMyFamilies.requestFocus()) {
            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(searchMyFamilies, InputMethodManager.SHOW_IMPLICIT);
            }
        }
    }

    private void initializeSearchClearCallback() {
        searchMyFamilies.addTextChangedListener(new TextWatcher() {

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
            searchMyFamilies.setText("");
            txtSearchResult.setVisibility(View.GONE);
            onSearchQueryListener(EditorInfo.IME_ACTION_SEARCH);
        });
    }

    private void initializeToolbar() {
        toolBarTitle.setText("My Families");
        profileImage.setOnClickListener(v -> {
            FamilyMember familyMember = new FamilyMember();
            familyMember.setId(Integer.parseInt(SharedPref.getUserRegistration().getId()));
            familyMember.setUserId(familyMember.getId());
            familyMember.setProPic(SharedPref.getUserRegistration().getPropic());
            Intent intent = new Intent(getActivity(), ProfileActivity.class);
            intent.putExtra(Constants.Bundle.DATA, familyMember);
            intent.putExtra(Constants.Bundle.FOR_EDITING, true);
            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation(requireActivity(), profileImage, "profile");
            startActivity(intent, options.toBundle());
        });
        setNotificationCount(mListener.getNotificationsCount());
        Utilities.loadProfilePicture(profileImage);
    }

    private void fetchMyFamilies(boolean isFromSearch) {
        if (families.size() == 0) {
            shimmer_view_container.setVisibility(View.VISIBLE);
            shimmer_view_container.startShimmer();
        }
        if (isFromSearch && !searchMyFamilies.getText().toString().equals("")) {
            txtSearchResult.setVisibility(View.VISIBLE);
        } else {
            txtSearchResult.setVisibility(View.GONE);
        }

        query = searchMyFamilies.getText().toString().trim();
        txtSearchResult.setText("Search results for: \"" + query + "\"");
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        jsonObject.addProperty("query", query);
        jsonObject.addProperty("offset", families.size());
        jsonObject.addProperty("limit", "10");
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getActivity());
        ApiCallbackParams apiCallbackParams = new ApiCallbackParams();
        apiCallbackParams.setDeveloperString(query);
        apiServiceProvider.listAllFamily(jsonObject, apiCallbackParams, this);
    }

    private void initView() {
        toolBarTitle.setText("My Families");
        familyListingAdapter = new FamilyListingAdapter(families, "");
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        myFamilyList.setLayoutManager(layoutManager);
        myFamilyList.setAdapter(familyListingAdapter);
        loadData();
/*        myFamilyList.addOnScrollListener(new PaginationScrollListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
               *//* progresbar.setVisibility(View.VISIBLE);
                isLoading = true;
                loadData();*//*
                progresbar.setVisibility(View.VISIBLE);
                isLoading = true;
                //Increment page index to load the next one
                loadData();
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

            @Override
            public void onScrolled(@NotNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                progresbar.setVisibility(View.VISIBLE);
            }
        });*/
        myFamilyList.addOnScrollListener(new PaginationListener(layoutManager){

            @Override
            protected void loadMoreItems() {
                progresbar.setVisibility(View.VISIBLE);
                myFamilyList.setPadding(0,0,0,150);
                isLoading = true;
                currentPage++;
                loadData();
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
        createFamilyNow.setOnClickListener(v -> createFamily.performClick());
        txt_pending.setOnClickListener(v -> startActivity(new Intent(getActivity(), PendingRequestActivity.class)));
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        if (context instanceof HomeInteractor) {
            mListener = (HomeInteractor) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @OnClick({R.id.imgMessage, R.id.bellIcon, R.id.bellNotificationCount, R.id.feedback, R.id.createFamily})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.imgMessage:
                mListener.navigateMessageScreen();
                break;
            case R.id.bellIcon:
            case R.id.bellNotificationCount:
                mListener.loadNotifications();
                break;
            case R.id.feedback:
                Utilities.showMainMenuPopup(getContext(), feedback);
                break;
            case R.id.createFamily:
                Intent intent = new Intent(getContext(), CreateFamilyActivity.class);
                startActivityForResult(intent, Constants.RequestCode.REQUEST_CODE);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.RequestCode.REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            assert data != null;
            Family family1 = data.getParcelableExtra(Constants.Bundle.DATA);
            Family family = new Family();
            family.setId(family1.getId());
            mListener.loadFamilyGroupDashboard(family);
        }
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {
        progresbar.setVisibility(View.GONE);
        myFamilyList.setPadding(0,0,0,0);
        isLoading=true;
        int familySize=families.size();
        if (shimmer_view_container != null)
            shimmer_view_container.setVisibility(View.GONE);
        if (searchMyFamilies.getText().toString().equals("")) {
            txtSearchResult.setVisibility(View.GONE);
        }

        List<Family> temp = FamilyParser.parseLinkedFamilies(responseBodyString);
        if (temp != null && temp.size() == 10) {
            progresbar.setVisibility(View.GONE);
            isLoading = false;
        } else {
            progresbar.setVisibility(View.GONE);
            isLastPage = true;
        }
        families.addAll(FamilyParser.parseLinkedFamilies(responseBodyString));

        //myFamilyList.scrollToPosition(familySize+2);
        Integer pending_count = FamilyParser.parsePendingCount(responseBodyString);
        familyListingAdapter.notifyDataSetChanged();
        if (families.size() > 0) {
            SharedPref.setUserHasFamily(true);
            showSearchResults();
        } else {
            //imgSearch.setVisibility(View.INVISIBLE);
            showEmptyFamilies();
            if (query.length() == 0)
                SharedPref.setUserHasFamily(false);
        }
        if (pending_count != null && pending_count > 0)
            txt_pending.setVisibility(View.VISIBLE);
        else txt_pending.setVisibility(View.GONE);
    }

    @Override
    public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
        progresbar.setVisibility(View.GONE);

        if (shimmer_view_container != null)
            shimmer_view_container.setVisibility(View.GONE);
        if (getContext() != null)
            Toast.makeText(getContext(), Constants.SOMETHING_WRONG, Toast.LENGTH_SHORT).show();
    }

    @OnEditorAction(R.id.searchMyFamilies)
    protected boolean onSearchQueryListener(int actionId) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            checkNetworkAndLoad(true);
            try {
                InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(searchMyFamilies.getWindowToken(), 0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }

    private void showSearchResults() {
        searchMyFamilies.setVisibility(View.VISIBLE);
        myFamilyList.setVisibility(View.VISIBLE);
        createFamily.setVisibility(View.INVISIBLE);
        familyEmptyContainer.setVisibility(View.INVISIBLE);
        emptyResultText.setVisibility(View.INVISIBLE);
    }

    private void showEmptyFamilies() {
        if (searchMyFamilies.getText().toString().trim().length() == 0)
            searchMyFamilies.setVisibility(View.INVISIBLE);
        else searchMyFamilies.setVisibility(View.VISIBLE);
        myFamilyList.setVisibility(View.INVISIBLE);
        createFamily.setVisibility(View.INVISIBLE);
        if (query != null && query.length() == 0) {
            familyEmptyContainer.setVisibility(View.VISIBLE);
            emptyResultText.setVisibility(View.INVISIBLE);
        } else {
            emptyResultText.setVisibility(View.VISIBLE);
            familyEmptyContainer.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        /*myFamilyList.setAdapter(null);
        myFamilyList.setLayoutManager(null);
        myFamilyList.getRecycledViewPool().clear();
        myFamilyList.swapAdapter(familyListingAdapter, false);
        familyListingAdapter.notifyDataSetChanged();*/
    }
    public void clearData() {
        myFamilyList.getLayoutManager().removeAllViews(); // clear list
        familyListingAdapter.notifyDataSetChanged(); // let your adapter know about the changes and reload view.
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onResume() {
      if (isAllowReload){
            assert getFragmentManager() != null;
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.fragmentsLoader, MyFamilyFragment.newInstance());
            transaction.addToBackStack(null);
            transaction.commit();
        }
        isAllowReload=true;
        super.onResume();
    }

    private void loadData() {
        checkNetworkAndLoad(false);
        Utilities.loadProfilePicture(profileImage);
    }

    private void checkNetworkAndLoad(boolean isFromSearch) {
        if (Utilities.isNetworkAvailable(requireActivity())) {

            if (isFromSearch) {
                families.clear();
                familyListingAdapter.notifyDataSetChanged();
            }
            fetchMyFamilies(isFromSearch);
        } else {
            showSnackBarNetworkError();
        }
    }

    private void showSnackBarNetworkError() {
        Snackbar snackbar = Snackbar
                .make(profileImage, "Network not available", Snackbar.LENGTH_INDEFINITE)
                .setAction("Retry", view -> checkNetworkAndLoad(false));
                snackbar.show();
    }

    @SuppressLint("NonConstantResourceId")
    @OnClick({R.id.createFamilyNow, R.id.buttonSearchFamily})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.createFamilyNow:
                Intent createFamilyIntent = new Intent(getContext(), CreateFamilyActivity.class);
                startActivityForResult(createFamilyIntent, Constants.RequestCode.REQUEST_CODE);
                break;
            case R.id.buttonSearchFamily:
                startActivity(new Intent(getContext(), DiscoverActivity.class).putExtra("POS", 1));
                break;
        }
    }

    public void setNotificationCount(int count) {
        if (count > 99) {
            bellNotificationCount.setText("99+", true);
        }
        else if (count >0){
            bellNotificationCount.setText(String.valueOf(count), true);
        } else
            bellNotificationCount.clear(true);
    }


}
