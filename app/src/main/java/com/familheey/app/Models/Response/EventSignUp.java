package com.familheey.app.Models.Response;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EventSignUp implements Parcelable {
    @SerializedName("created_at")
    private String mCreatedAt;
    @SerializedName("end_date")
    private String mEndDate;
    @SerializedName("event_id")
    private int mEventId;
    @SerializedName("id")
    private int mId;
    @SerializedName("is_active")
    private Boolean mIsActive;
    public static final Creator<EventSignUp> CREATOR = new Creator<EventSignUp>() {
        @Override
        public EventSignUp createFromParcel(Parcel in) {
            return new EventSignUp(in);
        }

        @Override
        public EventSignUp[] newArray(int size) {
            return new EventSignUp[size];
        }
    };
    @SerializedName("needed")
    private int mNeeded;
    @SerializedName("quantity_collected")
    private int mQuantityCollected;
    @SerializedName("slot_description")
    private String mSlotDescription;
    @SerializedName("slot_title")
    private String mSlotTitle;
    @SerializedName("start_date")
    private String mStartDate;
    @SerializedName("updated_at")
    private String mUpdatedAt;
    @SerializedName("user_id")
    private int mUserId;
    @SerializedName("item_quantity")
    private int mItemQuantity;

    protected EventSignUp(Parcel in) {
        mCreatedAt = in.readString();
        mEndDate = in.readString();
        mEventId = in.readInt();
        mId = in.readInt();
        byte tmpMIsActive = in.readByte();
        mIsActive = tmpMIsActive == 0 ? null : tmpMIsActive == 1;
        mItemQuantity = in.readInt();
        mNeeded = in.readInt();
        mQuantityCollected = in.readInt();
        mSlotDescription = in.readString();
        mSlotTitle = in.readString();
        mStartDate = in.readString();
        mUpdatedAt = in.readString();
        mUserId = in.readInt();
    }

    public int getmNeeded() {
        return mNeeded;
    }

    public String getCreatedAt() {
        return mCreatedAt;
    }

    public void setCreatedAt(String createdAt) {
        mCreatedAt = createdAt;
    }

    public String getEndDate() {
        return mEndDate;
    }

    public void setEndDate(String endDate) {
        mEndDate = endDate;
    }

    public int getEventId() {
        return mEventId;
    }

    public void setEventId(int eventId) {
        mEventId = eventId;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public Boolean getIsActive() {
        return mIsActive;
    }

    public void setIsActive(Boolean isActive) {
        mIsActive = isActive;
    }

    public int getItemQuantity() {
        return mItemQuantity;
    }

    public void setItemQuantity(int itemQuantity) {
        mItemQuantity = itemQuantity;
    }

    public int getNeeded() {
        return mNeeded;
    }

    public void setNeeded(int needed) {
        mNeeded = needed;
    }

    public int getQuantityCollected() {
        return mQuantityCollected;
    }

    public void setQuantityCollected(int quantityCollected) {
        mQuantityCollected = quantityCollected;
    }

    public String getSlotDescription() {
        return mSlotDescription;
    }

    public void setSlotDescription(String slotDescription) {
        mSlotDescription = slotDescription;
    }

    public String getSlotTitle() {
        return mSlotTitle;
    }

    public void setSlotTitle(String slotTitle) {
        mSlotTitle = slotTitle;
    }

    public String getStartDate() {
        return mStartDate;
    }

    public void setStartDate(String startDate) {
        mStartDate = startDate;
    }

    public String getUpdatedAt() {
        return mUpdatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        mUpdatedAt = updatedAt;
    }

    public int getUserId() {
        return mUserId;
    }

    public void setUserId(int userId) {
        mUserId = userId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mCreatedAt);
        dest.writeString(mEndDate);
        dest.writeInt(mEventId);
        dest.writeInt(mId);
        dest.writeByte((byte) (mIsActive == null ? 0 : mIsActive ? 1 : 2));
        dest.writeInt(mItemQuantity);
        dest.writeInt(mNeeded);
        dest.writeInt(mQuantityCollected);
        dest.writeString(mSlotDescription);
        dest.writeString(mSlotTitle);
        dest.writeString(mStartDate);
        dest.writeString(mUpdatedAt);
        dest.writeInt(mUserId);
    }

    public String getTimeRange() throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd yyyy", Locale.getDefault());
        SimpleDateFormat timeOnlyFormat = new SimpleDateFormat("hh:mm aa", Locale.getDefault());
        SimpleDateFormat fetchDisplay = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        Date from = fetchDisplay.parse(getStartDate());
        String fromDate = dateFormat.format(from);
        String requiredFormat = "";
        String fromTime = timeOnlyFormat.format(from);
        Date to = fetchDisplay.parse(getEndDate());
        String toDate = dateFormat.format(to);
        String toTime = timeOnlyFormat.format(to);
        if (fromDate.equalsIgnoreCase(toDate)) {
            requiredFormat += fromDate + ", ";
            requiredFormat += fromTime + "-" + toTime;
        } else {
            requiredFormat += fromDate + " " + fromTime + "-" + toDate + " " + toTime;
        }
        return requiredFormat;
    }
}
