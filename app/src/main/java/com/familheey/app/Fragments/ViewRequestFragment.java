package com.familheey.app.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.familheey.app.Adapters.FamilyInvitationAdapter;
import com.familheey.app.Interfaces.ProgressListener;
import com.familheey.app.Interfaces.RetrofitListener;
import com.familheey.app.Models.ApiCallbackParams;
import com.familheey.app.Models.ErrorData;
import com.familheey.app.Models.FamilyJoiningRequest;
import com.familheey.app.Models.Response.FamilyMember;
import com.familheey.app.Models.Response.ProfileResponse.UserProfile;
import com.familheey.app.Networking.Retrofit.ApiServiceProvider;
import com.familheey.app.Parsers.FamilyParser;
import com.familheey.app.R;
import com.familheey.app.Utilities.Constants;
import com.familheey.app.Utilities.SharedPref;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.familheey.app.Utilities.Constants.Bundle.DATA;

public class ViewRequestFragment extends Fragment implements RetrofitListener, FamilyInvitationAdapter.OnInvitationSelectedListener {

    @BindView(R.id.invitationList)
    RecyclerView invitationList;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.emptyIndicator)
    View emptyIndicator;
    private ProgressListener mListener;
    private FamilyInvitationAdapter familyInvitationAdapter;
    private final List<FamilyJoiningRequest> familyJoiningRequests = new ArrayList<>();
    public ViewRequestFragment() {
        // Required empty public constructor
    }

    public static ViewRequestFragment newInstance(FamilyMember familyMember) {
        ViewRequestFragment fragment = new ViewRequestFragment();
        Bundle args = new Bundle();
        args.putParcelable(DATA, familyMember);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            getArguments().clear();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_request, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initAdapter();
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchFamilyInvitations();

    }

    private void fetchFamilyInvitations() {
        JsonObject requestJson = new JsonObject();
        requestJson.addProperty("user_id", SharedPref.getUserRegistration().getId());
        progressBar.setVisibility(View.VISIBLE);
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getContext());
        apiServiceProvider.fetchUserInvitations(requestJson, null, this);
    }

    private void initAdapter() {
        familyInvitationAdapter = new FamilyInvitationAdapter(this, familyJoiningRequests);
        invitationList.setAdapter(familyInvitationAdapter);
        invitationList.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {
        if (progressBar != null)
            progressBar.setVisibility(View.INVISIBLE);
        switch (apiFlag) {
            case Constants.ApiFlags.FETCH_USER_INVITATION:
                familyJoiningRequests.clear();
                familyJoiningRequests.addAll(FamilyParser.parseinvitations(responseBodyString));
                familyInvitationAdapter.notifyDataSetChanged();
                toggleEmptyVisibility();
                break;
            case Constants.ApiFlags.RESPOND_FAMILY_INVITATION:
                familyJoiningRequests.remove(apiCallbackParams.getFamilyJoiningRequest());
                familyInvitationAdapter.notifyDataSetChanged();
                mListener.hideProgressDialog();
                toggleEmptyVisibility();
                break;
        }
    }

    @Override
    public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
        if (progressBar != null)
            progressBar.setVisibility(View.INVISIBLE);
        switch (apiFlag) {
            case Constants.ApiFlags.FETCH_USER_INVITATION:
                familyJoiningRequests.clear();
                familyInvitationAdapter.notifyDataSetChanged();
                toggleEmptyVisibility();
                break;
            case Constants.ApiFlags.RESPOND_FAMILY_INVITATION:
                mListener.hideProgressDialog();
                toggleEmptyVisibility();
                break;
        }
    }

    @Override
    public void onInvitationAccepted(FamilyJoiningRequest familyJoiningRequest) {
        mListener.showProgressDialog();
        ApiCallbackParams apiCallbackParams = new ApiCallbackParams();
        apiCallbackParams.setFamilyJoiningRequest(familyJoiningRequest);
        apiCallbackParams.setStatus(true);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", familyJoiningRequest.getReqId().toString());
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        jsonObject.addProperty("group_id", familyJoiningRequest.getGroupId().toString());
        jsonObject.addProperty("from_id", familyJoiningRequest.getFromId());
        jsonObject.addProperty("status", "accepted");
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getContext());
        apiServiceProvider.respondToFamilyInvitation(jsonObject, apiCallbackParams, this);
    }

    @Override
    public void onInvitationRejected(FamilyJoiningRequest familyJoiningRequest) {
        mListener.showProgressDialog();
        JsonObject jsonObject = new JsonObject();
        ApiCallbackParams apiCallbackParams = new ApiCallbackParams();
        apiCallbackParams.setFamilyJoiningRequest(familyJoiningRequest);
        apiCallbackParams.setStatus(true);
        jsonObject.addProperty("id", familyJoiningRequest.getReqId().toString());
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        jsonObject.addProperty("group_id", familyJoiningRequest.getGroupId().toString());
        jsonObject.addProperty("from_id", familyJoiningRequest.getFromId());
        jsonObject.addProperty("status", "rejected");
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getContext());
        apiServiceProvider.respondToFamilyInvitation(jsonObject, apiCallbackParams, this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ProgressListener) {
            mListener = (ProgressListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ProgressListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void fillDetails(UserProfile userProfile) {

    }

    void toggleEmptyVisibility() {
        if (familyJoiningRequests.size() > 0) {
            invitationList.setVisibility(View.VISIBLE);
        } else {
            emptyIndicator.setVisibility(View.VISIBLE);
        }
    }
}
