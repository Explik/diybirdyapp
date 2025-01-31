package com.explik.diybirdyapp.persistence.command;

import com.explik.diybirdyapp.TestDataConstants;
import com.explik.diybirdyapp.TestDataProvider;
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
    TestDataProvider dataProvider;

    // Runs before each test
    @BeforeEach
    void setUp() {
        dataProvider.resetData();
    }

    @Autowired
    private GenerateAudioForFlashcardCommandHandler handler;

    @Test
    void givenFlashcard_whenHandle_thenAttachPronunciations() {
        var flashcardId = TestDataConstants.Flashcard.Id;

        var command = new GenerateAudioForFlashcardCommand(flashcardId);
        command.setFailOnMissingVoice(true);

        handler.handle(command);

        // Assertions
        var leftContent = (TextContentVertex)FlashcardVertex.findById(traversalSource, flashcardId).getLeftContent();
        var rightContent = (TextContentVertex)FlashcardVertex.findById(traversalSource, flashcardId).getRightContent();

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
