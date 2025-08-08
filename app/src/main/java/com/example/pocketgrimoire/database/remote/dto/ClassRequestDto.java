package com.example.pocketgrimoire.database.remote.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ClassRequestDto {
    public String index;
    public String name;

    @SerializedName("hit_die")
    public Integer hitDie; // starting hit dice for the class

    @SerializedName("starting_equipment")
    public List<StartingEquipmentDto> startingEquipment;

    @SerializedName("starting_equipment_options")
    public List<StartingEquipmentOptionDto> startingEquipmentOptions;
}
