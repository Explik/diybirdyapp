package com.explik.diybirdyapp.persistence.operation;

import com.explik.diybirdyapp.model.ExerciseAnswerModel;
import com.explik.diybirdyapp.model.ExerciseFeedbackModel;
import com.explik.diybirdyapp.model.ExerciseModel;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;

public interface ExerciseOperations {
    ExerciseModel evaluate(GraphTraversalSource traversalSource, ExerciseAnswerModel answerModel);
}
