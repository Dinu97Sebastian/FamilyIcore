package com.familheey.app.Fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.familheey.app.Activities.AddressFetchingActivity;
import com.familheey.app.Activities.ImageChangerActivity;
import com.familheey.app.Dialogs.GroupTypeDialog;
import com.familheey.app.Interfaces.FamilyDashboardListener;
import com.familheey.app.Interfaces.RetrofitListener;
import com.familheey.app.Models.ApiCallbackParams;
import com.familheey.app.Models.ErrorData;
import com.familheey.app.Models.Response.Family;
import com.familheey.app.Networking.Retrofit.ApiServiceProvider;
import com.familheey.app.R;
import com.familheey.app.Utilities.Constants;
import com.familheey.app.Utilities.SharedPref;
import com.familheey.app.Utilities.Utilities;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.material.button.MaterialButton;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static com.familheey.app.Utilities.Constants.ApiPaths.IMAGE_BASE_URL;
import static com.familheey.app.Utilities.Constants.Bundle.ADDRESS;
import static com.familheey.app.Utilities.Constants.Bundle.DATA;
import static com.familheey.app.Utilities.Constants.Bundle.IDENTIFIER;
import static com.familheey.app.Utilities.Constants.Bundle.PLACE;
import static com.familheey.app.Utilities.Constants.Bundle.TYPE;
import static com.familheey.app.Utilities.Constants.ImageUpdate.FAMILY_COVER;
import static com.familheey.app.Utilities.Constants.ImageUpdate.FAMILY_LOGO;
import static com.familheey.app.Utilities.Constants.Paths.COVER_PIC;
import static com.familheey.app.Utilities.Constants.Paths.LOGO;

public class FamilyEditFragment extends Fragment {

    @BindView(R.id.goBack)
    ImageView goBack;

    @BindView(R.id.edtxOther)
    EditText edtxOther;

    @BindView(R.id.toolBarTitle)
    TextView toolBarTitle;
    @BindView(R.id.family_category)
    EditText familyCategory;
    @BindView(R.id.family_name)
    EditText familyName;
    @BindView(R.id.cover)
    AppCompatImageView cover;
    @BindView(R.id.logo)
    AppCompatImageView logo;
    @BindView(R.id.cancel)
    MaterialButton cancel;
    @BindView(R.id.submit)
    MaterialButton submit;
    @BindView(R.id.public_type)
    RadioButton publicType;
    @BindView(R.id.private_type)
    RadioButton privateType;
    @BindView(R.id.family_type)
    RadioGroup familyType;
    @BindView(R.id.public_searchable)
    RadioButton publicSearchable;
    @BindView(R.id.private_searchable)
    RadioButton privateSearchable;
    @BindView(R.id.searchable)
    RadioGroup searchable;
    @BindView(R.id.autocomplete_fragment)
    EditText livingAutocomplete;
    @BindView(R.id.linkable)
    RadioButton linkable;
    @BindView(R.id.nonLinkable)
    RadioButton nonLinkable;
    private Family family;

    private String region;
    private boolean isCoverPictureCapturing;
    private FamilyDashboardListener mListener;
    private Uri coverImageUri = null;
    private Uri profileCoverImageUri = null;
    private boolean isOther;
    private FusedLocationProviderClient fusedLocationClient;
    private LatLng latLng = null;
    private boolean isRegionEdited = false;

    public static FamilyEditFragment newInstance(Family family) {
        FamilyEditFragment fragment = new FamilyEditFragment();
        Bundle args = new Bundle();
        args.putParcelable(Constants.Bundle.DATA, family);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            family = getArguments().getParcelable(Constants.Bundle.DATA);
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_family_edit, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (FamilyDashboardListener) context;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeToolbar();
        prefillDatas();
        Utilities.removeEditableButClickable(livingAutocomplete, familyCategory);
    }

    private void initializeToolbar() {
        toolBarTitle.setText("Basic Settings");
        goBack.setOnClickListener(v -> requireActivity().onBackPressed());
    }

    private void addImageToMultiPartBuilder(MultipartBody.Builder builder, Uri imageUri, String keyname) {
        if (imageUri == null)
            return;
        File imageFile = new File(imageUri.getPath());
        final MediaType MEDIA_TYPE = imageUri.toString().endsWith("png") ? MediaType.parse("image/png") : MediaType.parse("image/jpeg");
        RequestBody profilePictureRequestBody = RequestBody.create(MEDIA_TYPE, imageFile);
        builder.addFormDataPart(keyname, imageFile.getName(), profilePictureRequestBody);
    }

