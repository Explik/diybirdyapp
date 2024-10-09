package com.explik.diybirdyapp.graph.vertex.modelFactory;

import com.explik.diybirdyapp.graph.model.ExerciseModel;
import com.explik.diybirdyapp.graph.vertex.ExerciseVertex;
import org.springframework.stereotype.Component;

@Component
public class LimitedExerciseModelFactory implements ExerciseModelFactory {
    @Override
    public ExerciseModel create(ExerciseVertex vertex) {
        if (vertex == null)
            throw new RuntimeException("Vertex is null");

        var instance = new ExerciseModel();
        instance.setId(vertex.getId());
        instance.setType(vertex.getType());
        return instance;
    }
}
