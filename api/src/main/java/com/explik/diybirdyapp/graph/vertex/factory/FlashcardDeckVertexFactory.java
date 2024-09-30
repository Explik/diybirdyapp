package com.explik.diybirdyapp.graph.vertex.factory;

import com.explik.diybirdyapp.graph.vertex.FlashcardDeckVertex;
import com.explik.diybirdyapp.graph.vertex.FlashcardVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FlashcardDeckVertexFactory implements VertexFactory<FlashcardDeckVertex, FlashcardDeckVertexFactory.Options> {
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

    public record Options (String id, String name, List<FlashcardVertex> flashcards) {}
}
