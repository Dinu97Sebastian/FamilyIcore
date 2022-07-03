package com.familheey.app.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.familheey.app.Dialogs.DateSelectorDialog;
import com.familheey.app.Fragments.UserDetailsLevelTwoFragment;
import com.familheey.app.Interfaces.ProgressListener;
import com.familheey.app.Interfaces.RetrofitListener;
import com.familheey.app.Models.ApiCallbackParams;
import com.familheey.app.Models.ErrorData;
import com.familheey.app.Models.Response.ProfileResponse.UserProfile;
import com.familheey.app.Networking.Retrofit.ApiServiceProvider;
import com.familheey.app.Networking.utils.GsonUtils;
import com.familheey.app.R;
import com.familheey.app.Utilities.SharedPref;
import com.familheey.app.Utilities.Utilities;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.MediaType;
import okhttp3.MultipartBody;

import static com.familheey.app.Utilities.Constants.Bundle.ADDRESS;
import static com.familheey.app.Utilities.Constants.Bundle.DATA;
import static com.familheey.app.Utilities.Constants.Bundle.IDENTIFIER;
import static com.familheey.app.Utilities.Constants.Bundle.PLACE;

public class ProfileEditActivity extends AppCompatActivity implements ProgressListener, RetrofitListener, DateSelectorDialog.OnDateSelectedListener, DatePickerDialog.OnDateSetListener {

    public static final int REQUEST_CODE = 100;
    private static final int ORIGIN = UserDetailsLevelTwoFragment.ORIGIN;
    private static final int LIVING_IN = UserDetailsLevelTwoFragment.LIVING_IN;

    @BindView(R.id.profileName)
    TextInputLayout profileName;
    @BindView(R.id.contactNumber)
    TextInputLayout contactNumber;
    @BindView(R.id.email)
    TextInputLayout email;
    @BindView(R.id.dob)
    TextInputLayout dob;
    @BindView(R.id.livingIn)
    TextInputLayout livingIn;
    @BindView(R.id.origin)
    TextInputLayout tiorigin;
    @BindView(R.id.proceed)
    MaterialButton proceed;
    @BindView(R.id.male)
    RadioButton male;
    @BindView(R.id.female)
    RadioButton female;
    @BindView(R.id.gender)
    RadioGroup gender;
    @BindView(R.id.genderRatherNotSay)
    RadioButton genderRatherNotSay;
    @BindView(R.id.toolbar_settings)
    Toolbar toolbar;
    @BindView(R.id.dateOfBirth)
    TextInputEditText dateOfBirth;
    @BindView(R.id.genderContainer)
    MaterialCardView genderContainer;
    @BindView(R.id.dataScroller)
    ScrollView dataScroller;
    @BindView(R.id.allowNotification)
    SwitchCompat allowNotification;
    @BindView(R.id.isUserSearchable)
    SwitchCompat isUserSearchable;
    @BindView(R.id.toolBarTitle)
    TextView toolBarTitle;
    @BindView(R.id.goBack)
    ImageView goBack;


