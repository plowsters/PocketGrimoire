package com.example.pocketgrimoire.database.remote.dto;

public class ChoiceDto {
    public String desc;
    public Integer choose; // how many to pick
    public String type; // "equipment"
    public FromDto from; // usually an equipment_category
}
