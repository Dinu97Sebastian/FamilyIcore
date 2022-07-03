package com.familheey.app.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.familheey.app.Activities.FamilyDashboardActivity;
import com.familheey.app.Activities.LinkFamilyActivity;
import com.familheey.app.Adapters.LinkedFamilyAdapter;
import com.familheey.app.Decorators.BottomAdditionalMarginDecorator;
import com.familheey.app.Interfaces.FamilyDashboardInteractor;
import com.familheey.app.Interfaces.ProgressListener;
import com.familheey.app.Interfaces.RetrofitListener;
import com.familheey.app.Models.ApiCallbackParams;
import com.familheey.app.Models.ErrorData;
import com.familheey.app.Models.Response.Family;
import com.familheey.app.Networking.Retrofit.ApiServiceProvider;
import com.familheey.app.Parsers.FamilyParser;
import com.familheey.app.R;
import com.familheey.app.Utilities.Constants;
import com.familheey.app.Utilities.SharedPref;
import com.familheey.app.Utilities.Utilities;
import com.google.android.material.button.MaterialButton;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.familheey.app.Utilities.Constants.ApiFlags.FETCH_LINKED_FAMILIES;
import static com.familheey.app.Utilities.Constants.Bundle.DATA;
import static com.familheey.app.Utilities.Constants.FamilyDashboardIdentifiers.TypeLinkedFamilyFragment;

public class LinkedFamilyFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, RetrofitListener, LinkedFamilyAdapter.OnLinkedFamilySelectedListener, ProgressListener {

