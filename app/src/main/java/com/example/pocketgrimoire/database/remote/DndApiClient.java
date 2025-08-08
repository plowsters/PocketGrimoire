package com.example.pocketgrimoire.database.remote;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public final class DndApiClient {
    private static final String BASE_URL = "https://www.dnd5eapi.co/";
    private static DndApiService service;

    public static DndApiService get() {
        if (service == null) {
            OkHttpClient ok = new OkHttpClient.Builder().build();
            Retrofit r = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(ok)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                    .build();
            service = r.create(DndApiService.class);
        }
        return service;
    }

    private DndApiClient() {}
}