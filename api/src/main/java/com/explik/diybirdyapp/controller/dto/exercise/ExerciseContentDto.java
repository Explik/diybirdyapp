package com.explik.diybirdyapp.controller.dto.exercise;

public abstract class ExerciseContentDto {
    public ExerciseContentDto(String type) {
        this.type = type;
    }

    private String id;

    private String type;

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public String getType() { return type; }

    public void setType(String type) { this.type = type; }
}
