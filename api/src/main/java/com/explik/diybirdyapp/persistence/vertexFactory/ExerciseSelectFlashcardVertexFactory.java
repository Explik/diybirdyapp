package com.explik.diybirdyapp.persistence.vertexFactory;

import com.explik.diybirdyapp.ComponentTypes;
import com.explik.diybirdyapp.ExerciseTypes;
import com.explik.diybirdyapp.persistence.vertex.AbstractVertex;
import com.explik.diybirdyapp.persistence.vertex.ExerciseVertex;
import com.explik.diybirdyapp.persistence.vertex.FlashcardVertex;
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
        vertex.setContent(options.flashcardVertex);
        vertex.setSession(options.sessionVertex);
        vertex.setFlashcardSide(options.flashcardSide);
        for(var alternativeFlashcard : options.alternativeFlashcards)
            vertex.addOption(alternativeFlashcard);

        // Make the flashcards vertex static so it can't be changed later
        options.flashcardVertex.makeStatic();
        for(var alternativeFlashcard : options.alternativeFlashcards)
            alternativeFlashcard.makeStatic();

        return vertex;
    }

    public record Options (String id, AbstractVertex sessionVertex, FlashcardVertex flashcardVertex, List<? extends FlashcardVertex> alternativeFlashcards, String flashcardSide) { }
}
