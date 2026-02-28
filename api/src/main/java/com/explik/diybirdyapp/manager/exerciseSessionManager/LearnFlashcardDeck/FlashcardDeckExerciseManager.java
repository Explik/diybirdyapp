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
 * Implements a round-based approach where each round covers all content with one exercise type.
 * For example: Round 1 reviews all content (X, Y, Z), Round 2 has write exercises for all content,
 * and so on. This maximizes content exposure per round and prevents working memory reliance.
 * Each piece of content is exercised MAX_EXERCISES_PER_CONTENT times (once per round).
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
     * Generates the next exercise for the session using a round-based approach.
     * Each round processes all content with one exercise type before moving to the next round.
     * For example: Round 1 creates review exercises for all content (X, Y, Z),
     * Round 2 creates write exercises for all content, etc.
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
        
        if (exerciseTypes.isEmpty()) {
            return null;
        }
        
        // Get current round and content index
        int currentRound = stateVertex.getCurrentRound();
        int currentIndex = stateVertex.getCurrentContentIndex();
        
        // Check if we've completed all rounds
        if (currentRound >= ExerciseSessionStateVertex.MAX_EXERCISES_PER_CONTENT) {
            return null; // All rounds complete
        }
        
        // Try to create exercise for content in the current round
        while (currentIndex < activeContent.size()) {
            var content = activeContent.get(currentIndex);
            String contentId = getContentId(content);
            
            if (contentId == null) {
                // Skip content without ID
                currentIndex++;
                stateVertex.setCurrentContentIndex(currentIndex);
                continue;
            }
            
            // Try to create an exercise for this content using the current round's exercise type
            var exerciseVertex = tryCreateExerciseForContentInRound(
                    traversalSource, 
                    sessionVertex, 
                    content, 
                    exerciseTypes,
                    currentRound);
            
            if (exerciseVertex != null) {
                // Exercise created successfully
                // Move to next content in this round
                currentIndex++;
                stateVertex.setCurrentContentIndex(currentIndex);
                
                // Increment per-content counter for tracking
                stateVertex.incrementExerciseCountForContent(contentId);
                
                // Check if we've completed this round (reached end of content list)
                if (currentIndex >= activeContent.size()) {
                    // Start next round
                    stateVertex.setCurrentRound(currentRound + 1);
                    stateVertex.setCurrentContentIndex(0);
                }
                
                return exerciseVertex;
            } else {
                // No exercise could be created for this content in this round
                // Move to next content
                currentIndex++;
                stateVertex.setCurrentContentIndex(currentIndex);
            }
        }
        
        // Reached end of content list for this round without creating an exercise
        // Move to next round and reset index
        stateVertex.setCurrentRound(currentRound + 1);
        stateVertex.setCurrentContentIndex(0);
        
        // Check if we've completed all rounds
        if (currentRound + 1 >= ExerciseSessionStateVertex.MAX_EXERCISES_PER_CONTENT) {
            return null; // All rounds complete
        }
        
        // Try again with the next round
        return nextExerciseVertex(traversalSource, sessionVertex);
    }
    
    /**
     * Attempts to create an exercise for the given content in a specific round.
     * Each round uses a specific exercise type from the enabled types list.
     * 
     * @param traversalSource The graph traversal source
     * @param sessionVertex The exercise session vertex
     * @param content The content to create an exercise for
     * @param exerciseTypes List of enabled exercise type IDs
     * @param round The current round number (used to select exercise type)
     * @return The created exercise vertex, or null if no exercise can be created
     */
    private ExerciseVertex tryCreateExerciseForContentInRound(
            GraphTraversalSource traversalSource,
            ExerciseSessionVertex sessionVertex,
            AbstractVertex content,
            java.util.List<String> exerciseTypes,
            int round) {
        
        // Determine the type of content
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
        
        // Select exercise type based on current round
        // Use modulo to cycle through available exercise types if we have more rounds than types
        int exerciseTypeIndex = round % exerciseTypes.size();
        String exerciseType = exerciseTypes.get(exerciseTypeIndex);
        
        // Get state vertex for tracking
        var stateVertex = getOrCreateActiveContentState(traversalSource, sessionVertex);
        String contentId = getContentId(content);
        
        // Try to create exercise with the selected type
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
            // Track this exercise type for the content
            stateVertex.setLastExerciseTypeForContent(contentId, exerciseType);
        }
        
        return exercise;
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
     * Gets the availableContent state vertex (all deck content for multiple choice options).
     */
    private ExerciseSessionStateVertex getAvailableContentState(
            GraphTraversalSource traversalSource,
            ExerciseSessionVertex sessionVertex) {
        
        var stateVertices = sessionVertex.getStatesWithType("availableContent");
        return stateVertices.isEmpty() ? null : stateVertices.get(0);
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
        String flashcardSide = null;
        var targetLanguage = sessionVertex.getOptions().getTargetLanguage();
        if (targetLanguage != null) {
            // Determine which side of the flashcard matches the target language
            if (flashcardVertex.getLeftContent() instanceof TextContentVertex leftTextContent &&
                leftTextContent.getLanguage().getId().equals(targetLanguage.getId())) {
                flashcardSide = "front";
            }
            else if (flashcardVertex.getRightContent() instanceof TextContentVertex rightTextContent &&
                     rightTextContent.getLanguage().getId().equals(targetLanguage.getId())) {
                flashcardSide = "back";
            }
        }

        var context = ExerciseCreationContext.createForFlashcard(
                sessionVertex,
                flashcardVertex,
                flashcardSide,
                ExerciseTypes.REVIEW_FLASHCARD);
        
        return reviewFlashcardExerciseCreationManager.createExercise(traversalSource, context);
    }

    private ExerciseVertex tryCreateSelectExercise(
            GraphTraversalSource traversalSource, 
            ExerciseSessionVertex sessionVertex,
            FlashcardVertex flashcardVertex) {
        
        var availableContentState = getAvailableContentState(traversalSource, sessionVertex);
        var availableContent = availableContentState != null ? availableContentState.getAvailableContent() : new ArrayList<AbstractVertex>();
        
        var context = ExerciseCreationContext.createForFlashcard(
                sessionVertex,
                flashcardVertex,
                "front",
                ExerciseTypes.SELECT_FLASHCARD);
        context.setActiveContent(availableContent);
        
        return selectFlashcardExerciseCreationManager.createExercise(traversalSource, context);
    }

    private ExerciseVertex tryCreateListenAndSelectExercise(
            GraphTraversalSource traversalSource, 
            ExerciseSessionVertex sessionVertex,
            PronunciationVertex pronunciationVertex) {
        // Skip if pronunciation language doesn't match session target language
        var currentLanguageId = pronunciationVertex.getAudioContent().getLanguage().getId();
        var targetLanguage = sessionVertex.getOptions().getTargetLanguage();
        if (targetLanguage != null && !currentLanguageId.equals(targetLanguage.getId())) {
            return null;
        }

        var availableContentState = getAvailableContentState(traversalSource, sessionVertex);
        var availableContent = availableContentState != null ? availableContentState.getAvailableContent() : new ArrayList<AbstractVertex>();
        
        var context = ExerciseCreationContext.createForPronunciation(
                sessionVertex,
                pronunciationVertex,
                ExerciseTypes.LISTEN_AND_SELECT);
        context.setActiveContent(availableContent);
        
        return listenAndSelectExerciseCreationManager.createExercise(traversalSource, context);
    }

    private ExerciseVertex tryCreateWriteExercise(
            GraphTraversalSource traversalSource, 
            ExerciseSessionVertex sessionVertex,
            FlashcardVertex flashcardVertex) {
        var availableContentState = getAvailableContentState(traversalSource, sessionVertex);
        var availableContent = availableContentState != null ? availableContentState.getAvailableContent() : new ArrayList<AbstractVertex>();

        var context = ExerciseCreationContext.createForFlashcard(
                sessionVertex,
                flashcardVertex,
                "front",
                ExerciseTypes.WRITE_FLASHCARD);
        context.setActiveContent(availableContent);
        
        return writeFlashcardExerciseCreationManager.createExercise(traversalSource, context);
    }

    private ExerciseVertex tryCreateListenAndWriteExercise(
            GraphTraversalSource traversalSource, 
            ExerciseSessionVertex sessionVertex,
            PronunciationVertex pronunciationVertex) {
        // Skip if pronunciation language doesn't match session target language
        var currentLanguageId = pronunciationVertex.getAudioContent().getLanguage().getId();
        var targetLanguage = sessionVertex.getOptions().getTargetLanguage();
        if (targetLanguage != null && !currentLanguageId.equals(targetLanguage.getId())) {
            return null;
        }
        
        var availableContentState = getAvailableContentState(traversalSource, sessionVertex);
        var availableContent = availableContentState != null ? availableContentState.getAvailableContent() : new ArrayList<AbstractVertex>();
        
        var context = ExerciseCreationContext.createForPronunciation(
                sessionVertex,
                pronunciationVertex,
                ExerciseTypes.LISTEN_AND_WRITE);
        context.setActiveContent(availableContent);
        
        return listenAndWriteExerciseCreationManager.createExercise(traversalSource, context);
    }

    private ExerciseVertex tryCreatePronounceExercise(
            GraphTraversalSource traversalSource, 
            ExerciseSessionVertex sessionVertex,
            TextContentVertex textContentVertex) {
        // Skip if text language doesn't match session target language
        var currentLanguageId = textContentVertex.getLanguage().getId();
        var targetLanguage = sessionVertex.getOptions().getTargetLanguage();
        if (targetLanguage != null && !currentLanguageId.equals(targetLanguage.getId())) {
            return null;
        }

        var context = ExerciseCreationContext.createForText(
                sessionVertex,
                textContentVertex,
                ExerciseTypes.PRONOUNCE_FLASHCARD);
        
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
