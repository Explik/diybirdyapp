package com.explik.diybirdyapp.persistence.vertex;

import org.apache.tinkerpop.gremlin.process.traversal.Text;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for the FlashcardVertex class.
 * Purpose: Test the external behavior of FlashcardVertex without regard to underlying graph.
 */
@SpringBootTest
public class FlashcardVertexUnitTest {
    GraphTraversalSource traversalSource = TinkerGraph.open().traversal();

    @Test
    void givenVertex_whenSetId_thenUpdatedId() {
        var vertex = FlashcardVertex.create(traversalSource);
        vertex.setId("new-id");
        assertEquals("new-id", vertex.getId());
    }

    @Test
    void givenVertexInDeck_whenGetDeck_thenReturnsDeck() {
        var vertex = FlashcardVertex.create(traversalSource);
        var deckVertex = FlashcardDeckVertex.create(traversalSource);
        deckVertex.setId("deck-id");
        deckVertex.addFlashcard(vertex);

        assertEquals("deck-id", vertex.getDeck().getId());
    }

    @Test
    void givenVertexInDeck_whenSetDeckOrder_thenUpdatedDeckOrder() {
        var vertex = FlashcardVertex.create(traversalSource);
        var deckVertex = FlashcardDeckVertex.create(traversalSource);
        deckVertex.setId("deck-id");
        deckVertex.addFlashcard(vertex);

        vertex.setDeckOrder(1);

        assertEquals(1, vertex.getDeckOrder());
    }

    @Test
    void givenVertex_whenSetLeftContent_thenUpdatedLeftContent() {
        var vertex = FlashcardVertex.create(traversalSource);
        var contentVertex = TextContentVertex.create(traversalSource);
        contentVertex.setId("content-id");

        vertex.setLeftContent(contentVertex);

        assertEquals("content-id", vertex.getLeftContent().getId());
    }

    @Test
    void givenVertex_whenSetRightContent_thenUpdatedRightContent() {
        var vertex = FlashcardVertex.create(traversalSource);
        var contentVertex = TextContentVertex.create(traversalSource);
        contentVertex.setId("content-id");

        vertex.setRightContent(contentVertex);

        assertEquals("content-id", vertex.getRightContent().getId());
    }

    @ParameterizedTest
    @MethodSource("provideSidePairs")
    void givenVertex_whenSetContentBySide_thenUpdatedContent(String side, String contentId) {

        var leftContentVertex = TextContentVertex.create(traversalSource);
        leftContentVertex.setId("left-id");

        var rightContentVertex = TextContentVertex.create(traversalSource);
        rightContentVertex.setId("right-id");

        var vertex = FlashcardVertex.create(traversalSource);
        vertex.setLeftContent(leftContentVertex);
        vertex.setRightContent(rightContentVertex);

        assertEquals(contentId, vertex.getSide(side).getId());
    }

    private static Stream<Arguments> provideSidePairs() {
        return Stream.of(
                Arguments.of("left", "left-id"),
                Arguments.of("front", "left-id"),
                Arguments.of("right", "right-id"),
                Arguments.of("back", "right-id")
        );
    }

    @ParameterizedTest
    @MethodSource("provideOtherSidePairs")
    void givenVertex_whenSetContentByOtherSide_thenUpdatedContent(String side, String contentId) {

        var leftContentVertex = TextContentVertex.create(traversalSource);
        leftContentVertex.setId("left-id");

        var rightContentVertex = TextContentVertex.create(traversalSource);
        rightContentVertex.setId("right-id");

        var vertex = FlashcardVertex.create(traversalSource);
        vertex.setLeftContent(leftContentVertex);
        vertex.setRightContent(rightContentVertex);

        assertEquals(contentId, vertex.getOtherSide(side).getId());
    }

    private static Stream<Arguments> provideOtherSidePairs() {
        return Stream.of(
                Arguments.of("left", "right-id"),
                Arguments.of("front", "right-id"),
                Arguments.of("right", "left-id"),
                Arguments.of("back", "left-id")
        );
    }
}
