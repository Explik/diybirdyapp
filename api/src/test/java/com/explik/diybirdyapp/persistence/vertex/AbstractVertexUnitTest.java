package com.explik.diybirdyapp.persistence.vertex;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the AbstractVertex class.
 * Purpose: Tests if all AbstractVertex methods handle the underlying graph correctly
 */
@SpringBootTest
public class AbstractVertexUnitTest {
    GraphTraversalSource traversalSource = TinkerGraph.open().traversal();

    @Test
    public void givenVertex_whenGetUnderlyingSource_thenReturnSource() {
        Vertex graphVertex = traversalSource.addV("label").next();
        TestVertex vertex = new TestVertex(traversalSource, graphVertex);

        GraphTraversalSource underlyingSource = vertex.getUnderlyingSource();

        assertEquals(traversalSource, underlyingSource);
    }

    @Test
    public void givenVertex_whenGetUnderlyingVertex_thenReturnVertex() {
        Vertex graphVertex = traversalSource.addV("label").next();
        TestVertex vertex = new TestVertex(traversalSource, graphVertex);

        Vertex underlyingVertex = vertex.getUnderlyingVertex();

        assertEquals(graphVertex, underlyingVertex);
    }

    // Only works with non-in-memory databases
//    @Test
//    public void givenVertex_whenReload_thenFetchExternalChanges() {
//        String vertexId = UUID.randomUUID().toString();
//        Vertex graphVertex = traversalSource
//                .addV("label")
//                .property("id", vertexId)
//                .property("key", "old-value")
//                .next();
//        TestVertex vertex = new TestVertex(traversalSource, graphVertex);
//
//        // State 1 - No change is made and TestVertex is fully up-to-date
//        assertEquals("old-value", vertex.getProperty("key"));
//
//        // State 2 - Change is made directly on the graph and is not reflected on TextVertex
//        traversalSource.V().has("label", "id", vertexId).property("key", "new-value").iterate();
//
//        assertEquals("old-value", vertex.getProperty("key"));
//
//        // State 3 - Reload is called and the change is reflected on TextVertex
//        vertex.reload();
//
//        assertEquals("new-value", vertex.getProperty("key"));
//    }

    @Test
    public void givenVertex_whenGetLabel_thenReturnLabel() {
        String vertexLabel = "specific-label";
        Vertex graphVertex = traversalSource.addV(vertexLabel).next();
        TestVertex vertex = new TestVertex(traversalSource, graphVertex);

        String label = vertex.getLabel();

        assertEquals(vertexLabel, label);
    }

    @Test
    public void givenVertexWithProperty_whenGetProperty_thenReturnProperty() {
        Vertex graphVertex = traversalSource
                .addV("label")
                .property("key", "value")
                .next();
        TestVertex vertex = new TestVertex(traversalSource, graphVertex);

        String propertyValue = vertex.getProperty("key");

        assertEquals("value", propertyValue);
    }

    @Test
    public void givenVertexWithoutProperty_whenGetRequiredProperty_thenThrowException() {
        Vertex graphVertex = traversalSource.addV("label").next();
        TestVertex vertex = new TestVertex(traversalSource, graphVertex);

        assertThrows(IllegalStateException.class, () -> vertex.getProperty("key"));
    }

    @Test
    public void givenVertexWithoutProperty_whenGetOptionalProperty_thenReturnDefaultValue() {
        String defaultValue = "default";
        Vertex graphVertex = traversalSource.addV("label").next();
        TestVertex vertex = new TestVertex(traversalSource, graphVertex);

        String actualValue = vertex.getProperty("key", defaultValue);

        assert(actualValue.equals(defaultValue));
    }

    @Test
    public void givenVertex_whenSetProperty_thenUpdateProperty() {
        Vertex graphVertex = traversalSource.addV("label").next();
        TestVertex vertex = new TestVertex(traversalSource, graphVertex);

        vertex.setProperty("key", "value");

        String actualValue = graphVertex.property("key").value().toString();

        assertEquals("value", actualValue);
    }

    @Test
    public void givenVertexWithoutEdge_whenAddEdgeOneToOne_thenAddEdge() {
        Vertex graphVertex1 = traversalSource.addV("label").next();
        Vertex graphVertex2 = traversalSource.addV("label").next();

        TestVertex vertex1 = new TestVertex(traversalSource, graphVertex1);
        TestVertex vertex2 = new TestVertex(traversalSource, graphVertex2);

        vertex1.addEdgeOneToOne("edge", vertex2);

        assertTrue(graphVertex1.edges(Direction.OUT, "edge").hasNext());
        assertTrue(graphVertex2.edges(Direction.IN, "edge").hasNext());
    }

    @Test
    public void givenVertexWithEdge_whenAddEdgeOneToOne_thenReplaceEdge() {
        Vertex graphVertex1 = traversalSource.addV("label").next();
        Vertex graphVertex2 = traversalSource.addV("label").next();
        Vertex graphVertex3 = traversalSource.addV("label").next();

        TestVertex vertex1 = new TestVertex(traversalSource, graphVertex1);
        TestVertex vertex2 = new TestVertex(traversalSource, graphVertex2);
        TestVertex vertex3 = new TestVertex(traversalSource, graphVertex3);

        vertex1.addEdgeOneToOne("edge", vertex2);
        vertex1.addEdgeOneToOne("edge", vertex3);

        assertFalse(graphVertex2.edges(Direction.IN, "edge").hasNext());
        assertTrue(graphVertex3.edges(Direction.IN, "edge").hasNext());
    }

