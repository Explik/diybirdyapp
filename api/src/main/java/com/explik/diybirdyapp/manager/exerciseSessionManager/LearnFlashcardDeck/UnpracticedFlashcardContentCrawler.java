package com.explik.diybirdyapp.manager.exerciseSessionManager.LearnFlashcardDeck;

import com.explik.diybirdyapp.persistence.vertex.*;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Unpracticed Content Crawler - Retrieves unpracticed flashcard content from a deck.
 * 
 * Collects content for the next flashcard that hasn't been added to activeContent yet.
 * Supports both sequential (chronological) and shuffled flashcard selection.
 */
@Component
public class UnpracticedFlashcardContentCrawler {
    
    /**
     * Collects content for the next flashcard that hasn't been added to activeContent yet.
     * Returns one flashcard and all its content and associated content per call.
     * Each vertex is returned only once - if it already exists in activeContent, it won't be returned again.
     * 
     * The order of flashcards is determined by the shuffleFlashcards setting:
     * - If shuffleFlashcards is false (default): flashcards are returned in the order they appear in the deck
     * - If shuffleFlashcards is true: flashcards are returned in random order
     * 
     * @param flashcardDeck The flashcard deck to crawl
     * @param sessionState The session state containing activeContent to check for duplicates
     * @return List of AbstractVertex including one flashcard, its content, and associated content (Pronunciation)
     *         Returns empty list if all flashcards have been processed
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
        
        // Get the session to check shuffle setting and target language
        ExerciseSessionVertex session = sessionState.getSession();
        ExerciseSessionOptionsVertex options = session.getOptions();
        boolean shuffleFlashcards = options != null && options.getShuffleFlashcards();
        
        var targetLanguage = options != null ? options.getTargetLanguage() : null;
        String targetLanguageId = targetLanguage != null ? targetLanguage.getId() : null;
        
        // Get flashcards in appropriate order
        List<FlashcardVertex> flashcards = new ArrayList<>(flashcardDeck.getFlashcards());
        
        // Find first flashcard not yet in activeContent
        FlashcardVertex targetFlashcard = null;
        
        if (shuffleFlashcards) {
            // Shuffle mode: filter out already processed flashcards, then pick randomly
            List<FlashcardVertex> unprocessedFlashcards = flashcards.stream()
                    .filter(f -> !activeVertexIds.contains(f.getId()))
                    .toList();
            
            if (!unprocessedFlashcards.isEmpty()) {
                // Pick a random flashcard from unprocessed ones
                Random random = new Random();
                targetFlashcard = unprocessedFlashcards.get(random.nextInt(unprocessedFlashcards.size()));
            }
        } else {
            // Sequential mode: take flashcards in order
            for (FlashcardVertex flashcard : flashcards) {
                if (!activeVertexIds.contains(flashcard.getId())) {
                    targetFlashcard = flashcard;
                    break;
                }
            }
        }
        
        // If no new flashcard found, return empty list
        if (targetFlashcard == null) {
            return new ArrayList<>();
        }
        
        // Collect content for this flashcard, excluding duplicates
        return collectFlashcardContent(targetFlashcard, activeVertexIds, targetLanguageId);
    }
    
    /**
     * Collects all content related to a flashcard including the flashcard itself, its left/right content,
     * and associated content like pronunciations. Excludes vertices already in the active set.
     * Filters pronunciations by target language if specified.
     * 
     * @param flashcardVertex The flashcard to collect content for
     * @param activeVertexIds Set of vertex IDs already in activeContent
     * @param targetLanguageId Target language ID to filter pronunciations (null = all languages)
     * @return List of AbstractVertex including flashcard, content, and associated content (Pronunciation)
     */
    private List<AbstractVertex> collectFlashcardContent(
            FlashcardVertex flashcardVertex,
            Set<String> activeVertexIds,
            String targetLanguageId) {
        
        List<AbstractVertex> contentList = new ArrayList<>();
        
        // Add the flashcard itself
        if (!activeVertexIds.contains(flashcardVertex.getId())) {
            contentList.add(flashcardVertex);
        }
        
        // Add left content (if exists and not duplicate)
        ContentVertex leftContent = flashcardVertex.getLeftContent();
        if (leftContent != null && !activeVertexIds.contains(leftContent.getId())) {
            contentList.add(leftContent);
            
            // Add pronunciation vertices for text content (needed for exercise generation)
            if (leftContent instanceof TextContentVertex textContent) {
                for (PronunciationVertex pronunciation : textContent.getPronunciations()) {
                    if (!activeVertexIds.contains(pronunciation.getId())) {
                        // Only fetch associated content if it matches the session's target language
                        if (targetLanguageId != null && matchesTargetLanguage(pronunciation, targetLanguageId)) {
                            contentList.add(pronunciation);
                        }
                    }
                }
            }
        }
        
        // Add right content (if exists and not duplicate)
        ContentVertex rightContent = flashcardVertex.getRightContent();
        if (rightContent != null && !activeVertexIds.contains(rightContent.getId())) {
            contentList.add(rightContent);
            
            // Add pronunciation vertices for text content (needed for exercise generation)
            if (rightContent instanceof TextContentVertex textContent) {
                for (PronunciationVertex pronunciation : textContent.getPronunciations()) {
                    if (!activeVertexIds.contains(pronunciation.getId())) {
                        // Only fetch associated content if it matches the session's target language
                        if (targetLanguageId != null && matchesTargetLanguage(pronunciation, targetLanguageId)) {
                            contentList.add(pronunciation);
                        }
                    }
                }
            }
        }
        
        return contentList;
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
     * Gets the ID from a vertex, handling both ContentVertex and PronunciationVertex types.
     * 
     * @param vertex The vertex to get the ID from
     * @return The vertex ID, or null if it cannot be determined
     */
    private String getVertexId(AbstractVertex vertex) {
        if (vertex instanceof ContentVertex contentVertex) {
            return contentVertex.getId();
        } else if (vertex instanceof PronunciationVertex pronunciationVertex) {
            return pronunciationVertex.getId();
        }
        return null;
    }
}
