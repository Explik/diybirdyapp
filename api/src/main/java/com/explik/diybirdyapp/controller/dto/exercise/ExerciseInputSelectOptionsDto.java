package com.explik.diybirdyapp.controller.dto.exercise;

import com.explik.diybirdyapp.ExerciseInputTypes;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class ExerciseInputSelectOptionsDto extends ExerciseInputDto {
    @NotNull
    private List<SelectOptionInputTextOption> options;

    @NotNull
    private String optionType = "text";

    private String value;

    private SelectOptionsInputFeedbackDto feedback;

    public ExerciseInputSelectOptionsDto() {
        setType(ExerciseInputTypes.SELECT_OPTIONS);
    }

    public SelectOptionsInputFeedbackDto getFeedback() {
        return feedback;
    }

    public void setFeedback(SelectOptionsInputFeedbackDto feedback) {
        this.feedback = feedback;
    }

    public String getOptionType() {
        return optionType;
    }

    public void setOptionType(String optionType) {
        this.optionType = optionType;
    }

    public List<SelectOptionInputTextOption> getOptions() {
        return options;
    }

    public void setOptions(List<SelectOptionInputTextOption> options) {
        this.options = options;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public static class SelectOptionInputTextOption {
        @NotNull
        private String id;

        @NotNull
        private String text;

        public SelectOptionInputTextOption() {

        }

        public SelectOptionInputTextOption(String id, String text) {
            this.id = id;
            this.text = text;
        }

        public String getId() { return id; }

        public void setId(String id) { this.id = id; }

        public String getText() { return text; }

        public void setText(String text) { this.text = text; }
    }

    public static class SelectOptionsInputFeedbackDto {
        @NotNull
        private List<String> correctOptionIds = List.of();

        @NotNull
        private List<String> incorrectOptionIds = List.of();

        public List<String> getCorrectOptionIds() { return correctOptionIds; }

        public void setCorrectOptionIds(List<String> correctOptionIds) { this.correctOptionIds = correctOptionIds; }

        public List<String> getIncorrectOptionIds() { return incorrectOptionIds; }

        public void setIncorrectOptionIds(List<String> incorrectOptionIds) { this.incorrectOptionIds = incorrectOptionIds; }
    }
}
