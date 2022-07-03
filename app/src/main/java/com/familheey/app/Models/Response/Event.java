package com.familheey.app.Models.Response;

import android.os.Parcel;
import android.os.Parcelable;

import com.familheey.app.Models.Request.HistoryImages;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Event implements Parcelable {
    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("full_name")
    @Expose
    private String fullName;
    @SerializedName("propic")
    @Expose
    private String propic;
    @SerializedName("event_id")
    @Expose
    private Integer eventId;
    @SerializedName("interested_count")
    @Expose
    private String interestedCount;
    @SerializedName("going_count")
    @Expose
    private String goingCount;
    @SerializedName("first_person_going")
    @Expose
    private String firstPersonGoing;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("event_type")
    @Expose
    private String eventType;
    @SerializedName("is_public")
    @Expose
    private Boolean isPublic;
    @SerializedName("event_name")
    @Expose
    private String eventName;
    @SerializedName("event_host")
    @Expose
    private String eventHost;
    @SerializedName("location")
    @Expose
    private String location;
    @SerializedName("from_date")
    @Expose
    private Long fromDate;
    @SerializedName("to_date")
    @Expose
    private Long toDate;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("is_agenda")
    @Expose
    private Boolean isAgenda;
    @SerializedName("rsvp")
    @Expose
    private Boolean rsvp;
    @SerializedName("event_image")
    @Expose
    private String eventImage;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("created_by")
    @Expose
    private String createdBy;
    @SerializedName("event_page")
    @Expose
    private String eventPage;
    @SerializedName("is_active")
    @Expose
    private Boolean isActive;
    @SerializedName("agenda")
    @Expose
    private String agenda;
    @SerializedName("is_item")
    @Expose
    private Boolean isItem;
    @SerializedName("is_document")
    @Expose
    private Boolean isDocument;
    @SerializedName("is_album")
    @Expose
    private Boolean isAlbum;
    @SerializedName("ticket_type")
    @Expose
    private String ticketType;
    @SerializedName("category")
    @Expose
    private String category;
    @SerializedName("lat")
    @Expose
    private Float lat;
    @SerializedName("long")
    @Expose
    private Float _long;
    @SerializedName("allow_others")
    @Expose
    private Boolean allowOthers;
    @SerializedName("event_original_image")
    @Expose
    private String eventOriginalImage;
    @SerializedName("is_cancel")
    @Expose
    private Boolean isCancel;
    @SerializedName("created_by_name")
    @Expose
    private String createdByName;
    @SerializedName("public_event")
    @Expose
    private Boolean publicEvent;
    @SerializedName("invited_by")
    @Expose
    private Integer invitedBy;
    @SerializedName("isShared")
    @Expose
    private String isShared;
    @SerializedName("is_sharable")
    @Expose
    private Boolean isSharable;
    @SerializedName("created_at_")
    @Expose
    private String createdAtListing;
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

    public List<RecurrenceDate> getRecurrenceDate() {
        return recurring_dates;
    }

    public void setRecurrenceDate(List<RecurrenceDate> recurring_dates) {
        recurring_dates = recurring_dates;
    }

    private List<RecurrenceDate> recurring_dates;
    protected Event(Parcel in) {
        if (in.readByte() == 0) {
            userId = null;
        } else {
            userId = in.readInt();
        }
        fullName = in.readString();
        propic = in.readString();
        if (in.readByte() == 0) {
            eventId = null;
        } else {
            eventId = in.readInt();
        }
        interestedCount = in.readString();
        goingCount = in.readString();
        firstPersonGoing = in.readString();
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        eventType = in.readString();
        byte tmpIsPublic = in.readByte();
        isPublic = tmpIsPublic == 0 ? null : tmpIsPublic == 1;
        eventName = in.readString();
        eventHost = in.readString();
        location = in.readString();
        if (in.readByte() == 0) {
            fromDate = null;
        } else {
            fromDate = in.readLong();
        }
        if (in.readByte() == 0) {
            toDate = null;
        } else {
            toDate = in.readLong();
        }
        description = in.readString();
        byte tmpIsAgenda = in.readByte();
        isAgenda = tmpIsAgenda == 0 ? null : tmpIsAgenda == 1;
        byte tmpRsvp = in.readByte();
        rsvp = tmpRsvp == 0 ? null : tmpRsvp == 1;
        eventImage = in.readString();
        createdAt = in.readString();
        updatedAt = in.readString();
        createdBy = in.readString();
        eventPage = in.readString();
        byte tmpIsActive = in.readByte();
        isActive = tmpIsActive == 0 ? null : tmpIsActive == 1;
        agenda = in.readString();
        byte tmpIsItem = in.readByte();
        isItem = tmpIsItem == 0 ? null : tmpIsItem == 1;
        byte tmpIsDocument = in.readByte();
        isDocument = tmpIsDocument == 0 ? null : tmpIsDocument == 1;
        byte tmpIsAlbum = in.readByte();
        isAlbum = tmpIsAlbum == 0 ? null : tmpIsAlbum == 1;
        ticketType = in.readString();
        category = in.readString();
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
        byte tmpAllowOthers = in.readByte();
        allowOthers = tmpAllowOthers == 0 ? null : tmpAllowOthers == 1;
        eventOriginalImage = in.readString();
        byte tmpIsCancel = in.readByte();
        isCancel = tmpIsCancel == 0 ? null : tmpIsCancel == 1;
        createdByName = in.readString();
        byte tmpPublicEvent = in.readByte();
        publicEvent = tmpPublicEvent == 0 ? null : tmpPublicEvent == 1;
        if (in.readByte() == 0) {
            invitedBy = null;
        } else {
            invitedBy = in.readInt();
        }
        isShared = in.readString();
        byte tmpIsSharable = in.readByte();
        isSharable = tmpIsSharable == 0 ? null : tmpIsSharable == 1;
        createdAtListing = in.readString();
        meetingLink = in.readString();
        meetingDialNumber = in.readString();
        meetingPin = in.readString();
        meetingLogo = in.readString();

        is_recurrence = in.readInt();
        recurrence_type = in.readString();
        recurrence_count = in.readInt();
        recurring_dates = in.createTypedArrayList(RecurrenceDate.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (userId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(userId);
        }
        dest.writeString(fullName);
        dest.writeString(propic);
        if (eventId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(eventId);
        }
        dest.writeString(interestedCount);
        dest.writeString(goingCount);
        dest.writeString(firstPersonGoing);
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(id);
        }
        dest.writeString(eventType);
        dest.writeByte((byte) (isPublic == null ? 0 : isPublic ? 1 : 2));
        dest.writeString(eventName);
        dest.writeString(eventHost);
        dest.writeString(location);
        if (fromDate == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(fromDate);
        }
        if (toDate == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(toDate);
        }
        dest.writeString(description);
        dest.writeByte((byte) (isAgenda == null ? 0 : isAgenda ? 1 : 2));
        dest.writeByte((byte) (rsvp == null ? 0 : rsvp ? 1 : 2));
        dest.writeString(eventImage);
        dest.writeString(createdAt);
        dest.writeString(updatedAt);
        dest.writeString(createdBy);
        dest.writeString(eventPage);
        dest.writeByte((byte) (isActive == null ? 0 : isActive ? 1 : 2));
        dest.writeString(agenda);
        dest.writeByte((byte) (isItem == null ? 0 : isItem ? 1 : 2));
        dest.writeByte((byte) (isDocument == null ? 0 : isDocument ? 1 : 2));
        dest.writeByte((byte) (isAlbum == null ? 0 : isAlbum ? 1 : 2));
        dest.writeString(ticketType);
        dest.writeString(category);
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
        dest.writeByte((byte) (allowOthers == null ? 0 : allowOthers ? 1 : 2));
        dest.writeString(eventOriginalImage);
        dest.writeByte((byte) (isCancel == null ? 0 : isCancel ? 1 : 2));
        dest.writeString(createdByName);
        dest.writeByte((byte) (publicEvent == null ? 0 : publicEvent ? 1 : 2));
        if (invitedBy == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(invitedBy);
        }
        dest.writeString(isShared);
        dest.writeByte((byte) (isSharable == null ? 0 : isSharable ? 1 : 2));
        dest.writeString(createdAtListing);
        dest.writeString(meetingLink);
        dest.writeString(meetingDialNumber);
        dest.writeString(meetingPin);
        dest.writeString(meetingLogo);

        dest.writeInt(is_recurrence);
        dest.writeString(recurrence_type);
        dest.writeInt(recurrence_count);
        dest.writeTypedList(recurring_dates);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPropic() {
        return propic;
    }

    public void setPropic(String propic) {
        this.propic = propic;
    }

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public String getInterestedCount() {
        return interestedCount;
    }

    public void setInterestedCount(String interestedCount) {
        this.interestedCount = interestedCount;
    }

    public String getGoingCount() {
        return goingCount;
    }

    public void setGoingCount(String goingCount) {
        this.goingCount = goingCount;
    }

    public String getFirstPersonGoing() {
        return firstPersonGoing;
    }

    public void setFirstPersonGoing(String firstPersonGoing) {
        this.firstPersonGoing = firstPersonGoing;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Boolean getPublic() {
        return isPublic;
    }

    public void setPublic(Boolean aPublic) {
        isPublic = aPublic;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventHost() {
        return eventHost;
    }

    public void setEventHost(String eventHost) {
        this.eventHost = eventHost;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Long getFromDate() {
        return fromDate;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getAgenda() {
        return isAgenda;
    }

    public void setAgenda(String agenda) {
        this.agenda = agenda;
    }

    public void setAgenda(Boolean agenda) {
        isAgenda = agenda;
    }

    public Boolean getItem() {
        return isItem;
    }

    public void setItem(Boolean item) {
        isItem = item;
    }

    public Boolean getDocument() {
        return isDocument;
    }

    public void setDocument(Boolean document) {
        isDocument = document;
    }

    public Boolean getAlbum() {
        return isAlbum;
    }

    public void setAlbum(Boolean album) {
        isAlbum = album;
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

    public Float getLat() {
        return lat;
    }

    public void setLat(Float lat) {
        this.lat = lat;
    }

    public Float get_long() {
        return _long;
    }

    public void set_long(Float _long) {
        this._long = _long;
    }

    public Boolean getAllowOthers() {
        return allowOthers;
    }

    public void setAllowOthers(Boolean allowOthers) {
        this.allowOthers = allowOthers;
    }

    public String getEventOriginalImage() {
        return eventOriginalImage;
    }

    public void setEventOriginalImage(String eventOriginalImage) {
        this.eventOriginalImage = eventOriginalImage;
    }

    public Boolean getCancel() {
        return isCancel;
    }

    public void setCancel(Boolean cancel) {
        isCancel = cancel;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    public Boolean getPublicEvent() {
        return publicEvent;
    }

    public void setPublicEvent(Boolean publicEvent) {
        this.publicEvent = publicEvent;
    }

    public Integer getInvitedBy() {
        return invitedBy;
    }

    public void setInvitedBy(Integer invitedBy) {
        this.invitedBy = invitedBy;
    }

    public String getIsShared() {
        return isShared;
    }

    public void setIsShared(String isShared) {
        this.isShared = isShared;
    }

    public Boolean getRsvp() {
        return rsvp;
    }

    public void setRsvp(Boolean rsvp) {
        this.rsvp = rsvp;
    }

    public String getEventImage() {
        return eventImage;
    }

    public void setEventImage(String eventImage) {
        this.eventImage = eventImage;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getEventPage() {
        return eventPage;
    }

    public void setEventPage(String eventPage) {
        this.eventPage = eventPage;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public Boolean getSharable() {
        return isSharable;
    }

    public String getCreatedAtListing() {
        return createdAtListing;
    }

    public String getFormattedDateForComparison() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(getCreatedAtListing()) * 1000);
        return formatter.format(calendar.getTime());
    }

    public boolean isOnlineEvent() {
        if ("online".equalsIgnoreCase(getEventType()))
            return true;
        return false;
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
        return meetingPin;
    }

    public String getMeetingLogo() {
        return meetingLogo;
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
}
