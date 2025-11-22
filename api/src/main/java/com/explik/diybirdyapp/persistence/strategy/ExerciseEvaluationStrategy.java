package com.explik.diybirdyapp.persistence.strategy;

import com.explik.diybirdyapp.dto.exercise.ExerciseDto;
import com.explik.diybirdyapp.persistence.vertex.ExerciseVertex;

public interface ExerciseEvaluationStrategy {
    ExerciseDto evaluate(ExerciseVertex vertex, ExerciseEvaluationContext context);
}
