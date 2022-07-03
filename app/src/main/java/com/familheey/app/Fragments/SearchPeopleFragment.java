package com.familheey.app.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.familheey.app.Activities.MyFamiliesActivity;
import com.familheey.app.Adapters.PeopleSearchAdapter;
import com.familheey.app.CustomViews.TextViews.SemiBoldTextView;
import com.familheey.app.Interfaces.GlobalSearchListener;
import com.familheey.app.Interfaces.RetrofitListener;
import com.familheey.app.Models.ApiCallbackParams;
import com.familheey.app.Models.ErrorData;
import com.familheey.app.Models.Response.FamilyMember;
import com.familheey.app.Models.Response.PeopleSearchModal;
import com.familheey.app.Networking.Retrofit.ApiServiceProvider;
import com.familheey.app.Networking.utils.GsonUtils;
import com.familheey.app.Parsers.FamilyParser;
import com.familheey.app.R;
import com.familheey.app.Utilities.Constants;
import com.familheey.app.Utilities.SharedPref;
import com.familheey.app.Utilities.Utilities;
import com.familheey.app.pagination.PaginationScrollListener;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.familheey.app.Activities.UserConnectionsActivity.CONNECTIONS;
import static com.familheey.app.Activities.UserConnectionsActivity.MUTUAL_CONNECTIONS;
import static com.familheey.app.Utilities.Constants.Bundle.DATA;
import static com.familheey.app.Utilities.Constants.Bundle.IDENTIFIER;

public class SearchPeopleFragment extends Fragment implements PeopleSearchAdapter.OnPeopleJoinInteraction, RetrofitListener {


    private final ArrayList<PeopleSearchModal> peopleSearchArrayList = new ArrayList<>();
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.searchLabelIndicator)
    TextView searchLabelIndicator;
    @BindView(R.id.emptyResultText)
    SemiBoldTextView emptyResultText;
