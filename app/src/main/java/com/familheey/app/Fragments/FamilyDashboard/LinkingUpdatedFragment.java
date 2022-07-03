package com.familheey.app.Fragments.FamilyDashboard;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.familheey.app.Activities.FamilyDashboardActivity;
import com.familheey.app.Activities.LinkFamilyActivity;
import com.familheey.app.Adapters.LinkedFamilypagerAdapter;
import com.familheey.app.Fragments.FamilyViewMembers.FamilyMembersFragment;
import com.familheey.app.Fragments.FamilyViewMembers.FamilyRequestsFragment;
import com.familheey.app.Fragments.LinkedFamilyFragment;
import com.familheey.app.Models.Response.Family;
import com.familheey.app.R;
import com.familheey.app.Utilities.Constants;
import com.google.android.material.card.MaterialCardView;
import com.google.rpc.Help;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.familheey.app.Utilities.Constants.Bundle.DATA;
import static com.familheey.app.Utilities.Constants.Selections.SELECTED;
import static com.familheey.app.Utilities.Constants.Selections.UN_SELECTED;
import static com.familheey.app.Utilities.Utilities.changeTint;


public class LinkingUpdatedFragment extends BackableFragment implements View.OnClickListener {

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
public static String linkedRequest="";
public String parentPage;
    public LinkingUpdatedFragment() {
        // Required empty public constructor
    }
    public static LinkingUpdatedFragment newInstance(Family family) {
        LinkingUpdatedFragment fragment = new LinkingUpdatedFragment();
        Bundle args = new Bundle();
        args.putParcelable(DATA, family);
        fragment.setArguments(args);
        return fragment;
    }

    public static LinkingUpdatedFragment newInstance(Family family,String parentPage) {
        LinkingUpdatedFragment fragment = new LinkingUpdatedFragment();
        Bundle args = new Bundle();
        args.putParcelable(DATA, family);
        linkedRequest=parentPage;
        fragment.setArguments(args);
        return fragment;
    }

    public static LinkingUpdatedFragment newInstance(String param1, String param2) {
        LinkingUpdatedFragment fragment = new LinkingUpdatedFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            family = getArguments().getParcelable(DATA);
            parentPage=linkedRequest;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_linking_updated, container, false);
        ButterKnife.bind(this, view);
        initializeTitle();
        //fabVisibility();
        members.setOnClickListener(this::onClick);
        requests.setOnClickListener(this::onClick);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadFamilies();
        if(parentPage.equals("toLinkedRequest")){
            loadRequests();
        }

    }

    @Override
    public void onBackButtonPressed() {
        if(parentPage.equals("toLinkedRequest")){
            requireActivity().onBackPressed();
        }else {
            ((FamilyDashboardActivity)requireActivity()).backAction();
        }
    }

    private void initializeTitle() {
        title.setText(getResources().getString(R.string.fragment_dashboard_menu_linkedFamilies));
    }
    private void initializeAdapter() {
        try {
            linkedFamilypagerAdapter = new LinkedFamilypagerAdapter(getChildFragmentManager());
            linkedFamilypagerAdapter.addFragment(LinkedFamilyFragment.newInstance(family), "Families");
           /* viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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
            });*/
            members.performClick();
            if (family.isAdmin()) {
               // viewPager.setPagingEnabled(true);
                linkedFamilypagerAdapter.addFragment(FamilyRequestsFragment.newInstance(family, FamilyRequestsFragment.LINK_REQUEST), "Request");
            } else {
               // viewPager.setPagingEnabled(false);
                members.setVisibility(View.GONE);
                requests.setVisibility(View.GONE);
            }
            //viewPager.setAdapter(linkedFamilypagerAdapter);
           // viewPager.setOffscreenPageLimit(2);
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
        //initializeAdapter();
    }
    public void createNewFamilyLink() {
        if (!family.canLinkFamily()) {
            Toast.makeText(getContext(), "You don't have permission to Link Families!!", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(getContext(), LinkFamilyActivity.class);
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

    @Override
    public void onClick(View view) {
        members.setTag(UN_SELECTED);
        requests.setTag(UN_SELECTED);
        if(view == members){
            members.setTag(SELECTED);
            changeTint(getContext(), true, labelMembers, imageMembers);
            changeTint(getContext(), false, labelRequests, imageRequests);
            loadFamilies();
        }else if(view == requests){
            requests.setTag(SELECTED);
            changeTint(getContext(), true, labelRequests, imageRequests);
            changeTint(getContext(), false, labelMembers, imageMembers);
            loadRequests();
        }
    }
   /* public void fabVisibility(){
        Fragment fragment = requireActivity().getSupportFragmentManager().findFragmentById(R.id.linked_family_container);
        if(fragment instanceof LinkedFamilyFragment){
            requireActivity().findViewById(R.id.addComponent).setVisibility(View.GONE);
        }
    }*/
    private void loadFamilies() {
        LinkedFamilyFragment linkedFamilyFragment = LinkedFamilyFragment.newInstance(family);
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.linked_family_container,linkedFamilyFragment,"linkedFamily");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        //requireActivity().findViewById(R.id.addComponent).setVisibility(View.GONE);
        requireActivity().findViewById(R.id.toolbar_with_search_icon).setVisibility(View.VISIBLE);
        requireActivity().findViewById(R.id.toolbar_without_search_icon).setVisibility(View.GONE);
    }

    private void loadRequests() {
        FamilyRequestsFragment familyRequestsFragment = FamilyRequestsFragment.newInstance(family,FamilyRequestsFragment.LINK_REQUEST);
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.linked_family_container, familyRequestsFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        requireActivity().findViewById(R.id.addComponent).setVisibility(View.GONE);
        requireActivity().findViewById(R.id.toolbar_with_search_icon).setVisibility(View.GONE);
        requireActivity().findViewById(R.id.toolbar_without_search_icon).setVisibility(View.VISIBLE);
    }

}