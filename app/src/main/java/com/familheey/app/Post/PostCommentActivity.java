package com.familheey.app.Post;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.LayerDrawable;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
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
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.bumptech.glide.Glide;
import com.devlomi.record_view.OnRecordListener;
import com.devlomi.record_view.RecordView;
import com.familheey.app.Activities.ShareEventActivity;
import com.familheey.app.Activities.SharelistActivity;
import com.familheey.app.Announcement.EditAnnouncementActivity;
import com.familheey.app.BuildConfig;
import com.familheey.app.FamilheeyApplication;
import com.familheey.app.Firebase.MessagePushDelegate;
import com.familheey.app.Fragments.Posts.FamilyPostFeedFragment;
import com.familheey.app.Fragments.Posts.MensionNameAdapter;
import com.familheey.app.Fragments.Posts.PostData;
import com.familheey.app.Fragments.Posts.PostSliderAdapter;
import com.familheey.app.Interfaces.PostCommentListener;
import com.familheey.app.Interfaces.RetrofitListener;
import com.familheey.app.Models.ApiCallbackParams;
import com.familheey.app.Models.ErrorData;
import com.familheey.app.Models.Request.RequstComment;
import com.familheey.app.Models.Response.GetCommentReplyResponse;
import com.familheey.app.Models.Response.GetCommentsResponse;
import com.familheey.app.Models.Response.PostComment;
import com.familheey.app.Models.Response.SocketCommentResponse;
import com.familheey.app.Need.NeedRequestDetailedActivity;
import com.familheey.app.Networking.Retrofit.ApiServiceProvider;
import com.familheey.app.Networking.Retrofit.ApiServices;
import com.familheey.app.Networking.Retrofit.RetrofitBase;
import com.familheey.app.Networking.utils.GsonUtils;
import com.familheey.app.R;
import com.familheey.app.SplashScreen;
import com.familheey.app.Topic.MainActivity;
import com.familheey.app.Utilities.Constants;
import com.familheey.app.Utilities.FileUtil;
import com.familheey.app.Utilities.FileUtils;
import com.familheey.app.Utilities.GifSizeFilter;
import com.familheey.app.Utilities.SharedPref;
import com.familheey.app.Utilities.Utilities;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.luseen.autolinklibrary.AutoLinkMode;
import com.luseen.autolinklibrary.AutoLinkTextView;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.vanniktech.emoji.EmojiPopup;
import com.volokh.danylo.hashtaghelper.HashTagHelper;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.filter.Filter;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
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

import butterknife.BindView;
import butterknife.ButterKnife;
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

//Activity
public class PostCommentActivity extends AppCompatActivity implements MessagePushDelegate, PostCommentAdapter.OnChatLongClickListener, PostCommentListener{
    private static final int RESULT_LOAD_VIDEO = 13;
    private static final int RESULT_DOC = 36;
    private final List<SocketCommentResponse.Attachment> attachments = new ArrayList<>();
    private static final int PICK_IMAGE = 23;
    private static final int REQUEST_CODE_PERMISSIONS = 12;
    private ImageView goBack;
    private ImageView btnqouteSend;
    private ImageView buttonSend;
    private ImageView imageAttach,qoute_imageCamera,qoute_imageAttach;
    private TextView toolBarTitle, txtImage, txtCancel, txtVideo, txtDoc, txt_qoute, txt_qoute_user_name, txt_qoute_date,txtAudio;
    private CardView cardBottomSheet;
    private ProgressBar progressBar, progress;
    private String postId;
    private PostData postData;
    private Socket mSocket;
    private RecyclerView recyclerView;
    private RelativeLayout chatview, qoutechat;
    private PostCommentAdapter chatAdapter;
    private final List<PostComment> chatModelList = new ArrayList<>();
    //private List<GetCommentsResponse.Data> chatModelList1 = new ArrayList<>();
    private EditText edtxqouteMessage;
    private com.devlomi.record_view.RecordButton btnRecord;
    List<Uri> mSelectedUri;
    private PostComment chatModel;
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
    public static final int REQUEST_CODE = 1002;

    private View rootView;
    private ImageView emojiButton,btn_qouteEmoji,btn_keyboard,btn_qouteKeyboard;
    private final String tempDirectoryName = "temp";
    private int recyclerPosition=0;
    private String postid;

    String description1 = "";
    private PostSliderAdapter adapter;
    @BindView(R.id.txt_temp)
    TextView txt_temp;
    @BindView(R.id.link_preview)
    LinearLayout link_preview;
    @BindView(R.id.scroll)
    ScrollView scroll;

    @BindView(R.id.url_img)
    ImageView url_img;

    @BindView(R.id.txt_url_des)
    TextView txt_url_des;
    @BindView(R.id.labelComment)
    TextView labelComment;

    @BindView(R.id.txt_url)
    TextView txt_url;

    @BindView(R.id.share_view)
    com.google.android.material.card.MaterialCardView share_view;
    @BindView(R.id.normal_view)
    com.google.android.material.card.MaterialCardView normal_view;


    @BindView(R.id.unread_status)
    View unread_status;
    @BindView(R.id.imgsha)
    ImageView imgsha;

    @BindView(R.id.share_txt_temp)
    TextView share_txt_temp;

    @BindView(R.id.txt_less_or_more)
    TextView txt_less_or_more;
    @BindView(R.id.postusername)
    TextView postusername;

    @BindView(R.id.txt_count)
    TextView txt_count;
    @BindView(R.id.img_con)
    ImageView img_con;
   /* @BindView(R.id.txt_des1)
    AutoLinkTextView txt_des1;*/
    @BindView(R.id.txt_des)
    TextView txt_des;
    @BindView(R.id.btn_more)
    ImageView btn_more;
    @BindView(R.id.imageSlider)
    com.smarteist.autoimageslider.SliderView sliderView;
    @BindView(R.id.view_count)
    TextView view_count;
    @BindView(R.id.imgviw)
    ImageView imgviw;

    @BindView(R.id.share_count)
    TextView share_count;


    @BindView(R.id.share_postusername)
    TextView share_postusername;

    @BindView(R.id.share_postedgroup)
    TextView share_postedgroup;

    @BindView(R.id.share_postdate)
    TextView share_postdate;

    @BindView(R.id.innerprofileImage)
    ImageView innerprofileImage;

    @BindView(R.id.innerpostedgroup)
    TextView innerpostedgroup;

    @BindView(R.id.innerpostusername)
    TextView innerpostusername;

    @BindView(R.id.share_imageSlider)
    com.smarteist.autoimageslider.SliderView share_imageSlider;

   /* @BindView(R.id.share_txt_des1)
    AutoLinkTextView share_txt_des1;
*/
    @BindView(R.id.share_txt_des)
    TextView share_txt_des;
    @BindView(R.id.share_txt_less_or_more)
    TextView share_txt_less_or_more;


    @BindView(R.id.share_txt_count)
    TextView share_txt_count;

    @BindView(R.id.share_share_count)
    TextView share_share_count;

    @BindView(R.id.share_view_count)
    TextView share_view_count;

    @BindView(R.id.share_unread_status)
    View share_unread_status;

    @BindView(R.id.share_imgsha)
    ImageView share_imgsha;

    @BindView(R.id.share_imgviw)
    ImageView share_imgviw;

    @BindView(R.id.share_img_con)
    ImageView share_img_con;

    @BindView(R.id.share_btn_share)
    ImageView share_btn_share;
    @BindView(R.id.shimmer_view_container)
    com.facebook.shimmer.ShimmerFrameLayout shimmer_view_container;

    @BindView(R.id.link_preview1)
    LinearLayout link_preview1;

    @BindView(R.id.url_img1)
    ImageView url_img1;

    @BindView(R.id.txt_url_des1)
    TextView txt_url_des1;

    @BindView(R.id.txt_url1)
    TextView txt_url1;


    @BindView(R.id.thanks_post_view)
    LinearLayout thanks_post_view;
    @BindView(R.id.thanks_post_img)
    ImageView thanks_post_img;
    @BindView(R.id.text_description)
    TextView text_description;
    @BindView(R.id.rv_name_list)
    RecyclerView rv_name_list;
    @BindView(R.id.txt_request_item)
    TextView txt_request_item;
    @BindView(R.id.btn_open_request)
    TextView btn_open_request;

    @BindView(R.id.imageCamera)
    ImageView imageCamera;

    private boolean isQuote=false;

    @BindView(R.id.rating_count)
    TextView rating_count;
    @BindView(R.id.rating_icon)
    ImageView rating_icon;
    @BindView(R.id.reviewers_count)
    TextView reviewers_count;
    private String user_id;
    private RatingBar ratingBar;
    private String starCount;
    private String rateCount;
    private LayerDrawable stars;
    private boolean isEmoji=true;
    private boolean isQouteEmoji=true;
    EmojiPopup popup;
    EmojiPopup qoutepopup;
    HashTagHelper hashTagHelper;
    //    private EmojIconActions emojIconActions;
    //SUBTYPE,

