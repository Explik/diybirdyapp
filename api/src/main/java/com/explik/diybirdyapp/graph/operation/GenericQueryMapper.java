package com.explik.diybirdyapp.graph.operation;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;

public interface GenericQueryMapper<T> {
    String getIdentifier();

    T apply(GraphTraversalSource traversal, Vertex vertex);
}
