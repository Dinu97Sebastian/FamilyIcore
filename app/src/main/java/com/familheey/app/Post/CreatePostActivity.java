package com.familheey.app.Post;

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
import android.graphics.Color;
import android.graphics.Rect;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
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
import androidx.annotation.RequiresApi;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.familheey.app.Adapters.AlbumPostAdapter;
import com.familheey.app.CustomViews.FSpinner;
import com.familheey.app.FamilheeyApplication;
import com.familheey.app.Interfaces.ProgressListener;
import com.familheey.app.Interfaces.RetrofitListener;
import com.familheey.app.Models.ApiCallbackParams;
import com.familheey.app.Models.ChatModel;
import com.familheey.app.Models.ErrorData;
import com.familheey.app.Models.Request.HistoryImages;
import com.familheey.app.Models.Request.Image;
import com.familheey.app.Models.Request.PostInfo;
import com.familheey.app.Models.Request.PostRequest;
import com.familheey.app.Models.Response.SelectFamilys;
import com.familheey.app.Models.imageSizes;
import com.familheey.app.Networking.Retrofit.ApiServiceProvider;
import com.familheey.app.Networking.Retrofit.ApiServices;
import com.familheey.app.Networking.Retrofit.RetrofitBase;
import com.familheey.app.R;
import com.familheey.app.Utilities.Constants;
import com.familheey.app.Utilities.FileUtil;
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
import id.zelory.compressor.Compressor;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;

import static com.familheey.app.Utilities.Constants.ApiPaths.IMAGE_BASE_URL;

