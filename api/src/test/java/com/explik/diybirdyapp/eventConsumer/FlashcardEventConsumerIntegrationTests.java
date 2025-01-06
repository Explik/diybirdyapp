package com.explik.diybirdyapp.eventConsumer;

import com.explik.diybirdyapp.service.DataInitializerService;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@SpringBootTest
public class FlashcardEventConsumerIntegrationTests {
    @Autowired
    DataInitializerService dataInitializer;

    @Autowired
    FlashcardEventConsumer consumer;

    // Runs before each test
    @BeforeEach
    void setUp() {
        dataInitializer.resetInitialData();
    }

    @Test
    void givenFlashcard_whenConsumeFlashcardAddedEvent_thenCreateWords() {



    }

    @TestConfiguration
    static class Configuration {
        @Bean
        public GraphTraversalSource traversalSource() {
            var graph = TinkerGraph.open();
            return graph.traversal();
        }
    }
}
