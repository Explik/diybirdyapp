package com.explik.diybirdyapp.persistence.schema;

import com.explik.diybirdyapp.ExerciseInputTypes;
import com.explik.diybirdyapp.persistence.vertex.ExerciseVertex;

import java.util.Map;
import java.util.function.Function;

public class ExerciseSchema {
    private String exerciseType;
    private String contentType;
    private String inputType;
    private boolean requireTargetLanguage = false;
    private Map<String, Function<ExerciseVertex, Object>> modelProperties;

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

    public Map<String, Function<ExerciseVertex, Object>> getModelProperties() {
        return modelProperties;
    }

    public void setModelProperties(Map<String, Function<ExerciseVertex, Object>> modelProperties) {
        this.modelProperties = modelProperties;
    }

    public ExerciseSchema withModelProperties(Map<String, Function<ExerciseVertex, Object>> modelProperties) {
        this.modelProperties = modelProperties;
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
