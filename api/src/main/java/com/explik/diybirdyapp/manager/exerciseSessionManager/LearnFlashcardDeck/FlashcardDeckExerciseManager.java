package com.explik.diybirdyapp.manager.exerciseSessionManager.LearnFlashcardDeck;

import com.explik.diybirdyapp.ExerciseTypes;
import com.explik.diybirdyapp.manager.exerciseCreationManager.*;
import com.explik.diybirdyapp.persistence.vertex.*;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Exercise Manager - Creates exercises for content selected from the active content batch.
 * 
 * Implements a content-first approach where content is selected in order from the active content batch.
 * Each piece of content is exercised up to 3 times across different exercise types before moving to the next.
 * The manager attempts to create exercises using available exercise creation strategies based on session settings.
 */
@Component
public class FlashcardDeckExerciseManager {
    
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
     * Generates the next exercise for the session using a content-first approach.
     * Selects content from the active content batch in order and attempts to create exercises for it.
     * Each piece of content can be exercised up to 3 times before moving to the next.
     * 
     * @param traversalSource The graph traversal source
     * @param sessionVertex The exercise session vertex
     * @return The created exercise vertex, null if more content is needed, or null if session is complete
     */
    public com.explik.diybirdyapp.persistence.vertex.ExerciseVertex nextExerciseVertex(
            org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource traversalSource, 
            com.explik.diybirdyapp.persistence.vertex.ExerciseSessionVertex sessionVertex) {
        
        // Get or create the activeContentBatch state
        var stateVertex = getOrCreateActiveContentState(traversalSource, sessionVertex);
        
        // Get active content list
        var activeContent = stateVertex.getActiveContent();
        
        // If active content is empty, return null (session manager should populate)
        if (activeContent.isEmpty()) {
            return null;
        }
        
        // Calculate enabled exercise types based on session options
        var exerciseTypes = calculateEnabledExerciseTypes(sessionVertex);
        
        // Get current content index
        int currentIndex = stateVertex.getCurrentContentIndex();
        
        // Try to create exercise for content starting from current index
        while (currentIndex < activeContent.size()) {
            var content = activeContent.get(currentIndex);
            String contentId = getContentId(content);
            
            if (contentId == null) {
                // Skip content without ID
                currentIndex++;
                stateVertex.setCurrentContentIndex(currentIndex);
                continue;
            }
            
            // Check if content has reached max exercises (3)
            if (stateVertex.hasReachedMaxExercises(contentId)) {
                // Move to next content
                currentIndex++;
                stateVertex.setCurrentContentIndex(currentIndex);
                continue;
            }
            
            // Try to create an exercise for this content
            var exerciseVertex = tryCreateExerciseForContent(
                    traversalSource, 
                    sessionVertex, 
                    content, 
                    exerciseTypes);
            
            if (exerciseVertex != null) {
                // Exercise created successfully, increment count
                stateVertex.incrementExerciseCountForContent(contentId);
                return exerciseVertex;
            } else {
                // No exercise could be created, mark as exercised 3 times to skip it
                stateVertex.setPropertyValue("exerciseCount_" + contentId, 3);
                currentIndex++;
                stateVertex.setCurrentContentIndex(currentIndex);
            }
        }
        
        // All content in current batch has been processed
        // Return null to signal session manager to populate more content
        return null;
    }
    
