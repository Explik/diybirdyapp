package com.explik.diybirdyapp.manager.exerciseSessionManager;

import com.explik.diybirdyapp.model.exercise.ExerciseSessionDto;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;

public interface ExerciseSessionManager {
    ExerciseSessionDto init(GraphTraversalSource traversalSource, ExerciseCreationContext context);

    ExerciseSessionDto nextExercise(GraphTraversalSource traversalSource, ExerciseCreationContext context);
}
