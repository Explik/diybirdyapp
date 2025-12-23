package com.explik.diybirdyapp.persistence.query.modelFactory;

import com.explik.diybirdyapp.model.exercise.ExerciseDto;
import com.explik.diybirdyapp.persistence.ExerciseRetrievalContext;
import com.explik.diybirdyapp.persistence.vertex.ExerciseVertex;
import org.springframework.stereotype.Component;

@Component
public class LimitedExerciseModelFactory implements ContextualModelFactory<ExerciseVertex, ExerciseDto, ExerciseRetrievalContext> {
    @Override
    public ExerciseDto create(ExerciseVertex vertex, ExerciseRetrievalContext context) {
        if (vertex == null)
            throw new RuntimeException("Vertex is null");

        var instance = new ExerciseDto();
        instance.setId(vertex.getId());
        instance.setType(vertex.getExerciseType().getId());
        return instance;
    }
}
