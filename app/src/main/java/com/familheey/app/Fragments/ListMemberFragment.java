package com.familheey.app.Fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.familheey.app.Adapters.ListMemberAdapter;
import com.familheey.app.CustomViews.TextViews.SemiBoldTextView;
import com.familheey.app.Interfaces.RetrofitListener;
import com.familheey.app.Models.ApiCallbackParams;
import com.familheey.app.Models.ErrorData;
import com.familheey.app.Models.Response.Family;
import com.familheey.app.Models.Response.ListAllMembersResponse;
import com.familheey.app.Models.Response.User;
import com.familheey.app.Networking.Retrofit.ApiServiceProvider;
import com.familheey.app.Networking.utils.GsonUtils;
import com.familheey.app.R;
import com.familheey.app.Utilities.SharedPref;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnEditorAction;

import static com.familheey.app.Utilities.Constants.Bundle.DATA;


public class ListMemberFragment extends Fragment implements RetrofitListener {

    @BindView(R.id.list_recyclerView)
    RecyclerView listRecyclerView;
    @BindView(R.id.progressListMember)
    ProgressBar progressBar;
    @BindView(R.id.searchMembers)
    EditText searchMembers;
    @BindView(R.id.clearSearch)
    ImageView clearSearch;
    @BindView(R.id.emptyResultText)
    SemiBoldTextView emptyResultText;

    @BindView(R.id.inviteFromContacts)
    TextView inviteFromContacts;

    private Family family;
    private ListMemberAdapter listMemberAdapter;
    private List<User> users = new ArrayList<>();

    public ListMemberFragment() {
    }

    public static ListMemberFragment newInstance(Family family) {
        ListMemberFragment fragment = new ListMemberFragment();
        Bundle args = new Bundle();
        args.putParcelable(DATA, family);
        fragment.setArguments(args);
        return fragment;
    }

    private void showProgress() {
        if (progressBar != null) {
            emptyResultText.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    private void hideProgress() {
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            family = getArguments().getParcelable(DATA);
            getArguments().clear();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.listmember_main, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeAdapter();
        callUserSearch();
        initializeSearchClearCallback();
    }

    private void initializeSearchClearCallback() {
        searchMembers.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0)
                    clearSearch.setVisibility(View.INVISIBLE);
                else clearSearch.setVisibility(View.VISIBLE);
            }
        });
        clearSearch.setOnClickListener(v -> {
            searchMembers.setText("");
            onSearchQueryListener(EditorInfo.IME_ACTION_SEARCH);
        });
    }

    private void initializeAdapter() {
        listMemberAdapter = new ListMemberAdapter(users, family, getActivity());
        listRecyclerView.setHasFixedSize(true);
        listRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        listRecyclerView.setAdapter(listMemberAdapter);
    }

    private void callUserSearch() {
        showProgress();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("client_id", SharedPref.getUserRegistration().getId());
        jsonObject.addProperty("group_id", family.getId().toString());
        jsonObject.addProperty("query", searchMembers.getText().toString().trim());
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getActivity());
        ApiCallbackParams apiCallbackParams = new ApiCallbackParams();
        apiCallbackParams.setStatus(false);
        apiServiceProvider.listMember(jsonObject, apiCallbackParams, this);
    }

    private void callGlobalSearch() {
        showProgress();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("searchtxt", searchMembers.getText().toString());
        jsonObject.addProperty("userid", SharedPref.getUserRegistration().getId());
        jsonObject.addProperty("offset", "0");
        jsonObject.addProperty("type", "users");
        jsonObject.addProperty("group_id", family.getId().toString());
        jsonObject.addProperty("limit", "500");
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getActivity());
        ApiCallbackParams apiCallbackParams = new ApiCallbackParams();
        apiCallbackParams.setStatus(true);
        apiServiceProvider.searchData(jsonObject, apiCallbackParams, this);
    }

    @Override
    public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {
        users.clear();
        if (apiCallbackParams.getStatus()) {
            try {
                JSONObject mainObject = new JSONObject(responseBodyString);
                if (mainObject.has("users")) {
                    JSONArray peopleArray = mainObject.getJSONArray("users");
                    users.addAll(GsonUtils.getInstance().getGson().fromJson(peopleArray.toString(), new TypeToken<ArrayList<User>>() {
                    }.getType()));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            ListAllMembersResponse listAllMembersResponse = new Gson().fromJson(responseBodyString, ListAllMembersResponse.class);
            users.addAll(listAllMembersResponse.getRows());
        }
        listMemberAdapter.notifyDataSetChanged();
        hideProgress();
        if (users.size() == 0)
            emptyResultText.setVisibility(View.VISIBLE);
        else emptyResultText.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
        hideProgress();
    }

    @Override
    public void onPause() {
        super.onPause();
        hideProgress();
    }


    @OnEditorAction(R.id.searchMembers)
    protected boolean onSearchQueryListener(int actionId) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            if (searchMembers.getText().toString().trim().length() == 0)
                callUserSearch();
            else callGlobalSearch();
            return true;
        }
        return false;
    }
}
