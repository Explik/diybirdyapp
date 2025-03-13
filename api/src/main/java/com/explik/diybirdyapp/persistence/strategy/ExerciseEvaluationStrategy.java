package com.explik.diybirdyapp.persistence.strategy;

import com.explik.diybirdyapp.model.exercise.ExerciseInputModel;
import com.explik.diybirdyapp.model.exercise.ExerciseModel;
import com.explik.diybirdyapp.persistence.vertex.ExerciseVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;

public interface ExerciseEvaluationStrategy {
    ExerciseModel evaluate(ExerciseVertex vertex, ExerciseInputModel answerModel);
}
