package com.explik.diybirdyapp.model;

public class ExerciseAnswerModel {
    private String id;
    private String exerciseId;
    private String type;
    private String rating;
    private String text;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(String exerciseId) {
        this.exerciseId = exerciseId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getText() { return text; }

    public void setText(String textInput) { this.text = textInput; }
}
