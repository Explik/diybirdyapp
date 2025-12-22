package com.explik.diybirdyapp.persistence.query.handler;

import com.explik.diybirdyapp.model.exercise.ExerciseSessionDto;
import com.explik.diybirdyapp.persistence.modelFactory.ExerciseSessionModelFactory;
import com.explik.diybirdyapp.persistence.query.GetExerciseSessionByIdQuery;
import com.explik.diybirdyapp.persistence.vertex.ExerciseSessionVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GetExerciseSessionByIdQueryHandler implements QueryHandler<GetExerciseSessionByIdQuery, ExerciseSessionDto> {
    @Autowired
    GraphTraversalSource traversalSource;

    @Autowired
    ExerciseSessionModelFactory sessionModelFactory;

    @Override
    public ExerciseSessionDto handle(GetExerciseSessionByIdQuery query) {
        var vertex = getSessionVertex(query.getId());
        return sessionModelFactory.create(vertex);
    }

    private ExerciseSessionVertex getSessionVertex(String modelId) {
        var sessionVertex = ExerciseSessionVertex.findById(traversalSource, modelId);
        if (sessionVertex == null)
            throw new IllegalArgumentException("No session with id " + modelId);
        return sessionVertex;
    }
}