public class
CreatePostActivity extends AppCompatActivity implements ProgressListener, AlbumPostAdapter.OnAlbumSelectedListener, ProgressRequestBody.UploadCallbacks{
    private static final String TAG = "CreatePostActivity";
    //private Uri compressedUri;
    private final int REQUEST_CODE_PERMISSIONS = 1;
    private final String MIME_TYPE_TEXT = "text/*";
    private final String MIME_TYPE_VIDEO = "video/*";
    private final String MIME_TYPE_IMAGE = "image/png";
    List<MultipartBody.Part> parts;
    private final String MIME_TYPE_PDF = "application/pdf";
    //final int FILE_REQUEST_CODE = 706;
    private final int RESULT_DOC = 36;
    int uploadCallCount = 0;
    Call<ResponseBody> call;
    private int type = 0;
    private Boolean isSpinnerSelection = true;
    private Boolean isUploadingImage = false;
    private String File_Name = "";
    private AlbumPostAdapter albumEventAdapter;
    private List<Uri> fileUris = new ArrayList<>();
    private ArrayList<SelectFamilys> family;
    public CompositeDisposable subscriptions;
    private int PICK_IMAGE = 10;
    private int RESULT_LOAD_VIDEO = 11;
    public static final int POST_CREATE_REQUEST_CODE = 101;
    private Intent receiverdIntent;
    private int UPLOAD_FILE_POSITION = 0;
    private int RESULT_AUDIO=111;
    @BindView(R.id.goBack)
    ImageView imageView;

    @BindView(R.id.txt_count)
    TextView txt_count;

    @BindView(R.id.txtDoc)
    TextView txtDoc;
    @BindView(R.id.txtVideo)
    TextView txtVideo;
    @BindView(R.id.txtAudio)
    TextView txtAudio;


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
    private List<Image> albumDocuments = new ArrayList<>();

    String familyId = "";
    String post_create = "";
    private SweetAlertDialog progressDialog;

    private BottomSheetBehavior sheetBehavior;
    ArrayAdapter spinnerArrayAdapter;
    private ArrayList<HistoryImages> historyImages = new ArrayList<>();
    private ArrayList<imageSizes> sizes = new ArrayList<>();
    @BindView(R.id.post_spinner)
    FSpinner postSpinner;
    @BindView(R.id.post_this)
    TextView post_this;
    @BindView(R.id.postThisToMandatoryIndication)
    TextView postThisToMandatoryIndication;
    private Boolean shared = false;
    public static Switch rating;
    static TransferUtility transferUtility;
    static TransferObserver uploadObserver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        rating = (Switch)findViewById(R.id.rating);
        ButterKnife.bind(this);
        inits();
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        sweetAlertDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        sweetAlertDialog.setTitleText("Optimizing video for upload.");
        sweetAlertDialog.setCancelable(false);
        subscriptions = new CompositeDisposable();
        Bundle b = getIntent().getExtras();

        if (SharedPref.userHasFamily()) {
            String[] options = {"Select", "Selected Families", "Only Me"};
            spinnerArrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, options);
        } else {
            String[] options = {"Select","Only Me"};
            spinnerArrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, options);
        }

        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        postSpinner.setAdapter(spinnerArrayAdapter);

        if (b != null && "FAMILY".equals(b.getString("FROM"))) {
            familyId = b.getString("id");
            post_create = b.getString("post_create");
            family = new ArrayList<>();
            SelectFamilys obj = new SelectFamilys();
            obj.setId(familyId);
            obj.setPost_create(post_create);
            family.add(obj);
            isSpinnerSelection = false;
        } else if (b != null && "CHAT".equals(b.getString("FROM"))) {
            shared=true;
            post_this.setVisibility( View.VISIBLE );
            postThisToMandatoryIndication.setVisibility( View.VISIBLE );
            txt_count.setVisibility( View.VISIBLE );
            postSpinner.setVisibility( View.VISIBLE );
            ChatModel chat = new Gson().fromJson(b.getString(Constants.Bundle.DATA), ChatModel.class);
            if (chat.getChatText() != null && !chat.getChatText().isEmpty()) {
                what_to_post_descrption.setText(chat.getChatText());
            } else if (chat.getFilename() != null && !chat.getFilename().isEmpty()) {
                if (chat.getFilename().toLowerCase().contains("mp4") || chat.getFilename().toLowerCase().contains("mov") || chat.getFilename().toLowerCase().contains("wmv") || chat.getFilename().toLowerCase().contains("webm") || chat.getFilename().toLowerCase().contains("mkv") || chat.getFilename().toLowerCase().contains("flv") || chat.getFilename().toLowerCase().contains("avi")) {
                    // copy_file_between_bucket("file_name/" + chat.getFilename(), "post/" +chat.getFilename());
                }
//                else if(chat.getType().toLowerCase().contains("txt") || chat.getType().toLowerCase().contains("xls")||chat.getType().toLowerCase().contains("pdf")||(chat.getType().toLowerCase().contains("doc") ||(chat.getType().toLowerCase().contains("word") )||chat.getType().toLowerCase().contains("ppt")||chat.getType().toLowerCase().contains("zip")||chat.getType().toLowerCase().contains("rar")){
//               // else if (chat.getFilename().toLowerCase().contains("docx") || chat.getFilename().toLowerCase().contains("odt") || chat.getFilename().toLowerCase().contains("ott") || chat.getFilename().toLowerCase().contains("pdf") || chat.getFilename().toLowerCase().contains("txt") || chat.getFilename().toLowerCase().contains("doc")) {
//                    copy_file_between_bucket("file_name/" + chat.getFilename(), chat.getFilename(), true,chat.getType());
//                } else {
//
//                }
                // Dinu (03-05-2021): for handle document type
                else if(chat.getType().toLowerCase().contains("pdf")){
                    copy_file_between_bucket("file_name/" + chat.getFilename(), chat.getFilename(), true,chat.getType());
                }
                else if(chat.getType().toLowerCase().contains("xls")|| chat.getType().toLowerCase().contains("excel")||chat.getType().toLowerCase().contains("sheet"))
                {
                    copy_file_between_bucket("file_name/" + chat.getFilename(), chat.getFilename(), true,chat.getType());
                }
                else if(chat.getType().toLowerCase().contains("ppt")||chat.getType().toLowerCase().contains("presentation")||chat.getType().toLowerCase().contains("powerpoint"))
                {
                    copy_file_between_bucket("file_name/" + chat.getFilename(), chat.getFilename(), true,chat.getType());
                }
                else if(chat.getType().toLowerCase().contains("doc")||chat.getType().toLowerCase().contains("word")  && (!chat.getType().toLowerCase().contains("presentation") || !chat.getType().toLowerCase().contains("sheet") ||!chat.getType().toLowerCase().contains("xls")))

                {
                    copy_file_between_bucket("file_name/" + chat.getFilename(), chat.getFilename(), true,chat.getType());
                }
                else if(chat.getType().toLowerCase().contains("zip")||chat.getType().toLowerCase().contains("rar")||chat.getType().toLowerCase().contains("octet-stream"))
                {
                    copy_file_between_bucket("file_name/" + chat.getFilename(), chat.getFilename(), true,chat.getType());
                }
                else if(chat.getType().toLowerCase().contains("txt") || chat.getType().toLowerCase().contains("rtf"))
                {
                    copy_file_between_bucket("file_name/" + chat.getFilename(), chat.getFilename(), true,chat.getType());
                }
                else {
                    setImageWidthandSizeINForward(IMAGE_BASE_URL + "file_name/" + chat.getFilename(), chat);
                }
            }
        }

        postSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (SharedPref.userHasFamily()) {
                    if (position == 1) {
                        if (isSpinnerSelection) {
                            txt_count.setText("");
                            if (family != null && family.size() > 0) {
                                startActivityForResult(new Intent(CreatePostActivity.this, SelectFamilesOrPeopleActivity.class).putExtra("from", "POST").putExtra("type", "FAMILY").putExtra("refid", new Gson().toJson(family)), POST_CREATE_REQUEST_CODE);
                            } else {
                                startActivityForResult(new Intent(CreatePostActivity.this, SelectFamilesOrPeopleActivity.class).putExtra("from", "POST").putExtra("type", "FAMILY"), POST_CREATE_REQUEST_CODE);
                            }
                        }
                        isSpinnerSelection = true;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                if (postSpinner.getSelectedItemPosition() == 1) {
                    txt_count.setText("");
                    startActivityForResult(new Intent(CreatePostActivity.this, SelectFamilesOrPeopleActivity.class).putExtra("from", "POST").putExtra("type", "FAMILY"), POST_CREATE_REQUEST_CODE);
                }
            }
        });
        CardView bottom_sheet = findViewById(R.id.bottom_sheet);
        sheetBehavior = BottomSheetBehavior.from(bottom_sheet);
        albumEventAdapter = new AlbumPostAdapter(this, albumDocuments);
        GridLayoutManager glm = new GridLayoutManager(this, 1);
        glm.setOrientation(LinearLayoutManager.HORIZONTAL);
        imgRecycler.setLayoutManager(glm);
        imgRecycler.setAdapter(albumEventAdapter);
        onSharedIntent();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // For upload audio(Dinu)
        if (requestCode == RESULT_AUDIO ) {
            if (data != null && resultCode == RESULT_OK) {
                fileUris = new ArrayList<>();
                no_files.setVisibility(View.GONE);

                Uri uri = data.getData();
                String path=uri.getPath();
                albumDocuments.addAll(generateUploadingAudioModels(RESULT_AUDIO,uri));
                albumEventAdapter.notifyDataSetChanged();
                imgRecycler.setVisibility(View.VISIBLE);
                try {
                    File file = AudioreadContentToFile(uri);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
                    String timeStamp = dateFormat.format(new Date());
                    File thumbFile = new File(getCacheDir(), timeStamp + "_thumb.jpg");

                    HistoryImages obj = new HistoryImages();
                    obj.setType("audio/mp3");
                    obj.setFilename(file.getName());
                    obj.setVideo_thumb("audio_thumb/" + thumbFile.getName());
                    historyImages.add(obj);
                    uploadWithTransferUtility(file, "post/" + file.getName(), albumDocuments.size(), MIME_TYPE_VIDEO);
                    uploadWithTransferUtility(thumbFile, "audio_thumb/" + thumbFile.getName(), -1, MIME_TYPE_VIDEO);


                } catch (Exception e) {
                    Log.e(TAG, "Unable to upload file from the given uri", e);
                }

            }
        }
        if (requestCode == POST_CREATE_REQUEST_CODE && resultCode == RESULT_OK) {
            assert data != null;
            family = new Gson().fromJson(Objects.requireNonNull(data.getExtras()).getString("DATA"), new TypeToken<ArrayList<SelectFamilys>>() {
            }.getType());
            assert family != null;
            if (family.size() == 1)
                txt_count.setText("1 Family selected");
            else if (family.size() > 1)
                txt_count.setText(family.size() + " Family selected");
            else
                postSpinner.setSelection(0);
        }

        else if (requestCode == PICK_IMAGE) {
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
            if (data != null && resultCode == RESULT_OK) {
                fileUris = new ArrayList<>();
                no_files.setVisibility(View.GONE);
                //  fileUris.addAll(data.getParcelableArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS));
                Uri uri = data.getData();
                fileUris = new ArrayList<>();
                fileUris.add(uri);

                albumDocuments.addAll(generateUploadingImageModels(RESULT_DOC));
                albumEventAdapter.notifyDataSetChanged();
                imgRecycler.setVisibility(View.VISIBLE);
                uploadImageToServer();

            }
        } else if (requestCode == RESULT_LOAD_VIDEO) {
            if (data != null && resultCode == RESULT_OK) {
                fileUris = new ArrayList<>();
                no_files.setVisibility(View.GONE);
                imgRecycler.setVisibility(View.VISIBLE);
                fileUris.addAll(Matisse.obtainResult(data));
                if (Matisse.obtainResult(data).size() != 0)
                    // if (isSizeLess(Matisse.obtainResult(data).get(0))) {
                    albumDocuments.addAll(generateUploadingImageModels(RESULT_LOAD_VIDEO));
                albumEventAdapter.notifyDataSetChanged();

                try {
                    File file = readContentToFile(fileUris.get(0));
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
                    String timeStamp = dateFormat.format(new Date());
                    Bitmap thumb = ThumbnailUtils.createVideoThumbnail(file.getPath(), MediaStore.Video.Thumbnails.MINI_KIND);
                    File thumbFile = new File(getCacheDir(), timeStamp + "_thumb.jpg");
                    thumbFile.createNewFile();
                    OutputStream os = new BufferedOutputStream(new FileOutputStream(thumbFile));
                    assert thumb != null;
                    thumb.compress(Bitmap.CompressFormat.JPEG, 70, os);
                    os.close();

                    HistoryImages obj = new HistoryImages();
                    obj.setType("video/wav");
                    obj.setFilename(timeStamp+"."+getfileExtension(fileUris.get(0)));
                    obj.setVideo_thumb("video_thumb/" + thumbFile.getName());
                    historyImages.add(obj);
                    uploadWithTransferUtility(file, "post/" + obj.getFilename(), albumDocuments.size(), MIME_TYPE_VIDEO);
                    uploadWithTransferUtility(thumbFile, "video_thumb/" + thumbFile.getName(), -1, MIME_TYPE_VIDEO);
                } catch (Exception e) {
                    Toast.makeText(this,
                            "Unable to find selected file.",
                            Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Unable to upload file from the given uri", e);
                }

            }
        }

    }

    private void uploadImageToServer() {
        sizes.clear();
        uploadCallCount = 0;
        if (checkConnection()) {
            isUploadingImage = true;
            ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(this);

            parts = new ArrayList<>();
            if (fileUris != null) {
                for (int i = 0; i < fileUris.size(); i++) {

                    String extension= getfileExtension(fileUris.get(i));
                    parts.add(prepareFilePartImage(fileUris.get(i), "application/"+extension, i));

                }
            }
            callImageUploadApi(apiServiceProvider, parts.get(uploadCallCount), "application/pdf");
        } else {
            Toast.makeText(CreatePostActivity.this,
                    "Internet connection not available", Toast.LENGTH_SHORT).show();
        }
    }
    //Dinu(20-04-2021)-for get file Extension
    private String getfileExtension(Uri uri)
    {
        String extension;
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        extension= mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
        return extension;
    }

    private void uploadImageToServer1(Uri uri) {
        sizes.clear();
        uploadCallCount = 0;
        if (checkConnection()) {
            isUploadingImage = true;
            ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(this);

            parts = new ArrayList<>();
            if (uri != null) {
                parts.add(prepareFilePartImage(uri, "application/pdf", 0));

            }
            callImageUploadApi(apiServiceProvider, parts.get(uploadCallCount), "application/pdf");
        } else {
            Toast.makeText(CreatePostActivity.this,
                    "Internet connection not available", Toast.LENGTH_SHORT).show();
        }
    }

    private void callImageUploadApi(ApiServiceProvider apiServiceProvider, MultipartBody.Part part, String type) {
        RequestBody description = createPartFromString();
        ApiCallbackParams apiCallbackParams = new ApiCallbackParams();
        apiCallbackParams.setDeveloperCode(uploadCallCount);
        call = apiServiceProvider.uploadSingle(description, part, apiCallbackParams, new RetrofitListener() {
            @Override
            public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {
                uploadCallCount++;
                try {
                    JSONArray array = new JSONObject(responseBodyString).getJSONArray("data");

                    for (int i = 0; i < array.length(); i++) {
                        HistoryImages obj = new HistoryImages();
                        obj.setType(array.getJSONObject(i).getString("type"));
                        obj.setFilename(array.getJSONObject(i).getString("filename"));
                        if (type.equals(MIME_TYPE_PDF)) {
                            obj.setOriginal_name(File_Name);
                        }
                        if (type.equals(MIME_TYPE_IMAGE)) {
                            obj.setHeight(sizes.get(i).getHeight() + "");
                            obj.setWidth(sizes.get(i).getWidth() + "");
                        }
                        historyImages.add(obj);
                    }

                    for (Image image : albumDocuments) {
                        image.setIsuploading(false);
                    }

                    if (CreatePostActivity.this.uploadCallCount == fileUris.size()) {
                        albumEventAdapter.notifyDataSetChanged();
                        isUploadingImage = false;
                    }
                    if (CreatePostActivity.this.uploadCallCount < fileUris.size())
                        callImageUploadApi(apiServiceProvider, parts.get(CreatePostActivity.this.uploadCallCount), type);


                } catch (Exception e) {
                    Log.e(TAG, "onResponse: " + e.toString());
                }
            }

            @Override
            public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
                if (CreatePostActivity.this.uploadCallCount == fileUris.size()) {
                    albumEventAdapter.notifyDataSetChanged();
                    isUploadingImage = false;
                }
                if (CreatePostActivity.this.uploadCallCount < fileUris.size()) {
                    callImageUploadApi(apiServiceProvider, parts.get(uploadCallCount), type);
                }
            }
        });

    }

    @Override
    public void onProgressUpdate(int percentage, int position) {

        if (percentage % 5 == 0) {
            try {
                albumDocuments.get(position).setPrograss(percentage);
                albumEventAdapter.notifyDataSetChanged();
            } catch (Exception e) {
                Log.i("", "");
            }
        }
    }

    /*private boolean isSizeLess(Uri uri) {
        File file = FileUtil.getFile(this, uri);
        int file_size = Integer.parseInt(String.valueOf(file.length() / 1024));
        if (file_size > 409600) {
            Toast.makeText(this, "Please upload video of size less than 200 mb", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }*/


    private void goToImageGalleryintent() {
        Matisse.from(CreatePostActivity.this)
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
        Matisse.from(CreatePostActivity.this)
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

    @OnClick({R.id.post, R.id.attachment, R.id.txtVideo, R.id.txtCancel, R.id.txtAudio, R.id.txtImage, R.id.goBack, R.id.txtDoc})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.post:
                if (validate())
                    createPostRequest();
                break;
            case R.id.attachment:
            case R.id.txtCancel:
                if (!isUploadingImage) {
                    showHideBottom();
                } else {
                    Toast.makeText(this, "Please wait.. upload in progress", Toast.LENGTH_SHORT).show();
                }
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
                    // FileUtils.pickDocument(this, RESULT_DOC);
                    pickDocument();
                }

                showHideBottom();
                break;

            // For upload audio(Dinu :11-03-2021)
            case R.id.txtAudio:
                Intent intent = new Intent();
                intent.setType("audio/mpeg");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Audio "), RESULT_AUDIO);

                showHideBottom();
                break;
            case R.id.goBack:
                onBackPressed();
                break;
        }

    }

    private boolean validate() {

        if (postSpinner.getSelectedItemPosition() == 0 && shared) {
            Snackbar snackbar = Snackbar.make(constraintLayout, "Please select the 'Post this to'", Snackbar.LENGTH_SHORT);
            snackbar.show();
            return false;
        }

        if (isUploadingImage) {
            Snackbar snackbar = Snackbar.make(constraintLayout, "Upload in progress please wait a moment.", Snackbar.LENGTH_SHORT);
            snackbar.show();
            return false;
        }

        if (historyImages.size() == 0 && what_to_post_descrption.getText().toString().trim().equals("")) {
            Snackbar snackbar = Snackbar.make(constraintLayout, "Please add description or Attachment", Snackbar.LENGTH_SHORT);
            snackbar.show();
            return false;
        }

        /*Megha(23/08/2021)-> rating validation*/
        if ( rating.isChecked()&&historyImages.size()>1){
            Snackbar snackbar = Snackbar.make(constraintLayout, "Rating is only possible for single media", Snackbar.LENGTH_SHORT);
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

    @NonNull
    private RequestBody createPartFromString() {
        return RequestBody.create("post", MediaType.parse(MIME_TYPE_TEXT));
    }

    @NonNull
    private MultipartBody.Part prepareFilePartImage(Uri fileUri, String type, int position) {
        try {
            File file = readContentToFile( fileUri);
            if (type.equals(MIME_TYPE_IMAGE)) {
                getImageDimension(file);
            }

            ProgressRequestBody fileBody = new ProgressRequestBody(file, type, this, position + historyImages.size());
            File_Name = getDisplayName(fileUri);
            return MultipartBody.Part.createFormData("file", File_Name, fileBody);
        } catch (IOException e) {
            return null;
        }
    }


    @NonNull
    private MultipartBody.Part prepareFilePart(Uri fileUri) {
        try {
            File file = FileUtil.getFile(this, fileUri);

            RequestBody requestFile = RequestBody.create(file, MediaType.parse("RESULT_DOC"));
            return MultipartBody.Part.createFormData("file", file.getName(), requestFile);
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
                        v -> ActivityCompat.requestPermissions(CreatePostActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                REQUEST_CODE_PERMISSIONS)).show();
            } else {
                /* Request for permission */
                ActivityCompat.requestPermissions(CreatePostActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE_PERMISSIONS);
            }

        } else {
            if (ctype == PICK_IMAGE)
                goToImageGalleryintent();
            else if (ctype == RESULT_DOC)
                //  FileUtils.pickDocument(this, RESULT_DOC);
                pickDocument();
            else
                goToVideoGalleryIntent();
        }
    }
    //10-05-2021(Dinu) for change document picker
    private void pickDocument() {
        Intent intent = new Intent();
        intent.setAction( Intent.ACTION_OPEN_DOCUMENT );
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

                else if (type == RESULT_DOC)
                    // FileUtils.pickDocument(this, RESULT_DOC);
                    pickDocument();
                else goToVideoGalleryIntent();
            } else {
                Toast.makeText(CreatePostActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == 1044 || requestCode == 1045 || requestCode == 1046) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (requestCode == 1044)
                    setShareImage();
                else if (requestCode == 1046) setSharePdf();
                else setShareVideo();

            } else {
                finish();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    @Override
    public void onAlbumDeleted(int position) {

        if (position < historyImages.size())
            historyImages.remove(position);
    }

    public void createPostRequest() {
        showProgressDialog();
        PostRequest request = new PostRequest();
        request.setCategory_id(3 + "");
        request.setCreated_by(SharedPref.getUserRegistration().getId());
        request.setPost_info(new PostInfo());
        request.setType("post");
        request.setConversation_enabled(conversation.isChecked());
        request.setIs_shareable(share.isChecked());
        request.setTitle("");
        request.setSnap_description(what_to_post_descrption.getText().toString());

        /*(megha, 25/08/21)->rating enabled or not*/
        if (rating.isChecked()){
            request.setRating_enabled(true);
        }else{
            request.setRating_enabled(false);
        }

        if(!shared){//(Megha 02/07/2021)-> for posting contents in family
            request.setPost_type("only_groups");
            request.setPrivacy_type("public");
            request.setSelected_groups(family);
        }
        else{
            if (SharedPref.userHasFamily()) {
                switch (postSpinner.getSelectedItemPosition()) {
                    case 1:
                        request.setPost_type("only_groups");
                        request.setPrivacy_type("public");
                        request.setSelected_groups(family);
                        break;
                    case 2:
                        request.setPost_type("private");
                        request.setPrivacy_type("private");
                        request.setSelected_groups(new ArrayList());
                        break;
                }
            } else {
                switch (postSpinner.getSelectedItemPosition()) {
                    case 1:
                        request.setPost_type("private");
                        request.setPrivacy_type("private");
                        request.setSelected_groups(new ArrayList<>());
                        break;
                }

            }
        }
        request.setPost_attachment(historyImages);
        FamilheeyApplication application = FamilheeyApplication.get(this);
        ApiServices apiServices = RetrofitBase.createRxResource(this, ApiServices.class);
        subscriptions.add(apiServices.createPost(request)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(response -> {
                    hideProgressDialog();
                    if (response.code() == 200) {
                        Toast.makeText(CreatePostActivity.this,
                                "Post successfully created", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }, throwable -> hideProgressDialog()));
    }


    private void copy_file_between_bucket(String source_file, String destination_file, Boolean isDoc,String fileType) {
        showProgressDialog();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("source_file", source_file);
        jsonObject.addProperty("destination_file", "post/" + destination_file);
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        FamilheeyApplication application = FamilheeyApplication.get(this);
        ApiServices apiServices = RetrofitBase.createRxResource(this, ApiServices.class);
        subscriptions.add(apiServices.copy_file_between_bucket(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(response -> {
                    Image image = new Image();
                    image.setIsuploading(false);
                    image.setUrl(true);
                    image.setmUrl(destination_file);
                    image.setDoc(isDoc);
                    image.setfileType(fileType);
                    albumDocuments.add(image);
                    albumEventAdapter.notifyDataSetChanged();
                    no_files.setVisibility(View.GONE);

                    HistoryImages obj = new HistoryImages();

                    obj.setFilename(destination_file);

                    if (isDoc) {
                        obj.setType(fileType);
                    } else {
                        obj.setType("image/png");
                        if (sizes.size() > 0) {
                            obj.setHeight(sizes.get(0).getHeight() + "");
                            obj.setWidth(sizes.get(0).getWidth() + "");
                        }
                    }
                    historyImages.add(obj);
                    hideProgressDialog();
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


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        try {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                View v = getCurrentFocus();
                if (v instanceof EditText) {
                    Rect outRect = new Rect();
                    v.getGlobalVisibleRect(outRect);
                    v.getGlobalVisibleRect(outRect);
                    if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                        v.clearFocus();
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        assert imm != null;
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.dispatchTouchEvent(event);
    }

    private boolean isReadWritePermissionGranted() {
        return TedPermission.isGranted(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
    }


    public void getPermissionToReadFile(int rcode) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(CreatePostActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    rcode);

        }
    }

    boolean check() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

    }


    private void onSharedIntent() {
        receiverdIntent = getIntent();
        String receivedAction = receiverdIntent.getAction();
        String receivedType = receiverdIntent.getType();

        if (Intent.ACTION_SEND.equals(receivedAction)) {
            shared=true;
            post_this.setVisibility( View.VISIBLE );
            postThisToMandatoryIndication.setVisibility( View.VISIBLE );
            txt_count.setVisibility( View.VISIBLE );
            postSpinner.setVisibility( View.VISIBLE );
            // check mime type
            if (receivedType != null && receivedType.startsWith("text/")) {
                String receivedText = receiverdIntent
                        .getStringExtra(Intent.EXTRA_TEXT);
                if (receivedText != null) {
                    String link = SharedPref.read(SharedPref.FAMILY_LINK, "");
                    if(link!=""){
                        receivedText=link;
                        SharedPref.write(SharedPref.FAMILY_LINK,"");
                    }

                    what_to_post_descrption.setText(receivedText);
                }
               /* if (receivedText != null) {
                    what_to_post_descrption.setText(receivedText);
                }*/
            } else if (receivedType != null && receivedType.startsWith("image/")) {

                if (check()) {
                    setShareImage();
                } else {
                    getPermissionToReadFile(1044);
                }


            } else if (receivedType != null && receivedType.startsWith("video/")) {

                if (check()) {

                    setShareVideo();
                } else {
                    getPermissionToReadFile(1045);
                }
            }
            //Dinu(22/07/2021): For share mpeg
            else if (receivedType != null && receivedType.startsWith("audio/mpeg")) {

                if (check()) {

                    setShareAudio();
                } else {
                    getPermissionToReadFile(1045);
                }
            }
            else if (receivedType != null && (receivedType.startsWith("application/pdf")
                    || receivedType.startsWith("application/msword")
                    || receivedType.startsWith("application/vnd.openxmlformats-officedocument.wordprocessingml.document")
                    || receivedType.startsWith("application/vnd.ms-excel application/x-excel")
                    || receivedType.startsWith("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                    || receivedType.startsWith("application/vnd.ms-excel")
                    || receivedType.startsWith("application/vnd.ms-powerpoint")
                    || receivedType.startsWith("application/vnd.openxmlformats-officedocument.presentationml.presentation")
                    || receivedType.startsWith("application/zip")
                    || receivedType.startsWith("application/octet-stream")
                    || receivedType.startsWith("application/vnd.rar")
                    || receivedType.startsWith("application/x-rar-compressed")
                    || receivedType.startsWith("text/plain"))

            ) {
                if (check()) {
                    setSharePdf();
                } else {
                    getPermissionToReadFile(1046);
                }
            }

        }
    }

    private void setShareImage() {
        Uri receiveUri = receiverdIntent
                .getParcelableExtra(Intent.EXTRA_STREAM);
        if (receiveUri != null) {
            fileUris = new ArrayList<>();
            no_files.setVisibility(View.GONE);
            fileUris.add(receiveUri);
            albumDocuments.addAll(generateUploadingImageModels(PICK_IMAGE));
            albumEventAdapter.notifyDataSetChanged();
            imgRecycler.setVisibility(View.VISIBLE);

            if (receiveUri.toString().contains("provider") || receiveUri.toString().contains("cache"))
                uploadShareImageToServer();
            else {
                try {
                    UPLOAD_FILE_POSITION = 0;
                    new ImageCompressionAsyncTask(this).execute(readContentToFile(fileUris.get(0)));
                } catch (Exception e) {
                    Log.e("", "");
                }
            }

        }
    }

    private void setSharePdf() {

        try {
            Uri receiveUri = receiverdIntent
                    .getParcelableExtra(Intent.EXTRA_STREAM);
            fileUris = new ArrayList<>();
            no_files.setVisibility(View.GONE);
            imgRecycler.setVisibility(View.VISIBLE);
            fileUris.add(receiveUri);
            albumDocuments.addAll(generateUploadingImageModels(RESULT_DOC));
            albumEventAdapter.notifyDataSetChanged();
            if (receiveUri.toString().contains("provider") || receiveUri.toString().contains("cache")){
                uploadShareDocumentToServer();
            }
            else
                uploadImageToServer();

        } catch (Exception e) {

            Toast.makeText(this, "Sorry can't find the shared file", Toast.LENGTH_SHORT).show();
        }


    }
    //Dinu(22/07/2021): For share mpeg
    private void setShareAudio() {
        try {
            Uri receiveUri = receiverdIntent
                    .getParcelableExtra(Intent.EXTRA_STREAM);

            fileUris = new ArrayList<>();
            no_files.setVisibility(View.GONE);
            imgRecycler.setVisibility(View.VISIBLE);
            fileUris.add(receiveUri);
            albumDocuments.addAll(generateUploadingAudioModels(RESULT_AUDIO,receiveUri));
            albumEventAdapter.notifyDataSetChanged();


            try {
                File file = AudioreadContentToFile(receiveUri);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
                String timeStamp = dateFormat.format(new Date());
                File thumbFile = new File(getCacheDir(), timeStamp + "_thumb.jpg");

                HistoryImages obj = new HistoryImages();
                obj.setType("audio/mp3");
                obj.setFilename(file.getName());
                obj.setVideo_thumb("audio_thumb/" + thumbFile.getName());
                historyImages.add(obj);
                uploadWithTransferUtility(file, "post/" + file.getName(), albumDocuments.size(), MIME_TYPE_VIDEO);
                uploadWithTransferUtility(thumbFile, "audio_thumb/" + thumbFile.getName(), -1, MIME_TYPE_VIDEO);


            } catch (Exception e) {
                Log.e(TAG, "Unable to upload file from the given uri", e);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Sorry can't find the shared file", Toast.LENGTH_SHORT).show();
        }
    }

    private void setShareVideo() {
        try {
            Uri receiveUri = receiverdIntent
                    .getParcelableExtra(Intent.EXTRA_STREAM);

            fileUris = new ArrayList<>();
            no_files.setVisibility(View.GONE);
            imgRecycler.setVisibility(View.VISIBLE);
            fileUris.add(receiveUri);
            albumDocuments.addAll(generateUploadingImageModels(RESULT_LOAD_VIDEO));
            albumEventAdapter.notifyDataSetChanged();
            String selectedVideoPath = getPath(receiveUri);
            try {
                File file = new File(selectedVideoPath);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
                String timeStamp = dateFormat.format(new Date());
                Bitmap thumb = ThumbnailUtils.createVideoThumbnail(file.getPath(), MediaStore.Video.Thumbnails.MINI_KIND);
                File thumbFile = new File(getCacheDir(), timeStamp + "_thumb.jpg");
                thumbFile.createNewFile();
                OutputStream os = new BufferedOutputStream(new FileOutputStream(thumbFile));
                assert thumb != null;
                thumb.compress(Bitmap.CompressFormat.JPEG, 70, os);
                os.close();

                HistoryImages obj = new HistoryImages();
                obj.setType("video/wav");
                obj.setFilename(timeStamp+"."+getfileExtension(receiveUri));
                obj.setVideo_thumb("video_thumb/" + thumbFile.getName());
                historyImages.add(obj);
                uploadWithTransferUtility(file, "post/" + obj.getFilename(), albumDocuments.size(), MIME_TYPE_VIDEO);
                uploadWithTransferUtility(thumbFile, "video_thumb/" + thumbFile.getName(), -1, MIME_TYPE_VIDEO);
            } catch (Exception e) {
                Toast.makeText(this,
                        "Unable to find selected file.",
                        Toast.LENGTH_LONG).show();
                Log.e(TAG, "Unable to upload file from the given uri", e);
            }


        } catch (Exception e) {
            Toast.makeText(this, "Sorry can't find the shared file", Toast.LENGTH_SHORT).show();
        }
    }
    //Dinu(22/07/2021): For share Document
    private void uploadShareDocumentToServer(){
        sizes.clear();
        uploadCallCount = 0;
        if (checkConnection()) {
            isUploadingImage = true;
            ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(this);

            parts = new ArrayList<>();
            if (fileUris != null) {
                try {
                    File file = readContentToFile(fileUris.get(0));
                    String extension= getfileExtension(fileUris.get(0));
                    ProgressRequestBody fileBody = new ProgressRequestBody(file, "application/"+extension, this, 0);
                    parts.add(MultipartBody.Part.createFormData("file", file.getName(), fileBody));
                    callImageUploadApi(apiServiceProvider, parts.get(uploadCallCount), "application/pdf");
                }
                catch (Exception e) {
                    Toast.makeText(CreatePostActivity.this,
                            "Internet connection not available", Toast.LENGTH_SHORT).show();
                }
            }

        } else {
            Toast.makeText(CreatePostActivity.this,
                    "Internet connection not available", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadShareImageToServer() {
        sizes.clear();
        uploadCallCount = 0;
        if (checkConnection()) {
            isUploadingImage = true;
            ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(this);

            parts = new ArrayList<>();
            if (fileUris != null) {
                File file = getBitmapFromUri(fileUris.get(0));
                getImageDimension(file);
                ProgressRequestBody fileBody = new ProgressRequestBody(file, "image/png", this, 0);
                parts.add(MultipartBody.Part.createFormData("file", file.getName(), fileBody));
            }
            callImageUploadApi(apiServiceProvider, parts.get(uploadCallCount), "image/png");
        } else {
            Toast.makeText(CreatePostActivity.this,
                    "Internet connection not available", Toast.LENGTH_SHORT).show();
        }
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


    private void setImageWidthandSizeINForward(String path, ChatModel chat) {
        Glide.with(this)
                .asBitmap()
                .load(path)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap bitmap,
                                                Transition<? super Bitmap> transition) {
                        imageSizes size = new imageSizes();
                        size.setWidth(bitmap.getWidth());
                        size.setHeight(bitmap.getHeight());
                        sizes.add(size);
                        copy_file_between_bucket("file_name/" + chat.getFilename(), chat.getFilename(), false,"");
                    }
                });
    }

    //*************************************AWS*****************************************

    private File readContentToFile(Uri uri) throws IOException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
        String timeStamp = dateFormat.format(new Date());
        final File file = new File(getCacheDir(), timeStamp +"."+ getfileExtension(uri));
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


    public void uploadWithTransferUtility(File file, String key, int position, String type) {
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
        uploadObserver.setTransferListener(new TransferListener() {

            @Override
            public void onStateChanged(int id, TransferState state) {
                try {
                    if (TransferState.COMPLETED == state) {
                        if (position > 0) {
                            isUploadingImage = false;
                            albumDocuments.get(position - 1).setIsuploading(false);
                            albumEventAdapter.notifyDataSetChanged();
                            if (type.equals(MIME_TYPE_IMAGE)) {
                                compressNextFile();
                            }
                        }
                    }
                } catch (Exception e) {
                    finish();
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                float percentDonef = ((float) bytesCurrent / (float) bytesTotal) * 100;
                int percentDone = (int) percentDonef;


                if (position > 0 && albumDocuments.size() > position - 1) {
                    albumDocuments.get(position - 1).setPrograss(percentDone);
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

    //*************************IMAGE COMPRESS*************************

    private void compressNextFile() {
        if (fileUris.size() > UPLOAD_FILE_POSITION + 1) {
            UPLOAD_FILE_POSITION += 1;
            try {
                new ImageCompressionAsyncTask(this).execute(readContentToFile(fileUris.get(UPLOAD_FILE_POSITION)));
            } catch (Exception ignored) {

            }
        }

    }

    @Override
    public void onBackPressed() {
        if (!what_to_post_descrption.getText().toString().trim().equals("") || historyImages.size() > 0) {
            confirmationBack();
        } else {
            finish();
            overridePendingTransition(R.anim.left,
                    R.anim.right);
        }
    }

    class ImageCompressionAsyncTask extends AsyncTask<File, Integer, File> {

        Context mContext;

        ImageCompressionAsyncTask(Context context) {
            mContext = context;
            isUploadingImage = true;
        }

        @Override
        protected File doInBackground(File... params) {
            try {
                File compressedFile = new Compressor(mContext)
                        .setQuality(60)
                        .compressToFile(params[0]);

                for (int i = 0; i <= 85; i++) {
                    try {
                        Thread.sleep(10);
                    } catch (Exception ignored) {
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
            try {
                albumDocuments.get(historyImages.size()).setPrograss(values[0]);
                albumEventAdapter.notifyDataSetChanged();
            } catch (Exception e) {
                finish();
            }
        }
    }

    private void confirmationBack() {

        SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setContentText("Are you sure want to close?")
                .setConfirmText("Yes")
                .setCancelText("No");

        pDialog.show();
        pDialog.setConfirmClickListener(sDialog -> {
            if(isUploadingImage) {
                int ids = uploadObserver.getId();
                Boolean canceled = transferUtility.cancel(ids);
                transferUtility.cancelAllWithType(TransferType.ANY);
                isUploadingImage = false;
            }
            pDialog.cancel();
            overridePendingTransition(R.anim.left,
                    R.anim.right);
            finish();
        });
        pDialog.setCancelClickListener(SweetAlertDialog::cancel);
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
    // for get path from uri
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

