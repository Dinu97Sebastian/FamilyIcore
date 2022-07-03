package com.familheey.app.Announcement;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
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

import com.familheey.app.Adapters.AlbumPostAdapter;
import com.familheey.app.FamilheeyApplication;
import com.familheey.app.Fragments.Posts.PostData;
import com.familheey.app.Interfaces.ProgressListener;
import com.familheey.app.Models.Request.HistoryImages;
import com.familheey.app.Models.Request.Image;
import com.familheey.app.Models.Request.PostInfo;
import com.familheey.app.Models.Request.PostRequest;
import com.familheey.app.Networking.Retrofit.ApiServices;
import com.familheey.app.Networking.Retrofit.RetrofitBase;
import com.familheey.app.R;
import com.familheey.app.Utilities.FileUtils;
import com.familheey.app.Utilities.GifSizeFilter;
import com.familheey.app.Utilities.SharedPref;
import com.familheey.app.Utilities.Utilities;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.filter.Filter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
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

public class EditAnnouncementActivity extends AppCompatActivity implements ProgressListener, AlbumPostAdapter.OnAlbumSelectedListener {


    private final int REQUEST_CODE_PERMISSIONS = 1;
    static final String MIME_TYPE_TEXT = "text/*";
    static final String MIME_TYPE_IMAGE = "image/png";
    static final String MIME_TYPE_VIDEO = "video/*";
    static final String MIME_TYPE_PDF = "application/pdf";
    private static final int RESULT_DOC = 36;
    int PICK_IMAGE = 10;
    int RESULT_LOAD_VIDEO = 11;

    private int type = 0;

    int pos;
    private List<Image> albumDocuments = new ArrayList<>();
    AlbumPostAdapter albumEventAdapter;
    private List<Uri> fileUris = new ArrayList<>();
    public CompositeDisposable subscriptions;
    ArrayList<HistoryImages> historyImages = new ArrayList<>();

    @BindView(R.id.goBack)
    ImageView imageView;


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

    @BindView(R.id.toolBarTitle)
    TextView toolBarTitle;
    private Boolean isUploadingImage = false;
    private PostData data;
    private SweetAlertDialog progressDialog;

