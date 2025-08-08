package com.example.pocketgrimoire.database.remote;

import com.example.pocketgrimoire.database.remote.dto.ResourceRefDto;
import com.example.pocketgrimoire.database.remote.dto.ResourceListDto;
import com.example.pocketgrimoire.database.remote.dto.EquipmentCategoryRequestDto;

import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface DndApiService {
    // Items: list everything
    @GET("api/2014/equipment")
    Single<ResourceListDto> listEquipment();

    // Items: enumerate categories to recover category per item
    @GET("api/2014/equipment-categories")
    Single<ResourceListDto> listEquipmentCategories();

    @GET("api/2014/equipment-categories/{index}")
    Single<EquipmentCategoryRequestDto> getEquipmentCategory(@Path("index") String index);
}
