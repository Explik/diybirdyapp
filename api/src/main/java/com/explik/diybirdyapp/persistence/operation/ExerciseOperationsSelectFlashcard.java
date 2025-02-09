package com.explik.diybirdyapp.persistence.operation;

import com.explik.diybirdyapp.ComponentTypes;
import com.explik.diybirdyapp.ExerciseTypes;
import com.explik.diybirdyapp.model.exercise.ExerciseFeedbackModel;
import com.explik.diybirdyapp.model.exercise.ExerciseInputModel;
import com.explik.diybirdyapp.model.exercise.ExerciseInputMultipleChoiceTextModel;
import com.explik.diybirdyapp.model.exercise.ExerciseModel;
import com.explik.diybirdyapp.persistence.vertex.ContentVertex;
import com.explik.diybirdyapp.persistence.vertex.ExerciseVertex;
import com.explik.diybirdyapp.persistence.vertex.TextContentVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component(ExerciseTypes.SELECT_FLASHCARD + ComponentTypes.OPERATIONS)
public class ExerciseOperationsSelectFlashcard implements ExerciseOperations {
    @Override
    public ExerciseModel evaluate(GraphTraversalSource traversalSource, ExerciseInputModel genericAnswerModel) {
        if (genericAnswerModel == null)
            throw new RuntimeException("Answer model is null");
        if (!(genericAnswerModel instanceof ExerciseInputMultipleChoiceTextModel))
            throw new RuntimeException("Answer model type is not ExerciseInputMultipleChoiceTextModel");

        var answerModel = (ExerciseInputMultipleChoiceTextModel)genericAnswerModel;

        // Evaluate exercise
        var exerciseVertex = ExerciseVertex.getById(traversalSource, genericAnswerModel.getExerciseId());
        var correctOptionVertex = exerciseVertex.getContent();
        var incorrectOptionVertices = exerciseVertex.getTextContentOptions();
        var options = Stream.concat(Stream.of(correctOptionVertex), incorrectOptionVertices.stream()).collect(Collectors.toList());

        for(var option : options) {
            var optionId = option.getId();
            if (optionId.equals(answerModel.getValue())) {
                exerciseVertex.setAnswer(option);
                break;
            }
        }

        // Generate feedback
        return createExerciseWithFeedback(correctOptionVertex, incorrectOptionVertices, answerModel);
    }

    private static ExerciseModel createExerciseWithFeedback(ContentVertex correctOptionVertex, List<? extends TextContentVertex> incorrectOptionVertices, ExerciseInputMultipleChoiceTextModel answerModel) {
        var correctOptionId = correctOptionVertex.getId();
        var incorrectOptionIds = incorrectOptionVertices.stream().map(ContentVertex::getId).toList();
        var isCorrect = answerModel.getValue().equals(correctOptionId);

        var exerciseFeedback = ExerciseFeedbackModel.createCorrectFeedback(isCorrect);
        exerciseFeedback.setMessage("Answer submitted successfully");

        var inputFeedback = new ExerciseInputMultipleChoiceTextModel.Feedback();
        inputFeedback.setCorrectOptionIds(List.of(correctOptionId));
        inputFeedback.setIncorrectOptionIds(incorrectOptionIds);

        var exerciseInput = new ExerciseInputMultipleChoiceTextModel();
        exerciseInput.setFeedback(inputFeedback);

        var exercise = new ExerciseModel();
        exercise.setId(correctOptionVertex.getId());
        exercise.setType(ExerciseTypes.SELECT_FLASHCARD);
        exercise.setFeedback(exerciseFeedback);
        exercise.setInput(exerciseInput);
        return exercise;
    }
}
