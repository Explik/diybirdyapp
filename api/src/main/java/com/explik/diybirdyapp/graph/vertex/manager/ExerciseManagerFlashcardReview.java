package com.explik.diybirdyapp.graph.vertex.manager;

import com.explik.diybirdyapp.ComponentTypes;
import com.explik.diybirdyapp.ExerciseAnswerTypes;
import com.explik.diybirdyapp.ExerciseTypes;
import com.explik.diybirdyapp.graph.model.ExerciseAnswerModel;
import com.explik.diybirdyapp.graph.model.ExerciseFeedbackModel;
import com.explik.diybirdyapp.graph.vertex.ExerciseFeedbackVertex;
import com.explik.diybirdyapp.graph.vertex.ExerciseVertex;
import com.explik.diybirdyapp.graph.vertex.factory.ExerciseAnswerRecognizabilityRatingVertexFactory;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(ExerciseTypes.REVIEW_FLASHCARD + ComponentTypes.MANAGER)
public class ExerciseManagerFlashcardReview implements ExerciseManager {
    @Autowired
    private ExerciseAnswerRecognizabilityRatingVertexFactory answerVertexFactory;

    @Override
    public ExerciseFeedbackModel evaluate(GraphTraversalSource traversalSource, String exerciseId, ExerciseAnswerModel answerModel) {
        // TODO Handle exercise not found
        var vertex = ExerciseVertex.getById(traversalSource, exerciseId);

        // Save answer to graph
        if (answerModel == null)
            throw new RuntimeException("Answer model is null");
        if (!answerModel.getType().equals(ExerciseAnswerTypes.RECOGNIZABILITY_RATING))
            throw new RuntimeException("Answer model type is not recognizability rating");

        answerVertexFactory.create(traversalSource, answerModel);

        // Generate feedback
        var feedback = new ExerciseFeedbackModel();
        feedback.setType("general");
        feedback.setMessage("Answer submitted successfully");
        return feedback;
    }
}
