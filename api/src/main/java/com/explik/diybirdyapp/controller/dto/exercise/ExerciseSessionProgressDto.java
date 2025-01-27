package com.explik.diybirdyapp.controller.dto.exercise;

public class ExerciseSessionProgressDto {
    private String type;
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
