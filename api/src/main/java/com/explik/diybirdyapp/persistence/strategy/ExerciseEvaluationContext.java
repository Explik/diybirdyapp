package com.explik.diybirdyapp.persistence.strategy;

import com.explik.diybirdyapp.model.admin.ExerciseAnswerModel;

public class ExerciseEvaluationContext {
    private ExerciseAnswerModel answer;
    private boolean retypeCorrectAnswerEnabled = false;
    private String algorithm = null;

    public ExerciseAnswerModel getAnswer() {
        return answer;
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
        context.answer = input;
        return context;
    }
}
