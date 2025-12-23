package com.explik.diybirdyapp.manager.exerciseEvaluationManager;

import com.explik.diybirdyapp.ComponentTypes;
import com.explik.diybirdyapp.ExerciseEvaluationTypes;
import com.explik.diybirdyapp.model.exercise.ExerciseDto;
import com.explik.diybirdyapp.model.exercise.ExerciseInputWriteTextDto;
import com.explik.diybirdyapp.persistence.command.CreateExerciseAnswerTextCommand;
import com.explik.diybirdyapp.persistence.command.handler.CommandHandler;
import com.explik.diybirdyapp.persistence.query.GetCorrectTextValuesForExerciseQuery;
import com.explik.diybirdyapp.persistence.query.handler.QueryHandler;
import com.explik.diybirdyapp.persistence.query.modelFactory.CorrectTextValuesForExerciseModel;
import com.explik.diybirdyapp.persistence.vertex.ExerciseVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component(ExerciseEvaluationTypes.CORRECT_TEXT + ComponentTypes.STRATEGY)
public class ExerciseEvaluationManagerWriteFlashcard implements ExerciseEvaluationManager {
    @Autowired
    private GraphTraversalSource traversalSource;

    @Autowired
    private CommandHandler<CreateExerciseAnswerTextCommand> createExerciseAnswerTextCommandHandler;

    @Autowired
    private QueryHandler<GetCorrectTextValuesForExerciseQuery, CorrectTextValuesForExerciseModel> getCorrectTextValuesForExerciseQueryHandler;

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

        // Fetch correct text values using query
        var query = new GetCorrectTextValuesForExerciseQuery();
        query.setExerciseId(context.getExerciseId());
        
        var correctTextValuesModel = getCorrectTextValuesForExerciseQueryHandler.handle(query);

        // Generate feedback
        return createExerciseWithFeedback(correctTextValuesModel, input, context);
    }

    private static ExerciseDto createExerciseWithFeedback(CorrectTextValuesForExerciseModel correctTextValuesModel, ExerciseInputWriteTextDto answerModel, ExerciseEvaluationContext context) {
        // Compare correct options and answer (CASE INSENSITIVE)
        var correctOptionValues = correctTextValuesModel.getCorrectTextValues();
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
        exercise.setId(correctTextValuesModel.getExerciseId());
        exercise.setType(correctTextValuesModel.getExerciseType());
        exercise.setFeedback(exerciseFeedback);
        exercise.setInput(input);
        return exercise;
    }
}
