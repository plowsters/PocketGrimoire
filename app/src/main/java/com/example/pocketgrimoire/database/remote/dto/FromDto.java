package com.example.pocketgrimoire.database.remote.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class FromDto {
    @SerializedName("option_set_type")
    public String optionSetType; // "options_array" | "equipment_category" | ...

    // For option_set_type == "options_array"
    public List<OptionDto> options;

    // For option_set_type == "equipment_category"
    @SerializedName("equipment_category")
    public ApiRef equipmentCategory;
}
