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

import com.familheey.app.Adapters.EventGuest.OtherGuestListingAdapter;
import com.familheey.app.Models.InvitedOther;
import com.familheey.app.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OtherGuestListingFragment extends Fragment {

    @BindView(R.id.othersList)
    RecyclerView othersList;
    @BindView(R.id.txtEmptyText)
    TextView txtEmptyText;
    private ArrayList<InvitedOther> invitedOthers = new ArrayList<>();
    private OtherGuestListingAdapter otherGuestListingAdapter;

    public OtherGuestListingFragment() {
        // Required empty public constructor
    }

    public static OtherGuestListingFragment newInstance() {
        OtherGuestListingFragment fragment = new OtherGuestListingFragment();
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
        View view = inflater.inflate(R.layout.fragment_other_guest_listing, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeAdapter();
    }

    private void initializeAdapter() {
        otherGuestListingAdapter = new OtherGuestListingAdapter(invitedOthers);
        othersList.setLayoutManager(new LinearLayoutManager(getContext()));
        othersList.setAdapter(otherGuestListingAdapter);
    }

    public void updateInvitations(ArrayList<InvitedOther> invitedOthers) {
        this.invitedOthers.clear();
        this.invitedOthers.addAll(invitedOthers);
        otherGuestListingAdapter.notifyDataSetChanged();

        if (invitedOthers!=null&&invitedOthers.size()==0){
            txtEmptyText.setVisibility(View.VISIBLE);}
    }
}
