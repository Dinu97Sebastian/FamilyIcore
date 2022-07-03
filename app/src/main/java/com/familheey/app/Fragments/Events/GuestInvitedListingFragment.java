package com.familheey.app.Fragments.Events;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.familheey.app.Adapters.GuestDetailsListAdapter;
import com.familheey.app.Models.Response.GuestRSVPResponse;
import com.familheey.app.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GuestInvitedListingFragment extends Fragment {

    @BindView(R.id.guestList)
    RecyclerView guestList;

    ArrayList<GuestRSVPResponse> responseArrayList = new ArrayList<>();
    private GuestDetailsListAdapter guestDetailsListAdapter;
    private String query = "";

    public GuestInvitedListingFragment() {
        // Required empty public constructor
    }

    public static GuestInvitedListingFragment newInstance() {
        GuestInvitedListingFragment fragment = new GuestInvitedListingFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_guest_invited_listing, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initRecyclerView();
    }

    private void initRecyclerView() {
        guestDetailsListAdapter = new GuestDetailsListAdapter(responseArrayList);
        guestList.setLayoutManager(new LinearLayoutManager(getContext()));
        guestList.setAdapter(guestDetailsListAdapter);
    }

    public void updateGuestList(ArrayList<GuestRSVPResponse> responseArrayList) {
        this.responseArrayList.clear();
        this.responseArrayList.addAll(responseArrayList);
        guestDetailsListAdapter.notifyDataSetChanged();
    }
}
