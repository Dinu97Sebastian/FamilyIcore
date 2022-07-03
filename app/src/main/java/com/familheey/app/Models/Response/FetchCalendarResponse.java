
package com.familheey.app.Models.Response;


import com.familheey.app.Utilities.Utilities;
import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;
import org.threeten.bp.ZoneId;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("unused")
public class FetchCalendarResponse {

    @SerializedName("data")
    private List<Datum> mData;

    public List<Datum> getData() {
        return mData;
    }

    public void setData(List<Datum> data) {
        mData = data;
    }


    public static void displayAllDates(List<Datum> events) {
        for (Datum event : events) {
        }
    }

    public class Datum {

        @SerializedName("created_by_name")
        private String mCreatedByName;
        @SerializedName("created_by_pic")
        private String mCreatedByPic;
        @SerializedName("event_id")
        private String mEventId;
        @SerializedName("event_image")
        private String mEventImage;
        @SerializedName("event_name")
        private String mEventName;
        @SerializedName("from_date")
        private Long mFromDate;
        @SerializedName("to_date")
        private Long mToDate;
        @SerializedName("is_public")
        Boolean isPublic;
        @SerializedName("location")
        String location;
        //Dinu(30/07/2021) for manage recurring event
        @SerializedName("is_recurrence")
        int is_recurrence;
        @SerializedName("recurrence_from_date")
        private Long recurrence_from_date;
        @SerializedName("recurrence_to_date")
        private Long recurrence_to_date;

        public String getCreatedByName() {
            return mCreatedByName;
        }

        public void setCreatedByName(String createdByName) {
            mCreatedByName = createdByName;
        }

        public String getCreatedByPic() {
            return mCreatedByPic;
        }

        public void setCreatedByPic(String createdByPic) {
            mCreatedByPic = createdByPic;
        }

        public String getEventId() {
            return mEventId;
        }

        public void setEventId(String eventId) {
            mEventId = eventId;
        }


        public String getEventImage() {
            return mEventImage;
        }

        public void setEventImage(String eventImage) {
            mEventImage = eventImage;
        }

        public String getEventName() {
            return mEventName;
        }

        public void setEventName(String eventName) {
            mEventName = eventName;
        }

        public Long getFromDate() {
            return mFromDate;
        }

        public void setFromDate(Long fromDate) {
            mFromDate = fromDate;
        }

        public Long getToDate() {
            return mToDate;
        }

        public void setToDate(Long toDate) {
            mToDate = toDate;
        }

        public Boolean getPublic() {
            return isPublic;
        }

        public String getLocation() {
            return location;
        }

        public Long getRecurrence_from_date() {
            return recurrence_from_date;
        }
        public void setRecurrence_from_date(Long recurrence_from_date) {
            recurrence_from_date = recurrence_from_date;
        }
        public Long getRecurrence_to_date() {
            return recurrence_to_date;
        }
        public void setRecurrence_to_date(Long recurrence_to_date) {
            recurrence_to_date = recurrence_to_date;
        }

        public int getIs_recurrence() {
            return is_recurrence;
        }
        public void setIs_recurrence(int is_recurrence) {
            is_recurrence = is_recurrence;
        }

        public String getFormattedDateForComparison() {
            DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy");
            Long value = TimeUnit.SECONDS.toMillis(getFromDate());
            DateTime dateTime = new DateTime(value);
            return formatter.print(dateTime);
        }

        public String getFormattedDate(Long timeInLong) {
            DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy");
            DateTime dateTime = new DateTime(timeInLong);
            return formatter.print(dateTime);
        }

        public List<Long> getEventDates() {
if(is_recurrence==1){
    if(getRecurrence_from_date()!=null && getRecurrence_to_date() !=null)
        return Utilities.getDatesBetween(getRecurrence_from_date(), getRecurrence_to_date());
        else return new ArrayList<>();
}
else {
    if (getFromDate() != null && getToDate() != null)
        return Utilities.getDatesBetween(getFromDate(), getToDate());
    else return new ArrayList<>();
}
        }

        public List<String> getEventFormattedDates() {
            if(is_recurrence==1){
                List<Long> dates = Utilities.getDatesBetween(getRecurrence_from_date(), getRecurrence_to_date());
                List<String> formattedDates = new ArrayList<>();
                for (int i = 0; i < dates.size(); i++) {
                    DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy");
                    DateTime dateTime = new DateTime(dates.get(i));
                    formattedDates.add(formatter.print(dateTime));
                }
                return formattedDates;
            }else{
                List<Long> dates = Utilities.getDatesBetween(getFromDate(), getToDate());
                List<String> formattedDates = new ArrayList<>();
                for (int i = 0; i < dates.size(); i++) {
                    DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy");
                    DateTime dateTime = new DateTime(dates.get(i));
                    formattedDates.add(formatter.print(dateTime));
                }
                return formattedDates;
            }

        }

        public boolean isInEventDateRange(Long timeInMilli) {
            Calendar fromCalendar = Calendar.getInstance();
            fromCalendar.setTimeInMillis(getFromDate() * 1000);
            Calendar toCalendar = Calendar.getInstance();
            fromCalendar.setTimeInMillis(getToDate() * 1000);
            Calendar candidateCalendar = Calendar.getInstance();
            candidateCalendar.setTimeInMillis(timeInMilli);
            LocalDate from = Instant.ofEpochMilli(fromCalendar.getTimeInMillis()).atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate to = Instant.ofEpochMilli(toCalendar.getTimeInMillis()).atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate candidate = Instant.ofEpochMilli(candidateCalendar.getTimeInMillis()).atZone(ZoneId.systemDefault()).toLocalDate();
            return (!candidate.isBefore(from)) && (candidate.isBefore(to));

        }
    }

    @Override
    public String toString() {
        return "FetchCalendarResponse{" +
                "mData=" + mData +
                '}';
    }
}
