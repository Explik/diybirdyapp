package com.explik.diybirdyapp.model.admin;

import com.explik.diybirdyapp.dto.exercise.ExerciseInputDto;

public class AnswerSubmissionModel {
    private String exerciseId;
    private ExerciseInputDto answer;

    public String getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(String exerciseId) {
        this.exerciseId = exerciseId;
    }

    public ExerciseInputDto getAnswer() {
        return answer;
    }

    public void setAnswer(ExerciseInputDto answer) {
        this.answer = answer;
    }
}
