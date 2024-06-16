package com.explik.diybirdyapp.converter;

import com.explik.diybirdyapp.model.Exercise;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;

public interface ExerciseConverter<T extends Exercise> {
    String getExerciseType();

    T convert(GraphTraversalSource traversalSource, Vertex sourceVertex);
}
