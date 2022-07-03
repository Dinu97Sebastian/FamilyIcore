package com.familheey.app.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.familheey.app.Interfaces.RetrofitListener;
import com.familheey.app.Models.ApiCallbackParams;
import com.familheey.app.Models.ErrorData;
import com.familheey.app.Models.Response.UserRegistrationResponse;
import com.familheey.app.Networking.Retrofit.ApiServiceProvider;
import com.familheey.app.Networking.utils.GsonUtils;
import com.familheey.app.R;
import com.familheey.app.Utilities.Constants;
import com.familheey.app.Utilities.SharedPref;
import com.familheey.app.Utilities.Utilities;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.familheey.app.Utilities.Constants.Bundle.JOIN_FAMILY_ID;
import static com.familheey.app.Utilities.Constants.Bundle.TO_CREATE_FAMILY;

public class UserRegistrationActivity extends AppCompatActivity implements RetrofitListener {

//    public static final int LIVING_IN = 9856;
//    public static final int ORIGIN = 4512;
//
////    @BindView(R.id.headerImage)
////    ImageView headerImage;
//

    @BindView(R.id.name)
    EditText name;
    @BindView(R.id.email)
    EditText email;
    @BindView(R.id.phone)
    EditText phone;


    @BindView(R.id.done)
    Button done;

    private SweetAlertDialog progressDialog;
     private UserRegistrationResponse userRegistration;
    private boolean toCreateFamily = false;
    private String joinFamilyId = "";

//    private LatLng latLng = null;
//    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);
        ButterKnife.bind(this);
        initialize();
        joinFamilyId=getIntent().getStringExtra( JOIN_FAMILY_ID);
        toCreateFamily=getIntent().getBooleanExtra( TO_CREATE_FAMILY, false);
        userRegistration = getIntent().getParcelableExtra(Constants.Bundle.DATA);

   //     fusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());

        SharedPref.init(getApplicationContext());
    //    initializeRestrictions();
       prefillProfile();
    }
    private void initialize() {
        phone.setEnabled(false);
        email.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

                if (validateEmailAddress(email.getText().toString().trim())) {
                    email.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.group_1509, 0);
                }
                else{
                    email.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                }
            }
        });
        name.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
//                if (name.getText().toString().length() == 0){
//                    name.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
//                }

                if (name.getText().toString().trim().length()>1) {

                    String userName = name.getText().toString();
                    char firstLetter = userName.charAt(0);
                    Pattern pattern = Pattern.compile("[^a-zA-Z0-9]");
                    Matcher matcher = pattern.matcher(Character.toString(firstLetter));
                    boolean isSFirstLatterSpecialCharacter = matcher.find();
                    if(isSFirstLatterSpecialCharacter){
                        name.setCompoundDrawablesWithIntrinsicBounds(0, 0,0, 0);
                    }
                    else{
                        name.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.group_1509, 0);
                    }
                }
                else{
                    name.setCompoundDrawablesWithIntrinsicBounds(0, 0,0, 0);
                }


            }
        });

    }
    private boolean validateEmailAddress(String emailAddress){
        return Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(emailAddress).matches();
    }

    private void prefillProfile() {
        if (userRegistration.getFullName() != null && userRegistration.getFullName().length() > 0) {
            name.setText( userRegistration.getFullName() );
            name.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.group_1509, 0);
        }

        if (userRegistration.getPhone() != null && userRegistration.getPhone().length() > 0) {
            phone.setText( userRegistration.getPhone() );
            phone.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.group_1509, 0);
        }

        if (userRegistration.getEmail() != null && userRegistration.getEmail().length() > 0) {
            email.setText( userRegistration.getEmail() );
            email.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.group_1509, 0);
        }

    }

    @OnClick({R.id.done})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.done:
                if (validate()) {
                    registerUser();
                }
                break;
        }
    }


    private boolean validate() {
        boolean isValid = true;
        if (name.getText().toString().trim().length()<=1) {
            Toast.makeText(this, "The name must contain at least two characters", Toast.LENGTH_SHORT).show();
            return false;
        }
        else{
            String userName = name.getText().toString().trim();//(megha)trimmed whitespace
            char firstLetter = userName.charAt(0);
            Pattern pattern = Pattern.compile("[^a-zA-Z0-9]");
            Matcher matcher = pattern.matcher(Character.toString(firstLetter));
            boolean isSFirstLatterSpecialCharacter = matcher.find();
            if(isSFirstLatterSpecialCharacter){
                Toast.makeText(this, "First letter of the name must be a character", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        if (email.getText().toString().trim().length() == 0) {
            Toast.makeText(this, "Email required", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!validateEmailAddress(email.getText().toString().trim())) {
            Toast.makeText(this, "Invalid email", Toast.LENGTH_SHORT).show();
            return false;
        }

        return isValid;
    }
    @Override
    public void onBackPressed() {
        finish();
    }
    public void registerUser() {
        userRegistration.setEmail(email.getText().toString().trim());
//        userRegistration.setOrigin(etorigin.getText().toString().trim());
//        userRegistration.setLocation(livingIn.getText().toString().trim());
        userRegistration.setFullName(name.getText().toString().trim());
        userRegistration.setVerified(true);
        progressDialog = Utilities.getProgressDialog(this);
        progressDialog.show();
        userRegistration.setActive(true);
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getApplicationContext());
        apiServiceProvider.completeUserRegistration(userRegistration, null, this);
    }

    @Override
    public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {

        UserRegistrationResponse userRegistrationResponse = SharedPref.getUserRegistration();
        userRegistration = GsonUtils.getInstance().getGson().fromJson(responseBodyString, UserRegistrationResponse.class);
        userRegistration.setAccessToken(userRegistrationResponse.getAccessToken());
        userRegistration.setRefreshToken(userRegistrationResponse.getRefreshToken());
        SharedPref.write(SharedPref.IS_REGISTERED, true);
        SharedPref.write(SharedPref.USER, GsonUtils.getInstance().getGson().toJson(userRegistration));
        SharedPref.setUserRegistration(userRegistration);
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
        Intent intent = new Intent(getApplicationContext(), WelcomeUserActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(TO_CREATE_FAMILY, toCreateFamily);
        intent.putExtra(JOIN_FAMILY_ID, joinFamilyId);
        intent.putExtra(Constants.Bundle.DATA, userRegistration);
//                Intent intent = new Intent(getApplicationContext(), WelcomeUserActivity.class);
//                intent.putExtra("name", "Dinu");
//                intent.putExtra(TO_CREATE_FAMILY, toCreateFamily);
        startActivity(intent);
        finish();
    }

    @Override
    public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismissWithAnimation();
        Utilities.getErrorDialog(this, Constants.SOMETHING_WRONG).show();
    }
    @Override
    protected void onStop() {
        super.onStop();
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.cancel();
        }
    }
}
