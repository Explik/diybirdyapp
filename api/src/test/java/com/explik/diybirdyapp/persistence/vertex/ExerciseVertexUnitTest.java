package com.explik.diybirdyapp.persistence.vertex;

import org.apache.tinkerpop.gremlin.process.traversal.TraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ExerciseVertexUnitTest {
    @Test
    public void givenNothing_whenAddOption_thenGetOptionsReturnList() {
        var traversalSource = createTraversalSource();
        var exerciseVertex = ExerciseVertex.create(traversalSource);
        var contentVertex = createContentVertex(traversalSource);

        exerciseVertex.addOption(contentVertex);

        var options = exerciseVertex.getOptions();
        assertEquals(options.size(), 1);
        assertEquals(options.getFirst().getId(), contentVertex.getId());
    }

    @Test
    public void givenNothing_whenAddCorrectOption_thenGetCorrectOptionsReturnList() {
        var traversalSource = createTraversalSource();
        var exerciseVertex = ExerciseVertex.create(traversalSource);
        var contentVertex = createContentVertex(traversalSource);

        exerciseVertex.addCorrectOption(contentVertex);

        var correctOptions = exerciseVertex.getCorrectOptions();
        assertEquals(correctOptions.size(), 1);
        assertEquals(correctOptions.getFirst().getId(), contentVertex.getId());
    }

    private ContentVertex createContentVertex(GraphTraversalSource traversalSource) {
        var contentVertex = TextContentVertex.create(traversalSource);
        contentVertex.setId("contentId");
        return contentVertex;
    }

    private GraphTraversalSource createTraversalSource() {
        return TinkerGraph.open().traversal();
    }
}
