package com.explik.diybirdyapp.persistence.operation;

import com.explik.diybirdyapp.dto.exercise.ExerciseSessionDto;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;

public interface ExerciseSessionOperations {
    ExerciseSessionDto init(GraphTraversalSource traversalSource, ExerciseCreationContext context);

    ExerciseSessionDto nextExercise(GraphTraversalSource traversalSource, ExerciseCreationContext context);
}
