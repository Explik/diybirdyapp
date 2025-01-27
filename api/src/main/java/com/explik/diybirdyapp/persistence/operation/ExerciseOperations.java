package com.explik.diybirdyapp.persistence.operation;

import com.explik.diybirdyapp.model.exercise.ExerciseInputModel;
import com.explik.diybirdyapp.model.exercise.ExerciseModel;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;

public interface ExerciseOperations {
    ExerciseModel evaluate(GraphTraversalSource traversalSource, ExerciseInputModel answerModel);
}
