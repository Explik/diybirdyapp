package com.explik.diybirdyapp.manager.exerciseEvaluationManager;

import com.explik.diybirdyapp.model.exercise.ExerciseDto;
import com.explik.diybirdyapp.persistence.vertex.ExerciseVertex;

public interface ExerciseEvaluationManager {
    ExerciseDto evaluate(ExerciseVertex vertex, ExerciseEvaluationContext context);
}
