package com.explik.diybirdyapp.persistence.repository;

import com.explik.diybirdyapp.model.exercise.ExerciseSessionModel;

public interface ExerciseSessionRepository {
    ExerciseSessionModel add(ExerciseSessionModel model);
    ExerciseSessionModel get(String id);
    ExerciseSessionModel nextExercise(String modelId);
}
