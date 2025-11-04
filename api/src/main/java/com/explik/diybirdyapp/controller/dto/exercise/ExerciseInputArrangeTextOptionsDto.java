package com.explik.diybirdyapp.controller.dto.exercise;

import com.explik.diybirdyapp.ExerciseInputTypes;
import jakarta.validation.constraints.NotNull;

public class ExerciseInputArrangeTextOptionsDto extends ExerciseInputDto {
    private String value;

    @NotNull(message = "options.required")
    private ArrangeTextOption[] options;

    public ExerciseInputArrangeTextOptionsDto() {
        setType(ExerciseInputTypes.ARRANGE_TEXT_OPTIONS);
    }

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
        @NotNull(message = "id.required")
        private String id;

        @NotNull(message = "text.required")
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
