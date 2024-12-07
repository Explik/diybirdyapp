package com.explik.diybirdyapp.model;

public class ExerciseAnswerModel {
    private String id;
    private String type;
    private String rating;
    private String textInput;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getTextInput() { return textInput; }

    public void setTextInput(String textInput) { this.textInput = textInput; }
}
