package com.explik.diybirdyapp.persistence.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.explik.diybirdyapp.model.FlashcardModel;
import com.explik.diybirdyapp.model.FlashcardLanguageModel;
import com.explik.diybirdyapp.persistence.vertexFactory.FlashcardDeckVertexFactory;
import com.explik.diybirdyapp.persistence.vertexFactory.FlashcardVertexFactory;
import com.explik.diybirdyapp.persistence.vertexFactory.LanguageVertexFactory;
import com.explik.diybirdyapp.persistence.vertexFactory.TextContentVertexFactory;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.List;

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
        @Autowired
        FlashcardVertexFactory flashcardVertexFactory;

        @Autowired
        FlashcardDeckVertexFactory flashcardDeckVertexFactory;

        @Autowired
        LanguageVertexFactory languageVertexFactory;

        @Autowired
        TextContentVertexFactory textContentVertexFactory;

        @Bean
        public GraphTraversalSource traversalSource() {

            var graph = TinkerGraph.open();
            var traversal = graph.traversal();

            var lang1 = languageVertexFactory.create(traversal, new LanguageVertexFactory.Options("lang1", "", ""));
            var lang2 = languageVertexFactory.create(traversal, new LanguageVertexFactory.Options("lang2", "", ""));
            var lang3 = languageVertexFactory.create(traversal, new LanguageVertexFactory.Options("lang3", "", ""));

            var content1 = textContentVertexFactory.create(traversal, new TextContentVertexFactory.Options("content1", "", lang1));
            var content2 = textContentVertexFactory.create(traversal, new TextContentVertexFactory.Options("content2", "", lang2));
            var content3 = textContentVertexFactory.create(traversal, new TextContentVertexFactory.Options("content3", "", lang3));

            var flashard0 = flashcardVertexFactory.create(traversal, new FlashcardVertexFactory.Options("pre-existent-id", content1, content2));
            var flashcard1 = flashcardVertexFactory.create(traversal, new FlashcardVertexFactory.Options("flashcard1", content1, content2));
            var flashcard2 = flashcardVertexFactory.create(traversal, new FlashcardVertexFactory.Options("flashcard2", content2, content3));

            flashcardDeckVertexFactory.create(traversal, new FlashcardDeckVertexFactory.Options("flashcardDeck1", "", List.of(flashard0, flashcard1, flashcard2)));

            return traversal;
        }
    }
}
