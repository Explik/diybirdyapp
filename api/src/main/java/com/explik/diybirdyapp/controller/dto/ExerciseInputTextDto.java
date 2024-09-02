package com.explik.diybirdyapp.controller.dto;

public class ExerciseInputTextDto extends ExerciseInputDto {
    private String text;

    public static String TYPE = "text-input";

    public String getText() { return text; }

    public void setText(String text) { this.text = text; }
}
