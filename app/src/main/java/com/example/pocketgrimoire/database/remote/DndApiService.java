package com.example.pocketgrimoire.database.remote;

import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface DndApiService {
    @GET("api/2014/equipment")
    Single<ResourceListDto> listEquipment();

    @GET("api/2014/equipment/{index}")
    Single<ItemRequestDto> getEquipment(@Path("index") String index);
}
