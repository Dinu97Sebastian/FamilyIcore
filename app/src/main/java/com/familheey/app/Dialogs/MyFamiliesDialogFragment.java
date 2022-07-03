package com.familheey.app.Dialogs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

import com.familheey.app.Adapters.FamilyMemberJoiningAdapter;
import com.familheey.app.Interfaces.ProgressListener;
import com.familheey.app.Interfaces.RetrofitListener;
import com.familheey.app.Models.ApiCallbackParams;
import com.familheey.app.Models.ErrorData;
import com.familheey.app.Models.Response.Family;
import com.familheey.app.Models.Response.PeopleSearchModal;
import com.familheey.app.Networking.Retrofit.ApiServiceProvider;
import com.familheey.app.Parsers.FamilyParser;
import com.familheey.app.R;
import com.familheey.app.Utilities.Constants;
import com.familheey.app.Utilities.SharedPref;
import com.familheey.app.Utilities.Utilities;
import com.google.android.material.button.MaterialButton;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MyFamiliesDialogFragment extends Fragment implements RetrofitListener {

    @BindView(R.id.titleMyFamilies)
    TextView titleMyFamilies;
    @BindView(R.id.familyList)
    RecyclerView familyList;
    @BindView(R.id.close)
    MaterialButton close;
    @BindView(R.id.progressBar3)
    ProgressBar progressBar;

    private ProgressListener mListener;
    private final List<Family> families = new ArrayList<>();
    private PeopleSearchModal user;
    private FamilyMemberJoiningAdapter familyMemberJoiningAdapter;

    private final boolean hasStateChanged = false;

    public MyFamiliesDialogFragment() {
        // Required empty public constructor
    }

    public static MyFamiliesDialogFragment newInstance(PeopleSearchModal user) {
        MyFamiliesDialogFragment fragment = new MyFamiliesDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable(Constants.Bundle.DATA, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_AppCompat_Light_Dialog_Alert);
        if (getArguments() != null) {
            user = getArguments().getParcelable(Constants.Bundle.DATA);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_families_dialog, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeAdapter();
        fetchCreatedFamilies();
    }

    private void initializeAdapter() {
        familyMemberJoiningAdapter = new FamilyMemberJoiningAdapter(getContext(), families, user.getId().toString(), SharedPref.getUserRegistration().getId());
        familyList.setAdapter(familyMemberJoiningAdapter);
        familyList.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = Utilities.getListener(this, ProgressListener.class);
        if (mListener == null) {
            throw new RuntimeException(context.toString()
                    + " must implement OnMyFamilyInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void fetchCreatedFamilies() {
        progressBar.setVisibility(View.VISIBLE);
        JsonObject lisFamily = new JsonObject();
        lisFamily.addProperty("user_id", SharedPref.getUserRegistration().getId());
        lisFamily.addProperty("member_to_add", user.getId().toString());
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getContext());
        apiServiceProvider.getAllGroupsBasedOnUserId(lisFamily, null, this);
    }

    @Override
    public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {
        families.clear();
        families.addAll(FamilyParser.parseLinkedFamilies(responseBodyString));
        progressBar.setVisibility(View.INVISIBLE);
        familyMemberJoiningAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
        progressBar.setVisibility(View.INVISIBLE);
        mListener.showErrorDialog(errorData.getMessage());
//        dismiss();
    }

    @OnClick(R.id.close)
    public void onViewClicked() {
//        if (hasStateChanged)
            performCallback();
//        else
//            dismiss();
    }

    // this is added ncase if callback is needed
    void performCallback() {
        Bundle bundle = new Bundle();
        Intent mIntent = new Intent();
        bundle.putString(Constants.Bundle.DATA, "WTF");
        mIntent.putExtras(bundle);
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, mIntent);
//        dismiss();
    }
}
