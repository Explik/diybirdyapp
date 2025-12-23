package com.explik.diybirdyapp.manager.exerciseEvaluationManager;

import com.explik.diybirdyapp.model.exercise.ExerciseInputDto;
import com.explik.diybirdyapp.model.admin.ExerciseAnswerModel;

public class ExerciseEvaluationContext<T extends ExerciseInputDto> {
    private String exerciseId;
    private String sessionId;

    private T input;
    private boolean retypeCorrectAnswerEnabled = false;
    private String algorithm = null;

    public String getExerciseId() {
        return exerciseId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public T getInput() {
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

    public static <TInput extends ExerciseInputDto> ExerciseEvaluationContext<TInput> create(ExerciseAnswerModel<TInput> answerModel) {
        var context = new ExerciseEvaluationContext<TInput>();
        context.exerciseId = answerModel.getExerciseId();
        context.sessionId = answerModel.getSessionId();
        context.input = answerModel.getInput();
        return context;
    }
}
