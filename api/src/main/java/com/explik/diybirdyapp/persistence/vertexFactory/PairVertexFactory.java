package com.explik.diybirdyapp.persistence.vertexFactory;

import com.explik.diybirdyapp.persistence.vertex.AbstractVertex;
import com.explik.diybirdyapp.persistence.vertex.PairVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.stereotype.Component;

@Component
public class PairVertexFactory implements VertexFactory<PairVertex, PairVertexFactory.Options> {
    @Override
    public PairVertex create(GraphTraversalSource traversalSource, Options options) {
        var vertex = PairVertex.create(traversalSource);
        vertex.setId(options.id);
        vertex.setLeftContent(options.left);
        vertex.setRightContent(options.right);

        return vertex;
    }

    public record Options(String id, AbstractVertex left, AbstractVertex right) { }
}
