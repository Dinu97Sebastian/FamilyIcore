package com.familheey.app.Models.Response;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public class SignUpContributor implements Parcelable {

    @SerializedName("full_name")
    @Expose
    public String fullName;
    @SerializedName("propic")
    @Expose
    public String propic;
    @SerializedName("contr_id")
    @Expose
    public Integer contrId;
    @SerializedName("contribute_user_id")
    @Expose
    public Integer contributeUserId;
    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("event_id")
    @Expose
    public Integer eventId;
    @SerializedName("user_id")
    @Expose
    public Integer userId;
    @SerializedName("slot_title")
    @Expose
    public String slotTitle;
    @SerializedName("item_quantity")
    @Expose
    public Integer itemQuantity;
    @SerializedName("slot_description")
    @Expose
    public String slotDescription;
    @SerializedName("start_date")
    @Expose
    public String startDate;
    @SerializedName("end_date")
    @Expose
    public String endDate;
    @SerializedName("is_active")
    @Expose
    public Boolean isActive;
    @SerializedName("created_at")
    @Expose
    public String createdAt;
    @SerializedName("updated_at")
    @Expose
    public String updatedAt;
    @SerializedName("quantity_collected")
    @Expose
    public Integer quantityCollected;
    @SerializedName("needed")
    @Expose
    public String needed;
    @SerializedName("updated_on")
    String updatedOn;

    protected SignUpContributor(Parcel in) {
        fullName = in.readString();
        propic = in.readString();
        if (in.readByte() == 0) {
            contrId = null;
        } else {
            contrId = in.readInt();
        }
        if (in.readByte() == 0) {
            contributeUserId = null;
        } else {
            contributeUserId = in.readInt();
        }
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        if (in.readByte() == 0) {
            eventId = null;
        } else {
            eventId = in.readInt();
        }
        if (in.readByte() == 0) {
            userId = null;
        } else {
            userId = in.readInt();
        }
        slotTitle = in.readString();
        if (in.readByte() == 0) {
            itemQuantity = null;
        } else {
            itemQuantity = in.readInt();
        }
        slotDescription = in.readString();
        startDate = in.readString();
        endDate = in.readString();
        byte tmpIsActive = in.readByte();
        isActive = tmpIsActive == 0 ? null : tmpIsActive == 1;
        createdAt = in.readString();
        updatedAt = in.readString();
        if (in.readByte() == 0) {
            quantityCollected = null;
        } else {
            quantityCollected = in.readInt();
        }
        needed = in.readString();
        updatedOn = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(fullName);
        dest.writeString(propic);
        if (contrId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(contrId);
        }
        if (contributeUserId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(contributeUserId);
        }
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(id);
        }
        if (eventId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(eventId);
        }
        if (userId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(userId);
        }
        dest.writeString(slotTitle);
        if (itemQuantity == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(itemQuantity);
        }
        dest.writeString(slotDescription);
        dest.writeString(startDate);
        dest.writeString(endDate);
        dest.writeByte((byte) (isActive == null ? 0 : isActive ? 1 : 2));
        dest.writeString(createdAt);
        dest.writeString(updatedAt);
        if (quantityCollected == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(quantityCollected);
        }
        dest.writeString(needed);
        dest.writeString(updatedOn);
    }

    public static final Creator<SignUpContributor> CREATOR = new Creator<SignUpContributor>() {
        @Override
        public SignUpContributor createFromParcel(Parcel in) {
            return new SignUpContributor(in);
        }

        @Override
        public SignUpContributor[] newArray(int size) {
            return new SignUpContributor[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPropic() {
        return propic;
    }

    public Integer getContrId() {
        return contrId;
    }

    public Integer getContributeUserId() {
        return contributeUserId;
    }

    public Integer getId() {
        return id;
    }

    public Integer getEventId() {
        return eventId;
    }

    public Integer getUserId() {
        return userId;
    }

    public String getSlotTitle() {
        return slotTitle;
    }

    public Integer getItemQuantity() {
        return itemQuantity;
    }

    public String getSlotDescription() {
        return slotDescription;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public Boolean getActive() {
        return isActive;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public Integer getQuantityCollected() {
        return quantityCollected;
    }

    public String getNeeded() {
        return needed;
    }

    public String getUpdatedOn() {
        return updatedOn;
    }

    public String getFormateedRepliedDate() {
        try {
            DateTime dateTime = ISODateTimeFormat.dateTimeParser().parseDateTime(getUpdatedOn());
            DateTimeFormatter dtfOut = DateTimeFormat.forPattern("MMM dd yyyy hh:mm aa");
            return dtfOut.print(dateTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
