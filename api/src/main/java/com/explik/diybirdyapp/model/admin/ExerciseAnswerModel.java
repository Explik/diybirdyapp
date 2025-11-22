package com.explik.diybirdyapp.model.admin;

import com.explik.diybirdyapp.dto.exercise.ExerciseInputDto;

public class ExerciseAnswerModel<T extends ExerciseInputDto> {
    private String exerciseId;
    private String sessionId;
    private T answer;

    public String getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(String exerciseId) {
        this.exerciseId = exerciseId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public T getInput() {
        return answer;
    }

    public void setInput(T answer) {
        this.answer = answer;
    }
}
