package com.familheey.app.Activities;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.familheey.app.Adapters.SubFoldersAdapter;
import com.familheey.app.Dialogs.DocumentNameEditorDialog;
import com.familheey.app.Dialogs.DocumentRenameDialog;
import com.familheey.app.Interfaces.RetrofitListener;
import com.familheey.app.Models.ApiCallbackParams;
import com.familheey.app.Models.ErrorData;
import com.familheey.app.Models.Response.ViewContents;
import com.familheey.app.Networking.Retrofit.ApiServiceProvider;
import com.familheey.app.R;
import com.familheey.app.Utilities.Constants;
import com.familheey.app.Utilities.FileUtils;
import com.familheey.app.Utilities.SharedPref;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

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
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static com.familheey.app.Activities.CreateAlbumDetailedActivity.ALBUM_EDIT_REQUEST_CODE;
import static com.familheey.app.Utilities.Constants.Bundle.CAN_CREATE;
import static com.familheey.app.Utilities.Constants.Bundle.CAN_UPDATE;
import static com.familheey.app.Utilities.Constants.Bundle.DATA;
import static com.familheey.app.Utilities.Constants.Bundle.DESCRIPTION;
import static com.familheey.app.Utilities.Constants.Bundle.ID;
import static com.familheey.app.Utilities.Constants.Bundle.IS_ADMIN;
import static com.familheey.app.Utilities.Constants.Bundle.IS_ALBUM;
import static com.familheey.app.Utilities.Constants.Bundle.IS_FAMILY_SETTINGS_NEEDED;
import static com.familheey.app.Utilities.Constants.Bundle.PERMISSION;
import static com.familheey.app.Utilities.Constants.Bundle.PERMISSION_GRANTED_USERS;
import static com.familheey.app.Utilities.Constants.Bundle.TITLE;
import static com.familheey.app.Utilities.Constants.Bundle.TYPE;
import static com.familheey.app.Utilities.Constants.FileUpload.TYPE_FAMILY;

public class SubFolderActivity extends AppCompatActivity implements SubFoldersAdapter.OnDocumentSelectedListener, DocumentNameEditorDialog.OnDocumentNameChanged, DocumentRenameDialog.OnDocumentNameChanged {

   private static int RESULT_DOC = 12;

    @BindView(R.id.recycler_folder)
    RecyclerView recycler_folder;

    @BindView(R.id.floatingAddFolder)
    CardView floatingAddFolder;

    @BindView(R.id.progressSubFolder)
    ProgressBar progressBar;

    @BindView(R.id.goBack)
    ImageView goBack;
    @BindView(R.id.album_tittle)
    TextView toolBarTitle;
    @BindView(R.id.imageEdit)
    ImageView imageEdit;

    @BindView(R.id.deleteAlbumElements)
    MaterialButton deleteAlbumElements;
    @BindView(R.id.album_description)
    TextView documentDescription;

    @BindView(R.id.txt_less_or_more)
    TextView txt_less_or_more;
    @BindView(R.id.txt_temp)
    TextView textTemp;
    @BindView(R.id.empty_tv)
    TextView emptyDocumentsIndicator;
    @BindView(R.id.addFiles)
    MaterialButton addFiles;

    private SubFoldersAdapter adapter;
    private final List<Long> selectedElementIds = new ArrayList<>();
    private final List<ViewContents.Document> documents = new ArrayList<>();
    private String id;
    private String folderId;
    private int type;
    private boolean isAdmin = false;
    private String title = "";
    private String description = "";
    private ViewContents viewContents;

