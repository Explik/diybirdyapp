package com.explik.diybirdyapp.persistence.operation;

import com.explik.diybirdyapp.dto.exercise.ExerciseSessionDto;

public class ExerciseCreationContext {
    private ExerciseSessionDto sessionModel;

    public ExerciseSessionDto getSessionModel() {
        return sessionModel;
    }

    public void setSessionModel(ExerciseSessionDto sessionModel) {
        this.sessionModel = sessionModel;
    }

    public static ExerciseCreationContext createDefault(ExerciseSessionDto sessionModel) {
        var context = new ExerciseCreationContext();
        context.setSessionModel(sessionModel);
        return context;
    }
}
