package com.explik.diybirdyapp.persistence.strategy;

import com.explik.diybirdyapp.model.exercise.ExerciseFeedbackDto;

public class ExerciseFeedbackHelper {
    public static ExerciseFeedbackDto createIndecisiveFeedback() {
        var feedback = new ExerciseFeedbackDto();
        feedback.setType("general");
        feedback.setState("indecisive");
        return feedback;
    }

    public static ExerciseFeedbackDto createCorrectFeedback() {
        var feedback = new ExerciseFeedbackDto();
        feedback.setType("general");
        feedback.setState("correct");
        return feedback;
    }

    public static ExerciseFeedbackDto createCorrectFeedback(boolean isCorrect) {
        return isCorrect ? createCorrectFeedback() : createIncorrectFeedback();
    }

    public static ExerciseFeedbackDto createIncorrectFeedback() {
        var feedback = new ExerciseFeedbackDto();
        feedback.setType("general");
        feedback.setState("incorrect");
        return feedback;
    }
}