    /**
     * Attempts to create an exercise for the given content using available exercise types.
     * Implements difficulty curve by trying to use a different exercise type than last time.
     * 
     * @param traversalSource The graph traversal source
     * @param sessionVertex The exercise session vertex
     * @param content The content to create an exercise for
     * @param exerciseTypes List of enabled exercise type IDs
     * @return The created exercise vertex, or null if no new exercise type can be created
     */
    private ExerciseVertex tryCreateExerciseForContent(
            GraphTraversalSource traversalSource,
            ExerciseSessionVertex sessionVertex,
            AbstractVertex content,
            java.util.List<String> exerciseTypes) {
        
        // Determine the type of content and filter applicable exercise types
        FlashcardVertex flashcardVertex = null;
        PronunciationVertex pronunciationVertex = null;
        TextContentVertex textContentVertex = null;
        
        if (content instanceof FlashcardVertex) {
            flashcardVertex = (FlashcardVertex) content;
        } else if (content instanceof PronunciationVertex) {
            pronunciationVertex = (PronunciationVertex) content;
        } else if (content instanceof TextContentVertex) {
            textContentVertex = (TextContentVertex) content;
        } else {
            return null;
        }
        
        // Get state vertex and last exercise type used for this content
        var stateVertex = getOrCreateActiveContentState(traversalSource, sessionVertex);
        String contentId = getContentId(content);
        String lastExerciseType = stateVertex.getLastExerciseTypeForContent(contentId);
        
        // Try each exercise type in order, skipping the last one used
        for (String exerciseType : exerciseTypes) {
            // Skip if this is the same exercise type as last time
            if (exerciseType.equals(lastExerciseType)) {
                continue;
            }
            
            ExerciseVertex exercise = null;
            
            switch (exerciseType) {
                case ExerciseTypes.REVIEW_FLASHCARD:
                    if (flashcardVertex != null) {
                        exercise = tryCreateReviewExercise(traversalSource, sessionVertex, flashcardVertex);
                    }
                    break;
                case ExerciseTypes.SELECT_FLASHCARD:
                    if (flashcardVertex != null) {
                        exercise = tryCreateSelectExercise(traversalSource, sessionVertex, flashcardVertex);
                    }
                    break;
                case ExerciseTypes.LISTEN_AND_SELECT:
                    if (pronunciationVertex != null) {
                        exercise = tryCreateListenAndSelectExercise(traversalSource, sessionVertex, pronunciationVertex);
                    }
                    break;
                case ExerciseTypes.WRITE_FLASHCARD:
                    if (flashcardVertex != null) {
                        exercise = tryCreateWriteExercise(traversalSource, sessionVertex, flashcardVertex);
                    }
                    break;
                case ExerciseTypes.LISTEN_AND_WRITE:
                    if (pronunciationVertex != null) {
                        exercise = tryCreateListenAndWriteExercise(traversalSource, sessionVertex, pronunciationVertex);
                    }
                    break;
                case ExerciseTypes.PRONOUNCE_FLASHCARD:
                    if (textContentVertex != null) {
                        exercise = tryCreatePronounceExercise(traversalSource, sessionVertex, textContentVertex);
                    }
                    break;
            }
            
            if (exercise != null) {
                // Track this exercise type for future difficulty curve
                stateVertex.setLastExerciseTypeForContent(contentId, exerciseType);
                return exercise;
            }
        }
        
        // No new exercise type could be created
        return null;
    }
    
    /**
     * Gets or creates the activeContentBatch state vertex.
     */
    private ExerciseSessionStateVertex getOrCreateActiveContentState(
            GraphTraversalSource traversalSource,
            ExerciseSessionVertex sessionVertex) {
        
        var stateVertices = sessionVertex.getStatesWithType("activeContentBatch");
        
        if (stateVertices.isEmpty()) {
            var stateVertex = ExerciseSessionStateVertex.create(traversalSource);
            stateVertex.setType("activeContentBatch");
            stateVertex.setCurrentContentIndex(0);
            sessionVertex.addState(stateVertex);
            return stateVertex;
        }
        
        return stateVertices.get(0);
    }
    
    /**
     * Gets the ID from a vertex, handling both ContentVertex and PronunciationVertex types.
     */
    private String getContentId(AbstractVertex vertex) {
        if (vertex instanceof ContentVertex contentVertex) {
            return contentVertex.getId();
        } else if (vertex instanceof PronunciationVertex pronunciationVertex) {
            return pronunciationVertex.getId();
        } else if (vertex instanceof FlashcardVertex flashcardVertex) {
            return flashcardVertex.getId();
        }
        return null;
    }

    private ExerciseVertex tryCreateReviewExercise(
            GraphTraversalSource traversalSource, 
            ExerciseSessionVertex sessionVertex,
            FlashcardVertex flashcardVertex) {
        
        var stateVertex = getOrCreateActiveContentState(traversalSource, sessionVertex);
        var activeContent = stateVertex.getActiveContent();
        
        var context = ExerciseCreationContext.createForFlashcard(
                sessionVertex,
                flashcardVertex,
                null,
                ExerciseTypes.REVIEW_FLASHCARD);
        context.setActiveContent(activeContent);
        
        return reviewFlashcardExerciseCreationManager.createExercise(traversalSource, context);
    }

