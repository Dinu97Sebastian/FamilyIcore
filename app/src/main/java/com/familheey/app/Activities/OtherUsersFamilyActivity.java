package com.familheey.app.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.familheey.app.Adapters.OtherUserFamilyJoiningAdapter;
import com.familheey.app.EditHistoryActivity;
import com.familheey.app.FamilheeyApplication;
import com.familheey.app.Interfaces.ProgressListener;
import com.familheey.app.Interfaces.RetrofitListener;
import com.familheey.app.Models.ApiCallbackParams;
import com.familheey.app.Models.ErrorData;
import com.familheey.app.Models.Request.FamilyHistoryUpdateRequest;
import com.familheey.app.Models.Response.Family;
import com.familheey.app.Networking.Retrofit.ApiServiceProvider;
import com.familheey.app.Networking.Retrofit.ApiServices;
import com.familheey.app.Networking.Retrofit.RetrofitBase;
import com.familheey.app.PaginationListener;
import com.familheey.app.Parsers.FamilyParser;
import com.familheey.app.R;
import com.familheey.app.Utilities.Constants;
import com.familheey.app.Utilities.SharedPref;
import com.familheey.app.Utilities.Utilities;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class OtherUsersFamilyActivity extends AppCompatActivity implements RetrofitListener, ProgressListener {


    public static final int CONNECTIONS = 1;
    public static final int MUTUAL_CONNECTIONS = 2;
    @BindView(R.id.goBack)
    ImageView goBack;
    @BindView(R.id.toolBarTitle)
    TextView toolBarTitle;
    @BindView(R.id.familyList)
    RecyclerView familyList;

    @BindView(R.id.progressBarFamilies)
    ProgressBar progressBarFamilies;

    private SweetAlertDialog progressDialog;

    private List<Family> families = new ArrayList<>();
    private OtherUserFamilyJoiningAdapter familyMemberJoiningAdapter;
    private String otherUserId;
    private int type = CONNECTIONS;
    public static final int PAGE_START = 1;
    private int currentPage = PAGE_START;
    private boolean isLastPage = false;
    private int totalPage = 10;
    private boolean isLoading = false;
    int itemCount = 0;
    public CompositeDisposable subscriptions;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_users_family);
        ButterKnife.bind(this);
        otherUserId = getIntent().getStringExtra(Constants.Bundle.ID);
        type = getIntent().getIntExtra(Constants.Bundle.TYPE, CONNECTIONS);
        subscriptions = new CompositeDisposable();
        fetchUserFamilies();
        initializeToolbar();
        initializeAdapter();
    }

    private void fetchUserFamilies() {
        showProgress();
        JsonObject lisFamily = new JsonObject();
        String family = String.valueOf(families.size());
        lisFamily.addProperty("user_id", otherUserId);
        lisFamily.addProperty("member_to_add", SharedPref.getUserRegistration().getId());
        lisFamily.addProperty("offset",families.size() +"");
        lisFamily.addProperty("limit","10");

        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getApplicationContext());
        if (type == CONNECTIONS) {
            apiServiceProvider.getAllGroupsBasedOnUserId(lisFamily, null, this);
        } else {
            lisFamily.addProperty("user_id_two", otherUserId);
            lisFamily.addProperty("user_id_one", SharedPref.getUserRegistration().getId());
            apiServiceProvider.getMutualFamilies(lisFamily, null, this);
        }
    }

    private void showProgress() {
        if (progressBarFamilies!=null){
            progressBarFamilies.setVisibility(View.VISIBLE);
        }
    }

    private void initializeToolbar() {
        if (type == CONNECTIONS)
            toolBarTitle.setText("Families");
        else toolBarTitle.setText("Mutual Families");
        goBack.setOnClickListener(v -> finish());
    }

    private void initializeAdapter() {
        familyMemberJoiningAdapter = new OtherUserFamilyJoiningAdapter(getApplicationContext(), families, SharedPref.getUserRegistration().getId(), otherUserId, type);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        familyList.setLayoutManager(layoutManager);
        familyList.setAdapter(familyMemberJoiningAdapter);
        familyList.addOnScrollListener(new PaginationListener(layoutManager){

            @Override
            protected void loadMoreItems() {
                progressBarFamilies.setVisibility(View.VISIBLE);
                isLoading = true;
                currentPage++;
                fetchUserFamilies();
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
    public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {
        /*families.clear();
        families.addAll(FamilyParser.parseLinkedFamilies(responseBodyString));
        familyMemberJoiningAdapter.notifyDataSetChanged();
        hideProgress();*/
        progressBarFamilies.setVisibility(View.GONE);
        isLoading=true;
        List<Family> temp = FamilyParser.parseLinkedFamilies(responseBodyString);
        if (temp != null && temp.size() == 10) {
            progressBarFamilies.setVisibility(View.GONE);
            isLoading = false;
        } else {
            progressBarFamilies.setVisibility(View.GONE);
            isLastPage = true;
        }
        //families.clear();
        families.addAll(FamilyParser.parseLinkedFamilies(responseBodyString));
        familyMemberJoiningAdapter.notifyDataSetChanged();


    }

    private void hideProgress() {
        if (progressBarFamilies!=null){
            progressBarFamilies.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
        hideProgress();
        showErrorDialog("Please try again later !! Something went wrong");
    }

    @Override
    public void showProgressDialog() {
        progressDialog = Utilities.getProgressDialog(this);
        progressDialog.show();
    }

    @Override
    public void hideProgressDialog() {
        if (progressDialog != null)
            progressDialog.dismiss();
    }

    @Override
    public void showErrorDialog(String errorMessage) {
        if (progressDialog == null) {
            progressDialog = Utilities.getErrorDialog(this, errorMessage);
            progressDialog.show();
            return;
        }
        Utilities.getErrorDialog(progressDialog, errorMessage);
    }
}