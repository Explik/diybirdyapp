package com.explik.diybirdyapp.graph.operation;

import com.explik.diybirdyapp.graph.model.Exercise;
import com.explik.diybirdyapp.graph.model.GenericExercise;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class GetGenericExerciseListQuery implements GenericQuery<GetGenericExerciseListQuery.Options, List<Exercise>> {
    @Override
    public String getIdentifier() {
        return null;
    }

    @Override
    public List<Exercise> apply(GraphTraversalSource traversal, GetGenericExerciseListQuery.Options options) {
        if (options == null)
            throw new IllegalArgumentException("Options cannot be null");

        return traversal.V().hasLabel("exercise")
                .toStream()
                .map(v -> new GenericExercise(v.value("id"), v.value("exerciseType")))
                .collect(Collectors.toList());
    }

    public static class Options {}
}
