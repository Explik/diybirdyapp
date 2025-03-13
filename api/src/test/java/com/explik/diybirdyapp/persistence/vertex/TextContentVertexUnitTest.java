package com.explik.diybirdyapp.persistence.vertex;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Unit tests for the TextContentVertex class.
 * Purpose: Test the external behavior of TextContentVertex without regard to underlying graph.
 */
@SpringBootTest
public class TextContentVertexUnitTest {
    GraphTraversalSource traversalSource = TinkerGraph.open().traversal();

    @Test
    void givenVertex_whenSetId_thenUpdatedId() {
        var vertex = TextContentVertex.create(traversalSource);
        vertex.setId("new-id");

        assertEquals("new-id", vertex.getId());
    }

    @Test
    void givenVertex_whenSetValue_thenUpdatedValue() {
        var vertex = TextContentVertex.create(traversalSource);
        vertex.setValue("new-value");

        assertEquals("new-value", vertex.getValue());
    }

    @Test
    void givenVertex_whenSetLanguage_thenUpdatedLanguage() {
        var vertex = TextContentVertex.create(traversalSource);
        var languageVertex = LanguageVertex.create(traversalSource);
        languageVertex.setId("language-id");

        vertex.setLanguage(languageVertex);

        assertEquals("language-id", vertex.getLanguage().getId());
    }

    @Test
    void givenNewlyCreatedVertex_whenGetPronunciations_thenEmptyList() {
        var vertex = TextContentVertex.create(traversalSource);

        assertEquals(0, vertex.getPronunciations().size());
    }

    @Test
    void givenVertex_whenAddPronunciation_thenAddedPronunciation() {
        var vertex = TextContentVertex.create(traversalSource);
        var pronunciationVertex = PronunciationVertex.create(traversalSource);
        pronunciationVertex.setId("pronunciation-id");

        vertex.addPronunciation(pronunciationVertex);

        assertEquals("pronunciation-id", vertex.getPronunciations().get(0).getId());
    }

    @Test
    void givenVertexWithPronunciation_whenRemovePronunciation_thenPronunciationRemoved() {
        var vertex = TextContentVertex.create(traversalSource);
        var pronunciationVertex = PronunciationVertex.create(traversalSource);
        pronunciationVertex.setId("pronunciation-id");

        vertex.addPronunciation(pronunciationVertex);
        vertex.removePronunciation(pronunciationVertex);

        assertEquals(0, vertex.getPronunciations().size());
    }

    @Test
    void givenNewlyCreatedVertex_whenHasMainPronunciation_thenFalse() {
        var vertex = TextContentVertex.create(traversalSource);

        assertFalse(vertex.hasMainPronunciation());
    }

    @Test
    void givenVertex_whenSetMainPronunciation_thenUpdatedMainPronunciation() {
        var vertex = TextContentVertex.create(traversalSource);
        var pronunciationVertex = PronunciationVertex.create(traversalSource);
        pronunciationVertex.setId("pronunciation-id");

        vertex.setMainPronunciation(pronunciationVertex);

        assertEquals("pronunciation-id", vertex.getMainPronunciation().getId());
    }
}