    private boolean validate() {
        if (familyName.getText().toString().trim().length() < 2) {
            familyName.setError("The name must contain at least two characters");
            familyName.requestFocus();
            return false;
        }
        else{
            String name = familyName.getText().toString();
            char firstLetter = name.charAt(0);
            Pattern pattern = Pattern.compile("[^a-zA-Z0-9]");
            Matcher matcher = pattern.matcher(Character.toString(firstLetter));
            boolean isSFirstLatterSpecialCharacter = matcher.find();
            if(isSFirstLatterSpecialCharacter){
                familyName.setError("First letter of the name must be an alphabet");
                return false;
            }
        }
        return !region.isEmpty();
    }

    private void prefillDatas() {
        if (family != null) {
            familyCategory.setText(family.getGroupCategory());


            familyName.setText(family.getGroupName());
            livingAutocomplete.setText(family.getBaseRegion());
            region = family.getBaseRegion();
            if (family.getGroupType() != null && family.getGroupType().equals("public")) {
                publicType.setChecked(true);
            } else
                privateType.setChecked(true);
            if (family.getSearchable() != null && family.getSearchable()) {
                publicSearchable.setChecked(true);
            } else
                privateSearchable.setChecked(true);
            if (family.getIsLinkable() != null && family.getIsLinkable())
                linkable.setChecked(true);
            else
                nonLinkable.setChecked(true);
            Glide.with(getActivity())
                    .load(IMAGE_BASE_URL + COVER_PIC + family.getCoverPic())
                    .apply(Utilities.getCurvedRequestOptions())
                    .placeholder(R.drawable.profile_dashboard_background)
                    .into(cover);
            Glide.with(getActivity())
                    .load(IMAGE_BASE_URL + LOGO + family.getLogo())
                    .apply(Utilities.getCurvedRequestOptions())
                    .placeholder(R.drawable.profile_dashboard_background)
                    .into(logo);
        }
    }

    @OnClick({R.id.cover, R.id.logo, R.id.cancel, R.id.submit, R.id.autocomplete_fragment, R.id.family_category})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cancel:
                requireActivity().onBackPressed();
                break;
            case R.id.cover:
                isCoverPictureCapturing = true;
                Intent familyCoverEditIntent = new Intent(getContext(), ImageChangerActivity.class);
                familyCoverEditIntent.putExtra(DATA, family);
                familyCoverEditIntent.putExtra(TYPE, FAMILY_COVER);
                familyCoverEditIntent.putExtra(IDENTIFIER, family.isAdmin());
                startActivityForResult(familyCoverEditIntent, ImageChangerActivity.REQUEST_CODE);
                break;
            case R.id.logo:
                isCoverPictureCapturing = false;
                Intent familyLogoEditIntent = new Intent(getContext(), ImageChangerActivity.class);
                familyLogoEditIntent.putExtra(DATA, family);
                familyLogoEditIntent.putExtra("logo",family.getLogo());
                familyLogoEditIntent.putExtra(TYPE, FAMILY_LOGO);
                familyLogoEditIntent.putExtra(IDENTIFIER, family.isAdmin());
                startActivityForResult(familyLogoEditIntent, ImageChangerActivity.REQUEST_CODE);
                break;
            case R.id.submit:
                if (validate()) {
                    updateFamilyWithLocation();
                }
                break;


