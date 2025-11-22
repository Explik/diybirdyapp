package com.explik.diybirdyapp.model.admin;

import com.explik.diybirdyapp.dto.exercise.ExerciseInputDto;

public class ExerciseAnswerModel {
    private String exerciseId;
    private String sessionId;
    private ExerciseInputDto answer;

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

    public ExerciseInputDto getInput() {
        return answer;
    }

    public void setAnswer(ExerciseInputDto answer) {
        this.answer = answer;
    }
}
