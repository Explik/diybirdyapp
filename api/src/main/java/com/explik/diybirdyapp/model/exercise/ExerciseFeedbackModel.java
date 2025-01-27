package com.explik.diybirdyapp.model.exercise;

public class ExerciseFeedbackModel {
    private String type;
    private String state;
    private String message;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static ExerciseFeedbackModel createIndecisiveFeedback() {
        var feedback = new ExerciseFeedbackModel();
        feedback.setType("general");
        feedback.setState("indecisive");
        return feedback;
    }

    public static ExerciseFeedbackModel createCorrectFeedback() {
        var feedback = new ExerciseFeedbackModel();
        feedback.setType("general");
        feedback.setState("correct");
        return feedback;
    }

    public static ExerciseFeedbackModel createCorrectFeedback(boolean isCorrect) {
        return isCorrect ? createCorrectFeedback() : createIncorrectFeedback();
    }

    public static ExerciseFeedbackModel createIncorrectFeedback() {
        var feedback = new ExerciseFeedbackModel();
        feedback.setType("general");
        feedback.setState("incorrect");
        return feedback;
    }
}
