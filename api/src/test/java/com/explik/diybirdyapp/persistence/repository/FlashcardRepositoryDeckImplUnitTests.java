package com.explik.diybirdyapp.persistence.repository;

import com.explik.diybirdyapp.model.FlashcardDeckModel;
import com.explik.diybirdyapp.persistence.builder.VertexBuilderFactory;
import com.explik.diybirdyapp.persistence.vertexFactory.FlashcardDeckVertexFactory;
import com.explik.diybirdyapp.persistence.vertexFactory.FlashcardVertexFactory;
import com.explik.diybirdyapp.persistence.vertexFactory.LanguageVertexFactory;
import com.explik.diybirdyapp.persistence.vertexFactory.TextContentVertexFactory;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class FlashcardRepositoryDeckImplUnitTests {
    @Autowired
    GraphTraversalSource traversalSource;

    @Autowired
    VertexBuilderFactory builderFactory;

    @Autowired
    FlashcardDeckRepository repository;

    @BeforeEach
    void setUp() {
        traversalSource.V().drop().iterate();
    }

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
        var flashcardDeckId = "pre-existent-id";
        builderFactory.createFlashcardDeckVertexBuilder()
                .withId(flashcardDeckId)
                .build(traversalSource);

        var actual = repository.get(flashcardDeckId);

        assertEquals(flashcardDeckId, actual.getId());
    }

    @Test
    void givenNewlyCreatedFlashcardDeck_whenGetById_thenReturnFlashcard() {
        var flashcardDeck = new FlashcardDeckModel();

        var savedFlashcardDeck1 = repository.add(flashcardDeck);
        var savedFlashcardDeck2 = repository.get(savedFlashcardDeck1.getId());

        assertEquals(savedFlashcardDeck1.getId(), savedFlashcardDeck2.getId());
    }

    @Test
    void givenNewlyCreatedFlashcardDeck_whenGetAll_thenReturnListContainingDeck() {
        var flashcardDeck = new FlashcardDeckModel();
        flashcardDeck.setId("new-id");

        repository.add(flashcardDeck);
        var savedFlashcardDeck = repository.getAll()
                .stream()
                .filter(deck -> deck.getId().equals(flashcardDeck.getId()))
                .findFirst()
                .orElse(null);

        assertNotNull(savedFlashcardDeck);
    }

    @Test
    void givenNewName_whenUpdate_thenReturnFlashcard() {
        var flashcardDeckId = "flashcardDeck1";
        builderFactory.createFlashcardDeckVertexBuilder()
                .withId(flashcardDeckId)
                .withName("old-value")
                .build(traversalSource);

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
            return TinkerGraph.open().traversal();
        }
    }
}
