package com.explik.diybirdyapp.persistence.vertex;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for the ExerciseAnswerVertex class.
 * Purpose: Test the external behavior of ExerciseAnswerVertex without regard to underlying graph.
 */
@SpringBootTest
public class ExerciseAnswerVertexUnitTest {
    GraphTraversalSource traversalSource = TinkerGraph.open().traversal();

    @Test
    void givenVertex_whenSetId_thenUpdatedId() {
        var vertex = ExerciseAnswerVertex.create(traversalSource);
        vertex.setId("new-id");
        assertEquals("new-id", vertex.getId());
    }

    @Test
    void givenVertex_whenSetType_thenUpdatedType() {
        var vertex = ExerciseAnswerVertex.create(traversalSource);
        vertex.setType("new-type");
        assertEquals("new-type", vertex.getType());
    }

    @Test
    void givenVertex_whenSetExercise_thenUpdatedExercise() {
        var vertex = ExerciseAnswerVertex.create(traversalSource);
        var exerciseVertex = ExerciseVertex.create(traversalSource);
        exerciseVertex.setId("exercise-id");

        vertex.setExercise(exerciseVertex);

        assertEquals("exercise-id", vertex.getExercise().getId());
    }
}
