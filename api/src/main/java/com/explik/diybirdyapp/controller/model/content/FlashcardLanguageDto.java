package com.explik.diybirdyapp.controller.model.content;

import jakarta.validation.constraints.NotNull;

public class FlashcardLanguageDto {
    @NotNull(message = "id.required")
    private String id;

    @NotNull(message = "name.required")
    private String name;

    private String isoCode;

    public FlashcardLanguageDto() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIsoCode() {
        return isoCode;
    }

    public void setIsoCode(String isoCode) {
        this.isoCode = isoCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
