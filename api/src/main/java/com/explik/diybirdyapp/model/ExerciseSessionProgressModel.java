package com.explik.diybirdyapp.model;

public class ExerciseSessionProgressModel {
    private String type = "percentage";

    private int percentage;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getPercentage() {
        return percentage;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }
}
