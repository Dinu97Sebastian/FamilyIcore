
package com.familheey.app.Fragments.FamilyViewMembers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.Group;
import androidx.fragment.app.Fragment;

import com.familheey.app.Activities.FamilyAddMemberActivity;
import com.familheey.app.Activities.FamilyDashboardActivity;
import com.familheey.app.Adapters.FamilySubscriptionAdapter;
import com.familheey.app.Dialogs.FamilyFeaturesFragment;
import com.familheey.app.Fragments.FamilyDashboard.BlockedUsersFragment;
import com.familheey.app.Fragments.GroupInviteFragment;
import com.familheey.app.Interfaces.FamilyDashboardInteractor;
import com.familheey.app.Interfaces.FamilyDashboardListener;
import com.familheey.app.Models.Response.Family;
import com.familheey.app.PaymentHistory.AllPaymentHistoryActivity;
import com.familheey.app.R;
import com.familheey.app.Utilities.Constants;
import com.familheey.app.Utilities.NonSwipeableViewPager;
import com.familheey.app.Utilities.Utilities;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.tabs.TabLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.familheey.app.Utilities.Constants.FamilyDashboardIdentifiers.TypeFamilySubscriptionFragment;
import static com.familheey.app.Utilities.Constants.Selections.SELECTED;
import static com.familheey.app.Utilities.Constants.Selections.UN_SELECTED;
import static com.familheey.app.Utilities.Utilities.changeTint;

public class FamilySubscriptionFragment extends Fragment implements TabLayout.OnTabSelectedListener, FamilyFeaturesFragment.OnFamilyFeaturesListener {


    @BindView(R.id.subscriptionViewPager)
    NonSwipeableViewPager subscriptionViewPager;
    @BindView(R.id.addMembers)
    CardView addMembers;
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
    @BindView(R.id.imageInvite)
    ImageView imageInvite;
    @BindView(R.id.labelInvite)
    TextView labelInvite;
    @BindView(R.id.invite)
    MaterialCardView invite;
    @BindView(R.id.imageMore)
    ImageView imageMore;
    @BindView(R.id.labelMore)
    TextView labelMore;
    @BindView(R.id.more)
    MaterialCardView more;
    @BindView(R.id.tabGroup)
    Group tabGroup;


   /* @BindView(R.id.toolbar_family_subscription)
    Toolbar toolbar;*/
    private FamilySubscriptionAdapter familySubscriptionAdapter;
    private OnFamilySubscriptionListener mListener;
    private FamilyDashboardListener familyDashboardListener;
    private FamilyDashboardInteractor familyDashboardInteractor;
    private Family family;

    public FamilySubscriptionFragment() {
        // Required empty public constructor
    }

    public static FamilySubscriptionFragment newInstance(Family family) {
        FamilySubscriptionFragment fragment = new FamilySubscriptionFragment();
        Bundle args = new Bundle();
        args.putParcelable(Constants.Bundle.DATA, family);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            family = getArguments().getParcelable(Constants.Bundle.DATA);
            getArguments().clear();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_family_subscription, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeTabs();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = Utilities.getListener(this, OnFamilySubscriptionListener.class);
        familyDashboardListener = Utilities.getListener(this, FamilyDashboardListener.class);
        familyDashboardInteractor = Utilities.getListener(this, FamilyDashboardInteractor.class);
        if (familyDashboardInteractor == null)
            throw new RuntimeException("The parent fragment must implement FamilyDashboardInteractor");
        if (mListener == null)
            throw new RuntimeException("The parent fragment must implement OnFamilySubscriptionListener");
        if (familyDashboardListener == null)
            throw new RuntimeException("The parent fragment must implement FamilyDashboardListener");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        familyDashboardListener = null;
        familyDashboardInteractor = null;
    }

    @OnClick(R.id.addMembers)
    public void onViewClicked() {
        createNewMember();
    }

    public void createNewMember() {
        Intent intent = new Intent(getContext(), FamilyAddMemberActivity.class);
        intent.putExtra(Constants.Bundle.DATA, family);
        startActivityForResult(intent, FamilyAddMemberActivity.REQUEST_CODE);
    }

