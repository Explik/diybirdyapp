package com.explik.diybirdyapp.persistence.schema;

import com.explik.diybirdyapp.ExerciseInputTypes;

public class ExerciseSchema {
    private String exerciseType;
    private String contentType;
    private String inputType;
    private boolean requireTargetLanguage = false;

    public String getExerciseType() {
        return exerciseType;
    }

    public void setExerciseType(String exerciseType) {
        this.exerciseType = exerciseType;
    }

    public ExerciseSchema withExerciseType(String exerciseType) {
        this.exerciseType = exerciseType;
        return this;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public ExerciseSchema withContentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public String getInputType() {
        return inputType;
    }

    public void setInputType(String inputType) {
        this.inputType = inputType;
    }

    public ExerciseSchema withInputType(String inputType) {
        this.inputType = inputType;
        return this;
    }

    public ExerciseSchema requireTargetLanguage() {
        this.requireTargetLanguage = true;
        return this;
    }

    public boolean getRequireTargetLanguage() {
        return requireTargetLanguage;
    }
}
