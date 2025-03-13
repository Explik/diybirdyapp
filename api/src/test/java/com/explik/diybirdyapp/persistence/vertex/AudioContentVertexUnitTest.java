package com.explik.diybirdyapp.persistence.vertex;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for the AudioContentVertex class.
 * Purpose: Test the external behavior of AudioContentVertex without regard to underlying graph.
 */
@SpringBootTest
public class AudioContentVertexUnitTest {
    GraphTraversalSource traversalSource = TinkerGraph.open().traversal();

    @Test
    void givenAudioContent_whenSetId_thenUpdatedId() {
        var vertex = AudioContentVertex.create(traversalSource);
        vertex.setId("new-id");
        assertEquals("new-id", vertex.getId());
    }

    @Test
    void givenAudioContent_whenSetUrl_thenUpdatedUrl() {
        var vertex = AudioContentVertex.create(traversalSource);
        vertex.setUrl("new-url");
        assertEquals("new-url", vertex.getUrl());
    }

    @Test
    void givenAudioContent_whenSetLanguage_thenUpdatedLanguage() {
        var vertex = AudioContentVertex.create(traversalSource);
        var languageVertex = LanguageVertex.create(traversalSource);
        languageVertex.setId("language-id");

        vertex.setLanguage(languageVertex);

        assertEquals("language-id", vertex.getLanguage().getId());
    }
}
