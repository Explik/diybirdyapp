package com.explik.diybirdyapp.persistence.query.handler;

import com.explik.diybirdyapp.model.exercise.ExerciseDto;
import com.explik.diybirdyapp.persistence.ExerciseRetrievalContext;
import com.explik.diybirdyapp.persistence.ExerciseRetrievalContextProvider;
import com.explik.diybirdyapp.persistence.modelFactory.ContextualModelFactory;
import com.explik.diybirdyapp.persistence.provider.GenericProvider;
import com.explik.diybirdyapp.persistence.query.GetExerciseByIdsQuery;
import com.explik.diybirdyapp.persistence.vertex.ExerciseSessionVertex;
import com.explik.diybirdyapp.persistence.vertex.ExerciseVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GetExerciseByIdsQueryHandler implements QueryHandler<GetExerciseByIdsQuery, ExerciseDto> {
    @Autowired
    GraphTraversalSource traversalSource;

    @Autowired
    private GenericProvider<ContextualModelFactory<ExerciseVertex, ExerciseDto, ExerciseRetrievalContext>> exerciseModelFactoryProvider;

    @Override
    public ExerciseDto handle(GetExerciseByIdsQuery getExerciseByIdsQuery) {
        var exerciseId = getExerciseByIdsQuery.getId();
        var sessionId = getExerciseByIdsQuery.getSessionId();

        var vertex = ExerciseVertex.getById(traversalSource, exerciseId);
        var exerciseType = vertex.getExerciseType().getId();
        var exerciseFactory = exerciseModelFactoryProvider.get(exerciseType);

        if (sessionId == null)
            return exerciseFactory.create(vertex, ExerciseRetrievalContext.createDefault());

        var sessionVertex = ExerciseSessionVertex.findById(traversalSource, sessionId);
        if (sessionVertex == null)
            throw new IllegalArgumentException("Session with ID " + sessionId + " does not exist");

        var context = generateRetrievalContext(sessionVertex);
        return exerciseFactory.create(vertex, context);
    }

    private ExerciseRetrievalContext generateRetrievalContext(ExerciseSessionVertex sessionVertex) {
        var provider = new ExerciseRetrievalContextProvider();
        return provider.get(sessionVertex);
    }
}
