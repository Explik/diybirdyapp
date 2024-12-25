package com.explik.diybirdyapp.persistence.repository;

import com.explik.diybirdyapp.ComponentTypes;
import com.explik.diybirdyapp.model.ExerciseAnswerModel;
import com.explik.diybirdyapp.model.ExerciseFeedbackModel;
import com.explik.diybirdyapp.model.ExerciseModel;
import com.explik.diybirdyapp.persistence.vertex.ExerciseAnswerVertex;
import com.explik.diybirdyapp.persistence.vertex.ExerciseVertex;
import com.explik.diybirdyapp.persistence.vertexFactory.VertexFactory;
import com.explik.diybirdyapp.persistence.operation.ExerciseOperations;
import com.explik.diybirdyapp.persistence.modelFactory.ExerciseModelFactory;
import com.explik.diybirdyapp.persistence.modelFactory.LimitedExerciseModelFactory;
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
    private Map<String, ExerciseOperations> exerciseManagers;

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
    public ExerciseModel submitAnswer(String id, ExerciseAnswerModel answer) {
        answer.setExerciseId(id);

        var exerciseVertex = ExerciseVertex.getById(traversalSource, id);
        var exerciseType = exerciseVertex.getType();
        var manager = exerciseManagers.getOrDefault(exerciseType + ComponentTypes.OPERATIONS, null);
        if (manager == null)
            throw new RuntimeException("Unsupported exercise type: " + exerciseType);

        return manager.evaluate(traversalSource, answer);
    }
}
