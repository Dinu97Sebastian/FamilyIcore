package com.familheey.app.Fragments.FamilyDashboard;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.familheey.app.Adapters.UserPendingRequestAdapter;
import com.familheey.app.Decorators.BottomAdditionalMarginDecorator;
import com.familheey.app.Interfaces.FamilyDashboardListener;
import com.familheey.app.Interfaces.RetrofitListener;
import com.familheey.app.Models.ApiCallbackParams;
import com.familheey.app.Models.ErrorData;
import com.familheey.app.Models.Response.MemberRequests;
import com.familheey.app.Networking.Retrofit.ApiServiceProvider;
import com.familheey.app.Parsers.FamilyParser;
import com.familheey.app.R;
import com.familheey.app.Utilities.SharedPref;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.familheey.app.Utilities.Constants.Bundle.DATA;

public class PendingRequestFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.pendingFamiliesList)
    RecyclerView pendingFamiliesList;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.emptyIndicatorContainer)
    View emptyIndicatorContainer;
    private String familyId = "";
    private UserPendingRequestAdapter userPendingRequestAdapter;

    private ArrayList<MemberRequests> pendingRequests = new ArrayList<>();
    private FamilyDashboardListener mListener;

    public PendingRequestFragment() {
        // Required empty public constructor
    }

    public static PendingRequestFragment newInstance(String familyId) {
        PendingRequestFragment fragment = new PendingRequestFragment();
        Bundle args = new Bundle();
        args.putString(DATA, familyId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            familyId = getArguments().getString(DATA);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pending_request, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeAdapter();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadPendingRequests();

    }

    private void loadPendingRequests() {
        progressBar.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setRefreshing(false);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("group_id", familyId);
        jsonObject.addProperty("admin_id", SharedPref.getUserRegistration().getId());
        jsonObject.addProperty("type", "invite");
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getContext());
        apiServiceProvider.getPendingRequests(jsonObject, null, new RetrofitListener() {
            @Override
            public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {
                progressBar.setVisibility(View.INVISIBLE);
                pendingRequests.clear();
                pendingRequests.addAll(FamilyParser.parseFamilyUserPendingRequests(responseBodyString));
                if (pendingRequests.size() == 0) {
                    emptyIndicatorContainer.setVisibility(View.VISIBLE);
                } else {
                    emptyIndicatorContainer.setVisibility(View.INVISIBLE);
                }
                userPendingRequestAdapter.notifyDataSetChanged();
            }

            @Override
            public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void initializeAdapter() {
        userPendingRequestAdapter = new UserPendingRequestAdapter(mListener, pendingRequests);
        pendingFamiliesList.setAdapter(userPendingRequestAdapter);
        pendingFamiliesList.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayout.VERTICAL));
        pendingFamiliesList.addItemDecoration(new BottomAdditionalMarginDecorator());
        pendingFamiliesList.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onRefresh() {
        loadPendingRequests();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FamilyDashboardListener) {
            mListener = (FamilyDashboardListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement FamilyDashboardListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
