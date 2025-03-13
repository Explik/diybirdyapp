package com.explik.diybirdyapp.persistence.vertexFactory;

import com.explik.diybirdyapp.persistence.vertex.LanguageVertex;
import com.explik.diybirdyapp.persistence.vertex.TextContentVertex;
import com.explik.diybirdyapp.persistence.vertex.WordVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.stereotype.Component;

@Component
public class WordVertexFactory implements VertexFactory<WordVertex, WordVertexFactory.Options> {
    @Override
    public WordVertex create(GraphTraversalSource traversalSource, Options options) {
        var graphVertex = traversalSource.addV(WordVertex.LABEL).next();
        var vertex = new WordVertex(traversalSource, graphVertex);
        vertex.setId(options.id);
        vertex.setValues(new String[] { options.value });
        vertex.addExample(options.mainExample);
        vertex.setTextContent(options.mainExample);
        vertex.setLanguage(options.languageVertex);

        // Make the main example vertex static so it can't be changed later
        options.mainExample.makeStatic();

        return vertex;
    }

    public record Options (String id, String value, TextContentVertex mainExample, LanguageVertex languageVertex) {}
}
