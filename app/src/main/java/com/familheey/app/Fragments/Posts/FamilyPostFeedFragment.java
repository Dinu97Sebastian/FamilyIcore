package com.familheey.app.Fragments.Posts;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
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
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.webkit.MimeTypeMap;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
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
import com.familheey.app.Activities.FamilyAddMemberActivity;
import com.familheey.app.Adapters.AlbumPostAdapter;
import com.familheey.app.Decorators.BottomAdditionalMarginDecorator;
import com.familheey.app.FamilheeyApplication;
import com.familheey.app.Fragments.FamilyDashboardFragment;
import com.familheey.app.Interfaces.FamilyDashboardInteractor;
import com.familheey.app.Interfaces.FamilyDashboardListener;
import com.familheey.app.Interfaces.ProgressListener;
import com.familheey.app.Interfaces.RetrofitListener;
import com.familheey.app.Models.ApiCallbackParams;
import com.familheey.app.Models.ErrorData;
import com.familheey.app.Models.Request.HistoryImages;
import com.familheey.app.Models.Request.Image;
import com.familheey.app.Models.Request.PostInfo;
import com.familheey.app.Models.Request.PostRequest;
import com.familheey.app.Models.Response.Family;
import com.familheey.app.Models.Response.SelectFamilys;
import com.familheey.app.Models.imageSizes;
import com.familheey.app.Networking.Retrofit.ApiServiceProvider;
import com.familheey.app.Networking.Retrofit.ApiServices;
import com.familheey.app.Networking.Retrofit.RetrofitBase;
import com.familheey.app.Parsers.FamilyParser;
import com.familheey.app.Post.PostAttachmentAdapter;
import com.familheey.app.Post.ProgressRequestBody;
import com.familheey.app.R;
import com.familheey.app.Utilities.Constants;
import com.familheey.app.Utilities.FileUtil;
import com.familheey.app.Utilities.FileUtils;
import com.familheey.app.Utilities.GifSizeFilter;
import com.familheey.app.Utilities.SharedPref;
import com.familheey.app.Utilities.Utilities;
import com.familheey.app.pagination.PaginationAdapterCallback;
import com.familheey.app.pagination.PaginationScrollListener;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.gun0912.tedpermission.TedPermission;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.filter.Filter;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

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

import static android.content.Context.CONNECTIVITY_SERVICE;
import static com.familheey.app.Utilities.Constants.Bundle.POSITION;
import static com.familheey.app.Utilities.Constants.FamilyDashboardIdentifiers.TypeFamilyPostFeedFragment;
import static com.familheey.app.Utilities.Constants.Bundle.DATA;

public class FamilyPostFeedFragment extends Fragment implements PaginationAdapterCallback, PostAttachmentAdapter.OnAlbumSelectedListener, PostAdapterInFamilyFeed.postItemClick,PostAdapterInFamilyFeed.postRating, ProgressRequestBody.UploadCallbacks, RetrofitListener,View.OnClickListener{
    public static final int EDIT_REQUEST_CODE = 1002;
    public static final int CHAT_REQUEST_CODE = 1003;
    private static final int RESULT_DOC = 36;
    private static final int RESLUT_DOC = 36;
    private final int REQUEST_CODE_PERMISSIONS = 1;
    private int UPLOAD_FILE_POSITION = 0;
    private int PICK_IMAGE = 10;
    private int RESULT_LOAD_VIDEO = 11;
    private int type = 0;
    int uploadCallCount = 0;
    private ArrayList<SelectFamilys> family;
    private String File_Name = "";
    List<MultipartBody.Part> parts;
    private int RESULT_AUDIO=111;
    private ArrayList<imageSizes> sizes = new ArrayList<>();
    Call<ResponseBody> call;
    private SweetAlertDialog progressDialogs;
    private final String MIME_TYPE_TEXT = "text/*";
    private final String MIME_TYPE_VIDEO = "video/*";
    private final String MIME_TYPE_IMAGE = "image/png";
    private final String MIME_TYPE_PDF = "application/pdf";
    private ArrayList<HistoryImages> historyImages = new ArrayList<>();
    private Boolean isUploadingImage = false;
    private List<Uri> fileUris = new ArrayList<>();
    private Dialog dialog;
    Spannable mspanable;
    int hashTagIsComing = 0;
    private static final int POST_CREATE_REQUEST = 1006;
    private static final int FROM_POST_COMMENT = 210;
    public CompositeDisposable subscriptions;
    //    @BindView(R.id.searchPost)megha(16/08/21)
//    EditText searchPost;
    @BindView(R.id.postList)
    RecyclerView postList;
    @BindView(R.id.stickypostList)
    RecyclerView stickypostList;
    List<PostData> data = new ArrayList<>();
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.layoutNoPost)
    LinearLayout layoutNoPost;
    //    @BindView(R.id.clearSearch)megha(16/08/2021)
//    ImageView clearSearch;
    @BindView(R.id.create_post)
    MaterialButton createPost;
    @BindView(R.id.create_new_post)
    CardView createPostFab;

    @BindView(R.id.layout_no_post_on_creation)
    LinearLayout layout_no_post_on_creation;
    @BindView(R.id.tv_empty_indicator_text)
    TextView tv_empty_indicator_text;
    @BindView(R.id.iv_empty_indicator)
    ImageView iv_empty_indicator;
    @BindView(R.id.create_post_on_family_creation)
    MaterialButton create_post_on_family_creation;
    @BindView(R.id.invite_members_on_family_creation)
    MaterialButton invite_members_on_family_creation;
    @BindView(R.id.inviteMemberToMyFamily)
    ExtendedFloatingActionButton inviteMemberToMyFamily;

    @BindView(R.id.postFeedParent)
    ConstraintLayout postFeedParent;
    private Integer prev_id = 0;
    private boolean isLoading = true;
    private boolean isLastPage = false;

    private boolean isAdmin = false;
    private boolean canCreateAnnouncement = false;
    private String PostCreate = "";
    private final int TOTAL_PAGES = 5;
    private int currentPage = 0;
    private String query = "";
    private PostAdapterInFamilyFeed adapter;
    private FamilyDashboardInteractor familyDashboardInteractor;
    private int id = 0;
    private Boolean canCreatePost;
    private Boolean isMember;
    private List<StickyPost> stickyPost = new ArrayList<>();
    String value = "";
    private String starCount;
    private String user_id;
    private String rateCount;
    private Integer rates;
    private AlbumPostAdapter albumEventAdapter;
    private List<Image> albumDocuments = new ArrayList<>();
    RecyclerView imgRecycler;
    PostAttachmentAdapter adapterAttach;
    EditText description;
    Switch conversation;
    Switch share;
    Switch rating;
    boolean isConversation=true;
    boolean isShare=true;
    boolean isRating=false;
    private ProgressListener mListener;
    private FamilyDashboardListener familyDashboardListener;
    RelativeLayout validateView;
    TextView validateMessage;
    Switch announcement;
    boolean isAnnouncement=false;
    boolean isRatingByDefault=false;
    public Family familyArray;
    public Family familyInfo;
    static TransferUtility transferUtility;
    static TransferObserver uploadObserver;
    private int uploadingId;
    public FamilyPostFeedFragment() {
    }

    public static FamilyPostFeedFragment newInstance(Family family,int id, Boolean isAdmin, Boolean canCreatePost, String PostCreate, Boolean isMember, Boolean canCreateAnnouncement) {
        FamilyPostFeedFragment fragment = new FamilyPostFeedFragment();
        Bundle args = new Bundle();
        args.putParcelable(DATA, family);
        args.putInt("ID", id);
        args.putBoolean("ADMIN", isAdmin);
        args.putBoolean("ISMEMBER", isMember);
        args.putBoolean("CANCREATE", canCreatePost);
        args.putString("POSTCREATE", PostCreate);
        args.putBoolean("CANCREATEANNOUNCEMENT", canCreateAnnouncement);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        subscriptions = new CompositeDisposable();
        assert getArguments() != null;
        familyInfo = getArguments().getParcelable(DATA);
        id = (getArguments().getInt("ID"));
        PostCreate = (getArguments().getString("POSTCREATE"));
        isAdmin = getArguments().getBoolean("ADMIN");
        canCreatePost = getArguments().getBoolean("CANCREATE");
        isMember = getArguments().getBoolean("ISMEMBER");
        canCreateAnnouncement=getArguments().getBoolean("CANCREATEANNOUNCEMENT");
        family = new ArrayList<>();
        SelectFamilys obj = new SelectFamilys();
        obj.setId(String.valueOf(id));
        family.add(obj);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_family_post_feed, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //initializeSearchClearCallback(); for hide search icon(megha)

        fetchFamilyFeed();
        inits();
        //create_post_on_family_creation.setOnClickListener(this::onClick);
        invite_members_on_family_creation.setOnClickListener(this::onClick);
        //visibilityRestrictions();
    }

    private void visibilityRestrictions() {
        if(adapter.getItemCount()==0){
            if(familyInfo.getUserStatus().equalsIgnoreCase("member")){
                layoutNoPost.setVisibility(View.VISIBLE);
            }else {
                if(familyInfo.getUserStatus().equalsIgnoreCase("admin")&&(Integer.parseInt(familyInfo.getMembersCount())>1)){
                    layout_no_post_on_creation.setVisibility(View.VISIBLE);
                    invite_members_on_family_creation.setGravity(View.INVISIBLE);
                    iv_empty_indicator.setVisibility(View.VISIBLE);
                    tv_empty_indicator_text.setVisibility(View.VISIBLE);
                }else {
                    layout_no_post_on_creation.setVisibility(View.VISIBLE);
                }
            }
        }
    }
