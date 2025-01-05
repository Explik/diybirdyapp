package com.explik.diybirdyapp.persistence.vertexFactory;

import com.explik.diybirdyapp.persistence.vertex.LanguageVertex;
import com.explik.diybirdyapp.persistence.vertex.WordVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;

public class WordVertexFactory implements VertexFactory<WordVertex, WordVertexFactory.Options> {
    @Override
    public WordVertex create(GraphTraversalSource traversalSource, Options options) {
        var graphVertex = traversalSource.addV(WordVertex.LABEL).next();
        var vertex = new WordVertex(traversalSource, graphVertex);
        vertex.setId(options.id);
        vertex.setValue(options.value);
        vertex.setLanguage(options.languageVertex);

        return vertex;
    }

    public record Options (String id, String value, LanguageVertex languageVertex) {}
}
