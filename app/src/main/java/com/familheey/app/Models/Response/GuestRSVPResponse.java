
package com.familheey.app.Models.Response;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class GuestRSVPResponse {


    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("full_name")
    @Expose
    private String fullName;
    @SerializedName("propic")
    @Expose
    private String propic;
    @SerializedName("others_count")
    @Expose
    private Integer othersCount;
    @SerializedName("location")
    @Expose
    private String location;
    @SerializedName("total_family")
    @Expose
    private String totalFamily;
    @SerializedName("mutual_contact")
    @Expose
    private String mutualContact;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPropic() {
        return propic;
    }

    public void setPropic(String propic) {
        this.propic = propic;
    }

    public Integer getOthersCount() {
        if (othersCount == null)
            return 0;
        return othersCount;
    }

    public void setOthersCount(Integer othersCount) {
        this.othersCount = othersCount;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTotalFamily() {
        if (totalFamily != null)
            return totalFamily;
        return "0";
    }

    public void setTotalFamily(String totalFamily) {
        this.totalFamily = totalFamily;
    }

    public String getMutualContact() {
        if (mutualContact != null)
            return mutualContact;
        return "0";
    }

    public void setMutualContact(String mutualContact) {
        this.mutualContact = mutualContact;
    }


}
