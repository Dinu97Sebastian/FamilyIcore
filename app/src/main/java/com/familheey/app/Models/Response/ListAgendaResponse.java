package com.familheey.app.Models.Response;

import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class ListAgendaResponse {

    @SerializedName("data")
    private List<Datum> mData = new ArrayList<>();

    public List<Datum> getData() {
        return mData;
    }

    public void setData(List<Datum> data) {
        mData = data;
    }

    public class Datum {

        @SerializedName("data")
        private List<Datum> mData = new ArrayList<>();
        @SerializedName("date")
        private String mDate;
        @SerializedName("description")
        private String mDescription;
        @SerializedName("event_created_time")
        private String mEventCreatedTime;
        @SerializedName("event_date")
        private String mEventDate;
        @SerializedName("event_end_time")
        private String mEventEndTime;
        @SerializedName("event_id")
        private Long mEventId;
        @SerializedName("event_start_time")
        private String mEventStartTime;
        @SerializedName("id")
        private Long mId;
        @SerializedName("title")
        private String mTitle;
        @SerializedName("user_id")
        private Long mUserId;
        @SerializedName("agenda_pic")
        private String agendaPic;
        @SerializedName("end_date")
        private String endDate;
        @SerializedName("start_date")
        private String startDate;

        public List<Datum> getData() {
            return mData;
        }

        public void setData(List<Datum> data) {
            mData = data;
        }

        public String getDate() {
            return mDate;
        }

        public void setDate(String date) {
            mDate = date;
        }

        public String
        getDescription() {
            return mDescription;
        }

        public void setDescription(String description) {
            mDescription = description;
        }

        public String getEventCreatedTime() {
            return mEventCreatedTime;
        }

        public void setEventCreatedTime(String eventCreatedTime) {
            mEventCreatedTime = eventCreatedTime;
        }

        public String getEventDate() {
            return mEventDate;
        }

        public void setEventDate(String eventDate) {
            mEventDate = eventDate;
        }

        public String getEventEndTime() {
            return mEventEndTime;
        }

        public void setEventEndTime(String eventEndTime) {
            mEventEndTime = eventEndTime;
        }

        public Long getEventId() {
            return mEventId;
        }

        public void setEventId(Long eventId) {
            mEventId = eventId;
        }

        public String getEventStartTime() {
            return mEventStartTime;
        }

        public void setEventStartTime(String eventStartTime) {
            mEventStartTime = eventStartTime;
        }

        public Long getId() {
            return mId;
        }

        public void setId(Long id) {
            mId = id;
        }

        public String getTitle() {
            return mTitle;
        }

        public void setTitle(String title) {
            mTitle = title;
        }

        public Long getUserId() {
            return mUserId;
        }

        public void setUserId(Long userId) {
            mUserId = userId;
        }

        public String getEndDate() {
            return endDate;
        }

        public String getStartDate() {
            return startDate;
        }

        public String getFormattedDate() {
            SimpleDateFormat userDisplay = new SimpleDateFormat("MMM dd yyyy", Locale.getDefault());
            SimpleDateFormat apiDisplay = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            try {
                Date date = apiDisplay.parse(getDate());
                return userDisplay.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
                return getDate();
            }
        }

        public String getFormattedDate(String dateToBeFormatted) {
            try {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                Date date = simpleDateFormat.parse(dateToBeFormatted);
                SimpleDateFormat ourFormat = new SimpleDateFormat("hh:mm aa", Locale.getDefault());
                return ourFormat.format(date);
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        }

        public String getFormattedStartTime() {
            return getFormattedDate(getStartDate());
        }

        public String getFormattedEndTime() {
            return getFormattedDate(getEndDate());
        }

        public String getAgendaPic() {
            return agendaPic;
        }
    }
}
