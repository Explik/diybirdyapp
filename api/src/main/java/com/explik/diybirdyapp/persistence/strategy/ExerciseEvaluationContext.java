package com.explik.diybirdyapp.persistence.strategy;

import com.explik.diybirdyapp.model.exercise.ExerciseInputModel;

public class ExerciseEvaluationContext {
    private ExerciseInputModel input;

    public ExerciseInputModel getInput() {
        return input;
    }

    public static ExerciseEvaluationContext create(ExerciseInputModel input) {
        var context = new ExerciseEvaluationContext();
        context.input = input;
        return context;
    }
}
