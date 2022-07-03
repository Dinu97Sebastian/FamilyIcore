package com.familheey.app.Fragments.FamilyDashboard;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.familheey.app.Activities.FamilyDashboardActivity;
import com.familheey.app.Activities.LinkFamilyActivity;
import com.familheey.app.Adapters.LinkedFamilypagerAdapter;
import com.familheey.app.Fragments.FamilyViewMembers.FamilyRequestsFragment;
import com.familheey.app.Fragments.LinkedFamilyFragment;
import com.familheey.app.Models.Response.Family;
import com.familheey.app.R;
import com.familheey.app.Utilities.NonSwipeableViewPager;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.tabs.TabLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.familheey.app.Utilities.Constants.Bundle.DATA;
import static com.familheey.app.Utilities.Constants.Selections.SELECTED;
import static com.familheey.app.Utilities.Constants.Selections.UN_SELECTED;
import static com.familheey.app.Utilities.Utilities.changeTint;

public class LinkingFragment extends BackableFragment implements TabLayout.OnTabSelectedListener {
    @BindView(R.id.viewPager)
    NonSwipeableViewPager viewPager;
    @BindView(R.id.imageMembers)
    ImageView imageMembers;
    @BindView(R.id.labelMembers)
    TextView labelMembers;
    @BindView(R.id.members)
    MaterialCardView members;
    @BindView(R.id.imageRequests)
    ImageView imageRequests;
    @BindView(R.id.labelRequests)
    TextView labelRequests;
    @BindView(R.id.requests)
    MaterialCardView requests;
    @BindView(R.id.familyToolBarTitle)
    TextView title;
    private Family family;
    private LinkedFamilypagerAdapter linkedFamilypagerAdapter;
    public LinkingFragment() {
        // Required empty public constructor
    }

    public static LinkingFragment newInstance(Family family) {
        LinkingFragment fragment = new LinkingFragment();
        Bundle args = new Bundle();
        args.putParcelable(DATA, family);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            family = getArguments().getParcelable(DATA);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
/** Defined by Devika on 24/08/2021 **/
    @Override
    public void onBackButtonPressed() {
        ((FamilyDashboardActivity)requireActivity()).backAction();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_linking, container, false);
        ButterKnife.bind(this, view);
        initializeTitle();
        return view;
    }

    private void initializeTitle() {
        title.setText(getResources().getString(R.string.fragment_dashboard_menu_linkedFamilies));
    }

    private void initializeAdapter() {
        try {
            linkedFamilypagerAdapter = new LinkedFamilypagerAdapter(getChildFragmentManager());
            linkedFamilypagerAdapter.addFragment(LinkedFamilyFragment.newInstance(family), "Families");
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    switch (position) {
                        case 0:
                            members.performClick();
                            break;
                        case 1:
                            requests.performClick();
                            break;
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
            members.performClick();
            if (family.isAdmin()) {
                viewPager.setPagingEnabled(true);
                linkedFamilypagerAdapter.addFragment(FamilyRequestsFragment.newInstance(family, FamilyRequestsFragment.LINK_REQUEST), "Request");
            } else {
                viewPager.setPagingEnabled(false);
                members.setVisibility(View.GONE);
                requests.setVisibility(View.GONE);
            }
            viewPager.setAdapter(linkedFamilypagerAdapter);
            viewPager.setOffscreenPageLimit(2);
            if (family.isAdmin()) {
                FamilyRequestsFragment familyRequestsFragment = (FamilyRequestsFragment) linkedFamilypagerAdapter.getRegisteredFragment(1);
                if (familyRequestsFragment != null)
                    familyRequestsFragment.updateFamily(family);
            }
        } catch (Exception e) {
            getActivity().finish();
        }
    }

    public void updateFamily(Family family) {
        this.family = family;
        initializeAdapter();
    }

    @OnClick({R.id.members, R.id.requests})
    public void onViewClicked(View view) {
        members.setTag(UN_SELECTED);
        requests.setTag(UN_SELECTED);
        switch (view.getId()) {
            case R.id.members:
                members.setTag(SELECTED);
                changeTint(getContext(), true, labelMembers, imageMembers);
                changeTint(getContext(), false, labelRequests, imageRequests);
                viewPager.setCurrentItem(0);
                break;
            case R.id.requests:
                requests.setTag(SELECTED);
                changeTint(getContext(), true, labelRequests, imageRequests);
                changeTint(getContext(), false, labelMembers, imageMembers);
                viewPager.setCurrentItem(1);
                break;
        }
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    public void createNewFamilyLink() {
        if (!family.canLinkFamily()) {
            Toast.makeText(getContext(), "You don't have permission to Link Families!!", Toast.LENGTH_SHORT).show();
           return;
        }Intent intent = new Intent(getContext(), LinkFamilyActivity.class);
        intent.putExtra(DATA, family);
        startActivity(intent);
    }

    public void displayLinkRequest() {
        try {
            new Handler().postDelayed(() -> requests.performClick(), 300);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
