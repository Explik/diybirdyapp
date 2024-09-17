package com.explik.diybirdyapp.graph.repository;

import com.explik.diybirdyapp.graph.model.FlashcardDeckModel;
import com.explik.diybirdyapp.graph.vertex.factory.FlashcardDeckVertexFactory;
import com.explik.diybirdyapp.graph.vertex.factory.FlashcardVertexFactory;
import com.explik.diybirdyapp.graph.vertex.factory.LanguageVertexFactory;
import com.explik.diybirdyapp.graph.vertex.factory.TextContentVertexFactory;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
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

            var flashcard1 = flashcardVertexFactory.create(traversal, new FlashcardVertexFactory.Options("flashcard1", content1, content2));
            var flashcard2 = flashcardVertexFactory.create(traversal, new FlashcardVertexFactory.Options("flashcard2", content2, content3));

            flashcardDeckVertexFactory.create(traversal, new FlashcardDeckVertexFactory.Options("pre-existent-id", "", List.of(flashcard1, flashcard2)));
            flashcardDeckVertexFactory.create(traversal, new FlashcardDeckVertexFactory.Options("flashcardDeck1", "", List.of()));
            flashcardDeckVertexFactory.create(traversal, new FlashcardDeckVertexFactory.Options("flashcardDeck2", "", List.of(flashcard1)));

            return traversal;
        }
    }
}
