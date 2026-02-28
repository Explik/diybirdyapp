package com.explik.diybirdyapp.manager.exerciseSessionManager.LearnFlashcardDeck;

import com.explik.diybirdyapp.persistence.vertex.*;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Failed Exercise Content Crawler - Retrieves content from recently failed exercises.
 * 
 * The crawler takes a flashcard deck, identifies all recently "failed" exercises, identifies 
 * any content associated with these exercises and returns a subset of this content to be used 
 * in the next exercise batch. A relevant exercise is any exercise with an answer with incorrect 
 * feedback and no "I was correct" feedback. Content is associated with an exercise if it is 
 * the main content, an answer option or etc. All returned content must be part of the flashcard 
 * deck, either flashcards or associated content.
 */
@Component
public class FailedExerciseContentCrawler {
    
    /**
     * Collects content from recently failed exercises that hasn't been added to activeContent yet.
     * Returns content associated with failed exercises (flashcards, text content, pronunciations, etc.).
     * Each vertex is returned only once - if it already exists in activeContent, it won't be returned again.
     * 
     * @param flashcardDeck The flashcard deck to check against
     * @param sessionState The session state containing activeContent to check for duplicates
     * @return List of AbstractVertex from failed exercises that are part of the flashcard deck
     *         Returns empty list if no failed exercise content is available
     */
    public List<AbstractVertex> collectNextFlashcardContent(
            FlashcardDeckVertex flashcardDeck,
            ExerciseSessionStateVertex sessionState) {
        
        // Get all vertices already in activeContent
        List<AbstractVertex> activeContent = sessionState.getActiveContent();
        Set<String> activeVertexIds = new HashSet<>();
        
        for (AbstractVertex vertex : activeContent) {
            String id = getVertexId(vertex);
            if (id != null) {
                activeVertexIds.add(id);
            }
        }
        
        // Get the session to find all exercises
        ExerciseSessionVertex session = sessionState.getSession();
        
        // Collect all content from failed exercises
        Set<AbstractVertex> failedContent = new HashSet<>();
        List<ExerciseVertex> exercises = session.getExercises();
        
        for (ExerciseVertex exercise : exercises) {
            if (isFailedExercise(exercise)) {
                // Collect all content associated with this exercise
                collectExerciseContent(exercise, flashcardDeck, failedContent);
            }
        }
        
        // Filter out content already in activeContent and convert to list
        List<AbstractVertex> newContent = failedContent.stream()
                .filter(content -> {
                    String id = getVertexId(content);
                    return id != null && !activeVertexIds.contains(id);
                })
                .collect(Collectors.toList());
        
        // Return all new failed content (or empty list if none available)
        return newContent;
    }
    
