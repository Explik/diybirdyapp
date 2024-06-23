package com.explik.diybirdyapp.graph.model;

public class LastAnswerExerciseFeedback extends ExerciseFeedback {
    private ExerciseAnswer lastAnswer;

    public LastAnswerExerciseFeedback() { }

    public LastAnswerExerciseFeedback(ExerciseAnswer lastAnswer) {
        this.lastAnswer = lastAnswer;
    }

    @Override
    public String getFeedbackType() {
        return "repeat-last-answer-exercise-feedback";
    }

    public ExerciseAnswer getLastAnswer() {
        return this.lastAnswer;
    }

    public void setLastAnswer(ExerciseAnswer lastAnswer) {
        this.lastAnswer = lastAnswer;
    }
}
