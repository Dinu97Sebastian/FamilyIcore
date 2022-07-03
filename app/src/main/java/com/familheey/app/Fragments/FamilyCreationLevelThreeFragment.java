package com.familheey.app.Fragments;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.familheey.app.CustomViews.Buttons.RegularButton;
import com.familheey.app.FamilheeyApplication;
import com.familheey.app.Interfaces.FamilyCreationListener;
import com.familheey.app.Interfaces.RetrofitListener;
import com.familheey.app.Models.ApiCallbackParams;
import com.familheey.app.Models.ErrorData;
import com.familheey.app.Models.FamilyCreation;
import com.familheey.app.Models.Response.Family;
import com.familheey.app.Networking.Retrofit.ApiServiceProvider;
import com.familheey.app.Parsers.FamilyParser;
import com.familheey.app.R;
import com.familheey.app.Utilities.Constants;
import com.familheey.app.Utilities.FileUtil;
import com.familheey.app.Utilities.GifSizeFilter;
import com.familheey.app.Utilities.SharedPref;
import com.familheey.app.Utilities.Utilities;
import com.google.android.material.button.MaterialButton;
import com.google.gson.JsonParseException;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.filter.Filter;

import java.io.File;
import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static android.app.Activity.RESULT_OK;
import static com.familheey.app.Utilities.Constants.COMPRESSION_QUALITY;

public class FamilyCreationLevelThreeFragment extends Fragment implements RetrofitListener {

    private static final int REQUEST_CODE_CROP = 30;
    @BindView(R.id.back)
    RegularButton back;
    @BindView(R.id.proceed)
    MaterialButton proceed;
    @BindView(R.id.imageCoverPic)
    ImageView imageCoverPic;
    @BindView(R.id.imageLogo)
    ImageView imageLogo;
    @BindView(R.id.skip)
    TextView skip;
    @BindView(R.id.labelCoverPic)
    TextView labelCoverPic;
    @BindView(R.id.labelLogo)
    TextView labelLogo;
    @BindView(R.id.labelPleaseTakePhoto)
    TextView labelPleaseTakePhoto;
    @BindView(R.id.addCoverPicSign)
    ImageView addCoverPicSign;
    @BindView(R.id.addLogoPicSign)
    ImageView addLogoPicSign;

    private Uri coverPicUri, logoUri, coverPicOriginalUri;
    private FamilyCreationListener mListener;
    private Family family;
    private FamilyCreation familyCreation;

    public FamilyCreationLevelThreeFragment() {
        // Required empty public constructor
    }


    public static FamilyCreationLevelThreeFragment newInstance(Family family) {
        FamilyCreationLevelThreeFragment fragment = new FamilyCreationLevelThreeFragment();
        Bundle args = new Bundle();
        args.putParcelable(Constants.Bundle.DATA, family);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            family = getArguments().getParcelable(Constants.Bundle.DATA);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_family_creation_level_three, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        familyCreation = mListener.getFamilyCreation();
        coverPicOriginalUri = familyCreation.getUnCroppedCoverPicUri();
        coverPicUri = familyCreation.getCoverPicUri();
        logoUri = familyCreation.getLogoUri();
        if (coverPicUri != null) {
            Glide.with(getContext())
                    .load(coverPicUri)
                    .apply(Utilities.getCurvedRequestOptions())
                    .into(imageCoverPic);
        }
        if (logoUri != null) {
            Glide.with(getContext())
                    .load(logoUri)
                    .apply(Utilities.getCurvedRequestOptions())
                    .into(imageLogo);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FamilyCreationListener) {
            mListener = (FamilyCreationListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement FamilyCreationListener");
        }
    }

    @Override
    public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {
        mListener.hideProgressDialog();
        try {
            family = FamilyParser.parseFamily(responseBodyString);
            mListener.loadFamilyCreationLevelFour(family);
        } catch (NullPointerException | JsonParseException e) {
            e.printStackTrace();
            mListener.showErrorDialog(Constants.SOMETHING_WRONG);
        }
    }

    @Override
    public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
        mListener.showErrorDialog(Constants.SOMETHING_WRONG);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @OnClick({R.id.back, R.id.proceed, R.id.imageCoverPic, R.id.imageLogo, R.id.skip})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                break;
            case R.id.proceed:
                if (isValid())
                    updateGroupImages();
                else
                    mListener.loadFamilyCreationLevelFour(family);
                break;
            case R.id.imageCoverPic:
                labelCoverPic.setTag(Constants.Selections.SELECTED);
                labelLogo.setTag(Constants.Selections.UN_SELECTED);
                if (isReadStoragePermissionGranted())
                    captureCoverPic();
                else
                    showPermission();
                break;
            case R.id.imageLogo:
                labelLogo.setTag(Constants.Selections.SELECTED);
                labelCoverPic.setTag(Constants.Selections.UN_SELECTED);
                if (isReadStoragePermissionGranted())
                    captureLogo();
                else
                    showPermission();
                break;
            case R.id.skip:
                mListener.loadFamilyCreationLevelFour(family);
                break;
        }
        //changeText();
    }

