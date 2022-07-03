package com.familheey.app.Need;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Item implements Parcelable {
    private String request_item_title;

    private String type;
    private String request_item_description;
    private Integer item_quantity;

    public void setSum(String sum) {
        this.sum = sum;
    }

    //New Code
    @SerializedName("item_id")
    @Expose
    private Integer itemId;
    @SerializedName("sum")
    @Expose
    private String sum;
    @SerializedName("total_contribution")
    @Expose
    private Integer totalContribution;
    @SerializedName("contribute_item_quantity")
    @Expose
    private Integer myContribution;
    @SerializedName("contribute_id")
    @Expose
    private Integer contributionId;


    @SerializedName("pledged_amount")
    @Expose
    private Double pledged_amount;


    @SerializedName("received_amount")
    @Expose
    private Double received_amount;


    public Item() {
    }


    protected Item(Parcel in) {
        request_item_title = in.readString();
        request_item_description = in.readString();
        if (in.readByte() == 0) {
            item_quantity = null;
        } else {
            item_quantity = in.readInt();
        }
        if (in.readByte() == 0) {
            itemId = null;
        } else {
            itemId = in.readInt();
        }
        if (in.readByte() == 0) {
            totalContribution = null;
        } else {
            totalContribution = in.readInt();
        }
        if (in.readByte() == 0) {
            myContribution = null;
        } else {
            myContribution = in.readInt();
        }
        if (in.readByte() == 0) {
            contributionId = null;
        } else {
            contributionId = in.readInt();
        }

        if (in.readByte() == 0) {
            pledged_amount = null;
        } else {
            pledged_amount = in.readDouble();
        }

        if (in.readByte() == 0) {
            received_amount = null;
        } else {
            received_amount = in.readDouble();
        }

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(request_item_title);
        dest.writeString(request_item_description);
        if (item_quantity == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(item_quantity);
        }
        if (itemId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(itemId);
        }
        if (totalContribution == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(totalContribution);
        }
        if (myContribution == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(myContribution);
        }
        if (contributionId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(contributionId);
        }


        if (pledged_amount == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(pledged_amount);
        }


        if (received_amount == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(received_amount);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Item> CREATOR = new Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };

    public String getRequest_item_title() {
        return request_item_title;
    }

    public void setRequest_item_title(String request_item_title) {
        this.request_item_title = request_item_title;
    }

    public String getRequest_item_description() {
        return request_item_description;
    }

    public void setRequest_item_description(String request_item_description) {
        this.request_item_description = request_item_description;
    }

    public int getItem_quantity() {
        if(item_quantity == null)
            return 0;
        return item_quantity;
    }

    public void setItem_quantity(int item_quantity) {
        this.item_quantity = item_quantity;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }
//New Code

    public Integer getItemId() {
        return itemId;
    }

    public int getTotalContribution() {
        if(totalContribution == null)
            return 0;
        return totalContribution;
    }

    public void setTotalContribution(int totalContribution) {
        this.totalContribution = totalContribution;
    }

    public int getMyContribution() {
        if(myContribution == null)
            return 0;
        return myContribution;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setMyContribution(int myContribution) {
        this.myContribution = myContribution;
    }

    public Integer getContributionId() {
        return contributionId;
    }

    public void setContributionId(int contributionId) {
        this.contributionId = contributionId;
    }

    public boolean isContributionCompleted(){
        return (Math.round((getItem_quantity() - getReceived_amount())) <= 0);
    }

    public String getSum() {
        return sum;
    }

    public Double getPledged_amount() {
        return pledged_amount;
    }

    public Double getReceived_amount() {
        if (received_amount == null)
            return 0.00;
        return received_amount;
    }
}
