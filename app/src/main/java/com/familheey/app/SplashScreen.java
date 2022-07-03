package com.familheey.app;

import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.familheey.app.Activities.CreateFamilyActivity;
import com.familheey.app.Activities.CreatedEventDetailActivity;
import com.familheey.app.Activities.FamilyDashboardActivity;
import com.familheey.app.Activities.LoginActivity;
import com.familheey.app.Activities.OTPVerificationActivity;
import com.familheey.app.Activities.UserRegistrationActivity;
import com.familheey.app.Announcement.AnnouncementDetailActivity;
import com.familheey.app.Interfaces.RetrofitListener;
import com.familheey.app.Models.ApiCallbackParams;
import com.familheey.app.Models.ErrorData;
import com.familheey.app.Models.Response.UserRegistrationResponse;
import com.familheey.app.Models.Response.UserSettings;
import com.familheey.app.Networking.Retrofit.ApiServiceProvider;
import com.familheey.app.Networking.Retrofit.ApiServices;
import com.familheey.app.Networking.Retrofit.RetrofitBase;
import com.familheey.app.Networking.utils.GsonUtils;
import com.familheey.app.Post.PostDetailForPushActivity;
import com.familheey.app.Topic.MainActivity;
import com.familheey.app.Utilities.BackGroundDataFetching;
import com.familheey.app.Utilities.Constants;
import com.familheey.app.Utilities.SharedPref;
import com.familheey.app.Utilities.Utilities;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.listener.StateUpdatedListener;
import com.google.android.play.core.tasks.OnFailureListener;
import com.google.android.play.core.tasks.OnSuccessListener;
import com.google.android.play.core.tasks.Task;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.luseen.autolinklibrary.AutoLinkMode;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Locale;
import java.util.Objects;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import static com.familheey.app.Utilities.Constants.Bundle.DATA;
import static com.familheey.app.Utilities.Constants.Bundle.DETAIL;
import static com.familheey.app.Utilities.Constants.Bundle.ID;
import static com.familheey.app.Utilities.Constants.Bundle.IS_DYNAMIC;
import static com.familheey.app.Utilities.Constants.Bundle.PUSH;
import static com.familheey.app.Utilities.Constants.Bundle.TYPE;

public class SplashScreen extends AppCompatActivity implements RetrofitListener,InstallStateUpdatedListener {
    @BindView(R.id.retry)
    MaterialButton retry;
    @BindView(R.id.exit)
    MaterialButton exit;
    @BindView(R.id.onBoardCheckProgress)
    ProgressBar progressBar;

    private boolean toCreateFamily = false;
    private UserSettings userSettings = null;
    private CompositeDisposable subscriptions;
    private AlertDialog alertDialog;
    private boolean isExistingUser = false;

    private String otpReceived = null;
    private String mobileNumber = null;
    private String reverify = "true";

    private CoordinatorLayout mSnackbarlayout;
    private AppUpdateManager appUpdateManager;
    private static final int RC_APP_UPDATE = 659;

