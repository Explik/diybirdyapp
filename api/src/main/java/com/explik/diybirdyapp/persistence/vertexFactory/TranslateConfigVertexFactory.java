package com.explik.diybirdyapp.persistence.vertexFactory;

import com.explik.diybirdyapp.ConfigurationTypes;
import com.explik.diybirdyapp.persistence.vertex.ConfigurationVertex;
import com.explik.diybirdyapp.persistence.vertex.LanguageVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.stereotype.Component;

@Component
public class TranslateConfigVertexFactory implements VertexFactory<ConfigurationVertex, TranslateConfigVertexFactory.Options> {

    @Override
    public ConfigurationVertex create(GraphTraversalSource traversalSource, Options o) {
        var vertex = ConfigurationVertex.create(traversalSource);
        vertex.setId(o.id);
        vertex.setType(ConfigurationTypes.GOOGLE_TRANSLATE);
        vertex.setPropertyValue("languageCode", o.languageCode);
        vertex.addLanguage(o.languageVertex);

        return vertex;
    }

    public record Options(String id, String languageCode, LanguageVertex languageVertex) {
    }
}
