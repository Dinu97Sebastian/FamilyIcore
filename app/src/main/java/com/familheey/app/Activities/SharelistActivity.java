package com.familheey.app.Activities;

import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.familheey.app.Adapters.EventShareAdapter;
import com.familheey.app.Interfaces.ProgressListener;
import com.familheey.app.Networking.Retrofit.ApiServices;
import com.familheey.app.Networking.Retrofit.RetrofitBase;
import com.familheey.app.R;
import com.familheey.app.Utilities.Constants;
import com.familheey.app.Utilities.SharedPref;
import com.familheey.app.Utilities.Utilities;
import com.google.gson.JsonObject;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class SharelistActivity extends AppCompatActivity implements ProgressListener {

    public CompositeDisposable subscriptions;
    public RecyclerView recyclerView;
    public ProgressBar progressBar;
    private SweetAlertDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sharelist);
        subscriptions = new CompositeDisposable();
        recyclerView = findViewById(R.id.share_list);
        progressBar = findViewById(R.id.progressBar);
        findViewById(R.id.goBack).setOnClickListener(v -> onBackPressed());
        TextView tittle = (findViewById(R.id.toolBarTitle));
        tittle.setText("Shared By");
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String type = bundle.getString(Constants.Bundle.TYPE);
            assert type != null;
            if(type.equals("EVENT")){
            getShareList(bundle.getString("event_id"));}
            else if(type.equals("POSTVIEW")){
                tittle.setText("Viewed By");
                getPostViewList(bundle.getString("event_id"));
            }else{
                getPostShareList(bundle.getString("event_id"));
            }
        }
    }

    private void getShareList( String event_id) {
        showProgressDialog();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        jsonObject.addProperty("event_id", event_id);
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        final ApiServices authService = RetrofitBase.createRxResource(this, ApiServices.class);
        subscriptions.add(authService.getShareList(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(response -> {
                    recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
                    assert response.body() != null;
                    recyclerView.setAdapter(new EventShareAdapter(this, response.body().getData()));
                    hideProgressDialog();
                }, throwable -> showErrorDialog("Something went wrong ! Please try again later")));
    }

    private void getPostShareList( String post_id) {
        showProgressDialog();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("origin_post_id", post_id);
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        final ApiServices authService = RetrofitBase.createRxResource(this, ApiServices.class);
        subscriptions.add(authService.getPostShareList(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(response -> {
                    recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
                    recyclerView.setAdapter(new EventShareAdapter(this, response.body()));
                    hideProgressDialog();
                }, throwable -> {
                    /*
                    need to handle
                     */
                }));
    }


    private void getPostViewList( String post_id) {
        showProgressDialog();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("post_id", post_id);
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        final ApiServices authService = RetrofitBase.createRxResource(this, ApiServices.class);
        subscriptions.add(authService.getPostViewList(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(response -> {
                    recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
                    assert response.body() != null;
                    recyclerView.setAdapter(new EventShareAdapter(this, response.body().getData()));
                    hideProgressDialog();
                }, throwable -> showErrorDialog("Something went wrong ! Please try again later")));
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