    private BottomSheetBehavior sheetBehavior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_announcement);
        ButterKnife.bind(this);
        subscriptions = new CompositeDisposable();


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            data = new Gson().fromJson(bundle.getString("POST"), PostData.class);
            pos = bundle.getInt("pos");
            UidataSet();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE) {
            if (resultCode == Activity.RESULT_OK && null != data) {
                fileUris = new ArrayList<>();
                no_files.setVisibility(View.GONE);
                fileUris.addAll(Matisse.obtainResult(data));
                albumDocuments.addAll(generateUploadingImageModels(PICK_IMAGE));
                albumEventAdapter.notifyDataSetChanged();
                imgRecycler.setVisibility(View.VISIBLE);
                uploadImagesToServer(MIME_TYPE_IMAGE);
            }
        } else if (requestCode == RESULT_DOC) {

            if (data != null && resultCode == RESULT_OK) {
                if (isReadStoragePermissionGranted()) {
                    fileUris = new ArrayList<>();
                    Uri uri = data.getData();
                    fileUris = new ArrayList<>();
                    fileUris.add(uri);
                    no_files.setVisibility(View.GONE);
                  //  fileUris.addAll(Objects.requireNonNull(data.getParcelableArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS)));
                    albumDocuments.addAll(generateUploadingImageModels(RESULT_DOC));
                    albumEventAdapter.notifyDataSetChanged();
                    imgRecycler.setVisibility(View.VISIBLE);
                    uploadImagesToServer(MIME_TYPE_PDF);
                }
            }
        } else if (requestCode == RESULT_LOAD_VIDEO) {
            if (data != null && resultCode == RESULT_OK) {
                fileUris = new ArrayList<>();
                no_files.setVisibility(View.GONE);
                imgRecycler.setVisibility(View.VISIBLE);
                if (Matisse.obtainResult(data).size() != 0)
                    if (Utilities.isSizeLess(EditAnnouncementActivity.this, Matisse.obtainResult(data).get(0))) {
                        fileUris.addAll(Matisse.obtainResult(data));
                        albumDocuments.addAll(generateUploadingImageModels(RESULT_LOAD_VIDEO));
                        albumEventAdapter.notifyDataSetChanged();
                        uploadImagesToServer(MIME_TYPE_VIDEO);
                    }
            }
        }
    }


    private void goToImageGalleryintent() {
        Matisse.from(EditAnnouncementActivity.this)
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
        Matisse.from(EditAnnouncementActivity.this)
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


    @OnClick({R.id.post, R.id.attachment, R.id.txtVideo, R.id.txtCancel, R.id.txtImage, R.id.goBack, R.id.txtDoc})
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
                    askForPermission(RESULT_LOAD_VIDEO);
                } else {
                    goToVideoGalleryIntent();
                }

                showHideBottom();
                break;
            case R.id.txtImage:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    askForPermission(PICK_IMAGE);
                } else {
                    goToImageGalleryintent();
                }
                showHideBottom();
                break;

            case R.id.txtDoc:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    askForPermission(RESULT_DOC);
                } else {
                   // FileUtils.pickDocument(this, RESULT_DOC);
                    pickDocument();
                }
                showHideBottom();
                break;
            case R.id.goBack:
                onBackPressed();
                break;
        }


    }

    private boolean validate() {
        if (isUploadingImage) {
            Snackbar snackbar = Snackbar.make(constraintLayout, "Uploading attachments Please Wait a moment.", Snackbar.LENGTH_SHORT);
            snackbar.show();
            return false;
        }

        if (what_to_post_descrption.getText().toString().trim().isEmpty()){
            Snackbar snackbar = Snackbar.make(constraintLayout, "What do you want to announce?", Snackbar.LENGTH_SHORT);
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

    //Dinu(24-04-2021)-for get file Extension
    private String getfileExtension(Uri uri)
    {
        String extension;
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        extension= mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
        return extension;
    }

    private void uploadImagesToServer(String type) {

        if (checkConnection()) {
            isUploadingImage = true;
            ApiServices service = RetrofitBase.createRxResource(this, ApiServices.class);
            List<MultipartBody.Part> parts = new ArrayList<>();
            if (fileUris != null) {
                for (int i = 0; i < fileUris.size(); i++) {
                    String extension= getfileExtension(fileUris.get(i));
                    if(type==MIME_TYPE_IMAGE){
                        parts.add(prepareFilePart(fileUris.get(i), type));
                    }
                    else{
                        parts.add(prepareFilePart(fileUris.get(i), "application/"+extension));
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
                                Toast.makeText(EditAnnouncementActivity.this,
                                        "Images successfully uploaded!", Toast.LENGTH_SHORT).show();


                            }
                            // Modified By: Dinu(05/02/2021) if->else if
                            else if(type.equals(MIME_TYPE_VIDEO)) {
                                Toast.makeText(EditAnnouncementActivity.this,
                                        "Video successfully uploaded!", Toast.LENGTH_SHORT).show();


                            } else {
                                Toast.makeText(EditAnnouncementActivity.this,
                                        "Document successfully uploaded!", Toast.LENGTH_SHORT).show();


                            }
                            assert response.body() != null;
                            JSONArray array = new JSONObject(response.body().string()).getJSONArray("data");

                            for (int i = 0; i < array.length(); i++) {
                                HistoryImages obj = new HistoryImages();
                                obj.setType(array.getJSONObject(i).getString("type"));
                                obj.setFilename(array.getJSONObject(i).getString("filename"));
                                historyImages.add(obj);
                                if (type.equals(MIME_TYPE_VIDEO)) {
                                    obj.setVideo_thumb(array.getJSONObject(i).getString("video_thumb"));
                                }
                            }

                            for (Image image : albumDocuments) {
                                image.setIsuploading(false);
                            }
                            albumEventAdapter.notifyDataSetChanged();

                        } catch (Exception e) {
                           /*
                           not needed
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
                    Snackbar.make(findViewById(android.R.id.content),
                            "Image upload failed!", Snackbar.LENGTH_LONG).show();
                }
            });

        } else {
            Toast.makeText(EditAnnouncementActivity.this,
                    "Internet connection not available", Toast.LENGTH_SHORT).show();
        }
    }

    @NonNull
    private RequestBody createPartFromString() {
        return RequestBody.create("post", MediaType.parse(MIME_TYPE_TEXT));
    }

    @NonNull
    private MultipartBody.Part prepareFilePart(Uri fileUri, String type) {
        try {
            File file = readContentToFile(fileUri);

            RequestBody requestFile = RequestBody.create(file, MediaType.parse(type));
            return MultipartBody.Part.createFormData("file", getDisplayName( fileUri ), requestFile);
        } catch (Exception e) {
            return null;
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

    private boolean checkConnection() {
        return ((ConnectivityManager) Objects.requireNonNull(getSystemService
                (CONNECTIVITY_SERVICE))).getActiveNetworkInfo() != null;
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
                        v -> ActivityCompat.requestPermissions(EditAnnouncementActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                REQUEST_CODE_PERMISSIONS)).show();
            } else {
                /* Request for permission */
                ActivityCompat.requestPermissions(EditAnnouncementActivity.this,
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (type == PICK_IMAGE)
                    goToImageGalleryintent();
                else
                    goToVideoGalleryIntent();
            } else {
                Toast.makeText(EditAnnouncementActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


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
    public void onAlbumDeleted(int position) {
        historyImages.remove(position);
    }

    public void createPostRequest() {
        showProgressDialog();
        PostRequest request = new PostRequest();
        request.setCategory_id(3 + "");
        request.setUpdateType("single");
        request.setCreated_by(SharedPref.getUserRegistration().getId());
        request.setPost_info(new PostInfo());
        request.setType("announcement");
        request.setPrivacy_type(data.getPrivacy_type());
        request.setPost_ref_id(data.getPost_ref_id());
        request.setConversation_enabled(conversation.isChecked());
        request.setIs_shareable(share.isChecked());
        request.setId(data.getPost_id() + "");
        request.setSnap_description(what_to_post_descrption.getText().toString());

        request.setPost_attachment(historyImages);
        FamilheeyApplication application = FamilheeyApplication.get(this);
        ApiServices apiServices = RetrofitBase.createRxResource(this, ApiServices.class);
        subscriptions.add(apiServices.updatePost(request)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(response -> {
                    hideProgressDialog();
                    if (response.code() == 200) {
                        data.setConversation_enabled(conversation.isChecked());
                        data.setIs_shareable(share.isChecked());
                        data.setPost_attachment(historyImages);
                        data.setSnap_description(what_to_post_descrption.getText().toString());
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("data", new Gson().toJson(data));
                        resultIntent.putExtra("pos", pos);
                        setResult(Activity.RESULT_OK, resultIntent);
                        Toast.makeText(EditAnnouncementActivity.this,
                                "Announcement successfully update", Toast.LENGTH_SHORT).show();
                        finish();


                    }
                }, throwable -> hideProgressDialog()));
    }

    private void UidataSet() {

        CardView bottom_sheet = findViewById(R.id.bottom_sheet);
        sheetBehavior = BottomSheetBehavior.from(bottom_sheet);
        albumEventAdapter = new AlbumPostAdapter(this, albumDocuments);
        GridLayoutManager glm = new GridLayoutManager(this, 1);
        glm.setOrientation(LinearLayoutManager.HORIZONTAL);
        imgRecycler.setLayoutManager(glm);
        imgRecycler.setAdapter(albumEventAdapter);

        what_to_post_descrption.setText(data.getSnap_description());
        toolBarTitle.setText("Update Announcement");
        share.setChecked(data.getIs_shareable());
        conversation.setChecked(data.getConversation_enabled());
        if (data.getPost_attachment().size() > 0) {
            historyImages = data.getPost_attachment();
            for (HistoryImages img : data.getPost_attachment()) {
                Image image = new Image();
                image.setIsuploading(false);
                image.setUrl(true);
                image.setmUrl(img.getFilename());
                if (img.getType().contains("video")) {
                    image.setVideo(true);
                } else {
                    image.setVideo(false);
                }
                if(img.getType().contains("audio")){
                    image.setAudio(true);
                }else {
                    image.setAudio(false);
                }
                if(img.getType().contains("pdf")){
                    image.setfileType(img.getType());
                    image.setDoc(true);
                }
                else if(img.getType().contains("xls")|| img.getType().contains("excel")||img.getType().contains("sheet"))
                {
                    image.setfileType(img.getType());
                    image.setDoc(true);
                }
                else if(img.getType().contains("ppt")||img.getType().contains("presentation")||img.getType().contains("powerpoint"))
                {
                    image.setfileType(img.getType());
                    image.setDoc(true);
                }
                else if(img.getType().contains("doc")||img.getType().contains("word")  && (!img.getType().contains("presentation") || !img.getType().contains("sheet") ||!img.getType().contains("xls")))

                {
                    image.setfileType(img.getType());
                    image.setDoc(true);
                }
                else if(img.getType().contains("zip")||img.getType().contains("rar")||img.getType().contains("octet-stream"))
                {
                    image.setfileType(img.getType());
                    image.setDoc(true);
                }
                else if(img.getType().contains("txt") || img.getType().contains("rtf"))
                {
                    image.setfileType(img.getType());
                    image.setDoc(true);
                }
                else {
                    image.setDoc(false);
                }
                albumDocuments.add(image);
            }
            no_files.setVisibility(View.GONE);
            albumEventAdapter.notifyDataSetChanged();
        }

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
                       // FileUtils.pickDocument(EditAnnouncementActivity.this, RESULT_DOC);
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
}

