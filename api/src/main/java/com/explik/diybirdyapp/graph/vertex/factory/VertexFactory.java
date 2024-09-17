package com.explik.diybirdyapp.graph.vertex.factory;

import com.explik.diybirdyapp.graph.vertex.AbstractVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;

public interface VertexFactory<T extends AbstractVertex, TOptions> {
    T create(GraphTraversalSource traversalSource, TOptions options);
}
