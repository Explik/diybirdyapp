package com.explik.diybirdyapp.model;

import com.explik.diybirdyapp.persistence.vertex.ExerciseSessionStateVertex;
import com.explik.diybirdyapp.persistence.vertex.FlashcardDeckVertex;

/**
 * Parameter object for crawlers that operate over a flashcard deck within an active session.
 *
 * @param flashcardDeck  the deck to crawl
 * @param sessionState   the session state providing active-content context
 */
public record FlashcardDeckSessionParams(
        FlashcardDeckVertex flashcardDeck,
        ExerciseSessionStateVertex sessionState) {
}
