package com.familheey.app.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.familheey.app.Activities.FamilyDashboardActivity;
import com.familheey.app.Adapters.FamilySearchAdapter;
import com.familheey.app.CustomViews.TextViews.SemiBoldTextView;
import com.familheey.app.Interfaces.GlobalSearchListener;
import com.familheey.app.Interfaces.ProgressListener;
import com.familheey.app.Interfaces.RetrofitListener;
import com.familheey.app.Models.ApiCallbackParams;
import com.familheey.app.Models.ErrorData;
import com.familheey.app.Models.Response.Family;
import com.familheey.app.Models.Response.FamilySearchModal;
import com.familheey.app.Networking.Retrofit.ApiServiceProvider;
import com.familheey.app.Parsers.FamilyParser;
import com.familheey.app.R;
import com.familheey.app.Utilities.Constants;
import com.familheey.app.Utilities.SharedPref;
import com.familheey.app.Utilities.Utilities;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.familheey.app.Utilities.Constants.ApiFlags.SEARCH_DATA;
import static com.familheey.app.Utilities.Constants.Bundle.DATA;
import static com.familheey.app.Utilities.Constants.Bundle.GLOBAL_SEARCH;
import static com.familheey.app.Utilities.Constants.Bundle.TYPE;

public class SearchFamilyFragment extends Fragment implements RetrofitListener, FamilySearchAdapter.OnFamilyJoiningStatusChanged, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.searchLabelIndicator)
    TextView searchLabelIndicator;
    @BindView(R.id.emptyResultText)
    SemiBoldTextView emptyResultText;
    @BindView(R.id.refresh)
    SwipeRefreshLayout refresh;
    @BindView(R.id.shimmer_view_container)
    ShimmerFrameLayout shimmer_view_container;
    private FamilySearchAdapter familySearchAdapter;
    private ProgressListener mListener;
    //    @BindView(R.id.progressBar)
