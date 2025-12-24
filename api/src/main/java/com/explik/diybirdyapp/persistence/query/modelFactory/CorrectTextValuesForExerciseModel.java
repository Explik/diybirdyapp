package com.explik.diybirdyapp.persistence.query.modelFactory;

import java.util.List;

public class CorrectTextValuesForExerciseModel {
    private String exerciseId;
    private String exerciseType;
    private List<String> correctTextValues;

    public String getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(String exerciseId) {
        this.exerciseId = exerciseId;
    }

    public String getExerciseType() {
        return exerciseType;
    }

    public void setExerciseType(String exerciseType) {
        this.exerciseType = exerciseType;
    }

    public List<String> getCorrectTextValues() {
        return correctTextValues;
    }

    public void setCorrectTextValues(List<String> correctTextValues) {
        this.correctTextValues = correctTextValues;
    }
}
