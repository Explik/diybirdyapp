package com.explik.diybirdyapp.persistence.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.explik.diybirdyapp.model.FlashcardModel;
import com.explik.diybirdyapp.model.FlashcardLanguageModel;
import com.explik.diybirdyapp.persistence.builder.VertexBuilderFactory;
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
    GraphTraversalSource traversalSource;

    @Autowired
    VertexBuilderFactory builderFactory;

    @Autowired
    FlashcardRepository repository;

    @Test
    void givenNewlyCreatedFlashcard_whenAdd_thenReturnFlashcard() {
        var languageId1 = "lang1";
        var languageId2 = "lang2";
        builderFactory.createLanguageVertexBuilder()
                .withId(languageId1)
                .build(traversalSource);
        builderFactory.createLanguageVertexBuilder()
                .withId(languageId2)
                .build(traversalSource);

        var flashcard = new FlashcardModel(
            null,
            "flashcardDeck1",
            "left-value",
            new FlashcardLanguageModel(languageId1),
            "right-value",
            new FlashcardLanguageModel(languageId2));

        var savedFlashcard1 = repository.add(flashcard);

        assertNotNull(savedFlashcard1.getId());
        assertNotNull(savedFlashcard1.getDeckId());
        assertEquals(languageId1, savedFlashcard1.getLeftLanguage().getId());
        assertEquals(languageId2, savedFlashcard1.getRightLanguage().getId());
    }

    @Test
    void givenNonExistentFlashcard_whenGetById_thenThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
           repository.get("non-existent-id");
        });
    }

    @Test
    void givenPreExistentFlashcard_whenGetById_thenReturnFlashcard() {
        var flashcardId = "pre-existent-id";
        builderFactory.createFlashcardVertexBuilder()
                .withId(flashcardId)
                .build(traversalSource);

        var actual = repository.get(flashcardId);
        assertEquals(flashcardId, actual.getId());
    }

    @Test
    void givenNewlyCreatedFlashcard_whenGetById_thenReturnFlashcard() {
        var languageId1 = "lang1";
        var languageId2 = "lang2";
        builderFactory.createLanguageVertexBuilder()
                .withId(languageId1)
                .build(traversalSource);
        builderFactory.createLanguageVertexBuilder()
                .withId(languageId2)
                .build(traversalSource);

        var flashcard = new FlashcardModel(
            null,
            "flashcardDeck1",
            "left-value",
            new FlashcardLanguageModel(languageId1),
            "right-value",
            new FlashcardLanguageModel(languageId2));

        var savedFlashcard1 = repository.add(flashcard);
        var savedFlashcard2 = repository.get(savedFlashcard1.getId());

        assertEquals(savedFlashcard1.getId(), savedFlashcard2.getId());
        assertEquals(languageId1, savedFlashcard2.getLeftLanguage().getId());
        assertEquals(languageId2, savedFlashcard2.getRightLanguage().getId());
    }

    @Test
    void givenNewLeftValue_whenUpdate_thenReturnFlashcard() {
        var flashcardId = "flashcard1";
        builderFactory.createFlashcardVertexBuilder()
                .withId(flashcardId)
                .withFrontText("old-value")
                .build(traversalSource);

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
        builderFactory.createFlashcardVertexBuilder()
                .withId(flashcardId)
                .withBackText("old-value")
                .build(traversalSource);

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
            return TinkerGraph.open().traversal();
        }
    }
}
