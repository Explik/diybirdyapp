package com.explik.diybirdyapp.persistence.vertexFactory;

import com.explik.diybirdyapp.persistence.vertex.LanguageVertex;
import com.explik.diybirdyapp.persistence.vertex.TextContentVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.stereotype.Component;

@Component
public class TextContentVertexFactory implements VertexFactory<TextContentVertex, TextContentVertexFactory.Options> {
    @Override
    public TextContentVertex create(GraphTraversalSource traversalSource, Options options) {
        var vertex = TextContentVertex.create(traversalSource);
        vertex.setId(options.id);
        vertex.setValue(options.value);
        vertex.setLanguage(options.language);

        return vertex;
    }

    public record Options (String id, String value, LanguageVertex language) {}
}
