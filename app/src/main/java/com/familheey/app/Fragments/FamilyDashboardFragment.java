package com.familheey.app.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.amazonaws.mobile.auth.core.internal.util.ThreadUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.familheey.app.Activities.CalendarActivity;
import com.familheey.app.Activities.FamilyAddMemberActivity;
import com.familheey.app.Activities.FamilyDashboardActivity;
import com.familheey.app.Activities.ImageChangerActivity;
import com.familheey.app.Activities.LinkedFamilyActivity;
import com.familheey.app.Activities.UserConnectionsActivity;
import com.familheey.app.Adapters.FamilyDashboardTabAdapter;
import com.familheey.app.Adapters.LinkFamilyAdapter;
import com.familheey.app.Announcement.AnnouncementListingFragment;
import com.familheey.app.Dialogs.FamilyEditDialogFragment;
import com.familheey.app.Fragments.Events.AlbumFragment;
import com.familheey.app.Fragments.FamilyDashboard.AboutUsFragment;
import com.familheey.app.Fragments.FamilyDashboard.BackableFragment;
import com.familheey.app.Fragments.FamilyDashboard.FamilyEventFragment;
import com.familheey.app.Fragments.FamilyDashboard.FamilyNeedsListingFragment;
import com.familheey.app.Fragments.FamilyDashboard.LinkingFragment;
import com.familheey.app.Fragments.FamilyDashboard.LinkingUpdatedFragment;
import com.familheey.app.Fragments.FamilyViewMembers.FamilySubscriptionFragment;
import com.familheey.app.Fragments.FamilyViewMembers.FamilySubscriptionUpdatedFragment;
import com.familheey.app.Fragments.Posts.FamilyPostFeedFragment;
import com.familheey.app.Interfaces.FamilyDashboardInteractor;
import com.familheey.app.Interfaces.FamilyDashboardListener;
import com.familheey.app.Interfaces.ProgressListener;
import com.familheey.app.Interfaces.RetrofitListener;
import com.familheey.app.Models.ApiCallbackParams;
import com.familheey.app.Models.ErrorData;
import com.familheey.app.Models.Response.Family;
import com.familheey.app.Models.Response.LinkfamilyList;
import com.familheey.app.Models.Response.SelectFamilys;
import com.familheey.app.Networking.Retrofit.ApiServiceProvider;
import com.familheey.app.Parsers.FamilyParser;
import com.familheey.app.R;
import com.familheey.app.Stripe.StripeActivity;
import com.familheey.app.Topic.MainActivity;
import com.familheey.app.Utilities.Constants;
import com.familheey.app.Utilities.NonSwipeableViewPager;
import com.familheey.app.Utilities.SharedPref;
import com.familheey.app.Utilities.Utilities;
import com.familheey.app.membership.MembershipFragment;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.shape.CornerFamily;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.content.Context.MODE_PRIVATE;
import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import static com.familheey.app.Utilities.Constants.ApiFlags.GET_FAMILY_DETAILS;
import static com.familheey.app.Utilities.Constants.ApiPaths.IMAGE_BASE_URL;
import static com.familheey.app.Utilities.Constants.ApiPaths.S3_DEV_IMAGE_URL_COVER;
import static com.familheey.app.Utilities.Constants.ApiPaths.S3_DEV_IMAGE_URL_SQUARE;
import static com.familheey.app.Utilities.Constants.Bundle.DATA;
import static com.familheey.app.Utilities.Constants.Bundle.DETAIL;
import static com.familheey.app.Utilities.Constants.Bundle.FAMILY;
import static com.familheey.app.Utilities.Constants.Bundle.GLOBAL_SEARCH;
import static com.familheey.app.Utilities.Constants.Bundle.ID;
import static com.familheey.app.Utilities.Constants.Bundle.IDENTIFIER;
import static com.familheey.app.Utilities.Constants.Bundle.IS_CREATED_NOW;
import static com.familheey.app.Utilities.Constants.Bundle.IS_JOIN_FAMILY;
import static com.familheey.app.Utilities.Constants.Bundle.LINKED_FAMILIES;
import static com.familheey.app.Utilities.Constants.Bundle.LINK_FAMILY_REQUEST;
import static com.familheey.app.Utilities.Constants.Bundle.MEMBER;
import static com.familheey.app.Utilities.Constants.Bundle.REQUEST;
import static com.familheey.app.Utilities.Constants.Bundle.TO_CREATE_FAMILY;
import static com.familheey.app.Utilities.Constants.Bundle.TYPE;
import static com.familheey.app.Utilities.Constants.FamilyDashboardIdentifiers.TypeAboutUsFragment;
import static com.familheey.app.Utilities.Constants.FamilyDashboardIdentifiers.TypeAlbumFragment;
import static com.familheey.app.Utilities.Constants.FamilyDashboardIdentifiers.TypeAnnouncementListingFragment;
import static com.familheey.app.Utilities.Constants.FamilyDashboardIdentifiers.TypeFamilyEventFragment;
import static com.familheey.app.Utilities.Constants.FamilyDashboardIdentifiers.TypeFamilyMembership;
import static com.familheey.app.Utilities.Constants.FamilyDashboardIdentifiers.TypeFamilyNeedsListingFragment;
import static com.familheey.app.Utilities.Constants.FamilyDashboardIdentifiers.TypeFamilyPostFeedFragment;
import static com.familheey.app.Utilities.Constants.FamilyDashboardIdentifiers.TypeFamilyRequestsFragment;
import static com.familheey.app.Utilities.Constants.FamilyDashboardIdentifiers.TypeFamilySubscriptionFragment;
import static com.familheey.app.Utilities.Constants.FamilyDashboardIdentifiers.TypeFoldersFragment;
import static com.familheey.app.Utilities.Constants.FamilyDashboardIdentifiers.TypeLinkedFamilyFragment;
import static com.familheey.app.Utilities.Constants.FamilyDashboardIdentifiers.TypeLinkingFragment;
import static com.familheey.app.Utilities.Constants.ImageUpdate.FAMILY_COVER;
import static com.familheey.app.Utilities.Constants.ImageUpdate.FAMILY_LOGO;
import static com.familheey.app.Utilities.Constants.Paths.COVER_PIC;
import static com.familheey.app.Utilities.Constants.Paths.LOGO;
import static com.familheey.app.Utilities.Utilities.hideCircularReveal;
import static com.familheey.app.Utilities.Utilities.showCircularReveal;

public class FamilyDashboardFragment extends BackableFragment implements TabLayout.OnTabSelectedListener, RetrofitListener, FamilyDashboardInteractor, FamilyEditDialogFragment.OnFamilyEditCompleted, ProgressListener, FamilyCreationLevelAdvancedFragment.OnFamilyCreationListener,View.OnClickListener {
    public static final int STRIPE_REQUEST_CODE = 1001;
    @BindView(R.id.btn_paynow)
    MaterialButton btn_paynow;
    @BindView(R.id.txt_mtype)
    TextView txt_mtype;
    @BindView(R.id.txt_mduration)
    TextView txt_mduration;
    @BindView(R.id.txt_sdate)
    TextView txt_sdate;
    @BindView(R.id.txt_tilldate)
    TextView txt_tilldate;
    @BindView(R.id.txt_fees)
    TextView txt_fees;
    @BindView(R.id.img_payment_status)
    ImageView img_payment_status;
    @BindView(R.id.member_view)
    LinearLayout member_view;
    @BindView(R.id.view_due)
    LinearLayout view_due;
    @BindView(R.id.familyLogo)
    ImageView familyLogo;
    @BindView(R.id.eventCalendar)
    ImageView eventCalendar;
    @BindView(R.id.familyName)
    TextView familyName;
    @BindView(R.id.txt_member)
    TextView txt_member;
    @BindView(R.id.familyDescription)
    TextView familyDescription;
    @BindView(R.id.familyLocation)
    TextView familyLocation;
    @BindView(R.id.txtMembers)
    TextView txtMembers;
    @BindView(R.id.postsText)
    TextView postsText;
   /* @BindView(R.id.familySettings)
    ImageView familySettings;*/
    @BindView(R.id.collapsingToolbar)
    CollapsingToolbarLayout collapsingToolbar;
    @BindView(R.id.appBar)
    AppBarLayout appBar;
    @BindView(R.id.myFamiliesTab)
    TabLayout myFamiliesTab;
    @BindView(R.id.myFamiliesDetailsViewPager)
    NonSwipeableViewPager myFamiliesDetailsViewPager;
    @BindView(R.id.goToSubscription)
    FloatingActionButton goToSubscription;
    @BindView(R.id.txtFolders)
    TextView txtFolders;
    @BindView(R.id.cancel_bottom)
    TextView cancel;
    @BindView(R.id.etxt_due_paid)
    EditText etxt_due_paid;
    @BindView(R.id.txt_paid_fees)
    TextView txt_paid_fees;
    @BindView(R.id.unfollow)
    TextView unfollow;
    @BindView(R.id.info_view)
    LinearLayout info_view;
    @BindView(R.id.txt_note)
    TextView txt_note;
    @BindView(R.id.bottom_sheet)
    LinearLayout bottomSheet;
    @BindView(R.id.bg)
    View bg;
    @BindView(R.id.bottom_sheet_more)
    ConstraintLayout bottomSheetMore;
    @BindView(R.id.backgroundCover)
    ImageView backgroundCover;
    @BindView(R.id.link_families)
    TextView linkFamilies;
    @BindView(R.id.editFamily)
    ImageView editFamily;
    @BindView(R.id.coverEdit)
    ImageView coverEdit;
    @BindView(R.id.parentCard)
    MaterialCardView parentCard;
    @BindView(R.id.membersCount)
    TextView membersCount;
    @BindView(R.id.familiesContainer)
    LinearLayout familiesContainer;
    @BindView(R.id.knownMembersCount)
    TextView knownMembersCount;
    @BindView(R.id.knownMembersContainer)
    LinearLayout knownMembersContainer;
    @BindView(R.id.eventsCount)
    TextView eventsCount;
    @BindView(R.id.eventsContainer)
    LinearLayout eventsContainer;
    @BindView(R.id.joinFamily)
    MaterialButton joinFamily;
   /* @BindView(R.id.imgMore)
    ImageView imgMore;*/
    @BindView(R.id.imgMoreOptions)
    ImageView imgMore;
    @BindView(R.id.toolBarTitle)
    TextView toolBarTitle;
    @BindView(R.id.progressFamily)
    ProgressBar progressFamily;
    @BindView(R.id.addFamilyComponent)
    CardView addFamilyComponent;
    @BindView(R.id.acceptRejectLayout)
    LinearLayout acceptRejectLayout;
    @BindView(R.id.acceptInvite)
    MaterialButton acceptInvite;
    @BindView(R.id.rejectInvite)
    MaterialButton rejectInvite;
    @BindView(R.id.joinThisFamily)
    MaterialButton joinThisFamily;
    //Toolbar with searchview
    @BindView(R.id.searchInfo)
    EditText searchAlbumQuery;
    @BindView(R.id.constraintSearch)
    ConstraintLayout constraintSearch;
    @BindView(R.id.familySearch)
    ImageView imgSearch;
    @BindView(R.id.imageBack)
    ImageView imageBack;
    @BindView(R.id.toolbar_family_dashboard)
    Toolbar toolbarFamilyDashboard;
    @BindView(R.id.acceptInvitation)
    MaterialButton acceptInvitation;
    /*@BindView(R.id.familyFilter)
    ImageView familyFilter;*/
    @BindView(R.id.clearSearch)
    ImageView clearSearch;
    @BindView(R.id.invitationToFamily)
    MaterialButton invitationToFamily;

