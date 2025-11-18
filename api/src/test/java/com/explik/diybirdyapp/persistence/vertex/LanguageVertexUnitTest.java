package com.explik.diybirdyapp.persistence.vertex;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for the LanguageVertex class.
 * Purpose: Test the external behavior of LanguageVertex without regard to underlying graph.
 */
public class LanguageVertexUnitTest {
    GraphTraversalSource traversalSource = TinkerGraph.open().traversal();

    @Test
    void givenVertex_whenSetId_thenUpdatedId() {
        var vertex = LanguageVertex.create(traversalSource);
        vertex.setId("new-id");
        assertEquals("new-id", vertex.getId());
    }

    @Test
    void givenVertex_whenSetAbbreviation_thenUpdatedAbbreviation() {
        var vertex = LanguageVertex.create(traversalSource);
        vertex.setIsoCode("new-iso-code");
        assertEquals("new-iso-code", vertex.getIsoCode());
    }

    @Test
    void givenVertex_whenSetName_thenUpdatedName() {
        var vertex = LanguageVertex.create(traversalSource);
        vertex.setName("new-name");
        assertEquals("new-name", vertex.getName());
    }
}
