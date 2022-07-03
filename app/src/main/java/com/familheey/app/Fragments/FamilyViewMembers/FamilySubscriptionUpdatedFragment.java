package com.familheey.app.Fragments.FamilyViewMembers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.familheey.app.Activities.FamilyAddMemberActivity;
import com.familheey.app.Activities.FamilyDashboardActivity;
import com.familheey.app.Adapters.FamilySubscriptionAdapter;
import com.familheey.app.Adapters.MemberRequestAdapter;
import com.familheey.app.Dialogs.FamilyFeaturesFragment;
import com.familheey.app.Fragments.FamilyDashboard.BackableFragment;
import com.familheey.app.Fragments.FamilyDashboard.BlockedUsersFragment;
import com.familheey.app.Fragments.FamilyDashboard.LinkingFragment;
import com.familheey.app.Fragments.FamilyDashboard.PendingRequestFragment;
import com.familheey.app.Fragments.FamilyDashboardFragment;
import com.familheey.app.Interfaces.FamilyDashboardInteractor;
import com.familheey.app.Interfaces.FamilyDashboardListener;
import com.familheey.app.Models.Response.Family;
import com.familheey.app.PaymentHistory.AllPaymentHistoryActivity;
import com.familheey.app.R;
import com.familheey.app.Utilities.Constants;
import com.familheey.app.Utilities.Utilities;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.familheey.app.Utilities.Constants.Bundle.DATA;
import static com.familheey.app.Utilities.Constants.FamilyDashboardIdentifiers.TypeFamilySubscriptionFragment;
import static com.familheey.app.Utilities.Constants.Selections.SELECTED;
import static com.familheey.app.Utilities.Utilities.changeTint;


public class FamilySubscriptionUpdatedFragment extends BackableFragment implements FamilyFeaturesFragment.OnFamilyFeaturesListener,View.OnClickListener{
    @BindView(R.id.moreOptions)
    ImageView moreOptions;
    /* @BindView(R.id.addMembers)
     CardView addMembers;*/
    @BindView(R.id.memberCount)
    TextView membersCount;
    @BindView(R.id.requestsCount)
    TextView requestsCount;
    @BindView(R.id.content_title)
    TextView contentTitle;
    private FamilySubscriptionAdapter familySubscriptionAdapter;
    private FamilySubscriptionFragment.OnFamilySubscriptionListener mListener;
    private FamilyDashboardListener familyDashboardListener;
    private FamilyDashboardInteractor familyDashboardInteractor;
    public Family family;
    public String parentPage="";
    public String requestStatus= "";
    public FamilySubscriptionUpdatedFragment() {

    }

