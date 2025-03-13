package com.explik.diybirdyapp.controller.dto.exercise;

import jakarta.validation.constraints.NotNull;

import java.util.Dictionary;

public class ExerciseDto {
    @NotNull
    private String id;

    @NotNull
    private String type;

    private ExerciseContentDto content;

    private ExerciseInputDto input;

    private ExerciseFeedbackDto feedback;

    private Dictionary<String, Object> properties;

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public String getType() { return type; }

    public void setType(String type) { this.type = type; }

    public ExerciseContentDto getContent() { return content; }

    public void setContent(ExerciseContentDto content) { this.content = content; }

    public ExerciseInputDto getInput() { return input; }

    public void setInput(ExerciseInputDto input) { this.input = input; }

    public ExerciseFeedbackDto getFeedback() { return feedback; }

    public void setFeedback(ExerciseFeedbackDto feedback) { this.feedback = feedback; }

    public Dictionary<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Dictionary<String, Object> properties) {
        this.properties = properties;
    }
}
