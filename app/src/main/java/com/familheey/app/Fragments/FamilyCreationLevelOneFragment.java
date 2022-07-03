package com.familheey.app.Fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.familheey.app.Activities.AddressFetchingActivity;
import com.familheey.app.Dialogs.GroupTypeDialog;
import com.familheey.app.FamilheeyApplication;
import com.familheey.app.Interfaces.FamilyCreationListener;
import com.familheey.app.Interfaces.RetrofitListener;
import com.familheey.app.Models.ApiCallbackParams;
import com.familheey.app.Models.ErrorData;
import com.familheey.app.Models.FamilyCreation;
import com.familheey.app.Models.Request.CreateFamilyRequest;
import com.familheey.app.Models.Request.SimilarFamilyRequest;
import com.familheey.app.Models.Response.Family;
import com.familheey.app.Networking.Retrofit.ApiServiceProvider;
import com.familheey.app.Parsers.FamilyParser;
import com.familheey.app.R;
import com.familheey.app.Topic.MainActivity;
import com.familheey.app.Utilities.Constants;
import com.familheey.app.Utilities.SharedPref;
import com.familheey.app.Utilities.Utilities;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.material.button.MaterialButton;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.familheey.app.Utilities.Constants.Bundle.ADDITIONAL_DATA;
import static com.familheey.app.Utilities.Constants.Bundle.ADDRESS;
import static com.familheey.app.Utilities.Constants.Bundle.DATA;
import static com.familheey.app.Utilities.Constants.Bundle.IS_EXISTING_USER;
import static com.familheey.app.Utilities.Constants.Bundle.PLACE;

public class FamilyCreationLevelOneFragment extends Fragment implements RetrofitListener {


    @BindView(R.id.proceed)
    MaterialButton proceed;
    @BindView(R.id.familyName)
    EditText familyName;
    @BindView(R.id.familyType)
    EditText familyType;
    @BindView(R.id.regionOrLocation)
    EditText regionOrLocation;

    @BindView(R.id.edtxOther)
    EditText edtxOther;
    @BindView(R.id.skip)
    TextView skip;

    private Family family;
    private SimilarFamilyRequest similarFamilyRequest;
    private FamilyCreationListener mListener;
    private String familyTypeData = "";

    private final ArrayList<Family> similarFamilies = new ArrayList<>();
    private boolean isOther = false;
    private FusedLocationProviderClient fusedLocationClient;
    private FamilyCreation familyCreation;
    private LatLng latLng = null;
    private boolean isExistUser=false;
    public FamilyCreationLevelOneFragment() {
        // Required empty public constructor
    }

    public static FamilyCreationLevelOneFragment newInstance(Family family,boolean isExist) {
        FamilyCreationLevelOneFragment fragment = new FamilyCreationLevelOneFragment();
        Bundle args = new Bundle();
        args.putParcelable(DATA, family);
        args.putBoolean(IS_EXISTING_USER, isExist);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            family = getArguments().getParcelable(DATA);
            isExistUser=getArguments().getBoolean(IS_EXISTING_USER);

        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_family_creation_level_one, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        setKeyboardVisibilityListener(getActivity());
    }

