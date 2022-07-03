package com.familheey.app.Fragments;

import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.Group;
import androidx.fragment.app.Fragment;

import com.familheey.app.Interfaces.ProgressListener;
import com.familheey.app.Interfaces.RetrofitListener;
import com.familheey.app.Models.ApiCallbackParams;
import com.familheey.app.Models.ErrorData;
import com.familheey.app.Models.Response.Family;
import com.familheey.app.Networking.Retrofit.ApiServiceProvider;
import com.familheey.app.Parsers.FamilyParser;
import com.familheey.app.R;
import com.familheey.app.Utilities.Constants;
import com.familheey.app.Utilities.SharedPref;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

import static com.familheey.app.Utilities.Constants.FamilySettings.LinkFamily.ADMINS;
import static com.familheey.app.Utilities.Constants.FamilySettings.LinkFamily.MEMBER;
import static com.familheey.app.Utilities.Constants.FamilySettings.MemberApproval.ANY_MEMBER;
import static com.familheey.app.Utilities.Constants.FamilySettings.MemberApproval.MEMBER_APPROVAL;
import static com.familheey.app.Utilities.Constants.FamilySettings.MemberJoining.ANYONE_CAN_JOIN;
import static com.familheey.app.Utilities.Constants.FamilySettings.MemberJoining.ANYONE_CAN_JOIN_WITH_APPROVAL;
import static com.familheey.app.Utilities.Constants.FamilySettings.MemberJoining.INVITATION_ONLY;
import static com.familheey.app.Utilities.Constants.FamilySettings.PostApproval.ADMIN;
import static com.familheey.app.Utilities.Constants.FamilySettings.PostApproval.MEMBERS;
import static com.familheey.app.Utilities.Constants.FamilySettings.PostApproval.NOT_NEEDED;
import static com.familheey.app.Utilities.Constants.FamilySettings.PostCreate.ANY_MEMBER_WITH_APPROVAL;
import static com.familheey.app.Utilities.Constants.FamilySettings.PostCreate.MEMBERS_ONLY;

public class FamilyCreationLevelAdvancedFragment extends Fragment implements RetrofitListener {


    @BindView(R.id.skip)
    TextView skip;
    @BindView(R.id.invitationOnly)
    RadioButton invitationOnly;
    @BindView(R.id.anyOneCanJoinWithApproval)
    RadioButton anyOneCanJoinWithApproval;
    @BindView(R.id.anyOneCanJoin)
    RadioButton anyOneCanJoin;
    @BindView(R.id.memberJoiningType)
    RadioGroup memberJoiningType;
    @BindView(R.id.adminCreatePost)
    RadioButton adminCreatePost;
    @BindView(R.id.membersCreatePost)
    RadioButton membersCreatePost;
    @BindView(R.id.anyOneWithApprovalCreatePost)
    RadioButton anyOneWithApprovalCreatePost;

    @BindView(R.id.postCreationAccess)
    RadioGroup postCreationAccess;
    @BindView(R.id.noApprovalNeeded)
    RadioButton noApprovalNeeded;
    @BindView(R.id.adminApprovalNeeded)
    RadioButton adminApprovalNeeded;
    @BindView(R.id.memberApprovalNeeded)
    RadioButton memberApprovalNeeded;

/** defined on 16/09/21 for rating settings **/
    @BindView(R.id.ratingVisibilityAccess)
    RadioGroup ratingVisibilityAccess;
    @BindView(R.id.enableRatingForAllPosts)
    RadioButton enableRatingForAllPosts;
    @BindView(R.id.disableRatingForAllPosts)
    RadioButton disableRatingForAllPosts;

    @BindView(R.id.requestcreateAccess)
    RadioGroup requestcreateAccess;
    @BindView(R.id.adminCreateRequestRequest)
    RadioButton adminCreateRequestRequest;
    @BindView(R.id.anyMemberCreateRequestRequest)
    RadioButton anyMemberCreateRequestRequest;

