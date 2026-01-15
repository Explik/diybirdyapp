package com.explik.diybirdyapp.manager.exerciseSessionManager.LearnFlashcardDeck;

import com.explik.diybirdyapp.persistence.vertex.FlashcardVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.stereotype.Component;

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
}
