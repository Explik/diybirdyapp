package com.explik.diybirdyapp.manager.exerciseSessionManager;

import com.explik.diybirdyapp.manager.exerciseCreationManager.ExerciseCreationContext;
import com.explik.diybirdyapp.model.exercise.ExerciseSessionDto;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;

public interface ExerciseSessionManager {
    ExerciseSessionDto init(GraphTraversalSource traversalSource, ExerciseCreationContext context);

    ExerciseSessionDto nextExercise(GraphTraversalSource traversalSource, ExerciseCreationContext context);
    
    /**
     * Called when session options are updated. Implementations can use this to reset state,
     * regenerate content batches, or perform other session-specific logic.
     * 
     * @param traversalSource The graph traversal source
     * @param sessionId The ID of the session being updated
     */
    default void updateOptions(GraphTraversalSource traversalSource, String sessionId) {
        // Default implementation does nothing
    }
}
