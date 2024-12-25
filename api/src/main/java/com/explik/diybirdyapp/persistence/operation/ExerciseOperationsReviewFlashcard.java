package com.explik.diybirdyapp.persistence.operation;

import com.explik.diybirdyapp.ComponentTypes;
import com.explik.diybirdyapp.ExerciseAnswerTypes;
import com.explik.diybirdyapp.ExerciseTypes;
import com.explik.diybirdyapp.model.ExerciseAnswerModel;
import com.explik.diybirdyapp.model.ExerciseFeedbackModel;
import com.explik.diybirdyapp.model.ExerciseModel;
import com.explik.diybirdyapp.persistence.vertex.ExerciseVertex;
import com.explik.diybirdyapp.persistence.vertexFactory.ExerciseAnswerVertexFactoryRecognizabilityRating;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(ExerciseTypes.REVIEW_FLASHCARD + ComponentTypes.OPERATIONS)
public class ExerciseOperationsReviewFlashcard implements ExerciseOperations {
    @Autowired
    private ExerciseAnswerVertexFactoryRecognizabilityRating answerVertexFactory;

    @Override
    public ExerciseModel evaluate(GraphTraversalSource traversalSource, ExerciseAnswerModel answerModel) {
        if (answerModel == null)
            throw new RuntimeException("Answer model is null");

        if (!answerModel.getType().equals(ExerciseAnswerTypes.RECOGNIZABILITY_RATING))
            throw new RuntimeException("Answer model type is not recognizability rating");

        // Save answer to graph
        answerVertexFactory.create(traversalSource, answerModel);

        // Generate feedback
        var feedback = new ExerciseFeedbackModel();
        feedback.setType("general");
        feedback.setState("indecisive");

        var exercise = new ExerciseModel();
        exercise.setFeedback(feedback);

        return exercise;
    }
}
