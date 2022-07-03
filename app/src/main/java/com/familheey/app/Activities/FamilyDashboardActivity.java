package com.familheey.app.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.familheey.app.Announcement.AnnouncementListingFragment;
import com.familheey.app.Fragments.Events.AlbumFragment;
import com.familheey.app.Fragments.FamilyCreationLevelAdvancedFragment;
import com.familheey.app.Fragments.FamilyDashboard.LinkingUpdatedFragment;
import com.familheey.app.Fragments.FamilyDashboardFragment;
import com.familheey.app.Fragments.FamilyEditFragment;
import com.familheey.app.Fragments.FamilySettingsFragment;
import com.familheey.app.Fragments.FamilyViewMembers.FamilyMembersFragment;
import com.familheey.app.Fragments.FamilyViewMembers.FamilyRequestsFragment;
import com.familheey.app.Fragments.FamilyViewMembers.FamilySubscriptionFragment;
import com.familheey.app.Fragments.FamilyViewMembers.FamilySubscriptionUpdatedFragment;
import com.familheey.app.Fragments.FoldersFragment;
import com.familheey.app.Fragments.LinkedFamilyFragment;
import com.familheey.app.Interfaces.FamilyDashboardInteractor;
import com.familheey.app.Interfaces.FamilyDashboardListener;
import com.familheey.app.Interfaces.ProgressListener;
import com.familheey.app.Models.Response.Family;
import com.familheey.app.R;
import com.familheey.app.Utilities.Constants;
import com.familheey.app.Utilities.SharedPref;
import com.familheey.app.Utilities.Utilities;
import com.familheey.app.membership.MembershipFragment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

import butterknife.OnEditorAction;
import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.familheey.app.Utilities.Constants.ApiPaths.FIREBASE_DATABASE_URL;
import static com.familheey.app.Utilities.Constants.ApiPaths.IMAGE_BASE_URL;
import static com.familheey.app.Utilities.Constants.ApiPaths.S3_DEV_IMAGE_URL_COVER;
import static com.familheey.app.Utilities.Constants.Bundle.DATA;
import static com.familheey.app.Utilities.Constants.Bundle.DESCRIPTION;
import static com.familheey.app.Utilities.Constants.Bundle.IS_CREATED_NOW;
import static com.familheey.app.Utilities.Constants.Bundle.NOTIFICATION_ID;
import static com.familheey.app.Utilities.Constants.Bundle.TITLE;
import static com.familheey.app.Utilities.Constants.Bundle.TYPE;
import static com.familheey.app.Utilities.Constants.Paths.COVER_PIC;
import static com.familheey.app.Utilities.Utilities.hideCircularReveal;
import static com.familheey.app.Utilities.Utilities.showCircularReveal;

public class FamilyDashboardActivity extends AppCompatActivity implements FamilyDashboardListener, FamilyDashboardInteractor, FamilySubscriptionFragment.OnFamilySubscriptionListener, FamilyCreationLevelAdvancedFragment.OnFamilyCreationListener, ProgressListener, FoldersFragment.OnFolderSelectedListener,View.OnClickListener, FamilyRequestsFragment.MemberRequestInterface {

