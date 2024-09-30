package com.explik.diybirdyapp.controller;

import com.explik.diybirdyapp.graph.model.FlashcardLanguageModel;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.explik.diybirdyapp.controller.dto.FlashcardDto;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class FlashcardControllerIntegrationTests {
    @Autowired
    FlashcardController controller;

    @Test
    void givenNothing_whenCreate_thenReturnFlashcard() {
        var flashcard = createFlashcard();

        var created = controller.create(flashcard);

        assertNotNull(created);
    }

    @Test
    void givenNewlyCreatedFlashcard_whenUpdateContent_thenReturnFlashcard() {
        var flashcard = createFlashcard();
        var created = controller.create(flashcard);
        created.setLeftLanguage(null);
        created.setLeftValue("new-left-value");
        created.setRightLanguage(null);
        created.setRightValue("new-right-value");

        var updated = controller.update(created);

        assertNotNull(updated);
        assertEquals("new-left-value", updated.getLeftValue());
        assertEquals("new-right-value", updated.getRightValue());
    }

    protected FlashcardDto createFlashcard() {
        // IMPORTANT: Relies on data from DataInitializer.addInitialFlashcardData()
        var flashcard = new FlashcardDto();
        flashcard.setId("id");
        flashcard.setDeckId("flashcardDeckVertex1");
        flashcard.setLeftValue("left-value");
        flashcard.setLeftLanguage(new FlashcardLanguageModel("langVertex1"));
        flashcard.setRightValue("right-value");
        flashcard.setRightLanguage(new FlashcardLanguageModel("langVertex2"));
        return flashcard;
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
