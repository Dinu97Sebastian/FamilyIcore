package com.familheey.app.Fragments.FamilyViewMembers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextUtils;
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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.familheey.app.Activities.InviteFriendsActivity;
import com.familheey.app.Activities.ProfileActivity;
import com.familheey.app.Adapters.FamilyMembersAdapter;
import com.familheey.app.CustomViews.TextViews.SemiBoldTextView;
import com.familheey.app.Decorators.BottomAdditionalMarginDecorator;
import com.familheey.app.Dialogs.RelationShipSelectionActivity;
import com.familheey.app.Fragments.FamilyDashboardFragment;
import com.familheey.app.Interfaces.FamilyDashboardInteractor;
import com.familheey.app.Interfaces.FamilyDashboardListener;
import com.familheey.app.Interfaces.RetrofitListener;
import com.familheey.app.Models.ApiCallbackParams;
import com.familheey.app.Models.ErrorData;
import com.familheey.app.Models.Response.Family;
import com.familheey.app.Models.Response.FamilyMember;
import com.familheey.app.Models.Response.RelationShip;
import com.familheey.app.Networking.Retrofit.ApiServiceProvider;
import com.familheey.app.PaginationListener;
import com.familheey.app.Parsers.FamilyParser;
import com.familheey.app.PaymentHistory.PaymentHistoryActivity;
import com.familheey.app.R;
import com.familheey.app.Utilities.Constants;
import com.familheey.app.Utilities.SharedPref;
import com.familheey.app.Utilities.Utilities;
import com.familheey.app.pagination.PaginationScrollListener;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;

import static com.familheey.app.Utilities.Constants.Bundle.FAMILY_ID;
import static com.familheey.app.Utilities.Constants.Bundle.ID;
import static com.familheey.app.Utilities.Constants.Bundle.MEMBER;
import static com.familheey.app.Utilities.Constants.Bundle.RELATIONSHIP;

public class FamilyMembersFragment extends Fragment implements RetrofitListener, FamilyMembersAdapter.OnFamilyMemberCallback {

    private static final int UPDATE_RELATIONSHIP = 2;
    private static final int ADD_RELATIONSHIP = 1;

    /*@BindView(R.id.searchMembers)
     EditText searchMembers;*/
    @BindView(R.id.membersCount)
    TextView membersCount;
    @BindView(R.id.adminsCount)
    TextView adminsCount;
    @BindView(R.id.inviteFriends)
    TextView inviteFriends;
    @BindView(R.id.membersList)
    RecyclerView membersList;
    @BindView(R.id.progressBar3)
    ProgressBar progressBar;
    @BindView(R.id.clearSearch)
    ImageView clearSearch;
    @BindView(R.id.emptyResultText)
    SemiBoldTextView emptyResultText;
    String query ="";
    static int membersNo;
    private Family family;

    private FamilyDashboardListener mListener;
    private final List<FamilyMember> familyMembers = new ArrayList<>();
    private final List<RelationShip> relationShips = new ArrayList<>();
    private FamilyDashboardInteractor familyDashboardInteractor;
    private FamilyMembersAdapter familyMembersAdapter;
    private boolean isLoading = true;
    private boolean isLastPage = false;
    private final int TOTAL_PAGES = 0;
    public FamilyMembersFragment() {
        // Required empty public constructor
    }

    public static FamilyMembersFragment newInstance(Family family) {
        FamilyMembersFragment fragment = new FamilyMembersFragment();
        Bundle args = new Bundle();
        args.putInt("membersCount",membersNo);
        args.putParcelable(Constants.Bundle.DATA, family);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            family = getArguments().getParcelable(Constants.Bundle.DATA);

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_family_members, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        familyMembers.clear();
        initMemberAdapters();

        //initializeSearchClearCallback();
    }

   /* private void initializeSearchClearCallback() {
        searchMembers.addTextChangedListener(new TextWatcher() {

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
            searchMembers.setText("");
            onSearchQueryListener(EditorInfo.IME_ACTION_SEARCH);
        });
    }*/