    @BindView(R.id.assosiationCount)
    TextView assosiationCount;
    @BindView(R.id.searchRelationShip)
    EditText searchRelationShip;
    @BindView(R.id.linkedFamilyList)
    RecyclerView linkedFamilyList;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.linkFamilies)
    MaterialButton linkFamilies;
    @BindView(R.id.linkFamiliesFab)
    CardView linkFamiliesFab;
    @BindView(R.id.emptyLinkedFamilyIncluder)
    View emptyLinkedFamilyIncluder;
    @BindView(R.id.labelAssosiation)
    TextView labelAssosiation;
    private Family family;
    private LinkedFamilyAdapter linkFamilyAdapter;
    private final ArrayList<Family> linkedFamilies = new ArrayList<>();
    private SweetAlertDialog progressDialog;
    private FamilyDashboardInteractor familyDashboardInteractor;
    String query = "";
    public LinkedFamilyFragment() {
        // Required empty public constructor
    }

    public static LinkedFamilyFragment newInstance(Family family) {
        LinkedFamilyFragment fragment = new LinkedFamilyFragment();
        Bundle args = new Bundle();
        args.putParcelable(DATA, family);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            family = getArguments().getParcelable(DATA);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_linked_family, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeAdapter();
        swipeRefreshLayout.setOnRefreshListener(this);
       // searchRelationShip.setLines(1);
       // searchRelationShip.setSingleLine(true);
       // searchRelationShip.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
    }

    private void initializeAdapter() {
        linkFamilyAdapter = new LinkedFamilyAdapter(this, this, linkedFamilies);
        linkFamilyAdapter.setAdminStatus(family.canLinkFamily());
        linkedFamilyList.setAdapter(linkFamilyAdapter);
        linkedFamilyList.setLayoutManager(new LinearLayoutManager(getContext()));
        linkedFamilyList.addItemDecoration(new BottomAdditionalMarginDecorator());
        //linkedFamilyList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }
/** modified for search functionality **/
    private void fetchLinkedFamilies() {
        progressBar.setVisibility(View.VISIBLE);
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getContext());
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("group_id", family.getId().toString());
        jsonObject.addProperty("requested_by", SharedPref.getUserRegistration().getId());
        jsonObject.addProperty("query", query.trim());
        apiServiceProvider.fetchLinkedFamilies(jsonObject, null, this);
    }


    @Override
    public void onRefresh() {
        fetchLinkedFamilies();
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {
        swipeRefreshLayout.setRefreshing(false);
        progressBar.setVisibility(View.GONE);
        linkedFamilies.clear();
        linkedFamilies.addAll(FamilyParser.parseLinkedFamilies(responseBodyString));
        linkFamilyAdapter.notifyDataSetChanged();
        assosiationCount.setText(String.valueOf(linkedFamilies.size()));
        if (linkedFamilies.size() > 0) {
            assosiationCount.setVisibility(View.GONE);
            labelAssosiation.setVisibility(View.GONE);
            swipeRefreshLayout.setVisibility(View.VISIBLE);
            emptyLinkedFamilyIncluder.setVisibility(View.GONE);
            /*if (familyDashboardInteractor != null)
                familyDashboardInteractor.onFamilyAddComponentVisible(TypeLinkedFamilyFragment);*/
            /**05-09-21**/
            requireActivity().findViewById(R.id.addComponent).setVisibility(View.VISIBLE);
        } else {
            assosiationCount.setVisibility(View.GONE);
            labelAssosiation.setVisibility(View.GONE);
            swipeRefreshLayout.setVisibility(View.GONE);
            emptyLinkedFamilyIncluder.setVisibility(View.VISIBLE);
            /*if (familyDashboardInteractor != null)
                familyDashboardInteractor.onFamilyAddComponentHidden(TypeLinkedFamilyFragment);*/
            /**05-09-21**/
            requireActivity().findViewById(R.id.addComponent).setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
        progressBar.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);
        Toast.makeText(getContext(), Constants.SOMETHING_WRONG, Toast.LENGTH_SHORT).show();
    }
/**05-09-2021 removed R.id.linkFamiliesFab with  **/
    @OnClick({R.id.linkFamilies})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.linkFamilies:
                if (!family.canLinkFamily()) {
                    Toast.makeText(getContext(), "You don't have permission to Link Families!!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (family.getIsLinkable() != null && family.getIsLinkable()) {
                    Intent intent = new Intent(getContext(), LinkFamilyActivity.class);
                    intent.putExtra(DATA, family);
                    startActivity(intent);
                } else {
                    Toast.makeText(getContext(), "This family cannot be linked!!", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
public void checkFamilyLinking(){
    if (!family.canLinkFamily()) {
        Toast.makeText(getContext(), "You don't have permission to Link Families!!", Toast.LENGTH_SHORT).show();
        return;
    }
    if (family.getIsLinkable() != null && family.getIsLinkable()) {
        Intent intent = new Intent(getContext(), LinkFamilyActivity.class);
        intent.putExtra(DATA, family);
        startActivity(intent);
    } else {
        Toast.makeText(getContext(), "This family cannot be linked!!", Toast.LENGTH_SHORT).show();
    }
}
    @Override
    public void onResume() {
        super.onResume();
        fetchLinkedFamilies();
        if (linkedFamilies != null && linkedFamilies.size() > 0) {
           /* if (familyDashboardInteractor != null)
                familyDashboardInteractor.onFamilyAddComponentVisible(TypeLinkedFamilyFragment);*/
            /**05-09-21**/
                requireActivity().findViewById(R.id.addComponent).setVisibility(View.VISIBLE);
        } else {
            /*if (familyDashboardInteractor != null)
                familyDashboardInteractor.onFamilyAddComponentHidden(TypeLinkedFamilyFragment);*/
            /**05-09-21**/
                requireActivity().findViewById(R.id.addComponent).setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onLinkedFamilySelected(Family selectedFamily) {
        Intent intent = new Intent(getContext(), FamilyDashboardActivity.class);
        Family intentFamily = new Family();
        if (selectedFamily.getToGroup().equals(family.getId())) {
            intentFamily.setId(selectedFamily.getFromGroup());
        } else
            intentFamily.setId(selectedFamily.getToGroup());
        intent.putExtra(DATA, intentFamily);
        startActivity(intent);
    }

    @Override
    public void onFamilyLinkingStatusChanged(Family family) {
        for (int i = 0; i < linkedFamilies.size(); i++) {
            if (linkedFamilies.get(i).getId().equals(family.getId())) {
                linkedFamilies.set(i, family);
                linkFamilyAdapter.notifyDataSetChanged();
                return;
            }
        }
    }

    @Override
    public void showProgressDialog() {
        progressDialog = Utilities.getProgressDialog(getContext());
        progressDialog.show();
    }

    @Override
    public void hideProgressDialog() {
        if (progressDialog != null)
            progressDialog.dismiss();
    }

    @Override
    public void showErrorDialog(String errorMessage) {
        if (progressDialog == null) {
            progressDialog = Utilities.getErrorDialog(getContext(), errorMessage);
            progressDialog.show();
            return;
        }
        Utilities.getErrorDialog(progressDialog, errorMessage);
    }

    @OnEditorAction(R.id.searchRelationShip)
    protected boolean onSearchQueryListener(int actionId) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            clearAndReload();
            return true;
        }
        return false;
    }

    private void clearAndReload() {
        JsonObject baseJson = new JsonObject();
        JsonArray data = new JsonArray();
        baseJson.add("data", data);
        onResponseSuccess(baseJson.toString(), null, FETCH_LINKED_FAMILIES);
        fetchLinkedFamilies();
    }

    public void updateFamily(Family family) {
        this.family = family;
        linkFamilyAdapter.setAdminStatus(family.canLinkFamily());
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        familyDashboardInteractor = Utilities.getListener(getParentFragment(), FamilyDashboardInteractor.class);
        if (familyDashboardInteractor == null)
            throw new RuntimeException("The parent fragment must implement FamilyDashboardInteractor");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        familyDashboardInteractor = null;
    }

    public void startSearch(String query) {
        this.query=query;
        clearAndReload();
    }
}
