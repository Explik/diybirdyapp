package com.explik.diybirdyapp.graph.vertex.factory;

import com.explik.diybirdyapp.graph.vertex.AbstractVertex;
import com.explik.diybirdyapp.graph.vertex.FlashcardVertex;
import com.explik.diybirdyapp.graph.vertex.TextContentVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.stereotype.Component;

@Component
public class FlashcardVertexFactory implements VertexFactory<FlashcardVertex, FlashcardVertexFactory.Options> {
    @Override
    public FlashcardVertex create(GraphTraversalSource traversalSource, Options options) {
        var graphVertex = traversalSource.addV(FlashcardVertex.LABEL).next();
        var vertex = new FlashcardVertex(traversalSource, graphVertex);
        vertex.setId(options.id);
        vertex.setLeftContent(options.leftContent);
        vertex.setRightContent(options.rightContent);

        return vertex;
    }

    public record Options (String id, TextContentVertex leftContent, TextContentVertex rightContent) {}
}
