package com.explik.diybirdyapp.persistence.vertex;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Unit tests for the ExerciseSessionOptionsVertex class.
 * Purpose: Test the external behavior of ExerciseSessionOptionsVertex without regard to underlying graph.
 */
@SpringBootTest
public class ExerciseSessionOptionsVertexUnitTest {
    GraphTraversalSource traversalSource = TinkerGraph.open().traversal();

    @Test
    void givenVertex_whenSetId_thenUpdatedId() {
        var vertex = ExerciseSessionOptionsVertex.create(traversalSource);
        vertex.setId("new-id");
        assertEquals("new-id", vertex.getId());
    }

    @Test
    void givenNewlyCreatedVertex_whenGetFlashcardSide_thenReturnsNull() {
        var vertex = ExerciseSessionOptionsVertex.create(traversalSource);
        assertNull(vertex.getInitialFlashcardLanguageId());
    }

    @Test
    void givenVertex_whenSetFlashcardSide_thenUpdatedFlashcardSide() {
        var vertex = ExerciseSessionOptionsVertex.create(traversalSource);
        vertex.setInitialFlashcardLanguageId("new-side");
        assertEquals("new-side", vertex.getInitialFlashcardLanguageId());
    }
}
