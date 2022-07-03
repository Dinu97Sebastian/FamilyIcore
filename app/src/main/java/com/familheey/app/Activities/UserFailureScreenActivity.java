package com.familheey.app.Activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.familheey.app.Interfaces.RetrofitListener;
import com.familheey.app.Models.ApiCallbackParams;
import com.familheey.app.Models.ErrorData;
import com.familheey.app.Models.Request.ResendOTP;
import com.familheey.app.Models.Response.UserRegistrationResponse;
import com.familheey.app.Networking.Retrofit.ApiServiceProvider;
import com.familheey.app.R;
import com.familheey.app.SplashScreen;
import com.familheey.app.Utilities.Constants;
import com.familheey.app.Utilities.Utilities;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class UserFailureScreenActivity extends AppCompatActivity{

    @BindView(R.id.resend)
    Button resend;
    private int countryFlags;
    private String countryName="";
    private UserRegistrationResponse userRegistrationResponse;
    /** declared for ticket 693 **/
    private String mobileNumber = null;
    private String otpReceived = null;
    private String reverify = "true";
    private SweetAlertDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_user_failure_screen );
        ButterKnife.bind(this);
        userRegistrationResponse=getIntent().getParcelableExtra( Constants.Bundle.DATA);
        countryFlags=getIntent().getIntExtra("countryFlag",0);
        countryName=getIntent().getStringExtra("countryName");
        mobileNumber = getIntent().getStringExtra("mobileNumber");

    }

    @OnClick({R.id.resend})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.resend:
                resendOTPRequest();
                break;
        }
    }

    private void resendOTPRequest() {
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(this);
        /**modified for ticket 693**/

            ResendOTP resendOTP = new ResendOTP();
            resendOTP.setDevice_unique_id(Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID));
            if(userRegistrationResponse == null){
                resendOTP.setPhone(mobileNumber);
                resendOTP.setReverify(reverify);
                apiServiceProvider.resendOTP(resendOTP, null, new RetrofitListener() {

                    @Override
                    public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {
                        JSONObject jObject = null;
                        try {
                            jObject = new JSONObject(responseBodyString);
                            String message = jObject.getString("message");
                            if(message.equalsIgnoreCase("otp limit exceeded")){
                                Toast.makeText(UserFailureScreenActivity.this,"Sorry.. you cannot request for OTP more than 2 times in 12 hours",Toast.LENGTH_LONG).show();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        UserFailureScreenActivity.this.finish();
                                    }
                                }, 3500);
                            }else{
                                Toast.makeText(getApplicationContext(), "Otp sent !!", Toast.LENGTH_SHORT).show();
                                JSONObject mobileData = jObject.getJSONObject("data");
                                otpReceived = mobileData.getString("otp");
                                mobileNumber = mobileData.getString("phone");
                                //isVerified = mobileData.getBoolean("is_verified");
                                Intent intent = new Intent(getApplicationContext(), OTPVerificationActivity.class);
                                intent.putExtra("otpReceived",otpReceived );
                                intent.putExtra("mobileNumber",mobileNumber );
                                // intent.putExtra("isVerified",isVerified );
                                startActivity(intent);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                       /* Intent intent = new Intent(getApplicationContext(), OTPVerificationActivity.class);
                        //intent.putExtra(Constants.Bundle.DATA, userRegistrationResponse);
                        intent.putExtra("mobileNumber", mobileNumber);
                        //intent.putExtra("CountryCode", userRegistrationResponse.getCapturedCountryCode());
                        intent.putExtra("countryFlag",countryFlags );
                        intent.putExtra("countryName",countryName);
                        startActivity(intent);*/
                    }
                    @Override
                    public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
                        Toast.makeText(getApplicationContext(), "Failed!", Toast.LENGTH_SHORT).show();
                    }
                });
                /**end**/
            }else {
                resendOTP.setPhone(userRegistrationResponse.getMobileNumber());
                apiServiceProvider.resendOTP(resendOTP, null, new RetrofitListener() {

                    @Override
                    public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {
                        JSONObject jObject = null;
                        String message="";
                        try {
                            jObject = new JSONObject(responseBodyString);

                             message = jObject.getString("message");
                            if (message.equalsIgnoreCase("api limit exceeded")) {
                                progressDialog = Utilities.getProgressDialog(UserFailureScreenActivity.this);
                                progressDialog.show();
                                Utilities.getErrorDialog(progressDialog, "You have exceeded the maximum number of requests per day");
                            }
                        }catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if(message.equalsIgnoreCase("otp send")) {
                            Toast.makeText(getApplicationContext(), "Otp sent !!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), OTPVerificationActivity.class);
                            intent.putExtra(Constants.Bundle.DATA, userRegistrationResponse);
                            intent.putExtra("Phone", userRegistrationResponse.getCapturedMobileNumber());
                            intent.putExtra("CountryCode", userRegistrationResponse.getCapturedCountryCode());
                            intent.putExtra("countryFlag", countryFlags);
                            intent.putExtra("countryName", countryName);
                            startActivity(intent);
                        }
                    }

                        @Override
                    public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
                            if(errorData.getMessage().equalsIgnoreCase("device is blocked")){
                                progressDialog = Utilities.getProgressDialog(UserFailureScreenActivity.this);
                                progressDialog.show();
                                Utilities.getErrorDialog(progressDialog, "Your device has been blocked");
                            }else{
                                Toast.makeText(getApplicationContext(), "Failed!", Toast.LENGTH_SHORT).show();
                            }
                    }
                });
            }
    }

}