package com.explik.diybirdyapp.persistence.vertex;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the VertexHelper class.
 * Purpose: Tests if all AbstractVertex methods handle the underlying graph correctly
 */
@SpringBootTest
public class VertexHelperUnitTest {
    GraphTraversalSource traversalSource = TinkerGraph.open().traversal();

    @ParameterizedTest
    @MethodSource("provideLabelClassPairsForContentVertices")
    public void givenVertex_whenCreateContent_thenReturnCorrectContent(String label, Class<?> type) {
        var graphVertex = traversalSource.addV(label).next();

        var vertex = VertexHelper.createContent(traversalSource, graphVertex);

        assertEquals(type, vertex.getClass());
    }

    @ParameterizedTest
    @MethodSource("provideLabelClassPairsForContentVertices")
    public void givenVertex_whenCreateContent_thenRecastContent(String label, Class<?> type) {
        var graphVertex = traversalSource.addV(label).next();
        var vertex = new ContentVertex(traversalSource, graphVertex);

        var recastVertex = VertexHelper.createContent(vertex);

        assertEquals(type, recastVertex.getClass());
    }

    public static Stream<Arguments> provideLabelClassPairsForContentVertices() {
        return Stream.of(
                Arguments.of(AudioContentVertex.LABEL, AudioContentVertex.class),
                Arguments.of(FlashcardVertex.LABEL, FlashcardVertex.class),
                Arguments.of(TextContentVertex.LABEL, TextContentVertex.class),
                Arguments.of(ImageContentVertex.LABEL, ImageContentVertex.class),
                Arguments.of(VideoContentVertex.LABEL, VideoContentVertex.class)
        );
    }

    @Test
    public void givenUnsupportedVertex_whenCreateContent_thenThrowException() {
        var graphVertex = traversalSource.addV("UnsupportedVertex").next();

        assertThrows(RuntimeException.class, () -> {
            VertexHelper.createContent(traversalSource, graphVertex);
        });
    }

    @Test
    public void givenVertexWithEdge_whenGetOutgoingEdge_thenReturnsEdgeVertex() {
        var graphVertex1 = traversalSource.addV("label1").next();
        var graphVertex2 = traversalSource.addV("label2").next();
        graphVertex1.addEdge("edgeLabel", graphVertex2);

        var vertex1 = new AbstractVertex(traversalSource, graphVertex1);
        var vertex2 = VertexHelper.getOutgoingModel(vertex1, "edgeLabel", AbstractVertex::new);

        assertEquals("label2", vertex2.getLabel());
    }

    @Test
    public void givenVertexWithoutEdge_whenGetOutgoingEdge_thenThrowException() {
        var graphVertex = traversalSource.addV("label").next();
        var vertex = new AbstractVertex(traversalSource, graphVertex);

        assertThrows(RuntimeException.class, () -> {
            VertexHelper.getOutgoingModel(vertex, "edgeLabel", AbstractVertex::new);
        });
    }

    @Test
    public void givenVertexWithEdge_whenGetIngoingEdge_thenReturnsEdgeVertex() {
        var graphVertex1 = traversalSource.addV("label1").next();
        var graphVertex2 = traversalSource.addV("label2").next();
        graphVertex1.addEdge("edgeLabel", graphVertex2);

        var vertex2 = new AbstractVertex(traversalSource, graphVertex2);
        var vertex1 = VertexHelper.getIngoingModel(vertex2, "edgeLabel", AbstractVertex::new);

        assertEquals("label1", vertex1.getLabel());
    }

    @Test
    public void givenVertexWithoutEdge_whenGetIngoingEdge_thenThrowException() {
        var graphVertex = traversalSource.addV("label").next();
        var vertex = new AbstractVertex(traversalSource, graphVertex);

        assertThrows(RuntimeException.class, () -> {
            VertexHelper.getIngoingModel(vertex, "edgeLabel", AbstractVertex::new);
        });
    }

    @Test
    public void givenVertexWithEdge_whenGetOptionalOutgoingEdge_thenReturnsEdgeVertex() {
        var graphVertex1 = traversalSource.addV("label1").next();
        var graphVertex2 = traversalSource.addV("label2").next();
        graphVertex1.addEdge("edgeLabel", graphVertex2);

        var vertex1 = new AbstractVertex(traversalSource, graphVertex1);
        var vertex2 = VertexHelper.getOptionalOutgoingModel(vertex1, "edgeLabel", AbstractVertex::new);

        assert vertex2 != null;
        assertEquals("label2", vertex2.getLabel());
    }

