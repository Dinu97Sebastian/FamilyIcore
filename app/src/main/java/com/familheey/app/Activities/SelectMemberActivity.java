package com.familheey.app.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.familheey.app.Adapters.SelectMemberAdapter;
import com.familheey.app.Interfaces.RetrofitListener;
import com.familheey.app.Models.ApiCallbackParams;
import com.familheey.app.Models.ErrorData;
import com.familheey.app.Models.Response.FamilyMember;
import com.familheey.app.Networking.Retrofit.ApiServiceProvider;
import com.familheey.app.Parsers.FamilyParser;
import com.familheey.app.R;
import com.familheey.app.Utilities.SharedPref;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;

import static com.familheey.app.Utilities.Constants.Bundle.DATA;
import static com.familheey.app.Utilities.Constants.Bundle.MEMBER;

public class SelectMemberActivity extends AppCompatActivity {

    public static final int REQUEST_CODE = 128;

    @BindView(R.id.searchMembers)
    EditText searchMembers;
    @BindView(R.id.membersList)
    RecyclerView membersList;
    @BindView(R.id.progressBar3)
    ProgressBar progressBar;
    @BindView(R.id.done)
    MaterialButton done;
    @BindView(R.id.toolBarTitle)
    TextView toolBarTitle;
    @BindView(R.id.goBack)
    ImageView goBack;
    @BindView(R.id.clearSearch)
    ImageView clearSearch;

    private SelectMemberAdapter selectMemberAdapter;
    private List<FamilyMember> familyMembers = new ArrayList<>();
    private List<Integer> selectedFamilyMembers = new ArrayList<>();
    private String groupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_member);
        ButterKnife.bind(this);
        groupId = getIntent().getStringExtra(DATA);
        selectedFamilyMembers = getIntent().getIntegerArrayListExtra(MEMBER);
        initializeToolbar();
        initializeAdapter();
        requestFamilyMembers();
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

    private void initializeToolbar() {
        toolBarTitle.setText("Select Members");
        goBack.setOnClickListener(v -> finish());
    }

    private void initializeAdapter() {
        selectMemberAdapter = new SelectMemberAdapter(getApplicationContext(), familyMembers);
        membersList.setAdapter(selectMemberAdapter);
        membersList.setLayoutManager(new LinearLayoutManager(this));
    }

    private void requestFamilyMembers() {
        progressBar.setVisibility(View.VISIBLE);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("group_id", groupId);
        jsonObject.addProperty("crnt_user_id", SharedPref.getUserRegistration().getId());
        if (!TextUtils.isEmpty(searchMembers.getText().toString()))
            jsonObject.addProperty("query", searchMembers.getText().toString());
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getApplicationContext());
        apiServiceProvider.viewFamilyMembers(jsonObject, null, new RetrofitListener() {
            @Override
            public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {
                progressBar.setVisibility(View.GONE);
                try {
                    familyMembers.clear();
                    familyMembers.addAll(FamilyParser.parseFamilyMembers(responseBodyString));
                    if (selectedFamilyMembers != null && selectedFamilyMembers.size() > 0) {
                        for (int i = 0; i < familyMembers.size(); i++) {
                            for (Integer selectedFamilyMember : selectedFamilyMembers) {
                                if (familyMembers.get(i).getUserId().equals(selectedFamilyMember)) {
                                    familyMembers.get(i).setDevEnabled(true);
                                    break;
                                } else familyMembers.get(i).setDevEnabled(false);
                            }
                        }
                    }
                    selectMemberAdapter.notifyDataSetChanged();
                } catch (JsonParseException | NullPointerException e) {
                    e.printStackTrace();
                    Snackbar.make(membersList, "Please try again later !! Something went wrong", Snackbar.LENGTH_SHORT);
                }
            }

            @Override
            public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    @OnClick(R.id.done)
    public void onClick() {
        ArrayList<Integer> selectedFamilyMembers = new ArrayList<>();
        for (FamilyMember familyMember : familyMembers) {
            if (familyMember.getDevEnabled()) {
                selectedFamilyMembers.add(familyMember.getUserId());
            }
        }
        if (selectedFamilyMembers.size() > 0) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra(DATA, selectedFamilyMembers);
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        } else {
            Snackbar.make(membersList, "Please select members!!", Snackbar.LENGTH_SHORT);
        }
    }

    @OnEditorAction(R.id.searchMembers)
    protected boolean onSearchQueryListener(int actionId) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            clearAndReload();
            return true;
        }
        return false;
    }

    private void clearAndReload() {
        familyMembers.clear();
        selectMemberAdapter.notifyDataSetChanged();
        requestFamilyMembers();
    }
}
