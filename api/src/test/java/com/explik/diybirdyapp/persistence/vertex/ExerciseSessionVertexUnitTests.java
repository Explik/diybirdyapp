package com.explik.diybirdyapp.persistence.vertex;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the ExerciseSessionVertex class.
 * Purpose: Test the external behavior of ExerciseSessionVertex without regard to underlying graph.
 */
@SpringBootTest
public class ExerciseSessionVertexUnitTests {
    GraphTraversalSource traversalSource = TinkerGraph.open().traversal();

    @Test
    void givenVertex_whenSetId_thenUpdatedId() {
        var vertex = ExerciseSessionVertex.create(traversalSource);
        vertex.setId("new-id");
        assertEquals("new-id", vertex.getId());
    }

    @Test
    void givenVertex_whenSetType_thenUpdatedType() {
        var vertex = ExerciseSessionVertex.create(traversalSource);
        vertex.setType("new-type");
        assertEquals("new-type", vertex.getType());
    }

    @Test
    void givenNewlyCreatedVertex_whenGetCompleted_thenReturnsFalse() {
        var vertex = ExerciseSessionVertex.create(traversalSource);
        assertFalse(vertex.getCompleted());
    }

    @Test
    void givenVertex_whenSetCompleted_thenUpdatedCompleted() {
        var vertex = ExerciseSessionVertex.create(traversalSource);
        vertex.setCompleted(true);
        assertTrue(vertex.getCompleted());
    }

    @Test
    void givenNewlyCreatedVertex_whenGetCurrentExercise_thenReturnsNull() {
        var vertex = ExerciseSessionVertex.create(traversalSource);
        assertNull(vertex.getCurrentExercise());
    }

    @Test
    void givenVertexWithExercise_whenGetCurrentExercise_thenReturnsExercise() {
        var vertex = ExerciseSessionVertex.create(traversalSource);
        var exerciseVertex = ExerciseVertex.create(traversalSource);
        exerciseVertex.setId("exercise-id");
        exerciseVertex.setSession(vertex);

        assertEquals("exercise-id", vertex.getCurrentExercise().getId());
    }

    @Test
    void givenNewlyCreatedVertex_whenGetFlashcardDeck_thenReturnsNull() {
        var vertex = ExerciseSessionVertex.create(traversalSource);
        assertNull(vertex.getFlashcardDeck());
    }

    @Test
    void givenVertexWithFlashcardDeck_whenGetFlashcardDeck_thenReturnsFlashcardDeck() {
        var flashcardDeckVertex = FlashcardDeckVertex.create(traversalSource);
        flashcardDeckVertex.setId("deck-id");

        var vertex = ExerciseSessionVertex.create(traversalSource);
        vertex.setFlashcardDeck(flashcardDeckVertex);

        assertEquals("deck-id", vertex.getFlashcardDeck().getId());
    }

    @Test
    void givenNewlyCreatedVertex_whenGetOptions_thenReturnsNull() {
        var vertex = ExerciseSessionVertex.create(traversalSource);
        assertNull(vertex.getOptions());
    }

    @Test
    void givenVertexWithOptions_whenGetOptions_thenReturnsOptions() {
        var optionsVertex = ExerciseSessionOptionsVertex.create(traversalSource);
        optionsVertex.setId("options-id");

        var vertex = ExerciseSessionVertex.create(traversalSource);
        vertex.setOptions(optionsVertex);

        assertEquals("options-id", vertex.getOptions().getId());
    }
}
