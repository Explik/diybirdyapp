package com.explik.diybirdyapp.manager.exerciseCreationManager;

import com.explik.diybirdyapp.persistence.vertex.ExerciseVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;

/**
 * Manager responsible for creating exercises of a specific type.
 * Each implementation handles the creation logic for one type of exercise.
 */
public interface ExerciseCreationManager {
    /**
     * Creates an exercise based on the provided context.
     * Returns null if the exercise cannot be created (e.g., no suitable content available).
     * 
     * @param traversalSource The graph traversal source for database access
     * @param context The exercise creation context containing session, content, and configuration
     * @return The created exercise vertex, or null if creation is not possible
     */
    ExerciseVertex createExercise(GraphTraversalSource traversalSource, ExerciseCreationContext context);
}
