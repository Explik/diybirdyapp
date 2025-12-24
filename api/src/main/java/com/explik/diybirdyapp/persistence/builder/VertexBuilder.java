package com.explik.diybirdyapp.persistence.builder;

import com.explik.diybirdyapp.persistence.vertex.AbstractVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;

public interface VertexBuilder<T extends AbstractVertex> {
    T build(GraphTraversalSource traversalSource);
}