//    ProgressBar progressBar;
    private String query = "";
    private final boolean isLoading = false;
    private GlobalSearchListener globalSearchListener;
    private String data = "";

    public static SearchFamilyFragment newInstance() {
        return new SearchFamilyFragment();
    }

    ArrayList<FamilySearchModal> familySearchArrayList = new ArrayList<>();
    public static final int DASHBOARD_REQUEST_CODE = 6458;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_result_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initAdapter();
        initListener();
        if(data.isEmpty()){
        if (shimmer_view_container != null) {
            shimmer_view_container.setVisibility(View.VISIBLE);
            shimmer_view_container.startShimmer();
        }}
        else
            dataParse(data);
    }

    private void initAdapter() {
        familySearchAdapter = new FamilySearchAdapter(getContext(), this, familySearchArrayList);
        recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerview.setAdapter(familySearchAdapter);
    }

    private void initListener() {
        refresh.setOnRefreshListener(() -> {
            refresh.setRefreshing(false);
            data = "";
            saveToEventSuggestion("");
            clearDatas();
            if (shimmer_view_container != null) {
                shimmer_view_container.setVisibility(View.VISIBLE);
                shimmer_view_container.startShimmer();
            }
            globalSearchListener.fetchFamilies();
        });
    }

    /*public void fetchFamilies(String query) {
        this.query = query;
         data = SharedPref.read(SharedPref.EVENT_SUGGESTION, "");

        if(this.query==null||this.query.isEmpty()&&data.isEmpty()) {
            loadDataFromApi();
        }
        else if (this.query!=null||!this.query.isEmpty()){
            loadDataFromApi();
        }

        else{
            if(emptyResultText!=null)
             dataParse(data);
        }
    }*/

    private void loadDataFromApi() {
        if (shimmer_view_container != null) {
            shimmer_view_container.setVisibility(View.VISIBLE);
            shimmer_view_container.startShimmer();
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("searchtxt", this.query);
        jsonObject.addProperty("userid", SharedPref.getUserRegistration().getId());
        jsonObject.addProperty("type", "family");
        jsonObject.addProperty("offset", "0");
        jsonObject.addProperty("limit", "10000");
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getActivity());
        apiServiceProvider.searchData(jsonObject, null, this);
    }

    private void clearDatas() {
        familySearchArrayList.clear();
        familySearchAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = Utilities.getListener(this, ProgressListener.class);
        if (mListener == null)
            throw new RuntimeException(context.getClass().getSimpleName() + " must implement HomeInteractor");
        globalSearchListener = Utilities.getListener(this, GlobalSearchListener.class);
        if (globalSearchListener == null)
            throw new RuntimeException(context.getClass().getSimpleName() + " must implement GlobalSearchListener");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        globalSearchListener = null;
    }


    @Override
    public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {

        switch (apiFlag) {
            case SEARCH_DATA:
                dataParse(responseBodyString);
                break;
            default:
                if (shimmer_view_container != null) {
                    shimmer_view_container.stopShimmer();
                    shimmer_view_container.setVisibility(View.GONE);
                }
                mListener.hideProgressDialog();
                familySearchArrayList.set(apiCallbackParams.getPosition(), apiCallbackParams.getGlobalSearchFamily());
                try {
                    String status = new JSONObject(responseBodyString).getJSONObject("data").getString("status");
                    //mListener.loadGlobalSearch();
                    if (status.equalsIgnoreCase("Pending")) {
                        familySearchArrayList.get(apiCallbackParams.getPosition()).setIsJoined(null);
                        familySearchArrayList.get(apiCallbackParams.getPosition()).setMemberJoining(null);
                        familySearchArrayList.get(apiCallbackParams.getPosition()).setStatus("pending");
                    } else {
                        familySearchArrayList.get(apiCallbackParams.getPosition()).setIsJoined("1");
                        familySearchArrayList.get(apiCallbackParams.getPosition()).setIsRemoved(null);
                        familySearchArrayList.get(apiCallbackParams.getPosition()).setStatus("Member");
                    }
                    familySearchAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
        if (mListener != null)
            mListener.showErrorDialog(Constants.SOMETHING_WRONG);

        if (shimmer_view_container != null) {
            shimmer_view_container.stopShimmer();
            shimmer_view_container.setVisibility(View.GONE);
        }
        if (apiFlag == SEARCH_DATA)
            emptyResultText.setVisibility(View.VISIBLE);

    }

    @Override
    public void onFamilySelected(FamilySearchModal familySearchModal) {
        Family family = new Family();
        family.setId(familySearchModal.getGroupId());
        Intent intent = new Intent(getContext(), FamilyDashboardActivity.class);
        intent.putExtra(DATA, family.getId().toString());
        intent.putExtra(TYPE, GLOBAL_SEARCH);
        startActivityForResult(intent, DASHBOARD_REQUEST_CODE);
    }

    @Override
    public void onFamilyJoinRequested(FamilySearchModal family, int position) {
        mListener.showProgressDialog();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        jsonObject.addProperty("group_id", "" + family.getGroupId());
        ApiCallbackParams apiCallbackParams = new ApiCallbackParams();
        apiCallbackParams.setGlobalSearchFamily(family);
        apiCallbackParams.setPosition(position);
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getActivity());
        apiServiceProvider.joinFamily(jsonObject, apiCallbackParams, this);
    }

    public void updateSearchIndication(String searchText) {
        if (searchLabelIndicator == null) return;
        if (searchText.length() == 0) {
            searchLabelIndicator.setText("Suggested");
        } else {
            searchLabelIndicator.setText("Showing results for \"" + searchText + "\"");
        }
        query = searchText;
        familySearchArrayList.clear();
        if (familySearchAdapter != null)
            familySearchAdapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 6458) {
            if (resultCode == Activity.RESULT_OK) {
                globalSearchListener.reloadFamilySearch();
            }
        }
    }

    @Override
    public void onRefresh() {

    }

    private void saveToEventSuggestion(String response) {
        //if (query == null || query.isEmpty())
        //  SharedPref.write(SharedPref.EVENT_SUGGESTION, response);
    }

    private void dataParse(String responseBodyString) {
        try {
            JSONObject mainObject = new JSONObject(responseBodyString);
            JSONArray familyArray = mainObject.getJSONArray("groups");
            saveToEventSuggestion(responseBodyString);
            familySearchArrayList.clear();
            familySearchArrayList.addAll(FamilyParser.parseSearchedFamily(familyArray));

            if (familySearchArrayList.size() == 0)
                emptyResultText.setVisibility(View.VISIBLE);
            else
                emptyResultText.setVisibility(View.INVISIBLE);
            familySearchAdapter.notifyDataSetChanged();
            if (shimmer_view_container != null) {
                shimmer_view_container.stopShimmer();
                shimmer_view_container.setVisibility(View.GONE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
