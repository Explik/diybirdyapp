package com.explik.diybirdyapp.manager.exerciseSessionManager.LearnFlashcardDeck;

import com.explik.diybirdyapp.persistence.vertex.*;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Content Crawler - Retrieves a subset of currently relevant flashcards to use in next exercise batch.
 * 
 * The crawler takes a flashcard deck and returns a subset of flashcards and their associated content 
 * to be used in the next exercise batch. The flashcards are either selected chronologically as they 
 * appear in the deck or randomly, depending on the shuffle flashcard setting. The crawler works on 
 * limited breadth first principle, first it will select x number of flashcards, then for each flashcard 
 * it will select y number of associated notes (e.g. pronunciation, transcription, so on) to include 
 * in the exercise batch.
 */
@Component
public class FlashcardDeckContentCrawler {
    
    /**
     * Finds the first flashcard that hasn't been exercised with the given exercise type in the session.
     * 
     * @param traversalSource The graph traversal source
     * @param sessionId The session ID
     * @param exerciseTypeId The exercise type ID
     * @return The flashcard vertex, or null if no flashcard is found
     */
    public FlashcardVertex findFirstNonExercisedFlashcard(
            GraphTraversalSource traversalSource, 
            String sessionId, 
            String exerciseTypeId) {
        return FlashcardVertex.findFirstNonExercised(traversalSource, sessionId, exerciseTypeId);
    }
    
    /**
     * Collects content for the next flashcard that hasn't been added to activeContent yet.
     * Returns one flashcard and all its content and associated content per call.
     * Each vertex is returned only once - if it already exists in activeContent, it won't be returned again.
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
        
        // Find first flashcard not yet in activeContent
        FlashcardVertex targetFlashcard = null;
        
        for (FlashcardVertex flashcard : flashcardDeck.getFlashcards()) {
            if (!activeVertexIds.contains(flashcard.getId())) {
                targetFlashcard = flashcard;
                break;
            }
        }
        
        // If no new flashcard found, return empty list
        if (targetFlashcard == null) {
            return new ArrayList<>();
        }
        
        // Collect content for this flashcard, excluding duplicates
        return collectFlashcardContent(targetFlashcard, activeVertexIds);
    }
    
    /**
     * Collects all content related to a flashcard including the flashcard itself, its left/right content,
     * and associated content like pronunciations. Excludes vertices already in the active set.
     * 
     * @param flashcardVertex The flashcard to collect content for
     * @param activeVertexIds Set of vertex IDs already in activeContent
     * @return List of AbstractVertex including flashcard, content, and associated content (Pronunciation)
     */
    private List<AbstractVertex> collectFlashcardContent(
            FlashcardVertex flashcardVertex,
            Set<String> activeVertexIds) {
        
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
                        contentList.add(pronunciation);
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
                        contentList.add(pronunciation);
                    }
                }
            }
        }
        
        return contentList;
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
