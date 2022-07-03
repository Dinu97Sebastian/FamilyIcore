package com.familheey.app.Activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.familheey.app.R;
import com.familheey.app.Topic.MainActivity;
import com.familheey.app.Utilities.Constants;
import com.familheey.app.Utilities.SharedPref;
import com.google.android.material.button.MaterialButton;
public class SplashActivity extends AppCompatActivity {
    private MaterialButton letsGo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.dark_blue));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        setContentView(R.layout.activity_splash);
        letsGo = findViewById(R.id.letsGo);
        letsGo.setOnClickListener(v -> {
            if (SharedPref.read(SharedPref.IS_REGISTERED, false)) {
                letsGo.setEnabled(false);
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra(Constants.Bundle.IS_GLOBAL_SEARCH_ENABLED, true);
                startActivity(intent);
            } else
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        letsGo.setEnabled(true);
    }
}
