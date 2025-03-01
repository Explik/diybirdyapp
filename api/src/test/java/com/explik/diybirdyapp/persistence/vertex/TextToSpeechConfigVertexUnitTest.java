package com.explik.diybirdyapp.persistence.vertex;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for the TextToSpeechConfig class.
 * Purpose: Test the external behavior of TextToSpeechConfig without regard to underlying graph.
 */
@SpringBootTest
public class TextToSpeechConfigVertexUnitTest {
    GraphTraversalSource traversalSource = TinkerGraph.open().traversal();

    @Test
    public void givenVertex_whenSetId_thenUpdatedId() {
        var vertex = TextToSpeechConfigVertex.create(traversalSource);
        vertex.setId("new-id");
        assertEquals("new-id", vertex.getId());
    }

    @Test
    public void givenVertex_whenSetLanguageCode_thenUpdatedLanguageCode() {
        var vertex = TextToSpeechConfigVertex.create(traversalSource);
        vertex.setLanguageCode("new-language-code");
        assertEquals("new-language-code", vertex.getLanguageCode());
    }

    @Test
    public void givenVertex_whenSetVoiceName_thenUpdatedVoiceName() {
        var vertex = TextToSpeechConfigVertex.create(traversalSource);
        vertex.setVoiceName("new-voice-name");
        assertEquals("new-voice-name", vertex.getVoiceName());
    }

    @Test
    public void givenVertex_whenSetLanguage_thenUpdatedLanguage() {
        var vertex = TextToSpeechConfigVertex.create(traversalSource);
        var languageVertex = LanguageVertex.create(traversalSource);
        languageVertex.setId("language-id");

        vertex.setLanguage(languageVertex);

        assertEquals("language-id", vertex.getLanguage().getId());
    }
}
