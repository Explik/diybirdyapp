package com.explik.diybirdyapp.controller.dto.exercise;

public class ExerciseInputArrangeTextOptionsDto extends ExerciseInputDto {
    private String value;
    private ArrangeTextOption[] options;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public ArrangeTextOption[] getOptions() {
        return options;
    }

    public void setOptions(ArrangeTextOption[] options) {
        this.options = options;
    }

    public static class ArrangeTextOption {
        private String id;
        private String text;

        public ArrangeTextOption() {

        }

        public ArrangeTextOption(String id, String text) {
            this.id = id;
            this.text = text;
        }

        public String getId() { return id; }

        public void setId(String id) { this.id = id; }

        public String getText() { return text; }

        public void setText(String text) { this.text = text; }
    }
}
