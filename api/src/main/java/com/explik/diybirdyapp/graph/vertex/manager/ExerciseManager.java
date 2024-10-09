package com.explik.diybirdyapp.graph.vertex.manager;

import com.explik.diybirdyapp.graph.model.ExerciseAnswerModel;
import com.explik.diybirdyapp.graph.model.ExerciseFeedbackModel;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;

public interface ExerciseManager {
    ExerciseFeedbackModel evaluate(GraphTraversalSource traversalSource, String exerciseId, ExerciseAnswerModel answerModel);
}
