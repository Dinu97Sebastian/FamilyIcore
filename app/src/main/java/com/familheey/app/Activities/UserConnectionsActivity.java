package com.familheey.app.Activities;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.familheey.app.Fragments.SearchPeopleFragment;
import com.familheey.app.Interfaces.GlobalSearchListener;
import com.familheey.app.Interfaces.ProgressListener;
import com.familheey.app.Interfaces.RetrofitListener;
import com.familheey.app.Models.ApiCallbackParams;
import com.familheey.app.Models.ErrorData;
import com.familheey.app.Models.Response.PeopleSearchModal;
import com.familheey.app.Networking.Retrofit.ApiServiceProvider;
import com.familheey.app.Networking.utils.GsonUtils;
import com.familheey.app.R;
import com.familheey.app.Utilities.Constants;
import com.familheey.app.Utilities.SharedPref;
import com.familheey.app.Utilities.Utilities;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class UserConnectionsActivity extends AppCompatActivity implements RetrofitListener, ProgressListener, GlobalSearchListener {

    public static final int CONNECTIONS = 0;
    public static final int MUTUAL_CONNECTIONS = 1;
    public static final int FAMILY_CONNECTIONS = 2;

    @BindView(R.id.goBack)
    ImageView goBack;
    @BindView(R.id.toolBarTitle)
    TextView toolBarTitle;
    private String otherUserId;
    private String familyId;
    private ArrayList<PeopleSearchModal> mutualConnections = new ArrayList<>();
    private SweetAlertDialog progressDialog;
    private int TYPE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_connections);
        ButterKnife.bind(this);
        otherUserId = getIntent().getStringExtra(Constants.Bundle.ID);
        TYPE = getIntent().getIntExtra(Constants.Bundle.TYPE, 100);
        loadPeopleFragment();
        switch (TYPE) {
            case CONNECTIONS:
                fetchConnections();
                break;
            case MUTUAL_CONNECTIONS:
                fetchMutualConnections();
                break;
            case FAMILY_CONNECTIONS:
                familyId = getIntent().getStringExtra(Constants.Bundle.FAMILY_ID);
                fetchFamilyMutualConnections();
                break;
            default:
                finish();
        }
        initializeToolbar();
    }

    private void initializeToolbar() {
        switch (TYPE) {
            case CONNECTIONS:
                toolBarTitle.setText("My Connections");
                break;
            case MUTUAL_CONNECTIONS:
                toolBarTitle.setText("Mutual Connections");
                break;
            case FAMILY_CONNECTIONS:
                toolBarTitle.setText("Known Members");
                break;
        }
        goBack.setOnClickListener(v -> finish());
    }

    private void loadPeopleFragment() {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.mutualConnectionContainer, SearchPeopleFragment.newInstance(false, false,TYPE,otherUserId))
                .commit();
    }

    private void fetchMutualConnections() {
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getApplicationContext());
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_one_id", SharedPref.getUserRegistration().getId());
        jsonObject.addProperty("user_two_id", otherUserId);
        jsonObject.addProperty("limit", "15");
        jsonObject.addProperty("offset", "0");
//        showProgressDialog();
        apiServiceProvider.getMutualConnections(jsonObject, null, this);
    }

    private void fetchConnections() {
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getApplicationContext());
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", otherUserId);
        jsonObject.addProperty("limit", "15");
        jsonObject.addProperty("offset", "0");
//        showProgressDialog();
        apiServiceProvider.getUserConnections(jsonObject, null, this);
    }

    private void fetchFamilyMutualConnections() {
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getApplicationContext());
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", otherUserId);
        jsonObject.addProperty("group_id", familyId);
//        showProgressDialog();
        apiServiceProvider.getFamilyMutualConnections(jsonObject, null, this);
    }

    @Override
    public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {
        mutualConnections.clear();
        hideProgressDialog();
        try {
            String parsedFormatJson = "";
            switch (TYPE) {
                case CONNECTIONS:
                    parsedFormatJson = new JSONArray(responseBodyString).toString();
                    break;
                case MUTUAL_CONNECTIONS:
                case FAMILY_CONNECTIONS:
                    parsedFormatJson = new JSONObject(responseBodyString).getJSONArray("data").toString();
                    break;
            }
            mutualConnections.addAll(GsonUtils.getInstance().getGson().fromJson(parsedFormatJson, new TypeToken<ArrayList<PeopleSearchModal>>() {
            }.getType()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.mutualConnectionContainer);
        if (currentFragment instanceof SearchPeopleFragment)
            ((SearchPeopleFragment) currentFragment).updatePeople(mutualConnections);
    }

    @Override
    public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
        showErrorDialog("Something went wrong ! please try again later");
    }

    @Override
    public void requsetGlobalSearch() {
        switch (TYPE) {
            case CONNECTIONS:
                fetchConnections();
                break;
            case MUTUAL_CONNECTIONS:
                fetchMutualConnections();
                break;
            case FAMILY_CONNECTIONS:
                fetchFamilyMutualConnections();
                break;
        }
    }

    @Override
    public void performSearch(String searchQuery) {

    }

    @Override
    public void reloadFamilySearch() {

    }

    @Override
    public void fetchFamilies() {

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
        try {
            if (progressDialog == null) {
                progressDialog = Utilities.getErrorDialog(this, errorMessage);
                progressDialog.show();
                return;
            }
            Utilities.getErrorDialog(progressDialog, errorMessage);
        } catch (Exception e) {
            finish();
        }
    }

}
