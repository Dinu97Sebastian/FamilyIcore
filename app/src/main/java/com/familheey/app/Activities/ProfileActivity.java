package com.familheey.app.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.familheey.app.Adapters.ProfilePagerAdapter;
import com.familheey.app.Dialogs.ProfileEditDialogFragment;
import com.familheey.app.Fragments.Events.AlbumFragment;
import com.familheey.app.Fragments.Posts.OnlyMePostFragment;
import com.familheey.app.Fragments.ProfileDashboard.AboutMeFragment;
import com.familheey.app.Fragments.ViewRequestFragment;
import com.familheey.app.Interfaces.ProgressListener;
import com.familheey.app.Interfaces.RetrofitListener;
import com.familheey.app.Models.ApiCallbackParams;
import com.familheey.app.Models.ErrorData;
import com.familheey.app.Models.Response.FamilyMember;
import com.familheey.app.Models.Response.PeopleSearchModal;
import com.familheey.app.Models.Response.ProfileResponse.Profile;
import com.familheey.app.Models.Response.ProfileResponse.UserProfile;
import com.familheey.app.Models.Response.UserRegistrationResponse;
import com.familheey.app.Need.ProfileNeedsListingFragment;
import com.familheey.app.Networking.Retrofit.ApiServiceProvider;
import com.familheey.app.Networking.utils.GsonUtils;
import com.familheey.app.PaymentHistory.PaymentHistoryActivity;
import com.familheey.app.R;
import com.familheey.app.Topic.CreateTopic;
import com.familheey.app.Topic.MainActivity;
import com.familheey.app.Utilities.Constants;
import com.familheey.app.Utilities.GifSizeFilter;
import com.familheey.app.Utilities.SharedPref;
import com.familheey.app.Utilities.Utilities;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.filter.Filter;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.MediaType;
import okhttp3.MultipartBody;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import static com.familheey.app.Activities.ImageChangerActivity.REQUEST_CODE_CROP;
import static com.familheey.app.Utilities.Constants.ApiFlags.UPDATE_USER_PROFILE;
import static com.familheey.app.Utilities.Constants.ApiFlags.VIEW_USER_PROFILE;
import static com.familheey.app.Utilities.Constants.ApiPaths.FIREBASE_DATABASE_URL;
import static com.familheey.app.Utilities.Constants.ApiPaths.IMAGE_BASE_URL;
import static com.familheey.app.Utilities.Constants.ApiPaths.S3_DEV_IMAGE_URL_COVER;
import static com.familheey.app.Utilities.Constants.ApiPaths.S3_DEV_IMAGE_URL_SQUARE;
import static com.familheey.app.Utilities.Constants.Bundle.DATA;
import static com.familheey.app.Utilities.Constants.Bundle.IDENTIFIER;
import static com.familheey.app.Utilities.Constants.Bundle.NOTIFICATION_ID;
import static com.familheey.app.Utilities.Constants.Bundle.REQUEST;
import static com.familheey.app.Utilities.Constants.Bundle.TYPE;
import static com.familheey.app.Utilities.Constants.ImageUpdate.USER_PROFILE_COVER;
import static com.familheey.app.Utilities.Constants.ImageUpdate.USER_PROFILE_LOGO;
import static com.familheey.app.Utilities.Constants.Paths.COVER_PIC;
import static com.familheey.app.Utilities.Constants.Paths.PROFILE_PIC;
public class ProfileActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener, ProgressListener, ProfileEditDialogFragment.OnFamilyMemberEditCompleted, RetrofitListener {

    public static final int REQUEST_CODE = 301;
    public static final int RESULT_CODE = 222;

