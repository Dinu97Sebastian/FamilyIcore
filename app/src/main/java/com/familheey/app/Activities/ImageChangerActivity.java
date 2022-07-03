package com.familheey.app.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.familheey.app.Interfaces.ProgressListener;
import com.familheey.app.Interfaces.RetrofitListener;
import com.familheey.app.Models.ApiCallbackParams;
import com.familheey.app.Models.ErrorData;
import com.familheey.app.Models.Response.EventDetail;
import com.familheey.app.Models.Response.Family;
import com.familheey.app.Models.Response.ProfileResponse.Profile;
import com.familheey.app.Models.Response.ProfileResponse.UserProfile;
import com.familheey.app.Models.Response.UserRegistrationResponse;
import com.familheey.app.Networking.Retrofit.ApiServiceProvider;
import com.familheey.app.Networking.utils.GsonUtils;
import com.familheey.app.Parsers.FamilyParser;
import com.familheey.app.R;
import com.familheey.app.Utilities.Constants;
import com.familheey.app.Utilities.GifSizeFilter;
import com.familheey.app.Utilities.SharedPref;
import com.familheey.app.Utilities.Utilities;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.button.MaterialButton;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.jsibbold.zoomage.ZoomageView;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.filter.Filter;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.MediaType;
import okhttp3.MultipartBody;

import static com.familheey.app.Utilities.Constants.ApiFlags.UPDATE_FAMILY;
import static com.familheey.app.Utilities.Constants.ApiFlags.UPDATE_USER_PROFILE;
import static com.familheey.app.Utilities.Constants.ApiPaths.IMAGE_BASE_URL;
import static com.familheey.app.Utilities.Constants.ApiPaths.S3_DEV_IMAGE_URL_COVER_DETAILED;
import static com.familheey.app.Utilities.Constants.ApiPaths.S3_DEV_IMAGE_URL_SQUARE_DETAILED;
import static com.familheey.app.Utilities.Constants.Bundle.DATA;
import static com.familheey.app.Utilities.Constants.Bundle.IDENTIFIER;
import static com.familheey.app.Utilities.Constants.ImageUpdate.EVENT_COVER;
import static com.familheey.app.Utilities.Constants.Paths.COVER_PIC;
import static com.familheey.app.Utilities.Constants.Paths.EVENT_IMAGE;
import static com.familheey.app.Utilities.Constants.Paths.LOGO;
import static com.familheey.app.Utilities.Constants.Paths.PROFILE_PIC;


public class ImageChangerActivity extends AppCompatActivity implements RetrofitListener, ProgressListener/*, MessagePushDelegate*/ {

    public static final int REQUEST_CODE = 19;
    public static final int REQUEST_CODE_CROP = 20;
    @BindView(R.id.imagedownload)
    ImageView imagedownload;
    @BindView(R.id.image)
    ZoomageView image;
    @BindView(R.id.goBack)
    ImageView goBack;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.imageOptions)
    ImageView imageOptions;
    @BindView(R.id.uploadImage)
    MaterialButton uploadImage;
    private String fname = "";
    private Uri croppedImageUri = null;
    private Uri imageUri = null;
    private MultipartBody.Builder multiPartBuilder;
    private int TYPE;
    private boolean isUpdated = false;
    private EventDetail eventDetail;
    private Family family;
    private UserProfile familyMember;
    private SweetAlertDialog progressDialog;
    private boolean changeImageAvailable = false;
    private String URL = "";
    private boolean isCoverImageSelected = false;
    private Bitmap loadedBitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_changer);
        ButterKnife.bind(this);
        TYPE = getIntent().getIntExtra(Constants.Bundle.TYPE, 0);
        changeImageAvailable = getIntent().getBooleanExtra(IDENTIFIER, true);
        if (getIntent().hasExtra("DOWNLOAD")) {
            fname = Objects.requireNonNull(getIntent().getExtras()).getString("FNAME");
            imagedownload.setVisibility(View.VISIBLE);
        }
        initializeToolbar();
        switch (TYPE) {
            case EVENT_COVER:
            case Constants.ImageUpdate.EVENT_LOGO:
                eventDetail = getIntent().getParcelableExtra(DATA);
                break;

            case Constants.ImageUpdate.FAMILY_COVER:
                isCoverImageSelected = true;
                family = getIntent().getParcelableExtra(DATA);
                break;
            case Constants.ImageUpdate.FAMILY_LOGO:
                family = getIntent().getParcelableExtra(DATA);
                family.setLogo(getIntent().getExtras().getString("logo"));
                break;
            case Constants.ImageUpdate.USER_PROFILE_COVER:
            case Constants.ImageUpdate.USER_PROFILE_LOGO:
                familyMember = getIntent().getParcelableExtra(DATA);
                break;
            case Constants.ImageUpdate.GENERAL:
                URL = getIntent().getStringExtra(DATA);
                break;
        }
        loadImage();
        if (!changeImageAvailable) {
            imageOptions.setVisibility(View.INVISIBLE);
        }
