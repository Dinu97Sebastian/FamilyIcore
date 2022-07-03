package com.familheey.app.Models.Response;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

@SuppressWarnings("unused")
public class PeopleShareResponse {

    @SerializedName("data")
    private ArrayList<Datum> mData = new ArrayList<>();

    public ArrayList<Datum> getData() {
        return mData;
    }

    public void setData(ArrayList<Datum> data) {
        mData = data;
    }

    public static class Datum {

        boolean devSet = false;
        boolean invited = false;
        @SerializedName("full_name")
        private String mFullName;
        @SerializedName("location")
        private String mLocation;
        @SerializedName("propic")
        private String mPropic;
        @SerializedName("user_id")
        private Long mUserId;
        @SerializedName("invitation_status")
        private Boolean invitationStatus;

        public boolean isInvited() {
            return invited;
        }

        public void setInvited(boolean invited) {
            this.invited = invited;
        }

        public boolean isDevSet() {
            return devSet;
        }

        public void setDevSet(boolean devSet) {
            this.devSet = devSet;
        }

        public String getFullName() {
            return mFullName;
        }

        public void setFullName(String fullName) {
            mFullName = fullName;
        }

        public String getLocation() {
            return mLocation;
        }

        public void setLocation(String location) {
            mLocation = location;
        }

        public String getPropic() {
            return mPropic;
        }

        public void setPropic(String propic) {
            mPropic = propic;
        }

        public Long getUserId() {
            return mUserId;
        }

        public void setUserId(Long userId) {
            mUserId = userId;
        }

        public Boolean getInvitationStatus() {
            return invitationStatus;
        }
    }
}
