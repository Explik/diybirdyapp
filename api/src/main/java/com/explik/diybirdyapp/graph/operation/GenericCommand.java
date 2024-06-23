package com.explik.diybirdyapp.graph.operation;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;

public interface GenericCommand<T> {
    String getIdentifier();

    void execute(GraphTraversalSource traversalSource, T options);
}