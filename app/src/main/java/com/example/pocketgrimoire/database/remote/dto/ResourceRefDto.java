package com.example.pocketgrimoire.database.remote.dto;

public class ResourceRefDto {
    public String index;
    public String name;
    public String url;

    // Some lists (e.g., /spells) include level; others won't — keep nullable
    public Integer level;
}
