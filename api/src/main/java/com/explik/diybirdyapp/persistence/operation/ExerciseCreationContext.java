package com.explik.diybirdyapp.persistence.operation;

import com.explik.diybirdyapp.model.exercise.ExerciseSessionModel;

public class ExerciseCreationContext {
    private ExerciseSessionModel sessionModel;

    public ExerciseSessionModel getSessionModel() {
        return sessionModel;
    }

    public void setSessionModel(ExerciseSessionModel sessionModel) {
        this.sessionModel = sessionModel;
    }

    public static ExerciseCreationContext createDefault(ExerciseSessionModel sessionModel) {
        var context = new ExerciseCreationContext();
        context.setSessionModel(sessionModel);
        return context;
    }
}
