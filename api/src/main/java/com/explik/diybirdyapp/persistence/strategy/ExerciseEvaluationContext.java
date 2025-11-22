package com.explik.diybirdyapp.persistence.strategy;

import com.explik.diybirdyapp.dto.exercise.ExerciseInputDto;
import com.explik.diybirdyapp.model.admin.ExerciseAnswerModel;

public class ExerciseEvaluationContext {
    private ExerciseInputDto input;
    private boolean retypeCorrectAnswerEnabled = false;
    private String algorithm = null;

    public ExerciseInputDto getInput() {
        return input;
    }

    public boolean getRetypeCorrectAnswerEnabled() {
        return retypeCorrectAnswerEnabled;
    }

    public void setRetypeCorrectAnswerEnabled(boolean retypeCorrectAnswerEnabled) {
        this.retypeCorrectAnswerEnabled = retypeCorrectAnswerEnabled;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public static ExerciseEvaluationContext create(ExerciseAnswerModel input) {
        var context = new ExerciseEvaluationContext();
        context.input = input.getInput();
        return context;
    }
}