//    @BindView(R.id.progressBar)
//    ProgressBar progressBar;
    private PeopleSearchAdapter peopleSearchAdapter;
    private GlobalSearchListener globalSearchListener;
    private boolean defaultSearchEnabled = false;
    private boolean isSuggestionTextNeeded = true;
    @BindView(R.id.shimmer_view_container)
    com.facebook.shimmer.ShimmerFrameLayout shimmer_view_container;
    private String otherUserId;
    private int TYPE;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private final int TOTAL_PAGES = 0;

    public static SearchPeopleFragment newInstance(boolean defaultSearchEnabled, boolean isSuggestionTextNeeded,int type,String otherUserId) {
        SearchPeopleFragment searchPeopleFragment = new SearchPeopleFragment();
        Bundle args = new Bundle();
        args.putBoolean(DATA, defaultSearchEnabled);
        args.putBoolean(IDENTIFIER, isSuggestionTextNeeded);
        args.putString("userId", otherUserId);
        args.putInt("type", type);
        searchPeopleFragment.setArguments(args);
        return searchPeopleFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            defaultSearchEnabled = getArguments().getBoolean(DATA, false);
            isSuggestionTextNeeded = getArguments().getBoolean(IDENTIFIER, true);
            otherUserId=getArguments().getString("userId");
            TYPE=getArguments().getInt("type");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_result_people_fragment, container, false);
        ButterKnife.bind(this, view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        peopleSearchAdapter = new PeopleSearchAdapter(this, peopleSearchArrayList, this);
        recyclerview.setHasFixedSize(true);
        recyclerview.setLayoutManager(layoutManager);
        recyclerview.setAdapter(peopleSearchAdapter);
        recyclerview.addOnScrollListener(new PaginationScrollListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                switch (TYPE) {
                    case CONNECTIONS:
                        fetchConnections();
                    case MUTUAL_CONNECTIONS:
                        fetchMutualConnections();
                        break;
                }

            }

            @Override
            public int getTotalPageCount() {
                return TOTAL_PAGES;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (defaultSearchEnabled) {
            if (shimmer_view_container != null) {
                shimmer_view_container.setVisibility(View.VISIBLE);
                shimmer_view_container.startShimmer();
            }
        }
        if (isSuggestionTextNeeded)
            searchLabelIndicator.setVisibility(View.VISIBLE);
        else searchLabelIndicator.setVisibility(View.INVISIBLE);

    }

    public void updatePeople(ArrayList<PeopleSearchModal> peopleSearchModalArrayList) {
        this.peopleSearchArrayList.clear();
        this.peopleSearchArrayList.addAll(peopleSearchModalArrayList);
        if (peopleSearchArrayList.size() == 0)
            emptyResultText.setVisibility(View.VISIBLE);
        else
            emptyResultText.setVisibility(View.INVISIBLE);
        if (shimmer_view_container != null) {
            shimmer_view_container.stopShimmer();
            shimmer_view_container.setVisibility(View.GONE);
        }
        peopleSearchAdapter.hideAddToTopics(true);
        peopleSearchAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        globalSearchListener = Utilities.getListener(this, GlobalSearchListener.class);
        if (globalSearchListener == null)
            throw new RuntimeException(context.getClass().getSimpleName() + " must implement GlobalSearchListener");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        globalSearchListener = null;
    }

    void fetchPeoples(String query) {
        if (shimmer_view_container != null) {
            shimmer_view_container.setVisibility(View.VISIBLE);
            shimmer_view_container.startShimmer();
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("userid", SharedPref.getUserRegistration().getId());//
        jsonObject.addProperty("type", "users");
        jsonObject.addProperty("searchtxt", query);
        jsonObject.addProperty("offset", "0");
        jsonObject.addProperty("limit", "10000");
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getContext());
        apiServiceProvider.searchData(jsonObject, null, this);
    }

    @Override
    public void onUserSelected(PeopleSearchModal user) {
        startActivity(new Intent(getActivity(), MyFamiliesActivity.class).putExtra(Constants.Bundle.DATA, user));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        globalSearchListener.requsetGlobalSearch();
    }

    void updateSearchIndication(String searchText) {
        if (searchLabelIndicator == null) return;
        peopleSearchArrayList.clear();
        peopleSearchAdapter.notifyDataSetChanged();
        if (searchText.length() == 0) {
            searchLabelIndicator.setText("Suggested");
        } else {
            searchLabelIndicator.setText("Showing results for \"" + searchText + "\"");
        }
    }

    @Override
    public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {
        if (shimmer_view_container != null) {
            shimmer_view_container.stopShimmer();
            shimmer_view_container.setVisibility(View.GONE);
        }
        try {
            ArrayList<PeopleSearchModal> temp =new ArrayList<>();
            String parsedFormatJson = "";
            switch (TYPE) {
                case CONNECTIONS:
                    parsedFormatJson = new JSONArray(responseBodyString).toString();
                    break;
                case MUTUAL_CONNECTIONS:
                    parsedFormatJson = new JSONObject(responseBodyString).getJSONArray("data").toString();
                    break;
            }
            temp.addAll(GsonUtils.getInstance().getGson().fromJson(parsedFormatJson, new TypeToken<ArrayList<PeopleSearchModal>>() {
            }.getType()));
            if (temp != null && temp.size() == 15) {
                isLoading = false;
            } else {
                isLastPage = true;
            }
            peopleSearchArrayList.addAll(GsonUtils.getInstance().getGson().fromJson(parsedFormatJson, new TypeToken<ArrayList<PeopleSearchModal>>() {
            }.getType()));
            peopleSearchAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
        if (shimmer_view_container != null) {
            shimmer_view_container.stopShimmer();
            shimmer_view_container.setVisibility(View.GONE);
        }
    }


    private void fetchConnections() {
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getActivity());
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", otherUserId);
        jsonObject.addProperty("limit", "15");
        jsonObject.addProperty("offset", String.valueOf(peopleSearchArrayList.size()));
//        showProgressDialog();
        apiServiceProvider.getUserConnections(jsonObject, null, this);
    }
    private void fetchMutualConnections() {
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getActivity());
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_one_id", SharedPref.getUserRegistration().getId());
        jsonObject.addProperty("user_two_id", otherUserId);
        jsonObject.addProperty("limit", "15");
        jsonObject.addProperty("offset",  String.valueOf(peopleSearchArrayList.size()));
//        showProgressDialog();
        apiServiceProvider.getMutualConnections(jsonObject, null, this);
    }
}
