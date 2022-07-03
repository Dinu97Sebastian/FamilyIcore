package com.familheey.app.Fragments.FamilyDashboard;

import android.content.Context;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.familheey.app.Adapters.BlockedUserAdapter;
import com.familheey.app.CustomViews.TextViews.SemiBoldTextView;
import com.familheey.app.Decorators.BottomAdditionalMarginDecorator;
import com.familheey.app.Interfaces.FamilyDashboardListener;
import com.familheey.app.Interfaces.RetrofitListener;
import com.familheey.app.Models.ApiCallbackParams;
import com.familheey.app.Models.ErrorData;
import com.familheey.app.Models.Response.Family;
import com.familheey.app.Models.Response.FamilyMember;
import com.familheey.app.Networking.Retrofit.ApiServiceProvider;
import com.familheey.app.Parsers.FamilyParser;
import com.familheey.app.R;
import com.familheey.app.Utilities.SharedPref;
import com.familheey.app.Utilities.Utilities;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnEditorAction;

import static com.familheey.app.Utilities.Constants.Bundle.DATA;
import static com.familheey.app.Utilities.Constants.Bundle.PERMISSION;

public class BlockedUsersFragment extends Fragment implements BlockedUserAdapter.OnUserUnblockedListener, RetrofitListener, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.searchMembers)
    EditText searchMembers;
    @BindView(R.id.blockedUsersList)
    RecyclerView blockedUsersList;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.emptyIndicatorContainer)
    ConstraintLayout emptyIndicatorContainer;
    @BindView(R.id.clearSearch)
    ImageView clearSearch;
    @BindView(R.id.emptyResultText)
    SemiBoldTextView emptyResultText;

    private ArrayList<FamilyMember> blockedFamilyMembers = new ArrayList<>();
    private FamilyDashboardListener mListener;
    private BlockedUserAdapter blockedUserAdapter;
    private Family family;
    private boolean hasBlockedAnyUser = false;

    public BlockedUsersFragment() {
        // Required empty public constructor
    }

    public static BlockedUsersFragment newInstance(Family family) {
        BlockedUsersFragment fragment = new BlockedUsersFragment();
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
        View view = inflater.inflate(R.layout.fragment_blocked_users, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        swipeRefreshLayout.setOnRefreshListener(this);
        initializeAdapter();
        fetchBlockedUsers();
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
            fetchBlockedUsers();
        });
    }

    private void initializeAdapter() {
        blockedUserAdapter = new BlockedUserAdapter(this, blockedFamilyMembers);
        blockedUsersList.setAdapter(blockedUserAdapter);
        blockedUsersList.setLayoutManager(new LinearLayoutManager(getContext()));
        blockedUsersList.addItemDecoration(new BottomAdditionalMarginDecorator());
        //blockedUsersList.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
    }

    private void fetchBlockedUsers() {
        progressBar.setVisibility(View.VISIBLE);
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getContext());
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        jsonObject.addProperty("type", "groups");
        jsonObject.addProperty("group_id", family.getId().toString());
        jsonObject.addProperty("query", searchMembers.getText().toString());
        apiServiceProvider.getBlockedUsers(jsonObject, null, this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = Utilities.getListener(this, FamilyDashboardListener.class);
        if (mListener == null) {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
/**modified for updating**/
    @Override
    public void onUserUnblocked(FamilyMember user) {
        hasBlockedAnyUser = true;
        fetchBlockedUsers();
    }

    @Override
    public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {
        progressBar.setVisibility(View.GONE);
        blockedFamilyMembers.clear();
        blockedFamilyMembers.addAll(FamilyParser.parseFamilyMembers(responseBodyString));
        blockedUserAdapter.notifyDataSetChanged();
        if (blockedFamilyMembers.size() > 0) {
            emptyIndicatorContainer.setVisibility(View.INVISIBLE);
            emptyResultText.setVisibility(View.INVISIBLE);
            searchMembers.setVisibility(View.VISIBLE);
            if (searchMembers.getText().toString().length() > 0)
                clearSearch.setVisibility(View.VISIBLE);
            else clearSearch.setVisibility(View.INVISIBLE);
            swipeRefreshLayout.setVisibility(View.VISIBLE);
        } else {
            if (searchMembers.getText().toString().length() > 0) {
                emptyIndicatorContainer.setVisibility(View.INVISIBLE);
                emptyResultText.setVisibility(View.VISIBLE);
                searchMembers.setVisibility(View.VISIBLE);
                clearSearch.setVisibility(View.VISIBLE);
            } else {
                emptyIndicatorContainer.setVisibility(View.VISIBLE);
                emptyResultText.setVisibility(View.INVISIBLE);
                searchMembers.setVisibility(View.INVISIBLE);
                clearSearch.setVisibility(View.INVISIBLE);
            }
            swipeRefreshLayout.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
        Toast.makeText(getContext(), errorData.getMessage(), Toast.LENGTH_SHORT).show();
        Toast.makeText(getContext(), "Swipe downwards to refresh", Toast.LENGTH_SHORT).show();
        progressBar.setVisibility(View.GONE);

    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(false);
        fetchBlockedUsers();
    }

    @OnEditorAction(R.id.searchMembers)
    protected boolean onSearchQueryListener(int actionId) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            fetchBlockedUsers();
            return true;
        }
        return false;
    }

    public void updateFamily(Family family) {
        this.family = family;
    }
}
