package com.explik.diybirdyapp.persistence.vertex;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for the VideoContentVertex class.
 * Purpose: Test the external behavior of VideoContentVertex without regard to underlying graph.
 */
@SpringBootTest
public class VideoContentVertexUnitTest {
    GraphTraversalSource traversalSource = TinkerGraph.open().traversal();

    @Test
    public void givenVertex_whenSetId_thenUpdatedId() {
        var vertex = VideoContentVertex.create(traversalSource);
        vertex.setId("new-id");
        assertEquals("new-id", vertex.getId());
    }

    @Test
    public void givenVertex_whenSetUrl_thenUpdatedUrl() {
        var vertex = VideoContentVertex.create(traversalSource);
        vertex.setUrl("new-url");
        assertEquals("new-url", vertex.getUrl());
    }

    @Test
    public void givenVertex_whenSetLanguage_thenUpdatedLanguage() {
        var vertex = VideoContentVertex.create(traversalSource);
        var languageVertex = LanguageVertex.create(traversalSource);
        languageVertex.setId("language-id");

        vertex.setLanguage(languageVertex);

        assertEquals("language-id", vertex.getLanguage().getId());
    }
}
