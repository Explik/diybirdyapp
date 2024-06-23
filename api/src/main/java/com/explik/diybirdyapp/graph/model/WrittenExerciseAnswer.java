package com.explik.diybirdyapp.graph.model;

public class WrittenExerciseAnswer extends ExerciseAnswer {
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
