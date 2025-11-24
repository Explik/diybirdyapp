package com.explik.diybirdyapp.persistence.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.explik.diybirdyapp.persistence.builder.VertexBuilderFactory;
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
    GraphTraversalSource traversalSource;

    @Autowired
    VertexBuilderFactory builderFactory;

    @Autowired
    FlashcardRepository repository;

    @Test
    void givenNewlyCreatedFlashcard_whenAdd_thenReturnFlashcard() {
        var languageId1 = "lang1";
        var languageId2 = "lang2";

        var flashcardFront = new FlashcardContentTextDto();
        flashcardFront.setLanguageId(languageId1);
        flashcardFront.setText("left-value");

        var flashcardBack = new FlashcardContentTextDto();
        flashcardBack.setLanguageId(languageId2);
        flashcardBack.setText("right-value");

        var flashcard = new FlashcardDto(
                "flashcard1",
                "flashcardDeck1",
                flashcardFront,
                flashcardBack);

        var savedFlashcard = repository.add(flashcard);
        var savedFlashcardFrontContent = (FlashcardContentTextDto)savedFlashcard.getFrontContent();
        var savedFlashcardBackContent = (FlashcardContentTextDto)savedFlashcard.getBackContent();

        assertNotNull(savedFlashcard.getId());
        assertNotNull(savedFlashcard.getDeckId());
        assertEquals(languageId1, savedFlashcardFrontContent.getLanguageId());
        assertEquals(languageId2, savedFlashcardBackContent.getLanguageId());
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

        var flashcardFront = new FlashcardContentTextDto();
        flashcardFront.setLanguageId(languageId1);
        flashcardFront.setText("left-value");

        var flashcardBack = new FlashcardContentTextDto();
        flashcardBack.setLanguageId(languageId2);
        flashcardBack.setText("right-value");

        var flashcard = new FlashcardDto(
                "flashcard1",
                "flashcardDeck1",
                flashcardFront,
                flashcardBack);

        var savedFlashcardId = repository.add(flashcard).getId();
        var savedFlashcard = repository.get(savedFlashcardId);
        var savedFlashcardFrontContent = (FlashcardContentTextDto)savedFlashcard.getFrontContent();
        var savedFlashcardBackContent = (FlashcardContentTextDto)savedFlashcard.getBackContent();

        assertEquals(savedFlashcardId, savedFlashcard.getId());
        assertEquals(languageId1, savedFlashcardFrontContent.getLanguageId());
        assertEquals(languageId2, savedFlashcardBackContent.getLanguageId());
    }

    @Test
    void givenNewLeftTextValue_whenUpdate_thenReturnFlashcard() {
        var flashcardId = "flashcard1";
        builderFactory.createFlashcardVertexBuilder()
                .withId(flashcardId)
                .withFrontText("old-value")
                .build(traversalSource);

        var textChanges = new FlashcardContentTextDto();
        textChanges.setText("new-value");

        var flashcardChanges = new FlashcardDto();
        flashcardChanges.setId(flashcardId);
        flashcardChanges.setFrontContent(textChanges);

        var savedFlashCard = repository.update(flashcardChanges);
        var savedFlashcardFrontContent = (FlashcardContentTextDto)savedFlashCard.getFrontContent();

        assertEquals(flashcardId, savedFlashCard.getId());
        assertEquals("new-value", savedFlashcardFrontContent.getText());
    }

    @Test
    void givenNewRightTextValue_whenUpdate_thenReturnFlashcard() {
        var flashcardId = "flashcard1";
        builderFactory.createFlashcardVertexBuilder()
                .withId(flashcardId)
                .withBackText("old-value")
                .build(traversalSource);

        var textChanges = new FlashcardContentTextDto();
        textChanges.setText("new-value");

        var flashcardChanges = new FlashcardDto();
        flashcardChanges.setId(flashcardId);
        flashcardChanges.setFrontContent(textChanges);

        var savedFlashCard = repository.update(flashcardChanges);
        var savedFlashcardBackContent = (FlashcardContentTextDto)savedFlashCard.getBackContent();

        assertEquals(flashcardId, savedFlashCard.getId());
        assertEquals("new-value", savedFlashcardBackContent.getText());
    }

    // TODO Add test for updating languageId on text content
    // TODO Add test for updating audio content/upload
    // TODO Add test for updating image content/upload
    // TODO Add test for updating video content/upload

    @TestConfiguration
    static class Configuration {
        @Bean
        public GraphTraversalSource traversalSource() {
            return TinkerGraph.open().traversal();
        }
    }
}
