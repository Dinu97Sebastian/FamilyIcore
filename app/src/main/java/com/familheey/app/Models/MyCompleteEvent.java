package com.familheey.app.Models;

import com.familheey.app.Models.Response.Event;

import java.util.List;

public class MyCompleteEvent {

    private String eventType;
    private List<Event> events;

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }
}