    public static void setKeyboardVisibilityListener(Activity activity) {
        View contentView = activity.findViewById(android.R.id.content);
        contentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            private int mPreviousHeight;

            @Override
            public void onGlobalLayout() {
                int newHeight = contentView.getHeight();
                if (mPreviousHeight != 0) {

                }
                mPreviousHeight = newHeight;
            }
        });
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
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @OnClick({R.id.proceed, R.id.familyType, R.id.regionOrLocation,R.id.skip})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.proceed:
                if (!isValid())
                    return;
                if (familyCreation.getId().length() > 0) {
                    createFamilyWithLocation();
                } else {
                    if (mListener.getFamilyIdToLink() != null && mListener.getFamilyIdToLink().length() > 0) {
                        if (mListener.familyNeedsLinking()) {
                            createFamilyWithLocation();
                        } else {
                            mListener.loadFamilyCreationLevelTwo(family);
                        }
                    } else {
                        similarFamilyRequest = new SimilarFamilyRequest(familyName.getText().toString().trim(), familyType.getText().toString(), regionOrLocation.getText().toString(), SharedPref.getUserRegistration().getId());
                        fetchSimilarFamilies();
                    }
                }
                break;
            case R.id.familyType:
                GroupTypeDialog groupTypeDialog = GroupTypeDialog.newInstance(this, new ArrayList<>());
                groupTypeDialog.show(getFragmentManager(), "GroupTypeDialog");
                break;
            case R.id.regionOrLocation:
                familyTypeData = familyType.getText().toString();
                Intent intent = new Intent(getContext(), AddressFetchingActivity.class);
                intent.putExtra(ADDITIONAL_DATA, familyType.getText().toString());
                startActivityForResult(intent, AddressFetchingActivity.RequestCode);
                break;
            case R.id.skip:
                Intent intents = new Intent(getContext(), MainActivity.class);
                intents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intents);
                break;
        }
    }

    private boolean isValid() {
        boolean isValid = true;

        if (familyName.getText().toString().trim().length() < 2) {
            isValid = false;
            familyName.requestFocus();
            familyName.setError("The name must contain at least two characters");
            return false;
        }
        else{
            String name = familyName.getText().toString().trim();//(Megha)trimmed white space
            char firstLetter = name.charAt(0);
            Pattern pattern = Pattern.compile("[^a-zA-Z0-9]");
            Matcher matcher = pattern.matcher(Character.toString(firstLetter));
            boolean isSFirstLatterSpecialCharacter = matcher.find();
                if(isSFirstLatterSpecialCharacter){
                familyName.setError("First letter of the name must be an alphabet");
                return false;
            }
        }

        if (familyType.getText().toString().trim().length() == 0) {
            isValid = false;
            familyType.requestFocus();
            familyType.setError("Required");
            return false;
        }

        if (regionOrLocation.getText().toString().trim().length() == 0) {
            isValid = false;
            regionOrLocation.requestFocus();
            regionOrLocation.setError("Required");
            return false;
        }
        return isValid;
    }

    private void fetchSimilarFamilies() {
        mListener.showProgressDialog();
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getContext());
        apiServiceProvider.fetchSimilarFamilies(similarFamilyRequest, null, this);
    }

    private void createFamilyWithLocation() {
        if (latLng != null)
            createFamily();
        else {
            if (TedPermission.isGranted(getContext(), Manifest.permission.ACCESS_FINE_LOCATION))
                getLastKnownLocation();
            else requestPermission();
        }
    }

    private void createFamily() {
        mListener.showProgressDialog();
        CreateFamilyRequest createFamilyRequest = new CreateFamilyRequest();
        createFamilyRequest.setBaseRegion(regionOrLocation.getText().toString());
        createFamilyRequest.setActive(false);
        if (isOther) {
            createFamilyRequest.setGroupCategory(edtxOther.getText().toString());
        } else {
            createFamilyRequest.setGroupCategory(familyType.getText().toString());
        }
        createFamilyRequest.setGroupName(familyName.getText().toString().trim());
        if (latLng != null) {
            createFamilyRequest.setLat(latLng.latitude);
            createFamilyRequest.setLng(latLng.longitude);
        }
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getContext());
        if (familyCreation.getId().length() > 0) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("group_category", createFamilyRequest.getGroupCategory());
            jsonObject.addProperty("group_name", createFamilyRequest.getGroupName());
            jsonObject.addProperty("created_by", createFamilyRequest.getCreatedBy());
            jsonObject.addProperty("base_region", createFamilyRequest.getBaseRegion());
            jsonObject.addProperty("id", familyCreation.getId());
            jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
            jsonObject.addProperty("is_active", false);
            if (latLng != null) {
                jsonObject.addProperty("lat", latLng.latitude);
                jsonObject.addProperty("long", latLng.longitude);
            }
            apiServiceProvider.updateFamily(jsonObject, null, this);
        } else apiServiceProvider.createFamily(createFamilyRequest, null, this);
    }

    @Override
    public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {
        switch (apiFlag) {
            case Constants.ApiFlags.FETCH_SIMILAR_FAMILIES:
                similarFamilies.clear();
                similarFamilies.addAll(FamilyParser.parseLinkedFamilies(responseBodyString));
                if (mListener.familyNeedsLinking()) {
                    linkFamily();
                } else {
                    if (similarFamilies.size() > 0) {
                        mListener.hideProgressDialog();
                        mListener.loadSimilarFamilies(similarFamilyRequest, similarFamilies);
                    } else {
                        createFamilyWithLocation();
                    }
                }
                break;
            default:
                mListener.hideProgressDialog();
                try {
                    family = FamilyParser.parseFamily(responseBodyString);
                    familyCreation.setId(family.getId() + "");
                    familyCreation.setName(family.getGroupName());
                    familyCreation.setLocation(family.getBaseRegion());
                    familyCreation.setType(family.getGroupCategory());
                    familyCreation.setLatLng(latLng);
                    if (mListener.familyNeedsLinking()) {
                        familyCreation.setLinkable(true);
                        linkFamily();
                    } else {
                        mListener.loadFamilyCreationLevelTwo(family);
                    }
                } catch (NullPointerException | JsonParseException e) {
                    e.printStackTrace();
                    Utilities.getErrorDialog(getContext(), Constants.SOMETHING_WRONG);
                }
        }
    }

    @Override
    public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
        mListener.showErrorDialog(Constants.SOMETHING_WRONG);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        familyCreation = mListener.getFamilyCreation();
        Utilities.attachEmptyListener(familyName, familyType);
        familyType.setLongClickable(false);
        familyType.setFocusable(false);
        familyType.setClickable(true);
        regionOrLocation.setLongClickable(false);
        regionOrLocation.setFocusable(false);
        regionOrLocation.setClickable(true);
        if(isExistUser){
            skip.setVisibility( View.VISIBLE );
        }
        prefillDatas();
    }

    private void prefillDatas() {
        familyName.setText(familyCreation.getName());
        familyType.setText(familyCreation.getType());
        regionOrLocation.setText(familyCreation.getLocation());
        familyName.setError(null);
        familyType.setError(null);
        regionOrLocation.setError(null);


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AddressFetchingActivity.RequestCode) {
            if (resultCode == Activity.RESULT_OK) {
                regionOrLocation.setError(null);
                Place place = data.getParcelableExtra(PLACE);
                if (place != null && place.getLatLng() != null)
                    latLng = place.getLatLng();
                else latLng = null;
                regionOrLocation.setText(data.getStringExtra(ADDRESS));
            }
        } else if (requestCode == GroupTypeDialog.REQUEST_CODE) {
            String familyTypeStr = "";
            if (data.hasExtra(DATA)) {
                familyType.setError(null);
                familyTypeStr = data.getStringExtra(DATA);
                familyType.setText(familyTypeStr);
            }
            if (familyTypeStr.equals("Others")) {
                isOther = true;
                edtxOther.setVisibility(View.VISIBLE);

            } else {
                isOther = false;
                edtxOther.setVisibility(View.GONE);
            }
        }
    }

    private void linkFamily() {
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(FamilheeyApplication.getInstance());
        JsonObject jsonObject = new JsonObject();
        JsonArray selectedFamiliesIds = new JsonArray();
        selectedFamiliesIds.add(mListener.getFamilyIdToLink());
        jsonObject.addProperty("from_group", family.getId().toString());
        jsonObject.add("to_group", selectedFamiliesIds);
        jsonObject.addProperty("requested_by", SharedPref.getUserRegistration().getId());
        apiServiceProvider.requestLinkFamily(jsonObject, null, new RetrofitListener() {
            @Override
            public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {
                mListener.hideProgressDialog();
                mListener.setLinked(true);
                mListener.loadFamilyCreationLevelTwo(family);
            }

            @Override
            public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
                mListener.showErrorDialog(Constants.SOMETHING_WRONG);
            }
        });
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
                        createFamily();
                    }
                })
                .setDeniedMessage("If you reject permission,you do not get perfect suggestions\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
                .check();
    }

    private void getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    } else latLng = null;
                    createFamily();
                })
                .addOnFailureListener(e -> {
                    latLng = null;
                    createFamily();
                    e.printStackTrace();
                });
    }
}
