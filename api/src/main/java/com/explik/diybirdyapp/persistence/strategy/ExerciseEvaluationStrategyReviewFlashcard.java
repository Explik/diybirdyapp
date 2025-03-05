package com.explik.diybirdyapp.persistence.strategy;

import com.explik.diybirdyapp.ComponentTypes;
import com.explik.diybirdyapp.ExerciseTypes;
import com.explik.diybirdyapp.model.exercise.ExerciseFeedbackModel;
import com.explik.diybirdyapp.model.exercise.ExerciseInputModel;
import com.explik.diybirdyapp.model.exercise.ExerciseInputRecognizabilityRatingModel;
import com.explik.diybirdyapp.model.exercise.ExerciseModel;
import com.explik.diybirdyapp.persistence.vertex.ExerciseVertex;
import com.explik.diybirdyapp.persistence.vertexFactory.RecognizabilityRatingVertexFactory;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(ExerciseTypes.REVIEW_FLASHCARD + ComponentTypes.OPERATIONS)
public class ExerciseEvaluationStrategyReviewFlashcard implements ExerciseEvaluationStrategy {
    @Autowired
    private GraphTraversalSource traversalSource;

    @Autowired
    private RecognizabilityRatingVertexFactory ratingVertexFactory;

    @Override
    public ExerciseModel evaluate(ExerciseVertex exerciseVertex, ExerciseInputModel genericAnswerModel) {
        if (genericAnswerModel == null)
            throw new RuntimeException("Answer model is null");
        if (!(genericAnswerModel instanceof ExerciseInputRecognizabilityRatingModel))
            throw new RuntimeException("Answer model type is not recognizability rating");

        var answerModel = (ExerciseInputRecognizabilityRatingModel)genericAnswerModel;

        // Save answer to graph
        var answerId = (answerModel.getId() != null) ? answerModel.getId() : java.util.UUID.randomUUID().toString();
        var answerVertex = ratingVertexFactory.create(
                traversalSource,
                new RecognizabilityRatingVertexFactory.Options(answerId, answerModel.getRating()));
        exerciseVertex.setAnswer(answerVertex);

        // Generate feedback
        var exerciseFeedback = ExerciseFeedbackModel.createIndecisiveFeedback();

        var exercise = new ExerciseModel();
        exercise.setId(exerciseVertex.getId());
        exercise.setType(exerciseVertex.getType());
        exercise.setFeedback(exerciseFeedback);

        return exercise;
    }
}
