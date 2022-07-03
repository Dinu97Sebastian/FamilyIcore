package com.familheey.app.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.familheey.app.Adapters.AlbumSliderAdapter;
import com.familheey.app.Dialogs.AlbumActionDialogFragment;
import com.familheey.app.Interfaces.ProgressListener;
import com.familheey.app.Interfaces.RetrofitListener;
import com.familheey.app.Models.ApiCallbackParams;
import com.familheey.app.Models.ErrorData;
import com.familheey.app.Models.Response.Document;
import com.familheey.app.Networking.Retrofit.ApiServiceProvider;
import com.familheey.app.R;
import com.familheey.app.Utilities.Utilities;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.familheey.app.Utilities.Constants.Bundle.DATA;
import static com.familheey.app.Utilities.Constants.Bundle.IS_ADMIN;
import static com.familheey.app.Utilities.Constants.Bundle.POSITION;
import static com.familheey.app.Utilities.Constants.Bundle.TYPE;

public class AlbumSliderActivity extends AppCompatActivity implements ProgressListener, AlbumActionDialogFragment.OnEventActionSelectedListener {

    public static final int DELETED = 0;
    public static final int COVER = 1;
    public static final int REQUEST_CODE = 2457;

    @BindView(R.id.goBack)
    ImageView goBack;
    @BindView(R.id.toolBarTitle)
    TextView toolBarTitle;
    @BindView(R.id.imageSlider)
    SliderView imageSlider;
    @BindView(R.id.more)
    ImageView more;
    @BindView(R.id.imagedownload)
    ImageView imagedownload;
    private ArrayList<Document> documents = new ArrayList<>();
    private int position = 0;
    private SweetAlertDialog progressDialog;
    private boolean isAdmin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_slider);
        ButterKnife.bind(this);
        documents = getIntent().getParcelableArrayListExtra(DATA);
        position = getIntent().getIntExtra(POSITION, 0);
        isAdmin = getIntent().getBooleanExtra(IS_ADMIN, false);
        initializeToolbar();
        initAdapter();
        initRestrictions();
    }

    private void initRestrictions() {
        if (isAdmin)
            more.setVisibility(View.VISIBLE);
        else more.setVisibility(View.INVISIBLE);
    }

    private void initializeToolbar() {
        toolBarTitle.setText("Photos");
        goBack.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.putExtra(POSITION, position);
            setResult(Activity.RESULT_CANCELED, intent);
            finish();
        });
        imagedownload.setOnClickListener(view -> {
            if (isReadStoragePermissionGranted()) {
                String url = documents.get(imageSlider.getCurrentPagePosition()).getUrl();
                if (!url.equals("")) {
                    Toast.makeText(this, "Downloading media...", Toast.LENGTH_SHORT).show();
                    Utilities.downloadDocuments(this, url, documents.get(imageSlider.getCurrentPagePosition()).getFile_name());
                } else {
                    Toast.makeText(this, "Unable to download ", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initAdapter() {
        AlbumSliderAdapter imageSliderAdapter = new AlbumSliderAdapter(getApplicationContext(), documents);
        imageSlider.setSliderAdapter(imageSliderAdapter);
        imageSlider.setIndicatorAnimation(IndicatorAnimations.THIN_WORM);
        imageSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        imageSlider.setCurrentPagePosition(position);
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
    public void onCoverImageChanged(int position) {
        showProgressDialog();
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(this);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("album_id", documents.get(position).getFolderId().toString());
        jsonObject.addProperty("cover_pic", documents.get(position).getUrl());
        apiServiceProvider.makePicCover(jsonObject, null, new RetrofitListener() {
            @Override
            public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {
                Intent intent = new Intent();
                intent.putExtra(TYPE, COVER);
                intent.putExtra(POSITION, position);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }

            @Override
            public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
                showErrorDialog("Please try again later");
            }
        });
    }

    @Override
    public void onAlbumImageDeleted(int position) {
        showProgressDialog();
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(this);
        JsonObject jsonObject = new JsonObject();
        JsonArray jsonArray = new JsonArray();
        jsonArray.add(documents.get(position).getId());
        jsonObject.add("id", jsonArray);
        apiServiceProvider.deleteFile(jsonObject, null, new RetrofitListener() {
            @Override
            public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {
                hideProgressDialog();
                Intent intent = new Intent();
                intent.putExtra(TYPE, DELETED);
                intent.putExtra(POSITION, position);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }

            @Override
            public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
                showErrorDialog("Please try again later");
            }
        });
    }

    @OnClick(R.id.more)
    public void onClick() {
        AlbumActionDialogFragment.newInstance(documents.get(imageSlider.getCurrentPagePosition()), imageSlider.getCurrentPagePosition()).show(getSupportFragmentManager(), "AlbumActionDialogFragment");
    }


    private boolean isReadStoragePermissionGranted() {
        if (TedPermission.isGranted(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE))
            return true;
        else {
            requestPermission();
            return false;
        }
    }

    private void requestPermission() {
        TedPermission.with(this)
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        imagedownload.performClick();
                    }

                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {

                    }
                })
                .setDeniedMessage("If you reject permission,you can not download/upload images\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();
    }
}