//    private void changeText() {
//        String text = "Please take a moment to upload your family cover pic as well as logo";
//        SpannableString ss1 = new SpannableString(text);
//        ss1.setSpan(new ForegroundColorSpan(Color.RED), 0, 5, 0);
//        TextView tv = labelPleaseTakePhoto.findViewById(R.id.labelPleaseTakePhoto);
//        tv.setText(ss1);
//    }

    private void captureCoverPic() {
        Matisse.from(FamilyCreationLevelThreeFragment.this)
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

    private void captureLogo() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .setCropShape(CropImageView.CropShape.RECTANGLE)
                .start(getContext(), this);
    }

//    private void avoidAttachment() {
//        if (SharedPref.getUserRegistration().getId().equalsIgnoreCase(family.getCreatedBy())) {
//            addCoverPicSign.setVisibility(View.VISIBLE);
//            addLogoPicSign.setVisibility(View.VISIBLE);
//            if (family.getHistoryImages() == null || family.getHistoryImages().size() > 0) {
//                addCoverPicSign.setVisibility(View.VISIBLE);
//                addLogoPicSign.setVisibility(View.VISIBLE);
//            }
//
//        } else {
//            addCoverPicSign.setVisibility(View.INVISIBLE);
//            addLogoPicSign.setVisibility(View.INVISIBLE);
//        }


    private boolean isValid() {
        return coverPicUri != null || logoUri != null;
    }

    private void updateGroupImages() {
        mListener.showProgressDialog();
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getContext());
        MultipartBody.Builder multiPartBodyBuilder = new MultipartBody.Builder().setType(MediaType.parse("multipart/form-data"));
        multiPartBodyBuilder.addFormDataPart("id", family.getId().toString());
        multiPartBodyBuilder.addFormDataPart("user_id", SharedPref.getUserRegistration().getId());
        multiPartBodyBuilder.addFormDataPart("intro", familyCreation.getIntroduction());
        if (coverPicUri != null) {
            File coverPicFile = FileUtil.getFile(FamilheeyApplication.getInstance().getApplicationContext(), coverPicUri);
            final MediaType MEDIA_TYPE = coverPicUri.toString().endsWith("png") ? MediaType.parse("image/png") : MediaType.parse("image/jpeg");
            try {
                File compressedFile = new Compressor(getContext()).setQuality(COMPRESSION_QUALITY).compressToFile(coverPicFile);
                if (compressedFile != null)
                    coverPicFile = compressedFile;
            } catch (IOException e) {
                e.printStackTrace();
            }
            RequestBody profilePictureRequestBody = RequestBody.create(MEDIA_TYPE, coverPicFile);
            multiPartBodyBuilder.addFormDataPart("cover_pic", coverPicFile.getName(), profilePictureRequestBody);
        }
        if (logoUri != null) {
            File logoFile = FileUtil.getFile(FamilheeyApplication.getInstance().getApplicationContext(), logoUri);
            try {
                File compressedFile = new Compressor(getContext()).setQuality(COMPRESSION_QUALITY).compressToFile(logoFile);
                if (compressedFile != null)
                    logoFile = compressedFile;
            } catch (IOException e) {
                e.printStackTrace();
            }
            final MediaType MEDIA_TYPE = logoUri.toString().endsWith("png") ? MediaType.parse("image/png") : MediaType.parse("image/jpeg");
            RequestBody profilePictureRequestBody = RequestBody.create(MEDIA_TYPE, logoFile);
            multiPartBodyBuilder.addFormDataPart("logo", logoFile.getName(), profilePictureRequestBody);
        }
        if (coverPicOriginalUri != null) {
            File logoFile = FileUtil.getFile(FamilheeyApplication.getInstance().getApplicationContext(), coverPicOriginalUri);
            final MediaType MEDIA_TYPE = coverPicOriginalUri.toString().endsWith("png") ? MediaType.parse("image/png") : MediaType.parse("image/jpeg");
            try {
                File compressedFile = new Compressor(getContext()).setQuality(COMPRESSION_QUALITY).compressToFile(logoFile);
                if (compressedFile != null)
                    logoFile = compressedFile;
            } catch (IOException e) {
                e.printStackTrace();
            }
            RequestBody profilePictureRequestBody = RequestBody.create(MEDIA_TYPE, logoFile);
            multiPartBodyBuilder.addFormDataPart("group_original_image", logoFile.getName(), profilePictureRequestBody);
        }
        apiServiceProvider.updateFamily(multiPartBodyBuilder.build(), null, this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                if (labelLogo.getTag().toString().equalsIgnoreCase(Constants.Selections.SELECTED)) {
                    logoUri = result.getUri();
                    familyCreation.setLogoUri(result.getUri());
                    if (addLogoPicSign !=null){
                        addLogoPicSign.setVisibility(View.GONE);
                        labelLogo.setVisibility(View.GONE);
                    }
                    Glide.with(getContext())
                            .load(logoUri)
                            .apply(Utilities.getCurvedRequestOptions())
                            .into(imageLogo);
                } else {
                    coverPicUri = result.getUri();
                    familyCreation.setCoverPicUri(result.getUri());
                    if (addCoverPicSign != null ) {
                        addCoverPicSign.setVisibility(View.GONE);
                        labelCoverPic.setVisibility(View.GONE);
                    }
                    Glide.with(getContext())
                            .load(coverPicUri)
                            .apply(Utilities.getCurvedRequestOptions())
                            .into(imageCoverPic);
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(getContext(), "Something error occured!! Please try again later", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_CODE_CROP) {
            if (resultCode == RESULT_OK && null != data) {
                coverPicOriginalUri = Matisse.obtainResult(data).get(0);
                familyCreation.setUnCroppedCoverPicUri(Matisse.obtainResult(data).get(0));
                CropImage.activity(coverPicOriginalUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(4, 3)
                        .start(getContext(), FamilyCreationLevelThreeFragment.this);
            }
        }
    }

    private boolean isReadStoragePermissionGranted() {
        return TedPermission.isGranted(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    private void requestPermission() {
        TedPermission.with(getContext())
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        if (labelLogo.getTag().toString().equalsIgnoreCase(Constants.Selections.SELECTED)) {
                            captureLogo();
                        } else {
                            captureCoverPic();
                        }
                    }

                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {

                    }
                })
                .setDeniedMessage("If you reject permission,you can not upload images\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();
    }


    private void showPermission() {
        Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialogue_permission);
        Window window = dialog.getWindow();
        assert window != null;
        window.setBackgroundDrawableResource(android.R.color.transparent);
        dialog.findViewById(R.id.btn_cancel).setOnClickListener(view -> dialog.dismiss());
        dialog.findViewById(R.id.btn_continue).setOnClickListener(view -> {
            dialog.dismiss();
            requestPermission();
        });
        dialog.show();
    }
}
