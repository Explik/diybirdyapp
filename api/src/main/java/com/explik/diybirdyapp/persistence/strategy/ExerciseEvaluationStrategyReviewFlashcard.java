package com.explik.diybirdyapp.persistence.strategy;

import com.explik.diybirdyapp.ComponentTypes;
import com.explik.diybirdyapp.ExerciseEvaluationTypes;
import com.explik.diybirdyapp.ExerciseTypes;
import com.explik.diybirdyapp.model.exercise.ExerciseFeedbackModel;
import com.explik.diybirdyapp.model.exercise.ExerciseInputModel;
import com.explik.diybirdyapp.model.exercise.ExerciseInputRecognizabilityRatingModel;
import com.explik.diybirdyapp.model.exercise.ExerciseModel;
import com.explik.diybirdyapp.persistence.vertex.ExerciseVertex;
import com.explik.diybirdyapp.persistence.vertexFactory.ExerciseAnswerVertexFactoryRecognizabilityRating;
import com.explik.diybirdyapp.persistence.vertexFactory.RecognizabilityRatingVertexFactory;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(ExerciseEvaluationTypes.RECOGNIZABILITY + ComponentTypes.STRATEGY)
public class ExerciseEvaluationStrategyReviewFlashcard implements ExerciseEvaluationStrategy {
    @Autowired
    GraphTraversalSource traversalSource;

    @Autowired
    ExerciseAnswerVertexFactoryRecognizabilityRating answerVertexFactory;

    @Override
    public ExerciseModel evaluate(ExerciseVertex exerciseVertex, ExerciseInputModel genericAnswerModel) {
        if (genericAnswerModel == null)
            throw new RuntimeException("Answer model is null");
        if (!(genericAnswerModel instanceof ExerciseInputRecognizabilityRatingModel answerModel))
            throw new RuntimeException("Answer model type is not recognizability rating");

        // Save answer to graph
        answerVertexFactory.create(traversalSource, answerModel);

        // Generate feedback
        var exerciseFeedback = ExerciseFeedbackModel.createIndecisiveFeedback();

        var exercise = new ExerciseModel();
        exercise.setId(exerciseVertex.getId());
        exercise.setType(exerciseVertex.getType());
        exercise.setFeedback(exerciseFeedback);

        return exercise;
    }
}