    private final Emitter.Listener onNewMessage = args -> PostCommentActivity.this.runOnUiThread(() -> {
        JSONArray data = (JSONArray) args[0];
        Type listType = new TypeToken<List<SocketCommentResponse>>() {
        }.getType();
        ArrayList<SocketCommentResponse> socketCommentResponseList = new Gson().fromJson(String.valueOf(data), listType);
        for (int i = 0; i < socketCommentResponseList.size(); i++) {
            String imagetype = "", image = "";
            int replyRemoveCount=0;
            if (socketCommentResponseList.get(0).getType().equals("delete_comment")) {
                for (int j = 0; j < chatModelList.size(); j++) {
                    if (chatModelList.get(j).getComment_id().equals(socketCommentResponseList.get(0).getDelete_id().get(0))) {
                        if(socketCommentResponseList.get(0).getDeleted_comments().get(0).getId()!=null && socketCommentResponseList.get(0).getDeleted_comments().get(0).getQuoted_id()==null) {
                            replyRemoveCount = chatModelList.get(j).getCommentReply().size();
                        }
                        chatModelList.remove(j);
                        chatAdapter.notifyDataSetChanged();
                        break;
                    }


                    for(int k=0;k<chatModelList.get(j).getCommentReply().size();k++){
                        String replyId= chatModelList.get(j).getCommentReply().get(k).getId().toString();
                        String socketId=socketCommentResponseList.get(0).getDelete_id().get(0);
                        if(replyId.equals(socketId)){
                            chatModelList.get(j).getCommentReply().remove(k);
                            int replyCount=Integer.parseInt(chatModelList.get(j).getReply_count())-1;
                            chatModelList.get(j).setReply_count(String.valueOf(replyCount));
                            chatAdapter.notifyDataSetChanged();
                            break;
                        }

                    }
                }
                int count=0;
                if(replyRemoveCount==0){
                    count= Integer.parseInt(postData.getConversation_count())-1 ;
                }else
                {
                    count= Integer.parseInt(postData.getConversation_count())-(replyRemoveCount+1) ;
                }

                if(postData.getShared_user_name() == null) {
                    postData.setConversation_count(String.valueOf(count));
                    txt_count.setText(postData.getConversation_count());
                }else{
                    postData.setConversation_count(String.valueOf(count));
                    share_txt_count.setText(postData.getConversation_count());
                }
            } else {
                if (socketCommentResponseList.get(0).getAttachmentList().size() > 0) {
                    image = socketCommentResponseList.get(0).getAttachmentList().get(0).getFilename();
                    imagetype = socketCommentResponseList.get(0).getAttachmentList().get(0).getType();
                }
                PostComment chatModel;
                if(socketCommentResponseList.get(i).getQuoted_id()==null) {
                    if (socketCommentResponseList.get(i).getCommented_by().equals(SharedPref.getUserRegistration().getId())) {
                        chatModel = new PostComment(true, socketCommentResponseList.get(i).getComment(), socketCommentResponseList.get(i).getFull_name(), image, IMAGE_BASE_URL + "propic/" + socketCommentResponseList.get(i).getPropic(), socketCommentResponseList.get(i).getCreatedAt(), imagetype, socketCommentResponseList.get(i).getComment_id(), Integer.parseInt(socketCommentResponseList.get(i).getCommented_by()), socketCommentResponseList.get(i).getQuoted_date(), socketCommentResponseList.get(i).getQuoted_id(), socketCommentResponseList.get(i).getQuoted_item(), socketCommentResponseList.get(i).getQuoted_user(), socketCommentResponseList.get(0).getAttachmentList(), ChildItemList(),"0");
                    } else {
                        chatModel = new PostComment(false, socketCommentResponseList.get(i).getComment(), socketCommentResponseList.get(i).getFull_name(), image, IMAGE_BASE_URL + "propic/" + socketCommentResponseList.get(i).getPropic(), socketCommentResponseList.get(i).getCreatedAt(), imagetype, socketCommentResponseList.get(i).getComment_id(), Integer.parseInt(socketCommentResponseList.get(i).getCommented_by()), socketCommentResponseList.get(i).getQuoted_date(), socketCommentResponseList.get(i).getQuoted_id(), socketCommentResponseList.get(i).getQuoted_item(), socketCommentResponseList.get(i).getQuoted_user(), socketCommentResponseList.get(0).getAttachmentList(), ChildItemList(),"0");
                    }
                    chatModelList.add(chatModel);
                    chatAdapter.notifyDataSetChanged();

                    scroll.fullScroll(ScrollView.FOCUS_DOWN);
                    recyclerView.scrollToPosition(chatModelList.size() - 1);
                    edtxMessage.setFocusableInTouchMode(true);
                    edtxMessage.requestFocus();

                    int count= Integer.parseInt(postData.getConversation_count())+1 ;
                    if(postData.getShared_user_name() == null){
                        postData.setConversation_count(String.valueOf(count));
                        txt_count.setText( postData.getConversation_count());
                    }else{
                        postData.setConversation_count(String.valueOf(count));
                        share_txt_count.setText( postData.getConversation_count());
                    }

                }else{
                    GetCommentReplyResponse replyModel = new GetCommentReplyResponse();
                    replyModel.setId(Integer.parseInt(socketCommentResponseList.get(i).getComment_id()));
                    replyModel.setRepliedUser(socketCommentResponseList.get(i).getFull_name());
                    replyModel.setComment(socketCommentResponseList.get(i).getComment());
                    replyModel.setProfilePic(socketCommentResponseList.get(i).getPropic());
                    replyModel.setUserId(socketCommentResponseList.get(i).getCommented_by());
                    replyModel.setCreatedAt(socketCommentResponseList.get(i).getCreatedAt());
                    replyModel.setAttachmentList(socketCommentResponseList.get(i).getAttachmentList());
                    int pos=0;
                    for(int n = 0; n < chatModelList.size(); n++) {
                        String quotedId=socketCommentResponseList.get(i).getQuoted_id();
                        String commentId=chatModelList.get(n).getComment_id();
                        if(quotedId.equals(commentId)){
                            pos=n;
                            break;
                        }
                    }

                    chatModelList.get(pos).getCommentReply().add(replyModel);
                    int replyCount=Integer.parseInt(chatModelList.get(pos).getReply_count())+1;
                    chatModelList.get(pos).setReply_count(String.valueOf(replyCount));
                    chatAdapter.notifyDataSetChanged();
                    if (socketCommentResponseList.get(i).getCommented_by().equals(SharedPref.getUserRegistration().getId())) {
                        scroll.fullScroll(ScrollView.FOCUS_DOWN);
                        recyclerView.scrollToPosition(pos);
                        edtxMessage.setFocusableInTouchMode(true);
                        edtxMessage.requestFocus();
                    }
                    int count= Integer.parseInt(postData.getConversation_count())+1 ;
                    if(postData.getShared_user_name() == null) {
                        postData.setConversation_count(String.valueOf(count));
                        txt_count.setText(postData.getConversation_count());
                    }else{
                        postData.setConversation_count(String.valueOf(count));
                        share_txt_count.setText(postData.getConversation_count());
                    }
                }
            }
        }
    });