            case R.id.family_category:
                GroupTypeDialog.newInstance(this, new ArrayList<>()).show(getFragmentManager(), "GroupTypeDialog");
                break;
            case R.id.autocomplete_fragment:
                Intent intent = new Intent(getContext(), AddressFetchingActivity.class);
                startActivityForResult(intent, AddressFetchingActivity.RequestCode);
                break;
        }
    }

    private void updateFamily() {
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MediaType.parse("multipart/form-data"));
        builder.addFormDataPart("id", family.getId().toString());
        builder.addFormDataPart("user_id", SharedPref.getUserRegistration().getId());
        if (isOther) {
            builder.addFormDataPart("group_category", edtxOther.getText().toString());
        } else {
            builder.addFormDataPart("group_category", familyCategory.getText().toString());

        }
        builder.addFormDataPart("group_name", familyName.getText().toString().trim());
        builder.addFormDataPart("base_region", region);
        builder.addFormDataPart("group_type", (publicType.isChecked() ? "public" : "private"));
        builder.addFormDataPart("searchable", String.valueOf(publicSearchable.isChecked()));
        builder.addFormDataPart("is_linkable", String.valueOf(linkable.isChecked()));
        if (latLng != null) {
            builder.addFormDataPart("lat", String.valueOf(latLng.latitude));
            builder.addFormDataPart("long", String.valueOf(latLng.longitude));
        }
        if (profileCoverImageUri != null)
            addImageToMultiPartBuilder(builder, coverImageUri, "cover_pic");
        if (coverImageUri != null)
            addImageToMultiPartBuilder(builder, profileCoverImageUri, "logo");
        mListener.showProgressDialog();


        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getActivity());
        apiServiceProvider.EdiFamily(builder, null, new RetrofitListener() {
            @Override
            public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {
                Toast.makeText(getActivity(), "Family Updated", Toast.LENGTH_SHORT).show();
                mListener.hideProgressDialog();
                mListener.onFamilyUpdated(true);
            }

            @Override
            public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
                mListener.showErrorDialog(errorData.getMessage());
                Toast.makeText(getActivity(), "Updated failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GroupTypeDialog.REQUEST_CODE) {
            String familyCategoryStr = data.getStringExtra(DATA);
            familyCategory.setText(familyCategoryStr);
            if (familyCategoryStr != null && familyCategoryStr.equals("Others")) {
                isOther = true;
                edtxOther.setVisibility(View.VISIBLE);

            } else {
                isOther = false;
                edtxOther.setVisibility(View.GONE);
            }


        }


        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == Activity.RESULT_OK) {
                if (isCoverPictureCapturing) {
                    coverImageUri = result.getUri();
                    Glide.with(getActivity())
                            .load(coverImageUri)
                            .apply(Utilities.getCurvedRequestOptions())
                            .placeholder(R.drawable.profile_dashboard_background)
                            .into(cover);
                } else {
                    profileCoverImageUri = result.getUri();
                    Glide.with(getActivity())
                            .load(profileCoverImageUri)
                            .apply(Utilities.getCurvedRequestOptions())
                            .placeholder(R.drawable.profile_dashboard_background)
                            .into(logo);
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

            }
        } else if (requestCode == AddressFetchingActivity.RequestCode && resultCode == Activity.RESULT_OK) {
            livingAutocomplete.setText(data.getStringExtra(ADDRESS));
            region = data.getStringExtra(ADDRESS);
            isRegionEdited = true;
            Place place = data.getParcelableExtra(PLACE);
            if (place != null && place.getLatLng() != null)
                latLng = place.getLatLng();
            else latLng = null;
        } else if (requestCode == Constants.RequestCode.REQUEST_CODE) {
            familyCategory.setText(data.getStringExtra(Constants.Bundle.DATA));
        } else if (requestCode == ImageChangerActivity.REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                if (isCoverPictureCapturing) {
                    coverImageUri = Uri.parse(data.getStringExtra(DATA));
                    Glide.with(getActivity())
                            .load(coverImageUri)
                            .apply(Utilities.getCurvedRequestOptions())
                            .placeholder(R.drawable.profile_dashboard_background)
                            .into(cover);
                } else {
                    profileCoverImageUri = Uri.parse(data.getStringExtra(DATA));
                    Glide.with(getActivity())
                            .load(profileCoverImageUri)
                            .apply(Utilities.getCurvedRequestOptions())
                            .placeholder(R.drawable.profile_dashboard_background)
                            .into(logo);
                }
            }
        }
    }

    private void updateFamilyWithLocation() {
        if (!isRegionEdited) {
            updateFamily();
            return;
        }
        if (latLng != null)
            updateFamily();
        else {
            if (TedPermission.isGranted(getContext(), Manifest.permission.ACCESS_FINE_LOCATION))
                getLastKnownLocation();
            else requestPermission();
        }
    }

    private void requestPermission() {
        TedPermission.with(getContext())
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        getLastKnownLocation();
                    }

                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {
                        latLng = null;
                        updateFamily();
                    }
                })
                .setDeniedMessage("If you reject permission,you do not get perfect suggestions\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
                .check();
    }

    private void getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    } else latLng = null;
                    updateFamily();
                })
                .addOnFailureListener(e -> {
                    latLng = null;
                    updateFamily();
                    e.printStackTrace();
                });
    }
}
