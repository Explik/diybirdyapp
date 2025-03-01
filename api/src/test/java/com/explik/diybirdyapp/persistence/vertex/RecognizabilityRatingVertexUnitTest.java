package com.explik.diybirdyapp.persistence.vertex;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for the RecognizabilityRatingVertex class.
 * Purpose: Test the external behavior of RecognizabilityRatingVertex without regard to underlying graph.
 */
@SpringBootTest
public class RecognizabilityRatingVertexUnitTest {
    GraphTraversalSource traversalSource = TinkerGraph.open().traversal();

    @Test
    public void givenVertex_whenSetId_thenUpdatedId() {
        var vertex = RecognizabilityRatingVertex.create(traversalSource);
        vertex.setId("new-id");
        assertEquals("new-id", vertex.getId());
    }

    @Test
    public void givenVertex_whenSetRating_thenUpdatedRating() {
        var vertex = RecognizabilityRatingVertex.create(traversalSource);
        vertex.setRating("rating-value");
        assertEquals("rating-value", vertex.getRating());
    }
}
