package com.familheey.app.Models.Response;

import android.os.Parcel;
import android.os.Parcelable;

import com.familheey.app.Utilities.SharedPref;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class EventDetail implements Parcelable {

    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("event_name")
    @Expose
    private String eventName;

    @SerializedName("remind_on")
    @Expose
    private String remindOn;

    @SerializedName("reminder_id")
    @Expose
    private String reminder_id;
    @SerializedName("ticket_type")
    @Expose
    private String ticketType;
    @SerializedName("category")
    @Expose
    private String category;
    @SerializedName("is_public")
    @Expose
    private Boolean isPublic;
    @SerializedName("event_type")
    @Expose
    private String eventType;
    @SerializedName("rsvp")
    @Expose
    private Boolean rsvp;
    @SerializedName("allow_others")
    @Expose
    private Boolean allowOthers;
    @SerializedName("created_by")
    @Expose
    private Integer createdBy;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("created_by_name")
    @Expose
    private String createdByName;
    @SerializedName("propic")
    @Expose
    private String propic;
    @SerializedName("others_count")
    @Expose
    private String others_count;
    @SerializedName("from_date")
    @Expose
    private Long fromDate;
    @SerializedName("event_image")
    @Expose
    private String eventImage;
    @SerializedName("location")
    @Expose
    private String location;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("lat")
    @Expose
    private Float lat;
    @SerializedName("long")
    @Expose
    private Float _long;
    @SerializedName("is_sharable")
    @Expose
    private Boolean isSharable;
    @SerializedName("event_id")
    @Expose
    private String eventId;
    @SerializedName("event_original_image")
    @Expose
    private String eventOriginalImage;
    private String event_page;
    @SerializedName("event_url")
    private String eventUrl;
    @SerializedName("event_host")
    @Expose
    private String eventHost;
    @SerializedName("to_date")
    @Expose
    private Long toDate;
    @SerializedName("meeting_link")
    @Expose
    private String meetingLink;
    @SerializedName("meeting_dial_number")
    @Expose
    private String meetingDialNumber;
    @SerializedName("meeting_pin")
    @Expose
    private String meetingPin;
    @SerializedName("meeting_logo")
    @Expose
    private String meetingLogo;
    //Dinu(26/07/2021) For implement recurring
    @SerializedName("is_recurrence")
    @Expose
    private int is_recurrence;
    @SerializedName("recurrence_type")
    @Expose
    private String recurrence_type;
    @SerializedName("recurrence_count")
    @Expose
    private int recurrence_count;

    @SerializedName("recurrence_from_date")
    @Expose
    private Long recurrence_from_date;

    @SerializedName("recurrence_to_date")
    @Expose
    private Long recurrence_to_date;


    public EventDetail() {

    }


    protected EventDetail(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        eventName = in.readString();
        remindOn = in.readString();
        reminder_id = in.readString();
        ticketType = in.readString();
        category = in.readString();
        byte tmpIsPublic = in.readByte();
        isPublic = tmpIsPublic == 0 ? null : tmpIsPublic == 1;
        eventType = in.readString();
        byte tmpRsvp = in.readByte();
        rsvp = tmpRsvp == 0 ? null : tmpRsvp == 1;
        byte tmpAllowOthers = in.readByte();
        allowOthers = tmpAllowOthers == 0 ? null : tmpAllowOthers == 1;
        if (in.readByte() == 0) {
            createdBy = null;
        } else {
            createdBy = in.readInt();
        }
        createdAt = in.readString();
        createdByName = in.readString();
        propic = in.readString();
        others_count = in.readString();
        if (in.readByte() == 0) {
            fromDate = null;
        } else {
            fromDate = in.readLong();
        }
        if (in.readByte() == 0) {
            recurrence_from_date = null;
        } else {
            recurrence_from_date = in.readLong();
        }
        eventImage = in.readString();
        location = in.readString();
        description = in.readString();
        if (in.readByte() == 0) {
            lat = null;
        } else {
            lat = in.readFloat();
        }
        if (in.readByte() == 0) {
            _long = null;
        } else {
            _long = in.readFloat();
        }
        byte tmpIsSharable = in.readByte();
        isSharable = tmpIsSharable == 0 ? null : tmpIsSharable == 1;
        eventId = in.readString();
        eventOriginalImage = in.readString();
        event_page = in.readString();
        eventUrl = in.readString();
        eventHost = in.readString();
        if (in.readByte() == 0) {
            toDate = null;
        } else {
            toDate = in.readLong();
        }
        if (in.readByte() == 0) {
            recurrence_to_date= null;
        } else {
            recurrence_to_date = in.readLong();
        }
        meetingLink = in.readString();
        meetingDialNumber = in.readString();
        meetingPin = in.readString();
        meetingLogo = in.readString();

        is_recurrence = in.readInt();
        recurrence_type = in.readString();
        recurrence_count = in.readInt();

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(id);
        }
        dest.writeString(eventName);
        dest.writeString(remindOn);
        dest.writeString(reminder_id);
        dest.writeString(ticketType);
        dest.writeString(category);
        dest.writeByte((byte) (isPublic == null ? 0 : isPublic ? 1 : 2));
        dest.writeString(eventType);
        dest.writeByte((byte) (rsvp == null ? 0 : rsvp ? 1 : 2));
        dest.writeByte((byte) (allowOthers == null ? 0 : allowOthers ? 1 : 2));
        if (createdBy == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(createdBy);
        }
        dest.writeString(createdAt);
        dest.writeString(createdByName);
        dest.writeString(propic);
        dest.writeString(others_count);
        if (fromDate == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(fromDate);
        }
        if (recurrence_from_date == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(recurrence_from_date);
        }
        dest.writeString(eventImage);
        dest.writeString(location);
        dest.writeString(description);
        if (lat == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeFloat(lat);
        }
        if (_long == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeFloat(_long);
        }
        dest.writeByte((byte) (isSharable == null ? 0 : isSharable ? 1 : 2));
        dest.writeString(eventId);
        dest.writeString(eventOriginalImage);
        dest.writeString(event_page);
        dest.writeString(eventUrl);
        dest.writeString(eventHost);
        if (toDate == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(toDate);
        }
        if (recurrence_to_date == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(recurrence_to_date);
        }
        dest.writeString(meetingLink);
        dest.writeString(meetingDialNumber);
        dest.writeString(meetingPin);
        dest.writeString(meetingLogo);

        dest.writeInt(is_recurrence);
        dest.writeString(recurrence_type);
        dest.writeInt(recurrence_count);

    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<EventDetail> CREATOR = new Creator<EventDetail>() {
        @Override
        public EventDetail createFromParcel(Parcel in) {
            return new EventDetail(in);
        }

        @Override
        public EventDetail[] newArray(int size) {
            return new EventDetail[size];
        }
    };

    public String getEventPage() {
        return event_page;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getRemindOn() {
        return remindOn;
    }

    public void setRemindOn(String remindOn) {
        this.remindOn = remindOn;
    }

    public String getTicketType() {
        return ticketType;
    }

    public void setTicketType(String ticketType) {
        this.ticketType = ticketType;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Boolean getRsvp() {
        return rsvp;
    }

    public void setRsvp(Boolean rsvp) {
        this.rsvp = rsvp;
    }

    public Boolean getAllowOthers() {
        return allowOthers;
    }

    public void setAllowOthers(Boolean allowOthers) {
        this.allowOthers = allowOthers;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    public String getPropic() {
        return propic;
    }

    public void setPropic(String propic) {
        this.propic = propic;
    }

    public Long getFromDate() {
        return fromDate;
    }
    public Long getRecurrence_from_date() {
        return recurrence_from_date;
    }
    public void setRecurrence_from_date(Long recurrence_from_date) {
        this.recurrence_from_date = recurrence_from_date;
    }

    public void setFromDate(Long fromDate) {
        this.fromDate = fromDate;
    }

    public Long getToDate() {
        return toDate;
    }

    public void setToDate(Long toDate) {
        this.toDate = toDate;
    }

    public String getEventImage() {
        return eventImage;
    }

    public void setEventImage(String eventImage) {
        this.eventImage = eventImage;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        if (description == null)
            return "";
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsSharable() {
        return isSharable;
    }

    public void setIsSharable(Boolean isSharable) {
        this.isSharable = isSharable;
    }

    public Float getLat() {
        return lat;
    }

    public void setLat(Float lat) {
        this.lat = lat;
    }

    public Float getLong() {
        return _long;
    }

    public void setLong(Float _long) {
        this._long = _long;
    }

    public String getEventOriginalImage() {
        return eventOriginalImage;
    }

    public String getEventUrl() {
        return eventUrl;
    }

    public Calendar getTimeForAlarm() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(getFromDate() * 1000L);
        return calendar;
    }

    public boolean isAdminViewing() {
        return (getCreatedBy() != null && getCreatedBy().toString().equalsIgnoreCase(SharedPref.getUserRegistration().getId()));
    }

    public boolean isPastEvent() {
        if(getIsRecurrence()==1) {
            if (getRecurrence_to_date() == null)
                return true;
            Long toTime = TimeUnit.SECONDS.toMillis(getRecurrence_to_date());
            DateTime dateTime = new DateTime(toTime);
            return dateTime.isBeforeNow();
        }else{
            if (getToDate() == null)
                return true;
            Long toTime = TimeUnit.SECONDS.toMillis(getToDate());
            DateTime dateTime = new DateTime(toTime);
            return dateTime.isBeforeNow();
        }
    }

    public boolean isRegularEvent() {
        return getEventType() == null || !getEventType().equalsIgnoreCase("Sign Up");
    }

    public String getEventHost() {
        return eventHost;
    }

    public String getOthers_count() {
        return others_count;
    }

    public void setOthers_count(String others_count) {
        this.others_count = others_count;
    }

    public String getReminder_id() {
        return reminder_id;
    }

    public void setReminder_id(String reminder_id) {
        this.reminder_id = reminder_id;
    }

    public String getMeetingLink() {
        return meetingLink;
    }

    public String getFormattedMeetingLink() {
        if (meetingLink == null)
            return "";
        return meetingLink.replaceAll("HTTP:", "http:")
                .replaceAll("Http:", "http:")
                .replaceAll("Https:", "https:")
                .replaceAll("HTTPS:", "https:")
                .replaceAll("WWW.", "www.")
                .replaceAll("Www.", "www.");
    }

    public String getMeetingDialNumber() {
        return meetingDialNumber;
    }

    public String getMeetingPin() {
        if (meetingPin != null)
            return meetingPin;
        return "";
    }

    public boolean isOnlineEvent() {
        if ("online".equalsIgnoreCase(getEventType()))
            return true;
        return false;
    }

    public String getMeetingLogo() {
        return meetingLogo;
    }

    public Boolean canShowJoinOnlineEventButton() {
        if (getFromDate() == null)
            return true;
        Long fromTime = TimeUnit.SECONDS.toMillis(getFromDate());
        DateTime dateTime = new DateTime(fromTime);
        return DateTime.now().isAfter(dateTime.minusMinutes(15));
    }

    public int getIsRecurrence() {
        return is_recurrence;
    }
    public void setIsRecurrence(int is_recurrence) {
        is_recurrence = is_recurrence;
    }

    public String getRecurrenceType() {
        return recurrence_type;
    }
    public void setRecurrenceType(String recurrence_type) {
        recurrence_type = recurrence_type;
    }

    public int getRecurrenceCount() {
        return recurrence_count;
    }
    public void setRecurrenceCount(int recurrence_count) {
        recurrence_count = recurrence_count;
    }

    public Long getRecurrence_to_date() {
        return recurrence_to_date;
    }

    public void setRecurrence_to_date(Long recurrence_to_date) {
        this.recurrence_to_date = recurrence_to_date;
    }
}
