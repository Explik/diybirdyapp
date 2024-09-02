package com.explik.diybirdyapp.controller.dto;

import java.util.List;

public class ExerciseInputMultipleChoiceTextDto extends ExerciseInputDto {
    private List<Option> options;

    public static String TYPE = "multiple-choice-text-input";

    public List<Option> getOptions() {
        return options;
    }

    public void setOptions(List<Option> options) {
        this.options = options;
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
}
