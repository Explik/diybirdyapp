package com.explik.diybirdyapp.service;

import com.explik.diybirdyapp.model.exercise.ExerciseSessionDto;
import com.explik.diybirdyapp.model.exercise.ExerciseSessionOptionsDto;
import com.explik.diybirdyapp.persistence.query.modelFactory.ExerciseSessionModelFactory;
import com.explik.diybirdyapp.manager.exerciseSessionManager.ExerciseCreationContext;
import com.explik.diybirdyapp.manager.exerciseSessionManager.ExerciseSessionManager;
import com.explik.diybirdyapp.persistence.provider.GenericProvider;
import com.explik.diybirdyapp.persistence.query.GetExerciseSessionByIdQuery;
import com.explik.diybirdyapp.persistence.query.GetExerciseSessionConfigQuery;
import com.explik.diybirdyapp.persistence.query.handler.QueryHandler;
import com.explik.diybirdyapp.persistence.vertex.ExerciseSessionVertex;
import com.explik.diybirdyapp.service.helper.ExerciseSessionConfigHelper;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ExerciseSessionService {
    @Autowired
    private GraphTraversalSource traversalSource;

    @Autowired
    private QueryHandler<GetExerciseSessionByIdQuery, ExerciseSessionDto> getExerciseSessionByIdQueryHandler;

    @Autowired
    private QueryHandler<GetExerciseSessionConfigQuery, ExerciseSessionOptionsDto> getExerciseSessionConfigQueryHandler;

    @Autowired
    private GenericProvider<ExerciseSessionManager> sessionOperationProvider;

    @Autowired
    private ExerciseSessionModelFactory sessionModelFactory;

    @Autowired
    private ExerciseSessionConfigHelper configHelper;

    public ExerciseSessionDto add(ExerciseSessionDto model) {
        var sessionType = model.getType();
        var sessionManager = sessionOperationProvider.get(sessionType);

        return sessionManager.init(traversalSource, ExerciseCreationContext.createDefault(model));
    }

    public ExerciseSessionDto get(String id) {
        var query = new GetExerciseSessionByIdQuery();
        query.setId(id);
        return getExerciseSessionByIdQueryHandler.handle(query);
    }

    public ExerciseSessionDto nextExercise(String modelId) {
        var sessionVertex = getSessionVertex(modelId);
        var sessionType = sessionVertex.getType();
        var sessionManager = sessionOperationProvider.get(sessionType);

        var context = ExerciseCreationContext.createDefault(sessionModelFactory.create(sessionVertex));

        return sessionManager.nextExercise(traversalSource, context);
    }

    public ExerciseSessionDto skipExercise(String modelId) {
        return nextExercise(modelId);
    }

    public ExerciseSessionOptionsDto getConfig(String modelId) {
        var query = new GetExerciseSessionConfigQuery();
        query.setSessionId(modelId);
        return getExerciseSessionConfigQueryHandler.handle(query);
    }

    public ExerciseSessionDto updateConfig(String modelId, ExerciseSessionOptionsDto config) {
        return configHelper.updateConfig(modelId, config);
    }

    private ExerciseSessionVertex getSessionVertex(String modelId) {
        var sessionVertex = ExerciseSessionVertex.findById(traversalSource, modelId);
        if (sessionVertex == null)
            throw new IllegalArgumentException("No session with id " + modelId);
        return sessionVertex;
    }
}
