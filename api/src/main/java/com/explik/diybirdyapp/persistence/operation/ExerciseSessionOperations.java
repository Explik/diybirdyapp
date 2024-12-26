package com.explik.diybirdyapp.persistence.operation;

import com.explik.diybirdyapp.model.ExerciseModel;
import com.explik.diybirdyapp.model.ExerciseSessionModel;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;

public interface ExerciseSessionOperations {
    ExerciseSessionModel init(GraphTraversalSource traversalSource, ExerciseSessionModel model);

    ExerciseSessionModel nextExercise(GraphTraversalSource traversalSource, String modelId);
}
