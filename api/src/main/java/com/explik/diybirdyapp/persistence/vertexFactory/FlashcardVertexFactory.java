package com.explik.diybirdyapp.persistence.vertexFactory;

import com.explik.diybirdyapp.persistence.vertex.ContentVertex;
import com.explik.diybirdyapp.persistence.vertex.FlashcardVertex;
import com.explik.diybirdyapp.persistence.vertex.TextContentVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class FlashcardVertexFactory implements ContentVertexFactory<FlashcardVertex, FlashcardVertexFactory.Options> {
    @Override
    public FlashcardVertex create(GraphTraversalSource traversalSource, Options options) {
        var graphVertex = traversalSource.addV(FlashcardVertex.LABEL).next();
        var vertex = new FlashcardVertex(traversalSource, graphVertex);
        vertex.setId(options.id);
        vertex.setLeftContent(options.leftContent);
        vertex.setRightContent(options.rightContent);

        return vertex;
    }

    @Override
    public FlashcardVertex copy(FlashcardVertex existingVertex) {
        var traversalSource = existingVertex.getUnderlyingSource();
        var graphVertex = traversalSource.addV(FlashcardVertex.LABEL).next();
        var vertex = new FlashcardVertex(traversalSource, graphVertex);
        vertex.setId(UUID.randomUUID().toString());
        vertex.setLeftContent(existingVertex.getLeftContent());
        vertex.setRightContent(existingVertex.getRightContent());

        var deckVertex = existingVertex.getDeck();
        deckVertex.addFlashcard(vertex);
        deckVertex.removeFlashcard(existingVertex);

        return vertex;
    }

    public record Options (String id, ContentVertex leftContent, ContentVertex rightContent) {}
}
