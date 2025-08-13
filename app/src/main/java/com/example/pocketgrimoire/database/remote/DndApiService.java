package com.example.pocketgrimoire.database.remote;

import com.example.pocketgrimoire.database.remote.dto.ClassRequestDto;
import com.example.pocketgrimoire.database.remote.dto.ResourceListDto;
import com.example.pocketgrimoire.database.remote.dto.EquipmentCategoryRequestDto;
import com.example.pocketgrimoire.database.remote.dto.SpellRequestDto;

import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Retrofit interface for the D&D 5e API endpoints used by Pocket Grimoire.
 */
public interface DndApiService {
    // Items
    @GET("api/2014/equipment")
    Single<ResourceListDto> listEquipment();

    @GET("api/2014/equipment-categories")
    Single<ResourceListDto> listEquipmentCategories();

    @GET("api/2014/equipment-categories/{index}")
    Single<EquipmentCategoryRequestDto> getEquipmentCategory(@Path("index") String index);

    // Spells
    @GET("api/2014/spells")
    Single<ResourceListDto> listSpells();

    @GET("api/2014/spells")
    Single<ResourceListDto> listSpellsBySchool(@Query("school") String school); // NEW

    @GET("api/2014/spells/{index}")
    Single<SpellRequestDto> getSpell(@Path("index") String index);

    // Abilities
    @GET("api/2014/features")
    Single<ResourceListDto> listFeatures();

    @GET("api/2014/traits")
    Single<ResourceListDto> listTraits();

    @GET("api/2014/classes")
    Single<ResourceListDto> listClasses();

    @GET("api/2014/races")
    Single<ResourceListDto> listRaces();

    // To get features for a specific class
    @GET("api/2014/classes/{index}/features")
    Single<ResourceListDto> listClassFeatures(@Path("index") String index);

    // To get traits for a specific race
    @GET("api/2014/races/{index}/traits")
    Single<ResourceListDto> listRaceTraits(@Path("index") String index);

    // Classes
    @GET("api/2014/classes/{cls}")
    Single<ClassRequestDto> getClassDetail(@Path("cls") String classIndex);

    // Class spells
    @GET("api/2014/classes/{cls}/levels/{lvl}/spells")
    Single<ResourceListDto> listClassSpellsAtOrBelow(@Path("cls") String classIndex, @Path("lvl") int level);

    @GET("api/2014/classes/{cls}/spells")
    Single<ResourceListDto> listClassSpells(@Path("cls") String classIndex); // OPTIONAL

    // Proficiencies
    @GET("api/2014/classes/{cls}/proficiencies")
    Single<ResourceListDto> listClassProficiencies(@Path("cls") String classIndex);
}