//        FamilheeyApplication.messagePushDelegate = this;
    }

    private void initializeToolbar() {
        /*if (changeImageAvailable)
            toolBarTitle.setText("Change Image");
        else toolBarTitle.setText("View Image");*/
    }

    private void loadImage() {
        String isDefalut = isDefaultImage();
        if (isDefalut == null) {
            if (changeImageAvailable)
                uploadImage.setVisibility(View.VISIBLE);
            else
                uploadImage.setVisibility(View.INVISIBLE);
            imageOptions.setVisibility(View.INVISIBLE);
            //Toast.makeText(this, "No image found", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isDefalut.isEmpty()) {
            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .placeholder(R.drawable.progress_animation)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .priority(Priority.HIGH)
                    .dontAnimate()
                    .dontTransform();
            Glide.with(this)
                    .load(getImageUrl().trim())
                    .transition(DrawableTransitionOptions.withCrossFade()).apply(options)
                    .into(image);


            Glide.with(getApplicationContext())
                    .asBitmap()
                    .load(getImageUrl())
                    .listener(new RequestListener<Bitmap>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                            loadedBitmap = resource;
                            return false;
                        }
                    }).into(image);

        }


    }

    @OnClick({R.id.goBack, R.id.imageOptions, R.id.uploadImage, R.id.imagedownload})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.uploadImage:
                triggerUpdateImage();
                break;
            case R.id.imageOptions:
                showImageOptions(imageOptions);
                break;
            case R.id.imagedownload:
                if (isReadStoragePermissionGranted()) {
                    Toast.makeText(this, "Downloading media...", Toast.LENGTH_SHORT).show();
                    Utilities.downloadDocuments(this, URL, fname);
                } else {
                    showPermission(1);
                }
                break;
            case R.id.goBack:
                Intent intent = new Intent();
                if (isUpdated) {
                    intent.putExtra(DATA, croppedImageUri.toString());
                    setResult(Activity.RESULT_OK, intent);
                } else setResult(Activity.RESULT_CANCELED, intent);
                finish();
        }
    }

    @Override
    public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {
        isUpdated = true;
        hideProgressDialog();
        Intent intent = new Intent();
        switch (apiFlag) {
            case UPDATE_FAMILY:
                family = FamilyParser.parseFamily(responseBodyString);
                intent.putExtra(Constants.Bundle.DATA, family);
                break;
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
                intent.putExtra(Constants.Bundle.PROFILE, profile);
                break;
        }
        intent.putExtra(DATA, croppedImageUri.toString());
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @Override
    public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
        showErrorDialog(errorData.getMessage());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ImagePicker.REQUEST_CODE && data != null) {
            if (resultCode == RESULT_OK) {
                switch (TYPE) {
                    case EVENT_COVER:
//                    case Constants.ImageUpdate.FAMILY_COVER:
//                    case Constants.ImageUpdate.USER_PROFILE_COVER:
                    case Constants.ImageUpdate.FAMILY_LOGO:
                    case Constants.ImageUpdate.EVENT_LOGO:
                    case Constants.ImageUpdate.USER_PROFILE_LOGO:
                        croppedImageUri = data.getData();
                        break;
                }
                updateImages();
            }
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                switch (TYPE) {
                    case EVENT_COVER:
                    case Constants.ImageUpdate.FAMILY_COVER:
                    case Constants.ImageUpdate.USER_PROFILE_COVER:
//                    case Constants.ImageUpdate.FAMILY_LOGO:
//                    case Constants.ImageUpdate.EVENT_LOGO:
//                    case Constants.ImageUpdate.USER_PROFILE_LOGO:
                        croppedImageUri =result.getUri();
                        break;
                }
                updateImages();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
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
        }
    }

    private void updateImages() {
        showProgressDialog();
        String IMAGE_KEY = "";
        String ORIGINAL_IMAGE_KEY = "";
        switch (TYPE) {
            case Constants.ImageUpdate.FAMILY_LOGO:
                IMAGE_KEY = "logo";
                break;
            case Constants.ImageUpdate.FAMILY_COVER:
                IMAGE_KEY = "cover_pic";
                ORIGINAL_IMAGE_KEY = "group_original_image";
                break;

            case Constants.ImageUpdate.EVENT_LOGO:
            case EVENT_COVER:
                IMAGE_KEY = "event_image";
                ORIGINAL_IMAGE_KEY = "event_original_image";
                break;
            case Constants.ImageUpdate.USER_PROFILE_COVER:
                IMAGE_KEY = "cover_pic";
                ORIGINAL_IMAGE_KEY = "user_original_image";
                break;
            case Constants.ImageUpdate.USER_PROFILE_LOGO:
                IMAGE_KEY = "propic";
                break;
        }
        multiPartBuilder = new MultipartBody.Builder().setType(MediaType.parse("multipart/form-data"));
        Utilities.addImageToMultiPartBuilder(multiPartBuilder, croppedImageUri, IMAGE_KEY);
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getApplicationContext());
        switch (TYPE) {
            case Constants.ImageUpdate.FAMILY_COVER:
                Utilities.addImageToMultiPartBuilder(multiPartBuilder, imageUri, ORIGINAL_IMAGE_KEY);
                multiPartBuilder.addFormDataPart("user_id", SharedPref.getUserRegistration().getId());
                multiPartBuilder.addFormDataPart("id", family.getId().toString());
                apiServiceProvider.EdiFamily(multiPartBuilder, null, this);
                break;
            case Constants.ImageUpdate.FAMILY_LOGO:
                multiPartBuilder.addFormDataPart("user_id", SharedPref.getUserRegistration().getId());
                multiPartBuilder.addFormDataPart("id", family.getId().toString());
                apiServiceProvider.EdiFamily(multiPartBuilder, null, this);
                break;

            case Constants.ImageUpdate.EVENT_LOGO:
            case EVENT_COVER:
                multiPartBuilder.addFormDataPart("user_id", SharedPref.getUserRegistration().getId());
                multiPartBuilder.addFormDataPart("id", eventDetail.getEventId());
                Utilities.addImageToMultiPartBuilder(multiPartBuilder, imageUri, ORIGINAL_IMAGE_KEY);
                apiServiceProvider.updateEventMedias(multiPartBuilder, null, this);
                break;

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

    private String getImageUrl() {
        switch (TYPE) {
            case Constants.ImageUpdate.FAMILY_LOGO:
                return S3_DEV_IMAGE_URL_SQUARE_DETAILED + IMAGE_BASE_URL + LOGO + family.getLogo();
            case Constants.ImageUpdate.FAMILY_COVER:
                if (family.getGroupOriginalImage() != null) {
                    return S3_DEV_IMAGE_URL_COVER_DETAILED + IMAGE_BASE_URL + "group_original_image/" + family.getGroupOriginalImage();
                } else
                    return S3_DEV_IMAGE_URL_COVER_DETAILED + IMAGE_BASE_URL + COVER_PIC + family.getCoverPic();
            case Constants.ImageUpdate.EVENT_LOGO:
            case EVENT_COVER:
                if (eventDetail.getEventOriginalImage() != null)
                    return S3_DEV_IMAGE_URL_COVER_DETAILED + IMAGE_BASE_URL + "event_original_image/" + eventDetail.getEventOriginalImage();
                else
                    return S3_DEV_IMAGE_URL_COVER_DETAILED + IMAGE_BASE_URL + EVENT_IMAGE + eventDetail.getEventImage();


            case Constants.ImageUpdate.USER_PROFILE_COVER:
                if (familyMember.getProfile().getUserOriginalImage() != null) {
                    return S3_DEV_IMAGE_URL_COVER_DETAILED + IMAGE_BASE_URL + "user_original_image/" + familyMember.getProfile().getUserOriginalImage();
                } else
                    return S3_DEV_IMAGE_URL_COVER_DETAILED + IMAGE_BASE_URL + COVER_PIC + familyMember.getProfile().getCoverPic();
            case Constants.ImageUpdate.USER_PROFILE_LOGO:
                return S3_DEV_IMAGE_URL_SQUARE_DETAILED + IMAGE_BASE_URL + PROFILE_PIC + familyMember.getProfile().getPropic();
            case Constants.ImageUpdate.GENERAL:
                return S3_DEV_IMAGE_URL_COVER_DETAILED + URL;
        }
        return "";
    }

    private String isDefaultImage() {
        switch (TYPE) {
            case Constants.ImageUpdate.FAMILY_LOGO:
                return family.getLogo();
            case Constants.ImageUpdate.FAMILY_COVER:
                if (family.getGroupOriginalImage() != null)
                    return family.getGroupOriginalImage();
                else
                    return family.getCoverPic();
            case Constants.ImageUpdate.EVENT_LOGO:
            case EVENT_COVER:
                if (eventDetail.getEventOriginalImage() != null)
                    return eventDetail.getEventOriginalImage();
                else
                    return eventDetail.getEventImage();

            case Constants.ImageUpdate.USER_PROFILE_COVER:
                if (familyMember != null && familyMember.getProfile() != null && familyMember.getProfile().getUserOriginalImage() != null)
                    return familyMember.getProfile().getUserOriginalImage();
                else if (familyMember != null && familyMember.getProfile() != null && familyMember.getProfile().getCoverPic() != null)
                    return familyMember.getProfile().getCoverPic();
                else return null;
            case Constants.ImageUpdate.USER_PROFILE_LOGO:
                if (familyMember != null && familyMember.getProfile() != null && familyMember.getProfile().getPropic() != null)
                    return familyMember.getProfile().getPropic();
                return null;
            case Constants.ImageUpdate.GENERAL:
                return URL;
        }
        return null;
    }

    private boolean isReadStoragePermissionGranted() {
        return TedPermission.isGranted(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    private void requestPermission(int type) {
        TedPermission.with(this)
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        if (type == 0)
                            goToCoverImagePicker();
                        else
                            imagedownload.performClick();
                    }

                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {

                    }
                })
                .setDeniedMessage("If you reject permission,you can not download/upload images\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();
    }

    private void goToCoverImagePicker() {
        Matisse.from(ImageChangerActivity.this)
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

    private void showImageOptions(View v) {
        PopupMenu popup = new PopupMenu(v.getContext(), v);
        popup.getMenuInflater().inflate(R.menu.popup_menu_image_options, popup.getMenu());
        if (isCoverImageSelected) {
            popup.getMenu().getItem(1).setVisible(true);
        }
        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.changeImage:
                    triggerUpdateImage();
                    break;
                case R.id.changeCropping:
                    if (loadedBitmap != null) {
                        if (TedPermission.isGranted(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            loadedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            String path = MediaStore.Images.Media.insertImage(getContentResolver(), loadedBitmap, "Compressed_Image", null);
                            Uri uri = Uri.parse(path);
                            CropImage.activity(uri)
                                    .setCropMenuCropButtonTitle("set thumbnail")
                                    .setGuidelines(CropImageView.Guidelines.ON)
                                    .setAspectRatio(4, 3)
                                    .start(this);

                        } else {
                            showPermission(2);
                        }
                    } else {
                        Toast.makeText(this, "Please upload a cover picture to change cropping position", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
            return true;
        });

        popup.show();
    }

    private void pickImage() {
        TedPermission.with(this)
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        loadedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        String path = MediaStore.Images.Media.insertImage(getContentResolver(), loadedBitmap, "Compressed_Image", null);
                        Uri uri = Uri.parse(path);
                        CropImage.activity(uri)
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .setAspectRatio(4, 3)
                                .start(ImageChangerActivity.this);
                    }

                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {

                    }
                })
                .setDeniedMessage("If you reject permission,you can not upload images\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();
    }

    private void triggerUpdateImage() {
        switch (TYPE) {
            case Constants.ImageUpdate.FAMILY_LOGO:
            case Constants.ImageUpdate.EVENT_LOGO:
            case Constants.ImageUpdate.USER_PROFILE_LOGO:
                if (isReadStoragePermissionGranted())
//                    CropImage.activity()
//                            .setGuidelines(CropImageView.Guidelines.ON)
//                            .setAspectRatio(1, 1)
//                            .start(this);
                    ImagePicker.with(this)
                            .crop()//Crop image(Optional), Check Customization for more option
                            .compress(1024)			//Final image size will be less than 1 MB(Optional)
                            .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                            .start();
                else
                    showPermission(3);

                break;
            case EVENT_COVER:
            case Constants.ImageUpdate.FAMILY_COVER:
            case Constants.ImageUpdate.USER_PROFILE_COVER:
                if (isReadStoragePermissionGranted())
                    goToCoverImagePicker();
                else
                    showPermission(0);
                break;

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
            if (type == 2) {
                pickImage();
            } else if (type == 3) {
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
                requestPermission(type);
            }
        });
        dialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //family.setLogo(null);
    }
}
