package com.explik.diybirdyapp.controller.dto.content;

import jakarta.validation.constraints.NotNull;

public class FlashcardDeckDto {
    @NotNull
    private String id;

    @NotNull
    private String name;

    private String description;

    public FlashcardDeckDto() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