    @BindView(R.id.postApprovalAccess)
    RadioGroup postApprovalAccess;
    @BindView(R.id.adminsCanLink)
    RadioButton adminsCanLink;
    @BindView(R.id.membersCanLink)
    RadioButton membersCanLink;
    @BindView(R.id.whoCanLinkFamily)
    RadioGroup whoCanLinkFamily;
    @BindView(R.id.noApprovalNeededToLink)
    RadioButton noApprovalNeededToLink;
    @BindView(R.id.adminsCanApproveFamilyLinking)
    RadioButton adminsCanApproveFamilyLinking;
    @BindView(R.id.whoCanApproveFamilyLink)
    RadioGroup whoCanApproveFamilyLink;
    @BindView(R.id.documents)
    SwitchCompat documents;
    @BindView(R.id.history)
    SwitchCompat history;
    @BindView(R.id.linkedFamily)
    SwitchCompat linkedFamily;
    @BindView(R.id.programs)
    SwitchCompat programs;
    @BindView(R.id.currentVacancies)
    SwitchCompat currentVacancies;
    @BindView(R.id.back)
    MaterialButton back;
    @BindView(R.id.proceed)
    MaterialButton proceed;
    @BindView(R.id.whoCanApproveMemberJoiningContainer)
    LinearLayout whoCanApproveMemberJoiningContainer;
    @BindView(R.id.adminsCanApproveMemberJoining)
    RadioButton adminsCanApproveMemberJoining;
    @BindView(R.id.membersCanApproveMemberJoining)
    RadioButton membersCanApproveMemberJoining;
    @BindView(R.id.whoCanApproveMemberJoining)
    RadioGroup whoCanApproveMemberJoining;
    @BindView(R.id.anyOneCanViewPost)
    RadioButton anyOneCanViewPost;
    @BindView(R.id.membersCanViewPost)
    RadioButton membersCanViewPost;
    @BindView(R.id.postVisibilityAccess)
    RadioGroup postVisibilityAccess;
    @BindView(R.id.linkFamilyContainer)
    MaterialCardView linkFamilyContainer;
    @BindView(R.id.linkFamilyApprovalContainer)
    MaterialCardView linkFamilyApprovalContainer;
    @BindView(R.id.adminCreateAnnouncement)
    RadioButton adminCreateAnnouncement;
    @BindView(R.id.anyMemberCreateAnnouncement)
    RadioButton anyMemberCreateAnnouncement;
    @BindView(R.id.anyMemberWithApprovalCreateAnnouncement)
    RadioButton anyMemberWithApprovalCreateAnnouncement;
    @BindView(R.id.announcementCreationAccess)
    RadioGroup announcementCreationAccess;
    @BindView(R.id.anyOneCanViewAnnouncement)
    RadioButton anyOneCanViewAnnouncement;
    @BindView(R.id.membersCanViewAnnouncement)
    RadioButton membersCanViewAnnouncement;
    @BindView(R.id.announcementVisibilityAccess)
    RadioGroup announcementVisibilityAccess;
    @BindView(R.id.noApprovalNeededForAnnouncement)
    RadioButton noApprovalNeededForAnnouncement;
    @BindView(R.id.adminApprovalForAnnouncementNeeded)
    RadioButton adminApprovalForAnnouncementNeeded;
    @BindView(R.id.memberApprovalNeededForAnnouncement)
    RadioButton memberApprovalNeededForAnnouncement;
    @BindView(R.id.announcementApprovalAccess)
    RadioGroup announcementApprovalAccess;

    @BindView(R.id.goBack)
    ImageView goBack;
    @BindView(R.id.toolBarTitle)
    TextView toolBarTitle;
    @BindView(R.id.familyOtherSettingsGroup)
    Group familyOtherSettingsGroup;
    @BindView(R.id.toolbarIncluder)
    View toolbarIncluder;


    private Family family;
    private boolean forEditing;
    private ProgressListener progressListener;
    private OnFamilyCreationListener familyCreationListener;

    public FamilyCreationLevelAdvancedFragment() {
        // Required empty public constructor
    }

