package com.familheey.app.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.familheey.app.Models.Response.UserRegistrationResponse;
import com.familheey.app.R;
import com.familheey.app.Utilities.Constants;

import static com.familheey.app.Utilities.Constants.Bundle.JOIN_FAMILY_ID;
import static com.familheey.app.Utilities.Constants.Bundle.TO_CREATE_FAMILY;

public class UserSuccessScreenActivity extends AppCompatActivity {

    private boolean toCreateFamily = false;
    private String joinFamilyId = "";
    private UserRegistrationResponse userRegistrationResponse;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_user_success_screen );
        toCreateFamily=getIntent().getBooleanExtra( Constants.Bundle.TO_CREATE_FAMILY, false);
        joinFamilyId=getIntent().getStringExtra( JOIN_FAMILY_ID);
        userRegistrationResponse=getIntent().getParcelableExtra( Constants.Bundle.DATA);
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(getApplicationContext(), UserRegistrationActivity.class);
            intent.putExtra(Constants.Bundle.DATA, userRegistrationResponse);
            intent.putExtra(TO_CREATE_FAMILY, toCreateFamily);
            intent.putExtra(JOIN_FAMILY_ID, joinFamilyId);
            startActivity( intent );
            finish();
        }, 1000);
    }
}