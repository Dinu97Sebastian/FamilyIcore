package com.familheey.app.Fragments;

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

import com.familheey.app.Adapters.GuestDetailsListAdapter;
import com.familheey.app.Interfaces.RetrofitListener;
import com.familheey.app.Models.ApiCallbackParams;
import com.familheey.app.Models.ErrorData;
import com.familheey.app.Models.Response.GuestRSVPResponse;
import com.familheey.app.Networking.Retrofit.ApiServiceProvider;
import com.familheey.app.Networking.utils.GsonUtils;
import com.familheey.app.R;
import com.familheey.app.Utilities.Constants;
import com.familheey.app.Utilities.SharedPref;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class FragmentMayAttend extends Fragment {


    @BindView(R.id.count)
    TextView count;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.no_data)
    TextView noData;
    private String eventId;
    private String query = "";
    private ArrayList<GuestRSVPResponse> responseArrayList = new ArrayList<>();
    private GuestDetailsListAdapter guestDetailsListAdapter;

    public static FragmentMayAttend newInstance(String eventId) {
        FragmentMayAttend fragment = new FragmentMayAttend();
        Bundle args = new Bundle();
        args.putString(Constants.Bundle.DATA, eventId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            eventId = getArguments().getString(Constants.Bundle.DATA);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.guest_fragment_generic_layout, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initRecyclerView();
        mayattend(query);
    }

    private void initRecyclerView() {
        guestDetailsListAdapter = new GuestDetailsListAdapter(responseArrayList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(guestDetailsListAdapter);
    }

    public void mayattend(String query) {
        this.query = query;
        recyclerView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("event_id", eventId);
        jsonObject.addProperty("current_id", SharedPref.getUserRegistration().getId());
        jsonObject.addProperty("rsvp_status", "interested");
        jsonObject.addProperty("query", query);


        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getActivity());
        apiServiceProvider.getRsvpGuestList(jsonObject, null, new RetrofitListener() {
            @Override
            public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {
                recyclerView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                responseArrayList.clear();
                responseArrayList.addAll(GsonUtils.getInstance().getGson().fromJson(responseBodyString, new TypeToken<ArrayList<GuestRSVPResponse>>() {
                }.getType()));
                count.setText(String.valueOf(responseArrayList.size()));
                if (responseArrayList.size() == 0) {
                    noData.setVisibility(View.VISIBLE);
                } else
                    noData.setVisibility(View.INVISIBLE);
                guestDetailsListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
                progressBar.setVisibility(View.GONE);
                noData.setVisibility(View.VISIBLE);
            }
        });
    }
}

