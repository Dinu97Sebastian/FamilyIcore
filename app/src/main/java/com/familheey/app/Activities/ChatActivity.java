package com.familheey.app.Activities;

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
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.UserStateDetails;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferService;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.devlomi.record_view.OnRecordListener;
import com.devlomi.record_view.RecordView;
import com.familheey.app.Adapters.ChatAdapter;
import com.familheey.app.Announcement.AnnouncementDetailActivity;
import com.familheey.app.FamilheeyApplication;
import com.familheey.app.Firebase.MessagePushDelegate;
import com.familheey.app.Fragments.Posts.PostData;
import com.familheey.app.Interfaces.RetrofitListener;
import com.familheey.app.Models.ApiCallbackParams;
import com.familheey.app.Models.ChatModel;
import com.familheey.app.Models.ErrorData;
import com.familheey.app.Models.Request.RequstComment;
import com.familheey.app.Models.Response.GetCommentsResponse;
import com.familheey.app.Models.Response.SocketCommentResponse;
import com.familheey.app.Networking.Retrofit.ApiServiceProvider;
import com.familheey.app.Networking.Retrofit.ApiServices;
import com.familheey.app.Networking.Retrofit.RetrofitBase;
import com.familheey.app.Post.CreatePostActivity;
import com.familheey.app.Post.PostDetailForPushActivity;
import com.familheey.app.R;
import com.familheey.app.Topic.MainActivity;
import com.familheey.app.Utilities.Constants;
import com.familheey.app.Utilities.FileUtil;
import com.familheey.app.Utilities.FileUtils;
import com.familheey.app.Utilities.GifSizeFilter;
import com.familheey.app.Utilities.SharedPref;
import com.familheey.app.Utilities.Utilities;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.vanniktech.emoji.EmojiPopup;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.filter.Filter;

import org.json.JSONArray;
import org.ocpsoft.prettytime.PrettyTime;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

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

import static com.familheey.app.Utilities.Constants.ApiPaths.FIREBASE_DATABASE_URL;
import static com.familheey.app.Utilities.Constants.ApiPaths.IMAGE_BASE_URL;
import static com.familheey.app.Utilities.Constants.ApiPaths.SOCKET_COMMENT_URL;
import static com.familheey.app.Utilities.Constants.Bundle.DATA;
import static com.familheey.app.Utilities.Constants.Bundle.NOTIFICATION_ID;
import static com.familheey.app.Utilities.Constants.Bundle.POSITION;
import static com.familheey.app.Utilities.Constants.Bundle.PUSH;
import static com.familheey.app.Utilities.Constants.Bundle.SUB_TYPE;
import static com.familheey.app.Utilities.Constants.Bundle.TYPE;
import static com.familheey.app.Utilities.Constants.Paths.PROFILE_PIC;
public class ChatActivity extends AppCompatActivity implements MessagePushDelegate, ChatAdapter.OnChatLongClickListener {
    private static final int RESULT_LOAD_VIDEO = 13;
    private static final int RESULT_DOC = 36;
    private final List<SocketCommentResponse.Attachment> attachments = new ArrayList<>();
    private static final int PICK_IMAGE = 23;
    private static final int REQUEST_CODE_PERMISSIONS = 12;
    private ImageView goBack;
    private ImageView chat_prof;
    private ImageView btnqouteSend;
    private ImageView buttonSend;
    private ImageView imageAttach;
    private TextView toolBarTitle, txtImage, txtCancel, txtVideo, txtDoc, txt_qoute, txt_qoute_user_name, txt_qoute_date,txtAudio;
    private CardView cardBottomSheet;
    private ProgressBar progressBar, progress;
    private String postId;
    private PostData postData;
    private Socket mSocket;
    private ChatAdapter chatAdapter;
    private RecyclerView recyclerView;
    private RelativeLayout chatview, qoutechat;
    private final List<ChatModel> chatModelList = new ArrayList<>();

    //private List<GetCommentsResponse.Data> chatModelList1 = new ArrayList<>();
    private EditText edtxqouteMessage;
    private com.devlomi.record_view.RecordButton btnRecord;
    List<Uri> mSelectedUri;
    private ChatModel chatModel;
    static final String MIME_TYPE_PDF = "application/pdf";
    static final String MIME_TYPE_TEXT = "text/*";
    static final String MIME_TYPE_AUDIO = "audio/*";

