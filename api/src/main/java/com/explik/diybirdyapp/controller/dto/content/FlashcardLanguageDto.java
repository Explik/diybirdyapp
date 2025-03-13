package com.explik.diybirdyapp.controller.dto.content;

import jakarta.validation.constraints.NotNull;

public class FlashcardLanguageDto {
    @NotNull
    private String id;

    @NotNull
    private String abbreviation;

    @NotNull
    private String name;

    public FlashcardLanguageDto() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
