package com.explik.diybirdyapp.persistence.strategy;

import com.explik.diybirdyapp.model.exercise.ExerciseInputModel;

public class ExerciseEvaluationContext {
    private ExerciseInputModel input;
    private boolean retypeCorrectAnswerEnabled = false;

    public ExerciseInputModel getInput() {
        return input;
    }

    public boolean getRetypeCorrectAnswerEnabled() {
        return retypeCorrectAnswerEnabled;
    }

    public void setRetypeCorrectAnswerEnabled(boolean retypeCorrectAnswerEnabled) {
        this.retypeCorrectAnswerEnabled = retypeCorrectAnswerEnabled;
    }

    public static ExerciseEvaluationContext create(ExerciseInputModel input) {
        var context = new ExerciseEvaluationContext();
        context.input = input;
        return context;
    }
}
