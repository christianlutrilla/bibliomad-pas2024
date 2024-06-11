package com.clamor.client;

import com.clamor.library.DatosMadridResponse;
import com.clamor.library.LibraryDetailsResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;


public interface DatosMadridService {

    String BASE_URL = "https://datos.madrid.es/";

    @GET("egob/catalogo/201747-0-bibliobuses-bibliotecas.json")
    Call<DatosMadridResponse> getRawLibraries(@Query("distrito_nombre") String district);

    @GET
    Call<LibraryDetailsResponse> getLibraryDetails(@Url String url);
}
