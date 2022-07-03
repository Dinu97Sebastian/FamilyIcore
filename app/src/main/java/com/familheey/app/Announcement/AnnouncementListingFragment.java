package com.familheey.app.Announcement;

import android.app.Activity;
import android.content.Context;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.familheey.app.Activities.FamilyDashboardActivity;
import com.familheey.app.Decorators.BottomAdditionalMarginDecorator;
import com.familheey.app.FamilheeyApplication;
import com.familheey.app.Fragments.FamilyDashboard.BackableFragment;
import com.familheey.app.Fragments.Posts.PostData;
import com.familheey.app.Interfaces.FamilyDashboardInteractor;
import com.familheey.app.Models.Response.Family;
import com.familheey.app.Networking.Retrofit.ApiServices;
import com.familheey.app.Networking.Retrofit.RetrofitBase;
import com.familheey.app.R;
import com.familheey.app.Utilities.SharedPref;
import com.familheey.app.Utilities.Utilities;
import com.google.android.material.button.MaterialButton;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import static com.familheey.app.Utilities.Constants.Bundle.CAN_CREATE;
import static com.familheey.app.Utilities.Constants.Bundle.DATA;
import static com.familheey.app.Utilities.Constants.Bundle.FAMILY;
import static com.familheey.app.Utilities.Constants.FamilyDashboardIdentifiers.TypeAnnouncementListingFragment;
import static com.familheey.app.Utilities.Utilities.hideCircularReveal;
import static com.familheey.app.Utilities.Utilities.showCircularReveal;

public class AnnouncementListingFragment extends BackableFragment {
    public static final int REQUEST_CODE = 1005;

    @BindView(R.id.shimmer_view_container)
    ShimmerFrameLayout shimmerFrameLayout;
    @BindView(R.id.refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.list_recyclerView)
    RecyclerView listRecyclerView;
    @BindView(R.id.progressListMember)
    ProgressBar progressListMember;
    @BindView(R.id.layoutEmpty)
    LinearLayout layoutEmpty;
    @BindView(R.id.linearLayout3)
    RelativeLayout linearLayout3;
    @BindView(R.id.clearSearch)
    ImageView clearSearch;
    /** variables added for searchview replace on search icon click*/
    @BindView(R.id.searchMyFamilies)
    EditText search;
    @BindView(R.id.toolbar_gallery)
    Toolbar toolbar;
    @BindView(R.id.constraintSearch)
    ConstraintLayout constraintSearch;
    @BindView(R.id.imgSearch)
    ImageView imgSearch;
    @BindView(R.id.imageBack)
    ImageView imageBack;
    @BindView(R.id.familyToolBarTitle)
    TextView toolbarTitle;
    public CompositeDisposable subscriptions;
    String query = "";
    List<PostData> data = new ArrayList<>();
    String groupId = "";
    AnnouncementAdapterFamily announcementAdapter;
    @BindView(R.id.create_announcement)
    MaterialButton createAnnouncement;
    private Family family;
    private boolean canCreate;
    private FamilyDashboardInteractor familyDashboardInteractor;
//search
   String value = "";
    public static AnnouncementListingFragment newInstance(String groupId) {
        AnnouncementListingFragment fragment = new AnnouncementListingFragment();
        Bundle args = new Bundle();
        args.putString(DATA, groupId);
        fragment.setArguments(args);
        return fragment;
    }
/** 06/09/2021 **/
public static AnnouncementListingFragment newInstance(Family family,Boolean canCreate,String groupId){
    AnnouncementListingFragment fragment = new AnnouncementListingFragment();
    Bundle args = new Bundle();
    args.putBoolean(CAN_CREATE, canCreate);
    args.putParcelable(FAMILY,family);
    args.putString(DATA,groupId);
    fragment.setArguments(args);
    return fragment;
}
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        subscriptions = new CompositeDisposable();
        if (getArguments() != null) {
            groupId = getArguments().getString(DATA);
            family = getArguments().getParcelable(FAMILY);
            canCreate = getArguments().getBoolean(CAN_CREATE);
            getArguments().clear();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_announcement_listing, container, false);
        ButterKnife.bind(this, view);
        initializeToolbar();
        //initListenerForSearch();
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        announcementAdapter = new AnnouncementAdapterFamily(getActivity(), data);
        listRecyclerView.setAdapter(announcementAdapter);

