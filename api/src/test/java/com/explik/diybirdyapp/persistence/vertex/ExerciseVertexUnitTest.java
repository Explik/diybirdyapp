package com.explik.diybirdyapp.persistence.vertex;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the ExerciseVertex class.
 * Purpose: Test the external behavior of ExerciseVertex without regard to underlying graph.
 */
@SpringBootTest
public class ExerciseVertexUnitTest {
    GraphTraversalSource traversalSource = TinkerGraph.open().traversal();

    @Test
    public void givenVertex_whenSetId_thenUpdatedId() {
        var vertex = ExerciseVertex.create(traversalSource);
        vertex.setId("new-id");
        assertEquals("new-id", vertex.getId());
    }

    @Test
    public void givenVertex_whenSetType_thenUpdatedType() {
        var vertex = ExerciseVertex.create(traversalSource);
        vertex.setType("new-type");
        assertEquals("new-type", vertex.getType());
    }

    @Test
    public void givenVertexWithAudioContent_whenGetContent_thenReturnsAudioContent() {
        var contentVertex = AudioContentVertex.create(traversalSource);
        contentVertex.setId("content-id");

        var vertex = ExerciseVertex.create(traversalSource);
        vertex.setContent(contentVertex);

        assertInstanceOf(AudioContentVertex.class, vertex.getContent());
        assertEquals("content-id", vertex.getContent().getId());
    }

    @Test
    public void givenVertexWithFlashcard_whenGetContent_thenReturnsFlashcardContent() {
        var contentVertex = FlashcardVertex.create(traversalSource);
        contentVertex.setId("content-id");

        var vertex = ExerciseVertex.create(traversalSource);
        vertex.setContent(contentVertex);

        assertInstanceOf(FlashcardVertex.class, vertex.getContent());
        assertEquals("content-id", vertex.getContent().getId());
    }

    @Test
    public void givenVertexWithImageContent_whenGetContent_thenReturnsImageContent() {
        var contentVertex = ImageContentVertex.create(traversalSource);
        contentVertex.setId("content-id");

        var vertex = ExerciseVertex.create(traversalSource);
        vertex.setContent(contentVertex);

        assertInstanceOf(ImageContentVertex.class, vertex.getContent());
        assertEquals("content-id", vertex.getContent().getId());
    }

    @Test
    public void givenVertexWithTextContent_whenGetContent_thenReturnsTextContent() {
        var contentVertex = TextContentVertex.create(traversalSource);
        contentVertex.setId("content-id");

        var vertex = ExerciseVertex.create(traversalSource);
        vertex.setContent(contentVertex);

        assertInstanceOf(TextContentVertex.class, vertex.getContent());
        assertEquals("content-id", vertex.getContent().getId());
    }

    @Test
    public void givenVertexWithVideoContent_whenGetContent_thenReturnsVideoContent() {
        var contentVertex = VideoContentVertex.create(traversalSource);
        contentVertex.setId("content-id");

        var vertex = ExerciseVertex.create(traversalSource);
        vertex.setContent(contentVertex);

        assertInstanceOf(VideoContentVertex.class, vertex.getContent());
        assertEquals("content-id", vertex.getContent().getId());
    }

    @Test
    public void givenNothing_whenAddOption_thenGetOptionsReturnList() {
        var exerciseVertex = ExerciseVertex.create(traversalSource);
        var contentVertex = TextContentVertex.create(traversalSource);
        contentVertex.setId("content-id");

        exerciseVertex.addOption(contentVertex);

        var options = exerciseVertex.getOptions();
        assertEquals(options.size(), 1);
        assertEquals("content-id", contentVertex.getId());
    }

    @Test
    public void givenVertexWithOption_thenRemoveOption_thenGetOptionsReturnEmptyList() {
        var exerciseVertex = ExerciseVertex.create(traversalSource);
        var contentVertex = TextContentVertex.create(traversalSource);
        exerciseVertex.addOption(contentVertex);

        exerciseVertex.removeOption(contentVertex);

        var options = exerciseVertex.getOptions();
        assertEquals(options.size(), 0);
    }

    @Test
    public void givenNothing_whenAddCorrectOption_thenGetCorrectOptionsReturnList() {
        var exerciseVertex = ExerciseVertex.create(traversalSource);
        var contentVertex = TextContentVertex.create(traversalSource);
        contentVertex.setId("content-id");

        exerciseVertex.addCorrectOption(contentVertex);

        var correctOptions = exerciseVertex.getCorrectOptions();
        assertEquals(correctOptions.size(), 1);
        assertEquals("content-id", contentVertex.getId());
    }

    @Test
    public void givenVertexWithCorrectOption_thenRemoveCorrectOption_thenGetCorrectOptionsReturnEmptyList() {
        var exerciseVertex = ExerciseVertex.create(traversalSource);
        var contentVertex = TextContentVertex.create(traversalSource);
        exerciseVertex.addCorrectOption(contentVertex);

        exerciseVertex.removeCorrectOption(contentVertex);

        var correctOptions = exerciseVertex.getCorrectOptions();
        assertEquals(correctOptions.size(), 0);
    }

    @Test
    public void givenVertexWithExerciseSession_whenGetSession_thenReturnsExerciseSession() {
        var sessionVertex = ExerciseSessionVertex.create(traversalSource);
        sessionVertex.setId("session-id");

        var vertex = ExerciseVertex.create(traversalSource);
        vertex.setSession(sessionVertex);

        assertEquals("session-id", vertex.getSession().getId());
    }
}
