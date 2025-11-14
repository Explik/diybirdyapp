package com.explik.diybirdyapp.persistence.vertexFactory;

import com.explik.diybirdyapp.ConfigurationTypes;
import com.explik.diybirdyapp.persistence.vertex.ConfigurationVertex;
import com.explik.diybirdyapp.persistence.vertex.LanguageVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.stereotype.Component;

@Component
public class TextToSpeechConfigVertexFactory implements VertexFactory<ConfigurationVertex, TextToSpeechConfigVertexFactory.Options> {

    @Override
    public ConfigurationVertex create(GraphTraversalSource traversalSource, Options o) {
        var vertex = ConfigurationVertex.create(traversalSource);
        vertex.setId(o.id);
        vertex.setType(ConfigurationTypes.GOOGLE_TEXT_TO_SPEECH);
        vertex.setProperty("languageCode", o.languageCode);
        vertex.setProperty("voiceName", o.voiceName);
        vertex.addLanguage(o.languageVertex);

        return vertex;
    }

    public record Options(String id, String languageCode, String voiceName, LanguageVertex languageVertex) {
    }
}
