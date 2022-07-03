package com.familheey.app;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.familheey.app.Adapters.HistoryImageAdapter;
import com.familheey.app.Interfaces.RetrofitListener;
import com.familheey.app.Models.ApiCallbackParams;
import com.familheey.app.Models.ErrorData;
import com.familheey.app.Models.Request.FamilyHistoryUpdateRequest;
import com.familheey.app.Models.Request.HistoryImages;
import com.familheey.app.Models.Response.Family;
import com.familheey.app.Networking.Retrofit.ApiServiceProvider;
import com.familheey.app.Networking.Retrofit.ApiServices;
import com.familheey.app.Networking.Retrofit.RetrofitBase;
import com.familheey.app.Utilities.Constants;
import com.familheey.app.Utilities.SharedPref;
import com.familheey.app.Utilities.Utilities;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.gun0912.tedpermission.TedPermission;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;

public class EditHistoryActivity extends AppCompatActivity {

    private Family family;
    public CompositeDisposable subscriptions;

    @BindView(R.id.attachment)
    ImageView attachment;
    @BindView(R.id.image_list)
    RecyclerView list;
    @BindView(R.id.edit_history)
    EditText text;
    @BindView(R.id.no_files)
    TextView no_files;
    @BindView(R.id.btn_post)
    MaterialButton btn_post;
    SweetAlertDialog progressDialog;
    ArrayList<HistoryImages> historyImages = new ArrayList<>();
    HistoryImageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_history);
        ButterKnife.bind(this);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            family = new Gson().fromJson(bundle.getString(Constants.Bundle.DATA), Family.class);

            text.setText(family.getHistoryText());
            historyImages = family.getHistoryImages();
        }

        subscriptions = new CompositeDisposable();
        findViewById(R.id.goBack).setOnClickListener(v -> onBackPressed());
        TextView tittle = (findViewById(R.id.toolBarTitle));
        tittle.setText("Edit History");
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        list.setLayoutManager(layoutManager);
        adapter = new HistoryImageAdapter(this, historyImages);
        list.setAdapter(adapter);
        if (historyImages.size() > 0) {
            no_files.setVisibility(View.GONE);
        }
    }


    @OnClick({R.id.attachment, R.id.btn_post})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.attachment:
                if (isReadWritePermissionGranted())
//                    CropImage.activity()
//                            .setGuidelines(CropImageView.Guidelines.ON)
//                            .setCropShape(CropImageView.CropShape.RECTANGLE)
//                            .start(this);
                    ImagePicker.with(this)
                            .crop()//Crop image(Optional), Check Customization for more option
                            .compress(1024)			//Final image size will be less than 1 MB(Optional)
                            .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                            .start();
                else
                    showPermission();
                break;
            case R.id.btn_post:
                updateHistory();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ImagePicker.REQUEST_CODE && data != null) {
//            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                no_files.setVisibility(View.GONE);
//                assert result != null;
                updateImages(data.getData());
            }/* else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }*/
        }
    }

    private void updateImages(Uri uri) {
        String IMAGE_KEY = "history_images";
        MultipartBody.Builder multiPartBuilder = new MultipartBody.Builder().setType(Objects.requireNonNull(MediaType.parse("multipart/form-data")));
        multiPartBuilder.addFormDataPart("name", IMAGE_KEY);
        multiPartBuilder.addFormDataPart("user_id", SharedPref.getUserRegistration().getId());
        Utilities.addImageToMultiPartBuilder(multiPartBuilder, uri, "file");
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(this);
        apiServiceProvider.updateFamilyHistoryImage(multiPartBuilder, null, new RetrofitListener() {
            @Override
            public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {
                try {
                    JSONArray array = new JSONObject(responseBodyString).getJSONArray("data");
                    HistoryImages obj = new HistoryImages();
                    obj.setType(array.getJSONObject(0).getString("type"));
                    obj.setFilename(array.getJSONObject(0).getString("filename"));
                    family.setHistoryText(text.getText().toString());
                    historyImages.add(obj);
                    adapter.notifyDataSetChanged();
                } catch (Exception e) {
                   /*
                   Not needed
                    */
                }
            }

            @Override
            public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
               /*
                   Not needed
                    */
            }
        });

    }

    public void updateHistory() {
        showProgressDialog();
        FamilyHistoryUpdateRequest obj = new FamilyHistoryUpdateRequest();
        obj.setHistory_images(historyImages);
        obj.setId(family.getId().toString());
        obj.setUser_id(SharedPref.getUserRegistration().getId());
        obj.setHistory_text(text.getText().toString());
        FamilheeyApplication application = FamilheeyApplication.get(this);
        ApiServices apiServices = RetrofitBase.createRxResource(this, ApiServices.class);
        subscriptions.add(apiServices.updateFamilyHistory(obj)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(response -> {
                    hideProgressDialog();
                    if (response.code() == 200) {
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("history", text.getText().toString());
                        resultIntent.putExtra("images", new Gson().toJson(historyImages));
                        setResult(Activity.RESULT_OK, resultIntent);
                        EditHistoryActivity.this.finish();
                    }
                }, throwable -> hideProgressDialog()));
    }

    public void showProgressDialog() {
        progressDialog = Utilities.getProgressDialog(this);
        progressDialog.show();
    }

    public void hideProgressDialog() {
        if (progressDialog != null)
            progressDialog.dismiss();
    }

    private boolean isReadWritePermissionGranted() {
        return TedPermission.isGranted(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    private void showPermission() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialogue_permission);
        Window window = dialog.getWindow();
        assert window != null;
        window.setBackgroundDrawableResource(android.R.color.transparent);
        dialog.findViewById(R.id.btn_cancel).setOnClickListener(view -> dialog.dismiss());
        dialog.findViewById(R.id.btn_continue).setOnClickListener(view -> {
            dialog.dismiss();
//            CropImage.activity()
//                    .setGuidelines(CropImageView.Guidelines.ON)
//                    .setCropShape(CropImageView.CropShape.RECTANGLE)
//                    .start(this);
            ImagePicker.with(this)
                    .crop()//Crop image(Optional), Check Customization for more option
                    .compress(1024)			//Final image size will be less than 1 MB(Optional)
                    .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                    .start();
        });
        dialog.show();
    }
}
