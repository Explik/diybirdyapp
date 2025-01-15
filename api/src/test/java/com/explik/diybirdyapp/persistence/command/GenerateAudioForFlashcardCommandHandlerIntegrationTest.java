package com.explik.diybirdyapp.persistence.command;

import com.explik.diybirdyapp.persistence.vertex.FlashcardVertex;
import com.explik.diybirdyapp.persistence.vertex.TextContentVertex;
import com.explik.diybirdyapp.service.DataInitializerService;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class GenerateAudioForFlashcardCommandHandlerIntegrationTest {
    @Autowired
    GraphTraversalSource traversalSource;

    @Autowired
    DataInitializerService dataInitializer;

    // Runs before each test
    @BeforeEach
    void setUp() {
        dataInitializer.resetInitialData();
    }

    @Autowired
    private GenerateAudioForFlashcardCommandHandler handler;

    @Test
    void givenFlashcard_whenHandle_thenAttachPronunciations() {
        var command = new GenerateAudioForFlashcardCommand("flashcardVertex1");
        command.setFailOnMissingVoice(true);

        handler.handle(command);

        // Assertions
        var leftContent = (TextContentVertex)FlashcardVertex.findById(traversalSource, "flashcardVertex1").getLeftContent();
        var rightContent = (TextContentVertex)FlashcardVertex.findById(traversalSource, "flashcardVertex1").getRightContent();

        var leftPronunciationVertex = leftContent.getPronunciations().getFirst();
        var rightPronunciationVertex = rightContent.getPronunciations().getFirst();
        assertNotNull(leftPronunciationVertex);
        assertNotNull(rightPronunciationVertex);
        assertNotNull(leftPronunciationVertex.getAudioContent());
        assertNotNull(rightPronunciationVertex.getAudioContent());
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
