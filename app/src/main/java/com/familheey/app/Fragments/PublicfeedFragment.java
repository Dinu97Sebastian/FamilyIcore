package com.familheey.app.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.familheey.app.Activities.CreateFamilyActivity;
import com.familheey.app.R;
import com.familheey.app.Utilities.Constants;

public class PublicfeedFragment extends Fragment  {



    public PublicfeedFragment() {
        // Required empty public constructor
    }

    public static PublicfeedFragment newInstance() {
        PublicfeedFragment fragment = new PublicfeedFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_public_feed, container, false);

        return view;
    }




    @Override
    public void onDetach() {
        super.onDetach();
    }

    //@OnClick(R.id.createFamily)
    public void onViewClicked() {
        Intent intent = new Intent(getContext(), CreateFamilyActivity.class);
        startActivityForResult(intent, Constants.RequestCode.REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

}
