package com.familheey.app.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.familheey.app.FamilheeyApplication;
import com.familheey.app.Interfaces.RetrofitListener;
import com.familheey.app.Models.ApiCallbackParams;
import com.familheey.app.Models.ErrorData;
import com.familheey.app.Models.Request.Device;
import com.familheey.app.Models.Request.OTPRequest;
import com.familheey.app.Models.Request.ResendOTP;
import com.familheey.app.Models.Response.UserRegistrationResponse;
import com.familheey.app.Networking.Retrofit.ApiServiceProvider;
import com.familheey.app.Networking.Retrofit.ApiServices;
import com.familheey.app.Networking.Retrofit.RetrofitBase;
import com.familheey.app.Networking.utils.GsonUtils;
import com.familheey.app.R;
import com.familheey.app.SmsBroadcastReceiver;
import com.familheey.app.SplashScreen;
import com.familheey.app.Utilities.Constants;
import com.familheey.app.Utilities.SharedPref;
import com.familheey.app.Utilities.Utilities;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mukesh.OtpView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

import static com.familheey.app.Utilities.Constants.Bundle.DATA;
import static com.familheey.app.Utilities.Constants.Bundle.JOIN_FAMILY_ID;
import static com.familheey.app.Utilities.Constants.Bundle.NOTIFICATION_ID;
import static com.familheey.app.Utilities.Constants.Bundle.PUSH;
import static com.familheey.app.Utilities.Constants.Bundle.TO_CREATE_FAMILY;
import static com.familheey.app.Utilities.Constants.Bundle.TYPE;

public class OTPVerificationActivity extends AppCompatActivity implements RetrofitListener {

    private CountDownTimer timer;
    private UserRegistrationResponse userRegistrationResponse;
    private SweetAlertDialog progressDialog;
    public CompositeDisposable subscriptions;
    private FirebaseAnalytics mFirebaseAnalytics;

    private static final int REQ_USER_CONSENT = 200;
    SmsBroadcastReceiver smsBroadcastReceiver;

    private ProgressDialog dialog;


    @BindView(R.id.smsOtpResend)
    Button smsOtpResend;
    @BindView( R.id.layoutOR )
    LinearLayout layoutOR;
    @BindView( R.id.layoutEmail )
    LinearLayout layoutEmail;
    @BindView( R.id.descriptionphone )
    TextView descriptionphone;
    @BindView(R.id.emailVerify)
    Button emailVerify;
    @BindView(R.id.otp)
    OtpView otp;
    @BindView(R.id.verifyOTP)
    MaterialButton verifyOTP;
    @BindView( R.id.labelPhone )
    TextView labelPhone;
    @BindView( R.id.labelEmail )
    TextView labelEmail;
    @BindView( R.id.editPhone )
    ImageView editPhone;
    @BindView( R.id.editEmail )
    ImageView editEmail;
    @BindView(R.id.countDownTimer)
    TextView countDownTimer;
    @BindView(R.id.email)
    TextView email;
    private boolean toCreateFamily = false;
    private String joinFamilyId="";
    private boolean isExistingUser = false;
    private int countryFlags;
    private String countryName="";

    /** declared for ticket 693 **/
    private String mobileNumber = null;
    private String otpReceived = null;
    private boolean isVerified = false;
    private String reverify = "true";
    TextWatcher otpTextChangeWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            int length = email.getText().toString().length();
            if (length > 0) {
                emailVerify.setEnabled(true);
            }
            else {
                emailVerify.setEnabled(false);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_otpverification );
        ButterKnife.bind(this);
        otp.setText("");
        startSmsUserConsent();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        subscriptions = new CompositeDisposable();
        userRegistrationResponse=getIntent().getParcelableExtra( Constants.Bundle.DATA);
        //labelPhone.setText( userRegistrationResponse.getCapturedCountryCode()+" "+userRegistrationResponse.getCapturedMobileNumber() );
        toCreateFamily=getIntent().getBooleanExtra(Constants.Bundle.TO_CREATE_FAMILY, false);
        joinFamilyId=getIntent().getStringExtra( JOIN_FAMILY_ID);
        isExistingUser=getIntent().getBooleanExtra( Constants.Bundle.IS_EXISTING_USER, false);
        countryFlags=getIntent().getIntExtra("countryFlag",0);
        countryName=getIntent().getStringExtra("countryName");

