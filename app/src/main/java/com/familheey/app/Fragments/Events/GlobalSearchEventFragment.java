package com.familheey.app.Fragments.Events;


import android.content.Context;
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

import com.familheey.app.Adapters.GlobalSearchEventAdapter;
import com.familheey.app.CustomViews.TextViews.SemiBoldTextView;
import com.familheey.app.Interfaces.GlobalSearchListener;
import com.familheey.app.Interfaces.RetrofitListener;
import com.familheey.app.Models.ApiCallbackParams;
import com.familheey.app.Models.ErrorData;
import com.familheey.app.Models.Response.Event;
import com.familheey.app.Networking.Retrofit.ApiServiceProvider;
import com.familheey.app.Parsers.EventsParser;
import com.familheey.app.R;
import com.familheey.app.Utilities.SharedPref;
import com.familheey.app.Utilities.Utilities;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GlobalSearchEventFragment extends Fragment implements RetrofitListener {
    @BindView(R.id.progressBarExplore)
    ProgressBar progressBarExplore;
    @BindView(R.id.empty_container)
    View empty_container;
    @BindView(R.id.recyclerExplore)
    RecyclerView recyclerExplore;
    @BindView(R.id.searchLabelIndicator)
    TextView searchLabelIndicator;
    @BindView(R.id.emptyResultText)
    SemiBoldTextView emptyResultText;
    private String query = "";

    private GlobalSearchEventAdapter exploreAdapter;
    private final List<Event> sharedEvents = new ArrayList<>();
    private GlobalSearchListener globalSearchListener;

    public GlobalSearchEventFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_global_search_event, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeRecyclerView();
        if (progressBarExplore != null)
            progressBarExplore.setVisibility(View.VISIBLE);
    }

    private void initializeRecyclerView() {
        exploreAdapter = new GlobalSearchEventAdapter(getContext(), sharedEvents);
        recyclerExplore.setHasFixedSize(true);
        recyclerExplore.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerExplore.setAdapter(exploreAdapter);
    }

    private void showProgress() {
        if (progressBarExplore != null) {
            progressBarExplore.setVisibility(View.VISIBLE);
            recyclerExplore.setVisibility(View.INVISIBLE);
        }
    }

    private void hideProgress() {
        if (progressBarExplore != null) {
            progressBarExplore.setVisibility(View.GONE);
            recyclerExplore.setVisibility(View.VISIBLE);
        }
    }

    public void fetchEvents(String query) {
        this.query = query;
        if (progressBarExplore != null)
        progressBarExplore.setVisibility(View.VISIBLE);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("userid", SharedPref.getUserRegistration().getId());//
        jsonObject.addProperty("type", "events");
        jsonObject.addProperty("searchtxt", query);
        jsonObject.addProperty("offset", "0");
        jsonObject.addProperty("limit", "10000");
        showProgress();
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getContext());
        apiServiceProvider.searchData(jsonObject, null, this);
    }

    @Override
    public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {
        if (progressBarExplore != null)
            progressBarExplore.setVisibility(View.INVISIBLE);
        try {
            JSONObject mainObject = new JSONObject(responseBodyString);
            JSONArray eventArray = mainObject.getJSONArray("events");
            sharedEvents.clear();
            sharedEvents.addAll(EventsParser.parseEventArray(eventArray));
            exploreAdapter.notifyDataSetChanged();
            if (sharedEvents.size() == 0)
                emptyResultText.setVisibility(View.VISIBLE);
            else
                emptyResultText.setVisibility(View.INVISIBLE);
            hideProgress();
        } catch (JSONException e) {
            e.printStackTrace();
            emptyResultText.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
        hideProgress();
        if (progressBarExplore != null)
            progressBarExplore.setVisibility(View.INVISIBLE);
    }

    public void addEvents(List<Event> passedEvents) {
        sharedEvents.clear();
        sharedEvents.addAll(passedEvents);
        if (sharedEvents.size() == 0)
            emptyResultText.setVisibility(View.VISIBLE);
        else
            emptyResultText.setVisibility(View.INVISIBLE);
        exploreAdapter.notifyDataSetChanged();
    }

    public void updateSearchIndication(String searchText) {
        if (searchLabelIndicator == null) return;
        if (searchText.length() == 0) {
            searchLabelIndicator.setText("Suggested");
        } else {
            searchLabelIndicator.setText("Showing results for \"" + searchText + "\"");
        }
        query = searchText;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        globalSearchListener = Utilities.getListener(this, GlobalSearchListener.class);
        if (globalSearchListener == null)
            throw new RuntimeException(context.getClass().getSimpleName() + " must implement GlobalSearchListener");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        globalSearchListener = null;
    }
}
