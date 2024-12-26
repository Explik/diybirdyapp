package com.explik.diybirdyapp.persistence.repository;

import com.explik.diybirdyapp.ComponentTypes;
import com.explik.diybirdyapp.model.ExerciseModel;
import com.explik.diybirdyapp.model.ExerciseSessionModel;
import com.explik.diybirdyapp.persistence.vertex.ExerciseSessionVertex;
import com.explik.diybirdyapp.persistence.operation.ExerciseSessionOperations;
import com.explik.diybirdyapp.persistence.modelFactory.ExerciseSessionModelFactory;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ExerciseSessionRepositoryImpl implements ExerciseSessionRepository {
    private final GraphTraversalSource traversalSource;

    @Autowired
    ExerciseSessionModelFactory sessionModelFactory;

    @Autowired
    Map<String, ExerciseSessionOperations> sessionManagers;

    public ExerciseSessionRepositoryImpl(@Autowired GraphTraversalSource traversalSource) {
        this.traversalSource = traversalSource;
    }

    @Override
    public ExerciseSessionModel add(ExerciseSessionModel model) {
        var sessionType = model.getType();
        var sessionManager = sessionManagers.getOrDefault(model.getType() + ComponentTypes.OPERATIONS, null);
        if (sessionManager == null)
            throw new IllegalArgumentException("No factory for type " + sessionType);

        return sessionManager.init(traversalSource, model);
    }

    @Override
    public ExerciseSessionModel get(String id) {
        var vertex = ExerciseSessionVertex.findById(traversalSource, id);
        if (vertex == null)
            throw new RuntimeException("No session with id " + id);

        return sessionModelFactory.create(vertex);
    }

    public ExerciseSessionModel nextExercise(String modelId) {
        var sessionVertex = ExerciseSessionVertex.findById(traversalSource, modelId);
        if (sessionVertex == null)
            throw new IllegalArgumentException("No session with id " + modelId);

        var sessionType = sessionVertex.getType();
        var sessionManager = sessionManagers.getOrDefault(sessionType + ComponentTypes.OPERATIONS, null);
        if (sessionManager == null)
            throw new IllegalArgumentException("No factory for type " + sessionType);

        return sessionManager.nextExercise(traversalSource, modelId);
    }
}
