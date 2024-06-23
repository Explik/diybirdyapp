package com.explik.diybirdyapp.graph.operation;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;

public interface GenericQuery<T1, T2> {
    String getIdentifier();

    T2 apply(GraphTraversalSource traversal, T1 options);
}