    private List<GetCommentReplyResponse> ChildItemList()
    {
        List<GetCommentReplyResponse> ChildItemList = new ArrayList<>();
        return ChildItemList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inits();
        setContentView(R.layout.activity_post_comment);
        ButterKnife.bind(this);
        rootView=findViewById(R.id.root_view);
        emojiButton=findViewById(R.id.btn_emoji);
        btn_keyboard=findViewById(R.id.btn_keyboard);
        btn_qouteKeyboard=findViewById(R.id.btn_qouteKeyboard);
        btn_qouteEmoji=findViewById(R.id.btn_qouteEmoji);
        subscriptions = new CompositeDisposable();
        goBack = findViewById(R.id.goBack);
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
        qoute_imageCamera=findViewById(R.id.qoute_imageCamera);
        txtImage = findViewById(R.id.txtImage);
        txtCancel = findViewById(R.id.txtCancel);
        recordView = findViewById(R.id.record_view);
        btnRecord.setRecordView(recordView);
        recordView.setCancelBounds(8);
        chatview = findViewById(R.id.chatview);
        qoutechat = findViewById(R.id.qoutechat);
        qoute_imageAttach = findViewById(R.id.qoute_imageAttach);
        txt_qoute_user_name = findViewById(R.id.txt_qoute_user_name);
        edtxqouteMessage = findViewById(R.id.edtxqouteMessage);
        btnqouteSend = findViewById(R.id.btnqouteSend);

//        emojIconActions=new EmojIconActions(this,rootView,edtxMessage,emojiButton);
//        emojIconActions.ShowEmojIcon();

        popup = EmojiPopup.Builder
                .fromRootView(findViewById(R.id.root_view)).build(edtxMessage);
        qoutepopup = EmojiPopup.Builder
                .fromRootView(findViewById(R.id.root_view)).build(editQuoteMessage);

        txtAudio = findViewById(R.id.txtAudio);
        getIntentData();
        emojiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    popup.toggle();
                    emojiButton.setVisibility(View.GONE);
                    btn_keyboard.setVisibility(View.VISIBLE);
            }
        });
        btn_qouteEmoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                qoutepopup.toggle();
                btn_qouteEmoji.setVisibility(View.GONE);
                btn_qouteKeyboard.setVisibility(View.VISIBLE);
            }
        });

        btn_keyboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popup.toggle();
                emojiButton.setVisibility(View.VISIBLE);
                btn_keyboard.setVisibility(View.GONE);
            }
        });

        btn_qouteKeyboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                qoutepopup.toggle();
                btn_qouteEmoji.setVisibility(View.VISIBLE);
                btn_qouteKeyboard.setVisibility(View.GONE);
            }
        });
        findViewById(R.id.btn_qoute_view_close).setOnClickListener(View -> {
            chatview.setVisibility(android.view.View.VISIBLE);
            qoutechat.setVisibility(android.view.View.GONE);
            isQuote=false;
            edtxMessage.setText(edtxqouteMessage.getText());
            edtxqouteMessage.setText("");
            qoutepopup.dismiss();
        });
        // Modified By: Dinu(22/02/2021) For update visible_status="read" in firebase
        if (getIntent().hasExtra(NOTIFICATION_ID)) {
            notificationId=getIntent().getStringExtra(NOTIFICATION_ID);
            database= FirebaseDatabase.getInstance(FIREBASE_DATABASE_URL).getReference(Constants.ApiPaths.FIREBASE_USER_TYPE + SharedPref.getUserRegistration().getId() + "_notification");
            database.child(notificationId).child("visible_status").setValue("read");
        }
    }


    private void iniView() {

        initRecyclerView();
        initListeners();
        getPost( postData.getPost_id().toString());

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

                    PostComment chatModel;
                    if(data.get(i).getQuoted_id()==null) {
                        if (String.valueOf(data.get(i).getUserId()).equals(SharedPref.getUserRegistration().getId())) {
                            chatModel = new PostComment(true, data.get(i).getComment(), data.get(i).getFullName(), imageUrl, Constants.ApiPaths.IMAGE_BASE_URL + PROFILE_PIC + profPic, data.get(i).getCreatedAt(), type, data.get(i).getCommentId() + "", data.get(i).getUserId(), data.get(i).getQuoted_date(), data.get(i).getQuoted_id(), data.get(i).getQuoted_item(), data.get(i).getQuoted_user(), data.get(i).getAttachment(), ChildItemList(),data.get(i).getReply_count());
                        } else {
                            chatModel = new PostComment(false, data.get(i).getComment(), data.get(i).getFullName(), imageUrl, Constants.ApiPaths.IMAGE_BASE_URL + PROFILE_PIC + profPic, data.get(i).getCreatedAt(), type, data.get(i).getCommentId() + "", data.get(i).getUserId(), data.get(i).getQuoted_date(), data.get(i).getQuoted_id(), data.get(i).getQuoted_item(), data.get(i).getQuoted_user(), data.get(i).getAttachment(), ChildItemList(),data.get(i).getReply_count());
                        }

                        chatModelList.add(chatModel);
                    }
                }
                if (data.size() > 0) {
                    readStatus(data.get(data.size() - 1).getCommentId() + "");
                    chatAdapter.notifyDataSetChanged();
                }

                new Handler().postDelayed(() -> {
                    if(Type.equals("1")) {
                        hideProgress();
                        scroll.fullScroll(ScrollView.FOCUS_DOWN);
                        recyclerView.scrollToPosition(chatModelList.size() - 1);
                    }else{
                        hideProgress();
                    }
                }, 500);

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
        if (Type.equals("1")){
            edtxMessage.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }
        if ((PUSH).equalsIgnoreCase(Type) || ("NOTIFICATION").equals(Type) || ("HOME").equals(Type)|| ("STICKY").equals(Type)) {
            initRecyclerView();
            initListeners();

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
        chatAdapter = new PostCommentAdapter(chatModelList, PostCommentActivity.this,this );
        recyclerView.setLayoutManager(new LinearLayoutManager(PostCommentActivity.this));
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
        //Call click method

        imageCamera.setOnClickListener(view -> {
            ImagePicker.with(this)
                    .crop()
                    .cameraOnly()//Crop image(Optional), Check Customization for more option
                    .compress(1024)			//Final image size will be less than 1 MB(Optional)
                    .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                    .start();
        });
        qoute_imageCamera.setOnClickListener(view -> {
            ImagePicker.with(this)
                    .crop()
                    .cameraOnly()
                    .compress(1024)
                    .maxResultSize(1080, 1080)
                    .start();
        });
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
        imageAttach.setOnClickListener(view -> {
            showHideCard();
            /*
            * hide keyboard*/
             try {
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edtxMessage.getWindowToken(), 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        qoute_imageAttach.setOnClickListener(view -> {
            showHideCard();
             /*
            * hide keyboard*/
             try {
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edtxMessage.getWindowToken(), 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
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
                addComment(edtxMessage.getText().toString(), isQuote);
                edtxMessage.setText("");
            }
        });

        btnqouteSend.setOnClickListener(view -> {
            if (validatedQouteChat()) {
                addComment(edtxqouteMessage.getText().toString(), isQuote);
                chatview.setVisibility(View.VISIBLE);
                qoutechat.setVisibility(View.GONE);
                chatModel = null;
                edtxqouteMessage.setText("");
                edtxMessage.requestFocus();
                isQuote=false;
                qoutepopup.dismiss();
                emojiButton.setVisibility(View.VISIBLE);
                btn_keyboard.setVisibility(View.GONE);

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
                    Toast.makeText(PostCommentActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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

                        imageCamera.setVisibility(View.VISIBLE);
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
                imageCamera.setVisibility(View.VISIBLE);
                recordView.setVisibility(View.INVISIBLE);
            }
        });


        recordView.setOnBasketAnimationEndListener(() -> {
            imageAttach.setVisibility(View.VISIBLE);
            edtxMessage.setVisibility(View.VISIBLE);
            emojiButton.setVisibility(View.VISIBLE);
            imageCamera.setVisibility(View.VISIBLE);
            recordView.setVisibility(View.INVISIBLE);

        });
    }


    private boolean validatedChat() {
        if (edtxMessage.getText().toString().trim().length() == 0) {
            Toast.makeText(PostCommentActivity.this, "Enter some text.", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private boolean validatedQouteChat() {
        if (edtxqouteMessage.getText().toString().trim().length() == 0) {
            Toast.makeText(PostCommentActivity.this, "Enter some text.", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void showHideCard() {
        if(isQuote)
            txtAudio.setVisibility(View.GONE);
        else
            txtAudio.setVisibility(View.VISIBLE);
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
                        v -> ActivityCompat.requestPermissions(PostCommentActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                REQUEST_CODE_PERMISSIONS)).show();
            } else {
                /* Request for permission */
                ActivityCompat.requestPermissions(PostCommentActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE_PERMISSIONS);
            }

        } else {
            if (i == 0) {
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
                Toast.makeText(PostCommentActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
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
            imageCamera.setVisibility(View.INVISIBLE);
            emojiButton.setVisibility(View.INVISIBLE);
            MediaRecorderReady();
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (IllegalStateException | IOException e) {
            e.printStackTrace();
        }
    }

    private void goToImageGalleryintent() {
        Matisse.from(PostCommentActivity.this)
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
        Matisse.from(PostCommentActivity.this)
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

        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            PostData postData = new Gson().fromJson(data.getExtras().getString("data"), PostData.class);
            getPostAfterResult(postData.getPost_id().toString());

        }
        // For upload audio(Dinu)
        if (requestCode == ImagePicker.REQUEST_CODE && data != null) {

            Uri uri = data.getData();
            if(uri!=null) {
                mSelectedUri = new ArrayList<>();
                mSelectedUri.add(uri);
                imageAttach.setVisibility(View.GONE);
                imageCamera.setVisibility(View.GONE);
                buttonSend.setVisibility(View.GONE);
                btnRecord.setVisibility(View.GONE);

                if(isQuote) {
                    qoute_imageAttach.setVisibility(View.GONE);
                    qoute_imageCamera.setVisibility(View.GONE);
                    btnqouteSend.setVisibility(View.GONE);
                }
                File file = FileUtil.getFile(this, uri);
                try {
                    new ImageCompressionAsyncTask(this).execute(file);
                } catch (Exception e) {

                    /*need to handle

                     */
                }
            }
        }else if (resultCode == ImagePicker.RESULT_ERROR){
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
        }

        if (requestCode == RESULT_AUDIO ) {
            if (data != null && resultCode == RESULT_OK) {
                showHideCard();
                try {
                    Uri uri = data.getData();
                    mSelectedUri = new ArrayList<>();
                    mSelectedUri.add(uri);
                    uploadAudioToServer(MIME_TYPE_AUDIO);

                } catch (Exception e) {

                    e.printStackTrace();
                    Toast.makeText(this, "Oops something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
        }

        if (requestCode == PICK_IMAGE) {
            if (resultCode == Activity.RESULT_OK && null != data) {

                imageAttach.setVisibility(View.GONE);
                imageCamera.setVisibility(View.GONE);
                buttonSend.setVisibility(View.GONE);
                btnRecord.setVisibility(View.GONE);

                if(isQuote) {
                    qoute_imageAttach.setVisibility(View.GONE);
                    qoute_imageCamera.setVisibility(View.GONE);
                    btnqouteSend.setVisibility(View.GONE);
                }

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
                        imageCamera.setVisibility(View.GONE);
                        if(isQuote) {
                            qoute_imageAttach.setVisibility(View.GONE);
                            qoute_imageCamera.setVisibility(View.GONE);
                            btnqouteSend.setVisibility(View.GONE);
                        }
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
            if(isQuote) {
                qoute_imageAttach.setVisibility(View.GONE);
                qoute_imageCamera.setVisibility(View.GONE);
                btnqouteSend.setVisibility(View.GONE);
            }
            //   mSelectedUri.addAll(Objects.requireNonNull(data.getParcelableArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS)));
            if (isReadStoragePermissionGranted()) {
                if (isQuote && chatModel != null)
                    uploadQuoteDocumentToServer(MIME_TYPE_PDF);
                else
                    uploadDocumentToServer(MIME_TYPE_PDF);
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
//            if(isQuote==false){
//                fetchComments();
//            }
        }
        /**
         *for showing keyboard
         */
//            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        }


    @Override
    protected void onStop() {
//        /*if (postData != null && postData.getPost_id() != null) {
//            deactivatePost();
//        }*
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
                    public void onPermissionDenied(List<String> deniedPermissions) {

                    }
                })
                .setDeniedMessage("If you reject permission,you can not upload images\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();
    }

    void addComment(String comment, Boolean isQuote) {
        showProgress();
        RequstComment requstComment = new RequstComment();
        requstComment.setPost_id(postData.getPost_id() + "");
        requstComment.setComment(comment);
        requstComment.setCommented_by(SharedPref.getUserRegistration().getId());
        requstComment.setGroup_id(postData.getTo_group_id());
        requstComment.setTopic_id(postData.getPost_id() + "");
        if (PostCommentActivity.this.attachments.size() > 0) {
            requstComment.setAttachment(PostCommentActivity.this.attachments);
            requstComment.setFile_name(PostCommentActivity.this.attachments.get(0).getFilename());
            requstComment.setFile_type(PostCommentActivity.this.attachments.get(0).getType());
        }
        if (isQuote && chatModel != null) {
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
                PostCommentActivity.this.attachments.clear();
                hideProgress();
                if (postData != null && postData.getPost_id() != null) {
                    activatePost();
                }
                scroll.fullScroll(ScrollView.FOCUS_DOWN);
                edtxMessage.setFocusableInTouchMode(true);
                edtxMessage.requestFocus();
            }

            @Override
            public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
                hideProgress();
            }
        });
    }
    //Dinu-->Implemented new method for upload audio file
    private void uploadAudioToServer(String type) {
        showProgress();
        ApiServices service = RetrofitBase.createRxResource(this, ApiServices.class);
        List<MultipartBody.Part> parts = new ArrayList<>();
        try {
            for (int i = 0; i < mSelectedUri.size(); i++) {

                File file = AudioreadContentToFile(mSelectedUri.get(i));

                RequestBody requestFile = RequestBody.create( file, MediaType.parse( type ) );
                MultipartBody.Part filePart=  MultipartBody.Part.createFormData( "file_name", file.getName(), requestFile );
                  parts.add(filePart);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Oops something went wrong", Toast.LENGTH_SHORT).show();

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
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(edtxMessage.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (("NOTIFICATION").equals(Type) || ("HOME").equals(Type)|| ("STICKY").equals(Type)) {
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
                intent.putExtra("data", new Gson().toJson(postData));
                setResult(RESULT_OK, intent);
                finish();

                overridePendingTransition(R.anim.left,
                        R.anim.right);
            } else {
                Intent intent = new Intent();
                intent.putExtra(POSITION, position);
                intent.putExtra("isChat", false);
                intent.putExtra("data", new Gson().toJson(postData));
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

            ActivityCompat.requestPermissions(PostCommentActivity.this,
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
        //   shimmer_view_container.setVisibility(View.VISIBLE);
        postid=id;
        shimmer_view_container.startShimmer();
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
                    shimmer_view_container.setVisibility(View.GONE);
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        if (response.body().getData().size() > 0) {
                            postData = response.body().getData().get(0);
                            fetchComments();
                            this.runOnUiThread(this::initSocket);
                            if (postData.getRating_enabled() == null) {
                                postData.setRating_enabled(false);
                            }
                            if (postData.getShared_user_name() == null) {
                                normal_view.setVisibility(View.VISIBLE);
                                labelComment.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.VISIBLE);
                                normalViewDataSet(postData);
                            } else {
                                share_view.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.VISIBLE);
                                labelComment.setVisibility(View.VISIBLE);
                                share_view(postData);
                            }
                            addViewCount(postData.getPost_id() + "");
                        } else {
                            SweetAlertDialog contentNotFoundDialog = Utilities.getContentNotFoundDialog(this);
                            contentNotFoundDialog.setConfirmClickListener(sweetAlertDialog -> {
                                contentNotFoundDialog.cancel();
                                Intent intent = new Intent( this, MainActivity.class );
                                intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                                startActivity( intent );
                            });
                            contentNotFoundDialog.setCanceledOnTouchOutside(false);
                            contentNotFoundDialog.setCancelable(false);
                            contentNotFoundDialog.show();
                            Utilities.addPositiveButtonMargin(contentNotFoundDialog);
                        }
                    } else {
                        SweetAlertDialog contentNotFoundDialog = Utilities.getContentNotFoundDialog(this);
                        contentNotFoundDialog.setConfirmClickListener(sweetAlertDialog -> {
                            contentNotFoundDialog.cancel();
                            Intent intent = new Intent( this, MainActivity.class );
                            intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                            startActivity( intent );
                        });
                        contentNotFoundDialog.setCanceledOnTouchOutside(false);
                        contentNotFoundDialog.setCancelable(false);
                        contentNotFoundDialog.show();
                        Utilities.addPositiveButtonMargin(contentNotFoundDialog);
                    }
                }, throwable -> shimmer_view_container.setVisibility(View.GONE)));
    }

    @Override
    public void onChatLongClicked(int position) {

    }

    private void confirmationDalog(String id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(PostCommentActivity.this);
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
        AudioManager am = (AudioManager) PostCommentActivity.this.getSystemService(Context.AUDIO_SERVICE);
        if (am != null)
            am.setStreamMute(AudioManager.STREAM_MUSIC, true);
    }

    private void unMuteBackgroundAudio() {
        AudioManager am = (AudioManager) PostCommentActivity.this.getSystemService(Context.AUDIO_SERVICE);
        if (am != null)
            am.setStreamMute(AudioManager.STREAM_MUSIC, false);
    }

    private void showChatOption(PostComment chat,String replyId) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialogue_comment_option);
        Window window = dialog.getWindow();
        assert window != null;
        window.setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        TextView delete = dialog.findViewById(R.id.delete);
        TextView forward = dialog.findViewById(R.id.forward);
        TextView copy = dialog.findViewById(R.id.copy);
        TextView qoute = dialog.findViewById(R.id.reply);

        RelativeLayout copyView =dialog.findViewById(R.id.copyView);
        RelativeLayout replyView =dialog.findViewById(R.id.replyView);
        RelativeLayout forwardView =dialog.findViewById(R.id.forwardView);
        RelativeLayout downloadView =dialog.findViewById(R.id.downloadView);
        RelativeLayout deleteView =dialog.findViewById(R.id.deleteView);

        TextView download = dialog.findViewById(R.id.download);
        if(replyId=="") {
            if (chat.getType().contains("audio") || chat.getType().contains("mp3")) {
                copyView.setVisibility(View.GONE);
                copy.setVisibility(View.GONE);
                qoute.setVisibility(View.GONE);
                replyView.setVisibility(View.GONE);
                forward.setVisibility(View.GONE);
                forwardView.setVisibility(View.GONE);
                delete.setVisibility(View.GONE);
                deleteView.setVisibility(View.GONE);
                download.setVisibility(View.VISIBLE);
                downloadView.setVisibility(View.VISIBLE);
            } else {
                if (chat.getChatText().isEmpty()) {
                    copyView.setVisibility(View.GONE);
                    copy.setVisibility(View.GONE);
                    qoute.setVisibility(View.GONE);
                    replyView.setVisibility(View.GONE);
                } else {
                    copyView.setVisibility(View.VISIBLE);

                    copy.setVisibility(View.VISIBLE);
                    qoute.setVisibility(View.VISIBLE);
                    replyView.setVisibility(View.VISIBLE);
                }
                if (chat.isOwner()) {
                    deleteView.setVisibility(View.VISIBLE);
                    delete.setVisibility(View.VISIBLE);
                }
            }
        }else{
            copyView.setVisibility(View.GONE);
            copy.setVisibility(View.GONE);
            qoute.setVisibility(View.GONE);
            replyView.setVisibility(View.GONE);
            forward.setVisibility(View.GONE);
            forwardView.setVisibility(View.GONE);
            deleteView.setVisibility(View.VISIBLE);
            delete.setVisibility(View.VISIBLE);
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
            if(replyId=="")
                confirmationDalog(chat.getComment_id());
            else
                confirmationDalog(replyId);
        });
        forward.setOnClickListener(v -> {
            startActivity(new Intent(PostCommentActivity.this, CreatePostActivity.class).putExtra("FROM", "CHAT").putExtra(DATA, new Gson().toJson(chat)));
            dialog.dismiss();
        });
        qoute.setOnClickListener(v -> {
            chatview.setVisibility(View.GONE);
            qoutechat.setVisibility(View.VISIBLE);
            txt_qoute_user_name.setText("Replying to "+chat.getSenderName());
            edtxqouteMessage.requestFocus();
            isQuote=true;
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
        final File file = new File(getCacheDir(), timeStamp+"."+getfileExtension(uri));

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
                        imageCamera.setVisibility(View.VISIBLE);

                        if(isQuote) {
                            qoute_imageAttach.setVisibility(View.VISIBLE);
                            qoute_imageCamera.setVisibility(View.VISIBLE);
                            btnqouteSend.setVisibility(View.VISIBLE);
                        }
                        edtxMessage.setEnabled(true);
                        if(isQuote==false) {
                            if (edtxMessage.getText().toString().length() == 0) {
                                buttonSend.setVisibility(View.GONE);
                                btnRecord.setVisibility(View.VISIBLE);
                            } else {
                                buttonSend.setVisibility(View.VISIBLE);
                                btnRecord.setVisibility(View.GONE);
                            }
                            popup.dismiss();
                            btn_keyboard.setVisibility(View.GONE);
                            emojiButton.setVisibility(View.VISIBLE);
                        }
                        addComment("", isQuote);
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
            imageCamera.setVisibility(View.VISIBLE);
            if(isQuote) {
                qoute_imageAttach.setVisibility(View.VISIBLE);
                qoute_imageCamera.setVisibility(View.VISIBLE);
                btnqouteSend.setVisibility(View.VISIBLE);
            }
            edtxMessage.setEnabled(true);
            if(isQuote==false) {
                if (edtxMessage.getText().toString().length() == 0) {
                    buttonSend.setVisibility(View.GONE);
                    btnRecord.setVisibility(View.VISIBLE);
                } else {
                    buttonSend.setVisibility(View.VISIBLE);
                    btnRecord.setVisibility(View.GONE);
                }
                popup.dismiss();
                btn_keyboard.setVisibility(View.GONE);
                emojiButton.setVisibility(View.VISIBLE);
            }else{
                qoutepopup.dismiss();
                btn_qouteKeyboard.setVisibility(View.GONE);
                btn_qouteEmoji.setVisibility(View.VISIBLE);
            }
            addComment("", isQuote);
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

    @Override
    public void longClickListener(int position) {
        chatview.setVisibility(android.view.View.VISIBLE);
        qoutechat.setVisibility(android.view.View.GONE);
        isQuote=false;
        edtxMessage.setText(edtxqouteMessage.getText());
        edtxqouteMessage.setText("");
        if (chatModelList.size() > position) {
            recyclerPosition=position;
            PostComment chat = chatModelList.get(position);
            chatModel = chatModelList.get(position);
            if (chat.getType().contains("audio")) {
                if (chat.isOwner())
                    confirmationDalog(chat.getComment_id());
                else
                    showChatOption(chat,"");
            } else if (chat.getFilename().toLowerCase().contains("mp4") || chat.getFilename().toLowerCase().contains("mov") || chat.getFilename().toLowerCase().contains("wmv") || chat.getFilename().toLowerCase().contains("webm") || chat.getFilename().toLowerCase().contains("mkv") || chat.getFilename().toLowerCase().contains("flv") || chat.getFilename().toLowerCase().contains("avi")) {
                if (chat.isOwner())
                    confirmationDalog(chat.getComment_id());
            } else
                showChatOption(chat,"");
        }
    }



    @Override
    public void commentReplyClickListener(int position, int currentPosition) {
        qoutechat.setVisibility(View.VISIBLE);
        isQuote=true;
        recyclerPosition=position;
        chatModel = chatModelList.get(position);
        txt_qoute_user_name.setText("Replying to "+chatModel.getSenderName());
        edtxqouteMessage.requestFocus();
        popup.dismiss();
        btn_qouteKeyboard.setVisibility(View.GONE);
        btn_qouteEmoji.setVisibility(View.VISIBLE);
        fetchCommentsReply(position);
    }

    @Override
    public void onReplyLongClickListener(int childPosition, int parentPosition) {
        if (chatModelList.size() > parentPosition) {
            PostComment chat = chatModelList.get(parentPosition);
            String replyId=chat.getCommentReply().get(childPosition).getId().toString();
            if(chat.getCommentReply().get(childPosition).getUserId().equals(SharedPref.getUserRegistration().getId()))
                showChatOption(chat,replyId);
        }
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

    private void showMenusShare(View v, PostData postData) {
        PopupMenu popup = new PopupMenu(v.getContext(), v);
        popup.getMenuInflater().inflate(R.menu.popup_menu_post_share, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.sharefamily:
                    startActivity(new Intent(this, ShareEventActivity.class).putExtra(Constants.Bundle.TYPE, "POST").putExtra("Post_id", postData.getPost_id() + ""));
                    break;
                case R.id.sharesocial:
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, Constants.ApiPaths.SHARE_URL + "page/posts/" + postData.getPost_id());
                    startActivity(Intent.createChooser(intent, "Share"));
                    break;
            }
            return true;
        });

        popup.show();
    }

    private void normalViewDataSet(PostData postData) {

        postusername.setText(postData.getCreated_user_name());

        if (postData.getConversation_enabled()) {
            txt_count.setText(postData.getConversation_count());
            img_con.setVisibility(View.VISIBLE);
            txt_count.setVisibility(View.VISIBLE);
        } else {
            img_con.setVisibility(View.GONE);
            txt_count.setVisibility(View.GONE);
        }
        if (postData.getValid_urls() != null && postData.getValid_urls().size() == 1 && postData.getPost_attachment() == null || postData.getPost_attachment().size() == 0) {
            if (postData.getUrl_metadata() != null) {
                if (postData.getUrl_metadata().getUrlMetadataResult() != null) {
                    link_preview.setVisibility(View.VISIBLE);
                    thanks_post_view.setVisibility(View.GONE);
                    sliderView.setVisibility(View.GONE);
                    String t = postData.getUrl_metadata().getUrlMetadataResult().getTitle();
                    String d = postData.getUrl_metadata().getUrlMetadataResult().getDescription();
                    if (t != null && !t.isEmpty()) {
                        txt_url_des.setText(t);
                    } else if (d != null && !d.isEmpty()) {
                        txt_url_des.setText(t);
                    }
                    txt_url.setText(postData.getUrl_metadata().getUrlMetadataResult().getUrl());

                    String url = postData.getUrl_metadata().getUrlMetadataResult().getImage() + "";
                    if (!url.contains("http")) {
                        url = postData.getUrl_metadata().getUrlMetadataResult().getUrl() + url;
                    }
                    if (!url.isEmpty() && url.length() > 5) {
                        url_img.setScaleType(ImageView.ScaleType.CENTER);
                        Glide.with(this)
                                .load(url)
                                .placeholder(R.drawable.d_image)
                                .apply(Utilities.getCurvedRequestOptions()).into(url_img);
                    } else {
                        url_img.setScaleType(ImageView.ScaleType.FIT_XY);
                    }
                    link_preview.setOnClickListener(view -> {
                        try {
                            String urls = postData.getValid_urls().get(0).trim();
                            if (!urls.contains("http")) {
                                urls = urls.replaceAll("www.", "http://www.");
                            }
                            if (urls.contains("blog.familheey.com")) {
                                // Dinu(09/11/2021) for open another app
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urls));
                                startActivity(intent);
                            }
                            else if (urls.contains("familheey")) {
                                openAppGetParams(urls);
                            } else {
                                // Dinu(15/07/2021) for open another app
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urls));
                                startActivity(intent);
                                // startActivity(new Intent(this, BrowserActivity.class).putExtra("URL", urls));
                            }
                        } catch (Exception e) {
                            /*
                            Not needed
                             */
                        }
                    });

                }
            }
        } else if (postData.getPublish_type() != null && "request".equals(postData.getPublish_type())) {
            link_preview.setVisibility(View.GONE);
            sliderView.setVisibility(View.GONE);
            txt_less_or_more.setVisibility(View.GONE);
            txt_des.setVisibility(View.GONE);
            thanks_post_view.setVisibility(View.VISIBLE);
            text_description.setText(postData.getSnap_description());

            if (postData.getPublish_mention_items() != null && postData.getPublish_mention_items().size() > 0)
                txt_request_item.setText(postData.getPublish_mention_items().get(0).getItem_name());
            Glide.with(this)
                    .load(BuildConfig.IMAGE_BASE_URL + "post/" + postData.getPost_attachment().get(0).getFilename())
                    .into(thanks_post_img);
            if (postData.getPublish_mention_users() != null) {
                rv_name_list.setLayoutManager(new GridLayoutManager(this, 5));
                rv_name_list.setAdapter(new MensionNameAdapter(postData.getPublish_mention_users(), this));
            }

        } else {

            thanks_post_view.setVisibility(View.GONE);
            link_preview.setVisibility(View.GONE);
            sliderView.setVisibility(View.GONE);
            if (postData.getPost_attachment() != null && postData.getPost_attachment().size() > 0) {
                sliderView.setVisibility(View.VISIBLE);
                if (postData.getPost_attachment().get(0).getType().contains("image")) {
                    if (postData.getPost_attachment().get(0).getWidth() != null && postData.getPost_attachment().get(0).getHeight() != null) {
                        ViewGroup.LayoutParams params = sliderView.getLayoutParams();
                     if(postData.getPost_attachment().get(0).getWidth().equals("700") && postData.getPost_attachment().get(0).getHeight().equals("100")) {

                         postData.getPost_attachment().get(0).setWidth("1000");
                         postData.getPost_attachment().get(0).setHeight("1200");

                     }

                        params.height = getwidgetsize(postData.getPost_attachment().get(0).getWidth(), postData.getPost_attachment().get(0).getHeight());
                        sliderView.setLayoutParams(params);
                    } else {
                        ViewGroup.LayoutParams params = sliderView.getLayoutParams();
                        params.height = getwidgetsize();
                        sliderView.setLayoutParams(params);
                    }
                } else if (postData.getPost_attachment().get(0).getType().contains("video")) {

                    ViewGroup.LayoutParams params = sliderView.getLayoutParams();
                    params.height = 850;
                    sliderView.setLayoutParams(params);
                } else {

                    ViewGroup.LayoutParams params = sliderView.getLayoutParams();
                    params.height = 600;
                    sliderView.setLayoutParams(params);
                }


                adapter = new PostSliderAdapter(this, postData);
                sliderView.setSliderAdapter(adapter);
                sliderView.setIndicatorAnimation(IndicatorAnimations.THIN_WORM);
                sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
            }
        }


        String id = postData.getCreated_by() + "";
        share_count.setText(postData.getShared_user_count());
/*
        txt_des.addAutoLinkMode(
                AutoLinkMode.MODE_HASHTAG,
                AutoLinkMode.MODE_URL);
        txt_des.setHashtagModeColor(ContextCompat.getColor(this, R.color.buttoncolor));
        txt_des.setUrlModeColor(ContextCompat.getColor(this, R.color.buttoncolor));*/
        txt_temp.setText(postData.getSnap_description());


        if (postData.getConversation_enabled() && Integer.parseInt(postData.getConversation_count_new()) > 0) {
            unread_status.setVisibility(View.VISIBLE);
        } else {
            unread_status.setVisibility(View.GONE);
        }

        if (id.equals(SharedPref.getUserRegistration().getId()) && !"request".equals(postData.getPublish_type())) {
            view_count.setText(postData.getViews_count());
            imgviw.setVisibility(View.VISIBLE);
            view_count.setVisibility(View.VISIBLE);
            imgsha .setVisibility(View.VISIBLE);
            share_count.setVisibility(View.VISIBLE);
        } else {
            imgviw.setVisibility(View.GONE);
            view_count.setVisibility(View.GONE);
            imgsha.setVisibility(View.GONE);
            share_count.setVisibility(View.GONE);
        }

        if (postData.getRating_enabled()) {
            rating_count.setVisibility(View.VISIBLE);
            rating_count.setText(postData.getRating());
            reviewers_count.setVisibility(View.VISIBLE);
            reviewers_count.setText("["+postData.getRating_count()+" Rating"+"]");
            rating_icon.setOnClickListener(v -> {

                View popupView = LayoutInflater.from(this).inflate(R.layout.popup_rating_bar, null);
                final PopupWindow popupWindow = new PopupWindow(popupView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
                popupWindow.showAsDropDown(rating_icon,500,0);
                popupWindow.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.new_background_rounded));
                popupWindow.setOutsideTouchable(true);

                View container = popupWindow.getContentView().getRootView();
                if (container != null) {
                    WindowManager wm = (WindowManager) this.getSystemService(WINDOW_SERVICE);
                    WindowManager.LayoutParams p = (WindowManager.LayoutParams) container.getLayoutParams();
                    p.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                    p.dimAmount = 0.1f;
                    if (wm != null) {
                        wm.updateViewLayout(container, p);
                    }
                }
                ratingBar = (RatingBar) popupView.findViewById(R.id.ratingBar);
                stars = (LayerDrawable) ratingBar.getProgressDrawable();
                ratingBar.setIsIndicator(false);

                String rating_by_user=postData.getRating_by_user();
                if (rating_by_user==null) {
                    String newrate = String.valueOf(ratingBar.getRating());
                    ratingBar.setRating(Float.parseFloat(newrate));

                }else{
                    switch (postData.getRating_by_user()) {
                        case "0":
                            ratingBar.setRating(0);
                            break;
                        case "1":
                            ratingBar.setRating(1);
                            break;
                        case "2":
                            ratingBar.setRating(2);
                            break;
                        case "3":
                            ratingBar.setRating(3);
                            break;
                        case "4":
                            ratingBar.setRating(4);
                            break;
                        case "5":
                            ratingBar.setRating(5);
                            break;
                        default:
                            ratingBar.setRating(Float.parseFloat(starCount));
                            break;
                    }
                }

                ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser){
                        JsonObject jsonObject=new JsonObject();
                        user_id=SharedPref.getUserRegistration().getId();
                        starCount = String.valueOf(ratingBar.getRating());
                        jsonObject.addProperty("user_id",user_id);
                        jsonObject.addProperty("post_id",postData.getPost_id().toString());
                        jsonObject.addProperty("type","rating");
                        jsonObject.addProperty("rating",starCount);

                        if (ratingBar.getRating()==0){
                            jsonObject.addProperty("rating","0");
                        }else if (ratingBar.getRating() == 1) {
                            jsonObject.addProperty("rating", "1");
                        } else if (ratingBar.getRating() == 2) {
                            jsonObject.addProperty("rating", "2");
                        } else if (ratingBar.getRating() == 3) {
                            jsonObject.addProperty("rating", "3");
                        } else if (ratingBar.getRating() == 4) {
                            jsonObject.addProperty("rating", "4");
                        } else if (ratingBar.getRating() == 5) {
                            jsonObject.addProperty("rating", "5");
                        }
                        onItemRating(jsonObject,String.valueOf((int)ratingBar.getRating()) );
                        /*(04/09/21)->for popup delay*/
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                popupWindow.dismiss();
                            }

                        }, 2000);

                    }

                });
            });

        }else{
            rating_icon.setVisibility(View.GONE);
            rating_count.setVisibility(View.GONE);
            reviewers_count.setVisibility(View.GONE);
        }
        if (postData.getRating_enabled()){
            rating_count.setText(postData.getRating());
            reviewers_count.setText("["+postData.getRating_count()+" Rating"+"]");
            if(postData.getRating_by_user()==null||postData.getRating_by_user().equals("0")) {
                rating_icon.setVisibility(View.VISIBLE);
                rating_icon.setImageResource(R.drawable.images);
            }
            else{
                rating_icon.setVisibility(View.VISIBLE);
                rating_icon.setImageResource(R.drawable.coloured_star);
            }
            rating_count.setVisibility(View.VISIBLE);
            reviewers_count.setVisibility(View.VISIBLE);
        }else{
            rating_icon.setVisibility(View.GONE);
            rating_count.setVisibility(View.GONE);
            reviewers_count.setVisibility(View.GONE);
        }



        txt_temp.post(() -> {
            String description = postData.getSnap_description().trim();
            description = description.replaceAll("HTTP:", "http:").replaceAll("Http:", "http:")
                    .replaceAll("Https:", "https:")
                    .replaceAll("HTTPS:", "https:");
            description = description.replaceAll("WWW.", "www.").replaceAll("Www.", "www.");
            if (txt_temp.getLineCount() > 2) {
                txt_less_or_more.setVisibility(View.VISIBLE);
                txt_des.setMaxLines(3);
                txt_des.setEllipsize(TextUtils.TruncateAt.END);
            } else {
                txt_less_or_more.setVisibility(View.GONE);
            }
            /**@author Devika on 07-12-2021
             * to handle links inside posts**/
            txt_des.setText(description);
            if(txt_des!=null){
                hashTagHelper = HashTagHelper.Creator.create(ContextCompat.getColor(this,R.color.buttoncolor),null);
                hashTagHelper.handle(txt_des);
                Linkify.addLinks(txt_des, Linkify.ALL);
                txt_des.setLinkTextColor(ContextCompat.getColor(this,R.color.buttoncolor));
            }
        });
