package com.explik.diybirdyapp.graph.repository;

import com.explik.diybirdyapp.graph.model.ExerciseModel;
import com.explik.diybirdyapp.graph.model.ExerciseSessionModel;

public interface ExerciseSessionRepository {
    ExerciseSessionModel add(ExerciseSessionModel model);
    ExerciseSessionModel get(String id);
    ExerciseModel nextExercise(String modelId);
}
