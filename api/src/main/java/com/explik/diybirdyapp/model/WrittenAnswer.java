package com.explik.diybirdyapp.model;

public class WrittenAnswer extends ExerciseAnswer {
    private String text;

    @Override
    public String getAnswerType() {
        return "written-exercise-answer";
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