    @Test
    public void givenVertexWithoutEdge_whenGetOptionalOutgoingEdge_thenReturnsNull() {
        var graphVertex = traversalSource.addV("label").next();
        var vertex = new AbstractVertex(traversalSource, graphVertex);

        var result = VertexHelper.getOptionalOutgoingModel(vertex, "edgeLabel", AbstractVertex::new);

        assertNull(result);
    }

    @Test
    public void givenVertexWithEdge_whenGetOptionalIngoingEdge_thenReturnsEdgeVertex() {
        var graphVertex1 = traversalSource.addV("label1").next();
        var graphVertex2 = traversalSource.addV("label2").next();
        graphVertex1.addEdge("edgeLabel", graphVertex2);

        var vertex2 = new AbstractVertex(traversalSource, graphVertex2);
        var vertex1 = VertexHelper.getOptionalIngoingModel(vertex2, "edgeLabel", AbstractVertex::new);

        assert vertex1 != null;
        assertEquals("label1", vertex1.getLabel());
    }

    @Test
    public void givenVertexWithoutEdge_whenGetOptionalIngoingEdge_thenReturnsNull() {
        var graphVertex = traversalSource.addV("label").next();
        var vertex = new AbstractVertex(traversalSource, graphVertex);

        var result = VertexHelper.getOptionalIngoingModel(vertex, "edgeLabel", AbstractVertex::new);

        assertNull(result);
    }

    @Test
    public void givenVertexWithMultipleEdges_whenGetOutgoingModels_thenReturnsAllEdgeVertices() {
        var graphVertex1 = traversalSource.addV("label1").next();
        var graphVertex2 = traversalSource.addV("label2").next();
        var graphVertex3 = traversalSource.addV("label3").next();
        graphVertex1.addEdge("edgeLabel", graphVertex2);
        graphVertex1.addEdge("edgeLabel", graphVertex3);

        var vertex1 = new AbstractVertex(traversalSource, graphVertex1);
        var vertices = VertexHelper.getOutgoingModels(vertex1, "edgeLabel", AbstractVertex::new);
        var vertexLabels = vertices.stream().map(AbstractVertex::getLabel).toList();

        // getOutgoingModels does not guarantee order
        assertEquals(2, vertexLabels.size());
        assertTrue(vertexLabels.contains("label2"));
        assertTrue(vertexLabels.contains("label3"));
    }

    @Test
    public void givenVertexWithoutEdges_whenGetOutgoingModels_thenReturnsEmptyList() {
        var graphVertex = traversalSource.addV("label").next();
        var vertex = new AbstractVertex(traversalSource, graphVertex);

        var vertices = VertexHelper.getOutgoingModels(vertex, "edgeLabel", AbstractVertex::new);

        assertEquals(0, vertices.size());
    }

    @Test
    public void givenVertexWithMultipleEdges_whenGetIngoingModels_thenReturnsAllEdgeVertices() {
        var graphVertex1 = traversalSource.addV("label1").next();
        var graphVertex2 = traversalSource.addV("label2").next();
        var graphVertex3 = traversalSource.addV("label3").next();
        graphVertex2.addEdge("edgeLabel", graphVertex1);
        graphVertex3.addEdge("edgeLabel", graphVertex1);

        var vertex1 = new AbstractVertex(traversalSource, graphVertex1);
        var vertices = VertexHelper.getIngoingModels(vertex1, "edgeLabel", AbstractVertex::new);
        var vertexLabels = vertices.stream().map(AbstractVertex::getLabel).toList();

        // getIngoingModels does not guarantee order
        assertEquals(2, vertexLabels.size());
        assertTrue(vertexLabels.contains("label2"));
        assertTrue(vertexLabels.contains("label3"));
    }

    @Test
    public void givenVertexWithoutEdges_whenGetIngoingModels_thenReturnsEmptyList() {
        var graphVertex = traversalSource.addV("label").next();
        var vertex = new AbstractVertex(traversalSource, graphVertex);

        var vertices = VertexHelper.getIngoingModels(vertex, "edgeLabel", AbstractVertex::new);

        assertEquals(0, vertices.size());
    }

    @Test
    public void givenVertexWithOrderedEdges_whenGetOrderedOutgoingModels_thenReturnsOrderedList() {
        var graphVertex1 = traversalSource.addV("label1").next();
        var graphVertex2 = traversalSource.addV("label2").next();
        var graphVertex3 = traversalSource.addV("label3").next();
        graphVertex1.addEdge("edgeLabel", graphVertex2, "order", 1);
        graphVertex1.addEdge("edgeLabel", graphVertex3, "order", 2);

        var vertex1 = new AbstractVertex(traversalSource, graphVertex1);
        var vertices = VertexHelper.getOrderedOutgoingModels(vertex1, "edgeLabel", "order", AbstractVertex::new);

        // getOrderedOutgoingModels guarantees order
        assertEquals(2, vertices.size());
        assertEquals("label2", vertices.get(0).getLabel());
        assertEquals("label3", vertices.get(1).getLabel());
    }

