package com.familheey.app.Announcement;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.UserStateDetails;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferService;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferType;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.familheey.app.Adapters.AlbumPostAdapter;
import com.familheey.app.CustomViews.FSpinner;
import com.familheey.app.FamilheeyApplication;
import com.familheey.app.Interfaces.ProgressListener;
import com.familheey.app.Models.Request.HistoryImages;
import com.familheey.app.Models.Request.Image;
import com.familheey.app.Models.Request.PostInfo;
import com.familheey.app.Models.Request.PostRequest;
import com.familheey.app.Models.Response.SelectFamilys;
import com.familheey.app.Models.imageSizes;
import com.familheey.app.Networking.Retrofit.ApiServices;
import com.familheey.app.Networking.Retrofit.RetrofitBase;
import com.familheey.app.Post.ProgressRequestBody;
import com.familheey.app.Post.SelectFamilesOrPeopleActivity;
import com.familheey.app.R;
import com.familheey.app.Utilities.FileUtils;
import com.familheey.app.Utilities.GifSizeFilter;
import com.familheey.app.Utilities.SharedPref;
import com.familheey.app.Utilities.Utilities;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.filter.Filter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.familheey.app.Utilities.Constants.Bundle.DATA;

public class CreateAnnouncementActivity extends AppCompatActivity implements ProgressListener, AlbumPostAdapter.OnAlbumSelectedListener, ProgressRequestBody.UploadCallbacks {


    private final int REQUEST_CODE_PERMISSIONS = 1;
    static final String MIME_TYPE_TEXT = "text/*";
    static final String MIME_TYPE_IMAGE = "image/png";
    static final String MIME_TYPE_VIDEO = "video/*";
    static final String MIME_TYPE_PDF = "application/pdf";
    private static final int RESULT_DOC = 36;
    int PICK_IMAGE = 10;
    int RESULT_LOAD_VIDEO = 11;

    private Boolean isSpinnerSelection = true;
    private int type = 0;

    private Boolean isUploadingImage = false;
    private final List<Image> albumDocuments = new ArrayList<>();
    AlbumPostAdapter albumEventAdapter;
    private List<Uri> fileUris = new ArrayList<>();
    ArrayList<String> users;
    ArrayList<SelectFamilys> family;
    public CompositeDisposable subscriptions;
    ArrayList<HistoryImages> historyImages = new ArrayList<>();
    private String File_Name = "";
    @BindView(R.id.goBack)
    ImageView imageView;

//    @BindView(R.id.post_spinner)
//    FSpinner postSpinner;

    @BindView(R.id.txt_count)
    TextView txt_count;

    @BindView(R.id.post)
    MaterialButton post;

    @BindView(R.id.fullview)
    ConstraintLayout constraintLayout;

    @BindView(R.id.img_recycler)
    RecyclerView imgRecycler;
    @BindView(R.id.no_files)
    TextView no_files;

    @BindView(R.id.conversation)
    Switch conversation;

    @BindView(R.id.share)
    Switch share;

    @BindView(R.id.what_to_post_descrption)
    EditText what_to_post_descrption;

    private SweetAlertDialog progressDialog;

    private BottomSheetBehavior sheetBehavior;
    private ArrayList<imageSizes> sizes = new ArrayList<>();
    static TransferUtility transferUtility;
    static TransferObserver uploadObserver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_announcement);
        ButterKnife.bind(this);
        subscriptions = new CompositeDisposable();
       // String[] colors = {"Select", "All My Families", "Selected Families"};
        inits();
//        ArrayAdapter spinnerArrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, colors);
//        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
//        postSpinner.setAdapter(spinnerArrayAdapter);
        if (getIntent() != null && getIntent().hasExtra(DATA)) {
            String familyID = getIntent().getStringExtra(DATA);
            family = new ArrayList<>();

            isSpinnerSelection = false;
            SelectFamilys familys = new SelectFamilys();
            familys.setId(familyID);
            familys.setPost_create("");
            family.add(familys);
//            postSpinner.setSelection(2);
//            txt_count.setText("1 Family selected");
        }

