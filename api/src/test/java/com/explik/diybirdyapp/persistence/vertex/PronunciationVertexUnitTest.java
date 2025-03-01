package com.explik.diybirdyapp.persistence.vertex;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for the PronunciationVertex class.
 * Purpose: Test the external behavior of PronunciationVertex without regard to underlying graph.
 */
@SpringBootTest
public class PronunciationVertexUnitTest {
    GraphTraversalSource traversalSource = TinkerGraph.open().traversal();

    @Test
    public void givenVertex_whenSetId_thenUpdatedId() {
        var vertex = PronunciationVertex.create(traversalSource);
        vertex.setId("new-id");
        assertEquals("new-id", vertex.getId());
    }

    @Test
    public void givenVertex_setAudioContent_thenUpdatedAudioContent() {
        var vertex = PronunciationVertex.create(traversalSource);
        var audioContentVertex = AudioContentVertex.create(traversalSource);
        audioContentVertex.setId("audio-id");

        vertex.setAudioContent(audioContentVertex);

        assertEquals("audio-id", vertex.getAudioContent().getId());
    }
}
