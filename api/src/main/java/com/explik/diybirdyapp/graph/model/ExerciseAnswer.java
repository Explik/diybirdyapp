package com.explik.diybirdyapp.graph.model;

public abstract class ExerciseAnswer {
    public String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public abstract  String getAnswerType();
}