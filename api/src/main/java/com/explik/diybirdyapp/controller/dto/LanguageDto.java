package com.explik.diybirdyapp.controller.dto;

public class LanguageDto {
    private String abbreviation;
    private String name;

    public LanguageDto() {}

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
