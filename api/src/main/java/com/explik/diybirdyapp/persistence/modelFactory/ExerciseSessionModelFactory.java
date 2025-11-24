package com.explik.diybirdyapp.persistence.modelFactory;

import com.explik.diybirdyapp.model.exercise.ExerciseSessionDto;
import com.explik.diybirdyapp.persistence.vertex.ExerciseSessionVertex;

public interface ExerciseSessionModelFactory {
    ExerciseSessionDto create(ExerciseSessionVertex vertex);
}
