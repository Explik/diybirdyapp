package com.explik.diybirdyapp.converter;

import com.explik.diybirdyapp.model.Exercise;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import java.util.Map;

public interface ExerciseConverter<T extends Exercise> {
    String getExerciseType();

    void create(GraphTraversalSource traversalSource, Map<String, Object> data);

    T get(GraphTraversalSource traversalSource, Vertex sourceVertex);
}

