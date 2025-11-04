package com.explik.diybirdyapp.persistence.repository;

import java.util.List;

import com.explik.diybirdyapp.model.exercise.ExerciseInputModel;
import com.explik.diybirdyapp.model.exercise.ExerciseModel;

public interface ExerciseRepository {
    ExerciseModel get(String id, String sessionId);

    List<ExerciseModel> getAll();

    ExerciseModel submitAnswer(ExerciseInputModel answer);
}
