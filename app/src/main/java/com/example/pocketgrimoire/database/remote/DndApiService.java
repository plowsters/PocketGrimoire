package com.example.pocketgrimoire.database.remote;

import com.example.pocketgrimoire.database.remote.dto.ClassRequestDto;
import com.example.pocketgrimoire.database.remote.dto.ResourceRefDto;
import com.example.pocketgrimoire.database.remote.dto.ResourceListDto;
import com.example.pocketgrimoire.database.remote.dto.EquipmentCategoryRequestDto;
import com.example.pocketgrimoire.database.remote.dto.SpellRequestDto;

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

    @GET("api/2014/spells")
    Single<ResourceListDto> listSpells();

    @GET("api/2014/spells/{index}")
    Single<SpellRequestDto> getSpell(@Path("index") String index);

    @GET("api/2014/features")
    Single<ResourceListDto> listFeatures();

    @GET("api/2014/traits")
    Single<ResourceListDto> listTraits();

    @GET("api/2014/classes/{cls}/features")
    Single<ResourceListDto> listClassFeatures(@Path("cls") String classIndex);

    @GET("api/2014/races/{race}/traits")
    Single<ResourceListDto> listRaceTraits(@Path("race") String raceIndex);

    @GET("api/2014/classes/{cls}")
    Single<ClassRequestDto> getClassDetail(@Path("cls") String classIndex);

    @GET("api/2014/classes/{cls}/levels/{lvl}/spells")
    Single<ResourceListDto> listClassSpellsAtOrBelow(@Path("cls") String classIndex, @Path("lvl") int level);

    @GET("api/2014/classes/{cls}/proficiencies")
    Single<ResourceListDto> listClassProficiencies(@Path("cls") String classIndex);
}