/*        txt_des.setAutoLinkOnClickListener((autoLinkMode, matchedText) -> {

            if (autoLinkMode == AutoLinkMode.MODE_URL) {
                try {
                    String url = matchedText.trim();
                    if (!url.contains("http")) {
                        url = url.replaceAll("www.", "http://www.");
                    }
                    if (url.contains("blog.familheey.com")) {
                        // Dinu(09/11/2021) for open another app
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                    }
                    else if (url.contains("familheey")) {
                        openAppGetParams(url);
                    } else {
                        // Dinu(15/07/2021) for open another app
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                        //  startActivity(new Intent(this, BrowserActivity.class).putExtra("URL", url));
                    }
                } catch (Exception e) {
                  *//*
                  Not needed
                   *//*
                }
            }

        });*/


        txt_less_or_more.setOnClickListener(v -> {
            String description = postData.getSnap_description().trim();
            description = description.replaceAll("HTTP:", "http:").replaceAll("Http:", "http:")
                    .replaceAll("Https:", "https:")
                    .replaceAll("HTTPS:", "https:");
            description = description.replaceAll("WWW.", "www.").replaceAll("Www.", "www.");
            if (txt_less_or_more.getText().equals("More")) {
                txt_less_or_more.setText("Less");
                /**@author Devika on 07-12-2021
                 * to handle links inside posts**/
                txt_des.setText(description);
                if(txt_des!=null){
                    hashTagHelper = HashTagHelper.Creator.create(ContextCompat.getColor(this,R.color.buttoncolor),null);
                    hashTagHelper.handle(txt_des);
                    Linkify.addLinks(txt_des, Linkify.ALL);
                    txt_des.setLinkTextColor(ContextCompat.getColor(this,R.color.buttoncolor));
                }
                txt_des.setMaxLines(Integer.MAX_VALUE);
                txt_des.setEllipsize(null);
            } else {
                txt_less_or_more.setText("More");
                txt_des.setMaxLines(3);
                txt_des.setEllipsize(TextUtils.TruncateAt.END);
            }
        });

        share_btn_share.setOnClickListener(v -> showMenusShare(v, postData));


        imgsha.setOnClickListener(v -> {
            if (Integer.parseInt(postData.getShared_user_count()) > 0) {
                startActivity(new Intent(this, SharelistActivity.class)
                        .putExtra(Constants.Bundle.TYPE, "POST")
                        .putExtra("event_id", postData.getOrgin_id() + "")
                        .putExtra("user_id", postData.getCreated_by() + ""));
            }
        });

        imgviw.setOnClickListener(v -> {
            if (Integer.parseInt(postData.getViews_count()) > 0) {
                startActivity(new Intent(this, SharelistActivity.class)
                        .putExtra(Constants.Bundle.TYPE, "POSTVIEW")
                        .putExtra("event_id", postData.getPost_id() + "")
                        .putExtra("user_id", postData.getCreated_by() + ""));
            }
        });

        btn_more.setOnClickListener(v ->

                {
                    if (id.equals(SharedPref.getUserRegistration().getId())) {
                        showMenusPostOwner(v, postData);
                    } else {
                        showMenusNormalUser(v, postData);
                    }
                }

        );
        btn_open_request.setOnClickListener(view -> {
            Intent requestDetailedIntent = new Intent(this, NeedRequestDetailedActivity.class);
            requestDetailedIntent.putExtra(DATA, postData.getPublish_id());
            startActivity(requestDetailedIntent);
        });

        /**04/04/2022**/
        // for update read status
        if(postData.getNotification_key()!=null) {
            database = FirebaseDatabase.getInstance(FIREBASE_DATABASE_URL).getReference(Constants.ApiPaths.FIREBASE_USER_TYPE + SharedPref.getUserRegistration().getId() + "_notification");
            database.child(postData.getNotification_key()).child("visible_status").setValue("read");
        }
    }
    private void openAppGetParams(String url) {
        // UserNotification
        SweetAlertDialog progressDialog = Utilities.getProgressDialog(this);
        progressDialog.show();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("url", url);
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        FamilheeyApplication application = FamilheeyApplication.get(this);
        ApiServices apiServices = RetrofitBase.createRxResource(this, ApiServices.class);
        subscriptions.add(apiServices.openAppGetParams(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(response -> {
                    progressDialog.dismiss();
                    if(response.body() != null) {
                        if (response.body().getData().getSub_type().equals("family_link")) {
                            response.body().getData().setSub_type("");
                        }
                        response.body().getData().goToCorrespondingDashboard(this);
                    }else{
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                    }
                }, throwable -> progressDialog.dismiss()));
    }

    private void showMenusPostOwnerShare(View v, PostData postData) {
        PopupMenu popup = new PopupMenu(v.getContext(), v);
        popup.getMenuInflater().inflate(R.menu.popup_menu_post_owner, popup.getMenu());
        if (postData.getMuted() != null && postData.getMuted()) {
            popup.getMenu().getItem(0).setTitle("UnMute");
        } else {
            popup.getMenu().getItem(0).setTitle("Mute");
        }
        popup.getMenu().getItem(1).setVisible(false);
popup.getMenu().getItem(3).setVisible(false);
        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.mutePost:
                    muteConversation(postData);
                    break;
                case R.id.editpost:

                    if(postData.getType().equals("announcement")) {
                        Intent editIntent = new Intent(this, EditAnnouncementActivity.class);
                        editIntent.putExtra("pos", 0);
                        editIntent.putExtra("POST", new Gson().toJson(postData));
                        startActivityForResult(editIntent, REQUEST_CODE);
                    }else{
                        Intent editIntent = new Intent(this, EditPostActivity.class);
                        editIntent.putExtra("pos", 0);
                        editIntent.putExtra("POST", new Gson().toJson(postData));
                        startActivityForResult(editIntent, REQUEST_CODE);
                    }
                    break;
                case R.id.delete:
                    confirmation(postData);
                    break;

                case R.id.share:
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_SEND);

                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, Constants.ApiPaths.BASE_URL);
                    startActivity(Intent.createChooser(intent, "Share"));
                    break;
            }
            return true;
        });

        popup.show();
    }

    private void showMenusNormalUser(View v, PostData postData) {
        PopupMenu popup = new PopupMenu(v.getContext(), v);
        popup.getMenuInflater().inflate(R.menu.popup_menu_post_user, popup.getMenu());
        if (postData.getMuted() != null && postData.getMuted()) {
            popup.getMenu().getItem(0).setTitle("UnMute");
        } else {
            popup.getMenu().getItem(0).setTitle("Mute");
        }
        popup.getMenu().getItem(3).setVisible(false);
        popup.getMenu().getItem(4).setVisible(false);

        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.mutePost:
                    muteConversation(postData);
                    break;
                case R.id.removepost:
                    removePost(postData.getPost_id() + "");
                    break;
                case R.id.report:
                    Dialoguereport(postData.getPost_id() + "");
                    break;

                case R.id.delete:
                    // confirmation(postData);
                    break;

                case R.id.share:
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_SEND);

                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, Constants.ApiPaths.BASE_URL);
                    startActivity(Intent.createChooser(intent, "Share"));
                    break;
            }
            return true;
        });

        popup.show();
    }

    private void showMenusPostOwner(View v, PostData postData) {
        PopupMenu popup = new PopupMenu(v.getContext(), v);
        popup.getMenuInflater().inflate(R.menu.popup_menu_post_owner, popup.getMenu());
        if (postData.getMuted() != null && postData.getMuted()) {
            popup.getMenu().getItem(0).setTitle("UnMute");
        } else {
            popup.getMenu().getItem(0).setTitle("Mute");
        }
popup.getMenu().getItem(3).setVisible(false);

        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.mutePost:
                    popup.dismiss();
                    muteConversation(postData);
                    break;
                case R.id.editpost:
                    if(postData.getType().equals("announcement")) {
                        Intent editIntent = new Intent(this, EditAnnouncementActivity.class);
                        editIntent.putExtra("pos", 0);
                        editIntent.putExtra("POST", new Gson().toJson(postData));
                        startActivityForResult(editIntent, REQUEST_CODE);
                    }else{
                        Intent editIntent = new Intent(this, EditPostActivity.class);
                        editIntent.putExtra("pos", 0);
                        editIntent.putExtra("POST", new Gson().toJson(postData));
                        startActivityForResult(editIntent, REQUEST_CODE);
                    }

                    break;
                case R.id.delete:
                    confirmation(postData);
                    break;

                case R.id.share:
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_SEND);

                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, Constants.ApiPaths.BASE_URL);
                    startActivity(Intent.createChooser(intent, "Share"));
                    break;
            }
            return true;
        });

        popup.show();
    }
    private void onItemRating(JsonObject jsonObject,String rating) {
        SweetAlertDialog progressDialog = Utilities.getProgressDialog(this);
        progressDialog.show();
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        FamilheeyApplication application = FamilheeyApplication.get(this);
        ApiServices apiServices = RetrofitBase.createRxResource(this, ApiServices.class);
        subscriptions.add(apiServices.rate(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(response -> {
                    JSONObject json= (JSONObject) new JSONTokener(response.body().string()).nextValue();
                    JSONObject json2 = json.getJSONObject("data");
                    rateCount = (String) json2.get("total_rating");

                    String reviewers=(String)json2.get("rating_count");
                    postData.setRating_by_user(rating);
                    postData.setRating(rateCount);
                    postData.setRating_count(reviewers);
                    rating_count.setText(rateCount);
                    reviewers_count.setText("["+reviewers+" Rating"+"]");
                    if(starCount.equals("0")||starCount.equals("0.0")) {
                        rating_icon.setVisibility(View.VISIBLE);
                        rating_icon.setImageResource(R.drawable.images);
                    }else{
                        rating_icon.setVisibility(View.VISIBLE);
                        rating_icon.setImageResource(R.drawable.coloured_star);
                    }
                    progressDialog.hide();
                },throwable -> {
                    progressDialog.hide();
                }));
    }
    private void muteConversation(PostData postData) {
        SweetAlertDialog progressDialog = Utilities.getProgressDialog(this);
        progressDialog.show();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        jsonObject.addProperty("post_id", postData.getPost_id() + "");
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        FamilheeyApplication application = FamilheeyApplication.get(this);
        ApiServices apiServices = RetrofitBase.createRxResource(this, ApiServices.class);
        subscriptions.add(apiServices.muteConversation(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(response -> {
                    progressDialog.dismiss();
                    assert response.body() != null;
                    postData.setMuted(response.body().getData().getIs_active());
                }, throwable -> progressDialog.dismiss()));
    }

    private void removePost(String post_id) {
        SweetAlertDialog progressDialog = Utilities.getProgressDialog(this);
        progressDialog.show();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        jsonObject.addProperty("post_id", post_id);
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        FamilheeyApplication application = FamilheeyApplication.get(this);
        ApiServices apiServices = RetrofitBase.createRxResource(this, ApiServices.class);
        subscriptions.add(apiServices.removePost(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(response -> progressDialog.dismiss(), throwable -> progressDialog.dismiss()));
    }

    private void confirmation(PostData postData) {

        SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setContentText("Are you sure you want to delete this post?")
                .setConfirmText("Yes")
                .setCancelText("No");

        pDialog.show();
        pDialog.setConfirmClickListener(sDialog -> {
            deletePost(postData.getPost_id() + "");
            pDialog.cancel();
        });
        pDialog.setCancelClickListener(SweetAlertDialog::cancel);
    }
    private void deletePost(String id) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", id);
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        FamilheeyApplication application = FamilheeyApplication.get(this);
        ApiServices apiServices = RetrofitBase.createRxResource(this, ApiServices.class);
        subscriptions.add(apiServices.deletePost(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(response -> onBackPressed(), throwable -> {
                }));
    }
    private void Dialoguereport(String id) {

        final Dialog dialog = new Dialog(this);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialogue_report);
        dialog.setCanceledOnTouchOutside(false);
        TextInputEditText editText = dialog.findViewById(R.id.description);
        dialog.findViewById(R.id.btn_close).setOnClickListener(view -> dialog.dismiss());
        dialog.findViewById(R.id.btndone).setOnClickListener(v -> {
            if (Objects.requireNonNull(editText.getText()).toString().trim().equals("")) {
                Toast.makeText(this, "Reason is required", Toast.LENGTH_LONG).show();
            } else {
                reportPost(id, editText.getText().toString().trim(), dialog);
            }
        });

        dialog.show();
    }

    private void reportPost(String post_id, String des, Dialog dialog) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        jsonObject.addProperty("type_id", post_id);
        jsonObject.addProperty("description", des);
        jsonObject.addProperty("spam_page_type", "post");
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        FamilheeyApplication application = FamilheeyApplication.get(this);
        ApiServices apiServices = RetrofitBase.createRxResource(this, ApiServices.class);
        subscriptions.add(apiServices.reportPost(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(response -> {
                    dialog.dismiss();
                    Toast.makeText(this, "Reported successfully.We will review the post and do the needful,thank you", Toast.LENGTH_LONG).show();
                }, throwable -> {
                }));
    }

    private void share_view(PostData postData) {
        if (postData.getValid_urls() != null && postData.getValid_urls().size() > 0 && postData.getValid_urls().size() == 1 && postData.getPost_attachment() == null || postData.getPost_attachment().size() == 0) {
            if (postData.getUrl_metadata() != null) {
                if (postData.getUrl_metadata().getUrlMetadataResult() != null) {
                    link_preview1.setVisibility(View.VISIBLE);
                    String t = postData.getUrl_metadata().getUrlMetadataResult().getTitle();
                    String d = postData.getUrl_metadata().getUrlMetadataResult().getDescription();
                    if (t != null && !t.isEmpty()) {
                        txt_url_des1.setText(t);
                    } else if (d != null && !d.isEmpty()) {
                        txt_url_des1.setText(t);
                    }
                    txt_url1.setText(postData.getUrl_metadata().getUrlMetadataResult().getUrl());

                    String url = postData.getUrl_metadata().getUrlMetadataResult().getImage() + "";
                    if (!url.contains("http")) {
                        url = postData.getUrl_metadata().getUrlMetadataResult().getUrl() + url;
                    }

                    url_img1.setScaleType(ImageView.ScaleType.CENTER);
                    Glide.with(this)
                            .load(url)
                            .placeholder(R.drawable.d_image)
                            .apply(Utilities.getCurvedRequestOptions())
                            .into(url_img1);

                    link_preview1.setOnClickListener(view -> {
                        try {
                            String urls = postData.getValid_urls().get(0).trim();
                            if (!urls.contains("http")) {
                                urls = urls.replaceAll("www.", "http://www.");
                            }
                            if (urls.contains("blog.familheey.com")) {
                                // Dinu(09/11/2021) for open another app
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urls));
                                startActivity(intent);
                            }
                            else if (urls.contains("familheey")) {
                                openAppGetParams(urls);
                            } else {
                                // Dinu(15/07/2021) for open another app
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urls));
                                startActivity(intent);
                                //   startActivity(new Intent(this, BrowserActivity.class).putExtra("URL", urls));
                            }
                        } catch (Exception e) {
                          /*
                          Not needed
                           */
                        }
                    });

                }
            }
        } else {
            link_preview1.setVisibility(View.GONE);
        }


        description1 = postData.getSnap_description().trim();

        description1 = description1.replaceAll("HTTP:", "http:").replaceAll("Http:", "http:")
                .replaceAll("Https:", "https:")
                .replaceAll("HTTPS:", "https:");
        description1 = description1.replaceAll("WWW.", "www.").replaceAll("Www.", "www.");
