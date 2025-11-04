package com.explik.diybirdyapp.persistence.modelFactory;

import com.explik.diybirdyapp.model.exercise.ExerciseModel;
import com.explik.diybirdyapp.persistence.ExerciseRetrievalContext;
import com.explik.diybirdyapp.persistence.vertex.ExerciseVertex;
import org.springframework.stereotype.Component;

@Component
public class LimitedExerciseModelFactory implements ContextualModelFactory<ExerciseVertex, ExerciseModel, ExerciseRetrievalContext> {
    @Override
    public ExerciseModel create(ExerciseVertex vertex, ExerciseRetrievalContext context) {
        if (vertex == null)
            throw new RuntimeException("Vertex is null");

        var instance = new ExerciseModel();
        instance.setId(vertex.getId());
        instance.setType(vertex.getType());
        return instance;
    }
}