    private void initializeTabs() {
        familySubscriptionAdapter = new FamilySubscriptionAdapter(getChildFragmentManager(), 5, family);
        subscriptionViewPager.setAdapter(familySubscriptionAdapter);
        subscriptionViewPager.setPagingEnabled(false);
        members.performClick();
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        subscriptionViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    public void fillDetails(Family family) {
        this.family = family;
        if (family.isUserInThisFamily()) {
            //subscriptionViewPager.setPagingEnabled(true);
            tabGroup.setVisibility(View.VISIBLE);
        } else {
            //subscriptionViewPager.setPagingEnabled(false);
            tabGroup.setVisibility(View.GONE);
        }

        FamilyMembersFragment familyMembersFragment = (FamilyMembersFragment) familySubscriptionAdapter.getRegisteredFragment(0);
        if (familyMembersFragment != null) familyMembersFragment.updateFamily(family);
        FamilyRequestsFragment familyRequestsFragment = (FamilyRequestsFragment) familySubscriptionAdapter.getRegisteredFragment(1);
        if (familyRequestsFragment != null) familyRequestsFragment.updateFamily(family);
        GroupInviteFragment groupInviteFragment = (GroupInviteFragment) familySubscriptionAdapter.getRegisteredFragment(2);
        if (groupInviteFragment != null) groupInviteFragment.updateFamily(family);
        BlockedUsersFragment blockedUsersFragment = (BlockedUsersFragment) familySubscriptionAdapter.getRegisteredFragment(3);
        if (blockedUsersFragment != null) blockedUsersFragment.updateFamily(family);
    }

    @Override
    public void onBlockedMembersRequested(Family family) {
        changeTint(getContext(), true, labelMore, imageMore);
        changeTint(getContext(), false, labelMembers, imageMembers, labelRequests, imageRequests, labelInvite, imageInvite);
        subscriptionViewPager.setCurrentItem(3);
    }

    @Override
    public void onPendingRequestRequested(Family family) {
        changeTint(getContext(), true, labelMore, imageMore);
        changeTint(getContext(), false, labelMembers, imageMembers, labelRequests, imageRequests, labelInvite, imageInvite);
        subscriptionViewPager.setCurrentItem(4);
    }


    public interface OnFamilySubscriptionListener {
        void loadMembersToBeAddedToFamily(Family family);
    }

    @OnClick({R.id.members, R.id.requests, R.id.invite, R.id.more})
    public void onViewClicked(View view) {
        members.setTag(UN_SELECTED);
        requests.setTag(UN_SELECTED);
        invite.setTag(UN_SELECTED);
        more.setTag(UN_SELECTED);
        switch (view.getId()) {
            case R.id.members:
                members.setTag(SELECTED);
                changeTint(getContext(), true, labelMembers, imageMembers);
                changeTint(getContext(), false, labelRequests, imageRequests, labelMore, imageMore, labelInvite, imageInvite);
                subscriptionViewPager.setCurrentItem(0);
                familyDashboardInteractor.onFamilyAddComponentVisible(TypeFamilySubscriptionFragment);
                break;
            case R.id.requests:
                requests.setTag(SELECTED);
                changeTint(getContext(), true, labelRequests, imageRequests);
                changeTint(getContext(), false, labelMembers, imageMembers, labelMore, imageMore, labelInvite, imageInvite);
                subscriptionViewPager.setCurrentItem(1);
                familyDashboardInteractor.onFamilyAddComponentHidden(TypeFamilySubscriptionFragment);
                break;
            case R.id.invite:

                // Modified By: Dinu(06/02/2021) for Only admin can invite
                if (family.getUserStatus() != null && family.getUserStatus().equalsIgnoreCase("admin")) {
                    members.performClick();
                    Intent intent = new Intent(getContext(), FamilyAddMemberActivity.class);
                    intent.putExtra(Constants.Bundle.DATA, family);
                    startActivityForResult(intent, FamilyAddMemberActivity.REQUEST_CODE);
                }
                else{
                    Toast.makeText(getContext(), "Only admin can invite", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.more:
                PopupMenu popup = new PopupMenu(getContext(), more);
                popup.getMenuInflater().inflate(R.menu.popup_family_more, popup.getMenu());
                if (family.isAdmin()) {
                    popup.getMenu().getItem(2).setVisible(true);
                }
                popup.setOnMenuItemClickListener(item -> {
                    if (item.getItemId() == R.id.blockedMembers) {
                        onBlockedMembersRequested(family);
                        familyDashboardInteractor.onFamilyAddComponentHidden(TypeFamilySubscriptionFragment);
                    } else if (item.getItemId() == R.id.pendingInvites) {
                        onPendingRequestRequested(family);
                        familyDashboardInteractor.onFamilyAddComponentHidden(TypeFamilySubscriptionFragment);
                    } else if (item.getItemId() == R.id.payment) {
                        startActivity(new Intent(getContext(), AllPaymentHistoryActivity.class)
                                .putExtra("NAME", family.getGroupName())
                                .putExtra(Constants.Bundle.FAMILY_ID, family.getId() + ""));
                    }else if(item.getItemId()==R.id.invite){
                        if (family.getUserStatus() != null && family.getUserStatus().equalsIgnoreCase("admin")) {
                            members.performClick();
                            Intent intent = new Intent(getContext(), FamilyAddMemberActivity.class);
                            intent.putExtra(Constants.Bundle.DATA, family);
                            startActivityForResult(intent, FamilyAddMemberActivity.REQUEST_CODE);
                        }
                        else{
                            Toast.makeText(getContext(), "Only admin can invite", Toast.LENGTH_SHORT).show();
                        }

                    }else if(item.getItemId()==R.id.requests){
                        requests.setTag(SELECTED);
                        changeTint(getContext(), true, labelRequests, imageRequests);
                        changeTint(getContext(), false, labelMembers, imageMembers, labelMore, imageMore, labelInvite, imageInvite);
                        subscriptionViewPager.setCurrentItem(1);
                        familyDashboardInteractor.onFamilyAddComponentHidden(TypeFamilySubscriptionFragment);
                    }
                    return true;
                });
                popup.show();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FamilyAddMemberActivity.REQUEST_CODE) {
            familyDashboardListener.onFamilyUpdated(false);
        }
    }

    public void displayMemberRequest() {
        new Handler().postDelayed(() -> requests.performClick(), 300);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (subscriptionViewPager != null && subscriptionViewPager.getCurrentItem() != 0) {
            if (familyDashboardInteractor != null)
                familyDashboardInteractor.onFamilyAddComponentHidden(TypeFamilySubscriptionFragment);
        }else {
            if (familyDashboardInteractor != null)
                familyDashboardInteractor.onFamilyAddComponentVisible(TypeFamilySubscriptionFragment);
        }

    }
}
