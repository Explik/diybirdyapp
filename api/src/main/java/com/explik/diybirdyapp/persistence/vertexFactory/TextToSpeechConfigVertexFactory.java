package com.explik.diybirdyapp.persistence.vertexFactory;

import com.explik.diybirdyapp.persistence.vertex.LanguageVertex;
import com.explik.diybirdyapp.persistence.vertex.TextToSpeechConfigVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.stereotype.Component;

@Component
public class TextToSpeechConfigVertexFactory implements VertexFactory<TextToSpeechConfigVertex, TextToSpeechConfigVertexFactory.Options> {

    @Override
    public TextToSpeechConfigVertex create(GraphTraversalSource traversalSource, Options o) {
        var vertex = TextToSpeechConfigVertex.create(traversalSource);
        vertex.setId(o.id);
        vertex.setLanguageCode(o.languageCode);
        vertex.setVoiceName(o.voiceName);
        vertex.setLanguage(o.languageVertex);

        return vertex;
    }

    public record Options(String id, String languageCode, String voiceName, LanguageVertex languageVertex) {
    }
}