    private ArrayList<Integer> permissionGrantedUsers = new ArrayList<>();
    private String permission = "";
    private boolean canUpdate = true;
    private boolean canCreate = true;
    private String Rename = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_folder);
        ButterKnife.bind(this);
        parseIntent();
        initializeToolbar();
        initializeAdapter();
        fetchData();
    }

    private void initializeAdapter() {
        adapter = new SubFoldersAdapter(documents, this, isAdmin || canUpdate || canCreate);
        recycler_folder.setHasFixedSize(true);
        recycler_folder.setLayoutManager(new GridLayoutManager(SubFolderActivity.this, 3));
        recycler_folder.setAdapter(adapter);
    }

    private void initializeToolbar() {
        toolBarTitle.setText(title);
        goBack.setOnClickListener(v -> finish());
    }

    private void parseIntent() {
        id = getIntent().getStringExtra(Constants.Bundle.ID);
        folderId = getIntent().getStringExtra(Constants.Bundle.FOLDER_ID);
        type = getIntent().getIntExtra(Constants.Bundle.TYPE, 0);
        isAdmin = getIntent().getBooleanExtra(Constants.Bundle.IS_ADMIN, false);
        title = getIntent().getStringExtra(Constants.Bundle.TITLE);
        description = getIntent().getStringExtra(Constants.Bundle.DESCRIPTION);
        isAdmin = getIntent().getBooleanExtra(IS_ADMIN, false);
        canUpdate = getIntent().getBooleanExtra(CAN_UPDATE, false);
        canCreate = getIntent().getBooleanExtra(CAN_CREATE, false);
        if (description != null)
            documentDescription.setText(description);
        else {
            description = "";
            documentDescription.setText("");
        }
        addViewMoreLogic(description);
        if (isAdmin || canUpdate || canCreate) {
            imageEdit.setVisibility(View.VISIBLE);
            addFiles.setVisibility(View.VISIBLE);
        } else {
            imageEdit.setVisibility(View.INVISIBLE);
            addFiles.setVisibility(View.INVISIBLE);
        }
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
                      //  FileUtils.pickDocument(SubFolderActivity.this, RESULT_DOC);
                        Intent intent = new Intent();
                        intent.setAction( Intent.ACTION_OPEN_DOCUMENT );
                        intent.addCategory( Intent.CATEGORY_OPENABLE );
                        intent.setType( "*/*" );
                        String[] mimeTypes = {FileUtils.MimeTypes.DOC, FileUtils.MimeTypes.DOCX, FileUtils.MimeTypes.PDF, FileUtils.MimeTypes.PPT,
                                FileUtils.MimeTypes.PPTX, FileUtils.MimeTypes.XLS, FileUtils.MimeTypes.XLSX, FileUtils.MimeTypes.XLA,
                                FileUtils.MimeTypes.zip1, FileUtils.MimeTypes.zip2,FileUtils.MimeTypes.rar1,FileUtils.MimeTypes.rar2, FileUtils.MimeTypes.text1};
                        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                        intent.putExtra( Intent.EXTRA_MIME_TYPES, mimeTypes );
                        startActivityForResult( Intent.createChooser( intent, "Select Document " ), RESULT_DOC );
                    }

                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {

                    }
                })
                .setDeniedMessage("If you reject permission,you can not upload Documents\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();
    }


    private void fetchData() {
        showProgressBar();
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(SubFolderActivity.this);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("folder_id", folderId);
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
                viewContents = new Gson().fromJson(responseBodyString, ViewContents.class);
                title = viewContents.getFolderDetails().getFolderName();
                description = viewContents.getFolderDetails().getDescription();
                toolBarTitle.setText(title);
                documentDescription.setText(description);
                permissionGrantedUsers = (ArrayList<Integer>) viewContents.getFolderDetails().getPermissionUsers();
                permission = viewContents.getFolderDetails().getPermissions();
                if (permission == null)
                    permission = "all";
                if (permissionGrantedUsers == null)
                    permissionGrantedUsers = new ArrayList<>();
                documents.clear();
                documents.addAll(viewContents.getDocuments());
                hideProgressBar();
                if (documents.size() == 0)
                    emptyDocumentsIndicator.setVisibility(View.VISIBLE);
                else emptyDocumentsIndicator.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
                hideProgressBar();
            }
        });
    }
    private void confirmation(List<Uri> fileUris ) {
        SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setContentText("This file name already exist, Are you sure to upload?")
                .setConfirmText("Upload")
                .setCancelText("Cancel");

        pDialog.show();
        pDialog.setConfirmClickListener(sDialog -> {
           // deletePost(postData.getPost_id() + "");
            MultipartBody.Builder builder = getPostMultipartBuilder(fileUris.get(0));
            callDocumentCreateApi(builder);
            pDialog.dismiss();

        });
        pDialog.setCancelClickListener(sDialog -> {

            DocumentRenameDialog.newInstance(documents,fileUris).show(getSupportFragmentManager(), "FileNameChanger");
           //  deletePost(postData.getPost_id() + "");
            pDialog.dismiss();

        });
    }

    private void callDocumentCreateApi(MultipartBody.Builder builder) {
        showProgressBar();
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getApplicationContext());
        apiServiceProvider.uploadFile(builder, null, new RetrofitListener() {
            @Override
            public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {
                hideProgressBar();
                fetchData();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
                hideProgressBar();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_DOC) {
            if (data == null)
                return;
         
          //  List<Uri> fileUris = data.getParcelableArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS);

            Uri uri = data.getData();
            List<Uri> fileUris;
            fileUris = new ArrayList<>();
            fileUris.add(uri);
        try {
            File file = readContentToFile( uri );
            boolean exist = false;
            String File_Name = getDisplayName( uri );


            for (int i = 0; i < documents.size(); i++) {
                if (documents.get( i ).getOriginalname().equals( File_Name )) {
                    exist = true;
                    break;
                }
            }
            if (exist) {
                confirmation( fileUris );
            } else {
                MultipartBody.Builder builder = getPostMultipartBuilder( fileUris.get( 0 ) );
                callDocumentCreateApi( builder );
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        } else if (requestCode == ALBUM_EDIT_REQUEST_CODE && resultCode == RESULT_OK) {
            assert data != null;
            toolBarTitle.setText(data.getStringExtra(TITLE));
            description = data.getStringExtra(DESCRIPTION);
            documentDescription.setText(description);
            addViewMoreLogic(description);
            viewContents.getFolderDetails().setPermissions(data.getStringExtra(PERMISSION));
            viewContents.getFolderDetails().setPermissionUsers(data.getIntegerArrayListExtra(PERMISSION_GRANTED_USERS));
            viewContents.getFolderDetails().setFolderName(data.getStringExtra(TITLE));
            viewContents.getFolderDetails().setDescription(data.getStringExtra(DESCRIPTION));
            permissionGrantedUsers = data.getIntegerArrayListExtra(PERMISSION_GRANTED_USERS);
            permission = data.getStringExtra(PERMISSION);
            title = data.getStringExtra(TITLE);
            description = data.getStringExtra(DESCRIPTION);
            fetchData();

        }
    }

    public MultipartBody.Builder getPostMultipartBuilder(Uri fileUri) {
        MultipartBody.Builder multiPartBodyBuilder = new MultipartBody.Builder().setType(Objects.requireNonNull(MediaType.parse("multipart/form-data")));
        multiPartBodyBuilder.addFormDataPart("permissions", "all");
        multiPartBodyBuilder.addFormDataPart("folder_id", folderId);
        multiPartBodyBuilder.addFormDataPart("user_id", SharedPref.getUserRegistration().getId());
        switch (type) {
            case Constants.FileUpload.TYPE_EVENTS:
                multiPartBodyBuilder.addFormDataPart("event_id", id);
                break;
            case Constants.FileUpload.TYPE_FAMILY:
                multiPartBodyBuilder.addFormDataPart("group_id", id);
                break;
            case Constants.FileUpload.TYPE_USER:

                break;
        }
        multiPartBodyBuilder.addFormDataPart("is_sharable", String.valueOf(true));
        try {
            File file = readContentToFile( fileUri );
            multiPartBodyBuilder.addFormDataPart( "file_name", getDisplayName( fileUri ) );
            String MIME_TYPE = "application/" + getfileExtension( fileUri );
            MediaType mediaType = MediaType.parse( MIME_TYPE );
            RequestBody requestBody = RequestBody.create( file, mediaType );
            multiPartBodyBuilder.addFormDataPart( "Documents", getDisplayName( fileUri ), requestBody );
            return multiPartBodyBuilder;
        } catch (IOException e) {
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
    public MultipartBody.Builder getPostMultipartBuilderWithNewName(Uri fileUri,String fileName) {
        MultipartBody.Builder multiPartBodyBuilder = new MultipartBody.Builder().setType(Objects.requireNonNull(MediaType.parse("multipart/form-data")));
        multiPartBodyBuilder.addFormDataPart("permissions", "all");
        multiPartBodyBuilder.addFormDataPart("folder_id", folderId);
        multiPartBodyBuilder.addFormDataPart("user_id", SharedPref.getUserRegistration().getId());
        switch (type) {
            case Constants.FileUpload.TYPE_EVENTS:
                multiPartBodyBuilder.addFormDataPart("event_id", id);
                break;
            case Constants.FileUpload.TYPE_FAMILY:
                multiPartBodyBuilder.addFormDataPart("group_id", id);
                break;
            case Constants.FileUpload.TYPE_USER:

                break;
        }
        multiPartBodyBuilder.addFormDataPart("is_sharable", String.valueOf(true));
        try {
            File file = readContentToFile( fileUri );
            multiPartBodyBuilder.addFormDataPart( "file_name", fileName );
            String MIME_TYPE = "application/" + getfileExtension( fileUri );
            MediaType mediaType = MediaType.parse( MIME_TYPE );
            RequestBody requestBody = RequestBody.create( file, mediaType );
            multiPartBodyBuilder.addFormDataPart( "Documents", fileName, requestBody );
            return multiPartBodyBuilder;
        } catch (IOException e) {
            return null;
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
    @OnClick({R.id.floatingAddFolder, R.id.deleteAlbumElements, R.id.addFiles, R.id.imageEdit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.floatingAddFolder:
            case R.id.addFiles:
                if (isAdmin || canUpdate || canCreate) {
                    if (isReadStoragePermissionGranted()) {
                        //   FileUtils.pickDocument(this, RESULT_DOC);
                        Intent intent = new Intent();
                        intent.setAction( Intent.ACTION_OPEN_DOCUMENT );
                        intent.addCategory( Intent.CATEGORY_OPENABLE );
                        intent.setType( "*/*" );
                        String[] mimeTypes = {FileUtils.MimeTypes.DOC, FileUtils.MimeTypes.DOCX, FileUtils.MimeTypes.PDF, FileUtils.MimeTypes.PPT,
                                FileUtils.MimeTypes.PPTX, FileUtils.MimeTypes.XLS, FileUtils.MimeTypes.XLSX, FileUtils.MimeTypes.XLA,
                                FileUtils.MimeTypes.zip1, FileUtils.MimeTypes.zip2,FileUtils.MimeTypes.rar1,FileUtils.MimeTypes.rar2, FileUtils.MimeTypes.text1};
                        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                        intent.putExtra( Intent.EXTRA_MIME_TYPES, mimeTypes );
                        startActivityForResult( Intent.createChooser( intent, "Select Document " ), RESULT_DOC );
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "You don't have authorization to create documents", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.deleteAlbumElements:
                SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                        .setContentText(" Are you sure to delete?")
                        .setConfirmText("Yes")
                        .setCancelText("Cancel");

                pDialog.show();
                pDialog.setConfirmClickListener(sDialog -> {
                    deleteAlbumElements();
                    pDialog.cancel();

                });
                pDialog.setCancelClickListener(sDialog -> {
                    pDialog.cancel();

                });
                break;
            case R.id.imageEdit:
                Intent intent = new Intent(this, CreateAlbumBasicActivity.class);
                intent.putExtra(TYPE, type);
                intent.putExtra(TITLE, title);
                intent.putExtra(DESCRIPTION, description);
                intent.putExtra(ID, folderId);
                intent.putExtra(IS_ALBUM, false);
                if (permission != null)
                    intent.putExtra(PERMISSION, viewContents.getFolderDetails().getPermissions());
                else intent.putExtra(PERMISSION, "all");
                intent.putExtra(DATA, id);
                if (type == TYPE_FAMILY) {
                    if (isAdmin && !canUpdate)
                        intent.putExtra(IS_FAMILY_SETTINGS_NEEDED, false);
                }
                if (permissionGrantedUsers != null)
                    intent.putIntegerArrayListExtra(PERMISSION_GRANTED_USERS, (ArrayList<Integer>) viewContents.getFolderDetails().getPermissionUsers());
                else intent.putExtra(PERMISSION_GRANTED_USERS, new ArrayList<>());
                startActivityForResult(intent, ALBUM_EDIT_REQUEST_CODE);
                break;
        }
    }

    @Override
    public void onDocumentElementSelected(ViewContents.Document document, int position) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(document.getUrl()));
        startActivity(browserIntent);
    }

    @Override
    public void onDocumentSelectedForDeletion(List<Long> selectedElementIds) {
        this.selectedElementIds.addAll(selectedElementIds);
        deleteAlbumElements.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDocumentDeselected() {
        deleteAlbumElements.setVisibility(View.GONE);
        selectedElementIds.clear();
    }

    @Override
    public void onDocumentSelectedForEditing(ViewContents.Document document, int position) {
        DocumentNameEditorDialog.newInstance(document,documents).show(getSupportFragmentManager(), "FileNameChanger");
    }

    private void deleteAlbumElements() {
        progressBar.setVisibility(View.VISIBLE);
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
                progressBar.setVisibility(View.INVISIBLE);
                for (Iterator<ViewContents.Document> it = documents.iterator(); it.hasNext(); ) {
                    if (selectedElementIds.contains(it.next().getId())) {
                        it.remove();
                    }
                }
                adapter.notifyDataSetChanged();
                deleteAlbumElements.setVisibility(View.GONE);
                if (documents.size() == 0)
                    emptyDocumentsIndicator.setVisibility(View.VISIBLE);
                else emptyDocumentsIndicator.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(SubFolderActivity.this, Constants.SOMETHING_WRONG, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDocumentNameChanged(ViewContents.Document document) {
        for (int i = 0; i < documents.size(); i++) {
            if (document.getId().equals(documents.get(i).getId())) {
                documents.get(i).setFileName(document.getFileName());
            }
        }
        adapter.notifyDataSetChanged();
    }
    @Override
    public void onDocumentNameChanged(String fileName,List<Uri> fileUri) {
        String fileNames=fileName;
        MultipartBody.Builder builder = getPostMultipartBuilderWithNewName(fileUri.get(0),fileName);
        callDocumentCreateApi(builder);
    }

    void addViewMoreLogic(String description) {
        documentDescription.setText(description);
        textTemp.setText(description);
        textTemp.post(() -> {
            if (textTemp.getLineCount() > 2) {
                txt_less_or_more.setVisibility(View.VISIBLE);
                documentDescription.setMaxLines(2);
                documentDescription.setEllipsize(TextUtils.TruncateAt.END);
            } else {
                txt_less_or_more.setVisibility(View.GONE);
            }
            documentDescription.setText(description);
        });
        txt_less_or_more.setOnClickListener(v -> {
            if (txt_less_or_more.getText().equals("Read More")) {
                txt_less_or_more.setText("Read Less");
                documentDescription.setText(description);
                documentDescription.setMaxLines(Integer.MAX_VALUE);
                documentDescription.setEllipsize(null);
            } else {
                txt_less_or_more.setText("Read More");
                documentDescription.setMaxLines(2);
                documentDescription.setEllipsize(TextUtils.TruncateAt.END);
            }
        });

    }
}
