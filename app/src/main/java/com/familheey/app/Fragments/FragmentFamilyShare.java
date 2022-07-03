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

import com.familheey.app.Adapters.FamilyShareAdapter;
import com.familheey.app.FamilheeyApplication;
import com.familheey.app.Interfaces.RetrofitListener;
import com.familheey.app.Models.ApiCallbackParams;
import com.familheey.app.Models.ErrorData;
import com.familheey.app.Models.Request.PostShareRequest;
import com.familheey.app.Models.Response.Family;
import com.familheey.app.Models.Response.SelectFamilys;
import com.familheey.app.Networking.Retrofit.ApiServiceProvider;
import com.familheey.app.Networking.Retrofit.ApiServices;
import com.familheey.app.Networking.Retrofit.RetrofitBase;
import com.familheey.app.Parsers.FamilyParser;
import com.familheey.app.R;
import com.familheey.app.Utilities.Constants;
import com.familheey.app.Utilities.SharedPref;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

import static com.familheey.app.Utilities.Constants.Bundle.IS_INVITATION;

public class FragmentFamilyShare extends Fragment implements RetrofitListener {

    @BindView(R.id.recyclerFamilyShare)
    RecyclerView recyclerFamilyShare;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.invite_progress)
    ProgressBar invite_progress;
    @BindView(R.id.inviteAll)
    Button inviteAll;
    @BindView(R.id.emptyResultText)
    TextView emptyResultText;
    private String eventId;
    private CompositeDisposable subscriptions;
    private final ArrayList<Family> families = new ArrayList<>();
    public FamilyShareAdapter linkFamilyAdapter;
    private String query = "";
    private boolean isInvitation = false;
    private String type;
    private String ref_id;
    private boolean isSearch = false;


    public static Fragment newInstance(String eventId, boolean isInvitation, String type) {
        FragmentFamilyShare fragment = new FragmentFamilyShare();
        Bundle args = new Bundle();
        args.putBoolean(IS_INVITATION, isInvitation);
        args.putString(Constants.Bundle.DATA, eventId);
        args.putString("type", type);
        fragment.setArguments(args);
        return fragment;
    }

    public static Fragment newInstance(String eventId, boolean isInvitation, boolean isSearch, String ref_id, String type) {
        FragmentFamilyShare fragment = new FragmentFamilyShare();
        Bundle args = new Bundle();
        args.putBoolean(IS_INVITATION, isInvitation);
        args.putString(Constants.Bundle.DATA, eventId);
        args.putBoolean("SEARCH", isSearch);
        args.putString("REF_ID", ref_id);
        args.putString("type", type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            eventId = getArguments().getString(Constants.Bundle.DATA);
            isInvitation = getArguments().getBoolean(IS_INVITATION);
            isSearch = getArguments().getBoolean("SEARCH", false);
            type = getArguments().getString("type", "");
            ref_id = getArguments().getString("REF_ID", "");
            subscriptions = new CompositeDisposable();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_family_share, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (isSearch) {
            inviteAll.setVisibility(View.GONE);
        }

        inviteAll.setOnClickListener(view1 -> {
            if (type.equals("EVENT")) {
                JsonArray jsonArray = new JsonArray();
                ArrayList<Family> families = linkFamilyAdapter.getSelectedList();
                for (int i = 0; i < families.size(); i++) {
                    if (families.get(i).isDevSelected())
                        jsonArray.add(families.get(i).getId());
                }
                if (jsonArray.size() == 0) {
                    Toast.makeText(getActivity(), "Nothing is selected", Toast.LENGTH_SHORT).show();
                } else {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("event_id", eventId);
                    jsonObject.addProperty("from_user", SharedPref.getUserRegistration().getId());
                    jsonObject.add("group_id", jsonArray);
                    jsonObject.addProperty("view", "template1");
                    if (isInvitation)
                        jsonObject.addProperty("type", "invitation");
                    else
                        jsonObject.addProperty("type", "share");

                    invite_progress.setVisibility(View.VISIBLE);
                    inviteAll.setVisibility(View.INVISIBLE);
                    //jsonObject.addProperty("txt", searchQuery);
                    ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getActivity());
                    apiServiceProvider.eventGroupInvite(jsonObject, null, this);

                }
            } else {
                postShare();
            }
        });

        fetchMyFamilies();

        setREcyclerview();
        if (isInvitation)
            inviteAll.setText("Invite");
        else
            inviteAll.setText("Share");
    }


    private void postShare() {

        PostShareRequest request = new PostShareRequest();
        request.setPost_id(eventId);

        ArrayList<SelectFamilys> users = new ArrayList<>();
        ArrayList<Family> data = linkFamilyAdapter.getSelectedList();
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).isDevSelected()) {
                SelectFamilys familys = new SelectFamilys();
                familys.setPost_create(data.get(i).getPostCreate());
                familys.setId(data.get(i).getId() + "");
                users.add(familys);
            }
        }


        if (users.size() == 0) {
            Toast.makeText(getActivity(), "Nothing is selected", Toast.LENGTH_SHORT).show();
            return;
        }
        request.setTo_group_id(users);
        request.setShared_user_id(SharedPref.getUserRegistration().getId());
        invite_progress.setVisibility(View.VISIBLE);
        inviteAll.setVisibility(View.INVISIBLE);
        FamilheeyApplication application = FamilheeyApplication.get(getActivity());
        ApiServices apiServices = RetrofitBase.createRxResource(getActivity(), ApiServices.class);
        assert application != null;
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

    private void setREcyclerview() {
        linkFamilyAdapter = new FamilyShareAdapter(type);
        recyclerFamilyShare.setAdapter(linkFamilyAdapter);
        recyclerFamilyShare.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void fetchMyFamilies() {


        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        jsonObject.addProperty("query", query);


        if (!type.equals("") && type.equals("ANNOUNCEMENT")) {
            jsonObject.addProperty("type", "announcement");
        }

        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getActivity());

        if (!type.equals("") && type.equals("POST") || type.equals("ANNOUNCEMENT") || type.equals("EVENT")) {
            apiServiceProvider.listAllFamilyPost(jsonObject, null, this);
        } else if (!type.equals("") && type.equals("NEED")) {
            apiServiceProvider.listAllFamily(jsonObject, null, this);
        } else if (!isSearch) {
            if (eventId != null && eventId.length() > 0)
                jsonObject.addProperty("event_id", eventId);
            apiServiceProvider.listAllFamily(jsonObject, null, this);
        } else {
            apiServiceProvider.listAllFamilyPost(jsonObject, null, this);
        }
    }

    @Override
    public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {
        if (apiFlag == Constants.ApiFlags.EVENT_GROUP_INVITE) {
            requireActivity().finish();
        } else {
            families.clear();

            ArrayList<Family> tempfamilies = new ArrayList<>(FamilyParser.parseLinkedFamilies(responseBodyString));

            //families.addAll(FamilyParser.parseLinkedFamilies(responseBodyString));
            for (int i = 0; i < linkFamilyAdapter.getSelectedFamilies().size(); i++) {
                for (Family fetchedFamilies : tempfamilies) {
                    if (fetchedFamilies.getId().equals(linkFamilyAdapter.getSelectedFamilies().get(i).getId())) {
                        fetchedFamilies.setDevSelected(true);
                    }
                }
            }


            ArrayList<SelectFamilys> ids = new Gson().fromJson(ref_id, new TypeToken<List<SelectFamilys>>() {
            }.getType());

            if (!ref_id.equals("")) {

                for (SelectFamilys obj : ids) {

                    for (Family family : tempfamilies) {

                        if (obj.getId().equals(family.getId().toString())) {
                            family.setDevSelected(true);

                            linkFamilyAdapter.addSelectedFamilies(family);
                        }

                    }

                }
                families.addAll(tempfamilies);
            } else {
                families.addAll(tempfamilies);
            }
            linkFamilyAdapter.setData(families);
            progressBar.setVisibility(View.GONE);
            if (families != null && families.size() > 0) {
                recyclerFamilyShare.setVisibility(View.VISIBLE);
                emptyResultText.setVisibility(View.INVISIBLE);
            } else {
                emptyResultText.setVisibility(View.VISIBLE);
                recyclerFamilyShare.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
        try {
            Toast.makeText(getActivity(), errorData.getMessage(), Toast.LENGTH_SHORT).show();
            invite_progress.setVisibility(View.INVISIBLE);
            inviteAll.setVisibility(View.VISIBLE);
        } catch (Exception e) {

        }
    }

    public void updateQuery(String query) {
        this.query = query;
        fetchMyFamilies();
    }


}