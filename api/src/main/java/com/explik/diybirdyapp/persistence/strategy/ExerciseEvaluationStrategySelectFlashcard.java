package com.explik.diybirdyapp.persistence.strategy;

import com.explik.diybirdyapp.ComponentTypes;
import com.explik.diybirdyapp.ExerciseEvaluationTypes;
import com.explik.diybirdyapp.ExerciseTypes;
import com.explik.diybirdyapp.dto.exercise.ExerciseDto;
import com.explik.diybirdyapp.dto.exercise.ExerciseInputSelectOptionsDto;
import com.explik.diybirdyapp.persistence.vertex.ContentVertex;
import com.explik.diybirdyapp.persistence.vertex.ExerciseAnswerVertex;
import com.explik.diybirdyapp.persistence.vertex.ExerciseSessionVertex;
import com.explik.diybirdyapp.persistence.vertex.ExerciseVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component(ExerciseEvaluationTypes.CORRECT_OPTIONS + ComponentTypes.STRATEGY)
public class ExerciseEvaluationStrategySelectFlashcard implements ExerciseEvaluationStrategy {
    @Autowired
    private GraphTraversalSource traversalSource;

    @Override
    public ExerciseDto evaluate(ExerciseVertex exerciseVertex, ExerciseEvaluationContext context) {
        if (context == null)
            throw new RuntimeException("Answer model is null");
        if (!(context.getInput() instanceof ExerciseInputSelectOptionsDto answerModel))
            throw new RuntimeException("Answer model type is not ExerciseInputMultipleChoiceTextModel");

        // Evaluate exercise
        var correctOptionVertex = exerciseVertex.getCorrectOptions().getFirst();
        var incorrectOptionVertices = exerciseVertex.getOptions();

        // Save answer
        var sessionVertex = ExerciseSessionVertex.findById(traversalSource, answerModel.getSessionId());

        var allOptionVertices = new ArrayList<ContentVertex>();
        allOptionVertices.add(correctOptionVertex);
        allOptionVertices.addAll(incorrectOptionVertices);

        var selectedOptionId = answerModel.getValue();
        var selectedOptionVertex = allOptionVertices.stream()
                .filter(option -> option.getId().equals(selectedOptionId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Selected option not found"));

        var exerciseAnswerVertex = ExerciseAnswerVertex.create(traversalSource);
        exerciseAnswerVertex.setExercise(exerciseVertex);
        exerciseAnswerVertex.setSession(sessionVertex);
        exerciseAnswerVertex.setContent(selectedOptionVertex);

        // Generate feedback
        return createExerciseWithFeedback(correctOptionVertex, incorrectOptionVertices, answerModel, context);
    }

    private static ExerciseDto createExerciseWithFeedback(ContentVertex correctOptionVertex, List<? extends ContentVertex> incorrectOptionVertices, ExerciseInputSelectOptionsDto answerModel, ExerciseEvaluationContext context) {
        var correctOptionId = correctOptionVertex.getId();
        var incorrectOptionIds = incorrectOptionVertices.stream().map(ContentVertex::getId).toList();
        var isCorrect = answerModel.getValue().equals(correctOptionId);

        var exerciseFeedback = ExerciseFeedbackHelper.createCorrectFeedback(isCorrect);
        exerciseFeedback.setMessage("Answer submitted successfully");

        var inputFeedback = new ExerciseInputSelectOptionsDto.SelectOptionsInputFeedbackDto();
        inputFeedback.setCorrectOptionIds(List.of(correctOptionId));
        inputFeedback.setIncorrectOptionIds(incorrectOptionIds);

        if (context.getRetypeCorrectAnswerEnabled())
            inputFeedback.setIsRetypeAnswerEnabled(!isCorrect);

        var exerciseInput = new ExerciseInputSelectOptionsDto();
        exerciseInput.setFeedback(inputFeedback);

        var exercise = new ExerciseDto();
        exercise.setId(correctOptionVertex.getId());
        exercise.setType(ExerciseTypes.SELECT_FLASHCARD);
        exercise.setFeedback(exerciseFeedback);
        exercise.setInput(exerciseInput);
        return exercise;
    }
}
