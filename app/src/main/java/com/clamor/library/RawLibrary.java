package com.clamor.library;

import com.google.gson.annotations.SerializedName;

public class RawLibrary {
    @SerializedName("@id")
    private String id;
    private String title;
    private Location location;

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Location getLocation() {
        return location;
    }
}