    public String requestCount = "0";
    public boolean isLinkable;
    Fragment fragment;
    private BottomSheetBehavior sheetBehaviorMore;
    public Family family;
    private FamilyDashboardListener mListener;
    private FamilyDashboardTabAdapter familyDetailsPagerAdapter;
    private String notificationType = "";
    private String pay = "";
    private Boolean isCreatedNow = false;
    private boolean editStatus = false;
    private static boolean isJoin = false;
    private Boolean toCreatedFamily = false;
    ArrayList<SelectFamilys> uploadingFamilies;
    @BindView(R.id.imgCreateFeed)
    ImageView imgCreateFeed;
    private Boolean uploadingProgress = false;
    public FamilyDashboardFragment() {
        // Required empty public constructor
    }
    public static FamilyDashboardFragment newInstance(Family family, Boolean isCreatedNow, boolean isJoinFamily,boolean toCreateFamily) {
        FamilyDashboardFragment fragment = new FamilyDashboardFragment();
        Bundle args = new Bundle();
        args.putParcelable(DATA, family);
        args.putBoolean(IS_CREATED_NOW, isCreatedNow);
        args.putBoolean(IS_JOIN_FAMILY, isJoinFamily);
        args.putBoolean(TO_CREATE_FAMILY, toCreateFamily);
        fragment.setArguments(args);
        return fragment;
    }
    public static FamilyDashboardFragment newInstance(Family family, String type, String pay, boolean isJoinFamily) {
        FamilyDashboardFragment fragment = new FamilyDashboardFragment();
        isJoin=isJoinFamily;
        Bundle args = new Bundle();
        args.putParcelable(DATA, family);
        args.putString(TYPE, type);
        args.putString("PAY", pay);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            pay = getArguments().getString("PAY", "");
            family = getArguments().getParcelable(DATA);
            requestCount=family.getRequestCount();
            notificationType = getArguments().getString(TYPE, "0");
            isCreatedNow = getArguments().getBoolean(IS_CREATED_NOW, false);
            toCreatedFamily=getArguments().getBoolean(TO_CREATE_FAMILY, false);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_family_dashboard, container, false);
        ButterKnife.bind(this, view);
        setSmallData();
        initListener();
        initializeCallbacks();
        searchAlbumQuery.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                onSearchQueryListener(i);
                return true;
            }
        });
        return view;
    }
    private void setSmallData() {
        ThreadUtils.runOnUiThread(() -> {
            if (family.getLogo() != null) {
                Glide.with(FamilyDashboardFragment.this.requireContext())
                        .load(S3_DEV_IMAGE_URL_SQUARE + IMAGE_BASE_URL + LOGO + family.getLogo())
                        .apply(Utilities.getCurvedRequestOptions())
                        .placeholder(R.drawable.family_logo)
                        .transition(withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                        .into(familyLogo);
            }
            if (family.getGroupName() != null)
                familyName.setText(family.getGroupName());
            if (family.getBaseRegion() != null)
                familyLocation.setText(family.getBaseRegion());

            if (family.getCreatedByName() != null && family.getGroupType() != null)
                familyDescription.setText(family.getGroupCategory() + ", By " + family.getCreatedByName());
            else if (family.getCreatedByName() == null && family.getGroupType() == null)
                familyDescription.setVisibility(View.GONE);
            else if (family.getCreatedByName() != null)
                familyDescription.setText("By " + family.getCreatedByName());
            else
                familyDescription.setText(family.getGroupCategory());
            if (family.getMembersCount() != null) {
                membersCount.setText(family.getMembersCount());
                if(Integer.parseInt(family.getMembersCount())>1){
                    txtMembers.setText("Members");
                }else {
                    txtMembers.setText("Member");
                }
            }
            /**updating post text based on count**/
            if (family.getPostCount() != null){
                if(Integer.parseInt(family.getPostCount())>0){
                    postsText.setText("Posts");
                }else {
                   postsText.setText("Post");
                }
                eventsCount.setText(family.getPostCount());
            }

            getFamilyDetails();
            initBottomSheet();
            applyStyle();
        });
    }

    private void initBottomSheet() {
        sheetBehaviorMore = BottomSheetBehavior.from(bottomSheetMore);
        sheetBehaviorMore.setHideable(true);
        sheetBehaviorMore.setPeekHeight(0);
        sheetBehaviorMore.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (BottomSheetBehavior.STATE_COLLAPSED == newState) {
                    bg.setVisibility(View.GONE);
                    addFamilyComponent.setVisibility(View.VISIBLE);
                } else bg.setVisibility(View.VISIBLE);
            }
            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });
    }
    private void applyStyle() {
        float radius = getResources().getDimension(R.dimen.card_top_radius);
        parentCard.setShapeAppearanceModel(
                parentCard.getShapeAppearanceModel()
                        .toBuilder()
                        .setTopLeftCorner(CornerFamily.ROUNDED, radius)
                        .setTopRightCorner(CornerFamily.ROUNDED, radius)
                        .setBottomRightCorner(CornerFamily.ROUNDED, 0)
                        .setBottomLeftCornerSize(0)
                        .build());
    }
    private void initRestrictions() {
        int memtab = Objects.requireNonNull(myFamiliesDetailsViewPager.getAdapter()).getCount() - 1;
        if (family.getUserStatus().equalsIgnoreCase("admin")) {
            editFamily.setVisibility(View.VISIBLE);
            coverEdit.setVisibility(View.VISIBLE);
            //familySettings.setVisibility(View.VISIBLE);
            joinFamily.setVisibility(View.GONE);
            //11-11-21
            joinThisFamily.setVisibility(View.GONE);
            knownMembersContainer.setVisibility(View.GONE);
            /**@author Devika on 22-09-21
             * To set the visibility of camera icon(for editing cover pic) based on toolbar collapsed and open state
             * **/
            appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                @Override
                public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                    if (verticalOffset == 0) {
                        coverEdit.setVisibility(View.VISIBLE);
                    } else {
                        coverEdit.setVisibility(View.INVISIBLE);
                    }
                }
            });

        } else if (family.getUserStatus().equalsIgnoreCase("not-member")) {
            editFamily.setVisibility(View.INVISIBLE);
            coverEdit.setVisibility(View.INVISIBLE);
            //familySettings.setVisibility(View.INVISIBLE);
           // joinFamily.setVisibility(View.VISIBLE);
            joinFamily.setVisibility(View.VISIBLE);
            //11-11-21
            joinThisFamily.setVisibility(View.VISIBLE);
            knownMembersContainer.setVisibility(View.VISIBLE);
        } else {
            editFamily.setVisibility(View.INVISIBLE);
            coverEdit.setVisibility(View.INVISIBLE);
            //familySettings.setVisibility(View.VISIBLE);
            joinFamily.setVisibility(View.GONE);
            //11-11-21
            joinThisFamily.setVisibility(View.GONE);
            knownMembersContainer.setVisibility(View.GONE);
        }
        //10-09-21
       /* if (isCreatedNow) {
            myFamiliesTab.removeOnTabSelectedListener(this);
            myFamiliesDetailsViewPager.setCurrentItem(memtab);
            myFamiliesTab.addOnTabSelectedListener(this);
        }*/

        if (!family.getUserStatus().equalsIgnoreCase("not-member")) {
            switch (notificationType) {
                case MEMBER:
                    fragment = FamilySubscriptionUpdatedFragment.newInstance(family,"toMember");
                    ((FamilyDashboardActivity) getActivity()).replaceFragment(fragment);
                    ((FamilyDashboardActivity) getActivity()).loadCoverPic(family);
                    ((FamilyDashboardActivity) getActivity()).setupToolbarForMembersWithoutSearch(family);
                    /*myFamiliesTab.removeOnTabSelectedListener(this);
                    myFamiliesDetailsViewPager.setCurrentItem(memtab);
                    myFamiliesTab.addOnTabSelectedListener(this);*/
                    break;
                case REQUEST:
                    fragment = FamilySubscriptionUpdatedFragment.newInstance(family,"fromPage");
                    ((FamilyDashboardActivity) getActivity()).replaceFragment(fragment);
                    ((FamilyDashboardActivity) getActivity()).loadCoverPic(family);
                    ((FamilyDashboardActivity) getActivity()).setupToolbarForMembersWithoutSearch(family);
                    /*myFamiliesTab.removeOnTabSelectedListener(this);
                    myFamiliesDetailsViewPager.setCurrentItem(memtab);
                    myFamiliesTab.addOnTabSelectedListener(this);
                    FamilySubscriptionFragment familySubscriptionFragment = (FamilySubscriptionFragment) familyDetailsPagerAdapter.getItem(memtab);
                    if (familySubscriptionFragment != null)
                        familySubscriptionFragment.displayMemberRequest();*/
                    break;
                case LINK_FAMILY_REQUEST:
                    fragment= LinkingUpdatedFragment.newInstance(family,"toLinkedRequest");
                    ((FamilyDashboardActivity) getActivity()).replaceFragment(fragment);
                    ((FamilyDashboardActivity) getActivity()).loadCoverPic(family);
                    ((FamilyDashboardActivity) getActivity()).setupToolbarForMembersWithoutSearch(family);
                    /*myFamiliesTab.removeOnTabSelectedListener(this);
                    myFamiliesDetailsViewPager.setCurrentItem(7);
                    myFamiliesTab.addOnTabSelectedListener(this);
                    LinkingFragment linkingFragment = (LinkingFragment) familyDetailsPagerAdapter.getItem(7);
                    if (linkingFragment != null)
                        linkingFragment.displayLinkRequest();*/
                    break;
                case LINKED_FAMILIES:
                    fragment= LinkingUpdatedFragment.newInstance(family);
                    ((FamilyDashboardActivity) getActivity()).replaceFragment(fragment);
                    ((FamilyDashboardActivity) getActivity()).loadCoverPic(family);
                    ((FamilyDashboardActivity) getActivity()).setupToolbarForMembersWithoutSearch(family);
                    /*myFamiliesTab.removeOnTabSelectedListener(this);
                    myFamiliesDetailsViewPager.setCurrentItem(7);
                    myFamiliesTab.addOnTabSelectedListener(this);*/
                    break;
                case DETAIL:
                    myFamiliesTab.removeOnTabSelectedListener(this);
                    myFamiliesDetailsViewPager.setCurrentItem(0);
                    myFamiliesTab.addOnTabSelectedListener(this);
                    break;
            }
        }
        if (family.getMemberJoining() != null && family.getMemberJoining().equalsIgnoreCase("1") && ( family.getInvitationStatus() != null && family.getInvitationStatus()==false ) ){
            joinFamily.setText("Private");
            //11-11-21
            joinThisFamily.setText("Private");
        }
        else if (family.getInvitationStatus() != null && family.getInvitationStatus()) {
            if ("invite".equalsIgnoreCase(family.getType())){
                joinFamily.setVisibility(View.GONE);
                //11-11-21
                joinThisFamily.setVisibility(View.GONE);
                acceptRejectLayout.setVisibility(View.VISIBLE);
                //11-11-21
                acceptInvitation.setVisibility(View.VISIBLE);
            }
            else
                joinFamily.setText("Pending");
            //11-11-21
            joinThisFamily.setText("Pending");
        } else {
            joinFamily.setText("Join");
            //11-11-21
            joinThisFamily.setText("Join this family");
        }
    }

    LinkFamilyAdapter linkFamilyAdapter;
    ArrayList<LinkfamilyList> linkfamilyLists = new ArrayList<>();

    private void followtheFamily() {
        JsonObject jsonObject = new JsonObject();
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getContext());
        jsonObject.addProperty("group_id", family.getId().toString());
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        apiServiceProvider.followtheFamily(jsonObject, null, this);
    }
    private void unFollowtheFamily() {
        JsonObject jsonObject = new JsonObject();
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getContext());
        jsonObject.addProperty("group_id", family.getId().toString());
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        apiServiceProvider.unFollowtheFamily(jsonObject, null, this);
    }
    private void openBottomSheetMore() {
        if (sheetBehaviorMore.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            dataSetInBottomSheet();
            sheetBehaviorMore.setState(BottomSheetBehavior.STATE_EXPANDED);
            addFamilyComponent.setVisibility(View.GONE);
        } else {
            sheetBehaviorMore.setState(BottomSheetBehavior.STATE_COLLAPSED);
            addFamilyComponent.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FamilyDashboardListener) {
            mListener = (FamilyDashboardListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement FamilyDashboardListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    private String dateFormat(Long value, String format) {
        if (value != null && value > 100) {
            value = TimeUnit.SECONDS.toMillis(value);
            DateTime dateTime = new DateTime(value);
            DateTimeFormatter dtfOut = DateTimeFormat.forPattern(format);
            return dtfOut.print(dateTime);
        }
        return "";
    }

    private void setMemberShipdata() {
        if (family.getMembership_type() != null && family.getMembership_to() != null) {
            if (!family.getMembership_type().isEmpty() && !family.getMembership_to().isEmpty()/*&&family.getIs_membership()*/) {
                member_view.setVisibility(View.VISIBLE);
                member_view.setVisibility(View.VISIBLE);
                if ("Pending".equals(family.getMembership_payment_status()) || "Partial".equals(family.getMembership_payment_status())) {
                    img_payment_status.setVisibility(View.VISIBLE);
                } else {
                    img_payment_status.setVisibility(View.GONE);
                }
            }
            if (family.getMembership_period_type_id() == 4) {
                txt_member.setText(family.getMembership_type() + ", " + family.getMembership_sub_type());
            } else
                txt_member.setText(family.getMembership_type() + ", till " + dateFormat(Long.parseLong(family.getMembership_to()), "MMM dd yyyy"));
        }
        if (pay.equals("PAY")) {
            pay = "";
            member_view.performClick();
        }
    }

    public void initializeTabs(Family family) {
        try {
            if(family.getIsRated()==null)
                family.setIsRated(false);
            FragmentManager manager=getFragmentManager();
            familyDetailsPagerAdapter = new FamilyDashboardTabAdapter(getChildFragmentManager());
            if (family.getUserStatus().equalsIgnoreCase("not-member")) {//Not Member
                if (family.isPublic()) {//Family is Public
                    familyDetailsPagerAdapter.addFragment("Feed", FamilyPostFeedFragment.newInstance(family,family.getId(), family.isAdmin(), family.canCreatePost(), family.getPostCreate(), family.isMember(),family.canCreateAnnouncement()));
                    familyDetailsPagerAdapter.addFragment("Announcements", AnnouncementListingFragment.newInstance(family.getId() + ""));
                    familyDetailsPagerAdapter.addFragment("About Us", AboutUsFragment.newInstance(family,false,""));
                    imgMore.setVisibility(View.INVISIBLE);
                } else {//Family is Private
                    familyDetailsPagerAdapter.addFragment("About Us", AboutUsFragment.newInstance(family,false,""));
                    imgMore.setVisibility(View.INVISIBLE);
                }
            }/*else if(family.getUserStatus().equalsIgnoreCase("member")&&family.getStatus().equalsIgnoreCase("joined")){

            }*/
            else {
               // familyDetailsPagerAdapter.replaceFragment("About Us", AboutUsFragment.newInstance(family,false,""));
                //myFamiliesDetailsViewPager.setSaveFromParentEnabled(false);

                //familyDetailsPagerAdapter.clear(manager);

                familyDetailsPagerAdapter.addFragment("Feeds", FamilyPostFeedFragment.newInstance(family,family.getId(), family.isAdmin(), family.canCreatePost(), family.getPostCreate(), family.isMember(),family.canCreateAnnouncement()));
                familyDetailsPagerAdapter.addFragment("Events", FamilyEventFragment.newInstance(family));
                familyDetailsPagerAdapter.addFragment("Requests", FamilyNeedsListingFragment.newInstance(family.getId() + "", family.getPostCreate(), family.getStripe_account_id(), family.isAdmin(), family.getGroupName()));
                imgMore.setVisibility(View.VISIBLE);
                /*familyDetailsPagerAdapter.addFragment("Announcements", AnnouncementListingFragment.newInstance(family.getId() + ""));
                familyDetailsPagerAdapter.addFragment("Albums", AlbumFragment.newInstance(Constants.FileUpload.TYPE_FAMILY, family.getId().toString(), family.isAdmin()));
                familyDetailsPagerAdapter.addFragment("About Us", AboutUsFragment.newInstance(family));
                familyDetailsPagerAdapter.addFragment("Linked Families", LinkingFragment.newInstance(family));
                familyDetailsPagerAdapter.addFragment("Documents", FoldersFragment.newInstance(family.getId().toString(), Constants.FileUpload.TYPE_FAMILY));
                familyDetailsPagerAdapter.addFragment("Linked Families", LinkingFragment.newInstance(family));
                if (family.getUserStatus().equalsIgnoreCase("admin") && family.getIs_membership() != null && family.getIs_membership()) {
                    familyDetailsPagerAdapter.addFragment("Membership", MembershipFragment.newInstance(family.getId().toString()));
                }
                familyDetailsPagerAdapter.addFragment("Members", FamilySubscriptionFragment.newInstance(family));*/
            }
            myFamiliesDetailsViewPager.setAdapter(familyDetailsPagerAdapter);
            myFamiliesTab.setupWithViewPager(myFamiliesDetailsViewPager);
            myFamiliesDetailsViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(myFamiliesTab));
            myFamiliesDetailsViewPager.setOffscreenPageLimit(myFamiliesTab.getTabCount());
            myFamiliesTab.addOnTabSelectedListener(this);

            /**@author Devika on 18/11/2021
             * To set visibility of calendar icon for event tab on swiping Viewpager
             ***/
            myFamiliesDetailsViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    appBar.setExpanded(false);
                    myFamiliesDetailsViewPager.setCurrentItem(position);
                    Fragment f = familyDetailsPagerAdapter.getItem(myFamiliesDetailsViewPager.getCurrentItem());
                    if(f instanceof FamilyEventFragment){
                        if (family.getUserStatus().equalsIgnoreCase("admin")) {
                            eventCalendar.setVisibility(View.VISIBLE);
                        }else if (family.getUserStatus().equalsIgnoreCase("not-member")) {
                            eventCalendar.setVisibility(View.GONE);
                        }
                        eventCalendar.setVisibility(View.VISIBLE);
                    }else {
                        eventCalendar.setVisibility(View.INVISIBLE);
                    }
                    eventCalendar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent calendarIntent = new Intent(getContext(), CalendarActivity.class);
                            calendarIntent.putExtra(IDENTIFIER, FAMILY);
                            calendarIntent.putExtra(DATA, family);
                            startActivity(calendarIntent);
                        }
                    });
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
            /**End of calendar visibility setting**/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
/** R.id.familySettings and R.id.imgMore excluded from onClick **/
    @OnClick({R.id.btn_paynow, R.id.btn_cancel, R.id.member_view, R.id.txt_member, R.id.editFamily, R.id.coverEdit, R.id.goBack, R.id.backgroundCover, R.id.familiesContainer, R.id.eventsContainer, R.id.knownMembersContainer, R.id.familyName, R.id.familyLogo, R.id.joinFamily,R.id.joinThisFamily,R.id.acceptInvitation, R.id.addFamilyComponent,R.id.acceptInvite,R.id.rejectInvite,R.id.imgMoreOptions,R.id.invitationToFamily})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.btn_paynow:
                if (!etxt_due_paid.getText().toString().equals("")) {
                    int amt = Integer.parseInt(etxt_due_paid.getText().toString());
                    if (amt > 0) {
                        openBottomSheetMore();
                        String acc = "";
                        if (family.getStripe_account_id() != null) {
                            acc = family.getStripe_account_id();
                        }
                        startActivityForResult(new Intent(getActivity(), StripeActivity.class)
                                .putExtra("ITEMID", "")
                                .putExtra("TYPE", "membership")
                                .putExtra("CID", family.getGroup_map_id())
                                .putExtra("RID", family.getGroup_map_id())
                                .putExtra("ID", family.getId().toString())
                                .putExtra("ACC", acc)
                                .putExtra("AMT", etxt_due_paid.getText().toString()), STRIPE_REQUEST_CODE);

                    }
                }

                break;
            case R.id.editFamily:
            case R.id.familyLogo:
                if (family.getUserStatus() == null)
                    return;
                Intent familyLogoEditIntent = new Intent(getContext(), ImageChangerActivity.class);
                familyLogoEditIntent.putExtra(DATA, family);
                familyLogoEditIntent.putExtra("logo",family.getLogo());
                familyLogoEditIntent.putExtra(IDENTIFIER, family.getUserStatus().equalsIgnoreCase("admin"));
                familyLogoEditIntent.putExtra(TYPE, FAMILY_LOGO);
                startActivityForResult(familyLogoEditIntent, ImageChangerActivity.REQUEST_CODE);
                break;
            case R.id.coverEdit:
            case R.id.backgroundCover:
                if (family.getUserStatus() == null)
                    return;
                Intent familyCoverEditIntent = new Intent(getContext(), ImageChangerActivity.class);
                familyCoverEditIntent.putExtra(DATA, family);
                familyCoverEditIntent.putExtra(IDENTIFIER, family.getUserStatus().equalsIgnoreCase("admin"));
                familyCoverEditIntent.putExtra(TYPE, FAMILY_COVER);
                startActivityForResult(familyCoverEditIntent, ImageChangerActivity.REQUEST_CODE);
                break;
           /* case R.id.familySettings:
                mListener.loadFamilySettings(family);
                break;*/
            case R.id.goBack:
                goBack();
                break;
            case R.id.eventsContainer:
                if (!family.isAnonymous()) {
                    myFamiliesDetailsViewPager.setCurrentItem(0);
                } else if (family.getPostVisibilty() != null && family.getPostVisibilty().equalsIgnoreCase(Constants.FamilySettings.PostVisibility.PUBLIC)) {
                    myFamiliesDetailsViewPager.setCurrentItem(0);
                } else
                    Toast.makeText(getContext(), "You don't have authorization to view posts", Toast.LENGTH_SHORT).show();
                break;
            case R.id.familiesContainer:
                if (!family.isAnonymous() ) {
                    getFamilyDetails();
                    /*int memtab = Objects.requireNonNull(myFamiliesDetailsViewPager.getAdapter()).getCount() - 1;
                    myFamiliesDetailsViewPager.setCurrentItem(memtab);*/
                    /**09/09/21**/
                    //refreshFamilyDetails(family);
                    fragment = FamilySubscriptionUpdatedFragment.newInstance(family);
                    ((FamilyDashboardActivity) getActivity()).replaceFragment(fragment);
                    ((FamilyDashboardActivity) getActivity()).loadCoverPic(family);
                    ((FamilyDashboardActivity) getActivity()).setupToolbarForMembersWithoutSearch(family);

                } else {
                    /*
                    * megha
                    * added condition for solving the issue that shows the warning even if the member clicks the family details*/
                    if (family.memberNotAnonymous()){
                        Toast.makeText(getContext(), "Please wait! Family is loading", Toast.LENGTH_SHORT).show();

                    }else{
                        Toast.makeText(getContext(), "You need to be a member to view this family", Toast.LENGTH_SHORT).show();
                    }
                }

                break;
            case R.id.knownMembersContainer:
                Intent mutualConnectionsIntent = new Intent(getActivity(), UserConnectionsActivity.class);
                mutualConnectionsIntent.putExtra(TYPE, UserConnectionsActivity.FAMILY_CONNECTIONS);
                mutualConnectionsIntent.putExtra(ID, SharedPref.getUserRegistration().getId());
                mutualConnectionsIntent.putExtra(Constants.Bundle.FAMILY_ID, family.getId().toString());
                startActivity(mutualConnectionsIntent);
                break;
            case R.id.familyName:
                //mListener.loadFileUploading(family);
                break;
            case R.id.btn_cancel:
            case R.id.txt_member:
            case R.id.member_view:
                openBottomSheetMore();
                break;
            case R.id.acceptInvitation:
            case R.id.acceptInvite:
                onInvitationAccept();
                break;
            case R.id.rejectInvite:
                onInvitationReject();
                break;
            case R.id.joinThisFamily:
            case R.id.joinFamily:
                if (family.getJoinedStatus() != null && family.getJoinedStatus().equalsIgnoreCase("pending")) {
                    Toast.makeText(getContext(), "You have already requested to join this family", Toast.LENGTH_SHORT).show();
                    return;
                } else if (family.getMemberJoining() != null && family.getMemberJoining().equalsIgnoreCase("1")) {
                    Toast.makeText(getContext(), "You can not join a private family", Toast.LENGTH_SHORT).show();
                    return;
                }else if (family.isAnonymous()) {
                    requestJoinFamily();
                }
                break;
            case R.id.addFamilyComponent:
                try {
                    Fragment selectedFragment = familyDetailsPagerAdapter.getItem(myFamiliesDetailsViewPager.getCurrentItem());
                    if (selectedFragment instanceof FamilyPostFeedFragment) {
                        FamilyPostFeedFragment familyPostFeedFragment = ((FamilyPostFeedFragment) selectedFragment);
                        familyPostFeedFragment.createNewPost();
                    } else if (selectedFragment instanceof AnnouncementListingFragment) {
                        AnnouncementListingFragment announcementListingFragment = ((AnnouncementListingFragment) selectedFragment);
                        announcementListingFragment.createNewAnnouncement();
                    } else if (selectedFragment instanceof FamilyEventFragment) {
                        FamilyEventFragment familyEventFragment = ((FamilyEventFragment) selectedFragment);

                        familyEventFragment.createNewEvent();
                    } else if (selectedFragment instanceof AlbumFragment) {
                        AlbumFragment albumFragment = ((AlbumFragment) selectedFragment);
                        albumFragment.createNewAlbum();
                    } else if (selectedFragment instanceof FoldersFragment) {
                        FoldersFragment foldersFragment = ((FoldersFragment) selectedFragment);
                        foldersFragment.createNewFolder();
                    } else if (selectedFragment instanceof LinkingFragment) {
                        LinkingFragment linkingFragment = ((LinkingFragment) selectedFragment);
                        linkingFragment.createNewFamilyLink();
                    } else if (selectedFragment instanceof FamilySubscriptionFragment) {
                        FamilySubscriptionFragment familySubscriptionFragment = ((FamilySubscriptionFragment) selectedFragment);
                        familySubscriptionFragment.createNewMember();
                    } else if (selectedFragment instanceof FamilyNeedsListingFragment) {
                        FamilyNeedsListingFragment familyNeedsListingFragment = ((FamilyNeedsListingFragment) selectedFragment);
                        familyNeedsListingFragment.createNewNeed();
                    } else if (selectedFragment instanceof MembershipFragment) {
                        MembershipFragment membershipFragment = ((MembershipFragment) selectedFragment);
                        membershipFragment.createMemberShipType();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.imgMoreOptions :popupWindow();
            break;
            case R.id.invitationToFamily :  if (family.getUserStatus().equalsIgnoreCase("admin")) {
                Intent intent = new Intent(getContext(), FamilyAddMemberActivity.class);
                intent.putExtra(Constants.Bundle.DATA, family);
                startActivityForResult(intent, FamilyAddMemberActivity.REQUEST_CODE);
            }
            else{
                Toast.makeText(getContext(), "Only admin can invite", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void goBack() {
        if (notificationType.equalsIgnoreCase("0")) {
            if(toCreatedFamily){
                Intent intent = new Intent(getContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                requireActivity().supportFinishAfterTransition();
            }
            requireActivity().supportFinishAfterTransition();
        } else if (notificationType.equalsIgnoreCase(GLOBAL_SEARCH)) {
            Intent returnIntent = new Intent();
            if (editStatus)
                getActivity().setResult(Activity.RESULT_OK, returnIntent);
            else if(isJoin || toCreatedFamily){

                if(isJoin){
                    SharedPref.setUserHasFamily(true);
                }
                Intent intent = new Intent(getContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                requireActivity().supportFinishAfterTransition();
            }
            else
                getActivity().setResult(Activity.RESULT_CANCELED, returnIntent);
            requireActivity().finish();
            getActivity().supportFinishAfterTransition();
        } else {
            Intent intent = new Intent(getContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().supportFinishAfterTransition();
        }
    }

    private void initListeners() {
        goToSubscription.setOnClickListener(view -> {
            // openBottomSheet();
        });

        txtFolders.setOnClickListener(v -> {
            mListener.loadFileUploading(family);
        });
        //  cancel.setOnClickListener(view -> openBottomSheet());
        if (family.getFollowing() != null && family.getFollowing())
            unfollow.setText(R.string.un_follow);
        else
            unfollow.setText(R.string.follow);
        unfollow.setOnClickListener(view -> {
            //  openBottomSheet();
            if (family.getFollowing() != null && family.getFollowing()) {
                unFollowtheFamily();
            } else {
                followtheFamily();
            }
        });


        if (family.getLinkFamily().contains("14")) {

            linkFamilies.setVisibility(View.GONE);

        }

        linkFamilies.setOnClickListener(v -> {
            //  openBottomSheet();
            Intent intent = new Intent(getContext(), LinkedFamilyActivity.class);
            intent.putExtra(DATA, family);
            startActivity(intent);
        });

    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        appBar.setExpanded(false);
        myFamiliesDetailsViewPager.setCurrentItem(tab.getPosition());
        Fragment f = familyDetailsPagerAdapter.getItem(myFamiliesDetailsViewPager.getCurrentItem());
        if(f instanceof FamilyEventFragment){
            if (family.getUserStatus().equalsIgnoreCase("admin")) {
                eventCalendar.setVisibility(View.VISIBLE);
            }else if (family.getUserStatus().equalsIgnoreCase("not-member")) {
                eventCalendar.setVisibility(View.GONE);
            }
            eventCalendar.setVisibility(View.VISIBLE);
        }
        eventCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent calendarIntent = new Intent(getContext(), CalendarActivity.class);
                calendarIntent.putExtra(IDENTIFIER, FAMILY);
                calendarIntent.putExtra(DATA, family);
                startActivity(calendarIntent);
            }
        });
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        Fragment f = familyDetailsPagerAdapter.getItem(myFamiliesDetailsViewPager.getCurrentItem());
        if(f instanceof FamilyEventFragment){
            eventCalendar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
    }

    public void refreshFamilyDetails(Family family) {
        this.family = family;
        getFamilyDetails();

        FamilyPostFeedFragment familyPostFeedFragment=new FamilyPostFeedFragment();

        if (family.getIsRated()==null){
           family.setIsRated(false);
        }else{
            familyPostFeedFragment.setFamilyData(family.getIsRated());
        }

    }
    /**Method for updating members count inside dashborad after a member got removed by admin(FamilyMembersFragment)
     * and also a member got accepted(FamilyRequestFragment-invoked inside FamilySubscriptionUpdatedFragment)
     ***/
    public void setUpdatedMemberCount(int count){
        membersCount.setText(String.valueOf(count));
        //getFamilyDetails();
    }
    public void getFamilyDetails() {
//        mListener.showProgressDialog();
        showProgressBar();
        JsonObject jsonObject = new JsonObject();
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getContext());
        jsonObject.addProperty("group_id", family.getId().toString());
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        apiServiceProvider.getFamilyDetailsByID(jsonObject, null, this);
    }

    public void getFamilyDetailsAsNew() {
        showProgressBar();
        JsonObject jsonObject = new JsonObject();
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getContext());
        jsonObject.addProperty("group_id", family.getId().toString());
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        apiServiceProvider.getFamilyDetailsByID(jsonObject, new ApiCallbackParams(), this);
    }

    private void showProgressBar() {
        if (progressFamily != null) {
            progressFamily.setVisibility(View.VISIBLE);
        }

    }

    private void hideProgressBar() {
        if (progressFamily != null) {
            progressFamily.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {
        if (mListener != null)
            mListener.hideProgressDialog();
        switch (apiFlag) {
            case Constants.ApiFlags.FOLLOW:
                Toast.makeText(getActivity(), family.getGroupName() + " followed", Toast.LENGTH_SHORT).show();
                getFamilyDetails();
                break;
            case Constants.ApiFlags.UNFOLLOW:
                getFamilyDetails();
                Toast.makeText(getActivity(), family.getGroupName() + " unfollowed", Toast.LENGTH_SHORT).show();
                break;

            case Constants.ApiFlags.FETCH_FAMILY_FOR_LINKING:
                linkfamilyLists.clear();
                linkfamilyLists.addAll(FamilyParser.parseLinkFamilyList(responseBodyString));
                linkFamilyAdapter.notifyDataSetChanged();
                break;
            default:
                hideProgressBar();
                try {
                    if (apiCallbackParams != null) {
                        familyDetailsPagerAdapter = null;
                    }
                    if (FamilyParser.parseLinkedFamilies(responseBodyString) != null && FamilyParser.parseLinkedFamilies(responseBodyString).size() == 0) {
                        SweetAlertDialog contentNotFoundDialog = Utilities.getContentNotFoundDialog(getContext());
                        contentNotFoundDialog.setConfirmClickListener(sweetAlertDialog -> {
                            requireActivity().finish();
                        });
                        contentNotFoundDialog.setCanceledOnTouchOutside(false);
                        contentNotFoundDialog.setCancelable(false);
                        contentNotFoundDialog.show();
                        Utilities.addPositiveButtonMargin(contentNotFoundDialog);
                        return;
                    }
                    family = FamilyParser.parseLinkedFamilies(responseBodyString).get(0);
                    /**04-09-2021**/
                    family.setMembersCount(FamilyParser.getFamilyMembers(responseBodyString));
                    family.setPostCount(FamilyParser.getPostCount(responseBodyString));
                    //fillFamilyBasicDetails();
                    try {
                        JSONArray array = new JSONObject(responseBodyString).getJSONArray("data");
                        family.setRequestCount(array.getJSONObject(0).getString("pending_request_count"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    setMemberShipdata();

                    if (familyDetailsPagerAdapter == null) {
                        initializeTabs(family);
                        //popupWindow(family);
                        initRestrictions();
                    } else {
                        initRestrictions();
                    }
                    fillFamilyBasicDetails();
                    updateFragment(family);
                    membersCount.setText(FamilyParser.getFamilyMembers(responseBodyString));
                    eventsCount.setText(FamilyParser.getPostCount(responseBodyString));
                    knownMembersCount.setText(FamilyParser.getKnownConnections(responseBodyString));
                    initListeners();
                } catch (JsonParseException | NullPointerException | ArrayIndexOutOfBoundsException e) {
                    e.printStackTrace();
                    if (mListener != null)
                        mListener.showErrorDialog("Something went wrong please try again!!");
                }
        }
    }

    @Override
    public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
        if (mListener != null)
            mListener.hideProgressDialog();

        if (apiFlag == GET_FAMILY_DETAILS) {
            hideProgressBar();

            try {
                if (errorData.getCode() == 500) {
                    SweetAlertDialog contentNotFoundDialog = Utilities.getContentNotFoundDialog(getContext());
                    contentNotFoundDialog.setConfirmClickListener(sweetAlertDialog -> {
                        getActivity().finish();
                    });
                    contentNotFoundDialog.setCanceledOnTouchOutside(false);
                    contentNotFoundDialog.setCancelable(false);
                    contentNotFoundDialog.show();
                    Utilities.addPositiveButtonMargin(contentNotFoundDialog);
                } else
                    networkError();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void updateFamily(int TYPE, Family family) {
        FamilyEditDialogFragment.newInstance(TYPE, family).show(getChildFragmentManager(), "FamilyEditDialogFragment");
    }

    @Override
    public void onFamilyAddComponentVisible(int type) {
        try {
            Fragment selectedFragment = familyDetailsPagerAdapter.getItem(myFamiliesDetailsViewPager.getCurrentItem());
            switch (type) {
                case TypeFamilySubscriptionFragment:
                /*if (selectedFragment instanceof FamilySubscriptionFragment) {
                    FamilySubscriptionFragment familySubscriptionFragment = ((FamilySubscriptionFragment) selectedFragment);
                    if (familySubscriptionFragment != null)
                        addFamilyComponent.setVisibility(View.VISIBLE);
                }*/
                    imgCreateFeed.setImageResource(R.drawable.add);
                    addFamilyComponent.setVisibility(View.VISIBLE);
                    break;
                case TypeAlbumFragment:
                    if (selectedFragment instanceof AlbumFragment) {
                        AlbumFragment albumFragment = ((AlbumFragment) selectedFragment);
                        imgCreateFeed.setImageResource(R.drawable.add);
                        addFamilyComponent.setVisibility(View.VISIBLE);
                    }
                    break;
                case TypeAboutUsFragment:
                    imgCreateFeed.setImageResource(R.drawable.add);
                    addFamilyComponent.setVisibility(View.VISIBLE);
                    break;
                case TypeFamilyEventFragment:
                    if (selectedFragment instanceof FamilyEventFragment) {
                        imgCreateFeed.setImageResource(R.drawable.add);
                        addFamilyComponent.setVisibility(View.VISIBLE);
                    }
                    break;
                case TypeFoldersFragment:
                    if (selectedFragment instanceof FoldersFragment) {
                        imgCreateFeed.setImageResource(R.drawable.add);
                        addFamilyComponent.setVisibility(View.VISIBLE);
                    }
                    break;
                case TypeFamilyNeedsListingFragment:
                    if (selectedFragment instanceof FamilyNeedsListingFragment) {
                        imgCreateFeed.setImageResource(R.drawable.add);
                        addFamilyComponent.setVisibility(View.VISIBLE);
                    }
                    break;
                case TypeAnnouncementListingFragment:
                    if (selectedFragment instanceof AnnouncementListingFragment) {
                        imgCreateFeed.setImageResource(R.drawable.add);
                        addFamilyComponent.setVisibility(View.VISIBLE);
                    }
                    break;
                case TypeFamilyPostFeedFragment:
                    if (selectedFragment instanceof FamilyPostFeedFragment) {
                        if (sheetBehaviorMore.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                            /** For handle the img src when uploading and comp **/

                            addFamilyComponent.setVisibility(View.VISIBLE);
                        }


                    }
                    break;
                case TypeLinkingFragment:
                case TypeLinkedFamilyFragment:
                case TypeFamilyRequestsFragment:
                    if (selectedFragment instanceof LinkingFragment) {
                        imgCreateFeed.setImageResource(R.drawable.add);
                        addFamilyComponent.setVisibility(View.VISIBLE);
                    }
                    break;
                case TypeFamilyMembership:
                    imgCreateFeed.setImageResource(R.drawable.add);
                    addFamilyComponent.setVisibility(View.VISIBLE);
                    break;
                default:
                    imgCreateFeed.setImageResource(R.drawable.add);
                    addFamilyComponent.setVisibility(View.VISIBLE);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFamilyAddComponentHidden(int type) {
        try {
            Fragment selectedFragment = familyDetailsPagerAdapter.getItem(myFamiliesDetailsViewPager.getCurrentItem());
            switch (type) {
                case TypeFamilySubscriptionFragment:
                    addFamilyComponent.setVisibility(View.INVISIBLE);
                    break;
                case TypeAlbumFragment:
                    if (selectedFragment instanceof AlbumFragment) {
                        addFamilyComponent.setVisibility(View.INVISIBLE);
                    }
                    break;
                case TypeAboutUsFragment:
                    addFamilyComponent.setVisibility(View.INVISIBLE);
                    break;
                case TypeFamilyEventFragment:
                    if (selectedFragment instanceof FamilyEventFragment) {
                        addFamilyComponent.setVisibility(View.INVISIBLE);
                    }
                    break;
                case TypeFoldersFragment:
                    if (selectedFragment instanceof FoldersFragment) {
                        addFamilyComponent.setVisibility(View.INVISIBLE);
                    }
                    break;
                case TypeFamilyNeedsListingFragment:
                    if (selectedFragment instanceof FamilyNeedsListingFragment) {
                        addFamilyComponent.setVisibility(View.INVISIBLE);
                    }
                    break;
                case TypeAnnouncementListingFragment:
                    if (selectedFragment instanceof AnnouncementListingFragment) {
                        addFamilyComponent.setVisibility(View.INVISIBLE);
                    }
                    break;
                case TypeFamilyPostFeedFragment:
                    if (selectedFragment instanceof FamilyPostFeedFragment) {
                        addFamilyComponent.setVisibility(View.INVISIBLE);
                    }
                    break;
                case TypeLinkingFragment:
                case TypeLinkedFamilyFragment:
                case TypeFamilyRequestsFragment:
                    if (selectedFragment instanceof LinkingFragment) {
                        addFamilyComponent.setVisibility(View.INVISIBLE);
                    }
                    break;
                default:
                    addFamilyComponent.setVisibility(View.INVISIBLE);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void updateFragment(Family family) {
        if (family == null) return;
        collapsingToolBarBehaviour();
        LinkingFragment linkedFamilyFragment = familyDetailsPagerAdapter.getInstance(LinkingFragment.class);
        if (linkedFamilyFragment != null)
            linkedFamilyFragment.updateFamily(family);
        FamilySubscriptionFragment familySubscriptionFragment = familyDetailsPagerAdapter.getInstance(FamilySubscriptionFragment.class);
        if (familySubscriptionFragment != null)
            familySubscriptionFragment.fillDetails(family);
        AlbumFragment albumFragment = familyDetailsPagerAdapter.getInstance(AlbumFragment.class);
        if (albumFragment != null)
            albumFragment.updateAdminStatus(family.isAdmin(), family.isAdmin() || family.canCreatePost(), family.isAdmin(), SharedPref.getUserRegistration().getId());
       /* AboutUsFragment aboutUsFragment = familyDetailsPagerAdapter.getInstance(AboutUsFragment.class);
        if (aboutUsFragment != null)
            aboutUsFragment.fillDetails(family);*/
        FamilyEventFragment familyEventFragment = familyDetailsPagerAdapter.getInstance(FamilyEventFragment.class);
        if (familyEventFragment != null)
            familyEventFragment.updateFamily(family);
        FoldersFragment foldersFragment = familyDetailsPagerAdapter.getInstance(FoldersFragment.class);
        if (foldersFragment != null)
            foldersFragment.updateAdminStatus(family.isAdmin(), family.isAdmin() || family.canCreatePost(), family.isAdmin(), SharedPref.getUserRegistration().getId());
        FamilyPostFeedFragment familyPostFeedFragment = familyDetailsPagerAdapter.getInstance(FamilyPostFeedFragment.class);
        if (familyPostFeedFragment != null)
            familyPostFeedFragment.updateFamily(family);
        AnnouncementListingFragment announcementListingFragment = familyDetailsPagerAdapter.getInstance(AnnouncementListingFragment.class);
        if (announcementListingFragment != null)
            announcementListingFragment.updateFamily(family);


        if (family.getFollowing() != null && family.getFollowing())
            unfollow.setText(R.string.un_follow);
        else
            unfollow.setText(R.string.follow);

        if (family.getLinkFamily().contains("14"))
            linkFamilies.setVisibility(View.GONE);
    }

    private void fillFamilyBasicDetails() {
        familyName.setText(family.getGroupName());
        familyLocation.setText(family.getBaseRegion());
        if (family.getCreatedByName() != null && family.getGroupType() != null){
            familyDescription.setVisibility(View.VISIBLE);
            familyDescription.setText(family.getGroupCategory() + ", By " + family.getCreatedByName());
            Log.i("test","data"+family.getGroupCategory());
        }

        else if (family.getCreatedByName() == null && family.getGroupType() == null)
            familyDescription.setVisibility(View.GONE);
        else if (family.getCreatedByName() != null)
            familyDescription.setText("By " + family.getCreatedByName());
        else
            familyDescription.setText(family.getGroupCategory());
        if (family.getLogo() != null) {
            Glide.with(FamilyDashboardFragment.this.requireContext())
                    .load(S3_DEV_IMAGE_URL_SQUARE + IMAGE_BASE_URL + LOGO + family.getLogo())
                    .apply(Utilities.getCurvedRequestOptions())
                    .placeholder(R.drawable.family_logo)
                    .transition(withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                    .into(familyLogo);
        }
        if (family.getCoverPic() != null) {
            Glide.with(FamilyDashboardFragment.this.requireContext())
                    .load(S3_DEV_IMAGE_URL_COVER + IMAGE_BASE_URL + COVER_PIC + family.getCoverPic())
                    .placeholder(R.drawable.family_dashboard_background)
                    .transition(DrawableTransitionOptions.withCrossFade(1000))
                    .into(backgroundCover);
        }
    }

    @Override
    public void onFamilyEditCompleted(int TYPE, Family family) {
        this.family = family;
        mListener.hideProgressDialog();
        updateFragment(family);
        fillFamilyBasicDetails();
    }

    @Override
    public void hideProgressDialog() {
        mListener.hideProgressDialog();
    }

    @Override
    public void showProgressDialog() {
        mListener.showProgressDialog();
    }

    @Override
    public void showErrorDialog(String errorMessage) {
        mListener.showErrorDialog(errorMessage);
    }

    @Override
    public void onFamilyCreated(Family family) {
        getActivity().onBackPressed();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ImageChangerActivity.REQUEST_CODE && resultCode == Activity.RESULT_OK || requestCode == STRIPE_REQUEST_CODE) {
            refreshFamilyDetails(family);
        }else if (requestCode == FamilyAddMemberActivity.REQUEST_CODE) {
            mListener.onFamilyUpdated(false);
        }
    }
  public void   makeInviteMemberVisible(){
        invitationToFamily.setVisibility(View.VISIBLE);
  }
   public void makeInviteMemberInvisible(){
       invitationToFamily.setVisibility(View.GONE);
   }

    public void onInvitationAccept() {
        mListener.showProgressDialog();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", family.getReqId());
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        jsonObject.addProperty("group_id", family.getGroupId().toString());
        jsonObject.addProperty("from_id", family.getFromId());
        jsonObject.addProperty("status", "accepted");
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getContext());
        apiServiceProvider.respondToFamilyInvitation(jsonObject, null, new RetrofitListener() {
            @Override
            public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {
                mListener.hideProgressDialog();
                try {
                    editStatus = true;
                    String status = new JSONObject(responseBodyString).getJSONObject("data").getString("status");
                    if (status.equalsIgnoreCase("accepted")) {
                        acceptRejectLayout.setVisibility( View.GONE );
                        //11-11-21
                        acceptInvitation.setVisibility(View.GONE);
                        getFamilyDetailsAsNew();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
                mListener.showErrorDialog(Constants.SOMETHING_WRONG);
            }
        });

    }

    public void onInvitationReject() {
        mListener.showProgressDialog();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", family.getReqId());
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        jsonObject.addProperty("group_id", family.getGroupId().toString());
        jsonObject.addProperty("from_id", family.getFromId());
        jsonObject.addProperty("status", "rejected");
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getContext());
        apiServiceProvider.respondToFamilyInvitation(jsonObject, null, new RetrofitListener() {

            @Override
            public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {
                mListener.hideProgressDialog();
                acceptRejectLayout.setVisibility( View.GONE );
                //11-11-21
                acceptInvitation.setVisibility(View.GONE);
                getFamilyDetailsAsNew();
            }
            @Override
            public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
                mListener.showErrorDialog(Constants.SOMETHING_WRONG);
            }
        });
    }


    private void requestJoinFamily() {
        mListener.showProgressDialog();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        jsonObject.addProperty("group_id", "" + family.getId());
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getActivity());
        apiServiceProvider.joinFamily(jsonObject, null, new RetrofitListener() {
            @Override
            public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {
                mListener.hideProgressDialog();
                try {
                    editStatus = true;
                    String status = new JSONObject(responseBodyString).getJSONObject("data").getString("status");
                    if (status.equalsIgnoreCase("Pending")) {
                        //   if ("invite".equalsIgnoreCase(getType()))

                        joinFamily.setText("Pending");
                        //11-11-21
                        joinThisFamily.setText("Pending");
                        family.setJoinedStatus("pending");
                    } else {
                        joinFamily.setText("Joined");
                        //11-11-21
                        joinThisFamily.setText("Joined");
                        getFamilyDetailsAsNew();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
                    if(errorData.getCode()==500){
                        mListener.showErrorDialog("You are blocked in this family");
                    }
                    else{
                        mListener.showErrorDialog(Constants.SOMETHING_WRONG);
                    }
            }
        });
    }

    private void collapsingToolBarBehaviour() {
        appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    if (family != null && family.getGroupName() != null)
                        toolBarTitle.setText(family.getGroupName());
                    isShow = true;
                    hide();
                } else if (isShow) {
                    toolBarTitle.setText("");
                    isShow = false;
                    /** method visible() invokation commentes as it causes crash issue on button(Done) click from settings**/
                   // visible();
                }
            }
        });
    }


    private void hide() {
        try {
            Fragment selectedFragment = familyDetailsPagerAdapter.getItem(myFamiliesDetailsViewPager.getCurrentItem());
            if (selectedFragment instanceof FamilyPostFeedFragment) {
                FamilyPostFeedFragment familyPostFeedFragment = ((FamilyPostFeedFragment) selectedFragment);
                //familyPostFeedFragment.stickyPostHide();
            }
        } catch (Exception e) {
        }
    }

    private void visible() {

        new Handler().postDelayed(() -> requireActivity().runOnUiThread(() -> {

            Fragment selectedFragment = familyDetailsPagerAdapter.getItem(myFamiliesDetailsViewPager.getCurrentItem());
            if (selectedFragment instanceof FamilyPostFeedFragment) {
                FamilyPostFeedFragment familyPostFeedFragment = ((FamilyPostFeedFragment) selectedFragment);
                // familyPostFeedFragment.stickyPostShow();
            }
        }), 200);
    }

    private void networkError() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Uh oh! Check your internet connection and retry.")
                .setCancelable(false)
                .setPositiveButton("Retry", (dialog, which) -> getFamilyDetails()).setNegativeButton("Cancel", (dialog, which) -> {
            requireActivity().finish();
            dialog.dismiss();
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

    private void dataSetInBottomSheet() {
        if (family.getMembership_fees() != null && family.getMembership_total_payed_amount() != null) {
            int due = (Integer.parseInt(family.getMembership_fees()) -
                    Integer.parseInt(family.getMembership_total_payed_amount()));
            if (due > 0)
                etxt_due_paid.setText(due + "");
            else etxt_due_paid.setText("0");
        }

        txt_paid_fees.setText(": " + family.getMembership_total_payed_amount());
        etxt_due_paid.requestFocus();
        etxt_due_paid.setSelection(etxt_due_paid.getText().length());
        txt_mtype.setText(": " + family.getMembership_type());
        txt_mduration.setText(": " + family.getMembership_period_type());
        txt_sdate.setText(": " + getFormattedCreatedAt(Long.parseLong(family.getMembership_from())));
        txt_tilldate.setText(": " + getFormattedCreatedAt(Long.parseLong(family.getMembership_to())));
        txt_fees.setText(": " + family.getMembership_fees());
        if (family.getMembership_payment_notes() != null && !family.getMembership_payment_notes().equals("")) {
            info_view.setVisibility(View.VISIBLE);
            txt_note.setText(family.getMembership_payment_notes());
        }

        if ("Pending".equals(family.getMembership_payment_status()) || "Partial".equals(family.getMembership_payment_status())) {
            btn_paynow.setVisibility(View.VISIBLE);
            view_due.setVisibility(View.VISIBLE);
        } else {
            view_due.setVisibility(View.GONE);
            btn_paynow.setVisibility(View.GONE);
        }
    }

    private String getFormattedCreatedAt(Long values) {
        Long value = values;
        if (value != null && value > 100) {
            value = TimeUnit.SECONDS.toMillis(value);
            DateTime dateTime = new DateTime(value);
            DateTimeFormatter dtfOut = DateTimeFormat.forPattern("MMM dd yyyy");
            return dtfOut.print(dateTime);
        }
        return "";
    }

    /**
     * Devika
     * Action to be taken place when clicking on more options ImageView placed next to the TabLayout
     **/

    @Override
    public void onClick(View view) {
        if(view == imgMore){
            popupWindow();
            onResume();
        }
    }
    /**
     * @author Devika
     *popup window loading on click of more options icon **/
    private void popupWindow(){
        PopupMenu popupMenu = new PopupMenu(getActivity(), imgMore);
        popupMenu.getMenuInflater().inflate(R.menu.menu_family_more_options, popupMenu.getMenu());
        try {
            //familyDetailsPagerAdapter = new FamilyDashboardTabAdapter(getChildFragmentManager());
            if (family.getUserStatus().equalsIgnoreCase("not-member")) {//Not Member
                if (family.isPublic()) {//Family is Public
                    popupMenu.getMenu().findItem(R.id.announcements).setVisible(true);
                    popupMenu.getMenu().findItem(R.id.aboutUs).setVisible(true);
                    popupMenu.getMenu().findItem(R.id.members).setVisible(false);
                    popupMenu.getMenu().findItem(R.id.linkedFamilies).setVisible(false);
                    popupMenu.getMenu().findItem(R.id.documents).setVisible(false);
                    popupMenu.getMenu().findItem(R.id.gallery).setVisible(false);
                    popupMenu.getMenu().findItem(R.id.settings).setVisible(false);
                } else {//Family is Private
                    popupMenu.getMenu().findItem(R.id.announcements).setVisible(false);
                    popupMenu.getMenu().findItem(R.id.aboutUs).setVisible(true);
                    popupMenu.getMenu().findItem(R.id.members).setVisible(false);
                    popupMenu.getMenu().findItem(R.id.linkedFamilies).setVisible(false);
                    popupMenu.getMenu().findItem(R.id.documents).setVisible(false);
                    popupMenu.getMenu().findItem(R.id.gallery).setVisible(false);
                    popupMenu.getMenu().findItem(R.id.settings).setVisible(false);
                }
            }else {
                if (family.getUserStatus().equalsIgnoreCase("admin") && family.getIs_membership() != null && family.getIs_membership()) {
                 popupMenu.getMenu().findItem(R.id.membership).setVisible(true);
                }else {
                    popupMenu.getMenu().findItem(R.id.membership).setVisible(false);
                }
                popupMenu.getMenu().findItem(R.id.announcements).setVisible(true);
                popupMenu.getMenu().findItem(R.id.aboutUs).setVisible(true);
                popupMenu.getMenu().findItem(R.id.members).setVisible(true);
                popupMenu.getMenu().findItem(R.id.linkedFamilies).setVisible(true);
                popupMenu.getMenu().findItem(R.id.documents).setVisible(true);
                popupMenu.getMenu().findItem(R.id.gallery).setVisible(true);
                popupMenu.getMenu().findItem(R.id.settings).setVisible(true);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.announcements : fragment = AnnouncementListingFragment.newInstance(family,family.canCreateAnnouncement(),family.getId() + "");
                    ((FamilyDashboardActivity) getActivity()).replaceFragment(fragment);
                    ((FamilyDashboardActivity) getActivity()).loadCoverPic(family);
                    break;

                    case R.id.members: fragment = FamilySubscriptionUpdatedFragment.newInstance(family);
                    ((FamilyDashboardActivity) getActivity()).replaceFragment(fragment);
                    ((FamilyDashboardActivity) getActivity()).loadCoverPic(family);
                    ((FamilyDashboardActivity) getActivity()).setupToolbarForMembersWithoutSearch(family);
                    break;

                   case R.id.membership :
                        //popupMenu.getMenu().findItem(R.id.membership).setVisible(true);
                        fragment=MembershipFragment.newInstance(family.getId().toString());
                        ((FamilyDashboardActivity) getActivity()).replaceFragment(fragment);
                        ((FamilyDashboardActivity) getActivity()).loadCoverPic(family);
                    break;
                case R.id.linkedFamilies :
                    fragment= LinkingUpdatedFragment.newInstance(family);
                    ((FamilyDashboardActivity) getActivity()).replaceFragment(fragment);
                    ((FamilyDashboardActivity) getActivity()).loadCoverPic(family);
                    ((FamilyDashboardActivity) getActivity()).setupToolbarForMembersWithoutSearch(family);
                    break;
                case R.id.documents :
/** added isAdmin to the instance for getting the"admin only" condition in family settings**/
                    fragment =  FoldersFragment.newInstance(family.getId().toString(),Constants.FileUpload.TYPE_FAMILY,family.isAdmin(),family.canCreatePost());
                    ((FamilyDashboardActivity) getActivity()).replaceFragment(fragment);
                    ((FamilyDashboardActivity) getActivity()).loadCoverPic(family);
                    break;
                case R.id.gallery :
                    fragment=AlbumFragment.newInstance(Constants.FileUpload.TYPE_FAMILY, family.getId().toString(), family.isAdmin(),"fromDashboard",family.canCreatePost());
                    ((FamilyDashboardActivity) getActivity()).replaceFragment(fragment);
                    ((FamilyDashboardActivity) getActivity()).loadCoverPic(family);
                    break;
                case R.id.aboutUs :
                    fragment = AboutUsFragment.newInstance(family,true,"fromGrid");
                    ((FamilyDashboardActivity)getActivity()).loadFragment(fragment);
                    ((FamilyDashboardActivity) getActivity()).setupToolbarAndImage(family);
                    break;
                case R.id.settings : mListener.loadFamilySettings(family);
                    break;
            }
            return true;
        });
        popupMenu.show();
    }

    /** Implementing searchview
     *Devika
     **/

    private void initListener() {
        imgSearch.setOnClickListener(view -> {
            showCircularReveal(constraintSearch);
            showKeyboard();
        });
        imageBack.setOnClickListener(view -> {
            hideCircularReveal(constraintSearch);
            searchAlbumQuery.setText("");
            onSearchQueryListener(EditorInfo.IME_ACTION_SEARCH);
        });
    }
    private void showKeyboard() {
        if (searchAlbumQuery.requestFocus()) {
            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(searchAlbumQuery, InputMethodManager.SHOW_IMPLICIT);
            }
        }
    }

    @OnEditorAction(R.id.searchInfo)
    protected boolean onSearchQueryListener(int actionId) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            search();
            try {
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchAlbumQuery.getWindowToken(), 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }
    /**
     * Method to start searching inside currently visible fragment based on the selected tab
     **/
    private void search(){
        Fragment f = familyDetailsPagerAdapter.getItem(myFamiliesDetailsViewPager.getCurrentItem());
        String s = searchAlbumQuery.getText().toString();
        Log.i("value","value"+s);
        /*FamilyPostFeedFragment feedFragment = ((FamilyPostFeedFragment) familyDetailsPagerAdapter.getRegisteredFragment(0));*/
        if(f instanceof  FamilyPostFeedFragment){
            ((FamilyPostFeedFragment) f).startSearch(searchAlbumQuery.getText().toString());
        }else if(f instanceof  FamilyEventFragment){
            ((FamilyEventFragment) f).startSearch(searchAlbumQuery.getText().toString());
        }else if(f instanceof FamilyNeedsListingFragment){
            ((FamilyNeedsListingFragment) f).startSearch(searchAlbumQuery.getText().toString());
        }
    }
    public void refreshFeed(){
        Fragment f = familyDetailsPagerAdapter.getItem(myFamiliesDetailsViewPager.getCurrentItem());
        if(f instanceof  FamilyPostFeedFragment){
            ((FamilyPostFeedFragment) f).fetchFamilyFeed();
        }
    }
    /**Method to observe the search box status**/
    private void initializeCallbacks() {
        searchAlbumQuery.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0)
                    clearSearch.setVisibility(View.INVISIBLE);
                else clearSearch.setVisibility(View.VISIBLE);
            }
        });
        clearSearch.setOnClickListener(v -> {
            searchAlbumQuery.setText("");
            onSearchQueryListener(EditorInfo.IME_ACTION_SEARCH);
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        //initializeTabs(family);
    }

    @Override
    public void onAttachFragment(@NonNull Fragment childFragment) {
        super.onAttachFragment(childFragment);
    }
    /**23/08/21 **/
    @Override
    public void onBackButtonPressed()
    {
        getActivity().finish();
        getActivity().onBackPressed();
    }


}



