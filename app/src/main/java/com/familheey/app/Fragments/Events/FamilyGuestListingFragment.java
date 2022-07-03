package com.familheey.app.Fragments.Events;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.familheey.app.Adapters.EventGuest.FamilyGuestListingAdapter;
import com.familheey.app.Models.Response.InvitedFamiliy;
import com.familheey.app.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FamilyGuestListingFragment extends Fragment {

    @BindView(R.id.familyList)
    RecyclerView familyList;

    @BindView(R.id.txtEmptyText)
    TextView txtEmptyText;
    private FamilyGuestListingAdapter familyGuestListingAdapter;
    private ArrayList<InvitedFamiliy> invitedFamilies = new ArrayList<>();

    public FamilyGuestListingFragment() {
        // Required empty public constructor
    }

    public static FamilyGuestListingFragment newInstance() {
        FamilyGuestListingFragment fragment = new FamilyGuestListingFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_family_guest_listing, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeAdapter();
    }

    private void initializeAdapter() {
        familyGuestListingAdapter = new FamilyGuestListingAdapter(invitedFamilies);
        familyList.setLayoutManager(new LinearLayoutManager(getContext()));
        familyList.setAdapter(familyGuestListingAdapter);
    }

    public void updateInvitations(ArrayList<InvitedFamiliy> invitedFamilies) {
        this.invitedFamilies.clear();
        this.invitedFamilies.addAll(invitedFamilies);
        familyGuestListingAdapter.notifyDataSetChanged();

        if (invitedFamilies!=null&&invitedFamilies.size()==0){
            txtEmptyText.setVisibility(View.VISIBLE);
        }
    }
}