    private void initMemberAdapters() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        familyMembersAdapter = new FamilyMembersAdapter(this, getContext(), familyMembers, family);
        membersList.setHasFixedSize(true);
        membersList.setItemViewCacheSize(1000);
        membersList.setDrawingCacheEnabled(true);
        membersList.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        membersList.setNestedScrollingEnabled(true);
        //membersList.setLayoutManager(layoutManager);
        membersList.addItemDecoration(new BottomAdditionalMarginDecorator());
        membersList.setLayoutManager(layoutManager);
        membersList.setAdapter(familyMembersAdapter);
        requestFamilyMembers();
        membersList.addOnScrollListener(new PaginationListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                progressBar.setVisibility(View.VISIBLE);
                isLoading = true;
                requestFamilyMembers();
                //membersList.getLayoutManager().smoothScrollToPosition(membersList, new RecyclerView.State(), 10);
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


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FamilyDashboardListener) {
            mListener = (FamilyDashboardListener) context;
            familyDashboardInteractor = Utilities.getListener(this, FamilyDashboardInteractor.class);
        } else {
            throw new RuntimeException(context.toString() + " must implement FamilyDashboardListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        familyDashboardInteractor = null;
        mListener = null;
    }

    @OnClick(R.id.inviteFriends)
    public void onViewClicked() {
        startActivity(new Intent(getActivity(), InviteFriendsActivity.class).putExtra("family", family));
    }
    /** modified on 25/08/2021 for search . used variable query passed from DashboardActivity instead of searchbox text**/
    private void requestFamilyMembers() {
        if(familyMembers.size()==0)
        progressBar.setVisibility(View.VISIBLE);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("group_id", family.getId().toString());
        jsonObject.addProperty("crnt_user_id", SharedPref.getUserRegistration().getId());
        jsonObject.addProperty("limit", "20");
        jsonObject.addProperty("offset",String.valueOf(familyMembers.size()) );
        if (query!=null)
            jsonObject.addProperty("query", query);
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getContext());
        apiServiceProvider.viewFamilyMembers(jsonObject, null, this);
    }

    @Override
    public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {
        progressBar.setVisibility(View.GONE);
        switch (apiFlag) {
            case Constants.ApiFlags.VIEW_FAMILY_MEMBERS:

                try {

                    List<FamilyMember> temp = FamilyParser.parseFamilyMembers(responseBodyString);
                    if (temp != null && temp.size() == 20) {
                        isLoading = false;
                    } else {
                        isLastPage = true;
                    }

                    familyMembers.addAll(FamilyParser.parseFamilyMembers(responseBodyString));
                    familyMembersAdapter.notifyDataSetChanged();
                    fillAdminsAndMembersCount();
                    if (familyMembers != null && familyMembers.size() > 0)
                        emptyResultText.setVisibility(View.INVISIBLE);
                    else emptyResultText.setVisibility(View.VISIBLE);
                } catch (JsonParseException | NullPointerException e) {
                    e.printStackTrace();
                    mListener.showErrorDialog("Please try again later !! Something went wrong");
                    return;
                }
                break;
            case Constants.ApiFlags.GET_ALL_RELATIONS:
                mListener.hideProgressDialog();
                ArrayList<RelationShip> fetchedRelations = (ArrayList<RelationShip>) FamilyParser.parseRelationShip(responseBodyString);
                relationShips.clear();
                relationShips.addAll(fetchedRelations);
                if (apiCallbackParams.getDeveloperCode() == ADD_RELATIONSHIP)
                    openRelationship(relationShips, apiCallbackParams.getFamilyMember(), ADD_RELATIONSHIP);
                else
                    openRelationship(relationShips, apiCallbackParams.getFamilyMember(), UPDATE_RELATIONSHIP);
                break;
            case Constants.ApiFlags.ADD_RELATION:
            case Constants.ApiFlags.UPDATE_RELATIONS:
            case Constants.ApiFlags.UPDATE_USER_RESTRICTIONS:
            default:
                mListener.hideProgressDialog();
                clearAndReload();
                break;
        }
    }

