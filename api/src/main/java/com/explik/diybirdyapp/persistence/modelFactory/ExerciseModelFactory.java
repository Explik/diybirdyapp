package com.explik.diybirdyapp.persistence.modelFactory;

import com.explik.diybirdyapp.model.exercise.ExerciseModel;
import com.explik.diybirdyapp.persistence.vertex.ExerciseVertex;

public interface ExerciseModelFactory {
    ExerciseModel create(ExerciseVertex vertex);
}
