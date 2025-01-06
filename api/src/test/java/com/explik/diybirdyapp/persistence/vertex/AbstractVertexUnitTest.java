package com.explik.diybirdyapp.persistence.vertex;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;

@SpringBootTest
public class AbstractVertexUnitTest {
    GraphTraversalSource traversalSource = TinkerGraph.open().traversal();

    @Test
    void givenVertex_whenSetProperty_thenUpdateProperty() {
        TestVertex vertex = createVertex();

        vertex.setProperty("key", "value");

        assert(vertex.getProperty("key").equals("value"));
    }

    TestVertex createVertex() {
        return new TestVertex(
                traversalSource,
                traversalSource.addV("random-label").next());
    }

    static class TestVertex extends AbstractVertex {
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
