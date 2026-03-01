package com.explik.diybirdyapp.persistence.query.modelFactory;

import com.explik.diybirdyapp.ExerciseSessionTypes;
import com.explik.diybirdyapp.ExerciseTypes;
import com.explik.diybirdyapp.model.exercise.ExerciseSessionProgressDto;
import com.explik.diybirdyapp.persistence.vertex.ExerciseSessionVertex;
import com.explik.diybirdyapp.persistence.vertex.FlashcardVertex;
import org.springframework.stereotype.Component;

/**
 * Progress factory for SELECT_FLASHCARD_DECK and WRITE_FLASHCARD sessions.
 * Calculates progress based on the percentage of flashcards that have been exercised.
 */
@Component
public class FlashcardDeckProgressFactory implements ExerciseSessionProgressFactory {
    
    @Override
    public ExerciseSessionProgressDto createProgress(ExerciseSessionVertex vertex) {
        ExerciseSessionProgressDto progressModel = new ExerciseSessionProgressDto();
        progressModel.setType("percentage");
        
        // Calculate actual progress based on exercised flashcards
        int percentage = calculateProgress(vertex);
        progressModel.setPercentage(percentage);
        
        return progressModel;
    }
    
    /**
     * Calculates the progress percentage for a session based on exercised flashcards.
     * @param vertex The exercise session vertex
     * @return The progress percentage (0-100)
     */
    private int calculateProgress(ExerciseSessionVertex vertex) {
        // Get the flashcard deck
        var flashcardDeck = vertex.getFlashcardDeck();
        if (flashcardDeck == null) {
            return 0;
        }
        
        // Get all flashcards in the deck
        var allFlashcards = flashcardDeck.getFlashcards();
        int totalFlashcards = allFlashcards.size();
        
        if (totalFlashcards == 0) {
            return 0;
        }
        
        // Map session type to exercise type
        String exerciseType = getExerciseTypeFromSessionType(vertex.getType());
        if (exerciseType == null) {
            return 0;
        }
        
        // Get non-exercised flashcards
        var traversalSource = vertex.getUnderlyingSource();
        var nonExercisedFlashcards = FlashcardVertex.findNonExercised(
            traversalSource, 
            vertex.getId(), 
            exerciseType
        );
        
        int exercisedCount = totalFlashcards - nonExercisedFlashcards.size();
        
        // Calculate percentage
        return (int) Math.round((double) exercisedCount / totalFlashcards * 100);
    }
    
    /**
     * Maps session type to exercise type.
     * @param sessionType The session type (e.g., "select-flashcard-session")
     * @return The corresponding exercise type (e.g., "select-flashcard-exercise")
     */
    private String getExerciseTypeFromSessionType(String sessionType) {
        return switch (sessionType) {
            case ExerciseSessionTypes.SELECT_FLASHCARD_DECK -> ExerciseTypes.SELECT_FLASHCARD;
            case ExerciseSessionTypes.WRITE_FLASHCARD -> ExerciseTypes.WRITE_FLASHCARD;
            case ExerciseSessionTypes.REVIEW_FLASHCARD -> ExerciseTypes.REVIEW_FLASHCARD;
            default -> null;
        };
    }
}
