package com.explik.diybirdyapp.graph.repository;

import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestGraphConfig {
    @Bean
    public TinkerGraph graph() {
        return TinkerGraph.open();
    }
}
