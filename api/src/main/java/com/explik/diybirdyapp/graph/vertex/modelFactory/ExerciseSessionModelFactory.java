package com.explik.diybirdyapp.graph.vertex.modelFactory;

import com.explik.diybirdyapp.graph.model.ExerciseSessionModel;
import com.explik.diybirdyapp.graph.vertex.ExerciseSessionVertex;

public interface ExerciseSessionModelFactory {
    ExerciseSessionModel create(ExerciseSessionVertex vertex);
}