    SweetAlertDialog progressDialog;
    private Family family;
    private String NOTIFICATION = "";
    private String PAY = "";
    private String notificationId = "";
    private DatabaseReference database;
    private Boolean IsJoinFamily=false;
    //search icon
    ConstraintLayout constraintSearch;
    ImageView imgSearch;
    EditText searchAlbumQuery;
    ImageView coverPic;
    ImageView cover_pic;
    Toolbar toolbar;
    Toolbar toolbarWithoutSearch,toolbar_no_search;
    ImageView imageBack;
    ImageView clearSearch;
    CardView cardView;
    TextView membersToolBarTitle;
    ImageView btnGoBack;
    TextView membersCount,RequestsCount;
    //03/08/2021
    LinearLayout layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_dashboard);
        //03/08/2021
       // layout = (LinearLayout) findViewById(R.id.ll_parent_included_fragments);
        coverPic = findViewById(R.id.iv_cover_image);
        cover_pic = findViewById(R.id.cover_image);
        toolbar = findViewById(R.id.toolbar_options);
        toolbarWithoutSearch = findViewById(R.id.toolbar_sublayout);
        toolbar_no_search =findViewById(R.id.toolbar_no_search);
        searchAlbumQuery = findViewById(R.id.searchInfo);
        imageBack = findViewById(R.id.imageBack);
        clearSearch = findViewById(R.id.clearSearch);
        cardView = findViewById(R.id.addComponent);
        //finding ids of views for searchview functionality
        constraintSearch = findViewById(R.id.constraintSearch);
        imgSearch = findViewById(R.id.imgSearch);
        membersToolBarTitle = findViewById(R.id.membersToolBarTitle);
        btnGoBack = findViewById(R.id.btnGoBack);
        if (getIntent() != null && getIntent().hasExtra("PAY")) {
            PAY = getIntent().getStringExtra("PAY");
        }
        if (getIntent() != null && getIntent().hasExtra(TYPE)) {
            Integer eventId = Integer.parseInt(Objects.requireNonNull(getIntent().getStringExtra(DATA)));
            family = new Family();
            family.setGroupId(eventId);
            family.setId(eventId);
            addFragment(FamilyDashboardFragment.newInstance(family, getIntent().getStringExtra(TYPE), PAY,  getIntent().getBooleanExtra( Constants.Bundle.IS_JOIN_FAMILY, false)  ));
        } else {
            family = Objects.requireNonNull(getIntent()).getParcelableExtra(DATA);
            addFragment(FamilyDashboardFragment.newInstance(family, getIntent().hasExtra(IS_CREATED_NOW),getIntent().getBooleanExtra( Constants.Bundle.IS_JOIN_FAMILY, false),getIntent().getBooleanExtra( Constants.Bundle.TO_CREATE_FAMILY, false)));
        }
        if (getIntent() != null && getIntent().hasExtra(Constants.Bundle.NOTIFICATION)) {
            NOTIFICATION = getIntent().getStringExtra(Constants.Bundle.NOTIFICATION);
        }

        // Modified By: Dinu(22/02/2021) For update visible_status="read" in firebase
        if (getIntent().hasExtra(NOTIFICATION_ID)) {
            notificationId=getIntent().getStringExtra(NOTIFICATION_ID);
            database= FirebaseDatabase.getInstance(FIREBASE_DATABASE_URL).getReference(Constants.ApiPaths.FIREBASE_USER_TYPE + SharedPref.getUserRegistration().getId() + "_notification");
            database.child(notificationId).child("visible_status").setValue("read");
        }
    /**
        * perform search inside fragments loaded from options menu in FamilyDashboardFragment
    **/
      searchAlbumQuery.setOnEditorActionListener(new TextView.OnEditorActionListener() {
           @Override
           public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
               onSearchQueryListener(i);
               return true;
           }
       });
      /**floating action button click **/
      cardView.setOnClickListener(this::onClick);
       // setupDashboard();
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
    public boolean addFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.familyDashboardContainer, fragment,"dashboard")
                    .setReorderingAllowed(true)
                    .addToBackStack("dashboard")
                    .commit();
            return true;
        }
        return false;
    }
    /**
     * @author Devika on 16/08/2021
     * Method invokes on clicking back button inside fragments loading from popup menu inside FamilyDashboardFragment
     **/
    public void backAction() {
        findViewById(R.id.included_fragments).setVisibility(View.GONE);
        findViewById(R.id.sublayout_container).setVisibility(View.GONE);
        findViewById(R.id.settings_container).setVisibility(View.GONE);
        findViewById(R.id.advanced_settings_container).setVisibility(View.GONE);
        findViewById(R.id.familyDashboardContainer).setVisibility(View.VISIBLE);
        getFragmentManager().popBackStack("dashboard", 0);
    }
