package com.explik.diybirdyapp.graph.repository;

import com.explik.diybirdyapp.graph.model.ExerciseModel;
import com.explik.diybirdyapp.graph.vertex.AbstractVertex;
import com.explik.diybirdyapp.graph.vertex.ExerciseVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ExerciseRepositoryImpl implements ExerciseRepository {
    @Autowired
    private ExerciseFactory exerciseFactory;

    private final GraphTraversalSource traversalSource;

    public ExerciseRepositoryImpl(@Autowired GraphTraversalSource traversalSource) {
        this.traversalSource = traversalSource;
    }

    @Override
    public ExerciseModel get(String id) {
        var vertex = ExerciseVertex.getById(traversalSource, id);
        return exerciseFactory.create(vertex);
    }

    @Override
    public List<ExerciseModel> getAll() {
        var vertices = ExerciseVertex.getAll(traversalSource);

        return vertices
                .stream()
                .map(exerciseFactory::create)
                .toList();
    }
}
