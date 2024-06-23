package com.explik.diybirdyapp.graph.operation;

import com.explik.diybirdyapp.graph.model.Exercise;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class GetGenericExerciseQueryMapper implements GenericQueryMapper<Exercise<?>> {
    private final Map<String, GenericQueryMapper<Exercise<?>>> mappers = new HashMap<>();

    public GetGenericExerciseQueryMapper(@Autowired List<GenericQueryMapper<Exercise<?>>> queries) {
        queries.forEach(c -> this.mappers.put(c.getIdentifier(), c));
    }

    @Override
    public String getIdentifier() {
        return null;
    }

    @Override
    public Exercise<?> apply(GraphTraversalSource g, Vertex vertex) {
        var exerciseType = vertex.value("exerciseType").toString();

        if (!mappers.containsKey(exerciseType))
            throw new IllegalArgumentException("Unsupported exercise type " + exerciseType);

        return mappers.get(exerciseType).apply(g, vertex);
    }
}
