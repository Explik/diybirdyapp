package com.explik.diybirdyapp.controller.dto.exercise;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public class ExerciseInputTextDto extends ExerciseInputDto {
    @NotNull
    private String text;

    private ExerciseInputFeedbackTextDto feedback;

    public static String TYPE = "text-input";

    public String getText() { return text; }

    public void setText(String text) { this.text = text; }

    public ExerciseInputFeedbackTextDto getFeedback() { return feedback; }

    public void setFeedback(ExerciseInputFeedbackTextDto feedback) { this.feedback = feedback; }

    public static class ExerciseInputFeedbackTextDto {
        @NotNull
        private List<String> correctValues = List.of();

        @NotNull
        private List<String> incorrectValues = List.of();

        public List<String> getCorrectValues() { return correctValues; }

        public void setCorrectValues(List<String> correctValues) { this.correctValues = correctValues; }

        public List<String> getIncorrectValues() { return incorrectValues; }

        public void setIncorrectValues(List<String> incorrectValues) { this.incorrectValues = incorrectValues; }
    }
}
