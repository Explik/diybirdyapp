package com.explik.diybirdyapp.manager.exerciseEvaluationManager;

import com.explik.diybirdyapp.ComponentTypes;
import com.explik.diybirdyapp.ExerciseEvaluationTypes;
import com.explik.diybirdyapp.model.exercise.ExerciseDto;
import com.explik.diybirdyapp.model.exercise.ExerciseInputDto;
import com.explik.diybirdyapp.model.exercise.ExerciseInputSortOptionsDto;
import com.explik.diybirdyapp.persistence.command.CreateOrUpdateSimpleSortStateCommand;
import com.explik.diybirdyapp.persistence.command.handler.CommandHandler;
import com.explik.diybirdyapp.persistence.vertex.ExerciseVertex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Evaluates sort-flashcard exercises.
 * Records whether the user placed the flashcard in the "know" or "dont-know" pile,
 * and updates the SimpleSort state accordingly.
 */
@Component(ExerciseEvaluationTypes.BINARY_SORT + ComponentTypes.STRATEGY)
public class ExerciseEvaluationManagerSortFlashcard implements ExerciseEvaluationManager {
    @Autowired
    CommandHandler<CreateOrUpdateSimpleSortStateCommand> createOrUpdateSimpleSortStateCommandHandler;

    @Override
    public ExerciseDto evaluate(ExerciseVertex exerciseVertex, ExerciseEvaluationContext<? extends ExerciseInputDto> context) {
        if (context == null)
            throw new RuntimeException("Answer model is null");
        if (!(context.getInput() instanceof ExerciseInputSortOptionsDto input))
            throw new RuntimeException("Answer model type is not sort-options");

        var contentVertex = exerciseVertex.getContent();
        if (contentVertex == null)
            throw new IllegalArgumentException("Exercise content is null");

        // Update SimpleSort state
        var updateStateCommand = new CreateOrUpdateSimpleSortStateCommand();
        updateStateCommand.setSessionId(input.getSessionId());
        updateStateCommand.setContentId(contentVertex.getId());
        updateStateCommand.setPile(input.getPile());

        createOrUpdateSimpleSortStateCommandHandler.handle(updateStateCommand);

        // Generate indecisive feedback (no right/wrong answer in sorting)
        var exerciseFeedback = ExerciseFeedbackHelper.createIndecisiveFeedback();

        var exercise = new ExerciseDto();
        exercise.setId(exerciseVertex.getId());
        exercise.setType(exerciseVertex.getExerciseType().getId());
        exercise.setFeedback(exerciseFeedback);

        return exercise;
    }
}
