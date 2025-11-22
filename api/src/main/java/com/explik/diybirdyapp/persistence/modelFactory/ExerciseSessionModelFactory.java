package com.explik.diybirdyapp.persistence.modelFactory;

import com.explik.diybirdyapp.dto.exercise.ExerciseSessionDto;
import com.explik.diybirdyapp.persistence.vertex.ExerciseSessionVertex;

public interface ExerciseSessionModelFactory {
    ExerciseSessionDto create(ExerciseSessionVertex vertex);
}
