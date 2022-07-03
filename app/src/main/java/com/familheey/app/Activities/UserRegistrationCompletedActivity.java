package com.familheey.app.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.familheey.app.FamilheeyApplication;
import com.familheey.app.Models.Response.UserSettings;
import com.familheey.app.Networking.Retrofit.ApiServices;
import com.familheey.app.Networking.Retrofit.RetrofitBase;
import com.familheey.app.Networking.utils.GsonUtils;
import com.familheey.app.R;
import com.familheey.app.Topic.MainActivity;
import com.familheey.app.Utilities.BackGroundDataFetching;
import com.familheey.app.Utilities.SharedPref;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import static com.familheey.app.Utilities.Constants.Bundle.IS_GLOBAL_SEARCH_ENABLED;

public class UserRegistrationCompletedActivity extends AppCompatActivity {

    @BindView(R.id.continueToHome)
    MaterialButton continueToHome;
    @BindView(R.id.prograss)
    ProgressBar prograss;

    private CompositeDisposable subscriptions;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration_completed);
        ButterKnife.bind(this);
        subscriptions = new CompositeDisposable();
        checkOnBoard();
        new BackGroundDataFetching(this).loadDataFromApi();
        continueToHome.setVisibility(View.INVISIBLE);
    }

    @OnClick(R.id.continueToHome)
    public void onViewClicked() {
        continueToHome.setEnabled(false);
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra(IS_GLOBAL_SEARCH_ENABLED, true);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


    private void checkOnBoard() {
        prograss.setVisibility(View.VISIBLE);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        FamilheeyApplication application = FamilheeyApplication.get(getApplicationContext());
        ApiServices apiServices = RetrofitBase.createRxResource(getApplicationContext(), ApiServices.class);
        subscriptions.add(apiServices.onBoardCheck(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(response -> {

                    UserSettings userSettings = GsonUtils.getInstance().getGson().fromJson(response.body().string(), UserSettings.class);
                    if (userSettings.getUser_is_blocked()) {
                    } else {
                        SharedPref.write(SharedPref.ON_BOARD, new Gson().toJson(userSettings));
                        prograss.setVisibility(View.GONE);
                        continueToHome.setVisibility(View.VISIBLE);
                    }
                }, throwable ->
                {
                    prograss.setVisibility(View.GONE);
                    continueToHome.setVisibility(View.VISIBLE);
                }));
    }
}
