package com.explik.diybirdyapp.manager.exerciseCreationManager;

import com.explik.diybirdyapp.persistence.command.CreateExerciseCommand;
import com.explik.diybirdyapp.persistence.command.handler.CommandHandler;
import com.explik.diybirdyapp.persistence.schema.ExerciseSchemas;
import com.explik.diybirdyapp.persistence.schema.parameter.ExerciseContentParameters;
import com.explik.diybirdyapp.persistence.schema.parameter.ExerciseParameters;
import com.explik.diybirdyapp.persistence.vertex.ExerciseVertex;
import com.explik.diybirdyapp.service.ExerciseCreationService;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Creates sort-flashcard exercises for the SimpleSort algorithm.
 * Shows one side of a flashcard and the user sorts it into "I know this" or "I don't know this".
 */
@Component
public class SortFlashcardExerciseCreationManager implements ExerciseCreationManager {
    @Autowired
    private ExerciseCreationService exerciseCreationService;

    @Autowired
    private CommandHandler<CreateExerciseCommand> createExerciseCommandHandler;

    @Override
    public ExerciseVertex createExercise(GraphTraversalSource traversalSource, ExerciseCreationContext context) {
        var sessionVertex = context.getSessionVertex();
        var flashcardVertex = context.getFlashcardVertex();

        if (sessionVertex == null || flashcardVertex == null) {
            return null;
        }

        var exerciseParameters = new ExerciseParameters()
                .withSession(sessionVertex)
                .withContent(new ExerciseContentParameters().withContent(flashcardVertex))
                .withBasedOnContent(flashcardVertex);

        var command = exerciseCreationService.createExerciseCommand(
                ExerciseSchemas.SORT_FLASHCARD_EXERCISE,
                exerciseParameters);
        createExerciseCommandHandler.handle(command);

        var exerciseId = exerciseParameters.getId() != null ? exerciseParameters.getId() : command.getId();
        return ExerciseVertex.getById(traversalSource, exerciseId);
    }
}