    private static final int FLEXIBLE_APP_UPDATE_REQ_CODE = 123;
    private InstallStateUpdatedListener installStateUpdatedListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        toCreateFamily=getIntent().getBooleanExtra( Constants.Bundle.TO_CREATE_FAMILY, false);
        isExistingUser=getIntent().getBooleanExtra( Constants.Bundle.IS_EXISTING_USER, false);
        ButterKnife.bind(this);
        subscriptions = new CompositeDisposable();
        mSnackbarlayout = findViewById(R.id.snackbarLayout);
        //startService(new Intent(getBaseContext(), ClearService.class));
        //callIsRegistered();
        /**In-app update**/
        appUpdateManager = AppUpdateManagerFactory.create(this);
        checkUpdate();
       installStateUpdatedListener = state -> {
            if (state.installStatus() == InstallStatus.DOWNLOADED) {
                popupSnackBarForCompleteUpdate();
            } else if (state.installStatus() == InstallStatus.INSTALLED) {
                removeInstallStateUpdateListener();
            } else {
                Toast.makeText(getApplicationContext(), "InstallStateUpdatedListener: state: " + state.installStatus(), Toast.LENGTH_LONG);
            }
        };
        appUpdateManager.registerListener(installStateUpdatedListener);
        /**End**/
    }
    private void removeInstallStateUpdateListener() {
        if (appUpdateManager != null) {
            appUpdateManager.unregisterListener(installStateUpdatedListener);
        }
    }
    /**method to load a Snackbar with Install button to notify the user once the update is ready**/
    private void popupSnackBarForCompleteUpdate() {
        Snackbar.make(mSnackbarlayout, "  New app is ready!", Snackbar.LENGTH_INDEFINITE)
                .setAction("Install", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (appUpdateManager != null) {
                            appUpdateManager.completeUpdate();
                        }
                    }
                })
                .setAnchorView(progressBar)
                .setTextColor(getResources().getColor(R.color.white))
                .setActionTextColor(getResources().getColor(R.color.purpleText))
                .show();
    }

    /**method to check whether any app updates available or not**/
    private void checkUpdate() {
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                startUpdateFlow(appUpdateInfo);

            } else if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                popupSnackBarForCompleteUpdate();
            }else {
               callIsRegistered();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                callIsRegistered();
            }
        });
    }
    /**method for displaying dialog for update**/
    private void startUpdateFlow(AppUpdateInfo appUpdateInfo) {
        try {
            appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.FLEXIBLE, this, FLEXIBLE_APP_UPDATE_REQ_CODE);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }
    /**Handling option selected by user from update dialog. RESULT_CANCELED when he chooses "No thanks" and RESULT_OK
        * on clicking "Update"**/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FLEXIBLE_APP_UPDATE_REQ_CODE) {
            if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), "Update canceled by user! Result Code: " + resultCode, Toast.LENGTH_LONG);
               callIsRegistered();
            } else if (resultCode == RESULT_OK) {
                Toast.makeText(getApplicationContext(),"Update success! Result Code: " + resultCode, Toast.LENGTH_LONG);
                callIsRegistered();
            } else {
                Toast.makeText(getApplicationContext(), "Update Failed! Result Code: " + resultCode, Toast.LENGTH_LONG);
                checkUpdate();
               // callIsRegistered();
            }
        }
    }
    /**End of In-App updates**/
    private void callIsRegistered() {
        if (SharedPref.read(SharedPref.IS_REGISTERED, false)) {
            refreshToken();
            getPost();
        } else {
            new Handler().postDelayed(() -> {
                FirebaseDynamicLinks.getInstance()
                        .getDynamicLink(getIntent())
                        .addOnSuccessListener(this, pendingDynamicLinkData -> {
                            if (pendingDynamicLinkData != null) {
                                Uri uri = pendingDynamicLinkData.getLink();
                                assert uri != null;
                                String type = (uri.getQueryParameter("type"));
                                if (type != null) {
                                    SharedPref.write(SharedPref.DYNAMIC_TYPE, uri.getQueryParameter("type"));
                                    SharedPref.write(SharedPref.DYNAMIC_TYPE_ID,   uri.getQueryParameter("type_id"));
                                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                    intent.putExtra(IS_DYNAMIC, true);
                                    startActivity(intent);
                                    finish();
                                }
                            }});

                startActivity(new Intent(getApplicationContext(),NewUserWelcomeActivity.class));
                finish();
            }, 1000);
        }
    }
    private void refreshToken() {
        JsonObject jsonObject1 = new JsonObject();
        jsonObject1.addProperty("refresh_token", SharedPref.getUserRegistration().getRefreshToken());
        jsonObject1.addProperty("phone", SharedPref.getUserRegistration().getMobileNumber());
        RequestBody requestBody = RequestBody.create(jsonObject1.toString(), MediaType.parse("application/json; charset=utf-8"));
        FamilheeyApplication application = FamilheeyApplication.get(this);
        ApiServices apiServices = RetrofitBase.createRxResource(this, ApiServices.class);
        subscriptions.add(apiServices.delegation(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(response -> {
                    final String accessToken = response.getAsJsonPrimitive("accessToken").getAsString();
                    UserRegistrationResponse userRegistrationResponse = SharedPref.getUserRegistration();
                    userRegistrationResponse.setAccessToken(accessToken);
                    SharedPref.write(SharedPref.USER, GsonUtils.getInstance().getGson().toJson(userRegistrationResponse));
                    SharedPref.setUserRegistration(userRegistrationResponse);
                    getKeys();
                    checkOnBoard();
                    new BackGroundDataFetching(this).loadDataFromApi();
                }, throwable ->
                {
                    if (!(throwable instanceof IOException)) {
                        startActivity(new Intent(getApplicationContext(), NewUserWelcomeActivity.class));
                        finish();
                    } else {
                        retry.setVisibility(View.VISIBLE);
                        exit.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    }
                }));
    }


    private void getKeys() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        FamilheeyApplication application = FamilheeyApplication.get(getApplicationContext());
        ApiServices apiServices = RetrofitBase.createRxResource(getApplicationContext(), ApiServices.class);
        subscriptions.add(apiServices.getKeys(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(response -> {
                    assert response.body() != null;
                    SharedPref.write(SharedPref.GOOGLE_API, response.body().getGoogle_api_key());
                    SharedPref.write(SharedPref.STRIPE_KEY, response.body().getStripe());
                }, throwable ->
                        checkOnBoard()));
    }

    private void checkOnBoard() {
        retry.setVisibility(View.INVISIBLE);
        exit.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        SharedPreferences pref = getSharedPreferences("Token_pref", Context.MODE_PRIVATE);
        String token = pref.getString("USER_FCM_TOKEN", "");
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        jsonObject.addProperty("login_type", "Android");
        jsonObject.addProperty("login_device", Utilities.getDeviceName());
        jsonObject.addProperty("login_ip", Utilities.getIPAddress(true));
        jsonObject.addProperty("app_version", BuildConfig.VERSION_NAME);
        jsonObject.addProperty("device_token", token);
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        FamilheeyApplication application = FamilheeyApplication.get(getApplicationContext());
        ApiServices apiServices = RetrofitBase.createRxResource(getApplicationContext(), ApiServices.class);
        subscriptions.add(apiServices.onBoardCheck(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(response -> {
                    assert response.body() != null;
                    userSettings = GsonUtils.getInstance().getGson().fromJson(response.body().string(), UserSettings.class);
                    //(userSettings.getAllowReVerification())
                    if ((userSettings.getUser_is_blocked())&&(userSettings.getAllowReVerification())) {
                        //DialogBlockUsers(userSettings.getId() + "");
                        DialogReverifyUser(userSettings.getId() + "");
                    }else if((userSettings.getUser_is_blocked()) && (!userSettings.getAllowReVerification())){
                        DialogBlockUsers(userSettings.getId() + "");
                    } else if (!userSettings.getIsActive()) {
                        startActivity(new Intent(getApplicationContext(), NewUserWelcomeActivity.class));
                        finish();
                    } else {
                        saveToPreference(new Gson().toJson(userSettings));
                        goToNormalWorkFlow1(userSettings);
                        //checkUpdate();
                        //VersionChecker versionChecker = new VersionChecker();
                        //Dinu(07/08/2021) to check version update
                      /// String currentVersion= BuildConfig.VERSION_NAME;
                      //  if(!latestVersion.equals(currentVersion))
                         //   if(!latestVersion.equals(currentVersion) && !currentVersion.contains("-TEST"))
                           // forceUpdateApp(latestVersion);
                           //     goToNormalWorkFlow1(userSettings);

                       // else goToNormalWorkFlow1(userSettings);
                    }
                }, throwable ->
                {
//                    if (!(throwable instanceof IOException)) {
//                        startActivity(new Intent(getApplicationContext(), NewUserWelcomeActivity.class));
//                        finish();
//                    } else {
                        retry.setVisibility(View.VISIBLE);
                        exit.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);

                }));
    }

    private void forceUpdateApp(String latestVersion) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(SplashScreen.this);
//        builder.setTitle("New version "+latestVersion+" is available! Please update for latest features, improvements & fixes.");`
//        builder.setPositiveButton("Update", (dialog, which) -> {
//            try {
//                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + "com.familheey.app")));
//            } catch (android.content.ActivityNotFoundException anfe) {
//                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + "com.familheey.app")));
//            }
//            dialog.dismiss();
//        });
//
//        builder.setNegativeButton("Exit", (dialog, which) -> {
//            dialog.dismiss();
//            finish();
//        });
//        alertDialog = builder.show();
//        alertDialog.setCanceledOnTouchOutside(false);
//        alertDialog.setCancelable(false);

//Dinu(07/08/2021) to check version updates
        SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setContentText("New version "+latestVersion+" is available! Please update for latest features, improvements & fixes.")
                .setConfirmText("Update")
                .setCancelText("Not Now");
        pDialog.setCancelable(false);
        pDialog.show();
        pDialog.setConfirmClickListener(sDialog -> {
                        try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + "com.familheey.app")));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + "com.familheey.app")));
            }
            pDialog.cancel();
        });
        pDialog.setCancelClickListener(sDialog -> {
            goToNormalWorkFlow1(userSettings);
            pDialog.cancel();

        });

    }

    private void saveToPreference(String response) {
        SharedPref.write(SharedPref.ON_BOARD, response);
    }

    public void goToNormalWorkFlow1(UserSettings userSettings) {

        try {
            if (!userSettings.isValidUser()) {
                UserRegistrationResponse userRegistrationResponse = new UserRegistrationResponse();
                userRegistrationResponse.setId(userSettings.getId().toString());
                Intent registrationIntent = new Intent(getApplicationContext(), UserRegistrationActivity.class);
                registrationIntent.putExtra(Constants.Bundle.DATA, userRegistrationResponse);
                startActivity(registrationIntent);
                finish();
                return;
            }
            boolean isGlobalSearchEnabled;
            isGlobalSearchEnabled = userSettings.getFamilyCount() <= 0;
            SharedPref.setUserHasFamily(!isGlobalSearchEnabled);

            FirebaseDynamicLinks.getInstance()
                    .getDynamicLink(getIntent())
                    .addOnSuccessListener(this, pendingDynamicLinkData -> {
                        if (pendingDynamicLinkData != null) {
                            Uri uri = pendingDynamicLinkData.getLink();
                            assert uri != null;
                            String type = (uri.getQueryParameter("type"));
                            if (type != null) {
                                if (type.contains("family")) {
                                    startActivity(new Intent(getApplicationContext(), FamilyDashboardActivity.class).putExtra(TYPE, DETAIL).putExtra(DATA, uri.getQueryParameter("type_id")));
                                    finish();
                                } else if (type.contains("event")) {
                                    startActivity(new Intent(getApplicationContext(), CreatedEventDetailActivity.class).putExtra(TYPE, PUSH).putExtra(ID, uri.getQueryParameter("type_id")));
                                    finish();
                                } else if (type.contains("post")) {
                                    startActivity(new Intent(getApplicationContext(), PostDetailForPushActivity.class).putExtra("ids", uri.getQueryParameter("type_id")).putExtra(TYPE, "PUSH"));
                                    finish();
                                } else if (type.contains("announcement")) {
                                    startActivity(new Intent(getApplicationContext(), AnnouncementDetailActivity.class).putExtra("id", uri.getQueryParameter("type_id")).putExtra(TYPE, "PUSH"));
                                    finish();
                                }
                            }
                        }
                        else{
                           String dynamicType= SharedPref.read(SharedPref.DYNAMIC_TYPE, "");
                            String dynamicTypeId= SharedPref.read(SharedPref.DYNAMIC_TYPE_ID, "");
                            SharedPref.write(SharedPref.DYNAMIC_TYPE, "");
                            SharedPref.write(SharedPref.DYNAMIC_TYPE_ID,"");
                            if(dynamicType !="" && dynamicTypeId !=""){
                                if (dynamicType.contains("family")) {
                                    startActivity(new Intent(getApplicationContext(), FamilyDashboardActivity.class).putExtra(TYPE, DETAIL).putExtra(DATA, dynamicTypeId));
                                    finish();
                                } else if (dynamicType.contains("event")) {
                                    startActivity(new Intent(getApplicationContext(), CreatedEventDetailActivity.class).putExtra(TYPE, PUSH).putExtra(ID, dynamicTypeId));
                                    finish();
                                } else if (dynamicType.contains("post")) {
                                    startActivity(new Intent(getApplicationContext(), PostDetailForPushActivity.class).putExtra("ids", dynamicTypeId).putExtra(TYPE, "PUSH"));
                                    finish();
                                } else if (dynamicType.contains("announcement")) {
                                    startActivity(new Intent(getApplicationContext(), AnnouncementDetailActivity.class).putExtra("id", dynamicTypeId).putExtra(TYPE, "PUSH"));
                                    finish();
                                }
                            }
                        }
                    });

            navigateMainActivity(isGlobalSearchEnabled);
        } catch (JsonParseException e) {
            e.printStackTrace();
            retry.setVisibility(View.VISIBLE);
            exit.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }


    private void navigateMainActivity(boolean isGlobalSearchEnabled) {
        Intent homeIntent;
    if(toCreateFamily){
            homeIntent = new Intent( SplashScreen.this, CreateFamilyActivity.class );
            homeIntent.putExtra(Constants.Bundle.IS_GLOBAL_SEARCH_ENABLED, isGlobalSearchEnabled);
            homeIntent.putExtra(Constants.Bundle.IS_LOGGED_IN_NOW, true);
            homeIntent.putExtra(Constants.Bundle.TO_CREATE_FAMILY, toCreateFamily);
            homeIntent.putExtra(Constants.Bundle.IS_EXISTING_USER, isExistingUser);
        }
    else{
            homeIntent = new Intent( SplashScreen.this, MainActivity.class );
            homeIntent.putExtra(Constants.Bundle.IS_GLOBAL_SEARCH_ENABLED, isGlobalSearchEnabled);
            homeIntent.putExtra(Constants.Bundle.IS_LOGGED_IN_NOW, true);
            startActivity(homeIntent);
        }
        startActivity(homeIntent);
        finish();
    }

    @OnClick({R.id.retry, R.id.exit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.retry:
                checkOnBoard();
                break;
            case R.id.exit:
                finish();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        NotificationManagerCompat.from(this).cancelAll();
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.cancelAll();
        }
        if (userSettings != null) {
            if (alertDialog != null && alertDialog.isShowing())
                return;
            checkOnBoard();
        }
    }
    private void DialogReverifyUser(String id) {
        progressBar.setVisibility(View.INVISIBLE);
        final Dialog dialog = new Dialog(this);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_user_reverification);
        com.luseen.autolinklibrary.AutoLinkTextView textView = dialog.findViewById(R.id.txt_msg);
        dialog.setCanceledOnTouchOutside(false);
        textView.setText("Your account has been suspended due to malicious behaviour detected.You need to verify your account to continue with Familheey App.");
        dialog.findViewById(R.id.btn_verify).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDetailsForVerification(id);
            }
        });
        dialog.show();
    }
    /**ticket 693**/
    private void getDetailsForVerification(String id) {
        JsonObject jsonObject = new JsonObject();
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(this);
        jsonObject.addProperty("user_id", id);
        apiServiceProvider.getMobileDetailsFromUserId(jsonObject,null,this);
    }
    @Override
    public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {
        JSONObject jObject = null;
        try {
                    jObject = new JSONObject(responseBodyString);
                    String message = jObject.getString("message");
                    if(message.equalsIgnoreCase("otp limit exceeded")){
                        Toast.makeText(this,"Your OTP limit has exceeded. Please try again after 12 hours.",Toast.LENGTH_LONG).show();
                        //this.finish();
                        //thread.start();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                SplashScreen.this.finish();
                            }
                        }, 3500);
                    }else {
                       // jObject = new JSONObject(responseBodyString);
                        JSONObject mobileData = jObject.getJSONObject("data");
                        otpReceived = mobileData.getString("otp");
                        mobileNumber = mobileData.getString("phone");
                        Intent intent = new Intent(getApplicationContext(), OTPVerificationActivity.class);
                        intent.putExtra("otpReceived",otpReceived );
                        intent.putExtra("mobileNumber",mobileNumber );
                        startActivity(intent);
                    }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
        Utilities.getErrorDialog(this, Constants.SOMETHING_WRONG);
    }
    /** end **/

    private void DialogBlockUsers(String id) {
        progressBar.setVisibility(View.INVISIBLE);
        final Dialog dialog = new Dialog(this);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialogue_user_block);
        com.luseen.autolinklibrary.AutoLinkTextView textView = dialog.findViewById(R.id.txt_msg);
        textView.addAutoLinkMode(AutoLinkMode.MODE_EMAIL);
        textView.setEmailModeColor(ContextCompat.getColor(this, R.color.buttoncolor));
        dialog.setCanceledOnTouchOutside(false);
        textView.setAutoLinkText("Sorry, your account have been suspended. Please email us on contact@familheey.com to reactivate your account.");

        textView.setAutoLinkOnClickListener((autoLinkMode, matchedText) -> {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("*/*");
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{matchedText});
            intent.putExtra(Intent.EXTRA_SUBJECT, "Reactivate Account - " + id);
            intent.setType("text/html");
            intent.setPackage("com.google.android.gm");
            startActivity(Intent.createChooser(intent, "Send mail"));
            finish();
        });

        dialog.findViewById(R.id.btn_close).setOnClickListener(view -> dialog.dismiss());
        dialog.findViewById(R.id.btn_login).setOnClickListener(view -> {
            dialog.dismiss();
            startActivity(new Intent(getApplicationContext(), NewUserWelcomeActivity.class));
            finish();
        });
        dialog.show();
    }

    private static String getSystemProperty(String name) throws Exception {
        Class systemPropertyClazz = Class.forName("android.os.SystemProperties");
        return (String) systemPropertyClazz.getMethod("get", new Class[]{String.class
        }).invoke(systemPropertyClazz,
                new Object[]{name});
    }

    public static boolean checkEmulator() {
        try {
            boolean goldfish = getSystemProperty("ro.hardware").contains("goldfish");
            boolean qemu = getSystemProperty("ro.kernel.qemu").length() > 0;
            boolean sdk = getSystemProperty("ro.product.model").equals("sdk");
            return qemu || goldfish || sdk;
        } catch (Exception e) {
            return false;
        }
    }

    private void getPost() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());//
        jsonObject.addProperty("type", "post");
        jsonObject.addProperty("query", "");
        jsonObject.addProperty("offset", 0 + "");
        jsonObject.addProperty("limit", "15");
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        FamilheeyApplication application = FamilheeyApplication.get(getApplicationContext());
        ApiServices apiServices = RetrofitBase.createRxResource(getApplicationContext(), ApiServices.class);
        assert application != null;
        subscriptions.add(apiServices.getPost(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(response -> {
                    SharedPref.write(SharedPref.POST_DATA, "");
                    SharedPref.write(SharedPref.POST_DATA, new Gson().toJson(response.body()));
                }, throwable -> {
                    Log.i("","error");
                }));
    }

    @Override
    public void onStateUpdate(@NonNull InstallState installState) {

    }
}
