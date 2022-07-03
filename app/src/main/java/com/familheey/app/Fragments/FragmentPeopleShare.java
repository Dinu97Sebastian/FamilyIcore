package com.familheey.app.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.familheey.app.Adapters.PeopleShareAdapter;
import com.familheey.app.FamilheeyApplication;
import com.familheey.app.Interfaces.RetrofitListener;
import com.familheey.app.Models.ApiCallbackParams;
import com.familheey.app.Models.ErrorData;
import com.familheey.app.Models.Request.PostShareRequest;
import com.familheey.app.Models.Response.PeopleShareResponse;
import com.familheey.app.Networking.Retrofit.ApiServiceProvider;
import com.familheey.app.Networking.Retrofit.ApiServices;
import com.familheey.app.Networking.Retrofit.RetrofitBase;
import com.familheey.app.R;
import com.familheey.app.Utilities.Constants;
import com.familheey.app.Utilities.SharedPref;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

import static com.familheey.app.Utilities.Constants.Bundle.IS_INVITATION;

public class FragmentPeopleShare extends Fragment implements RetrofitListener {


    @BindView(R.id.inviteAll2)
    Button inviteAll2;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.progressBar7)
    ProgressBar progressBar7;
    @BindView(R.id.invite_progress)
    ProgressBar invite_progress;
    @BindView(R.id.emptyResultText)
    TextView emptyResultText;
    private String eventId;
    public PeopleShareAdapter peopleShareAdapter;
    private String query = "";
    private boolean isInvitation = false;
    private boolean isSearch = false;
    private String type;
    private CompositeDisposable subscriptions;

    public static Fragment newInstance(String eventId, boolean isInvitation, String type) {
        FragmentPeopleShare fragment = new FragmentPeopleShare();
        Bundle args = new Bundle();
        args.putBoolean(IS_INVITATION, isInvitation);
        args.putString(Constants.Bundle.DATA, eventId);
        args.putString("type", type);
        fragment.setArguments(args);
        return fragment;
    }


    public static Fragment newInstance(String eventId, boolean isInvitation, boolean isSearch) {
        FragmentPeopleShare fragment = new FragmentPeopleShare();
        Bundle args = new Bundle();
        args.putBoolean(IS_INVITATION, isInvitation);
        args.putString(Constants.Bundle.DATA, eventId);
        args.putBoolean("SEARCH", isSearch);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_people_share, container, false);
        subscriptions = new CompositeDisposable();
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            eventId = getArguments().getString(Constants.Bundle.DATA);
            isInvitation = getArguments().getBoolean(IS_INVITATION);
            isSearch = getArguments().getBoolean("SEARCH", false);
            type = getArguments().getString("type");
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (isSearch) {
            inviteAll2.setVisibility(View.GONE);
        }

        fetchMyFamilies();
        setREcyclerview();
        if (isInvitation)
            inviteAll2.setText("Invite");
        else
            inviteAll2.setText("Share");
    }

    private void setREcyclerview() {
        peopleShareAdapter = new PeopleShareAdapter();
        recyclerview.setAdapter(peopleShareAdapter);
        recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void fetchMyFamilies() {
        progressBar7.setVisibility(View.VISIBLE);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        jsonObject.addProperty("query", query);
        if (type != null && type.equalsIgnoreCase("EVENT")) {
            if (eventId != null && eventId.length() > 0)
                jsonObject.addProperty("event_id", eventId);
        }
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getActivity());
        apiServiceProvider.viewPeopleforShare(jsonObject, null, this);
    }

    @Override
    public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {
        progressBar7.setVisibility(View.INVISIBLE);
        if (apiFlag == Constants.ApiFlags.EVENT_GROUP_INVITE) {
            ArrayList<PeopleShareResponse.Datum> data = peopleShareAdapter.getSelectedList();
            invite_progress.setVisibility(View.INVISIBLE);
            inviteAll2.setVisibility(View.VISIBLE);
            for (PeopleShareResponse.Datum datum :
                    data) {
                if (datum.isDevSet())
                    datum.setInvited(true);
            }
            peopleShareAdapter.setData(new ArrayList<PeopleShareResponse.Datum>());
            peopleShareAdapter.setData(data);
            getActivity().finish();
        } else {
            PeopleShareResponse peopleShareResponse = new Gson().fromJson(responseBodyString, PeopleShareResponse.class);
            if (peopleShareResponse.getData().size() == 0) {
                emptyResultText.setVisibility(View.VISIBLE);
                peopleShareAdapter.setData(new ArrayList<PeopleShareResponse.Datum>());
            } else {
                emptyResultText.setVisibility(View.INVISIBLE);
                peopleShareAdapter.setData(peopleShareResponse.getData());
            }
            for (int i = 0; i < peopleShareAdapter.getSelectedList().size(); i++) {
                for (PeopleShareResponse.Datum fetchedUsers : peopleShareResponse.getData()) {
                    if (fetchedUsers.getUserId().equals(peopleShareAdapter.getSelectedList().get(i).getUserId())) {
                        fetchedUsers.setDevSet(true);
                    }
                }
            }
        }
    }

    @Override
    public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
        progressBar7.setVisibility(View.INVISIBLE);

    }

    @OnClick(R.id.inviteAll2)
    public void onViewClicked() {


        if (type.equals("EVENT")) {
            JsonArray jsonArray = new JsonArray();
            ArrayList<PeopleShareResponse.Datum> data = peopleShareAdapter.getSelectedList();
            for (int i = 0; i < data.size(); i++) {
                if (data.get(i).isDevSet())
                    jsonArray.add(data.get(i).getUserId());
            }

            if (jsonArray.size() == 0) {
                Toast.makeText(getActivity(), "Nothing is selected", Toast.LENGTH_SHORT).show();
            } else {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("event_id", eventId);
                jsonObject.addProperty("from_user", SharedPref.getUserRegistration().getId());
                jsonObject.add("user_id", jsonArray);
                jsonObject.addProperty("view", "template1");
                if (isInvitation)
                    jsonObject.addProperty("type", "invitation");
                else
                    jsonObject.addProperty("type", "share");

                invite_progress.setVisibility(View.VISIBLE);
                inviteAll2.setVisibility(View.INVISIBLE);
                ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getActivity());
                apiServiceProvider.eventGroupInvite(jsonObject, null, this);
            }
        } else {
            postShare();
        }

    }

    private void postShare() {

        PostShareRequest request = new PostShareRequest();
        request.setPost_id(eventId);

        ArrayList<String> users = new ArrayList<>();
        ArrayList<PeopleShareResponse.Datum> data = peopleShareAdapter.getSelectedList();
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).isDevSet())
                users.add(data.get(i).getUserId() + "");
        }

        if (users.size() == 0) {
            Toast.makeText(getActivity(), "Nothing is selected", Toast.LENGTH_SHORT).show();
            return;
        }
        request.setTo_user_id(users);
        request.setShared_user_id(SharedPref.getUserRegistration().getId());
        invite_progress.setVisibility(View.VISIBLE);
        inviteAll2.setVisibility(View.INVISIBLE);
        FamilheeyApplication application = FamilheeyApplication.get(getActivity());
        ApiServices apiServices = RetrofitBase.createRxResource(getActivity(), ApiServices.class);
        subscriptions.add(apiServices.postShare(request)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(response -> {
                    if (response.code() == 200) {
                        getActivity().finish();
                    }
                }, throwable -> {
                }));
    }


    public void updateQuery(String query) {
        this.query = query;
        fetchMyFamilies();
    }


}
