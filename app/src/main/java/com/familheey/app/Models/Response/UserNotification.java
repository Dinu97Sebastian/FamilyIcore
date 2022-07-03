package com.familheey.app.Models.Response;

import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;

import com.familheey.app.Activities.CalendarActivity;
import com.familheey.app.Activities.ChatActivity;
import com.familheey.app.Activities.CreatedEventDetailActivity;
import com.familheey.app.Activities.FamilyDashboardActivity;
import com.familheey.app.Activities.GuestActivity;
import com.familheey.app.Activities.ProfileActivity;
import com.familheey.app.Announcement.AnnouncementDetailActivity;
import com.familheey.app.Need.NeedRequestDetailedActivity;
import com.familheey.app.Post.PostDetailForPushActivity;
import com.familheey.app.Topic.MainActivity;
import com.familheey.app.Topic.TopicChatActivity;
import com.familheey.app.Utilities.Constants;
import com.familheey.app.Utilities.Utilities;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import static com.familheey.app.Utilities.Constants.Bundle.DATA;
import static com.familheey.app.Utilities.Constants.Bundle.GUEST_ATTENDING;
import static com.familheey.app.Utilities.Constants.Bundle.GUEST_INTERESTED;
import static com.familheey.app.Utilities.Constants.Bundle.GUEST_MAY_ATTEND;
import static com.familheey.app.Utilities.Constants.Bundle.ID;
import static com.familheey.app.Utilities.Constants.Bundle.LINKED_FAMILIES;
import static com.familheey.app.Utilities.Constants.Bundle.LINK_FAMILY_REQUEST;
import static com.familheey.app.Utilities.Constants.Bundle.MEMBER;
import static com.familheey.app.Utilities.Constants.Bundle.POSITION;
import static com.familheey.app.Utilities.Constants.Bundle.PUSH;
import static com.familheey.app.Utilities.Constants.Bundle.REQUEST;
import static com.familheey.app.Utilities.Constants.Bundle.SUB_TYPE;
import static com.familheey.app.Utilities.Constants.Bundle.TYPE;

public class UserNotification implements Parcelable {

    public static final Creator<UserNotification> CREATOR = new Creator<UserNotification>() {
        @Override
        public UserNotification createFromParcel(Parcel in) {
            return new UserNotification(in);
        }

        @Override
        public UserNotification[] newArray(int size) {
            return new UserNotification[size];
        }
    };
    @SerializedName("category")
    @Expose
    private String category;
    @SerializedName("create_time")
    @Expose
    private String create_time;
    @SerializedName("from_id")
    @Expose
    private String from_id;
    @SerializedName("link_to")
    @Expose
    private String link_to;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("message_title")
    @Expose
    private String message_title;
    @SerializedName("propic")
    @Expose
    private String propic;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("visible_status")
    @Expose
    private String visible_status;
    String key;
    @SerializedName("type_id")
    @Expose
    private String type_id;


    @SerializedName("sub_type")
    @Expose
    private String sub_type;

    public UserNotification() {
    }

