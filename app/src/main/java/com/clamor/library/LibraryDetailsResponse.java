package com.clamor.library;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

public class LibraryDetailsResponse {
    @SerializedName("@context")
    private Map<String, String> context;

    @SerializedName("@graph")
    private List<LibraryDetail> graph;

    public List<LibraryDetail> getDetailedLibrary() {
        return graph;
    }
}
