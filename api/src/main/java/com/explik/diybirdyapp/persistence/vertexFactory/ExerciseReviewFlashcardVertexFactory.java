package com.explik.diybirdyapp.persistence.vertexFactory;

import com.explik.diybirdyapp.ComponentTypes;
import com.explik.diybirdyapp.ExerciseTypes;
import com.explik.diybirdyapp.persistence.vertex.AbstractVertex;
import com.explik.diybirdyapp.persistence.vertex.ExerciseVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.stereotype.Component;

@Component(ExerciseTypes.REVIEW_FLASHCARD + ComponentTypes.VERTEX_FACTORY)
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