    private ExerciseVertex tryCreateSelectExercise(
            GraphTraversalSource traversalSource, 
            ExerciseSessionVertex sessionVertex,
            FlashcardVertex flashcardVertex) {
        
        var stateVertex = getOrCreateActiveContentState(traversalSource, sessionVertex);
        var activeContent = stateVertex.getActiveContent();
        
        var context = ExerciseCreationContext.createForFlashcard(
                sessionVertex,
                flashcardVertex,
                "front",
                ExerciseTypes.SELECT_FLASHCARD);
        context.setActiveContent(activeContent);
        
        return selectFlashcardExerciseCreationManager.createExercise(traversalSource, context);
    }

    private ExerciseVertex tryCreateListenAndSelectExercise(
            GraphTraversalSource traversalSource, 
            ExerciseSessionVertex sessionVertex,
            PronunciationVertex pronunciationVertex) {
        
        var stateVertex = getOrCreateActiveContentState(traversalSource, sessionVertex);
        var activeContent = stateVertex.getActiveContent();
        
        var context = ExerciseCreationContext.createForPronunciation(
                sessionVertex,
                pronunciationVertex,
                ExerciseTypes.LISTEN_AND_SELECT);
        context.setActiveContent(activeContent);
        
        return listenAndSelectExerciseCreationManager.createExercise(traversalSource, context);
    }

    private ExerciseVertex tryCreateWriteExercise(
            GraphTraversalSource traversalSource, 
            ExerciseSessionVertex sessionVertex,
            FlashcardVertex flashcardVertex) {
        
        var stateVertex = getOrCreateActiveContentState(traversalSource, sessionVertex);
        var activeContent = stateVertex.getActiveContent();
        
        var context = ExerciseCreationContext.createForFlashcard(
                sessionVertex,
                flashcardVertex,
                "front",
                ExerciseTypes.WRITE_FLASHCARD);
        context.setActiveContent(activeContent);
        
        return writeFlashcardExerciseCreationManager.createExercise(traversalSource, context);
    }

    private ExerciseVertex tryCreateListenAndWriteExercise(
            GraphTraversalSource traversalSource, 
            ExerciseSessionVertex sessionVertex,
            PronunciationVertex pronunciationVertex) {
        
        var stateVertex = getOrCreateActiveContentState(traversalSource, sessionVertex);
        var activeContent = stateVertex.getActiveContent();
        
        var context = ExerciseCreationContext.createForPronunciation(
                sessionVertex,
                pronunciationVertex,
                ExerciseTypes.LISTEN_AND_WRITE);
        context.setActiveContent(activeContent);
        
        return listenAndWriteExerciseCreationManager.createExercise(traversalSource, context);
    }

    private ExerciseVertex tryCreatePronounceExercise(
            GraphTraversalSource traversalSource, 
            ExerciseSessionVertex sessionVertex,
            TextContentVertex textContentVertex) {
        
        var stateVertex = getOrCreateActiveContentState(traversalSource, sessionVertex);
        var activeContent = stateVertex.getActiveContent();
        
        var context = ExerciseCreationContext.createForText(
                sessionVertex,
                textContentVertex,
                ExerciseTypes.PRONOUNCE_FLASHCARD);
        context.setActiveContent(activeContent);
        
        return pronounceFlashcardExerciseCreationManager.createExercise(traversalSource, context);
    }

    /**
     * Calculates the list of enabled exercise types based on session options.
     * 
     * @param sessionVertex The exercise session vertex
     * @return List of enabled exercise type IDs
     */
    private List<String> calculateEnabledExerciseTypes(ExerciseSessionVertex sessionVertex) {
        var options = sessionVertex.getOptions();
        if (options == null) {
            return List.of();
        }
        
        var exerciseTypes = new ArrayList<String>();
        
        if (options.getIncludeReviewExercises()) {
            exerciseTypes.add(ExerciseTypes.REVIEW_FLASHCARD);
        }
        
        if (options.getIncludeMultipleChoiceExercises()) {
            exerciseTypes.add(ExerciseTypes.SELECT_FLASHCARD);
            
            if (options.getIncludeListeningExercises()) {
                exerciseTypes.add(ExerciseTypes.LISTEN_AND_SELECT);
            }
        }
        
        if (options.getIncludeWritingExercises()) {
            exerciseTypes.add(ExerciseTypes.WRITE_FLASHCARD);
            
            if (options.getIncludeListeningExercises()) {
                exerciseTypes.add(ExerciseTypes.LISTEN_AND_WRITE);
            }
        }
        
        if (options.getIncludePronunciationExercises()) {
            exerciseTypes.add(ExerciseTypes.PRONOUNCE_FLASHCARD);
        }
        
        return exerciseTypes;
    }
}
