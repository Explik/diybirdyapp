package com.explik.diybirdyapp.manager.contentCrawler;

import com.explik.diybirdyapp.persistence.vertex.*;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Stream;

/**
 * Content Crawler - Retrieves all content associated with a flashcard deck.
 * 
 * Collects all flashcards and their associated content (left/right content, pronunciations, etc.) 
 * from the entire deck. Used primarily for generating options in multiple choice exercises.
 */
@Component
public class FlashcardDeckContentCrawler implements ContentCrawler<FlashcardDeckVertex> {
    
    /**
     * Collects all content from all flashcards in the deck.
     *
     * @param flashcardDeck the flashcard deck to crawl
     * @return stream of all AbstractVertex including all flashcards and their associated content
     */
    @Override
    public Stream<AbstractVertex> crawl(FlashcardDeckVertex flashcardDeck) {
        Set<String> seenVertexIds = new HashSet<>();

        return flashcardDeck.getFlashcards().stream()
                .flatMap(this::streamFlashcardContent)
                .filter(vertex -> markIfUnseen(vertex, seenVertexIds));
    }

    /**
     * Collects all content related to a flashcard including the flashcard itself, its left/right content,
     * and associated content like pronunciations.
     *
     * @param flashcardVertex The flashcard to collect content for
     * @return stream of related content in deterministic order
     */
    private Stream<AbstractVertex> streamFlashcardContent(FlashcardVertex flashcardVertex) {
        return Stream.concat(
                Stream.of(flashcardVertex),
                Stream.of(flashcardVertex.getLeftContent(), flashcardVertex.getRightContent())
                        .filter(Objects::nonNull)
                        .flatMap(this::streamContentAndAssociations));
    }

    private Stream<AbstractVertex> streamContentAndAssociations(ContentVertex content) {
        if (content instanceof TextContentVertex textContent) {
            return Stream.concat(Stream.of(content), textContent.getPronunciations().stream().map(p -> (AbstractVertex) p));
        }
        return Stream.of(content);
    }

    private boolean markIfUnseen(AbstractVertex vertex, Set<String> seenVertexIds) {
        String vertexId = getVertexId(vertex);
        return vertexId != null && seenVertexIds.add(vertexId);
    }

    private String getVertexId(AbstractVertex vertex) {
        if (vertex instanceof ContentVertex contentVertex) {
            return contentVertex.getId();
        }
        if (vertex instanceof PronunciationVertex pronunciationVertex) {
            return pronunciationVertex.getId();
        }
        return null;
    }
}