/*
        share_txt_des.addAutoLinkMode(
                AutoLinkMode.MODE_HASHTAG,
                AutoLinkMode.MODE_URL);
        share_txt_des.setHashtagModeColor(ContextCompat.getColor(this, R.color.buttoncolor));
        share_txt_des.setUrlModeColor(ContextCompat.getColor(this, R.color.buttoncolor));*/
        String id = postData.getCreated_by() + "";

        if (postData.getCommon_share_count() != null && Integer.parseInt(postData.getCommon_share_count()) > 1) {
            int c = Integer.parseInt(postData.getCommon_share_count());
            share_postusername.setText(postData.getShared_user_name() + " and other " + (c - 1) + " shared a post");
        } else {
            share_postusername.setText(postData.getShared_user_name() + " shared a post");
        }

        postusername.setText(postData.getShared_user_name());
        innerpostusername.setText(postData.getParent_post_created_user_name());

        if (postData.getGroup_name() != null) {
            share_postedgroup.setText("Shared in " + postData.getGroup_name());
        } else if (postData.getTo_user_name() != null) {
            share_postedgroup.setText("Shared to you");
        }

        if (postData.getParent_post_grp_name() != null) {
            innerpostedgroup.setText("Posted in " + postData.getParent_post_grp_name());
        } else {
            innerpostedgroup.setText("Posted in " + postData.getPrivacy_type());
        }

        share_postdate.setText(dateFormat(postData.getCreatedAt()));
        share_txt_temp.setText(description1);

        share_txt_temp.post(() -> {
            if (share_txt_temp.getLineCount() > 2) {
                share_txt_less_or_more.setVisibility(View.VISIBLE);
                share_txt_des.setMaxLines(3);
                share_txt_des.setEllipsize(TextUtils.TruncateAt.END);
                /**@author Devika on 07-12-2021
                 * to handle links inside shared posts**/
                share_txt_des.setText(description1);
                if(share_txt_des!=null){
                    hashTagHelper = HashTagHelper.Creator.create(ContextCompat.getColor(this,R.color.buttoncolor),null);
                    hashTagHelper.handle(share_txt_des);
                    Linkify.addLinks(share_txt_des, Linkify.ALL);
                    share_txt_des.setLinkTextColor(ContextCompat.getColor(this,R.color.buttoncolor));
                }

                share_txt_temp.setVisibility(View.GONE);
            } else {
                share_txt_less_or_more.setVisibility(View.GONE);
                /**@author Devika on 07-12-2021
                 * to handle links inside shared posts**/
                share_txt_des.setText(description1);
                if(share_txt_des!=null){
                    hashTagHelper = HashTagHelper.Creator.create(ContextCompat.getColor(this,R.color.buttoncolor),null);
                    hashTagHelper.handle(share_txt_des);
                    Linkify.addLinks(share_txt_des, Linkify.ALL);
                    share_txt_des.setLinkTextColor(ContextCompat.getColor(this,R.color.buttoncolor));
                }
            }
        });


        if (postData.getParent_post_created_user_propic() != null && !postData.getParent_post_created_user_propic().isEmpty()) {
            Glide.with(this)
                    .load(IMAGE_BASE_URL + Constants.Paths.PROFILE_PIC + postData.getParent_post_created_user_propic())
                    .placeholder(R.drawable.family_dashboard_background)
                    .into(innerprofileImage);
        } else {
            innerprofileImage.setImageResource(R.drawable.avatar_male);
        }


        if (postData.getPost_attachment() != null && postData.getPost_attachment().size() > 0) {
            share_imageSlider.setVisibility(View.VISIBLE);

            if (postData.getPost_attachment().get(0).getType().contains("image")) {
                if (postData.getPost_attachment().get(0).getWidth() != null && postData.getPost_attachment().get(0).getHeight() != null) {

                    ViewGroup.LayoutParams params = share_imageSlider.getLayoutParams();
                    params.height = getinnerwidgetsize(postData.getPost_attachment().get(0).getWidth(), postData.getPost_attachment().get(0).getHeight());
                    share_imageSlider.setLayoutParams(params);

                } else {
                    ViewGroup.LayoutParams params = share_imageSlider.getLayoutParams();
                    params.height = getinnerwidgetsize();
                    share_imageSlider.setLayoutParams(params);
                }
            } else {

                ViewGroup.LayoutParams params = share_imageSlider.getLayoutParams();
                params.height = 600;
                share_imageSlider.setLayoutParams(params);
            }


            adapter = new PostSliderAdapter(this, postData);
            share_imageSlider.setSliderAdapter(adapter);
            share_imageSlider.setIndicatorAnimation(IndicatorAnimations.THIN_WORM);
            share_imageSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        } else {
            share_imageSlider.setVisibility(View.GONE);
        }


        share_txt_count.setText(postData.getConversation_count());

        share_txt_less_or_more.setOnClickListener(view -> {
            String description = postData.getSnap_description().trim();
            description = description.replaceAll("HTTP:", "http:").replaceAll("Http:", "http:")
                    .replaceAll("Https:", "https:")
                    .replaceAll("HTTPS:", "https:");
            description = description.replaceAll("WWW.", "www.").replaceAll("Www.", "www.");
            if (share_txt_less_or_more.getText().equals("More")) {
                share_txt_less_or_more.setText("Less");
                /**@author Devika on 07-12-2021
                 * to handle links inside shared posts**/
                share_txt_des.setText(description);
                if(share_txt_des!=null){
                    hashTagHelper = HashTagHelper.Creator.create(ContextCompat.getColor(this,R.color.buttoncolor),null);
                    hashTagHelper.handle(share_txt_des);
                    Linkify.addLinks(share_txt_des, Linkify.ALL);
                    share_txt_des.setLinkTextColor(ContextCompat.getColor(this,R.color.buttoncolor));
                }
                share_txt_des.setMaxLines(Integer.MAX_VALUE);
                share_txt_des.setEllipsize(null);
            } else {
                share_txt_less_or_more.setText("More");
                share_txt_des.setMaxLines(3);
                share_txt_des.setEllipsize(TextUtils.TruncateAt.END);
            }
        });
        share_share_count.setText(postData.getShared_user_count());


        if (id.equals(SharedPref.getUserRegistration().getId())) {
            share_view_count.setText(postData.getViews_count());
            share_view_count.setVisibility(View.VISIBLE);
            share_share_count.setVisibility(View.VISIBLE);

            share_imgviw.setVisibility(View.VISIBLE);
            share_imgsha.setVisibility(View.VISIBLE);
        } else {
            share_imgviw.setVisibility(View.GONE);
            share_imgsha.setVisibility(View.GONE);

            share_view_count.setVisibility(View.GONE);
            share_share_count.setVisibility(View.GONE);
        }

        share_imgsha.setOnClickListener(v -> {
            addViewCount(postData.getPost_id() + "");
            if (Integer.parseInt(postData.getShared_user_count()) > 0) {

                this.startActivity(new Intent(this, SharelistActivity.class)
                        .putExtra(Constants.Bundle.TYPE, "POST")
                        .putExtra("event_id", postData.getOrgin_id() + "")
                        .putExtra("user_id", postData.getCreated_by() + ""));
            }
        });

        share_imgviw.setOnClickListener(v -> {
            if (Integer.parseInt(postData.getViews_count()) > 0) {
                this.startActivity(new Intent(this, SharelistActivity.class)
                        .putExtra(Constants.Bundle.TYPE, "POSTVIEW")
                        .putExtra("event_id", postData.getPost_id() + "")
                        .putExtra("user_id", postData.getCreated_by() + ""));
            }
        });
        btn_more.setOnClickListener(v ->

                {
                    if (id.equals(SharedPref.getUserRegistration().getId())) {
                        showMenusPostOwnerShare(v, postData);
                    } else {
                        showMenusNormalUser(v, postData);
                    }
                }

        );

        if (postData.getConversation_enabled()) {
            share_txt_count.setText(postData.getConversation_count());
            share_img_con.setVisibility(View.VISIBLE);
            share_txt_count.setVisibility(View.VISIBLE);
        } else {
            share_img_con.setVisibility(View.GONE);
            share_txt_count.setVisibility(View.GONE);
        }

        if (postData.getConversation_enabled() && Integer.parseInt(postData.getConversation_count_new()) > 0) {
            share_unread_status.setVisibility(View.VISIBLE);
        } else {
            share_unread_status.setVisibility(View.GONE);
        }

        /**04/04/2022**/
        // for update read status
        if(postData.getNotification_key()!=null) {
            database = FirebaseDatabase.getInstance(FIREBASE_DATABASE_URL).getReference(Constants.ApiPaths.FIREBASE_USER_TYPE + SharedPref.getUserRegistration().getId() + "_notification");
            database.child(postData.getNotification_key()).child("visible_status").setValue("read");
        }
    }
    public int getwidgetsize(String width, String hight) {
        float screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        float wf = Float.parseFloat(width);
        float hf = Float.parseFloat(hight);
        return Math.round((screenWidth / wf) * hf);
    }
    public int getwidgetsize() {

        float screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        float s = screenWidth / 4;
        return Math.round(s * 3);
    }
    private void addViewCount(String post_id) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        jsonObject.addProperty("post_id", post_id);
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        FamilheeyApplication application = FamilheeyApplication.get(this);
        ApiServices apiServices = RetrofitBase.createRxResource(this, ApiServices.class);
        subscriptions.add(apiServices.addViewCount(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(response -> {
                }, throwable -> {
                }));
    }
    private String dateFormat(String time) {
        DateTime dateTime = ISODateTimeFormat.dateTimeParser().parseDateTime(time);
        DateTimeFormatter dtfOut = DateTimeFormat.forPattern("MMM dd yyyy hh:mm aa");
        return dtfOut.print(dateTime);
    }
    public int getinnerwidgetsize(String width, String hight) {
        float screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels - 100;
        float wf = Float.parseFloat(width);
        float hf = Float.parseFloat(hight);
        return Math.round((screenWidth / wf) * hf);
    }

    public int getinnerwidgetsize() {

        float screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels - 100;
        float s = screenWidth / 4;
        return Math.round(s * 3);
    }

    // to fetch replys corresponding comment id
    private void fetchCommentsReply(int pos) {
        PostComment childItem =chatModelList.get(pos);
        ArrayList<GetCommentReplyResponse> commentReply = new ArrayList<>();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("comment_id", childItem.getComment_id());
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(this);
        apiServiceProvider.getCommentReplies(jsonObject, null, new RetrofitListener() {
            @Override
            public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {

                try {
                    JSONArray mainObject = new JSONArray(responseBodyString);
                    for (int i = 0; i < mainObject.length(); i++) {
                            if(!mainObject.getJSONObject(i).getString("file_type").contains("audio")){
                                commentReply.add(getCommentReply(mainObject.get(i).toString()));
                            }
                    }
                    chatModelList.get(pos).setCommentReply(commentReply);
                    //  childItem.setCommentReply(commentReply);
                    chatAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
                hideProgress();
            }
        });
    }
    private static GetCommentReplyResponse getCommentReply(String replyJson) {
        return GsonUtils.getInstance().getGson().fromJson(replyJson, GetCommentReplyResponse.class);
    }
    private void uploadDocumentToServer(String type) {
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
        Call<ResponseBody> call = service.uploadMultipleChatAttachment(SOCKET_COMMENT_URL + "posts/post_comment", description, postId, comment, commentedBy, groupId,parts);

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
    private void uploadQuoteDocumentToServer(String type) {
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
        RequestBody  quoted_item = createPartFromString(chatModel.getChatText());
        RequestBody  quoted_id = createPartFromString(chatModel.getComment_id());
        RequestBody quoted_user = createPartFromString(chatModel.getSenderName());
        RequestBody quoted_date = createPartFromString(chatModel.getCreatedAt());
        if (parts.size() == 0) {
            return;
        }
        Call<ResponseBody> call = service.uploadMultipleChatAttachment(SOCKET_COMMENT_URL + "posts/post_comment", description, postId, comment, commentedBy, groupId,quoted_item,quoted_id,quoted_user,quoted_date,parts);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if(isQuote) {
                    qoute_imageAttach.setVisibility(View.VISIBLE);
                    qoute_imageCamera.setVisibility(View.VISIBLE);
                    btnqouteSend.setVisibility(View.VISIBLE);
                }
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
// refresh for, when comes from edit page
    private void getPostAfterResult(String id) {
        //   shimmer_view_container.setVisibility(View.VISIBLE);
        postid=id;
        shimmer_view_container.startShimmer();
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
                    shimmer_view_container.setVisibility(View.GONE);
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        if (response.body().getData().size() > 0) {
                            postData = response.body().getData().get(0);
                            fetchComments();

                            if (postData.getShared_user_name() == null) {
                                normal_view.setVisibility(View.VISIBLE);
                                labelComment.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.VISIBLE);
                                normalViewDataSet(postData);
                            } else {
                                share_view.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.VISIBLE);
                                labelComment.setVisibility(View.VISIBLE);
                                share_view(postData);
                            }
                            addViewCount(postData.getPost_id() + "");
                        } else {
                            SweetAlertDialog contentNotFoundDialog = Utilities.getContentNotFoundDialog(this);
                            contentNotFoundDialog.setConfirmClickListener(sweetAlertDialog -> {
                                contentNotFoundDialog.cancel();
                                Intent intent = new Intent( this, MainActivity.class );
                                intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                                startActivity( intent );
                            });
                            contentNotFoundDialog.setCanceledOnTouchOutside(false);
                            contentNotFoundDialog.setCancelable(false);
                            contentNotFoundDialog.show();
                            Utilities.addPositiveButtonMargin(contentNotFoundDialog);
                        }
                    } else {
                        SweetAlertDialog contentNotFoundDialog = Utilities.getContentNotFoundDialog(this);
                        contentNotFoundDialog.setConfirmClickListener(sweetAlertDialog -> {
                            contentNotFoundDialog.cancel();
                            Intent intent = new Intent( this, MainActivity.class );
                            intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                            startActivity( intent );
                        });
                        contentNotFoundDialog.setCanceledOnTouchOutside(false);
                        contentNotFoundDialog.setCancelable(false);
                        contentNotFoundDialog.show();
                        Utilities.addPositiveButtonMargin(contentNotFoundDialog);
                    }
                }, throwable -> shimmer_view_container.setVisibility(View.GONE)));
    }


}
