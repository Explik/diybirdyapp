package com.explik.diybirdyapp.persistence.query.modelFactory;

import com.explik.diybirdyapp.model.exercise.ExerciseSessionProgressDto;
import com.explik.diybirdyapp.persistence.vertex.ExerciseSessionVertex;
import org.springframework.stereotype.Component;

/**
 * Progress factory for LEARN_FLASHCARD sessions.
 * Calculates progress based on batch completion with visual indicators for upcoming batches.
 */
@Component
public class LearnFlashcardProgressFactory implements ExerciseSessionProgressFactory {
    
    // Should match the BATCH_SIZE constant in ExerciseSessionManagerLearnFlashcardDeck
    private static final int BATCH_SIZE = 20;
    
    @Override
    public ExerciseSessionProgressDto createProgress(ExerciseSessionVertex vertex) {
        ExerciseSessionProgressDto progressModel = new ExerciseSessionProgressDto();
        progressModel.setType("batch-percentage");
        
        // Get the active content batch state
        var stateVertices = vertex.getStatesWithType("activeContentBatch");
        if (stateVertices.isEmpty()) {
            // No batch started yet
            progressModel.setPercentage(0);
            progressModel.setCurrentBatch(1);
            progressModel.setHasMoreBatches(true);
            return progressModel;
        }
        
        var stateVertex = stateVertices.get(0);
        
        // Get batch exercise count
        Integer batchExerciseCount = stateVertex.getPropertyValue("batchExerciseCount", 0);
        int exerciseCount = batchExerciseCount != null ? batchExerciseCount : 0;
        
        // Calculate percentage within the current batch
        int percentage = Math.min(100, (int) Math.round((double) exerciseCount / BATCH_SIZE * 100));
        progressModel.setPercentage(percentage);
        
        // Determine current batch number (for display purposes)
        // This could be enhanced to track actual batch number if needed
        progressModel.setCurrentBatch(1);

        // Assume there are more batches until we know otherwise (e.g., when batch is completed)
        progressModel.setHasMoreBatches(true);
        
        return progressModel;
    }
}
