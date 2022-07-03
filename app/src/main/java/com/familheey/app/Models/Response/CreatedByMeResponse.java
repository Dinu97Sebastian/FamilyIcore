
package com.familheey.app.Models.Response;

import com.familheey.app.Models.MyCompleteEvent;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class CreatedByMeResponse {

    @SerializedName("data")
    private Data mData;

    public Data getData() {
        return mData;
    }

    public void setData(Data data) {
        mData = data;
    }

    public List<MyCompleteEvent> getMyCompleteEvents() {
        List<MyCompleteEvent> myCompleteEvents = new ArrayList<>();
        if (getData() != null && getData().getMyEventList() != null && getData().getMyEventList().getCreatedByMe() != null && getData().getMyEventList().getCreatedByMe().size() > 0) {
            MyCompleteEvent createdByMe = new MyCompleteEvent();
            createdByMe.setEvents(getData().getMyEventList().getCreatedByMe());
            createdByMe.setEventType("Created by me");
            myCompleteEvents.add(createdByMe);
        }
        if (getData() != null && getData().getMyEventList() != null && getData().getMyEventList().getRsvpYes() != null && getData().getMyEventList().getRsvpYes().size() > 0) {
            MyCompleteEvent createdByMe = new MyCompleteEvent();
            createdByMe.setEvents(getData().getMyEventList().getRsvpYes());
            createdByMe.setEventType("Attending");
            myCompleteEvents.add(createdByMe);
        }
        if (getData() != null && getData().getMyEventList() != null && getData().getMyEventList().getRsvpMaybe() != null && getData().getMyEventList().getRsvpMaybe().size() > 0) {
            MyCompleteEvent createdByMe = new MyCompleteEvent();
            createdByMe.setEvents(getData().getMyEventList().getRsvpMaybe());
            createdByMe.setEventType("Interested");
            myCompleteEvents.add(createdByMe);
        }
        if (getData() != null && getData().getPastEventList() != null && getData().getPastEventList().getData() != null && getData().getPastEventList().getData().size() > 0) {
            MyCompleteEvent createdByMe = new MyCompleteEvent();
            if(getData().getPastEventList().getData().size()>50)
                createdByMe.setEvents(getData().getPastEventList().getData().subList(0,50));
            else
                createdByMe.setEvents(getData().getPastEventList().getData());
            createdByMe.setEventType("Past Events");
            myCompleteEvents.add(createdByMe);
        }
        return myCompleteEvents;
    }

    public MyCompleteEvent getMyEventsRange(int toIndex) {
        MyCompleteEvent createdByMe = new MyCompleteEvent();
        List<MyCompleteEvent> myCompleteEvents = new ArrayList<>();
        if (getData() != null && getData().getPastEventList() != null && getData().getPastEventList().getData() != null && getData().getPastEventList().getData().size() > 0) {

            createdByMe.setEvents(getData().getPastEventList().getData().subList(0,toIndex));
            createdByMe.setEventType("Past Events");
            myCompleteEvents.add(createdByMe);
        }
        return createdByMe;
    }

    public int getPastMyEventCount() {

        if (getData() != null && getData().getPastEventList() != null && getData().getPastEventList().getData() != null && getData().getPastEventList().getData().size() > 0) {
           return getData().getPastEventList().getData().size();
        }
        else return 0;
    }


    public class Data {

        @SerializedName("my_event_list")
        private MyEventList mMyEventList;

        @SerializedName("past_event_list")
        private MyEventList pastEventList;

        public MyEventList getMyEventList() {
            return mMyEventList;
        }

        public MyEventList getPastEventList() {
            return pastEventList;
        }

        public void setMyEventList(MyEventList myEventList) {
            mMyEventList = myEventList;
        }

    }

    public class CreatedByMe {
        @SerializedName("propic")
        private String mPropic;
        @SerializedName("agenda")
        private Object mAgenda;
        @SerializedName("category")
        private String mCategory;
        @SerializedName("created_at")
        private String mCreatedAt;
        @SerializedName("created_by_name")
        private String mCreatedByName;
        @SerializedName("description")
        private String mDescription;
        @SerializedName("event_host")
        private String mEventHost;
        @SerializedName("event_image")
        private String mEventImage;
        @SerializedName("event_name")
        private String mEventName;
        @SerializedName("event_page")
        private String mEventPage;
        @SerializedName("event_type")
        private String mEventType;
        @SerializedName("from_date")
        private Long mFromDate;
        @SerializedName("id")
        private Long mId;
        @SerializedName("is_active")
        private Boolean mIsActive;
        @SerializedName("is_agenda")
        private Boolean mIsAgenda;
        @SerializedName("is_album")
        private Boolean mIsAlbum;
        @SerializedName("is_document")
        private Boolean mIsDocument;
        @SerializedName("is_item")
        private Boolean mIsItem;
        @SerializedName("is_public")
        private Boolean mIsPublic;
        @SerializedName("is_sharable")
        private Boolean mIsSharable;
        @SerializedName("location")
        private String mLocation;
        @SerializedName("rsvp")
        private Boolean mRsvp;
        @SerializedName("ticket_type")
        private String mTicketType;
        @SerializedName("to_date")
        private Long mToDate;
        @SerializedName("updatedAt")
        private String mUpdatedAt;

        public Object getAgenda() {
            return mAgenda;
        }

        public void setAgenda(Object agenda) {
            mAgenda = agenda;
        }

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

        public String getCreatedBy() {
            return mCreatedByName;
        }

        public void setCreatedBy(String mCreatedByName) {
            this.mCreatedByName = mCreatedByName;
        }

        public String getDescription() {
            return mDescription;
        }

        public void setDescription(String description) {
            mDescription = description;
        }

        public String getEventHost() {
            return mEventHost;
        }

        public void setEventHost(String eventHost) {
            mEventHost = eventHost;
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

        public String getEventPage() {
            return mEventPage;
        }

        public void setEventPage(String eventPage) {
            mEventPage = eventPage;
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

        public Boolean getIsActive() {
            return mIsActive;
        }

        public void setIsActive(Boolean isActive) {
            mIsActive = isActive;
        }

        public Boolean getIsAgenda() {
            return mIsAgenda;
        }

        public void setIsAgenda(Boolean isAgenda) {
            mIsAgenda = isAgenda;
        }

        public Boolean getIsAlbum() {
            return mIsAlbum;
        }

        public void setIsAlbum(Boolean isAlbum) {
            mIsAlbum = isAlbum;
        }

        public Boolean getIsDocument() {
            return mIsDocument;
        }

        public void setIsDocument(Boolean isDocument) {
            mIsDocument = isDocument;
        }

        public Boolean getIsItem() {
            return mIsItem;
        }

        public void setIsItem(Boolean isItem) {
            mIsItem = isItem;
        }

        public Boolean getIsPublic() {
            return mIsPublic;
        }

        public void setIsPublic(Boolean isPublic) {
            mIsPublic = isPublic;
        }

        public Boolean getIsSharable() {
            return mIsSharable;
        }

        public void setIsSharable(Boolean isSharable) {
            mIsSharable = isSharable;
        }

        public String getLocation() {
            return mLocation;
        }

        public void setLocation(String location) {
            mLocation = location;
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

        public String getUpdatedAt() {
            return mUpdatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            mUpdatedAt = updatedAt;
        }

        public String getmPropic() {
            return mPropic;
        }

        public void setmPropic(String mPropic) {
            this.mPropic = mPropic;
        }
    }

    public class MyEventList {

        @SerializedName("created_by_me")
        private List<Event> mCreatedByMe;
        @SerializedName("rsvp_maybe")
        private List<Event> mRsvpMaybe;
        @SerializedName("rsvp_yes")
        private List<Event> mRsvpYes;
        @SerializedName("data")
        private List<Event> data;
        private int size = 0;

        public List<Event> getCreatedByMe() {
            return mCreatedByMe;
        }

        public void setCreatedByMe(List<Event> createdByMe) {
            mCreatedByMe = createdByMe;
        }

        public List<Event> getRsvpMaybe() {
            return mRsvpMaybe;
        }

        public void setRsvpMaybe(List<Event> rsvpMaybe) {
            mRsvpMaybe = rsvpMaybe;
        }

        public List<Event> getRsvpYes() {
            return mRsvpYes;
        }

        public List<Event> getData() {
            return data;
        }

        public void setRsvpYes(List<Event> rsvpYes) {
            mRsvpYes = rsvpYes;
        }


        public int getCount() {
            if (mCreatedByMe != null && mCreatedByMe.size() > 0) {
                size = 1;
            }

            if (mRsvpMaybe != null && mRsvpMaybe.size() > 0) {
                size = size + 1;
            }

            if (mRsvpYes != null && mRsvpYes.size() > 0) {
                size = size + 1;
            }

            return size;
        }

    }
}
