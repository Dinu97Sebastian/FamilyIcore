package com.familheey.app.Activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.familheey.app.Interfaces.ProgressListener;
import com.familheey.app.Interfaces.RetrofitListener;
import com.familheey.app.Models.ApiCallbackParams;
import com.familheey.app.Models.ErrorData;
import com.familheey.app.Networking.Retrofit.ApiServiceProvider;
import com.familheey.app.R;
import com.familheey.app.Utilities.SharedPref;
import com.familheey.app.Utilities.Utilities;
import com.google.android.material.button.MaterialButton;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.MediaType;
import okhttp3.MultipartBody;

import static com.familheey.app.Utilities.Utilities.addImageToMultiPartBuilder;

public class
FeedbackActivity extends AppCompatActivity implements ProgressListener, RetrofitListener {

    @BindView(R.id.goBack)
    ImageView goBack;
    @BindView(R.id.toolBarTitle)
    TextView toolBarTitle;
    @BindView(R.id.type)
    EditText type;
    @BindView(R.id.description)
    EditText description;
    @BindView(R.id.uploadImage)
    MaterialButton uploadImage;
    @BindView(R.id.submit)
    MaterialButton submit;

    @BindView(R.id.imageViewAttachment)
    ImageView imageViewAttachment;

    private Uri imageUri;
    private SweetAlertDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        ButterKnife.bind(this);
        initializeToolbar();


    }

    private void initializeToolbar() {
        toolBarTitle.setText("Feedback");
        goBack.setOnClickListener(v -> onBackPressed());
    }

    @OnClick({R.id.uploadImage, R.id.submit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.uploadImage:
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setCropShape(CropImageView.CropShape.RECTANGLE)
                        .start(this);
                break;
            case R.id.submit:
                if (validate())
                    postFeedback();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == Activity.RESULT_OK) {
                assert result != null;
                imageUri = result.getUri();
                imageViewAttachment.setImageURI(imageUri);
            }
        }
    }

    private boolean validate() {
        if (TextUtils.isEmpty(type.getText().toString()) || TextUtils.isEmpty(description.getText().toString())) {
            Toast.makeText(this, "Please enter the missing fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void postFeedback() {
        showProgressDialog();
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(Objects.requireNonNull(MediaType.parse("multipart/form-data")));
        builder.addFormDataPart("description", description.getText().toString());
        builder.addFormDataPart("type", type.getText().toString());
        builder.addFormDataPart("user", SharedPref.getUserRegistration().getId());
        builder.addFormDataPart("os", String.valueOf(android.os.Build.VERSION.SDK_INT));
        builder.addFormDataPart("device", Utilities.getDeviceName());
        addImageToMultiPartBuilder(builder, imageUri, "image");
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getApplicationContext());
        apiServiceProvider.addFeedback(builder, null, this);
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

    @Override
    public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {
        type.setText("");
        description.setText("");
        imageUri = null;
        hideProgressDialog();
        Toast.makeText(this, "Feedback Posted", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
        showErrorDialog(errorData.getMessage());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left,
                R.anim.right);
    }
}
