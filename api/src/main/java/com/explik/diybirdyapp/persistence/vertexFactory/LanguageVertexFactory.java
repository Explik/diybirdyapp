package com.explik.diybirdyapp.persistence.vertexFactory;

import com.explik.diybirdyapp.persistence.vertex.LanguageVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.stereotype.Component;

@Component
public class LanguageVertexFactory implements VertexFactory<LanguageVertex, LanguageVertexFactory.Options> {
    @Override
    public LanguageVertex create(GraphTraversalSource traversalSource, Options options) {
        var graphVertex = traversalSource.addV(LanguageVertex.LABEL).next();
        var vertex = new LanguageVertex(traversalSource, graphVertex);
        vertex.setId(options.id);
        vertex.setName(options.name);
        vertex.setIsoCode(options.isoCode);

        return vertex;
    }

    public record Options (String id, String name, String isoCode) {}
}
