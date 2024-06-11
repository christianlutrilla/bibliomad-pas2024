package com.clamor.library;

import com.google.gson.annotations.SerializedName;

public class Address {
    @SerializedName("@id")
    private String id;
    private String locality;
    @SerializedName("postal-code")
    private String postalCode;
    @SerializedName("street-address")
    private String streetAddress;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }
}
