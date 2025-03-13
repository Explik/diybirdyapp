package com.explik.diybirdyapp.persistence.vertex;

import org.apache.tinkerpop.gremlin.process.traversal.Text;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for the airVertex class.
 * Purpose: Test the external behavior of airVertex without regard to underlying graph.
 */
public class PairVertexUnitTest {
    GraphTraversalSource traversalSource = TinkerGraph.open().traversal();

    @Test
    void givenVertex_whenSetId_thenUpdatedId() {
        var vertex = PairVertex.create(traversalSource);
        vertex.setId("new-id");
        assertEquals("new-id", vertex.getId());
    }

    @Test
    void givenVertex_whenLeftContent_thenUpdatedLeftContent() {
        var vertex = PairVertex.create(traversalSource);
        var contentVertex = TextContentVertex.create(traversalSource);
        contentVertex.setId("content-id");

        vertex.setLeftContent(contentVertex);

        assertEquals("content-id", vertex.getLeftContent().getId());
    }

    @Test
    void givenVertex_whenRightContent_thenUpdatedRightContent() {
        var vertex = PairVertex.create(traversalSource);
        var contentVertex = TextContentVertex.create(traversalSource);
        contentVertex.setId("content-id");

        vertex.setRightContent(contentVertex);

        assertEquals("content-id", vertex.getRightContent().getId());
    }
}
