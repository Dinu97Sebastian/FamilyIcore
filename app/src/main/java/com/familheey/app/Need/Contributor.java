package com.familheey.app.Need;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public class Contributor {

    @SerializedName("paid_user_name")
    @Expose
    private String paid_user_name;

    @SerializedName("membership_customer_notes")
    @Expose
    private String membership_customer_notes;

    @SerializedName("is_anonymous")
    @Expose
    private boolean is_anonymous;

    @SerializedName("is_pending_thank_post")
    @Expose
    private boolean is_pending_thank_post;
    @SerializedName("skip_thank_post")
    @Expose
    private boolean skip_thank_post;
    @SerializedName("is_thank_post")
    @Expose
    private boolean is_thank_post;

    @SerializedName("is_acknowledge")
    @Expose
    private boolean is_acknowledge;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("contribute_user_id")
    @Expose
    private Integer contributeUserId;
    @SerializedName("full_name")
    @Expose
    private String fullName;
    @SerializedName("payment_status")
    @Expose
    private String payment_status;
    @SerializedName("propic")
    @Expose
    private String propic;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("location")
    @Expose
    private String location;
    @SerializedName("post_request_item_id")
    @Expose
    private Integer postRequestItemId;
    @SerializedName("contribute_item_quantity")
    @Expose
    private Double contributeItemQuantity;

    /*
        @SerializedName("contribute_item_quantity")
        @Expose
        private Integer contribute_item_quantity;*/
    @SerializedName("created_at")
    @Expose
    private String createdAt;

    @SerializedName("total_received")
    @Expose
    private Integer totalReceived;


    @SerializedName("total_contribution")
    @Expose
    private Integer total_contribution;


    @SerializedName("contribution_count")
    @Expose
    private Integer contribution_count;

    @SerializedName("total_needed")
    @Expose
    private Integer totalNeeded;
    @SerializedName("request_item_title")
    @Expose
    private String requestItemTitle;
    @SerializedName("requested_by")
    @Expose
    private Integer createdBy;

    public Contributor() {

    }

    public String getPaid_user_name() {
        return paid_user_name;
    }

    public String getMembership_customer_notes() {
        return membership_customer_notes;
    }

    public String isMembership_customer_notes() {
        return membership_customer_notes;
    }

    public Integer getId() {
        return id;
    }

    public Integer getContributeUserId() {
        return contributeUserId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPropic() {
        return propic;
    }

    public String getPhone() {
        return phone;
    }

    public String getLocation() {
        return location;
    }

    public Integer getPostRequestItemId() {
        return postRequestItemId;
    }

    public Double getContributeItemQuantity() {
        return contributeItemQuantity;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public Integer getTotalReceived() {
        return totalReceived;
    }

    public Integer getTotalNeeded() {
        return totalNeeded;
    }

    public String getRequestItemTitle() {
        return requestItemTitle;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTotal_contribution() {
        return total_contribution;
    }

    public Integer getContribution_count() {
        return contribution_count;
    }

    public void setContribution_count(Integer contribution_count) {
        this.contribution_count = contribution_count;
    }

    public boolean isIs_thank_post() {
        return is_thank_post;
    }

    public void setIs_thank_post(boolean is_thank_post) {
        this.is_thank_post = is_thank_post;
    }

    public boolean isIs_acknowledge() {
        return is_acknowledge;
    }

    public void setIs_acknowledge(boolean is_acknowledge) {
        this.is_acknowledge = is_acknowledge;
    }

    public boolean isSkip_thank_post() {
        return skip_thank_post;
    }

    public void setSkip_thank_post(boolean skip_thank_post) {
        this.skip_thank_post = skip_thank_post;
    }

    public boolean isIs_pending_thank_post() {
        return is_pending_thank_post;
    }

    public void setIs_pending_thank_post(boolean is_pending_thank_post) {
        this.is_pending_thank_post = is_pending_thank_post;
    }

    public boolean isIs_anonymous() {
        return is_anonymous;
    }

    public void setIs_anonymous(boolean is_anonymous) {
        this.is_anonymous = is_anonymous;
    }

    public String getPayment_status() {
        return payment_status;
    }

    public String getFormattedCreatedAt() {
        if (getCreatedAt() != null && getCreatedAt().length() > 0) {
            DateTime dateTime = ISODateTimeFormat.dateTimeParser().parseDateTime(getCreatedAt());
            DateTimeFormatter dtfOut = DateTimeFormat.forPattern("MMM dd yyyy hh:mm aa");
            return dtfOut.print(dateTime);
        }
        return null;
    }
}
