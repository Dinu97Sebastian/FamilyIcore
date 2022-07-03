package com.familheey.app.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.familheey.app.Interfaces.RetrofitListener;
import com.familheey.app.Models.ApiCallbackParams;
import com.familheey.app.Models.ErrorData;
import com.familheey.app.Networking.Retrofit.ApiServiceProvider;
import com.familheey.app.R;
import com.familheey.app.Utilities.SharedPref;
import com.google.android.material.button.MaterialButton;
import com.google.gson.JsonObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.MultipartBody;

import static com.familheey.app.Utilities.Constants.Bundle.DATA;
import static com.familheey.app.Utilities.Constants.Bundle.ID;

public class TextEditActivity extends AppCompatActivity implements RetrofitListener {

    @BindView(R.id.description)
    EditText description;
    @BindView(R.id.goBack)
    ImageView goBack;
    private String familyid;
    @BindView(R.id.done)
    MaterialButton done;

    @BindView(R.id.toolBarTitle)
    TextView toolBarTitle;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private boolean isUpdate;
    private String des = "";
    private String type = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_edit);
        ButterKnife.bind(this);
        Bundle b = getIntent().getExtras();
        if (b != null) {
            des = b.getString(DATA);
            description.setText(des);
            familyid = b.getString(ID);
            type = b.getString("type");
            toolBarTitle.setText(b.getString("tittle"));
            if (b.getString("tittle").equalsIgnoreCase("Edit Introduction")) {
                done.setText("Done");
            }
        }

        done.setOnClickListener(v -> {
            if (type.equals("family")) {
                hideKeyboard();
                updateFamily();
            }
            else
                updateProfile();
        });

        goBack.setOnClickListener(v -> onBackPressed());
    }

    void showProgress() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
        if (done != null) {
            done.setClickable(false);
            done.setEnabled(false);
        }
    }

    void hideProgress() {
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
        if (done != null) {
            done.setClickable(true);
            done.setEnabled(true);
        }
    }
    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = getCurrentFocus();
        if (view == null) {
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    @Override
    public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {
        hideProgress();
        isUpdate = true;
        onBackPressed();

    }

    @Override
    public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
        hideProgress();
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        if (isUpdate) {
            intent.putExtra(DATA, description.getText().toString().trim());
        } else {
            intent.putExtra(DATA, des);
        }
        setResult(RESULT_OK, intent);
        finish();

        super.onBackPressed();
    }

    private void updateFamily() {
        showProgress();
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(this);
        JsonObject updateJson = new JsonObject();
        updateJson.addProperty("id", familyid);
        updateJson.addProperty("user_id", SharedPref.getUserRegistration().getId());
        updateJson.addProperty("intro", description.getText().toString().trim());
        apiServiceProvider.updateFamily(updateJson, null, this);
    }

    private void updateProfile() {
        showProgress();
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(this);
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MediaType.parse("multipart/form-data"));
        builder.addFormDataPart("id", SharedPref.getUserRegistration().getId());
        JsonObject updateJson = new JsonObject();
        if (type.equals("about"))
            builder.addFormDataPart("about", description.getText().toString().trim());
        else
            builder.addFormDataPart("work", description.getText().toString().trim());
        apiServiceProvider.updateUserProfile(builder, null, this);
    }

}
