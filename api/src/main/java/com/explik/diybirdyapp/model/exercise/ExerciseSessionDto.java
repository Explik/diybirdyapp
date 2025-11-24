package com.explik.diybirdyapp.model.exercise;

import jakarta.validation.constraints.NotNull;

public class ExerciseSessionDto {
    private String id;

    @NotNull(message = "type.required")
    private String type;

    private boolean completed;

    private String flashcardDeckId;

    private ExerciseDto exercise;

    private ExerciseSessionProgressDto progress;

    private ExerciseSessionOptionsDto options;

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

    public boolean getCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getFlashcardDeckId() {
        return flashcardDeckId;
    }

    public void setFlashcardDeckId(String flashcardDeckId) {
        this.flashcardDeckId = flashcardDeckId;
    }

    public ExerciseDto getExercise() {
        return exercise;
    }

    public void setExercise(ExerciseDto exercise) {
        this.exercise = exercise;
    }

    public ExerciseSessionProgressDto getProgress() {
        return progress;
    }

    public void setProgress(ExerciseSessionProgressDto progress) {
        this.progress = progress;
    }
}
