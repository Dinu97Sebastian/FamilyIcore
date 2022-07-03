package com.familheey.app.Models.Response;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RecurrenceDate implements Parcelable {
    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("event_id")
    @Expose
    private Integer event_id;
    @SerializedName("recurrence_from_date")
    @Expose
    private Long recurrence_from_date;
    @SerializedName("recurrence_to_date")
    @Expose
    private Long recurrence_to_date;
    @SerializedName("invited_by")
    @Expose
    private Integer invited_by;


    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getEvent_id() {
        return event_id;
    }

    public void setEvent_id(Integer event_id) {
        this.event_id = event_id;
    }

    public Long getRecurrence_from_date() {
        return recurrence_from_date;
    }

    public void setRecurrence_from_date(Long recurrence_from_date) {
        this.recurrence_from_date = recurrence_from_date;
    }

    public Long getRecurrence_to_date() {
        return recurrence_to_date;
    }

    public void setRecurrence_to_date(Long recurrence_to_date) {
        this.recurrence_to_date = recurrence_to_date;
    }

    public Integer getInvited_by() {
        return invited_by;
    }

    public void setInvited_by(Integer invited_by) {
        this.invited_by = invited_by;
    }


    protected RecurrenceDate(Parcel in) {

        if (in.readByte() == 0) {
            userId = null;
        } else {
            userId = in.readInt();
        }
        if (in.readByte() == 0) {
            invited_by = null;
        } else {
            invited_by = in.readInt();
        }
        if (in.readByte() == 0) {
            event_id = null;
        } else {
            event_id = in.readInt();
        }
        recurrence_from_date = in.readLong();
        recurrence_to_date = in.readLong();
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (userId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(userId);
        }
        if (invited_by == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(invited_by);
        }
        if (event_id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(event_id);
        }
        dest.writeLong(recurrence_from_date);
        dest.writeLong(recurrence_to_date);
    }

    public static final Creator<RecurrenceDate> CREATOR = new Creator<RecurrenceDate>() {
        @Override
        public RecurrenceDate createFromParcel(Parcel in) {
            return new RecurrenceDate(in);
        }

        @Override
        public RecurrenceDate[] newArray(int size) {
            return new RecurrenceDate[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

}
