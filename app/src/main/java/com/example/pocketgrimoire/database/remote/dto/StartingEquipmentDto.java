package com.example.pocketgrimoire.database.remote.dto;

public class StartingEquipmentDto {
    public ApiRef equipment; // { index, name, url }
    public Integer quantity; // # of this item owned

    public StartingEquipmentDto() {
        // Default no-argument constructor for Gson
    }

    public StartingEquipmentDto(ApiRef equipment, Integer quantity) {
        this.equipment = equipment;
        this.quantity = quantity;
    }
}