        initializeSearchClearCallback();
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            data.clear();
            announcementAdapter.notifyDataSetChanged();
            mSwipeRefreshLayout.setRefreshing(false);
            getNewAnnouncements();
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        getAnnouncement();
    }
/** Defined by Devika on 24/08/2021 **/
    @Override
    public void onBackButtonPressed() {
        ((FamilyDashboardActivity)requireActivity()).backAction();
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
        jsonObject.addProperty("query", this.value);
        jsonObject.addProperty("offset", "0");
        jsonObject.addProperty("limit", "1000");
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        FamilheeyApplication application = FamilheeyApplication.get(getActivity());
        ApiServices apiServices = RetrofitBase.createRxResource(getActivity(), ApiServices.class);

        subscriptions.add(apiServices.getPost(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(response -> {
                    shimmerFrameLayout.setVisibility(View.GONE);
                    shimmerFrameLayout.stopShimmer();
                    if (response.body().getData().size() == 0) {
                        mSwipeRefreshLayout.setVisibility(View.GONE);
                        layoutEmpty.setVisibility(View.VISIBLE);
                        createAnnouncement.setVisibility(View.VISIBLE);

                        if (familyDashboardInteractor != null)
                           // familyDashboardInteractor.onFamilyAddComponentHidden(TypeAnnouncementListingFragment);
                        /** 05/09/2021 **/
                        requireActivity().findViewById(R.id.addComponent).setVisibility(View.GONE);
                    } else {
                        mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                        linearLayout3.setVisibility(View.VISIBLE);
                        layoutEmpty.setVisibility(View.GONE);
                        if (familyDashboardInteractor != null)
                           // familyDashboardInteractor.onFamilyAddComponentVisible(TypeAnnouncementListingFragment);
                        /** 05/09/2021 **/
                        requireActivity().findViewById(R.id.addComponent).setVisibility(View.VISIBLE);
                        data.addAll(response.body().getData());
                        announcementAdapter.notifyDataSetChanged();
                    }


                    if (listRecyclerView.getItemDecorationCount() > 0) {
                        listRecyclerView.removeItemDecorationAt(0);
                    }
                    listRecyclerView.addItemDecoration(new BottomAdditionalMarginDecorator());
                }, throwable -> {
                    shimmerFrameLayout.setVisibility(View.GONE);
                    shimmerFrameLayout.stopShimmer();
                }));


    }

    private void getNewAnnouncements() {
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        shimmerFrameLayout.startShimmer();
        //  progressListMember.setVisibility(View.VISIBLE);
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
        FamilheeyApplication application = FamilheeyApplication.get(getActivity());
        ApiServices apiServices = RetrofitBase.createRxResource(getActivity(), ApiServices.class);

        subscriptions.add(apiServices.getPost(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(response -> {
                    if (data != null) {
                        data.clear();
                    }
                    shimmerFrameLayout.setVisibility(View.GONE);
                    if (response.body().getData().size() == 0) {
                        linearLayout3.setVisibility(View.GONE);
                        layoutEmpty.setVisibility(View.VISIBLE);
                        createAnnouncement.setVisibility(View.VISIBLE);
                        if (familyDashboardInteractor != null)
                            familyDashboardInteractor.onFamilyAddComponentHidden(TypeAnnouncementListingFragment);
                    } else {
                        linearLayout3.setVisibility(View.VISIBLE);
                        layoutEmpty.setVisibility(View.GONE);
                        if (familyDashboardInteractor != null)
                            familyDashboardInteractor.onFamilyAddComponentVisible(TypeAnnouncementListingFragment);
                        data.addAll(response.body().getData());
                        announcementAdapter.notifyDataSetChanged();
                    }


                }, throwable -> {
                    shimmerFrameLayout.setVisibility(View.GONE);
                }));


    }

    @OnClick({R.id.create_announcement, R.id.create_new_post})
    public void onViewClicked() {
        createNewAnnouncement();
    }

    public void createNewAnnouncement() {
        if (family != null && family.canCreateAnnouncement()) {
            Intent intent = new Intent(getContext(), CreateAnnouncementActivity.class);
            intent.putExtra(DATA, family.getId() + "");
            startActivityForResult(intent, REQUEST_CODE);
        } else
            Toast.makeText(getContext(), "You don't have sufficient privileges to create announcement", Toast.LENGTH_SHORT).show();
    }

    public void updateFamily(Family family) {
        this.family = family;
    }

    @Override
    public void onResume() {
        super.onResume();
//        if (data != null && data.size() > 0) {
//            if (familyDashboardInteractor != null)
//                familyDashboardInteractor.onFamilyAddComponentVisible(TypeFamilyPostFeedFragment);
//            else createPostFab.setVisibility(View.VISIBLE);
//        } else {
//            if (familyDashboardInteractor != null)
//                familyDashboardInteractor.onFamilyAddComponentHidden(TypeFamilyPostFeedFragment);
//            else createPostFab.setVisibility(View.INVISIBLE);
//        }

        if (data != null)
            data.clear();
        if (announcementAdapter != null)
            announcementAdapter.notifyDataSetChanged();
        getAnnouncement();
        if (familyDashboardInteractor != null)
            familyDashboardInteractor.onFamilyAddComponentHidden(TypeAnnouncementListingFragment);

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        familyDashboardInteractor = Utilities.getListener(this, FamilyDashboardInteractor.class);
        if (familyDashboardInteractor == null)
            throw new RuntimeException("The parent fragment must implement FamilyDashboardInteractor");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        familyDashboardInteractor = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (this.data != null)
            this.data.clear();
        if (announcementAdapter != null)
            announcementAdapter.notifyDataSetChanged();
//        getAnnouncement();
    }


    private void initializeSearchClearCallback() {
        search.addTextChangedListener(new TextWatcher() {
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
            search.setText("");
            onSearchQueryListener(EditorInfo.IME_ACTION_SEARCH);
        });
    }

    @OnEditorAction(R.id.searchMyFamilies)
    protected boolean onSearchQueryListener(int actionId) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            searchPost(search.getText().toString());
            try {
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(search.getWindowToken(), 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }

    public void searchPost(String query) {
        this.query = query;
        data.clear();
        announcementAdapter.notifyDataSetChanged();
        getAnnouncement();
    }
    /**created by Devika on 02/08/2021
     * method for inflating the searchview on click of search icon in toolbar*/
    private void initListenerForSearch() {
        imgSearch.setOnClickListener(view -> {
            showCircularReveal(constraintSearch);
            //profileImage.setEnabled(false);
            showKeyboard();

        });

        imageBack.setOnClickListener(view -> {
            hideCircularReveal(constraintSearch);
            search.setText("");
            //profileImage.setEnabled(true);
            onSearchQueryListener(EditorInfo.IME_ACTION_SEARCH);


        });
    }
    private void showKeyboard() {
        if (search.requestFocus()) {
            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(search, InputMethodManager.SHOW_IMPLICIT);
            }
        }
    }
    /**setting toolbar title*/
    private void initializeToolbar(){
        toolbarTitle.setText(getResources().getString(R.string.fragment_dashboard_menu_announcements));
    }
    /** This method is invokes from FamilyDashboardActivity for searching folders based on the input
     * entered by the user in the searchview
     * @param value : user input passed from method call
     */
    public void startSearch(String value) {
        this.value = value;
        searchPost(this.value);
        //getAnnouncement();
    }
}