    boolean isCardOpen = false;
    boolean isChated = false;
    private int position = 0;
    public CompositeDisposable subscriptions;
    private final int RECORD_AUDIO_REQUEST_CODE = 123;
    private RecordView recordView;
    private MediaRecorder mediaRecorder;
    String AudioSavePathInDevice = null;
    private String SUBTYPE = "";
    private String Type = "";
    private String Filename = "";
    private int UPLOAD_FILE_POSITION = 0;
    private String notificationId = "";
    private DatabaseReference database;
    private EditText edtxMessage,editQuoteMessage;
    private int RESULT_AUDIO=111;
    private View rootView;
    private ImageView emojiButton,btn_qouteEmoji;
    private final String tempDirectoryName = "temp";
    //    private EmojIconActions emojIconActions;
    //SUBTYPE,
    private final Emitter.Listener onNewMessage = args -> ChatActivity.this.runOnUiThread(() -> {

        JSONArray data = (JSONArray) args[0];
        Type listType = new TypeToken<List<SocketCommentResponse>>() {
        }.getType();
        ArrayList<SocketCommentResponse> socketCommentResponseList = new Gson().fromJson(String.valueOf(data), listType);
        for (int i = 0; i < socketCommentResponseList.size(); i++) {
            String imagetype = "", image = "";
            if (socketCommentResponseList.get(0).getType().equals("delete_comment")) {
                for (int j = 0; j < chatModelList.size(); j++) {
                    if (chatModelList.get(j).getComment_id().equals(socketCommentResponseList.get(0).getDelete_id().get(0))) {
                        chatModelList.remove(j);
                        chatAdapter.notifyDataSetChanged();
                        break;
                    }
                }
            } else {
                if (socketCommentResponseList.get(0).getAttachmentList().size() > 0) {
                    image = socketCommentResponseList.get(0).getAttachmentList().get(0).getFilename();
                    imagetype = socketCommentResponseList.get(0).getAttachmentList().get(0).getType();
                }
                ChatModel chatModel;
                if (socketCommentResponseList.get(i).getCommented_by().equals(SharedPref.getUserRegistration().getId())) {
                    chatModel = new ChatModel(true, socketCommentResponseList.get(i).getComment(), socketCommentResponseList.get(i).getFull_name(), image, "", socketCommentResponseList.get(i).getCreatedAt(), imagetype, socketCommentResponseList.get(i).getComment_id(), Integer.parseInt(socketCommentResponseList.get(i).getCommented_by()), socketCommentResponseList.get(i).getQuoted_date(), socketCommentResponseList.get(i).getQuoted_id(), socketCommentResponseList.get(i).getQuoted_item(), socketCommentResponseList.get(i).getQuoted_user(), socketCommentResponseList.get(0).getAttachmentList());
                } else {
                    chatModel = new ChatModel(false, socketCommentResponseList.get(i).getComment(), socketCommentResponseList.get(i).getFull_name(), image, IMAGE_BASE_URL + "propic/" + socketCommentResponseList.get(i).getPropic(), socketCommentResponseList.get(i).getCreatedAt(), imagetype, socketCommentResponseList.get(i).getComment_id(), Integer.parseInt(socketCommentResponseList.get(i).getCommented_by()), socketCommentResponseList.get(i).getQuoted_date(), socketCommentResponseList.get(i).getQuoted_id(), socketCommentResponseList.get(i).getQuoted_item(), socketCommentResponseList.get(i).getQuoted_user(), socketCommentResponseList.get(0).getAttachmentList());
                }
                chatModelList.add(chatModel);
                chatAdapter.notifyDataSetChanged();
                if (recyclerView != null) {
                    recyclerView.scrollToPosition(chatModelList.size() - 1);
                }
            }

        }
    });

