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
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.familheey.app.Adapters.LinkFamilyAdapter;
import com.familheey.app.CustomViews.TextViews.SemiBoldTextView;
import com.familheey.app.Decorators.VerticalSpaceItemDecoration;
import com.familheey.app.Interfaces.ProgressListener;
import com.familheey.app.Interfaces.RetrofitListener;
import com.familheey.app.Models.ApiCallbackParams;
import com.familheey.app.Models.ErrorData;
import com.familheey.app.Models.Response.Family;
import com.familheey.app.Networking.Retrofit.ApiServiceProvider;
import com.familheey.app.Parsers.FamilyParser;
import com.familheey.app.R;
import com.familheey.app.Utilities.SharedPref;
import com.familheey.app.Utilities.Utilities;
import com.google.android.material.button.MaterialButton;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.familheey.app.Utilities.Constants.ApiFlags.FETCH_FAMILY_FOR_LINKING;
import static com.familheey.app.Utilities.Constants.ApiFlags.REQUEST_LINK_FAMILY;
import static com.familheey.app.Utilities.Constants.Bundle.DATA;
import static com.familheey.app.Utilities.Constants.Bundle.IDENTIFIER;
import static com.familheey.app.Utilities.Utilities.hideCircularReveal;
import static com.familheey.app.Utilities.Utilities.showCircularReveal;

public class LinkFamilyActivity extends AppCompatActivity implements RetrofitListener, ProgressListener {
    @BindView(R.id.goBack)
    ImageView goBack;
    @BindView(R.id.toolBarTitle)
    TextView toolBarTitle;
    @BindView(R.id.emptyIndicatorContainer)
    View emptyIndicatorContainer;
    @BindView(R.id.linkedFamilyList)
    RecyclerView linkedFamilyList;
    @BindView(R.id.linkFamilies)
    MaterialButton linkFamilies;
    @BindView(R.id.createFamilyWithLinking)
    TextView createFamilyWithLinking;
    @BindView(R.id.emptyResultText)
    SemiBoldTextView emptyResultText;
    @BindView(R.id.searchFamiliesToLink)
    ImageView searchFamiliesToLink;
    @BindView(R.id.imageBack)
    ImageView imageBack;
    @BindView(R.id.clearSearch)
    ImageView clearSearch;
    @BindView(R.id.searchInfo)
    EditText searchInfo;
    @BindView(R.id.constraintSearch)
    ConstraintLayout constraintSearch;
    private int type=0;
    String query="";
    private Family family;
    private LinkFamilyAdapter linkFamilyAdapter;
    private SweetAlertDialog progressDialog;
    private ArrayList<Family> familiesToBeLinked = new ArrayList<>();
    private List<Family> tempFamilies = new ArrayList<>();
    private Boolean isAdmin=false;
    private Boolean isMember=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link_family);
        ButterKnife.bind(this);
        family = getIntent().getParcelableExtra(DATA);
        if (getIntent().hasExtra(IDENTIFIER))
            createFamilyWithLinking.setVisibility(View.INVISIBLE);
        initializeToolbar();
        initializeAdapter();
        initListener();
        initializeCallbacks();
        fetchFamiliesForlinking();
    }
    /**@author Devika on 09-12-2021
     * search functionality in linking families*/
    private void initListener() {
        searchFamiliesToLink.setOnClickListener(view -> {
            showCircularReveal(constraintSearch);
            showKeyboard();

        });

        imageBack.setOnClickListener(view -> {
            hideCircularReveal(constraintSearch);
            searchInfo.setText("");
            onSearchQueryListener(EditorInfo.IME_ACTION_SEARCH);
        });
    }
    private void showKeyboard() {
        if (searchInfo.requestFocus()) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(searchInfo, InputMethodManager.SHOW_IMPLICIT);
            }
        }
    }
    @OnEditorAction(R.id.searchInfo)
    protected boolean onSearchQueryListener(int actionId) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            String s = searchInfo.getText().toString();
            search(s);
            try {
                InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchInfo.getWindowToken(), 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }
    private void search(String query) {
        this.query = query;
        clearAndReload();
    }
    private void clearAndReload(){
        familiesToBeLinked.clear();
        linkFamilyAdapter.notifyDataSetChanged();
        type=1;
        fetchFamiliesForlinking();
    }

    private void initializeCallbacks() {
        searchInfo.addTextChangedListener(new TextWatcher() {
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
            searchInfo.setText("");
            onSearchQueryListener(EditorInfo.IME_ACTION_SEARCH);
        });
    }
