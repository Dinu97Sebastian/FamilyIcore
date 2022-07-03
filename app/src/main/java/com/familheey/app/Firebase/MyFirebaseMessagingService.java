package com.familheey.app.Firebase;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.familheey.app.Activities.CalendarActivity;
import com.familheey.app.Activities.ChatActivity;
import com.familheey.app.Activities.CreatedEventDetailActivity;
import com.familheey.app.Activities.FamilyAddMemberActivity;
import com.familheey.app.Activities.FamilyDashboardActivity;
import com.familheey.app.Activities.GuestActivity;
import com.familheey.app.Activities.OTPVerificationActivity;
import com.familheey.app.Activities.ProfileActivity;
import com.familheey.app.Announcement.AnnouncementDetailActivity;
import com.familheey.app.FamilheeyApplication;
import com.familheey.app.Interfaces.RetrofitListener;
import com.familheey.app.Models.ApiCallbackParams;
import com.familheey.app.Models.ErrorData;
import com.familheey.app.Models.Response.EventDetail;
import com.familheey.app.Need.NeedRequestDetailedActivity;
import com.familheey.app.Networking.Retrofit.ApiServiceProvider;
import com.familheey.app.Post.PostCommentActivity;
import com.familheey.app.Post.PostDetailForPushActivity;
import com.familheey.app.R;
import com.familheey.app.SplashScreen;
import com.familheey.app.Topic.MainActivity;
import com.familheey.app.Topic.TopicChatActivity;
import com.familheey.app.Utilities.Constants;
import com.familheey.app.Utilities.SharedPref;
import com.familheey.app.Utilities.Utilities;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.SecureRandom;
import java.util.Random;

