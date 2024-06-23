package com.explik.diybirdyapp.graph.model;

public class GenericExercise extends Exercise {
    private String exerciseType;

    public GenericExercise() { }

    public GenericExercise(String id, String exerciseType) {
        this.setId(id);
        this.setExerciseType(exerciseType);
    }

    @Override
    public String getExerciseType() {
        return exerciseType;
    }

    public void setExerciseType(String exerciseType) {
        this.exerciseType = exerciseType;
    }
}
