package com.explik.diybirdyapp.persistence.query.handler;

import com.explik.diybirdyapp.model.exercise.ExerciseSessionOptionsDto;
import com.explik.diybirdyapp.persistence.query.modelFactory.ModelFactory;
import com.explik.diybirdyapp.persistence.query.GetExerciseSessionConfigQuery;
import com.explik.diybirdyapp.persistence.vertex.ExerciseSessionOptionsVertex;
import com.explik.diybirdyapp.persistence.vertex.ExerciseSessionVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GetExerciseSessionConfigQueryHandler implements QueryHandler<GetExerciseSessionConfigQuery, ExerciseSessionOptionsDto> {
    @Autowired
    GraphTraversalSource traversalSource;

    @Autowired
    ModelFactory<ExerciseSessionOptionsVertex, ExerciseSessionOptionsDto> sessionOptionsModelFactory;

    @Override
    public ExerciseSessionOptionsDto handle(GetExerciseSessionConfigQuery query) {
        var sessionVertex = getSessionVertex(query.getSessionId());
        var optionsVertex = sessionVertex.getOptions();

        return sessionOptionsModelFactory.create(optionsVertex);
    }

    private ExerciseSessionVertex getSessionVertex(String modelId) {
        var sessionVertex = ExerciseSessionVertex.findById(traversalSource, modelId);
        if (sessionVertex == null)
            throw new IllegalArgumentException("No session with id " + modelId);
        return sessionVertex;
    }
}
