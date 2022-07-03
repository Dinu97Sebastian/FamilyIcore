package com.familheey.app.Fragments;

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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.familheey.app.Activities.ProfileActivity;
import com.familheey.app.Discover.DiscoverActivity;
import com.familheey.app.Discover.model.ElasticSearch;
import com.familheey.app.Discover.ui.main.ItemElasticSearchPost;
import com.familheey.app.Interfaces.HomeInteractor;
import com.familheey.app.Models.Response.FamilyMember;
import com.familheey.app.Networking.Retrofit.ApiServices;
import com.familheey.app.Networking.Retrofit.RetrofitBase;
import com.familheey.app.R;
import com.familheey.app.Utilities.BackGroundDataFetching;
import com.familheey.app.Utilities.Constants;
import com.familheey.app.Utilities.SharedPref;
import com.familheey.app.Utilities.Utilities;
import com.google.android.material.appbar.AppBarLayout;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.nex3z.notificationbadge.NotificationBadge;

import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import static com.familheey.app.Utilities.Utilities.hideCircularReveal;

public class SearchFragment extends Fragment {

    public static final int FAMILY = 1;
    @BindView(R.id.data_list)
    RecyclerView data_list;
    @BindView(R.id.btn_post)
    TextView btn_post;
    @BindView(R.id.btn_family)
    TextView btn_family;
    @BindView(R.id.search_post)
    EditText searchQuery;
    @BindView(R.id.toolBarTitle)
    TextView toolBarTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.bellNotificationCount)
    NotificationBadge bellNotificationCount;
    @BindView(R.id.bellIcon)
    ImageView bellIcon;
    @BindView(R.id.profileImage)
    ImageView profileImage;
    @BindView(R.id.feedback)
    ImageView feedback;
    @BindView(R.id.clearSearch)
    ImageView clearSearch;

    @BindView(R.id.constraintSearch)
    ConstraintLayout constraintSearch;

    @BindView(R.id.imgSearch)
    ImageView imgSearch;

    @BindView(R.id.imageBack)
    ImageView imageBack;
    @BindView(R.id.appBar)
    AppBarLayout appBar;
    @BindView(R.id.btn_event)
    TextView btn_event;
    @BindView(R.id.btn_people)
    TextView btn_people;
    private String data = "";
    private CompositeDisposable subscriptions;

    private HomeInteractor mListener;

    public SearchFragment() {
        // Required empty public constructor
    }

    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();

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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeToolbar();
        initializeSearchClearCallback();
        initListener();
        fetchData();

    }





    private void initListener() {

        btn_post.setOnClickListener(view -> {
            startActivity(new Intent(getContext(), DiscoverActivity.class).putExtra("POS", 0));
            requireActivity().overridePendingTransition(R.anim.enter,
                    R.anim.exit);
        });
        btn_people.setOnClickListener(view -> {
            startActivity(new Intent(getContext(), DiscoverActivity.class).putExtra("POS", 2));

            requireActivity().overridePendingTransition(R.anim.enter,
                    R.anim.exit);
        });
        btn_event.setOnClickListener(view -> {
            startActivity(new Intent(getContext(), DiscoverActivity.class).putExtra("POS", 4));

            requireActivity().overridePendingTransition(R.anim.enter,
                    R.anim.exit);
        });
        btn_family.setOnClickListener(view -> {
            startActivity(new Intent(getContext(), DiscoverActivity.class).putExtra("POS", 1));

            requireActivity().overridePendingTransition(R.anim.enter,
                    R.anim.exit);
        });

        imgSearch.setOnClickListener(view -> {
            startActivity(new Intent(getContext(), DiscoverActivity.class));
            requireActivity().overridePendingTransition(R.anim.enter,
                    R.anim.exit);
        });

        imageBack.setOnClickListener(view -> {
            hideCircularReveal(constraintSearch);
            profileImage.setEnabled(true);
            searchQuery.setText("");
            onSearchQueryListener(EditorInfo.IME_ACTION_SEARCH);

        });
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

    private void initializeToolbar() {

        subscriptions = new CompositeDisposable();
        toolBarTitle.setText("Discover");
        setNotificationCount(mListener.getNotificationsCount());
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
        Utilities.loadProfilePicture(profileImage);
    }

    @OnEditorAction(R.id.search_post)
    protected boolean onSearchQueryListener(int actionId) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {

            try {
                InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                assert imm != null;
                imm.hideSoftInputFromWindow(searchQuery.getWindowToken(), 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
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


    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        if (context instanceof HomeInteractor) {
            mListener = (HomeInteractor) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement HomeInteractor");
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;

    }


    @OnClick({R.id.imgMessage, R.id.bellIcon, R.id.bellNotificationCount, R.id.feedback})
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
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Utilities.loadProfilePicture(profileImage);
    }


    public void fetchData() {

        new BackGroundDataFetching(requireActivity()).loadDataFromApi();
        data = SharedPref.read(SharedPref.EVENT_SUGGESTION, "");
        if (data.isEmpty()) {
            getElsticSearchPost();
        } else {
            ElasticSearch elasticSearch = new Gson().fromJson(data, ElasticSearch.class);

            StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
            staggeredGridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);


            data_list.setItemAnimator(null);
            data_list.setLayoutManager(staggeredGridLayoutManager);
            data_list.setHasFixedSize(true);
            data_list.setItemViewCacheSize(elasticSearch.getHits().size());
            data_list.setDrawingCacheEnabled(true);
            data_list.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
            data_list.setAdapter(new ItemElasticSearchPost(elasticSearch.getHits()));


            //data_list.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
            //data_list.setAdapter(new ItemElasticSearchPost(elasticSearch.getHits()));
        }
    }


    private void getElsticSearchPost() {

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("index", "post");
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        /*jsonObject.addProperty("search_text", query);
        jsonObject.addProperty("offset", currentPage + "");
        jsonObject.addProperty("limit", "10");*/
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        final ApiServices authService = RetrofitBase.createRxResource(getContext(), ApiServices.class);
        subscriptions.add(authService.getRecords(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(response -> {
                    assert response.body() != null;
                    SharedPref.write(SharedPref.EVENT_SUGGESTION, new Gson().toJson(response));
                    //gallery.layoutManager=StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
                    // layoutmanager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
                    data_list.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
                    data_list.setAdapter(new ItemElasticSearchPost(response.body().getHits()));
                }, throwable -> {
                }));
    }

}
