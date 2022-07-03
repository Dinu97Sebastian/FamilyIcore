package com.familheey.app.Fragments.Events;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.familheey.app.Adapters.ExploreAdapter;
import com.familheey.app.Interfaces.RetrofitListener;
import com.familheey.app.Models.ApiCallbackParams;
import com.familheey.app.Models.ErrorData;
import com.familheey.app.Models.Response.Event;
import com.familheey.app.Networking.Retrofit.ApiServiceProvider;
import com.familheey.app.Parsers.EventsParser;
import com.familheey.app.R;
import com.familheey.app.Utilities.Utilities;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class InvitationFragment extends Fragment {


    @BindView(R.id.recyclerInvitation)
    RecyclerView recyclerInvitation;
    @BindView(R.id.progressInvitation)
    ProgressBar progressInvitation;
    @BindView(R.id.no_data)
    TextView no_data;

    private ExploreAdapter invitedEventsAdapter;
    private final List<Event> invitationEvents = new ArrayList<>();
    private JsonObject jsonObject = Utilities.getDefaultEventSearchJson();

    public InvitationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_invitation, container, false);
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
        getInvitationResponse();

    }

    private void initializeAdapter() {
        invitedEventsAdapter = new ExploreAdapter(getContext(), invitationEvents);
        recyclerInvitation.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerInvitation.setLayoutManager(layoutManager);
        recyclerInvitation.setAdapter(invitedEventsAdapter);
    }

    public void showProgress() {
        if (progressInvitation != null) {
            progressInvitation.setVisibility(View.VISIBLE);
        }
    }


    public void hideProgress() {
        if (progressInvitation != null) {
            progressInvitation.setVisibility(View.GONE);
        }
    }

    private void getInvitationResponse() {
        showProgress();
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getContext());
        apiServiceProvider.eventInvitation(jsonObject, null, new RetrofitListener() {
            @Override
            public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {
                hideProgress();
                invitationEvents.clear();
                invitationEvents.addAll(EventsParser.parseInvitationEvents(responseBodyString));
                invitedEventsAdapter.notifyDataSetChanged();
                if (invitationEvents.size() == 0) {
                    no_data.setVisibility(View.VISIBLE);
                } else
                    no_data.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
                hideProgress();
            }
        });
    }

    public void updateEvent(JsonObject jsonObject) {
        this.jsonObject = jsonObject;
        getInvitationResponse();
    }
}
