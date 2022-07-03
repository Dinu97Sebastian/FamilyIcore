package com.familheey.app.Fragments.Posts;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.familheey.app.Activities.FamilyDashboardActivity;
import com.familheey.app.Activities.ProfileActivity;
import com.familheey.app.Adapters.EventTabAdapter;
import com.familheey.app.Announcement.AnnouncementDetailActivity;
import com.familheey.app.FamilheeyApplication;
import com.familheey.app.Interfaces.HomeInteractor;
import com.familheey.app.Models.Response.Banner;
import com.familheey.app.Models.Response.FamilyMember;
import com.familheey.app.Need.NeedsRequestListingFragment;
import com.familheey.app.Networking.Retrofit.ApiServices;
import com.familheey.app.Networking.Retrofit.RetrofitBase;
import com.familheey.app.R;
import com.familheey.app.Utilities.Constants;
import com.familheey.app.Utilities.SharedPref;
import com.familheey.app.Utilities.Utilities;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.JsonObject;
import com.nex3z.notificationbadge.NotificationBadge;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import static com.familheey.app.Utilities.Constants.Bundle.DATA;
import static com.familheey.app.Utilities.Constants.Bundle.DETAIL;
import static com.familheey.app.Utilities.Constants.Bundle.POSITION;
import static com.familheey.app.Utilities.Constants.Bundle.TYPE;
import static com.familheey.app.Utilities.Utilities.hideCircularReveal;
import static com.familheey.app.Utilities.Utilities.showCircularReveal;

public class PostFragment extends Fragment implements TabLayout.OnTabSelectedListener, HashTagSearch {

    public static final int ANNOUNCEMENT_DETAILS_REQUEST_CODE = 201;

    public CompositeDisposable subscriptions;
    @BindView(R.id.toolBarTitle)
    TextView title;
    @BindView(R.id.announcement_notification)
    LinearLayout announcement_notification;
    @BindView(R.id.need_notification)
    LinearLayout need_notification;
    @BindView(R.id.notification_banner)
    ImageView notification_banner;
    @BindView(R.id.bellNotificationCount)
    NotificationBadge bellNotificationCount;
    @BindView(R.id.bellIcon)
    ImageView bellIcon;
    @BindView(R.id.search_post)
    EditText search_post;
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
    @BindView(R.id.profileImage)
    ImageView profileImage;
    @BindView(R.id.appBar)
    AppBarLayout appBar;
    @BindView(R.id.notification_container)
    LinearLayout notification_container;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private EventTabAdapter eventTabAdapter;
    private HomeInteractor mListener;
    private Banner banner;
//    boolean isVisible = false;

    public PostFragment() {
        // Required empty public constructor
    }

    public static PostFragment newInstance() {
        PostFragment fragment = new PostFragment();
        return fragment;
    }

