package com.explik.diybirdyapp.graph.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.explik.diybirdyapp.graph.GraphHelper;
import com.explik.diybirdyapp.graph.model.FlashcardModel;
import com.explik.diybirdyapp.graph.model.FlashcardLanguageModel;
import org.apache.tinkerpop.gremlin.process.traversal.TraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
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
        public GraphTraversalSource traversalSource() {
            var graph = TinkerGraph.open();
            var traversal = graph.traversal();

            var lang1 = GraphHelper.addLanguage(traversal, "lang1");
            var lang2 = GraphHelper.addLanguage(traversal, "lang2");
            var lang3 = GraphHelper.addLanguage(traversal, "lang3");

            var content1 = GraphHelper.addTextContentWithLanguage(traversal, "content1", lang1);
            var content2 = GraphHelper.addTextContentWithLanguage(traversal, "content2", lang2);
            var content3 = GraphHelper.addTextContentWithLanguage(traversal, "content3", lang3);

            var flashcard0 = GraphHelper.addFlashcardWithTextContent(traversal, "pre-existent-id", content1, content2);
            var flashcard1 = GraphHelper.addFlashcardWithTextContent(traversal, "flashcard1", content1, content2);
            var flashcard2 = GraphHelper.addFlashcardWithTextContent(traversal, "flashcard2", content2, content3);

            GraphHelper.addFlashcardDeckWithFlashcards(traversal, "flashcardDeck1", flashcard0, flashcard1, flashcard2);

            return traversal;
        }
    }
}