import static com.familheey.app.Utilities.Constants.Bundle.DATA;
import static com.familheey.app.Utilities.Constants.Bundle.GUEST_ATTENDING;
import static com.familheey.app.Utilities.Constants.Bundle.GUEST_INTERESTED;
import static com.familheey.app.Utilities.Constants.Bundle.GUEST_MAY_ATTEND;
import static com.familheey.app.Utilities.Constants.Bundle.ID;
import static com.familheey.app.Utilities.Constants.Bundle.INVITE;
import static com.familheey.app.Utilities.Constants.Bundle.LINKED_FAMILIES;
import static com.familheey.app.Utilities.Constants.Bundle.LINK_FAMILY_REQUEST;
import static com.familheey.app.Utilities.Constants.Bundle.MEMBER;
import static com.familheey.app.Utilities.Constants.Bundle.NOTIFICATION_ID;
import static com.familheey.app.Utilities.Constants.Bundle.POSITION;
import static com.familheey.app.Utilities.Constants.Bundle.PUSH;
import static com.familheey.app.Utilities.Constants.Bundle.REQUEST;
import static com.familheey.app.Utilities.Constants.Bundle.SUB_TYPE;
import static com.familheey.app.Utilities.Constants.Bundle.TYPE;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private String otpReceived = null;
    private String mobileNumber = null;
    private boolean isVerified = false;
    @Override
    public void onMessageReceived(@NotNull RemoteMessage remoteMessage) {

        if (FamilheeyApplication.messagePushDelegate != null) {
            showNotification(remoteMessage);
            FamilheeyApplication.get(this).getPush(remoteMessage.getData().get("type"));
        } else {
            showNotification(remoteMessage);
        }

    }

    @Override
    public void onNewToken(@NotNull String token) {

    }




    public void showNotification(RemoteMessage remoteMessage) {
        Intent intent;
        String type = remoteMessage.getData().get("type");
        String type_id = remoteMessage.getData().get("type_id");
        String subtype = remoteMessage.getData().get("sub_type");
        //passed notification_key to each page for update view_status at click of push notification
        // Modified By: Dinu(22/02/2021)
        String notification_key = remoteMessage.getData().get("notification_key");
        String noti_key = remoteMessage.getData().get("notification_key");
        if (!Utilities.isNullOrEmpty(type_id)) {


            switch (type) {
                case "home": {
                    if ("familyfeed".equals(subtype) || "publicfeed".equals(subtype)) {
                        intent = new Intent(this, PostDetailForPushActivity.class).putExtra("ids", type_id).putExtra(TYPE, "PUSH").putExtra(NOTIFICATION_ID,notification_key);
                    } else if ("requestFeed".equals(subtype)) {
                        intent = new Intent(this, NeedRequestDetailedActivity.class).putExtra(TYPE, MEMBER).putExtra(DATA, type_id).putExtra(NOTIFICATION_ID,notification_key);
                    } else {
                        intent = new Intent(this, MainActivity.class);
                    }
                    break;
                }
                case "announcement": {
                    intent = new Intent(this, AnnouncementDetailActivity.class).putExtra(TYPE, "PUSH").putExtra("id", type_id).putExtra(NOTIFICATION_ID,notification_key);
                    break;
                }
                case "event":
                    EventDetail eventDetail = new EventDetail();
                    eventDetail.setEventId(type_id);
                    switch (subtype) {
                        case GUEST_INTERESTED:
                            intent = new Intent(this, GuestActivity.class).putExtra(TYPE, PUSH).putExtra(Constants.Bundle.DATA, type_id).putExtra(POSITION, GUEST_MAY_ATTEND).putExtra(NOTIFICATION_ID,notification_key);
                            break;
                        case GUEST_ATTENDING:
                            intent = new Intent(this, GuestActivity.class).putExtra(TYPE, PUSH).putExtra(Constants.Bundle.DATA, type_id).putExtra(POSITION, GUEST_ATTENDING).putExtra(NOTIFICATION_ID,notification_key);
                            break;
                        case "calendar":
                            intent = new Intent(this, CalendarActivity.class).putExtra(TYPE, PUSH).putExtra(ID, type_id).putExtra(SUB_TYPE, subtype).putExtra(NOTIFICATION_ID,notification_key);
                            break;
                        default:
                            intent = new Intent(this, CreatedEventDetailActivity.class).putExtra(TYPE, PUSH).putExtra(ID, type_id).putExtra(SUB_TYPE, subtype).putExtra(NOTIFICATION_ID,notification_key);
                            break;
                    }
                    break;
                case "member_count":
                    intent = new Intent(this, FamilyAddMemberActivity.class).putExtra(TYPE, INVITE).putExtra(DATA, type_id).putExtra(NOTIFICATION_ID,notification_key);
                    break;
                case "family":
                    switch (subtype) {
                        case "member":
                            intent = new Intent(this, FamilyDashboardActivity.class).putExtra(TYPE, MEMBER).putExtra(DATA, type_id).putExtra(NOTIFICATION_ID,notification_key);
                            break;
                        case "request":
                            intent = new Intent(this, FamilyDashboardActivity.class).putExtra(TYPE, REQUEST).putExtra(DATA, type_id).putExtra(NOTIFICATION_ID,notification_key);
                            break;
                        case "family_link":
                            intent = new Intent(this, FamilyDashboardActivity.class).putExtra(TYPE, LINK_FAMILY_REQUEST).putExtra(DATA, type_id).putExtra(NOTIFICATION_ID,notification_key);
                            break;
                        case "fetch_link":
                            intent = new Intent(this, FamilyDashboardActivity.class).putExtra(TYPE, LINKED_FAMILIES).putExtra(DATA, type_id).putExtra(NOTIFICATION_ID,notification_key);
                            break;
                        default:
                            intent = new Intent(this, FamilyDashboardActivity.class).putExtra(TYPE, PUSH).putExtra(DATA, type_id).putExtra(NOTIFICATION_ID,notification_key);
                            break;
                    }
                    break;
                case "user":
                    if ("request".equals(subtype)) {
                        intent = new Intent(this, ProfileActivity.class).putExtra(TYPE, REQUEST).putExtra(DATA, type_id).putExtra(NOTIFICATION_ID,notification_key);
                    } else {
                        intent = new Intent(this, ProfileActivity.class).putExtra(TYPE, PUSH).putExtra(DATA, type_id).putExtra(NOTIFICATION_ID,notification_key);
                    }
                    break;
                case "post":
                    intent = new Intent(this, PostCommentActivity.class)
                            .putExtra(DATA, type_id)
                            .putExtra("POS", 0)
                            .putExtra(SUB_TYPE, "POST")
                            .putExtra(TYPE, PUSH).putExtra(NOTIFICATION_ID,notification_key);
                    break;
                case "topic":
                    intent = new Intent(this, TopicChatActivity.class).putExtra(DATA, type_id).putExtra(TYPE, PUSH).putExtra("NOTIFICATION_ID",noti_key);
                    break;
                case "reverification":
                    intent=new Intent(this, OTPVerificationActivity.class).putExtra(TYPE, type);
                    break;
                case "profile": {
                    intent = new Intent(this, ProfileActivity.class).putExtra(TYPE, PUSH).putExtra(DATA, type_id).putExtra(NOTIFICATION_ID,notification_key);
                    break;
                }
                default:
                    intent = new Intent(this, MainActivity.class);
                    break;
            }
        } else {
            intent = new Intent(this, MainActivity.class);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 101 /* Request code */, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        int notificationId = 1;
        String channelId = "channel-01";
        String channelName = "Channel Name";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(mChannel);
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, channelId);
//        mBuilder.setSmallIcon(R.drawable.launcher_icon);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBuilder.setSmallIcon(R.drawable.notification_small);
            mBuilder.setColor(getResources().getColor(android.R.color.white));
        } else {
            mBuilder.setSmallIcon(R.drawable.notification_small);
        }
        mBuilder.setAutoCancel(true);
        mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        mBuilder.setContentTitle(remoteMessage.getData().get("title"));
        mBuilder.setContentText(remoteMessage.getData().get("body"));
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntent(intent);
        mBuilder.setContentIntent(pendingIntent);

        if (notificationManager != null) {
            notificationManager.notify(createRandomCode(notificationId), mBuilder.build());
            // notificationManager.cancelAll();
        }
    }

    public int createRandomCode(int codeLength) {
        char[] chars = "1234567890".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new SecureRandom();
        for (int i = 0; i < codeLength; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        return Integer.parseInt(sb.toString());
    }
}
