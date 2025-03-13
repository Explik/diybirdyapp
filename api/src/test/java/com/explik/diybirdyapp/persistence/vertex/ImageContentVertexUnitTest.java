package com.explik.diybirdyapp.persistence.vertex;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for the ImageContentVertex class.
 * Purpose: Test the external behavior of ImageContentVertex without regard to underlying graph.
 */
public class ImageContentVertexUnitTest {
    GraphTraversalSource traversalSource = TinkerGraph.open().traversal();

    @Test
    void givenVertex_whenSetId_thenUpdatedId() {
        var vertex = ImageContentVertex.create(traversalSource);
        vertex.setId("new-id");
        assertEquals("new-id", vertex.getId());
    }

    @Test
    void givenVertex_whenSetUrl_thenUpdatedUrl() {
        var vertex = ImageContentVertex.create(traversalSource);
        vertex.setUrl("new-url");
        assertEquals("new-url", vertex.getUrl());
    }
}
