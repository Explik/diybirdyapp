package com.explik.diybirdyapp.persistence.operation;

import com.explik.diybirdyapp.model.ExerciseAnswerModel;
import com.explik.diybirdyapp.model.ExerciseFeedbackModel;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;

public interface ExerciseOperations {
    ExerciseFeedbackModel evaluate(GraphTraversalSource traversalSource, ExerciseAnswerModel answerModel);
}
