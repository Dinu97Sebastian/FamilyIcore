package com.familheey.app.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.familheey.app.Adapters.FamilyAddMembersPagerAdapter;
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
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.familheey.app.Utilities.Constants.ApiPaths.FIREBASE_DATABASE_URL;
import static com.familheey.app.Utilities.Constants.Bundle.DATA;
import static com.familheey.app.Utilities.Constants.Bundle.INVITE;
import static com.familheey.app.Utilities.Constants.Bundle.NOTIFICATION_ID;
import static com.familheey.app.Utilities.Constants.Bundle.TYPE;

public class FamilyAddMemberActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener, RetrofitListener, ProgressListener {

    public static final int REQUEST_CODE = 1;
    @BindView(R.id.addMembersViewPager)
    androidx.viewpager.widget.ViewPager addMembersViewPager;
    @BindView(R.id.addMembersTab)
    com.google.android.material.tabs.TabLayout addMembersTab;

    private Family family;
    private SweetAlertDialog progressDialog;
    private String notificationId = "";
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_add_member);
        ButterKnife.bind(this);

        if (getIntent() != null && getIntent().hasExtra(TYPE)) {
            String eventId = getIntent().getStringExtra(DATA);
            getFamilyDetails(eventId);

            // Modified By: Dinu(12/10/2021) For update visible_status="read" in firebase
            if (getIntent().hasExtra(NOTIFICATION_ID)) {
                notificationId=getIntent().getStringExtra(NOTIFICATION_ID);
                database= FirebaseDatabase.getInstance(FIREBASE_DATABASE_URL).getReference(Constants.ApiPaths.FIREBASE_USER_TYPE + SharedPref.getUserRegistration().getId() + "_notification");
                database.child(notificationId).child("visible_status").setValue("read");
            }
        }
        else{
            family = getIntent().getParcelableExtra(DATA);
            initializeTabs();
            initializeAdapter();
        }
        Objects.requireNonNull(getIntent().getExtras()).clear();

    }

    private void initializeTabs() {
        addMembersTab.addTab(addMembersTab.newTab().setText("PEOPLE"));
        addMembersTab.addTab(addMembersTab.newTab().setText("OTHER CONTACTS"));
    }

    private void initializeAdapter() {
        FamilyAddMembersPagerAdapter familyAddMembersPagerAdapter = new FamilyAddMembersPagerAdapter(family, getSupportFragmentManager(), addMembersTab.getTabCount());
        addMembersViewPager.setAdapter(familyAddMembersPagerAdapter);
        addMembersViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(addMembersTab));
        addMembersTab.addOnTabSelectedListener(this);
        addMembersViewPager.setOffscreenPageLimit(addMembersTab.getTabCount());
    }


    @OnClick({R.id.btn_back})
    public void onClick(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        addMembersViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

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

    public void getFamilyDetails(String eventId) {
        JsonObject jsonObject = new JsonObject();
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getApplicationContext());
        jsonObject.addProperty("group_id", eventId);
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        apiServiceProvider.getFamilyDetailsByID(jsonObject, null, this);
    }

    @Override
    public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {

        try {
            family = FamilyParser.parseLinkedFamilies(responseBodyString).get(0);
            initializeTabs();
            initializeAdapter();
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {

    }
}
