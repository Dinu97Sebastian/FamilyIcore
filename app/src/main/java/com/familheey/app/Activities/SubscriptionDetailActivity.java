package com.familheey.app.Activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.familheey.app.Models.Response.UserSettings;
import com.familheey.app.Networking.utils.GsonUtils;
import com.familheey.app.R;
import com.familheey.app.Utilities.SharedPref;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SubscriptionDetailActivity extends AppCompatActivity {
    @BindView(R.id.goBack)
    ImageView goBack;
    @BindView(R.id.toolBarTitle)
    TextView toolBarTitle;

    @BindView(R.id.txtUser)
    TextView txtUser;

    @BindView(R.id.txtExpiry)
    TextView txtExpiry;

    @BindView(R.id.txtSubStartDate)
    TextView txtSubStartDate;

    @BindView(R.id.txtActive)
    TextView txtActive;

    Calendar calendar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription_detail);
        ButterKnife.bind(this);
        calendar = Calendar.getInstance();
        initializeToolbar();
        fetchDataFromPref();
    }

    private void fetchDataFromPref() {
        String response = SharedPref.read(SharedPref.ON_BOARD, "");
        if (!response.equals("")) {
            UserSettings userSettings = GsonUtils.getInstance().getGson().fromJson(response, UserSettings.class);
            prepopulateData(userSettings);
        }
    }

    private void prepopulateData(UserSettings userSettings) {
        if (userSettings.getSubscriptionsList().size()==0){return;}
        UserSettings.subscriptions subscription=userSettings.getSubscriptionsList().get(0);

        txtUser.setText(userSettings.getFullName());
        txtExpiry.setText(dateFormat(subscription.getSubscribe_expiry()));
        txtSubStartDate.setText(dateFormat(subscription.getSubscribed_date()));

        if (subscription.isIsexpired()){
            txtActive.setText("Not Active");
            txtActive.setTextColor(getResources().getColor(R.color.red));
        }else {
            txtActive.setText("Active");

        }
    }

    @SuppressLint("SetTextI18n")
    private void initializeToolbar() {
        toolBarTitle.setText("Subscription");
        goBack.setOnClickListener(v -> finish());
    }



    private String dateFormat(String time) {
        DateTime dateTime = ISODateTimeFormat.dateTimeParser().parseDateTime(time);
        DateTimeFormatter dtfOut = DateTimeFormat.forPattern("MMM dd yyyy hh:mm aa");
        return dtfOut.print(dateTime);
    }
}