    public static FamilyCreationLevelAdvancedFragment newInstance(Family family, boolean forEditing) {
        FamilyCreationLevelAdvancedFragment fragment = new FamilyCreationLevelAdvancedFragment();
        Bundle args = new Bundle();
        args.putParcelable(Constants.Bundle.DATA, family);
        args.putBoolean(Constants.Bundle.FOR_EDITING, forEditing);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            family = getArguments().getParcelable(Constants.Bundle.DATA);
            forEditing = getArguments().getBoolean(Constants.Bundle.FOR_EDITING, false);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_family_creation_level_advanced, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        skip.setPaintFlags(skip.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        addRestrictions();
        initializeToolbar();
        if (forEditing) {
            familyOtherSettingsGroup.setVisibility(View.GONE);
            toolbarIncluder.setVisibility(View.VISIBLE);
            back.setVisibility(View.INVISIBLE);
        } else {
            toolbarIncluder.setVisibility(View.GONE);
            back.setVisibility(View.VISIBLE);
            familyOtherSettingsGroup.setVisibility(View.VISIBLE);
        }
        prefillEditValues();
    }

    private void initializeToolbar() {
        toolBarTitle.setText("Advanced Settings");
        goBack.setOnClickListener(v -> {
            requireActivity().onBackPressed();
        });
    }

    private void addRestrictions() {
        if (family.getIsLinkable() != null && family.getIsLinkable()) {
            linkFamilyApprovalContainer.setVisibility(View.VISIBLE);
            linkFamilyContainer.setVisibility(View.VISIBLE);
        } else {
            linkFamilyApprovalContainer.setVisibility(View.GONE);
            linkFamilyContainer.setVisibility(View.GONE);
        }
    }

    private void prefillEditValues() {
        switch (family.getMemberJoining()) {
            case INVITATION_ONLY:
                invitationOnly.setChecked(true);
                break;
            case ANYONE_CAN_JOIN_WITH_APPROVAL:
                anyOneCanJoinWithApproval.setChecked(true);
                break;
            case ANYONE_CAN_JOIN:
                anyOneCanJoin.setChecked(true);
                break;
        }
        if (family.getMemberApproval() != null)
            switch (family.getMemberApproval().toString()) {
                case MEMBER_APPROVAL:
                    adminsCanApproveMemberJoining.setChecked(true);
                    break;
                case ANY_MEMBER:
                    membersCanApproveMemberJoining.setChecked(true);
                    break;
            }
        switch (family.getPostCreate()) {
            case Constants.FamilySettings.PostCreate.ADMIN:
                adminCreatePost.setChecked(true);
                break;
            case MEMBERS_ONLY:
                membersCreatePost.setChecked(true);
                break;
            case ANY_MEMBER_WITH_APPROVAL:
                anyOneWithApprovalCreatePost.setChecked(true);
                break;

        }
      if(family.getIsRated()==null || family.getIsRated()==false){
          disableRatingForAllPosts.setChecked(true);
      }else {
          enableRatingForAllPosts.setChecked(true);
      }

        if (family.getMemberApproval() != null)
            switch (family.getRequest_visibility()) {
                case Constants.FamilySettings.ViewRequest.MEMBERS_ONLY:
                    anyMemberCreateRequestRequest.setChecked(true);
                    break;
                case Constants.FamilySettings.ViewRequest.ADMIN:
                    adminCreateRequestRequest.setChecked(true);
                    break;
            }

        switch (family.getPostApproval()) {
            case NOT_NEEDED:
                noApprovalNeeded.setChecked(true);
                break;
            case ADMIN:
                adminApprovalNeeded.setChecked(true);
                break;
            case MEMBERS:
                memberApprovalNeeded.setChecked(true);
                break;
        }

        switch (family.getAnnouncementCreate()) {
            case Constants.FamilySettings.AnnouncementCreate.ADMIN:
                adminCreateAnnouncement.setChecked(true);
                break;
            case Constants.FamilySettings.AnnouncementCreate.ANY_MEMBER:
                anyMemberCreateAnnouncement.setChecked(true);
                break;
            case Constants.FamilySettings.AnnouncementCreate.ANY_MEMBER_WITH_APPROVAL:
                anyMemberWithApprovalCreateAnnouncement.setChecked(true);
                break;
        }

        if (family.isPublic()) {
            anyOneCanViewPost.setVisibility(View.VISIBLE);
            membersCanViewPost.setVisibility(View.GONE);
            anyOneCanViewPost.setChecked(true);
            membersCanViewPost.setChecked(false);

            anyOneCanViewAnnouncement.setVisibility(View.VISIBLE);
            membersCanViewAnnouncement.setVisibility(View.GONE);
            anyOneCanViewAnnouncement.setChecked(true);
            membersCanViewAnnouncement.setChecked(false);
        } else {
            anyOneCanViewPost.setVisibility(View.GONE);
            membersCanViewPost.setVisibility(View.VISIBLE);
            anyOneCanViewPost.setChecked(false);
            membersCanViewPost.setChecked(true);

            anyOneCanViewAnnouncement.setVisibility(View.GONE);
            membersCanViewAnnouncement.setVisibility(View.VISIBLE);
            anyOneCanViewAnnouncement.setChecked(false);
            membersCanViewAnnouncement.setChecked(true);
        }
        /*switch (family.getAnnouncementVisibility()) {
            case Constants.FamilySettings.AnnouncementVisibility.PUBLIC:
                anyOneCanViewAnnouncement.setChecked(true);
                break;
            case Constants.FamilySettings.AnnouncementVisibility.MEMBERS_ONLY:
                membersCanViewAnnouncement.setChecked(true);
                break;
        }*/
        //membersCanViewAnnouncement.setChecked(true);

        /*switch (family.getAnnouncementApproval()) {
            case Constants.FamilySettings.AnnouncementApproval.NOT_NEEDED:
                noApprovalNeededForAnnouncement.setChecked(true);
                break;
            case Constants.FamilySettings.AnnouncementApproval.ADMIN:
                adminApprovalForAnnouncementNeeded.setChecked(true);
                break;
            case Constants.FamilySettings.AnnouncementApproval.MEMBERS:
                memberApprovalNeededForAnnouncement.setChecked(true);
                break;
        }*/

        switch (family.getLinkFamily()) {
            case ADMINS:
                adminsCanLink.setChecked(true);
                break;
            case MEMBER:
                membersCanLink.setChecked(true);
                break;
        }
        switch (family.getLinkApproval()) {
            case Constants.FamilySettings.LinkApproval.NOT_NEEDED:
                noApprovalNeededToLink.setChecked(true);
                break;
            case Constants.FamilySettings.LinkApproval.ADMIN:
                adminsCanApproveFamilyLinking.setChecked(true);
                break;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ProgressListener) {
            progressListener = (ProgressListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement ProgressListener");
        }
        if (context instanceof OnFamilyCreationListener) {
            familyCreationListener = (OnFamilyCreationListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFamilyCreationListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        progressListener = null;
        familyCreationListener = null;
    }

    @OnClick({R.id.skip, R.id.back, R.id.proceed})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.skip:
                if (forEditing) {
                    familyCreationListener.onFamilyCreated(family);
                } else {
                    updateFamilyStatus();
                }
                break;
            case R.id.back:
                getActivity().onBackPressed();
                break;
            case R.id.proceed:
                updateFamilyAdvancedSettings();
                break;
        }
    }

    private boolean isValid() {
        boolean isValid = true;
        if (memberJoiningType.getCheckedRadioButtonId() == -1) {
            isValid = false;
        }
        if (postCreationAccess.getCheckedRadioButtonId() == -1) {
            isValid = false;
        }
        if (postApprovalAccess.getCheckedRadioButtonId() == -1) {
            isValid = false;
        }
        if (whoCanLinkFamily.getCheckedRadioButtonId() == -1) {
            isValid = false;
        }
        if (whoCanApproveFamilyLink.getCheckedRadioButtonId() == -1) {
            isValid = false;
        }
        if (announcementCreationAccess.getCheckedRadioButtonId() == -1) {
            isValid = false;
        }
        if (announcementVisibilityAccess.getCheckedRadioButtonId() == -1) {
            isValid = false;
        }
        if (announcementApprovalAccess.getCheckedRadioButtonId() == -1) {
            isValid = false;
        }
        /** for rating settings **/
        if(ratingVisibilityAccess.getCheckedRadioButtonId() == -1){
            isValid = false;
        }
        return isValid;
    }

    private void updateFamilyAdvancedSettings() {
        progressListener.showProgressDialog();
        JsonObject advancedJsonObject = new JsonObject();
        switch (memberJoiningType.getCheckedRadioButtonId()) {
            case R.id.invitationOnly:
                advancedJsonObject.addProperty("member_joining", "1");
                break;
            case R.id.anyOneCanJoinWithApproval:
                advancedJsonObject.addProperty("member_joining", "2");
                break;
            case R.id.anyOneCanJoin:
                advancedJsonObject.addProperty("member_joining", "3");
                break;
        }
        if (whoCanApproveMemberJoiningContainer.getVisibility() == View.VISIBLE) {
            switch (whoCanApproveMemberJoining.getCheckedRadioButtonId()) {
                case R.id.adminsCanApproveMemberJoining:
                    advancedJsonObject.addProperty("member_approval", "4");
                    break;
                case R.id.membersCanApproveMemberJoining:
                    advancedJsonObject.addProperty("member_approval", "5");
                    break;
            }
        }
        switch (postCreationAccess.getCheckedRadioButtonId()) {
            case R.id.adminCreatePost:
                advancedJsonObject.addProperty("post_create", Constants.FamilySettings.PostCreate.ADMIN);
                break;
            case R.id.membersCreatePost:
                advancedJsonObject.addProperty("post_create", MEMBERS_ONLY);
                break;
            case R.id.anyOneWithApprovalCreatePost:
                advancedJsonObject.addProperty("post_create", ANY_MEMBER_WITH_APPROVAL);
                break;
        }
        /**for rating settings**/
        switch (ratingVisibilityAccess.getCheckedRadioButtonId()){
            case R.id.enableRatingForAllPosts:
                advancedJsonObject.addProperty("rate_all_post",true);
                break;
            case R.id.disableRatingForAllPosts:
                advancedJsonObject.addProperty("rate_all_post",false);
                break;
        }
        switch (postVisibilityAccess.getCheckedRadioButtonId()) {
            case R.id.anyOneCanViewPost:
                advancedJsonObject.addProperty("post_visibilty", "9");
                break;
            case R.id.membersCanViewPost:
                advancedJsonObject.addProperty("post_visibilty", "8");
                break;
        }
        switch (postApprovalAccess.getCheckedRadioButtonId()) {
            case R.id.noApprovalNeeded:
                advancedJsonObject.addProperty("post_approval", "10");
                break;
            case R.id.adminApprovalNeeded:
                advancedJsonObject.addProperty("post_approval", "11");
                break;
            case R.id.memberApprovalNeeded:
                advancedJsonObject.addProperty("post_approval", "12");
                break;
        }


        switch (requestcreateAccess.getCheckedRadioButtonId()) {
            case R.id.adminCreateRequestRequest:
                advancedJsonObject.addProperty("request_visibility", "26");
                break;
            case R.id.anyMemberCreateRequestRequest:
                advancedJsonObject.addProperty("request_visibility", "27");
                break;
        }

        if (linkFamilyContainer.getVisibility() == View.VISIBLE) {
            switch (whoCanLinkFamily.getCheckedRadioButtonId()) {
                case R.id.adminsCanLink:
                    advancedJsonObject.addProperty("link_family", "13");
                    break;
                case R.id.membersCanLink:
                    advancedJsonObject.addProperty("link_family", "14");
                    break;
            }
        }
        if (linkFamilyApprovalContainer.getVisibility() == View.VISIBLE) {
            switch (whoCanApproveFamilyLink.getCheckedRadioButtonId()) {
                case R.id.noApprovalNeededToLink:
                    advancedJsonObject.addProperty("link_approval", "15");
                    break;
                case R.id.adminsCanApproveFamilyLinking:
                    advancedJsonObject.addProperty("link_approval", "16");
                    break;
            }
        }

        switch (announcementCreationAccess.getCheckedRadioButtonId()) {
            case R.id.adminCreateAnnouncement:
                advancedJsonObject.addProperty("announcement_create", Constants.FamilySettings.AnnouncementCreate.ADMIN);
                break;
            case R.id.anyMemberCreateAnnouncement:
                advancedJsonObject.addProperty("announcement_create", Constants.FamilySettings.AnnouncementCreate.ANY_MEMBER);
                break;
            case R.id.anyMemberWithApprovalCreateAnnouncement:
                advancedJsonObject.addProperty("announcement_create", Constants.FamilySettings.AnnouncementCreate.ANY_MEMBER_WITH_APPROVAL);
                break;
        }

        switch (announcementVisibilityAccess.getCheckedRadioButtonId()) {
            case R.id.anyOneCanViewAnnouncement:
                advancedJsonObject.addProperty("announcement_visibility", Constants.FamilySettings.AnnouncementVisibility.PUBLIC);
                break;
            case R.id.membersCanViewAnnouncement:
                advancedJsonObject.addProperty("announcement_visibility", Constants.FamilySettings.AnnouncementVisibility.MEMBERS_ONLY);
                break;
        }

        switch (announcementApprovalAccess.getCheckedRadioButtonId()) {
            case R.id.noApprovalNeededForAnnouncement:
                advancedJsonObject.addProperty("announcement_approval", Constants.FamilySettings.AnnouncementApproval.NOT_NEEDED);
                break;
            case R.id.adminApprovalForAnnouncementNeeded:
                advancedJsonObject.addProperty("announcement_approval", Constants.FamilySettings.AnnouncementApproval.ADMIN);
                break;
            case R.id.memberApprovalNeededForAnnouncement:
                advancedJsonObject.addProperty("announcement_approval", Constants.FamilySettings.AnnouncementApproval.MEMBERS);
                break;
        }

        advancedJsonObject.addProperty("id", family.getId().toString());
        advancedJsonObject.addProperty("is_active", true);
        /*advancedJsonObject.addProperty("", documents.isChecked());
        advancedJsonObject.addProperty("", history.isChecked());
        advancedJsonObject.addProperty("", linkedFamily.isChecked());
        advancedJsonObject.addProperty("", programs.isChecked());
        advancedJsonObject.addProperty("", currentVacancies.isChecked());*/
        advancedJsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getContext());
        apiServiceProvider.updateFamily(advancedJsonObject, null, this);
    }

    private void updateFamilyStatus() {
        progressListener.showProgressDialog();
        JsonObject advancedJsonObject = new JsonObject();
        advancedJsonObject.addProperty("id", family.getId().toString());
        advancedJsonObject.addProperty("is_active", true);
        advancedJsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getContext());
        apiServiceProvider.updateFamily(advancedJsonObject, null, this);
    }

    @Override
    public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {
        progressListener.hideProgressDialog();
        try {
            family = FamilyParser.parseFamily(responseBodyString);
            familyCreationListener.onFamilyCreated(family);
        } catch (NullPointerException | JsonParseException e) {
            e.printStackTrace();
            progressListener.showErrorDialog(Constants.SOMETHING_WRONG);
        }
    }

    @Override
    public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
        progressListener.showErrorDialog(Constants.SOMETHING_WRONG);
    }

    @OnCheckedChanged({R.id.invitationOnly, R.id.anyOneCanJoinWithApproval, R.id.anyOneCanJoin})
    void OnRadioButtonCheckedChanges(CompoundButton radioButton, boolean isChecked) {
        if (!isChecked)
            return;
        switch (radioButton.getId()) {
            case R.id.anyOneCanJoinWithApproval:
                whoCanApproveMemberJoiningContainer.setVisibility(View.VISIBLE);
                break;
            case R.id.invitationOnly:
            case R.id.anyOneCanJoin:
                whoCanApproveMemberJoiningContainer.setVisibility(View.GONE);
                break;
        }
    }

    public interface OnFamilyCreationListener {
        void onFamilyCreated(Family family);
    }
}
