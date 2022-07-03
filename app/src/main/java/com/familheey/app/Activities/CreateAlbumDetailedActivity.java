package com.familheey.app.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.familheey.app.Adapters.AlbumEventAdapter;
import com.familheey.app.FamilheeyApplication;
import com.familheey.app.Interfaces.Bottomupinterface;
import com.familheey.app.Interfaces.ProgressListener;
import com.familheey.app.Interfaces.RetrofitListener;
import com.familheey.app.Models.ApiCallbackParams;
import com.familheey.app.Models.ErrorData;
import com.familheey.app.Models.Request.AlbumRequest;
import com.familheey.app.Models.Response.CreateAlbumEventResponse;
import com.familheey.app.Models.Response.Document;
import com.familheey.app.Models.Response.GetAlbumImagesResponse;
import com.familheey.app.Models.Response.ListEventAlbumsResponse;
import com.familheey.app.Networking.Retrofit.ApiServiceProvider;
import com.familheey.app.Networking.Retrofit.ApiServices;
import com.familheey.app.Networking.Retrofit.RetrofitBase;
import com.familheey.app.Networking.utils.GsonUtils;
import com.familheey.app.Post.VideoActivity;
import com.familheey.app.R;
import com.familheey.app.Utilities.Constants;
import com.familheey.app.Utilities.GifSizeFilter;
import com.familheey.app.Utilities.ImageCompressionTask;
import com.familheey.app.Utilities.SharedPref;
import com.familheey.app.Utilities.Utilities;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.filter.Filter;

import org.json.JSONException;
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
import java.util.HashSet;
import java.util.Iterator;
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

import static com.familheey.app.Utilities.Constants.ApiPaths.IMAGE_BASE_URL;
import static com.familheey.app.Utilities.Constants.Bundle.CAN_CREATE;
import static com.familheey.app.Utilities.Constants.Bundle.CAN_UPDATE;
import static com.familheey.app.Utilities.Constants.Bundle.DATA;
import static com.familheey.app.Utilities.Constants.Bundle.DESCRIPTION;
import static com.familheey.app.Utilities.Constants.Bundle.ID;
import static com.familheey.app.Utilities.Constants.Bundle.IDENTIFIER;
import static com.familheey.app.Utilities.Constants.Bundle.IS_ADMIN;
import static com.familheey.app.Utilities.Constants.Bundle.IS_ALBUM;
import static com.familheey.app.Utilities.Constants.Bundle.IS_FAMILY_SETTINGS_NEEDED;
import static com.familheey.app.Utilities.Constants.Bundle.IS_UPDATE_MODE;
import static com.familheey.app.Utilities.Constants.Bundle.PERMISSION;
import static com.familheey.app.Utilities.Constants.Bundle.PERMISSION_GRANTED_USERS;
import static com.familheey.app.Utilities.Constants.Bundle.POSITION;
import static com.familheey.app.Utilities.Constants.Bundle.TITLE;
import static com.familheey.app.Utilities.Constants.Bundle.TYPE;
import static com.familheey.app.Utilities.Constants.FileUpload.TYPE_EVENTS;
import static com.familheey.app.Utilities.Constants.FileUpload.TYPE_FAMILY;
import static com.familheey.app.Utilities.Constants.ImageUpdate.GENERAL;
import static com.familheey.app.Utilities.Constants.Paths.LOGO;

public class CreateAlbumDetailedActivity extends AppCompatActivity implements Bottomupinterface, RetrofitListener, ProgressListener, AlbumEventAdapter.OnAlbumSelectedListener, ImageCompressionTask.OnCompressedFilesListener {

