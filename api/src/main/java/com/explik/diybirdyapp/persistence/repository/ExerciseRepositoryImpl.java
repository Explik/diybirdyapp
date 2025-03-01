package com.explik.diybirdyapp.persistence.repository;

import com.explik.diybirdyapp.ComponentTypes;
import com.explik.diybirdyapp.model.exercise.ExerciseInputModel;
import com.explik.diybirdyapp.model.exercise.ExerciseModel;
import com.explik.diybirdyapp.persistence.modelFactory.ExerciseAbstractModelFactory;
import com.explik.diybirdyapp.persistence.modelFactory.ModelFactory;
import com.explik.diybirdyapp.persistence.provider.GenericProvider;
import com.explik.diybirdyapp.persistence.schema.ExerciseSchemas;
import com.explik.diybirdyapp.persistence.vertex.ExerciseVertex;
import com.explik.diybirdyapp.persistence.operation.ExerciseOperations;
import com.explik.diybirdyapp.persistence.modelFactory.LimitedExerciseModelFactory;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class ExerciseRepositoryImpl implements ExerciseRepository {
    private final GraphTraversalSource traversalSource;

    @Autowired
    private GenericProvider<ModelFactory<ExerciseVertex, ExerciseModel>> exerciseModelFactoryProvider;

    @Autowired
    private GenericProvider<ExerciseOperations> exerciseOperationProvider;

    public ExerciseRepositoryImpl(@Autowired GraphTraversalSource traversalSource) {
        this.traversalSource = traversalSource;
    }

    @Override
    public ExerciseModel get(String id) {
        var vertex = ExerciseVertex.getById(traversalSource, id);
        var exerciseType = vertex.getType();
        var exerciseFactory = exerciseModelFactoryProvider.get(exerciseType);

        return exerciseFactory.create(vertex);
    }

    @Override
    public List<ExerciseModel> getAll() {
        // Null indicates generic exercise model factory
        var factory = exerciseModelFactoryProvider.get(null);
        var vertices = ExerciseVertex.getAll(traversalSource);

        return vertices
                .stream()
                .map(factory::create)
                .toList();
    }

    @Override
    public ExerciseModel submitAnswer(String id, ExerciseInputModel answer) {
        answer.setExerciseId(id);

        var exerciseVertex = ExerciseVertex.getById(traversalSource, id);
        var exerciseType = exerciseVertex.getType();
        var exerciseOperations = exerciseOperationProvider.get(exerciseType);

        return exerciseOperations.evaluate(traversalSource, answer);
    }
}
