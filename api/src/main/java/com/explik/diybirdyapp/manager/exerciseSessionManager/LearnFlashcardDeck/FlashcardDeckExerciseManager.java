package com.explik.diybirdyapp.manager.exerciseSessionManager.LearnFlashcardDeck;

import com.explik.diybirdyapp.ExerciseTypes;
import com.explik.diybirdyapp.manager.exerciseCreationManager.*;
import com.explik.diybirdyapp.persistence.vertex.ExerciseSessionVertex;
import com.explik.diybirdyapp.persistence.vertex.ExerciseVertex;
import com.explik.diybirdyapp.persistence.vertex.FlashcardVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Exercise Manager - Creates or repeats exercises for the selected content (incl. flashcard, 
 * associated content) based on the exercise answer/feedback history.
 * 
 * It implements the difficulty curve logic to determine whether to create a new exercise or repeat 
 * an existing exercise. If required, the manager will use a set of exercise creation strategies using 
 * the ExerciseCreationContext to create new exercises for the selected content.
 */
@Component
public class FlashcardDeckExerciseManager {
    
    @Autowired
    private FlashcardDeckContentCrawler contentCrawler;
    
    @Autowired
    private ReviewFlashcardExerciseCreationManager reviewFlashcardExerciseCreationManager;

    @Autowired
    private SelectFlashcardExerciseCreationManager selectFlashcardExerciseCreationManager;

    @Autowired
    private WriteFlashcardExerciseCreationManager writeFlashcardExerciseCreationManager;

    @Autowired
    private ListenAndSelectExerciseCreationManager listenAndSelectExerciseCreationManager;

    @Autowired
    private ListenAndWriteExerciseCreationManager listenAndWriteExerciseCreationManager;

    @Autowired
    private PronounceFlashcardExerciseCreationManager pronounceFlashcardExerciseCreationManager;

    /**
     * Generates the next exercise for the session based on exercise types enabled and flashcard availability.
     * 
     * @param traversalSource The graph traversal source
     * @param sessionVertex The exercise session vertex
     * @return The created exercise vertex, or null if the session is complete
     */
    public com.explik.diybirdyapp.persistence.vertex.ExerciseVertex nextExerciseVertex(
            org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource traversalSource, 
            com.explik.diybirdyapp.persistence.vertex.ExerciseSessionVertex sessionVertex) {
        
        var exerciseTypes = sessionVertex.getOptions().getExerciseTypes().stream()
                .map(com.explik.diybirdyapp.persistence.vertex.ExerciseTypeVertex::getId)
                .toList();

        if (exerciseTypes.contains(com.explik.diybirdyapp.ExerciseTypes.REVIEW_FLASHCARD)) {
            var reviewExerciseVertex = tryGenerateReviewExercise(traversalSource, sessionVertex);
            if (reviewExerciseVertex != null)
                return reviewExerciseVertex;
        }

        if (exerciseTypes.contains(com.explik.diybirdyapp.ExerciseTypes.SELECT_FLASHCARD)) {
            var selectExerciseVertex = tryGenerateSelectExercise(traversalSource, sessionVertex);
            if (selectExerciseVertex != null)
                return selectExerciseVertex;
        }

        if (exerciseTypes.contains(ExerciseTypes.LISTEN_AND_SELECT)) {
            var listenAndSelectExerciseVertex = tryGenerateListenAndSelectExercise(traversalSource, sessionVertex);
            if (listenAndSelectExerciseVertex != null)
                return listenAndSelectExerciseVertex;
        }

        if (exerciseTypes.contains(ExerciseTypes.WRITE_FLASHCARD)) {
            var writeExerciseVertex = tryGenerateWriteExercise(traversalSource, sessionVertex);
            if (writeExerciseVertex != null)
                return writeExerciseVertex;
        }

        if (exerciseTypes.contains(ExerciseTypes.LISTEN_AND_WRITE)) {
            var listenAndWriteExerciseVertex = tryGenerateListenAndWriteExercise(traversalSource, sessionVertex);
            if (listenAndWriteExerciseVertex != null)
                return listenAndWriteExerciseVertex;
        }

        if (exerciseTypes.contains(ExerciseTypes.PRONOUNCE_FLASHCARD)) {
            var pronounceExerciseVertex = tryGeneratePronounceExercise(traversalSource, sessionVertex);
            if (pronounceExerciseVertex != null)
                return pronounceExerciseVertex;
        }

        // If no flashcards are found, the session is complete
        return null;
    }

