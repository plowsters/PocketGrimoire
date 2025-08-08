package com.example.pocketgrimoire.database.remote.dto;

import java.util.List;

public class SpellRequestDto {
    public String index;
    public String name;
    public Integer level; // nullable in theory; API usually provides it
    public ApiRef school; // { index, name, url }
    public List<ApiRef> classes; // [{ index, name, url }]
}
