package com.clamor.library;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

public class DatosMadridResponse {
    @SerializedName("@context")
    private Map<String, String> context;

    @SerializedName("@graph")
    private List<RawLibrary> graph;

    public List<RawLibrary> getRawLibraries() {
        return graph;
    }
}
