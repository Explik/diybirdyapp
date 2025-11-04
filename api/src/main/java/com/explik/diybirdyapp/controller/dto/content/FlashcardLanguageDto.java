package com.explik.diybirdyapp.controller.dto.content;

import jakarta.validation.constraints.NotNull;

public class FlashcardLanguageDto {
    @NotNull(message = "id.required")
    private String id;

    @NotNull(message = "abbreviation.required")
    private String abbreviation;

    @NotNull(message = "name.required")
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
