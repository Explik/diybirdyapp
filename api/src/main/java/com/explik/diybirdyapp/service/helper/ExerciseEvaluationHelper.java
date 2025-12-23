package com.explik.diybirdyapp.service.helper;

import com.explik.diybirdyapp.model.admin.ExerciseAnswerModel;
import com.explik.diybirdyapp.model.exercise.ExerciseDto;
import com.explik.diybirdyapp.persistence.provider.GenericProvider;
import com.explik.diybirdyapp.manager.exerciseEvaluationManager.ExerciseEvaluationContext;
import com.explik.diybirdyapp.manager.exerciseEvaluationManager.ExerciseEvaluationManager;
import com.explik.diybirdyapp.persistence.vertex.ExerciseSessionVertex;
import com.explik.diybirdyapp.persistence.vertex.ExerciseVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Helper class for evaluating exercise answers.
 * Handles the logic for evaluating different types of exercises.
 */
@Component
public class ExerciseEvaluationHelper {
    @Autowired
    private GraphTraversalSource traversalSource;

    @Autowired
    private GenericProvider<ExerciseEvaluationManager> evaluationStrategyProvider;

    /**
     * Evaluates an exercise answer and returns the result.
     * @param answer The answer model containing the user's input
     * @return The evaluated exercise DTO with feedback
     */
    public ExerciseDto evaluateAnswer(ExerciseAnswerModel answer) {
        assert answer != null;
        assert answer.getExerciseId() != null;

        var exerciseVertex = ExerciseVertex.getById(traversalSource, answer.getExerciseId());
        var exerciseType = exerciseVertex.getExerciseType().getId();
        var strategy = evaluationStrategyProvider.get(exerciseType);
        var strategyContext = getEvaluationContext(answer);

        return strategy.evaluate(exerciseVertex, strategyContext);
    }

    private ExerciseEvaluationContext getEvaluationContext(ExerciseAnswerModel answer) {
        var sessionVertex = ExerciseSessionVertex.findById(traversalSource, answer.getSessionId());
        var sessionOptionsVertex = (sessionVertex != null) ? sessionVertex.getOptions() : null;

        var strategyContext = ExerciseEvaluationContext.create(answer);
        if (sessionOptionsVertex != null) {
            strategyContext.setRetypeCorrectAnswerEnabled(sessionOptionsVertex.getRetypeCorrectAnswer());
            strategyContext.setAlgorithm(sessionOptionsVertex.getAlgorithm());
        }

        return strategyContext;
    }
}
