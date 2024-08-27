package com.explik.diybirdyapp.graph.repository;

import com.explik.diybirdyapp.graph.model.ExerciseModel;
import com.explik.diybirdyapp.graph.vertex.ExerciseVertex;

public interface ExerciseFactory {
    ExerciseModel create(ExerciseVertex vertex);

    ExerciseModel createLimited(ExerciseVertex vertex);
}
