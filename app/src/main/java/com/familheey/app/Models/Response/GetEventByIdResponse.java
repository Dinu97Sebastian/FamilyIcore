
package com.familheey.app.Models.Response;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class GetEventByIdResponse {

    @SerializedName("data")
    private Data mData;

    public Data getData() {
        return mData;
    }

    public void setData(Data data) {
        mData = data;
    }


    public class Data {

        @SerializedName("contacts")
        private List<EventContacts> mContacts;
        @SerializedName("event")
        private List<EventDetail> mEventDetail;
        @SerializedName("rsvp")
        private List<Rsvp> mRsvp;

        public List<EventContacts> getContacts() {
            return mContacts;
        }

        public void setContacts(List<EventContacts> contacts) {
            mContacts = contacts;
        }

        public List<EventDetail> getEvent() {
            return mEventDetail;
        }

        public void setEvent(List<EventDetail> eventDetail) {
            mEventDetail = eventDetail;
        }

        public List<Rsvp> getRsvp() {
            return mRsvp;
        }

        public void setRsvp(List<Rsvp> rsvp) {
            mRsvp = rsvp;
        }

    }


    public class Rsvp {

        @SerializedName("attendee_id")
        private Long mAttendeeId;
        @SerializedName("attendee_type")
        private String mAttendeeType;
        @SerializedName("createdAt")
        private String mCreatedAt;
        @SerializedName("event_id")
        private Long mEventId;
        @SerializedName("id")
        private Long mId;
        @SerializedName("is_active")
        private Boolean mIsActive;
        @SerializedName("others_count")
        private Long mOthersCount;
        @SerializedName("updatedAt")
        private String mUpdatedAt;


        public Long getAttendeeId() {
            return mAttendeeId;
        }

        public void setAttendeeId(Long attendeeId) {
            mAttendeeId = attendeeId;
        }

        public String getAttendeeType() {
            return mAttendeeType;
        }

        public void setAttendeeType(String attendeeType) {
            mAttendeeType = attendeeType;
        }

        public String getCreatedAt() {
            return mCreatedAt;
        }

        public void setCreatedAt(String createdAt) {
            mCreatedAt = createdAt;
        }

        public Long getEventId() {
            return mEventId;
        }

        public void setEventId(Long eventId) {
            mEventId = eventId;
        }

        public Long getId() {
            return mId;
        }

        public void setId(Long id) {
            mId = id;
        }

        public Boolean getIsActive() {
            return mIsActive;
        }

        public void setIsActive(Boolean isActive) {
            mIsActive = isActive;
        }

        public Long getOthersCount() {
            return mOthersCount;
        }

        public void setOthersCount(Long othersCount) {
            mOthersCount = othersCount;
        }

        public String getUpdatedAt() {
            return mUpdatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            mUpdatedAt = updatedAt;
        }

    }

}




