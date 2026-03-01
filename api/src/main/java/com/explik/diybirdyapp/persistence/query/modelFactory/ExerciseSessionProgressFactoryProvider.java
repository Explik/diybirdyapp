package com.explik.diybirdyapp.persistence.query.modelFactory;

import com.explik.diybirdyapp.ExerciseSessionTypes;
import com.explik.diybirdyapp.model.exercise.ExerciseSessionProgressDto;
import com.explik.diybirdyapp.persistence.vertex.ExerciseSessionVertex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Provider that selects the appropriate progress factory based on session type.
 */
@Component
public class ExerciseSessionProgressFactoryProvider {
    
    @Autowired
    private FlashcardDeckProgressFactory flashcardDeckProgressFactory;
    
    @Autowired
    private LearnFlashcardProgressFactory learnFlashcardProgressFactory;
    
    /**
     * Default implementation for sessions without progress tracking.
     */
    private final ExerciseSessionProgressFactory defaultProgressFactory = new ExerciseSessionProgressFactory() {
        @Override
        public ExerciseSessionProgressDto createProgress(ExerciseSessionVertex vertex) {
            // No progress tracking for sessions without specific factories
            return null;
        }
    };
    
    /**
     * Gets the appropriate progress factory for the given session.
     * @param vertex The exercise session vertex
     * @return The appropriate progress factory
     */
    public ExerciseSessionProgressFactory getFactory(ExerciseSessionVertex vertex) {
        return switch (vertex.getType()) {
            case ExerciseSessionTypes.SELECT_FLASHCARD_DECK,
                 ExerciseSessionTypes.WRITE_FLASHCARD -> flashcardDeckProgressFactory;
            case ExerciseSessionTypes.LEARN_FLASHCARD -> learnFlashcardProgressFactory;
            default -> defaultProgressFactory;
        };
    }
}
