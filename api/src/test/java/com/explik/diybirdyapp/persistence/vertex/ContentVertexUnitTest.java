package com.explik.diybirdyapp.persistence.vertex;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the ContentVertex class.
 * Purpose: Test the external behavior of ContentVertex without regard to underlying graph.
 */
@SpringBootTest
public class ContentVertexUnitTest {
    GraphTraversalSource traversalSource = TinkerGraph.open().traversal();

    @Test
    void givenNewlyCreatedContent_whenGetId_thenReturnsNonNull() {
        var vertex = createVertex();
        assertNotNull(vertex.getId());
    }

    @Test
    void givenNewlyCreatedContent_whenSetId_thenUpdatedId() {
        var vertex = createVertex();
        vertex.setId("new-id");
        assertEquals("new-id", vertex.getId());
    }

    @Test
    void givenNewlyCreatedContent_whenIsStatic_thenReturnFalse() {
        var vertex = createVertex();
        assert(!vertex.isStatic());
    }

    @Test
    void givenDynamicContent_whenModify_thenSucceeds() {
        var vertex = createVertex();

        vertex.setId("new-id");

        assert(vertex.getId().equals("new-id"));
    }

    @Test
    void givenStaticContent_whenModify_thenThrowsException() {
        var vertex = createVertex();

        vertex.makeStatic();

        assertThrows(IllegalStateException.class, () -> {
            vertex.setId("new-id");
        });
    }

    ContentVertex createVertex() {
        Vertex graphVertex = traversalSource
                .addV("random-label")
                .property("id", "REQUIRED PROPERTY")
                .next();

        return new ContentVertex(traversalSource, graphVertex);
    }
}
