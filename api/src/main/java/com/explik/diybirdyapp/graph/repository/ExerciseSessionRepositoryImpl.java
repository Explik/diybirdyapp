package com.explik.diybirdyapp.graph.repository;

import com.explik.diybirdyapp.graph.model.ExerciseModel;
import com.explik.diybirdyapp.graph.model.ExerciseSessionModel;
import com.explik.diybirdyapp.graph.vertex.ExerciseSessionVertex;
import com.explik.diybirdyapp.graph.vertex.ExerciseVertex;
import com.explik.diybirdyapp.graph.vertex.factory.VertexFactory;
import com.explik.diybirdyapp.graph.vertex.manager.ExerciseSessionManager;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ExerciseSessionRepositoryImpl implements ExerciseSessionRepository {
    private final GraphTraversalSource traversalSource;

    @Autowired
    Map<String, ExerciseSessionManager> factories;

    public ExerciseSessionRepositoryImpl(@Autowired GraphTraversalSource traversalSource) {
        this.traversalSource = traversalSource;
    }

    @Override
    public ExerciseSessionModel add(ExerciseSessionModel model) {
        if (!factories.containsKey(model.getType()))
            throw new IllegalArgumentException("No factory for type " + model.getType());

        var factory = factories.get(model.getType());
        var vertex = factory.init(traversalSource, model);
        return create(vertex);
    }

    public ExerciseModel nextExercise(String modelId) {
        var sessionVertex = ExerciseSessionVertex.findById(traversalSource, modelId);
        if (sessionVertex == null)
            throw new IllegalArgumentException("No session with id " + modelId);

        var sessionType = sessionVertex.getType();
        if (!factories.containsKey(sessionType))
            throw new IllegalArgumentException("No factory for type " + sessionType);

        var factory = factories.get(sessionType);
        var vertex = factory.next(traversalSource, modelId);
        return (vertex != null) ? create(vertex) : null;
    }

    private ExerciseModel create(ExerciseVertex vertex) {
        var model = new ExerciseModel();
        model.setId(vertex.getId());
        model.setType(vertex.getType());
        return model;
    }

    private ExerciseSessionModel create(ExerciseSessionVertex vertex) {
        var model = new ExerciseSessionModel();
        model.setId(vertex.getId());
        model.setType(vertex.getType());
        model.setFlashcardDeckId(vertex.getFlashcardDeck().getId());
        return model;
    }
}
