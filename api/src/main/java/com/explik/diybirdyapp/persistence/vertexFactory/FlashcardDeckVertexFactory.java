package com.explik.diybirdyapp.persistence.vertexFactory;

import com.explik.diybirdyapp.persistence.vertex.FlashcardDeckVertex;
import com.explik.diybirdyapp.persistence.vertex.FlashcardVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class FlashcardDeckVertexFactory implements ContentVertexFactory<FlashcardDeckVertex, FlashcardDeckVertexFactory.Options> {
    @Override
    public FlashcardDeckVertex create(GraphTraversalSource traversalSource, Options options) {
        var graphVertex = traversalSource.addV(FlashcardDeckVertex.LABEL).next();
        var vertex = new FlashcardDeckVertex(traversalSource, graphVertex);
        vertex.setId(options.id);
        vertex.setName(options.name);

        for(var flashcard : options.flashcards) {
            vertex.addFlashcard(flashcard);
        }

        return vertex;
    }

    @Override
    public FlashcardDeckVertex copy(FlashcardDeckVertex existingVertex) {
        var traversalSource = existingVertex.getUnderlyingSource();
        var graphVertex = traversalSource.addV(FlashcardDeckVertex.LABEL).next();
        var vertex = new FlashcardDeckVertex(traversalSource, graphVertex);
        vertex.setId(UUID.randomUUID().toString());
        vertex.setName(existingVertex.getName());

        for(var flashcard : existingVertex.getFlashcards()) {
            vertex.addFlashcard(flashcard);
        }

        return vertex;
    }

    public record Options (String id, String name, List<FlashcardVertex> flashcards) {}
}
