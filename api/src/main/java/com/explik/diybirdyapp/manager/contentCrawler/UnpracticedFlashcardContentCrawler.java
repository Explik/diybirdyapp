package com.explik.diybirdyapp.manager.contentCrawler;

import com.explik.diybirdyapp.model.FlashcardDeckSessionParams;
import com.explik.diybirdyapp.persistence.vertex.*;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Stream;

/**
 * Unpracticed Content Crawler - Retrieves new (unpracticed) flashcards from a deck.
 *
 * Implements the specification for identifying "new" content:
 * 1. Select all flashcards from the flashcard deck.
 * 2. Subtract all flashcards already in "practiced content".
 * 3A. If shuffle = false: return flashcards ordered by deck order.
 * 3B. If shuffle = true: return flashcards in random order.
 *
 * Note: Associated content for new flashcards is pulled by InsufficientlyExercisedContentCrawler.
 */
@Component
public class UnpracticedFlashcardContentCrawler implements ContentCrawler<FlashcardDeckSessionParams> {

    private static final int ROOT_FLASHCARD_BATCH_SIZE = 3;

    @Override
    public Stream<AbstractVertex> crawl(FlashcardDeckSessionParams params) {
        var flashcardDeck = params.flashcardDeck();
        var sessionState = params.sessionState();

        // Collect IDs of already-practiced flashcards
        Set<String> practicedIds = new HashSet<>();
        for (FlashcardVertex flashcard : sessionState.getPracticedContent()) {
            String id = flashcard.getId();
            if (id != null) practicedIds.add(id);
        }

        // Determine shuffle setting from session options
        var session = sessionState.getSession();
        var options = session != null ? session.getOptions() : null;
        boolean shuffle = options != null && options.getShuffleFlashcards();

        var traversalSource = flashcardDeck.getUnderlyingSource();
        var deckVertex = flashcardDeck.getUnderlyingVertex();

        List<Vertex> rawVertices;
        if (shuffle) {
            // Get all unpracticed flashcards then shuffle in Java
            var traversal = traversalSource.V(deckVertex)
                    .out(FlashcardDeckVertex.EDGE_FLASHCARD)
                    .hasLabel(FlashcardVertex.LABEL);
            if (!practicedIds.isEmpty()) {
                traversal = traversal.has(FlashcardVertex.PROPERTY_ID, P.without(practicedIds));
            }
            rawVertices = new ArrayList<>(traversal.toList());
            Collections.shuffle(rawVertices);
            if (rawVertices.size() > ROOT_FLASHCARD_BATCH_SIZE) {
                rawVertices = rawVertices.subList(0, ROOT_FLASHCARD_BATCH_SIZE);
            }
        } else {
            // Get unpracticed flashcards ordered by deck order
            var traversal = traversalSource.V(deckVertex)
                    .outE(FlashcardDeckVertex.EDGE_FLASHCARD)
                    .order().by(FlashcardDeckVertex.EDGE_FLASHCARD_PROPERTY_ORDER)
                    .inV()
                    .hasLabel(FlashcardVertex.LABEL);
            if (!practicedIds.isEmpty()) {
                traversal = traversal.has(FlashcardVertex.PROPERTY_ID, P.without(practicedIds));
            }
            rawVertices = traversal.limit(ROOT_FLASHCARD_BATCH_SIZE).toList();
        }

        return rawVertices.stream()
                .map(v -> (AbstractVertex) new FlashcardVertex(traversalSource, v));
    }
}