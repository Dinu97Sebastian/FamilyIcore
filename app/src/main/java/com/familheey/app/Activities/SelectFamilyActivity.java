package com.familheey.app.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.familheey.app.FamilySelectAdapter;
import com.familheey.app.Interfaces.RetrofitListener;
import com.familheey.app.Models.ApiCallbackParams;
import com.familheey.app.Models.ErrorData;
import com.familheey.app.Models.Response.Family;
import com.familheey.app.Networking.Retrofit.ApiServiceProvider;
import com.familheey.app.Parsers.FamilyParser;
import com.familheey.app.R;
import com.familheey.app.Utilities.Constants;
import com.familheey.app.Utilities.SharedPref;
import com.google.android.material.button.MaterialButton;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;

import static com.familheey.app.Utilities.Constants.Bundle.DATA;
import static com.familheey.app.Utilities.Constants.Bundle.POSITION;
import static com.familheey.app.Utilities.Constants.Bundle.TYPE;

public class SelectFamilyActivity extends AppCompatActivity implements RetrofitListener, FamilySelectAdapter.OnItemSelectedListener {

    public static final int REQUEST_CODE = 154;
    @BindView(R.id.progressBar2)
    ProgressBar progressBar;
    @BindView(R.id.searchMyFamilies)
    EditText searchMyFamilies;
    @BindView(R.id.myFamilyList)
    RecyclerView myFamilyList;
    @BindView(R.id.familyEmptyContainer)
    View familyEmptyContainer;
    @BindView(R.id.createFamilyNow)
    MaterialButton createFamilyNow;
    @BindView(R.id.toolBarTitle)
    TextView toolBarTitle;
    @BindView(R.id.goBack)
    ImageView goBack;
    @BindView(R.id.clearSearch)
    ImageView clearSearch;
    @BindView(R.id.searchContainer)
    ConstraintLayout searchContainer;
    @BindView(R.id.addFamilyLayout)
    ConstraintLayout addFamilyLayout;
    private FamilySelectAdapter familyListingAdapter;
    private final ArrayList<Family> families = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_family);
        ButterKnife.bind(this);
        initView();
        initializeToolbar();
        initializeSearchClearCallback();
    }

    private void initializeSearchClearCallback() {
        searchMyFamilies.addTextChangedListener(new TextWatcher() {

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
            searchMyFamilies.setText("");
            onSearchQueryListener(EditorInfo.IME_ACTION_SEARCH);
        });
    }

    private void initializeToolbar() {
        toolBarTitle.setText("Select Family");
        goBack.setOnClickListener(v -> onBackPressed());
    }

    private void fetchMyFamilies() {
        progressBar.setVisibility(View.VISIBLE);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        jsonObject.addProperty("query", searchMyFamilies.getText().toString());
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getApplicationContext());
        apiServiceProvider.listAllFamily(jsonObject, null, this);
    }

    private void initView() {
        familyListingAdapter = new FamilySelectAdapter(families, this);
        myFamilyList.setLayoutManager(new LinearLayoutManager(this));
        myFamilyList.setAdapter(familyListingAdapter);
    }

    @OnClick({R.id.createFamilyNow})
    public void onViewClicked() {
        Intent intent = new Intent(getApplicationContext(), CreateFamilyActivity.class);
        startActivityForResult(intent, Constants.RequestCode.REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.RequestCode.REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            fetchMyFamilies();
        }
    }

    @Override
    public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {
        families.clear();
        families.addAll(FamilyParser.parseLinkedFamilies(responseBodyString));
        familyListingAdapter.notifyDataSetChanged();
        if (families.size() > 0)
            showSearchResults();
        else showEmptyFamilies();
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
        progressBar.setVisibility(View.GONE);
        Toast.makeText(getApplicationContext(), "" + errorData, Toast.LENGTH_SHORT).show();
    }

    @OnEditorAction(R.id.searchMyFamilies)
    protected boolean onSearchQueryListener(int actionId) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            fetchMyFamilies();
            try {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                assert imm != null;
                imm.hideSoftInputFromWindow(searchMyFamilies.getWindowToken(), 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }

    private void showSearchResults() {
        searchMyFamilies.setVisibility(View.VISIBLE);
        myFamilyList.setVisibility(View.VISIBLE);
        familyEmptyContainer.setVisibility(View.INVISIBLE);
    }

    private void showEmptyFamilies() {
        familyEmptyContainer.setVisibility(View.VISIBLE);
        if (searchMyFamilies.getText().toString().trim().length() == 0)
            searchMyFamilies.setVisibility(View.INVISIBLE);
        else searchMyFamilies.setVisibility(View.VISIBLE);
        myFamilyList.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        families.clear();
        familyListingAdapter.notifyDataSetChanged();
        fetchMyFamilies();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onItemSelected(Family family) {
        Intent intent = new Intent();
        intent.putExtra(DATA, family);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}
