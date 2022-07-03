
package com.familheey.app.Models.Response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

@SuppressWarnings("unused")
public class ExploreEvents {

    @SerializedName("data")
    private Data mData;

    public Data getData() {
        return mData;
    }

    public void setData(Data data) {
        mData = data;
    }


    public class Explore {

        @SerializedName("basedOnLocation")
        private List<Event> mBasedOnLocation;
        @SerializedName("publicEvents")
        private List<Event> mPublicEvents;
        @SerializedName("sharedEvents")
        private List<Event> mEvents;

        public List<Event> getBasedOnLocation() {
            return mBasedOnLocation;
        }

        public void setBasedOnLocation(List<Event> basedOnLocation) {
            mBasedOnLocation = basedOnLocation;
        }

        public List<Event> getPublicEvents() {
            return mPublicEvents;
        }

        public void setPublicEvents(List<Event> publicEvents) {
            mPublicEvents = publicEvents;
        }

        public List<Event> getSharedEvents() {
            return mEvents;
        }

        public void setSharedEvents(List<Event> events) {
            mEvents = events;
        }

    }

    public class Data {

        @SerializedName("explore")
        private Explore mExplore;

        public Explore getExplore() {
            return mExplore;
        }

        public void setExplore(Explore explore) {
            mExplore = explore;
        }

    }
}
