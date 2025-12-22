package com.explik.diybirdyapp.persistence.strategy;

import com.explik.diybirdyapp.ComponentTypes;
import com.explik.diybirdyapp.ExerciseEvaluationTypes;
import com.explik.diybirdyapp.model.exercise.ExerciseDto;
import com.explik.diybirdyapp.model.exercise.ExerciseInputWriteTextDto;
import com.explik.diybirdyapp.model.admin.ExerciseAnswerModel;
import com.explik.diybirdyapp.persistence.command.CreateExerciseAnswerTextCommand;
import com.explik.diybirdyapp.persistence.command.handler.CommandHandler;
import com.explik.diybirdyapp.persistence.vertex.ExerciseVertex;
import com.explik.diybirdyapp.persistence.vertex.TextContentVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component(ExerciseEvaluationTypes.CORRECT_TEXT + ComponentTypes.STRATEGY)
public class ExerciseEvaluationStrategyWriteFlashcard implements ExerciseEvaluationStrategy {
    @Autowired
    private GraphTraversalSource traversalSource;

    @Autowired
    private CommandHandler<CreateExerciseAnswerTextCommand> createExerciseAnswerTextCommandHandler;

    @Override
    public ExerciseDto evaluate(ExerciseVertex exerciseVertex, ExerciseEvaluationContext context) {
        if (context == null)
            throw new RuntimeException("Answer model is null");
        if (!(context.getInput() instanceof ExerciseInputWriteTextDto input))
            throw new RuntimeException("Answer model type is ExerciseInputTextModel");

        // Save answer
        var command = new CreateExerciseAnswerTextCommand();
        command.setExerciseId(context.getExerciseId());
        command.setSessionId(context.getSessionId());
        command.setText(input.getText());

        createExerciseAnswerTextCommandHandler.handle(command);

        // Generate feedback
        return createExerciseWithFeedback(exerciseVertex, input, context);
    }

    private static ExerciseDto createExerciseWithFeedback(ExerciseVertex exerciseVertex, ExerciseInputWriteTextDto answerModel, ExerciseEvaluationContext context) {
        // Compare correct options and answer (CASE INSENSITIVE)
        var correctOptions = exerciseVertex.getCorrectOptions().stream().map(v -> (TextContentVertex)v).toList();
        var correctOptionValues = correctOptions.stream().map(TextContentVertex::getValue).toList();
        var isAnswerCorrect = correctOptionValues.stream().anyMatch(v -> v.equalsIgnoreCase(answerModel.getText()));
        var exerciseFeedback = ExerciseFeedbackHelper.createCorrectFeedback(isAnswerCorrect);
        exerciseFeedback.setMessage("Answer submitted successfully");

        var inputFeedback = new ExerciseInputWriteTextDto.ExerciseInputFeedbackTextDto();
        inputFeedback.setCorrectValues(correctOptionValues);
        if (!isAnswerCorrect) inputFeedback.setIncorrectValues(List.of(answerModel.getText()));

        if (context.getRetypeCorrectAnswerEnabled())
            inputFeedback.setIsRetypeAnswerEnabled(!isAnswerCorrect);

        var input = new ExerciseInputWriteTextDto();
        input.setFeedback(inputFeedback);

        var exercise = new ExerciseDto();
        exercise.setId(exerciseVertex.getId());
        exercise.setType(exerciseVertex.getExerciseType().getId());
        exercise.setFeedback(exerciseFeedback);
        exercise.setInput(input);
        return exercise;
    }
}