    private void initToolbar() {

        toolBarTitle.setText(postData.getSnap_description());

        if (postData.getPost_attachment().size() > 0) {

            if (postData.getPost_attachment().get(0).getType().contains("image")) {

                RequestOptions requestOptions = new RequestOptions();
                requestOptions.transforms(new RoundedCorners(16));
                Glide.with(this)
                        .load(Constants.ApiPaths.IMAGE_BASE_URL + "post/" + postData.getPost_attachment().get(0).getFilename())
                        .apply(requestOptions).into(chat_prof);
            } else {
                chat_prof.setVisibility(View.GONE);
            }

        } else {
            chat_prof.setVisibility(View.GONE);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inits();
        setContentView(R.layout.activity_chat);
        rootView=findViewById(R.id.root_view);
        emojiButton=findViewById(R.id.btn_emoji);
        btn_qouteEmoji=findViewById(R.id.btn_qouteEmoji);
        subscriptions = new CompositeDisposable();
        goBack = findViewById(R.id.goBack);
        chat_prof = findViewById(R.id.chat_prof);
        buttonSend = findViewById(R.id.btnSend);
        txtVideo = findViewById(R.id.txtVideo);
        txtDoc = findViewById(R.id.txtDoc);
        edtxMessage = findViewById(R.id.edtxMessage);
        editQuoteMessage= findViewById(R.id.edtxqouteMessage);
        btnRecord = findViewById(R.id.btnRecord);
        recyclerView = findViewById(R.id.recyclerView);
        cardBottomSheet = findViewById(R.id.bottom_sheet);
        progressBar = findViewById(R.id.progressBar);
        progress = findViewById(R.id.progress);
        imageAttach = findViewById(R.id.imageAttach);
        toolBarTitle = findViewById(R.id.toolBarTitle);
        txtImage = findViewById(R.id.txtImage);
        txtCancel = findViewById(R.id.txtCancel);
        recordView = findViewById(R.id.record_view);
        btnRecord.setRecordView(recordView);
        recordView.setCancelBounds(8);
        chatview = findViewById(R.id.chatview);
        qoutechat = findViewById(R.id.qoutechat);
        txt_qoute = findViewById(R.id.txt_qoute);
        txt_qoute_user_name = findViewById(R.id.txt_qoute_user_name);
        txt_qoute_date = findViewById(R.id.txt_qoute_date);
        edtxqouteMessage = findViewById(R.id.edtxqouteMessage);
        btnqouteSend = findViewById(R.id.btnqouteSend);
//        emojIconActions=new EmojIconActions(this,rootView,edtxMessage,emojiButton);
//        emojIconActions.ShowEmojIcon();

        final EmojiPopup popup = EmojiPopup.Builder
                .fromRootView(findViewById(R.id.root_view)).build(edtxMessage);
        final EmojiPopup qoutepopup = EmojiPopup.Builder
                .fromRootView(findViewById(R.id.root_view)).build(editQuoteMessage);

        txtAudio = findViewById(R.id.txtAudio);
        getIntentData();
        emojiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popup.toggle();
            }
        });
        btn_qouteEmoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                qoutepopup.toggle();
            }
        });

        chat_prof.setOnClickListener(view -> {
            if ("ANNOUNCEMENT".equals(SUBTYPE)) {
                Intent intent = new Intent(this, AnnouncementDetailActivity.class).putExtra(TYPE, "NOTIFICATION").putExtra("id", postData.getPost_id() + "");
                startActivity(intent);

            } else {
                Intent intent = new Intent(this, PostDetailForPushActivity.class).putExtra(TYPE, "NOTIFICATION").putExtra("ids", postData.getPost_id() + "");
                startActivity(intent);
            }
        });

        toolBarTitle.setOnClickListener(view -> {
            if ("ANNOUNCEMENT".equals(SUBTYPE)) {
                Intent intent = new Intent(this, AnnouncementDetailActivity.class).putExtra(TYPE, "NOTIFICATION").putExtra("id", postData.getPost_id() + "");
                startActivity(intent);
            } else {
                Intent intent = new Intent(this, PostDetailForPushActivity.class).putExtra(TYPE, "NOTIFICATION").putExtra("ids", postData.getPost_id() + "");
                startActivity(intent);
            }
        });

        findViewById(R.id.btn_qoute_view_close).setOnClickListener(View -> {
            chatview.setVisibility(android.view.View.VISIBLE);
            qoutechat.setVisibility(android.view.View.GONE);
            edtxMessage.setText(edtxqouteMessage.getText());
            edtxqouteMessage.setText("");
        });
        // Modified By: Dinu(22/02/2021) For update visible_status="read" in firebase
        if (getIntent().hasExtra(NOTIFICATION_ID)) {
            notificationId=getIntent().getStringExtra(NOTIFICATION_ID);
            database= FirebaseDatabase.getInstance(FIREBASE_DATABASE_URL).getReference(Constants.ApiPaths.FIREBASE_USER_TYPE + SharedPref.getUserRegistration().getId() + "_notification");
            database.child(notificationId).child("visible_status").setValue("read");
        }
    }


    private void iniView() {
        initToolbar();

        initListeners();
        initRecyclerView();

        if (postData != null) {
            fetchComments();
            this.runOnUiThread(this::initSocket);
        }
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (check()) {
                getPermissionToRecordAudio();
            }
        }*/
    }

    void showProgress() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    void hideProgress() {
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);

        }
    }

    private void fetchComments() {
        showProgress();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("post_id", postData.getPost_id() + "");
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());

        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(this);
        apiServiceProvider.getCommentsByPost(jsonObject, null, new RetrofitListener() {
            @Override
            public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {

                chatModelList.clear();
                GetCommentsResponse getCommentsResponse = new Gson().fromJson(responseBodyString, GetCommentsResponse.class);
                List<GetCommentsResponse.Data> data = getCommentsResponse.getData();


                for (int i = 0; i < data.size(); i++) {
                    String imageUrl = "", type = "", profPic;
                    profPic = data.get(i).getPropic();
                    if (data.get(i).getAttachment().size() != 0) {
                        imageUrl = data.get(i).getAttachment().get(0).getFilename();
                        type = data.get(i).getAttachment().get(0).getType();
                    }

                    ChatModel chatModel;
                    if (String.valueOf(data.get(i).getUserId()).equals(SharedPref.getUserRegistration().getId())) {
                        chatModel = new ChatModel(true, data.get(i).getComment(), data.get(i).getFullName(), imageUrl, Constants.ApiPaths.IMAGE_BASE_URL + PROFILE_PIC + profPic, data.get(i).getCreatedAt(), type, data.get(i).getCommentId() + "", data.get(i).getUserId(), data.get(i).getQuoted_date(), data.get(i).getQuoted_id(), data.get(i).getQuoted_item(), data.get(i).getQuoted_user(), data.get(i).getAttachment());
                    } else {
                        chatModel = new ChatModel(false, data.get(i).getComment(), data.get(i).getFullName(), imageUrl, Constants.ApiPaths.IMAGE_BASE_URL + PROFILE_PIC + profPic, data.get(i).getCreatedAt(), type, data.get(i).getCommentId() + "", data.get(i).getUserId(), data.get(i).getQuoted_date(), data.get(i).getQuoted_id(), data.get(i).getQuoted_item(), data.get(i).getQuoted_user(), data.get(i).getAttachment());
                    }
                    chatModelList.add(chatModel);
                }
                if (data.size() > 0) {
                    readStatus(data.get(data.size() - 1).getCommentId() + "");
                    chatAdapter.notifyDataSetChanged();
                }


                hideProgress();
                if (recyclerView != null) {
                    recyclerView.scrollToPosition(chatModelList.size() - 1);
                }

            }

            @Override
            public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
                hideProgress();
            }
        });
    }

    private void getIntentData() {
        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        position = bundle.getInt("POS");
        SUBTYPE = bundle.getString(SUB_TYPE);
        Type = bundle.getString(TYPE);
        if ((PUSH).equalsIgnoreCase(Type) || ("NOTIFICATION").equals(Type) || ("HOME").equals(Type)) {
            getPost(bundle.getString(DATA));
            postId = "post_channel_" + bundle.getString(DATA);
        } else {
            postData = getIntent().getParcelableExtra(DATA);
            if (postData != null) {
                postId = "post_channel_" + postData.getPost_id();
            }
            iniView();
        }
        getIntent().getExtras().clear();
        bundle.clear();
    }

    private void initSocket() {
        try {
            mSocket = IO.socket(Constants.ApiPaths.SOCKET_URL);

            mSocket.on(Socket.EVENT_CONNECT, args -> {
            })
                    .on(Socket.EVENT_ERROR, args -> {
                    });

            mSocket.on(postId, onNewMessage);
            mSocket.connect();

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSocket != null && mSocket.connected()) {
            mSocket.disconnect();
        }
    }

    private void initRecyclerView() {
        chatAdapter = new ChatAdapter(chatModelList, ChatActivity.this, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(ChatActivity.this));
        recyclerView.setAdapter(chatAdapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        FamilheeyApplication.messagePushDelegate = null;
        if (chatAdapter != null)
            chatAdapter.notifyDataSetChanged();
    }

    private void initListeners() {
        txtDoc.setOnClickListener(view -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (isReadWritePermissionGranted()) {
                    //   FileUtils.pickDocument(this, RESULT_DOC);
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
                else
                    showPermission(0);
            } else {
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
                //  FileUtils.pickDocument(this, RESULT_DOC);
            }
        });


        txtVideo.setOnClickListener(view -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (isReadWritePermissionGranted())
                    goToVideoGalleryIntent();
                else
                    showPermission(1);
            } else {
                goToVideoGalleryIntent();
            }
        });


        goBack.setOnClickListener(view -> onBackPressed());
        imageAttach.setOnClickListener(view -> showHideCard());
        txtCancel.setOnClickListener(view -> showHideCard());

        txtImage.setOnClickListener(view -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                if (isReadWritePermissionGranted())
                    goToImageGalleryintent();
                else
                    showPermission(3);

            } else {
                goToImageGalleryintent();
            }
        });

        txtAudio.setOnClickListener(view -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (isReadWritePermissionGranted()) {
                    Intent intent = new Intent();
                    intent.setType("audio/mpeg");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Audio "), RESULT_AUDIO);
                } else
                    showPermission(3);
            }
            else {
                Intent intent = new Intent();
                intent.setType("audio/mpeg");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Audio "), RESULT_AUDIO);
            }


        });

        buttonSend.setOnClickListener(view -> {
            if (validatedChat()) {
                addComment(edtxMessage.getText().toString(), false);
                edtxMessage.setText("");
            }
        });

        btnqouteSend.setOnClickListener(view -> {
            if (validatedQouteChat()) {
                addComment(edtxqouteMessage.getText().toString(), true);
                chatview.setVisibility(View.VISIBLE);
                qoutechat.setVisibility(View.GONE);
                chatModel = null;
                edtxqouteMessage.setText("");
            }
        });

        edtxMessage.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    buttonSend.setVisibility(View.VISIBLE);
                    btnRecord.setVisibility(View.GONE);
                } else {
                    btnRecord.setVisibility(View.VISIBLE);
                    buttonSend.setVisibility(View.GONE);
                }
            }
        });


        recordView.setOnRecordListener(new OnRecordListener() {
            @Override
            public void onStart() {
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (check()) {
                            recordStart();
                        } else {
                            showPermission(4);
                        }
                    } else {
                        recordStart();
                    }
                } catch (Exception e) {
                    Toast.makeText(ChatActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancel() {
                //On Swipe To Cancel
                if (mediaRecorder != null) {
                    unMuteBackgroundAudio();
                    mediaRecorder.stop();
                    mediaRecorder.release();

                }
            }

            @Override
            public void onFinish(long recordTime) {
                //Stop Recording..
                //String time = getHumanTimeText(recordTime);
                if (mediaRecorder != null) {
                    try {
                        unMuteBackgroundAudio();
                        mediaRecorder.stop();
                        mediaRecorder.release();
                        Uri uri = Uri.fromFile(new File(AudioSavePathInDevice));
                        mSelectedUri = new ArrayList<>();
                        mSelectedUri.add(uri);
                        uploadImagesToServer(MIME_TYPE_AUDIO);


                        imageAttach.setVisibility(View.VISIBLE);
                        edtxMessage.setVisibility(View.VISIBLE);
                        emojiButton.setVisibility(View.VISIBLE);
                        recordView.setVisibility(View.INVISIBLE);
                    } catch (Exception e) {
                        /*
                        need to handle
                         */
                    }
                }
            }

            @Override
            public void onLessThanSecond() {
                imageAttach.setVisibility(View.VISIBLE);
                edtxMessage.setVisibility(View.VISIBLE);
                emojiButton.setVisibility(View.VISIBLE);
                recordView.setVisibility(View.INVISIBLE);
            }
        });


        recordView.setOnBasketAnimationEndListener(() -> {
            imageAttach.setVisibility(View.VISIBLE);
            edtxMessage.setVisibility(View.VISIBLE);
            emojiButton.setVisibility(View.VISIBLE);
            recordView.setVisibility(View.INVISIBLE);

        });
    }


    private boolean validatedChat() {
        if (edtxMessage.getText().toString().trim().length() == 0) {
            Toast.makeText(ChatActivity.this, "Enter some text.", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private boolean validatedQouteChat() {
        if (edtxqouteMessage.getText().toString().trim().length() == 0) {
            Toast.makeText(ChatActivity.this, "Enter some text.", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void showHideCard() {
        if (isCardOpen) {
            cardBottomSheet.setVisibility(View.GONE);
        } else {
            cardBottomSheet.setVisibility(View.VISIBLE);

        }
        isCardOpen = !isCardOpen;
    }

    private void askForPermission(int i) {
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
                        v -> ActivityCompat.requestPermissions(ChatActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                REQUEST_CODE_PERMISSIONS)).show();
            } else {
                /* Request for permission */
                ActivityCompat.requestPermissions(ChatActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE_PERMISSIONS);
            }

        } else {
            if (i == 0) {
                FileUtils.pickDocument(this, RESULT_DOC);
            } else if (i == 1) {
                goToVideoGalleryIntent();
            } else {
                goToImageGalleryintent();
            }

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                goToImageGalleryintent();
            } else {
                Toast.makeText(ChatActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == RECORD_AUDIO_REQUEST_CODE) {
            if (grantResults.length != 3 &&
                    grantResults[0] != PackageManager.PERMISSION_GRANTED
                    && grantResults[1] != PackageManager.PERMISSION_GRANTED
                    && grantResults[2] != PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(this, "You must give permissions to use audio.", Toast.LENGTH_SHORT).show();

            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }

    private void recordStart() {
        try {
            muteBackgroundAudio();
            edtxMessage.setVisibility(View.INVISIBLE);
            recordView.setVisibility(View.VISIBLE);
            imageAttach.setVisibility(View.INVISIBLE);
            emojiButton.setVisibility(View.INVISIBLE);
            MediaRecorderReady();
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (IllegalStateException | IOException e) {
            e.printStackTrace();
        }
    }

    private void goToImageGalleryintent() {
        Matisse.from(ChatActivity.this)
                .choose(MimeType.ofImage())
                .showSingleMediaType(true)
                .countable(false)
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
        Matisse.from(ChatActivity.this)
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // For upload audio(Dinu)
        if (requestCode == RESULT_AUDIO ) {
            if (data != null && resultCode == RESULT_OK) {
                showHideCard();
                try {
                    Uri uri = data.getData();
                    mSelectedUri = new ArrayList<>();
                    mSelectedUri.add(uri);
                    uploadImagesToServer(MIME_TYPE_AUDIO);


                } catch (Exception e) {
                        /*
                        need to handle
                         */
                }
            }
        }

        if (requestCode == PICK_IMAGE) {
            if (resultCode == Activity.RESULT_OK && null != data) {

                imageAttach.setVisibility(View.GONE);

                buttonSend.setVisibility(View.GONE);
                btnRecord.setVisibility(View.GONE);
                edtxMessage.setEnabled(false);
                showHideCard();
                mSelectedUri = Matisse.obtainResult(data);
                try {
                    attachments.clear();
                    progress.setVisibility(View.VISIBLE);
                    new ImageCompressionAsyncTask(this).execute(readContentToFile(mSelectedUri.get(0)));
                } catch (Exception e) {
                    /*need to handle

                     */
                }
            }
        }

        if (requestCode == RESULT_LOAD_VIDEO) {
            if (resultCode == Activity.RESULT_OK && null != data) {
                showHideCard();
                if (Matisse.obtainResult(data).size() != 0) {
                    try {

                        imageAttach.setVisibility(View.GONE);
                        buttonSend.setVisibility(View.GONE);
                        btnRecord.setVisibility(View.GONE);
                        edtxMessage.setEnabled(false);
                        mSelectedUri = Matisse.obtainResult(data);
                        File file = readContentToFile(mSelectedUri.get(0));
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
                        String timeStamp = dateFormat.format(new Date());
                        Bitmap thumb = ThumbnailUtils.createVideoThumbnail(file.getPath(), MediaStore.Video.Thumbnails.MINI_KIND);
                        File thumbFile = new File(getCacheDir(), timeStamp + "_thumb.jpg");
                        boolean s = thumbFile.createNewFile();
                        OutputStream os = new BufferedOutputStream(new FileOutputStream(thumbFile));
                        assert thumb != null;
                        thumb.compress(Bitmap.CompressFormat.JPEG, 70, os);
                        os.close();
                        SocketCommentResponse.Attachment attachment = new SocketCommentResponse.Attachment();
                        attachment.setFilename(file.getName().replaceAll(" ", ""));
                        attachment.setType("video/mp4");
                        attachment.setVideo_thumb("video_thumb/" + thumbFile.getName());
                        attachments.add(attachment);
                        uploadWithTransferUtility(thumbFile, "video_thumb/" + thumbFile.getName(), "SINGLE");
                        uploadWithTransferUtility(file, "file_name/" + file.getName().replaceAll(" ", ""), "VIDEO");
                    } catch (Exception e) {
                        /*
                        need to handle
                         */
                    }
                }
            }

        }

        if (requestCode == RESULT_DOC) {
            if (data == null)
                return;
            mSelectedUri = new ArrayList<>();
            Uri uri = data.getData();
            mSelectedUri = new ArrayList<>();
            mSelectedUri.add(uri);

            //   mSelectedUri.addAll(Objects.requireNonNull(data.getParcelableArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS)));
            if (isReadStoragePermissionGranted()) {
                uploadImagesToServer(MIME_TYPE_PDF);
                showHideCard();
            }

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        FamilheeyApplication.messagePushDelegate = this;
        if (postData != null && postData.getPost_id() != null) {
            activatePost();
        }
    }

    @Override
    protected void onStop() {
        /*if (postData != null && postData.getPost_id() != null) {
            deactivatePost();
        }*/
        super.onStop();
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
                        FileUtils.pickDocument(ChatActivity.this, RESULT_DOC);
                    }

                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {

                    }
                })
                .setDeniedMessage("If you reject permission,you can not upload images\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();
    }

    void addComment(String comment, Boolean isQoute) {
        showProgress();
        RequstComment requstComment = new RequstComment();
        requstComment.setPost_id(postData.getPost_id() + "");
        requstComment.setComment(comment);
        requstComment.setCommented_by(SharedPref.getUserRegistration().getId());
        requstComment.setGroup_id(postData.getTo_group_id());
        requstComment.setTopic_id(postData.getPost_id() + "");
        if (ChatActivity.this.attachments.size() > 0) {
            requstComment.setAttachment(ChatActivity.this.attachments);
            requstComment.setFile_name(ChatActivity.this.attachments.get(0).getFilename());
            requstComment.setFile_type(ChatActivity.this.attachments.get(0).getType());
        }
        if (isQoute && chatModel != null) {
            requstComment.setQuoted_item(chatModel.getChatText());
            requstComment.setQuoted_id(chatModel.getComment_id());
            requstComment.setQuoted_user(chatModel.getSenderName());
            requstComment.setQuoted_date(chatModel.getCreatedAt());
        }
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(this);
        apiServiceProvider.postComment(requstComment, null, new RetrofitListener() {
            @Override
            public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {
                isChated = true;
                ChatActivity.this.attachments.clear();
                hideProgress();
                if (postData != null && postData.getPost_id() != null) {
                    activatePost();
                }
            }

            @Override
            public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
                hideProgress();
            }
        });
    }

    private void uploadImagesToServer(String type) {
        showProgress();
        ApiServices service = RetrofitBase.createRxResource(this, ApiServices.class);
        List<MultipartBody.Part> parts = new ArrayList<>();

        if (!type.contains("pdf")) {
            if (mSelectedUri != null) {
                for (int i = 0; i < mSelectedUri.size(); i++) {
                    parts.add(prepareFilePart(mSelectedUri.get(i), type));
                }
            }
        } else {
            if (mSelectedUri != null) {
                for (int i = 0; i < mSelectedUri.size(); i++) {
                    try {
                        String extension= "application/"+ getfileExtension(mSelectedUri.get(i));
                        parts.add(prepareFilePartPdf(mSelectedUri.get(i), extension));

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Oops something went wrong", Toast.LENGTH_SHORT).show();

                    }
                }
            }
        }


        RequestBody description = createPartFromString("post");
        RequestBody postId = createPartFromString(postData.getPost_id() + "");
        RequestBody commentedBy = createPartFromString(SharedPref.getUserRegistration().getId());
        RequestBody groupId = createPartFromString(postData.getTo_group_id());
        RequestBody comment = createPartFromString("");

        if (parts.size() == 0) {
            return;
        }

        Call<ResponseBody> call = service.uploadMultipleChatAttachment(SOCKET_COMMENT_URL + "posts/post_comment", description, postId, comment, commentedBy, groupId, parts);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                hideProgress();
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                hideProgress();
                Snackbar.make(findViewById(android.R.id.content),
                        "Upload failed!", Snackbar.LENGTH_LONG).show();
            }
        });

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

    private MultipartBody.Part prepareFilePartPdf(Uri fileUri, String type) {
        try {
            File file = readContentToFile( fileUri );

            RequestBody requestFile = RequestBody.create( file, MediaType.parse( type ) );
            return MultipartBody.Part.createFormData( "file_name", getDisplayName(fileUri), requestFile );
        } catch (IOException e) {
            return null;
        }
    }

    private RequestBody createPartFromString(String descriptionString) {
        if (descriptionString == null) {
            descriptionString = "";
        }
        return RequestBody.create(descriptionString, MediaType.parse(MIME_TYPE_TEXT));
    }

    @NonNull
    private MultipartBody.Part prepareFilePart(Uri fileUri, String type) {

        File file = FileUtil.getFile(this, fileUri);
//        getImageDimension(file);
        RequestBody requestFile = RequestBody.create( file, MediaType.parse( type ) );
        return MultipartBody.Part.createFormData( "file_name", file.getName(), requestFile );

    }


    private void readStatus(String m_id) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        jsonObject.addProperty("post_id", postData.getPost_id() + "");
        jsonObject.addProperty("last_read_message_id", m_id);
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        FamilheeyApplication application = FamilheeyApplication.get(this);
        ApiServices apiServices = RetrofitBase.createRxResource(this, ApiServices.class);
        subscriptions.add(apiServices.readStatus(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(response -> {
                }, throwable -> {
                }));
    }

    private void deleteComment(String comment_id) {
        showProgress();

        JsonObject jsonObject = new JsonObject();
        JsonArray jsonElements = new JsonArray();
        jsonElements.add(comment_id);
        jsonObject.add("comment_id", jsonElements);
        jsonObject.addProperty("post_id", postData.getPost_id() + "");
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(this);
        apiServiceProvider.postDelete(jsonObject, null, new RetrofitListener() {
            @Override
            public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {
                isChated = true;
                hideProgress();
                if (postData != null && postData.getPost_id() != null) {
                    activatePost();
                }
            }

            @Override
            public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
                hideProgress();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (("NOTIFICATION").equals(Type) || ("HOME").equals(Type)) {
            finish();

            overridePendingTransition(R.anim.left,
                    R.anim.right);
        } else if ((PUSH).equals(Type)) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();

            overridePendingTransition(R.anim.left,
                    R.anim.right);
        } else {
            if (isChated) {
                Intent intent = new Intent();
                intent.putExtra(POSITION, position);
                intent.putExtra("isChat", isChated);
                setResult(RESULT_OK, intent);
                finish();

                overridePendingTransition(R.anim.left,
                        R.anim.right);
            } else {
                Intent intent = new Intent();
                intent.putExtra(POSITION, position);
                intent.putExtra("isChat", false);
                setResult(RESULT_OK, intent);
                finish();

                overridePendingTransition(R.anim.left,
                        R.anim.right);
            }
        }


    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void getPermissionToRecordAudio() {

        if ((ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO) +
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) +
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE))
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(ChatActivity.this,
                    new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    RECORD_AUDIO_REQUEST_CODE);

        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }


    boolean check() {

        return TedPermission.isGranted(getApplicationContext(), Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);

    }

    //Dinu(20/08/2021) to get temp folder
    public String getTempFolder(Context context) {
        File tempDirectory = new File(context.getExternalFilesDir(null) + File.separator + tempDirectoryName);
        if (!tempDirectory.exists()) {
            System.out.println("creating directory: temp");
            tempDirectory.mkdir();
        }

        return tempDirectory.getAbsolutePath();
    }
    public void MediaRecorderReady() {
        AudioSavePathInDevice =
                getTempFolder(getApplicationContext())+"/" +
                        System.currentTimeMillis() + "AudioRecording.wav";
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(AudioSavePathInDevice);
    }

    @Override
    public void getPush(String type) {
        if (postData != null && postData.getPost_id() != null) {
            activatePost();
        }
    }


    private void getPost(String id) {
        showProgress();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("post_id", id);
        if ("ANNOUNCEMENT".equals(SUBTYPE)) {
            jsonObject.addProperty("type", "announcement");
        } else {
            jsonObject.addProperty("type", "post");
        }
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        FamilheeyApplication application = FamilheeyApplication.get(this);
        ApiServices apiServices = RetrofitBase.createRxResource(this, ApiServices.class);
        subscriptions.add(apiServices.getMyPost(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(response -> {
                    hideProgress();
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        if (response.body().getData().size() > 0) {
                            postData = response.body().getData().get(0);
                            iniView();
                        } else {

                            SweetAlertDialog contentNotFoundDialog = Utilities.getContentNotFoundDialog(this);
                            contentNotFoundDialog.setConfirmClickListener(sweetAlertDialog -> finish());
                            contentNotFoundDialog.setCanceledOnTouchOutside(false);
                            contentNotFoundDialog.setCancelable(false);
                            contentNotFoundDialog.show();
                            Utilities.addPositiveButtonMargin(contentNotFoundDialog);
                        }
                    } else {
                        SweetAlertDialog contentNotFoundDialog = Utilities.getContentNotFoundDialog(this);
                        contentNotFoundDialog.setConfirmClickListener(sweetAlertDialog -> finish());
                        contentNotFoundDialog.setCanceledOnTouchOutside(false);
                        contentNotFoundDialog.setCancelable(false);
                        contentNotFoundDialog.show();
                        Utilities.addPositiveButtonMargin(contentNotFoundDialog);
                    }
                }, throwable -> hideProgress()));
    }

    @Override
    public void onChatLongClicked(int position) {
        if (chatModelList.size() > position) {
            ChatModel chat = chatModelList.get(position);
            if (chat.getType().contains("audio")) {
                if (chat.isOwner())
                    confirmationDalog(chat.getComment_id());
                else
                    showChatOption(chat);
            } else if (chat.getFilename().toLowerCase().contains("mp4") || chat.getFilename().toLowerCase().contains("mov") || chat.getFilename().toLowerCase().contains("wmv") || chat.getFilename().toLowerCase().contains("webm") || chat.getFilename().toLowerCase().contains("mkv") || chat.getFilename().toLowerCase().contains("flv") || chat.getFilename().toLowerCase().contains("avi")) {
                if (chat.isOwner())
                    confirmationDalog(chat.getComment_id());
            } else
                showChatOption(chat);
        }
    }

    private void confirmationDalog(String id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
        builder.setTitle("Do you really want to delete this conversation?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            deleteComment(id);
            dialog.dismiss();
        });

        builder.setNegativeButton("No", (dialog, which) -> dialog.cancel());
        builder.setCancelable(false);
        AlertDialog alertDialog = builder.show();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);
    }

    private void activatePost() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("post_id", postData.getPost_id());
        jsonObject.addProperty("device_type", "android");
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        FamilheeyApplication application = FamilheeyApplication.get(this);
        ApiServices apiServices = RetrofitBase.createRxResource(this, ApiServices.class);
        subscriptions.add(apiServices.activatePost(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(response -> {
                }, throwable -> {
                }));
    }

    private void deactivatePost() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("post_id", postData.getPost_id());
        jsonObject.addProperty("device_type", "android");
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        FamilheeyApplication application = FamilheeyApplication.get(this);
        ApiServices apiServices = RetrofitBase.createRxResource(this, ApiServices.class);
        subscriptions.add(apiServices.deactivatePost(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(response -> {
                }, throwable -> {
                }));
    }

    private void muteBackgroundAudio() {
        AudioManager am = (AudioManager) ChatActivity.this.getSystemService(Context.AUDIO_SERVICE);
        if (am != null)
            am.setStreamMute(AudioManager.STREAM_MUSIC, true);
    }

    private void unMuteBackgroundAudio() {
        AudioManager am = (AudioManager) ChatActivity.this.getSystemService(Context.AUDIO_SERVICE);
        if (am != null)
            am.setStreamMute(AudioManager.STREAM_MUSIC, false);
    }

    private void showChatOption(ChatModel chat) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialogue_chat_option);
        Window window = dialog.getWindow();
        assert window != null;
        window.setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        TextView delete = dialog.findViewById(R.id.delete);
        TextView forward = dialog.findViewById(R.id.forward);
        TextView copy = dialog.findViewById(R.id.copy);
        TextView qoute = dialog.findViewById(R.id.qoute);

        TextView download = dialog.findViewById(R.id.download);
        if (chat.getType().contains("audio")|| chat.getType().contains("mp3")) {
            copy.setVisibility(View.GONE);
            qoute.setVisibility(View.GONE);
            forward.setVisibility(View.GONE);
            delete.setVisibility(View.GONE);
            download.setVisibility(View.VISIBLE);
        } else {
            if (chat.getChatText().isEmpty()) {
                copy.setVisibility(View.GONE);
                qoute.setVisibility(View.GONE);
            } else {
                copy.setVisibility(View.VISIBLE);
                qoute.setVisibility(View.VISIBLE);
            }
            if (chat.isOwner()) {
                delete.setVisibility(View.VISIBLE);
            }
        }
        copy.setOnClickListener(v -> {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("chat_text", chat.getChatText());
            assert clipboard != null;
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "copied to clipboard", Toast.LENGTH_SHORT).show();
            dialog.dismiss();

        });
        delete.setOnClickListener(v -> {
            dialog.dismiss();
            confirmationDalog(chat.getComment_id());
        });
        forward.setOnClickListener(v -> {
            startActivity(new Intent(ChatActivity.this, CreatePostActivity.class).putExtra("FROM", "CHAT").putExtra(DATA, new Gson().toJson(chat)));
            dialog.dismiss();
        });
        qoute.setOnClickListener(v -> {
            chatview.setVisibility(View.GONE);
            qoutechat.setVisibility(View.VISIBLE);
            txt_qoute.setText(chat.getChatText());
            edtxqouteMessage.setText(edtxMessage.getText());
            txt_qoute_user_name.setText(chat.getSenderName() + ", ");
            txt_qoute_date.setText(getFormattedDate(chat.getCreatedAt()));
            chatModel = chat;
            dialog.dismiss();
        });
        download.setOnClickListener(view -> {
            dialog.dismiss();
            Filename = chat.getFilename();
            audioDownload(Filename);
        });
        dialog.show();


    }

    private void audioDownload(String fname) {

        if (isReadStoragePermissionGranted1()) {

            String url = (Constants.ApiPaths.IMAGE_BASE_URL + "file_name/" + fname);
            Toast.makeText(this, "Downloading media...", Toast.LENGTH_SHORT).show();
            Utilities.downloadDocuments(this, url, fname);
        } else
            showPermission();

    }

    private String getFormattedDate(String createdAt) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        TimeZone tzInAmerica = TimeZone.getTimeZone("IST");
        dateFormatter.setTimeZone(tzInAmerica);
        try {
            Date date = dateFormatter.parse(createdAt);
            PrettyTime p = new PrettyTime();
            return p.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "Just now";
    }

    /********************AWS****************************************/
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


    public void uploadWithTransferUtility(File file, String key, String type) {

        progress.setVisibility(View.VISIBLE);
        ClientConfiguration configuration = new ClientConfiguration()
                .withMaxErrorRetry(2)
                .withConnectionTimeout(1200000)
                .withSocketTimeout(1200000);

        TransferUtility transferUtility =
                TransferUtility.builder()
                        .context(this)
                        .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                        .s3Client(new AmazonS3Client(AWSMobileClient.getInstance(), Region.getRegion(Regions.US_EAST_1), configuration))
                        .build();

        TransferObserver uploadObserver =
                transferUtility.upload(key
                        , file);

        // Attach a listener to the observer to get state update and progress notifications
        uploadObserver.setTransferListener(new TransferListener() {

            @Override
            public void onStateChanged(int id, TransferState state) {
                if (TransferState.COMPLETED == state) {
                    if (type.equals("VIDEO")) {
                        progress.setVisibility(View.GONE);
                        buttonSend.setVisibility(View.GONE);
                        btnRecord.setVisibility(View.VISIBLE);
                        imageAttach.setVisibility(View.VISIBLE);
                        edtxMessage.setEnabled(true);
                        addComment("", false);
                    } else if (type.equals("IMAGE")) {
                        compressNextFile();
                    }
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                float percentDonef = ((float) bytesCurrent / (float) bytesTotal) * 100;
                int percentDone = (int) percentDonef;

                if (type.equals("VIDEO") || type.equals("IMAGE")) {
                    progress.setProgress(percentDone);
                }
            }

            @Override
            public void onError(int id, Exception ex) {

            }
        });

    }

    private void compressNextFile() {
        if (mSelectedUri.size() > UPLOAD_FILE_POSITION + 1) {
            UPLOAD_FILE_POSITION += 1;
            try {
                new ImageCompressionAsyncTask(this).execute(readContentToFile(mSelectedUri.get(UPLOAD_FILE_POSITION)));
            } catch (Exception ignored) {
            }
        } else {
            progress.setVisibility(View.GONE);
            buttonSend.setVisibility(View.GONE);
            btnRecord.setVisibility(View.VISIBLE);
            imageAttach.setVisibility(View.VISIBLE);
            edtxMessage.setEnabled(true);
            addComment("", false);
        }
    }

    private boolean isReadWritePermissionGranted() {
        return TedPermission.isGranted(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void showPermission(int type) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialogue_permission);
        Window window = dialog.getWindow();
        assert window != null;
        window.setBackgroundDrawableResource(android.R.color.transparent);
        if (type == 4) {
            dialog.findViewById(R.id.txt_plus).setVisibility(View.VISIBLE);
            dialog.findViewById(R.id.img_mic).setVisibility(View.VISIBLE);
            TextView txt_decs = dialog.findViewById(R.id.txt_decs);
            txt_decs.setText("To record a Voice Message, allow Familheey access to your microphone and your device's photos,media, and files");
        }
        dialog.findViewById(R.id.btn_cancel).setOnClickListener(view -> dialog.dismiss());
        dialog.findViewById(R.id.btn_continue).setOnClickListener(view -> {
            dialog.dismiss();
            if (type == 0) {
                askForPermission(0);
            } else if (type == 1) {
                askForPermission(1);
            } else if (type == 3) {
                askForPermission(3);
            } else if (type == 4) {
                getPermissionToRecordAudio();
            } else
                askForPermission(type);

        });
        dialog.show();
    }

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

                for (int i = 0; i <= 85; i++) {
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
                uploadWithTransferUtility(s, "file_name/" + s.getName().replaceAll(" ", ""), "IMAGE");
                SocketCommentResponse.Attachment attachment = new SocketCommentResponse.Attachment();
                attachment.setFilename(s.getName().replaceAll(" ", ""));
                attachment.setType("image/png");
                attachments.add(attachment);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progress.setProgress(values[0]);
        }
    }

    private boolean isReadStoragePermissionGranted1() {
        return TedPermission.isGranted(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    private void requestPermission1() {
        TedPermission.with(this)
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        audioDownload(Filename);
                    }

                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {

                    }
                })
                .setDeniedMessage("If you reject permission,you can not download images\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();
    }

    private void showPermission() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialogue_permission);
        Window window = dialog.getWindow();
        assert window != null;
        window.setBackgroundDrawableResource(android.R.color.transparent);

        TextView txt_decs = dialog.findViewById(R.id.txt_decs);
        txt_decs.setText("To view images, allow Familheey access to your device's photos,media, and files.");
        dialog.findViewById(R.id.btn_cancel).setOnClickListener(view -> dialog.dismiss());
        dialog.findViewById(R.id.btn_continue).setOnClickListener(view -> {
            dialog.dismiss();
            requestPermission1();
        });
        dialog.show();
    }
}
