package com.explik.diybirdyapp.controller;

import com.explik.diybirdyapp.service.DataInitializerService;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class VocabularyControllerIntegrationTests {
    @Autowired
    DataInitializerService dataInitializer;

    @Autowired
    VocabularyController controller;

    @BeforeEach
    void setUp() {
        dataInitializer.resetInitialData();
    }

    @Test
    void givenNothing_whenCreate_thenReturnVocabulary() {
        var vocabulary = controller.get();

        assertNotNull(vocabulary);
        assertEquals("Bereshit", vocabulary.getWords().getFirst().getText());
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
