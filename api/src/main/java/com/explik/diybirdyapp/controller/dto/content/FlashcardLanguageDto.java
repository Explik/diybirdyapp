package com.explik.diybirdyapp.controller.dto.content;

public class FlashcardLanguageDto {
    private String id;
    private String abbreviation;
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