//        postSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//
//                switch (position) {
//                    case 1:
//                        txt_count.setText("");
//                        break;
//                    case 2:
//                        if (isSpinnerSelection) {
//                            txt_count.setText("");
//                            if (family != null && family.size() > 0) {
//                                startActivityForResult(new Intent(CreateAnnouncementActivity.this, SelectFamilesOrPeopleActivity.class).putExtra("from", "Announcement").putExtra("type", "FAMILY").putExtra("refid", new Gson().toJson(family)), 101);
//                            } else {
//                                startActivityForResult(new Intent(CreateAnnouncementActivity.this, SelectFamilesOrPeopleActivity.class).putExtra("from", "Announcement").putExtra("type", "FAMILY"), 101);
//                            }
//                        }
//                        isSpinnerSelection = true;
//                        break;
//                    case 3:
//                        txt_count.setText("");
//                        startActivityForResult(new Intent(CreateAnnouncementActivity.this, SelectFamilesOrPeopleActivity.class).putExtra("type", "PEOPLE"), 102);
//                        break;
//
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//                switch (postSpinner.getSelectedItemPosition()) {
//                    case 2:
//                        txt_count.setText("");
//                        startActivityForResult(new Intent(CreateAnnouncementActivity.this, SelectFamilesOrPeopleActivity.class).putExtra("from", "Announcement").putExtra("type", "FAMILY"), 101);
//                        break;
//                    case 3:
//                        txt_count.setText("");
//                        startActivityForResult(new Intent(CreateAnnouncementActivity.this, SelectFamilesOrPeopleActivity.class).putExtra("type", "PEOPLE"), 102);
//                        break;
//
//                }
//            }
//        });

        CardView bottom_sheet = findViewById(R.id.bottom_sheet);
        sheetBehavior = BottomSheetBehavior.from(bottom_sheet);
        albumEventAdapter = new AlbumPostAdapter(this, albumDocuments);
        GridLayoutManager glm = new GridLayoutManager(this, 1);
        glm.setOrientation(LinearLayoutManager.HORIZONTAL);
        imgRecycler.setLayoutManager(glm);
        imgRecycler.setAdapter(albumEventAdapter);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK) {
//            family = new Gson().fromJson(data.getExtras().getString("DATA"), new TypeToken<ArrayList<SelectFamilys>>() {
//            }.getType());
//            assert family != null;
//            if (family.size() == 1)
//                txt_count.setText("1 Family selected");
//            else if (family.size() > 1)
//                txt_count.setText(family.size() + " Family selected");
//            else
//                postSpinner.setSelection(0);
//        } else if (requestCode == 102 && resultCode == RESULT_OK) {
//            users = new Gson().fromJson(data.getExtras().getString("DATA"), new TypeToken<ArrayList<String>>() {
//            }.getType());
//
//            assert users != null;
//            if (users.size() == 1)
//                txt_count.setText("1 People selected");
//            else if (users.size() > 1)
//                txt_count.setText(users.size() + " People selected");
//            else
//                postSpinner.setSelection(0);
        } else if (requestCode == PICK_IMAGE) {
            if (resultCode == Activity.RESULT_OK && null != data) {
                fileUris = new ArrayList<>();
                no_files.setVisibility(View.GONE);
                fileUris.addAll(Matisse.obtainResult(data));
                albumDocuments.addAll(generateUploadingImageModels(PICK_IMAGE));
                albumEventAdapter.notifyDataSetChanged();
                imgRecycler.setVisibility(View.VISIBLE);
                uploadToServer(MIME_TYPE_IMAGE);
            }
        }

        else if (requestCode == RESULT_DOC) {

            if (data != null && resultCode == RESULT_OK) {
                if (isReadStoragePermissionGranted()) {
                    fileUris = new ArrayList<>();
                    Uri uri = data.getData();
                    fileUris = new ArrayList<>();
                    fileUris.add(uri);
                    no_files.setVisibility(View.GONE);
                  //  fileUris.addAll(data.getParcelableArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS));
                    albumDocuments.addAll(generateUploadingImageModels(RESULT_DOC));
                    albumEventAdapter.notifyDataSetChanged();
                    imgRecycler.setVisibility(View.VISIBLE);
                    uploadToServer(MIME_TYPE_PDF);
                }

            }
        }
        else if (requestCode == RESULT_LOAD_VIDEO) {


            if (data != null && resultCode == RESULT_OK) {
                fileUris = new ArrayList<>();
                no_files.setVisibility(View.GONE);
                imgRecycler.setVisibility(View.VISIBLE);
                fileUris.addAll(Matisse.obtainResult(data));
                if (Matisse.obtainResult(data).size() != 0)
                    // if (isSizeLess(Matisse.obtainResult(data).get(0))) {
                    albumDocuments.addAll(generateUploadingImageModels(RESULT_LOAD_VIDEO));
                albumEventAdapter.notifyDataSetChanged();
                Uri selectedVideoUri = fileUris.get(0);
                String selectedVideoPath = getPath(selectedVideoUri);
                try {
                    File file = new File(selectedVideoPath);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
                    String timeStamp = dateFormat.format(new Date());
                    Bitmap thumb = ThumbnailUtils.createVideoThumbnail(file.getPath(), MediaStore.Video.Thumbnails.MINI_KIND);
                    File thumbFile = new File(getCacheDir(), timeStamp + "_thumb.jpg");
                    boolean s = thumbFile.createNewFile();
                    OutputStream os = new BufferedOutputStream(new FileOutputStream(thumbFile));
                    assert thumb != null;
                    thumb.compress(Bitmap.CompressFormat.JPEG, 70, os);
                    os.close();

                    HistoryImages obj = new HistoryImages();
                    obj.setType("video/wav");
                    obj.setFilename(file.getName());
                    obj.setVideo_thumb("video_thumb/" + thumbFile.getName());
                    historyImages.add(obj);
                    uploadWithTransferUtility(file, "post/" + file.getName(), albumDocuments.size());
                    uploadWithTransferUtility(thumbFile, "video_thumb/" + thumbFile.getName(), -1);
                } catch (Exception e) {
                    Toast.makeText(this,
                            "Unable to find selected file.",
                            Toast.LENGTH_LONG).show();
                }


            }

        }
    }


    private void goToImageGalleryintent() {
        Matisse.from(CreateAnnouncementActivity.this)
                .choose(MimeType.ofImage())
                .showSingleMediaType(true)
                .countable(true)
                .maxSelectable(10)
                .addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
                .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)
                .imageEngine(new GlideEngine())
                .showPreview(true)
                .forResult(PICK_IMAGE);
    }

    private void goToVideoGalleryIntent() {
        Matisse.from(CreateAnnouncementActivity.this)
                .choose(MimeType.ofVideo())
                .showSingleMediaType(true)
                .countable(true)
                .maxSelectable(1)
                .addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
                .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)
                .imageEngine(new GlideEngine())
                .showPreview(true)
                .forResult(RESULT_LOAD_VIDEO);
    }

    void showHideBottom() {
        if (sheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else {
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        }
    }


    @OnClick({R.id.post, R.id.attachment, R.id.txtVideo, R.id.txtCancel, R.id.txtImage, R.id.goBack,R.id.txtDoc})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.post:
                if (validate())
                    createPostRequest();
                break;

            case R.id.attachment:
            case R.id.txtCancel:
                showHideBottom();
                break;
            case R.id.txtVideo:

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    if (isReadWritePermissionGranted()) {
                        askForPermission(RESULT_LOAD_VIDEO);
                    } else {
                        showPermission(RESULT_LOAD_VIDEO);
                    }
                } else {
                    goToVideoGalleryIntent();
                }

                showHideBottom();
                break;
            case R.id.txtImage:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (isReadWritePermissionGranted()) {
                        askForPermission(PICK_IMAGE);
                    } else {
                        showPermission(PICK_IMAGE);
                    }
                } else {
                    goToImageGalleryintent();
                }
                showHideBottom();
                break;

            case R.id.txtDoc:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (isReadWritePermissionGranted()) {
                        askForPermission(RESULT_DOC);
                    } else {
                        showPermission(RESULT_DOC);
                    }
                } else {
                 //   FileUtils.pickDocument(this, RESULT_DOC);
                    pickDocument();
                }
                showHideBottom();
                break;
            case R.id.goBack:
                finish();
                overridePendingTransition(R.anim.left,
                        R.anim.right);
                break;
        }


    }

    @Override
    public void onBackPressed() {
        if(isUploadingImage) {
            int ids = uploadObserver.getId();
            Boolean canceled = transferUtility.cancel(ids);
            transferUtility.cancelAllWithType(TransferType.ANY);
            isUploadingImage=false;
        }
        finish();
        overridePendingTransition(R.anim.left,
                R.anim.right);
    }

    private boolean validate() {
        if (isUploadingImage) {
            Snackbar snackbar = Snackbar.make(constraintLayout, "Uploading images Please Wait a moment.", Snackbar.LENGTH_SHORT);
            snackbar.show();
            return false;
        }
//        if (postSpinner.getSelectedItemPosition() == 0) {
//            Snackbar snackbar = Snackbar.make(constraintLayout, "Please select the 'Announce this to'", Snackbar.LENGTH_SHORT);
//            snackbar.show();
//            return false;
//        }

        if (what_to_post_descrption.getText().toString().trim().equals("")) {
            Snackbar snackbar = Snackbar.make(constraintLayout, "What do you want to announce.", Snackbar.LENGTH_SHORT);
            snackbar.show();
            return false;
        }
        return true;
    }


    @Override
    public void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }

    }

    @Override
    public void showProgressDialog() {
        progressDialog = Utilities.getProgressDialog(this);
        progressDialog.show();

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

    public List<Image> generateUploadingImageModels(int type) {
        List<Image> loadingDocuments = new ArrayList<>();
        for (Uri imageUri : fileUris) {
            Image document = new Image();
            document.setIsuploading(true);
            document.setmUrl(imageUri.toString());
            document.setUrl(false);
            if (type == RESULT_DOC) {
                document.setfileType("application/"+getfileExtension(imageUri));
                document.setDoc(true);
            } else {

                document.setDoc(false);
            }


            loadingDocuments.add(document);
        }
        return loadingDocuments;
    }

    //Dinu(21-04-2021)-for get file Extension
    private String getfileExtension(Uri uri)
    {
        String extension;
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        extension= mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
        return extension;
    }

    private void uploadToServer(String type) {

        if (checkConnection()) {
            isUploadingImage = true;
            ApiServices service = RetrofitBase.createRxResource(this, ApiServices.class);
            List<MultipartBody.Part> parts = new ArrayList<>();
            if (fileUris != null) {
                for (int i = 0; i < fileUris.size(); i++) {
                    File file = getBitmapFromUri(fileUris.get(0));
                   /**15-10-21**//*
                    File file = getBitmapFromUri(fileUris.get(0));
                    if(file.getName().contains(''))
                    getImageDimension(file);
                    ProgressRequestBody fileBody = new ProgressRequestBody(file, "image/png", this, 0);
                    parts.add(MultipartBody.Part.createFormData("file", file.getName(), fileBody));*/

                    if(type==MIME_TYPE_IMAGE){
                        getImageDimension(file);
                        parts.add(prepareFilePartImage(fileUris.get(i), type, i));
                    }
                    else {
                        String extension = "application/" + getfileExtension( fileUris.get( i ) );
                        parts.add( prepareFilePartImage( fileUris.get( i ), extension, i ) );
                    }
                }
            }
            RequestBody description = createPartFromString();
            Call<ResponseBody> call = service.uploadMultiple(description, parts);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    //   hideProgress();
                    if (response.isSuccessful()) {
                        isUploadingImage = false;
                        try {

                            if (type.equals(MIME_TYPE_IMAGE)) {
                                Toast.makeText(CreateAnnouncementActivity.this,
                                        "Images successfully uploaded!", Toast.LENGTH_SHORT).show();

                            }
                            // Modified By: Dinu(05/02/2021) if->else if
                            else if(type.equals(MIME_TYPE_VIDEO)) {
                                Toast.makeText(CreateAnnouncementActivity.this,
                                        "Video successfully uploaded!", Toast.LENGTH_SHORT).show();


                            }
                            else {
                                Toast.makeText(CreateAnnouncementActivity.this,
                                        "Document successfully uploaded!", Toast.LENGTH_SHORT).show();


                            }


                            assert response.body() != null;
                            JSONArray array = new JSONObject(response.body().string()).getJSONArray("data");

                            for (int i = 0; i < array.length(); i++) {
                                HistoryImages obj = new HistoryImages();
                                obj.setType(array.getJSONObject(i).getString("type"));
                                obj.setFilename(array.getJSONObject(i).getString("filename"));

                                if (type.equals(MIME_TYPE_VIDEO)) {
                                    obj.setVideo_thumb(array.getJSONObject(i).getString("video_thumb"));
                                }
                                if (type.equals(MIME_TYPE_PDF)) {
                                    obj.setOriginal_name(File_Name);
                                }
                                if(type.equals(MIME_TYPE_IMAGE)){
                                    /**15-10-21**/
                                    obj.setHeight(sizes.get(i).getHeight() + "");
                                    obj.setWidth(sizes.get(i).getWidth() + "");
                                }
                                historyImages.add(obj);
                            }

                            for (Image image : albumDocuments) {
                                image.setIsuploading(false);
                            }
                            albumEventAdapter.notifyDataSetChanged();

                        } catch (Exception e) {
                            /*
                            Not needed
                             */
                        }

                    } else {
                        isUploadingImage = false;
                        Snackbar.make(findViewById(android.R.id.content),
                                "Some thing wrong", Snackbar.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    //hideProgress();

                    if (type.equals(MIME_TYPE_IMAGE)) {
                        Snackbar.make(findViewById(android.R.id.content),
                                "Image upload failed!", Snackbar.LENGTH_LONG).show();
                    } else {

                        Snackbar.make(findViewById(android.R.id.content),
                                "Video upload failed!", Snackbar.LENGTH_LONG).show();
                    }
                }
            });

        } else {
            Toast.makeText(CreateAnnouncementActivity.this,
                    "Internet connection not available", Toast.LENGTH_SHORT).show();
        }
    }

    @NonNull
    private RequestBody createPartFromString() {
        return RequestBody.create("post", MediaType.parse(MIME_TYPE_TEXT));
    }


    @NonNull
    private MultipartBody.Part prepareFilePartImage(Uri fileUri, String type, int position) {
        try {
        File file = readContentToFile(fileUri);
        //Logging interceptor commented for this to work
        ProgressRequestBody fileBody = new ProgressRequestBody(file, type, this, position + historyImages.size());
        File_Name =getDisplayName( fileUri );
        return MultipartBody.Part.createFormData("file",File_Name, fileBody);
         } catch (Exception e) {
            return null;
        }
    }

    private boolean checkConnection() {
        return ((ConnectivityManager) Objects.requireNonNull(getSystemService
                (CONNECTIVITY_SERVICE))).getActiveNetworkInfo() != null;
    }

    private boolean isReadWritePermissionGranted() {
        return TedPermission.isGranted(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    private void askForPermission(int ctype) {
        type = ctype;
        if ((ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) +
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE))
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) &&
                    ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                Snackbar.make(this.findViewById(android.R.id.content),
                        "Please grant permissions to write data in sdcard",
                        Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                        v -> ActivityCompat.requestPermissions(CreateAnnouncementActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                REQUEST_CODE_PERMISSIONS)).show();
            } else {
                /* Request for permission */
                ActivityCompat.requestPermissions(CreateAnnouncementActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE_PERMISSIONS);
            }

        } else {
            if (ctype == PICK_IMAGE)
                goToImageGalleryintent();
            else if (ctype == RESULT_DOC)
              // FileUtils.pickDocument(this, RESULT_DOC);
                pickDocument();
            else
                goToVideoGalleryIntent();
        }
    }

    //10-05-2021(Dinu) for change document picker
    private void pickDocument() {
        Intent intent = new Intent();
        intent.setAction( Intent.ACTION_OPEN_DOCUMENT );
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        String[] mimeTypes = {FileUtils.MimeTypes.DOC, FileUtils.MimeTypes.DOCX, FileUtils.MimeTypes.PDF, FileUtils.MimeTypes.PPT,
                FileUtils.MimeTypes.PPTX, FileUtils.MimeTypes.XLS, FileUtils.MimeTypes.XLSX,FileUtils.MimeTypes.XLA,
                FileUtils.MimeTypes.zip1,FileUtils.MimeTypes.zip2,FileUtils.MimeTypes.rar1,FileUtils.MimeTypes.rar2,FileUtils.MimeTypes.text1};
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult( Intent.createChooser( intent, "Select Document " ), RESULT_DOC );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (type == PICK_IMAGE)
                    goToImageGalleryintent();
                else
                    goToVideoGalleryIntent();
            } else {
                Toast.makeText(CreateAnnouncementActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    @Override
    public void onAlbumDeleted(int position) {
        historyImages.remove(position);
    }

    public void createPostRequest() {
        showProgressDialog();
        PostRequest request = new PostRequest();
        request.setCategory_id(3 + "");
        request.setCreated_by(SharedPref.getUserRegistration().getId());
        request.setPost_info(new PostInfo());
        request.setType("announcement");
        request.setConversation_enabled(conversation.isChecked());
        request.setIs_shareable(share.isChecked());
        request.setTitle("");
        request.setSnap_description(what_to_post_descrption.getText().toString());

        
        request.setPost_type("only_groups");
        request.setPrivacy_type("public");
        request.setSelected_groups(family);

//        switch (postSpinner.getSelectedItemPosition()) {
//            case 1:
//                request.setPost_type("all_family");
//                request.setPrivacy_type("public");
//                request.setSelected_groups(new ArrayList<>());
//                break;
//            case 2:
//                request.setPost_type("only_groups");
//                request.setPrivacy_type("public");
//                request.setSelected_groups(family);
//                break;
//
//        }
        request.setPost_attachment(historyImages);
        FamilheeyApplication application = FamilheeyApplication.get(this);
        ApiServices apiServices = RetrofitBase.createRxResource(this, ApiServices.class);
        subscriptions.add(apiServices.createPost(request)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(response -> {
                    hideProgressDialog();
                    if (response.code() == 200) {
                        Toast.makeText(CreateAnnouncementActivity.this,
                                "Announcement successfully created", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }, throwable -> {
                    hideProgressDialog();
                }));
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        try {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                View v = getCurrentFocus();
                if (v instanceof EditText) {
                    Rect outRect = new Rect();
                    v.getGlobalVisibleRect(outRect);
                    if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                        v.clearFocus();
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.dispatchTouchEvent(event);
    }

    private boolean isReadStoragePermissionGranted() {
        if (TedPermission.isGranted(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE))
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
                       // FileUtils.pickDocument(CreateAnnouncementActivity.this, RESULT_DOC);
                        pickDocument();
                    }

                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {

                    }
                })
                .setDeniedMessage("If you reject permission,you can not upload images\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();
    }

    @Override
    public void onProgressUpdate(int percentage, int position) {
        if (percentage % 5 == 0) {
            try {
                albumDocuments.get(position).setPrograss(percentage);
                albumEventAdapter.notifyDataSetChanged();
            } catch (Exception e) {

            }
        }
    }


    //*************************************AWS*****************************************

    private File readContentToFile(Uri uri) throws IOException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
        String timeStamp = dateFormat.format(new Date());
        final File file = new File(getCacheDir(), timeStamp + getDisplayName(uri));
        try (
                final InputStream in = getContentResolver().openInputStream(uri);
                final OutputStream out = new FileOutputStream(file, false)
        ) {
            byte[] buffer = new byte[1024];
            assert in != null;
            for (int len; (len = in.read(buffer)) != -1; ) {
                out.write(buffer, 0, len);
            }


            return file;
        }
    }

    private String getDisplayName(Uri uri) {
        final String[] projection = {MediaStore.Images.Media.DISPLAY_NAME};
        try (
                Cursor cursor = getContentResolver().query(uri, projection, null, null, null)
        ) {
            assert cursor != null;
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
            if (cursor.moveToFirst()) {
                return cursor.getString(columnIndex);
            }
        }
        return Objects.requireNonNull(uri.getPath()).replaceAll(" ", "");
    }


    void inits() {
        getApplicationContext().startService(new Intent(getApplicationContext(), TransferService.class));


        AWSMobileClient.getInstance().initialize(this, new com.amazonaws.mobile.client.Callback<UserStateDetails>() {
            @Override
            public void onResult(UserStateDetails result) {

            }

            @Override
            public void onError(Exception e) {
            }
        });

    }


    public void uploadWithTransferUtility(File file, String key, int position) {
        isUploadingImage=true;
        ClientConfiguration configuration = new ClientConfiguration()
                .withMaxErrorRetry(2)
                .withConnectionTimeout(1200000)
                .withSocketTimeout(1200000);

        transferUtility =
                TransferUtility.builder()
                        .context(this)
                        .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                        .s3Client(new AmazonS3Client(AWSMobileClient.getInstance(), Region.getRegion(Regions.US_EAST_1), configuration))
                        .build();

        uploadObserver =
                transferUtility.upload(key
                        , file);

        // Attach a listener to the observer to get state update and progress notifications
        uploadObserver.setTransferListener(new TransferListener() {

            @Override
            public void onStateChanged(int id, TransferState state) {
                if (TransferState.COMPLETED == state) {
                    if (position > 0) {
                        isUploadingImage=false;
                        albumDocuments.get(position-1).setIsuploading(false);
                        albumEventAdapter.notifyDataSetChanged();

                        Toast.makeText(CreateAnnouncementActivity.this,
                                "Video successfully uploaded!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                float percentDonef = ((float) bytesCurrent / (float) bytesTotal) * 100;
                int percentDone = (int) percentDonef;
                if (position > 0) {
                    albumDocuments.get(position-1).setPrograss(percentDone);
                    albumEventAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(int id, Exception ex) {
                if (position > 0) {
                    isUploadingImage = false;
                    albumDocuments.remove(position - 1);
                    albumEventAdapter.notifyDataSetChanged();
                }
            }
        });

    }

    private void showPermission(int type) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialogue_permission);
        Window window = dialog.getWindow();
        assert window != null;
        window.setBackgroundDrawableResource(android.R.color.transparent);
        dialog.findViewById(R.id.btn_cancel).setOnClickListener(view -> dialog.dismiss());
        dialog.findViewById(R.id.btn_continue).setOnClickListener(view -> {
            dialog.dismiss();
            askForPermission(type);
        });
        dialog.show();
    }

    private File getBitmapFromUri(Uri uri) {
        ParcelFileDescriptor parcelFileDescriptor = null;
        try {
            parcelFileDescriptor =
                    getContentResolver().openFileDescriptor(uri, "r");
            assert parcelFileDescriptor != null;
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            parcelFileDescriptor.close();


            File f = new File(getCacheDir(), "share.jpg");
            f.createNewFile();
            OutputStream os = new BufferedOutputStream(new FileOutputStream(f));
            image.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.close();


            return f;
        } catch (Exception e) {
            return null;
        } finally {
            try {
                if (parcelFileDescriptor != null) {
                    parcelFileDescriptor.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void getImageDimension(File file) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        imageSizes obj = new imageSizes();
        obj.setWidth(options.outWidth);
        obj.setHeight(options.outHeight);
        sizes.add(obj);
    }

    public String getPath(Uri uri) {
        Cursor cursor = null;
        try {
            String[] projection = {MediaStore.Images.Media.DATA};
            cursor = getContentResolver().query(uri, projection, null, null, null);

            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

}

