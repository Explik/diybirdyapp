package com.explik.diybirdyapp.model.dto;

public class ExerciseLightDTO extends BaseDTO {
    String id;
    String exerciseType;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getExerciseType() {
        return exerciseType;
    }

    public void setExerciseType(String exerciseType) {
        this.exerciseType = exerciseType;
    }
}