        mobileNumber = getIntent().getStringExtra("mobileNumber");
        otpReceived = getIntent().getStringExtra("otpReceived");
        //isVerified = getIntent().getBooleanExtra("isVerified",false);
        if(userRegistrationResponse == null){
            labelPhone.setText(mobileNumber);
            editPhone.setVisibility(View.GONE);
        }else {
            labelPhone.setText( userRegistrationResponse.getCapturedCountryCode()+" "+userRegistrationResponse.getCapturedMobileNumber() );
            editPhone.setVisibility(View.VISIBLE);
        }
        startTimer();
        otp.setOtpCompletionListener(otpEntered -> {
            try {
                InputMethodManager imm = (InputMethodManager) getSystemService( Activity.INPUT_METHOD_SERVICE);
                assert imm != null;
                imm.hideSoftInputFromWindow(otp.getWindowToken(), 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
            verifyOTP.performClick();
        });

        email.addTextChangedListener(otpTextChangeWatcher);
        if (getIntent().hasExtra(TYPE)) {
            String kk=getIntent().getStringExtra(TYPE);
            getDetailsForVerification();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        otp.setText("");
    }
    private void startSmsUserConsent() {
        SmsRetrieverClient client = SmsRetriever.getClient(this);
        client.startSmsUserConsent(null).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Toast.makeText(getApplicationContext(), "On Success", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //  Toast.makeText(getApplicationContext(), "On OnFailure", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_USER_CONSENT) {
            if ((resultCode == RESULT_OK) && (data != null)) {
                String message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE);
                if(message!=null) {
                    getOtpFromMessage(message);
                }
            }
        }
    }
    private void getOtpFromMessage(String message) {
        Pattern pattern = Pattern.compile("(|^)\\d{6}");
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            otp.setText(matcher.group(0));
            String msg=matcher.group(0);
        }
    }
    private void registerBroadcastReceiver() {
        smsBroadcastReceiver = new SmsBroadcastReceiver();
        smsBroadcastReceiver.smsBroadcastReceiverListener =
                new SmsBroadcastReceiver.SmsBroadcastReceiverListener() {
                    @Override
                    public void onSuccess(Intent intent) {
                        startActivityForResult(intent, REQ_USER_CONSENT);
                    }
                    @Override
                    public void onFailure() {

                    }
                };
        IntentFilter intentFilter = new IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION);
        registerReceiver(smsBroadcastReceiver, intentFilter);
    }
    @Override
    protected void onStart() {
        super.onStart();
        registerBroadcastReceiver();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(smsBroadcastReceiver);
    }
    private void startTimer() {
        smsOtpResend.setClickable(false);
        smsOtpResend.setTextColor(getResources().getColor(R.color.gray_btn_bg_pressed_color));
        timer = new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
                countDownTimer.setText("" + (millisUntilFinished / 1000));
            }

            public void onFinish() {
                // Enable Resend OTP From Here
                smsOtpResend.setClickable(true);
                smsOtpResend.setTextColor(getResources().getColor(R.color.black));
            }

        }.start();
    }

    @OnClick({R.id.smsOtpResend,R.id.emailVerify,R.id.editPhone,R.id.verifyOTP})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.smsOtpResend:
                layoutOR.setVisibility( View.GONE );
                layoutEmail.setVisibility( View.GONE );
                descriptionphone.setVisibility( View.VISIBLE );
                otp.setText("");
                resendOTPRequest();
                break;
            case  R.id.emailVerify:
                layoutOR.setVisibility( View.GONE );
                layoutEmail.setVisibility( View.GONE);
                descriptionphone.setVisibility( View.GONE );
                break;
            case   R.id.editPhone:
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.putExtra(TO_CREATE_FAMILY, toCreateFamily);
                if(userRegistrationResponse == null){
                    intent.putExtra("Phone","");
                    intent.putExtra("CountryCode", "");
                }else {
                    intent.putExtra("Phone", userRegistrationResponse.getCapturedMobileNumber());
                    intent.putExtra("CountryCode", userRegistrationResponse.getCapturedCountryCode());
                }
                intent.putExtra("countryFlag",countryFlags );
                intent.putExtra("countryName",countryName);
                intent.putExtra(Constants.Bundle.IS_EXISTING_USER, isExistingUser);
                startActivity(intent);
                break;
            case R.id.verifyOTP:
                OTPRequest otpRequest = new OTPRequest();
                if(userRegistrationResponse == null){
                    otpReceived=otp.getText().toString();
                    //Toast.makeText(this,"dd"+otp.getText().toString()+" "+otpReceived,Toast.LENGTH_LONG).show();
                    if(otp.getText().toString() != null && otp.getText().toString().equalsIgnoreCase(otpReceived)){
                            otpRequest.setPhone(mobileNumber);
                            otpRequest.setOtp(otpReceived);
                            otpRequest.setReverify(reverify);
                    }else {
                        Intent intent1 = new Intent(getApplicationContext(), UserFailureScreenActivity.class);
                        intent1.putExtra("mobileNumber", mobileNumber);
                        intent1.putExtra("countryFlag",countryFlags );
                        intent1.putExtra("countryName",countryName);
                        startActivity(intent1);
                        break;
                    }
                }else{
                    otpRequest.setPhone(userRegistrationResponse.getMobileNumber());
                    otpRequest.setOtp(otp.getText().toString());
                }
                validateOTP(otpRequest);
                break;
        }
    }
    private void resendOTPRequest() {
        dialog = new ProgressDialog( OTPVerificationActivity.this);
        dialog.getWindow().setBackgroundDrawable(new
                ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setIndeterminate(true);
        //   dialog.setCancelable(true);
        dialog.show();
        dialog.setContentView(R.layout.progress_layout);
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(this);
        /**modified for ticket 693**/
        if(userRegistrationResponse == null){
            ResendOTP resendOTP = new ResendOTP();
            resendOTP.setPhone(mobileNumber);
            resendOTP.setReverify(reverify);
            resendOTP.setDevice_unique_id(Settings.Secure.getString(getContentResolver(),
                    Settings.Secure.ANDROID_ID));
            apiServiceProvider.resendOTP(resendOTP, null, this);
        }else {
            ResendOTP resendOTP = new ResendOTP();
            resendOTP.setPhone(userRegistrationResponse.getMobileNumber());
            resendOTP.setDevice_unique_id(Settings.Secure.getString(getContentResolver(),
                    Settings.Secure.ANDROID_ID));
            apiServiceProvider.resendOTP(resendOTP, null, this);
        }
    }
    private void validateOTP(OTPRequest otpRequest) {
        dialog = new ProgressDialog( OTPVerificationActivity.this);
        dialog.getWindow().setBackgroundDrawable(new
                ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setIndeterminate(true);
        //   dialog.setCancelable(true);
        dialog.show();
        dialog.setContentView(R.layout.progress_layout);
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(this);
        apiServiceProvider.confirmOTP(otpRequest, null, this);
        verifyOTP.setEnabled(true);
    }

    @Override
    public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {

        switch (apiFlag) {
            case Constants.ApiFlags.CONFIRM_OTP:
                dialog.dismiss();
                if (timer != null) timer.cancel();
                UserRegistrationResponse userRegistrationResponse = GsonUtils.getInstance().getGson().fromJson(responseBodyString, UserRegistrationResponse.class);
                SharedPref.write(SharedPref.IS_REGISTERED, true);
                SharedPref.write(SharedPref.USER, GsonUtils.getInstance().getGson().toJson(userRegistrationResponse));
                SharedPref.setUserRegistration(userRegistrationResponse);
                getFCMToken();
                if (userRegistrationResponse.isAnExistingUser()) {
                    Intent intent = new Intent(getApplicationContext(), WelcomeUserActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(Constants.Bundle.DATA, userRegistrationResponse);
                    intent.putExtra(TO_CREATE_FAMILY, toCreateFamily);
                    intent.putExtra(JOIN_FAMILY_ID, joinFamilyId);
                    intent.putExtra(Constants.Bundle.IS_EXISTING_USER, isExistingUser);
                    startActivity(intent);
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString(FirebaseAnalytics.Param.METHOD, "sign_up");
                    bundle.putString("loginId", userRegistrationResponse.getId());
                    mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SIGN_UP, bundle);
                    Intent intent = new Intent(getApplicationContext(), UserSuccessScreenActivity.class);
                    intent.putExtra(Constants.Bundle.DATA, userRegistrationResponse);
                    intent.putExtra(TO_CREATE_FAMILY, toCreateFamily);
                    intent.putExtra(JOIN_FAMILY_ID, joinFamilyId);
                    startActivity(intent);
                }
                finish();
                break;
            case Constants.ApiFlags.MOBILE_NUMBER_DETAILS:
                JSONObject jsonObj = null;
                try {
                    jsonObj = new JSONObject(responseBodyString);
                    String message = jsonObj.getString("message");
                    if(message.equalsIgnoreCase("otp limit exceeded")){
                        Toast.makeText(this,"Sorry.. you cannot request for OTP more than 2 times in 12 hours",Toast.LENGTH_LONG).show();
                    }else {
                        JSONObject data = jsonObj.getJSONObject("data");
                        mobileNumber = data.getString("phone");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case Constants.ApiFlags.RESEND_OTP:
                dialog.dismiss();
                /**@author Devika
                 * modified on 28/02/2022 for managing the resend otp option for blocked
                 * users.Users can reuest for otp only two times in 12 hours**/
                JSONObject jObject = null;
                try {
                    jObject = new JSONObject(responseBodyString);
                    String message = jObject.getString("message");
                    if(message.equalsIgnoreCase("otp limit exceeded")){
                        Toast.makeText(this,"Sorry.. you cannot request for OTP more than 2 times in 12 hours",Toast.LENGTH_LONG).show();
                    }
                    else if(message.equalsIgnoreCase("api limit exceeded")){
                        progressDialog = Utilities.getProgressDialog(this);
                        progressDialog.show();
                        Utilities.getErrorDialog(progressDialog, "You have exceeded the maximum number of requests per day");
                    }
                    else {
                        startTimer();
                        startSmsUserConsent();
                        otp.setText("");
                        Toast.makeText(this, "Otp sent !!", Toast.LENGTH_SHORT).show();
                    }
                    break;
                }catch (JSONException e){
                    e.printStackTrace();
                }
        }
    }

    @Override
    public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
        dialog.dismiss();
        switch (apiFlag) {
            case Constants.ApiFlags.CONFIRM_OTP:
//                Utilities.getErrorDial ogWithoutTitle(progressDialog, "\nInvalid OTP, Please try again\n");
//                otp.setText("");
                Intent intent = new Intent(getApplicationContext(), UserFailureScreenActivity.class);
                intent.putExtra(Constants.Bundle.DATA, userRegistrationResponse);
                intent.putExtra("countryFlag",countryFlags );
                intent.putExtra("countryName",countryName);
                intent.putExtra("mobileNumber",mobileNumber);
                startActivity(intent);
                break;
            case Constants.ApiFlags.RESEND_OTP:
                progressDialog = Utilities.getProgressDialog(this);
                progressDialog.show();

                if(errorData.getMessage().equalsIgnoreCase("device is blocked")){
                    Utilities.getErrorDialog(progressDialog, "Your device has been blocked");
                }else {
                    Utilities.getErrorDialog(progressDialog, Constants.SOMETHING_WRONG);
                }
                break;
        }
    }
    private void getFCMToken() {
        SharedPreferences pref = getSharedPreferences("Token_pref", Context.MODE_PRIVATE);
        String token = pref.getString("USER_FCM_TOKEN", "");
        assert token != null;
        if (token.isEmpty()) {
            generateFcmToken();
        }

    }

    public void generateFcmToken() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        return;
                    }
                    String token = task.getResult().getToken();
                    saveDevice(token);
                    saveFcmToken(token);
                });
    }
    private void saveDevice(String fcmToken) {
        String androidId = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);
        Device device = new Device();
        device.setUser_id(SharedPref.getUserRegistration().getId());
        device.setDevice_id(androidId);
        device.setDevice_token(fcmToken);
        device.setDevice_type("ANDROID");
        FamilheeyApplication application = FamilheeyApplication.get(this);
        ApiServices apiServices = RetrofitBase.createRxResourceWithoutAuth(ApiServices.class, this);
        subscriptions.add(apiServices.saveDevice(device)
                .observeOn( AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(response -> {
                }, throwable -> {
                }));
    }
    private void saveFcmToken(String idToken) {
        SharedPreferences pref = getSharedPreferences("Token_pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("USER_FCM_TOKEN", idToken);
        editor.apply();
    }
    /**ticket 693**/
    private void getDetailsForVerification() {
        JsonObject jsonObject = new JsonObject();
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(this);
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        apiServiceProvider.getMobileDetailsFromUserId(jsonObject,null,this);
    }
}
