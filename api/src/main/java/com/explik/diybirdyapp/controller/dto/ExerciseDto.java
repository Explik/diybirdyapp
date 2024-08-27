package com.explik.diybirdyapp.controller.dto;

import java.util.Dictionary;

public class ExerciseDto {
    private String id;
    private String type;
    private ExerciseContentDto content;
    private ExerciseInputDto input;
    private Dictionary<String, Object> properties;

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public String getType() { return type; }

    public void setType(String type) { this.type = type; }

    public ExerciseContentDto getContent() { return content; }

    public void setContent(ExerciseContentDto content) { this.content = content; }

    public ExerciseInputDto getInput() { return input; }

    public void setInput(ExerciseInputDto input) { this.input = input; }

    public Dictionary<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Dictionary<String, Object> properties) {
        this.properties = properties;
    }
}
