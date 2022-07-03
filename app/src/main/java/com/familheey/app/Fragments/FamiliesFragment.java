package com.familheey.app.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;

import com.familheey.app.Activities.ProfileActivity;
import com.familheey.app.Adapters.FamilyPagerAdapter;
import com.familheey.app.Interfaces.HomeInteractor;
import com.familheey.app.Models.Response.FamilyMember;
import com.familheey.app.R;
import com.familheey.app.Utilities.Constants;
import com.familheey.app.Utilities.NonSwipeableViewPager;
import com.familheey.app.Utilities.SharedPref;
import com.familheey.app.Utilities.Utilities;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.nex3z.notificationbadge.NotificationBadge;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FamiliesFragment extends Fragment implements TabLayout.OnTabSelectedListener {


    @BindView(R.id.familiesTab)
    TabLayout familiesTab;
    @BindView(R.id.familiyViewPager)
    NonSwipeableViewPager familiyViewPager;
    @BindView(R.id.toolBarTitle)
    TextView toolBarTitle;
    @BindView(R.id.bellNotificationCount)
    NotificationBadge bellNotificationCount;
    @BindView(R.id.bellIcon)
    ImageView bellIcon;
    @BindView(R.id.profileImage)
    ImageView profileImage;
    @BindView(R.id.feedback)
    ImageView feedback;

    FamilyPagerAdapter familyPagerAdapter;
    @BindView(R.id.appBar)
    AppBarLayout appBar;

    private HomeInteractor mListener;

    public FamiliesFragment() {
        // Required empty public constructor
    }

    public static FamiliesFragment newInstance() {
        FamiliesFragment fragment = new FamiliesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_families, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeTabs();
        initializeToolbar();
    }

    private void initializeToolbar() {
        toolBarTitle.setText("My Families");
        profileImage.setOnClickListener(v -> {
            FamilyMember familyMember = new FamilyMember();
            familyMember.setId(Integer.parseInt(SharedPref.getUserRegistration().getId()));
            familyMember.setUserId(familyMember.getId());
            Intent intent = new Intent(getActivity(), ProfileActivity.class);
            intent.putExtra(Constants.Bundle.DATA, familyMember);
            intent.putExtra(Constants.Bundle.FOR_EDITING, true);
            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation(getActivity(), profileImage, "profile");
            startActivity(intent, options.toBundle());
        });
        setNotificationCount(mListener.getNotificationsCount());
        Utilities.loadProfilePicture(profileImage);
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

    private void initializeTabs() {
        familiesTab.addTab(familiesTab.newTab().setText("My Family"));
        //familiesTab.addTab(familiesTab.newTab().setText("Discover"));
        familyPagerAdapter = new FamilyPagerAdapter(getChildFragmentManager(), familiesTab.getTabCount());
        familiyViewPager.setAdapter(familyPagerAdapter);
        familiyViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(familiesTab));
        familiesTab.addOnTabSelectedListener(this);
        familiyViewPager.setPagingEnabled(false);
        familiesTab.addOnTabSelectedListener(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof HomeInteractor) {
            mListener = (HomeInteractor) context;
        } else {
            //throw new RuntimeException(context.toString() + " must implement HomeInteractor");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        if (tab.getPosition() == 1) {
            mListener.loadGlobalSearch();
            return;
        }
        familiyViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        appBar.setExpanded(true);

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @OnClick({R.id.bellIcon, R.id.bellNotificationCount, R.id.feedback})
    public void onViewClicked(View view) {
        switch (view.getId()) {
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
}