/**end of search functionality in linking families**/

    private void initializeAdapter() {
        linkFamilyAdapter = new LinkFamilyAdapter(familiesToBeLinked);
        linkedFamilyList.setAdapter(linkFamilyAdapter);
        linkedFamilyList.setLayoutManager(new LinearLayoutManager(this));
        linkedFamilyList.addItemDecoration(new VerticalSpaceItemDecoration(16));
    }

    private void initializeToolbar() {
        searchFamiliesToLink.setVisibility(View.VISIBLE);
        toolBarTitle.setText("Link Families");
        goBack.setOnClickListener(v -> finish());
    }

    private void fetchFamiliesForlinking() {
        showProgressDialog();
        JsonObject fetchFamiliesRequestJson = new JsonObject();
        fetchFamiliesRequestJson.addProperty("user_id", SharedPref.getUserRegistration().getId());
        fetchFamiliesRequestJson.addProperty("group_id", family.getId());
        if(this.query!=null)
        fetchFamiliesRequestJson.addProperty("query", this.query);
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(this);
        apiServiceProvider.fetchFamiliesForLinking(fetchFamiliesRequestJson, null, this);
    }

    private void requestFamilyLinking() {
        showProgressDialog();
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getApplicationContext());
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("from_group", family.getId());
        jsonObject.add("to_group", linkFamilyAdapter.getSelectedFamiliesForLinking());
        jsonObject.addProperty("requested_by", SharedPref.getUserRegistration().getId());
        apiServiceProvider.requestLinkFamily(jsonObject, null, this);
    }

    @OnClick({R.id.goBack, R.id.linkFamilies, R.id.exit, R.id.createFamilyWithLinking})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.exit:
            case R.id.goBack:
                finish();
                break;
            case R.id.linkFamilies:
                if (linkFamilyAdapter.getSelectedFamiliesForLinking().size() > 0){
                    requestFamilyLinking();
                    tempFamilies=linkFamilyAdapter.getSelectedTempFamiliesForLinking();
                    for (Family familyForLinking : tempFamilies) {
                        if (familyForLinking.getUserStatus().equals("admin"))
                            isAdmin=true;
                        else
                            isMember=true;
                    }
                }
                else
                    Toast.makeText(this, "Please select families for linking!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.createFamilyWithLinking:
                Intent createFamilyIntent = new Intent(getApplicationContext(), CreateFamilyActivity.class);
                createFamilyIntent.putExtra(DATA, family.getId().toString());
                startActivity(createFamilyIntent);
                break;
        }
    }

    @Override
    public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {
        hideProgressDialog();
        switch (apiFlag) {
            case FETCH_FAMILY_FOR_LINKING:
                familiesToBeLinked.addAll(FamilyParser.parseLinkedFamilies(responseBodyString));
                if (familiesToBeLinked.size() == 0 && type==1) {
                    emptyResultText.setVisibility(View.VISIBLE);
                    emptyIndicatorContainer.setVisibility(View.INVISIBLE);
                    linkedFamilyList.setVisibility(View.INVISIBLE);
                    linkFamilies.setVisibility(View.INVISIBLE);
                } else if(familiesToBeLinked.size()==0 && type==0) {
                    emptyResultText.setVisibility(View.INVISIBLE);
                    emptyIndicatorContainer.setVisibility(View.VISIBLE);
                    linkedFamilyList.setVisibility(View.INVISIBLE);
                    linkFamilies.setVisibility(View.INVISIBLE);
                }else {
                    emptyResultText.setVisibility(View.INVISIBLE);
                    emptyIndicatorContainer.setVisibility(View.INVISIBLE);
                    linkedFamilyList.setVisibility(View.VISIBLE);
                    linkFamilies.setVisibility(View.VISIBLE);
                }
                linkFamilyAdapter.notifyDataSetChanged();
                break;
            case REQUEST_LINK_FAMILY:
                if(isAdmin && !isMember)
                Toast.makeText(this, "Link successful", Toast.LENGTH_SHORT).show();
                else if(!isAdmin && isMember)
                    Toast.makeText(this, "Request sent", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, "Successfully Linked with your own families and sent request for others", Toast.LENGTH_SHORT).show();
                finish();
                break;
        }
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
}
