package com.example.pocketgrimoire.database.remote.dto;

import com.google.gson.annotations.SerializedName;

public class PrerequisiteDto {
    public String type; // e.g., "proficiency"

    // when type == "proficiency"
    public ApiRef proficiency;

    // sometimes ability score gates appear on other endpoints
    @SerializedName("ability_score")
    public ApiRef abilityScore;
}
