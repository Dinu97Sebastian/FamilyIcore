package com.familheey.app.Fragments.Events;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.familheey.app.Activities.CreateEventActivity;
import com.familheey.app.Adapters.MyEventsParentAdapter;
import com.familheey.app.Interfaces.RetrofitListener;
import com.familheey.app.Models.ApiCallbackParams;
import com.familheey.app.Models.ErrorData;
import com.familheey.app.Models.MyCompleteEvent;
import com.familheey.app.Models.Response.CreatedByMeResponse;
import com.familheey.app.Networking.Retrofit.ApiServiceProvider;
import com.familheey.app.R;
import com.familheey.app.Utilities.SharedPref;
import com.familheey.app.Utilities.Utilities;
import com.familheey.app.pagination.PaginationScrollListener;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

public class MyEventsFragment extends Fragment implements RetrofitListener {


    RecyclerView recyclerListEvents;
    MyEventsParentAdapter myEventsParentAdapter;
    ConstraintLayout constraintEmpty;
    ProgressBar progressMyEvents;
    MaterialButton create_event;
    List<MyCompleteEvent> events = new ArrayList<>();
    String resposeString="";
    int pastEventCount=50;
    int totalPastMyEventCount=0;

    View view;
    private JsonObject jsonObject = Utilities.getDefaultEventSearchJson();

    public MyEventsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_my_events, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerListEvents = view.findViewById(R.id.recyclerListEvents);
        constraintEmpty = view.findViewById(R.id.constraintEmpty);
        progressMyEvents = view.findViewById(R.id.progressMyEvents);
        create_event = view.findViewById(R.id.create_event);
        initListener();
    }

    private void initListener() {
        create_event.setOnClickListener(v -> startActivity(new Intent(getActivity(), CreateEventActivity.class)));
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    private void showProgressBar() {
        if (progressMyEvents != null) {
            progressMyEvents.setVisibility(View.VISIBLE);
        }
    }

    private void hideProgressBar() {
        if (progressMyEvents != null) {
            progressMyEvents.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        callEventListApi(jsonObject);

    }

    private void callEventListApi(JsonObject jsonObject) {
        showProgressBar();
        if (SharedPref.getUserRegistration() != null) {
            ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getContext());
            apiServiceProvider.createdByMe(jsonObject, null, this);
        }
    }

    @Override
    public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {
        hideProgressBar();
        events.clear();
        resposeString=responseBodyString;
        CreatedByMeResponse createdByMeResponse = new Gson().fromJson(responseBodyString, CreatedByMeResponse.class);
        events.addAll(createdByMeResponse.getMyCompleteEvents());
        totalPastMyEventCount=createdByMeResponse.getPastMyEventCount();
        if (myEventsParentAdapter == null) {
            myEventsParentAdapter = new MyEventsParentAdapter(getActivity(), events);
            recyclerListEvents.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
            recyclerListEvents.setLayoutManager(layoutManager);
            recyclerListEvents.setAdapter(myEventsParentAdapter);

            recyclerListEvents.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (!recyclerView.canScrollVertically(1)) {
                      if(events.get(events.size()-1).getEventType().contains("Past Events") && pastEventCount<=totalPastMyEventCount){
                          CreatedByMeResponse createdByMeResponse = new Gson().fromJson(resposeString, CreatedByMeResponse.class);
                          events.set(events.size()-1,createdByMeResponse.getMyEventsRange(pastEventCount));
                          pastEventCount = pastEventCount+50;
                          if(pastEventCount>=totalPastMyEventCount) {
                              pastEventCount = totalPastMyEventCount;
                          }
                          myEventsParentAdapter.notifyDataSetChanged();
                      }
                    }
                }
            });

        } else {
            myEventsParentAdapter.notifyDataSetChanged();
        }

        if (events.size() == 0) {
            constraintEmpty.setVisibility(View.VISIBLE);
        } else {
            constraintEmpty.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
        hideProgressBar();
    }

    public void updateEvent(JsonObject jsonObject) {
        this.jsonObject = jsonObject;
        callEventListApi(jsonObject);
    }
}
