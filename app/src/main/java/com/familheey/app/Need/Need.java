package com.familheey.app.Need;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Need implements Parcelable {
    private String post_request_id;
    private String user_id;
    public static final Creator<Need> CREATOR = new Creator<Need>() {
        @Override
        public Need createFromParcel(Parcel source) {
            return new Need(source);
        }

        @Override
        public Need[] newArray(int size) {
            return new Need[size];
        }
    };
    private long start_date;
    private long end_date;
    private String request_location;
    private String group_type;
    private ArrayList<Integer> to_group_id;
    private ArrayList<Group> to_groups;
    private ArrayList<Item> items = new ArrayList<>();


    @SerializedName("supporters")
    @Expose
    private String supporters;
    // Newly Generated
    @SerializedName("full_name")
    @Expose
    private String fullName;
    @SerializedName("propic")
    @Expose
    private String propic;
    @SerializedName("request_title")
    @Expose
    private String requestTitle;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("request_type")
    @Expose
    private String request_type;
    private transient boolean isViewMoreClicked = false;

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    protected Need(Parcel in) {
        this.post_request_id = in.readString();
        this.user_id = in.readString();
        this.thank_post_id = in.readString();
        this.start_date = in.readLong();
        this.end_date = in.readLong();
        this.request_location = in.readString();
        this.group_type = in.readString();
        this.to_group_id = new ArrayList<Integer>();
        in.readList(this.to_group_id, Integer.class.getClassLoader());
        this.to_groups = in.createTypedArrayList(Group.CREATOR);
        this.items = in.createTypedArrayList(Item.CREATOR);
        this.supporters = in.readString();
        this.fullName = in.readString();
        this.propic = in.readString();
        this.requestTitle = in.readString();
        this.createdAt = in.readString();
        this.phone = in.readString();
        this.request_type = in.readString();
    }

    public String getRequest_type() {
        return request_type;
    }

    public long getStart_date() {
        return start_date;
    }

    public void setStart_date(long start_date) {
        this.start_date = start_date;
    }

    public long getEnd_date() {
        return end_date;
    }

    public void setEnd_date(long end_date) {
        this.end_date = end_date;
    }

    public String getRequest_location() {
        return request_location;
    }

    public void setRequest_location(String request_location) {
        this.request_location = request_location;
    }

    private String thank_post_id;

    public String getGroup_type() {
        return group_type;
    }

    public void setGroup_type(String group_type) {
        this.group_type = group_type;
    }

    public ArrayList<Integer> getTo_group_id() {
        return to_group_id;
    }

    public void setTo_group_id(ArrayList<Integer> to_group_id) {
        this.to_group_id = to_group_id;
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public void setItems(ArrayList<Item> items) {
        this.items = items;
    }

    public String getPost_request_id() {
        return post_request_id;
    }

    public void setPost_request_id(String post_request_id) {
        this.post_request_id = post_request_id;
    }

    // Newly Generated

    public String getFullName() {
        return fullName;
    }

    public String getPropic() {
        return propic;
    }

    public String getRequestTitle() {
        return requestTitle;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public boolean isViewMoreClicked() {
        return isViewMoreClicked;
    }

    public void setViewMoreClicked(boolean isViewMoreClicked) {
        this.isViewMoreClicked = isViewMoreClicked;
    }

    public ArrayList<Group> getTo_groups() {
        return to_groups;
    }

    public Need() {
    }

    public String getSupporters() {
        return supporters;
    }

    public String getPhone() {
        return phone;
    }

    public List<Item> getFirstTwoNeedItems() {
        List<Item> needs = new ArrayList<>();
        for (int i = 0; i < getItems().size(); i++) {
            needs.add(getItems().get(i));
            if (needs.size() == 2)
                return needs;
        }
        return needs;
    }

    public List<Item> getViewMoreAppliedNeedItems() {
        if (isViewMoreClicked())
            return getItems();
        else {
            if (isMoreThanTwoItems()) {
                return getFirstTwoNeedItems();
            } else {
                return getItems();
            }
        }

    }

    public String getFormattedCreatedAt() {
        if (getCreatedAt() != null && getCreatedAt().length() > 0) {
            DateTime dateTime = ISODateTimeFormat.dateTimeParser().parseDateTime(getCreatedAt());
            DateTimeFormatter dtfOut = DateTimeFormat.forPattern("MMM dd yyyy hh:mm aa");
            return dtfOut.print(dateTime);
        }
        return null;
    }

    public String getFormattedNeedStartDate() {
        return dateFormat(getStart_date(), "MMM dd yyyy hh:mm aa");
    }

    public String getFormattedNeedEndDate() {
        return dateFormat(getEnd_date(), "MMM dd yyyy hh:mm aa");
    }

    public String getFormattedPostedIn() {
        if (getTo_groups().size() > 0) {
            if (getTo_groups().size() == 1) {
                return "Posted in " + getTo_groups().get(0).getGroup_name();
            }
            else if(getTo_groups().size() == 2){
                return "Posted in " + getTo_groups().get(0).getGroup_name() + " and other 1 family";
            }

            else {
                return "Posted in " + getTo_groups().get(0).getGroup_name() + " and other " + (getTo_groups().size() - 1) + " families";
            }
        }
        return "";
    }

    public String getFormattedPostedInTime() {
        try {
            if (isSameDate(getStart_date(), getEnd_date())) {
                String fromTime1 = dateFormat(getStart_date(), "hh:mm aa");
                String toTime1 = dateFormat(getEnd_date(), "hh:mm aa");
                String fromTime = dateFormat(getStart_date(), "EEE");
                return String.format(fromTime + ", " + "%s - %s", fromTime1, toTime1);
            } else {
                String fromTime = dateFormat(getStart_date(), "EEE MMM dd");
                String toTime = dateFormat(getEnd_date(), "EEE MMM dd, yyyy");
                return String.format("%s - %s", fromTime, toTime);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Boolean isSameDate(Long from, Long to) {

        if (from != null && to != null) {
            DateTimeFormatter dtfOut = DateTimeFormat.forPattern("yyyyMMdd");
            from = TimeUnit.SECONDS.toMillis(from);
            to = TimeUnit.SECONDS.toMillis(to);
            DateTime date1 = new DateTime(from);
            DateTime date2 = new DateTime(to);
            return dtfOut.print(date1).equals(dtfOut.print(date2));
        } else return false;
    }

    private String dateFormat(Long value, String format) {
        if (value != null&&value>100) {
            value = TimeUnit.SECONDS.toMillis(value);
            DateTime dateTime = new DateTime(value);
            DateTimeFormatter dtfOut = DateTimeFormat.forPattern(format);
            return dtfOut.print(dateTime);
        }
        return "";
    }

    public void setRequest_type(String request_type) {
        this.request_type = request_type;
    }

    public String getThank_post_id() {
        return thank_post_id;
    }

    public boolean isMoreThanTwoItems() {
        return getItems() != null && getItems().size() > 2;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.post_request_id);
        dest.writeString(this.user_id);
        dest.writeString(this.thank_post_id);
        dest.writeLong(this.start_date);
        dest.writeLong(this.end_date);
        dest.writeString(this.request_location);
        dest.writeString(this.group_type);
        dest.writeList(this.to_group_id);
        dest.writeTypedList(this.to_groups);
        dest.writeTypedList(this.items);
        dest.writeString(this.supporters);
        dest.writeString(this.fullName);
        dest.writeString(this.propic);
        dest.writeString(this.requestTitle);
        dest.writeString(this.createdAt);
        dest.writeString(this.phone);
        dest.writeString(this.request_type);
    }
}