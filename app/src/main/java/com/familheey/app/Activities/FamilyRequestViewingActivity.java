package com.familheey.app.Activities;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.familheey.app.Adapters.FamilyInvitationAdapter;
import com.familheey.app.Interfaces.ProgressListener;
import com.familheey.app.Interfaces.RetrofitListener;
import com.familheey.app.Models.ApiCallbackParams;
import com.familheey.app.Models.ErrorData;
import com.familheey.app.Models.FamilyJoiningRequest;
import com.familheey.app.Networking.Retrofit.ApiServiceProvider;
import com.familheey.app.Parsers.FamilyParser;
import com.familheey.app.R;
import com.familheey.app.Utilities.Constants;
import com.familheey.app.Utilities.SharedPref;
import com.familheey.app.Utilities.Utilities;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class FamilyRequestViewingActivity extends AppCompatActivity implements RetrofitListener, ProgressListener, FamilyInvitationAdapter.OnInvitationSelectedListener {


    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.invitationList)
    RecyclerView invitationList;

    private SweetAlertDialog progressDialog;
    private FamilyInvitationAdapter familyInvitationAdapter;
    private final List<FamilyJoiningRequest> familyJoiningRequests = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_request_viewing);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Invitations");
        initAdapter();
        fetchFamilyInvitations();
    }

    private void fetchFamilyInvitations() {
        JsonObject requestJson = new JsonObject();
        requestJson.addProperty("user_id", SharedPref.getUserRegistration().getId());
        showProgressDialog();
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(this);
        apiServiceProvider.fetchUserInvitations(requestJson, null, this);
    }

    private void initAdapter() {
        familyInvitationAdapter = new FamilyInvitationAdapter(this, familyJoiningRequests);
        invitationList.setAdapter(familyInvitationAdapter);
        invitationList.setLayoutManager(new LinearLayoutManager(this));
        invitationList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {
        hideProgressDialog();
        switch (apiFlag) {
            case Constants.ApiFlags.FETCH_USER_INVITATION:
                familyJoiningRequests.clear();
                familyJoiningRequests.addAll(FamilyParser.parseinvitations(responseBodyString));
                familyInvitationAdapter.notifyDataSetChanged();
                break;
            case Constants.ApiFlags.RESPOND_FAMILY_INVITATION:
                fetchFamilyInvitations();
                break;
        }
    }

    @Override
    public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
        showErrorDialog(errorData.getMessage());
        switch (apiFlag) {
            case Constants.ApiFlags.FETCH_USER_INVITATION:
                familyJoiningRequests.clear();
                familyInvitationAdapter.notifyDataSetChanged();
                break;
            case Constants.ApiFlags.RESPOND_FAMILY_INVITATION:
                break;
        }
    }

    @Override
    public void hideProgressDialog() {
        if (progressDialog != null)
            progressDialog.dismiss();
    }

    @Override
    public void showProgressDialog() {
        progressDialog = Utilities.getProgressDialog(this);
        progressDialog.show();
    }

    @Override
    public void showErrorDialog(String errorMessage) {
        Utilities.getErrorDialog(progressDialog, errorMessage);
    }

    @Override
    public void onInvitationAccepted(FamilyJoiningRequest familyJoiningRequest) {
        showProgressDialog();
        ApiCallbackParams apiCallbackParams = new ApiCallbackParams();
        apiCallbackParams.setFamilyJoiningRequest(familyJoiningRequest);
        apiCallbackParams.setStatus(true);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", familyJoiningRequest.getReqId());
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        jsonObject.addProperty("group_id", familyJoiningRequest.getGroupId());
        jsonObject.addProperty("status", "accepted");
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getApplicationContext());
        apiServiceProvider.respondToFamilyInvitation(jsonObject, null, this);
    }

    @Override
    public void onInvitationRejected(FamilyJoiningRequest familyJoiningRequest) {
        showProgressDialog();
        JsonObject jsonObject = new JsonObject();
        ApiCallbackParams apiCallbackParams = new ApiCallbackParams();
        apiCallbackParams.setFamilyJoiningRequest(familyJoiningRequest);
        apiCallbackParams.setStatus(true);
        jsonObject.addProperty("id", familyJoiningRequest.getReqId());
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        jsonObject.addProperty("group_id", familyJoiningRequest.getGroupId());
        jsonObject.addProperty("status", "rejected");
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getApplicationContext());
        apiServiceProvider.respondToFamilyInvitation(jsonObject, null, this);
    }
}
