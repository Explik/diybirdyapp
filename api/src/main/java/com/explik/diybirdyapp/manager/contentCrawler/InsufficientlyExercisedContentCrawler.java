package com.explik.diybirdyapp.manager.contentCrawler;

import com.explik.diybirdyapp.model.FlashcardDeckSessionParams;
import com.explik.diybirdyapp.persistence.vertex.*;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Stream;

/**
 * Insufficiently Exercised Content Crawler - Retrieves content that needs more practice.
 * 
 * The crawler takes a flashcard deck, identifies all practiced content, identifies any content 
 * that has not been sufficiently exercised and returns a subset of this content to be used in 
 * the next exercise batch. An item of content has not been sufficiently exercised if it has been 
 * exercised less than MAX_EXERCISES_PER_CONTENT times overall.
 */
@Component
public class InsufficientlyExercisedContentCrawler implements ContentCrawler<FlashcardDeckSessionParams> {
    
    /**
     * Collects content that has been practiced but needs more exercises (< MAX_EXERCISES_PER_CONTENT total).
     * Returns content from the flashcard deck that hasn't been added to activeContent yet.
     * 
     * @param flashcardDeck The flashcard deck to check against
     * @param sessionState The session state containing activeContent to check for duplicates
     * @return List of AbstractVertex that need more practice
     *         Returns empty list if no insufficiently exercised content is available
     */
    @Override
    public Stream<AbstractVertex> crawl(FlashcardDeckSessionParams params) {
        return collectNextFlashcardContent(params.flashcardDeck(), params.sessionState()).stream();
    }

    private List<AbstractVertex> collectNextFlashcardContent(
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
        
        // Get the session to count exercises per content
        ExerciseSessionVertex session = sessionState.getSession();
        ExerciseSessionOptionsVertex options = session.getOptions();
        
        var targetLanguage = options != null ? options.getTargetLanguage() : null;
        String targetLanguageId = targetLanguage != null ? targetLanguage.getId() : null;
        
        // Collect all flashcard content from the deck
        Set<AbstractVertex> deckContent = new HashSet<>();
        for (FlashcardVertex flashcard : flashcardDeck.getFlashcards()) {
            deckContent.add(flashcard);
            
            // Add left and right content
            if (flashcard.getLeftContent() != null) {
                addContentAndAssociations(flashcard.getLeftContent(), deckContent, targetLanguageId);
            }
            if (flashcard.getRightContent() != null) {
                addContentAndAssociations(flashcard.getRightContent(), deckContent, targetLanguageId);
            }
        }
        
        // Filter content that has been exercised but less than 5 times
        List<AbstractVertex> insufficientlyExercised = new ArrayList<>();
        
        for (AbstractVertex content : deckContent) {
            String contentId = getVertexId(content);
            if (contentId == null || activeVertexIds.contains(contentId)) {
                continue; // Skip if no ID or already in active content
            }
            
            int exerciseCount = countExercisesForContent(session, contentId);
            
            // Include if exercised at least once but less than MAX_EXERCISES_PER_CONTENT times
            if (exerciseCount > 0 && exerciseCount < ExerciseSessionStateVertex.MAX_EXERCISES_PER_CONTENT) {
                insufficientlyExercised.add(content);
            }
        }
        
        return insufficientlyExercised;
    }
    
    /**
     * Counts how many exercises have been created for a specific content vertex in this session.
     * 
     * @param session The exercise session
     * @param contentId The content ID to count exercises for
     * @return The number of exercises for this content
     */
    private int countExercisesForContent(ExerciseSessionVertex session, String contentId) {
        List<ExerciseVertex> exercises = session.getExercises();
        int count = 0;
        
        for (ExerciseVertex exercise : exercises) {
            // Check if this exercise's content matches the contentId
            ContentVertex exerciseContent = exercise.getContent();
            if (exerciseContent != null && contentId.equals(exerciseContent.getId())) {
                count++;
            }
            
            // Also check based-on content
            AbstractVertex basedOnContent = exercise.getBasedOnContent();
            if (basedOnContent instanceof ContentVertex && 
                contentId.equals(((ContentVertex) basedOnContent).getId())) {
                count++;
            }
            
            // Check if content appears in options
            List<? extends ContentVertex> options = exercise.getOptions();
            for (ContentVertex option : options) {
                if (contentId.equals(option.getId())) {
                    count++;
                    break; // Only count once per exercise
                }
            }
        }
        
        return count;
    }
    
    /**
     * Adds content and its associated content (like pronunciations) to the set.
     * Filters pronunciations by target language if specified.
     * 
     * @param content The content to add
     * @param contentSet The set to add content to
     * @param targetLanguageId Target language ID to filter pronunciations (null = all languages)
     */
    private void addContentAndAssociations(ContentVertex content, Set<AbstractVertex> contentSet, String targetLanguageId) {
        contentSet.add(content);
        
        // Add pronunciations if text content
        if (content instanceof TextContentVertex) {
            TextContentVertex textContent = (TextContentVertex) content;
            for (PronunciationVertex pronunciation : textContent.getPronunciations()) {
                // Filter by target language if specified
                if (targetLanguageId == null || matchesTargetLanguage(pronunciation, targetLanguageId)) {
                    contentSet.add(pronunciation);
                }
            }
        }
    }
    
    /**
     * Checks if a pronunciation matches the target language.
     * 
     * @param pronunciation The pronunciation vertex to check
     * @param targetLanguageId The target language ID
     * @return True if the pronunciation's text content matches the target language
     */
    private boolean matchesTargetLanguage(PronunciationVertex pronunciation, String targetLanguageId) {
        var textContent = pronunciation.getTextContent();
        if (textContent == null) {
            return false;
        }
        var language = textContent.getLanguage();
        return language != null && targetLanguageId.equals(language.getId());
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
