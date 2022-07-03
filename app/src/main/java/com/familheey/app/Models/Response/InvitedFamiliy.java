package com.familheey.app.Models.Response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class InvitedFamiliy {
    @SerializedName("event_id")
    @Expose
    private Integer eventId;
    @SerializedName("group_id")
    @Expose
    private Integer groupId;
    @SerializedName("event_name")
    @Expose
    private String eventName;
    @SerializedName("event_image")
    @Expose
    private String eventImage;
    @SerializedName("event_original_image")
    @Expose
    private Object eventOriginalImage;
    @SerializedName("location")
    @Expose
    private String location;
    @SerializedName("from_date")
    @Expose
    private Integer fromDate;
    @SerializedName("to_date")
    @Expose
    private Integer toDate;
    @SerializedName("group_name")
    @Expose
    private String groupName;
    @SerializedName("base_region")
    @Expose
    private String baseRegion;
    @SerializedName("logo")
    @Expose
    private String logo;

    public Integer getEventId() {
        return eventId;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public String getEventName() {
        return eventName;
    }

    public String getEventImage() {
        return eventImage;
    }

    public Object getEventOriginalImage() {
        return eventOriginalImage;
    }

    public String getLocation() {
        return location;
    }

    public Integer getFromDate() {
        return fromDate;
    }

    public Integer getToDate() {
        return toDate;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getBaseRegion() {
        return baseRegion;
    }

    public String getLogo() {
        return logo;
    }
}
