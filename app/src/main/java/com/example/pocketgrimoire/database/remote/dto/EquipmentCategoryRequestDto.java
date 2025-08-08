package com.example.pocketgrimoire.database.remote.dto;

import java.util.List;

public class EquipmentCategoryRequestDto {
    public String index;
    public String name;
    public List<ApiRef> equipment; // equipment in this category

    public String url;

}