    @Test
    public void givenVertexWithoutEdges_whenGetOrderedOutgoingModels_thenReturnsEmptyList() {
        var graphVertex = traversalSource.addV("label").next();
        var vertex = new AbstractVertex(traversalSource, graphVertex);

        var vertices = VertexHelper.getOrderedOutgoingModels(vertex, "edgeLabel", "order", AbstractVertex::new);

        assertEquals(0, vertices.size());
    }

    @Test
    public void givenVertexWithOrderedEdges_whenGetOrderedIngoingModels_thenReturnsOrderedList() {
        var graphVertex1 = traversalSource.addV("label1").next();
        var graphVertex2 = traversalSource.addV("label2").next();
        var graphVertex3 = traversalSource.addV("label3").next();
        graphVertex2.addEdge("edgeLabel", graphVertex1, "order", 1);
        graphVertex3.addEdge("edgeLabel", graphVertex1, "order", 2);

        var vertex1 = new AbstractVertex(traversalSource, graphVertex1);
        var vertices = VertexHelper.getOrderedIngoingModels(vertex1, "edgeLabel", "order", AbstractVertex::new);

        // getOrderedIngoingModels guarantees order
        assertEquals(2, vertices.size());
        assertEquals("label2", vertices.get(0).getLabel());
        assertEquals("label3", vertices.get(1).getLabel());
    }

    @Test
    public void givenVertexWithEdges_whenGetOutgoingProperty_thenReturnsEdgePropertyValue() {
        var graphVertex1 = traversalSource.addV("label1").next();
        var graphVertex2 = traversalSource.addV("label2").next();
        graphVertex1.addEdge("edgeLabel", graphVertex2, "property", "value");

        var vertex1 = new AbstractVertex(traversalSource, graphVertex1);
        var property = VertexHelper.getOutgoingProperty(vertex1, "edgeLabel", "property");

        assertEquals("value", property);
    }

    @Test
    public void givenVertexWithoutEdges_whenGetOutgoingProperty_thenThrowException() {
        var graphVertex = traversalSource.addV("label").next();
        var vertex = new AbstractVertex(traversalSource, graphVertex);

        assertThrows(RuntimeException.class, () -> {
            VertexHelper.getOutgoingProperty(vertex, "edgeLabel", "property");
        });
    }

    @Test
    public void givenVertexWithEdges_whenGetIngoingProperty_thenReturnsEdgePropertyValue() {
        var graphVertex1 = traversalSource.addV("label1").next();
        var graphVertex2 = traversalSource.addV("label2").next();
        graphVertex2.addEdge("edgeLabel", graphVertex1, "property", "value");

        var vertex1 = new AbstractVertex(traversalSource, graphVertex1);
        var property = VertexHelper.getIngoingProperty(vertex1, "edgeLabel", "property");

        assertEquals("value", property);
    }

    @Test
    public void givenVertexWithoutEdges_whenGetIngoingProperty_thenThrowException() {
        var graphVertex = traversalSource.addV("label").next();
        var vertex = new AbstractVertex(traversalSource, graphVertex);

        assertThrows(RuntimeException.class, () -> {
            VertexHelper.getIngoingProperty(vertex, "edgeLabel", "property");
        });
    }

    @Test
    public void givenVertexWithEdges_whenSetOutgoingProperty_thenUpdatedEdgePropertyValue() {
        var graphVertex1 = traversalSource.addV("label1").next();
        var graphVertex2 = traversalSource.addV("label2").next();
        graphVertex1.addEdge("edgeLabel", graphVertex2, "property", "value");

        var vertex1 = new AbstractVertex(traversalSource, graphVertex1);
        VertexHelper.setOutgoingProperty(vertex1, "edgeLabel", "property", "new-value");

        var property = VertexHelper.getOutgoingProperty(vertex1, "edgeLabel", "property");
        assertEquals("new-value", property);
    }

    @Test
    public void givenVertexWithEdges_whenSetIngoingProperty_thenUpdatedEdgePropertyValue() {
        var graphVertex1 = traversalSource.addV("label1").next();
        var graphVertex2 = traversalSource.addV("label2").next();
        graphVertex2.addEdge("edgeLabel", graphVertex1, "property", "value");

        var vertex1 = new AbstractVertex(traversalSource, graphVertex1);
        VertexHelper.setIngoingProperty(vertex1, "edgeLabel", "property", "new-value");

        var property = VertexHelper.getIngoingProperty(vertex1, "edgeLabel", "property");
        assertEquals("new-value", property);
    }
}
