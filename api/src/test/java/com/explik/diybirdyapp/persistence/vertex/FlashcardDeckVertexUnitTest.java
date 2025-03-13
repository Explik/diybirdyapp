package com.explik.diybirdyapp.persistence.vertex;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for the FlashcardDeckVertex class.
 * Purpose: Test the external behavior of FlashcardDeckVertex without regard to underlying graph.
 */
@SpringBootTest
public class FlashcardDeckVertexUnitTest {
    GraphTraversalSource traversalSource = TinkerGraph.open().traversal();

    @Test
    void givenVertex_whenSetId_thenUpdatedId() {
        var vertex = FlashcardDeckVertex.create(traversalSource);
        vertex.setId("new-id");
        assertEquals("new-id", vertex.getId());
    }

    @Test
    void givenVertex_whenSetName_thenUpdatedName() {
        var vertex = FlashcardDeckVertex.create(traversalSource);
        vertex.setName("new-name");
        assertEquals("new-name", vertex.getName());
    }

    @Test
    void givenVertex_whenSetDescription_thenUpdatedDescription() {
        var vertex = FlashcardDeckVertex.create(traversalSource);
        vertex.setDescription("new-description");
        assertEquals("new-description", vertex.getDescription());
    }

    @Test
    void givenVertex_whenAddFlashcard_thenFlashcardAdded() {
        var flashcardVertex = FlashcardVertex.create(traversalSource);
        flashcardVertex.setId("flashcard-id");

        var vertex = FlashcardDeckVertex.create(traversalSource);
        vertex.addFlashcard(flashcardVertex);

        assertEquals("flashcard-id", vertex.getFlashcards().get(0).getId());
    }

    @Test
    void givenVertexWithFlashcard_whenRemoveFlashcard_thenFlashcardRemoved() {
        var flashcardVertex = FlashcardVertex.create(traversalSource);
        flashcardVertex.setId("flashcard-id");

        var vertex = FlashcardDeckVertex.create(traversalSource);
        vertex.addFlashcard(flashcardVertex);

        vertex.removeFlashcard(flashcardVertex);

        assertEquals(0, vertex.getFlashcards().size());
    }

    @Test
    void givenVertexWithFlashcards_whenGetFlashcards_thenReturnsFlashcardsInCorrectOrder() {
        var flashcardVertex1 = FlashcardVertex.create(traversalSource);
        flashcardVertex1.setId("flashcard-id-1");

        var flashcardVertex2 = FlashcardVertex.create(traversalSource);
        flashcardVertex2.setId("flashcard-id-2");

        var vertex = FlashcardDeckVertex.create(traversalSource);
        vertex.addFlashcard(flashcardVertex1);
        vertex.addFlashcard(flashcardVertex2);

        // Flashcard order is guaranteed by the order in which they were added
        assertEquals(2, vertex.getFlashcards().size());
        assertEquals("flashcard-id-1", vertex.getFlashcards().get(0).getId());
        assertEquals("flashcard-id-2", vertex.getFlashcards().get(1).getId());
    }
}