/*
    @Override
    public void onItemClick(int pid)
        unStickyPost(pid + "");
    }*/

    public void fetchFamilyFeed() {
        isLastPage = false;
        data.clear();
        if (adapter != null)
            adapter.notifyDataSetChanged();
        else {
            adapter = new PostAdapterInFamilyFeed(this, getContext(), stickyPost, data, this, "PRIVATE", isAdmin,this,FamilyPostFeedFragment.this,familyInfo.getSticky_auto_scroll(),familyInfo.getSticky_scroll_time(),familyInfo.getSticky_post_limit());
            setupRecyclerView();
        }
        currentPage = 0;
        getStickyPost(false);
        getPost();
        if (familyDashboardInteractor != null)
            familyDashboardInteractor.onFamilyAddComponentHidden(TypeFamilyPostFeedFragment);
        else createPostFab.setVisibility(View.INVISIBLE);
    }
//to hide search icon(megha)
//    private void initializeSearchClearCallback() {
//        searchPost.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void afterTextChanged(Editable s) {
//            }
//
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (s.length() == 0)
//                    clearSearch.setVisibility(View.INVISIBLE);
//                else clearSearch.setVisibility(View.VISIBLE);
//            }
//        });
//        clearSearch.setOnClickListener(v -> {
//            searchPost.setText("");
//            onSearchQueryListener(EditorInfo.IME_ACTION_SEARCH);
//        });
//    }

    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        postList.setLayoutManager(layoutManager);
        postList.setItemAnimator(new DefaultItemAnimator());
        postList.setAdapter(adapter);
        postList.addOnScrollListener(new PaginationScrollListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;

                //currentPage = data.size();
                getPost();

            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    int c = layoutManager.findFirstVisibleItemPosition();
                    if (c > 0 && data.size() > c) {
                        if (data.get(c) != null) {
                            if (data.get(c).getPost_id() != null) {
                                if (!(prev_id.equals(data.get(c).getPost_id()))) {
                                    addViewCount(data.get(c).getPost_id() + "");
                                    prev_id = data.get(c).getPost_id();
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public int getTotalPageCount() {
                return TOTAL_PAGES;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });

    }

    private void addViewCount(String post_id) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        jsonObject.addProperty("post_id", post_id);
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        FamilheeyApplication application = FamilheeyApplication.get(getActivity());
        ApiServices apiServices = RetrofitBase.createRxResource(getActivity(), ApiServices.class);
        assert application != null;
        subscriptions.add(apiServices.addViewCount(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(response -> {
                }, throwable -> {
                }));
    }


    private void getStickyPost(boolean value) {
        SweetAlertDialog progressDialog = Utilities.getProgressDialog(getActivity());
        if (value) {
            progressDialog.show();
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        if (id != 0) {
            jsonObject.addProperty("group_id", id + "");
        }
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        FamilheeyApplication application = FamilheeyApplication.get(getActivity());
        ApiServices apiServices = RetrofitBase.createRxResource(getActivity(), ApiServices.class);
        subscriptions.add(apiServices.getStickyPost(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(response -> {
                    if (response.body().size() > 0) {
                        stickyPost = response.body();
                        adapter.addStickyPost(stickyPost, id + "");
                    } else {

                        stickyPost.clear();
                        adapter.addStickyPost(stickyPost, id + "");
                        stickypostList.setVisibility(View.GONE);

                    }
                    if (value) {
                        progressDialog.hide();
                    }

                }, throwable -> {
                    if (value) {
                        progressDialog.hide();
                    }
                }));
    }

    public void getPost() {
        if (currentPage == 0) {
            progressBar.setVisibility(View.VISIBLE);
        }

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());

        if (id != 0) {
            jsonObject.addProperty("group_id", id);
        }

        jsonObject.addProperty("type", "post");
        jsonObject.addProperty("query", this.value);
        jsonObject.addProperty("offset", currentPage + "");
        jsonObject.addProperty("limit", "30");
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        FamilheeyApplication application = FamilheeyApplication.get(getActivity());
        ApiServices apiServices = RetrofitBase.createRxResource(getActivity(), ApiServices.class);
        subscriptions.add(apiServices.getPost(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(response -> {
                    if (currentPage > 0) {
                        adapter.removeLoadingFooter();
                    }


                    if (response.body() != null && response.body().getData() != null && response.body().getData().size() == 30) {
                        data.addAll(response.body().getData());
                        /*(28/08/2021)megha-> post rating is enabled or not */
                        for (int i=0;i<data.size();i++){
                            Boolean rate = data.get(i).rating_enabled;
                            if (rate==null){
                                data.get(i).setRating_enabled(false);
                            }
                            else if (rate){
                                data.get(i).setRating_enabled(true);
                                data.get(i).setRating(data.get(i).getRating());
                                data.get(i).setRating_by_user(data.get(i).getRating_by_user());
                                data.get(i).setRating_count(data.get(i).getRating_count());
                            }else if(!rate){
                                data.get(i).setRating_enabled(false);
                            }
                        }
                        isLoading = false;
                        if (adapter != null) {
                            adapter.notifyDataSetChanged();
                            adapter.addLoadingFooter();
                        }

                    } else {
                        assert response.body() != null;
                        assert response.body().getData() != null;
                        data.addAll(response.body().getData());
                        /*(28/08/2021)megha-> post rating is enabled or not */
                        for (int i=0;i<data.size();i++){
                            Boolean rate = data.get(i).rating_enabled;
                            if(data.get(i).getRating_by_user()==null)
                                data.get(i).setRating_by_user("0");
                            if (rate==null){
                                data.get(i).setRating_enabled(false);
                            }
                            else if (rate){
                                data.get(i).setRating_enabled(true);
                                data.get(i).setRating(data.get(i).getRating());
                                data.get(i).setRating_by_user(data.get(i).getRating_by_user());
                                data.get(i).setRating_count(data.get(i).getRating_count());
                            }else if(!rate){
                                data.get(i).setRating_enabled(false);
                            }
                        }
                        isLastPage = true;
                    }

                    currentPage = data.size();
                    progressBar.setVisibility(View.GONE);
                    if (postList.getItemDecorationCount() > 0) {
                        postList.removeItemDecorationAt(0);
                    }
                    postList.addItemDecoration(new BottomAdditionalMarginDecorator());
                    adapter.notifyDataSetChanged();

                    /*if (response.body() != null && response.body().getData() != null && response.body().getData().size() > 0) {
                        data.addAll(response.body().getData());
                        currentPage = data.size();
                        if (adapter != null) {
                            adapter.notifyDataSetChanged();
                            adapter.addLoadingFooter();
                        }
                    } else {
                        isLastPage = true;
                        if (data.size() > 0) {
                            adapter.removeLoadingFooter();
                        }
                        if (postList.getItemDecorationCount() > 0) {
                            postList.removeItemDecorationAt(0);
                        }
                        adapter.removeLoadingFooter();
                        if (postList.getItemDecorationCount() > 0) {
                            postList.removeItemDecorationAt(0);
                        }
                        postList.addItemDecoration(new BottomAdditionalMarginDecorator());
                        adapter.notifyDataSetChanged();
                    }*/

                    if (data.size() > 0) {
                        /**@author Devika
                         * Modified on 22-02-2022 for ticket 652**/
                        if(Integer.parseInt(familyInfo.getMembersCount())>1){
                            if(familyInfo.getUserStatus().equalsIgnoreCase("member")){
                                layoutNoPost.setVisibility(View.GONE);
                            }else if(familyInfo.getUserStatus().equalsIgnoreCase("not-member")){
                                layoutNoPost.setVisibility(View.GONE);
                                layout_no_post_on_creation.setVisibility(View.GONE);
                            }else {
                                layout_no_post_on_creation.setVisibility(View.GONE);
                                invite_members_on_family_creation.setVisibility(View.GONE);
                            }
                        }else {
                            if(familyInfo.getUserStatus().equalsIgnoreCase("not-member")){
                                layoutNoPost.setVisibility(View.GONE);
                                layout_no_post_on_creation.setVisibility(View.GONE);
                            }else {
                               /* layout_no_post_on_creation.setVisibility(View.VISIBLE);
                                invite_members_on_family_creation.setVisibility(View.VISIBLE);
                                tv_empty_indicator_text.setVisibility(View.INVISIBLE);
                                create_post_on_family_creation.setVisibility(View.INVISIBLE);
                                iv_empty_indicator.setVisibility(View.INVISIBLE);*/
                                layout_no_post_on_creation.setVisibility(View.GONE);
                                //TODO
                                Fragment fragment1 = requireActivity().getSupportFragmentManager().findFragmentById(R.id.familyDashboardContainer);
                                if (fragment1 instanceof FamilyDashboardFragment) {
                                    FamilyDashboardFragment familyDashboardFragment = (FamilyDashboardFragment) fragment1;
                                    familyDashboardFragment.makeInviteMemberVisible();
                                }
                            }
                        }
                        /** end **/
                        postList.setVisibility(View.VISIBLE);
                        if (familyDashboardInteractor != null)
                            familyDashboardInteractor.onFamilyAddComponentVisible(TypeFamilyPostFeedFragment);
                        else createPostFab.setVisibility(View.VISIBLE);
                    } else {
                        /**@author Devika
                         * Modified on 22-02-2022 for ticket 652**/
                        if(Integer.parseInt(familyInfo.getMembersCount())>1){
                            if(familyInfo.getUserStatus().equalsIgnoreCase("member")){
                                layoutNoPost.setVisibility(View.VISIBLE);
                            }else   if(familyInfo.getUserStatus().equalsIgnoreCase("not-member")){
                                layout_no_post_on_creation.setVisibility(View.GONE);
                                layoutNoPost.setVisibility(View.VISIBLE);
                            }else {
                                layout_no_post_on_creation.setVisibility(View.VISIBLE);
                                invite_members_on_family_creation.setVisibility(View.GONE);
                                Fragment fragment1 = requireActivity().getSupportFragmentManager().findFragmentById(R.id.familyDashboardContainer);
                                if (fragment1 instanceof FamilyDashboardFragment) {
                                    FamilyDashboardFragment familyDashboardFragment = (FamilyDashboardFragment) fragment1;
                                    familyDashboardFragment.makeInviteMemberInvisible();
                                }
                                //TODO
                            }
                        }else {
                            if(familyInfo.getUserStatus().equalsIgnoreCase("not-member")){
                                layout_no_post_on_creation.setVisibility(View.GONE);
                                layoutNoPost.setVisibility(View.VISIBLE);
                            }else{
                                layout_no_post_on_creation.setVisibility(View.VISIBLE);
                            }
                        }
                        /** end **/
                        postList.setVisibility(View.GONE);
                        if (familyDashboardInteractor != null)
                            familyDashboardInteractor.onFamilyAddComponentHidden(TypeFamilyPostFeedFragment);
                        else createPostFab.setVisibility(View.INVISIBLE);
                    }

                }, throwable -> {
                    progressBar.setVisibility(View.GONE);
                    if (adapter != null)
                        adapter.showRetry(true, "");
                }));
    }

    private void makeStickyPost(String pid) {
        SweetAlertDialog progressDialog = Utilities.getProgressDialog(getActivity());
        progressDialog.show();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("post_id", pid);
        if (id != 0) {
            jsonObject.addProperty("group_id", id + "");
        }
        jsonObject.addProperty("type", "stick");
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        FamilheeyApplication application = FamilheeyApplication.get(getActivity());
        ApiServices apiServices = RetrofitBase.createRxResource(getActivity(), ApiServices.class);
        subscriptions.add(apiServices.makeStickyPost(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(response -> {
                    /*
                     * megha(06/11/2021), condition for checking whether the post is sticky or not.*/
                    if (response.isSuccessful()) {
                        getStickyPost(true);
                        progressDialog.hide();
                    } else {
                        Toast.makeText(application, "Already exists as sticky post", Toast.LENGTH_SHORT).show();
                        progressDialog.hide();
                    }
                }, throwable -> {
                    progressDialog.hide();
                }));
    }

    private void unStickyPost(String pid) {
        SweetAlertDialog progressDialog = Utilities.getProgressDialog(getActivity());
        progressDialog.show();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("post_id", pid);
        if (id != 0) {
            jsonObject.addProperty("group_id", id + "");
        }
        jsonObject.addProperty("type", "unstick");
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        FamilheeyApplication application = FamilheeyApplication.get(getActivity());
        ApiServices apiServices = RetrofitBase.createRxResource(getActivity(), ApiServices.class);
        subscriptions.add(apiServices.makeStickyPost(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(response -> {
                    getStickyPost(true);
                    progressDialog.hide();

                }, throwable -> {
                    progressDialog.hide();
                }));
    }

    /*public void searchPost(String query) {
        this.query = query;
        isLastPage = false;
        data.clear();
        currentPage = 0;
        isLoading = true;
        getPost();
    }*/
    /* megha(03/09/21) for rating api*/
    private void onItemRating(JsonObject jsonObject, int position,String rating) {
        SweetAlertDialog progressDialog = Utilities.getProgressDialog(getActivity());
        progressDialog.show();
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        FamilheeyApplication application = FamilheeyApplication.get(getActivity());
        ApiServices apiServices = RetrofitBase.createRxResource(getActivity(), ApiServices.class);
        subscriptions.add(apiServices.rate(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(response -> {
                    JSONObject json= (JSONObject) new JSONTokener(response.body().string()).nextValue();
                    JSONObject json2 = json.getJSONObject("data");
                    rateCount = (String) json2.get("total_rating");

                    String reviewers=(String)json2.get("rating_count");
                    if(stickyPost.size()!=0) {
                        data.get(position-1).setRating_by_user(rating);
                        data.get(position-1).setRating(rateCount);
                        data.get(position-1).setRating_count(reviewers);
                    }else{
                        data.get(position).setRating_by_user(rating);
                        data.get(position).setRating(rateCount);
                        data.get(position).setRating_count(reviewers);
                    }
                    adapter.notifyDataSetChanged();
                    progressDialog.hide();
                },throwable -> {
                    progressDialog.hide();
                }));
    }

//    public void searchPost(String query) {Megha(16/08/2021)
//        this.query = query;
//        isLastPage = false;
//        data.clear();
//        currentPage = 0;
//        isLoading = true;
//        getPost();
//    }

    //    @OnEditorAction(R.id.searchPost)megha(16/08/2021)
//    protected boolean onSearchQueryListener(int actionId) {
//        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//            searchPost(searchPost.getText().toString());
//            try {
//                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
//                imm.hideSoftInputFromWindow(searchPost.getWindowToken(), 0);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return true;
//        }
//        return false;
//    }
    @Override
    public void onRating(int position, JsonObject jsonObject, String rating) {
        onItemRating(jsonObject,position,rating);

    }
    @Override
    public void makeAsSticky(int poston) {
        makeStickyPost(data.get(poston).getPost_id() + "");
    }

    @Override
    public void retryPageLoad() {
        getPost();
    }

    @Override
    public void onEditActivity(Intent intent) {
        startActivityForResult(intent, EDIT_REQUEST_CODE);
    }

    @Override
    public void onChatActivity(Intent intent) {
        startActivityForResult(intent, CHAT_REQUEST_CODE);
    }

    @Override
    public void makeAsUnSticky(String id) {
        unStickyPost(id);
    }
    /**@author Devika defined on 18/02/2022 for setting the visibility of views inside the
     * empty indicator layout once the user deletes the last post in feed**/
    @Override
    public void loadPosts() {
        familyDashboardInteractor.onFamilyAddComponentHidden(TypeFamilyPostFeedFragment);
        createPostFab.setVisibility(View.INVISIBLE);
        if(Integer.parseInt(familyInfo.getMembersCount())>1){
            if(familyInfo.getUserStatus().equalsIgnoreCase("member")){
                layoutNoPost.setVisibility(View.VISIBLE);
            }else {
                layout_no_post_on_creation.setVisibility(View.VISIBLE);
                invite_members_on_family_creation.setVisibility(View.GONE);
                tv_empty_indicator_text.setVisibility(View.VISIBLE);
                iv_empty_indicator.setVisibility(View.VISIBLE);
                create_post_on_family_creation.setVisibility(View.VISIBLE);
            }
        }else {
            layout_no_post_on_creation.setVisibility(View.VISIBLE);
            invite_members_on_family_creation.setVisibility(View.VISIBLE);
            tv_empty_indicator_text.setVisibility(View.VISIBLE);
            iv_empty_indicator.setVisibility(View.VISIBLE);
            create_post_on_family_creation.setVisibility(View.VISIBLE);
            //16-03-22
            Fragment fragment1 = requireActivity().getSupportFragmentManager().findFragmentById(R.id.familyDashboardContainer);
            if (fragment1 instanceof FamilyDashboardFragment) {
                FamilyDashboardFragment familyDashboardFragment = (FamilyDashboardFragment) fragment1;
                familyDashboardFragment.makeInviteMemberInvisible();
            }
        }
    }
    /**end**/
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            int pos = data.getExtras().getInt("pos");
            PostData postData = new Gson().fromJson(data.getExtras().getString("data"), PostData.class);
            this.data.get(pos).setPost_attachment(postData.getPost_attachment());
            this.data.get(pos).setIs_shareable(postData.getIs_shareable());
            this.data.get(pos).setShared_user_count(postData.getShared_user_count());
            this.data.get(pos).setSnap_description(postData.getSnap_description());
            this.data.get(pos).setConversation_enabled(postData.getConversation_enabled());
            this.data.get(pos).setRating_enabled(postData.getRating_enabled());
            this.data.get(pos).setIs_viewed(postData.getIs_viewed());
            this.data.get(pos).setViews_count(postData.getViews_count());
//            this.data.get(pos).setRating(postData.getRating());
//            this.data.get(pos).setRating_count(postData.getRating_count());
            adapter.notifyDataSetChanged();
            getStickyPost(false);
        } else if (requestCode == CHAT_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            if (data.getExtras().getBoolean("isChat")) {
                /*this.data.clear();
                currentPage = 0;
                isLoading = true;
                isLastPage = false;
                setupRecyclerView();
                progressBar.setVisibility(View.VISIBLE);
                getPost();*/
                this.data.get(data.getExtras().getInt(POSITION)).setConversation_count_new("0");
                int pos = data.getExtras().getInt(POSITION);
                PostData postData = new Gson().fromJson(data.getExtras().getString("data"), PostData.class);
                this.data.get(pos).setPost_attachment(postData.getPost_attachment());
                this.data.get(pos).setIs_shareable(postData.getIs_shareable());
                this.data.get(pos).setShared_user_count(postData.getShared_user_count());
                this.data.get(pos).setSnap_description(postData.getSnap_description());
                this.data.get(pos).setConversation_enabled(postData.getConversation_enabled());
                this.data.get(pos).setRating_enabled(postData.getRating_enabled());
                this.data.get(pos).setConversation_count(postData.getConversation_count());
                this.data.get(pos).setRating(postData.getRating());
                this.data.get(pos).setRating_count(postData.getRating_count());
                this.data.get(pos).setRating_by_user(postData.getRating_by_user());
                this.data.get(pos).setIs_viewed(postData.getIs_viewed());
                this.data.get(pos).setViews_count(postData.getViews_count());
                adapter.notifyDataSetChanged();
            }
            else {
                //if(stickyPost.size()!=0)
                if((stickyPost.size())!=0) {
                    this.data.get(data.getExtras().getInt(POSITION)).setConversation_count_new("0");

                    int pos = data.getExtras().getInt(POSITION);
                    if(pos==0)
                        pos=0;
                    else
                        pos=pos-1;
                    PostData postData = new Gson().fromJson(data.getExtras().getString("data"), PostData.class);
                    this.data.get(pos).setPost_attachment(postData.getPost_attachment());
                    this.data.get(pos).setIs_shareable(postData.getIs_shareable());
                    this.data.get(pos).setShared_user_count(postData.getShared_user_count());
                    this.data.get(pos).setSnap_description(postData.getSnap_description());
                    this.data.get(pos).setConversation_enabled(postData.getConversation_enabled());
                    this.data.get(pos).setRating_enabled(postData.getRating_enabled());
                    this.data.get(pos).setConversation_count(postData.getConversation_count());
                    this.data.get(pos).setRating(postData.getRating());
                    this.data.get(pos).setRating_count(postData.getRating_count());
                    this.data.get(pos).setRating_by_user(postData.getRating_by_user());
                    this.data.get(pos).setIs_viewed(postData.getIs_viewed());
                    this.data.get(pos).setViews_count(postData.getViews_count());
                    adapter.notifyDataSetChanged();
                }
                else{
                    this.data.get(data.getExtras().getInt(POSITION)).setConversation_count_new("0");

                    int pos = data.getExtras().getInt(POSITION);
                    PostData postData = new Gson().fromJson(data.getExtras().getString("data"), PostData.class);
                    this.data.get(pos).setPost_attachment(postData.getPost_attachment());
                    this.data.get(pos).setIs_shareable(postData.getIs_shareable());
                    this.data.get(pos).setShared_user_count(postData.getShared_user_count());
                    this.data.get(pos).setSnap_description(postData.getSnap_description());
                    this.data.get(pos).setConversation_enabled(postData.getConversation_enabled());
                    this.data.get(pos).setRating_enabled(postData.getRating_enabled());
                    this.data.get(pos).setConversation_count(postData.getConversation_count());
                    this.data.get(pos).setRating(postData.getRating());
                    this.data.get(pos).setRating_count(postData.getRating_count());
                    this.data.get(pos).setRating_by_user(postData.getRating_by_user());
                    this.data.get(pos).setIs_viewed(postData.getIs_viewed());
                    this.data.get(pos).setViews_count(postData.getViews_count());
                    adapter.notifyDataSetChanged();
                }
            }
        } else if (requestCode == POST_CREATE_REQUEST && resultCode == Activity.RESULT_OK) {
            fetchFamilyFeed();
        }
        if (requestCode == ImagePicker.REQUEST_CODE) {

            Uri uri = data.getData();
            if(uri!=null) {
                fileUris = new ArrayList<>();
                fileUris.add(uri);
                dialog.findViewById(R.id.attachView).setVisibility(View.INVISIBLE);
                dialog.findViewById(R.id.no_files).setVisibility(View.GONE);
                dialog.findViewById(R.id.attachment).setVisibility(View.VISIBLE);

                albumDocuments.addAll(generateUploadingImageModels(PICK_IMAGE));
                adapterAttach.notifyDataSetChanged();
                File file = FileUtil.getFile(getContext(), uri);

                try {
                    UPLOAD_FILE_POSITION = 0;
                    new FamilyPostFeedFragment.ImageCompressionAsyncTask(getContext()).execute(file);
                } catch (Exception e) {

                }
            }
        }else if (resultCode == ImagePicker.RESULT_ERROR){
            Toast.makeText(getContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
        }
        if (requestCode == RESULT_AUDIO ) {
            if (data != null && resultCode == getActivity().RESULT_OK) {
                fileUris = new ArrayList<>();


                Uri uri = data.getData();
                String path=uri.getPath();

                albumDocuments.addAll(generateUploadingAudioModels(RESULT_AUDIO,uri));
                dialog.findViewById(R.id.attachView).setVisibility(View.INVISIBLE);
                dialog.findViewById(R.id.no_files).setVisibility(View.GONE);
                dialog.findViewById(R.id.attachment).setVisibility(View.VISIBLE);

                adapterAttach.notifyDataSetChanged();
                imgRecycler.setVisibility(View.VISIBLE);
                try {
                    File file = AudioreadContentToFile(uri);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
                    String timeStamp = dateFormat.format(new Date());
                    File thumbFile = new File(getActivity().getCacheDir(), timeStamp + "_thumb.jpg");

                    HistoryImages obj = new HistoryImages();
                    obj.setType("audio/mp3");
                    obj.setFilename(file.getName());
                    obj.setVideo_thumb("audio_thumb/" + thumbFile.getName());
                    historyImages.add(obj);
                    uploadWithTransferUtility(file, "post/" + file.getName(), albumDocuments.size(), MIME_TYPE_VIDEO);
                    uploadWithTransferUtility(thumbFile, "audio_thumb/" + thumbFile.getName(), -1, MIME_TYPE_VIDEO);


                } catch (Exception e) {

                }

            }
        }
        if (requestCode == PICK_IMAGE) {
            if (resultCode == Activity.RESULT_OK && null != data) {
                fileUris = new ArrayList<>();
                dialog.findViewById(R.id.attachView).setVisibility(View.INVISIBLE);
                dialog.findViewById(R.id.no_files).setVisibility(View.GONE);
                dialog.findViewById(R.id.attachment).setVisibility(View.VISIBLE);

                fileUris.addAll(Matisse.obtainResult(data));
                albumDocuments.addAll(generateUploadingImageModels(PICK_IMAGE));
                adapterAttach.notifyDataSetChanged();
                try {
                    UPLOAD_FILE_POSITION = 0;
                    new FamilyPostFeedFragment.ImageCompressionAsyncTask(getContext()).execute(readContentToFile(fileUris.get(0)));
                } catch (Exception e) {

                }
            }
        }
        else if (requestCode == RESULT_LOAD_VIDEO) {
            if (data != null && resultCode == Activity.RESULT_OK) {
                fileUris = new ArrayList<>();
                dialog.findViewById(R.id.attachView).setVisibility(View.INVISIBLE);
                dialog.findViewById(R.id.no_files).setVisibility(View.GONE);
                dialog.findViewById(R.id.attachment).setVisibility(View.VISIBLE);




                fileUris.addAll(Matisse.obtainResult(data));
                Uri selectedVideoUri = fileUris.get(0);
                String selectedVideoPath = getPath(selectedVideoUri);
                if (Matisse.obtainResult(data).size() != 0)
                    // if (isSizeLess(Matisse.obtainResult(data).get(0))) {
                    albumDocuments.addAll(generateUploadingImageModels(RESULT_LOAD_VIDEO));
                adapterAttach.notifyDataSetChanged();

                try {
                    File file = new File(selectedVideoPath);
                    // File file = readContentToFile(fileUris.get(0));
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
                    String timeStamp = dateFormat.format(new Date());
                    Bitmap thumb = ThumbnailUtils.createVideoThumbnail(file.getPath(), MediaStore.Video.Thumbnails.MINI_KIND);
                    File thumbFile = new File(getActivity().getCacheDir(), timeStamp + "_thumb.jpg");
                    thumbFile.createNewFile();
                    OutputStream os = new BufferedOutputStream(new FileOutputStream(thumbFile));
                    assert thumb != null;
                    thumb.compress(Bitmap.CompressFormat.JPEG, 70, os);
                    os.close();

                    HistoryImages obj = new HistoryImages();
                    obj.setType("video/wav");
                    obj.setFilename(timeStamp+"."+getfileExtension(selectedVideoUri));
                    obj.setVideo_thumb("video_thumb/" + thumbFile.getName());
                    historyImages.add(obj);
                    uploadWithTransferUtility(file, "post/" + obj.getFilename(), albumDocuments.size(), MIME_TYPE_VIDEO);
                    uploadWithTransferUtility(thumbFile, "video_thumb/" + thumbFile.getName(), -1, MIME_TYPE_VIDEO);
                } catch (Exception e) {
                    Toast.makeText(getContext(),
                            "Unable to find selected file.",
                            Toast.LENGTH_LONG).show();
                }

            }
        }
        else if (requestCode == RESULT_DOC) {
            if (data != null && resultCode == getActivity().RESULT_OK) {
                fileUris = new ArrayList<>();
                dialog.findViewById(R.id.attachView).setVisibility(View.INVISIBLE);
                dialog.findViewById(R.id.no_files).setVisibility(View.GONE);
                dialog.findViewById(R.id.attachment).setVisibility(View.VISIBLE);

                Uri uri = data.getData();
                fileUris = new ArrayList<>();
                fileUris.add(uri);

                albumDocuments.addAll(generateUploadingImageModels(RESULT_DOC));
                adapterAttach.notifyDataSetChanged();
                imgRecycler.setVisibility(View.VISIBLE);
                uploadImageToServer();
            }
        }else if (requestCode == FamilyAddMemberActivity.REQUEST_CODE) {
            familyDashboardListener.onFamilyUpdated(false);
        }
    }


    @Override
    public void onSearchTag(String hashtag) {

        //searchPost(hashtag);megha(16/08/2021)
        //PostFragment poostFrag = ((PostFragment)this.getParentFragment());
        //poostFrag.search_post.setText(hashtag);
    }

    public void searchPost(String query) {
        this.query = query;
        isLastPage = false;
        data.clear();
        currentPage = 0;
        isLoading = true;
        getPost();
    }
    @OnClick({R.id.create_post, R.id.create_new_post,R.id.create_post_on_family_creation})
    public void onViewClicked() {
        createNewPost();
    }
    public void setFamilyData(boolean isRated){

    }
    public void createNewPost() {

        if (canCreatePost) {
//            Intent intent = new Intent(getContext(), CreatePostActivity.class).putExtra("post_create", PostCreate).putExtra("FROM", "FAMILY").putExtra("id", id + "");
//            startActivityForResult(intent, POST_CREATE_REQUEST);
            //   ;
            showDialogueForCreatePost();


        } else {
            if (isMember)
                Toast.makeText(getContext(), "You don't have sufficient privileges to create post", Toast.LENGTH_SHORT).show();
            else {
                Toast.makeText(getContext(), "Please join the family to create a post", Toast.LENGTH_SHORT).show();
            }
        }
    }



    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = Utilities.getListener(this, ProgressListener.class);
        familyDashboardInteractor = Utilities.getListener(this, FamilyDashboardInteractor.class);
        familyDashboardListener = Utilities.getListener(this, FamilyDashboardListener.class);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        familyDashboardInteractor = null;
        mListener = null;
        if(familyInfo.getSticky_auto_scroll())
            adapter.swipeStop();
    }

    public void updateFamily(Family family) {
        id = family.getId();
        isAdmin = family.isAdmin();
        canCreatePost = family.canCreatePost();
        PostCreate = family.getPostCreate();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (data != null && data.size() > 0) {
            if (familyDashboardInteractor != null) {
                if(familyInfo.getSticky_auto_scroll()){
                    String pos=SharedPref.read(SharedPref.STICKY_POST_POSITION, "0");
                   adapter.setStickyPostPosition(Integer.parseInt(pos));
                }

                familyDashboardInteractor.onFamilyAddComponentVisible(TypeFamilyPostFeedFragment);
            }
            else createPostFab.setVisibility(View.VISIBLE);
        } else {
            if (familyDashboardInteractor != null)
            {
                //layout_no_post_on_creation.setVisibility(View.VISIBLE);
                familyDashboardInteractor.onFamilyAddComponentHidden(TypeFamilyPostFeedFragment);
            }
            else{
                createPostFab.setVisibility(View.INVISIBLE);
            }
        }
        //visibilityRestrictions();
    }

    public void startSearch(String value) {
        this.value = value;
        isLastPage = false;
        data.clear();
        currentPage = 0;
        isLoading = true;
        getPost();
    }

    @Override
    public void onAlbumDeleted(int position) {
        if (position < historyImages.size())
            historyImages.remove(position);
        if(historyImages.size()==0) {
            dialog.findViewById(R.id.no_files).setVisibility(View.VISIBLE);
            dialog.findViewById(R.id.attachment).setVisibility(View.GONE);
        }
    }


    private void showDialogueForCreatePost() {
        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialogue_create_post);
        Window window = dialog.getWindow();
        assert window != null;
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        window.setBackgroundDrawableResource(android.R.color.transparent);
        historyImages.clear();
        albumDocuments.clear();
        LinearLayout full_View = dialog.findViewById(R.id.full_View);
        setAnimation(full_View);
        LinearLayout attachment = dialog.findViewById(R.id.attachment);
        LinearLayout settings_View = dialog.findViewById(R.id.settings_View);

        ImageView settings = dialog.findViewById(R.id.settings);
        ImageView dialogClose=dialog.findViewById(R.id.btnDismiss);
        ImageView SettingsClose=dialog.findViewById(R.id.btnCloseSettings);
        LinearLayout attachView=dialog.findViewById(R.id.attachView);
        ImageView btnAttach=dialog.findViewById(R.id.btnAttach);
        ImageView btnImage=dialog.findViewById(R.id.btnImage);
        ImageView btnVideo=dialog.findViewById(R.id.btnVideo);
        ImageView btnDoc=dialog.findViewById(R.id.btnDocument);
        ImageView btnAudio=dialog.findViewById(R.id.btnAudio);
        MaterialButton btn_post=dialog.findViewById(R.id.btn_post);
        ImageView btnSettings_back=dialog.findViewById(R.id.settings_back);
        MaterialButton btnSettingsDone=dialog.findViewById(R.id.settings_done);
        ImageView btnCamera=dialog.findViewById(R.id.btnCamera);
        description = dialog.findViewById(R.id.edtxdescription);
        conversation=dialog.findViewById(R.id.conversation);
        announcement=dialog.findViewById(R.id.is_announcement);
        isAnnouncement=false;
        share=dialog.findViewById(R.id.share);
        rating=dialog.findViewById(R.id.rating);
        validateMessage=dialog.findViewById(R.id.validation_message);
        validateView=dialog.findViewById(R.id.validation_view);
        conversation.setChecked(true);
        share.setChecked(true);
        announcement.setChecked(false);
        getFamilyDetails();

        rating.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    dialog.findViewById(R.id.is_announcement).setEnabled(false);
                else
                    dialog.findViewById(R.id.is_announcement).setEnabled(true);
            }
        });
        announcement.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    rating.setEnabled(false);
                else
                    rating.setEnabled(true);
            }
        });

        if (!canCreateAnnouncement) {
            announcement.setVisibility(View.GONE);
            dialog.findViewById(R.id.txt_is_announcement).setVisibility(View.GONE);
        }else{
            announcement.setVisibility(View.VISIBLE);
            dialog.findViewById(R.id.txt_is_announcement).setVisibility(View.VISIBLE);
        }

        dialogClose.setOnClickListener(view->{


            if(isUploadingImage) {
                int ids = uploadObserver.getId();
                Boolean canceled = transferUtility.cancel(ids);
                transferUtility.cancelAllWithType(TransferType.ANY);
                isUploadingImage=false;
            }
            dialog.dismiss();
        });
        SettingsClose.setOnClickListener(view->{
            dialog.dismiss();
        });
        btnAttach.setOnClickListener(View->{
            if (!isUploadingImage) {
                setAnimation(attachView);
                attachView.setVisibility(View.VISIBLE);
            }else{
                setAnimation(validateView);
                validateView.setVisibility(View.VISIBLE);
                validateMessage.setText("Upload in progress please wait a moment.");
                setAnimation(validateView);
                new Handler().postDelayed(() -> {
                    validateView.setVisibility(View.GONE);
                }, 3000);
            }
        });
        settings.setOnClickListener(view->{
            full_View.setVisibility(View.GONE);
            setAnimation(settings_View);
            settings_View.setVisibility(View.VISIBLE);
        });
        btnSettings_back.setOnClickListener(View->{
            settings_View.setVisibility(View.GONE);
            setAnimation(full_View);
            full_View.setVisibility(View.VISIBLE);
            conversation.setChecked(isConversation);
            share.setChecked(isShare);
            rating.setChecked(isRating);
            announcement.setChecked(isAnnouncement);
        });
        btnSettingsDone.setOnClickListener(View->{
            settings_View.setVisibility(View.GONE);
            setAnimation(full_View);
            full_View.setVisibility(View.VISIBLE);
            isConversation=conversation.isChecked();
            isRating=rating.isChecked();
            isShare=share.isChecked();
            isAnnouncement=announcement.isChecked();
        });
        btnImage.setOnClickListener(view -> {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (isReadWritePermissionGranted()) {
                    askForPermission(PICK_IMAGE,dialog.findViewById(android.R.id.content));
                } else {
                    showPermission(PICK_IMAGE,dialog.findViewById(android.R.id.content));
                }
            } else {

                goToImageGalleryintent();
            }
        });
        btnVideo.setOnClickListener(view -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (isReadWritePermissionGranted()) {
                    askForPermission(RESULT_LOAD_VIDEO,dialog.findViewById(android.R.id.content));
                } else {
                    showPermission(RESULT_LOAD_VIDEO,dialog.findViewById(android.R.id.content));
                }
            } else {
                goToVideoGalleryIntent();
            }

        });
        btnDoc.setOnClickListener(view -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (isReadWritePermissionGranted()) {
                    askForPermission(RESULT_DOC,dialog.findViewById(android.R.id.content));
                } else {
                    showPermission(RESULT_DOC,dialog.findViewById(android.R.id.content));
                }
            } else {
                // FileUtils.pickDocument(this, RESULT_DOC);
                pickDocument();
            }

        });
        btnAudio.setOnClickListener(view -> {

            Intent intent = new Intent();
            intent.setType("audio/mpeg");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent,"Select Audio "), RESULT_AUDIO);

        });
        btnCamera.setOnClickListener(view -> {
            ImagePicker.with(this)
                    .crop()
                    .cameraOnly()//Crop image(Optional), Check Customization for more option
                    .compress(1024)			//Final image size will be less than 1 MB(Optional)
                    .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                    .start();
        });

        btn_post.setOnClickListener(view->{
            if (validate()) {
                createPostRequest();
            }
            else{
                new Handler().postDelayed(() -> {
                    validateView.setVisibility(View.GONE);
                }, 3000);
            }
        });
