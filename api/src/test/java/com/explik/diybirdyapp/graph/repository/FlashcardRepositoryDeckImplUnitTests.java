package com.explik.diybirdyapp.graph.repository;

import com.explik.diybirdyapp.graph.GraphHelper;
import com.explik.diybirdyapp.graph.model.FlashcardDeckModel;
import org.apache.tinkerpop.gremlin.process.traversal.TraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class FlashcardRepositoryDeckImplUnitTests {
    @Autowired
    FlashcardDeckRepository repository;

    @Test
    void givenNewlyCreatedFlashcardDeck_whenAdd_thenReturnFlashcardDeck() {
        var flashcardDeck = new FlashcardDeckModel();

        var savedFlashcardDeck = repository.add(flashcardDeck);

        assertNotNull(savedFlashcardDeck.getId());
        assertNotNull(savedFlashcardDeck.getName());
    }

    @Test
    void givenNonExistentFlashcardDeck_whenGetById_thenThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            repository.get("non-existent-id");
        });
    }

    @Test
    void givenPreExistentFlashcardDeck_whenGetById_thenReturnFlashcard() {
        var actual = repository.get("pre-existent-id");
        assertEquals("pre-existent-id", actual.getId());
    }

    @Test
    void givenNewlyCreatedFlashcardDeck_whenGetById_thenReturnFlashcard() {
        var flashcardDeck = new FlashcardDeckModel();

        var savedFlashcardDeck1 = repository.add(flashcardDeck);
        var savedFlashcardDeck2 = repository.get(savedFlashcardDeck1.getId());

        assertEquals(savedFlashcardDeck1.getId(), savedFlashcardDeck2.getId());
    }

    @Test
    void givenNewName_whenUpdate_thenReturnFlashcard() {
        var flashcardDeckId = "flashcardDeck1";
        var flashcardDeckChanges = new FlashcardDeckModel();
        flashcardDeckChanges.setId(flashcardDeckId);
        flashcardDeckChanges.setName("new-value");

        var savedFlashCard = repository.update(flashcardDeckChanges);

        assertEquals(flashcardDeckId, savedFlashCard.getId());
        assertEquals("new-value", savedFlashCard.getName());
    }

    @TestConfiguration
    static class Configuration {
        @Bean
        public GraphTraversalSource traversalSource() {
            var graph = TinkerGraph.open();
            var traversal = graph.traversal();

            var lang1 = GraphHelper.addLanguage(traversal, "lang1");
            var lang2 = GraphHelper.addLanguage(traversal, "lang2");
            var lang3 = GraphHelper.addLanguage(traversal, "lang3");

            var content1 = GraphHelper.addTextContentWithLanguage(traversal, "content1", lang1);
            var content2 = GraphHelper.addTextContentWithLanguage(traversal, "content2", lang2);
            var content3 = GraphHelper.addTextContentWithLanguage(traversal, "content3", lang3);

            var flashcard1 = GraphHelper.addFlashcardWithTextContent(traversal, "flashcard1", content1, content2);
            var flashcard2 = GraphHelper.addFlashcardWithTextContent(traversal, "flashcard2", content2, content3);

            GraphHelper.addFlashcardDeckWithFlashcards(traversal, "pre-existent-id", flashcard1, flashcard2);
            GraphHelper.addFlashcardDeckWithFlashcards(traversal, "flashcardDeck1");
            GraphHelper.addFlashcardDeckWithFlashcards(traversal, "flashcardDeck2", flashcard1);

            return traversal;
        }
    }
}