    private SweetAlertDialog progressDialog;
    private UserProfile userProfile;
    private LatLng latLng = null;
    private FusedLocationProviderClient fusedLocationClient;
    private boolean isLivingInEdited = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);
        ButterKnife.bind(this);
        initializeToolbar();
        // Fix this !! Dont know why the object cannot be received as parcelable even it is passed via the intent a sparcelable
        userProfile = GsonUtils.getInstance().getGson().fromJson(getIntent().getStringExtra(DATA), UserProfile.class);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        Objects.requireNonNull(dob.getEditText()).setFocusable(false);
        dob.getEditText().setClickable(true);
        dob.getEditText().setLongClickable(false);
        Objects.requireNonNull(livingIn.getEditText()).setFocusable(false);
        livingIn.getEditText().setClickable(true);
        livingIn.getEditText().setLongClickable(false);
        Objects.requireNonNull(tiorigin.getEditText()).setFocusable(false);
        tiorigin.getEditText().setClickable(true);
        tiorigin.getEditText().setLongClickable(false);
        fillDatas();
    }

    private void initializeToolbar() {
        toolBarTitle.setText("User Settings");
        goBack.setOnClickListener(v -> onBackPressed());
    }

    private void fillDatas() {
        if (userProfile.getProfile().getFullName() != null)
            Objects.requireNonNull(profileName.getEditText()).setText(userProfile.getProfile().getFullName());
        if (userProfile.getProfile().getPhone() != null)
            Objects.requireNonNull(contactNumber.getEditText()).setText(userProfile.getProfile().getPhone());
        if (userProfile.getProfile().getEmail() != null)
            Objects.requireNonNull(email.getEditText()).setText(userProfile.getProfile().getEmail());
        if (userProfile.getProfile().getDob() != null)
            Objects.requireNonNull(dob.getEditText()).setText(userProfile.getProfile().getFormattedDate());
        if (userProfile.getProfile().getGender() != null && userProfile.getProfile().getGender().equalsIgnoreCase("female"))
            female.setChecked(true);
        else if (userProfile.getProfile().getGender() != null && userProfile.getProfile().getGender().equalsIgnoreCase("male"))
            male.setChecked(true);
        else
            genderRatherNotSay.setChecked(true);
        allowNotification.setChecked(userProfile.getProfile().getNotification() != null && userProfile.getProfile().getNotification());
        isUserSearchable.setChecked(userProfile.getProfile().getSearchable() != null && userProfile.getProfile().getSearchable());
        Objects.requireNonNull(livingIn.getEditText()).setText(userProfile.getProfile().getLocation());
        Objects.requireNonNull(tiorigin.getEditText()).setText(userProfile.getProfile().getOrigin());
    }

    @OnClick({R.id.proceed, R.id.dob, R.id.dateOfBirth, R.id.livingIn, R.id.origin, R.id.livingInValue, R.id.originValue})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.livingInValue:
            case R.id.livingIn:
                Intent livingInAddressintent = new Intent(getApplicationContext(), AddressFetchingActivity.class);
                livingInAddressintent.putExtra(IDENTIFIER, LIVING_IN);
                startActivityForResult(livingInAddressintent, AddressFetchingActivity.RequestCode);
                break;
            case R.id.originValue:
            case R.id.origin:
                Intent originAddressIntent = new Intent(getApplicationContext(), AddressFetchingActivity.class);
                originAddressIntent.putExtra(IDENTIFIER, ORIGIN);
                startActivityForResult(originAddressIntent, AddressFetchingActivity.RequestCode);
                break;
            case R.id.proceed:
                updateUseProfileWithLocation();
                break;
            case R.id.dateOfBirth:
                captureDateOfBirth();
                break;
        }
    }

    public boolean isValidName(String name) {
        Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(name);
        boolean b = m.find();
        return !b;
    }

    public void updateUseProfile() {
        if (Objects.requireNonNull(profileName.getEditText()).getText().toString().trim().length() < 2) {
            Toast.makeText(this, "The name must contain at least two alphabets", Toast.LENGTH_SHORT).show();
            return;
        }
        else{
            String userName = (profileName.getEditText()).getText().toString().trim();
            char firstLetter = userName.charAt(0);
            Pattern pattern = Pattern.compile("[^a-zA-Z0-9]");
            Matcher matcher = pattern.matcher(Character.toString(firstLetter));
            boolean isSFirstLatterSpecialCharacter = matcher.find();
            if(isSFirstLatterSpecialCharacter){
                Toast.makeText(this, "First letter of the name must be an alphabet", Toast.LENGTH_SHORT).show();
                return;
            }
        }





        if (Objects.requireNonNull(livingIn.getEditText()).getText().toString().trim().length() == 0) {
            Toast.makeText(this, "City you are residing in is empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if (Objects.requireNonNull(tiorigin.getEditText()).getText().toString().trim().length() == 0) {
            Toast.makeText(this, "City you are residing from is empty", Toast.LENGTH_SHORT).show();
            return;
        }
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(Objects.requireNonNull(MediaType.parse("multipart/form-data")));
        builder.addFormDataPart("id", SharedPref.getUserRegistration().getId());
        builder.addFormDataPart("full_name", profileName.getEditText().getText().toString().trim());
        try {
            if (Objects.requireNonNull(dob.getEditText()).toString().trim().length() > 0) {
                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy", Locale.getDefault());
                Date date = sdf.parse(dob.getEditText().getText().toString());
                SimpleDateFormat ourSdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                assert date != null;
                String parsedDate = ourSdf.format(date);
                builder.addFormDataPart("dob", parsedDate);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        builder.addFormDataPart("location", livingIn.getEditText().getText().toString());
        builder.addFormDataPart("origin", tiorigin.getEditText().getText().toString());
        if (latLng != null) {
            builder.addFormDataPart("lat", String.valueOf(latLng.latitude));
            builder.addFormDataPart("long", String.valueOf(latLng.longitude));
        }
        if (male.isChecked())
            builder.addFormDataPart("gender", "male");
        else if (female.isChecked())
            builder.addFormDataPart("gender", "female");
        else builder.addFormDataPart("gender", "");
        builder.addFormDataPart("notification", String.valueOf(allowNotification.isChecked()));
        builder.addFormDataPart("searchable", String.valueOf(isUserSearchable.isChecked()));
//        addImageToMultiPartBuilder(builder, profileCoverImageUri, "cover_pic");
//        addImageToMultiPartBuilder(builder, profilePictureImageUri, "propic");
        showProgressDialog();
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getApplicationContext());
        apiServiceProvider.updateUserProfile(builder, null, this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AddressFetchingActivity.RequestCode && resultCode == Activity.RESULT_OK) {
            assert data != null;
            if (data.getIntExtra(IDENTIFIER, ORIGIN) == ORIGIN)
                Objects.requireNonNull(tiorigin.getEditText()).setText(data.getStringExtra(ADDRESS));
            else {
                isLivingInEdited = true;
                Objects.requireNonNull(livingIn.getEditText()).setText(data.getStringExtra(ADDRESS));
                Place place = data.getParcelableExtra(PLACE);
                if (place != null && place.getLatLng() != null)
                    latLng = place.getLatLng();
                else latLng = null;
            }
        }
    }

    @Override
    public void hideProgressDialog() {
        if (progressDialog != null)
            progressDialog.dismiss();
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
    public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {
        hideProgressDialog();
        this.userProfile = GsonUtils.getInstance().getGson().fromJson(responseBodyString, UserProfile.class);
        Intent intent = new Intent();
        intent.putExtra(DATA, userProfile);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @Override
    public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
        showErrorDialog(errorData.getMessage());
    }

    @Override
    public void onDateSelected(String date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date parsedDate = sdf.parse(date);
            SimpleDateFormat ourFormat = new SimpleDateFormat("MMM dd yyyy", Locale.getDefault());
            assert parsedDate != null;
            Objects.requireNonNull(dob.getEditText()).setText(ourFormat.format(parsedDate));
        } catch (ParseException e) {
            e.printStackTrace();
            Objects.requireNonNull(dob.getEditText()).setText(date);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent();
        intent.putExtra(DATA, userProfile);
        setResult(Activity.RESULT_CANCELED, intent);
        finish();
    }

    public void captureDateOfBirth() {
        Calendar calendar = Calendar.getInstance();
        try {
            SimpleDateFormat ourFormat = new SimpleDateFormat("MMM dd yyyy", Locale.getDefault());
            Date selectedDate = ourFormat.parse(Objects.requireNonNull(dateOfBirth.getText()).toString());
            assert selectedDate != null;
            calendar.setTimeInMillis(selectedDate.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this, AlertDialog.THEME_HOLO_LIGHT, this,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
        datePickerDialog.show();
        try {
            /*datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setBackgroundColor(Color.parseColor("#7e57c2"));
            datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setBackgroundColor(Color.parseColor("#7e57c2"));*/
            datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.WHITE);
            datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.WHITE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar dateOfBirthCalendar = Calendar.getInstance();
        dateOfBirthCalendar.set(year, month, dayOfMonth);
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        String date = dateFormatter.format(dateOfBirthCalendar.getTime());
        try {
            Date parsedDate = dateFormatter.parse(date);
            SimpleDateFormat ourFormat = new SimpleDateFormat("MMM dd yyyy", Locale.getDefault());
            assert parsedDate != null;
            Objects.requireNonNull(dob.getEditText()).setText(ourFormat.format(parsedDate));
        } catch (ParseException e) {
            e.printStackTrace();
            Objects.requireNonNull(dob.getEditText()).setText(date);
        }
    }

    private void updateUseProfileWithLocation() {
        if (!isLivingInEdited) {
            updateUseProfile();
            return;
        }
        if (latLng != null)
            updateUseProfile();
        else {
            if (TedPermission.isGranted(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION))
                getLastKnownLocation();
            else requestPermission();
        }
    }

    private void requestPermission() {
        TedPermission.with(getApplicationContext())
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        getLastKnownLocation();
                    }

                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {
                        latLng = null;
                        updateUseProfile();
                    }
                })
                .setDeniedMessage("If you reject permission,you do not get perfect suggestions\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
                .check();
    }

    private void getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    } else latLng = null;
                    updateUseProfile();
                })
                .addOnFailureListener(e -> {
                    latLng = null;
                    updateUseProfile();
                    e.printStackTrace();
                });
    }
}
