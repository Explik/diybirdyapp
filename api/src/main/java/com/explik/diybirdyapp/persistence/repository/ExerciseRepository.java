package com.explik.diybirdyapp.persistence.repository;

import java.util.List;

import com.explik.diybirdyapp.model.ExerciseInputModel;
import com.explik.diybirdyapp.model.ExerciseModel;

public interface ExerciseRepository {
    ExerciseModel get(String id);

    List<ExerciseModel> getAll();

    ExerciseModel submitAnswer(String id, ExerciseInputModel answer);
}
