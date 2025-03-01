package com.explik.diybirdyapp.persistence.vertex;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for the ExerciseFeedbackVertex class.
 * Purpose: Test the external behavior of ExerciseFeedbackVertex without regard to underlying graph.
 */
@SpringBootTest
public class ExerciseFeedbackVertexUnitTest {
    GraphTraversalSource traversalSource = TinkerGraph.open().traversal();

    @Test
    void givenVertex_whenSetId_thenUpdatedId() {
        var vertex = ExerciseFeedbackVertex.create(traversalSource);
        vertex.setId("new-id");
        assertEquals("new-id", vertex.getId());
    }

    @Test
    void givenVertex_whenSetFeedback_thenUpdatedFeedback() {
        var vertex = ExerciseFeedbackVertex.create(traversalSource);
        vertex.setType("new-type");
        assertEquals("new-type", vertex.getType());
    }

    @Test
    void givenVertex_whenSetAnswer_thenUpdatedAnswer() {
        var vertex = ExerciseFeedbackVertex.create(traversalSource);
        var answerVertex = ExerciseAnswerVertex.create(traversalSource);
        answerVertex.setId("answer-id");

        vertex.setAnswer(answerVertex);

        assertEquals("answer-id", vertex.getAnswer().getId());
    }
}
