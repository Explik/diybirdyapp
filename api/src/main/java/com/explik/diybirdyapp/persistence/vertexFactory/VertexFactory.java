package com.explik.diybirdyapp.persistence.vertexFactory;

import com.explik.diybirdyapp.persistence.vertex.AbstractVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;

public interface VertexFactory<T extends AbstractVertex, TOptions> {
    T create(GraphTraversalSource traversalSource, TOptions options);
}
