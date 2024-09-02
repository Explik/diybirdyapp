package com.explik.diybirdyapp.graph.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.explik.diybirdyapp.graph.GraphHelper;
import com.explik.diybirdyapp.graph.model.FlashcardModel;
import com.explik.diybirdyapp.graph.model.FlashcardLanguageModel;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@SpringBootTest
public class FlashcardRepositoryImplUnitTest {
    @Autowired
    FlashcardRepository repository;

    @Test
    void givenNewlyCreatedFlashcard_whenAdd_thenReturnFlashcard() {
        var flashcard = new FlashcardModel(
            null,
            "flashcardDeck1",
            "left-value",
            new FlashcardLanguageModel("lang1"),
            "right-value",
            new FlashcardLanguageModel("lang2"));

        var savedFlashcard1 = repository.add(flashcard);

        assertNotNull(savedFlashcard1.getId());
        assertNotNull(savedFlashcard1.getDeckId());
        assertEquals("lang1", savedFlashcard1.getLeftLanguage().getId());
        assertEquals("lang2", savedFlashcard1.getRightLanguage().getId());
    }

    @Test
    void givenNonExistentFlashcard_whenGetById_thenThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
           repository.get("non-existent-id");
        });
    }

    @Test
    void givenPreExistentFlashcard_whenGetById_thenReturnFlashcard() {
        var actual = repository.get("pre-existent-id");
        assertEquals("pre-existent-id", actual.getId());
    }

    @Test
    void givenNewlyCreatedFlashcard_whenGetById_thenReturnFlashcard() {
        var flashcard = new FlashcardModel(
            null,
            "flashcardDeck1",
            "left-value",
            new FlashcardLanguageModel("lang1"),
            "right-value",
            new FlashcardLanguageModel("lang2"));

        var savedFlashcard1 = repository.add(flashcard);
        var savedFlashcard2 = repository.get(savedFlashcard1.getId());

        assertEquals(savedFlashcard1.getId(), savedFlashcard2.getId());
        assertEquals("lang1", savedFlashcard2.getLeftLanguage().getId());
        assertEquals("lang2", savedFlashcard2.getRightLanguage().getId());
    }

    @Test
    void givenNewLeftValue_whenUpdate_thenReturnFlashcard() {
        var flashcardId = "flashcard1";
        var flashcardChanges = new FlashcardModel();
        flashcardChanges.setId(flashcardId);
        flashcardChanges.setLeftValue("new-value");

        var savedFlashCard = repository.update(flashcardChanges);

        assertEquals(flashcardId, savedFlashCard.getId());
        assertEquals("new-value", savedFlashCard.getLeftValue());
    }

    @Test
    void givenNewRightValue_whenUpdate_thenReturnFlashcard() {
        var flashcardId = "flashcard1";
        var flashcardChanges = new FlashcardModel();
        flashcardChanges.setId(flashcardId);
        flashcardChanges.setRightValue("new-value");

        var savedFlashCard = repository.update(flashcardChanges);

        assertEquals(flashcardId, savedFlashCard.getId());
        assertEquals("new-value", savedFlashCard.getRightValue());
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

            var flashcard0 = GraphHelper.addFlashcardWithTextContent(graph, "pre-existent-id", content1, content2);
            var flashcard1 = GraphHelper.addFlashcardWithTextContent(graph, "flashcard1", content1, content2);
            var flashcard2 = GraphHelper.addFlashcardWithTextContent(graph, "flashcard2", content2, content3);

            GraphHelper.addFlashcardDeckWithFlashcards(graph, "flashcardDeck1", flashcard0, flashcard1, flashcard2);

            return graph;
        }
    }
}
