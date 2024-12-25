package com.explik.diybirdyapp.model;

import java.util.ArrayList;
import java.util.List;

public class ExerciseInputMultipleChoiceTextModel extends ExerciseInputModel {
    private Feedback feedback;
    private List<Option> options = new ArrayList<>();

    public Feedback getFeedback() { return feedback; }

    public void setFeedback(Feedback feedback) { this.feedback = feedback; }

    public List<Option> getOptions() { return options; }

    public void addOption(Option option) { options.add(option); }

    public void setOptions(List<Option> options) { this.options = options; }

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

    public static class Feedback {
        private List<String> correctOptionIds;
        private List<String> incorrectOptionIds;

        public List<String> getCorrectOptionIds() { return correctOptionIds; }

        public void setCorrectOptionIds(List<String> correctOptionIds) { this.correctOptionIds = correctOptionIds; }

        public List<String> getIncorrectOptionIds() { return incorrectOptionIds; }

        public void setIncorrectOptionIds(List<String> incorrectOptionIds) { this.incorrectOptionIds = incorrectOptionIds; }
    }
}
