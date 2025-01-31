package com.explik.diybirdyapp.persistence.vertexFactory;

import com.explik.diybirdyapp.persistence.vertex.LanguageVertex;
import com.explik.diybirdyapp.persistence.vertex.TextContentVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TextContentVertexFactory implements ContentVertexFactory<TextContentVertex, TextContentVertexFactory.Options> {
    @Override
    public TextContentVertex create(GraphTraversalSource traversalSource, Options options) {
        var vertex = TextContentVertex.create(traversalSource);
        vertex.setId(options.id);
        vertex.setValue(options.value);
        vertex.setLanguage(options.language);

        return vertex;
    }

    @Override
    public TextContentVertex copy(TextContentVertex existingVertex) {
        var traversalSource = existingVertex.getUnderlyingSource();
        var vertex = TextContentVertex.create(traversalSource);
        vertex.setId(UUID.randomUUID().toString());
        vertex.setValue(existingVertex.getValue());
        vertex.setLanguage(existingVertex.getLanguage());

        return vertex;
    }

    public record Options (String id, String value, LanguageVertex language) {}
}
