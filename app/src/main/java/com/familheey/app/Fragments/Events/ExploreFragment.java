package com.familheey.app.Fragments.Events;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import com.familheey.app.Models.Response.ExploreEvents;
import com.familheey.app.Networking.Retrofit.ApiServiceProvider;
import com.familheey.app.R;
import com.familheey.app.Utilities.Utilities;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExploreFragment extends Fragment implements RetrofitListener {
//    @BindView(R.id.progressBarExplore)
//    ProgressBar progressBarExplore;
    @BindView(R.id.empty_container)
    View empty_container;
    @BindView(R.id.recyclerExplore)
    RecyclerView recyclerExplore;

    private ExploreAdapter exploreAdapter;
    private JsonObject jsonObject = Utilities.getDefaultEventSearchJson();
    private final List<Event> sharedEvents = new ArrayList<>();
    @BindView(R.id.shimmer_view_container)
    com.facebook.shimmer.ShimmerFrameLayout shimmer_view_container;
    public ExploreFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_explore, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeRecyclerView();
    }

    private void initializeRecyclerView() {
        exploreAdapter = new ExploreAdapter(getContext(), sharedEvents);
        recyclerExplore.setHasFixedSize(true);
        recyclerExplore.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerExplore.setAdapter(exploreAdapter);
    }

    private void showProgress() {
        if (shimmer_view_container != null) {
            shimmer_view_container.setVisibility(View.VISIBLE);
            shimmer_view_container.startShimmer();
            recyclerExplore.setVisibility(View.INVISIBLE);
        }
    }

    private void hideProgress() {
        if (shimmer_view_container != null) {
            shimmer_view_container.stopShimmer();
            shimmer_view_container.setVisibility(View.GONE);
            recyclerExplore.setVisibility(View.VISIBLE);
        }
    }

    private void fetchData(JsonObject jsonObject) {
        showProgress();
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getContext());
        apiServiceProvider.exploreEvents(jsonObject, null, this);
    }

    @Override
    public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {
        ExploreEvents exploreEvents = new Gson().fromJson(responseBodyString, ExploreEvents.class);
        sharedEvents.clear();
        sharedEvents.addAll(exploreEvents.getData().getExplore().getSharedEvents());
        for (Event aud : sharedEvents) {
            aud.setIsShared("SHARE");
        }
        sharedEvents.addAll(exploreEvents.getData().getExplore().getBasedOnLocation());
        sharedEvents.addAll(exploreEvents.getData().getExplore().getPublicEvents());
        exploreAdapter.notifyDataSetChanged();
        if (sharedEvents.size() == 0) {
            empty_container.setVisibility(View.VISIBLE);
        } else
            empty_container.setVisibility(View.INVISIBLE);

        hideProgress();
    }

    @Override
    public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
        Toast.makeText(getActivity(), errorData.getMessage(), Toast.LENGTH_SHORT).show();
        hideProgress();
    }

    public void updateEvent(JsonObject jsonObject) {
        this.jsonObject = jsonObject;
        fetchData(jsonObject);
    }

    public void addEvents(List<Event> passedEvents) {
        sharedEvents.clear();
        sharedEvents.addAll(passedEvents);
        exploreAdapter.notifyDataSetChanged();
    }
}
