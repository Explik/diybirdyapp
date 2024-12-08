package com.explik.diybirdyapp.persistence.modelFactory;

import com.explik.diybirdyapp.model.ExerciseSessionModel;
import com.explik.diybirdyapp.persistence.vertex.ExerciseSessionVertex;

public interface ExerciseSessionModelFactory {
    ExerciseSessionModel create(ExerciseSessionVertex vertex);
}