public void gotoDashboard(){
    findViewById(R.id.familyDashboardContainer).setVisibility(View.VISIBLE);
    FragmentManager fm = getSupportFragmentManager();
    for (int i = fm.getBackStackEntryCount() - 1; i > 0; i--) {
        if (!fm.getBackStackEntryAt(i).getName().equalsIgnoreCase("dashboard")) {
            Log.i("name","is:"+fm.getBackStackEntryAt(i).getName());
            fm.popBackStack();
        }
        else
        {
            break;
        }
    }
}
    public void backFromSettingsOptions(){
        findViewById(R.id.familyDashboardContainer).setVisibility(View.GONE);
        findViewById(R.id.included_fragments).setVisibility(View.GONE);
        findViewById(R.id.settings_container).setVisibility(View.GONE);
        findViewById(R.id.advanced_settings_container).setVisibility(View.GONE);
        findViewById(R.id.sublayout_container).setVisibility(View.VISIBLE);
        getFragmentManager().popBackStackImmediate();
    }

    public void setupToolbarForSettings(Toolbar toolbar){
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Basic Settings");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_menu_fragments);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backFromSettingsOptions();
            }
        });
    }
    /**
     * @author Devika on 03/08/2021
     * Method to change fragments based on item click in options menu inside fragment_family_dashboard
     * only applicable for fragments which require search and floating action button
     * @param fragment It is the fragment passing from FamilyDashboardFragment which is to be replaced
     * inside container included_fragments
     **/
    public boolean replaceFragment(Fragment fragment){
        if(fragment!=null){
                    findViewById(R.id.familyDashboardContainer).setVisibility(View.GONE);
                    findViewById(R.id.included_fragments).setVisibility(View.VISIBLE);
                    initListener();
                    initializeCallbacks();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.ll_container_for_menu_fragments,fragment)
                            .addToBackStack("dashboard")
                            .commit();
            Log.i("counts","is"+getFragmentManager().getBackStackEntryCount());
                return true;
        }
        return false;
    }
    /**
     * @author Devika on 12/08/2021
     * @param family model passed from FamilyDashboardFragment to get family_name and cover_pic
     * setup toolbar title with family name and imageview with coverpic
     * This method is invoked for fragments which require search and floating action button
     **/
    public void loadCoverPic(Family family){
        findViewById(R.id.included_fragments).setVisibility(View.VISIBLE);
        findViewById(R.id.familyDashboardContainer).setVisibility(View.GONE);
        if (family.getCoverPic() != null) {
            Glide.with(FamilyDashboardActivity.this)
                    .load(S3_DEV_IMAGE_URL_COVER + IMAGE_BASE_URL + COVER_PIC + family.getCoverPic())
                    .placeholder(R.drawable.family_dashboard_background)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            Log.e("TAG","Error loading page",e);
                            return false;
                        }
                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            Boolean bitmap = resource.isVisible();
                            Log.e("image","ready"+bitmap);
                            return false;
                        }
                    } )
                    .into(coverPic);
        }
        if(family.getGroupName()!=null){
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(family.getGroupName());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    backAction();
                }
            });
        }
    }
    /** End of loadCoverPic method **/
    /**
     * @author Devika on 09/08/2021
     * Method to change fragments based on item click in options menu inside fragment_family_dashboard
     * only applicable for fragments which do not require search and floating action button
     * @param fragment It is the fragment passing from FamilyDashboardFragment which is to be replaced
     **/
    public void loadFragment(Fragment fragment){
        if(fragment!=null){
            findViewById(R.id.familyDashboardContainer).setVisibility(View.GONE);
            findViewById(R.id.included_fragments).setVisibility(View.GONE);
            findViewById(R.id.sublayout_container).setVisibility(View.VISIBLE);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.ll_for_menu_fragments,fragment)
                    .addToBackStack("dashboard")
                    .commit();
        }
    }
    /**
     * @author Devika on 11/08/2021
     * @param family model passed from FamilyDashboardFragment to get family_name and cover_pic
     * setup toolbar title with family name and imageview with coverpic
     * This method is invoked for fragments which don't require search and floating action button
     **/
    public void setupToolbarAndImage(Family family){
        findViewById(R.id.sublayout_container).setVisibility(View.VISIBLE);
        findViewById(R.id.included_fragments).setVisibility(View.GONE);
        findViewById(R.id.familyDashboardContainer).setVisibility(View.GONE);
        if (family.getCoverPic() != null) {
            Glide.with(FamilyDashboardActivity.this)
                    .load(S3_DEV_IMAGE_URL_COVER + IMAGE_BASE_URL + COVER_PIC + family.getCoverPic())
                    .placeholder(R.drawable.family_dashboard_background)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }
                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            Boolean bitmap = resource.isVisible();
                            return false;
                        }
                    })
                    .into(cover_pic);
        }
        if(family.getGroupName()!=null){
            setSupportActionBar(toolbarWithoutSearch);
            getSupportActionBar().setTitle(family.getGroupName());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white);
            toolbarWithoutSearch.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    backAction();
                }
            });
        }
    }
    public void setupToolbarForMembersWithoutSearch(Family family){
        if(family.getGroupName()!=null){
            setSupportActionBar(toolbar_no_search);
            getSupportActionBar().setTitle(family.getGroupName());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white);
            toolbar_no_search.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    backAction();
                }
            });
        }
    }
    @Override
    public void loadFamilySubscriptions(Family family) {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.familyDashboardContainer, FamilySubscriptionFragment.newInstance(family))
                .addToBackStack("FamilySubscriptionFragment")
                .commit();
    }
    @Override
    public void loadFamilyAdvancedSettings(Family family) {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.familyDashboardContainer, FamilyCreationLevelAdvancedFragment.newInstance(family, true))
                .addToBackStack("FamilyCreationLevelAdvancedFragment")
                .commit();
    }
    @Override
    public void loadFamilyBasicSettings(Family family) {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.familyDashboardContainer, FamilyEditFragment.newInstance(family))
                .addToBackStack("FamilyEditFragment")
                .commit();
    }
    @Override
    public void loadFamilySettings(Family family) {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.familyDashboardContainer, FamilySettingsFragment.newInstance(family))
                .addToBackStack("FamilySettingsFragment")
                .commit();
    }
    @Override
    public void loadFileUploading(Family family) {
        getSupportFragmentManager().beginTransaction()
/** added the isAdmin value for getting the "admin only" condition in family settings**/
                .add(R.id.familyDashboardContainer, FoldersFragment.newInstance(family.getId().toString(), Constants.FileUpload.TYPE_FAMILY,family.isAdmin(),family.canCreatePost()))
                .addToBackStack("FoldersFragment")
                .commit();
    }
