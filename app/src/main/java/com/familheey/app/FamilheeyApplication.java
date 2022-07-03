package com.familheey.app;

import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;

import androidx.core.app.NotificationManagerCompat;

import com.familheey.app.Firebase.MessagePushDelegate;
import com.familheey.app.Utilities.SharedPref;
import com.google.android.libraries.places.api.Places;
import com.jakewharton.threetenabp.AndroidThreeTen;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.ios.IosEmojiProvider;

import net.danlew.android.joda.JodaTimeAndroid;

import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;

public class FamilheeyApplication extends Application {

    private static FamilheeyApplication instance;
    private Scheduler defaultSubscribeScheduler;
    public static MessagePushDelegate messagePushDelegate;
    public static String announcementData;
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        // Add this line, to include the Auth plugin.
        EmojiManager.install(new IosEmojiProvider());
        SharedPref.init(getApplicationContext());
        AndroidThreeTen.init(this);
        JodaTimeAndroid.init(this);

        Places.initialize(getApplicationContext(), getString(R.string.google_map_v2_api_key));
        NotificationManagerCompat.from(this).cancelAll();
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.cancelAll();
        }
    }

    public static FamilheeyApplication get(Context context) {
        if (context != null)
            return (FamilheeyApplication) context.getApplicationContext();
        return null;
    }
    public static FamilheeyApplication getInstance() {
        return instance;
    }

    public Scheduler defaultSubscribeScheduler() {
        if (defaultSubscribeScheduler == null) {
            defaultSubscribeScheduler = Schedulers.io();
        }
        return defaultSubscribeScheduler;
    }

    public void getPush(String type) {
        messagePushDelegate.getPush(type);
    }
}
