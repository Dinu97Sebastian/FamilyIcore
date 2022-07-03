package com.familheey.app.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.familheey.app.Adapters.GroupInvitationPagerAdapter;
import com.familheey.app.Fragments.Events.FamilyGuestListingFragment;
import com.familheey.app.Fragments.Events.OtherGuestListingFragment;
import com.familheey.app.Fragments.Events.UserGuestListingFragment;
import com.familheey.app.Interfaces.ProgressListener;
import com.familheey.app.Interfaces.RetrofitListener;
import com.familheey.app.Models.ApiCallbackParams;
import com.familheey.app.Models.ErrorData;
import com.familheey.app.Models.InvitedOther;
import com.familheey.app.Models.Response.EventDetail;
import com.familheey.app.Models.Response.InvitedFamiliy;
import com.familheey.app.Models.Response.InvitedUser;
import com.familheey.app.Networking.Retrofit.ApiServiceProvider;
import com.familheey.app.Networking.utils.GsonUtils;
import com.familheey.app.R;
import com.familheey.app.Utilities.Constants;
import com.familheey.app.Utilities.SharedPref;
import com.familheey.app.Utilities.Utilities;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.familheey.app.Utilities.Constants.Bundle.DATA;

public class EventInvitationsListingActivity extends AppCompatActivity implements ProgressListener, TabLayout.OnTabSelectedListener {

    @BindView(R.id.goBack)
    ImageView goBack;
    @BindView(R.id.toolBarTitle)
    TextView toolBarTitle;
    @BindView(R.id.invitedTab)
    TabLayout invitedTab;
    @BindView(R.id.pendingViewPager)
    ViewPager pendingViewPager;
    @BindView(R.id.searchText)
    EditText searchText;
    @BindView(R.id.clearSearch)
    ImageView clearSearch;

    @BindView(R.id.progressBarGuests)
    ProgressBar progressBarGuests;


    private GroupInvitationPagerAdapter groupInvitationPagerAdapter;
    private SweetAlertDialog progressDialog;
    private EventDetail eventDetail;

    private ArrayList<InvitedFamiliy> family = new ArrayList<>();
    private ArrayList<InvitedUser> people = new ArrayList<>();
    private ArrayList<InvitedOther> others = new ArrayList<>();
    String eventID = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_invitation_listing);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            if (extras.containsKey(Constants.Bundle.DATA)) {
                eventID = extras.getString(Constants.Bundle.DATA, "");
            }
        }

        initializeToolbar();
        initializeTabs();
        fetchPendingGuestsToAcceptInvitation();
        initializeSearchClearCallback();
    }

    private void initializeSearchClearCallback() {
        searchText.addTextChangedListener(new TextWatcher() {

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
            searchText.setText("");
            onSearchQueryListener(EditorInfo.IME_ACTION_SEARCH);
        });
    }

    private void initializeToolbar() {
        toolBarTitle.setText("Guests");
    }

    @OnEditorAction(R.id.searchText)
    protected boolean onSearchQueryListener(int actionId) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            fetchPendingGuestsToAcceptInvitation();
            return true;
        }
        return false;
    }

    private void initializeTabs() {
        invitedTab.addTab(invitedTab.newTab().setText("Family"));
        invitedTab.addTab(invitedTab.newTab().setText("People"));
        invitedTab.addTab(invitedTab.newTab().setText("Others"));

        groupInvitationPagerAdapter = new GroupInvitationPagerAdapter(getSupportFragmentManager(), invitedTab.getTabCount());
        pendingViewPager.setAdapter(groupInvitationPagerAdapter);
        pendingViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(invitedTab));
        invitedTab.addOnTabSelectedListener(this);
        pendingViewPager.setOffscreenPageLimit(invitedTab.getTabCount());
    }

    private void fetchPendingGuestsToAcceptInvitation() {
        showProgress();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        jsonObject.addProperty("event_id", eventID);
        jsonObject.addProperty("searchText", searchText.getText().toString());
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getApplicationContext());
        apiServiceProvider.getInvitedGuestList(jsonObject, null, new RetrofitListener() {
            @Override
            public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {
                try {
                    JSONObject rootJson = new JSONObject(responseBodyString);
                    JSONObject data = rootJson.getJSONObject("data");
                    family.clear();
                    people.clear();
                    others.clear();
                    family.addAll(GsonUtils.getInstance().getGson().fromJson(data.getJSONArray("groups").toString(), new TypeToken<ArrayList<InvitedFamiliy>>() {
                    }.getType()));
                    people.addAll(GsonUtils.getInstance().getGson().fromJson(data.getJSONArray("users").toString(), new TypeToken<ArrayList<InvitedUser>>() {
                    }.getType()));
                    others.addAll(GsonUtils.getInstance().getGson().fromJson(data.getJSONArray("others").toString(), new TypeToken<ArrayList<InvitedOther>>() {
                    }.getType()));


                    UserGuestListingFragment userGuestListingFragment = (UserGuestListingFragment) groupInvitationPagerAdapter.getRegisteredFragment(1);
                    if (userGuestListingFragment != null)
                        userGuestListingFragment.updateInvitations(people);

                    FamilyGuestListingFragment familyGuestListingFragment = (FamilyGuestListingFragment) groupInvitationPagerAdapter.getRegisteredFragment(0);
                    if (familyGuestListingFragment != null)
                        familyGuestListingFragment.updateInvitations(family);

                    OtherGuestListingFragment othersFragment = (OtherGuestListingFragment) groupInvitationPagerAdapter.getRegisteredFragment(2);
                    if (othersFragment != null) othersFragment.updateInvitations(others);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                hideProgress();
            }

            @Override
            public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
                hideProgress();
            }
        });
    }

    private void showProgress() {
        if (progressBarGuests!=null){
            progressBarGuests.setVisibility(View.VISIBLE);
        }
    }

    private void hideProgress() {
        if (progressBarGuests!=null){
            progressBarGuests.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.goBack)
    public void onClick() {
        finish();
    }

    @Override
    public void showProgressDialog() {
        progressDialog = Utilities.getProgressDialog(this);
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
            progressDialog = Utilities.getErrorDialog(this, errorMessage);
            progressDialog.show();
            return;
        }
        Utilities.getErrorDialog(progressDialog, errorMessage);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        pendingViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}
