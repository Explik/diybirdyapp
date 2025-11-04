package com.explik.diybirdyapp;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.springframework.context.annotation.Bean;

// Shared configurations for integration tests
public class IntegrationTestConfigurations {
    // Default, no actual persistence
    public static class Default {
        @Bean
        public GraphTraversalSource traversalSource() {
            return TinkerGraph.open().traversal();
        }
    }

}
