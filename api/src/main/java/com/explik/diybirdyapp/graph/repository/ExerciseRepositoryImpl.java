package com.explik.diybirdyapp.graph.repository;

import com.explik.diybirdyapp.ComponentTypes;
import com.explik.diybirdyapp.graph.model.ExerciseAnswerModel;
import com.explik.diybirdyapp.graph.model.ExerciseFeedbackModel;
import com.explik.diybirdyapp.graph.model.ExerciseModel;
import com.explik.diybirdyapp.graph.vertex.ExerciseAnswerVertex;
import com.explik.diybirdyapp.graph.vertex.ExerciseVertex;
import com.explik.diybirdyapp.graph.vertex.factory.VertexFactory;
import com.explik.diybirdyapp.graph.vertex.manager.ExerciseManager;
import com.explik.diybirdyapp.graph.vertex.modelFactory.ExerciseModelFactory;
import com.explik.diybirdyapp.graph.vertex.modelFactory.LimitedExerciseModelFactory;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class ExerciseRepositoryImpl implements ExerciseRepository {
    @Autowired
    private LimitedExerciseModelFactory limitedExerciseModelFactory;

    @Autowired
    private Map<String, ExerciseModelFactory> exerciseModelFactories;

    @Autowired
    private Map<String, ExerciseManager> exerciseManagers;

    @Autowired
    private Map<String, VertexFactory<ExerciseAnswerVertex, ExerciseAnswerModel>> exerciseAnswerVertexFactories;

    private final GraphTraversalSource traversalSource;

    public ExerciseRepositoryImpl(@Autowired GraphTraversalSource traversalSource) {
        this.traversalSource = traversalSource;
    }

    @Override
    public ExerciseModel get(String id) {
        var vertex = ExerciseVertex.getById(traversalSource, id);
        var exerciseType = vertex.getType();

        var modelFactory = exerciseModelFactories.getOrDefault(exerciseType + ComponentTypes.MODEL_FACTORY, null);
        if (modelFactory == null)
            throw new RuntimeException("Unsupported exercise type: " + exerciseType);

        return modelFactory.create(vertex);
    }

    @Override
    public List<ExerciseModel> getAll() {
        var vertices = ExerciseVertex.getAll(traversalSource);

        return vertices
                .stream()
                .map(limitedExerciseModelFactory::create)
                .toList();
    }

    @Override
    public ExerciseFeedbackModel submitAnswer(String id, ExerciseAnswerModel answer) {
        var exerciseVertex = ExerciseVertex.getById(traversalSource, id);
        var exerciseType = exerciseVertex.getType();

        var manager = exerciseManagers.getOrDefault(exerciseType + ComponentTypes.MANAGER, null);
        if (manager == null)
            throw new RuntimeException("Unsupported exercise type: " + exerciseType);

        return manager.evaluate(traversalSource, id, answer);
    }
}
