package com.explik.diybirdyapp.persistence.vertexFactory;

import com.explik.diybirdyapp.ComponentTypes;
import com.explik.diybirdyapp.ExerciseTypes;
import com.explik.diybirdyapp.persistence.vertex.AbstractVertex;
import com.explik.diybirdyapp.persistence.vertex.ContentVertex;
import com.explik.diybirdyapp.persistence.vertex.ExerciseVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component(ExerciseTypes.SELECT_FLASHCARD + ComponentTypes.VERTEX_FACTORY)
public class ExerciseSelectFlashcardVertexFactory implements VertexFactory<ExerciseVertex, ExerciseSelectFlashcardVertexFactory.Options> {
    @Override
    public ExerciseVertex create(GraphTraversalSource traversalSource, Options options) {
        var graphVertex = traversalSource.addV(ExerciseVertex.LABEL).next();
        var vertex = new ExerciseVertex(traversalSource, graphVertex);
        vertex.setId(options.id);
        vertex.setType(ExerciseTypes.SELECT_FLASHCARD);
        vertex.setSession(options.sessionVertex);
        vertex.setContent(options.correctVertex);

        // Add options and make them static
        vertex.addCorrectOption(options.correctVertex);
        options.correctVertex.makeStatic();

        for(var alternativeFlashcard : options.incorrectVertices) {
            vertex.addOption(alternativeFlashcard);
            alternativeFlashcard.makeStatic();
        }

        return vertex;
    }

    public record Options (String id, AbstractVertex sessionVertex, ContentVertex correctVertex, List<? extends ContentVertex> incorrectVertices) { }
}
