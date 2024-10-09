package com.explik.diybirdyapp.graph.vertex.modelFactory;

import com.explik.diybirdyapp.graph.model.ExerciseModel;
import com.explik.diybirdyapp.graph.vertex.ExerciseVertex;

public interface ExerciseModelFactory {
    ExerciseModel create(ExerciseVertex vertex);
}
