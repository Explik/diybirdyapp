package com.explik.diybirdyapp.persistence.strategy;

import com.explik.diybirdyapp.ComponentTypes;
import com.explik.diybirdyapp.ExerciseEvaluationTypes;
import com.explik.diybirdyapp.model.exercise.ExerciseDto;
import com.explik.diybirdyapp.model.exercise.ExerciseInputSelectReviewOptionsDto;
import com.explik.diybirdyapp.model.admin.ExerciseAnswerModel;
import com.explik.diybirdyapp.persistence.vertex.ContentVertex;
import com.explik.diybirdyapp.persistence.vertex.ExerciseSessionStateVertex;
import com.explik.diybirdyapp.persistence.vertex.ExerciseSessionVertex;
import com.explik.diybirdyapp.persistence.vertex.ExerciseVertex;
import com.explik.diybirdyapp.persistence.vertexFactory.ExerciseAnswerVertexFactoryRecognizabilityRating;
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
    public ExerciseDto evaluate(ExerciseVertex exerciseVertex, ExerciseEvaluationContext context) {
        if (context == null)
            throw new RuntimeException("Answer model is null");
        if (!(context.getInput() instanceof ExerciseInputSelectReviewOptionsDto input))
            throw new RuntimeException("Answer model type is not recognizability rating");

        // Save answer to graph
        var answerModel = new ExerciseAnswerModel<ExerciseInputSelectReviewOptionsDto>();
        answerModel.setExerciseId(context.getExerciseId());
        answerModel.setSessionId(context.getSessionId());
        answerModel.setInput(input);

        answerVertexFactory.create(traversalSource, answerModel);

        // Update spaced repetition data (if applicable)
        updateSpacedRepetitionData(exerciseVertex, input);

        // Generate feedback
        var exerciseFeedback = ExerciseFeedbackHelper.createIndecisiveFeedback();

        var exercise = new ExerciseDto();
        exercise.setId(exerciseVertex.getId());
        exercise.setType(exerciseVertex.getExerciseType().getId());
        exercise.setFeedback(exerciseFeedback);

        return exercise;
    }

    private void updateSpacedRepetitionData(ExerciseVertex exerciseVertex, ExerciseInputSelectReviewOptionsDto answerModel) {
        var sessionId = answerModel.getSessionId();
        var sessionVertex = ExerciseSessionVertex.findById(traversalSource, sessionId);
        if (sessionVertex == null)
            throw new IllegalArgumentException("Session with ID " + sessionId + " does not exist");

        var contentVertex = exerciseVertex.getContent();
        if (contentVertex == null)
            throw new IllegalArgumentException("Exercise content is null");

        var algorithm = sessionVertex.getOptions().getAlgorithm();
        if (algorithm == null)
            return;
        if (algorithm.equals("SuperMemo2"))
            updateSuperMemo2Data(sessionVertex, contentVertex, answerModel);

    }

    private void updateSuperMemo2Data(ExerciseSessionVertex sessionVertex, ContentVertex contentVertex, ExerciseInputSelectReviewOptionsDto inputModel) {
        var stateVertex = ExerciseSessionStateVertex.findBy(traversalSource, "SuperMemo2", contentVertex, sessionVertex);
        if (stateVertex == null) {
            stateVertex = ExerciseSessionStateVertex.create(traversalSource);
            stateVertex.setType("SuperMemo2");
            stateVertex.setContent(contentVertex);
            sessionVertex.addState(stateVertex);

            resetSuperMemo2Data(stateVertex);
            return;
        }

        var q = getQualityOfResponse(inputModel);
        if (q <= 3) {
            resetSuperMemo2Data(stateVertex);
            return;
        }

        var n = (int)stateVertex.getPropertyValue("n");
        var last_eq = (double)stateVertex.getPropertyValue("e_factor");
        var last_l = (int)stateVertex.getPropertyValue("last_l");

        var new_eq = last_eq + (0.1 - (5 - q) * (0.08 + (5 - q) * 0.02));
        new_eq = clamp(new_eq, 1.3, 2.5);

        int new_l = switch (n) {
            case 1 -> 1;
            case 2 -> 6;
            default -> (int) Math.ceil(last_l * new_eq);
        };

        var next_show = System.currentTimeMillis() + (new_l * 86400000L);
        stateVertex.setPropertyValue("e_factor", new_eq);
        stateVertex.setPropertyValue("n", n + 1);
        stateVertex.setPropertyValue("last_l", new_l);
        stateVertex.setPropertyValue("last_show", System.currentTimeMillis());
        stateVertex.setPropertyValue("next_show", next_show);
    }

    private int getQualityOfResponse(ExerciseInputSelectReviewOptionsDto inputModel) {
        return switch (inputModel.getRating()) {
            case "again" -> 2;
            case "hard" -> 3;
            case "good" -> 4;
            case "easy" -> 5;
            default -> 1;
        };
    }

    private void resetSuperMemo2Data(ExerciseSessionStateVertex stateVertex) {
        stateVertex.setPropertyValue("n", 1);
        stateVertex.setPropertyValue("e_factor", 2.5);
        stateVertex.setPropertyValue("last_l", 0);
        stateVertex.setPropertyValue("last_show", System.currentTimeMillis());
        stateVertex.setPropertyValue("next_show", System.currentTimeMillis() + 86400000L); // Next day
    }

    public static double clamp(double val, double min, double max) {
        return Math.max(min, Math.min(max, val));
    }
}
