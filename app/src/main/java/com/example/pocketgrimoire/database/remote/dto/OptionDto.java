package com.example.pocketgrimoire.database.remote.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Represents a single option node in starting_equipment_options.
 *  - option_type == "counted_reference": use 'count' and 'of' (an ApiRef)
 *  - option_type == "multiple": 'items' is a list of OptionDto to take together
 *  - option_type == "choice": 'choice' describes a nested choose-from set
 */
public class OptionDto {
    @SerializedName("option_type")
    public String optionType; // "counted_reference" | "multiple" | "choice"

    // counted_reference
    public Integer count; // how many of 'of'
    public ApiRef of; // the equipment ref

    // multiple
    public List<OptionDto> items; // a bundle of options taken together

    // choice
    public ChoiceDto choice; // choose N from list of items

    // Some entries include gating like proficiency prerequisites; we ignore them
    // still required to add this field or deserialization into Json string will fail
    public List<PrerequisiteDto> prerequisites;
}