    public static PostFragment newInstance(int pos) {
        PostFragment fragment = new PostFragment();
        Bundle args = new Bundle();
        args.putInt(POSITION, pos);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        subscriptions = new CompositeDisposable();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_post, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        tabLayout = getActivity().findViewById(R.id.tabLayout);
        viewPager = getActivity().findViewById(R.id.eventPager);
        viewPager.setOffscreenPageLimit(3);
        initializeToolbar();
        eventTabAdapter = new EventTabAdapter(requireActivity().getSupportFragmentManager());
        //search_post, constraintSearch
        MyFamilyPostFragment myFamilyPostFragment = new MyFamilyPostFragment();
        eventTabAdapter.addFragment(myFamilyPostFragment, "MY FAMILY FEED");
//        PublicFeedPostFragment publicFeedPostFragment = new PublicFeedPostFragment();
//        eventTabAdapter.addFragment(publicFeedPostFragment, "PUBLIC FEED");
//        NeedsRequestListingFragment needsRequestListingFragment = NeedsRequestListingFragment.newInstance();
//        eventTabAdapter.addFragment(needsRequestListingFragment, "REQUESTS");
        viewPager.setAdapter(eventTabAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(this);

        if (getArguments() != null) {
            viewPager.setCurrentItem(getArguments().getInt(POSITION));
        }
        myFamilyPostFragment.setmListener(this);
       // publicFeedPostFragment.setmListener(this);
        initializeSearchClearCallback();
        initListener();
        GetBanners();
    }


    private void initListener() {
        imgSearch.setOnClickListener(view -> {
            showCircularReveal(constraintSearch);
            profileImage.setEnabled(false);
            showKeyboard();

        });

        imageBack.setOnClickListener(view -> {
            hideCircularReveal(constraintSearch);
            search_post.setText("");
            profileImage.setEnabled(true);
            onSearchQueryListener(EditorInfo.IME_ACTION_SEARCH);

        });
    }

    private void showKeyboard() {
        if (search_post.requestFocus()) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(search_post, InputMethodManager.SHOW_IMPLICIT);
            }
        }
    }


    private void initializeSearchClearCallback() {
        search_post.addTextChangedListener(new TextWatcher() {

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
            search_post.setText("");
            onSearchQueryListener(EditorInfo.IME_ACTION_SEARCH);
        });
    }

    private void initializeToolbar() {
        title.setText("Home");
        profileImage.setOnClickListener(v -> {
            FamilyMember familyMember = new FamilyMember();
            familyMember.setId(Integer.parseInt(SharedPref.getUserRegistration().getId()));
            familyMember.setUserId(familyMember.getId());
            familyMember.setProPic(SharedPref.getUserRegistration().getPropic());
            Intent intent = new Intent(getActivity(), ProfileActivity.class);
// Pass data object in the bundle and populate details activity.
            //intent.putExtra(ProfileActivity.EXTRA_CONTACT, contact);
            intent.putExtra(Constants.Bundle.DATA, familyMember);
            intent.putExtra(Constants.Bundle.FOR_EDITING, true);
            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation(getActivity(), profileImage, "profile");
            startActivity(intent, options.toBundle());
        });
        setNotificationCount(mListener.getNotificationsCount());
        Utilities.loadProfilePicture(profileImage);
    }

    @OnEditorAction(R.id.search_post)
    protected boolean onSearchQueryListener(int actionId) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {

            MyFamilyPostFragment fragment1 = (MyFamilyPostFragment) eventTabAdapter.getRegisteredFragment(0);
            if (fragment1 != null) {
                fragment1.searchPost(search_post.getText().toString());
            }
            PublicFeedPostFragment fragment2 = (PublicFeedPostFragment) eventTabAdapter.getRegisteredFragment(1);
            if (fragment2 != null) {
                fragment2.searchPost(search_post.getText().toString());
            }
            NeedsRequestListingFragment fragment3 = (NeedsRequestListingFragment) eventTabAdapter.getRegisteredFragment(2);
            if (fragment3 != null) {
                fragment3.searchNeeds(search_post.getText().toString());
            }
            try {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(search_post.getWindowToken(), 0);
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
        }
        else
            bellNotificationCount.clear(true);
    }

    @OnClick({R.id.imgMessage, R.id.bellIcon, R.id.bellNotificationCount, R.id.feedback, R.id.btn_close, R.id.announcement_notification, R.id.need_notification})
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
            case R.id.btn_close:
                announcement_notification.setVisibility(View.GONE);
                toggleNotificationContainerVisibility();
                addUpdate_Announcement_Seen();
                break;
            case R.id.announcement_notification:
                announcement_notification.setVisibility(View.GONE);
                toggleNotificationContainerVisibility();
                startActivityForResult(new Intent(getActivity(), AnnouncementDetailActivity.class).putExtra(TYPE, "UNREAD"), ANNOUNCEMENT_DETAILS_REQUEST_CODE);
                break;
            case R.id.need_notification:
                if (banner != null) {
                    if (banner.getLink_type().equals("family")) {

                        startActivity(new Intent(getActivity(), FamilyDashboardActivity.class)
                                .putExtra(TYPE, DETAIL)
                                .putExtra(DATA, banner.getLink_id()));
                    } else if (banner.getLink_type().equals("home")) {
                        if (banner.getLink_subtype().equals("request")) {
                            new Handler().postDelayed(() -> viewPager.setCurrentItem(2), 300);
                            need_notification.setVisibility(View.GONE);
                            toggleNotificationContainerVisibility();
                        }
                    }
                }

                break;
        }
    }

    @Override
    public void onAttach(Context context) {
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

    @Override
    public void onResume() {
        super.onResume();
        Utilities.loadProfilePicture(profileImage);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ANNOUNCEMENT_DETAILS_REQUEST_CODE) {
            getNew_announcement_count();
        }
    }


    private void GetBanners() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        FamilheeyApplication application = FamilheeyApplication.get(getActivity());
        ApiServices apiServices = RetrofitBase.createRxResource(getActivity(), ApiServices.class);
        subscriptions.add(apiServices.GetBanners(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(response -> {
                    if (response.body() != null && response.body().size() > 0) {
                        banner = response.body().get(0);
                        if (banner.getVisibility()) {
                            need_notification.setVisibility(View.VISIBLE);
                            toggleNotificationContainerVisibility();
                            Glide.with(getActivity()).load(banner.getBanner_url().get(0)).into(notification_banner);
                        }
                    }
                }, throwable -> {
                }));
    }


    private void getNew_announcement_count() {

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        FamilheeyApplication application = FamilheeyApplication.get(getActivity());
        ApiServices apiServices = RetrofitBase.createRxResource(getActivity(), ApiServices.class);
        subscriptions.add(apiServices.get_announcement_banner_count(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(response -> {
                    if (response.body() != null) {
                        if (response.body().get(0).getNew_announcement_count().equals("0")) {
                            announcement_notification.postDelayed(() -> announcement_notification.setVisibility(View.GONE), 1000);

                        } else {
                            announcement_notification.postDelayed(() -> announcement_notification.setVisibility(View.VISIBLE), 1000);
                        }
                        toggleNotificationContainerVisibility();
                    }

                }, throwable -> {
                }));
    }

    private void addUpdate_Announcement_Seen() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        FamilheeyApplication application = FamilheeyApplication.get(getActivity());
        ApiServices apiServices = RetrofitBase.createRxResource(getActivity(), ApiServices.class);
        subscriptions.add(apiServices.addUpdate_Announcement_Seen(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(response -> {

                }, throwable -> {
                }));
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        appBar.setExpanded(true);
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        switch (tab.getPosition()) {
            case 0:
                Fragment fragment = eventTabAdapter.getItem(0);
                ((MyFamilyPostFragment) fragment).pauseVideo();
                break;
            case 1:
                Fragment fragment1 = eventTabAdapter.getItem(1);
                ((PublicFeedPostFragment) fragment1).pauseVideo();
                break;
        }
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    public void onSearchTag(String hashtag) {
        search_post.setText(hashtag);
        Utilities.showCircularReveal(constraintSearch);
    }

    @Override
    public void newAnnouncement(Boolean isShow) {
        getNew_announcement_count();
    }

    private void toggleNotificationContainerVisibility() {
        if (announcement_notification.getVisibility() == View.VISIBLE || need_notification.getVisibility() == View.VISIBLE)
            notification_container.setVisibility(View.VISIBLE);
        else notification_container.setVisibility(View.GONE);
    }

    public void initializeSpotLight() {
        TapTargetSequence sequence = new TapTargetSequence(getActivity())
                .targets(
                        TapTarget.forView(bellIcon, "Notification and alert")
                                .cancelable(false)
                                .id(3),
                        TapTarget.forView(((ViewGroup) tabLayout.getChildAt(0)).getChildAt(0), "Explore post on family feed")
                                .cancelable(false)
                                .id(4),
                        TapTarget.forView(((ViewGroup) tabLayout.getChildAt(0)).getChildAt(1), "Explore post on public feed")
                                .cancelable(false)
                                .id(5),
                        TapTarget.forView(((ViewGroup) tabLayout.getChildAt(0)).getChildAt(2), "Ask and provide help to your families")
                                .cancelable(false)
                                .id(6))
                .listener(new TapTargetSequence.Listener() {
                    @Override
                    public void onSequenceFinish() {
                        try {
                            if (!SharedPref.userHasFamily())
                                mListener.loadNewUserHelper();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {

                    }

                    @Override
                    public void onSequenceCanceled(TapTarget lastTarget) {
                    }
                });
        sequence.start();
    }
}
