
package com.familheey.app.Models.Response;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class EventInvitationsResponse {

    @SerializedName("data")
    private List<Datum> mData = new ArrayList<>();

    public List<Datum> getData() {
        return mData;
    }

    public void setData(List<Datum> data) {
        mData = data;
    }



    public class Datum {

        @SerializedName("category")
        private String mCategory;
        @SerializedName("created_at")
        private String mCreatedAt;
        @SerializedName("event_image")
        private Object mEventImage;
        @SerializedName("event_name")
        private String mEventName;
        @SerializedName("event_type")
        private String mEventType;
        @SerializedName("from_date")
        private Long mFromDate;
        @SerializedName("id")
        private Long mId;
        @SerializedName("invited_by")
        private Object mInvitedBy;
        @SerializedName("location")
        private String mLocation;
        @SerializedName("public_event")
        private Boolean mPublicEvent;
        @SerializedName("rsvp")
        private Boolean mRsvp;
        @SerializedName("ticket_type")
        private String mTicketType;
        @SerializedName("to_date")
        private Long mToDate;

        public String getCategory() {
            return mCategory;
        }

        public void setCategory(String category) {
            mCategory = category;
        }

        public String getCreatedAt() {
            return mCreatedAt;
        }

        public void setCreatedAt(String createdAt) {
            mCreatedAt = createdAt;
        }

        public Object getEventImage() {
            return mEventImage;
        }

        public void setEventImage(Object eventImage) {
            mEventImage = eventImage;
        }

        public String getEventName() {
            return mEventName;
        }

        public void setEventName(String eventName) {
            mEventName = eventName;
        }

        public String getEventType() {
            return mEventType;
        }

        public void setEventType(String eventType) {
            mEventType = eventType;
        }

        public Long getFromDate() {
            return mFromDate;
        }

        public void setFromDate(Long fromDate) {
            mFromDate = fromDate;
        }

        public Long getId() {
            return mId;
        }

        public void setId(Long id) {
            mId = id;
        }

        public Object getInvitedBy() {
            return mInvitedBy;
        }

        public void setInvitedBy(Object invitedBy) {
            mInvitedBy = invitedBy;
        }

        public String getLocation() {
            return mLocation;
        }

        public void setLocation(String location) {
            mLocation = location;
        }

        public Boolean getPublicEvent() {
            return mPublicEvent;
        }

        public void setPublicEvent(Boolean publicEvent) {
            mPublicEvent = publicEvent;
        }

        public Boolean getRsvp() {
            return mRsvp;
        }

        public void setRsvp(Boolean rsvp) {
            mRsvp = rsvp;
        }

        public String getTicketType() {
            return mTicketType;
        }

        public void setTicketType(String ticketType) {
            mTicketType = ticketType;
        }

        public Long getToDate() {
            return mToDate;
        }

        public void setToDate(Long toDate) {
            mToDate = toDate;
        }

    }


}
