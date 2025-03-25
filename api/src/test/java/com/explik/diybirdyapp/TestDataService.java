package com.explik.diybirdyapp;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TestDataService {
    @Autowired
    GraphTraversalSource traversalSource;

    public void clear() {
        traversalSource.V().drop().iterate();
    }
}
