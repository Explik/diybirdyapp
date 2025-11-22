package com.explik.diybirdyapp.persistence.repository;

import com.explik.diybirdyapp.dto.exercise.ExerciseDto;
import com.explik.diybirdyapp.dto.exercise.ExerciseInputDto;
import com.explik.diybirdyapp.model.admin.ExerciseAnswerModel;

import java.util.List;

public interface ExerciseRepository {
    ExerciseDto get(String id, String sessionId);

    List<ExerciseDto> getAll();

    ExerciseDto submitAnswer(ExerciseAnswerModel answer);
}
