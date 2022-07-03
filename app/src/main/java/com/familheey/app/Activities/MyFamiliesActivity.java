package com.familheey.app.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.familheey.app.Adapters.FamilyMemberJoiningAdapter;
import com.familheey.app.CustomViews.TextViews.SemiBoldTextView;
import com.familheey.app.Interfaces.RetrofitListener;
import com.familheey.app.Models.ApiCallbackParams;
import com.familheey.app.Models.ErrorData;
import com.familheey.app.Models.Response.Family;
import com.familheey.app.Models.Response.PeopleSearchModal;
import com.familheey.app.Networking.Retrofit.ApiServiceProvider;
import com.familheey.app.Parsers.FamilyParser;
import com.familheey.app.R;
import com.familheey.app.Utilities.Constants;
import com.familheey.app.Utilities.SharedPref;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;

public class MyFamiliesActivity extends AppCompatActivity implements RetrofitListener {
    private PeopleSearchModal user;
    private FamilyMemberJoiningAdapter familyMemberJoiningAdapter;
    @BindView(R.id.familyList)
    RecyclerView familyList;
    @BindView(R.id.progressBar3)
    ProgressBar progressBar;
    @BindView(R.id.clearSearch)
    ImageView clearSearch;
    @BindView(R.id.search)
    EditText search;
    @BindView(R.id.emptyIndicator)
    SemiBoldTextView emptyIndicator;

    private List<Family> families = new ArrayList<>();
    //private ProgressListener mListener;

    /*public MyFamiliesActivity(ProgressListener mListener) {
        this.mListener = mListener;
    }
*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_families);
        ButterKnife.bind(this);
        if (getIntent().hasExtra(Constants.Bundle.DATA)) {
            user = getIntent().getParcelableExtra(Constants.Bundle.DATA);
        }

        initListener();
        initializeAdapter();
        fetchCreatedFamilies("");

    }

    @OnEditorAction(R.id.search)
    protected boolean onSearchQueryListener(int actionId) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            fetchCreatedFamilies(search.getText().toString());
            return true;
        }
        return false;
    }

    private void initListener() {
        search.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int st, int b, int c) {
                if (s.length() == 0)
                    clearSearch.setVisibility(View.INVISIBLE);
                else clearSearch.setVisibility(View.VISIBLE);

            }

            @Override
            public void beforeTextChanged(CharSequence s, int st, int c, int a) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        clearSearch.setOnClickListener(v -> {
            search.setText("");
            onSearchQueryListener(EditorInfo.IME_ACTION_SEARCH);
        });
    }

    private void initializeAdapter() {
        familyMemberJoiningAdapter = new FamilyMemberJoiningAdapter(this, families, user.getId().toString(), SharedPref.getUserRegistration().getId());
        familyList.setAdapter(familyMemberJoiningAdapter);
        familyList.setLayoutManager(new LinearLayoutManager(this));
    }

    private void fetchCreatedFamilies(String q) {
        progressBar.setVisibility(View.VISIBLE);
        JsonObject lisFamily = new JsonObject();
        lisFamily.addProperty("user_id", SharedPref.getUserRegistration().getId());
        lisFamily.addProperty("member_to_add", user.getId().toString());
        if (!q.isEmpty()) {
            lisFamily.addProperty("query", q);
        }
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(this);
        apiServiceProvider.getAllGroupsBasedOnUserId(lisFamily, null, this);
    }

    @Override
    public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {
        families.clear();
        families.addAll(FamilyParser.parseLinkedFamilies(responseBodyString));
        if (search.getText().toString().trim().length() > 0) {
            emptyIndicator.setText("No families found");
        } else {
            emptyIndicator.setText("You should be part of a family to add someone");
        }
        if (families != null && families.size() > 0) {
            emptyIndicator.setVisibility(View.INVISIBLE);
        } else
            emptyIndicator.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        familyMemberJoiningAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
        progressBar.setVisibility(View.INVISIBLE);
        //mListener.showErrorDialog(errorData.getMessage());
    }

    @OnClick(R.id.close)
    public void onViewClicked() {
        performCallback();
    }


    void performCallback() {
        Bundle bundle = new Bundle();
        Intent mIntent = new Intent();
        bundle.putString(Constants.Bundle.DATA, "WTF");
        mIntent.putExtras(bundle);
        setResult(Activity.RESULT_OK, mIntent);
        finish();
    }
}
