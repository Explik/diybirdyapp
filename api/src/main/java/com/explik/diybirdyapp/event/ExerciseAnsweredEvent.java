package com.explik.diybirdyapp.event;

import org.springframework.context.ApplicationEvent;

public class ExerciseAnsweredEvent extends ApplicationEvent {
    private final String exerciseType;
    private final String exerciseId;
    private final String answerId;

    public ExerciseAnsweredEvent(Object source, String exerciseType, String exerciseId, String answerId) {
        super(source);
        this.exerciseType = exerciseType;
        this.exerciseId = exerciseId;
        this.answerId = answerId;
    }

    public String getExerciseId() {
        return exerciseId;
    }

    public String getExerciseType() {
        return exerciseType;
    }

    public String getAnswerId() {
        return answerId;
    }
}
