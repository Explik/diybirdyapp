package com.explik.diybirdyapp.graph.repository;

import com.explik.diybirdyapp.graph.GraphHelper;
import com.explik.diybirdyapp.graph.model.FlashcardDeckModel;
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
        public TinkerGraph graph() {
            var graph = TinkerGraph.open();
            var traversal = graph.traversal();

            var lang1 = GraphHelper.addLanguage(graph, "lang1");
            var lang2 = GraphHelper.addLanguage(graph, "lang2");
            var lang3 = GraphHelper.addLanguage(graph, "lang3");

            var content1 = GraphHelper.addTextContentWithLanguage(graph, "content1", lang1);
            var content2 = GraphHelper.addTextContentWithLanguage(graph, "content2", lang2);
            var content3 = GraphHelper.addTextContentWithLanguage(graph, "content3", lang3);

            var flashcard1 = GraphHelper.addFlashcardWithTextContent(graph, "flashcard1", content1, content2);
            var flashcard2 = GraphHelper.addFlashcardWithTextContent(graph, "flashcard2", content2, content3);

            GraphHelper.addFlashcardDeckWithFlashcards(graph, "pre-existent-id", flashcard1, flashcard2);
            GraphHelper.addFlashcardDeckWithFlashcards(graph, "flashcardDeck1");
            GraphHelper.addFlashcardDeckWithFlashcards(graph, "flashcardDeck2", flashcard1);

            return graph;
        }
    }
}
