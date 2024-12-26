package com.explik.diybirdyapp.controller.dto;

import java.util.List;

public class ExerciseInputMultipleChoiceTextDto extends ExerciseInputDto {
    private ExerciseInputFeedbackMultipleChoiceTextFeedbackDto feedback;
    private List<Option> options;
    private String value;

    public static String TYPE = "multiple-choice-text-input";

    public ExerciseInputFeedbackMultipleChoiceTextFeedbackDto getFeedback() {
        return feedback;
    }

    public void setFeedback(ExerciseInputFeedbackMultipleChoiceTextFeedbackDto feedback) {
        this.feedback = feedback;
    }

    public List<Option> getOptions() {
        return options;
    }

    public void setOptions(List<Option> options) {
        this.options = options;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public static class Option {
        private String id;
        private String text;

        public Option() {

        }

        public Option(String id, String text) {
            this.id = id;
            this.text = text;
        }

        public String getId() { return id; }

        public void setId(String id) { this.id = id; }

        public String getText() { return text; }

        public void setText(String text) { this.text = text; }
    }

    public static class ExerciseInputFeedbackMultipleChoiceTextFeedbackDto {
        private List<String> correctOptionIds;
        private List<String> incorrectOptionIds;

        public List<String> getCorrectOptionIds() { return correctOptionIds; }

        public void setCorrectOptionIds(List<String> correctOptionIds) { this.correctOptionIds = correctOptionIds; }

        public List<String> getIncorrectOptionIds() { return incorrectOptionIds; }

        public void setIncorrectOptionIds(List<String> incorrectOptionIds) { this.incorrectOptionIds = incorrectOptionIds; }
    }
}
