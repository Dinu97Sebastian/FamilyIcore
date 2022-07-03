package com.familheey.app.Fragments.Events;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.familheey.app.Activities.AddSignUpActivity;
import com.familheey.app.Activities.CreatedEventDetailActivity;
import com.familheey.app.Activities.EditSignupDescriptionActivity;
import com.familheey.app.Activities.VolunteersActivity;
import com.familheey.app.Adapters.SignupAdapter;
import com.familheey.app.Interfaces.RetrofitListener;
import com.familheey.app.Models.ApiCallbackParams;
import com.familheey.app.Models.ErrorData;
import com.familheey.app.Models.Response.EventDetail;
import com.familheey.app.Models.Response.EventItemsResponse;
import com.familheey.app.Models.Response.EventSignUp;
import com.familheey.app.Models.Response.SignUpContributor;
import com.familheey.app.Networking.Retrofit.ApiServiceProvider;
import com.familheey.app.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import butterknife.ButterKnife;

import static android.os.Build.ID;
import static com.familheey.app.Utilities.Constants.Bundle.ADDITIONAL_DATA;
import static com.familheey.app.Utilities.Constants.Bundle.DATA;
import static com.familheey.app.Utilities.Constants.Bundle.EVENT_ID;
import static com.familheey.app.Utilities.Constants.Bundle.IS_ADMIN;
import static com.familheey.app.Utilities.Constants.Bundle.IS_EDIT;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignupFragment extends Fragment implements SignupAdapter.OnSignUpListener, EventSignupDialog.OnSignupUpdatedListener {
    ImageView imageViewEdit;
    RecyclerView recyclerView;
    SignupAdapter signupAdapter;
    ProgressBar progressSignUp;
    ConstraintLayout constraintEmpty, constraintRecycler;
    CardView floatingAddSignup;
    Button continu;
    EventDetail eventDetail;
    private EventItemsResponse eventItemsResponse;

    public SignupFragment() {
        // Required empty public constructor
    }

    public static SignupFragment newInstance(EventDetail eventDetail) {
        SignupFragment fragment = new SignupFragment();
        Bundle args = new Bundle();
        args.putParcelable(DATA, eventDetail);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            eventDetail = getArguments().getParcelable(DATA);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_signup, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recyclerView = getActivity().findViewById(R.id.recyclerSignupFrag);
        progressSignUp = getActivity().findViewById(R.id.progressSignUp);
        imageViewEdit = getActivity().findViewById(R.id.imageViewEdit);
        constraintEmpty = getActivity().findViewById(R.id.constraintEmpty);
        constraintRecycler = getActivity().findViewById(R.id.constraintRecycler);
        continu = getActivity().findViewById(R.id.create_event);
        imageViewEdit.setOnClickListener(view -> {
                    if (eventDetail.isPastEvent()) {
                        Toast.makeText(getContext(), "This is a past event ! You cannot edit description", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    startActivity(new Intent(getActivity(), EditSignupDescriptionActivity.class).putExtra("ID",eventDetail.getId()).putExtra("EVENT_ID",eventDetail.getEventId()));
                }
        );
        continu.setOnClickListener(view -> {
            if (eventDetail.isPastEvent()) {
                Toast.makeText(getContext(), "This is a past event ! You cannot add sign up", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!eventDetail.isAdminViewing()) {
                Toast.makeText(getContext(), "Only admin of this group can create signups!", Toast.LENGTH_SHORT).show();
                return;
            }
            CreatedEventDetailActivity createdEventDetailActivity = (CreatedEventDetailActivity) getContext();
            if (createdEventDetailActivity.isAdmin()) {
                Intent intent = new Intent(getActivity(), AddSignUpActivity.class);
                intent.putExtra(IS_EDIT, false);
                intent.putExtra(EVENT_ID, eventDetail.getEventId());
                intent.putExtra(ID, eventDetail.getId());
                intent.putExtra("START_DATE", eventDetail.getFromDate());
                intent.putExtra("END_DATE", eventDetail.getToDate());
                intent.putExtra(ADDITIONAL_DATA, eventDetail);
                startActivity(intent);
            } else {
                Toast.makeText(getActivity(), "You are not an admin", Toast.LENGTH_SHORT).show();
            }
        });
        floatingAddSignup = getActivity().findViewById(R.id.floatingAddSignup);
//        imageViewEdit.setOnClickListener(view ->
//                startActivity(new Intent(getActivity(), EditSignupDescriptionActivity.class).putExtra("ID",eventDetail.getId()).putExtra("EVENT_ID",eventDetail.getEventId())));
        floatingAddSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (eventDetail.isPastEvent()) {
                    Toast.makeText(getContext(), "This is a past event ! You cannot add sign up", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!eventDetail.isAdminViewing()) {
                    Toast.makeText(getContext(), "Only admin of this group can create signups!", Toast.LENGTH_SHORT).show();
                    return;
                }
                CreatedEventDetailActivity createdEventDetailActivity = (CreatedEventDetailActivity) getContext();
                if (createdEventDetailActivity.isAdmin()) {
                    Intent intent = new Intent(getActivity(), AddSignUpActivity.class);
                    intent.putExtra(IS_EDIT, false);
                    intent.putExtra(EVENT_ID, eventDetail.getEventId());
                    intent.putExtra(DATA, eventDetail);
                    intent.putExtra("START_DATE", eventDetail.getFromDate());
                    intent.putExtra("END_DATE", eventDetail.getToDate());
                    intent.putExtra(ADDITIONAL_DATA, eventDetail);
                    startActivity(intent);
                } else {
                    Toast.makeText(getActivity(), "You are not an admin", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchData();

    }

    void showProgress() {
        if (progressSignUp != null) {
            progressSignUp.setVisibility(View.VISIBLE);
        }
    }

    void hideProgress() {
        if (progressSignUp != null) {
            progressSignUp.setVisibility(View.GONE);
        }
    }

    private void fetchData() {
        showProgress();
        JsonObject lisFamily = new JsonObject();
        lisFamily.addProperty("event_id", String.valueOf(eventDetail.getEventId()));
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getContext());
        apiServiceProvider.getEventItems(lisFamily, null, new RetrofitListener() {
            @Override
            public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {
                hideProgress();
                 eventItemsResponse = new Gson().fromJson(responseBodyString, EventItemsResponse.class);
                if (eventItemsResponse.getData().size() == 0) {
                    constraintEmpty.setVisibility(View.VISIBLE);
                    constraintRecycler.setVisibility(View.GONE);
                    floatingAddSignup.setVisibility(View.GONE);
                } else {
                    constraintEmpty.setVisibility(View.GONE);
                    constraintRecycler.setVisibility(View.VISIBLE);
                    if (eventDetail.isAdminViewing()) {
                        imageViewEdit.setVisibility(View.GONE);
                        floatingAddSignup.setVisibility(View.VISIBLE);
                    } else {
                        imageViewEdit.setVisibility(View.GONE);
                    }

                }

                signupAdapter = new SignupAdapter(SignupFragment.this, eventItemsResponse.getData(), getActivity());
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                if (eventDetail != null)
                    signupAdapter.setRestriction(eventDetail.isPastEvent());
                recyclerView.setAdapter(signupAdapter);
            }

            @Override
            public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
                hideProgress();
            }
        });
    }

    @Override
    public void viewSignUp(EventSignUp eventSignUp) {
        Intent intent = new Intent(getContext(), VolunteersActivity.class);
        intent.putExtra(DATA, eventSignUp);
        intent.putExtra(IS_ADMIN, eventDetail.isAdminViewing());
        startActivity(intent);
    }

    @Override
    public void addSignUp(EventSignUp eventSignUp) {
        EventSignupDialog.newInstance(eventSignUp).show(getChildFragmentManager(), "EventSignupDialog");
    }

    @Override
    public void editSignUp(EventSignUp eventSignUp) {
        if (eventDetail.isPastEvent()) {
            Toast.makeText(getContext(), "Past events cannot be edited", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(getContext(), AddSignUpActivity.class);
        intent.putExtra(IS_EDIT, true);
        intent.putExtra(EVENT_ID, eventSignUp.getEventId());
        intent.putExtra(ID, eventSignUp.getId());
        intent.putExtra(DATA, eventSignUp);
        intent.putExtra("START_DATE", eventDetail.getFromDate());
        intent.putExtra("END_DATE", eventDetail.getToDate());
        intent.putExtra(ADDITIONAL_DATA, eventDetail);
        startActivity(intent);
    }

    @Override
    public void onSignUpAdded(EventSignUp eventSignUp) {
        fetchData();
    }

    @Override
    public void onSignUpUpdated(SignUpContributor signUpContributor) {
        fetchData();
    }

    public void updateEvent(EventDetail eventDetail) {
        this.eventDetail = eventDetail;
        if (eventDetail.isPastEvent()) {
            floatingAddSignup.setVisibility(View.INVISIBLE);
        } else if (eventDetail.isAdminViewing())
            floatingAddSignup.setVisibility(View.VISIBLE);
        if (signupAdapter != null) {
            signupAdapter.setRestriction(eventDetail.isPastEvent());
            signupAdapter.notifyDataSetChanged();
        }
    }

    public void onRefreshItems() {
        fetchData();
    }
}
