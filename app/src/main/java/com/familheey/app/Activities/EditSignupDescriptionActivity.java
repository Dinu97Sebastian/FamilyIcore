package com.familheey.app.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.familheey.app.Interfaces.RetrofitListener;
import com.familheey.app.Models.ApiCallbackParams;
import com.familheey.app.Models.ErrorData;
import com.familheey.app.Networking.Retrofit.ApiServiceProvider;
import com.familheey.app.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;

import java.util.Objects;

public class EditSignupDescriptionActivity extends AppCompatActivity {
    private EditText editTextDescription;
    private ProgressBar progressUpdateDesc;
    private int id;
    private String eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_signup);
        TextView toolBarTitle = findViewById(R.id.toolBarTitle);
        Button continu = findViewById(R.id.create_event);
        editTextDescription = findViewById(R.id.editTextDescription);
        progressUpdateDesc = findViewById(R.id.progressUpdateDesc);
        toolBarTitle.setText("Edit Sign Ups");
        id = Objects.requireNonNull(getIntent().getExtras()).getInt("ID");
        eventId = Objects.requireNonNull(getIntent().getExtras()).getString("EVENT_ID");
        continu.setOnClickListener(view -> {
            if (editTextDescription.getText().toString().length() == 0) {
                showSnackBar();


            } else {
                callUpdateEventSignupApi();
            }
        });
    }


    private void callUpdateEventSignupApi() {
        showProgress();

        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("slot_description", editTextDescription.getText().toString());
        jsonObject.addProperty("event_id", eventId);
        jsonObject.addProperty("id", String.valueOf(id));

        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(this);
        apiServiceProvider.callUpdateEventSignupApi(jsonObject, null, new RetrofitListener() {
            @Override
            public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {
                hideProgress();
                finish();
            }

            @Override
            public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
                hideProgress();
            }
        });

    }


    void showSnackBar() {
        Snackbar snackbar = Snackbar
                .make(progressUpdateDesc, "Enter description", Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    void showProgress() {
        if (progressUpdateDesc != null) {
            progressUpdateDesc.setVisibility(View.VISIBLE);
        }
    }

    void hideProgress() {
        if (progressUpdateDesc != null) {
            progressUpdateDesc.setVisibility(View.GONE);

        }
    }


}