    public static FamilySubscriptionUpdatedFragment newInstance(Family family) {
        FamilySubscriptionUpdatedFragment fragment = new FamilySubscriptionUpdatedFragment();
        Bundle args = new Bundle();
        args.putParcelable(Constants.Bundle.DATA, family);
        fragment.setArguments(args);
        return fragment;
    }
    public static FamilySubscriptionUpdatedFragment newInstance(Family family,String from) {
        FamilySubscriptionUpdatedFragment fragment = new FamilySubscriptionUpdatedFragment();
        Bundle args = new Bundle();
        args.putParcelable(Constants.Bundle.DATA, family);
        args.putString("parentpage",from);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            family = getArguments().getParcelable(Constants.Bundle.DATA);
            if(getArguments().getString("parentpage")!=null)
                parentPage=getArguments().getString("parentpage");
            getArguments().clear();
        }

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_family_subscription_updated, container, false);
        ButterKnife.bind(this, view);
       // getMemberCount();
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        replaceFragments();
        if(parentPage.equals("fromPage")){
            loadRequestFragment();
        }else if(parentPage.equals("toMember")){
            replaceFragments();
        }
        moreOptions.setOnClickListener(this::onClick);
        getMemberCount();
    }

    @Override
    public void onBackButtonPressed() {
        if(parentPage.equals("fromPage")){
            requireActivity().onBackPressed();
        }else if(parentPage.equals("toMember")){
            requireActivity().onBackPressed();
        }
        else {
            ((FamilyDashboardActivity)requireActivity()).backAction();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = Utilities.getListener(this, FamilySubscriptionFragment.OnFamilySubscriptionListener.class);
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
        parentPage="";
    }


    public void createNewMember() {

        if (family.getUserStatus() != null && family.getUserStatus().equalsIgnoreCase("admin")) {
            Intent intent = new Intent(requireActivity(), FamilyAddMemberActivity.class);
            intent.putExtra(Constants.Bundle.DATA, family);
            startActivityForResult(intent, FamilyAddMemberActivity.REQUEST_CODE);
        }
        else{
            Toast.makeText(getContext(), "Only admin can invite", Toast.LENGTH_SHORT).show();
        }


    }
    @Override
    public void onBlockedMembersRequested(Family family) {

    }

    @Override
    public void onPendingRequestRequested(Family family) {

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FamilyAddMemberActivity.REQUEST_CODE) {
            familyDashboardListener.onFamilyUpdated(false);
        }
    }
    @Override
    public void onResume() {
        super.onResume();
    }

    /*public void displayMemberRequest() {
        new Handler().postDelayed(() -> requests.performClick(), 300);
    }*/

    @Override
    public void onClick(View view) {
        if(view == moreOptions){
            PopupMenu popup = new PopupMenu(getContext(), moreOptions);
            popup.getMenuInflater().inflate(R.menu.popup_family_more, popup.getMenu());
            if (family.isAdmin()) {
                popup.getMenu().getItem(2).setVisible(true);
            }
            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.blockedMembers) {
                    /*onBlockedMembersRequested(family);
                    familyDashboardInteractor.onFamilyAddComponentHidden(TypeFamilySubscriptionFragment);*/
                    if (family.getUserStatus() != null && family.getUserStatus().equalsIgnoreCase("admin")) {
                        Fragment fragment = BlockedUsersFragment.newInstance(family);
                        loadFamilyRequest(fragment);
                    }else {
                        Toast.makeText(getContext(), "Not Authorised", Toast.LENGTH_SHORT).show();
                    }
                } else if (item.getItemId() == R.id.pendingInvites) {
                  /*  onPendingRequestRequested(family);
                    familyDashboardInteractor.onFamilyAddComponentHidden(TypeFamilySubscriptionFragment);*/
                    if (family.getUserStatus() != null && family.getUserStatus().equalsIgnoreCase("admin")) {
                        Fragment fragment = PendingRequestFragment.newInstance(family.getId().toString());
                        loadFamilyRequest(fragment);
                    }
                    else{
                        Toast.makeText(getContext(), "Not Authorised", Toast.LENGTH_SHORT).show();
                    }
                } else if (item.getItemId() == R.id.payment) {
                    startActivity(new Intent(getContext(), AllPaymentHistoryActivity.class)
                            .putExtra("NAME", family.getGroupName())
                            .putExtra(Constants.Bundle.FAMILY_ID, family.getId() + ""));
                }else if(item.getItemId()==R.id.invite){
                    if (family.getUserStatus() != null && family.getUserStatus().equalsIgnoreCase("admin")) {
                        Intent intent = new Intent(getContext(), FamilyAddMemberActivity.class);
                        intent.putExtra(Constants.Bundle.DATA, family);
                        startActivityForResult(intent, FamilyAddMemberActivity.REQUEST_CODE);
                    }
                    else{
                        Toast.makeText(getContext(), "Only admin can invite", Toast.LENGTH_SHORT).show();
                    }
                }else if(item.getItemId()==R.id.requests){
                    /*Fragment fragment = FamilyRequestsFragment.newInstance(family,Constants.FileUpload.TYPE_FAMILY);
                    loadFamilyRequest(fragment);*/
                    if ((family.getUserStatus() != null && family.getUserStatus().equalsIgnoreCase("admin")) || family.getMemberApproval().equals(5) ) {
                        loadRequestFragment();
                    }
                    else{
                        Toast.makeText(getContext(), "Not Authorised", Toast.LENGTH_SHORT).show();
                    }

                }else if(item.getItemId()==R.id.members){
                    Fragment fragment = FamilyMembersFragment.newInstance(family);
                    loadFamilyRequest(fragment);
                }
                return true;
            });
            popup.show();
        }
    }

    private void loadFamilyRequest(Fragment fragment) {
        if(fragment!=null){
            if(fragment instanceof BlockedUsersFragment){
                requireActivity().findViewById(R.id.addComponent).setVisibility(View.GONE);
                requireActivity().findViewById(R.id.toolbar_with_search_icon).setVisibility(View.GONE);
                requireActivity().findViewById(R.id.toolbar_without_search_icon).setVisibility(View.VISIBLE);
                contentTitle.setText("Blocked Users");
            }/*else if(fragment instanceof FamilyRequestsFragment){
                requireActivity().findViewById(R.id.addComponent).setVisibility(View.GONE);
                requireActivity().findViewById(R.id.toolbar_with_search_icon).setVisibility(View.GONE);
                requireActivity().findViewById(R.id.toolbar_without_search_icon).setVisibility(View.VISIBLE);
                contentTitle.setText("Requests");
            }*/else if(fragment instanceof PendingRequestFragment){
                requireActivity().findViewById(R.id.addComponent).setVisibility(View.GONE);
                requireActivity().findViewById(R.id.toolbar_with_search_icon).setVisibility(View.GONE);
                requireActivity().findViewById(R.id.toolbar_without_search_icon).setVisibility(View.VISIBLE);
                contentTitle.setText("Pending Invites");
            }else if(fragment instanceof FamilyMembersFragment){
                requireActivity().findViewById(R.id.addComponent).setVisibility(View.VISIBLE);
                requireActivity().findViewById(R.id.toolbar_with_search_icon).setVisibility(View.VISIBLE);
                requireActivity().findViewById(R.id.toolbar_without_search_icon).setVisibility(View.GONE);
                //getMembersCount(family);
                contentTitle.setText("Members");
            }
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.members_container, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }

    /** Replace fragments **/
    public void replaceFragments(){
        FamilyMembersFragment familyMembersFragment = FamilyMembersFragment.newInstance(family);
        familyMembersFragment.updateFamily(family);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.members_container,familyMembersFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        requireActivity().findViewById(R.id.addComponent).setVisibility(View.VISIBLE);
        requireActivity().findViewById(R.id.toolbar_with_search_icon).setVisibility(View.VISIBLE);

    }
    public void loadRequestFragment(){
        FamilyRequestsFragment familyRequestsFragment = FamilyRequestsFragment.newInstance(family, FamilyRequestsFragment.MEMBER_REQUEST);
        familyRequestsFragment.updateFamily(family);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.members_container, familyRequestsFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        requireActivity().findViewById(R.id.addComponent).setVisibility(View.GONE);
        requireActivity().findViewById(R.id.toolbar_with_search_icon).setVisibility(View.GONE);
        requireActivity().findViewById(R.id.toolbar_without_search_icon).setVisibility(View.VISIBLE);
        contentTitle.setText("Requests");
    }
    /** method for setting members count **/
    private void getMemberCount(){
        if (family.getMembersCount() != null) {
            membersCount.setText(family.getMembersCount());
        }
        if(family.getRequestCount()!=null){
            requestsCount.setText(family.getRequestCount());
        }
    }

    @Override
    public void onAttachFragment(@NonNull Fragment childFragment) {
        super.onAttachFragment(childFragment);
    }

   /* @Override
    public void requestStatus(String status) {
        requestStatus=status;
        if(requestStatus.equals("accepted")){
            String s1 = membersCount.getText().toString();
            int i=Integer.parseInt(s1);
            i = i+1;
            membersCount.setText(String.valueOf(i));
        }
    }*/
    /**@author Devika on 01-11-2021
     * for updating members count inside family dashboard and FamilySubscriptionUpdatedFragment
     * when a member request got accepted
     * **/
    public void setUpdatedMemberCount(int count){
        membersCount.setText(String.valueOf(count));
        /**@author Devika on 01-11-2021
         * for updation members count inside family dashboard when admin removes a member
         * **/
        Fragment fragment1 = requireActivity().getSupportFragmentManager().findFragmentById(R.id.familyDashboardContainer);
        if (fragment1 instanceof FamilyDashboardFragment) {
            FamilyDashboardFragment familyDashboardFragment = (FamilyDashboardFragment) fragment1;
            familyDashboardFragment.setUpdatedMemberCount(count);
        }
    }
    public void setUpdatedRequestCount(int count){
        requestsCount.setText(String.valueOf(count));
    }
    public void updateMemberCountOnRemoving(int count){
        membersCount.setText(String.valueOf(count));
    }
}