//        RecyclerView recyclerView = dialog.findViewById(R.id.img_recycler);
//        recyclerView.setAdapter(albumEventAdapter);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        imgRecycler = dialog.findViewById(R.id.img_recycler);
        adapterAttach = new PostAttachmentAdapter(getContext(),albumDocuments,this);
        imgRecycler.setAdapter(adapterAttach);
        imgRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        dialog.show();


    }
    private void setAnimation(View view) {
        AnimationSet animationSet = new AnimationSet(true);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.2f, 1.0f);
        alphaAnimation.setDuration(400);
        view.startAnimation(alphaAnimation);
        ScaleAnimation scaleAnimation = new ScaleAnimation(0.75f, 1.0f, 0.75f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(400);
        animationSet.addAnimation(scaleAnimation);
        animationSet.addAnimation(alphaAnimation);
        view.startAnimation(animationSet);
    }


    private boolean isReadWritePermissionGranted() {
        return TedPermission.isGranted(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    private void askForPermission(int ctype,View view) {
        type = ctype;
        if ((ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) +
                ContextCompat.checkSelfPermission(getContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE))
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE) &&
                    ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Snackbar.make(view,
                        "Please grant permissions to write data in sdcard",
                        Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                        v -> ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                REQUEST_CODE_PERMISSIONS)).show();
            } else {
                /* Request for permission */
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE_PERMISSIONS);
            }

        } else {
            if (ctype == PICK_IMAGE)
                goToImageGalleryintent();
            else if (ctype == RESLUT_DOC)
                //  FileUtils.pickDocument(this, RESULT_DOC);
                pickDocument();
            else
                goToVideoGalleryIntent();
        }
    }
    private void showPermission(int type,View views) {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialogue_permission);
        Window window = dialog.getWindow();
        assert window != null;
        window.setBackgroundDrawableResource(android.R.color.transparent);
        dialog.findViewById(R.id.btn_cancel).setOnClickListener(view -> dialog.dismiss());
        dialog.findViewById(R.id.btn_continue).setOnClickListener(view -> {
            dialog.dismiss();
            askForPermission(type,views);
        });
        dialog.show();
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
    private void goToImageGalleryintent() {
        Matisse.from(FamilyPostFeedFragment.this)
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
        Matisse.from(FamilyPostFeedFragment.this)
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
    private String getfileExtension(Uri uri)
    {
        String extension;
        ContentResolver contentResolver =getActivity().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        extension= mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
        return extension;
    }

    @Override
    public void onProgressUpdate(int percentage, int position) {
        if (percentage % 5 == 0) {
            try {
                albumDocuments.get(position).setPrograss(percentage);
                adapterAttach.notifyDataSetChanged();
            } catch (Exception e) {
                Log.i("", "");
            }
        }
    }

    @Override
    public void onClick(View view) {
        if(view == create_post_on_family_creation){
            showDialogueForCreatePost();
        }else if(view == invite_members_on_family_creation){
            if (isAdmin) {
                Intent intent = new Intent(getContext(), FamilyAddMemberActivity.class);
                intent.putExtra(Constants.Bundle.DATA, familyInfo);
                startActivityForResult(intent, FamilyAddMemberActivity.REQUEST_CODE);
            }
            else{
                Toast.makeText(getContext(), "Only admin can invite", Toast.LENGTH_SHORT).show();
            }
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
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), Uri.fromFile(s));
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
                adapterAttach.notifyDataSetChanged();
            } catch (Exception e) {
                getActivity().finish();
            }
        }
    }

    private File readContentToFile(Uri uri) throws IOException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
        String timeStamp = dateFormat.format(new Date());
        final File file = new File(getActivity().getCacheDir(), "post-"+timeStamp);
        try (
                final InputStream in = getActivity().getContentResolver().openInputStream(uri);
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
                Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null)
        ) {
            assert cursor != null;
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
            if (cursor.moveToFirst()) {
                return cursor.getString(columnIndex);
            }
        }
        return Objects.requireNonNull(uri.getPath()).replaceAll(" ", "");
    }
    public void uploadWithTransferUtility(File file, String key, int position, String type) {
        isUploadingImage = true;
        ClientConfiguration configuration = new ClientConfiguration()
                .withMaxErrorRetry(2)
                .withConnectionTimeout(1200000)
                .withSocketTimeout(1200000);

        transferUtility =
                TransferUtility.builder()
                        .context(getContext())
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
                            adapterAttach.notifyDataSetChanged();
                            if (type.equals(MIME_TYPE_IMAGE)) {
                                compressNextFile();
                            }
                        }
                    }
                } catch (Exception e) {
                    getActivity().finish();
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                Log.i("AWS_ID", String.valueOf(id));
                uploadingId=id;
                float percentDonef = ((float) bytesCurrent / (float) bytesTotal) * 100;
                int percentDone = (int) percentDonef;
                if (position > 0 && albumDocuments.size() > position - 1) {
                    albumDocuments.get(position - 1).setPrograss(percentDone);
                    adapterAttach.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(int id, Exception ex) {
                if (position > 0) {
                    isUploadingImage = false;
                    albumDocuments.remove(position - 1);
                    adapterAttach.notifyDataSetChanged();
                }
            }
        });
    }
    //*************************IMAGE COMPRESS*************************

    private void compressNextFile() {
        if (fileUris.size() > UPLOAD_FILE_POSITION + 1) {
            UPLOAD_FILE_POSITION += 1;
            try {
                new FamilyPostFeedFragment.ImageCompressionAsyncTask(getContext()).execute(readContentToFile(fileUris.get(UPLOAD_FILE_POSITION)));
            } catch (Exception ignored) {

            }
        }

    }

    void inits() {
        getContext().startService(new Intent(getContext(), TransferService.class));
        AWSMobileClient.getInstance().initialize(getContext(), new com.amazonaws.mobile.client.Callback<UserStateDetails>() {
            @Override
            public void onResult(UserStateDetails result) {
            }
            @Override
            public void onError(Exception e) {
            }
        });
    }

    private boolean validate() {
        if (isUploadingImage) {
            setAnimation(validateView);
            validateView.setVisibility(View.VISIBLE);
            validateMessage.setText("Upload in progress please wait a moment.");
            setAnimation(validateView);
            return false;
        }
        if (historyImages.size() == 0 && description.getText().toString().trim().equals("")) {
            setAnimation(validateView);
            validateView.setVisibility(View.VISIBLE);
            validateMessage.setText("Please add description or attachment");
            setAnimation(validateView);
            return false;
        }
        /*Megha(23/08/2021)-> rating validation*/
        if ( rating.isChecked()&&historyImages.size()>1){
            setAnimation(validateView);
            validateView.setVisibility(View.VISIBLE);
            validateMessage.setText("Rating is enabled. Only one attachment is allowed.");
            setAnimation(validateView);
            return false;
        }
        return true;
    }
    public void createPostRequest() {
        mListener.showProgressDialog();
        PostRequest request = new PostRequest();
        request.setCategory_id(3 + "");
        request.setCreated_by(SharedPref.getUserRegistration().getId());
        request.setPost_info(new PostInfo());
        if(isAnnouncement){
            request.setType("announcement");
        }else {
            request.setType("post");
            request.setRating_enabled(isRating);
        }
        request.setConversation_enabled(isConversation);
        request.setIs_shareable(isShare);
        request.setTitle("");
        request.setSnap_description(description.getText().toString());
        request.setPost_type("only_groups");
        request.setPrivacy_type("public");
        request.setSelected_groups(family);




        request.setPost_attachment(historyImages);
        FamilheeyApplication application = FamilheeyApplication.get(getContext());
        ApiServices apiServices = RetrofitBase.createRxResource(getContext(), ApiServices.class);
        subscriptions.add(apiServices.createPost(request)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(response -> {
                    mListener.hideProgressDialog();
                    if (response.code() == 200) {
                        if(isAnnouncement)
                            Toast.makeText(getContext(), "Announcement successfully created", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(getContext(), "Post successfully created", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        /**@author Devika on 01-11-2021
                         * for updation posts count inside family dashboard when the user creates a new post
                         * **/
                        Fragment fragment1 = requireActivity().getSupportFragmentManager().findFragmentById(R.id.familyDashboardContainer);
                        if (fragment1 instanceof FamilyDashboardFragment) {
                            FamilyDashboardFragment familyDashboardFragment = (FamilyDashboardFragment) fragment1;
                            familyDashboardFragment.refreshFamilyDetails(familyArray);
                        }
                        fetchFamilyFeed();
                    }
                }, throwable -> mListener.hideProgressDialog()));
    }
    private void uploadImageToServer() {
        sizes.clear();
        uploadCallCount = 0;
        if (checkConnection()) {
            isUploadingImage = true;
            ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getContext());

            parts = new ArrayList<>();
            if (fileUris != null) {
                for (int i = 0; i < fileUris.size(); i++) {

                    String extension= getfileExtension(fileUris.get(i));
                    parts.add(prepareFilePartImage(fileUris.get(i), "application/"+extension, i));

                }
            }
            callImageUploadApi(apiServiceProvider, parts.get(uploadCallCount), "application/pdf");
        } else {
            Toast.makeText(getContext(),
                    "Internet connection not available", Toast.LENGTH_SHORT).show();
        }
    }
    private boolean checkConnection() {
        return ((ConnectivityManager) requireActivity().getSystemService(CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null;
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

                    if (FamilyPostFeedFragment.this.uploadCallCount == fileUris.size()) {
                        adapterAttach.notifyDataSetChanged();
                        isUploadingImage = false;
                    }
                    if (FamilyPostFeedFragment.this.uploadCallCount < fileUris.size())
                        callImageUploadApi(apiServiceProvider, parts.get(FamilyPostFeedFragment.this.uploadCallCount), type);


                } catch (Exception e) {
                }
            }

            @Override
            public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
                if (FamilyPostFeedFragment.this.uploadCallCount == fileUris.size()) {
                    adapterAttach.notifyDataSetChanged();
                    isUploadingImage = false;
                }
                if (FamilyPostFeedFragment.this.uploadCallCount < fileUris.size()) {
                    callImageUploadApi(apiServiceProvider, parts.get(uploadCallCount), type);
                }
            }
        });
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
    @NonNull
    private RequestBody createPartFromString() {
        return RequestBody.create("post", MediaType.parse(MIME_TYPE_TEXT));
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
    private File AudioreadContentToFile(Uri uri) throws IOException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
        String timeStamp = dateFormat.format(new Date());
        final File file = new File(getActivity().getCacheDir(), "Audio_Post-"+timeStamp+".mp3");
        try (
                final InputStream in = getActivity().getContentResolver().openInputStream(uri);
                final OutputStream out = new FileOutputStream(file, false)
        ) {
            byte[] buffer = new byte[1024];
            for (int len; (len = in.read(buffer)) != -1; ) {
                out.write(buffer, 0, len);
            }


            return file;
        }
    }

    public void getFamilyDetails() {
//        mListener.showProgressDialog();
        JsonObject jsonObject = new JsonObject();
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getContext());
        jsonObject.addProperty("group_id",String.valueOf(id));
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        apiServiceProvider.getFamilyDetailsByID(jsonObject, null, this);
    }
    @Override
    public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {
        familyArray = FamilyParser.parseLinkedFamilies(responseBodyString).get(0);
        if(familyArray.getIsRated()==null){
            familyArray.setIsRated(false);
        }
        if(familyArray.getIsRated()) {
            isRating=true;
            rating.setChecked(true);
            dialog.findViewById(R.id.rating).setEnabled(false);
            dialog.findViewById(R.id.is_announcement).setEnabled(false);
        }
        else {
            isRating=false;
            rating.setChecked(false);
            dialog.findViewById(R.id.is_announcement).setEnabled(true);
        }
    }

    @Override
    public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
        networkError();
    }
    // for get path from uri
    public String getPath(Uri uri) {
        Cursor cursor = null;
        try {
            String[] projection = {MediaStore.Images.Media.DATA};
            cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);

            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
    private void changeTheColor(String s, int start, int end) {
        mspanable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.buttoncolor)), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private void networkError() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Uh oh! Check your internet connection and retry.")
                .setCancelable(false)
                .setPositiveButton("Retry", (dialogs, which) ->  {

                    dialog.dismiss();
                    dialogs.dismiss();
                    createNewPost();
                }).

                setNegativeButton("Cancel", (dialogs, which) -> {
                    dialog.dismiss();
                    dialogs.dismiss();
                });
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        AlertDialog alert = builder.create();
        alert.setTitle("Connection Unavailable");
        alert.show();
        params.setMargins(0, 0, 20, 0);
        alert.getButton(DialogInterface.BUTTON_NEGATIVE).setLayoutParams(params);
    }
}
