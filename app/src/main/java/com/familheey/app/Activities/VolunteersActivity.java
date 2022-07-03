package com.familheey.app.Activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.familheey.app.Adapters.VolunteersAdapter;
import com.familheey.app.Fragments.Events.EventSignupDialog;
import com.familheey.app.Interfaces.ProgressListener;
import com.familheey.app.Interfaces.RetrofitListener;
import com.familheey.app.Models.ApiCallbackParams;
import com.familheey.app.Models.ErrorData;
import com.familheey.app.Models.Response.EventSignUp;
import com.familheey.app.Models.Response.SignUpContributor;
import com.familheey.app.Networking.Retrofit.ApiServiceProvider;
import com.familheey.app.Parsers.EventsParser;
import com.familheey.app.R;
import com.familheey.app.Utilities.Utilities;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.familheey.app.Utilities.Constants.Bundle.DATA;
import static com.familheey.app.Utilities.Constants.Bundle.IS_ADMIN;

public class VolunteersActivity extends AppCompatActivity implements RetrofitListener, ProgressListener, VolunteersAdapter.OnSignUpVolunteersChangedListener, EventSignupDialog.OnSignupUpdatedListener {

    @BindView(R.id.toolBarTitle)
    TextView toolBarTitle;
    @BindView(R.id.searchMembers)
    EditText searchMembers;
    @BindView(R.id.volunteersList)
    RecyclerView volunteersList;
    @BindView(R.id.progressListMember)
    ProgressBar progressBar;
    @BindView(R.id.clearSearch)
    ImageView clearSearch;

    @BindView(R.id.llEmpty)
    LinearLayout llEmpty;
    private boolean isAdmin = false;

    private EventSignUp eventSignUp;
    private SweetAlertDialog progressDialog;
    private VolunteersAdapter volunteersAdapter;
    private final List<SignUpContributor> signUpContributors = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteers);
        eventSignUp = getIntent().getParcelableExtra(DATA);
        isAdmin = getIntent().getBooleanExtra(IS_ADMIN, false);
        ButterKnife.bind(this);
        initializeToolbar();
        initializeAdapter();
        fetchVolunteers();
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
            //onSearchQueryListener(EditorInfo.IME_ACTION_SEARCH);
        });
    }

    private void fetchVolunteers() {
        showProgressDialog();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("event_id", String.valueOf(eventSignUp.getEventId()));
        jsonObject.addProperty("item_id", String.valueOf(eventSignUp.getId()));
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(this);
        apiServiceProvider.fetchEventSignUpContributors(jsonObject, null, this);
    }

    private void initializeAdapter() {
        volunteersAdapter = new VolunteersAdapter(this, signUpContributors, isAdmin);
        volunteersList.setAdapter(volunteersAdapter);
        volunteersList.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));
        volunteersList.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initializeToolbar() {
        toolBarTitle.setText("View Sign Ups");
    }

    @OnClick(R.id.goBack)
    public void onClick() {
        onBackPressed();
    }

    @Override
    public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {
        hideProgressDialog();
        signUpContributors.clear();
        signUpContributors.addAll(EventsParser.parseSignUpContributors(responseBodyString));


        if (signUpContributors.size()==0){
            llEmpty.setVisibility(View.VISIBLE);
        }else {
            llEmpty.setVisibility(View.GONE);

        }
        volunteersAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
        showErrorDialog(errorData.getMessage());
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
    public void onSignUpVolunteerQuantityUpdateRequested(SignUpContributor signUpContributor) {
        EventSignupDialog.newInstance(signUpContributor).show(getSupportFragmentManager(), "EventSignupDialog");
    }

    @Override
    public void onSignUpAdded(EventSignUp eventSignUp) {
        fetchVolunteers();
    }

    @Override
    public void onSignUpUpdated(SignUpContributor signUpContributor) {
        fetchVolunteers();
    }
}
