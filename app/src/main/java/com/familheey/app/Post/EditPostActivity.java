package com.familheey.app.Post;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.familheey.app.FamilheeyApplication;
import com.familheey.app.Fragments.Posts.PostData;
import com.familheey.app.Interfaces.ProgressListener;
import com.familheey.app.Models.Request.HistoryImages;
import com.familheey.app.Models.Request.Image;
import com.familheey.app.Models.Request.PostRequest;
import com.familheey.app.Models.Response.SelectFamilys;
import com.familheey.app.Models.imageSizes;
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
import com.google.gson.JsonObject;
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
import id.zelory.compressor.Compressor;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditPostActivity extends AppCompatActivity implements ProgressListener, AlbumPostAdapter.OnAlbumSelectedListener {

    public static final int REQUEST_CODE = 101;
    private final int REQUEST_CODE_PERMISSIONS = 1;
    static final String MIME_TYPE_TEXT = "text/*";
    static final String MIME_TYPE_IMAGE = "image/png";
    static final String MIME_TYPE_VIDEO = "video/*";
    static final String MIME_TYPE_PDF = "application/pdf";
    private static final int RESULT_DOC = 36;
    private int UPLOAD_FILE_POSITION = 0;
    int PICK_IMAGE = 10;
    int RESULT_LOAD_VIDEO = 11;
    private  String File_Name = "";
    private int type = 0;
    private int RESULT_AUDIO=111;

    private Boolean isUploadingImage = false;
    private List<Image> albumDocuments = new ArrayList<>();
    AlbumPostAdapter albumEventAdapter;
    private List<Uri> fileUris = new ArrayList<>();
    ArrayList<SelectFamilys> family = new ArrayList<>();
    public CompositeDisposable subscriptions;
    ArrayList<HistoryImages> historyImages = new ArrayList<>();
    ArrayList<imageSizes> sizes = new ArrayList<>();
    ArrayList<Integer> deletedFamilies = new ArrayList<>();

    private PostData data;

    @BindView(R.id.btn_edit)
    ImageView btn_edit;
    @BindView(R.id.goBack)
    ImageView imageView;


    @BindView(R.id.txt_count)
    TextView txt_count;

    @BindView(R.id.toolBarTitle)
    TextView toolBarTitle;

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

    @BindView(R.id.familyview)
    LinearLayout familyview;

    @BindView(R.id.attachment)
    LinearLayout attachment;

    private SweetAlertDialog progressDialog;

    private BottomSheetBehavior sheetBehavior;
    private CardView bottom_sheet;
    int pos;
    public static Switch rating;
    static TransferUtility transferUtility;
    static TransferObserver uploadObserver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);
        rating = (Switch)findViewById(R.id.rating);
        ButterKnife.bind(this);
        subscriptions = new CompositeDisposable();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            data = new Gson().fromJson(bundle.getString("POST"), PostData.class);
            pos = bundle.getInt("pos");
            UidataSet();
            getPost(data.getPost_id() + "");
        }

        inits();
        btn_edit.setOnClickListener(v -> startActivityForResult(new Intent(EditPostActivity.this, SelectFamilesOrPeopleActivity.class).putExtra("from", "POST").putExtra("type", "FAMILY").putExtra("refid", new Gson().toJson(family)), REQUEST_CODE));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // For audio upload-Dinu
        if (requestCode == RESULT_AUDIO ) {
            if (data != null && resultCode == RESULT_OK) {
                fileUris = new ArrayList<>();
                no_files.setVisibility(View.GONE);

                Uri uri = data.getData();
                albumDocuments.addAll(generateUploadingAudioModels(RESULT_AUDIO,uri));
                albumEventAdapter.notifyDataSetChanged();
                imgRecycler.setVisibility(View.VISIBLE);
                try {
                    File file = AudioreadContentToFile(uri);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
                    String timeStamp = dateFormat.format(new Date());
                    //  uploadImageToServer();
                    File thumbFile = new File(getCacheDir(), timeStamp + "_thumb.jpg");
                    thumbFile.createNewFile();

                    HistoryImages obj = new HistoryImages();
                    obj.setType("audio/mp3");
                    obj.setFilename(file.getName());
                    obj.setVideo_thumb("audio_thumb/" + thumbFile.getName());
                    historyImages.add(obj);
                    uploadWithTransferUtility(file, "post/" + file.getName(), albumDocuments.size(), MIME_TYPE_VIDEO);
                    uploadWithTransferUtility(thumbFile, "audio_thumb/" + thumbFile.getName(), -1, MIME_TYPE_VIDEO);


                } catch (Exception e) {
                    Toast.makeText(this,
                            "Unable to find selected file.",
                            Toast.LENGTH_LONG).show();
                }

            }
        }
        if (requestCode == PICK_IMAGE) {
            if (resultCode == Activity.RESULT_OK && null != data) {
                fileUris = new ArrayList<>();
                no_files.setVisibility(View.GONE);
                fileUris.addAll(Matisse.obtainResult(data));
                albumDocuments.addAll(generateUploadingImageModels(PICK_IMAGE));
                albumEventAdapter.notifyDataSetChanged();
                imgRecycler.setVisibility(View.VISIBLE);
                try {
                    UPLOAD_FILE_POSITION = 0;
                    new ImageCompressionAsyncTask(this).execute(readContentToFile(fileUris.get(0)));
                } catch (Exception e) {

                }
            }
        } else if (requestCode == RESULT_DOC) {
            if (data == null)
                return;

            if (isReadStoragePermissionGranted()) {

                if (resultCode == RESULT_OK) {
                    fileUris = new ArrayList<>();
                    Uri uri = data.getData();
                    fileUris = new ArrayList<>();
                    fileUris.add(uri);
                    //  fileUris.addAll(data.getParcelableArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS));
                    no_files.setVisibility(View.GONE);
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
                fileUris.addAll(Matisse.obtainResult(data));
                Uri selectedVideoUri = fileUris.get(0);
                String selectedVideoPath = getPath(selectedVideoUri);
                if (Matisse.obtainResult(data).size() != 0)
                    // if (isSizeLess(Matisse.obtainResult(data).get(0))) {
                    albumDocuments.addAll(generateUploadingImageModels(RESULT_LOAD_VIDEO));
                albumEventAdapter.notifyDataSetChanged();

                try {
                    File file = new File(selectedVideoPath);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
                    String timeStamp = dateFormat.format(new Date());
                    Bitmap thumb = ThumbnailUtils.createVideoThumbnail(file.getPath(), MediaStore.Video.Thumbnails.MINI_KIND);
                    File thumbFile = new File(getCacheDir(), timeStamp + "_thumb.jpg");
                    thumbFile.createNewFile();
                    OutputStream os = new BufferedOutputStream(new FileOutputStream(thumbFile));
                    thumb.compress(Bitmap.CompressFormat.JPEG, 70, os);
                    os.close();

                    HistoryImages obj = new HistoryImages();
                    obj.setType("video/wav");
                    obj.setFilename(file.getName());
                    obj.setVideo_thumb("video_thumb/" + thumbFile.getName());
                    historyImages.add(obj);
                    uploadWithTransferUtility(file, "post/" + file.getName(), albumDocuments.size(), MIME_TYPE_VIDEO);
                    uploadWithTransferUtility(thumbFile, "video_thumb/" + thumbFile.getName(), -1, MIME_TYPE_VIDEO);
                } catch (Exception e) {
                    Toast.makeText(this,
                            "Unable to find selected file.",
                            Toast.LENGTH_LONG).show();
                }
            }
        } else if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            family = new Gson().fromJson(data.getExtras().getString("DATA"), new TypeToken<ArrayList<SelectFamilys>>() {
            }.getType());
//            if (family.size() == 1)
//                txt_count.setText("1 Family selected");
//            else if (family.size() > 1)
//                txt_count.setText(family.size() + " Family selected");
//            else {
//
//                txt_count.setText("");
//            }
            deletedFamilies.clear();
            ArrayList<Integer> selectedFamiliesNow = new ArrayList<>();
            for (SelectFamilys familySelected : family) {
                selectedFamiliesNow.add(Integer.parseInt(familySelected.getId()));
            }
            for (SelectFamilys processingRecentFamilies : this.data.getFamilyList()) {
                if (!selectedFamiliesNow.contains(Integer.parseInt(processingRecentFamilies.getId()))) {
                    deletedFamilies.add(Integer.parseInt(processingRecentFamilies.getId()));
                }
            }


            for (SelectFamilys familySelected : family) {
                for (SelectFamilys obj : this.data.getFamilyList()) {
                    if (familySelected.getId().equals(obj.getId())) {
                        familySelected.setType("old");
                    }
                }
            }


            for (SelectFamilys familySelected : family) {
                if (familySelected.getType() == null) {
                    familySelected.setType("new");
                }
            }

        }
    }
    private File AudioreadContentToFile(Uri uri) throws IOException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
        String timeStamp = dateFormat.format(new Date());
        final File file = new File(getCacheDir(), "Audio_Post-"+timeStamp+".mp3");
        try (
                final InputStream in = getContentResolver().openInputStream(uri);
                final OutputStream out = new FileOutputStream(file, false)
        ) {
            byte[] buffer = new byte[1024];
            for (int len; (len = in.read(buffer)) != -1; ) {
                out.write(buffer, 0, len);
            }


            return file;
        }
    }

    private void goToImageGalleryintent() {
        Matisse.from(EditPostActivity.this)
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
        Matisse.from(EditPostActivity.this)
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


    @OnClick({R.id.post, R.id.attachment, R.id.txtVideo, R.id.txtCancel, R.id.txtImage, R.id.goBack, R.id.txtDoc,R.id.txtAudio})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.txtAudio:
                Intent intent = new Intent();
                intent.setType("audio/mpeg");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Audio "), RESULT_AUDIO);

                showHideBottom();
                break;
            case R.id.post:
                if (validate())
                    createPostRequest();
                break;

            case R.id.attachment:
            case R.id.txtCancel:
                if(rating.isChecked()==false) {
                    showHideBottom();
                }else if(rating.isChecked() && historyImages.size()==0){
                    showHideBottom();
                }
                break;
            case R.id.txtVideo:

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    askForPermission(RESULT_LOAD_VIDEO);
                } else {
                    goToVideoGalleryIntent();
                }

                showHideBottom();
                break;
            case R.id.txtDoc:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    askForPermission(RESULT_DOC);
                } else {
                    //  FileUtils.pickDocument(this, RESULT_DOC);
                    pickDocument();
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

            case R.id.goBack:
                onBackPressed();
                break;
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
                FileUtils.MimeTypes.zip1,FileUtils.MimeTypes.zip2,FileUtils.MimeTypes.text1,FileUtils.MimeTypes.rar1,FileUtils.MimeTypes.rar2};
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult( Intent.createChooser( intent, "Select Document " ), RESULT_DOC );
    }

    private boolean validate() {
        if (isUploadingImage) {
            Snackbar snackbar = Snackbar.make(constraintLayout, "Uploading images Please Wait a moment.", Snackbar.LENGTH_SHORT);
            snackbar.show();
            return false;
        }


        if (historyImages.size() == 0 && what_to_post_descrption.getText().toString().equals("")) {
            Snackbar snackbar = Snackbar.make(constraintLayout, "Please add description or Attachment", Snackbar.LENGTH_SHORT);
            snackbar.show();
            return false;
        }

        /*Megha(23/08/2021)-> rating validation*/
        if ( rating.isChecked()&&historyImages.size()>1){
            Snackbar snackbar = Snackbar.make(constraintLayout, "Rating is enabled. Only one attachment is allowed.", Snackbar.LENGTH_SHORT);
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
    //Dinu(24-04-2021)-for get file Extension
    private String getfileExtension(Uri uri)
    {
        String extension;
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        extension= mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
        return extension;
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


    private void uploadImagesToServer(String type) {
        if (checkConnection()) {
            isUploadingImage = true;
            ApiServices service = RetrofitBase.createRxResource(this, ApiServices.class);
            List<MultipartBody.Part> parts = new ArrayList<>();
            if (fileUris != null) {
                for (int i = 0; i < fileUris.size(); i++) {
                    String extension= getfileExtension(fileUris.get(i));
                    parts.add(prepareFilePart(fileUris.get(i), "application/"+extension));

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
                                Toast.makeText(EditPostActivity.this,
                                        "Images successfully uploaded!", Toast.LENGTH_SHORT).show();
                            }
                            if (type.equals(MIME_TYPE_VIDEO)) {
                                Toast.makeText(EditPostActivity.this,
                                        "Video successfully uploaded!", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(EditPostActivity.this,
                                        "Document successfully uploaded!", Toast.LENGTH_SHORT).show();
                            }
                            assert response.body() != null;
                            JSONArray array = new JSONObject(response.body().string()).getJSONArray("data");

                            for (int i = 0; i < array.length(); i++) {
                                HistoryImages obj = new HistoryImages();
                                obj.setType(array.getJSONObject(i).getString("type"));
                                obj.setFilename(array.getJSONObject(i).getString("filename"));

                                if (type.equals(MIME_TYPE_IMAGE)) {
                                    obj.setHeight(sizes.get(i).getHeight() + "");
                                    obj.setWidth(sizes.get(i).getWidth() + "");
                                } else if (type.equals(MIME_TYPE_PDF)) {
                                    obj.setOriginal_name(File_Name);
                                }

                                historyImages.add(obj);
                            }

                            for (Image image : albumDocuments) {
                                image.setIsuploading(false);
                            }
                            sizes.clear();
                            albumEventAdapter.notifyDataSetChanged();

                        } catch (Exception e) {
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
            Toast.makeText(EditPostActivity.this,
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
            File_Name =getDisplayName(fileUri);
            if (type.equals("MIME_TYPE_IMAGE")) {
                getImageDimension(file);
            }
            RequestBody requestFile = RequestBody.create(file, MediaType.parse(type));
            return MultipartBody.Part.createFormData("file", File_Name, requestFile);
        } catch (Exception e) {
            return null;
        }
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
                        v -> ActivityCompat.requestPermissions(EditPostActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                REQUEST_CODE_PERMISSIONS)).show();
            } else {
                /* Request for permission */
                ActivityCompat.requestPermissions(EditPostActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE_PERMISSIONS);
            }

        } else {
            if (ctype == PICK_IMAGE)
                goToImageGalleryintent();
            else if (ctype == RESULT_DOC)
                //   FileUtils.pickDocument(this, RESULT_DOC);
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
                Toast.makeText(EditPostActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    @Override
    public void onAlbumDeleted(int position) {
        /*
         * changed for solving the crash issue while deleting the attachment of already existing posts*/
        if(historyImages.size()>0 && !rating.isChecked()) {
            historyImages.remove(position);
            albumDocuments.remove(position);
            albumEventAdapter.notifyDataSetChanged();
        }
        else if((historyImages.size()==1 )&& (data.getRating_enabled()!=null)){
            Snackbar snackbar = Snackbar.make(constraintLayout, "You can't delete this attachment", Snackbar.LENGTH_SHORT);
            snackbar.show();
        }
        else if ((data.getPost_attachment().size() == historyImages.size() )&& (data.getRating_enabled()==null)){
            historyImages.remove(position);
            albumDocuments.remove(position);
            albumEventAdapter.notifyDataSetChanged();
        }
        else{
            historyImages.remove(position);
            albumDocuments.remove(position);
            albumEventAdapter.notifyDataSetChanged();
        }
    }

    public void createPostRequest() {
        showProgressDialog();
        PostRequest request = new PostRequest();
        request.setId(data.getPost_id() + "");
        request.setType_id(data.getPost_id() + "");
        request.setCreated_by(SharedPref.getUserRegistration().getId());
        request.setType("post");
        request.setPost_ref_id(data.getPost_ref_id());
        request.setConversation_enabled(conversation.isChecked());
        request.setIs_shareable(share.isChecked());
        request.setSnap_description(what_to_post_descrption.getText().toString());
        request.setUpdateType("multiple");
        request.setPrivacy_type(data.getPrivacy_type());
        request.setInactive_active_array(data.getInactive_active_array());
        request.setTo_group_id_array(family);
        request.setDeletedFamily(deletedFamilies);
        request.setPost_attachment(historyImages);
        request.setPost_type("only_groups");
        request.setSelected_groups(family);

        if (rating.isChecked()){
            request.setRating_enabled(true);
        }else{
            request.setRating_enabled(false);
        }

        if (data.getFamilyList() != null && data.getFamilyList().size() > 0) {
            request.setPost_type("only_groups");
            request.setSelected_groups(family);
        }

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
                        data.setRating_enabled(rating.isChecked());

                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("data", new Gson().toJson(data));
                        resultIntent.putExtra("pos", pos);
                        setResult(Activity.RESULT_OK, resultIntent);
                        Toast.makeText(EditPostActivity.this,
                                "Post successfully updated", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                        hideProgressDialog();
                    }
                }, throwable -> hideProgressDialog()));
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


    private void UidataSet() {

        bottom_sheet = findViewById(R.id.bottom_sheet);
        sheetBehavior = BottomSheetBehavior.from(bottom_sheet);
        albumEventAdapter = new AlbumPostAdapter(this, albumDocuments,"editPost");
        GridLayoutManager glm = new GridLayoutManager(this, 1);
        glm.setOrientation(LinearLayoutManager.HORIZONTAL);
        imgRecycler.setLayoutManager(glm);
        imgRecycler.setAdapter(albumEventAdapter);

        what_to_post_descrption.setText(data.getSnap_description());
        toolBarTitle.setText("Update Post");
        share.setChecked(data.getIs_shareable());
        conversation.setChecked(data.getConversation_enabled());

        /*04/08/21->for setting rating*/
        if (data.getRating_enabled()){
            rating.setEnabled(false);
            rating.setChecked(data.rating_enabled);
        }else{
            rating.setEnabled(true);
            rating.setChecked(!data.rating_enabled);
            rating.setChecked(false);
        }
        if (data.getPost_attachment().size() > 0) {
            historyImages = data.getPost_attachment();
            for (HistoryImages img : data.getPost_attachment()) {
                Image image = new Image();
                image.setIsuploading(false);
                image.setUrl(true);
                image.setmUrl(img.getFilename());

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
                else if(img.getType().contains("audio")){
                    image.setAudio(true);
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


    private void getPost(String id) {
        showProgressDialog();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", id);
        jsonObject.addProperty("type", "post");
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        FamilheeyApplication application = FamilheeyApplication.get(this);
        ApiServices apiServices = RetrofitBase.createRxResource(this, ApiServices.class);
        subscriptions.add(apiServices.getMyPostWithSelectedFamilies(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(response -> {
                    if (response.body().getData().size() > 0) {
                        hideProgressDialog();
                        data = response.body().getData().get(0);
//                        if (data.getFamilyList() != null && data.getFamilyList().size() > 0) {
//                            familyview.setVisibility(View.VISIBLE);
//                            txt_count.setText(data.getFamilyList().size() + " Family selected");
//                            family = data.getFamilyList();
//                        }
                    }

                }, throwable -> {
                    hideProgressDialog();
                }));
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
                        //  FileUtils.pickDocument(EditPostActivity.this, RESULT_DOC);
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
        return uri.getPath().replaceAll(" ", "");
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


    public void uploadWithTransferUtility(File file, String key, int position,String type) {
        isUploadingImage = true;
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
                        isUploadingImage = false;
                        albumDocuments.get(position-1).setIsuploading(false);
                        albumEventAdapter.notifyDataSetChanged();
                        if (type.equals(MIME_TYPE_IMAGE)) {
                            compressNextFile();
                        }
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
                    albumDocuments.remove(position-1);
                    albumEventAdapter.notifyDataSetChanged();
                    isUploadingImage = true;
                }
            }

        });
        if (TransferState.COMPLETED == uploadObserver.getState()) {

        }
    }
    //*************************IMAGE COMPRESS*************************


    class ImageCompressionAsyncTask extends AsyncTask<File, Integer, File> {

        Context mContext;

        public ImageCompressionAsyncTask(Context context) {
            mContext = context;
        }

        @Override
        protected File doInBackground(File... params) {
            try {
                File compressedFile = new Compressor(mContext)
                        .setQuality(60)
                        .compressToFile(params[0]);

                for(int i = 0; i <= 85 ;i++){
                    try {
                        Thread.sleep(10);
                    } catch (Exception e) {
                    }

                    publishProgress(i);
                }

                return compressedFile;
            } catch (Exception e) {
                return params[0];
            }

        }

        @Override
        protected void onPostExecute(File s) {

            try {
                uploadWithTransferUtility(s, "post/" + s.getName().replaceAll(" ", ""), historyImages.size() + 1, MIME_TYPE_IMAGE);
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(s));
                HistoryImages obj1 = new HistoryImages();
                obj1.setType("image/png");
                obj1.setFilename(s.getName().replaceAll(" ", ""));
                obj1.setHeight(bitmap.getHeight() + "");
                obj1.setWidth(bitmap.getWidth() + "");
                historyImages.add(obj1);
                bitmap.recycle();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            albumDocuments.get(historyImages.size()).setPrograss(values[0]);
            albumEventAdapter.notifyDataSetChanged();
        }
    }

    private void compressNextFile(){
        if(fileUris.size()>UPLOAD_FILE_POSITION+1){
            isUploadingImage = true;
            UPLOAD_FILE_POSITION+=1;
            try {
                new ImageCompressionAsyncTask(this).execute(readContentToFile(fileUris.get(UPLOAD_FILE_POSITION)));
            } catch (Exception e) {

            }
        }
    }
    public List<Image> generateUploadingAudioModels(int type,Uri uri) {
        List<Image> loadingDocuments = new ArrayList<>();

        Image document = new Image();
        document.setIsuploading(true);
        document.setmUrl(uri.toString());
        document.setUrl(false);
        if (type == RESULT_AUDIO) {
            document.setAudio(true);
        } else {

            document.setAudio(false);
        }
        loadingDocuments.add(document);

        return loadingDocuments;
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
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(isUploadingImage) {
            int ids = uploadObserver.getId();
            Boolean canceled = transferUtility.cancel(ids);
            transferUtility.cancelAllWithType(TransferType.ANY);
            isUploadingImage=false;
        }
    }
}