    @BindView(R.id.profileImage)
    ImageView profileImage;
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.familiesCount)
    TextView familiesCount;
    @BindView(R.id.familiesContainer)
    LinearLayout familiesContainer;
    @BindView(R.id.connectionsCount)
    TextView connectionsCount;
    @BindView(R.id.connectionsContainer)
    LinearLayout connectionsContainer;
    @BindView(R.id.mutualCount)
    TextView mutualCount;
    @BindView(R.id.mutualContainer)
    LinearLayout mutualContainer;
    @BindView(R.id.mutualFamiliesContainer)
    LinearLayout mutualFamiliesContainer;
    @BindView(R.id.familyLocation)
    TextView familyLocation;
    @BindView(R.id.familyInfoBlocksContainer)
    ConstraintLayout familyInfoBlocksContainer;
    @BindView(R.id.collapsingToolbar)
    CollapsingToolbarLayout collapsingToolbar;
    @BindView(R.id.appBar)
    AppBarLayout appBar;
    @BindView(R.id.profileTab)
    TabLayout profileTab;
    @BindView(R.id.myProfileViewPager)
    ViewPager myProfileViewPager;
    @BindView(R.id.locationIcon)
    ImageView locationIcon;
    @BindView(R.id.profileBack)
    ImageView profileBack;
    @BindView(R.id.payment)
    ImageView payment;
    @BindView(R.id.profileSettings)
    ImageView profileSettings;
    @BindView(R.id.profileTabContainer)
    CardView profileTabContainer;
    @BindView(R.id.profileEdit)
    ImageView profileEdit;
    @BindView(R.id.coverImage)
    ImageView coverImage;
    @BindView(R.id.addToFamily)
    MaterialButton addToFamily;
    @BindView(R.id.mutualFamiliesCount)
    TextView mutualFamiliesCount;
    @BindView(R.id.toolBarTitle)
    TextView toolBarTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.createTopic)
    ImageView createTopic;

    @BindView(R.id.nestedScrollProfile)
    NestedScrollView nestedScrollProfile;

    @BindView(R.id.progressProfile)
    ProgressBar progressProfile;

    private ProfilePagerAdapter profilePagerAdapter;
    private FamilyMember familyMember;
    private UserProfile userProfile = null;
    private SweetAlertDialog progressDialog;
    private boolean isAdmin = false;
    private int profileCallCount = 0;
    private Uri croppedImageUri;
    private Uri imageUri;
    private int imageType = 0;
    private boolean hideAddToFamily = false;
    private String notificationId = "";
    private DatabaseReference database;
    List<Uri> mSelectedUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        if (getIntent().hasExtra(TYPE)) {
            familyMember = new FamilyMember();
            familyMember.setUserId(Integer.parseInt(Objects.requireNonNull(getIntent().getStringExtra(DATA))));
            familyMember.setId(Integer.parseInt(Objects.requireNonNull(getIntent().getStringExtra(DATA))));
        } else {
            familyMember = getIntent().getParcelableExtra(DATA);
            assert familyMember != null;
            if (familyMember.getProPic() != null)
                Glide.with(getApplicationContext())
                        .load(S3_DEV_IMAGE_URL_SQUARE + IMAGE_BASE_URL + PROFILE_PIC + familyMember.getProPic())
                        .apply(Utilities.getCurvedRequestOptions())
                        .transition(withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                        .placeholder(R.drawable.avatar_male)
                        .into(profileImage);
        }
        if (getIntent().hasExtra(NOTIFICATION_ID)) {
            notificationId=getIntent().getStringExtra(NOTIFICATION_ID);
            database= FirebaseDatabase.getInstance(FIREBASE_DATABASE_URL).getReference(Constants.ApiPaths.FIREBASE_USER_TYPE + SharedPref.getUserRegistration().getId() + "_notification");
            database.child(notificationId).child("visible_status").setValue("read");
        }


        hideAddToFamily = getIntent() != null && getIntent().hasExtra(IDENTIFIER) && getIntent().getBooleanExtra(IDENTIFIER, false);

    }

    private void initializeTabs() {
        profilePagerAdapter = new ProfilePagerAdapter(getSupportFragmentManager());
        if (SharedPref.getUserRegistration().getId().equalsIgnoreCase(String.valueOf(userProfile.getProfile().getId()))) {
            profilePagerAdapter.addFragment(AboutMeFragment.newInstance(familyMember), "About Me");
            profilePagerAdapter.addFragment(OnlyMePostFragment.newInstance(familyMember), "My Posts");
            profilePagerAdapter.addFragment(AlbumFragment.newInstance(Constants.FileUpload.TYPE_USER, String.valueOf(userProfile.getProfile().getId()), familyMember.getIsAdminByValidatingID()), "Albums");
            profilePagerAdapter.addFragment(ViewRequestFragment.newInstance(familyMember), "Invites");
            profilePagerAdapter.addFragment(ProfileNeedsListingFragment.newInstance(String.valueOf(userProfile.getProfile().getId())), "My Requests");
        } else {
            profilePagerAdapter.addFragment(AboutMeFragment.newInstance(familyMember), "About Me");
            profilePagerAdapter.addFragment(AlbumFragment.newInstance(Constants.FileUpload.TYPE_USER, String.valueOf(userProfile.getProfile().getId()), familyMember.getIsAdminByValidatingID()), "Albums");
            //  profilePagerAdapter.addFragment(ProfileNeedsListingFragment.newInstance(String.valueOf(userProfile.getProfile().getId())), "Requests");
            profileTab.setTabMode(TabLayout.MODE_FIXED);
        }


        myProfileViewPager.setAdapter(profilePagerAdapter);
        myProfileViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(profileTab));
        profileTab.addOnTabSelectedListener(this);
        profileTab.setupWithViewPager(myProfileViewPager);
        myProfileViewPager.setOffscreenPageLimit(profileTab.getTabCount());
    }

    private void fillDetails() {
        Animation animFadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        name.startAnimation(animFadeIn);
        familyLocation.startAnimation(animFadeIn);
        name.setText(userProfile.getProfile().getFullName());
        familyLocation.setText(userProfile.getProfile().getLocation());
        if(userProfile.getProfile().getLocation()==null){
            locationIcon.setVisibility( View.GONE );
        }
        familiesCount.setText(String.valueOf(userProfile.getCount().getFamilyCount()));
        connectionsCount.setText(String.valueOf(userProfile.getCount().getConnections()));
        mutualCount.setText(String.valueOf(userProfile.getCount().getMutualConnections()));
        mutualFamiliesCount.setText(String.valueOf(userProfile.getCount().getMutualFamiliesCount()));
        if (userProfile.getProfile().getPropic() != null) {
            Glide.with(getApplicationContext())
                    .load(S3_DEV_IMAGE_URL_SQUARE + IMAGE_BASE_URL + PROFILE_PIC + userProfile.getProfile().getPropic())
                    .apply(Utilities.getCurvedRequestOptions())
                    .transition(withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                    .placeholder(R.drawable.avatar_male)
                    .into(profileImage);
        } else {
            Glide.with(getApplicationContext())
                    .load(R.drawable.avatar_male)
                    .transition(withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                    .into(profileImage);
        }
        if (userProfile.getProfile().getCoverPic() != null) {
            Glide.with(getApplicationContext())
                    .load(S3_DEV_IMAGE_URL_COVER + IMAGE_BASE_URL + COVER_PIC + userProfile.getProfile().getCoverPic())
                    .placeholder(R.drawable.user_profile_default_image)
                    .transition(withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                    .into(coverImage);
        } else {
            Glide.with(getApplicationContext())
                    .load(R.drawable.user_profile_default_image)
                    .transition(withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                    .into(coverImage);
        }
        if (SharedPref.getUserRegistration().getId().equalsIgnoreCase(familyMember.getUserId().toString()))
            createTopic.setVisibility(View.INVISIBLE);
        else createTopic.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchUserProfile();
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        myProfileViewPager.setCurrentItem(tab.getPosition());
        appBar.setExpanded(tab.getPosition() == 0);
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {


    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    private void updateFragment(UserProfile userProfile) {
        if (familyMember == null) return;
        collapsingToolBarBehaviour();
        if (profilePagerAdapter == null)
            initializeTabs();
        familiesContainer.setVisibility(View.VISIBLE);
        AboutMeFragment aboutMeFragment = profilePagerAdapter.getInstance(AboutMeFragment.class);
        if (aboutMeFragment != null) aboutMeFragment.fillDetails(userProfile);
        if (SharedPref.getUserRegistration().getId().equalsIgnoreCase(familyMember.getUserId().toString())) {
            isAdmin = true;
            ViewRequestFragment viewRequestFragment = profilePagerAdapter.getInstance(ViewRequestFragment.class);
            if (viewRequestFragment != null) viewRequestFragment.fillDetails(userProfile);
            if (getIntent() != null && getIntent().hasExtra(TYPE) && Objects.requireNonNull(getIntent().getStringExtra(TYPE)).equalsIgnoreCase(REQUEST)) {
                Objects.requireNonNull(profileTab.getTabAt(3)).select();
            }
            mutualFamiliesContainer.setVisibility(View.GONE);
            mutualContainer.setVisibility(View.GONE);
            connectionsContainer.setVisibility(View.VISIBLE);
            profileSettings.setVisibility(View.VISIBLE);

            payment.setVisibility(View.VISIBLE);
            profileEdit.setVisibility(View.VISIBLE);
            addToFamily.setVisibility(View.INVISIBLE);
        } else {
            isAdmin = false;
            mutualFamiliesContainer.setVisibility(View.VISIBLE);
            mutualContainer.setVisibility(View.VISIBLE);
            connectionsContainer.setVisibility(View.GONE);
            payment.setVisibility(View.GONE);
            profileSettings.setVisibility(View.GONE);
            profileEdit.setVisibility(View.GONE);
            if (!hideAddToFamily)
                addToFamily.setVisibility(View.VISIBLE);
        }
        AlbumFragment albumFragment = profilePagerAdapter.getInstance(AlbumFragment.class);
        if (albumFragment != null)
            albumFragment.updateAdminStatus(isAdmin, isAdmin, isAdmin, SharedPref.getUserRegistration().getId());
    }

    private void fetchUserProfile() {
        if (progressProfile != null && profileCallCount == 0)
            progressProfile.setVisibility(View.VISIBLE);
        profileCallCount++;
        JsonObject requestjson = new JsonObject();
        if (familyMember.getUserId() != null) {
            requestjson.addProperty("user_id", SharedPref.getUserRegistration().getId());
            requestjson.addProperty("profile_id", familyMember.getUserId().toString());
            ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getApplicationContext());
            apiServiceProvider.viewUserProfile(requestjson, null, this);
        }
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
        Utilities.getErrorDialog(progressDialog, errorMessage);
    }

    @Override
    public void onFamilyMemberEditCompleted(int TYPE, UserProfile userProfile) {
        this.userProfile = userProfile;
        fetchUserProfile();
    }

    @Override
    public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {

        switch (apiFlag) {
            case UPDATE_USER_PROFILE:
                Profile profile = GsonUtils.getInstance().getGson().fromJson(responseBodyString, Profile.class);
                try {
                    if (String.valueOf(profile.getId()).equalsIgnoreCase(SharedPref.getUserRegistration().getId()))
                        if (profile.getPropic() != null) {
                            UserRegistrationResponse userRegistrationResponse = SharedPref.getUserRegistration();
                            userRegistrationResponse.setPropic(profile.getPropic());
                            SharedPref.write(SharedPref.USER, GsonUtils.getInstance().getGson().toJson(userRegistrationResponse));
                            SharedPref.setUserRegistration(userRegistrationResponse);
                        }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                hideProgressDialog();
                userProfile.setProfile(profile);
                if (userProfile.getProfile().getPropic() != null) {
                    Glide.with(getApplicationContext())
                            .load(S3_DEV_IMAGE_URL_SQUARE + IMAGE_BASE_URL + PROFILE_PIC + userProfile.getProfile().getPropic())
                            .apply(Utilities.getCurvedRequestOptions())
                            .transition(withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                            .placeholder(R.drawable.avatar_male)
                            .into(profileImage);
                } else {
                    Glide.with(getApplicationContext())
                            .load(R.drawable.avatar_male)
                            .transition(withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                            .into(profileImage);
                }
                break;
            case VIEW_USER_PROFILE:
                try {
                    userProfile = GsonUtils.getInstance().getGson().fromJson(responseBodyString, UserProfile.class);
                    fillDetails();
                    updateFragment(userProfile);
                } catch (JsonParseException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Something unexpected occured! Please try again later", Toast.LENGTH_SHORT).show();
                    if (progressProfile != null)
                        progressProfile.setVisibility(View.GONE);
                    onBackPressed();
                }
                if (progressProfile != null)
                    progressProfile.setVisibility(View.GONE);
                break;
        }

        if (nestedScrollProfile != null) {
            nestedScrollProfile.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
        if (progressProfile != null)
            progressProfile.setVisibility(View.GONE);
        if (apiFlag == VIEW_USER_PROFILE) {
            try {
                //if (true) {
                    SweetAlertDialog contentNotFoundDialog = Utilities.getContentNotFoundDialog(this);
                contentNotFoundDialog.setConfirmClickListener(sweetAlertDialog -> finish());
                contentNotFoundDialog.setCanceledOnTouchOutside(false);
                    contentNotFoundDialog.setCancelable(false);
                    contentNotFoundDialog.show();
                    Utilities.addPositiveButtonMargin(contentNotFoundDialog);
                //}
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (nestedScrollProfile != null) {
            nestedScrollProfile.setVisibility(View.VISIBLE);
        }
    }

    @OnClick({R.id.payment, R.id.profileBack, R.id.profileSettings, R.id.profileEdit, R.id.coverImage, R.id.mutualContainer, R.id.familiesContainer, R.id.connectionsContainer, R.id.addToFamily, R.id.profileImage, R.id.mutualFamiliesContainer, R.id.createTopic})
    public void onViewClicked(View view) {
        if (userProfile == null)
            return;
        switch (view.getId()) {
            case R.id.payment:
                startActivity(new Intent(this, PaymentHistoryActivity.class));
                break;
            case R.id.profileBack:
                supportFinishAfterTransition();
                break;
            case R.id.profileSettings:
                Intent intent = new Intent(getApplicationContext(), ProfileEditActivity.class);
                intent.putExtra(DATA, GsonUtils.getInstance().toJson(userProfile));
                startActivity(intent);
                break;
            case R.id.profileEdit:
                imageType = USER_PROFILE_LOGO;
                if (isReadStoragePermissionGranted())
//                    CropImage.activity()
//                            .setGuidelines(CropImageView.Guidelines.ON)
//                            .setAspectRatio(1, 1)
//                            .start(this);
                /**
                 * ImagePicker for supporting in android11
                 * megha(23/10/2021)
                 */
                    ImagePicker.with(this)
                            .crop()//Crop image(Optional), Check Customization for more option
                            .compress(1024)			//Final image size will be less than 1 MB(Optional)
                            .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                            .start();
                else
                    showPermission(3);
                break;
            case R.id.profileImage:
                if (userProfile != null) {
                    Intent familyCoverEditIntent = new Intent(getApplicationContext(), ImageChangerActivity.class);
                    familyCoverEditIntent.putExtra(DATA, userProfile);
                    familyCoverEditIntent.putExtra(TYPE, USER_PROFILE_LOGO);
                    familyCoverEditIntent.putExtra(IDENTIFIER, isAdmin);
                    startActivityForResult(familyCoverEditIntent, ImageChangerActivity.REQUEST_CODE);
                }
                break;
            case R.id.coverImage:
                if (userProfile != null) {
                    Intent userProfileCoverIntent = new Intent(this, ImageChangerActivity.class);
                    userProfileCoverIntent.putExtra(DATA, userProfile);
                    userProfileCoverIntent.putExtra(TYPE, USER_PROFILE_COVER);
                    userProfileCoverIntent.putExtra(IDENTIFIER, isAdmin);
                    startActivityForResult(userProfileCoverIntent, ImageChangerActivity.REQUEST_CODE);
                }

                break;
            case R.id.mutualContainer:
                if (userProfile == null)
                    return;
                Intent mutualConnectionsIntent = new Intent(getApplicationContext(), UserConnectionsActivity.class);
                mutualConnectionsIntent.putExtra(TYPE, UserConnectionsActivity.MUTUAL_CONNECTIONS);
                mutualConnectionsIntent.putExtra(Constants.Bundle.ID, String.valueOf(userProfile.getProfile().getId()));
                startActivity(mutualConnectionsIntent);
                break;
            case R.id.familiesContainer:
                if (userProfile == null)
                    return;
                if (userProfile.getCount() != null && userProfile.getCount().getFamilyCount() == 0)
                    return;
                Intent otherUsersFamilyIntent = new Intent(getApplicationContext(), OtherUsersFamilyActivity.class);
                otherUsersFamilyIntent.putExtra(TYPE, OtherUsersFamilyActivity.CONNECTIONS);
                otherUsersFamilyIntent.putExtra(Constants.Bundle.ID, String.valueOf(userProfile.getProfile().getId()));
                startActivity(otherUsersFamilyIntent);
                break;
            case R.id.connectionsContainer:
                if (userProfile == null)
                    return;
                Intent userConnectionsIntent = new Intent(getApplicationContext(), UserConnectionsActivity.class);
                userConnectionsIntent.putExtra(TYPE, UserConnectionsActivity.CONNECTIONS);
                userConnectionsIntent.putExtra(Constants.Bundle.ID, String.valueOf(userProfile.getProfile().getId()));
                startActivity(userConnectionsIntent);
                break;
            case R.id.mutualFamiliesContainer:
                if (userProfile == null)
                    return;
                Intent mutualFamiliesIntent = new Intent(getApplicationContext(), OtherUsersFamilyActivity.class);
                mutualFamiliesIntent.putExtra(TYPE, OtherUsersFamilyActivity.MUTUAL_CONNECTIONS);
                mutualFamiliesIntent.putExtra(Constants.Bundle.ID, String.valueOf(userProfile.getProfile().getId()));
                startActivity(mutualFamiliesIntent);
                break;
            case R.id.addToFamily:
                showAddToFamilyDialogPopUp();
                break;
            case R.id.createTopic:
                Intent topicIntent = new Intent(getApplicationContext(), CreateTopic.class);
                topicIntent.putExtra(DATA, familyMember.getUserId().toString());
                startActivity(topicIntent);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (getIntent() != null && getIntent().hasExtra(TYPE)) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {
            Intent intent = new Intent();
            intent.putExtra(DATA, familyMember);
            setResult(RESULT_CODE, intent);
        }
        supportFinishAfterTransition();
    }

    private void showAddToFamilyDialogPopUp() {
        PeopleSearchModal peopleSearchModal = new PeopleSearchModal();
        peopleSearchModal.setId(userProfile.getProfile().getId());
        startActivity(new Intent(this, MyFamiliesActivity.class).putExtra(Constants.Bundle.DATA, peopleSearchModal));
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
                    if (userProfile != null && userProfile.getProfile().getFullName() != null)
                        toolBarTitle.setText(userProfile.getProfile().getFullName());
                    isShow = true;
                } else if (isShow) {
                    toolBarTitle.setText("");
                    isShow = false;
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /**
         * changed to enable camera capture in android 11
         * megha(23/10/2021)
         */
        if (requestCode == ImagePicker.REQUEST_CODE && data != null) {
            if (resultCode == RESULT_OK) {
                switch (imageType) {
                    case Constants.ImageUpdate.USER_PROFILE_COVER:
                    case Constants.ImageUpdate.USER_PROFILE_LOGO:
                        croppedImageUri = data.getData();
                        break;
                }

                updateImages();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
//                Exception error = result.getError();
                Toast.makeText(getApplicationContext(), "Something error occured!! Please try again later", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_CODE_CROP) {
            if (resultCode == Activity.RESULT_OK && null != data) {
                imageUri = Matisse.obtainResult(data).get(0);
                CropImage.activity(imageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(4, 3)
                        .start(this);
            }
        } else if (requestCode == ProfileEditActivity.REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                fetchUserProfile();
            }
        }
    }

    private void updateImages() {
        String IMAGE_KEY = "";
        String ORIGINAL_IMAGE_KEY = "";
        switch (imageType) {
            case Constants.ImageUpdate.USER_PROFILE_COVER:
                IMAGE_KEY = "cover_pic";
                ORIGINAL_IMAGE_KEY = "user_original_image";
                break;
            case Constants.ImageUpdate.USER_PROFILE_LOGO:
                IMAGE_KEY = "propic";
                break;
        }
        showProgressDialog();
        MultipartBody.Builder multiPartBuilder = new MultipartBody.Builder().setType(Objects.requireNonNull(MediaType.parse("multipart/form-data")));
        Utilities.addImageToMultiPartBuilder(multiPartBuilder, croppedImageUri, IMAGE_KEY);
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getApplicationContext());
        switch (imageType) {
            case Constants.ImageUpdate.USER_PROFILE_COVER:
                Utilities.addImageToMultiPartBuilder(multiPartBuilder, imageUri, ORIGINAL_IMAGE_KEY);
                multiPartBuilder.addFormDataPart("id", SharedPref.getUserRegistration().getId());
                apiServiceProvider.updateUserProfile(multiPartBuilder, null, this);
                break;
            case Constants.ImageUpdate.USER_PROFILE_LOGO:
                multiPartBuilder.addFormDataPart("id", SharedPref.getUserRegistration().getId());
                apiServiceProvider.updateUserProfile(multiPartBuilder, null, this);
                break;
        }
    }

    private boolean isReadStoragePermissionGranted() {
        return TedPermission.isGranted(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    private void requestPermission() {
        TedPermission.with(this)
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        goToCoverImagePicker();
                    }

                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {

                    }
                })
                .setDeniedMessage("If you reject permission,you can not upload images\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();
    }

    private void goToCoverImagePicker() {
        Matisse.from(ProfileActivity.this)
                .choose(MimeType.ofImage())
                .showSingleMediaType(true)
                .countable(true)
                .maxSelectable(1)
                .addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
                .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)
                .imageEngine(new GlideEngine())
                .showPreview(true)
                .forResult(REQUEST_CODE_CROP);
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
            if (type == 3) {
//                CropImage.activity()
//                        .setGuidelines(CropImageView.Guidelines.ON)
//                        .setAspectRatio(1, 1)
//                        .start(this);
                ImagePicker.with(this)
                        .crop()//Crop image(Optional), Check Customization for more option
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start();

            } else {
                requestPermission();
            }
        });
        dialog.show();
    }


}
