package com.familheey.app.Models.Request;

import java.io.Serializable;

public class CreateEventRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private String user_id;
    private String event_id;
    private String remind_id;
    private int remind_on;
    private String event_date;
    private String remind_date;
    private String is_recurrence;

    private String timezone_offset;

    public String getTimezone_offset() {
        return timezone_offset;
    }

    public void setTimezone_offset(String timezone_offset) {
        this.timezone_offset = timezone_offset;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getEvent_id() {
        return event_id;
    }

    public void setEvent_id(String event_id) {
        this.event_id = event_id;
    }

    public int getRemind_on() {
        return remind_on;
    }

    public void setRemind_on(int remind_on) {
        this.remind_on = remind_on;
    }

    public String getEvent_date() {
        return event_date;
    }

    public void setEvent_date(String event_date) {
        this.event_date = event_date;
    }

    public String getRemind_date() {
        return remind_date;
    }

    public void setRemind_date(String remind_date) {
        this.remind_date = remind_date;
    }

    public String getRemind_id() {
        return remind_id;
    }

    public void setRemind_id(String remind_id) {
        this.remind_id = remind_id;
    }
    public void setIs_recurrence(String is_recurrence) {
        this.is_recurrence = is_recurrence;
    }
    public String  getIs_recurrence() {
        return is_recurrence;
    }
}
