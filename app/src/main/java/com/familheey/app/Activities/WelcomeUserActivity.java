package com.familheey.app.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.familheey.app.Interfaces.RetrofitListener;
import com.familheey.app.Models.ApiCallbackParams;
import com.familheey.app.Models.ErrorData;
import com.familheey.app.Models.Response.UserRegistrationResponse;
import com.familheey.app.Networking.Retrofit.ApiServiceProvider;
import com.familheey.app.R;
import com.familheey.app.SplashScreen;
import com.familheey.app.Utilities.Constants;
import com.familheey.app.Utilities.SharedPref;
import com.google.gson.JsonObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.familheey.app.Utilities.Constants.Bundle.IS_EXISTING_USER;
import static com.familheey.app.Utilities.Constants.Bundle.JOIN_FAMILY_ID;
import static com.familheey.app.Utilities.Constants.Bundle.TO_CREATE_FAMILY;

public class WelcomeUserActivity extends AppCompatActivity {
    private boolean toCreateFamily = false;
    private boolean isExistingUser = false;
    private String joinFamilyId = "";
    private String fullName;
    private String firstname;
    private String lastname;
    private String scdLastname;
    private UserRegistrationResponse userRegistration;
    @BindView(R.id.heading)
    TextView heading;
    @BindView(R.id.begin)
    Button begin;
    @BindView(R.id.subheading)
    TextView subheading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_welcome_user );
        ButterKnife.bind(this);
        toCreateFamily=getIntent().getBooleanExtra( Constants.Bundle.TO_CREATE_FAMILY, false);
        isExistingUser=getIntent().getBooleanExtra( Constants.Bundle.IS_EXISTING_USER, false);
        userRegistration = getIntent().getParcelableExtra(Constants.Bundle.DATA);
        joinFamilyId=getIntent().getStringExtra( JOIN_FAMILY_ID);

        if(isExistingUser) {
            /* Megha(12/08/2021) for checking the first name*/

            if (userRegistration.getFullName().contains(" ")) {
                fullName = userRegistration.getFullName();
                int space = fullName.indexOf(" ");
                int secondSpace = fullName.indexOf(" ", space + 1);
                firstname = userRegistration.getFullName().substring(0, space);
                lastname = userRegistration.getFullName().substring(space + 1);
                scdLastname = userRegistration.getFullName().substring(0,secondSpace+1);

                if (firstname.length() < 2) {
                    if (secondSpace<0) {

                        heading.setText("Welcome back, " + fullName + "!");
                        begin.setVisibility(View.GONE);
                        subheading.setVisibility(View.GONE);
                        new Handler().postDelayed(() -> {
                            startActivity(new Intent(getApplicationContext(), SplashScreen.class));
                            finish();
                        }, 3000);
                    }else{
                        heading.setText("Welcome back, " + scdLastname + "!");
                        begin.setVisibility(View.GONE);
                        subheading.setVisibility(View.GONE);
                        new Handler().postDelayed(() -> {
                            startActivity(new Intent(getApplicationContext(), SplashScreen.class));
                            finish();
                        }, 3000);

                    }
                } else if (firstname.length() > 2) {

                    heading.setText("Welcome back, " + firstname + "!");
                    begin.setVisibility(View.GONE);
                    subheading.setVisibility(View.GONE);
                    new Handler().postDelayed(() -> {
                        startActivity(new Intent(getApplicationContext(), SplashScreen.class));
                        finish();
                    }, 3000);
                }

            } else {

                heading.setText("Welcome back, " + userRegistration.getFullName() + "!");
                begin.setVisibility(View.GONE);
                subheading.setVisibility(View.GONE);
                new Handler().postDelayed(() -> {
                    startActivity(new Intent(getApplicationContext(), SplashScreen.class));
                    finish();
                }, 3000);
            }
        }
        else{
            if(userRegistration.isAnExistingUser()) {
                /* Megha(12/08/2021) for getting firstname*/

                if (userRegistration.getFullName().contains(" ")) {
                    fullName = userRegistration.getFullName();
                    int space = fullName.indexOf(" ");
                    int secondSpace = fullName.indexOf(" ", space + 1);
                    firstname = userRegistration.getFullName().substring(0, space);
                    lastname = userRegistration.getFullName().substring(space + 1);
                    scdLastname = userRegistration.getFullName().substring(0,secondSpace+1);

                    if (firstname.length() < 2) {
                        if (secondSpace<0) {

                            heading.setText("Welcome back, " + fullName + "!");
                            begin.setVisibility(View.GONE);
                            subheading.setVisibility(View.GONE);
                            new Handler().postDelayed(() -> {
                                begin.performClick();
                            }, 3000);
                        }else{
                            heading.setText("Welcome back, " + scdLastname + "!");
                            begin.setVisibility(View.GONE);
                            subheading.setVisibility(View.GONE);
                            new Handler().postDelayed(() -> {
                                begin.performClick();
                            }, 3000);

                        }
                    } else if (firstname.length() > 2) {

                        heading.setText("Welcome back, " + firstname + "!");
                        begin.setVisibility(View.GONE);
                        subheading.setVisibility(View.GONE);
                        new Handler().postDelayed(() -> {
                            begin.performClick();
                        }, 3000);
                    }

                } else {

                    heading.setText("Welcome back, " + userRegistration.getFullName() + "!");
                    begin.setVisibility(View.GONE);
                    subheading.setVisibility(View.GONE);
                    new Handler().postDelayed(() -> {
                        begin.performClick();
                    }, 3000);
                }

            }
            else {
                if (userRegistration.getFullName().contains(" ")) {
                    fullName = userRegistration.getFullName();
                    int space = fullName.indexOf(" ");
                    int secondSpace = fullName.indexOf(" ", space + 1);
                    firstname = userRegistration.getFullName().substring(0, space);
                    lastname = userRegistration.getFullName().substring(space + 1);
                    scdLastname = userRegistration.getFullName().substring(0,secondSpace+1);

                    if (firstname.length() < 2) {
                        if (secondSpace<0) {

                            heading.setText("Welcome, "+ fullName + "!");

                        }else {
                            heading.setText("Welcome, " + scdLastname + "!");
                        }
                    } else if (firstname.length() > 2) {

                        heading.setText("Welcome, "+ firstname + "!");

                    }

                } else {

                    heading.setText("Welcome, "+ userRegistration.getFullName() + "!");
                    
                }

            }
        }


    }

    @OnClick({R.id.begin})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.begin:
                if(!toCreateFamily && joinFamilyId!=null){

                    joinFamily(joinFamilyId);
                }
                else if(!toCreateFamily && joinFamilyId==null){
                    startActivity(new Intent(getApplicationContext(), SplashScreen.class));
                    finish();
                }
                else{
                    Intent intent=new Intent(getApplicationContext(),SplashScreen.class);
                    if(userRegistration.isAnExistingUser()){
                        intent.putExtra(IS_EXISTING_USER, true);
                    }
                    intent.putExtra(TO_CREATE_FAMILY, toCreateFamily);
                    startActivity(intent);
                    finish();
                }
                break;
        }
    }
    private void joinFamily(String familyId) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        jsonObject.addProperty("group_id", "" + familyId);
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getApplicationContext());
        apiServiceProvider.joinFamily(jsonObject, null, new RetrofitListener() {

            @Override
            public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {
                Intent intent=new Intent(getApplicationContext(),FamilyDashboardActivity.class);
                intent.putExtra(Constants.Bundle.DATA, familyId);
                intent.putExtra(Constants.Bundle.TYPE, Constants.Bundle.GLOBAL_SEARCH);
                intent.putExtra(Constants.Bundle.IS_JOIN_FAMILY, true);
                startActivity(intent);
            }

            @Override
            public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
                Intent intent=new Intent(getApplicationContext(),FamilyDashboardActivity.class);
                intent.putExtra(Constants.Bundle.DATA, familyId);
                intent.putExtra(Constants.Bundle.TYPE, Constants.Bundle.GLOBAL_SEARCH);
                intent.putExtra(Constants.Bundle.IS_JOIN_FAMILY, true);
                startActivity(intent);
            }
        });
    }

//    @Override
//    public void onFamilyJoinRequested(FamilySearchModal family, int position) {
//        mListener.showProgressDialog();
//        JsonObject jsonObject = new JsonObject();
//        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
//        jsonObject.addProperty("group_id", "" + family.getGroupId());
//        ApiCallbackParams apiCallbackParams = new ApiCallbackParams();
//        apiCallbackParams.setGlobalSearchFamily(family);
//        apiCallbackParams.setPosition(position);
//        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getActivity());
//        apiServiceProvider.joinFamily(jsonObject, apiCallbackParams, this);
//    }
}