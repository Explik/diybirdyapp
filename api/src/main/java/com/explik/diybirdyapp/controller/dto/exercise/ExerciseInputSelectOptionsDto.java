package com.explik.diybirdyapp.controller.dto.exercise;

import com.explik.diybirdyapp.ExerciseInputTypes;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class ExerciseInputSelectOptionsDto extends ExerciseInputDto {
    @NotNull
    private List<SelectOptionInputBaseOption> options;

    @NotNull
    private String optionType;

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

    public List<SelectOptionInputBaseOption> getOptions() {
        return options;
    }

    public void setOptions(List<SelectOptionInputBaseOption> options) {
        this.options = options;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public static class SelectOptionInputBaseOption {
        @NotNull
        private String id;

        public SelectOptionInputBaseOption() {
        }

        public SelectOptionInputBaseOption(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

    public static class SelectOptionInputAudioOption extends SelectOptionInputBaseOption {
        @NotNull
        private String url;

        public SelectOptionInputAudioOption() {
            super();
        }

        public SelectOptionInputAudioOption(String id, String url) {
            super(id);
            this.url = url;
        }

        public String getUrl() { return url; }

        public void setUrl(String url) { this.url = url; }
    }

    public static class SelectOptionInputTextOption extends SelectOptionInputBaseOption {
        @NotNull
        private String text;

        public SelectOptionInputTextOption() {
            super();
        }

        public SelectOptionInputTextOption(String id, String text) {
            super(id);
            this.text = text;
        }

        public String getText() { return text; }

        public void setText(String text) { this.text = text; }
    }

    public static class SelectOptionInputImageOption extends SelectOptionInputBaseOption {
        @NotNull
        private String url;

        public SelectOptionInputImageOption() {
            super();
        }

        public SelectOptionInputImageOption(String id, String url) {
            super(id);
            this.url = url;
        }

        public String getUrl() { return url; }

        public void setUrl(String url) { this.url = url; }
    }

    public static class SelectOptionInputVideoOption extends SelectOptionInputBaseOption {
        @NotNull
        private String url;

        public SelectOptionInputVideoOption() {
            super();
        }

        public SelectOptionInputVideoOption(String id, String url) {
            super(id);
            this.url = url;
        }

        public String getUrl() { return url; }

        public void setUrl(String url) { this.url = url; }
    }

    public static class SelectOptionsInputFeedbackDto {
        @NotNull
        private List<String> correctOptionIds = List.of();

        @NotNull
        private List<String> incorrectOptionIds = List.of();

        private boolean isRetypeAnswerEnabled = false;

        public boolean getIsRetypeAnswerEnabled() { return isRetypeAnswerEnabled; }

        public void setIsRetypeAnswerEnabled(boolean retypeAnswerEnabled) { isRetypeAnswerEnabled = retypeAnswerEnabled; }

        public List<String> getCorrectOptionIds() { return correctOptionIds; }

        public void setCorrectOptionIds(List<String> correctOptionIds) { this.correctOptionIds = correctOptionIds; }

        public List<String> getIncorrectOptionIds() { return incorrectOptionIds; }

        public void setIncorrectOptionIds(List<String> incorrectOptionIds) { this.incorrectOptionIds = incorrectOptionIds; }
    }
}
