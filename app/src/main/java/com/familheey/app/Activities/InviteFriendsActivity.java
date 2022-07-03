package com.familheey.app.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.familheey.app.Interfaces.RetrofitListener;
import com.familheey.app.Models.ApiCallbackParams;
import com.familheey.app.Models.ErrorData;
import com.familheey.app.Models.Response.Family;
import com.familheey.app.Networking.Retrofit.ApiServiceProvider;
import com.familheey.app.R;
import com.familheey.app.Utilities.SharedPref;
import com.google.gson.JsonObject;
import com.hbb20.CountryCodePicker;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InviteFriendsActivity extends AppCompatActivity implements RetrofitListener {
    @BindView(R.id.editTextName)
    EditText editTextName;

    @BindView(R.id.editTextPhone)
    EditText editTextPhone;

    @BindView(R.id.editTextEmail)
    EditText editTextEmail;

    @BindView(R.id.editTextCountryPick)
    CountryCodePicker editTextCountryPick;


    Button materialButtonInvite;

    @BindView(R.id.progressInvite)
    ProgressBar progressInvite;
    Family family;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_friends);
        ButterKnife.bind(this);
        Intent i = getIntent();
        family = i.getParcelableExtra("family");

        materialButtonInvite = findViewById(R.id.materialButtonInvite);
        initListeners();

    }

    void showProgress() {
        if (progressInvite != null) {
            progressInvite.setVisibility(View.VISIBLE);
        }
    }

    void hideProgress() {
        if (progressInvite != null) {
            progressInvite.setVisibility(View.GONE);
        }
    }

    private void initListeners() {
        editTextCountryPick.setOnClickListener(view -> {


        });


        materialButtonInvite.setOnClickListener(view -> {
            if (validated())
                if (family != null) {
                    callApi();

                } else {
                    Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                }
        });
    }

    private boolean validated() {

        if (editTextName.getText().toString().trim().length() <= 2) {
            Toast.makeText(this, "Name must be at least 3 character", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (isValidName(editTextName.getText().toString().trim().replace(" ", ""))) {
            Toast.makeText(this, "No special characters allowed", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (editTextPhone.getText().toString().length() < 6) {
            Toast.makeText(this, "Enter valid phone", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (isEmailValid(editTextEmail.getText().toString())) {
            Toast.makeText(this, "Enter Email", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public static boolean isEmailValid(String email) {
        String expression = "^[\\w.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public boolean isValidName(String name) {
        Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(name);
        boolean b = m.find();
        return !b;
    }

    private void callApi() {
        showProgress();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        jsonObject.addProperty("phone", "+" + editTextCountryPick.getFullNumberWithPlus() + editTextPhone.getText().toString());
        jsonObject.addProperty("email", editTextEmail.getText().toString());
        jsonObject.addProperty("full_name", editTextName.getText().toString().trim());
        jsonObject.addProperty("group_id", family.getId());

        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getApplicationContext());
        apiServiceProvider.inviteViaSms(jsonObject, null, this);
    }

    @Override
    public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {
        hideProgress();
        finish();
    }

    @Override
    public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
        hideProgress();
        Toast.makeText(this, errorData.getMessage(), Toast.LENGTH_SHORT).show();
    }
}
