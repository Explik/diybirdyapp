package com.explik.diybirdyapp.persistence.operation;

import com.explik.diybirdyapp.model.exercise.ExerciseSessionModel;
import com.explik.diybirdyapp.persistence.ExerciseRetrievalContext;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;

public interface ExerciseSessionOperations {
    ExerciseSessionModel init(GraphTraversalSource traversalSource, ExerciseCreationContext context);

    ExerciseSessionModel nextExercise(GraphTraversalSource traversalSource, ExerciseCreationContext context);
}
