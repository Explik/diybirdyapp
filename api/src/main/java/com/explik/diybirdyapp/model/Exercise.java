package com.explik.diybirdyapp.model;

public abstract class Exercise<T extends ExerciseAnswer> {
    public String id;
    public T exerciseAnswer;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public T getExerciseAnswer() {
        return this.exerciseAnswer;
    }

    public void setExerciseAnswer(T exerciseAnswer) {
        this.exerciseAnswer = exerciseAnswer;
    }

    public abstract String getExerciseType();
}