    /**
     * Checks if an exercise is considered "failed".
     * An exercise is failed if it has at least one answer with incorrect feedback 
     * and no "I was correct" type feedback.
     * 
     * @param exercise The exercise to check
     * @return true if the exercise is failed, false otherwise
     */
    private boolean isFailedExercise(ExerciseVertex exercise) {
        GraphTraversalSource traversalSource = exercise.getUnderlyingSource();
        
        // Get all answers for this exercise (using incoming edge)
        List<ExerciseAnswerVertex> answers = VertexHelper.getIngoingModels(
                exercise, 
                ExerciseAnswerVertex.EDGE_EXERCISE, 
                ExerciseAnswerVertex::new);
        
        if (answers.isEmpty()) {
            return false;
        }
        
        // Check each answer for incorrect feedback
        for (ExerciseAnswerVertex answer : answers) {
            // Get all feedback for this answer (using incoming edge)
            List<ExerciseFeedbackVertex> feedbacks = VertexHelper.getIngoingModels(
                    answer, 
                    ExerciseFeedbackVertex.EDGE_EXERCISE_ANSWER, 
                    ExerciseFeedbackVertex::new);
            
            // Check if there's any "I was correct" feedback - if so, don't count as failed
            boolean hasIWasCorrectFeedback = feedbacks.stream()
                    .anyMatch(f -> "i-was-correct".equals(f.getType()));
            
            if (hasIWasCorrectFeedback) {
                continue;
            }
            
            // Check if there's any incorrect feedback
            boolean hasIncorrectFeedback = feedbacks.stream()
                    .anyMatch(f -> "incorrect".equals(f.getStatus()));
            
            if (hasIncorrectFeedback) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Collects all content associated with an exercise that belongs to the flashcard deck.
     * This includes the main content, answer options, and their associated content (pronunciations, etc.).
     * 
     * @param exercise The exercise to collect content from
     * @param flashcardDeck The flashcard deck to validate against
     * @param contentSet The set to add collected content to
     */
    private void collectExerciseContent(
            ExerciseVertex exercise, 
            FlashcardDeckVertex flashcardDeck,
            Set<AbstractVertex> contentSet) {
        
        // Get all flashcards in the deck for validation
        Set<String> deckFlashcardIds = flashcardDeck.getFlashcards().stream()
                .map(FlashcardVertex::getId)
                .collect(Collectors.toSet());
        
        // Collect main content
        ContentVertex mainContent = exercise.getContent();
        if (mainContent != null) {
            addContentIfInDeck(mainContent, deckFlashcardIds, contentSet);
        }
        
        // Collect based on content (if different from main content)
        AbstractVertex basedOnContent = exercise.getBasedOnContent();
        if (basedOnContent != null && basedOnContent instanceof ContentVertex) {
            addContentIfInDeck((ContentVertex) basedOnContent, deckFlashcardIds, contentSet);
        }
        
        // Collect option content
        List<? extends ContentVertex> options = exercise.getOptions();
        for (ContentVertex option : options) {
            addContentIfInDeck(option, deckFlashcardIds, contentSet);
        }
        
        // Collect correct options
        List<? extends ContentVertex> correctOptions = exercise.getCorrectOptions();
        for (ContentVertex correctOption : correctOptions) {
            addContentIfInDeck(correctOption, deckFlashcardIds, contentSet);
        }
    }
    
    /**
     * Adds content to the set if it belongs to the flashcard deck.
     * For flashcard content, validates it's in the deck.
     * For text content, adds it along with its pronunciations if its parent flashcard is in the deck.
     * 
     * @param content The content to add
     * @param deckFlashcardIds Set of flashcard IDs in the deck
     * @param contentSet The set to add content to
     */
    private void addContentIfInDeck(
            ContentVertex content,
            Set<String> deckFlashcardIds,
            Set<AbstractVertex> contentSet) {
        
        if (content instanceof FlashcardVertex) {
            // Add flashcard if it's in the deck
            FlashcardVertex flashcard = (FlashcardVertex) content;
            if (deckFlashcardIds.contains(flashcard.getId())) {
                contentSet.add(flashcard);
                
                // Also add left and right content
                if (flashcard.getLeftContent() != null) {
                    addContentAndAssociations(flashcard.getLeftContent(), contentSet);
                }
                if (flashcard.getRightContent() != null) {
                    addContentAndAssociations(flashcard.getRightContent(), contentSet);
                }
            }
        } else if (content instanceof TextContentVertex) {
            // For text content, check if it belongs to a flashcard in the deck
            TextContentVertex textContent = (TextContentVertex) content;
            
            // Get parent flashcards
            List<FlashcardVertex> parentFlashcards = getParentFlashcards(textContent);
            
            // Add if any parent flashcard is in the deck
            boolean belongsToDeck = parentFlashcards.stream()
                    .anyMatch(f -> deckFlashcardIds.contains(f.getId()));
            
            if (belongsToDeck) {
                addContentAndAssociations(textContent, contentSet);
            }
        } else {
            // For other content types, add directly
            // (could be enhanced to check deck membership more thoroughly)
            contentSet.add(content);
        }
    }
    
    /**
     * Adds content and its associated content (like pronunciations) to the set.
     * 
     * @param content The content to add
     * @param contentSet The set to add content to
     */
    private void addContentAndAssociations(ContentVertex content, Set<AbstractVertex> contentSet) {
        contentSet.add(content);
        
        // Add pronunciations if text content
        if (content instanceof TextContentVertex) {
            TextContentVertex textContent = (TextContentVertex) content;
            for (PronunciationVertex pronunciation : textContent.getPronunciations()) {
                contentSet.add(pronunciation);
            }
        }
    }
    
    /**
     * Gets all parent flashcards for a text content.
     * 
     * @param textContent The text content
     * @return List of parent flashcards
     */
    private List<FlashcardVertex> getParentFlashcards(TextContentVertex textContent) {
        GraphTraversalSource traversalSource = textContent.getUnderlyingSource();
        
        // Get flashcards where this is the left or right content
        List<FlashcardVertex> flashcards = new ArrayList<>();
        
        // Check left content relationships
        List<FlashcardVertex> leftParents = VertexHelper.getIngoingModels(
                textContent,
                FlashcardVertex.EDGE_LEFT_CONTENT,
                FlashcardVertex::new);
        flashcards.addAll(leftParents);
        
        // Check right content relationships
        List<FlashcardVertex> rightParents = VertexHelper.getIngoingModels(
                textContent,
                FlashcardVertex.EDGE_RIGHT_CONTENT,
                FlashcardVertex::new);
        flashcards.addAll(rightParents);
        
        return flashcards;
    }
    
    /**
     * Gets the ID from a vertex, handling different vertex types.
     * 
     * @param vertex The vertex to get the ID from
     * @return The vertex ID, or null if it cannot be determined
     */
    private String getVertexId(AbstractVertex vertex) {
        if (vertex instanceof ContentVertex contentVertex) {
            return contentVertex.getId();
        } else if (vertex instanceof PronunciationVertex pronunciationVertex) {
            return pronunciationVertex.getId();
        } else if (vertex instanceof FlashcardVertex flashcardVertex) {
            return flashcardVertex.getId();
        }
        return null;
    }
}
