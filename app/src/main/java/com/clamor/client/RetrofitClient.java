package com.clamor.client;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static RetrofitClient retrofit;
    private final DatosMadridService datosMadridService;

    private RetrofitClient() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(DatosMadridService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        datosMadridService = retrofit.create(DatosMadridService.class);
    }

    public static synchronized RetrofitClient getInstance() {
        if (retrofit == null) {
            retrofit = new RetrofitClient();
        }
        return retrofit;
    }

    public DatosMadridService getDatosMadridService() {
        return datosMadridService;
    }
}
