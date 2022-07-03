package com.familheey.app.Fragments;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.familheey.app.Adapters.SubFoldersAdapter;
import com.familheey.app.Interfaces.RetrofitListener;
import com.familheey.app.Models.ApiCallbackParams;
import com.familheey.app.Models.ErrorData;
import com.familheey.app.Models.Response.ViewContents;
import com.familheey.app.Networking.Retrofit.ApiServiceProvider;
import com.familheey.app.R;
import com.familheey.app.Utilities.Constants;
import com.familheey.app.Utilities.FileUtil;
import com.familheey.app.Utilities.FileUtils;
import com.familheey.app.Utilities.PathUtil;
import com.familheey.app.Utilities.SharedPref;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.File;
import java.net.URISyntaxException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import droidninja.filepicker.FilePickerConst;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class SubFoldersFragment extends Fragment/* implements SubFoldersAdapter.FolderAdapterInterface*/ {

    int PICK_IMAGE = 10;
    int RESULT_LOAD_VIDEO = 11;
    int RESULT_DOC = 12;
    int RESULT_AUDIO = 13;

    @BindView(R.id.recycler_folder)
    RecyclerView recycler_folder;

    @BindView(R.id.txtCancel)
    TextView txtCancel;

    @BindView(R.id.txtImage)
    TextView txtImage;

    @BindView(R.id.txtAudio)
    TextView txtAudio;

    @BindView(R.id.txtDoc)
    TextView txtDoc;

    @BindView(R.id.txtVideo)
    TextView txtVideo;

    @BindView(R.id.floatingAddFolder)
    FloatingActionButton floatingAddFolder;

    @BindView(R.id.progressSubFolder)
    ProgressBar progressBar;

    @BindView(R.id.bottom_sheet)
    LinearLayout bottom_sheet;
    boolean isSheetOpen = false;
    View view;

    private String id;
    private String FolderId;
    private int type;

    public SubFoldersFragment() {
// Required empty public constructor
    }

    public static SubFoldersFragment newInstance(String id, String folderId, int type) {
        SubFoldersFragment fragment = new SubFoldersFragment();
        Bundle args = new Bundle();
        args.putString(Constants.Bundle.ID, id);
        args.putString(Constants.Bundle.FOLDER_ID, folderId);
        args.putInt(Constants.Bundle.TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sub_folders, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    void showProgressBar() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    void hideProgressBar() {
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id = getArguments().getString(Constants.Bundle.ID);
            FolderId = getArguments().getString(Constants.Bundle.FOLDER_ID);
            type = getArguments().getInt(Constants.Bundle.TYPE);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initListener();
        fetchData();

    }

    private void fetchData() {
        showProgressBar();
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getContext());
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("folder_id", FolderId);
        switch (type) {
            case Constants.FileUpload.TYPE_EVENTS:
                jsonObject.addProperty("event_id", id);
                break;
            case Constants.FileUpload.TYPE_FAMILY:
                jsonObject.addProperty("group_id", id);
                break;
            case Constants.FileUpload.TYPE_USER:

                break;
        }
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        apiServiceProvider.viewContents(jsonObject, null, new RetrofitListener() {
            @Override
            public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {
                ViewContents viewContents = new Gson().fromJson(responseBodyString, ViewContents.class);
                final SubFoldersAdapter adapter = null;/*new SubFoldersAdapter(viewContents.getDocuments(), SubFoldersFragment.this::onFolderClick);*/
                recycler_folder.setHasFixedSize(true);
                recycler_folder.setLayoutManager(new GridLayoutManager(getActivity(), 2));
                recycler_folder.setAdapter(adapter);
                hideProgressBar();
            }

            @Override
            public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
                hideProgressBar();
            }
        });
    }

    private void initListener() {
        txtCancel.setOnClickListener(view -> floatingAddFolder.callOnClick());

        floatingAddFolder.setOnClickListener(view -> {
            if (isSheetOpen) {
                bottom_sheet.setVisibility(View.GONE);
            } else {
                isReadStoragePermissionGranted();
                bottom_sheet.setVisibility(View.VISIBLE);
            }
            isSheetOpen = !isSheetOpen;


        });

        txtImage.setOnClickListener(view -> goToImageGalleryintent());
        txtVideo.setOnClickListener(view -> goToVideoGalleryIntent());
        txtDoc.setOnClickListener(view -> FileUtils.pickDocument(getActivity(), RESULT_DOC));
        txtAudio.setOnClickListener(view -> goToAudioIntent());


    }

    private void goToAudioIntent() {
        Intent intent_upload = new Intent();
        intent_upload.setType("audio/*");
        intent_upload.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent_upload, RESULT_AUDIO);
    }


    private boolean isReadStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (getContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                return true;
            } else {
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation

            return true;
        }
    }

    private void goToVideoGalleryIntent() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, RESULT_LOAD_VIDEO);
    }

    @SuppressLint("IntentReset")
    private void goToImageGalleryintent() {
        @SuppressLint("IntentReset")
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE) {
            Uri selectedImage = data.getData();

            try {
                String filePath = PathUtil.getPath(getActivity(), selectedImage);
                assert filePath != null;
                File file = new File(filePath);
                uploadFileFromRetrofit(file, "image/jpeg", "image");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (requestCode == RESULT_LOAD_VIDEO) {
            Uri selectedImage = data.getData();

            String filePath = null;
            try {
                filePath = PathUtil.getPath(getActivity(), selectedImage);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            assert filePath != null;
            File file = new File(filePath);

            uploadFileFromRetrofit(file, "video/mp4", "video");
        }


        if (requestCode == RESULT_DOC) {
            if (data == null)
                return;

            List<Uri> fileUris = data.getParcelableArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS);

            File file = FileUtil.getFile(getContext(), fileUris.get(0));

            uploadFileFromRetrofit(file, "document/pdf", "Document");
        }


        if (requestCode == RESULT_AUDIO) {
            if (data == null)
                return;
            Uri selectedDoc = data.getData();
            File file = FileUtil.getFile(getContext(), selectedDoc);
            uploadFileFromRetrofit(file, "audio/mp3", "audio");
        }
    }

    private void uploadFileFromRetrofit(File file, String type, String format) {
        floatingAddFolder.callOnClick();
        showProgressBar();

        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getContext());

        RequestBody requestFile =
                RequestBody.create(MediaType.parse(type), file);
        MultipartBody.Part body =
                MultipartBody.Part.createFormData(format, file.getName(), requestFile);

        RequestBody folderId = RequestBody.create(MediaType.parse("multipart/form-data"), FolderId);
        RequestBody userId = RequestBody.create(MediaType.parse("multipart/form-data"), SharedPref.getUserRegistration().getId());
        RequestBody groupId = RequestBody.create(MediaType.parse("multipart/form-data"), id);
        RequestBody isSharable = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(true));


        apiServiceProvider.uploadFile(body, folderId, userId, groupId, isSharable, null, new RetrofitListener() {
            @Override
            public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {
                hideProgressBar();
                fetchData();
            }

            @Override
            public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
                hideProgressBar();
            }
        });
    }

    /*@Override
    public void onFolderClick(ViewContents.Document document) {

    }*/
}