    private ExerciseVertex tryGenerateReviewExercise(
            GraphTraversalSource traversalSource, 
            ExerciseSessionVertex sessionVertex) {
        var flashcardVertex = contentCrawler.findFirstNonExercisedFlashcard(
                traversalSource, 
                sessionVertex.getId(), 
                ExerciseTypes.REVIEW_FLASHCARD);
        
        if (flashcardVertex == null)
            return null;

        var context = ExerciseCreationContext.createForFlashcard(
                sessionVertex,
                flashcardVertex,
                null,
                ExerciseTypes.REVIEW_FLASHCARD);
        
        return reviewFlashcardExerciseCreationManager.createExercise(traversalSource, context);
    }

    private ExerciseVertex tryGenerateSelectExercise(GraphTraversalSource traversalSource, ExerciseSessionVertex sessionVertex) {
        var flashcardVertex = contentCrawler.findFirstNonExercisedFlashcard(traversalSource, sessionVertex.getId(), ExerciseTypes.SELECT_FLASHCARD);
        if (flashcardVertex == null)
            return null;

        var context = ExerciseCreationContext.createForFlashcard(
                sessionVertex,
                flashcardVertex,
                "front",
                ExerciseTypes.SELECT_FLASHCARD);
        
        return selectFlashcardExerciseCreationManager.createExercise(traversalSource, context);
    }

    private ExerciseVertex tryGenerateListenAndSelectExercise(GraphTraversalSource traversalSource, ExerciseSessionVertex sessionVertex) {
        var flashcardVertex = contentCrawler.findFirstNonExercisedFlashcard(traversalSource, sessionVertex.getId(), ExerciseTypes.LISTEN_AND_SELECT);
        if (flashcardVertex == null)
            return null;

        var context = ExerciseCreationContext.createForFlashcard(
                sessionVertex,
                flashcardVertex,
                "front",
                ExerciseTypes.LISTEN_AND_SELECT);
        
        return listenAndSelectExerciseCreationManager.createExercise(traversalSource, context);
    }

    private ExerciseVertex tryGenerateWriteExercise(GraphTraversalSource traversalSource, ExerciseSessionVertex sessionVertex) {
        var flashcardVertex = contentCrawler.findFirstNonExercisedFlashcard(traversalSource, sessionVertex.getId(), ExerciseTypes.WRITE_FLASHCARD);
        if (flashcardVertex == null)
            return null;

        var context = ExerciseCreationContext.createForFlashcard(
                sessionVertex,
                flashcardVertex,
                "front",
                ExerciseTypes.WRITE_FLASHCARD);
        
        return writeFlashcardExerciseCreationManager.createExercise(traversalSource, context);
    }

    private ExerciseVertex tryGenerateListenAndWriteExercise(GraphTraversalSource traversalSource, ExerciseSessionVertex sessionVertex) {
        var flashcardVertex = contentCrawler.findFirstNonExercisedFlashcard(traversalSource, sessionVertex.getId(), ExerciseTypes.LISTEN_AND_WRITE);
        if (flashcardVertex == null)
            return null;

        var context = ExerciseCreationContext.createForFlashcard(
                sessionVertex,
                flashcardVertex,
                "front",
                ExerciseTypes.LISTEN_AND_WRITE);
        
        return listenAndWriteExerciseCreationManager.createExercise(traversalSource, context);
    }

    private ExerciseVertex tryGeneratePronounceExercise(GraphTraversalSource traversalSource, ExerciseSessionVertex sessionVertex) {
        var flashcardVertex = contentCrawler.findFirstNonExercisedFlashcard(
                traversalSource, 
                sessionVertex.getId(), 
                ExerciseTypes.PRONOUNCE_FLASHCARD);
        if (flashcardVertex == null)
            return null;

        var context = ExerciseCreationContext.createForFlashcard(
                sessionVertex,
                flashcardVertex,
                "front",
                ExerciseTypes.PRONOUNCE_FLASHCARD);
        
        return pronounceFlashcardExerciseCreationManager.createExercise(traversalSource, context);
    }
}