    @Test
    public void givenVertexWithoutEdge_whenAddEdgeOneToMany_thenAddEdge() {
        Vertex graphVertex1 = traversalSource.addV("label").next();
        Vertex graphVertex2 = traversalSource.addV("label").next();

        TestVertex vertex1 = new TestVertex(traversalSource, graphVertex1);
        TestVertex vertex2 = new TestVertex(traversalSource, graphVertex2);

        vertex1.addEdgeOneToMany("edge", vertex2);

        assertTrue(graphVertex1.edges(Direction.OUT, "edge").hasNext());
        assertTrue(graphVertex2.edges(Direction.IN, "edge").hasNext());
    }

    @Test
    public void givenVertexWithEdge_whenAddEdgeOneToMany_thenAddEdge() {
        Vertex graphVertex1 = traversalSource.addV("label").next();
        Vertex graphVertex2 = traversalSource.addV("label").next();
        Vertex graphVertex3 = traversalSource.addV("label").next();

        TestVertex vertex1 = new TestVertex(traversalSource, graphVertex1);
        TestVertex vertex2 = new TestVertex(traversalSource, graphVertex2);
        TestVertex vertex3 = new TestVertex(traversalSource, graphVertex3);

        vertex1.addEdgeOneToMany("edge", vertex2);
        vertex1.addEdgeOneToMany("edge", vertex3);

        assertTrue(graphVertex1.edges(Direction.OUT, "edge").hasNext());
        assertTrue(graphVertex2.edges(Direction.IN, "edge").hasNext());
        assertTrue(graphVertex3.edges(Direction.IN, "edge").hasNext());
    }

    @Test
    public void givenVertexWithoutEdge_whenAddOrderedEdgeOneToMany_thenAddEdge() {
        String edgeLabel = "edge";
        Vertex graphVertex1 = traversalSource.addV("label").next();
        Vertex graphVertex2 = traversalSource.addV("label").next();

        TestVertex vertex1 = new TestVertex(traversalSource, graphVertex1);
        TestVertex vertex2 = new TestVertex(traversalSource, graphVertex2);

        vertex1.addOrderedEdgeOneToMany("edge", vertex2, "order", 1);

        Edge graphEdge = graphVertex1.edges(Direction.OUT, edgeLabel).next();
        assertEquals(graphVertex2, graphEdge.inVertex());
        assertEquals(graphVertex1, graphEdge.outVertex());
        assertEquals(1, graphEdge.property("order").value());
    }

    @Test
    public void givenVertexWithEdges_whenRemoveEdge_thenRemovesEdges() {
        Vertex graphVertex1 = traversalSource.addV("label").next();
        Vertex graphVertex2 = traversalSource.addV("label").next();
        Vertex graphVertex3 = traversalSource.addV("label").next();

        Edge graphEdge1 = graphVertex1.addEdge("edge", graphVertex2);
        Edge graphEdge2 = graphVertex1.addEdge("edge", graphVertex3);

        TestVertex vertex1 = new TestVertex(traversalSource, graphVertex1);
        TestVertex vertex2 = new TestVertex(traversalSource, graphVertex2);
        TestVertex vertex3 = new TestVertex(traversalSource, graphVertex3);

        vertex1.removeEdges("edge");

        assertFalse(graphVertex1.edges(Direction.OUT, "edge").hasNext());
        assertFalse(graphVertex2.edges(Direction.IN, "edge").hasNext());
        assertFalse(graphVertex3.edges(Direction.IN, "edge").hasNext());
    }

    @Test
    public void givenVertexWithEdge_whenRemoveEdge_thenRemovesEdge() {
        Vertex graphVertex1 = traversalSource.addV("label").next();
        Vertex graphVertex2 = traversalSource.addV("label").next();
        Vertex graphVertex3 = traversalSource.addV("label").next();

        Edge graphEdge1 = graphVertex1.addEdge("edge", graphVertex2);
        Edge graphEdge2 = graphVertex1.addEdge("edge", graphVertex3);

        TestVertex vertex1 = new TestVertex(traversalSource, graphVertex1);
        TestVertex vertex2 = new TestVertex(traversalSource, graphVertex2);
        TestVertex vertex3 = new TestVertex(traversalSource, graphVertex3);

        vertex1.removeEdge("edge", vertex2);

        // Only the edge between vertex1 and vertex3 should remain
        assertTrue(graphVertex1.edges(Direction.OUT, "edge").hasNext());
        assertTrue(graphVertex3.edges(Direction.IN, "edge").hasNext());
        assertFalse(graphVertex2.edges(Direction.IN, "edge").hasNext());
    }

    // Exposes protected methods for testing
    public static class TestVertex extends AbstractVertex {
        public TestVertex(GraphTraversalSource traversalSource, Vertex vertex) {
            super(traversalSource, vertex);
        }

        public <T> T getProperty(String propertyKey) {
            return super.getProperty(propertyKey);
        }

        public void setProperty(String propertyKey, Object value) {
            super.setProperty(propertyKey, value);
        }
    }
}
