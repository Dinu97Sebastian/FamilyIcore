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

import com.familheey.app.Adapters.EventGuest.UserGuestListingAdapter;
import com.familheey.app.Models.Response.InvitedUser;
import com.familheey.app.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserGuestListingFragment extends Fragment {

    @BindView(R.id.userList)
    RecyclerView userList;
    @BindView(R.id.txtEmptyText)
    TextView txtEmptyText;
    private ArrayList<InvitedUser> invitedUsers = new ArrayList<>();
    private UserGuestListingAdapter userGuestListingAdapter;

    public UserGuestListingFragment() {
        // Required empty public constructor
    }

    public static UserGuestListingFragment newInstance() {
        UserGuestListingFragment fragment = new UserGuestListingFragment();
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
        View view = inflater.inflate(R.layout.fragment_user_guest_listing, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeAdapter();
    }

    private void initializeAdapter() {
        userGuestListingAdapter = new UserGuestListingAdapter(invitedUsers);
        userList.setLayoutManager(new LinearLayoutManager(getContext()));
        userList.setAdapter(userGuestListingAdapter);
    }

    public void updateInvitations(ArrayList<InvitedUser> invitedUsers) {
        this.invitedUsers.clear();
        this.invitedUsers.addAll(invitedUsers);
        userGuestListingAdapter.notifyDataSetChanged();
        if (invitedUsers!=null&&invitedUsers.size()==0){
            txtEmptyText.setVisibility(View.VISIBLE);
        }
    }
}