    protected UserNotification(Parcel in) {
        key = in.readString();
        category = in.readString();
        create_time = in.readString();
        from_id = in.readString();
        link_to = in.readString();
        message = in.readString();
        message_title = in.readString();
        propic = in.readString();
        type = in.readString();
        visible_status = in.readString();
        type_id = in.readString();
        sub_type = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(key);
        dest.writeString(category);
        dest.writeString(create_time);
        dest.writeString(from_id);
        dest.writeString(link_to);
        dest.writeString(message);
        dest.writeString(message_title);
        dest.writeString(propic);
        dest.writeString(type);
        dest.writeString(visible_status);
        dest.writeString(type_id);
        dest.writeString(sub_type);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getFrom_id() {
        return from_id;
    }

    public void setFrom_id(String from_id) {
        this.from_id = from_id;
    }

    public String getLink_to() {
        return link_to;
    }

    public void setLink_to(String link_to) {
        this.link_to = link_to;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage_title() {
        return message_title;
    }

    public void setMessage_title(String message_title) {
        this.message_title = message_title;
    }

    public String getPropic() {
        return propic;
    }

    public void setPropic(String propic) {
        this.propic = propic;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVisible_status() {
        return visible_status;
    }

    public void setVisible_status(String visible_status) {
        this.visible_status = visible_status;
    }

    public String getType_id() {
        return type_id;
    }

    public void setType_id(String type_id) {
        this.type_id = type_id;
    }


    public String getSub_type() {
        return sub_type;
    }

    public void setSub_type(String sub_type) {
        this.sub_type = sub_type;
    }

    public boolean isMessageRead() {
        return getVisible_status().equalsIgnoreCase("read");
    }

    public static final int getUnreadUserNotifications(List<UserNotification> userNotifications) {
        int unreadNotificationCount = 0;
        for (UserNotification userNotification : userNotifications) {

            if (!userNotification.type.equals(""))
                if (!userNotification.isMessageRead())
                    unreadNotificationCount++;
        }
        return unreadNotificationCount;
    }

    public void goToCorrespondingDashboard(Context context) {
        if (Utilities.isNullOrEmpty(type_id)) {
            return;
        }
        Intent intent;
        switch (getType()) {
            case "home": {
                if ("familyfeed".equals(getSub_type()) || "publicfeed".equals(getSub_type())) {
                    intent = new Intent(context, PostDetailForPushActivity.class).putExtra("ids", type_id).putExtra(TYPE, "NOTIFICATION");
                } else if ("requestFeed".equals(getSub_type())) {
                    intent = new Intent(context, NeedRequestDetailedActivity.class).putExtra("FROM", "INAPP").putExtra(TYPE, MEMBER).putExtra(DATA, type_id);
                } else {
                    intent = new Intent(context, MainActivity.class);
                }
                break;
            }
            case "announcement": {
                if ("conversation".equals(getSub_type())) {
                    intent = new Intent(context, ChatActivity.class)
                            .putExtra(DATA, type_id)
                            .putExtra("POS", 0)
                            .putExtra(TYPE, "NOTIFICATION")
                            .putExtra(SUB_TYPE, "ANNOUNCEMENT");
                } else {
                    intent = new Intent(context, AnnouncementDetailActivity.class).putExtra(TYPE, "NOTIFICATION").putExtra("id", type_id);
                }
                break;
            }
            case "event":
                EventDetail eventDetail = new EventDetail();
                eventDetail.setEventId(type_id);
                switch (getSub_type()) {
                    case GUEST_INTERESTED:
                        intent = new Intent(context, GuestActivity.class).putExtra(TYPE, PUSH).putExtra(Constants.Bundle.DATA, type_id).putExtra(POSITION, GUEST_MAY_ATTEND);
                        break;
                    case GUEST_ATTENDING:
                        intent = new Intent(context, GuestActivity.class).putExtra(TYPE, PUSH).putExtra(Constants.Bundle.DATA, type_id).putExtra(POSITION, GUEST_ATTENDING);
                        break;
                    case "calendar":
                        intent = new Intent(context, CalendarActivity.class).putExtra(TYPE, PUSH).putExtra(ID, type_id).putExtra(SUB_TYPE, getSub_type());
                        break;
                    default:
                        intent = new Intent(context, CreatedEventDetailActivity.class).putExtra(TYPE, PUSH).putExtra(ID, type_id).putExtra(SUB_TYPE, getSub_type());
                        break;
                }
                break;
            case "family":
                switch (getSub_type()) {
                    case "member":
                        intent = new Intent(context, FamilyDashboardActivity.class).putExtra(TYPE, MEMBER).putExtra(DATA, type_id);
                        break;
                    case "request":
                        intent = new Intent(context, FamilyDashboardActivity.class).putExtra(TYPE, REQUEST).putExtra(DATA, type_id);
                        break;
                    case "family_link":
                        intent = new Intent(context, FamilyDashboardActivity.class).putExtra(TYPE, LINK_FAMILY_REQUEST).putExtra(DATA, type_id);
                        break;
                    case "fetch_link":
                        intent = new Intent(context, FamilyDashboardActivity.class).putExtra(TYPE, LINKED_FAMILIES).putExtra(DATA, type_id);
                        break;
                    default:
                        intent = new Intent(context, FamilyDashboardActivity.class).putExtra(TYPE, PUSH).putExtra(DATA, type_id);
                        break;
                }
                break;
            case "user":
                if ("request".equals(getSub_type())) {
                    intent = new Intent(context, ProfileActivity.class).putExtra(TYPE, REQUEST).putExtra(DATA, type_id);
                } else {
                    intent = new Intent(context, ProfileActivity.class).putExtra(TYPE, PUSH).putExtra(DATA, type_id);
                }
                break;

            case "request":
                intent = new Intent(context, NeedRequestDetailedActivity.class).putExtra("FROM", "INAPP").putExtra(TYPE, MEMBER).putExtra(DATA, type_id);
                break;
            case "post":
                intent = new Intent(context, ChatActivity.class)
                        .putExtra(DATA, type_id)
                        .putExtra("POS", 0)
                        .putExtra(TYPE, "NOTIFICATION")
                        .putExtra(SUB_TYPE, "POST");
                break;
            case "topic":
                intent = new Intent(context, TopicChatActivity.class).putExtra(DATA, type_id);
                break;
            default:
                intent = new Intent(context, MainActivity.class);
                break;
        }
        context.startActivity(intent);
    }
}