    private static final int TYPE_IMAGE = 0;
    private static final int TYPE_VIDEO = 1;
    public static final int ALBUM_EDIT_REQUEST_CODE = 100;
    GetAlbumImagesResponse getAlbumImages;
    @BindView(R.id.txtVideo)
    TextView txtVideo;
    @BindView(R.id.txtCancel)
    TextView txtCancel;
    @BindView(R.id.txtImage)
    TextView txtImage;
    @BindView(R.id.album_cover_view)
    ImageView albumCoverView;
    @BindView(R.id.album_tittle)
    TextView albumTittle;
    @BindView(R.id.album_description)
    TextView albumDescription;
    @BindView(R.id.empty_tv)
    TextView emptyTv;
    @BindView(R.id.add_photos)
    MaterialButton addPhotos;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.deleteAlbumElements)
    MaterialButton deleteAlbumElements;
    @BindView(R.id.goBack)
    ImageView goBack;
    @BindView(R.id.imageEdit)
    ImageView imageEdit;
    @BindView(R.id.imagePlaceholder)
    ImageView imagePlaceholder;
    @BindView(R.id.txt_less_or_more)
    TextView txt_less_or_more;
    @BindView(R.id.txt_temp)
    TextView textTemp;


    ArrayList<AlbumRequest.Document_file> attachment = new ArrayList<>();
    ListEventAlbumsResponse.Datum datum = new ListEventAlbumsResponse.Datum();

    boolean isCreatedNow = false;
    int PICK_IMAGE = 10;
    int RESULT_LOAD_VIDEO = 11;
    private String coverPic = "";
    private CompositeDisposable subscriptions;
    private String FROM = "";
    @BindView(R.id.progressBar6)
    ProgressBar progressBar;
    AlbumEventAdapter albumEventAdapter;
    private List<Uri> fileUris = new ArrayList<>();
    private List<File> files = new ArrayList<>();
    private int type;
    private final List<Document> albumDocuments = new ArrayList<>();
    private SweetAlertDialog progressDialog;

    private BottomSheetBehavior sheetBehavior;
    private boolean isAdmin = false;
    private ApiServiceProvider apiServiceProvider;
    private RequestBody folderId;
    private RequestBody userId;
    private RequestBody isSharable, folderType;

    private final List<Long> selectedElementIds = new ArrayList<>();
    private String id = "";
    private String parentId = "";
    private String description = "";
    private boolean canUpdate = true;
    private boolean canCreate = true;
    private ArrayList<CreateAlbumEventResponse.Row> rows = new ArrayList<>();
    private int UPLOAD_FILE_POSITION = 0;

    private Boolean isUploadingImage = false;
    static TransferUtility transferUtility;
    static TransferObserver uploadObserver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_album_detailed);
        ButterKnife.bind(this);
        CardView bottom_sheet = findViewById(R.id.bottom_sheet);
        sheetBehavior = BottomSheetBehavior.from(bottom_sheet);
        subscriptions = new CompositeDisposable();
        inits();
        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        FROM = (bundle.getString("FROM", ""));
        isCreatedNow = (!bundle.getBoolean(IS_UPDATE_MODE, false));
        isAdmin = getIntent().getBooleanExtra(IS_ADMIN, false);
        canCreate = getIntent().getBooleanExtra(CAN_CREATE, false);
        canUpdate = getIntent().getBooleanExtra(CAN_UPDATE, false);
        type = getIntent().getIntExtra(TYPE, Constants.FileUpload.TYPE_USER);
        if (FROM.equals("POST")) {
            getSingleFolder(bundle.getString(IDENTIFIER, ""), bundle.getString(ID, ""));
        } else {
            if (isCreatedNow) {
                rows = bundle.getParcelableArrayList(Constants.Bundle.DATA);
                if (rows != null) {
                    bindUI();
                    getAllImages();
                    initListner();
                }
            } else {
                datum = bundle.getParcelable(Constants.Bundle.DATA);
                bindUI();
                getAllImages();
                initListner();
            }
        }

        bundle.clear();
        albumEventAdapter = new AlbumEventAdapter(this, this, albumDocuments, (isAdmin || (isCreatedNow && canCreate) || (!isCreatedNow && canUpdate)));
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setAdapter(albumEventAdapter);
        if (isAdmin || (isCreatedNow && canCreate) || (!isCreatedNow && canUpdate)) {
            imageEdit.setVisibility(View.VISIBLE);
            addPhotos.setVisibility(View.VISIBLE);
        } else {
            imageEdit.setVisibility(View.INVISIBLE);
            addPhotos.setVisibility(View.INVISIBLE);
        }

    }

    void addViewMoreLogic(String description) {
        albumDescription.setText(description);
        textTemp.setText(description);
        textTemp.post(() -> {
            if (textTemp.getLineCount() > 2) {
                txt_less_or_more.setVisibility(View.VISIBLE);
                albumDescription.setMaxLines(2);
                albumDescription.setEllipsize(TextUtils.TruncateAt.END);
                albumDescription.setText(description);
            } else {
                txt_less_or_more.setVisibility(View.GONE);
                albumDescription.setText(description);
            }
        });
        txt_less_or_more.setOnClickListener(v -> {
            if (txt_less_or_more.getText().equals("Read More")) {
                txt_less_or_more.setText("Read Less");
                albumDescription.setText(description);
                albumDescription.setMaxLines(Integer.MAX_VALUE);
                albumDescription.setEllipsize(null);
            } else {
                txt_less_or_more.setText("Read More");
                albumDescription.setMaxLines(2);
                albumDescription.setEllipsize(TextUtils.TruncateAt.END);
            }
        });
        albumDescription.setOnClickListener(v -> {
        });
        textTemp.setOnClickListener(v -> {
        });
    }

    void showHideBottom() {
        if (sheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else {
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        }
    }


    private void initListner() {
        imageEdit.setOnClickListener(v -> {
            if (isCreatedNow) {
                id = rows.get(0).getId() + "";
                if (isAdmin || canCreate) {
                    switch (type) {
                        case TYPE_EVENTS:
                            parentId = rows.get(0).getEventId();
                            break;
                        case TYPE_FAMILY:
                            parentId = rows.get(0).getGroupId();
                            break;
                        default:
                            parentId = "";
                            break;
                    }
                } else
                    Toast.makeText(getApplicationContext(), "You don't have privilege to edit this album", Toast.LENGTH_SHORT).show();
            } else {
                id = datum.getId() + "";
                if (isAdmin || canUpdate) {
                    switch (type) {
                        case TYPE_EVENTS:
                            parentId = datum.getEventId();
                            break;
                        case TYPE_FAMILY:
                            parentId = datum.getGroupId();
                            break;
                        default:
                            parentId = "";
                            break;
                    }
                } else
                    Toast.makeText(getApplicationContext(), "You don't have privilege to edit this album", Toast.LENGTH_SHORT).show();
            }
            if (!canCreate) {
                Toast.makeText(this, "You don't have authorization to " + (isCreatedNow ? "update" : "create") + " album", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(CreateAlbumDetailedActivity.this, CreateAlbumBasicActivity.class);
            intent.putExtra(DATA, parentId);
            intent.putExtra(TYPE, type);
            intent.putExtra(TITLE, albumTittle.getText().toString());
            intent.putExtra(DESCRIPTION, albumDescription.getText().toString());
            intent.putExtra(PERMISSION, datum.getPermission());
            if (type == TYPE_FAMILY) {
                if (isAdmin && !canUpdate)
                    intent.putExtra(IS_FAMILY_SETTINGS_NEEDED, false);
            }
            if (datum.getPermissionGrantedUsers() != null)
                intent.putIntegerArrayListExtra(PERMISSION_GRANTED_USERS, (ArrayList<Integer>) datum.getPermissionGrantedUsers());
            else intent.putIntegerArrayListExtra(PERMISSION_GRANTED_USERS, new ArrayList<>());
            intent.putExtra(ID, id);
            intent.putExtra(IS_ALBUM, true);
            startActivityForResult(intent, ALBUM_EDIT_REQUEST_CODE);
        });

        txtCancel.setOnClickListener(v -> showHideBottom());

        addPhotos.setOnClickListener(view -> {
            if (isAdmin || (isCreatedNow && canCreate) || (!isCreatedNow && canUpdate))
                showHideBottom();
            else
                Toast.makeText(getApplicationContext(), "You don't have authorization to create album", Toast.LENGTH_SHORT).show();
        });


        txtVideo.setOnClickListener(v -> {

            if (isReadStoragePermissionGranted()) {
                goToVideoGalleryIntent();
            } else {
                showPermission(TYPE_VIDEO);
            }
            showHideBottom();

        });

        txtImage.setOnClickListener(v -> {

            if (isReadStoragePermissionGranted()) {
                goToImageGalleryintent();
            } else {
                showPermission(TYPE_IMAGE);
            }
            showHideBottom();
        });

        deleteAlbumElements.setOnClickListener(v -> deleteAlbumElements());
    }


    private void goToVideoGalleryIntent() {
        Matisse.from(CreateAlbumDetailedActivity.this)
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

    public String getMimeType(Context context, Uri uri) {
        String mimeType;
        if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
            ContentResolver cr = context.getContentResolver();
            mimeType = cr.getType(uri);
        } else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
                    .toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    fileExtension.toLowerCase());
        }
        return mimeType;
    }

    private void isReadStoragePermissionGranted(int TYPE) {
        if (TedPermission.isGranted(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)) {
        } else {
            requestPermission(TYPE);
        }
    }

    private boolean isReadStoragePermissionGranted() {
        return TedPermission.isGranted(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
    }


    @SuppressLint("IntentReset")
    private void goToImageGalleryintent() {
        Matisse.from(CreateAlbumDetailedActivity.this)
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

    private void getAllImages() {
        progressBar.setVisibility(View.VISIBLE);
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(this);
        JsonObject jsonObject = new JsonObject();
        if (isCreatedNow)
            jsonObject.addProperty("folder_id", rows.get(0).getId() + "");
        else
            jsonObject.addProperty("folder_id", datum.getId() + "");
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        apiServiceProvider.viewContents(jsonObject, null, this);

    }


    private void bindUI() {
        if (isCreatedNow) {
            description = rows.get(0).getDescription();
            albumTittle.setText(rows.get(0).getFolderName());
            if (rows.get(0).getCoverPic() != null) {
                Glide.with(this)
                        .load(IMAGE_BASE_URL + LOGO + rows.get(0).getCoverPic())
                        .placeholder(R.drawable.default_event_image)
                        .into(albumCoverView);
                coverPic = IMAGE_BASE_URL + LOGO + rows.get(0).getCoverPic();
            } else {
                Glide.with(this)
                        .load(R.drawable.default_event_image)
                        .into(albumCoverView);
            }
        } else {
            description = datum.getDescription();
            albumTittle.setText(datum.getFolderName());
            if (datum.getCoverPic() != null) {
                Glide.with(this)
                        .load(datum.getCoverPic())
                        .placeholder(R.drawable.default_event_image)
                        .into(albumCoverView);
                coverPic = datum.getCoverPic();
            } else {
                Glide.with(this)
                        .load(R.drawable.default_event_image)
                        .into(albumCoverView);
            }
        }
        addViewMoreLogic(description);
    }

    @OnClick({R.id.album_cover_view, R.id.add_photos, R.id.goBack})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.album_cover_view:
                goToCoverFullScreenActivity();
                break;
            case R.id.add_photos:
                break;
            case R.id.goBack:
                finish();
                break;
        }
    }

    private void goToCoverFullScreenActivity() {
        Intent imageChangingIntent = new Intent(getApplicationContext(), ImageChangerActivity.class);
        imageChangingIntent.putExtra(TYPE, GENERAL);
        imageChangingIntent.putExtra(IDENTIFIER, false);
        imageChangingIntent.putExtra(DATA, coverPic);
        startActivityForResult(imageChangingIntent, ImageChangerActivity.REQUEST_CODE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ALBUM_EDIT_REQUEST_CODE && resultCode == RESULT_OK) {
            datum.setFolderName(data.getStringExtra(TITLE));
            albumTittle.setText(data.getStringExtra(TITLE));
            datum.setDescription(data.getStringExtra(DESCRIPTION));
            description = data.getStringExtra(DESCRIPTION);
            addViewMoreLogic(description);
            datum.setPermission(data.getStringExtra(PERMISSION));
            datum.setPermissionGrantedUsers(data.getIntegerArrayListExtra(PERMISSION_GRANTED_USERS));
            getAllImages();
            emptyTv.setVisibility(View.INVISIBLE);
        }

        if (requestCode == PICK_IMAGE) {

            if (resultCode == Activity.RESULT_OK && null != data) {
                fileUris = new ArrayList<>();
                fileUris.addAll(Matisse.obtainResult(data));
                albumDocuments.addAll(generateUploadingImageModels());
                albumEventAdapter.notifyDataSetChanged();
                emptyTv.setVisibility(View.INVISIBLE);
                try {
                    UPLOAD_FILE_POSITION = 0;
                    new ImageCompressionAsyncTask(this).execute(readContentToFile(fileUris.get(0)));
                } catch (Exception e) {

                }
            }

        } else if (requestCode == RESULT_LOAD_VIDEO) {
            if (data != null && resultCode == RESULT_OK) {
                emptyTv.setVisibility(View.INVISIBLE);
                fileUris = new ArrayList<>();
                files = new ArrayList<>();
                fileUris.addAll(Matisse.obtainResult(data));

                Uri selectedVideoUri = fileUris.get(0);
                String selectedVideoPath = getPath(selectedVideoUri);

                if (Matisse.obtainResult(data).size() != 0) {
                    try {
                        albumDocuments.addAll(generateUploadingImageModels());
                        albumEventAdapter.notifyDataSetChanged();
                        File file = new File(selectedVideoPath);
                      //  File file = readContentToFile(fileUris.get(0));
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
                        String timeStamp = dateFormat.format(new Date());
                        Bitmap thumb = ThumbnailUtils.createVideoThumbnail(file.getPath(), MediaStore.Video.Thumbnails.MINI_KIND);
                        File thumbFile = new File(getCacheDir(), System.currentTimeMillis() + "_thumb.jpg");
                        boolean s = thumbFile.createNewFile();
                        OutputStream os = new BufferedOutputStream(new FileOutputStream(thumbFile));
                        assert thumb != null;
                        thumb.compress(Bitmap.CompressFormat.JPEG, 70, os);
                        os.close();

                        AlbumRequest.Document_file document_file = new AlbumRequest.Document_file();
                        document_file.setVideo_thumb("video_thumb/" + thumbFile.getName());
                        document_file.setFile_name(timeStamp+"."+getFileExtension(selectedVideoUri));
                        document_file.setFile_type("video");
                        document_file.setOriginal_name(file.getName());
                        document_file.setUrl("Documents/" + file.getName());
                        attachment.add(document_file);
                        uploadWithTransferUtility(thumbFile, "video_thumb/" + thumbFile.getName(), -1, "MIME_TYPE_THUMB_IMAGE");
                        uploadWithTransferUtility(file, "Documents/" + document_file.getFile_name(), albumDocuments.size(), "MIME_TYPE_VIDEO");
                    } catch (Exception e) {
                    }
                }
            }
        } else if (requestCode == AlbumSliderActivity.REQUEST_CODE) {
            if (resultCode != Activity.RESULT_OK)
                return;
            if (data.hasExtra(TYPE) && data.hasExtra(POSITION)) {
                if (data.getIntExtra(TYPE, AlbumSliderActivity.COVER) == AlbumSliderActivity.COVER) {
                    Glide.with(this)
                            .load(albumDocuments.get(data.getIntExtra(POSITION, 0)).getUrl())
                            .placeholder(R.drawable.default_event_image)
                            .into(albumCoverView);
                    coverPic = albumDocuments.get(data.getIntExtra(POSITION, 0)).getUrl();
                } else if (data.getIntExtra(TYPE, AlbumSliderActivity.COVER) == AlbumSliderActivity.DELETED) {
                    albumDocuments.remove(data.getIntExtra(POSITION, 0));
                    albumEventAdapter.notifyDataSetChanged();
                }
            } else {
                getAllImages();
            }
        }
    }

    private void requestPermission(int TYPE) {
        TedPermission.with(this)
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        if (TYPE == TYPE_IMAGE)
                            goToImageGalleryintent();
                        else goToVideoGalleryIntent();
                    }

                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {

                    }
                })
                .setDeniedMessage("If you reject permission,you can not upload images\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();
    }


    private MultipartBody.Part prepareFilePart(File file, MediaType mediaType) {
        try {
            RequestBody requestBody = RequestBody.create(file, mediaType);
            return MultipartBody.Part.createFormData("Documents", file.getName(), requestBody);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public void updateCoverPic(ListEventAlbumsResponse.Datum datum) {

        if (datum.getCoverPic() != null) {
            RequestOptions ro = new RequestOptions()
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .override(256, 140)
                    .fitCenter();
            if (datum.getCoverPic() != null) {
                Glide.with(this)
                        .load(datum.getCoverPic())
                        .apply(ro)
                        .placeholder(R.drawable.default_event_image)
                        .into(albumCoverView);
            } else {
                Glide.with(this)
                        .load((R.drawable.default_event_image))
                        .apply(ro)
                        .into(albumCoverView);
            }
        }
    }
    @Override
    public void deletedFile() {
        recyclerView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        getAllImages();
    }


    public List<Document> generateUploadingImageModels() {
        List<Document> loadingDocuments = new ArrayList<>();
        for (Uri imageUri : fileUris) {
            Document document = new Document();
            document.setId(0L);
            document.setUrl(imageUri.toString());
            loadingDocuments.add(document);
        }
        return loadingDocuments;
    }

    public void clearUploadingImageModels() {
        for (int i = 0; i < albumDocuments.size(); i++) {
            if (albumDocuments.get(i).getId().equals(0L))
                albumDocuments.remove(i);
        }
        albumEventAdapter.notifyDataSetChanged();
        fileUris.clear();
    }

    @Override
    public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {
        switch (apiFlag) {
            case Constants.ApiFlags.VIEW_CONTENTS:
                getAlbumImages = new Gson().fromJson(responseBodyString, GetAlbumImagesResponse.class);

                if (getAlbumImages.getCover_pic() == null || getAlbumImages.getCover_pic().equals("")) {
                    imagePlaceholder.setVisibility(View.VISIBLE);
                }

                if (datum != null) {
                    datum.setPermissionGrantedUsers(getAlbumImages.getFolderDetails().getPermissionUsers());
                    datum.setPermission(getAlbumImages.getFolderDetails().getPermissions());
                }

                progressBar.setVisibility(View.INVISIBLE);
                if (getAlbumImages.getDocuments().size() == 0) {
                    emptyTv.setVisibility(View.VISIBLE);
                } else {
                    albumDocuments.clear();
                    albumDocuments.addAll(getAlbumImages.getDocuments());
                    albumEventAdapter.notifyDataSetChanged();
                    recyclerView.setVisibility(View.VISIBLE);
                    emptyTv.setVisibility(View.INVISIBLE);
                    try {
                        Glide.with(getApplicationContext())
                                .load(getAlbumImages.getCover_pic())
                                .placeholder(R.drawable.default_event_image)
                                .into(albumCoverView);
                        imagePlaceholder.setVisibility(View.INVISIBLE);
                        coverPic = getAlbumImages.getCover_pic();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case Constants.ApiFlags.UPLOAD_IMAGE:
                getAllImages();
                fileUris.clear();
                try {
                    Document document = GsonUtils.getInstance().getGson().fromJson(responseBodyString, Document.class);
                    Glide.with(getApplicationContext())
                            .load(document.getUrl())
                            .placeholder(R.drawable.default_event_image)
                            .into(albumCoverView);
                } catch (JsonParseException e) {
                    e.printStackTrace();
                }
                break;

            case Constants.ApiFlags.LIST_GROUP_FOLDERS:
                ListEventAlbumsResponse listGroupFoldersResponse = new Gson().fromJson(responseBodyString, ListEventAlbumsResponse.class);
                if (listGroupFoldersResponse.getData().size() > 0) {
                    datum = listGroupFoldersResponse.getData().get(0);
                    bindUI();
                    getAllImages();
                    initListner();
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    SweetAlertDialog contentNotFoundDialog = Utilities.getContentNotFoundDialog(this);
                    contentNotFoundDialog.setConfirmClickListener(sweetAlertDialog -> finish());
                    contentNotFoundDialog.setCanceledOnTouchOutside(false);
                    contentNotFoundDialog.setCancelable(false);
                    contentNotFoundDialog.show();
                    Utilities.addPositiveButtonMargin(contentNotFoundDialog);
                }
                break;
        }
    }

    @Override
    public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
        switch (apiFlag) {
            case Constants.ApiFlags.VIEW_CONTENTS:
                Toast.makeText(CreateAlbumDetailedActivity.this, "Failed to load Album!! Please try again !!", Toast.LENGTH_SHORT).show();
                break;
            case Constants.ApiFlags.UPLOAD_IMAGE:
                clearUploadingImageModels();
                Toast.makeText(CreateAlbumDetailedActivity.this, "Uploading failed!! Please try again !!", Toast.LENGTH_SHORT).show();
                break;
        }
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
    public void onAlbumElementSelected(Document document, int position) {
        if (document.isVideo()) {
            Intent videoIntent = new Intent(getApplicationContext(), VideoActivity.class);
            videoIntent.putExtra("URL", document.getUrl());
            videoIntent.putExtra("NAME", document.getFile_name());
            videoIntent.putExtra(IS_ADMIN, (isAdmin || (isCreatedNow && canCreate) || (!isCreatedNow && canUpdate)));
            startActivityForResult(videoIntent, AlbumSliderActivity.REQUEST_CODE);
        } else {
            Intent intent = new Intent(getApplicationContext(), AlbumSliderActivity.class);
            intent.putParcelableArrayListExtra(DATA, (ArrayList<? extends Parcelable>) albumDocuments);
            intent.putExtra(POSITION, position);
            intent.putExtra(IS_ADMIN, (isAdmin || (isCreatedNow && canCreate) || (!isCreatedNow && canUpdate)));
            startActivityForResult(intent, AlbumSliderActivity.REQUEST_CODE);
        }
    }

    @Override
    public void onAlbumSelectedForDeletion(List<Long> selectedElementIds) {
        this.selectedElementIds.addAll(selectedElementIds);
        deleteAlbumElements.setVisibility(View.VISIBLE);
    }

    @Override
    public void onAlbumDeselected() {
        deleteAlbumElements.setVisibility(View.GONE);
        selectedElementIds.clear();
    }

    @Override
    public void onImageFilesCompressed(List<File> compressedImageFiles) {
        this.files = compressedImageFiles;
        List<MultipartBody.Part> parts = new ArrayList<>();
        for (int i = 0; i < files.size(); i++) {
            String MIME_TYPE = getMimeType(getApplicationContext(), fileUris.get(i));
            MediaType mediaType = MediaType.parse(MIME_TYPE);
            MultipartBody.Part part = prepareFilePart(files.get(i), mediaType);
            if (part != null) {
                parts.add(part);
            }
        }
        apiServiceProvider.uploadAlbumFile(parts, folderId, userId, isSharable, folderType, null, this);
    }

    private void deleteAlbumElements() {
        showProgressDialog();
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(this);
        JsonObject jsonObject = new JsonObject();
        JsonArray jsonArray = new JsonArray();
        List<Long> deDupStringList = new ArrayList<>(new HashSet<>(selectedElementIds));

        for (Long documentId : deDupStringList) {
            jsonArray.add(documentId);
        }
        jsonObject.add("id", jsonArray);
        apiServiceProvider.deleteFile(jsonObject, null, new RetrofitListener() {
            @Override
            public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {

                try {
                    JSONObject jsonObject1 = new JSONObject(responseBodyString);
                    String s = jsonObject1.getString("message");
                    if(s.equals("Deleted files")){
                        Glide.with(CreateAlbumDetailedActivity.this)
                                .load(R.drawable.default_event_image)
                                .placeholder((new ColorDrawable(Color.BLACK)))
                                .into(albumCoverView);
                        hideProgressDialog();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                /** end **/
                if (Utilities.isCoverImageSelected(albumDocuments, selectedElementIds, coverPic)) {
                    JsonObject jsonObject = new JsonObject();
                    if (isCreatedNow)
                        jsonObject.addProperty("album_id", rows.get(0).getId() + "");
                    else
                        jsonObject.addProperty("album_id", datum.getId() + "");
                    jsonObject.addProperty("cover_pic", "");
                    ApiServiceProvider deletionServiceprovider = ApiServiceProvider.getInstance(getApplicationContext());
                    deletionServiceprovider.makePicCover(jsonObject, null, new RetrofitListener() {
                        @Override
                        public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {
                            Glide.with(CreateAlbumDetailedActivity.this)
                                    .load(R.drawable.default_event_image)
                                    .placeholder((new ColorDrawable(Color.BLACK)))
                                    .into(albumCoverView);
                            hideProgressDialog();
                        }

                        @Override
                        public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
                            hideProgressDialog();
                        }
                    });
                } else {
                    hideProgressDialog();
                }
                for (Iterator<Document> it = albumDocuments.iterator(); it.hasNext(); ) {
                    if (selectedElementIds.contains(it.next().getId())) {
                        it.remove();
                    }
                }
                albumEventAdapter.notifyDataSetChanged();
                deleteAlbumElements.setVisibility(View.GONE);
                selectedElementIds.clear();
            }

            @Override
            public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
                showErrorDialog("Please try again later");
            }
        });
    }


    //*************************************AWS*****************************************

    private File readContentToFile(Uri uri) throws IOException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
        String timeStamp = dateFormat.format(new Date());
        final File file = new File(getCacheDir(), timeStamp + "."+getFileExtension(uri));
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
                    if (type.equals("MIME_TYPE_VIDEO")) {
                        isUploadingImage=false;
                        albumUpload();
                        albumEventAdapter.notifyDataSetChanged();
                    } else if (type.equals("MIME_TYPE_IMAGE")) {
                        compressNextFile();
                    }
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                float percentDonef = ((float) bytesCurrent / (float) bytesTotal) * 100;
                //  int percentDone = (int) percentDonef;
                if (position > 0) {
                    // albumDocuments.get(position-1).setPrograss(percentDone);
                    albumEventAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(int id, Exception ex) {
                if (position > 0) {
                    albumDocuments.remove(position - 1);
                    albumEventAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void albumUpload() {
        AlbumRequest albumRequest = new AlbumRequest();
        if (isCreatedNow) {
            albumRequest.setFolder_id(rows.get(0).getId() + "");
        } else {
            albumRequest.setFolder_id(datum.getId() + "");
        }
        albumRequest.setUser_id(SharedPref.getUserRegistration().getId());
        albumRequest.setIs_sharable("true");
        albumRequest.setFolderType("albums");
        albumRequest.setDocument_files(attachment);
        FamilheeyApplication application = FamilheeyApplication.get(this);
        ApiServices apiServices = RetrofitBase.createRxResource(this, ApiServices.class);
        subscriptions.add(apiServices.albumUpload(albumRequest)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(response -> {
                    getAllImages();
                    fileUris.clear();
                    attachment.clear();
                    files.clear();
                    fileUris.clear();
                    try {
                        Document document = GsonUtils.getInstance().getGson().fromJson(response.body().toString(), Document.class);
                        Glide.with(getApplicationContext())
                                .load(document.getUrl())
                                .placeholder(R.drawable.default_event_image)
                                .into(albumCoverView);
                    } catch (JsonParseException e) {
                        e.printStackTrace();
                    }
                }, throwable -> {
                }));
    }


    //*************************IMAGE COMPRESS*************************

    private void compressNextFile() {
        if (fileUris.size() > UPLOAD_FILE_POSITION + 1) {
            UPLOAD_FILE_POSITION += 1;
            try {
                new ImageCompressionAsyncTask(this).execute(readContentToFile(fileUris.get(UPLOAD_FILE_POSITION)));
            } catch (Exception ignored) {

            }
        } else {
            albumUpload();
        }
    }

    private void getSingleFolder(String group_id, String folder_id) {
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(this);
        JsonObject jsonObject = new JsonObject();
//{"folder_type":"albums","txt":"","user_id":"140","group_id":"235","folder_for":"groups"}
//{"folder_type":"albums",,"txt":"""user_id":"140","group_id":"277","folder_for":"groups"}
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        jsonObject.addProperty("folder_type", "albums");
        jsonObject.addProperty("group_id", group_id);
        jsonObject.addProperty("folder_for", "groups");
        jsonObject.addProperty("folder_id", folder_id);
        jsonObject.addProperty("txt", "");
        apiServiceProvider.listGroupFolders(jsonObject, null, this);
    }

    class ImageCompressionAsyncTask extends AsyncTask<File, Integer, File> {

        Context mContext;

        ImageCompressionAsyncTask(Context context) {
            mContext = context;
            // isUploadingImage = true;
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
                uploadWithTransferUtility(s, "Documents/" + s.getName(), 0, "MIME_TYPE_IMAGE");
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(s));
                AlbumRequest.Document_file document_file = new AlbumRequest.Document_file();
                document_file.setFile_name(s.getName());
                document_file.setFile_type("image/png");
                document_file.setHeight(bitmap.getHeight() + "");
                document_file.setWidth(bitmap.getWidth() + "");
                document_file.setOriginal_name(s.getName());
                document_file.setUrl("Documents/" + s.getName());
                attachment.add(document_file);
                bitmap.recycle();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            // albumDocuments.get(historyImages.size()).setPrograss(values[0]);
            // albumEventAdapter.notifyDataSetChanged();
        }
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
            isReadStoragePermissionGranted(type);

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

    private String getFileExtension(Uri uri)
    {
        String extension;
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        extension= mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
        return extension;
    }


}
