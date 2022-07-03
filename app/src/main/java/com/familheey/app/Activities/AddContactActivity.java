package com.familheey.app.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.familheey.app.Interfaces.RetrofitListener;
import com.familheey.app.Models.ApiCallbackParams;
import com.familheey.app.Models.ErrorData;
import com.familheey.app.Models.Response.EventContacts;
import com.familheey.app.Networking.Retrofit.ApiServiceProvider;
import com.familheey.app.R;
import com.familheey.app.Utilities.Utilities;
import com.google.gson.JsonObject;
import com.hbb20.CountryCodePicker;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.familheey.app.Utilities.Constants.Bundle.DATA;
import static com.familheey.app.Utilities.Constants.Bundle.ID;

public class AddContactActivity extends AppCompatActivity {
    boolean isEdit = false;
    String eventId = "";
    @BindView(R.id.goBack)
    ImageView goBack;
    @BindView(R.id.toolBarTitle)
    TextView toolbarTitle;
    @BindView(R.id.edtxName)
    EditText edtxName;
    @BindView(R.id.edtxPhone)
    EditText edtxPhone;
    @BindView(R.id.edtxEmail)
    EditText edtxEmail;
    @BindView(R.id.btnAdd)
    Button btnAdd;
    ProgressBar progressBar;
    @BindView(R.id.editTextCountryPick)
    CountryCodePicker editTextCountryPick;

    EventContacts contact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
        ButterKnife.bind(this);
        toolbarTitle.setText("Contact");
        progressBar = findViewById(R.id.progressBar);
        parseIntent();
        initListener();
    }

    private void parseIntent() {
        if (getIntent().hasExtra(ID)) {
            eventId = getIntent().getStringExtra(ID);
        }
        if (getIntent().hasExtra(DATA)) {
            contact = getIntent().getParcelableExtra(DATA);
            isEdit = true;
            assert contact != null;
            populateViews(contact);
        }
    }

    private void populateViews(EventContacts contact) {
        btnAdd.setText("Update");
        edtxName.setText(contact.getName());
        edtxPhone.setText(contact.getPhone().substring(3));
        edtxEmail.setText(contact.getEmail());
    }


    private void initListener() {
        btnAdd.setOnClickListener(view -> {
            if (validated())
                if (isEdit) {
                    callEditEventContactApi();
                } else
                    callAddEventContactApi();
        });
        goBack.setOnClickListener(v -> finish());
    }

    private void callEditEventContactApi() {
        showProgress();
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(this);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", edtxName.getText().toString());
        jsonObject.addProperty("email", edtxEmail.getText().toString());
        jsonObject.addProperty("phone", "+" + editTextCountryPick.getFullNumberWithPlus() + edtxPhone.getText().toString());
        jsonObject.addProperty("id", contact.getId());

        apiServiceProvider.editEventContact(jsonObject, null, new RetrofitListener() {
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

    void showProgress() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    void hideProgress() {
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }

    private boolean validated() {
        if (edtxName.getText().toString().trim().length() == 0) {
            Toast.makeText(this, "Enter name", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (edtxEmail.getText().toString().length() == 0) {
            Toast.makeText(this, "Enter email", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            if (!Utilities.isValidEmail(edtxEmail.getText().toString().trim())) {
                Toast.makeText(this, "Enter valid email", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        if (edtxPhone.getText().toString().length() == 0) {
            Toast.makeText(this, "Enter phone", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void callAddEventContactApi() {
        showProgress();
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(this);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", edtxName.getText().toString().trim());
        jsonObject.addProperty("email", edtxEmail.getText().toString().trim());
        jsonObject.addProperty("phone", "+" + editTextCountryPick.getFullNumberWithPlus() + edtxPhone.getText().toString());
        jsonObject.addProperty("event_id", eventId);

        apiServiceProvider.addEventContact(jsonObject, null, new RetrofitListener() {
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


}
