package com.explik.diybirdyapp.graph.vertex.factory;

import com.explik.diybirdyapp.graph.vertex.LanguageVertex;
import com.explik.diybirdyapp.graph.vertex.TextContentVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.stereotype.Component;

@Component
public class TextContentVertexFactory implements VertexFactory<TextContentVertex, TextContentVertexFactory.Options> {
    @Override
    public TextContentVertex create(GraphTraversalSource traversalSource, Options options) {
        var graphVertex = traversalSource.addV(TextContentVertex.LABEL).next();
        var vertex = new TextContentVertex(traversalSource, graphVertex);
        vertex.setId(options.id);
        vertex.setValue(options.value);
        vertex.setLanguage(options.language);

        return vertex;
    }

    public record Options (String id, String value, LanguageVertex language) {}
}
