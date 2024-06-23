package com.explik.diybirdyapp.graph.operation;

import com.explik.diybirdyapp.graph.model.ExerciseAnswer;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class GetGenericExerciseAnswerQueryMapper implements GenericQueryMapper<ExerciseAnswer> {
    private final Map<String, GenericQueryMapper<ExerciseAnswer>> mappers = new HashMap<>();

    public GetGenericExerciseAnswerQueryMapper(@Autowired List<GenericQueryMapper<ExerciseAnswer>> mappers) {
        mappers.forEach(c -> this.mappers.put(c.getIdentifier(), c));
    }

    @Override
    public String getIdentifier() {
        return null;
    }

    @Override
    public ExerciseAnswer apply(GraphTraversalSource traversal, Vertex vertex) {
        var exerciseType = vertex.value("answerType").toString();

        if (!mappers.containsKey(exerciseType))
            throw new IllegalArgumentException("Unsupported exercise type " + exerciseType);

        return mappers.get(exerciseType).apply(traversal, vertex);
    }
}
