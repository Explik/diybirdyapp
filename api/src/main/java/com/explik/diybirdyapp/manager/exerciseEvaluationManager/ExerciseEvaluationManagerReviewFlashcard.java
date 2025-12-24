package com.explik.diybirdyapp.manager.exerciseEvaluationManager;

import com.explik.diybirdyapp.ComponentTypes;
import com.explik.diybirdyapp.ExerciseEvaluationTypes;
import com.explik.diybirdyapp.model.exercise.ExerciseDto;
import com.explik.diybirdyapp.model.exercise.ExerciseInputSelectReviewOptionsDto;
import com.explik.diybirdyapp.persistence.command.CreateExerciseAnswerRecognizabilityRatingCommand;
import com.explik.diybirdyapp.persistence.command.CreateOrUpdateSuperMemo2StateCommand;
import com.explik.diybirdyapp.persistence.command.handler.CommandHandler;
import com.explik.diybirdyapp.persistence.vertex.ExerciseVertex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(ExerciseEvaluationTypes.RECOGNIZABILITY + ComponentTypes.STRATEGY)
public class ExerciseEvaluationManagerReviewFlashcard implements ExerciseEvaluationManager {
    @Autowired
    CommandHandler<CreateExerciseAnswerRecognizabilityRatingCommand> createExerciseAnswerRecognizabilityRatingCommandHandler;

    @Autowired
    CommandHandler<CreateOrUpdateSuperMemo2StateCommand> createOrUpdateSuperMemo2StateCommandHandler;

    @Override
    public ExerciseDto evaluate(ExerciseVertex exerciseVertex, ExerciseEvaluationContext context) {
        if (context == null)
            throw new RuntimeException("Answer model is null");
        if (!(context.getInput() instanceof ExerciseInputSelectReviewOptionsDto input))
            throw new RuntimeException("Answer model type is not recognizability rating");

        // Save answer to graph
        var command = new CreateExerciseAnswerRecognizabilityRatingCommand();
        command.setExerciseId(context.getExerciseId());
        command.setSessionId(context.getSessionId());
        command.setRating(input.getRating());

        createExerciseAnswerRecognizabilityRatingCommandHandler.handle(command);

        // Update spaced repetition data (if applicable)
        var contentVertex = exerciseVertex.getContent();
        if (contentVertex == null)
            throw new IllegalArgumentException("Exercise content is null");

        var updateStateCommand = new CreateOrUpdateSuperMemo2StateCommand();
        updateStateCommand.setSessionId(input.getSessionId());
        updateStateCommand.setContentId(contentVertex.getId());
        updateStateCommand.setRating(input.getRating());

        createOrUpdateSuperMemo2StateCommandHandler.handle(updateStateCommand);

        // Generate feedback
        var exerciseFeedback = ExerciseFeedbackHelper.createIndecisiveFeedback();

        var exercise = new ExerciseDto();
        exercise.setId(exerciseVertex.getId());
        exercise.setType(exerciseVertex.getExerciseType().getId());
        exercise.setFeedback(exerciseFeedback);

        return exercise;
    }
}
