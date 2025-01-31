package com.explik.diybirdyapp.model.exercise;

import java.util.List;

public class ExerciseInputTextModel extends ExerciseInputModel {
    private String text;
    private Feedback feedback;

    public String getText() { return text; }

    public void setText(String text) { this.text = text; }

    public Feedback getFeedback() { return feedback; }

    public void setFeedback(Feedback feedback) { this.feedback = feedback; }

    public static class Feedback {
        private List<String> correctValues;
        private List<String> incorrectValues;

        public List<String> getCorrectValues() { return correctValues; }

        public void setCorrectValues(List<String> correctValues) { this.correctValues = correctValues; }

        public List<String> getIncorrectValues() { return incorrectValues; }

        public void setIncorrectValues(List<String> incorrectValues) { this.incorrectValues = incorrectValues; }
    }
}