    private void openRelationship(List<RelationShip> relationShips, FamilyMember familyMember, int TYPE) {
        Intent intent = new Intent(getContext(), RelationShipSelectionActivity.class);
        Bundle args = new Bundle();
        args.putParcelableArrayList(RELATIONSHIP, (ArrayList<? extends Parcelable>) relationShips);
        args.putParcelable(Constants.Bundle.MEMBER, familyMember);
        args.putInt(Constants.Bundle.TYPE, TYPE);
        args.putBoolean(Constants.Bundle.IDENTIFIER, family.isRegularGroup());
        intent.putExtras(args);
        startActivityForResult(intent, RelationShipSelectionActivity.REQUEST_CODE);
    }

    private void fillAdminsAndMembersCount() {
        int adminsNo = FamilyParser.getAdminsCount(familyMembers);
        //int membersNo = familyMembers.size() - adminsNo;
        membersNo=Integer.parseInt( family.getMembersCount());
        /*adminsCount.setText(adminsNo + " admins");
        membersCount.setText(membersNo + " members");*/
        Fragment fragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.ll_container_for_menu_fragments);
        if(fragment != null && fragment instanceof FamilySubscriptionUpdatedFragment)
            ((FamilySubscriptionUpdatedFragment) fragment).setUpdatedMemberCount(membersNo);
        /**@author Devika on 01-11-2021
         * for updation members count inside family dashboard when admin removes a member
         * **/
        Fragment fragment1 = requireActivity().getSupportFragmentManager().findFragmentById(R.id.familyDashboardContainer);
        if (fragment1 instanceof FamilyDashboardFragment) {
            FamilyDashboardFragment familyDashboardFragment = (FamilyDashboardFragment) fragment1;
            familyDashboardFragment.setUpdatedMemberCount(membersNo);
        }
    }
    /**
     * on
     * method to return the members count to FamilySubscriptionFragment
     * **/
    public int membersCount(){
        int adminsNo = FamilyParser.getAdminsCount(familyMembers);
        membersNo = familyMembers.size() - adminsNo;
        return membersNo;
    }

    private void clearAndReload() {
        familyMembers.clear();
        familyMembersAdapter.notifyDataSetChanged();
        requestFamilyMembers();
    }

    @Override
    public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
        try {
            mListener.showErrorDialog(errorData.getMessage());
        } catch (Exception e) {
        }
    }


    private void fetchAllRelations(FamilyMember familyMember, int TYPE) {
        mListener.showProgressDialog();
        JsonObject jsonObject = new JsonObject();
        if (family.isRegularGroup()) {
            jsonObject.addProperty("type", "regular");
        } else
            jsonObject.addProperty("type", family.getGroupCategory());
        ApiCallbackParams apiCallbackParams = new ApiCallbackParams();
        apiCallbackParams.setFamilyMember(familyMember);
        apiCallbackParams.setDeveloperCode(TYPE);
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getContext());
        apiServiceProvider.getallRelations(jsonObject, apiCallbackParams, this);
    }

    @Override
    public void onMemberSelected(FamilyMember familyMember) {
        Intent profileIntent = new Intent(getActivity(), ProfileActivity.class);
        profileIntent.putExtra(Constants.Bundle.DATA, familyMember);
        profileIntent.putExtra(Constants.Bundle.FOR_EDITING, true);
        getActivity().startActivityForResult(profileIntent, ProfileActivity.REQUEST_CODE);
    }

    @Override
    public void addMemberRelationShip(FamilyMember familyMember) {
        fetchAllRelations(familyMember, ADD_RELATIONSHIP);
    }

    @Override
    public void blockMember(FamilyMember familyMember) {
        mListener.showProgressDialog();
        ApiCallbackParams apiCallbackParams = new ApiCallbackParams();
        apiCallbackParams.setFamilyMember(familyMember);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", familyMember.getId());
        jsonObject.addProperty("user_id", familyMember.getUserId());
        jsonObject.addProperty("group_id", familyMember.getGroupId());
        jsonObject.addProperty("is_blocked", "true");
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getContext());
        apiServiceProvider.updateUserRestrictionStatus(jsonObject, apiCallbackParams, this);
    }

    @Override
    public void unBlockMember(FamilyMember familyMember) {
        mListener.showProgressDialog();
        ApiCallbackParams apiCallbackParams = new ApiCallbackParams();
        apiCallbackParams.setFamilyMember(familyMember);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", familyMember.getId());
        jsonObject.addProperty("user_id", familyMember.getUserId());
        jsonObject.addProperty("group_id", familyMember.getGroupId());
        jsonObject.addProperty("is_blocked", "false");
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getContext());
        apiServiceProvider.updateUserRestrictionStatus(jsonObject, apiCallbackParams, this);
    }

    @Override
    public void removeMember(FamilyMember familyMember) {
        mListener.showProgressDialog();
        ApiCallbackParams apiCallbackParams = new ApiCallbackParams();
        apiCallbackParams.setFamilyMember(familyMember);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", familyMember.getId());
        jsonObject.addProperty("user_id", familyMember.getUserId());
        jsonObject.addProperty("group_id", familyMember.getGroupId());
        jsonObject.addProperty("is_removed", "true");
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getContext());
        apiServiceProvider.updateUserRestrictionStatus(jsonObject, apiCallbackParams, this);
    }

    @Override
    public void showPaymentHistory(FamilyMember familyMember) {
        startActivity(new Intent(getActivity(), PaymentHistoryActivity.class)
                .putExtra(ID, familyMember.getUserId() + "")
                .putExtra("NAME", familyMember.getFullName())
                .putExtra(FAMILY_ID, family.getId() + ""));

    }

    @Override
    public void updateRelationShip(FamilyMember familyMember) {
        fetchAllRelations(familyMember, UPDATE_RELATIONSHIP);
    }

    @Override
    public void makeAdmin(FamilyMember familyMember) {
        mListener.showProgressDialog();
        ApiCallbackParams apiCallbackParams = new ApiCallbackParams();
        apiCallbackParams.setFamilyMember(familyMember);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", familyMember.getId());
        jsonObject.addProperty("user_id", familyMember.getUserId());
        jsonObject.addProperty("group_id", familyMember.getGroupId());

        if (familyMember.isAdmin()) {
            jsonObject.addProperty("type", "member");
        } else {
            jsonObject.addProperty("type", "admin");
        }
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getContext());
        apiServiceProvider.updateUserRestrictionStatus(jsonObject, apiCallbackParams, this);
    }

   /* @OnEditorAction(R.id.searchMembers)
    protected boolean onSearchQueryListener(int actionId) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            clearAndReload();
            try {
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchMembers.getWindowToken(), 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RelationShipSelectionActivity.REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            mListener.showProgressDialog();
            FamilyMember familyMember = data.getParcelableExtra(MEMBER);
            RelationShip relationShip = data.getParcelableExtra(RELATIONSHIP);
            int TYPE = data.getIntExtra(Constants.Bundle.TYPE, 0);
            ApiCallbackParams apiCallbackParams = new ApiCallbackParams();
            apiCallbackParams.setFamilyMember(familyMember);
            apiCallbackParams.setDeveloperCode(ADD_RELATIONSHIP);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("primary_user", SharedPref.getUserRegistration().getId());
            jsonObject.addProperty("secondary_user", familyMember.getUserId().toString());

            if (relationShip.getId() == 0) {
                jsonObject.addProperty("relation_id", "others");
                jsonObject.addProperty("value", relationShip.getRelationship());
            } else {
                jsonObject.addProperty("relation_id", String.valueOf(relationShip.getId()));

            }
            jsonObject.addProperty("group_id", String.valueOf(familyMember.getGroupId()));
            if (family.isRegularGroup())
                jsonObject.addProperty("type", "relation");
            else jsonObject.addProperty("type", "role");
            ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getContext());
            if (TYPE == ADD_RELATIONSHIP) {
                apiServiceProvider.addRelation(jsonObject, apiCallbackParams, this);
            } else {
                jsonObject.addProperty("id", familyMember.getRelationId());
                apiServiceProvider.updateRelation(jsonObject, apiCallbackParams, this);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //requestFamilyMembers();
    }

    public void updateFamily(Family family) {
        this.family = family;
        if (familyMembersAdapter != null)
            familyMembersAdapter.updateFamily(family);
    }
    /** 25/08/2021 **/
    public void startSearch(String query) {
        this.query = query;
        clearAndReload();
    }
}
