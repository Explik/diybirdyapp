package com.explik.diybirdyapp.persistence.repository;

import com.explik.diybirdyapp.model.exercise.ExerciseDto;
import com.explik.diybirdyapp.model.admin.ExerciseAnswerModel;

import java.util.List;

public interface ExerciseRepository {
    ExerciseDto get(String id, String sessionId);

    List<ExerciseDto> getAll();

    ExerciseDto submitAnswer(ExerciseAnswerModel answer);
}
