package com.explik.diybirdyapp.model;

public class ExerciseSessionModel {
    private String id;
    private String type;
    private String flashcardDeckId;
    private ExerciseModel exercise;
    private ExerciseSessionProgressModel progress;

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

    public String getFlashcardDeckId() {
        return flashcardDeckId;
    }

    public void setFlashcardDeckId(String flashcardDeckId) {
        this.flashcardDeckId = flashcardDeckId;
    }

    public ExerciseModel getExercise() {
        return exercise;
    }

    public void setExercise(ExerciseModel exercise) {
        this.exercise = exercise;
    }

    public ExerciseSessionProgressModel getProgress() {
        return progress;
    }

    public void setProgress(ExerciseSessionProgressModel progress) {
        this.progress = progress;
    }
}