/** modified by Devika
 * invoked backAction() instead of onBackPressed() as it requires to navigate back to
 * dashboard after settings updation
 * **/
    @Override
    public void onFamilyUpdated(boolean isBackPressedNeeded) {
        if (isBackPressedNeeded)
            onBackPressed();
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.familyDashboardContainer);
        if (fragment instanceof FamilySettingsFragment) {
            onBackPressed();
            Fragment fragment1 = getSupportFragmentManager().findFragmentById(R.id.familyDashboardContainer);
            if (fragment1 instanceof FamilyDashboardFragment) {
                FamilyDashboardFragment familyDashboardFragment = (FamilyDashboardFragment) fragment1;
                familyDashboardFragment.refreshFamilyDetails(family);
            }
        } else if (fragment instanceof FamilyDashboardFragment) {
            FamilyDashboardFragment familyDashboardFragment = (FamilyDashboardFragment) fragment;
            familyDashboardFragment.refreshFamilyDetails(family);
        }
    }
    @Override
    public void loadMembersToBeAddedToFamily(Family family) {
        Intent intent = new Intent(getApplicationContext(), FamilyAddMemberActivity.class);
        intent.putExtra(Constants.Bundle.DATA, family);
        startActivityForResult(intent, FamilyAddMemberActivity.REQUEST_CODE);
    }
    @Override
    public void onFamilyCreated(Family family) {
        onBackPressed();

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.familyDashboardContainer);
        if (fragment instanceof FamilySettingsFragment) {
            onBackPressed();

            Fragment fragment1 = getSupportFragmentManager().findFragmentById(R.id.familyDashboardContainer);
            if (fragment1 instanceof FamilyDashboardFragment) {
                FamilyDashboardFragment familyDashboardFragment = (FamilyDashboardFragment) fragment1;
                familyDashboardFragment.refreshFamilyDetails(family);
            }
        } else if (fragment instanceof FamilyDashboardFragment) {
            FamilyDashboardFragment familyDashboardFragment = (FamilyDashboardFragment) fragment;
            familyDashboardFragment.refreshFamilyDetails(family);
        }
    }
    @Override
    public void onBackPressed() {
        if (NOTIFICATION.equals("NOTIFICATION")) {
            finish();

            overridePendingTransition(R.anim.left,
                    R.anim.right);
        } else {
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.familyDashboardContainer);
            if (fragment instanceof FamilyDashboardFragment) {
                ((FamilyDashboardFragment) fragment).goBack();
            } else
                super.onBackPressed();
        }
    }
    @Override
    public void loadSubFolder(String id, String folderId, int type, String title, String description, boolean isAdmin, boolean canCreate, boolean canUpdate) {
        Intent intent = new Intent(this, SubFolderActivity.class);
        intent.putExtra(Constants.Bundle.ID, id).putExtra(Constants.Bundle.FOLDER_ID, folderId);
        intent.putExtra(Constants.Bundle.TYPE, type);
        intent.putExtra(Constants.Bundle.IS_ADMIN, isAdmin);
        intent.putExtra(TITLE, title);
        intent.putExtra(DESCRIPTION, description);
        startActivity(intent);
    }

    @Override
    public void updateFamily(int TYPE, Family family) {
    }
    @Override
    public void onFamilyAddComponentVisible(int type) {
    }
    @Override
    public void onFamilyAddComponentHidden(int type) {
    }

    /**method for inflating the searchview on click of search icon in toolbar*/
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
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(searchAlbumQuery, InputMethodManager.SHOW_IMPLICIT);
            }
        }
    }

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

    @OnEditorAction(R.id.searchInfo)
    protected boolean onSearchQueryListener(int actionId) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            String s = searchAlbumQuery.getText().toString();
            search();
            try {
                InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchAlbumQuery.getWindowToken(), 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }
    private void search() {
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.ll_container_for_menu_fragments);
        if(f instanceof AlbumFragment){
            ((AlbumFragment) f).startSearch(searchAlbumQuery.getText().toString());
        }else if(f instanceof FoldersFragment){
            ((FoldersFragment)f).startSearch(searchAlbumQuery.getText().toString());
        }else if(f instanceof AnnouncementListingFragment){
            ((AnnouncementListingFragment) f).startSearch(searchAlbumQuery.getText().toString());
        }else if(f instanceof FamilySubscriptionUpdatedFragment){
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.members_container);
            if(fragment instanceof FamilyMembersFragment){
                ((FamilyMembersFragment)fragment).startSearch(searchAlbumQuery.getText().toString());
            }
        }else if(f instanceof LinkingUpdatedFragment){
            Fragment fragment = f.getChildFragmentManager().findFragmentById(R.id.linked_family_container);
            assert fragment != null;
                    ((LinkedFamilyFragment)fragment).startSearch(searchAlbumQuery.getText().toString());
        }
    }
    @Override
    public void onClick(View view) {
        if(view == cardView){
            navigateToNext();
        }
    }
    private void navigateToNext() {
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.ll_container_for_menu_fragments);
        if(f instanceof AlbumFragment){
            ((AlbumFragment) f).createNewAlbum();
        }else if(f instanceof FoldersFragment){
            ((FoldersFragment) f).createNewFolder();
        }else if(f instanceof AnnouncementListingFragment){
            ((AnnouncementListingFragment) f).createNewAnnouncement();
        }else if(f instanceof MembershipFragment){
            ((MembershipFragment)f).createMemberShipType();
        } else if(f instanceof FamilySubscriptionUpdatedFragment){
            ((FamilySubscriptionUpdatedFragment) f).createNewMember();
        }else if(f instanceof LinkingUpdatedFragment){
            Fragment fragment = f.getChildFragmentManager().findFragmentByTag("linkedFamily");
            assert fragment != null;
            ((LinkedFamilyFragment)fragment).checkFamilyLinking();
        }
    }
/**
 * 23-09-21
 */

    @Override
    public void sendMemberCount(int member) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.ll_container_for_menu_fragments);
        if(fragment instanceof FamilySubscriptionUpdatedFragment){
            ((FamilySubscriptionUpdatedFragment) fragment).setUpdatedMemberCount(member);
        }
    }

    @Override
    public void sendRequestCount(int request) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.ll_container_for_menu_fragments);
        if(fragment instanceof FamilySubscriptionUpdatedFragment){
            ((FamilySubscriptionUpdatedFragment) fragment).setUpdatedRequestCount(request);
        }
    }

    @Override
    public void refreshDashboard() {
        Fragment myFragment = getSupportFragmentManager().findFragmentById(R.id.familyDashboardContainer);
        if(myFragment instanceof FamilyDashboardFragment){
            FamilyDashboardFragment familyDashboardFragment = (FamilyDashboardFragment) myFragment;
            familyDashboardFragment.getFamilyDetails();
        }
    }
}
