package com.explik.diybirdyapp.manager.contentCrawler;

import com.explik.diybirdyapp.persistence.vertex.*;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

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
        if (flashcardDeck == null) {
            return Stream.empty();
        }

        GraphTraversalSource traversalSource = flashcardDeck.getUnderlyingSource();
        Vertex deckVertex = flashcardDeck.getUnderlyingVertex();
        if (traversalSource == null || deckVertex == null) {
            return Stream.empty();
        }

        Set<String> seenVertexIds = new HashSet<>();

        var traversal = traversalSource.V(deckVertex)
                .outE(FlashcardDeckVertex.EDGE_FLASHCARD)
                .order().by(FlashcardDeckVertex.EDGE_FLASHCARD_PROPERTY_ORDER)
                .inV()
                .hasLabel(FlashcardVertex.LABEL)
                .local(__.union(
                        __.identity(),
                        __.out(FlashcardVertex.EDGE_LEFT_CONTENT),
                        __.out(FlashcardVertex.EDGE_LEFT_CONTENT)
                                .hasLabel(TextContentVertex.LABEL)
                                .out(TextContentVertex.EDGE_PRONUNCIATION)
                                .hasLabel(PronunciationVertex.LABEL),
                        __.out(FlashcardVertex.EDGE_RIGHT_CONTENT),
                        __.out(FlashcardVertex.EDGE_RIGHT_CONTENT)
                                .hasLabel(TextContentVertex.LABEL)
                                .out(TextContentVertex.EDGE_PRONUNCIATION)
                                .hasLabel(PronunciationVertex.LABEL)
                    ));

                return StreamSupport.stream(
                        Spliterators.spliteratorUnknownSize(traversal, Spliterator.ORDERED),
                        false)
                .filter(vertex -> markIfUnseen(vertex, seenVertexIds))
                .map(vertex -> mapToVertexModel(traversalSource, vertex))
                .filter(Objects::nonNull);
    }

    private boolean markIfUnseen(Vertex vertex, Set<String> seenVertexIds) {
        String vertexId = getVertexId(vertex);
        return vertexId != null && seenVertexIds.add(vertexId);
    }

    private AbstractVertex mapToVertexModel(GraphTraversalSource traversalSource, Vertex vertex) {
        String label = vertex.label();
        if (PronunciationVertex.LABEL.equals(label)) {
            return new PronunciationVertex(traversalSource, vertex);
        }

        if (FlashcardVertex.LABEL.equals(label)
                || TextContentVertex.LABEL.equals(label)
                || AudioContentVertex.LABEL.equals(label)
                || ImageContentVertex.LABEL.equals(label)
                || VideoContentVertex.LABEL.equals(label)) {
            return VertexHelper.createContent(traversalSource, vertex);
        }

        return null;
    }

    private String getVertexId(Vertex vertex) {
        if (vertex == null || !vertex.property(ContentVertex.PROPERTY_ID).isPresent()) {
            return null;
        }

        Object idValue = vertex.property(ContentVertex.PROPERTY_ID).value();
        return idValue != null ? idValue.toString() : null;
    }
}
