package com.explik.diybirdyapp.persistence.query.handler;

import com.explik.diybirdyapp.model.exercise.ExerciseSessionDto;
import com.explik.diybirdyapp.persistence.query.GetUncompletedMatchingExerciseSessionQuery;
import com.explik.diybirdyapp.persistence.query.modelFactory.ExerciseSessionModelFactory;
import com.explik.diybirdyapp.persistence.vertex.ExerciseSessionVertex;
import com.explik.diybirdyapp.persistence.vertex.FlashcardDeckVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GetUncompletedMatchingExerciseSessionQueryHandler implements QueryHandler<GetUncompletedMatchingExerciseSessionQuery, ExerciseSessionDto> {
    @Autowired
    private GraphTraversalSource traversalSource;

    @Autowired
    private ExerciseSessionModelFactory sessionModelFactory;

    @Override
    public ExerciseSessionDto handle(GetUncompletedMatchingExerciseSessionQuery query) {
        var sessionVertex = getSessionVertex(query);
        if (sessionVertex == null)
            return null;

        return sessionModelFactory.create(sessionVertex);
    }

    private ExerciseSessionVertex getSessionVertex(GetUncompletedMatchingExerciseSessionQuery query) {
        GraphTraversal<Vertex, Vertex> traversal = traversalSource.V()
                .hasLabel(ExerciseSessionVertex.LABEL)
                .has(ExerciseSessionVertex.PROPERTY_TYPE, query.getType())
                .has(ExerciseSessionVertex.PROPERTY_COMPLETED, false);

        if (query.getFlashcardDeckId() != null) {
            traversal = traversal.where(
                    __.out(ExerciseSessionVertex.EDGE_FLASHCARD_DECK)
                            .has(FlashcardDeckVertex.PROPERTY_ID, query.getFlashcardDeckId()));
        } else {
            traversal = traversal.where(__.not(__.out(ExerciseSessionVertex.EDGE_FLASHCARD_DECK)));
        }

        var vertex = traversal.tryNext().orElse(null);
        if (vertex == null)
            return null;

        return new ExerciseSessionVertex(traversalSource, vertex);
    }
}
