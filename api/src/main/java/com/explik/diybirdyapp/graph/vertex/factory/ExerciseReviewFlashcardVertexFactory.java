package com.explik.diybirdyapp.graph.vertex.factory;

import com.explik.diybirdyapp.graph.vertex.AbstractVertex;
import com.explik.diybirdyapp.graph.vertex.ExerciseVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.stereotype.Component;

@Component("review-flashcard-exercise")
public class ExerciseReviewFlashcardVertexFactory implements VertexFactory<ExerciseVertex, ExerciseReviewFlashcardVertexFactory.Options> {
    @Override
    public ExerciseVertex create(GraphTraversalSource traversalSource, Options options) {
        var graphVertex = traversalSource.addV(ExerciseVertex.LABEL).next();
        var vertex = new ExerciseVertex(traversalSource, graphVertex);
        vertex.setId(options.id);
        vertex.setType("review-flashcard-exercise");
        vertex.setContent(options.flashcardVertex);
        vertex.setSession(options.sessionVertex);

        return vertex;
    }

    public record Options (String id, AbstractVertex sessionVertex, AbstractVertex flashcardVertex) { }
}
